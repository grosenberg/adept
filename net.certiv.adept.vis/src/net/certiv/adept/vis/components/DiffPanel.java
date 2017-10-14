package net.certiv.adept.vis.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import net.certiv.adept.vis.utils.Point;
import net.certiv.adept.vis.utils.TextUtils;

public class DiffPanel extends JPanel {

	public static final String CLICK1_LEFT = "click1_left";

	private int width;
	private int height;

	private String lhsTitle;
	private String rhsTitle;
	private JTextPane lhs;
	private JTextPane rhs;

	public DiffPanel(int width, int height, FontChoiceBox fontBox, JComboBox<Integer> sizeBox, String lhsTitle,
			String rhsTitle) {
		super();
		this.width = width;
		this.height = height;
		this.lhsTitle = lhsTitle;
		this.rhsTitle = rhsTitle;

		create();
		fontBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Font font = (Font) e.getItem();
				float size = getFont().getSize();
				font = font.deriveFont(size);
				updateDocFont(lhs, font);
				updateDocFont(rhs, font);
			}
		});
		sizeBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				float size = (int) e.getItem();
				Font font = getFont();
				font = font.deriveFont(size);
				updateDocFont(lhs, font);
				updateDocFont(rhs, font);
			}
		});
	}

	protected void updateDocFont(JTextPane pane, Font font) {
		MutableAttributeSet attrs = pane.getInputAttributes();
		StyleConstants.setFontFamily(attrs, font.getFamily());
		StyleConstants.setFontSize(attrs, font.getSize());
		StyleConstants.setItalic(attrs, (font.getStyle() & Font.ITALIC) != 0);
		StyleConstants.setBold(attrs, (font.getStyle() & Font.BOLD) != 0);
		StyledDocument doc = pane.getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
	}

	private void create() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 2));

		JPanel lhsPanel = new JPanel();
		JPanel rhsPanel = new JPanel();
		lhsPanel.setLayout(new BorderLayout());
		rhsPanel.setLayout(new BorderLayout());

		lhs = new JTextPane();
		lhs.setSize(width, height);
		lhs.setEditable(false);
		lhs.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				try {
					Document doc = lhs.getDocument();
					int line = TextUtils.getLineOfOffset(doc, e.getDot()); 			// 0..n
					int col = e.getDot() - TextUtils.getLineStartOffset(doc, line); // 0..n
					firePropertyChange(CLICK1_LEFT, null, new Point(col, line));
				} catch (BadLocationException e1) {}
			}
		});

		rhs = new JTextPane();
		rhs.setSize(width, height);
		rhs.setEditable(false);

		JScrollPane lhsScroll = new JScrollPane(lhs);
		TextLineNumber lhsNums = new TextLineNumber(lhs);
		lhsNums.setUpdateFont(true);
		lhsScroll.setRowHeaderView(lhsNums);
		lhsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		lhsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		JScrollPane rhsScroll = new JScrollPane(rhs);
		TextLineNumber rhsNums = new TextLineNumber(rhs);
		rhsNums.setUpdateFont(true);
		rhsScroll.setRowHeaderView(rhsNums);
		rhsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		rhsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		new Synchronizer(lhsScroll, rhsScroll);

		JLabel lhsLabel = new JLabel(lhsTitle, JLabel.CENTER);
		lhsLabel.setForeground(Color.black);

		JLabel rhsLabel = new JLabel(rhsTitle, JLabel.CENTER);
		rhsLabel.setForeground(Color.black);

		lhsPanel.add(lhsLabel, BorderLayout.NORTH);
		lhsPanel.add(lhsScroll, BorderLayout.CENTER);

		rhsPanel.add(rhsLabel, BorderLayout.NORTH);
		rhsPanel.add(rhsScroll, BorderLayout.CENTER);

		mainPanel.add(lhsPanel);
		mainPanel.add(rhsPanel);

		setLayout(new BorderLayout());
		add(mainPanel);
	}

	public void setTabStops(int lhsTabWidth, int rhsTabWidth) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		FontMetrics fm = lhs.getFontMetrics(getFont());

		TabSet tabs = calcTabSet(fm, lhsTabWidth);
		AttributeSet attrs = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabs);
		lhs.setParagraphAttributes(attrs, true);

		tabs = calcTabSet(fm, rhsTabWidth);
		attrs = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabs);
		rhs.setParagraphAttributes(attrs, true);
	}

	private TabSet calcTabSet(FontMetrics fm, int tabWidth) {
		int px = fm.getMaxAdvance();
		TabStop[] tabs = new TabStop[20];
		for (int idx = 0; idx < 20; idx++) {
			tabs[idx] = new TabStop((idx + 1) * tabWidth * px);
		}
		return new TabSet(tabs);
	}

	public void clear() {
		load("", "");
	}

	public void load(String lhsContent, String rhsContent) {
		lhs.setText(lhsContent);
		rhs.setText(rhsContent);
		// rhs.setText(diff(lhsContent, rhsContent));
		lhs.setCaretPosition(0);
	}

	@SuppressWarnings("unused")
	private String diff(String lhsContent, String rhsContent) {
		Patch<String> patch = DiffUtils.diff(Arrays.asList(lhsContent), Arrays.asList(rhsContent));
		try {
			return DiffUtils.patch(Arrays.asList(lhsContent), patch).get(0);
		} catch (PatchFailedException e) {
			return "";
		}
	}

	private class Synchronizer implements AdjustmentListener {

		private JScrollBar v1, h1, v2, h2;

		public Synchronizer(JScrollPane sp1, JScrollPane sp2) {
			v1 = sp1.getVerticalScrollBar();
			h1 = sp1.getHorizontalScrollBar();
			v2 = sp2.getVerticalScrollBar();
			h2 = sp2.getHorizontalScrollBar();

			v1.addAdjustmentListener(this);
			h1.addAdjustmentListener(this);
			v2.addAdjustmentListener(this);
			h2.addAdjustmentListener(this);
		}

		public void adjustmentValueChanged(AdjustmentEvent e) {
			JScrollBar scrollBar = (JScrollBar) e.getSource();
			int value = scrollBar.getValue();
			JScrollBar target = null;

			if (scrollBar == v1) target = v2;
			if (scrollBar == h1) target = h2;
			if (scrollBar == v2) target = v1;
			if (scrollBar == h2) target = h1;

			target.setValue(value);
		}
	}
}
