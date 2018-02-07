package hr.fer.zemris.thread;

import hr.fer.zemris.exceptions.NumOfDotArguments;
import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.dot.Dot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.swing.JOptionPane;

public class MasterMethod {
	
	private final int PORT = 7654;
	
	private Parametars parametars;
	private String[] workersAddress;
	private InetAddress[] workers;
	
	private int numOfComponents;
	private Path dotsPath;
	private PrintWriter logOutput;
	
	public MasterMethod(Parametars parametars,
			String[] workers, Path dotsPath, PrintWriter logOutput) throws IOException, NumOfDotArguments{
		
		this.parametars = parametars;
		this.workersAddress = workers;
		this.numOfComponents = parametars.minValues.length;
		this.dotsPath = dotsPath;
		this.logOutput = logOutput;
	}
	public void run() throws IOException, NumOfDotArguments {
		try {
			initConnection();
			
		} catch (UnknownHostException e) {
			//e.getMessage()
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		initParametars();
		initDots(dotsPath);
	}
	private void initParametars() throws IOException 
	{
		for(int i=0;i<workers.length;++i)
		{
			Network.sendObject(workers[i], PORT, parametars);
		}
		
	}
	private void initConnection() throws UnknownHostException, IOException
	{
		this.workers = new Inet4Address[this.workers.length];
		for(int i=0;i<workers.length;++i)
		{	
			this.workers[i] = InetAddress.getByName(workersAddress[i]);
		}
	}
	private void initDots(Path dotsPath) throws IOException, NumOfDotArguments
	{
		List<String> lines = Files.readAllLines(dotsPath);
		int numOfLine = 0;
		for(String line : lines)
		{
			++numOfLine;
			String[] strValues = line.split(",");
			if(strValues.length != numOfComponents)
				throw new NumOfDotArguments(numOfLine);
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
			Network.sendObject(workers[numOfWorker], port, dot);
		}
	}
}
