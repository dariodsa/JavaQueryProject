package hr.fer.zemris.exceptions;

public class InvalidIpAddress extends Exception {
	
	private String ipAddress;
	public InvalidIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	@Override
	public String getMessage()
	{
		return "Your IpAddress ("+ipAddress+") is invalid.";
	}
}
