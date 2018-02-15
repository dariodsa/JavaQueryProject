package hr.fer.zemris.thread;

import hr.fer.zemris.exceptions.NumOfDotArguments;
import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.dot.Dot;
import hr.fer.zemris.structures.dot.DotCache;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JOptionPane;

public class MasterMethod {
	
	private static Random rand = new Random();
	private Parametars parametars;
	private String[] workersAddress;
	private InetAddress[] workers;
	
	private int portMaster;
	private int numOfComponents;
	private Path dotsPath;
	private PrintWriter logOutput;

	private List<DotCache>[] cacheDots;
	
	private int port;
	
	private double[][] minValues;
	private double[][] maxValues;
	
	private ArrayBlockingQueue<Long> result = new ArrayBlockingQueue<Long>(5);
	
	public MasterMethod(Parametars parametars,
			String[] workers, Path dotsPath, PrintWriter logOutput, int portMaster,int port) throws IOException, NumOfDotArguments{
		
		this.port = port;
		this.portMaster = portMaster;
		this.parametars = parametars;
		this.workersAddress = workers;
		this.numOfComponents = parametars.minValues.length;
		this.dotsPath = dotsPath;
		this.logOutput = logOutput;
		
		this.minValues = new double[workers.length][this.numOfComponents];
		this.maxValues = new double[workers.length][this.numOfComponents];
		
		this.cacheDots = new ArrayList[this.workersAddress.length];
		for(int i=0;i<this.cacheDots.length; ++i)
		{
			this.cacheDots[i] = new ArrayList<DotCache>();
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
		logOutput.write("All workers recieved parametars.");
		logOutput.write("Sending dots to the workers");
		initDots(dotsPath);
		for(int i=0;i<5;++i)
		{
			System.out.print(String.format("%d. %s%n", i+1, "iteration"));
			double rand = MasterMethod.rand.nextDouble();
			if(rand < 1/*parametars.queryFactor*/)
			{
				logOutput.write("I will perform query operation.");
				//todo
				double mini = 0;
				double maxi = 10;
				List<Thread> threads = new ArrayList<Thread>();
				for(int j=0;j<workersAddress.length;++j)
				{
					if(true)
					{
						//kreiraj novu dretvu sa socketom
						Thread T = new QueryThread(workersAddress[j], port,1,mini,maxi);
					}
				}
				for(Thread thread : threads)
					thread.start();
				for(Thread thread : threads)
					thread.join();
				logOutput.write("Query operation completed.");
			}
			if(rand < 1/*parametars.moveFactor*/)
			{
				logOutput.write("I will perform move operation.");
				List<Thread> threads = new ArrayList<Thread>();
				for(int j=0;j<workersAddress.length;++j)
				{
					Thread T = new MoveThread(workersAddress[j],port); 
					
					threads.add(T);
				}
				for(Thread thread : threads)
					thread.start();
				for(Thread thread : threads)
					thread.join();
			}
		}
	}
	private void initParametars() throws IOException 
	{
		for(int i=0;i<workers.length;++i)
		{
			Socket S = new Socket(this.workers[i], this.port);
			
			ObjectOutputStream oos = new ObjectOutputStream(S.getOutputStream());
			oos.writeInt(1);
			oos.writeObject(parametars);
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
		long numOfLine = 0;
		String line;
		while((line = br.readLine()) != null) 
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
			sendDot(dot, port);
			if(numOfLine % 100000 == 0){
				System.out.println(numOfLine + " / ");
				sendsDot();
			}
		}
		sendsDot();
		br.close();
		logOutput.println("Dots are sent over the network.");
	}
	private void sendsDot() throws IOException
	{
		for(int i=0;i<this.workers.length; ++i)
		{
			if(cacheDots[i].isEmpty())
				continue;
			Socket S = new Socket(this.workers[i], port);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(S.getOutputStream()));
			oos.writeInt(2);
			oos.writeInt(cacheDots[i].size());
			for(DotCache dot : cacheDots[i])
			{
				oos.writeLong(dot.getId());
				oos.write(dot.getComponent());
				oos.writeDouble(dot.getValue());
			}
			oos.flush();
			cacheDots[i].clear();
			ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
			int val = ois.readInt();
			if(val == 1){}
			ois.close();
			oos.close();
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
}
