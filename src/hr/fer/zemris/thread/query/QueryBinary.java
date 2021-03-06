package hr.fer.zemris.thread.query;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.management.ListenerNotFoundException;

import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.binary.NetworkNode;
import hr.fer.zemris.structures.binary.Node;
import hr.fer.zemris.structures.binary.NumberNode;
import hr.fer.zemris.structures.dot.Functions;
import hr.fer.zemris.thread.MasterMethod;
import hr.fer.zemris.thread.MyInteger;
import hr.fer.zemris.thread.QueryThread;
import hr.fer.zemris.thread.workers.MainWorker;

public class QueryBinary extends Query {


	public QueryBinary(Parametars parametars, String[] workersAddress, int port) {
		super(parametars, workersAddress, port);
	}
	private static List<MyInteger> result = new ArrayList<>(50000);
	
	private static Stack<MyInteger> cache = new Stack<>();
	@Override
	public List<MyInteger> performQuery(double min, double max) {

		result.clear();
		
		for(int k = 0; k < numOfComponents; ++k) {
			try (Socket S=new Socket(workersAddress[workersAddress.length/2], port)){
				
			
				ObjectOutputStream oos = new ObjectOutputStream(S.getOutputStream());
				oos.write(14);
				oos.writeDouble(min);
				oos.writeDouble(max);
				oos.write(k);
				oos.flush();
				System.out.printf("%f %f %d iz query%n",min,max,k);
				ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
				
				
				List<MyInteger> list2 = new ArrayList<>();
				if(k == 0)
					while(true)
					{
						int val = ois.readInt();
						if(val == -1) break;
						if(cache.isEmpty())
							result.add(new MyInteger(val));
						else result.add(cache.pop().setValue(val));
					}
				else {
					while(true)
					{
						
						int val = ois.readInt();
						if(val == -1)break;
						//list2.add(new MyInteger(val));
						if(cache.isEmpty())
							list2.add(new MyInteger(val));
						else list2.add(cache.pop().setValue(val));
					}
					result = Functions.intersection(result, list2);
				}
				for(MyInteger m : list2)
					cache.push(m);
				list2.clear();
				ois.close();
				oos.close();
				S.close();
			} catch(Exception e) {e.printStackTrace();}
		}
		return result;
	}

}
