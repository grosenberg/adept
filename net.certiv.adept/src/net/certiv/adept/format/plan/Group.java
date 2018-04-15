package net.certiv.adept.format.plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.certiv.adept.format.FormatterOps;
import net.certiv.adept.format.plan.enums.Align;
import net.certiv.adept.lang.AdeptToken;
import net.certiv.adept.unit.AdeptComp;
import net.certiv.adept.unit.TableMultilist;
import net.certiv.adept.unit.TreeMultilist;

public class Group {

	// key=align type; index=line number; value=set of tokens in align group
	private final TableMultilist<Align, Integer, AdeptToken> group = new TableMultilist<>();
	private boolean updated;

	public Group() {
		group.setValueComp(AdeptComp.Instance);
	}

	public Group(Align align, int line, AdeptToken token) {
		this();
		addGroupMembers(align, line, token);
	}

	public void addGroupMembers(Align align, int line, AdeptToken... tokens) {
		List<AdeptToken> tmp = new ArrayList<>();
		Collections.addAll(tmp, tokens);
		group.put(align, line, tmp);
	}

	public void addGroupMembers(Align align, int line, List<AdeptToken> tokens) {
		group.put(align, line, tokens);
	}

	/**
	 * Update the line number/token association of the group to reflect any current in-progress
	 * formatting TextEdits.
	 */
	public void update(FormatterOps ops) {
		if (!updated) {
			for (Align align : group.keySet()) {
				TreeMultilist<Integer, AdeptToken> parsed = group.get(align);
				TreeMultilist<Integer, AdeptToken> modded = ops.modLines(parsed);

				group.remove(align);
				group.put(align, modded);
			}
			updated = true;
		}
	}

	public TableMultilist<Align, Integer, AdeptToken> getMembers() {
		return group;
	}

	// --------------------------------------------------

	public TreeMultilist<Integer, AdeptToken> get(Align align) {
		return group.get(align);
	}

	public int lastLine(Align align) {
		TreeMultilist<Integer, AdeptToken> sub = group.get(align);
		return sub.lastKey();
	}

	public boolean contiguous(Align align, int line) {
		TreeMultilist<Integer, AdeptToken> sub = group.get(align);
		return sub.firstKey() - 1 == line || sub.lastKey() + 1 == line;
	}
}