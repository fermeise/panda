package edu.kit.iti.algo2.panda.indexing.kgrams;

import java.util.ArrayList;
import java.util.List;

/**
 * A fast set of integer values. The values have to be added in ascending order.
 * It is checked that the same value is not added twice.
 */
public class IntSet {
	private static final int INITIAL_SPACE = 16;
	
	private int[] elements;
	private int elementCount;
	
	public IntSet() {
		this.elements = new int[INITIAL_SPACE];
		this.elementCount = 0;
	}
	
	protected int[] getElements() {
		return elements;
	}

	public void add(int id) {
		if(elementCount > 0 && elements[elementCount - 1] == id) {
			return;
		}
		if(elements.length == elementCount) {
			int[] newElements = new int[elements.length * 2];
			for(int i = 0; i < elements.length; i++) {
				newElements[i] = elements[i];
			}
			elements = newElements;
		}
		elements[elementCount] = id;
		elementCount++;
	}
	
	public boolean isEmpty() {
		return elementCount == 0;
	}
	
	public int get(int index) {
		return this.elements[index];
	}
	
	public int size() {
		return elementCount;
	}
	
	public List<Integer> asList() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < elementCount; i++) {
			list.add(elements[i]);
		}
		
		return list;
	}
}
