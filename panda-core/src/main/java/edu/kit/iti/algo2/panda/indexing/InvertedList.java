package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvertedList implements DocumentList {
	private static int initialSpace = 16;
	
	private int[] documents;
	private int[] wordFrequency;
	private int[] scores;
	private int documentCount;
	
	protected InvertedList() {
		this.documents = new int[initialSpace];
		this.wordFrequency = new int[initialSpace];
		this.scores = new int[initialSpace];
		this.documentCount = 0;
	}
	
	protected void add(int id) {
		if(documentCount > 0 && documents[documentCount - 1] == id) {
			wordFrequency[documentCount - 1]++;
			return;
		}
		if(documents.length == documentCount) {
			int[] newDocuments = new int[documents.length * 2];
			int[] newWordFrequency = new int[documents.length * 2];
			for(int i = 0; i < documents.length; i++) {
				newDocuments[i] = documents[i];
			}
			for(int i = 0; i < documents.length; i++) {
				newWordFrequency[i] = wordFrequency[i];
			}
			documents = newDocuments;
			wordFrequency = newWordFrequency;
		}
		documents[documentCount] = id;
		wordFrequency[documentCount] = 1;
		documentCount++;
	}
	
	protected void add(int id, int score) {
		// TODO: This is rather dirty stuff :(
		if(documents.length == documentCount) {
			int[] newDocuments = new int[documents.length * 2];
			int[] newScores = new int[documents.length * 2];
			for(int i = 0; i < documents.length; i++) {
				newDocuments[i] = documents[i];
			}
			for(int i = 0; i < documents.length; i++) {
				newScores[i] = scores[i];
			}
			documents = newDocuments;
			scores = newScores;
		}
		documents[documentCount] = id;
		scores[documentCount] = score;
		documentCount++;
	}
	
	protected void score(int totalDocumentCount) {
		this.scores = new int[documentCount];
		int idf = binaryLog(totalDocumentCount / documentCount);
		for(int i = 0; i < documentCount; i++) {
			this.scores[i] = wordFrequency[i] * idf;
		}
	}
	
	private int binaryLog(int arg) {
		int r = -1;
		while(arg > 0) {
			arg >>= 1;
			r++;
		}
		return r;
	}
	
	@Override
	public List<Integer> asList() {
		ArrayList<ScoredDocument> scoredList = new ArrayList<ScoredDocument>();
		for(int i = 0; i < documentCount; i++) {
			scoredList.add(new ScoredDocument(documents[i], scores[i]));
		}
		Collections.sort(scoredList);
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(ScoredDocument d: scoredList) {
			list.add(d.getId());
		}
		return list;
	}

	public static InvertedList intersect(InvertedList a, InvertedList b) {
		InvertedList r = new InvertedList();
		int indexA = 0, indexB = 0;
		while(indexA < a.documentCount && indexB < b.documentCount) {
			if(a.documents[indexA] < b.documents[indexB]) {
				indexA++;
			} else if(a.documents[indexA] > b.documents[indexB]) {
				indexB++;
			} else {
				r.add(a.documents[indexA], a.scores[indexA] + b.scores[indexB]);
				indexA++;
				indexB++;
			}
		}
		
		return r;
	}
}
