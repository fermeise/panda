package edu.kit.iti.algo2.panda.parsing;

import edu.kit.iti.algo2.panda.indexing.Document;

/**
 * A factory for storing documents.
 */
public interface DocumentStorage {
	/**
	 * Add a document to the library. The change is only made persistent after
	 * commitChanges() has been called.
	 * @param id The id of the document. The first valid id is 0. The id has to be unique.
	 * @param document The document to be added to the library.
	 */
	public void addDocument(int id, Document document);
	
	/**
	 * Removes a document from the library. The change is only made persistent after
	 * commitChanges() has been called.
	 * @param id The id of the document. The first valid id is 0. The id has to be unique.
	 */
	public void removeDocument(int id);
	
	/**
	 * Change the id of a stored document.
	 * @param oldId The old id of the document.
	 * @param newId The new id of the document.
	 */
	public void changeId(int oldId, int newId);
	
	/**
	 * Commits all changes made to the storage.
	 */
	public void commitChanges();
	
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

	/**
	 * Clear all stored documents and return to the initial state.
	 */
	public void reset();
}
