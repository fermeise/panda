package edu.kit.iti.algo2.panda.parsing;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

import org.apache.tika.exception.TikaException;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.DocumentIndex;

public class FileSystemCrawler {
	private static final Logger log = Logger.getLogger("FileSystemCrawler");
	
	private DocumentFactory factory;
	private DocumentIndex index;
	
	public FileSystemCrawler(DocumentFactory factory, DocumentIndex index) {
		this.factory = factory;
		this.index = index;
	}
	
	public void crawl(Path directory) throws IOException {
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				super.visitFile(file, attrs);
				try {
					Document document = factory.createDocument(file.toFile());
					if (!document.getContent().isEmpty()) {
						log.info(file.toString());
						factory.addToLibrary(document);
						index.addDocument(document);
					}
				} catch (TikaException e) {
					log.warning("Could not parse content of file '" + file.toString() + "'.");
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
