package hr.fer.zemris.exceptions;

public class NumOfDotArguments extends Exception {

	private int line;
	public NumOfDotArguments(int line) {
		super();
		this.line = line;
	}
	@Override
	public String getMessage()
	{
		return "Check your input file.%n There is a dot, who doesn't have specifed number of arguments. Check line : "+ line;
	}

}
