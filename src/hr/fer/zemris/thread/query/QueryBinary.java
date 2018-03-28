package hr.fer.zemris.thread.query;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

	private BinaryTree binaryTree;

	public QueryBinary(Parametars parametars, String[] workersAddress, int port, BinaryTree binaryTree) {
		super(parametars, workersAddress, port);
		this.binaryTree = binaryTree;
	}

	@Override
	public List<Integer> performQuery(double min, double max) {

		List<Integer> result = new ArrayList<Integer>();
		long t1 = System.currentTimeMillis();

		for (int k = 0; k < numOfComponents; ++k) {
			
			
			List<Node> results = binaryTree.query(min, max);
			
			List<Integer> tempList = new ArrayList<>(5000);
			for(Node node : results) {
				if(node instanceof NumberNode) {
					if(k > 0) tempList.add(node.getId());
					else result.add(node.getId());
				} else {
					//send request to the network
					List<Integer> listFromNetwork = new ArrayList<>();
					NetworkNode networkNode =(NetworkNode)node;
					try {
						Socket S = new Socket(networkNode.getAddress(), port);
						ObjectOutputStream oos = new ObjectOutputStream(S.getOutputStream());
						oos.write(14);
						oos.writeDouble(min);
						oos.writeDouble(max);
						oos.write(k);
						oos.flush();
						ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
						
						int len = ois.readInt();
						for(int i = len-1; i>=0; --i)
						{
							listFromNetwork.add(ois.readInt());
						}
						S.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if(k == 0) {
						for(int id : listFromNetwork) 
							result.add(id);
					} else for(int id : listFromNetwork) 
						tempList.add(id);
				}
			}
			if(k > 0) {
				result = Functions.intersection(result, tempList);
			}
		}
		return result;
	}

}
