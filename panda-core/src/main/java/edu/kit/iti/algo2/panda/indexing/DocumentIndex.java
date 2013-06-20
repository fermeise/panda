package edu.kit.iti.algo2.panda.indexing;

public interface DocumentIndex {
	public void addDocument(Document document);
	public int getDocumentCount();
	
	public DocumentList queryWord(String word);
}
