package hr.fer.zemris.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

import hr.fer.zemris.exceptions.DimmensionException;
import hr.fer.zemris.exceptions.InvalidStructureType;
import hr.fer.zemris.exceptions.NumOfDotArguments;
import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.BucketStructure;
import hr.fer.zemris.structures.Structure;
import hr.fer.zemris.structures.dot.Dot;
import hr.fer.zemris.structures.dot.Functions;

public class Program 
{
	
	private int numOfArguments;
	private int structureType;
	
	private double changeFactor;
	private double queryFactor;
	
	private Path dotsPath;
	
	private List<Dot> dots;
	
	private final double[] minValue;
	private final double[] maxValue;
	private final int numOfBuckets = 70000; 
	
	private final double CHANGE_RATE = 0.01;
	private final int ITERATIONS = 5;
	
	private Random rand = new Random();
	
	private Structure[] S;
	
	public Program(int numOfArguments, Path dotsPath, int structureType, double queryFactor, double changeFactor) throws IOException, NumOfDotArguments, InvalidStructureType
	{
		this.numOfArguments = numOfArguments;
		this.dotsPath = dotsPath;
		this.structureType = structureType;
		this.queryFactor = queryFactor;
		this.changeFactor = changeFactor;
		
		this.dots = new ArrayList<>();
		
		this.S = new Structure[this.numOfArguments];
		this.maxValue = new double[this.numOfArguments];
		this.minValue = new double[this.numOfArguments];
		
		//stupid init 
		for(int i=0;i<numOfArguments;++i)
		{
			this.minValue[i] = -180.0;
			this.maxValue[i] = 180.0;
		}
		
		if(!Files.exists(dotsPath))
			throw new FileNotFoundException(dotsPath.toString());
		
		initDots();
		initStructure();
		//set dots in their starting position
		setDotsPosition();
	}
	public void run() throws DimmensionException
	{
		
		
		int iter = ITERATIONS;
		long timeStarting = System.currentTimeMillis();
		while(iter-->=0)
		{
			System.out.printf("Iterations remaining %d %n",iter+1);
			double randValue = rand.nextDouble();
			if(randValue<changeFactor)
				updatePosition();
			randValue = rand.nextDouble();
			if(randValue<queryFactor)
				runQuery(1);
		}
		long timeEnding = System.currentTimeMillis();
		System.out.println("Total duration is equal to "
		                   +(timeEnding-timeStarting)
		                   +" msec. ");
		System.out.println("Structure used: "+this.S[0].getClass().getSimpleName());
	}
	
	private void setDotsPosition() 
	{
		for(int i=0, len=dots.size(); i<len; ++i)
		{
			double[] value = dots.get(i).getValues();
			for(int j=0, len2 = value.length; j<len2; ++j)
				S[j].add(value[j], dots.get(i).getId());
		}
		System.out.println("Dots are in their position.");
	}
	private void initStructure() throws InvalidStructureType {
		
		if(structureType==1)
			for(int i=0;i<numOfArguments;++i)
				S[i] = new BucketStructure(minValue[i], maxValue[i], numOfBuckets);
		else if(structureType==2)
			for(int i=0;i<numOfArguments;++i)
				S[i] = new BinaryTree();
		else 
			throw new InvalidStructureType(structureType);
		
		System.out.println("Structures are ready.");
	}
	private void initDots() throws IOException, NumOfDotArguments
	{
		List<String> lines = Files.readAllLines(dotsPath);
		int numOfLine = 0;
		for(String line : lines)
		{
			++numOfLine;
			
			String[] strValues = line.split(",");
			if(strValues.length != numOfArguments)
				throw new NumOfDotArguments(numOfLine);
			double[] values = new double[strValues.length];
			int iter = 0;
			Dot dot = new Dot(numOfArguments , numOfLine-1);
			for(String strValue : strValues)
			{
				dot.setValue(iter++, Double.parseDouble(strValue));
			}
			dots.add(dot);
		}
		System.out.println("Dots are loaded.");
	}
	private void updatePosition() throws DimmensionException
	{
		for(int i=0, len=dots.size(); i<len; ++i)
		{
			for(int j=0; j<numOfArguments; ++j)
			{
				double newValue = dots.get(i).getValue(j) + CHANGE_RATE * rand.nextGaussian();
				
				S[j].update(dots.get(i).getValue(j), newValue, dots.get(i).getId());
			}
		}
	}
	private void runQuery(int num)
	{
		while(num-->0)
		{
			List<Integer>[] result = new ArrayList[numOfArguments];
			for(int i=0;i<numOfArguments;++i)
			{
				double diff = Math.abs(maxValue[i]-minValue[i] - 15);
				double min = rand.nextDouble()*diff + minValue[i];
				double max = min + 15;
				result[i] = new ArrayList<>();
				result[i] = S[i].query(min, max);
			}
			List<Integer> finalList = new ArrayList<>(result[0]);
			for(int i=1;i<numOfArguments;++i)
				finalList = Functions.intersection(finalList, result[i]);
		}
	}
	public List<Dot> getDots()
	{
		return this.dots;
	}
}
