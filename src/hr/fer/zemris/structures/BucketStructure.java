package hr.fer.zemris.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import hr.fer.zemris.exceptions.DimmensionException;
import hr.fer.zemris.structures.dot.Dot;

public class BucketStructure implements Structure {

	//private arrray
	
	
	private double minValue;
	private double maxValue;
	private int numOfBuckets;
	private double sizePerBucket;
	
	private LinkedList<Pair>[] buckets;
		
	public BucketStructure(double minValue,double maxValue, int numOfBuckets) 
	{
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.numOfBuckets = numOfBuckets;
		
		double size = Math.abs(maxValue - minValue);
		this.sizePerBucket = size / numOfBuckets;
		
		this.buckets = new LinkedList[numOfBuckets];
		initBuckets();
		
	}
	private int getBucket(double value)
	{
		return (int)(Math.abs(value - this.minValue) / sizePerBucket);
	}
	@Override
	public void add(double newValue, Long dot)
	{
		int newBucket = getBucket(newValue);
		boolean k  = this.buckets[newBucket].add(new Pair(dot,newValue));
		if(k==false)
		{
			boolean K = this.buckets[newBucket].contains(new Pair(dot,newValue));
			for(Pair P : this.buckets[newBucket]){
				if(P.equals(new Pair(dot,newValue))){
					System.out.println("jednak kao dolje "+P.id +" "+ P.value);
					break;
				}
			}
			System.out.printf("%d %f sranje %d%n",dot,newValue,K?1:0);
		}
		
	}
	@Override
	public void update(double oldValue, double newValue, Long dot) throws DimmensionException 
	{
		if(!(minValue <= newValue && newValue <= maxValue))
			throw new DimmensionException(newValue, minValue, maxValue); 
		int oldBucket = getBucket(oldValue);
		int newBucket = getBucket(newValue);
		
		if(newBucket != oldBucket)
		{
			this.buckets[oldBucket].remove(new Pair(dot,oldValue));
			this.buckets[newBucket].add(new Pair(dot,newValue));
		}
	}

	@Override
	public List<Long> query(double min, double max) throws IllegalArgumentException 
	{
		if(max<min)
			throw new IllegalArgumentException("In the function query, max param was lower than min. %nMAX: "+max+" , MIN: "+min);
		int firstBucket = getBucket(min);
		int lastBucket = getBucket(max);
		System.out.println(firstBucket + " " +lastBucket);
		List<Long> result = new ArrayList<>();
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
	public void delete(double value, Long dot)
	{
		int oldBucket = getBucket(value);
		this.buckets[oldBucket].remove(dot);
	}
	private void initBuckets()
	{
		for(int i=0; i<numOfBuckets; ++i)
		{
			//System.out.printf("%d %d %n",i,numOfBuckets);
			this.buckets[i] = new LinkedList<>();
		}
	}
	@Override
	public Iterator<Pair> iterator() {
		
		Iterator<Pair> it = new Iterator<Pair>(){
			private int numPosition = 0;
			private int buckPosition = 0;
			private Iterator<Pair> iterator = buckets[buckPosition].iterator();
			@Override
			public boolean hasNext() {
				while(true)
				{
					if(iterator.hasNext())
						return true;
					else{
						++buckPosition;
						if(numOfBuckets <= buckPosition)
							return false;
						iterator = buckets[buckPosition].iterator();
					}
				}
			}

			@Override
			public Pair next() {
				return iterator.next();
			}
			
		};
		return it;
	}
}
