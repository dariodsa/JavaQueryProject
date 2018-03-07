package hr.fer.zemris.graphics.component;

import hr.fer.zemris.exceptions.ComputerIsNotFound;
import hr.fer.zemris.exceptions.DotFileNotFound;
import hr.fer.zemris.exceptions.DuplicateComputerName;
import hr.fer.zemris.exceptions.InvalidIpAddress;
import hr.fer.zemris.graphics.component.ip.*;
import hr.fer.zemris.network.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class IpTable extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6887805418581516885L;
	private JButton btnAddIp;
	private JButton btnLoadIp;
	private DataTable table;
	
	public IpTable() 
	{
		setLayout(new BorderLayout());
		initGUI();
		
	}
	public DataTable getTable()
	{
		return this.table;
	}
	private void initGUI() 
	{
		btnAddIp = new JButton("Add new computer");
		btnAddIp.addActionListener((e)->
		{
			try {
			click_btnAddIp("192.168.1.1");
			} catch (Exception e1) {e1.printStackTrace();}
		});
		
		btnLoadIp = new JButton("Load IP address");
		btnLoadIp.addActionListener((e)->{
			JFileChooser fc = new JFileChooser(".");
			int value = fc.showOpenDialog(this);
			if(value == JFileChooser.APPROVE_OPTION){
				Path ipAddresses = Paths.get(fc.getSelectedFile().getPath());
				JOptionPane.showMessageDialog(this, "File loaded.");
				
				try {
					List<String> lines = Files.readAllLines(ipAddresses);
					for(String line : lines){
						addNewComputer("", line);
					}
					
				}
				 catch (Exception e1) {
					 JOptionPane.showMessageDialog(this,new DotFileNotFound(fc.getSelectedFile().getAbsolutePath()).getMessage());
				}
				
				
			}
		});
		
		table = new DataTable();
		
		 
		 
		ButtonColumn buttonColumn = new ButtonColumn(table, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JTable table = (JTable)e.getSource();
		        int modelRow = Integer.valueOf( e.getActionCommand() );
		        ((DefaultTableModel)table.getModel()).removeRow(modelRow);
			}
		}, table.model.findColumn("Delete"));
		buttonColumn.setMnemonic(KeyEvent.VK_D);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(btnAddIp,BorderLayout.WEST);
		panel.add(btnLoadIp,BorderLayout.EAST);
		JScrollPane pane = new JScrollPane(table);
		
		add(panel,BorderLayout.NORTH);
		add(pane,BorderLayout.CENTER);
	}
	private void checkComputerName(String name) throws DuplicateComputerName
	{
		for(int i=0;i<table.getRowCount();++i)
		{
			String computerName = (String) table.model.getValueAt(i, 0);
			if(computerName.compareTo(name)==0)
			{
				throw new DuplicateComputerName(name);
			}
		}
	}
	private void addNewComputer(String name, String ipAddress)
	{
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener((e)->{
			removeComputer(name,ipAddress);
		});
		table.model.addRow(new Object[] {
				name,
				ipAddress,
				"",
				"7654",
				deleteButton});
		//table.repaint();
		table.invalidate();
	}
	private void removeComputer(String name, String ipAddress) 
	{
		for(int i=0;i<table.model.getRowCount();++i)
		{
			String rowName = table.model.getColumnName(0);
			String rowIpAddress = table.model.getColumnName(1);
			if( name == rowName && ipAddress == rowIpAddress)
			{
				table.model.removeRow(i);
				return;
			}
		}
	}
	private void click_btnAddIp(String ipAdress) throws IOException
	{
		/*boolean ok = Network.checkIsReachable(ipAdress);
		System.out.println(ok?"OK":"WRONG");*/
		AddNewComputer addNewComputer = new AddNewComputer();
		
		int result = JOptionPane.showConfirmDialog(null, addNewComputer.getComponent(), "Dodaj novu radilicu",JOptionPane.INFORMATION_MESSAGE);
		if(result == JOptionPane.YES_OPTION)
		{
			try {
				addNewComputer.checkIP();
				checkComputerName(addNewComputer.computerName.getText());
				if(addNewComputer.pingTest.isSelected())
				{
					if(!Network.checkIsReachable(addNewComputer.ipAddress.getText()))
					{
						throw new ComputerIsNotFound(addNewComputer.ipAddress.getText());
					}
				}
				addNewComputer(addNewComputer.computerName.getText(), addNewComputer.ipAddress.getText());
				
				table.repaint();
			} catch (InvalidIpAddress | DuplicateComputerName | ComputerIsNotFound e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	class AddNewComputer
	{
		JCheckBox pingTest = new JCheckBox("PingTest");
		JTextField computerName = new JTextField(1);
		JTextField ipAddress = new JTextField(1);
		
		public JPanel getComponent()
		{
			JPanel component = new JPanel();
			component.setLayout(new GridLayout(4, 2));
			
			JButton hidden = new JButton("");
			hidden.setVisible(false);
			
			component.add(new JLabel("Naziv radilice: "));
			component.add(computerName);
			component.add(new JLabel("Ip adresa: "));
			component.add(ipAddress);
			component.add(new JLabel("PingTest: "));
			component.add(pingTest);
			
			return component;
		}
		public void checkIP() throws InvalidIpAddress
		{
			String[] sep = ipAddress.getText().split("\\.");
			if(sep.length != 4) throw new InvalidIpAddress(ipAddress.getText());
			
			for(int i=0;i<4;++i)
			{
				int value = 0;
				for(int j=0;j<sep[i].length();++j)
				{
					if(!(sep[i].charAt(j) >= '0' && sep[i].charAt(j) <= '9'))
						throw new InvalidIpAddress(ipAddress.getText());
					value = value * 10 +sep[i].charAt(j)-'0';
				}
				
				if(value>255)
					throw new InvalidIpAddress(ipAddress.getText());
			}
		}
	}
}
