package edu.kit.iti.algo2.panda.parsing;

import java.io.File;

import edu.kit.iti.algo2.panda.indexing.Document;

public class GenericDocument implements Document {
	private final File file;
	private final String title;
	private final String content;
	
	public GenericDocument(File file, String title, String content) {
		this.file = file;
		this.title = title;
		this.content = content;
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getContent() {
		return this.content;
	}
}
