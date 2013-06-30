package edu.kit.iti.algo2.panda.indexing;

public class WordOccurrence {
	private final String word;
	private final int begin;
	private final int end;
	
	public WordOccurrence(String word, int begin, int end) {
		this.word = word;
		this.begin = begin;
		this.end = end;
	}
	
	public String getWord() {
		return word;
	}
	
	public int getBegin() {
		return begin;
	}
	
	public int getEnd() {
		return end;
	}
}
