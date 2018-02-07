package hr.fer.zemris.exceptions;

public class DotFileIsNotSet extends Exception {
	
	public DotFileIsNotSet()
	{}
	@Override
	public String getMessage()
	{
		return "You didn't upload file that contains dot's location.";
	}
}
