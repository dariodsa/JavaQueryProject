package hr.fer.zemris.exceptions;

public class InvalidStructureType extends Exception {
	private int strcutureType;
	public InvalidStructureType(int structureType)
	{
		this.strcutureType = structureType;
	}
	@Override
	public String getMessage()
	{
		return "Structure type ("+strcutureType+") was not specified.";
	}
}
