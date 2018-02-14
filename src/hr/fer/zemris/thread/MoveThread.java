package hr.fer.zemris.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MoveThread extends Thread 
{
	private String address;
	private int port;
	public MoveThread(String address, int port)
	{
		this.address = address;
		this.port = port;
	}
	public void run()
	{
		try {
			Socket S = new Socket(address, port);
			ObjectOutputStream oos = new ObjectOutputStream(S.getOutputStream());
			oos.writeInt(3);
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
			int val = ois.readInt();
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
