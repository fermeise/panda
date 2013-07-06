package edu.kit.iti.algo2.panda.parsing;

import java.io.File;
import java.io.IOException;

import edu.kit.iti.algo2.panda.indexing.Document;

/**
 * A factory for creating (reading and parsing) documents.
 */
public interface DocumentFactory {
	/**
	 * Read and parse a document from disk.
	 * @param file The file containing the document.
	 * @return The parsed document.
	 * @throws IOException If the file could not be read.
	 */
	public Document createDocument(File file) throws IOException;
}
