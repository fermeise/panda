package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryProcessor {
	private DocumentIndex index;
	
	public QueryProcessor(DocumentIndex index) {
		this.index = index;
	}

	public List<ScoredDocument> query(String string) {
		Scanner scanner = new Scanner(string);
		scanner.useDelimiter(" ");
		if(!scanner.hasNext()) {
			scanner.close();
			return new ArrayList<ScoredDocument>();
		}
		InvertedList query = index.queryWord(scanner.next());
		while(scanner.hasNext()) {
			query = InvertedList.intersect(query, index.queryWord(scanner.next()));
		}
		scanner.close();
		return query.asList();
	}
	
}
