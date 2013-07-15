package edu.kit.iti.algo2.panda.indexing.kgrams;

import java.util.Arrays;

/**
 * Container for functions to operate on Levenshtein distance.
 */
public class LevenshteinDistance {
	
	/**
	 * Computes the edit distance between <code>first</code> and <code>second</code>
	 * using weight 1 for replace, add and remove.
	 * 
	 * <p>Running time: O(|first| * |second|)</p>
	 */
	public static int distance(char[] first, char[] second) {
		if (Arrays.equals(first, second)) return 0;
		if (first.length == 0) return second.length;
		if (second.length == 0) return first.length;
		
		int[] prevRow = new int[second.length + 1];
		int[] currentRow = new int[second.length + 1];
		
		// Fill the first row with edit distance of replacing all letters
		for (int row=0; row < prevRow.length; row++) {
			prevRow[row] = row;
		}
		
		// Compute the other rows until all letters of first are used.
		for (int row=0; row < first.length; row++) {
			currentRow[0] = row + 1;
			
			for (int col=0; col < second.length; col++) {
				if (first[row] == second[col]) {
					currentRow[col+1] = prevRow[col];
				} else {
					currentRow[col+1] = Math.min(Math.min(currentRow[col], prevRow[col + 1]), prevRow[col]) + 1;
				}
				prevRow[col] = currentRow[col];
			}
			prevRow[second.length] = currentRow[second.length];
			
		}
		return currentRow[second.length];
	}
	
	/**
	 * Partially computes the Levenshtein distance of
	 * <code>first</code> and <code>second</code>. Returns true 
	 * if the computed distance <= <code>maxDistance</code>.
	 * 
	 * <p>Running time: O(maxDistance * l) with l = min(|first|, |second|)</p>
	 */
	public static boolean smallerOrEqualThan(char[] first, char[] second, int maxDistance) {
		if (maxDistance < 0) return false;
		if (maxDistance == 0) return Arrays.equals(first, second);
		if (Arrays.equals(first, second)) return true;
		if (first.length == 0) return second.length <= maxDistance;
		if (second.length == 0) return first.length <= maxDistance;
		
		// Swap the smaller string to save some memory.
		if (second.length > first.length) {
			char[] temp = second;
			second = first;
			first = temp;
		}
		
		int[] prevRow = new int[second.length + 1];
		int[] currentRow = new int[second.length + 1];
		
		// Fill the first row with consecutive replace operation
		for (int col=0; col < Math.min(maxDistance + 2, prevRow.length); col++) {
			prevRow[col] = col;
		}
		
		// Compute the other rows until all letters of first are used.
		for (int row=0; row < first.length; row++) {
			currentRow[0] = row + 1;
			int lower = Math.max(0, row - maxDistance - 1);
			int upper = Math.min(second.length, row + maxDistance + 1);
			for (int col=lower; col < upper; col++) {
				if (first[row] == second[col]) {
					currentRow[col + 1] = prevRow[col];
				} else {
					currentRow[col + 1] = Math.min(Math.min(currentRow[col], prevRow[col + 1]), prevRow[col]) + 1;
				}
				prevRow[col] = currentRow[col];
			}
			prevRow[upper] = currentRow[upper];
		}
		return prevRow[second.length] <= maxDistance;
	}
}
