package hr.fer.zemris.structures;

import hr.fer.zemris.exceptions.DimmensionException;

import java.util.Iterator;
import java.util.List;


public interface Structure extends Iterable<Pair>{
	
	/*
	 * Adds the new element to the structure. The new element is specified with the double 
	 * value and the Integer id.
	 * @param double 
	 * @param Integer id of the Dot 
	 * @return void
	 */
	public void add(double newValue, Long dot);
	/*
	 * Replace the oldValue with the newValue in the structure.
	 * @param double oldValue
	 * @param double newValue
	 * @param Integer id
	 * @return void
	 * @throws DimmensionException
	 */
	public void update(double oldValue, double newValue, Long dot) throws DimmensionException;
	
	/*
	 * Returns the List<Long> of ids which are in the specified range.
	 * @param double min
	 * @param double max
	 * @return List<Long>
	 * @throws IllegialArgumentException
	 */
	public List<Long> query(double min, double max) throws IllegalArgumentException;
	
	public void delete(double value, Long dot);
}
