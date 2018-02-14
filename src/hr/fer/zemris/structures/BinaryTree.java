package hr.fer.zemris.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import hr.fer.zemris.structures.dot.Dot;

public class BinaryTree extends TreeMap<Double, List<Long>> implements Structure
{
	public BinaryTree()
	{
		super();
	}
	public void update(double oldValue, double newValue, Long dot)
	{
		delete(oldValue, dot);
		
		add(newValue, dot);
	}
	
	public void delete(double oldValue, Long dot) 
	{
		if(!this.containsKey(oldValue))
			return;
		List<Long> list = new ArrayList<>(this.get(oldValue));
		list.remove(dot);
		
		this.put(oldValue, list);	
	}
	public void add(double newValue, Long dot)
	{
		List<Long> tempList = new ArrayList<>();
		tempList.add(dot);
		
		if(this.containsKey(newValue))
		{
			List<Long> list = new ArrayList<>(this.get(newValue));
			list.add(dot);
			
			//this.remove(newValue);
			this.put(newValue, list);
		}
		else
			this.put(newValue, tempList);
	}
	public List<Long> query(double min, double max) throws IllegalArgumentException
	{
		if(max<min)
			throw new IllegalArgumentException("In the function query, max param was lower than min. %nMAX: "+max+" , MIN: "+min);
		SortedMap<Double,List<Long>> sortedMap = this.subMap(min, max);
		Collection<List<Long>> list = sortedMap.values();
		
		List<Long> resultList = new ArrayList<>();
		for(List<Long> tempList : list)
		{
			for(Long dot : tempList)
			{
				resultList.add(dot);
			}
		}
		return resultList;
	}
}
