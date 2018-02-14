package hr.fer.zemris.structures.dot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Functions 
{
	public static List<Long> intersection(List<Long> list1, List<Long> list2)
	{
		List<Long> resultList = new ArrayList<>();
		Map<Long, Boolean> map = new HashMap<Long,Boolean>();
		for(int i=0, len=list1.size();i<len;++i)
			map.put(list1.get(i), true);
		
		for(int i=0, len=list2.size();i<len;++i)
			if(map.containsKey(list2.get(i)))
				resultList.add(list2.get(i));
		
		return resultList;
	}
}
