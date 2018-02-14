package hr.fer.zemris.structures;

import java.io.Serializable;

public class Parametars implements Serializable{
	
	public int structureType;
	public int bucketSize;
	
	public double queryFactor;
	public double moveFactor;
	
	public double[] minValues;
	public double[] maxValues;
	
	public double[] minMove;
	public double[] maxMove;

	public Parametars(int structureType, double queryFactor, double moveFactor, double[]  minValues, double[] maxValues, int bucketSize)
	{
		this.structureType = structureType;
		this.queryFactor = queryFactor;
		this.moveFactor = moveFactor;
		this.bucketSize = bucketSize;
		
		this.minValues = new double[minValues.length];
		this.maxValues = new double[maxValues.length];
		
		this.minMove = new double[minMove.length];
		this.maxMove = new double[maxMove.length];
		
		for(int i=0;i<minValues.length;++i)
			this.minValues[i] = minValues[i];
		for(int i=0;i<maxValues.length;++i)
			this.maxValues[i] = maxValues[i];
		
		for(int i=0;i<minMove.length;++i)
			this.minMove[i] = minMove[i];
		for(int i=0;i<maxMove.length;++i)
			this.maxMove[i] = maxMove[i];
	}
}
