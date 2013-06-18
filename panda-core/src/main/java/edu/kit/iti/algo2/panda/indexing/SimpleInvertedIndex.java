package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.HashMap;

public class SimpleInvertedIndex implements DocumentIndex {
	private static final int minimumWordLength = 2;
	
	private HashMap<String, ArrayList<Integer>> invertedIndex;
	private int documentCount;
	
	public SimpleInvertedIndex() {
		this.invertedIndex = new HashMap<String, ArrayList<Integer>>();
		this.documentCount = 0;
	}

	public void addDocument(Document document) {
		char[] content = document.getContent().toCharArray();
		int contentLength = document.getContent().length();
		
		int pos = 0;
		while(pos < contentLength) {
			while(pos < contentLength && !Character.isAlphabetic(content[pos])) pos++;
			int wordBegin = pos;
			while(pos < contentLength && Character.isAlphabetic(content[pos])) pos++;
			int wordEnd = pos;
			
			int wordLength = wordEnd - wordBegin;
			if(wordLength >= minimumWordLength) {
				String word = normalizeWord(new String(content, wordBegin, wordLength));
				addToIndex(word, documentCount);
			}
		}
		documentCount++;
	}
	
	private void addToIndex(String word, int documentNumber) {
		ArrayList<Integer> list = invertedIndex.get(word);
		if(list == null) {
			list = new ArrayList<Integer>();
			invertedIndex.put(word, list);
		}
		list.add(documentNumber);
	}
	
	private String normalizeWord(String word) {
		return word.toLowerCase();
	}
	
	@Override
	public ArrayList<Integer> queryWord(String word) {
		return invertedIndex.get(normalizeWord(word));
	}

	@Override
	public int getDocumentCount() {
		return documentCount;
	}
}
