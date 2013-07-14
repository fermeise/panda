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
	public static int numberOfResults = 20;
	public static int snippetLength = 300;

	private final IndexFacade index;
	private final HashMap<String, Path> fileViewer;
	
	private Query suggestion;
	private List<Document> documents;
	private List<String> results;
	
	public QueryModel(IndexFacade index, HashMap<String, Path> fileViewer) {
		this.index = index;
		this.fileViewer = fileViewer;
		
		this.suggestion = null;
		this.documents = Collections.emptyList();
		this.results = Collections.emptyList();
	}
	
	public void setQuery(String queryString) {
		final Query query = new Query(queryString);
		
		final Query newSuggestion = index.getSuggestion(query);
		final List<Document> newDocuments = index.query(query, numberOfResults);
		final List<String> newResults = new ArrayList<String>();
		if(newSuggestion != null) {
			newResults.add("<html><font color=red>Did you mean: </font>" + newSuggestion.toString() + "</html>");
		}
		for(Document document: newDocuments) {
			newResults.add("<html><h3>" + document.getTitle() + "</h3><p>" +
					index.extractSnippet(document, query, snippetLength, true) + "</p></html>");
		}
		
		int oldSize = results.size();
		int newSize = newResults.size();
		
		synchronized(this) {
			this.suggestion = newSuggestion;
			this.documents = newDocuments;
			this.results = newResults;
		}
			
		if (oldSize > 0) {
			this.fireIntervalRemoved(this, 0, oldSize - 1);
		}
		if (newSize > 0) {
			this.fireIntervalAdded(this, 0, newSize - 1);
		}
	}

	@Override
	public synchronized int getSize() {
		return results.size();
	}

	@Override
	public synchronized String getElementAt(int idx) {
		if(idx >= getSize()) {
			// Somehow the list model sometimes doesn't get length changes
			return null;
		}
		return results.get(idx);
	}

	public void close() {
		index.close();
	}
	
	public void rebuildIndex() {
		index.rebuild();
	}

	public void addStatusListener(StatusListener statusListener) {
		index.addStatusListener(statusListener);
	}

	public synchronized String clickItem(int itemIndex) {
		int documentIndex = (suggestion != null) ? itemIndex - 1 : itemIndex;
		if(suggestion != null && itemIndex == 0) {
			return suggestion.toString();
		}
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
		
		return null;
	}
}
