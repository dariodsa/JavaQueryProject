package hr.fer.zemris.graphics.component;

import hr.fer.zemris.exceptions.ComputerIsNotFound;
import hr.fer.zemris.exceptions.DuplicateComputerName;
import hr.fer.zemris.exceptions.InvalidIpAddress;
import hr.fer.zemris.graphics.component.ip.*;
import hr.fer.zemris.graphics.constants.GraphicsConstants;
import hr.fer.zemris.network.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.io.IOException;

import javax.swing.*;
import javax.swing.table.*;

public class IpTable extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6887805418581516885L;
	private JButton btnAddIp;
	private DataTable table;
	
	public IpTable() 
	{
		setLayout(new BorderLayout());
		initGUI();
		
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
		
		
		
		table = new DataTable();
		JScrollPane pane = new JScrollPane(table);
		
		add(btnAddIp,BorderLayout.NORTH);
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
				table.model.addRow(new RowItem(
						addNewComputer.computerName.getText(),
						addNewComputer.ipAddress.getText(),
						new JButton("ok")));
				update(getGraphics());
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
