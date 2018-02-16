package hr.fer.zemris.structures;
 
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.dot.Dot;

public class BinaryTree extends TreeSet<Pair> implements Structure
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
		this.remove(new Pair(dot, oldValue));
	}
	public void add(double newValue, Long dot)
	{
		add(new Pair(dot, newValue));
	}
	public List<Long> query(double min, double max) throws IllegalArgumentException
	{
		if(max<min)
			throw new IllegalArgumentException("In the function query, max param was lower than min. %nMAX: "+max+" , MIN: "+min);
		
		SortedSet<Pair> sortedMap = this.subSet(new Pair(0,min), new Pair(0,max));
		List<Long> resultList = new ArrayList<>();
		
		for(Pair P : sortedMap)
		{
			resultList.add(P.id);
		}
		return resultList;
	}
}
