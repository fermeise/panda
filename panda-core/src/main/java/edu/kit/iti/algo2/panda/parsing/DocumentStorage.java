package edu.kit.iti.algo2.panda.parsing;

import edu.kit.iti.algo2.panda.indexing.Document;

/**
 * A factory for storing documents.
 */
public interface DocumentStorage {
	/**
	 * Add a document to the library.
	 * @param id The id of the document. The first valid id is 0. The id has to be unique.
	 * @param document The document to be added to the library.
	 */
	public void addDocument(int id, Document document);
	
	/**
	 * Add a document to the library.
	 * @param id The id of the document. The first valid id is 0. The id has to be unique.
	 * @param document The document to be added to the library.
	 */
	public void removeDocument(int id);
	
	/**
	 * Restore a document from the library.
	 * @param id The id of the document to be restored.
	 * @return The document.
	 */
	public Document restoreDocument(int id);
	
	/**
	 * @return the first unused document id. (often the last used document id + 1)
	 */
	public int getMaxDocumentId();
}
