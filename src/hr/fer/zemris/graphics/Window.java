package hr.fer.zemris.graphics;

import hr.fer.zemris.exceptions.DotFileIsNotSet;
import hr.fer.zemris.exceptions.DotFileNotFound;
import hr.fer.zemris.exceptions.MinMaxValuesAreNotSet;
import hr.fer.zemris.graphics.component.IpTable;
import hr.fer.zemris.graphics.component.MultiValueChoose;
import hr.fer.zemris.graphics.component.MultiValueSliderChoose;
import hr.fer.zemris.graphics.component.PPicture;
import hr.fer.zemris.graphics.component.statistics.StatisticsPanel;
import hr.fer.zemris.graphics.constants.Constants;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.types.StructureType;
import hr.fer.zemris.thread.MasterMethod;
import hr.fer.zemris.thread.workers.MainWorker;









import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;


import java.nio.file.Paths;









import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Window extends JFrame{
	
	int port1 = 3457;
	int port2 = 4567;
	
	private JSlider moveFactor;
	private JSlider queryFactor;
	private JTextField bucketNumber;
	private JButton minMaxValue;
	private JButton minMaxMove;
	private Path dotFile;
	private JButton fileButton;
	private JLabel moveFactorValue;
	private JLabel queryFactorValue;
	private JComboBox<String> structureType;

	
	private int numOfComponent = 0;
	
	public PPicture picture;
	public IpTable ipTable = new IpTable();
	
	
	private JButton killThemAll = new JButton("Prepare for new iteration");
	private StatisticsPanel statisticsPanel;
	
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
		
		minMaxValue = new JButton("Min/Max values");
		minMaxMove = new JButton("Min/Max moves");
		
		moveFactor  = new JSlider(0,100);
		queryFactor = new JSlider(0,100);
		bucketNumber = new JTextField(3);
		bucketNumber.setText("100");
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
		tabs.addTab("Statistika", getStatisticsTab());
		tabs.addTab("Radilice", getComputersInfo());
		
		add(tabs);
	}
	private Component getStatisticsTab() {
		statisticsPanel = new StatisticsPanel();
		statisticsPanel.initGui();
		return statisticsPanel;
	}
	private JComponent getDataTab(){
		
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
				try {
					numOfComponent = Files.readAllLines(dotFile).get(0).split(",").length;
					minMaxValue.setEnabled(true);
					minMaxMove.setEnabled(true);
				}
				 catch (Exception e1) {
					 JOptionPane.showMessageDialog(this,new DotFileNotFound(fc.getSelectedFile().getAbsolutePath()).getMessage());
				}
				
				
			}
		});
		rightFactors.add(groupComponents(
				new JLabel("Dot's file: "),
				fileButton
				));
		
		minMaxValue.setEnabled(false);
		minMaxMove.setEnabled(false);
		minMaxValue.addActionListener((e)->{
			MultiValueChoose M = new MultiValueChoose("Min/Max", "min / max po komponentama", numOfComponent);
			int result = JOptionPane.showConfirmDialog(this, M);
			if(result == JOptionPane.YES_OPTION)
			{
				
				try {
					MasterMethod.parametars.minValues = M.getMinValue();
					MasterMethod.parametars.maxValues = M.getMaxValue();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, e1.getMessage());
				}
				
			}
		});
		minMaxMove.addActionListener((e)->{
			MultiValueSliderChoose M = new MultiValueSliderChoose("Pomaci", "Pomaci po komponentama", numOfComponent);
			int result = JOptionPane.showConfirmDialog(this, M);
			if(result == JOptionPane.YES_OPTION)
			{
				try {
					MasterMethod.parametars.maxMove = new double[numOfComponent];
					MasterMethod.parametars.minMove = new double[numOfComponent];
					if(M.getType() == 0) {
						for(int i=0;i<numOfComponent;++i) MasterMethod.parametars.minMove[i] = 0;
						for(int i=0;i<numOfComponent;++i) MasterMethod.parametars.maxMove[i] = 0.01;
					} else if(M.getType() == 1) {
						for(int i=0;i<numOfComponent;++i) MasterMethod.parametars.minMove[i] = 0.15;
						for(int i=0;i<numOfComponent;++i) MasterMethod.parametars.maxMove[i] = 0.30;
					} else if(M.getType() == 2) {
						for(int i=0;i<numOfComponent;++i) MasterMethod.parametars.minMove[i] = 0.4;
						for(int i=0;i<numOfComponent;++i) MasterMethod.parametars.maxMove[i] = 0.5;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		rightFactors.add(groupComponents(
				new JLabel("Dots size: "),
				minMaxValue
				));
		rightFactors.add(groupComponents(
				new JLabel("Moves size: "),
				minMaxMove
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
		btnOk.addActionListener((e)->{btnOkClick(1);});
		rightFactors.add(btnOk);
		JButton runWorker = new JButton("Run only worker");
		rightFactors.add(runWorker);
		runWorker.addActionListener((e)->{btnOkClick(2);});
		
		JScrollPane jsRight = new JScrollPane(rightFactors);
		
		panel.add(jsRight,BorderLayout.EAST);
		
		panel.add(killThemAll, BorderLayout.WEST);
		killThemAll.addActionListener((e)->{
			//killThemAll
			DefaultTableModel model = ipTable.getTable().model;
			String[] adrese = new String[model.getRowCount()];
			for(int i=0;i<model.getRowCount();++i)
			{
				adrese[i] = (String)model.getValueAt(i, 1);
			}
			
			try 
			{
				Socket S = new Socket(InetAddress.getLocalHost().getHostAddress().toString(),port1);
				ObjectOutputStream oos = new ObjectOutputStream(S.getOutputStream());
				oos.writeInt(7);
				oos.flush();
				S.close();
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}
		});
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
		panel.add(ipTable);
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
	private void checkInputErrors() throws DotFileIsNotSet, MinMaxValuesAreNotSet
	{
		if(dotFile == null)
			throw new DotFileIsNotSet();
		
	}
	private static int run = 0;
	public static MasterMethod masterMethod;
	private void btnOkClick(int type)
	{
		
		
		try {
			switch (type) {
			case 1:
				
				checkInputErrors();
				
				StructureType typeOfStrcuture = structureType.getSelectedIndex() == 0 ? StructureType.BUCKET : StructureType.BINARY_TREE;
				
				MasterMethod.parametars.structureType = typeOfStrcuture;
				MasterMethod.parametars.queryFactor = ((double)queryFactor.getValue())/100.0;
				MasterMethod.parametars.moveFactor = ((double)moveFactor.getValue())/100.0;
				MasterMethod.parametars.bucketSize = Integer.parseInt(bucketNumber.getText());
				MasterMethod.dotsPath = dotFile;
				
				
				DefaultTableModel model = ipTable.getTable().model;
				String[] adrese = new String[model.getRowCount()];
				for(int i=0;i<model.getRowCount();++i)
				{
					adrese[i] = (String)model.getValueAt(i, 1);
				}
				if(run == 1) {
					masterMethod.run();
					break;
				}
				masterMethod = new MasterMethod(
						adrese,dotFile,port1,port2
						);
				
				Thread workerThread = new Thread(()-> {
					MainWorker main = new MainWorker(port2,port1);
					main.run();
				});
				Thread masterThread = new Thread(()->{
					try {
						masterMethod.run();
					} catch (Exception e) {e.printStackTrace();}
				});
				if(Window.run == 0)
					workerThread.start();
				Window.run = 1;
				if(type==1){
					masterThread.start();
					masterThread.join();
				}
				break;
			case 2:
				Thread workerThread2 = new Thread(()-> {
					MainWorker main = new MainWorker(port2,port1);
					main.run();
				});
				workerThread2.start();
			default:
				break;
			}
			
		}
		catch(Exception ex){
			JOptionPane.showMessageDialog(this,ex.getMessage());
			System.out.println(ex.getClass().toString());
			ex.printStackTrace();
			
		}
	}
	class Values
	{
		double[] minValues;
		double[] maxValues;
		public Values(){}
		public void setMinMaxValue(double[] minValues, double[] maxValues){
			this.minValues = new double[minValues.length];
			this.maxValues = new double[maxValues.length];
			for(int i=0;i<minValues.length;++i){
				this.minValues[i] = minValues[i];
				this.maxValues[i] = maxValues[i];
			}
		}
	}
}
