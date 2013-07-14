package edu.kit.iti.algo2.panda.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Autocomplete {
	
	private SortedSet<String> words;
	
	public Autocomplete() {
		this.words = new TreeSet<>();
	}
	
	public void addWord(String word) {
		this.words.add(word);
	}
	
	public void addWords(Collection<String> words) {
		this.words.addAll(words);
	}
	
	public List<String> completeWord(String query) {
		return new ArrayList<>(words.subSet(query, query + Character.MAX_VALUE));
	}

}
