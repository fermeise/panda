package edu.kit.iti.algo2.panda.indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class InvertedIndex implements DocumentIndex {
	private static final int minimumWordLength = 2;
	private static final int initialSpace = 16;
	
	protected HashMap<String, InvertedList> invertedIndex;
	protected int[] documentLength;
	protected int documentCount;
	
	protected float averageDocumentLength;
	
	public InvertedIndex() {
		this.invertedIndex = new HashMap<String, InvertedList>();
		this.documentLength = new int[initialSpace];
		this.documentCount = 0;
		
		this.averageDocumentLength = 0.0f;
	}

	public void addDocument(Document document) {
		char[] content = document.getContent().toCharArray();
		
		if(documentLength.length == documentCount) {
			int[] newDocumentLength = new int[documentLength.length * 2];
			for(int i = 0; i < documentLength.length; i++) {
				newDocumentLength[i] = documentLength[i];
			}
			documentLength = newDocumentLength;
		}
		
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
				documentLength[documentCount]++;
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
		list.add(documentNumber, 1);
	}
	
	private String normalizeWord(String word) {
		return word.toLowerCase();
	}
	
	@Override
	public void finish() {
		long totalDocumentLength = 0;
		for(int i = 0; i < documentCount; i++) {
			totalDocumentLength += documentLength[i];
		}
		this.averageDocumentLength = (float)totalDocumentLength / documentCount;
		
		Iterator<Entry<String, InvertedList>> it = invertedIndex.entrySet().iterator();
		while(it.hasNext()) {
			final Entry<String, InvertedList> entry = it.next();
			entry.getValue().score(this);
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
	
	@Override
	public Set<String> getWords() {
		return invertedIndex.keySet();
	}

	public void saveToFile(File file) throws IOException {
		InvertedIndexSerializer.toStream(this, new FileOutputStream(file));
	}
	
	public static InvertedIndex loadFromFile(File file) throws IOException {
		return InvertedIndexSerializer.fromStream(new FileInputStream(file));
	}
}
