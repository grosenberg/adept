/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Myers Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.adept.vis.panels;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import net.certiv.adept.core.CoreMgr;
import net.certiv.adept.format.TextEdit;
import net.certiv.adept.lang.AdeptToken;
import net.certiv.adept.model.Feature;
import net.certiv.adept.model.RefToken;
import net.certiv.adept.vis.FormatView;

public class FormatInfoPanel extends JPanel {

	private FormatPerfPanel perfPane;
	private FormatEditPanel editPane;
	private FormatRealPanel srcPane;
	private FormatRealPanel matchPane;

	/**
	 * Create the panel.
	 */
	public FormatInfoPanel() {
		super();
		setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("195px"),
						FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, },
				new RowSpec[] { FormSpecs.UNRELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, }));

		perfPane = new FormatPerfPanel();
		add(perfPane, "3, 2, 1, 3");
		perfPane.setBorder(new TitledBorder(null, "Performance", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		srcPane = new FormatRealPanel();
		add(srcPane, "4, 2");
		srcPane.setBorder(new TitledBorder(null, "Source", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		editPane = new FormatEditPanel();
		add(editPane, "6, 2, default, top");
		editPane.setBorder(new TitledBorder(null, "Edits", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		matchPane = new FormatRealPanel();
		add(matchPane, "4, 4");
		matchPane.setBorder(new TitledBorder(null, "Matched", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	}

	public FormatInfoPanel(FormatView view) {
		this();
		srcPane.hideSim();
		matchPane.hideAncestors();
		matchPane.hideLineCol();
	}

	public void loadPerfData(CoreMgr mgr) {
		perfPane.loadPerfData(mgr);
	}

	public void loadData(Feature feature, AdeptToken token, RefToken ref, RefToken matched, double sim, TextEdit ledit,
			TextEdit redit) {

		srcPane.loadData(1, feature, token, ref, matched, sim, ledit, redit);
		matchPane.loadData(2, feature, token, ref, matched, sim, ledit, redit);
		editPane.loadData(ref, matched, ledit, redit);
	}

	public void clearData() {
		srcPane.clearData();
		matchPane.clearData();
		editPane.clearData();
	}

	public void clearAll() {
		perfPane.clearAll();
	}
}
