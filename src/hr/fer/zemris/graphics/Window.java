package hr.fer.zemris.graphics;

import hr.fer.zemris.graphics.component.IpTable;
import hr.fer.zemris.graphics.component.PPicture;
import hr.fer.zemris.graphics.constants.Constants;
import hr.fer.zemris.graphics.constants.StructureType;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.*;

public class Window extends JFrame{
	
	private JComboBox<String> comboStructure;
	
	private JLabel labelStructure;
	public PPicture picture;
	
	public Window(int width,int height)
	{
		super("Završni rad, Dario Sindicic");
		setSize(width, height);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		setVisible(true);
		Constants.init();
	}
	public void initGUI()
	{
		setLayout(new BorderLayout());
		
		JTabbedPane tabs = new JTabbedPane();
		
		tabs.addTab("Postavke", getDataTab());
		tabs.addTab("Slika", getPictureTab());
		tabs.addTab("Radilice", getComputersInfo());
		
		add(tabs);
	}
	private JComponent getDataTab() {
		
		JPanel panel = new JPanel();
		panel.add(new JButton("Button1"));
		return panel;
	}
	private JComponent getPictureTab() {
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setSize(getWidth(), getHeight());
		picture = new PPicture(getWidth(),getHeight());
		//panel.add(new JButton("Button2"));
		panel.add(picture);
		return panel;
	}
	private JComponent getComputersInfo()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setSize(getWidth(),getHeight());
		panel.add(new IpTable());
		return panel;
	}
}