package edu.kit.iti.algo2.panda.parsing;

import java.io.File;
import java.io.IOException;

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
	
	public Document createDocument(File file) throws IOException {
		String title = file.getName();
		String content;
		try {
			content = tika.parseToString(file);
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
