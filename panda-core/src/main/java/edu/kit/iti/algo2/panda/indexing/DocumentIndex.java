package edu.kit.iti.algo2.panda.indexing;

import java.util.List;

public interface DocumentIndex {
	public void addDocument(Document document);
	public int getDocumentCount();
	
	public List<Integer> queryWord(String word);
}
