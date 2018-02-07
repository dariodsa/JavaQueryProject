package hr.fer.zemris.structures.dot;

public class Dot {
	
	private int id;
	private double[] values;
	
	public Dot(int numOfArguments, int id)
	{
		this.values = new double[numOfArguments];
		this.id = id;
	}
	public int getId()
	{
		return this.id;
	}
	public double getValue(int pos)
	{
		return this.values[pos];
	}
	public void setValue(int pos, double value)
	{
		this.values[pos] = value;
	}
	public double[] getValues()
	{
		return this.values;
	}
	public int getNumOfComponents()
	{
		return this.values.length;
	}
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Dot)
		{
			return ((Dot) o).id == this.id;
		}
		return false;
	}
	@Override
	public Dot clone()
	{
		Dot clone = new Dot(this.values.length,this.id);
		for(int i=0;i<this.values.length;++i)
			clone.setValue(i, getValue(i));
		return clone;
	}
}
