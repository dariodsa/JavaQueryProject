package hr.fer.zemris.thread;

import hr.fer.zemris.thread.workers.MainWorker;

import java.io.BufferedInputStream;
import java.io.IOException;
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
	byte[] bi = new byte[8192]; 
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
			BufferedInputStream ois = new BufferedInputStream(S.getInputStream());
			
			System.out.println("Response from worker "+address);
			while(true)
			{
				int len = ois.read(bi);
				if(len == -1)break;
				for(int i=0;i<len;i+=4)
				{
					int dotId = MainWorker.bytesToInt(new byte[] {bi[i],bi[i+1],bi[i+2],bi[i+3]});
					MasterMethod.result[this.id].add(dotId);
				}
				//MasterMethod.result[this.id].add(id);
				
			}
			S.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	} 
}
