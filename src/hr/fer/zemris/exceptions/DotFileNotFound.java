package hr.fer.zemris.exceptions;

public class DotFileNotFound extends Exception {

	private String message;
	public DotFileNotFound(String message) {
		this.message = message;
	}
	@Override
	public String getMessage()
	{
		return String.format("File (%s) was not found.", this.message);
	}
}
