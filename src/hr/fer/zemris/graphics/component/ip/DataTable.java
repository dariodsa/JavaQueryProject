package hr.fer.zemris.graphics.component.ip;

import java.awt.Image;

import javax.swing.*;
import javax.swing.table.*;

public class DataTable extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String[] columnNames = new String[] {"Naziv","IpAdresa","TCP-Connection","Port","Delete"};
	private static final Class<?>[]  columnClasses = new Class<?>[] {String.class,String.class,Image.class,String.class, JButton.class};
	public DefaultTableModel model;
	
	public DataTable() 
	{
		setVisible(true);
		model  = new DefaultTableModel(columnNames,0);
		
		//model.addRow(new RowItem("mirko",new JButton("ok")));
		setModel(model);
		getColumn("Delete").setCellRenderer(new ButtonRenderer());
	}


}
