package hr.fer.zemris.thread.move;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.thread.MoveThread;

public class MoveBinary extends Move{

	public MoveBinary(String[] workersAddress, int port, int numOfComponents) {
		super(workersAddress, port, numOfComponents);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void move() {
		System.out.println("I will perform move operation.");
		List<Thread> threads = new ArrayList<Thread>();
		long t1 = System.currentTimeMillis();

		for (int j = 0, len = workersAddress.length; j < len; ++j) {
			Thread T = new MoveThread(workersAddress[j], port, 15);

			threads.add(T);
		}
		for (Thread thread : threads)
			thread.start();
		for (Thread thread : threads)
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		long t2 = System.currentTimeMillis();

		System.out.printf("Move operation completed. %d milisec%n", t2 - t1);
		
	}

	@Override
	public void relocate() {
		// TODO Auto-generated method stub
		
	}

}
