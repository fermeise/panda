package edu.kit.iti.algo2.panda.management;

import java.nio.file.Path;
import java.util.List;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.Query;

public interface IndexFacade {
	/**
	 * Add a directory to the index. All documents in the directory or subdirectories
	 * are indexed.
	 * @param directory The directory to be indexed.
	 * @throws UserException If the directory is already indexed.
	 */
	public void addDirectory(Path directory);
	
	/**
	 * Removes a directory from the index.
	 * @param directory The directory to be removed from the index.
	 */
	public void removeDirectory(Path directory);
	
	/**
	 * Query the index.
	 * @param query The query to be executed.
	 * @param maxResultCount The maximum number of results to be returned.
	 * @return The documents matching the query sorted by relevance.
	 */
	public List<Document> query(Query query, int maxResultCount);
	
	/**
	 * Generate a snippet for a document returned by a query.
	 * @param document The document returned by a query.
	 * @param query The query for which the snippet is generated.
	 * @param maxSnippetSize The maximum size of the snippet.
	 * @return The generated snippet.
	 */
	public String extractSnippet(Document document, Query query, int maxSnippetSize);
	
	/**
	 * Checks whether the content of the watched directories has changed and
	 * updates the index if necessary.
	 */
	public void update();
	
	/**
	 * Rebuilds the index explicitly, so the obsolete files are no longer in
	 * the index and the scores are updated. 
	 */
	public void rebuild();
	
	/**
	 * Terminate the indexing process and store the index and all auxiliary
	 * data structures to the disk.
	 */
	public void close();
}
