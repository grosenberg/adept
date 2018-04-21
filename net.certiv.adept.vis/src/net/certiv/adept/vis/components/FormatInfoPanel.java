package net.certiv.adept.vis.components;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import net.certiv.adept.core.CoreMgr;
import net.certiv.adept.core.util.Facet;
import net.certiv.adept.format.TextEdit;
import net.certiv.adept.lang.ParseRecord;
import net.certiv.adept.model.Feature;
import net.certiv.adept.model.RefToken;
import net.certiv.adept.util.Refs;
import net.certiv.adept.util.Strings;
import net.certiv.adept.util.Time;

public class FormatInfoPanel extends JPanel {

	private static final String MsFmt = "%s ms";

	private JTextField txtExecute;
	private JTextField txtLoad;
	private JTextField txtBuild;
	private JTextField txtFormat;

	private JTextField txtCorpusFeatures;
	private JTextField txtCorpusRefs;
	private JTextField txtDocFeatures;

	private JTextField txtCol;
	private JTextField txtLine;
	private JTextField txtToken;
	private JTextField txtVisCol;
	private JTextField txtAncestors;

	private JTextField txtMPlace;
	private JTextField txtMDent;
	private JTextField txtMAlignment;
	private JTextField txtMToken;
	private JTextField txtMSpacing;
	private JTextField txtMSimularity;
	private JTextField txtDent;
	private JTextField txtDocRefs;
	private JTextField txtMatch;
	private JTextField txtParse;
	private JTextField txtPlace;
	private JTextField txtSpacing;
	private JTextField txtAlignment;
	private JTextField txtRExist;
	private JTextField txtRRepl;
	private JTextField txtLExist;
	private JTextField txtLRepl;

