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
import java.util.ArrayDeque;
import java.util.Deque;

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
	
	//static ArrayDeque<Integer> DQ = new ArrayDeque<>();
	
	public static void main(String[] args) 
	{
		int port = 4564;
		
		if(args[0].length()==1)
			runGUI();
		
		Program program;
		try {
			//program = new Program(numOfArgumentsPerDot, dotsPath, structureType, queryFactor, changeFactor);
			
			SwingUtilities.invokeAndWait(()->
			{
				//frame.picture.updatePicture(program.getDots());
			});
			//program.run();
			
		
		} catch (Exception ex) 
		{
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		
		/*
		Thread T = new Thread(
				()->{
					Socket echoSocket;
					try {
						
						InetAddress host = InetAddress.getLocalHost();
						System.out.println(host.getHostName());
						
						int N = 1005;
						for(int i=0; i<N;i++){
							echoSocket = new Socket(host.getHostName(),port);
							DataOutputStream out = new DataOutputStream(echoSocket.getOutputStream());
						out.writeInt(N-i);
						//out.flush();
						out.close();
						System.out.println(N-i);
						
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				});
		T.start();
		Network.socketTest(port);*/
		
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
