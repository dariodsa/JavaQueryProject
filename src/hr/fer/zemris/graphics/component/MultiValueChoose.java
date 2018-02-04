package hr.fer.zemris.graphics.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;

public class MultiValueChoose extends JPanel {
	
	private JTextField[] minValue;
	private JTextField[] maxValue;
	public MultiValueChoose(String title, String message, int numOfComponents)
	{
		minValue = new JTextField[numOfComponents];
		maxValue = new JTextField[numOfComponents];
		
		setVisible(true);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(new JLabel(message));
		for(int i=0;i<numOfComponents;++i)
		{
			JPanel paneltemp = new JPanel();
			paneltemp.setLayout(new BorderLayout());
			paneltemp.add(groupComponents(
					new JLabel("Min value: "), new JTextField(5)),
					BorderLayout.WEST);
			paneltemp.add(groupComponents(
					new JLabel("Max value: "), new JTextField(5)),
					BorderLayout.EAST);
			add(paneltemp);
		}
	}
	private JPanel groupComponents(JComponent... components)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		for(int i=0;i<components.length;++i)
			panel.add(components[i]);
		return panel;
	}
}
