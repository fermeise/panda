package edu.kit.iti.algo2.panda.management;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
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

/**
 * The index manager is the central entity of the indexing system and coordinates
 * the behavior of the DocumentStorage, the FileWatcher and the InvertedIndex.
 * The Index manager ensures that the public interface can be used in a threaded
 * way.
 * Implementation hint: The FileSystemWatcher is synchronized too. It has to be
 * ensured that the FileSystemWatcher is never locked after the IndexManager in
 * order to avoid deadlocks.
 */
public class IndexManager implements IndexFacade, FileSystemHandler {
	private static final Logger log = Logger.getLogger("IndexManager");
	private final File documentLibraryFile;
	private final File documentIndexFile;
	private final File fileSystemFile;
	private final HashSet<StatusListener> statusListener;
	
	private DocumentStorage storage;
	private FileSystemWatcher fileWatcher;
	private InvertedIndex index;
	private QueryProcessor queryProcessor;
	
	public IndexManager(String storagePath, List<Path> documentPaths) {
		documentLibraryFile = new File(storagePath + ".db");
		documentIndexFile = new File(storagePath + ".pnd");
		fileSystemFile = new File(storagePath + ".pfs");
		statusListener = new HashSet<StatusListener>();
		
		this.storage = new SQLiteDocumentStorage(documentLibraryFile.getAbsolutePath());
		
		try {
			fileWatcher = FileSystemWatcher.createFromFile(this, documentPaths, fileSystemFile);
			index = InvertedIndex.loadFromFile(documentIndexFile);
		} catch (ParseException | IOException e) {
		}
		if(fileWatcher != null && index != null) {
			updateStatus("Index with " + index.getDocumentCount() + " documents loaded successfully.");
		} else {
			storage.reset();
			this.fileWatcher = new FileSystemWatcher(this, documentPaths);
			this.index = new InvertedIndex();
		}
		this.queryProcessor = new QueryProcessor(index);
		
		fileWatcher.start();
	}
	
	public void rebuild() {
		// Pause fileWatcher first in order to avoid deadlock
		synchronized(fileWatcher) {
			synchronized(this) {
				updateStatus("Rebuilding index...");
				
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
				
				updateStatus("Index rebuild completed.");
			}
		}
	}
	
	@Override
	public synchronized List<Document> query(Query query, int maxResultCount) {
		final List<ScoredDocument> queryResult = queryProcessor.query(query, maxResultCount);
		
		ArrayList<Document> documents = new ArrayList<Document>();
		for(ScoredDocument scoredDocument: queryResult) {
			documents.add(storage.restoreDocument(scoredDocument.getId()));
		}
		return documents;
	}

	@Override
	public synchronized String extractSnippet(Document document, Query query, int maxSnippetSize, boolean html) {
		return queryProcessor.extractSnippet(document, query, maxSnippetSize, html);
	}

	@Override
	public void close() {
		fileWatcher.stop();
		updateStatus("Saving index to disk...");
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
	public synchronized void addStatusListener(StatusListener listener) {
		this.statusListener.add(listener);
	}
	
	@Override
	public synchronized void removeStatusListener(StatusListener listener) {
		this.statusListener.remove(listener);
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
				synchronized(this) {
					updateStatus("Added document: " + path.toString());
					int id = index.addDocument(document);
					storage.addDocument(id, document);
					return id;
				}
			}
		} catch (IOException e) {
			log.warning("Could not parse content of file '" + path.toString() + "'.");
		}
		return -1;
	}

	@Override
	public synchronized void documentRemoved(Path path, int documentId) {
		updateStatus("Removed document: " + path.toString());
		index.removeDocument(documentId);
		storage.removeDocument(documentId);
	}

	@Override
	public void signalInitialUpdateComplete() {
		synchronized(this) {
			if(!index.isScored()) {
				updateStatus("Indexing completed.");
				index.initialScoring();
			}
		}
		synchronized(fileWatcher) {
			synchronized(this) {
				if(index.needsRebuild()) {
					this.rebuild();
				}
			}
		}
	}
	
	private synchronized void updateStatus(String status) {
		log.info(status);
		for(StatusListener listener: statusListener) {
			listener.statusUpdate(status);
		}
	}
}
