package hr.fer.zemris.exceptions;

public class NumOfArguments extends Exception {
	
	private int numOfArguments;
	private int numOfDesiredArguments;
	public NumOfArguments(int numOfArguments, int numOfDesiredArguments)
	{
		super();
		this.numOfArguments = numOfArguments;
		this.numOfDesiredArguments = numOfDesiredArguments;
	}
	@Override
	public String getMessage()
	{
		return "You should pass "+numOfDesiredArguments+", but you pass "+numOfArguments;
	}
}
