package hr.fer.zemris.thread.workers;

import hr.fer.zemris.exceptions.DimmensionException;
import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.BucketStructure;
import hr.fer.zemris.structures.Pair;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.Structure;
import hr.fer.zemris.structures.binary.Node;
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
	
	public int preferredNum;
	
	private String leftWorker;
	private String rightWorker;
	
	BucketStructure[] bucket;
	BinaryTree[] binaryTree;
	
	private double[] minValues;
	private double[] maxValues;
	
	public MainWorker(int port,int mainPort)
	{
		this.port = port;
		this.mainPort = mainPort;
	}
	private void init(int preferredNum, String leftIp, String rightIp)
	{
		
		this.numOfComponents = parametars.minValues.length;
		
		this.minValues = new double[numOfComponents];
		this.maxValues = new double[numOfComponents];
		for(int i = 0; i < numOfComponents; ++i) {
			this.minValues[i] = parametars.minValues[i];
			this.maxValues[i] = parametars.maxValues[i];
		}
		
		this.preferredNum = preferredNum;
		
		this.bucket = new BucketStructure[numOfComponents];
		this.binaryTree = new BinaryTree[numOfComponents];
		for(int i=0,len = parametars.minValues.length; i < len; ++i)
		{
			System.out.println(parametars.structureType);
			switch (parametars.structureType) {
			case BUCKET:
				bucket[i] = new BucketStructure(parametars.minValues[i], parametars.maxValues[i], parametars.bucketSize, 
						preferredNum, leftIp, rightIp, port, i);
			case BINARY_TREE:
				binaryTree[i] = new BinaryTree(parametars.minValues[i], parametars.maxValues[i]);
				if(leftIp.length() > 0) {
					binaryTree[i].addNetworkNode(leftIp, minValues[i], Orientation.LEFT);
				} else if(rightIp.length() > 0) {
					binaryTree[i].addNetworkNode(rightIp, maxValues[i], Orientation.RIGHT);
				}
				break;
			/*default:
				S[i] = new BinaryTree();
				break;*/
			}
		}
		
		wrongDots = new ArrayList<DotCache>();
		toRemoveValue = new ArrayList[parametars.maxValues.length];
		toRemoveId = new ArrayList[parametars.maxValues.length];
		toAdd = new ArrayList[parametars.maxValues.length];
		for(int k = parametars.maxValues.length - 1; k >= 0; --k) {
			
			toRemoveValue[k] = new ArrayList<Double>();
			toRemoveId[k] = new ArrayList<Integer>();
			toAdd[k] = new ArrayList<Double>();
		}
		
	}
	private List<DotCache> wrongDots;
	private List<Double>[] toRemoveValue;
	private List<Integer>[] toRemoveId;
	private List<Double>[] toAdd;
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
					
					int num = ois.readInt();
					init(num, leftWorker, rightWorker);
					
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
						
						toRemoveId[k].clear();
						toRemoveValue[k].clear();
						toAdd[k].clear();
						for(Pair P : bucket[k])
						{
								double oldValue = P.value;
								int idDot = P.id;
								double newValue = move(oldValue,parametars.minMove[k],parametars.maxMove[k],parametars.minValues[k],parametars.maxValues[k]);
								if(newValue < bucket[k].minValue || newValue > bucket[k].maxValue)
								{
									wrongDots.add(new DotCache(idDot,k,oldValue));
								}
								else
								{
									toRemoveId[k].add(idDot);
									toRemoveValue[k].add(oldValue);
									toAdd[k].add(newValue);
								}
						}
						for(int i=toRemoveId[k].size() - 1; i >= 0; --i)
						{
							try {
								bucket[k].update(toRemoveValue[k].get(i), toAdd[k].get(i), toRemoveId[k].get(i));
							} catch (DimmensionException e) {
								e.printStackTrace();
							}
						}
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
					s
					oos.close();
					
					break;
					
				case 5:
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
					break;
				case 12:
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
					break;
				case 15:
					//move BinaryTree
					wrongDots.clear();
					for(int k=0;k<numOfComponents;++k)
					{
						System.err.println("Move component => " + k);
						
						toRemoveId[k].clear();
						toRemoveValue[k].clear();
						toAdd[k].clear();
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
									toRemoveId[k].add(idDot);
									toRemoveValue[k].add(oldValue);
									toAdd[k].add(newValue);
								}
						}
						for(int i=toRemoveId[k].size() - 1; i >= 0; --i)
						{
							try {
								bucket[k].update(toRemoveValue[k].get(i), toAdd[k].get(i), toRemoveId[k].get(i));
							} catch (DimmensionException e) {
								e.printStackTrace();
							}
						}
					}
					ObjectOutputStream os15 = new ObjectOutputStream(client.getOutputStream());
					os15.write(1);
					os15.close();
					break;
				case 16:
					//relocate BinaryTree
					List<DotCache> toLeft = new ArrayList<>();
					List<DotCache> toRight = new ArrayList<>();
					for(DotCache dot : wrongDots) {
							if(dot.getValue() < minValues[dot.getComponent()]) {
								toLeft.add(dot);
							} else if(dot.getValue() > maxValues[dot.getComponent()]) {
								toRight.add(dot);
							}
					}
					Thread threadLeft;
					Thread threadRight;
					if(toLeft.size() > 0) {
						threadLeft = new RelocateThread(leftWorker, port, 17, toLeft);
					}
					if(toRight.size() > 0) {
						threadRight = new RelocateThread(rightWorker, port, 17, toRight);
					}
					
					if(threadLeft != null) {
						threadLeft.start();
						threadLeft.join();
					}
					if(threadRight != null) {
						threadRight.start();
						threadRight.join();
					}
					ObjectOutputStream os16 = new ObjectOutputStream(client.getOutputStream());
					os16.write(1);
					os16.close();
					break;
				case 17:
					ObjectInputStream ois17 = new ObjectInputStream(client.getInputStream());
					int sizeOfList = ois17.readInt();
					List<DotCache> toLeft2 = new ArrayList<>();
					List<DotCache> toRight2 = new ArrayList<>();
					for(int i = sizeOfList -1 ; i >= 0; --i) {
						int dotComponent = ois17.readInt();
						int dotId = ois17.readInt();
						double dotValue = ois17.readDouble();
						if(minValues[dotComponent] <= dotValue && dotValue < maxValues[dotComponent]) {
							binaryTree[dotComponent].addNumberNode(dotValue, dotId);
						} else if(minValues[dotComponent] > dotValue) {
							toLeft2.add(new DotCache(dotId,dotComponent,dotValue));
						} else {
							toRight2.add(new DotCache(dotId,dotComponent,dotValue));
						}
					}
					Thread threadLeft2;
					Thread threadRight2;
					if(toLeft2.size() > 0) {
						threadLeft2 = new RelocateThread(leftWorker, port, 17, toLeft2);
					}
					if(toRight2.size() > 0) {
						threadRight2 = new RelocateThread(rightWorker, port, 17, toRight2);
					}
					if(threadLeft2 != null) {
						threadLeft2.start();
						threadLeft2.join();
					}
					if(threadRight2 != null) {
						threadRight2.start();
						threadRight2.join();
					}
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
