package edu.kit.iti.algo2.panda.indexing;

import java.util.Set;

/**
 * An index of documents.
 * @author Christian
 */
public interface DocumentIndex {
	/**
	 * Add a document to the index.
	 * @param document The document to be added.
	 */
	public void addDocument(Document document);
	
	/**
	 * Call this after all documents were added to the index.
	 */
	public void finish();
	
	/**
	 * @return The total number of documents.
	 */
	public int getDocumentCount();
	
	/**
	 * 
	 * @return All words in the index.
	 */
	public Set<String> getWords();
	
	/**
	 * Query the index for documents containing a certain word.
	 * @param word The word in question.
	 * @return All documents containing the word.
	 */
	public InvertedList queryWord(String word);
}
