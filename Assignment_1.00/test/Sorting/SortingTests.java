package Sorting;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SortingTests {

	long numbers[] = {4,3,8,9,7,2,1,5};
	long sortedNumbers[] = {1,2,3,4,5,7,8,9};
	
	long testNumbers[];
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Code in here executes ONCE before any tests in this class are run
		
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//Code in here executes ONCE after ALL tests in this class have run
		
	}
	
	@Before
	public void setUp() throws Exception {
		//Code in here executes before EACH test in this class
		
		// Copy our test numbers into a 2nd array so the
		// numbers array doesn't get clobbered during the tests
		testNumbers = new long[8];
		System.arraycopy(numbers,0,testNumbers,0, 8);
	}

	@After
	public void tearDown() throws Exception {
		//Code in here executes after EACH test in this class
	}
	
	
	@Test
	public void testSwap() {
			SortingApp.swap(testNumbers, 0, 1);
			assertEquals(testNumbers[0],3);
			assertEquals(testNumbers[1],4);
	}
	
	@Test
	public void testMerge() {
		long a[] = {1,3,5,7,2,4,8,9};
		long workSpace[] = new long[a.length];
		SortingApp.merge(a, workSpace, 0, 4, 7);
		assertArrayEquals(a,sortedNumbers);
	}
	
	@Test
	public void testPartition() {
		int storedIndex = SortingApp.partition(testNumbers, 0, 7, 4);
		long[] expectedNumbers = {4, 3, 5, 2, 1, 7, 8, 9};
		assertArrayEquals(testNumbers, expectedNumbers);
		assertEquals(storedIndex , 5);
	}
	
	@Test
	public void testNonrecMergeSort() {
		SortingApp.nonrecMergeSort(testNumbers);
		assertArrayEquals(testNumbers, sortedNumbers);
	}
	
	@Test
	public void testMergeSort() {
		SortingApp.recMergeSort(testNumbers);
		assertArrayEquals(testNumbers, sortedNumbers);
	}
	
	@Test
	public void testQuickSort() {
		SortingApp.quicksort(testNumbers);
		assertArrayEquals(testNumbers, sortedNumbers);
	}
	
	@Test
	public void testBubbleSort() {	
		SortingApp.bubbleSort(testNumbers);
		assertArrayEquals(testNumbers, sortedNumbers);
	}	
}
