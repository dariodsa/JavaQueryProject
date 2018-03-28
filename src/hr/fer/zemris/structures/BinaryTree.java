package hr.fer.zemris.structures;
 
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.binary.NetworkNode;
import hr.fer.zemris.structures.binary.Node;
import hr.fer.zemris.structures.binary.NumberNode;
import hr.fer.zemris.structures.binary.Orientation;
import hr.fer.zemris.structures.dot.Dot;

public class BinaryTree extends TreeSet<Node> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4681002200497431623L;
	
	public double minValue;
	public double maxValue;

	
	public BinaryTree(double minValue, double maxValue)
	{
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	public void updateNumberNode(double oldValue, double newValue, int dot)
	{
		deleteNumberNode(oldValue, dot);
		addNumberNode(newValue, dot);
	}
	
	public void deleteNumberNode(double oldValue, int dot) 
	{
		this.remove(new NumberNode(oldValue, dot));
	}
	public void addNumberNode(double newValue, int dot)
	{
		add(new NumberNode(newValue, dot));
	}
	public void addNetworkNode(String address, double value, Orientation orientation) {
		add(new NetworkNode(value, address, orientation));
	}
	public List<Node> query(double min, double max) throws IllegalArgumentException
	{
		if(max<min)
			throw new IllegalArgumentException("In the function query, max param was lower than min. %nMAX: "+max+" , MIN: "+min);
		
		SortedSet<Node> sortedMap = this.subSet(new NumberNode(min,-2), new NumberNode(max,-2));
		List<Node> resultList = new ArrayList<>();
		
		for(Node P : sortedMap)
		{
			resultList.add(P);
		}
		return resultList;
	}
}
