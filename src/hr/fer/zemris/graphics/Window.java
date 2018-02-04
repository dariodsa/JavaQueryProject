package hr.fer.zemris.graphics;

import hr.fer.zemris.graphics.component.IpTable;
import hr.fer.zemris.graphics.component.MultiValueChoose;
import hr.fer.zemris.graphics.component.PPicture;
import hr.fer.zemris.graphics.constants.Constants;
import hr.fer.zemris.graphics.constants.StructureType;




import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.file.Path;


import java.nio.file.Paths;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

public class Window extends JFrame{
	
	private JSlider moveFactor;
	private JSlider queryFactor;
	private JTextField bucketNumber;
	private Path dotFile;
	private JButton fileButton;
	private JLabel moveFactorValue;
	private JLabel queryFactorValue;
	private JComboBox<String> structureType;
	
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
		structureType = new JComboBox<>(Constants.structureName);
		fileButton  = new JButton("Browser ...");
		moveFactor  = new JSlider(0,100);
		queryFactor = new JSlider(0,100);
		bucketNumber = new JTextField(3);
		bucketNumber.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				bucketNumber.setColumns(bucketNumber.getText().length()>3?bucketNumber.getText().length():3);
				repaint();
				try{
					if(bucketNumber.getText().length()>0)
						Long.parseLong(bucketNumber.getText());
				}catch(Exception ex){
					bucketNumber.setText("100");
				}
			}
			public void keyPressed(KeyEvent e) {}
		});
		queryFactorValue  = new JLabel("50%");
		moveFactorValue   = new JLabel("50%");
		
		
		queryFactor.addChangeListener((e)->{
			queryFactorValue.setText(queryFactor.getValue()+"%"); 
		});
		moveFactor.addChangeListener((e)->{
			moveFactorValue.setText(moveFactor.getValue()+"%"); 
		});
		
		JTabbedPane tabs = new JTabbedPane();
		
		tabs.addTab("Postavke", getDataTab());
		tabs.addTab("Slika", getPictureTab());
		tabs.addTab("Radilice", getComputersInfo());
		
		add(tabs);
	}
	private JComponent getDataTab() {
		
		JPanel panel = new JPanel(new BorderLayout());
		
		//panel.add(new JButton("Button1"));
		JPanel rightFactors = new JPanel();
		rightFactors.setLayout(new BoxLayout(rightFactors, BoxLayout.Y_AXIS));
		
		rightFactors.add(groupComponents(
				new JLabel("Structure type: "),
				structureType
				));
		fileButton.addActionListener((e)->{
			JFileChooser fc = new JFileChooser(".");
			int value = fc.showOpenDialog(this);
			if(value == JFileChooser.APPROVE_OPTION){
				dotFile = Paths.get(fc.getSelectedFile().getPath());
				JOptionPane.showMessageDialog(this, "File loaded.");
				fileButton.setText(dotFile.getFileName().toString());
			}
		});
		rightFactors.add(groupComponents(
				new JLabel("Dot's file: "),
				fileButton
				));
		JButton t = new JButton("temp"); //todo remove
		t.addActionListener((e)->{
			JPanel M = new MultiValueChoose("naziv", "poruka", 2);
			JOptionPane.showConfirmDialog(this, M);
		});
		rightFactors.add(groupComponents(
				new JLabel("Dots size: "),
				t
				));
		rightFactors.add(groupComponents(
				new JLabel("QueryFactor"),
				queryFactor,
				queryFactorValue));
		rightFactors.add(groupComponents(
				new JLabel("MoveFactor"),
				moveFactor,
				moveFactorValue));
		rightFactors.add(groupComponents(
				new JLabel("Move size: ")
				));
		rightFactors.add(groupComponents(
				new JLabel("Number of buckets"),
				bucketNumber));
		JButton btnOk = new JButton("Run");
		rightFactors.add(btnOk);
		
		panel.add(rightFactors,BorderLayout.EAST);
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
	private JPanel groupComponents(JComponent... components)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		for(int i=0;i<components.length;++i)
			panel.add(components[i]);
		return panel;
	}
}