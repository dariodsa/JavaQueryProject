package hr.fer.zemris.structures;

import hr.fer.zemris.exceptions.DimmensionException;

import java.util.List;


public interface Structure {
	
	/*
	 * Adds the new element to the structure. The new element is specified with the double 
	 * value and the Integer id.
	 * @param double 
	 * @param Integer id of the Dot 
	 * @return void
	 */
	public void add(double newValue, Integer dot);
	/*
	 * Replace the oldValue with the newValue in the structure.
	 * @param double oldValue
	 * @param double newValue
	 * @param Integer id
	 * @return void
	 * @throws DimmensionException
	 */
	public void update(double oldValue, double newValue, Integer dot) throws DimmensionException;
	
	/*
	 * Returns the List<Integer> of ids which are in the specified range.
	 * @param double min
	 * @param double max
	 * @return List<Integer>
	 * @throws IllegialArgumentException
	 */
	public List<Integer> query(double min, double max) throws IllegalArgumentException;
	
}
