package edu.kit.iti.algo2.panda.indexing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestInvertedIndex {
	
	@Test
	public void testInvertedIndex() throws IOException {
		ArrayList<WikipediaArticle> entries = WikipediaArticle.loadArticles();
		
		InvertedIndex documents = new InvertedIndex();
		for(WikipediaArticle entry: entries) {
			documents.addDocument(entry);
		}
		documents.finish();
		
		QueryProcessor queryProcessor = new QueryProcessor(documents);
		List<ScoredDocument> query = queryProcessor.query("Schrödinger cat");
		for(ScoredDocument doc: query) {
			System.out.println(doc.getScore() + " " + entries.get(doc.getId()).getTitle());
		}
		assertEquals(8, query.size());
		System.out.println("");
		
		query = queryProcessor.query("relativity theory");
		for(ScoredDocument doc: query) {
			System.out.println(doc.getScore() + " " + entries.get(doc.getId()).getTitle());
		}
	}
}
