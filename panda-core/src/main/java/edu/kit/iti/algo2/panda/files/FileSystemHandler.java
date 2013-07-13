package edu.kit.iti.algo2.panda.files;

import java.nio.file.Path;

public interface FileSystemHandler {
	/**
	 * Determine whether the file is a document that should be tracked.
	 * @param path The path of the document.
	 * @return True iff the file is a document that should be tracked.
	 */
	public boolean fileMatches(Path path);
	
	/**
	 * A document has been created in the filesystem.
	 * @param path The path of the document.
	 * @return The unique id of the document.
	 */
	public int addDocument(Path path);
	
	/**
	 * A document has been removed from the filesystem.
	 * @param path The path of the document.
	 * @param documentId The unique id of the document.
	 */
	public void documentRemoved(Path path, int documentId);
	
	/**
	 * This is called after the initial update process completed.
	 */
	public void signalInitialUpdateComplete();
}
