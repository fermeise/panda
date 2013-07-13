package edu.kit.iti.algo2.panda.gui.model;

import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.Query;
import edu.kit.iti.algo2.panda.management.IndexFacade;
import edu.kit.iti.algo2.panda.management.StatusListener;

public class QueryModel extends AbstractListModel<String> {
	private static final long serialVersionUID = -968307538266585151L;
	private static final int numberOfResults = 50;

	private final IndexFacade index;
	
	private Query query;
	private List<Document> result;
	
	public QueryModel(IndexFacade index) {
		this.index = index;
		
		this.query = null;
		this.result = Collections.emptyList();
	}
	
	public void setQuery(String queryString) {
		Query query = new Query(queryString);
		List<Document> oldResult = this.result;
		List<Document> newResult = index.query(query, numberOfResults);
		if (!oldResult.isEmpty()) {
			this.fireIntervalRemoved(this, 0, oldResult.size()-1);
		}
		this.result = newResult;
		this.query = query;
		if (!newResult.isEmpty()) {
			this.fireIntervalAdded(this, 0, newResult.size()-1);
		}
	}

	@Override
	public int getSize() {
		return this.result.size();
	}

	@Override
	public String getElementAt(int idx) {
		Document document = result.get(idx);
		return document.getTitle() + ": " + index.extractSnippet(document, query, 100);
	}

	public void close() {
		index.close();
	}

	public void addStatusListener(StatusListener statusListener) {
		index.addStatusListener(statusListener);
	}
}
