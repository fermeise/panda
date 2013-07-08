package edu.kit.iti.algo2.panda.indexing;

import java.util.Collections;
import java.util.List;

/**
 * Class to quickly sort list partially.
 */
public class RankSorter<E extends Comparable<E>> {
	
	private final List<E> list;
	
	public RankSorter(List<E> list) {
		this.list = list;
	}
	/**
	 * Sorts {@link RankSorter#list} in-place sorting at most
	 * <code>rank</code> numbers of elements.
	 * 
	 * @param fromIndex begin of range (inclusive)
	 * @param toIndex end of range (inclusive)
	 * @param rank number of elements to sort
	 */
	public void partialSort(int fromIndex, int toIndex, int rank) {
		if (fromIndex > toIndex) return;
	    E pivot = list.get((fromIndex + toIndex) / 2);
		int i = fromIndex, j = toIndex;
		while (i <= j) {
			while (pivot.compareTo(list.get(i)) > 0) i++;
			while (pivot.compareTo(list.get(j)) < 0) j--;
			if (i <= j) {
				Collections.swap(list, i, j);
				i++; j--;
			}
		}
		if (fromIndex < j) {
			partialSort(fromIndex, j, rank);
		}
		if (i < toIndex && fromIndex + rank >= i) {
			partialSort(i, toIndex, rank + fromIndex - i);
		}
	}

	/**
	 * Sorts {@link RankSorter#list} in-place sorting at most
	 * <code>rank</code> numbers of elements.
	 * 
	 * @see RankSorter#partialSort(int, int, int)
	 * @param fromIndex begin of range (inclusive)
	 * @param rank number of elements to sort
	 */
	public void partialSort(int fromIndex, int rank) {
		partialSort(fromIndex, list.size()-1, rank);
		
	}

	/**
	 * Sorts {@link RankSorter#list} in-place sorting at most
	 * <code>rank</code> numbers of elements.
	 * 
	 * @see RankSorter#partialSort(int, int, int)
	 * @param rank number of elements to sort
	 */
	public void partialSort(int rank) {
		partialSort(0, list.size()-1, rank);
	}
}
