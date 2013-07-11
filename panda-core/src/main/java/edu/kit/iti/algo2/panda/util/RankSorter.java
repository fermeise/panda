package edu.kit.iti.algo2.panda.util;

import java.util.Collections;
import java.util.List;

/**
 * Class to quickly sort a list partially.
 */
public class RankSorter<E extends Comparable<? super E>> {
	
	protected final List<E> list;
	
	protected RankSorter(List<E> list) {
		this.list = list;
	}
	
	/**
	 * Sorts {@link RankSorter#list} in-place sorting at most
	 * <code>rank</code> elements.
	 * 
	 * @param fromIndex begin of range (inclusive)
	 * @param toIndex end of range (inclusive)
	 * @param rank number of elements to sort
	 */
	protected void partialSort(int fromIndex, int toIndex, int rank) {
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
	protected void partialSort(int fromIndex, int rank) {
		partialSort(fromIndex, list.size()-1, rank);
	}
	
	/**
	 * Sorts {@link RankSorter#list} in-place so that the first <code>rank</code>
	 * elements in the list are the smallest ones in ascending order.
	 * 
	 * @see RankSorter#partialSort(int, int, int)
	 * @param rank number of elements to sort
	 */
	protected void partialSort(int rank) {
		partialSort(0, list.size()-1, rank);
	}
	
	/**
	 * Sorts <code>list</code> in-place so that the first <code>rank</code>
	 * elements in the list are the smallest ones in ascending order.
	 * 
	 * @see RankSorter#partialSort(int)
	 * @param rank number of elements to sort
	 */
    public static <T extends Comparable<? super T>> void partialSort(List<T> list, int rank) {
    	RankSorter<T> sorter = new RankSorter<>(list);
    	sorter.partialSort(rank);
    }
}