	public FormatInfoPanel() {
		setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(34dlu;default)"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.UNRELATED_GAP_COLSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(40dlu;default)"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(30dlu;default)"), FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default)"), FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default)"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
				ColumnSpec.decode("default:grow"), },
				new RowSpec[] { FormSpecs.UNRELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.UNRELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.UNRELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, }));

		JLabel lblPerformance = new JLabel("Performance");
		lblPerformance.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblPerformance, "2, 2, 5, 1, left, default");

		JLabel lblDocLine = new JLabel("Line");
		add(lblDocLine, "12, 2, center, default");

		JLabel lblDocCol = new JLabel("Col");
		add(lblDocCol, "14, 2, center, default");

		JLabel lblVisCol = new JLabel("Visual Col");
		add(lblVisCol, "16, 2, center, default");

		JLabel lblLoad = new JLabel("Load");
		add(lblLoad, "2, 4, right, default");

		txtLoad = new JTextField();
		txtLoad.setColumns(10);
		txtLoad.setEditable(false);
		add(txtLoad, "4, 4, 3, 1, fill, default");

		JLabel lblSource = new JLabel("Source");
		lblSource.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblSource, "10, 4");

		txtLine = new JTextField();
		txtLine.setHorizontalAlignment(SwingConstants.CENTER);
		txtLine.setColumns(5);
		txtLine.setEditable(false);
		add(txtLine, "12, 4, center, default");

		txtCol = new JTextField();
		txtCol.setHorizontalAlignment(SwingConstants.CENTER);
		txtCol.setColumns(5);
		txtCol.setEditable(false);
		add(txtCol, "14, 4, fill, default");

		txtVisCol = new JTextField();
		txtVisCol.setHorizontalAlignment(SwingConstants.CENTER);
		txtVisCol.setColumns(5);
		txtVisCol.setEditable(false);
		add(txtVisCol, "16, 4, fill, default");

		JLabel lblRebuildTime = new JLabel("Build");
		add(lblRebuildTime, "2, 6, right, default");

		txtBuild = new JTextField();
		txtBuild.setColumns(10);
		txtBuild.setEditable(false);
		add(txtBuild, "4, 6, 3, 1, fill, default");

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		add(separator, "8, 2, 1, 29");

		JLabel lblAncestors = new JLabel("Ancestors");
		add(lblAncestors, "10, 6, right, default");

		txtAncestors = new JTextField();
		txtAncestors.setEditable(false);
		add(txtAncestors, "12, 6, 13, 1, fill, default");

		JLabel lblParse = new JLabel("Parse");
		add(lblParse, "2, 8, right, default");

		txtParse = new JTextField();
		txtParse.setEditable(false);
		add(txtParse, "4, 8, 3, 1, fill, default");
		txtParse.setColumns(10);

		JLabel lblNewLabel = new JLabel("Token");
		add(lblNewLabel, "10, 8, right, default");

		txtToken = new JTextField();
		txtToken.setEditable(false);
		add(txtToken, "12, 8, 5, 1, fill, default");

		txtPlace = new JTextField();
		txtPlace.setEditable(false);
		add(txtPlace, "18, 8, fill, default");
		txtPlace.setColumns(8);

		txtDent = new JTextField();
		txtDent.setColumns(16);
		txtDent.setEditable(false);
		add(txtDent, "20, 8, 5, 1, fill, default");

		JLabel lblMatching = new JLabel("Match");
		add(lblMatching, "2, 10, right, default");

		txtMatch = new JTextField();
		txtMatch.setEditable(false);
		add(txtMatch, "4, 10, 3, 1, fill, default");
		txtMatch.setColumns(10);

		JLabel lblSpacing = new JLabel("Spacing");
		add(lblSpacing, "10, 10, right, default");

		txtSpacing = new JTextField();
		txtSpacing.setEditable(false);
		add(txtSpacing, "12, 10, 13, 1, fill, default");
		txtSpacing.setColumns(10);

		JLabel lblFormatting = new JLabel("Format");
		add(lblFormatting, "2, 12, right, default");

		txtFormat = new JTextField();
		txtFormat.setEditable(false);
		add(txtFormat, "4, 12, 3, 1, fill, default");
		txtFormat.setColumns(10);

		JLabel lblAlignment = new JLabel("Alignment");
		add(lblAlignment, "10, 12, right, default");

		txtAlignment = new JTextField();
		txtAlignment.setEditable(false);
		add(txtAlignment, "12, 12, 13, 1, fill, default");
		txtAlignment.setColumns(10);

		JLabel lblExecute = new JLabel("Execute");
		add(lblExecute, "2, 14, right, default");

		txtExecute = new JTextField();
		txtExecute.setColumns(10);
		txtExecute.setEditable(false);
		add(txtExecute, "4, 14, 3, 1, fill, default");

		// --

		JLabel lblMatched = new JLabel("Matched");
		lblMatched.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblMatched, "10, 16, left, default");

		JLabel lblDocFeatures = new JLabel("Features");
		add(lblDocFeatures, "4, 18, center, default");

		JLabel lblRefTokens = new JLabel("Refs");
		add(lblRefTokens, "6, 18, center, default");

		JLabel lblSimilarity = new JLabel("Similarity");
		add(lblSimilarity, "10, 18, right, default");

		txtMSimularity = new JTextField();
		txtMSimularity.setEditable(false);
		add(txtMSimularity, "12, 18, 3, 1, fill, default");

		JLabel lblDocument = new JLabel("Source");
		lblDocument.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblDocument, "2, 20, left, default");

		txtDocFeatures = new JTextField();
		txtDocFeatures.setColumns(5);
		txtDocFeatures.setEditable(false);
		add(txtDocFeatures, "4, 20, fill, default");

		txtDocRefs = new JTextField();
		txtDocRefs.setColumns(5);
		txtDocRefs.setEditable(false);
		add(txtDocRefs, "6, 20, fill, default");

		JLabel lblMToken = new JLabel("Token");
		add(lblMToken, "10, 20, right, default");

		txtMToken = new JTextField();
		txtMToken.setEditable(false);
		add(txtMToken, "12, 20, 5, 1, fill, default");

		txtMPlace = new JTextField();
		txtMPlace.setEditable(false);
		add(txtMPlace, "18, 20, fill, default");
		txtMPlace.setColumns(8);

		txtMDent = new JTextField();
		txtMDent.setEditable(false);
		add(txtMDent, "20, 20, 5, 1, fill, default");
		txtMDent.setColumns(16);

		JLabel lblCorpus = new JLabel("Corpus");
		lblCorpus.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblCorpus, "2, 22, left, default");

		txtCorpusFeatures = new JTextField();
		txtCorpusFeatures.setColumns(5);
		txtCorpusFeatures.setEditable(false);
		add(txtCorpusFeatures, "4, 22, fill, default");

		txtCorpusRefs = new JTextField();
		txtCorpusRefs.setColumns(5);
		txtCorpusRefs.setEditable(false);
		add(txtCorpusRefs, "6, 22, fill, default");

		JLabel lblMSpacing = new JLabel("Spacing");
		add(lblMSpacing, "10, 22, right, default");

		txtMSpacing = new JTextField();
		txtMSpacing.setEditable(false);
		add(txtMSpacing, "12, 22, 13, 1, fill, default");

		JLabel lblMAlignment = new JLabel("Alignment");
		add(lblMAlignment, "10, 24, right, default");

		txtMAlignment = new JTextField();
		txtMAlignment.setEditable(false);
		add(txtMAlignment, "12, 24, 13, 1, fill, default");
		txtMAlignment.setColumns(5);

		JLabel lblEdits = new JLabel("Edits");
		lblEdits.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblEdits, "10, 28");

		JLabel lblNewLabel_2 = new JLabel("Left");
		add(lblNewLabel_2, "12, 28");

		JLabel lblNewLabel_3 = new JLabel("Right");
		add(lblNewLabel_3, "20, 28");

		JLabel lblFrom = new JLabel("From");
		add(lblFrom, "10, 30, right, default");

		txtLExist = new JTextField();
		txtLExist.setEditable(false);
		add(txtLExist, "12, 30, 5, 1, fill, default");
		txtLExist.setColumns(10);

		JLabel lblFrom_1 = new JLabel("From");
		add(lblFrom_1, "18, 30, right, default");

		txtRExist = new JTextField();
		txtRExist.setEditable(false);
		add(txtRExist, "20, 30, 5, 1, fill, default");
		txtRExist.setColumns(20);

		JLabel lblTo = new JLabel("To");
		add(lblTo, "10, 32, right, default");

		txtLRepl = new JTextField();
		txtLRepl.setEditable(false);
		add(txtLRepl, "12, 32, 5, 1, fill, default");
		txtLRepl.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("To");
		add(lblNewLabel_1, "18, 32, right, default");

		txtRRepl = new JTextField();
		txtRRepl.setEditable(false);
		add(txtRRepl, "20, 32, 5, 1, fill, default");
		txtRRepl.setColumns(40);
	}

	public void loadPerfData(CoreMgr mgr) {
		ParseRecord rec = mgr.getDocModel().getParseRecord();
		Refs.setup(rec.getRuleNames(), rec.getTokenNames());

		txtLoad.setText(Time.elapsed(Facet.LOAD, MsFmt));
		txtBuild.setText(Time.elapsed(Facet.BUILD, MsFmt));
		txtParse.setText(Time.elapsed(Facet.PARSE, MsFmt));
		txtMatch.setText(Time.elapsed(Facet.MATCH, MsFmt));
		txtFormat.setText(Time.elapsed(Facet.FORMAT, MsFmt));
		txtExecute.setText(Time.elapsed(Facet.EXECUTE, MsFmt));

		txtDocFeatures.setText(String.valueOf(mgr.getDocModel().getFeaturesCount()));
		txtDocRefs.setText(String.valueOf(mgr.getDocModel().getTokenRefsCount()));

		txtCorpusFeatures.setText(String.valueOf(mgr.getCorpusModel().getCorpusFeaturesCount()));
		txtCorpusRefs.setText(String.valueOf(mgr.getCorpusModel().getCorpusRefTokensCount()));
	}

	public void loadData(Feature feature, RefToken ref, RefToken matched, double sim, TextEdit ledit, TextEdit redit) {
		txtLine.setText(String.valueOf(ref.line + 1));
		txtCol.setText(String.valueOf(ref.col + 1));
		txtVisCol.setText(String.valueOf(ref.visCol));

		txtPlace.setText(Refs.tPlace(ref));
		txtDent.setText(ref.dent.toString());
		txtAncestors.setText(Refs.evalAncestors(feature.getAncestors()));
		txtToken.setText(Refs.fType(ref.type));
		txtAlignment.setText(Refs.tAlign(ref));
		txtSpacing.setText(Refs.tSpace(ref));

		if (matched != null) {
			txtMPlace.setText(Refs.tPlace(matched));
			txtMDent.setText(matched.dent.toString());
			txtMToken.setText(Refs.fType(matched.type));
			txtMAlignment.setText(Refs.tAlign(matched));
			txtMSpacing.setText(Refs.tSpace(matched));
			txtMSimularity.setText(String.valueOf(sim));
		} else {
			clearMatched();
		}

		if (ledit != null) {
			txtLExist.setText(Strings.encodeWS(ledit.existing()));
			txtLRepl.setText(Strings.encodeWS(ledit.replacement()));
		} else {
			txtLExist.setText("");
			txtLRepl.setText("");
		}

		if (redit != null) {
			txtRExist.setText(Strings.encodeWS(redit.existing()));
			txtRRepl.setText(Strings.encodeWS(redit.replacement()));
		} else {
			txtRExist.setText("");
			txtRRepl.setText("");
		}
	}

	public void clearAll() {
		txtLoad.setText("");
		txtBuild.setText("");
		txtParse.setText("");
		txtMatch.setText("");
		txtFormat.setText("");
		txtExecute.setText("");

		txtDocFeatures.setText("");
		txtDocRefs.setText("");
		txtCorpusFeatures.setText("");
		txtCorpusRefs.setText("");

		clearData();
	}

	public void clearData() {
		txtLine.setText("");
		txtCol.setText("");
		txtVisCol.setText("");

		txtPlace.setText("");
		txtDent.setText("");
		txtAncestors.setText("");
		txtToken.setText("");
		txtAlignment.setText("");
		txtSpacing.setText("");

		clearMatched();
	}

	private void clearMatched() {
		txtMPlace.setText("");
		txtMDent.setText("");
		txtMAlignment.setText("");
		txtMToken.setText("");
		txtMSpacing.setText("");
		txtMSimularity.setText("");
	}
}
