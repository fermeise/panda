package edu.kit.iti.algo2.panda.parsing;

import java.io.IOException;
import java.nio.file.Path;

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
	public Document createDocument(Path file) throws IOException;
}
