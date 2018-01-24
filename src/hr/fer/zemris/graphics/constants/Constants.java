package hr.fer.zemris.graphics.constants;

import java.util.ArrayList;
import java.util.List;

public class Constants 
{
	private static List<StructureType> structureType;
	
	public static String[] structureName;
	
	public static void init()
	{
		structureType = new ArrayList<>();
		
		structureType.add(new StructureType("Bucket struktura",1));
		structureType.add(new StructureType("Binarno stablo",2));
		
		structureName = new String[structureType.size()];
		int iter = 0;
		for(StructureType S : structureType)
			structureName[iter++] = S.name;
	}
}
