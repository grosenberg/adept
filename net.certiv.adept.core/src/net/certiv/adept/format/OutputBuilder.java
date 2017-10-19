package net.certiv.adept.format;

import org.antlr.v4.runtime.Token;

import net.certiv.adept.util.Strings;

public class OutputBuilder {

	private final StringBuilder sb = new StringBuilder();

	public OutputBuilder() {}

	public void add(Token token) {
		sb.append(token.getText());
	}

	public void add(String text) {
		sb.append(text);
	}

	public void flush() {}

	public void ensureEndEol() {
		int dot = sb.lastIndexOf(Strings.EOL);
		dot = Math.max(dot, 0);
		String last = sb.substring(dot).trim();
		if (!last.isEmpty()) {
			sb.append(Strings.EOL);
		}
	}

	public boolean isEmpty() {
		return sb.length() == 0;
	}

	public void clear() {
		sb.setLength(0);
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
