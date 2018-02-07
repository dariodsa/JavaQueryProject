package hr.fer.zemris.network;

import hr.fer.zemris.structures.dot.Dot;

import java.io.*;
import java.net.*;

public class Network {

	private static Socket echoSocket;
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
	public static void sendObject(InetAddress address, int port, Object dot) throws IOException
	{
		echoSocket = new Socket(address, port);
		ObjectOutputStream oos = new ObjectOutputStream(echoSocket.getOutputStream());
		oos.writeObject(dot);
		oos.close();
	}
	public static void socketTest(int port)
	{
		try{
		ServerSocket serverSocket = new ServerSocket(port);
		
		while(true)
		{
			System.out.println("Waiting for client request");
			Socket clientSocket = serverSocket.accept();
			DataInputStream ois = new DataInputStream(clientSocket.getInputStream());
			int message = (int)ois.readInt();
			ois.close();
			System.out.println("I see ... "+message);
			if(message==0) break;
		}
		System.out.println("Shutting down Socket server!!");
        serverSocket.close();
		}
		catch(Exception e){System.out.println(e.getMessage());}
	}
	
}
