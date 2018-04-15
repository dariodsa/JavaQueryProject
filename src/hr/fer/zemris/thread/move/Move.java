package hr.fer.zemris.thread.move;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.thread.MasterMethod;

public class Move extends Thread{
	
	protected String workersAddress;
	protected int port;
	protected int numOfComponents;
	private int moveNum;
	private int relocNum;
	public int STATUS;
	private MasterMethod parent;
	
	public Move(String workersAddress, int port, int numOfComponents, int moveNum, int relocNum, MasterMethod parent) 
	{
		this.workersAddress = workersAddress;
		this.port = port;
		this.numOfComponents = numOfComponents;
		this.moveNum = moveNum;
		this.relocNum = relocNum;
		this.parent = parent;
	}
	public void run() {
		
				move();
		
	}
	public void move() {
		
		try {
			Socket S = new Socket(workersAddress, port);
			
			OutputStream oos = new ObjectOutputStream(S.getOutputStream());
			oos.write(moveNum);
			oos.flush();
			InputStream ois = new ObjectInputStream(S.getInputStream());
			ois.read();
			S.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void relocate() {
		
		
	}
}
