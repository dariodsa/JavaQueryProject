package hr.fer.zemris.thread.workers;

import hr.fer.zemris.exceptions.DimmensionException;
import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.BucketStructure;
import hr.fer.zemris.structures.Pair;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.Structure;
import hr.fer.zemris.structures.binary.NetworkNode;
import hr.fer.zemris.structures.binary.Node;
import hr.fer.zemris.structures.binary.NumberNode;
import hr.fer.zemris.structures.binary.Orientation;
import hr.fer.zemris.structures.dot.Dot;
import hr.fer.zemris.structures.dot.DotCache;
import hr.fer.zemris.structures.types.StructureType;
import hr.fer.zemris.thread.RelocateThread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.*;
import java.util.function.BiConsumer;

public class MainWorker {
	
	private String masterAddress;
	private int port;
	private Parametars parametars;
	private int mainPort;
	public int numOfComponents;
	
	private String leftWorker;
	private String rightWorker;
	
	BucketStructure[] bucket;
	BinaryTree[] binaryTree;
	
	private double[] minValues;
	private double[] maxValues;
	
	private String[] workers;
	private RelocateThread[] relocationThreads;
	private double[][] minValuesWorkers;
	private double[][] maxValuesWorkers;
	
	
	public MainWorker(int port,int mainPort)
	{
		this.port = port;
		this.mainPort = mainPort;
	}
	private void init(String leftIp, String rightIp)
	{
		
		this.numOfComponents = parametars.minValues.length;
		
		this.minValues = new double[numOfComponents];
		this.maxValues = new double[numOfComponents];
		for(int i = 0; i < numOfComponents; ++i) {
			this.minValues[i] = parametars.minValues[i];
			this.maxValues[i] = parametars.maxValues[i];
		}
		
		this.bucket = new BucketStructure[numOfComponents];
		this.binaryTree = new BinaryTree[numOfComponents];
		for(int i=0,len = parametars.minValues.length; i < len; ++i)
		{
			System.out.println(parametars.structureType);
			switch (parametars.structureType) {
			case BUCKET:
				bucket[i] = new BucketStructure(parametars.minValues[i], parametars.maxValues[i], parametars.bucketSize);
				binaryTree[i] = null;
				break;
			case BINARY_TREE:
				binaryTree[i] = new BinaryTree(parametars.minValues[i], parametars.maxValues[i]);
				bucket[i] = null;
				if(leftIp.length() > 0) {
					binaryTree[i].addNetworkNode(leftIp, minValues[i], Orientation.LEFT);
				} else if(rightIp.length() > 0) {
					binaryTree[i].addNetworkNode(rightIp, maxValues[i], Orientation.RIGHT);
				}
				break;
			}
		}
		
		wrongDots = new ArrayList<DotCache>();
		toRemoveValue = new ArrayList<Double>();
		toRemoveId = new ArrayList<Integer>();
		toAdd = new ArrayList<Double>();
		
		
	}
	private List<DotCache> wrongDots;
	private List<Double> toRemoveValue;
	private List<Integer> toRemoveId;
	private List<Double> toAdd;
	
	private List<Pair> toRemove = new ArrayList<>();
	
