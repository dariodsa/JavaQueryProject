package hr.fer.zemris.structures;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import hr.fer.zemris.exceptions.DimmensionException;


public class BucketStructure /*implements Iterable<Pair>*/ {

	
	public double minValue;
	public double maxValue;
	
	private int numOfBuckets;
	
	public ArrayList<Pair>[] buckets;
	private double sizePerBucket;
	
	private static Stack<Pair> cache;
	
	private List<Integer> result = new ArrayList<>(500000);
	
	public BucketStructure(double minValue,double maxValue, int numOfBuckets) 
	{
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.numOfBuckets = numOfBuckets;
		
		this.buckets = new ArrayList[numOfBuckets];
		
		BucketStructure.cache = new Stack<>();

		double size = Math.abs(maxValue - minValue);
		this.sizePerBucket = size / numOfBuckets;
		
		initBuckets();
		
	}
	private int getBucket(double value)
	{
		return (int)(Math.abs(value - this.minValue) / sizePerBucket);
	}
	
	
	public void add(double newValue, int dot)
	{
		int newBucket = getBucket(newValue);
		//this.buckets[newBucket].add(new Pair(dot,newValue));
		add(newValue, dot, newBucket);
	}
	public void add(double newValue, int dot, int newBucket)
	{
		if(cache.isEmpty()) {
			this.buckets[newBucket].add(new Pair(dot,newValue));
		} else {
			Pair p = cache.pop();
			p.setId(dot);
			p.setValue(newValue);
			this.buckets[newBucket].add(p);
		}
	}
	public void delete(Pair pair)
	{
		int oldBucket = getBucket(pair.value);
		
		cache.push(pair);
		this.buckets[oldBucket].remove(pair);
	}
	private void initBuckets()
	{
		for(int i=0; i<numOfBuckets; ++i)
		{
			this.buckets[i] = new ArrayList<>(120);
		}
	}
	public void update(Pair old, double newValue) throws DimmensionException 
	{
		if(!(minValue <= newValue && newValue <= maxValue))
			throw new DimmensionException(newValue, minValue, maxValue); 
		int oldBucket = getBucket(old.value);
		int newBucket = getBucket(newValue);
		
		if(newBucket != oldBucket)
		{
			this.buckets[oldBucket].remove(old);
			old.setValue(newValue);
			old.state = !old.state;
			this.buckets[newBucket].add(old);
		} else {
			int index = this.buckets[oldBucket].indexOf(old);
			this.buckets[oldBucket].get(index).setValue(newValue);
			this.buckets[oldBucket].get(index).state = !this.buckets[oldBucket].get(index).state;
		}
	}
	
	public List<Integer> query(double min, double max) throws IllegalArgumentException 
	{
		if(max<min)
			throw new IllegalArgumentException("In the function query, max param was lower than min. %nMAX: "+max+" , MIN: "+min);
		int firstBucket = getBucket(min);
		int lastBucket = getBucket(max);
		//System.out.println(min+"("+firstBucket +")"+ " " +max+" ("+lastBucket+")");
		result.clear();
		for(int i=firstBucket; i<=lastBucket; ++i)
		{
			for(Pair id : buckets[i])
			{
				if(id.value >= min && id.value <= max)
					result.add(id.id);
			}
		}
		return result;
	}
	private int buckPosition = 0;
	private int positionInside = 0;
	
	public boolean hasNext() {
		while(true)
		{
			if(positionInside + 1 < buckets[buckPosition].size()) {
				++positionInside;
				return true;
			}
			if(!buckets[buckPosition].isEmpty()) {
				positionInside = 0;
				return true;
			}
			else{
				++buckPosition;
				if(numOfBuckets <= buckPosition)
					return false;
				
			}
		}
	}

	public Pair next() {
		return buckets[buckPosition].get(positionInside);
	}
			
}
