/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.adept.lang.comment.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import net.certiv.adept.Tool;
import net.certiv.adept.format.CommentProcessor;
import net.certiv.adept.lang.AdeptToken;
import net.certiv.adept.lang.comment.parser.gen.CommentLexer;
import net.certiv.adept.lang.comment.parser.gen.CommentParser;
import net.certiv.adept.lang.comment.parser.gen.CommentParser.CommentContext;
import net.certiv.adept.tool.ErrorType;
import net.certiv.adept.util.Log;

public class CommentSourceParser extends BaseErrorListener {

	private CommentVisitor visitor;
	private int errCnt;

	public CommentSourceParser(CommentProcessor helper) {
		visitor = new CommentVisitor(helper);
	}

	public boolean process(AdeptToken comment) {
		CommentContext tree = null;
		errCnt = 0;
		try {
			CodePointCharStream input = CharStreams.fromString(comment.getText());
			CommentLexer lexer = new CommentLexer(input);
			lexer.removeErrorListeners();
			lexer.addErrorListener(this);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			CommentParser parser = new CommentParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(this);
			tree = parser.comment();

		} catch (RecognitionException e) {
			Log.error(this, ErrorType.PARSE_ERROR.msg + ": " + comment.toString());
			Tool.errMgr.toolError(ErrorType.PARSE_ERROR, comment.toString());
			return false;

		} catch (Exception e) {
			Log.error(this, ErrorType.PARSE_FAILURE.msg + ": " + comment.toString());
			Tool.errMgr.toolError(ErrorType.PARSE_FAILURE, e, comment.toString());
			return false;

		} finally {
			if (errCnt > 0 || tree == null || tree instanceof ErrorNode) {
				Tool.errMgr.toolError(ErrorType.PARSE_ERROR, "Bad parse tree: " + comment);
				return false;
			}
		}

		ParseTreeWalker.DEFAULT.walk(visitor, tree);
		return true;
	}

	private static final String ErrMsg = "Comment %s error @%s:%s %s";

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {

		errCnt++;
		String source = recognizer instanceof Parser ? "parser" : "lexer";
		String errMsg = String.format(ErrMsg, source, line, charPositionInLine, msg);
		Log.error(this, errMsg);
	}

	public void dispose() {
		visitor = null;
	}
}
