package edu.kit.iti.algo2.panda.indexing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestInvertedIndex {
	ArrayList<WikipediaEntry> entries;
	
	@Before
	public void setUp() throws IOException {
		entries = WikipediaEntry.loadSentences();
	}
	
	@Test
	public void test() {
		SimpleInvertedIndex documents = new SimpleInvertedIndex();
		
		for(WikipediaEntry entry: entries) {
			documents.addDocument(entry);
		}
		
		QueryProcessor queryProcessor = new QueryProcessor(documents);
		
		List<Integer> query = queryProcessor.queryTwoWords("Schrödinger", "cat");
		assertEquals(12, query.size());
		for(Integer documentId: query) {
			System.out.println(entries.get(documentId));
		}
	}
}
