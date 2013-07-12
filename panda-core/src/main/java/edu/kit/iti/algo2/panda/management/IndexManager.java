package edu.kit.iti.algo2.panda.management;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.kit.iti.algo2.panda.files.FileSystemHandler;
import edu.kit.iti.algo2.panda.files.FileSystemWatcher;
import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.indexing.Query;
import edu.kit.iti.algo2.panda.indexing.QueryProcessor;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;
import edu.kit.iti.algo2.panda.parsing.DocumentStorage;
import edu.kit.iti.algo2.panda.parsing.SQLiteDocumentStorage;
import edu.kit.iti.algo2.panda.parsing.TikaDocumentFactory;

public class IndexManager implements IndexFacade, FileSystemHandler {
	private static final Logger log = Logger.getLogger("IndexManager");
	
	private final File documentLibraryFile;
	private final File documentIndexFile;
	private final File fileSystemFile;
	
	private DocumentStorage storage;
	private FileSystemWatcher fileWatcher;
	private InvertedIndex index;
	private QueryProcessor queryProcessor;
	
	public IndexManager(String storagePath) {
		documentLibraryFile = new File(storagePath + ".db");
		documentIndexFile = new File(storagePath + ".pnd");
		fileSystemFile = new File(storagePath + ".pfs");
		
		this.storage = new SQLiteDocumentStorage(documentLibraryFile.getAbsolutePath());
		
		try {
			fileWatcher = FileSystemWatcher.createFromFile(this, fileSystemFile);
			index = InvertedIndex.loadFromFile(documentIndexFile);
		} catch (ParseException | IOException e) {
		}
		if(fileWatcher != null && index != null) {
			log.info("Index with " + index.getDocumentCount() + " documents loaded successfully.");
			
			this.fileWatcher.update();
		} else {
			storage.reset();
			this.fileWatcher = new FileSystemWatcher(this);
			this.index = new InvertedIndex();
		}
		
		this.queryProcessor = new QueryProcessor(index);
	}
	
	@Override
	public void addDirectory(Path directory) {
		fileWatcher.addDirectory(directory);
		update();
		if(!index.isScored()) {
			log.info("Calculating initial scoring.");
			index.initialScoring();
		}
	}
	
	@Override
	public void removeDirectory(Path directory) {
		fileWatcher.removeDirectory(directory);
		update();
	}
	
	@Override
	public void update() {
		fileWatcher.update();
		if(index.needsRebuild()) {
			rebuild();
		}
	}
	
	public void rebuild() {
		log.info("Rebuilding index...");
		
		index = new InvertedIndex();
		queryProcessor = new QueryProcessor(index);
		
		final int maxDocumentId = storage.getMaxDocumentId();
		for(int id = 0; id < maxDocumentId; id++) {
			Document document = storage.restoreDocument(id);
			if(document != null) {
				int newId = index.addDocument(document);
				storage.changeId(id, newId);
				fileWatcher.changeId(document.getFile(), newId);
			}
		}
		index.initialScoring();
	}
	
	@Override
	public List<Document> query(Query query, int maxResultCount) {
		final List<ScoredDocument> queryResult = queryProcessor.query(query, maxResultCount);
		
		ArrayList<Document> documents = new ArrayList<Document>();
		for(ScoredDocument scoredDocument: queryResult) {
			documents.add(storage.restoreDocument(scoredDocument.getId()));
		}
		return documents;
	}

	@Override
	public String extractSnippet(Document document, Query query, int maxSnippetSize) {
		return queryProcessor.extractSnippet(document, query, maxSnippetSize);
	}

	@Override
	public void close() {
		log.info("Saving files to disk...");
		try {
			storage.commitChanges();
			fileWatcher.saveToFile(fileSystemFile);
			index.saveToFile(documentIndexFile);
			log.info("Success.");
		} catch(IOException e) {
			log.severe("Error while saving to disk.");
		}
	}
		
	@Override
	public boolean fileMatches(Path path) {
		final String pathString = path.toString().toLowerCase();
		return pathString.endsWith(".pdf") ||
				pathString.endsWith(".ps") ||
				pathString.endsWith(".txt") ||
				pathString.endsWith(".doc") ||
				pathString.endsWith(".docx");
	}

	@Override
	public int addDocument(Path path) {
		try {
			Document document = TikaDocumentFactory.getInstance().createDocument(path);
			if (!document.getContent().isEmpty()) {
				log.info(path.toString());
				int id = index.addDocument(document);
				storage.addDocument(id, document);
				return id;
			}
		} catch (IOException e) {
			log.warning("Could not parse content of file '" + path.toString() + "'.");
		}
		return -1;
	}

	@Override
	public void documentRemoved(Path path, int documentId) {
		log.info(path.toString());
		index.removeDocument(documentId);
		storage.removeDocument(documentId);
	}
}
