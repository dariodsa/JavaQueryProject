package hr.fer.zemris.thread.move;

import java.net.ServerSocket;

public abstract class Move {
	
	protected String[] workersAddress;
	protected int port;
	protected int numOfComponents;
	
	
	public Move(String[] workersAddress, int port, int numOfComponents) 
	{
		this.workersAddress = workersAddress;
		this.port = port;
		this.numOfComponents = numOfComponents;
	
	}
	
	public abstract void move();
	public abstract void relocate();
}
