package hr.fer.zemris.thread;

import hr.fer.zemris.thread.workers.MainWorker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class QueryThread extends Thread {
	
	private String address;
	
	private int id;
	private int port;
	private int component;
	
	private double min;
	private double max;
	
	public QueryThread(int id, String address, int port, int component, double min, double max)
	{
		this.id = id;
		this.address = address;
		this.port = port; 
		this.component = component;
		this.min = min;
		this.max = max;
	} 
	public void run()
	{
		try {
			Socket S = new Socket(address, port);
			ObjectOutputStream oos = new ObjectOutputStream(S.getOutputStream());
			oos.write(4);
			oos.writeDouble(min);
			oos.writeDouble(max);
			oos.write(component);
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
			
			System.out.println("Response from worker "+address);
			int len = ois.readInt();
			
			for(int i=0;i<len;++i)
			{
				int dotId = ois.readInt();
				if(MasterMethod.cache[this.id].isEmpty())
					MasterMethod.result[this.id].add(new MyInteger(dotId));
				else 
					MasterMethod.result[this.id].add(MasterMethod.cache[this.id].pop().setValue(dotId));
			}
				//MasterMethod.result[this.id].add(id);
				
			
			S.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	} 
}
