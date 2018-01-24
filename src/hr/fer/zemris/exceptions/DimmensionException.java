package hr.fer.zemris.exceptions;

public class DimmensionException extends Exception {
	private double value;
	private double minValue;
	private double maxValue;
	public DimmensionException(double value, double minValue, double maxValue)
	{
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	@Override
	public String getMessage()
	{
		return String.format("Your value %f was not in the correct bound. %nMin bound: %f%nMax bound: %f", value,minValue,maxValue);
	}
}
