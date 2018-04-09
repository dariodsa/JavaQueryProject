package hr.fer.zemris.graphics.component;

import javax.swing.*;


public class MultiValueSliderChoose extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7276278491908404127L;
	private JComboBox<String> type = new JComboBox<>(new String[] {"SmallMove","MediumMove", "BigMove"});
	
	public MultiValueSliderChoose(String title, String message, int numOfComponents)
	{
		add(type);
	}
	public int getType() {
		return type.getSelectedIndex();
	}
}
