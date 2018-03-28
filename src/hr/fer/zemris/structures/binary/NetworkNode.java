package hr.fer.zemris.structures.binary;

public class NetworkNode extends Node{

	private Orientation orientation;
	private String address;
	
	public NetworkNode(double value, String address, Orientation orientation) {
		super(value, -1);
		this.address = address;
		this.orientation = orientation;
	}
	
	@Override
	public int compareTo(Node node) {
		return Double.compare(this.getValue(), node.getValue());
	}

	@Override
	public boolean equalsTo(Object o) {
		
		NumberNode node = (NumberNode)o;
		if(node.getValue() < getValue() && orientation == Orientation.LEFT)
			return true;
		if(node.getValue() > getValue() && orientation == Orientation.RIGHT)
			return true;
		return false;
	}

	public String getAddress() {
		return this.address;
	}
}
