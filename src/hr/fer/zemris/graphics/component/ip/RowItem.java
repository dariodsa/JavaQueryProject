package hr.fer.zemris.graphics.component.ip;

import java.util.*;

public class RowItem 
{
	private List<Object> objects = new ArrayList<>();
	public RowItem(Object... objects)
	{
		for(Object O :objects)
		{
			this.objects.add(O);
		}
	}
	public Object getItem(int colIndex)
	{
		return objects.get(colIndex);
	}
}
