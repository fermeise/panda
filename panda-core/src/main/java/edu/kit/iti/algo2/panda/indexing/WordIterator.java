package edu.kit.iti.algo2.panda.indexing;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class WordIterator implements Iterator<WordOccurrence> {
	private static final int minimumWordLength = 2;
	
	private final char[] content;
	private int pos;
	private WordOccurrence occurrence;
	private boolean hasNext;
	
	public WordIterator(String str) {
		this.content = str.toCharArray();
		this.pos = 0;
		findNextWord();
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public WordOccurrence next() {
		if(!hasNext) {
			throw new NoSuchElementException();
		}
		WordOccurrence result = occurrence;
		findNextWord();
		return result;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private void findNextWord() {
		while(pos < content.length) {
			while(pos < content.length && !Character.isAlphabetic(content[pos])) pos++;
			int wordBegin = pos;
			while(pos < content.length && Character.isAlphabetic(content[pos])) pos++;
			int wordEnd = pos;
			
			int wordLength = wordEnd - wordBegin;
			if(wordLength >= minimumWordLength) {
				occurrence = new WordOccurrence(
						normalizeWord(new String(content, wordBegin, wordLength)),
						wordBegin, wordEnd);
				hasNext = true;
				return;
			}
		}
		hasNext = false;
		return;
	}
	
	public static String normalizeWord(String word) {
		return word.toLowerCase();
	}
}
