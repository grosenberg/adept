package net.certiv.adept.xvisitor.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import net.certiv.adept.Tool;
import net.certiv.adept.model.Document;
import net.certiv.adept.parser.AdeptTokenFactory;
import net.certiv.adept.parser.Collector;
import net.certiv.adept.parser.ISourceParser;
import net.certiv.adept.parser.ParserErrorListener;
import net.certiv.adept.tool.ErrorType;
import net.certiv.adept.xvisitor.parser.gen.XVisitorLexer;
import net.certiv.adept.xvisitor.parser.gen.XVisitorParser;

public class XVisitorSourceParser implements ISourceParser {

	protected int errorCount;

	@Override
	public void process(Collector collector, Document doc) throws RecognitionException, Exception {
		ParserErrorListener errors = new ParserErrorListener(this);
		CharStream input = new ANTLRInputStream(doc.getContent());
		collector.lexer = new XVisitorLexer(input);
		collector.VWS = XVisitorLexer.VERT_WS;
		collector.HWS = XVisitorLexer.HORZ_WS;
		collector.BLOCKCOMMENT = XVisitorLexer.BLOCK_COMMENT;
		collector.LINECOMMENT = XVisitorLexer.LINE_COMMENT;

		AdeptTokenFactory factory = new AdeptTokenFactory(input);
		collector.lexer.setTokenFactory(factory);
		collector.stream = new CommonTokenStream(collector.lexer);

		collector.parser = new XVisitorParser(collector.stream);
		collector.parser.setTokenFactory(factory);
		collector.parser.removeErrorListeners();
		collector.parser.addErrorListener(errors);
		collector.tree = ((XVisitorParser) collector.parser).grammarSpec();

		collector.errCount = errorCount;

		if (collector.tree == null || collector.tree instanceof ErrorNode || collector.errCount > 0) {
			Tool.errMgr.toolError(ErrorType.PARSE_ERROR, "Bad parse tree: " + doc.getPathname());
		}
	}

	@Override
	public void reportRecognitionError(Token offendingToken, int errorIdx, int line, int col, String msg,
			RecognitionException e) {
		errorCount++;	// TODO: record location and extent of errors in the model
	}

	@Override
	public void annotate(Collector collector) {
		ParseTreeWalker walker = new ParseTreeWalker();
		XVisitorVisitor visitor = new XVisitorVisitor(collector);
		walker.walk(visitor, collector.tree);

	}

	@Override
	public List<Integer> excludedTypes() {
		List<Integer> excludes = new ArrayList<>();
		excludes.add(XVisitorParser.ERRCHAR);
		// excludes.add(XVisitorParser.RULE_??? << 10);
		return excludes;
	}
}