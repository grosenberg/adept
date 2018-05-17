package net.certiv.adept.lang.java.parser;

import net.certiv.adept.format.plan.Indenter;
import net.certiv.adept.lang.java.parser.gen.JavaParser.BlockContext;
import net.certiv.adept.lang.java.parser.gen.JavaParser.CompilationUnitContext;
import net.certiv.adept.lang.java.parser.gen.JavaParserBaseListener;

public class JavaIndentVisitor extends JavaParserBaseListener {

	private Indenter indenter;

	public JavaIndentVisitor(Indenter indenter) {
		this.indenter = indenter;
	}

	// ---- Statements ----

	// @Override
	// public void enterPrimary(PrimaryContext ctx) {
	// indenter.statement(indenter.first(ctx.keyword()), ctx.SEMI());
	// }
	//
	// @Override
	// public void enterBlock(BlockContext ctx) {
	// // indenter.statement(indenter.first(ctx.keyword()), indenter.last(ctx.keyword()));
	// }
	//
	// @Override
	// public void enterAtBlock(AtBlockContext ctx) {
	// indenter.statement(ctx.AT(), indenter.before(ctx.action()));
	// }
	//
	// @Override
	// public void enterRuleSpec(RuleSpecContext ctx) {
	// indenter.statement(indenter.first(ctx.prefix(), ctx.id()), indenter.before(ctx.ruleBlock()));
	// }
	//
	// // ---- Bodies ----
	//
	@Override
	public void enterBlock(BlockContext ctx) {
		indenter.indent(ctx.LBRACE(), ctx.RBRACE());
	}

	// @Override
	// public void enterRuleBlock(RuleBlockContext ctx) {
	// indenter.indent(ctx.COLON(), ctx.SEMI());
	// }
	//
	// @Override
	// public void enterBody(BodyContext ctx) {
	// indenter.indent(ctx.LBRACE(), ctx.RBRACE());
	// }
	//
	// @Override
	// public void enterAltBlock(AltBlockContext ctx) {
	// indenter.indent(ctx.LPAREN(), ctx.RPAREN());
	// }
	//
	// @Override
	// public void enterAction(ActionContext ctx) {
	// indenter.indent(ctx.BEG_ACTION(), ctx.END_ACTION());
	// }

	// ---- Done ----

	@Override
	public void exitCompilationUnit(CompilationUnitContext ctx) {
		indenter.finalize(ctx);
	}
}