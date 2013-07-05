package edu.kit.iti.algo2.panda.management;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.parsing.DocumentFactory;
import edu.kit.iti.algo2.panda.parsing.FileSystemCrawler;

public class IndexManager {
	public final DocumentFactory factory;
	public final InvertedIndex index;
	
	public IndexManager(File documentLibraryFile, File indexFile, Path documentPath) throws IOException {
		this.factory = new DocumentFactory(documentLibraryFile.getAbsolutePath());

		if(indexFile.exists()) {
			System.out.println("Loading index...");
			InvertedIndex restoredIndex;
			try {
				restoredIndex = InvertedIndex.loadFromFile(indexFile);
			} catch (ParseException e) {
				System.out.println("Index file has wrong format.");
				System.out.println("Rebuilding...");
				restoredIndex = new InvertedIndex();
				for(int i = 0; i < factory.getDocumentCount(); i++) {
					restoredIndex.addDocument(factory.restoreDocument(i));
				}
				restoredIndex.finish();
				System.out.println("Saving index...");
				restoredIndex.saveToFile(indexFile);
			}
			index = restoredIndex;
		} else {
			System.out.println("Building index...");
			index = new InvertedIndex();
			FileSystemCrawler crawler = new FileSystemCrawler(factory, index);
			crawler.crawl(documentPath);
			index.finish();
			System.out.println("Saving index...");
			index.saveToFile(indexFile);
		}
	}

	public DocumentFactory getDocumentFactory() {
		return factory;
	}

	public InvertedIndex getDocumentIndex() {
		return index;
	}
}
