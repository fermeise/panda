package edu.kit.iti.algo2.panda.parsing;

import java.nio.file.Path;

import edu.kit.iti.algo2.panda.indexing.Document;

public class GenericDocument implements Document {
	private final Path file;
	private final String title;
	private final String content;
	
	public GenericDocument(Path file, String title, String content) {
		this.file = file;
		this.title = title;
		this.content = content;
	}

	@Override
	public Path getFile() {
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
