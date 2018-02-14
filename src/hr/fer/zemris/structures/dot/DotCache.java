package hr.fer.zemris.structures.dot;

import java.io.Serializable;

public class DotCache implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8330283207842965329L;
	private long id;
	private int component;
	private double val;
	public DotCache(long id, int component, double val)
	{
		this.id = id;
		this.component = component;
		this.val = val;
	}
	public long getId()
	{
		return this.id;
	}
	public double getValue()
	{
		return this.val;
	}
	public int getComponent()
	{
		return this.component;
	}
}
