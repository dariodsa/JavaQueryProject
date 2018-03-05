package hr.fer.zemris.graphics.component.statistics;

import javax.swing.*;


import java.awt.BorderLayout;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;import java.util.stream.Stream;

import java.util.stream.Collectors;

public class StatisticsPanel extends JPanel 
{
	private JButton loadButton;
	public StatisticsPanel()
	{
		setLayout(new BorderLayout());
	}
	public void initGui()
	{
		loadButton = new JButton("Load file");
		loadButton.addActionListener((e)->{
			JFileChooser fc = new JFileChooser(".");
			int value = fc.showOpenDialog(this);
			if(value == JFileChooser.APPROVE_OPTION){
				Path path = Paths.get(fc.getSelectedFile().getPath());
				JOptionPane.showMessageDialog(this, "File loaded.");
				
				try {
					HashMap<Integer, List<Integer>> queryList = new HashMap<Integer, List<Integer>>();
					HashMap<Integer, List<Integer>> moveList  = new HashMap<Integer, List<Integer>>();
					HashMap<Integer, List<Integer>> relocList  = new HashMap<Integer, List<Integer>>();
					List<String> lines = Files.readAllLines(path);
					int bucket = 0;
					for(String line : lines)
					{
						if(line.startsWith("Bucket size:  "))
						{
							bucket = Integer.parseInt(line.substring(new String("Bucket size:  ").length()));
						}
						if(line.startsWith("Query operation completed. "))
						{
							String S = "";
							for(int i = new String("Query operation completed. ").length() ; i < line.length(); ++i)
							{
								if((line.charAt(i) >='0' && line.charAt(i) <= '9'))
								{
									S += line.charAt(i);
								}
								else
									break;
							}
							int broj = Integer.parseInt(S);
							if(!queryList.containsKey(bucket))
								queryList.put(bucket, new ArrayList<Integer>());
							queryList.get(bucket).add(broj);
						}
						if(line.startsWith("Move operation completed. "))
						{
							String S = "";
							for(int i = new String("Move operation completed. ").length() ; i < line.length(); ++i)
							{
								if((line.charAt(i) >='0' && line.charAt(i) <= '9'))
								{
									S += line.charAt(i);
								}
								else
									break;
							}
							int broj = Integer.parseInt(S);
							if(!moveList.containsKey(bucket))
								moveList.put(bucket, new ArrayList<Integer>());
							moveList.get(bucket).add(broj);
						}
						if(line.startsWith("Realocation completed. ")){
							String S = "";
							for(int i = new String("Realocation completed. ").length() ; i < line.length(); ++i)
							{
								if((line.charAt(i) >='0' && line.charAt(i) <= '9'))
								{
									S += line.charAt(i);
								}
								else
									break;
							}
							int broj = Integer.parseInt(S);
							if(!relocList.containsKey(bucket))
								relocList.put(bucket, new ArrayList<Integer>());
							relocList.get(bucket).add(broj);
						}
					}
					System.out.println("Query results: ");
					for(Integer bucketNum : queryList.keySet())
					{
						List<Integer> list = queryList.get(bucketNum);
						System.out.printf("%d -> ",bucketNum);
						
						list.stream().sorted().skip(1).sorted(Comparator.reverseOrder()).skip(1).collect(Collectors.toList());
						list.stream().mapToInt(t -> t).average().ifPresent(avg -> System.out.println("Avg: " + avg));
					}
					System.out.println("Move results: ");
					for(Integer bucketNum : moveList.keySet())
					{
						List<Integer> list = moveList.get(bucketNum);
						System.out.printf("%d -> ",bucketNum);
						/*for(Integer rez : list){
							System.out.printf("%d ",rez);
						}*/
						list = list.stream().sorted().skip(2).sorted(Comparator.reverseOrder()).skip(2).collect(Collectors.toList());
						list.stream().mapToInt(t -> t).average().ifPresent(avg -> System.out.println("Avg: " + avg));
						
					}
					System.out.println("Relocation results: ");
					for(Integer bucketNum : relocList.keySet())
					{
						List<Integer> list = relocList.get(bucketNum);
						System.out.printf("%d -> ",bucketNum);
						
						list.stream().sorted().skip(2).sorted(Comparator.reverseOrder()).skip(2).collect(Collectors.toList());
						list.stream().mapToInt(t -> t).average().ifPresent(avg -> System.out.println("Avg: " + avg));
					}
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		add(loadButton, BorderLayout.NORTH);
	}
}
