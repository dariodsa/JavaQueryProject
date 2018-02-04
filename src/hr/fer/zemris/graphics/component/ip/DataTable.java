package hr.fer.zemris.graphics.component.ip;

import javax.swing.JButton;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.*;

public class DataTable extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String[] columnNames = new String[] {"Naziv","IpAdresa","Edit"};
	private static final Class<?>[]  columnClasses = new Class<?>[] {String.class,String.class,JButton.class};
	public MyTableModel model;
	
	public DataTable() 
	{
		setVisible(true);
		model  = new MyTableModel(columnNames,columnClasses);
		
		//model.addRow(new RowItem("mirko",new JButton("ok")));
		setModel(model);
		getColumn("Edit").setCellRenderer(new ButtonRenderer());
	}


}
