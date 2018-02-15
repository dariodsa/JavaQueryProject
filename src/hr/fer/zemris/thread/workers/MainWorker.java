package hr.fer.zemris.thread.workers;

import hr.fer.zemris.exceptions.DimmensionException;
import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.BucketStructure;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.Structure;
import hr.fer.zemris.structures.dot.Dot;
import hr.fer.zemris.structures.dot.DotCache;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.BiConsumer;

public class MainWorker {
	
	private String masterAddress;
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
			System.out.println(parametars.structureType);
			switch (parametars.structureType) {
			case 0:
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
				ObjectInputStream ois = new ObjectInputStream( new BufferedInputStream(client.getInputStream()));
				int id = ois.readInt();
				System.out.printf("I see %d%n", id);
				switch (id) {
				case 0:      // terminate thread and connection
					serverSocket.close();
					return;  
				case 1:      // send parametars
					System.out.println("Primio parametre");
					parametars = (Parametars)ois.readObject();
					masterAddress = ((InetSocketAddress)client.getRemoteSocketAddress()).getAddress().toString();
					init();
					//Network.sendResponse(client.getLocalAddress(), mainPort, 1); // parametars sent
					ObjectOutputStream os = new ObjectOutputStream(client.getOutputStream());
					os.writeInt(1);
					os.close();
					//client.close();
					
					break;   
				case 2:      // init position of dots
					int num = ois.readInt();
					System.out.println("Loading ... "+num);
					for(int i=0;i<num;++i)
					{
						long identi   = ois.readLong();
						int component = ois.read();
						double value  = ois.readDouble();
						//System.out.printf("%d %d %f%n", identi, component, value);
						S[component].add(value, identi);
						idInStructure[component].put(identi, value);
						//if(i%10000==0)System.out.println(i+"/"+num);
					}
					ObjectOutputStream os2 = new ObjectOutputStream(client.getOutputStream());
					os2.writeInt(1);
					os2.close();
					System.out.println("Primio ");
					System.out.println("Hash size: " + idInStructure[0].size() + "," +idInStructure[1].size());
					break;
				case 3:      // please move  
					for(int k=0;k<numOfComponents;++k)
					{
						Set<Long> set = idInStructure[k].keySet();
						
						for(Long idDot : set) 
						{
								double oldValue = idInStructure[k].get(idDot);
								double value = move(oldValue,parametars.minMove[k],parametars.maxMove[k],parametars.minValues[k],parametars.maxValues[k]);
								try {
									S[k].update(oldValue, value, idDot);
								} catch (DimmensionException e) {
									e.printStackTrace();
								}
								idInStructure[k].replace(idDot, value);
						}
						
					}
					
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
					HashMap<String, List<DotCache>> wrongPositionDots = new HashMap<>();
					for(int k=0;k<numOfComponents;++k)
					{
						Set<Long> set = idInStructure[k].keySet();
						for(Long idDot : set) 
						{
							double value = idInStructure[k].get(idDot);
							if(parametars.minValues[k] > value && parametars.maxValues[k] <= value)
							{
								Socket S = new Socket(masterAddress, mainPort);
								ObjectOutputStream oos2 = new ObjectOutputStream(S.getOutputStream());
								oos2.writeInt(4);
								oos2.writeInt(k);
								oos2.writeDouble(value);
								oos2.flush();
								ObjectInputStream ois2 = new ObjectInputStream(S.getInputStream());
								String ipAddress = ois2.readUTF();
								S.close();
								System.out.println("Radilica : "+ipAddress);
								if(!wrongPositionDots.containsKey(ipAddress))
								{
									wrongPositionDots.put(ipAddress, new ArrayList<>());
								}
								List<DotCache> list = wrongPositionDots.get(ipAddress);
								list.add(new DotCache(idDot,k,value));
								wrongPositionDots.replace(ipAddress, list);
							}
						}
					}
					ObjectOutputStream oos5 = new ObjectOutputStream(client.getOutputStream());
					oos5.write(1);
					oos5.flush();
					Set<String> set = wrongPositionDots.keySet();
					for(String address : set){
						Socket S = new Socket(address, port);
						ObjectOutputStream oos3 = new ObjectOutputStream(S.getOutputStream());
						oos3.writeInt(6);
						int dotsSize = wrongPositionDots.get(address).size();
						oos3.writeInt(dotsSize);
						for(DotCache D : wrongPositionDots.get(address)){
							
							oos3.writeLong(D.getId());
							oos3.writeInt(D.getComponent());
							oos3.writeDouble(D.getValue());
							this.S[D.getComponent()].delete(D.getValue(), D.getId());
							idInStructure[D.getComponent()].remove(D.getId());
						}
						oos3.flush();
						ObjectInputStream ois3 = new ObjectInputStream(S.getInputStream());
						int val = ois3.read();
						if(val == 1){}
						S.close();
					}
					break;
				case 6:
					ObjectInputStream ois4 = new ObjectInputStream(client.getInputStream());
					int size = ois4.readInt();
					for(int i=0;i<size;++i){
						long dotId = ois4.readLong();
						int compo  = ois4.readInt();
						double value = ois4.readDouble();
						S[compo].add(value, dotId);
						idInStructure[compo].put(dotId, value);
					}
					ObjectOutputStream oos4 = new ObjectOutputStream(client.getOutputStream());
					oos4.write(1);
					break;
				default:
					break;
				}
				client.close();
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
