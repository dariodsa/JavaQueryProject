package hr.fer.zemris.main;

import java.io.FileNotFoundException;

import hr.fer.zemris.graphics.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

	private static final int NUM_OF_ARG = 7;
	
	private static int numOfDots;
	private static Path dotsPath;
	private static double queryFactor;
	private static double changeFactor;
	private static int problemType;

	private static int numOfArgumentsPerDot;

	private static int structureType;
	
	public static void main(String[] args) 
	{
		if(args.length != NUM_OF_ARG)
		{
			if(args[0].compareTo("--help")==0)
				printHelp();
			else 
				System.out.println("Expected num of arguments to be "+NUM_OF_ARG);
		}
		
		numOfDots = Integer.parseInt(args[0]);
		dotsPath = Paths.get(args[1]);
		numOfArgumentsPerDot = Integer.parseInt(args[2]);
		
		structureType = Integer.parseInt(args[3]);
		
		queryFactor = Double.parseDouble(args[3]); 
		changeFactor = Double.parseDouble(args[4]);
		
		problemType = Integer.parseInt(args[4]);
		
		try {
			SwingUtilities.invokeAndWait(
					()->
					{
						Window frame = new Window(100,100);
						frame.init();
					}
			);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Program program;
		try {
			program = new Program(numOfDots, numOfArgumentsPerDot, dotsPath, structureType, queryFactor, changeFactor, problemType);
			//program.run();
		
		} catch (Exception ex) 
		{
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		
		
		
	}
	private static void printHelp()
	{
		System.out.println("You should pass this arguments to the program.");
		System.out.println("");
	}

}
