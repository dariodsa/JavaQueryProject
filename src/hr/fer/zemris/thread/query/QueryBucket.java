package hr.fer.zemris.thread.query;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.structures.dot.*;
import hr.fer.zemris.structures.Parametars;
import hr.fer.zemris.thread.MasterMethod;
import hr.fer.zemris.thread.MyInteger;
import hr.fer.zemris.thread.QueryThread;

public class QueryBucket extends Query{
	
	private static List<MyInteger> result = new ArrayList<>(50000);
	
	public QueryBucket(Parametars parametars, String[] workersAddress, int port) {
		super(parametars,workersAddress, port);
	}

	@Override
	public List<MyInteger> performQuery(double min, double max) {
		System.out.println("I will perform query operation.");
		
		result.clear();
		long t1 = System.currentTimeMillis();
		for(int k=0;k<numOfComponents;++k)
		{
			List<Thread> threads = new ArrayList<Thread>();
			for(int j=0;j<workersAddress.length;++j)
			{
				Thread T = new QueryThread(j, workersAddress[j], port,1,min,max);
				threads.add(T);
				
			}
			for(Thread thread : threads)
				thread.start();
			for(Thread thread : threads)
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		
			long t3 = System.currentTimeMillis();
			System.out.printf("%d milisec for response.%n",t3-t1);
			System.out.println(workersAddress.length);
			for(int j=0;j<workersAddress.length;++j)
			{
				if(!MasterMethod.result[j].isEmpty())
				{
					
					if(k == 0)
					{
						for(MyInteger r : MasterMethod.result[j]){
							result.add(r);
						}
					}
					else
					{
						result = Functions.intersection(result, MasterMethod.result[j]);
						//result.retainAll(MasterMethod.result[j]);
					}
					for(MyInteger m : MasterMethod.result[j]) {
						MasterMethod.cache[j].push(m);
					}
					MasterMethod.result[j].clear();
					
				}
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.printf("Query operation completed. %d milisec. Size %d%n",t2-t1,result.size());
		return result;
	}
}
