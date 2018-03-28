package hr.fer.zemris.thread;

import java.io.*;
import java.net.Socket;

public class MoveThread extends Thread 
{
	private String address;
	private int port;
	private int type;
	public MoveThread(String address, int port, int type)
	{
		this.address = address;
		this.port = port;
		this.type = type;
	}
	public void run()
	{
		try {
			Socket S = new Socket(address, port);
			OutputStream oos = new ObjectOutputStream(S.getOutputStream());
			oos.write(type);
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
