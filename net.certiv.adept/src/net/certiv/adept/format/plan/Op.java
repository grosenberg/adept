/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.adept.format.plan;

// Mark operations
public enum Op {
	ROOT,	// 0 indent level (force)

	IN,		// indent
	OUT,	// dedent

	BEG,	// wrappable statement
	END;
}
