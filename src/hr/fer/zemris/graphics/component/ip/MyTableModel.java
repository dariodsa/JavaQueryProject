package hr.fer.zemris.graphics.component.ip;

import java.util.*;
import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class MyTableModel implements TableModel {

	private List<RowItem> rows;
	private String[] columns;
	private Class<?>[] columnClass;
	public MyTableModel(String[] columns, Class<?>[] columnClass)
	{
		rows = new ArrayList<>();
		this.columns = columns;
		this.columnClass = columnClass;
	}
	public void removeRow(int pos)
	{
		rows.remove(pos);
	}
	public void addRow(RowItem rowItem)
	{
		rows.add(rowItem);
	}
	@Override
	public int getRowCount() 
	{
		return rows.size();
	}

	@Override
	public int getColumnCount() 
	{
		return columns.length;
	}

	@Override
	public String getColumnName(int columnIndex) 
	{
		return columns[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		return columnClass[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		return this.rows.get(rowIndex).getItem(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
	{
	
	}

	@Override
	public void addTableModelListener(TableModelListener l) 
	{
	
	}

	@Override
	public void removeTableModelListener(TableModelListener l) 
	{
	
	}

}