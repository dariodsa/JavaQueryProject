package hr.fer.zemris.main;

import java.io.*;

import hr.fer.zemris.graphics.*;
import hr.fer.zemris.network.Network;
import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.binary.Node;
import hr.fer.zemris.structures.binary.NumberNode;
import hr.fer.zemris.thread.workers.MainWorker;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

	private static Window frame;
	
	public static void main(String[] args) 
	{
		int port = 4564;
		System.out.println((new NumberNode(10,5).equals(new NumberNode(30,5))));
		BinaryTree  B = new BinaryTree(0, 180);
		
		
		
		B.add(new NumberNode(40, 3));
		B.add(new NumberNode(20, 4));
		B.add(new NumberNode(10, 5));
		
		System.out.println(B.size());
		B.add(new NumberNode(30, 5));
		System.out.println(B.size());
		B.add(new NumberNode(50, 6));
		System.out.println(B.size());
		for(Node n: B) {
			System.out.println(n.getValue() + " "+n.getId());
		}
		runGUI();
		
		
	}
	private static void runGUI()
	{
		try {
			SwingUtilities.invokeAndWait(
					()->
					{
						frame = new Window(700,400);
						frame.initGUI();
					}
			);
		} catch (InvocationTargetException | InterruptedException e) {
			
			e.printStackTrace();
		}
	}

}
