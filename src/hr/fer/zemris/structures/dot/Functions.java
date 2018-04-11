package hr.fer.zemris.structures.dot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Functions 
{
	public static Map<Integer, Boolean> map = new HashMap<>(150000);
	public static List<Integer> intersection(List<Integer> list1, List<Integer> list2)
	{
		List<Integer> resultList = new ArrayList<>();
		for(int i=0, len=list1.size();i<len;++i)
			map.put(list1.get(i), true);
		
		for(int i=0, len=list2.size();i<len;++i) {
			if(map.containsKey(list2.get(i))) {
				resultList.add(list2.get(i).intValue());
			}
		}
		map.clear();
		list1.clear();
		list2.clear();
		
		return resultList;
	}
}
