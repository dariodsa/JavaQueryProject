package hr.fer.zemris.thread;

import hr.fer.zemris.exceptions.NumOfDotArguments;
import hr.fer.zemris.graphics.component.Line;
import hr.fer.zemris.graphics.component.PPicture;
import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.dot.Dot;
import hr.fer.zemris.structures.dot.DotCache;
import hr.fer.zemris.structures.dot.Functions;
import hr.fer.zemris.thread.move.Move;

import hr.fer.zemris.thread.query.Query;
import hr.fer.zemris.thread.query.QueryBinary;
import hr.fer.zemris.thread.query.QueryBucket;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JOptionPane;

public class MasterMethod {
	
	private static Random rand = new Random();
	
	//private PPicture picture;
	
	public static Parametars parametars = new Parametars();
	private Query query;
	
	private String[] workersAddress;
	private InetAddress[] workers;
	
	private int portMaster;
	private int numOfComponents;
	public static Path dotsPath;

	private List<DotCache>[] cacheDots;
	
	private int port;
	
	private double[][] minValues;
	private double[][] maxValues;
	
	private Move[] moveThreads; 
	
	int numOfDots;
	
	public static ArrayList<MyInteger>[] result;
	public static Stack<MyInteger>[] cache;
	public static int moveFinish;
	
	public MasterMethod( String[] workers, Path dotsPath, int portMaster,int port) throws IOException, NumOfDotArguments{
		
		
		this.port = port;
		this.portMaster = portMaster;
		this.workersAddress = workers;
		this.numOfComponents = parametars.minValues.length;
		this.dotsPath = dotsPath;
		
		
		this.minValues = new double[workers.length][this.numOfComponents];
		this.maxValues = new double[workers.length][this.numOfComponents];
		
		this.cacheDots = new ArrayList[this.workersAddress.length];
		
		result    = new ArrayList[this.workersAddress.length];
		cache     = new Stack[this.workersAddress.length];
		
		for(int i=0;i<this.cacheDots.length; ++i)
		{
			this.cacheDots[i] = new ArrayList<DotCache>();
			result[i]    = new ArrayList<MyInteger>();
			cache[i] = new Stack<MyInteger>();
		}
		int moveNum = 0;
		int relocNum = 0;
		switch(parametars.structureType) {
			case BUCKET:
				query = new QueryBucket(parametars, workersAddress, port);
				moveNum = 3;
				relocNum = 16;
				break;
			case BINARY_TREE:
				query = new QueryBinary(parametars, workers, port);
				moveNum = 15;
				relocNum = 16;
				break;
		}
		
		this.moveThreads = new Move[this.workersAddress.length];
		for(int i = 0;i < workersAddress.length; ++i) {
			moveThreads[i] = new Move(this.workersAddress[i], port, portMaster, moveNum, relocNum,this);
			moveThreads[i].start();
		}
		
	}
	
