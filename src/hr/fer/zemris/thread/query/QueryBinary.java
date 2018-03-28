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

import javax.management.ListenerNotFoundException;

import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.binary.NetworkNode;
import hr.fer.zemris.structures.binary.Node;
import hr.fer.zemris.structures.binary.NumberNode;
import hr.fer.zemris.structures.dot.Functions;
import hr.fer.zemris.thread.MasterMethod;
import hr.fer.zemris.thread.QueryThread;
import hr.fer.zemris.thread.workers.MainWorker;

public class QueryBinary extends Query {


	public QueryBinary(Parametars parametars, String[] workersAddress, int port) {
		super(parametars, workersAddress, port);
	}

	@Override
	public List<Integer> performQuery(double min, double max) {

		List<Integer> result = new ArrayList<Integer>();
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
				
				int len = ois.readInt();
				List<Integer> list2 = new ArrayList<>();
				if(k == 0)
					for(int i = len-1; i>=0; --i)
					{
						result.add(ois.readInt());
					}
				else 
					for(int i = len-1; i>=0; --i)
					{
						list2.add(ois.readInt());
					}
				result = Functions.intersection(result, list2);
				S.close();
			} catch(Exception e) {e.printStackTrace();}
		}
		return result;
	}

}
