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

	public String extractSnippet(Document document, String query) {
		String content = document.getContent();
		Scanner scanner = new Scanner(query);
		scanner.useDelimiter(" ");
		if(scanner.hasNext()) {
			String word = scanner.next();
			WordIterator it = new WordIterator(content);
			while(it.hasNext()) {
				WordOccurrence occurence = it.next();
				if(occurence.getWord().equals(WordIterator.normalizeWord(word))) {
					String snippet = content.substring(Math.max(0, occurence.getBegin() - 70), Math.min(content.length(), occurence.getBegin() + 70));
					scanner.close();
					return snippet.replace("\n", " ");
				}
			}
		}
		
		scanner.close();
		return "";
	}  
}
