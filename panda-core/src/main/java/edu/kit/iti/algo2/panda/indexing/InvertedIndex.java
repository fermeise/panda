package edu.kit.iti.algo2.panda.indexing;

import java.util.HashMap;
import java.util.Map.Entry;

public class InvertedIndex implements DocumentIndex {
	private static final int minimumWordLength = 2;
	
	private HashMap<String, InvertedList> invertedIndex;
	private int documentCount;
	
	public InvertedIndex() {
		this.invertedIndex = new HashMap<String, InvertedList>();
		this.documentCount = 0;
	}

	public void addDocument(Document document) {
		char[] content = document.getContent().toCharArray();
		
		int pos = 0;
		while(pos < content.length) {
			while(pos < content.length && !Character.isAlphabetic(content[pos])) pos++;
			int wordBegin = pos;
			while(pos < content.length && Character.isAlphabetic(content[pos])) pos++;
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
		InvertedList list = invertedIndex.get(word);
		if(list == null) {
			list = new InvertedList();
			invertedIndex.put(word, list);
		}
		list.add(documentNumber);
	}
	
	private String normalizeWord(String word) {
		return word.toLowerCase();
	}
	
	@Override
	public void finish() {
		for(Entry<String, InvertedList> entry: invertedIndex.entrySet()) {
			entry.getValue().score(documentCount);
		}
	}
	
	@Override
	public InvertedList queryWord(String word) {
		InvertedList documents = invertedIndex.get(normalizeWord(word));
		if(documents == null) {
			documents = new InvertedList();
		}
		return documents;
	}

	@Override
	public int getDocumentCount() {
		return documentCount;
	}
}
