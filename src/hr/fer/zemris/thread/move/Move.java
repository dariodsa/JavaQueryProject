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
import hr.fer.zemris.thread.MoveThread;

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
		while(true) {
			System.out.println("run");
			synchronized (this ) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(STATUS == 1) {
				move();
			} else {
				relocate();
			}
			
			System.out.println("Enter.");
			synchronized (parent) {
				parent.notify();
			}
		}
	}
	public void move() {
		System.out.println("I will perform move operation.");
		
		long t1 = System.currentTimeMillis();
		try {
			Socket S = new Socket(workersAddress, port);
			
			OutputStream oos = new ObjectOutputStream(S.getOutputStream());
			oos.write(moveNum);
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
		long t2 = System.currentTimeMillis();

		System.out.printf("Move operation completed. %d milisec%n", t2 - t1);
	}
	public void relocate() {
		System.out.println("I will perform relocation operation.");
		
		long t1 = System.currentTimeMillis();
		try {
			Socket S = new Socket(workersAddress, port);
			OutputStream oos = new ObjectOutputStream(S.getOutputStream());
			oos.write(relocNum);
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
		long t2 = System.currentTimeMillis();

		System.out.printf("Relocation operation completed. %d milisec%n", t2 - t1);
	}
}
