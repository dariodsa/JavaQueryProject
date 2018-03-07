package hr.fer.zemris.network;

import hr.fer.zemris.structures.dot.Dot;
import hr.fer.zemris.thread.workers.MainWorker;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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
	public static void sendResponse(InetAddress address, int port, int responseId) throws IOException
	{
		//System.out.println(address.getHostAddress().toString()+" "+port);
		Socket echoSocket = new Socket(address, port);
		ObjectOutputStream os = new ObjectOutputStream(echoSocket.getOutputStream());
		os.writeInt(responseId);
		os.close();
		echoSocket.close();
	}
	public static void socketTest(int port)
	{
		try{
		ServerSocket serverSocket = new ServerSocket(port);
		Socket clientSocket = serverSocket.accept();
		System.out.println("Waiting for client request");
		BufferedInputStream ois = new BufferedInputStream(clientSocket.getInputStream());
		List<Byte> b = new ArrayList<>();
		while(true)
		{
			byte B = (byte) ois.read();
			if(B != -1)
				b.add(B);
			else break;
		}
		/*for(int i=0;i<b.size()/8;++i){
			long ans = MainWorker.bytesToLong(b.subList(i*8, (i+1)*8));
			System.out.println("I see "+ans);
		}*/
		ois.close();
		System.out.println("Shutting down Socket server!!");
        serverSocket.close();
		}
		catch(Exception e){System.out.println(e.getMessage());}
	}
	
}
