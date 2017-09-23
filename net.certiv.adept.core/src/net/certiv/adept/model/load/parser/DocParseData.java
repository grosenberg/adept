package net.certiv.adept.model.load.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.certiv.adept.model.Document;
import net.certiv.adept.model.Feature;

public class DocParseData {

	public Document doc;

	public Parser parser;
	public ParserRuleContext tree;

	public Lexer lexer;
	public CommonTokenStream stream;
	public CodePointCharStream input;

	public int VWS;
	public int HWS;
	public int BLOCKCOMMENT = -2;
	public int LINECOMMENT = -2;
	public int[] VARS = { -2 }; // token types whose underlying values can be ignored
	public int[] ALIGN_ANY = { -2 }; // token types that may be recogized as aligned
	public int[] ALIGN_SAME = { -2 }; // token types that may be mutually recogized as aligned

	public int ERR_RULE = -2;
	public int ERR_TOKEN = -2;

	public int errCount;

	// key=node feature token; value=token's node
	public Map<Token, TerminalNode> nodeIndex;

	// key=rule feature start token; value=token's context
	public Map<Token, ParserRuleContext> contextIndex;

	// key=rule context; value=the owning feature
	public Map<ParserRuleContext, Feature> ruleIndex;

	// key=token; value=the owning feature
	public Map<TerminalNode, Feature> terminalIndex;

	// key=token start index; value=the owning feature
	public Map<Integer, Feature> tokenIndex;

	// key=line number; value=tokens of line
	public Map<Integer, List<Token>> lineIndex;

	// key=token; value=vis offset
	public Map<Token, Integer> visIndex;

	// key=feature type
	public HashSet<Long> typeSet;

	public DocParseData(Document doc) {
		super();
		this.doc = doc;
		nodeIndex = new HashMap<>();
		contextIndex = new HashMap<>();
		ruleIndex = new HashMap<>();
		terminalIndex = new HashMap<>();
		tokenIndex = new HashMap<>();
		lineIndex = new HashMap<>();
		visIndex = new HashMap<>();
		typeSet = new HashSet<>();
	}

	public Document getDocument() {
		return doc;
	}

	public CommonTokenStream getTokenStream() {
		return stream;
	}

	public List<Token> getTokens() {
		return stream.getTokens();
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

	@SuppressWarnings("deprecation")
	public List<String> getTokenNames() {
		return Arrays.asList(parser.getTokenNames());
	}
}