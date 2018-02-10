package test.hr.fer.zemris.structures.dot;

import static org.junit.Assert.*;
import hr.fer.zemris.structures.dot.Dot;

import org.junit.Test;

public class DotTest {
	
	@Test
	public void getValue()
	{
		Dot temp = new Dot(1,3);
		temp.setValue(0, 7);
		assertEquals((int)7,(int) temp.getValue(0));
	}
	@Test
	public void testEquals()
	{
		Dot d1 = new Dot(2,4);
		Dot d2 = new Dot(2,5);
		d1.setValue(0, 1);
		d2.setValue(0, 2);
		d1.setValue(1, 1);
		d2.setValue(1, 2);
		assertEquals(d1.equals(d2), false);
		d2.setValue(0,1);
		d2.setValue(1, 1);
		assertEquals(d1.equals(d2), false);
		Dot d3 = new Dot(2,4);
		assertEquals(d1.equals(d3),true);
	}
	
}
