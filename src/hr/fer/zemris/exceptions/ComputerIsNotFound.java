package hr.fer.zemris.exceptions;

public class ComputerIsNotFound extends Exception 
{
	private String name;
	public ComputerIsNotFound(String name)
	{
		this.name = name;
	}
	@Override
	public String getMessage()
	{
		return String.format("Computer (%s) was not found.", name);
	}
}
