package hr.fer.zemris.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	public void addOrUpdate(double oldValue, double newValue, Integer dot) 
	{
		int oldBucket = getBucket(oldValue);
		int newBucket = getBucket(newValue);
		
		this.buckets[oldBucket].remove(dot);
		this.buckets[newBucket].add(dot);
	}

	@Override
	public List<Integer> query(double min, double max) 
	{
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
			this.buckets[i] = new ArrayList<>();
		}
	}
}
