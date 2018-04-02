package net.certiv.adept.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import net.certiv.adept.format.align.Aligner;
import net.certiv.adept.format.indent.Indenter;
import net.certiv.adept.model.Document;
import net.certiv.adept.model.Feature;
import net.certiv.adept.unit.AdeptComp;
import net.certiv.adept.unit.TreeMultilist;
import net.certiv.adept.unit.TreeMultiset;

/** Record of the information generated through the parsing of a single Document. */
public class ParseRecord {

	public Document doc;

	public Parser parser;
	public ParserRuleContext tree;

	public Lexer lexer;
	public CommonTokenStream tokenStream;
	public CodePointCharStream charStream;

	public int VWS;
	public int HWS;
	public int BLOCKCOMMENT = -2;
	public int LINECOMMENT = -2;
	public int ERR_RULE = -2 << 16;
	public int ERR_TOKEN = -2;

	public int errCount;

	// ---------------------------------------------------------

	/** Primary index of a parsed document. Ordered by token index. */
	// key=token; value=feature
	public TreeMap<AdeptToken, Feature> index;

	// key=token index; value=token
	public TreeMap<Integer, AdeptToken> tokenIndex;

	// key=feature id; value=feature
	public HashMap<Integer, Feature> featureIndex;

	// key=unique feature token types
	public HashSet<Integer> typeSet;

	// key=line number; value=list of tokens
	public TreeMultilist<Integer, AdeptToken> lineTokensIndex;

	// key=line number; value=bol char offset
	public HashMap<Integer, Integer> lineStartIndex;

	// key=line number; value=blank?
	public HashMap<Integer, Boolean> blanklines;

	// key=line number; value=list of comments
	public TreeMultiset<Integer, AdeptToken> commentIndex;

	// key=line number; value=list of fields
	public TreeMultiset<Integer, AdeptToken> fieldIndex;

	// effective: key=token index; value=indentation level
	protected Indenter indenter;

	// effective: key=token index; value=indentation level
	protected Aligner aligner;

	// ---------------------------------------------------------

	public ParseRecord(Document doc) {
		super();
		this.doc = doc;
		index = new TreeMap<>(AdeptComp.Instance);
		tokenIndex = new TreeMap<>();
		featureIndex = new HashMap<>();
		typeSet = new HashSet<>();
		lineTokensIndex = new TreeMultilist<>(null, AdeptComp.Instance);
		lineStartIndex = new HashMap<>();
		blanklines = new HashMap<>();
		commentIndex = new TreeMultiset<>();
		fieldIndex = new TreeMultiset<>();

		indenter = new Indenter(this);
		aligner = new Aligner(this);
	}

	public void dispose() {
		index.clear();
		tokenIndex.clear();
		featureIndex.clear();
		typeSet.clear();
		lineTokensIndex.clear();
		lineStartIndex.clear();
		blanklines.clear();
		commentIndex.clear();
		fieldIndex.clear();

		indenter.clear();
		aligner.clear();
	}

	public Document getDocument() {
		return doc;
	}

	public CommonTokenStream getTokenStream() {
		return tokenStream;
	}

	public List<AdeptToken> getTokens() {
		return convert(tokenStream.getTokens());
	}

	public String getTokenName(int type) {
		return lexer.getVocabulary().getDisplayName(type);
	}

	/** Returns a count of the real tokens in the given token index range, inclusive. */
	public int getRealTokenCount(int begIndex, int endIndex) {
		List<Token> tokens = tokenStream.get(begIndex, endIndex);
		if (tokens == null) return 0;

		int cnt = 0;
		for (Token token : tokens) {
			if (!isWsOrComment(token.getType())) cnt++;
		}
		return cnt;
	}

	/** Returns a list of the tokens in the given token index range, inclusive. */
	public List<Token> getTokenInterval(int begIndex, int endIndex) {
		return tokenStream.get(begIndex, endIndex);
	}

	/** Returns the token at the given token index . */
	public AdeptToken getToken(int index) {
		return (AdeptToken) tokenStream.getTokens().get(index);
	}

	/** Returns the text of the tokens in the given token index range, exclusive. */
	public String getTextBetween(int begIndex, int endIndex) {
		List<Token> tokens = getTokenInterval(begIndex, endIndex);
		if (tokens == null || tokens.size() < 3) return "";

		StringBuilder sb = new StringBuilder();
		for (Token token : tokens.subList(1, tokens.size() - 1)) {
			sb.append(token.getText());
		}
		return sb.toString();
	}

	/** Protect against null */
	public List<AdeptToken> getHiddenLeft(int tokenIndex) {
		return convert(tokenStream.getHiddenTokensToLeft(tokenIndex));
	}

	/** Protect against null */
	public List<AdeptToken> getHiddenRight(int tokenIndex) {
		return convert(tokenStream.getHiddenTokensToRight(tokenIndex));
	}

	@SuppressWarnings("unchecked")
	private List<AdeptToken> convert(List<Token> tokens) {
		List<AdeptToken> adepts = new ArrayList<>();
		if (tokens != null) adepts.addAll((List<AdeptToken>) ((List<?>) tokens));
		return adepts;
	}

	public ParserRuleContext getTree() {
		return tree;
	}

	public int getParseErrCount() {
		return errCount;
	}

	public List<String> getRuleNames() {
		return Arrays.asList(parser.getRuleNames());
	}

	public String getRuleName(int idx) {
		return getRuleNames().get(idx);
	}

	@SuppressWarnings("deprecation")
	public List<String> getTokenNames() {
		return Arrays.asList(parser.getTokenNames());
	}

	/** Returns the unique features created for the parsed document. */
	public List<Feature> getFeatures() {
		return new ArrayList<>(featureIndex.values());
	}

	/** Returns the token feature map for the document, ordered by token index. */
	public TreeMap<AdeptToken, Feature> getIndex() {
		TreeMap<AdeptToken, Feature> clone = new TreeMap<>(AdeptComp.Instance);
		clone.putAll(index);
		return clone;
	}

	public Feature getFeature(AdeptToken token) {
		return index.get(token);
	}

	/**
	 * Returns the token on the given line (0..n) that includes the given visual column.
	 */
	public AdeptToken getVisualToken(int line, int vcol) {
		List<AdeptToken> tokens = lineTokensIndex.get(line);
		if (tokens == null || tokens.isEmpty()) return null;
		if (tokens.size() == 1) return tokens.get(0);

		for (int idx = 0, len = tokens.size(); idx < len - 1; idx++) {
			int beg = tokens.get(idx).visCol();
			int end = tokens.get(idx + 1).visCol() - 1;

			if (idx == 0 && vcol < beg) return tokens.get(idx);
			if (beg <= vcol && vcol < end) return tokens.get(idx);
		}
		return tokens.get(tokens.size() - 1);
	}

	public boolean isWhitespace(int type) {
		return type == HWS || type == VWS;
	}

	public boolean isVertWS(int type) {
		return type == VWS;
	}

	public boolean isHorzWS(int type) {
		return type == HWS;
	}

	public boolean isComment(int type) {
		return type == BLOCKCOMMENT || type == LINECOMMENT;
	}

	public boolean isWsOrComment(int type) {
		return type == VWS || type == HWS || type == BLOCKCOMMENT || type == LINECOMMENT;
	}

	public boolean isBlankLine(int line) {
		if (line < 0 && line >= blanklines.size()) return true;
		return blanklines.get(line);
	}
}