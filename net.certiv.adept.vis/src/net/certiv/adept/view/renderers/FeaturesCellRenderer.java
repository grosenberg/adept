package net.certiv.adept.view.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import net.certiv.adept.model.Kind;
import net.certiv.adept.view.models.BaseTableModel;
import net.certiv.adept.view.models.FeatureTableModel;

public class FeaturesCellRenderer extends BaseCellRenderer {

	public FeaturesCellRenderer(FeatureTableModel model, int alignment) {
		super(model, alignment);
	}

	@Override
	public Component adjustColors(Component c, JTable table, BaseTableModel model, boolean isSelected, boolean hasFocus,
			int row, int column) {

		if (isSelected) {
			c.setForeground(Color.white);
			c.setBackground(new Color(100, 150, 250));

		} else {
			int mRow = table.convertRowIndexToModel(row);
			Object kind = model.getValueAt(mRow, 2); // kind

			if (kind.equals(Kind.LINECOMMENT.toString())) {
				c.setForeground(new Color(0, 145, 75));
			} else if (kind.equals(Kind.BLOCKCOMMENT.toString())) {
				c.setForeground(new Color(15, 115, 205));
			} else if (kind.equals(Kind.TERMINAL.toString())) {
				c.setForeground(Color.black);
			} else {
				c.setForeground(Color.black);
			}
			c.setBackground(Color.white);
		}
		return c;
	}
}
