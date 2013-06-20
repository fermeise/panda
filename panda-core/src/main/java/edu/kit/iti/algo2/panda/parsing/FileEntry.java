package edu.kit.iti.algo2.panda.parsing;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import edu.kit.iti.algo2.panda.indexing.Document;

public class FileEntry implements Document {
	
	private static Tika TIKA = new Tika();
	private final String content;
	private final File file;
	
	public FileEntry(File file) {
		this.file = file;
		String content = "";
		try {
			content = TIKA.parseToString(file);
		} catch (IOException | TikaException e) {}
		this.content = content;
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContent() {
		return this.content;
	}

}
