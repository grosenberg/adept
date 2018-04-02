package net.certiv.adept.view.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.Token;

import net.certiv.adept.format.align.Align;
import net.certiv.adept.model.RefToken;

public class RefUtils {

	private static final String DentMsg = "%s (%s: %s)";
	private static final String AlignMsg = "%s {%s}  %s:%s (%s)";
	private static final String SpaceMsg = "%s  %s  <  %s  >  %s  %s";
	private static final String LocMsg = "@%s:%s <%s>";

	private static List<String> _ruleNames;
	private static List<String> _tokenNames;

	public static void set(List<String> ruleNames, List<String> tokenNames) {
		_ruleNames = ruleNames;
		_tokenNames = tokenNames;
	}

	public static String tPlace(RefToken ref) {
		return ref.place.toString();
	}

	public static String tIndent(RefToken ref) {
		return String.format(DentMsg, ref.dent.indents, ref.dent.op, ref.dent.bind);
	}

	public static String tAlign(RefToken ref) {
		if (ref.align == Align.NONE) return "None --";
		return String.format(AlignMsg, ref.align, ref.gap, ref.inGroup, ref.inLine, ref.grpTotal);
	}

	public static String tSpace(RefToken ref) {
		return String.format(SpaceMsg, fType(ref.lType), ref.lSpacing, fType(ref.type), ref.rSpacing, fType(ref.rType));
	}

	public static String tLocation(RefToken ref) {
		return String.format(LocMsg, ref.line, ref.col, ref.visCol);
	}

	public static String tText(int type, String text) {
		return fType(type) + " " + text;
	}

	public static String fType(int type) {
		String name = "";
		switch (type) {
			case 0:
				name = "BOF";
				break;
			case -1:
				name = "EOF";
				break;
			default:
				name = _tokenNames.get(type);
		}
		return String.format("%s (%s)", name, type);
	}

	public static String sType(int type) {
		String name = type == Token.EOF ? "EOF" : _tokenNames.get(type);
		return String.format("%s", name);
	}

	public static String tAssoc(RefToken ref) {
		StringBuilder sb = new StringBuilder();
		if (ref.lAssocs != null) {
			List<Integer> lAssocs = new ArrayList<>(ref.lAssocs);
			Collections.reverse(lAssocs);
			sb.append(evalTokens(lAssocs, false));
		} else {
			sb.append("BOF");
		}

		sb.append(" > " + sType(ref.type) + " > ");

		if (ref.rAssocs != null) {
			sb.append(evalTokens(ref.rAssocs, false));
		} else {
			sb.append("EOF");
		}
		return sb.toString();
	}

	public static String evalAncestors(List<Integer> rules) {
		StringBuilder sb = new StringBuilder();
		for (int rule : rules) {
			sb.append(_ruleNames.get(rule) + " > ");
		}
		sb.setLength(sb.length() - 3);
		return sb.toString();
	}

	public static String evalTokens(Collection<Integer> indexes, boolean showType) {
		StringBuilder sb = new StringBuilder();
		for (int index : indexes) {
			String name = "";
			switch (index) {
				case -1:
					name = "EOF";
					break;
				case 0:
					name = "";
					break;
				default:
					name = _tokenNames.get(index);
			}
			sb.append(name);
			if (showType) sb.append(String.format(" [%s], ", index));
			sb.append(" | ");
		}
		if (sb.length() > 1) sb.setLength(sb.length() - 3);
		return sb.toString();
	}
}
