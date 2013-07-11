package edu.kit.iti.algo2.panda.parsing;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import edu.kit.iti.algo2.panda.indexing.Document;

/**
 * Document factory that parses documents using Tika.
 */
public class TikaDocumentFactory implements DocumentFactory {
	private static final Tika tika = new Tika();
	private static TikaDocumentFactory instance;
	
	private TikaDocumentFactory() {
	}
	
	@Override
	public Document createDocument(Path file) throws IOException {
		String title = file.getFileName().toString();
		String content;
		try {
			content = tika.parseToString(file.toFile());
		} catch (TikaException e) {
			throw new IOException(e);
		}

		return new GenericDocument(file, title, content);
	}
	
	public static TikaDocumentFactory getInstance() {
		if(instance == null) {
			instance = new TikaDocumentFactory();
		}
		return instance;
	}
}
