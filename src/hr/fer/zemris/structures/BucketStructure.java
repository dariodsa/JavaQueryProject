package hr.fer.zemris.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hr.fer.zemris.exceptions.DimmensionException;
import hr.fer.zemris.structures.dot.Dot;

public class BucketStructure implements Structure{

	//private arrray
	
	
	private double minValue;
	private double maxValue;
	private int numOfBuckets;
	private double sizePerBucket;
	
	private List<Integer>[] buckets;
		
	public BucketStructure(double minValue,double maxValue, int numOfBuckets) 
	{
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.numOfBuckets = numOfBuckets;
		
		double size = Math.abs(maxValue - minValue);
		this.sizePerBucket = size / numOfBuckets;
		
		this.buckets = new ArrayList[numOfBuckets];
		initBuckets();
		
	}
	private int getBucket(double value)
	{
		return (int)(Math.abs(value - this.minValue) / sizePerBucket);
	}
	@Override
	public void add(double newValue, Integer dot)
	{
		int newBucket = getBucket(newValue);
		this.buckets[newBucket].add(dot);
	}
	@Override
	public void update(double oldValue, double newValue, Integer dot) throws DimmensionException 
	{
		if(!(minValue <= newValue && newValue <= maxValue))
			throw new DimmensionException(newValue, minValue, maxValue); 
		int oldBucket = getBucket(oldValue);
		int newBucket = getBucket(newValue);
		
		this.buckets[oldBucket].remove(dot);
		this.buckets[newBucket].add(dot);
	}

	@Override
	public List<Integer> query(double min, double max) throws IllegalArgumentException 
	{
		if(max<min)
			throw new IllegalArgumentException("In the function query, max param was lower than min. %nMAX: "+max+" , MIN: "+min);
		int firstBucket = getBucket(min);
		int lastBucket = getBucket(max);
		List<Integer> result = new ArrayList<>();
		for(int i=firstBucket; i<=lastBucket; ++i)
		{
			for(Integer id : buckets[i])
			{
				result.add(id);
			}
		}
		return result;
	}
	private void initBuckets()
	{
		for(int i=0; i<numOfBuckets; ++i)
		{
			//System.out.printf("%d %d %n",i,numOfBuckets);
			this.buckets[i] = new ArrayList<>();
		}
	}
}
