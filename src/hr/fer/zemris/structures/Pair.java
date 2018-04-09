package hr.fer.zemris.structures;


public class Pair implements Comparable<Pair>{
	public double value;
	public int id;
	public boolean state;
	public Pair(int id, double value)
	{
		this.id = id;
		this.value = value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public boolean equals(Object O)
	{
		Pair P =(Pair)O;
		return id == P.id && value == P.value;
	}
	@Override
	public int compareTo(Pair o) 
	{
		return Double.compare(value, o.value)==0?Integer.compare(id, o.id):Double.compare(value, o.value);
	}
	
}