	public void run() throws IOException, NumOfDotArguments, InterruptedException {
		
		ServerSocket serverSocket = new ServerSocket(portMaster);
		System.out.println("Usao  ....");
		try {
			initConnection();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			serverSocket.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			serverSocket.close();
			return;
		}
		initParametars();
		serverSocket.close();

		long tMain = System.currentTimeMillis();
		initDots(dotsPath);
		for(int i=0;i<20;++i)
		{
			System.out.print(String.format("%d. %s%n", i+1, "iteration"));
			double rand = MasterMethod.rand.nextDouble();
			
			if(rand < parametars.queryFactor)
			{
				double min = -40;
				double max = +40;
				int size = query.performQuery(min, max).size();
				System.out.println("Result: " + size);
				
			}
			if(rand < parametars.moveFactor)
			{
				moveFinish = 0;
				System.out.println("move");
				for(int j=0;j<workersAddress.length;++j) {
					moveThreads[j].STATUS = 1;
					synchronized (moveThreads[j]) {
						try {
							moveThreads[j].notify();
						} catch(IllegalMonitorStateException ex) {System.out.println(ex.getMessage());}
					}
					synchronized (this) {
						System.out.println("waiting.");
						this.wait();
						System.out.println("waiting exit..");
					}
				}
				
				
				System.out.println("Done.");
				moveFinish = 0;
				for(int j=0;j<workersAddress.length;++j) {
					moveThreads[j].STATUS = 2;
					synchronized (moveThreads[j]) {
						try {
							moveThreads[j].notify();
						} catch(IllegalMonitorStateException ex) {System.out.println(ex.getMessage());}
					}
					synchronized (this) {
						System.out.println("waiting.");
						this.wait();
						System.out.println("waiting exit..");
					}
				}
				
				System.out.println("Done.");
			}
			
		}
		long tMainEnd = System.currentTimeMillis();
		System.out.println("Total time: "+(tMainEnd-tMain));
		for(int j=0;j<workersAddress.length;++j) {
			Socket S = new Socket(this.workers[j], port);
			OutputStream oos = new ObjectOutputStream(S.getOutputStream());
			oos.write(5);
			oos.flush();
			InputStream ois = new ObjectInputStream(S.getInputStream());
			int val = ois.read();
			if(val==1) 
			{
				S.close();
				return;
			}
			S.close();
		}
		/*
		for(int i=0;i<workers.length;++i)
		{
			Socket S = new Socket(workers[i], port);
			ObjectOutputStream oo = new ObjectOutputStream(S.getOutputStream());
			oo.write(12);
			oo.flush();
			ObjectInputStream oi = new ObjectInputStream(S.getInputStream());
			int x1 = oi.readInt();
			for(int j=0;j<x1;++j)
			{
				double d = oi.readDouble();
				picture.addLine(new Line(d, -90, d, 90),1);
			}
			int x2 = oi.readInt();
			for(int j=0;j<x2;++j)
			{
				double d = oi.readDouble();
				picture.addLine(new Line(-180, d, 180, d),2);
			}
			S.close();
		}*/
		
	}
	private void initParametars() throws IOException 
	{
		System.out.println("Sending parametars ...");
		System.out.println(parametars);
		
		for(int i=0;i<workers.length;++i)
		{
			Socket S = new Socket(this.workers[i], this.port);
			
			ObjectOutputStream oos = new ObjectOutputStream(S.getOutputStream());
			oos.write(1);
			oos.write(i);
			oos.writeObject(parametars);
			
			if(i == 0)
				oos.writeObject("");
			else
				oos.writeObject(workersAddress[i-1]);
			if(i+1 == workers.length)
				oos.writeObject("");
			else
				oos.writeObject(workersAddress[i+1]);
			
			oos.write(workers.length);
			for(int j=0;j<workers.length; ++j) {
				oos.writeObject(this.workersAddress[j]);
				for(int k=0;k<numOfComponents; ++k) {
					oos.writeDouble(minValues[j][k]);
					oos.writeDouble(maxValues[j][k]);
				}
			}
			
			
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
			int val = ois.readInt();
			if(val == 1){
				System.out.println("Primio "+this.workersAddress[i]);
			}
			else {
				S.close();
				throw new IOException("nisam dobio potvrdu.");
			}
			ois.close();
			oos.close();
			S.close();
		}
		System.out.println("Operation completed.");
	}
	private void initConnection() throws UnknownHostException, IOException
	{
		this.workers = new Inet4Address[this.workersAddress.length];
		
		for(int i=0;i<workers.length;++i)
		{	
			System.out.printf("%s %s%n",this.workersAddress[i],InetAddress.getByName(workersAddress[i]).getHostAddress());
			this.workers[i] = InetAddress.getByName(workersAddress[i]);
			
			//Init min and max values
			for(int k=0;k<numOfComponents;++k)
			{
				double diff = Math.abs(parametars.minValues[k] - parametars.maxValues[k]);
				double each = diff / (double)this.workers.length;
				if(i == 0){
					minValues[i][k] = parametars.minValues[k];
					maxValues[i][k] = minValues[i][k] + each;
				}
				else if(i == workers.length - 1){
					minValues[i][k] = maxValues[i][k] - each;
					maxValues[i][k] = parametars.maxValues[k];
				}
				else {
					minValues[i][k] = maxValues[i-1][k];
					maxValues[i][k] = minValues[i][k] + each;
				}
			}
		}
	}
	private void initDots(Path dotsPath) throws IOException, NumOfDotArguments
	{
		//List<String> lines = Files.readAllLines(dotsPath);
		BufferedReader br = new BufferedReader(new FileReader(dotsPath.toString()));
		int numOfLine = 0;
		String line;
		while((line = br.readLine()) != null) 
		{
			++numOfLine;
			String[] strValues = line.split(",");
			if(strValues.length != numOfComponents)
			{
				br.close();
				throw new NumOfDotArguments((int)numOfLine);
			}
			int iter = 0;
			Dot dot = new Dot(numOfComponents , numOfLine-1);
			for(String strValue : strValues)
			{
				dot.setValue(iter++, Double.parseDouble(strValue));
			}
			//picture.addDot(dot);
			sendDot(dot, port);
			if(numOfLine % 1000000 == 0){
				System.out.println(numOfLine + " / ");
				sendsDot();
			}
		}
		numOfDots = numOfLine;
		sendsDot();
		br.close();
		//logOutput.println("Dots are sent over the network.");
	}
	private void sendsDot() throws IOException
	{
		System.out.println("Sending dots over the network.");
		for(int i=0;i<this.workers.length; ++i)
		{
			if(cacheDots[i].isEmpty())
				continue;
			Socket S = new Socket(this.workers[i], port);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(S.getOutputStream()));
			oos.write(2);
			oos.writeInt(cacheDots[i].size());
			
			for(DotCache dot : cacheDots[i])
			{
				oos.writeInt(dot.getId());
				oos.write(dot.getComponent());
				oos.writeDouble(dot.getValue());
			}
			oos.flush();
			cacheDots[i].clear();
			ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
			ois.read();
			ois.close();
			S.close();
		}
	}
	private void sendDot(Dot dot, int port) throws IOException 
	{
		int numOfWorkers = workersAddress.length;
		for(int i=0;i<dot.getNumOfComponents();++i)
		{
			for(int j=0;j<numOfWorkers;++j)
			{
				if(minValues[j][i] <= dot.getValue(i) && maxValues[j][i] >= dot.getValue(i))
				{
					cacheDots[j].add(new DotCache(dot.getId(),i,dot.getValue(i)));
					break;
				}
			}
		}
	}
	class EchoThread extends Thread
	{
		Socket client;
		public EchoThread(Socket client) 
		{
			this.client = client;
		}
		public void run() 
		{
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(client.getInputStream());
				while(true)
				{
					int component = ois.read();
					if(component == -1)
					{
						client.close();
						return;
					}
					double value  = ois.readDouble();
					for(int i=0;i<workersAddress.length;++i)
					{
						if(minValues[i][component] <= value && maxValues[i][component] >= value)
						{
							ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
							oos.writeBytes(workersAddress[i]);
							oos.flush();
							break;
						}
					}
				}
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
}
