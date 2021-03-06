/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.adept.lang.java.parser;

import net.certiv.adept.format.prep.Aligner;
import net.certiv.adept.lang.java.parser.gen.Java8ParserBaseListener;

public class JavaAlignVisitor extends Java8ParserBaseListener {

	@SuppressWarnings("unused") private Aligner aligner;

	public JavaAlignVisitor(Aligner aligner) {
		this.aligner = aligner;
	}

	// ---- Symbols ----

	// // align pair - no group
	// @Override
	// public void enterAssignStmt(AssignStmtContext ctx) {
	// aligner.align(Scheme.PAIR, ctx, ctx.EQ(), ctx.SEMI());
	// }
	//
	// // align list - no group
	// @Override
	// public void enterElements(ElementsContext ctx) {
	// if (ctx.POUND() != null) aligner.align(Scheme.LIST, ctx, ctx.POUND());
	// }
	//
	// // align guards - allow group members
	// @Override
	// public void enterRuleBlock(RuleBlockContext ctx) {
	// aligner.groupBeg(ctx);
	// aligner.align(Scheme.GROUP, ctx, ctx.COLON(), ctx.SEMI());
	// }
	//
	// // align guards - allow group members
	// @Override
	// public void enterAltBlock(AltBlockContext ctx) {
	// aligner.groupBeg(ctx);
	// aligner.align(Scheme.GROUP, ctx, ctx.LPAREN(), ctx.RPAREN());
	// }
	//
	// // align list - include in parent group
	// @Override
	// public void enterAltList(AltListContext ctx) {
	// if (ctx.OR() != null) aligner.align(Scheme.GROUP, ctx.getParent(), ctx.OR());
	// }
	//
	// // ----
	//
	// @Override
	// public void exitAltBlock(AltBlockContext ctx) {
	// aligner.groupEnd(ctx);
	// }
	//
	// @Override
	// public void exitRuleBlock(RuleBlockContext ctx) {
	// aligner.groupEnd(ctx);
	// }
}
