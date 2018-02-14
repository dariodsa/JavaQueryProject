package hr.fer.zemris.thread.workers;

import hr.fer.zemris.exceptions.DimmensionException;
import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.BucketStructure;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.Structure;
import hr.fer.zemris.structures.dot.Dot;
import hr.fer.zemris.structures.dot.DotCache;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.BiConsumer;

public class MainWorker {
	
	private int port;
	private Parametars parametars;
	private int mainPort;
	public int numOfComponents;
	
	Structure[] S;
	private HashMap<Long, Double>[] idInStructure;
	
	public MainWorker(int port,int mainPort)
	{
		this.port = port;
		this.mainPort = mainPort;
	}
	private void init()
	{
		this.S = new Structure[parametars.minValues.length];
		this.numOfComponents = parametars.minValues.length;
		this.idInStructure = new HashMap[numOfComponents];
		for(int i=0;i<numOfComponents; ++i)
		{
			idInStructure[i] = new HashMap<Long, Double>();
		}
		
		for(int i=0,len = parametars.minValues.length; i < len; ++i)
		{
			switch (parametars.structureType) {
			case 1:
				S[i] = new BucketStructure(parametars.minValues[i], parametars.maxValues[i], parametars.bucketSize);
				break;
			default:
				S[i] = new BinaryTree();
				break;
			}
		}
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
					System.out.println("Primio parametre");
					parametars = (Parametars)ois.readObject();
					init();
					System.out.println("Primio parametre");
					//Network.sendResponse(client.getLocalAddress(), mainPort, 1); // parametars sent
					ObjectOutputStream os = new ObjectOutputStream(client.getOutputStream());
					os.writeInt(1);
					os.close();
					//client.close();
					
					break;   
				case 2:      // init position of dots
					int num = ois.readInt();
					
					for(int i=0;i<num;++i)
					{
						long identi   = ois.readLong();
						int component = ois.readInt();
						double value  = ois.readDouble();
						
						S[component].add(value, identi);
						idInStructure[component].put(identi, value);
						
					}
					ObjectOutputStream os2 = new ObjectOutputStream(client.getOutputStream());
					os2.writeInt(1);
					os2.close();
					System.out.println("Primio ");
					break;
				case 3:      // please move  
					Thread dretvaMove = new Thread(new Runnable(){
						int k = 0;
						@Override
						public void run()
						{
							for(k=0;k<numOfComponents;++k)
							{
								idInStructure[k].forEach(new BiConsumer<Long, Double>() {
									@Override
									public void accept(Long id, Double oldValue) 
									{
										double value = move(oldValue,parametars.minMove[k],parametars.maxMove[k],parametars.minValues[k],parametars.maxValues[k]);
										try {
											S[k].update(oldValue, value, id);
										} catch (DimmensionException e) {
											e.printStackTrace();
										}
										idInStructure[k].replace(id, value);
									}
								});
							}
						}
					});
					dretvaMove.run();
					dretvaMove.join();
					ObjectOutputStream os3 = new ObjectOutputStream(client.getOutputStream());
					os3.writeInt(1);
					os3.close();
					break;
				case 4:      // please query 
					double min  = ois.readDouble();
					double max  = ois.readDouble();
					int component = ois.readInt();
					//int idQuery = ois.readInt();
					
					List<Long> answer = S[component].query(min, max);
					ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
					//oos.writeInt(idQuery);
					oos.writeInt(answer.size());
					for(Long val : answer){
						oos.writeLong(val);
					}
					oos.close();
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
	private double move(double oldValue, double minMove, double maxMove, double minValue, double maxValue)
	{
		double rand = minMove + (maxMove-minMove) * new Random().nextDouble();
		double diff = Math.abs(maxValue-minValue);
		double move = rand * diff / 2.0;
		move = System.currentTimeMillis()%2 == 0 ? -move : move; 
		double newValue = oldValue + move;
		newValue -= minValue;
	
		newValue = mod(newValue, diff) + minValue;
		
		return newValue;
	}
private double mod(double x,double y) {
		
		if(x < 0)
		{
			while(x<0)
			{
				x+=y;
			}
		}
		else if(x>0)
		{
			while(x>=y)
			{
				x-=y;
			}
		}
		return x;
	}
}
