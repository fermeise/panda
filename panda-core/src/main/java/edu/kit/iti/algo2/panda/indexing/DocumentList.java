package edu.kit.iti.algo2.panda.indexing;

import java.util.List;

/**
 * A list of document indices.
 */
public interface DocumentList {
	/**
	 * @param maxResultCount The maximum number of results returned.
	 * @return The best ranked documents in order of descending score.
	 */
	public List<ScoredDocument> bestResults(int maxResultCount);
}
