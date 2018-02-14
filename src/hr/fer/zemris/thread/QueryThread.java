package hr.fer.zemris.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class QueryThread extends Thread {
	
	private String address;
	private int port;
	private int component;
	private double min;
	private double max;
	
	public QueryThread(String address, int port, int component, double min, double max)
	{
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
			oos.writeInt(4);
			oos.writeDouble(min);
			oos.writeDouble(max);
			oos.writeInt(component);
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
			int size = ois.readInt();
			for(int i=0;i<size;++i)
			{
				long id = ois.readLong(); 
				//dodaj id u listu za presjek
				
			}
			S.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	} 
}
