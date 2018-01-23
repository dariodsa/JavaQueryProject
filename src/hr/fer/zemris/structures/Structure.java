package hr.fer.zemris.structures;

import java.util.List;


public interface Structure {
	
	public void add(double newValue, Integer dot);
	
	public void update(double oldValue, double newValue, Integer dot);
	
	
	public List<Integer> query(double min, double max);
	
}
