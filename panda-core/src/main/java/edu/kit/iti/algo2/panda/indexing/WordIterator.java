package edu.kit.iti.algo2.panda.indexing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class WordIterator implements Iterator<TextOccurrence> {
	private static HashSet<String> stopWords = StopWords.getInstance().getWords();
	
	private final char[] content;
	private int pos;
	private TextOccurrence occurrence;
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
	public TextOccurrence next() {
		if(!hasNext) {
			throw new NoSuchElementException();
		}
		TextOccurrence result = occurrence;
		findNextWord();
		return result;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private void findNextWord() {
		while(pos < content.length) {
			while(pos < content.length && !InvertedIndex.isCharacter(content[pos])) pos++;
			int wordBegin = pos;
			while(pos < content.length && InvertedIndex.isCharacter(content[pos])) pos++;
			int wordEnd = pos;
			
			int wordLength = wordEnd - wordBegin;
			if(wordLength >= InvertedIndex.minimumWordLength) {
				final String word = InvertedIndex.normalizeWord(new String(content, wordBegin, wordLength));
				if(!stopWords.contains(word)) {
					occurrence = new TextOccurrence(word, wordBegin);
					hasNext = true;
					return;
				}
			}
		}
		hasNext = false;
		return;
	}
}
