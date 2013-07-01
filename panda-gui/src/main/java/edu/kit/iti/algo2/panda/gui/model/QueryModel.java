package edu.kit.iti.algo2.panda.gui.model;

import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.indexing.QueryProcessor;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;
import edu.kit.iti.algo2.panda.parsing.DocumentFactory;

public class QueryModel extends AbstractListModel<String> {
	private static final long serialVersionUID = 1L;
	
	private final DocumentFactory documentFactory;
	private final QueryProcessor processor;
	
	private List<ScoredDocument> result = Collections.emptyList();
	
	public QueryModel(DocumentFactory factory, InvertedIndex index) {
		this.documentFactory = factory;
		this.processor = new QueryProcessor(index);
	}
	
	public void setQuery(String queryString) {
		this.fireIntervalRemoved(this, 0, result.size());
		this.result = processor.query(queryString);
		this.fireIntervalAdded(this, 0, result.size());
	}

	@Override
	public int getSize() {
		return this.result.size();
	}

	@Override
	public String getElementAt(int index) {
		Document document = documentFactory.restoreDocument(result.get(index).getId());
		return document.getTitle();
	}

}
