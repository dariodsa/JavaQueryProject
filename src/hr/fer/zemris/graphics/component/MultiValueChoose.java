package hr.fer.zemris.graphics.component;

import hr.fer.zemris.exceptions.MultiValueFormatException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;

public class MultiValueChoose extends JPanel {
	
	private JTextField[] minValue;
	private JTextField[] maxValue;
	private int numOfComponents;
	
	public MultiValueChoose(String title, String message, int numOfComponents)
	{
		this.numOfComponents = numOfComponents;
		minValue = new JTextField[numOfComponents];
		maxValue = new JTextField[numOfComponents];
		setVisible(true);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(new JLabel(message));
		for(int i=0;i<numOfComponents;++i)
		{
			JPanel paneltemp = new JPanel();
			paneltemp.setLayout(new BorderLayout());
			minValue[i] = new JTextField(5);
			maxValue[i] = new JTextField(5);
			paneltemp.add(groupComponents(
					new JLabel("Min value: "), minValue[i]),
					BorderLayout.WEST);
			paneltemp.add(groupComponents(
					new JLabel("Max value: "), maxValue[i]),
					BorderLayout.EAST);
			add(paneltemp);
		}
	}
	public double[] getMinValue()  throws MultiValueFormatException
	{
		double[] arr = new double[numOfComponents];
		for(int i=0;i<numOfComponents;++i)
		{
			try{
				arr[i] = Double.parseDouble(minValue[i].getText());
			}
			catch(NumberFormatException ex){
				throw new MultiValueFormatException(String.format("Number format is wrong.%n%s%nPlease enter your input again.",minValue[i].getText()));
			}
			catch(NullPointerException ex){
				throw new MultiValueFormatException("You left some of the textboxes empty.%nPlease enter your input again.");
			}
		}
		return arr;
	}
	public double[] getMaxValue()  throws MultiValueFormatException
	{
		double[] arr = new double[numOfComponents];
		for(int i=0;i<numOfComponents;++i)
		{
			try{
				arr[i] = Double.parseDouble(maxValue[i].getText());
			}
			catch(NumberFormatException ex){
				throw new MultiValueFormatException(String.format("Number format is wrong.%n%s%nPlease enter your input again.",minValue[i].getText()));
			}
			catch(NullPointerException ex){
				throw new MultiValueFormatException("You left some of the textboxes empty.%nPlease enter your input again.");
			}
		}
		return arr;
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
