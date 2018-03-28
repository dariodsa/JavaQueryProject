package hr.fer.zemris.thread.query;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.structures.binary.Node;
import hr.fer.zemris.structures.binary.NumberNode;
import hr.fer.zemris.structures.dot.Functions;
import hr.fer.zemris.thread.QueryThread;

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
