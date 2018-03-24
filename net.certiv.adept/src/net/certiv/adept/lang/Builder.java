package net.certiv.adept.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.certiv.adept.core.CoreMgr;
import net.certiv.adept.format.align.Aligner;
import net.certiv.adept.format.align.Place;
import net.certiv.adept.format.indent.Indenter;
import net.certiv.adept.model.Document;
import net.certiv.adept.model.Feature;
import net.certiv.adept.model.Kind;
import net.certiv.adept.model.RefToken;
import net.certiv.adept.model.Spacing;
import net.certiv.adept.util.Log;
import net.certiv.adept.util.Strings;

public class Builder extends ParseRecord {

	private static final int LIMIT = 6; // ancestor path limit

	private CoreMgr mgr;
	private List<Integer> exTypes;

	public Builder() {
		super(null);
	}

	public Builder(CoreMgr mgr, Document doc) {
		super(doc);
		this.mgr = mgr;

		if (doc != null) doc.setBuilder(this);
		exTypes = mgr.excludedLangTypes();
	}

	// ---------------------------------------------------------------------

	/** Build indexes prior to feature extraction. */
	public void index() {
		int tabWidth = doc.getTabWidth();
		int line = -1;				// current line (0..n)
		AdeptToken start = null;	// start of current line

		// create line -> formattable tokens index
		// create line -> comment tokens index
		// create line -> blankline? index
		for (AdeptToken token : getTokens()) {
			int num = token.getLine();
			if (num > line) {	// track line changes
				line = num;
				start = token;
				blanklines.put(line, true);
			}

			int type = token.getType();
			if (!isWhitespace(type)) {			// formattable only
				token.setRefToken(new RefToken(token));
				token.setVisCol(calcVisualColumn(start, token, tabWidth));

				lineTokensIndex.put(line, token);
				blanklines.put(line, false);	// correct assumption

				if (isComment(type)) commentIndex.put(line, token);
			}
		}

		for (List<AdeptToken> values : lineTokensIndex.valuesList()) {
			if (values.size() == 1) {
				values.get(0).setPlace(Place.SOLO);
			} else {
				int len = values.size();
				values.get(0).setPlace(Place.BEG);
				for (int idx = 1; idx < len - 2; idx++) {
					values.get(idx).setPlace(Place.MID);
				}
				values.get(values.size() - 1).setPlace(Place.END);
			}
		}
	}

	// ---- Indent and aligner operations -----

	public Indenter indenter() {
		return indenter;
	}

	public Aligner aligner() {
		return aligner;
	}

	// ---- Feature recognition operations -----

	/**
	 * Evaluates a rule context to build the corresponding set of features. Called from the parse-tree
	 * walker.
	 *
	 * @param ctx the current rule context
	 */
	public void extractFeatures(ParserRuleContext ctx) {
		if (ctx.getChildCount() == 0) {
			// glorious abundance of caution
			String rule = getRuleName(ctx.getRuleIndex());
			Log.error(this, rule + " has no children.");
			return;
		}

		List<ParseTree> ancestors = getAncestors(ctx);
		for (ParseTree child : ctx.children) {
			if (child instanceof ErrorNode) {
				String err = ((ErrorNode) child).getText();
				String msg = String.format("Failed to parse: %s", Utils.escapeWhitespace(err, false));
				Log.debug(this, msg);

			} else if (child instanceof TerminalNode) {
				TerminalNode node = (TerminalNode) child;
				AdeptToken token = (AdeptToken) node.getSymbol();
				// token.setTerminal(node);

				if (token.getType() == Token.EOF) continue;

				// real feature
				defineFeature(ancestors, token);

				// comment features
				AdeptToken left = findCommentLeft(token);
				if (left != null) defineFeature(ancestors, left);
				AdeptToken right = findCommentRight(token);
				if (right != null) defineFeature(ancestors, right);
			}
		}
	}

	// ---------------------------------------------------------------------

	/**
	 * Define a new feature focused on a given feature node. All lists ordered from closest to farthest
	 * relative to the feature node.
	 *
	 * @param parents ancestor list
	 * @param lead leading nodes in the current context
	 * @param node the feature node
	 */
	private void defineFeature(List<ParseTree> parents, AdeptToken token) {

		// current context
		ParserRuleContext ctx = (ParserRuleContext) parents.get(0);
		int rule = ctx.getRuleIndex();
		int ruleType = rule << 16;
		if (exTypes.contains(ruleType)) {
			if (ruleType == ERR_RULE) {
				Log.debug(this, String.format("Error rule: %s", Utils.escapeWhitespace(ctx.getText(), false)));
			}
			return;
		}

		// target token
		int type = token.getType();
		if (exTypes.contains(type)) {
			if (type == ERR_TOKEN) {
				Log.debug(this, String.format("Error token: %s", Utils.escapeWhitespace(token.getText(), false)));
			}
			return;
		}

		token.setKind(evalKind(type));
		token.setNodeName(getTokenName(type));
		int tokenIdx = token.getTokenIndex();
		token.setIndents(indenter.getIndents(tokenIdx));

		RefToken ref = token.refToken();
		AdeptToken left = findRealLeft(tokenIdx);
		if (left != null) {
			String ws = findWsLeft(tokenIdx);
			Spacing spacing = evalSpacing(left.getTokenIndex(), tokenIdx);
			ref.setLeft(left, spacing, ws);
		}

		AdeptToken right = findRealRight(tokenIdx);
		if (right != null) {
			String ws = findWsRight(tokenIdx);
			Spacing spacing = evalSpacing(tokenIdx, right.getTokenIndex());
			ref.setRight(right, spacing, ws);
		}

		Feature feature = Feature.create(mgr, doc, genPath(parents), token);

		index.put(token, feature);
		featureIndex.put(feature.getId(), feature);
		typeSet.add(type);
	}

