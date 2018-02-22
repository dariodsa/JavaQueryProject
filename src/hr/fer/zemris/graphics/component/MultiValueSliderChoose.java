package hr.fer.zemris.graphics.component;

import hr.fer.zemris.exceptions.MultiValueFormatException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MultiValueSliderChoose extends JPanel {
	
	private JSlider[] minValue;
	private JSlider[] maxValue;
	private int numOfComponents;
	
	public MultiValueSliderChoose(String title, String message, int numOfComponents)
	{
		this.numOfComponents = numOfComponents;
		minValue = new JSlider[numOfComponents];
		maxValue = new JSlider[numOfComponents];
		setVisible(true);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(new JLabel(message));
		for(int i=0;i<numOfComponents;++i)
		{
			JPanel paneltemp = new JPanel();
			paneltemp.setLayout(new BorderLayout());
			minValue[i] = new JSlider(0,100,50);
			maxValue[i] = new JSlider(0,100,50);
			JLabel label1 = new JLabel();
			JLabel label2 = new JLabel();
			
			minValue[i].addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					label1.setText(((JSlider)e.getSource()).getValue() + "%");
				}
			});
			
			maxValue[i].addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					label2.setText(((JSlider)e.getSource()).getValue() + "%");
				}
			});
			
			paneltemp.add(groupComponents(
					new JLabel("Min value: "), minValue[i],
					label1),
					BorderLayout.WEST);
			paneltemp.add(groupComponents(
					new JLabel("Max value: "), maxValue[i],
					label2),
					BorderLayout.EAST);
			add(paneltemp);
		}
	}
	public double[] getMinValue()  throws MultiValueFormatException
	{
		double[] arr = new double[numOfComponents];
		for(int i=0;i<numOfComponents;++i)
		{
			arr[i] = minValue[i].getValue()/2.0;
		}
		return arr;
	}
	public double[] getMaxValue()  throws MultiValueFormatException
	{
		double[] arr = new double[numOfComponents];
		for(int i=0;i<numOfComponents;++i)
		{
			arr[i] = maxValue[i].getValue()/2.0;
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
