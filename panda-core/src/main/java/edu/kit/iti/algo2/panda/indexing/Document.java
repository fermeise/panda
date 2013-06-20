package edu.kit.iti.algo2.panda.indexing;

import java.io.File;

/**
 * A representation of an indexed document.
 * @author Christian
 */
public interface Document {
	/**
	 * @return The file from which the document originates.
	 */
	public File getFile();
	
	/**
	 * @return The title of the document.
	 */
	public String getTitle();
	
	/**
	 * @return The content of the document in plain text.
	 */
	public String getContent();
}
