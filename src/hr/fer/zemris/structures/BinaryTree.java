package hr.fer.zemris.structures;
 
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.binary.NetworkNode;
import hr.fer.zemris.structures.binary.Node;
import hr.fer.zemris.structures.binary.NumberNode;
import hr.fer.zemris.structures.binary.Orientation;

public class BinaryTree extends TreeSet<Node>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4681002200497431623L;
	
	public double minValue;
	public double maxValue;
	public static Stack<NumberNode> cache;
	
	public BinaryTree(double minValue, double maxValue)
	{
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		BinaryTree.cache = new Stack<>();
	}
	public void updateNumberNode(NumberNode node, double newValue)
	{
		
		this.remove(node);
		node.setValue(newValue);
		this.add(node);
	}
	
	public void deleteNumberNode(NumberNode node) 
	{
		this.remove(node);
		cache.add(node);
	}
	public void addNumberNode(double newValue, int dot)
	{
		if(cache.isEmpty())
			add(new NumberNode(newValue, dot));
		else {
			NumberNode node = cache.pop();
			node.setValue(newValue);
			node.setId(dot);
			add(node);
		}
	}
	public void addNetworkNode(String address, double value, Orientation orientation) {
		add(new NetworkNode(value, address, orientation));
	}
	public SortedSet<Node> query(double min, double max) throws IllegalArgumentException
	{
		if(max<min)
			throw new IllegalArgumentException("In the function query, max param was lower than min. %nMAX: "+max+" , MIN: "+min);
		
		SortedSet<Node> sortedMap = this.subSet(new NumberNode(min,-2), new NumberNode(max,-2));
		return sortedMap;

	}
}