	public void run()
	{
		try{
			ServerSocket serverSocket = new ServerSocket(port);
			while(true)
			{
				Socket client = serverSocket.accept();
				ObjectInputStream ois = new ObjectInputStream( new BufferedInputStream(client.getInputStream()));
				int id = ois.read();
				System.err.printf("I see %d%n", id);
				switch (id) {
				case 0:      // terminate thread and connection
					serverSocket.close();
					return;  
				case 1:      // send parametars
					System.err.println("Primio parametre");
					parametars = (Parametars)ois.readObject();
					masterAddress = (client.getRemoteSocketAddress()).toString();
					
					this.leftWorker = (String)ois.readObject();
					this.rightWorker = (String)ois.readObject();
					
					int numOfWorker = ois.read();
					
					this.workers = new String[numOfWorker];
					this.minValuesWorkers = new double[numOfWorker][parametars.maxValues.length];
					this.maxValuesWorkers = new double[numOfWorker][parametars.maxValues.length];
					
					for(int i=0;i<numOfWorker;++i) {
						this.workers[i] = (String) ois.readObject();
						for(int j=0;j<minValuesWorkers[0].length;++j) {
							this.minValuesWorkers[i][j] = ois.readDouble();
							this.maxValuesWorkers[i][j] = ois.readDouble();
						}
					}
					this.relocationThreads = new RelocateThread[numOfWorker];
					for(int i = 0;i < numOfWorker; ++i) {
						relocationThreads[i] = new RelocateThread(this.workers[i], port, 17);
						relocationThreads[i].start();
					}
					
					init(leftWorker, rightWorker);
					
					ObjectOutputStream os = new ObjectOutputStream(client.getOutputStream());
					os.writeInt(1);
					os.close();
					break;   
				case 2:      // init position of dots
					int num2 = ois.readInt();
					System.err.println("Loading ... "+num2);
					for(int i=0;i<num2;++i)
					{
						int identi   = ois.readInt();
						int component = ois.read();
						double value  = ois.readDouble();
						
						if(parametars.structureType == StructureType.BUCKET)
							bucket[component].add(value, identi);
						if(parametars.structureType == StructureType.BINARY_TREE)
							binaryTree[component].addNumberNode(value, identi);
						
					}
					ObjectOutputStream os2 = new ObjectOutputStream(client.getOutputStream());
					os2.writeInt(1);
					os2.close();
					System.err.println("Primio početne pozicije.");
					
					break;
				case 3:      // please move  
					for(int k=0;k<numOfComponents;++k)
					{
						System.err.println("Move component => " + k);
						
						toRemove.clear();
						toAdd.clear();
						boolean oldState = false;
						boolean newState = false;
						boolean stateSet = false;
						
						for(int i=0;i<bucket[k].buckets.length; ++i)
						{
								for(int j=0;j<bucket[k].buckets[i].size();++j) 
								{
									Pair P = bucket[k].buckets[i].get(j);
									if(!stateSet) {
										oldState = P.state;
										newState = !oldState;
										stateSet = true;
									}
									if(P.state == newState)
										continue;
									double oldValue = P.value;
									int idDot = P.id;
									double newValue = move(oldValue,parametars.minMove[k],parametars.maxMove[k],parametars.minValues[k],parametars.maxValues[k]);
									if(newValue < bucket[k].minValue || newValue > bucket[k].maxValue)
									{
										wrongDots.add(new DotCache(idDot,k,oldValue));
									}
									else
									{
										//toRemove.add(P);
										//toAdd.add(newValue);
										bucket[k].update(P, newValue);
									}
								}
						}
						/*for(int i=toRemove.size() - 1; i >= 0; --i)
						{
							try {
								bucket[k].update(toRemove.get(i), toAdd.get(i));
							} catch (DimmensionException e) {
								e.printStackTrace();
							}
						}*/
					}
					ObjectOutputStream os3 = new ObjectOutputStream(client.getOutputStream());
					os3.write(1);
					os3.close();
					break;
				case 4:      // please query 
					double min  = ois.readDouble();
					double max  = ois.readDouble();
					int component = ois.read();
					
					List<Integer> answer = bucket[component].query(min, max);
					ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
					
					/*byte[] res = intToByte(answer);*/
					oos.writeInt(answer.size());
					for(int idDot : answer) {
						oos.writeInt(idDot);
					}
					
					oos.close();
					
					break;
					
				/*case 5:
					//HashMap<String, List<DotCache>> wrongPositionDots = new HashMap<>();
					System.out.println("wrong dots " + wrongDots.size());
				 	if(!wrongDots.isEmpty())
				 	{
				 		System.out.println("Usao, nisam zelio.");
				 		Socket S = new Socket(this.leftIp, port);
				 		ObjectOutputStream oos2 = new ObjectOutputStream(S.getOutputStream());
				 		oos2.write(6);
				 		oos2.writeInt(wrongDots.size());
				 		for(DotCache D : wrongDots) {
				 			oos2.writeInt(D.getId());
				 			oos2.write(D.getComponent());
				 			oos2.writeDouble(D.getValue());
				 		}
				 		oos2.flush();
				 		ObjectInputStream ois4 = new ObjectInputStream(S.getInputStream());
				 		ois4.read();
				 		S.close();
						wrongDots.clear();
					}
				 	ObjectOutputStream os6 = new ObjectOutputStream(client.getOutputStream());
					os6.write(1);
					System.out.println("Poslao jedan");
					os6.close();
					break;
				case 6:
					ObjectInputStream ois4 = new ObjectInputStream(client.getInputStream());
					int size = ois4.readInt();
					wrongDots.clear();
					for(int i=0;i<size;++i){
						int dotId = ois4.readInt();
						int compo  = ois4.read();
						double value = ois4.readDouble();
						if(S[compo].minValue >= value && S[compo].maxValue > value)
							S[compo].add(value, dotId);
						else
							wrongDots.add(new DotCache(dotId, compo, value));
					}
					ObjectOutputStream oos4 = new ObjectOutputStream(client.getOutputStream());
					oos4.write(1);
					Socket S1 = new Socket(this.leftIp, port);
					ObjectOutputStream oos2 = new ObjectOutputStream(S1.getOutputStream());
			 		oos2.write(6);
			 		oos2.writeInt(wrongDots.size());
			 		for(DotCache D : wrongDots) {
			 			oos2.writeInt(D.getId());
			 			oos2.write(D.getComponent());
			 			oos2.writeDouble(D.getValue());
			 		}
			 		oos2.flush();
			 		ObjectInputStream ois5 = new ObjectInputStream(S1.getInputStream());
			 		ois5.read();
					S1.close();
					break;
				case 7:
					
					for(int i=0;i<parametars.minValues.length;++i)
					{
						for(int j=0;j<parametars.bucketSize;++j)
							S[i].balance(j);
							
					}
					ObjectOutputStream os7 = new ObjectOutputStream(client.getOutputStream());
					os7.write(1);
					os7.close();
					break;
				case 11:
					ObjectInputStream ois8 = new ObjectInputStream(client.getInputStream());
					int compo = ois8.read();
					int leftOrRight = ois8.read();
					double newBound = ois8.readDouble();
					int len = ois8.readInt();
					List<Pair> list = new ArrayList<>();
					for(int i=0;i<len;++i)
					{
						int idDot = ois8.readInt();
						double val = ois8.readDouble();
						list.add(new Pair(idDot, val));
					}
					S[compo].acceptNewDots(list, newBound, leftOrRight);
					ObjectOutputStream os9 = new ObjectOutputStream(client.getOutputStream());
					os9.write(1);
					os9.close();
					break;*/
				/*case 12: drawing 
					ObjectOutputStream os10 = new ObjectOutputStream(client.getOutputStream());
					os10.writeInt(S[1].minValuesPerBucket.length);
					int br = 0;
					for(double x : S[1].minValuesPerBucket) {
						os10.writeDouble(x);
						System.out.println(S[1].buckets[br++].size()+ " " + preferredNum + " => " + x);
						
					}
					br = 0;
					os10.writeInt(S[0].minValuesPerBucket.length);
					for(double x : S[0].minValuesPerBucket) {
						os10.writeDouble(x);
						System.out.println(S[0].buckets[br++].size()+ " " + preferredNum + " => " + x);
					}
					//os10.write(1);
					System.out.println("done");
					os10.close();
					break;*/
				case 14:
					
					double minValue = ois.readDouble();
					double maxValue = ois.readDouble();
					int componentValue = ois.read();
					System.out.printf("%f %f %d%n",minValue,maxValue,componentValue);
					List<Node> results = binaryTree[componentValue].query(minValue, maxValue);
					System.out.println("SIZE: "+binaryTree[0].size());
					List<Integer> listInt = new ArrayList<>();
					for(Node node : results) {
						if(node instanceof NumberNode) {
							listInt.add(node.getId());
						} else {
							System.out.println("Send request to the network");
							//List<Integer> listFromNetwork = new ArrayList<>();
							NetworkNode networkNode =(NetworkNode)node;
							try {
								Socket S = new Socket(networkNode.getAddress(), port);
								ObjectOutputStream oos14 = new ObjectOutputStream(S.getOutputStream());
								oos14.write(14);
								oos14.writeDouble(minValue);
								oos14.writeDouble(maxValue);
								oos14.write(componentValue);
								oos14.flush();
								ObjectInputStream ois14a = new ObjectInputStream(S.getInputStream());
								
								int lenArray = ois14a.readInt();
								for(int i = lenArray-1; i>=0; --i)
								{
									listInt.add(ois14a.readInt());
								}
								S.close();
							} catch(Exception ex) {ex.printStackTrace();}
						}
					}
					ObjectOutputStream oos14 = new ObjectOutputStream(client.getOutputStream());
					oos14.writeInt(results.size());
					for(int res : listInt) {
						oos14.writeInt(res);
					}
					oos14.flush();
					oos14.close();
					break;
				case 15:
					//move BinaryTree
					wrongDots.clear();
					for(int k=0;k<numOfComponents;++k)
					{
						System.err.println("Move component => " + k);
						
						toRemoveId.clear();
						toRemoveValue.clear();
						toAdd.clear();
						for(Node P : binaryTree[k])
						{
								double oldValue = P.getValue();
								int idDot = P.getId();
								if(idDot < 0) continue;
								double newValue = move(oldValue,parametars.minMove[k],parametars.maxMove[k],parametars.minValues[k],parametars.maxValues[k]);
								if(newValue < binaryTree[k].minValue || newValue > binaryTree[k].maxValue)
								{
									wrongDots.add(new DotCache(idDot,k,oldValue));
								}
								else
								{
									toRemoveId.add(idDot);
									toRemoveValue.add(oldValue);
									toAdd.add(newValue);
								}
						}
						for(int i=toRemoveId.size() - 1; i >= 0; --i)
						{
							binaryTree[k].updateNumberNode(toRemoveValue.get(i), toAdd.get(i), toRemoveId.get(i));
						}
					}
					ObjectOutputStream os15 = new ObjectOutputStream(client.getOutputStream());
					os15.write(1);
					os15.close();
					break;
				case 16:
					//relocate Structure
					List<DotCache>[] listToMove = new ArrayList[workers.length];
					for(int i = 0; i < workers.length; ++i) {
						listToMove[i] = new ArrayList<>();
					}
					for(DotCache dot : wrongDots) {
						int workerId=getWorker(dot);
						listToMove[workerId].add(dot);
					}
					for(int i=0;i<workers.length; ++i) {
						if(!listToMove[i].isEmpty()) {
							relocationThreads[i].setList(wrongDots);
						}
					}
					for(int i=0;i<workers.length; ++i) {
						if(!listToMove[i].isEmpty()) {
							relocationThreads[i].notify();
						}
					}
					
					
					ObjectOutputStream os16 = new ObjectOutputStream(client.getOutputStream());
					os16.write(1);
					os16.close();
					break;
				case 17:
					//ObjectInputStream ois17 = new ObjectInputStream(client.getInputStream());
					int sizeOfList = ois.readInt();
					
					for(int i = sizeOfList -1 ; i >= 0; --i) {
						int dotComponent = ois.readInt();
						int dotId = ois.readInt();
						double dotValue = ois.readDouble();
						
						switch(parametars.structureType) {
							case BUCKET:
								bucket[dotComponent].add(dotValue, dotId);
								break;
							case BINARY_TREE:
								binaryTree[dotComponent].addNumberNode(dotValue, dotId);
								break;
							default:
								break;
						}
					}
					System.out.println("Accept new dots.");
					ObjectOutputStream os17 = new ObjectOutputStream(client.getOutputStream());
					os17.write(1);
					os17.close();
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
	private int getWorker(DotCache dot) {
		for(int i = 0, len = workers.length; i < len; ++i) {
			if(minValuesWorkers[i][dot.getComponent()] <= dot.getValue() &&
			   maxValuesWorkers[i][dot.getComponent()] >= dot.getValue()) {
				return i;
			}
		}
		return 0;
	}
	public static byte[] intToByte(List<Integer> input)
	{
	    ByteBuffer byteBuffer = ByteBuffer.allocate(input.size() * 4);        
	    IntBuffer intBuffer = byteBuffer.asIntBuffer();
	    int[] li = new int[input.size()];
	    int iter = 0;
	    for(int l: input){
	    	li[iter++] = l;
	    }
	    intBuffer.put(li);

	    byte[] array = byteBuffer.array();

	    return array;
	}
	public static int bytesToInt(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
	    
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getInt();
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
