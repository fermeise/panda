package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.List;

public class QueryProcessor {
	private DocumentIndex index;
	
	public QueryProcessor(DocumentIndex index) {
		this.index = index;
	}
	
	public List<Integer> queryTwoWords(String wordA, String wordB) {
		return intersect(index.queryWord(wordA), index.queryWord(wordB));
	}
	
	public List<Integer> intersect(List<Integer> listA, List<Integer> listB) {
		ArrayList<Integer> list = new ArrayList<Integer>(listA);
		list.retainAll(listB);
		return list;
	}
	
}