	// ---------------------------------------------------------------------

	private AdeptToken findCommentLeft(AdeptToken token) {
		List<AdeptToken> hidden = getHiddenLeft(token.getTokenIndex());
		Collections.reverse(hidden);
		return findComment(hidden);
	}

	private AdeptToken findCommentRight(AdeptToken token) {
		List<AdeptToken> hidden = getHiddenRight(token.getTokenIndex());
		return findComment(hidden);
	}

	private AdeptToken findComment(List<AdeptToken> hidden) {
		for (AdeptToken token : hidden) {
			if (isComment(token.getType())) return token;
		}
		return null;
	}

	private Kind evalKind(int type) {
		if (type == BLOCKCOMMENT) {
			return Kind.BLOCKCOMMENT;
		} else if (type == LINECOMMENT) {
			return Kind.LINECOMMENT;
		} else {
			return Kind.TERMINAL;
		}
	}

	// characterize the white space between two tokens (exclusive)
	private Spacing evalSpacing(int from, int to) {
		if (from < to) {
			if (from + 1 == to) return Spacing.NONE;

			int hws = 0;
			int vws = 0;
			int tabWidth = doc.getTabWidth();
			for (Token token : tokenStream.get(from + 1, to - 1)) {
				int type = token.getType();
				if (type == HWS) {
					hws += Strings.measureVisualWidth(token.getText(), tabWidth);
				} else if (type == VWS) {
					vws++;
				}
			}

			if (vws > 1) return Spacing.VFLEX;
			if (vws == 1) return Spacing.VLINE;
			if (hws > 1) return Spacing.HFLEX;
			if (hws == 1) return Spacing.HSPACE;
		}
		return Spacing.UNKNOWN;
	}

	// convert ancestor list to integers
	private List<Integer> genPath(List<ParseTree> nodes) {
		List<Integer> path = new ArrayList<>();
		for (ParseTree node : nodes) {
			if (node instanceof ParserRuleContext) {
				path.add(((ParserRuleContext) node).getRuleIndex());
			} else {
				throw new IllegalArgumentException("Ancestors must be rules.");
			}
		}
		return path;
	}

	private String findWsLeft(int idx) {
		StringBuilder sb = new StringBuilder();
		for (Token token : getHiddenLeft(idx)) {
			int type = token.getType();
			if (type != BLOCKCOMMENT && type != LINECOMMENT) {
				sb.append(token.getText());
			}
		}
		return sb.toString();
	}

	private String findWsRight(int idx) {
		StringBuilder sb = new StringBuilder();
		for (Token token : getHiddenRight(idx)) {
			int type = token.getType();
			if (type != BLOCKCOMMENT && type != LINECOMMENT) {
				sb.append(token.getText());
			}
		}
		return sb.toString();
	}

	private AdeptToken findRealLeft(int idx) {
		for (int jdx = idx - 1; jdx > -1; jdx--) {
			AdeptToken left = (AdeptToken) tokenStream.get(jdx);
			if (left.getChannel() == Token.DEFAULT_CHANNEL) return left;
		}
		return null;
	}

	private AdeptToken findRealRight(int idx) {
		for (int jdx = idx + 1, len = tokenStream.size(); jdx < len; jdx++) {
			AdeptToken right = (AdeptToken) tokenStream.get(jdx);
			if (right.getChannel() == Token.DEFAULT_CHANNEL) return right;
		}
		return null;
	}

	// ---------------------------------------------------------------------

	// parents of given context, including the current context
	// ordered from the current context to farthest parent context
	private List<ParseTree> getAncestors(ParserRuleContext ctx) {
		List<ParseTree> parents = new ArrayList<>();
		ParserRuleContext parent = ctx;
		for (int idx = 0; parent != null && idx < LIMIT; idx++) {
			parents.add(parent);
			parent = parent.getParent();
		}
		return parents;
	}

	private int calcVisualColumn(Token start, Token mark, int tabWidth) {
		if (start == null || start == mark) return 0;

		int beg = start.getStartIndex();
		int end = mark.getStartIndex() - 1;
		String text = mark.getInputStream().getText(new Interval(beg, end));
		return Strings.measureVisualWidth(text, tabWidth);
	}
}
