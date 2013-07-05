package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvertedList implements DocumentList {
	private static final int initialSpace = 16;
	private static final float k = 1.75f;
	private static final float b = 0.75f;
	
	private int[] documents;
	private int[] scores;
	private int documentCount;
	
	protected InvertedList() {
		this.documents = new int[initialSpace];
		this.scores = new int[initialSpace];
		this.documentCount = 0;
	}
	
	protected int[] getDocuments() {
		return documents;
	}

	protected int[] getScores() {
		return scores;
	}
	
	public int getDocumentCount() {
		return documentCount;
	}

	protected void add(int id, int occurrences) {
		if(documentCount > 0 && documents[documentCount - 1] == id) {
			scores[documentCount - 1]++;
			return;
		}
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
		scores[documentCount] = occurrences;
		documentCount++;
	}
	
	protected void score(InvertedIndex index) {
		// Maximum score is binaryLog(index.documentCount) * (k + 1)
		
		float idf = binaryLog(index.documentCount / documentCount);
		
		for(int i = 0; i < documentCount; i++) {
			float alpha = 1.0f - b + b * index.documentLength[documents[i]] /
					index.averageDocumentLength;
			float tf = (float)scores[i] * (k + 1.0f) / (k * alpha + scores[i]);
			this.scores[i] = (int)(tf * idf * 256.f);
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
	public List<ScoredDocument> asList() {
		ArrayList<ScoredDocument> scoredList = new ArrayList<ScoredDocument>();
		for(int i = 0; i < documentCount; i++) {
			scoredList.add(new ScoredDocument(documents[i], scores[i]));
		}
		Collections.sort(scoredList);
		
		return scoredList;
	}
	
	public int getWordScore(InvertedIndex index) {
		return binaryLog(index.documentCount / documentCount) * 256;
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
