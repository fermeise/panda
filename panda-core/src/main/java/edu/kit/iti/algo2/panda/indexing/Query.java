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
					addWord(word);
				}
				wordBegin = i + 1;
			}
		}
		String word = queryString.substring(wordBegin);
		if(!word.isEmpty()) {
			addWord(word);
		}
	}

	public ArrayList<String> getIncludedWords() {
		return includedWords;
	}

	public ArrayList<String> getExcludedWords() {
		return excludedWords;
	}
	
	private void addWord(String word) {
		if(word.startsWith("-")) {
			if(word.length() >= InvertedIndex.minimumWordLength + 1) {
				excludedWords.add(InvertedIndex.normalizeWord(word.substring(1)));
			}
		} else {
			if(word.length() >= InvertedIndex.minimumWordLength) {
				includedWords.add(InvertedIndex.normalizeWord(word));
			}
		}
	}
}
