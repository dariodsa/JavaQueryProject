package hr.fer.zemris.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import hr.fer.zemris.structures.dot.Dot;

public class BinaryTree extends TreeMap<Double, List<Integer>> implements Structure
{
	public BinaryTree()
	{
		super();
	}
	public void update(double oldValue, double newValue, Integer dot)
	{
		delete(oldValue, dot);
		
		add(newValue, dot);
	}
	
	private void delete(double oldValue, Integer dot) 
	{
		if(!this.containsKey(oldValue))
			return;
		List<Integer> list = new ArrayList<>(this.get(oldValue));
		list.remove(dot);
		this.remove(oldValue);
		
		this.put(oldValue, list);	
	}
	public void add(double newValue, Integer dot)
	{
		List<Integer> tempList = new ArrayList<>();
		tempList.add(dot);
		
		if(this.containsKey(newValue))
		{
			List<Integer> list = new ArrayList<>(this.get(newValue));
			list.add(dot);
			
			//this.remove(newValue);
			this.put(newValue, list);
		}
		else
			this.put(newValue, tempList);
	}
	public List<Integer> query(double min, double max)
	{
		SortedMap<Double,List<Integer>> sortedMap = this.subMap(min, max);
		Collection<List<Integer>> list = sortedMap.values();
		
		List<Integer> resultList = new ArrayList<>();
		for(List<Integer> tempList : list)
		{
			for(Integer dot : tempList)
			{
				resultList.add(dot);
			}
		}
		return resultList;
	}
}
