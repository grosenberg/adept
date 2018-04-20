package net.certiv.adept.format.plan;

// Mark operations
public enum Op {
	ROOT,	// 0 indent level (force)

	IN,		// indent
	OUT,	// dedent

	BEG,	// wrappable statement
	END;
}