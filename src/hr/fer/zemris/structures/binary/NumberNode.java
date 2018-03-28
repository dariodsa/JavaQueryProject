package hr.fer.zemris.structures.binary;

public class NumberNode extends Node{

	public NumberNode(double value, int id) {
		super(value, id);
		
	}

	@Override
	public int compareTo(Node node) {
		
		return Double.compare(this.getValue(), node.getValue());
	}

	@Override
	public boolean equalsTo(Object o) {
		
		return ((Node)o).getId() == getId();
	}
	
}
