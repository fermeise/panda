package edu.kit.iti.algo2.panda.indexing;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class InvertedIndex implements DocumentIndex {
	private static final int minimumWordLength = 2;
	private static final int initialSpace = 16;
	
	private HashMap<String, InvertedList> invertedIndex;
	
	protected int[] documentLength;
	protected float averageDocumentLength;
	protected int documentCount;
	
	public InvertedIndex() {
		this.invertedIndex = new HashMap<String, InvertedList>();
		this.documentLength = new int[initialSpace];
		this.averageDocumentLength = 0.0f;
		this.documentCount = 0;
	}
	
	public Set<String> getWords() {
		return this.invertedIndex.keySet();
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
		list.add(documentNumber);
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

	public void saveToFile(File file) throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
		for(Entry<String, InvertedList> entry: invertedIndex.entrySet()) {
			final String word = entry.getKey();
			stream.writeInt(word.length());
			stream.writeChars(word);
			
			final int[] documents = entry.getValue().getDocuments();
			final float[] scores = entry.getValue().getScores();
			final int documentCount = entry.getValue().getDocumentCount();
			
			stream.writeInt(documentCount);
			int offset = 0;
			for(int i = 0; i < documentCount; i++) {
				stream.writeInt(documents[i] - offset);
				stream.writeByte((int)(scores[i] * 4.0f));
				offset = documents[i];
			}
		}
		stream.close();
	}
	
	public static InvertedIndex loadFromFile(File file) throws IOException {
		InvertedIndex index = new InvertedIndex();
		
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
		try {
			while(true) {
				final int wordLength = stream.readInt();
				String word = "";
				for(int i = 0; i < wordLength; i++) {
					word += stream.readChar();
				}
				
				InvertedList list = new InvertedList();
				final int documentCount = stream.readInt();
				int offset = 0;
				for(int i = 0; i < documentCount; i++) {
					offset += stream.readInt();
					final int score = stream.readByte();
					list.add(offset, (float)score * 0.25f);
				}
				
				index.invertedIndex.put(word, list);
			}
		} catch(EOFException e) {
		} finally {
			stream.close();
		}
		
		return index;
	}
}
