package edu.kit.iti.algo2.panda.gui.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractListModel;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.Query;
import edu.kit.iti.algo2.panda.management.IndexFacade;
import edu.kit.iti.algo2.panda.management.StatusListener;

public class QueryModel extends AbstractListModel<String> {
	private static final long serialVersionUID = -968307538266585151L;
	private static final int numberOfResults = 50;
	private static final int snippetLength = 300;

	private final IndexFacade index;
	private final HashMap<String, Path> fileViewer;
	
	private List<Document> documents;
	private List<String> snippets;
	
	public QueryModel(IndexFacade index, HashMap<String, Path> fileViewer) {
		this.index = index;
		this.fileViewer = fileViewer;
		
		this.documents = Collections.emptyList();
		this.snippets = Collections.emptyList();
	}
	
	public void setQuery(String queryString) {
		final Query query = new Query(queryString);
		final List<Document> newDocuments = index.query(query, numberOfResults);
		final List<String> newSnippets = new ArrayList<String>();
		for(Document document: newDocuments) {
			newSnippets.add(index.extractSnippet(document, query, snippetLength, true));
		}
		
		synchronized(this) {
			int oldSize = documents.size();
			
			this.documents = newDocuments;
			this.snippets = newSnippets;
			
			if (oldSize > 0) {
				this.fireIntervalRemoved(this, 0, oldSize - 1);
			}
			if (!newDocuments.isEmpty()) {
				this.fireIntervalAdded(this, 0, newDocuments.size()-1);
			}
		}
	}

	@Override
	public synchronized int getSize() {
		return this.documents.size();
	}

	@Override
	public synchronized String getElementAt(int idx) {
		return "<html><h3>" + documents.get(idx).getTitle() + "</h3><p>" + snippets.get(idx) + "</p></html>";
	}

	public void close() {
		index.close();
	}

	public void addStatusListener(StatusListener statusListener) {
		index.addStatusListener(statusListener);
	}

	public synchronized void viewDocument(int documentIndex) {
		if(documentIndex >= 0 && documentIndex <= documents.size()) {
			Document document = documents.get(documentIndex);
			String filename = document.getFile().getFileName().toString();
			String extension = filename.substring(filename.lastIndexOf('.') + 1);
			Path viewer = fileViewer.get(extension);
			if(viewer != null) {
				try {
					new ProcessBuilder(viewer.toString(), document.getFile().toString()).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
