package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import edu.kit.iti.algo2.panda.util.RankSorter;

public class InvertedList {
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
		
		float averageDocumentLength = (float)index.totalDocumentLength / (float)index.maxDocumentId;
		float idf = binaryLog(index.maxDocumentId / documentCount);
		
		for(int i = 0; i < documentCount; i++) {
			float alpha = 1.0f - b + b * index.documentLength[documents[i]] /
					averageDocumentLength;
			float tf = (float)scores[i] * (k + 1.0f) / (k * alpha + scores[i]);
			this.scores[i] = (int)(tf * idf * 256.f);
		}
	}
	
	public void scoreLast(InvertedIndex index) {
		float averageDocumentLength = (float)index.totalDocumentLength / (float)index.maxDocumentId;
		float idf = binaryLog(index.maxDocumentId / documentCount);
		
		int i = documentCount - 1;
		float alpha = 1.0f - b + b * index.documentLength[documents[i]] /
				averageDocumentLength;
		float tf = (float)scores[i] * (k + 1.0f) / (k * alpha + scores[i]);
		this.scores[i] = (int)(tf * idf * 256.f);
	}

	public List<ScoredDocument> rankResults(int maxResultCount) {
		ArrayList<ScoredDocument> scoredList = new ArrayList<ScoredDocument>();
		for(int i = 0; i < documentCount; i++) {
			scoredList.add(new ScoredDocument(documents[i], scores[i]));
		}
		int resultCount = Math.min(maxResultCount, scoredList.size());
		RankSorter.partialSort(scoredList, resultCount);
		
		return scoredList.subList(0, Math.min(maxResultCount, resultCount));
	}
	
	public int getWordScore(InvertedIndex index) {
		return binaryLog(index.maxDocumentId / documentCount) + 1;
	}

	public InvertedList intersect(InvertedList b) {
		InvertedList result = new InvertedList();
		int indexA = 0, indexB = 0;
		while(indexA < this.documentCount && indexB < b.documentCount) {
			if(this.documents[indexA] < b.documents[indexB]) {
				indexA++;
			} else if(this.documents[indexA] > b.documents[indexB]) {
				indexB++;
			} else {
				result.add(this.documents[indexA], this.scores[indexA] + b.scores[indexB]);
				indexA++;
				indexB++;
			}
		}
		
		return result;
	}

	public InvertedList remove(InvertedList b) {
		InvertedList result = new InvertedList();
		int indexA = 0, indexB = 0;
		while(indexA < this.documentCount && indexB < b.documentCount) {
			if(this.documents[indexA] < b.documents[indexB]) {
				result.add(this.documents[indexA], this.scores[indexA]);
				indexA++;
			} else if(this.documents[indexA] > b.documents[indexB]) {
				indexB++;
			} else {
				indexA++;
				indexB++;
			}
		}
		while(indexA < this.documentCount) {
			result.add(this.documents[indexA], this.scores[indexA]);
			indexA++;
		}
		
		return result;
	}
	
	public InvertedList remove(TreeSet<Integer> b) {
		InvertedList result = new InvertedList();
		Iterator<Integer> bIt = b.iterator();
		int indexA = 0;
		int bValue = bIt.hasNext() ? bIt.next() : -1;
		while(indexA < this.documentCount && bValue >= 0) {
			if(this.documents[indexA] < bValue) {
				result.add(this.documents[indexA], this.scores[indexA]);
				indexA++;
			} else if(this.documents[indexA] > bValue) {
				bValue = bIt.hasNext() ? bIt.next() : -1;
			} else {
				indexA++;
				bValue = bIt.hasNext() ? bIt.next() : -1;
			}
		}
		while(indexA < this.documentCount) {
			result.add(this.documents[indexA], this.scores[indexA]);
			indexA++;
		}
		
		return result;
	}
	
	private static int binaryLog(int arg) {
		int r = -1;
		while(arg > 0) {
			arg >>= 1;
			r++;
		}
		return r;
	}
}
