package hr.fer.zemris.structures;

import java.io.Serializable;

import hr.fer.zemris.structures.types.StructureType;

public class Parametars implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7183805197637432354L;
	public StructureType structureType;
	public int bucketSize;
	
	public double queryFactor;
	public double moveFactor;
	
	public double[] minValues;
	public double[] maxValues;
	
	public double[] minMove;
	public double[] maxMove;
	
	@Override
	public String toString()
	{
		String rez="";
		rez+=String.format("%s %d%n","Bucket size: ",bucketSize);
		rez+=String.format("%s %f%n","Move factor: ",moveFactor);
		rez+=String.format("%s %f%n","Query factor: ",queryFactor);
		rez+=String.format("%s %s%n","Structure type: ",structureType);
		rez+=String.format("%s %d%n","Number of components: ",minMove.length);
		for(int i=0;i<minMove.length;++i){
			rez+=String.format("%s (%d) %f <--> %f%n","Min and max value ",i+1,minValues[i],maxValues[i]);
		}
		for(int i=0;i<minMove.length;++i){
			rez+=String.format("%s (%d) %f <--> %f%n","Min and max value moves",i+1,minMove[i],maxMove[i]);
		}
		return rez;
	}
}
