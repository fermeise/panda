package edu.kit.iti.algo2.panda.parsing;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.kit.iti.algo2.panda.indexing.DocumentIndex;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;

public class FileSystemCrawler {
	private static final Logger LOG = Logger.getLogger("Crawler");
	
	private final ArrayList<FileEntry> documents;
	
	public FileSystemCrawler() {
		this.documents = new ArrayList<FileEntry>();
	}
	
	public List<FileEntry> getDocuments() {
		return this.documents;
	}
	
	public List<Path> getDocuments(List<ScoredDocument> documentIndices) {
		ArrayList<Path> result = new ArrayList<Path>(documentIndices.size());
		for (ScoredDocument doc : documentIndices) {
			result.add(documents.get(doc.getId()).getFile().toPath());
		}
		return result;
	}
	
	public void crawl(Path directory) throws IOException {
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				super.visitFile(file, attrs);
				FileEntry entry = new FileEntry(file.toFile());
				if (!entry.getContent().isEmpty()) {
					documents.add(entry);
					LOG.info(file.toString());
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	public void index(DocumentIndex index) {
		for(FileEntry entry: documents) {
			index.addDocument(entry);
		}
	}
}
