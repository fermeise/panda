package edu.kit.iti.algo2.panda.indexing;

import java.util.List;

/**
 * A list of document indices.
 * @author Christian
 */
public interface DocumentList {
	/**
	 * @return The document indices.
	 */
	public List<ScoredDocument> asList();
}
