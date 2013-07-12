package edu.kit.iti.algo2.panda.indexing.kgrams;

import java.util.Iterator;

import org.apache.poi.ss.formula.eval.NotImplementedException;

public class KGramIterator implements Iterator<String> {
	private final String word;
	private int startIndex;
	private int endIndex;
	
	public KGramIterator(String word, int k) {
		this.word = word;
		this.startIndex = 0;
		this.endIndex = k;
	}

	@Override
	public boolean hasNext() {
		return endIndex <= word.length();
	}

	@Override
	public String next() {
		String result = word.substring(startIndex, endIndex);
		startIndex++; endIndex++;
		return result;
	}

	@Override
	public void remove() {
		throw new NotImplementedException("String is unmodifiable");
	}
}