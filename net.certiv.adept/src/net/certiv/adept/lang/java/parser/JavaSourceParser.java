package net.certiv.adept.lang.java.parser;

import static net.certiv.adept.lang.java.parser.gen.JavaLexer.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import net.certiv.adept.Tool;
import net.certiv.adept.format.align.Aligner;
import net.certiv.adept.format.indent.Indenter;
import net.certiv.adept.lang.AdeptTokenFactory;
import net.certiv.adept.lang.Builder;
import net.certiv.adept.lang.ISourceParser;
import net.certiv.adept.lang.ParseRecord;
import net.certiv.adept.lang.ParserErrorListener;
import net.certiv.adept.lang.java.parser.gen.JavaLexer;
import net.certiv.adept.lang.java.parser.gen.JavaParser;
import net.certiv.adept.model.Document;
import net.certiv.adept.tool.ErrorType;

public class JavaSourceParser implements ISourceParser {

	protected int errorCount;
	private Builder builder;

	@Override
	public void process(Builder builder, Document doc) throws RecognitionException, Exception {
		this.builder = builder;

		ParserErrorListener errors = new ParserErrorListener(this);
		fillCollector(doc.getContent());

		AdeptTokenFactory factory = new AdeptTokenFactory();
		builder.lexer.setTokenFactory(factory);
		builder.parser.setTokenFactory(factory);
		builder.parser.removeErrorListeners();
		builder.parser.addErrorListener(errors);

		builder.tree = ((JavaParser) builder.parser).compilationUnit();
		builder.errCount = errorCount;

		if (builder.tree == null || builder.tree instanceof ErrorNode || builder.errCount > 0) {
			Tool.errMgr.toolError(ErrorType.PARSE_ERROR, "Bad parse tree: " + doc.getPathname());
		}
	}

	private void fillCollector(String content) {
		builder.charStream = CharStreams.fromString(content);
		builder.lexer = new JavaLexer(builder.charStream);
		builder.VWS = VWS;
		builder.HWS = HWS;
		builder.BLOCKCOMMENT = BLOCKCOMMENT;
		builder.LINECOMMENT = LINECOMMENT;
		builder.ERR_TOKEN = ERRCHAR;
		// featureBuilder.ERR_RULE = JavaParser.RULE_other << 16;
		builder.tokenStream = new CommonTokenStream(builder.lexer);
		builder.parser = new JavaParser(builder.tokenStream);
	}

	@Override
	public void reportRecognitionError(Token offendingToken, int errorIdx, int line, int col, String msg,
			RecognitionException e) {
		errorCount++;	// TODO: record location and extent of errors in the model
	}

	@Override
	public void extractFeatures(Builder model) {
		ParseTreeWalker walker = new ParseTreeWalker();
		JavaVisitor visitor = new JavaVisitor(model);
		walker.walk(visitor, model.tree);
	}

	@Override
	public void defineIndentation(Indenter indenter) {}

	@Override
	public void locateAlignables(Aligner aligner) {}

	@Override
	public List<Integer> excludedTypes() {
		List<Integer> excludes = new ArrayList<>();
		excludes.add(Token.EOF);
		excludes.add(JavaParser.ERRCHAR);
		// excludes.add(JavaParser.RULE_??? << 16);
		return excludes;
	}

	@Override
	public ParseTree getParseTree() {
		return builder.tree;
	}

	@Override
	public ParseRecord getParseData() {
		return builder;
	}

	@Override
	public List<String> getRuleNames() {
		if (builder == null) {
			builder = new Builder();
			fillCollector("");
		}
		return Arrays.asList(builder.parser.getRuleNames());
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<String> getTokenNames() {
		if (builder == null) {
			builder = new Builder();
			fillCollector("");
		}
		return Arrays.asList(builder.lexer.getTokenNames());
	}
}