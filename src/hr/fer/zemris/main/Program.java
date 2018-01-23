package hr.fer.zemris.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

import hr.fer.zemris.exceptions.InvalidStructureType;
import hr.fer.zemris.exceptions.NumOfDotArguments;
import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.BucketStructure;
import hr.fer.zemris.structures.Structure;
import hr.fer.zemris.structures.dot.Dot;

public class Program 
{
	private int numOfDots;
	private int numOfArguments;
	private int problemType;
	private int structureType;
	
	private double changeFactor;
	private double queryFactor;
	
	private Path dotsPath;
	
	private List<Dot> dots;
	
	private final double minValue = -180.0;
	private final double maxValue = 180.0;
	private final int numOfBuckets = 7000000; 
	
	private final double CHANGE_RATE = 0.5;
	private final int ITERATIONS = 5;
	
	private Random rand = new Random();
	
	private Structure[] S;
	
	public Program(int numOfDots, int numOfArguments, Path dotsPath, int structureType, double queryFactor, double changeFactor, int problemType) throws FileNotFoundException
	{
		this.numOfDots = numOfDots;
		this.numOfArguments = numOfArguments;
		this.dotsPath = dotsPath;
		this.structureType = structureType;
		this.queryFactor = queryFactor;
		this.changeFactor = changeFactor;
		this.problemType = problemType;
		
		this.dots = new ArrayList<>();
		this.S = new Structure[this.numOfArguments];
		
		if(!Files.exists(dotsPath))
			throw new FileNotFoundException(dotsPath.toString());
		
	}
	public void run() throws IOException, NumOfDotArguments, InvalidStructureType
	{
		initDots();
		initStructure();
		
		//set dots in their starting position
		for(int i=0, len=dots.size(); i<len; ++i)
		{
			double[] value = dots.get(i).getValues();
			for(int j=0, len2 = value.length; j<len2; ++j)
				S[i].add(value[j], dots.get(i).getId());
		}
		
		int iter = ITERATIONS;
		while(iter-->=0)
		{
			double randValue = rand.nextDouble();
			if(randValue<changeFactor)
				updatePosition();
			randValue = rand.nextDouble();
			if(randValue<queryFactor)
				runQuery(1);
		}
		
	}
	
	private void initStructure() throws InvalidStructureType {
		
		if(structureType==1)
			for(int i=0;i<numOfArguments;++i)
				S[i] = new BucketStructure(minValue, maxValue, numOfBuckets);
		else if(structureType==2)
			for(int i=0;i<numOfArguments;++i)
				S[i] = new BinaryTree();
		else 
			throw new InvalidStructureType(structureType);
		
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
			for(String strValue : strValues)
			{
				values[iter++] = Double.parseDouble(strValue);
			}
			Dot dot = new Dot(numOfArguments , numOfLine-1);
			dots.add(dot);
		}
	}
	private void updatePosition()
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
		double diff = Math.abs(maxValue-minValue - 50);
		while(num-->0)
		{
			List<Integer>[] result = new ArrayList[numOfArguments];
			for(int i=0;i<numOfArguments;++i)
			{
				double min = rand.nextDouble()*diff + minValue;
				double max = min + 15;
				result[i] = new ArrayList<>();
				result[i] = S[i].query(min, max);
			}
			//todo intersection
		}
	}
}
