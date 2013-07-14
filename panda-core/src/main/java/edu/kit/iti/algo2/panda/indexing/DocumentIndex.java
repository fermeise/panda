package edu.kit.iti.algo2.panda.indexing;

import java.util.Set;

import edu.kit.iti.algo2.panda.indexing.kgrams.KGramIndex;

/**
 * An index of documents. All indexed document can be identified using an
 * integer id. The ids start with 0.
 */
public interface DocumentIndex {
	/**
	 * Add a document to the index.
	 * @param document The document to be added.
	 * @return The id of the added document.
	 */
	public int addDocument(Document document);
	
	/**
	 * Remove a document from the index.
	 * @param id The id of the document to be removed.
	 */
	public void removeDocument(int id);
	
	/**
	 * Call this before the first query is made so the initial score values
	 * are calculated.
	 */
	public void initialScoring();
	
	/**
	 * @return The total number of documents.
	 */
	public int getDocumentCount();
	
	/**
	 * @return The first free document id (the highest used document id + 1).
	 */
	public int getMaxDocumentId();
	
	/**
	 * @return All words in the index.
	 */
	public Set<String> getWords();
	
	/**
	 * @param word The word.
	 * @return A score value for an occurrence of this word.
	 */
	public int getWordScore(String word);
	
	/**
	 * @param word The word.
	 * @return The number of occurrences of the word.
	 */
	public int getTermFrequency(String word);
	
	/**
	 * Query the index for documents containing a certain word.
	 * @param word The word in question.
	 * @return All documents containing the word.
	 */
	public InvertedList queryWord(String word);

	/**
	 * @return The KGramIndex for the words used in the index.
	 */
	public KGramIndex getKGramIndex();
}
