package hr.fer.zemris.exceptions;

public class MinMaxValuesAreNotSet extends Exception {
	
	public MinMaxValuesAreNotSet(){}
	@Override
	public String getMessage()
	{
		return "You didn't set min and the max values.";
	}
}
