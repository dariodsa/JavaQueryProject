package hr.fer.zemris.graphics;

import hr.fer.zemris.graphics.constants.Constants;
import hr.fer.zemris.graphics.constants.StructureType;

import java.awt.BorderLayout;

import javax.swing.*;

public class Window extends JFrame{
	
	private JComboBox<String> comboStructure;
	
	public Window(int width,int height)
	{
		super("Završni rad, Dario Sindicic");
		setSize(width, height);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		setVisible(true);
		Constants.init();
	}
	public void initGUI()
	{
		comboStructure = new JComboBox<>(Constants.structureName);
		
		add(comboStructure);
	}
}
