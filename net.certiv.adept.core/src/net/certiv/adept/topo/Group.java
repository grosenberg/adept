package net.certiv.adept.topo;

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import net.certiv.adept.model.Feature;
import net.certiv.adept.parser.Parse;

/** Identifies features that exist within a well-defined local group */
public class Group {

	private static final int INTERNODE = 4;
	private static final int NODE2COMMENT = 1;
	private static final int NODE2RULE = 4;

	private Feature locus;
	private Parse data;

	public Group(Parse data) {
		this.data = data;
	}

	public void setLocus(Feature locus) {
		this.locus = locus;
	}

	public boolean isLocal(Feature target) {
		final int tStart = target.getStart();
		final int lStart = locus.getStart();

		switch (locus.getFeatureType()) {
			case BLOCKCOMMENT:
			case LINECOMMENT:
				switch (target.getFeatureType()) {
					case BLOCKCOMMENT:
					case LINECOMMENT:
						if (samePriorLine(target)) return true;
						break;
					case NODE:
						if (Math.abs(tStart - lStart) <= NODE2COMMENT) return true;
						break;
					case RULE:
						break;
				}
				break;
			case NODE:
				switch (target.getFeatureType()) {
					case BLOCKCOMMENT:
					case LINECOMMENT:
						if (Math.abs(tStart - lStart) <= NODE2COMMENT) return true;
						break;
					case NODE:
						if (samePriorLine(target)) return true;
						if (Math.abs(tStart - lStart) <= INTERNODE) return true;
						break;
					case RULE:
						if (parentRank(target, locus) <= NODE2RULE) return true;
						break;
				}
				break;
			case RULE:
				switch (target.getFeatureType()) {
					case BLOCKCOMMENT:
					case LINECOMMENT:
						break;
					case NODE:
						if (parentRank(locus, target) <= NODE2RULE) return true;
						break;
					case RULE:
						break;
				}
				break;
		}
		return false;
	}

	private boolean samePriorLine(Feature target) {
		if (locus.getType() == target.getType()) {
			int cnt = linesBetween(locus.getStart(), target.getStart());
			if (cnt < 2) return true;
		}
		return false;
	}

	// count excludes blank lines
	private int linesBetween(int beg, int end) {
		List<Token> tokens = data.getTokens();
		int vws = 0;
		boolean blank = true;
		for (int ptr = beg + 1; ptr < end; ptr++) {
			Token token = tokens.get(ptr);
			if (token.getType() == data.HWS) continue;
			if (token.getType() == data.VWS) {
				if (!blank) vws++;
				blank = true;
			} else {
				blank = false;
			}
		}
		return vws;
	}

	private int parentRank(Feature rule, Feature node) {
		Token token = data.getTokens().get(rule.getStart());
		ParseTree context = data.contextIndex.get(token);

		int rank = 1;
		token = data.getTokens().get(node.getStart());
		ParseTree parent = data.nodeIndex.get(token).getParent();
		while (parent != null) {
			if (parent.equals(context)) return rank;
			parent = parent.getParent();
			rank++;
		}
		return rank;
	}
}