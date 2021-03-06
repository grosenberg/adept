/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.adept.lang.java.parser;

import org.antlr.v4.runtime.ParserRuleContext;

import net.certiv.adept.lang.Builder;
import net.certiv.adept.lang.java.parser.gen.Java8ParserBaseListener;

public class JavaFeatureVisitor extends Java8ParserBaseListener {

	private Builder builder;

	public JavaFeatureVisitor(Builder builder) {
		this.builder = builder;
	}

	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
		builder.extractFeatures(ctx);
	}
}
