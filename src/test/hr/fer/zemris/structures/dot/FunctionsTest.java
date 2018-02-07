package test.hr.fer.zemris.structures.dot;

import hr.fer.zemris.structures.dot.Dot;
import hr.fer.zemris.structures.dot.Functions;

import java.util.*;

import org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;

public class FunctionsTest {
	@Test 
	public void intersectionTest1()
	{
		List<Integer> list1 = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		
		list1.add(1);list1.add(2);list1.add(3);
		list2.add(3);list2.add(4);
		List<Integer> list3 = Functions.intersection(list1, list2);
		Assert.assertEquals(1, list3.size());
		Assert.assertEquals(3, (int)list3.get(0));
		
	}
	@Test
	public void intersectionTest2()
	{
		List<Integer> list1 = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		
		list1.add(3);list1.add(5);list1.add(6);list1.add(7);
		list2.add(5);list2.add(6);list2.add(7);
		List<Integer> list3 = Functions.intersection(list1, list2);
		Assert.assertEquals(3, list3.size());
		list2.add(3);
		List<Integer> list4 = Functions.intersection(list1, list2);
		Assert.assertEquals(4, list4.size());
	}
}
