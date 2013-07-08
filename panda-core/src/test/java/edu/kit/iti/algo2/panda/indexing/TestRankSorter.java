package edu.kit.iti.algo2.panda.indexing;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;


public class TestRankSorter {
	
	public List<Integer> generateRandomList(int size) {
		Random rnd = new Random();
		List<Integer> intList = new ArrayList<>();
		for (int i=0; i < size; i++) {
			intList.add(rnd.nextInt(100));
		}
		return intList;
	}
	
	/**
	 * Test whether no exception are thrown.
	 */
	@Test
	public void testEmptyList() {
		List<Integer> actual = new ArrayList<>();
		RankSorter<Integer> sorter = new RankSorter<>(actual);
		sorter.partialSort(10);
	}

	/**
	 * Test whether a small list is sorted completely.
	 */
	@Test
	public void testSimple() {
		List<Integer> expected = generateRandomList(5);
		ArrayList<Integer> actual = new ArrayList<>(expected);
		Collections.sort(expected);
		RankSorter<Integer> sorter = new RankSorter<>(actual);
		sorter.partialSort(actual.size());
		assertEquals(expected, actual);
	}

	/**
	 * Test whether multiple sorts on different sublists works.
	 */
	@Test
	public void testMultiSort() {
		final int SIZE = 100;
		List<Integer> expected = generateRandomList(SIZE);
		ArrayList<Integer> actual = new ArrayList<>(expected);
		Collections.sort(expected);
		RankSorter<Integer> sorter = new RankSorter<>(actual);
		for (int i=9; i < SIZE; i += 10) {
			sorter.partialSort(i);
			assertEquals(expected.subList(0, i), actual.subList(0, i));
		}
	}

	/**
	 * Test whether sequential list sort works.
	 */
	@Test
	public void testIncrementalSort() {
		final int SIZE = 100;
		List<Integer> expected = generateRandomList(SIZE);
		ArrayList<Integer> actual = new ArrayList<>(expected);
		Collections.sort(expected);
		RankSorter<Integer> sorter = new RankSorter<>(actual);
		for (int i=9; i < SIZE; i += 10) {
			sorter.partialSort(i-9, i);
		}
		assertEquals(expected, actual);
	}
	
	/**
	 * Performance test to validate that list is only sorted partially.
	 */
	@Test
	@Ignore
	public void testPerformance() {
		final int SIZE = 10000000; long before = 0, timeTaken = 0;
		List<Integer> expected = generateRandomList(SIZE);
		ArrayList<Integer> actual = new ArrayList<>(expected);
		
		before = System.currentTimeMillis();
		Collections.sort(expected);
		timeTaken = System.currentTimeMillis() - before;
		System.out.println("Collections.sort: " + timeTaken);
		
		before = System.currentTimeMillis();
		RankSorter<Integer> sorter = new RankSorter<>(actual);
		sorter.partialSort(50);
		timeTaken = System.currentTimeMillis() - before;
		System.out.println("Partial sort (50): " + timeTaken);
		
		// Test whether something was tested
		assertEquals(expected.subList(0, 50), actual.subList(0, 50));
	}

}
