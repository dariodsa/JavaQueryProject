package hr.fer.zemris.network;

import java.io.IOException;
import java.net.*;

public class Network {

	public static boolean checkIsReachable(String ipAdress) throws IOException
	{
		InetAddress inet = InetAddress.getByName(ipAdress);
		boolean reachable = inet.isReachable(500); 
		return reachable;
	}
	public static String[] getAllAdressInLocalNetwork()
	{
		for(int i=0;i<256;++i)
		{
			String subnet = "192.168.1";
			String adresa = subnet + "." + new Integer(i).toString();
			try {
				if(InetAddress.getByName(adresa).isReachable(60))
				{
					System.out.println("Found: "+adresa + " " + InetAddress.getByName(adresa).getCanonicalHostName());
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
