package hr.fer.zemris.thread;

import hr.fer.zemris.exceptions.NumOfDotArguments;
import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.dot.Dot;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.swing.JOptionPane;

public class MasterMethod {
	
	private final int PORT = 8965;
	
	private Parametars parametars;
	private String[] workersAddress;
	private InetAddress[] workers;
	
	private int portMaster;
	private int numOfComponents;
	private Path dotsPath;
	private PrintWriter logOutput;
	
	public MasterMethod(Parametars parametars,
			String[] workers, Path dotsPath, PrintWriter logOutput, int portMaster) throws IOException, NumOfDotArguments{
		
		this.portMaster = portMaster;
		this.parametars = parametars;
		this.workersAddress = workers;
		this.numOfComponents = parametars.minValues.length;
		this.dotsPath = dotsPath;
		this.logOutput = logOutput;
	}
	public void run() throws IOException, NumOfDotArguments {
		System.out.println("Usao  ....");
		try {
			initConnection();
			
		} catch (UnknownHostException e) {
			//e.getMessage()
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		ServerSocket serverSocket = new ServerSocket(portMaster);
		initParametars();
		int workersLeft = workers.length;
		while(workersLeft > 0)
		{
			Socket client = serverSocket.accept();
			int br = new ObjectInputStream(client.getInputStream()).readInt();
			System.out.println("Br "+br);
			if(br == 1)
				--workersLeft;
		}
		serverSocket.close();
		logOutput.println("All workers recieved parametars.");
		logOutput.println("Sending dots to the workers");
		initDots(dotsPath);
	}
	private void initParametars() throws IOException 
	{
		for(int i=0;i<workers.length;++i)
		{
			Network.sendObject(workers[i], PORT, 1, parametars);
		}
		
	}
	private void initConnection() throws UnknownHostException, IOException
	{
		this.workers = new Inet4Address[this.workersAddress.length];
		for(int i=0;i<workers.length;++i)
		{	
			System.out.printf("%s %s%n",this.workersAddress[i],InetAddress.getByName(workersAddress[i]).getHostAddress());
			this.workers[i] = InetAddress.getByName(workersAddress[i]);
			System.out.printf("%s %s %s%n",this.workersAddress[i],InetAddress.getByName(workersAddress[i]).getHostAddress(),workers[i].getHostAddress().toString());
		}
	}
	private void initDots(Path dotsPath) throws IOException, NumOfDotArguments
	{
		List<String> lines = Files.readAllLines(dotsPath);
		long numOfLine = 0;
		for(String line : lines)
		{
			++numOfLine;
			String[] strValues = line.split(",");
			if(strValues.length != numOfComponents)
				throw new NumOfDotArguments((int)numOfLine);
			int iter = 0;
			Dot dot = new Dot(numOfComponents , numOfLine-1);
			for(String strValue : strValues)
			{
				dot.setValue(iter++, Double.parseDouble(strValue));
			}
			sendDot(dot, PORT);
		}
		logOutput.println("Dots are sent over the network.");
	}
	private void sendDot(Dot dot, int port) throws IOException 
	{
		int numOfWorkers = workersAddress.length;
		for(int i=0;i<dot.getNumOfComponents();++i){
			double diff = Math.abs(parametars.maxValues[i]-parametars.minValues[i]);
			double start = Math.abs(parametars.minValues[i]-dot.getValue(i));
			int numOfWorker = (int)(start/(diff/numOfWorkers));
			Network.sendObject(workers[numOfWorker], port, 2, dot);
		}
	}
}
