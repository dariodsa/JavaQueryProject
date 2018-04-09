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
	
	public BucketStructure(double minValue,double maxValue, int numOfBuckets) 
	{
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.numOfBuckets = numOfBuckets;
		
		this.buckets = new ArrayList[numOfBuckets];
		
		this.cache = new Stack<>();

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
		
		
		cache.add(pair);
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
		int id = old.id;
		
		old.state = !old.state;
		
		if(newBucket != oldBucket)
		{
			this.buckets[oldBucket].remove(old);
			old.setValue(newValue);
			
			this.buckets[newBucket].add(old);
		} else {
			int index = this.buckets[oldBucket].indexOf(old);
			this.buckets[oldBucket].get(index).setValue(newValue);
		}
	}
	/*
	public void acceptNewDots(List<Pair> dots, double newBound, int leftOrRight) 
	{
		if(leftOrRight == 0)
		{
			this.minValue = newBound;
			this.minValuesPerBucket[0] = minValue;
			
			for(Pair P : dots)
				add(P.value, P.id, 0);
		}
		else if(leftOrRight == 1) 
		{
			this.maxValue = newBound;
			
			for(Pair P : dots)
				add(P.value, P.id, numOfBuckets - 1);
		}
		
	}
	
	private void sendDots(String ipAddress, List<Pair> dots, double newBound, int leftOrRight)
	{
		try {
			Socket S = new Socket(ipAddress, PORT);
			ObjectOutputStream os = new ObjectOutputStream(S.getOutputStream());
			os.write(11);
			os.write(id);
			os.write(leftOrRight);
			os.writeDouble(newBound);
			os.writeInt(dots.size());
			for(Pair P : dots) {
				os.writeInt(P.id);
				os.writeDouble(P.value);
			}
			os.flush();
			ObjectInputStream is = new ObjectInputStream(S.getInputStream());
			is.read();
			S.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void balance(int bucket)
	{
		if(bucket == 0)
		{
			int numInBucket = buckets[bucket].size();
			if(numInBucket > preferredValue * 1.3) {
				//must be balanced
				double minValue = minValuesPerBucket[bucket];
				double maxValue = minValuesPerBucket[bucket + 1];
				//predefined percents 10%
				List<Pair> toDelete = new ArrayList<>();
				List<Pair> toAdd = new ArrayList<>();
				for(Pair P : buckets[bucket]) {
					if(P.value < minValue * 1.2) {
						//add(P.value, P.id, bucket - 1);
						toAdd.add(P);
						toDelete.add(P);
					}
					else if(P.value > 0.8 * maxValue) {
						add(P.value, P.id, bucket + 1);
						toDelete.add(P);
					}
				}
				for(Pair P : toDelete)
					buckets[bucket].remove(P);
				
				minValuesPerBucket[bucket] *= 1.2;
				minValuesPerBucket[bucket + 1] *= 0.8;
				this.minValue = minValuesPerBucket[0];
				//need to connect to my left neighbor 
				//sendDots(ipAddressLeft, toAdd, minValuesPerBucket[0], 1);
				
			}
		}
		else if(bucket == numOfBuckets - 1)
		{
			int numInBucket = buckets[bucket].size();
			if(numInBucket > preferredValue * 1.3) {
				//must be balanced
				double minValue = minValuesPerBucket[bucket];
				double maxValue = this.maxValue;
				//predefined percents 10%
				List<Pair> toDelete = new ArrayList<>();
				List<Pair> toAdd = new ArrayList<>();
				for(Pair P : buckets[bucket]) {
					if(P.value < minValue * 1.2) {
						//add(P.value, P.id, bucket - 1);
						toDelete.add(P);
					}
					else if(P.value > 0.8 * maxValue) {
						//add(P.value, P.id, bucket + 1);
						toAdd.add(P);
						toDelete.add(P);
					}
				}
				for(Pair P : toDelete)
					buckets[bucket].remove(P);
				
				minValuesPerBucket[bucket] *= 1.2;
				//minValuesPerBucket[bucket + 1] *= 0.9;
				this.maxValue = this.maxValue * 0.8;
				this.minValue = minValuesPerBucket[0];
				//need to connect to my right neighbor 
				//sendDots(ipAddressRight, toAdd, this.maxValue, 0);
			}
		}
		else 
		{
			int numInBucket = buckets[bucket].size();
			if(numInBucket > preferredValue * 1.3) {
				//must be balanced
				double minValue = minValuesPerBucket[bucket];
				double maxValue = minValuesPerBucket[bucket + 1];
				//predefined percents 10%
				List<Pair> toDelete = new ArrayList<>();
				for(Pair P : buckets[bucket]) {
					if(P.value < minValue * 1.2) {
						add(P.value, P.id, bucket - 1);
						toDelete.add(P);
					}
					else if(P.value > 0.8 * maxValue) {
						add(P.value, P.id, bucket + 1);
						toDelete.add(P);
					}
				}
				for(Pair P : toDelete)
					buckets[bucket].remove(P);
				minValuesPerBucket[bucket] *= 1.2;
				minValuesPerBucket[bucket + 1] *= 0.8;
			}
		}
		
	}*/
	
	public List<Integer> query(double min, double max) throws IllegalArgumentException 
	{
		if(max<min)
			throw new IllegalArgumentException("In the function query, max param was lower than min. %nMAX: "+max+" , MIN: "+min);
		int firstBucket = getBucket(min);
		int lastBucket = getBucket(max);
		//System.out.println(min+"("+firstBucket +")"+ " " +max+" ("+lastBucket+")");
		List<Integer> result = new ArrayList<>();
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
