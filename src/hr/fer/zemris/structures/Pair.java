package hr.fer.zemris.structures;


public class Pair implements Comparable<Pair>{
	public double value;
	public long id;
	public Pair(long id, double value)
	{
		this.id = id;
		this.value = value;
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
		return Double.compare(value, o.value)==0?Long.compare(id, o.id):Double.compare(value, o.value);
	}
	
}
