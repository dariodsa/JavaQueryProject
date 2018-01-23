package hr.fer.zemris.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

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
		
		if(!Files.exists(dotsPath))
			throw new FileNotFoundException(dotsPath.toString());
		
	}
	public void run() throws IOException, NumOfDotArguments
	{
		initDots();
		
		Structure S = null;
		switch (structureType) {
		case 1:
			S = new BucketStructure(minValue, maxValue, numOfBuckets);
			break;
		case 2:
			S = new BinaryTree();
			break;
		default:
			break;
		}
		
		for(int i=0, len=dots.size(); i<len; ++i)
		{
			S.
		}
		
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
}
