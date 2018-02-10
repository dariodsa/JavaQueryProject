package hr.fer.zemris.thread.workers;

import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.dot.Dot;

import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainWorker {
	
	private int port;
	private Parametars parametars;
	private int mainPort;
	public MainWorker(int port,int mainPort)
	{
		this.port = port;
		this.mainPort = mainPort;
	}
	public void run()
	{
		try{
			ServerSocket serverSocket = new ServerSocket(port);
			while(true)
			{
				Socket client = serverSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
				int id = ois.readInt();
				System.out.printf("I see %d%n", id);
				switch (id) {
				case 0:      // terminate thread and connection
					serverSocket.close();
					return;  
				case 1:      // send parametars
					parametars = (Parametars)ois.readObject();
					Network.sendResponse(client.getLocalAddress(), mainPort, 1); // parametars sent
					break;   
				case 2:      // init position of dots
					Dot initDot = (Dot)ois.readObject();
					break;
				case 3:      // please move  
					Worker workerMove = new Worker();
					Thread dretvaMove = new Thread(workerMove);
					dretvaMove.run();
					dretvaMove.join();
					
					break;
				case 4:      // please query 
					double min  = ois.readDouble();
					double max  = ois.readDouble();
					int idQuery = ois.readInt();
					
					break;
				case 5:
					break;
				default:
					break;
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
