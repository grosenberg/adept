/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.adept.lang.antlr.parser;

import net.certiv.adept.lang.antlr.parser.gen.Antlr4Parser.ActionContext;
import net.certiv.adept.lang.antlr.parser.gen.Antlr4Parser.AltBlockContext;
import net.certiv.adept.lang.antlr.parser.gen.Antlr4Parser.AntlrContext;
import net.certiv.adept.lang.antlr.parser.gen.Antlr4Parser.AtBlockContext;
import net.certiv.adept.lang.antlr.parser.gen.Antlr4Parser.BodyContext;
import net.certiv.adept.lang.antlr.parser.gen.Antlr4Parser.CmdBlockContext;
import net.certiv.adept.lang.antlr.parser.gen.Antlr4Parser.PrimaryContext;
import net.certiv.adept.lang.antlr.parser.gen.Antlr4Parser.RuleBlockContext;
import net.certiv.adept.lang.antlr.parser.gen.Antlr4Parser.RuleSpecContext;
import net.certiv.adept.format.prep.Indenter;
import net.certiv.adept.lang.antlr.parser.gen.Antlr4ParserBaseListener;

public class IndentVisitor extends Antlr4ParserBaseListener {

	private Indenter indenter;

	public IndentVisitor(Indenter indenter) {
		this.indenter = indenter;
	}

	// ---- Statements ----

	@Override
	public void enterPrimary(PrimaryContext ctx) {
		indenter.statement(indenter.first(ctx.keyword()), ctx.SEMI());
	}

	@Override
	public void enterCmdBlock(CmdBlockContext ctx) {
		indenter.statement(indenter.first(ctx.keyword()), indenter.last(ctx.keyword()));
	}

	@Override
	public void enterAtBlock(AtBlockContext ctx) {
		indenter.statement(ctx.AT(), indenter.before(ctx.action()));
	}

	@Override
	public void enterRuleSpec(RuleSpecContext ctx) {
		indenter.statement(indenter.first(ctx.prefix(), ctx.id()), indenter.before(ctx.ruleBlock()));
	}

	// ---- Bodies ----

	@Override
	public void enterRuleBlock(RuleBlockContext ctx) {
		indenter.indent(ctx.COLON(), ctx.SEMI());
	}

	@Override
	public void enterBody(BodyContext ctx) {
		indenter.indent(ctx.LBRACE(), ctx.RBRACE());
	}

	@Override
	public void enterAltBlock(AltBlockContext ctx) {
		indenter.indent(ctx.LPAREN(), ctx.RPAREN());
	}

	@Override
	public void enterAction(ActionContext ctx) {
		indenter.indent(ctx.BEG_ACTION(), ctx.END_ACTION());
	}

	// ---- Done ----

	@Override
	public void exitAntlr(AntlrContext ctx) {
		indenter.finalize(ctx);
	}
}
