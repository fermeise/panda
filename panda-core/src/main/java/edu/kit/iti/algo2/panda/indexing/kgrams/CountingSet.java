package edu.kit.iti.algo2.panda.indexing.kgrams;

/**
 * A set of unique sorted elements and the number of their occurrences.
 */
public class CountingSet {
	private static final int INITIAL_SPACE = 16;
	
	private int[] elements;
	private int[] countings;
	private int size;
	
	public CountingSet() {
		this.elements = new int[INITIAL_SPACE];
		this.countings = new int[INITIAL_SPACE];
	}
	
	/**
	 * Add all elements from other to this and updates the counting of this.
	 * 
	 * Can be seen as a special case of union between this and other.
	 * 
	 * @param other is a sorted set of integers to merge with.
	 */
	public void addAll(IntSet other) {
		int newInitialSize = size + other.size();
		int[] newElements = new int[newInitialSize];
		int[] newCountings = new int[newInitialSize];
		int i = 0, j = 0, k = 0;
		while (i < size || j < other.size()) {
			while (i < size && (j == other.size() || elements[i] < other.get(j))) {
				newElements[k] = elements[i];
				newCountings[k] = countings[i];
				++i; ++k;
			}
			while (j < other.size() && (i == size || elements[i] > other.get(j))) {
				newElements[k] = other.get(j);
				newCountings[k] = 1;
				++j; ++k;
			}
			if (i < size && j < other.size() && elements[i] == other.get(j)){
				newElements[k] = elements[i];
				newCountings[k] = countings[i] + 1;
				++i; ++j; ++k;
			}
		}
		elements = newElements;
		countings = newCountings;
		size = k;
	}
	
	/**
	 * @param index is the same index as the element.
	 * @return how many time the element at index was inserted.
	 */
	public int getCount(int index) {
		return this.countings[index];
	}
	
	/**
	 * @param index is the elements array index positions.
	 * @return the element at the given position.
	 */
	public int getElement(int index) {
		return this.elements[index];
	}
	
	/**
	 * @return the number of elements.
	 */
	public int size() {
		return this.size;
	}
}
