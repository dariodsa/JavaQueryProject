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

import java.awt.Robot;
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
		
		
	}
	private static List<DotCache> wrongDots;
	private static List<NumberNode> toRemoveNode = new ArrayList<>();
	private static List<Pair> toRemove = new ArrayList<>();
	private static List<Double> newValue = new ArrayList<>();
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
				case 5:      // terminate data
					if(binaryTree != null) {
						for(int i=0;i<binaryTree.length;++i) {
							binaryTree[i] = null;
						}
					}
					if(bucket != null) {
						for(int i=0;i<bucket.length;++i) {
							bucket[i] = null;
						}
					}
					ObjectOutputStream os5 = new ObjectOutputStream(client.getOutputStream());
					os5.write(1);
					os5.close();
					break;
				
				case 14:  //query
					
					double minValue = ois.readDouble();
					double maxValue = ois.readDouble();
					int componentValue = ois.read();
					System.out.printf("%f %f %d%n",minValue,maxValue,componentValue);
					SortedSet<Node> results = binaryTree[componentValue].query(minValue, maxValue);
					System.out.println("SIZE: "+results.size());
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
						
						for(Node _P : binaryTree[k])
						{
								if(_P instanceof NetworkNode) continue;	
								NumberNode P =(NumberNode)_P;
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
									toRemoveNode.add(P);
									MainWorker.newValue.add(newValue);
								}
						}
						for(int i=toRemoveNode.size() - 1; i >= 0; --i)
						{
							binaryTree[k].updateNumberNode(toRemoveNode.get(i),newValue.get(i));
						}
						toRemoveNode.clear();
						newValue.clear();
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
					System.out.println("RELOCATION: "+wrongDots.size());
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
					
					wrongDots.clear();
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
