package edu.kit.iti.algo2.panda.indexing;

import java.util.HashMap;

public class SimpleInvertedIndex implements DocumentIndex {
	private static final int minimumWordLength = 2;
	
	private HashMap<String, DocumentList> invertedIndex;
	private int documentCount;
	
	public SimpleInvertedIndex() {
		this.invertedIndex = new HashMap<String, DocumentList>();
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
		DocumentList list = invertedIndex.get(word);
		if(list == null) {
			list = new DocumentList();
			invertedIndex.put(word, list);
		}
		list.add(documentNumber);
	}
	
	private String normalizeWord(String word) {
		return word.toLowerCase();
	}
	
	@Override
	public DocumentList queryWord(String word) {
		return invertedIndex.get(normalizeWord(word));
	}

	@Override
	public int getDocumentCount() {
		return documentCount;
	}
}
