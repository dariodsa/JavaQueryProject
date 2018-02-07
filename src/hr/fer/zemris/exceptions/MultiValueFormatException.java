package hr.fer.zemris.exceptions;

public class MultiValueFormatException extends Exception {
	private String message;
	public MultiValueFormatException(String message)
	{
		this.message = message;
	}
	@Override
	public String getMessage()
	{
		return message;
	}
}
