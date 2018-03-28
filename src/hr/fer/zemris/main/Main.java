package hr.fer.zemris.main;

import java.io.*;

import hr.fer.zemris.graphics.*;
import hr.fer.zemris.network.Network;
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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

	private static Window frame;
	
	public static void main(String[] args) 
	{
		int port = 4564;
		
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
