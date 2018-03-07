package hr.fer.zemris.main;

import hr.fer.zemris.graphics.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

public class Main {

	private static Window frame;
	
	public static void main(String[] args) 
	{
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
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
}
