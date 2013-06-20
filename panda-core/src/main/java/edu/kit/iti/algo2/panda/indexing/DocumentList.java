package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.List;

public class DocumentList {
	private static int initialSpace = 16;
	
	private int[] documents;
	private int documentCount;
	
	public DocumentList() {
		this.documents = new int[initialSpace];
		this.documentCount = 0;
	}
	
	public void add(int id) {
		if(documentCount > 0 && documents[documentCount - 1] == id) {
			return;
		}
		if(documents.length == documentCount) {
			int[] newDocuments = new int[documents.length * 2];
			for(int i = 0; i < documents.length; i++) {
				newDocuments[i] = documents[i];
			}
			documents = newDocuments;
		}
		documents[documentCount++] = id;
	}
	
	public int[] getDocumentArray() {
		return documents;
	}
	
	public int getDocumentCount() {
		return documentCount;
	}
	
	public List<Integer> asList() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < documentCount; i++) {
			list.add(documents[i]);
		}
		return list;
	}

	public static DocumentList intersect(DocumentList a, DocumentList b) {
		DocumentList r = new DocumentList();
		int indexA = 0, indexB = 0;
		while(indexA < a.documentCount && indexB < b.documentCount) {
			if(a.documents[indexA] < b.documents[indexB]) {
				indexA++;
			} else if(a.documents[indexA] > b.documents[indexB]) {
				indexB++;
			} else {
				r.add(a.documents[indexA]);
				indexA++;
				indexB++;
			}
		}
		
		return r;
	}
}
