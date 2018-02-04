package hr.fer.zemris.graphics.component.ip;

import java.awt.Component;


import java.awt.Insets;

import javax.swing.*;
import javax.swing.table.*;

public class ButtonRenderer extends JButton implements TableCellRenderer {

	public ButtonRenderer()
	{
		setOpaque(true);
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setMargin(new Insets(20, 0, 20, 0));
		setText("Delete");
		return this;
	}

}
