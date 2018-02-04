package hr.fer.zemris.main;

import java.io.*;

import hr.fer.zemris.graphics.*;
import hr.fer.zemris.network.Network;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

	private static final int NUM_OF_ARG = 6;
	
	private static Path dotsPath;
	private static double queryFactor;
	private static double changeFactor;
	private static int problemType;

	private static int numOfArgumentsPerDot;

	private static int structureType;
	
	private static Window frame;
	
	public static void main(String[] args) 
	{
		if(args.length != NUM_OF_ARG)
		{
			if(args[0].compareTo("--help")==0)
				System.out.println(showHelp());
			else 
				System.out.println("Expected num of arguments to be "+NUM_OF_ARG);
		}
		
		dotsPath = Paths.get(args[0]);
		numOfArgumentsPerDot = Integer.parseInt(args[1]);
		
		structureType = Integer.parseInt(args[2]);
		
		queryFactor = Double.parseDouble(args[3]); 
		changeFactor = Double.parseDouble(args[4]);
		
		
		
		if(args[5].length()==1)
			runGUI();
		
		Program program;
		try {
			program = new Program(numOfArgumentsPerDot, dotsPath, structureType, queryFactor, changeFactor);
			
			SwingUtilities.invokeAndWait(()->
			{
				frame.picture.updatePicture(program.getDots());
			});
			//program.run();
			
		
		} catch (Exception ex) 
		{
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		
		
		/*Thread T = new Thread(
				()->{
					Socket echoSocket;
					try {
						
						InetAddress host = InetAddress.getLocalHost();
						System.out.println(host.getHostName());
						//echoSocket = new Socket(host.getHostName(),2001);
						for(int i=0; i<1500;i++){
							echoSocket = new Socket(host.getHostName(),2001);
						OutputStream out = new BufferedOutputStream(echoSocket.getOutputStream());
					    //    new PrintWriter(echoSocket.getOutputStream(), true);
						out.write(1500-i);
						out.flush();
						out.close();
						System.out.println("42");
						
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
		T.start();
		Network.socketTest(2001);*/
		
	}
	private static void runGUI()
	{
		try {
			SwingUtilities.invokeAndWait(
					()->
					{
						frame = new Window(700,400);
						frame.initGUI();
					}
			);
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	private static String showHelp()
	{
		/*System.out.println("You should pass this arguments to the program.");
		System.out.println("");*/
		return "";
	}

}
