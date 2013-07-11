package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;

public class Query {
	private final ArrayList<String> includedWords;
	private final ArrayList<String> excludedWords;

	public Query(String queryString) {
		this.includedWords = new ArrayList<String>();
		this.excludedWords = new ArrayList<String>();

		int wordBegin = 0;
		
		for(int i = 0; i < queryString.length(); i++) {
			char current = queryString.charAt(i);
			if(current == ' ') {
				String word = queryString.substring(wordBegin, i);
				if(!word.isEmpty()) {
					if(word.startsWith("-")) {
						excludedWords.add(WordIterator.normalizeWord(word.substring(1)));
					} else {
						includedWords.add(WordIterator.normalizeWord(word));
					}
				}
				wordBegin = i + 1;
			}
		}
		String word = queryString.substring(wordBegin);
		if(!word.isEmpty()) {
			if(word.startsWith("-")) {
				excludedWords.add(WordIterator.normalizeWord(word.substring(1)));
			} else {
				includedWords.add(WordIterator.normalizeWord(word));
			}
		}
	}

	public ArrayList<String> getIncludedWords() {
		return includedWords;
	}

	public ArrayList<String> getExcludedWords() {
		return excludedWords;
	}
}
