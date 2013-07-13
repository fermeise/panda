package edu.kit.iti.algo2.panda.management;

import java.util.List;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.Query;

public interface IndexFacade {
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
