package hr.fer.zemris.graphics.component;

import hr.fer.zemris.structures.dot.Dot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PPicture extends JPanel {

	private int height;
	private int width;
	
	private List<Dot> dots = new ArrayList<>();
	
	public PPicture(int width, int  height) 
	{
		setLayout(null);
		setVisible(true);
		repaint();
		add(new JButton("mirko"));
		setSize(width, height);
		this.width = width;
		this.height = height;
	}
	public void updatePicture(List<Dot> dots)
	{
		this.dots.clear();
		for(Dot D : dots)
		{
			this.dots.add(D.clone());
		}
		repaint();
	}
	public PPicture(LayoutManager layout) 
	{
		super(layout);
	}
	@Override
	public void paintComponent(Graphics g) {
		//g.clearRect(0, 0, width, height);
		super.paintComponent(g);
		g.setColor(Color.RED);
		System.out.println(dots.size() + " "+width+ " "+height);
		for(Dot D : dots)
        {
        	int y = getPixel(-90, 90, height, -D.getValue(0));
        	int x = getPixel(-180, 180, width, D.getValue(1));
        	
        	g.drawOval(x, y, 3, 3);
        	//System.out.printf("%d %d -> %.2f %.2f%n",x,y,D.getValue(0),D.getValue(1));
        	//g.drawLine(0, 0, 850, 850);
        }
		System.out.println("Draw finished.");
	}
	private int getPixel(double minValue,double maxValue, int size, double value)
	{
		double diff = Math.abs(maxValue-minValue);
		double part = diff/(double)size;
		//System.out.println(part+" "+diff);
		return (int)((value-minValue)/part);
	}
}
