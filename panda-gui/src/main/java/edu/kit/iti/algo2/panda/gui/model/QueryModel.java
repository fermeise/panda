package edu.kit.iti.algo2.panda.gui.model;

import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.DocumentIndex;
import edu.kit.iti.algo2.panda.indexing.QueryProcessor;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;
import edu.kit.iti.algo2.panda.management.IndexManager;
import edu.kit.iti.algo2.panda.parsing.DocumentStorage;

public class QueryModel extends AbstractListModel<String> {
	private static final long serialVersionUID = -968307538266585151L;
	private static final int numberOfResults = 50;

	private final DocumentStorage documentStorage;
	private final DocumentIndex documentIndex;
	private final QueryProcessor processor;
	
	private String query;
	private List<ScoredDocument> result;
	
	public QueryModel(IndexManager manager) {
		this.documentStorage = manager.getDocumentStorage();
		this.documentIndex = manager.getDocumentIndex();
		this.processor = new QueryProcessor(documentIndex);
		
		this.query = "";
		this.result = Collections.emptyList();
	}
	
	public void setQuery(String queryString) {
		List<ScoredDocument> oldResult = this.result;
		List<ScoredDocument> newResult = processor.query(queryString, numberOfResults);
		if (!oldResult.isEmpty()) {
			this.fireIntervalRemoved(this, 0, oldResult.size()-1);
		}
		this.result = newResult;
		this.query = queryString;
		if (!newResult.isEmpty()) {
			this.fireIntervalAdded(this, 0, newResult.size()-1);
		}
	}

	@Override
	public int getSize() {
		return this.result.size();
	}

	@Override
	public String getElementAt(int index) {
		Document document = documentStorage.restoreDocument(result.get(index).getId());
		return document.getTitle() + ": " + processor.extractSnippet(document, documentIndex, query, 100);
	}

}
