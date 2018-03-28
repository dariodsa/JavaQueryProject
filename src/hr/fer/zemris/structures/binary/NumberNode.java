package hr.fer.zemris.structures.binary;

public class NumberNode extends Node{

	public NumberNode(double value, int id) {
		super(value, id);
		
	}

	@Override
	public int compareTo(Node node) {
		

		return Double.compare(getValue(), node.getValue())==0?Integer.compare(getId(), node.getId()):Double.compare(getValue(), node.getValue());
	}

	@Override
	public boolean equals(Object o) {
		
		if(o instanceof NumberNode) {
			NumberNode n =(NumberNode)o;
			return n.getId() == getId();
		}
		return false;
	}
	
}
