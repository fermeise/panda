package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.HashSet;

public class Query {
	private static HashSet<String> stopWords = StopWords.getInstance().getWords();
	
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
				final String finalWord = InvertedIndex.normalizeWord(word.substring(1));
				if(!stopWords.contains(finalWord)) {
					excludedWords.add(finalWord);
				}
			}
		} else {
			if(word.length() >= InvertedIndex.minimumWordLength) {
				final String finalWord = InvertedIndex.normalizeWord(word);
				if(!stopWords.contains(finalWord)) {
					includedWords.add(finalWord);
				}
			}
		}
	}
}
