package hr.fer.zemris.structures;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import hr.fer.zemris.exceptions.DimmensionException;


public class BucketStructure implements Iterable<Pair> {

	
	public double minValue;
	public double maxValue;
	
	private int numOfBuckets;
	
	public LinkedList<Pair>[] buckets;
	public double[] minValuesPerBucket;
	
	private int preferredValue;
	
	private final int PORT;
	private int id;
	private String ipAddressLeft;
	private String ipAddressRight;
	
	public BucketStructure(double minValue,double maxValue, int numOfBuckets, int preferredValue, String ipAddressLeft, String ipAddressRight, int port, int id) 
	{
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.numOfBuckets = numOfBuckets;
		
		this.buckets = new LinkedList[numOfBuckets];
		this.minValuesPerBucket = new double[numOfBuckets];
		this.preferredValue = preferredValue;
		
		this.ipAddressLeft = ipAddressLeft;
		this.ipAddressRight = ipAddressRight;
		this.PORT = port;
		this.id = id;
		initBuckets();
		
	}
	private int getBucket(double value)
	{
		int low = 0;
		int midd = 0;
		int high = numOfBuckets - 1;
		
		while(low < high) {
			midd = (low + high + 1) >> 1;
		
			if(minValuesPerBucket[midd] > value)
				high = midd - 1;
			else
				low = midd;
		}
		if(low == numOfBuckets - 1)
			return low;
		if(minValuesPerBucket[low] <= value && minValuesPerBucket[low + 1] > value)
			return low;
		else return low + 1;
		
	}
	
	
	public void add(double newValue, int dot)
	{
		int newBucket = getBucket(newValue);
		this.buckets[newBucket].add(new Pair(dot,newValue));
	}
	public void add(double newValue, int dot, int newBucket)
	{
		this.buckets[newBucket].add(new Pair(dot,newValue));
	}
	
	public void update(double oldValue, double newValue, int dot) throws DimmensionException 
	{
		if(!(minValue <= newValue && newValue <= maxValue))
			throw new DimmensionException(newValue, minValue, maxValue); 
		int oldBucket = getBucket(oldValue);
		int newBucket = getBucket(newValue);
																	
		if(newBucket != oldBucket)
		{
			this.buckets[oldBucket].remove(new Pair(dot,oldValue));
			add(newValue, dot, newBucket);
		}
	}
	
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
		
	}
	
	public List<Integer> query(double min, double max) throws IllegalArgumentException 
	{
		if(max<min)
			throw new IllegalArgumentException("In the function query, max param was lower than min. %nMAX: "+max+" , MIN: "+min);
		int firstBucket = getBucket(min);
		int lastBucket = getBucket(max);
		System.out.println(min+"("+firstBucket +")"+ " " +max+" ("+lastBucket+")");
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
	public void delete(double value, int dot)
	{
		int oldBucket = getBucket(value);
		this.buckets[oldBucket].remove(new Pair(dot, value));
	}
	private void initBuckets()
	{
		double minVal = this.minValue;
		double step = (this.maxValue - this.minValue) / numOfBuckets;
		for(int i=0; i<numOfBuckets; ++i)
		{
			this.buckets[i] = new LinkedList<>();
			this.minValuesPerBucket[i] = minVal;
			minVal += step;
		}
	}
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
