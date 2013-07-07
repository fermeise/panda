package edu.kit.iti.algo2.panda.indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class InvertedIndex implements DocumentIndex {
	private static final int minimumWordLength = 2;
	private static final int initialSpace = 16;
	
	protected HashMap<String, InvertedList> invertedIndex;
	protected TreeSet<Integer> obsoleteDocuments;
	protected int[] documentLength;
	protected long totalDocumentLength;
	protected int maxDocumentId;
	protected boolean calculatedInitialScoring;	
	
	public InvertedIndex() {
		this.invertedIndex = new HashMap<String, InvertedList>();
		this.obsoleteDocuments = new TreeSet<Integer>();
		this.documentLength = new int[initialSpace];
		this.totalDocumentLength = 0;
		this.maxDocumentId = 0;
		this.calculatedInitialScoring = false;
	}

	public int addDocument(Document document) {
		char[] content = document.getContent().toCharArray();
		
		if(documentLength.length == maxDocumentId) {
			int[] newDocumentLength = new int[documentLength.length * 2];
			for(int i = 0; i < documentLength.length; i++) {
				newDocumentLength[i] = documentLength[i];
			}
			documentLength = newDocumentLength;
		}
		
		if(calculatedInitialScoring) {
			HashSet<InvertedList> wordsUsed = new HashSet<InvertedList>();
			int pos = 0;
			while(pos < content.length) {
				while(pos < content.length && !Character.isAlphabetic(content[pos])) pos++;
				int wordBegin = pos;
				while(pos < content.length && Character.isAlphabetic(content[pos])) pos++;
				int wordEnd = pos;
				
				int wordLength = wordEnd - wordBegin;
				if(wordLength >= minimumWordLength) {
					String word = normalizeWord(new String(content, wordBegin, wordLength));
					InvertedList list = addToIndex(word, maxDocumentId);
					wordsUsed.add(list);
					documentLength[maxDocumentId]++;
				}
			}
			totalDocumentLength += documentLength[maxDocumentId];
			
			Iterator<InvertedList> it = wordsUsed.iterator();
			while(it.hasNext()) {
				it.next().scoreLast(this);
			}
		} else {
			int pos = 0;
			while(pos < content.length) {
				while(pos < content.length && !Character.isAlphabetic(content[pos])) pos++;
				int wordBegin = pos;
				while(pos < content.length && Character.isAlphabetic(content[pos])) pos++;
				int wordEnd = pos;
				
				int wordLength = wordEnd - wordBegin;
				if(wordLength >= minimumWordLength) {
					String word = normalizeWord(new String(content, wordBegin, wordLength));
					addToIndex(word, maxDocumentId);
					documentLength[maxDocumentId]++;
				}
			}
			totalDocumentLength += documentLength[maxDocumentId];
		}
		
		return maxDocumentId++;
	}
	
	@Override
	public void removeDocument(int id) {
		obsoleteDocuments.add(id);
	}
	
	@Override
	public void initialScoring() {
		if(calculatedInitialScoring) {
			throw new IllegalStateException("The initial scoring has already been calculated.");
		}
		
		Iterator<Entry<String, InvertedList>> it = invertedIndex.entrySet().iterator();
		while(it.hasNext()) {
			it.next().getValue().score(this);
		}
		calculatedInitialScoring = true;
	}
	
	@Override
	public InvertedList queryWord(String word) {
		InvertedList documents = invertedIndex.get(word);
		if(documents == null) {
			return new InvertedList();
		}
		return documents.remove(obsoleteDocuments);
	}

	@Override
	public int getDocumentCount() {
		return maxDocumentId - obsoleteDocuments.size();
	}
	
	@Override
	public int getMaxDocumentId() {
		return maxDocumentId;
	}
	
	@Override
	public Set<String> getWords() {
		return invertedIndex.keySet();
	}
	
	@Override
	public int getWordScore(String word) {
		final InvertedList list = invertedIndex.get(word);
		if(list != null) {
			return list.getWordScore(this);
		}
		return 0;
	}
	
	public void saveToFile(File file) throws IOException {
		InvertedIndexSerializer.toStream(this, new FileOutputStream(file));
	}
	
	public static InvertedIndex loadFromFile(File file) throws IOException, ParseException {
		return InvertedIndexSerializer.fromStream(new FileInputStream(file));
	}
	
	private InvertedList addToIndex(String word, int documentNumber) {
		InvertedList list = invertedIndex.get(word);
		if(list == null) {
			list = new InvertedList();
			invertedIndex.put(word, list);
		}
		list.add(documentNumber, 1);
		return list;
	}
	
	private String normalizeWord(String word) {
		return word.toLowerCase();
	}
}
