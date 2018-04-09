package hr.fer.zemris.structures.binary;

public abstract class Node implements Comparable<Node>{
	
	private double value;
	private int id;
	
	protected Node(double value, int id) {
		this.value = value;
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public abstract int compareTo(Node n);
	@Override
	public abstract boolean equals(Object o);
}
