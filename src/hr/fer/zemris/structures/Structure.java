package hr.fer.zemris.structures;

import java.util.List;


public interface Structure {
	
	public void addOrUpdate(double oldValue, double newValue, Integer dot);
	
	
	public List<Integer> query(double min, double max);
	
}
