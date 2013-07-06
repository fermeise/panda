package edu.kit.iti.algo2.panda.parsing;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.DocumentIndex;

public class FileSystemCrawler {
	private static final Logger log = Logger.getLogger("FileSystemCrawler");
	
	private DocumentStorage storage;
	private DocumentIndex index;
	
	public FileSystemCrawler(DocumentStorage storage, DocumentIndex index) {
		this.storage = storage;
		this.index = index;
	}
	
	public void crawl(Path directory) throws IOException {
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				super.visitFile(file, attrs);
				try {
					Document document = TikaDocumentFactory.getInstance().createDocument(file.toFile());
					if (!document.getContent().isEmpty()) {
						log.info(file.toString());
						int id = index.addDocument(document);
						storage.addDocument(id, document);
					}
				} catch (IOException e) {
					log.warning("Could not parse content of file '" + file.toString() + "'.");
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
