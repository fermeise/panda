package edu.kit.iti.algo2.panda.parsing;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import edu.kit.iti.algo2.panda.indexing.DocumentIndex;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;

public class FilesystemCrawler {
	
	private final DocumentIndex index;
	private final List<Path> documents = new ArrayList<>();
	private static final Logger LOG = Logger.getLogger("Crawler");
	
	private final MyFileVisitor visitor = new MyFileVisitor();
	
	
	public FilesystemCrawler(DocumentIndex index) {
		this.index = index;
	}
	
	public List<Path> getDocuments(List<ScoredDocument> documentIndices) {
		List<Path> result = new ArrayList<Path>(documentIndices.size());
		for (ScoredDocument doc : documentIndices) {
			result.add(documents.get(doc.getId()));
		}
		return result;
	}
	
	public void crawl(Path directory) throws IOException {
		Files.walkFileTree(directory, visitor);
		documents.addAll(visitor.getVisitedFiles());
		visitor.clean();
	}
	
	private class MyFileVisitor extends SimpleFileVisitor<Path> {
		
		private final List<Path> visitedFiles = new LinkedList<>();
		
		public List<Path> getVisitedFiles() {
			return visitedFiles;
		}
		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			super.visitFile(file, attrs);
			FileEntry entry = new FileEntry(file.toFile());
			if (!entry.getContent().isEmpty()) {
				index.addDocument(entry);
				visitedFiles.add(file);
				LOG.info(file.toString());
			}
			return FileVisitResult.CONTINUE;
		}
		
		public void clean() {
			this.visitedFiles.clear();
		}
		
	}
}
