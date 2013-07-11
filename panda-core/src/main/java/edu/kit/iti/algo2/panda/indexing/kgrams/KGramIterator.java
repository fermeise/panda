package edu.kit.iti.algo2.panda.indexing.kgrams;

import java.util.Iterator;

import org.apache.poi.ss.formula.eval.NotImplementedException;

public class KGramIterator implements Iterator<String> {
	private final String word;
	private final int k;
	private int startIndex;
	private int endIndex;
	
	public KGramIterator(String word, int k) {
		this.word = word;
		this.k = Math.min(k, word.length());
		this.startIndex = 0;
		this.endIndex = Math.min(1, word.length());
	}

	@Override
	public boolean hasNext() {
		return startIndex < endIndex;
	}

	@Override
	public String next() {
		String result = word.substring(startIndex, endIndex);
		if (endIndex >= k) startIndex++;
		if (endIndex < word.length()) endIndex++;
		return result;
	}

	@Override
	public void remove() {
		throw new NotImplementedException("String is unmodifiable");
	}
}