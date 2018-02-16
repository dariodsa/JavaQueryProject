package test.hr.fer.zemris.structures.dot;

import hr.fer.zemris.structures.BinaryTree;
import hr.fer.zemris.structures.BucketStructure;
import hr.fer.zemris.structures.Structure;
import hr.fer.zemris.structures.dot.Dot;
import hr.fer.zemris.structures.dot.Functions;

import java.util.*;

import org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;

public class FunctionsTest {
	@Test 
	public void structureTest()
	{
		Structure S1 =  new BucketStructure(0, 200, 1000);
		Structure S2 =  new BinaryTree();
		for(long i=0;i<5000;++i){
			double rand = new Random().nextDouble();
			S1.add(rand, i);
			S2.add(rand, i);
		}
		
	}
}
