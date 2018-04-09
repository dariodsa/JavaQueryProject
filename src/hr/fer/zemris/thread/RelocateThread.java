package hr.fer.zemris.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import hr.fer.zemris.structures.dot.DotCache;

public class RelocateThread extends Thread {

	private String address;
	private int port;
	private int type;
	private List<DotCache> list;
	
	public RelocateThread(String address, int port, int type)
	{
		this.address = address;
		this.port = port;
		this.type = type;
	}
	public void setList(List<DotCache> list) {
		this.list = list;
	}
	public synchronized void run()
	{
		while(true) {
			try {
				this.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Socket S = new Socket(address, port);
				int listSize = list.size();
				ObjectOutputStream oos = new ObjectOutputStream(S.getOutputStream());
				oos.write(type);
				
				oos.writeInt(listSize);
				for(DotCache dot :list) {
					oos.writeInt(dot.getComponent());
					oos.writeInt(dot.getId());
					oos.writeDouble(dot.getValue());
				}
				oos.flush();
				InputStream ois = new ObjectInputStream(S.getInputStream());
				int val = ois.read();
				if(val==1) 
				{
					S.close();
					return;
				}
				S.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
