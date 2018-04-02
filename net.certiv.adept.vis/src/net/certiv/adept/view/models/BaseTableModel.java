package net.certiv.adept.view.models;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import net.certiv.adept.model.RefToken;
import net.certiv.adept.view.utils.RefUtils;

public abstract class BaseTableModel extends AbstractTableModel {

	// private static final String DentMsg = "%s (%s: %s)";
	// private static final String AlignMsg = "%s {%s} %s:%s (%s)";
	// private static final String SpaceMsg = "%s %s < %s > %s %s";
	// private static final String LocMsg = "@%s:%s <%s>";

	protected static final Comparator<Number> NumComp = new Comparator<Number>() {

		@Override
		public int compare(Number o1, Number o2) {
			if (o1.doubleValue() < o2.doubleValue()) return -1;
			if (o1.doubleValue() > o2.doubleValue()) return 1;
			return 0;
		}
	};

	// private static List<String> ruleNames;
	// private static List<String> tokenNames;

	public BaseTableModel(List<String> ruleNames, List<String> tokenNames) {
		super();
		RefUtils.set(ruleNames, tokenNames);
		// BaseTableModel.ruleNames = ruleNames;
		// BaseTableModel.tokenNames = tokenNames;
	}

	protected static String tPlace(RefToken ref) {
		return ref.place.toString();
	}

	protected static String tAssoc(RefToken ref) {
		return RefUtils.tAssoc(ref);
	}

	protected static String tIndent(RefToken ref) {
		return RefUtils.tIndent(ref);
		// return String.format(DentMsg, ref.dent.indents, ref.dent.op, ref.dent.bind);
	}

	protected static String tAlign(RefToken ref) {
		return RefUtils.tAlign(ref);
		// if (ref.align == Align.NONE) return "None --";
		// return String.format(AlignMsg, ref.align, ref.gap, ref.inGroup, ref.inLine, ref.grpTotal);
	}

	protected static String tSpace(RefToken ref) {
		return RefUtils.tSpace(ref);
		// return String.format(SpaceMsg, fType(ref.lType), ref.lSpacing, fType(ref.type), ref.rSpacing,
		// fType(ref.rType));
	}

	protected static String tLocation(RefToken ref) {
		return RefUtils.tLocation(ref);
		// return String.format(LocMsg, ref.line, ref.col, ref.visCol);
	}

	protected static String tText(int type, String text) {
		return RefUtils.fType(type);
		// return fType(type) + " " + text;
	}

	protected static String fType(int type) {
		return RefUtils.fType(type);
		// String name = "";
		// switch (type) {
		// case 0:
		// name = "BOF";
		// break;
		// case -1:
		// name = "EOF";
		// break;
		// default:
		// name = tokenNames.get(type);
		// }
		// return String.format("%s (%s)", name, type);
	}

	protected static String sType(int type) {
		return RefUtils.sType(type);
		// String name = type == Token.EOF ? "EOF" : tokenNames.get(type);
		// return String.format("%s", name);
	}

	protected static String evalAncestors(List<Integer> ancestors) {
		return RefUtils.evalAncestors(ancestors);
		// int[] rules = ancestors.stream().mapToInt(i -> i).toArray();
		// StringBuilder sb = new StringBuilder();
		// for (int rule : rules) {
		// sb.append(ruleNames.get(rule) + " > ");
		// }
		// sb.setLength(sb.length() - 3);
		// return sb.toString();
	}

	protected static String evalTokens(Set<Integer> indexes, boolean showType) {
		return RefUtils.evalTokens(indexes, showType);
		// StringBuilder sb = new StringBuilder();
		// for (int index : indexes) {
		// String name = "";
		// switch (index) {
		// case -1:
		// name = "EOF";
		// break;
		// case 0:
		// name = "";
		// break;
		// default:
		// name = tokenNames.get(index);
		// }
		// sb.append(name);
		// if (showType) sb.append(String.format(" [%s], ", index));
		// sb.append(" | ");
		// }
		// if (sb.length() > 1) sb.setLength(sb.length() - 3);
		// return sb.toString();
	}
}