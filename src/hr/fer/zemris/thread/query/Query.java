package hr.fer.zemris.thread.query;

import java.net.ServerSocket;
import java.util.List;

import hr.fer.zemris.structures.Parametars;

public abstract class Query {
	
	protected Parametars parametars;
	
	protected int port;
	protected int numOfComponents;
	
	protected String[] workersAddress;
	
	public Query(Parametars parametars, String[] workersAddress, int port) {
		this.parametars = parametars;
		this.numOfComponents = parametars.maxValues.length;
		this.workersAddress = workersAddress;
		this.port = port;
	}
	
	public abstract List<Integer> performQuery(double min, double max);
}
