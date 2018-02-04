package hr.fer.zemris.exceptions;

public class DuplicateComputerName extends Exception {
	private String name;
	public DuplicateComputerName(String name){
		this.name = name;
	}
	@Override
	public String getMessage()
	{
		return String.format("Your name is already in use(%s).%nUser another one.",name); 
	}
}
