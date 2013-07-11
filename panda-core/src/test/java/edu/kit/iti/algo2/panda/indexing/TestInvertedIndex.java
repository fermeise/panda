package edu.kit.iti.algo2.panda.indexing;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.kit.iti.algo2.panda.indexing.mock.WikipediaArticle;
import edu.kit.iti.algo2.panda.parsing.GenericDocument;

public class TestInvertedIndex {
	
	public Document karlsruhe = new GenericDocument(null, "Karlsruhe", "The University of Karlsruhe, " +
			"also known as Fridericiana, was founded in 1825. It is located in the city of Karlsruhe, " +
			"Germany, and is one of the most prestigious technical universities in Germany");
	public Document freiburg = new GenericDocument(null, "Freiburg", "The University of Freiburg (German " +
			"Albert-Ludwigs-Universität Freiburg, colloquially Uni Freiburg), sometimes referred to " +
			"with its full title, the Albert Ludwig University of Freiburg, is a public research university " +
			"located in Freiburg im Breisgau, Baden-Württemberg, Germany.");
	public Document lmu = new GenericDocument(null, "LMU", "The Ludwig Maximilian University of Munich " +
			"(German: Ludwig-Maximilians-Universität München), commonly known as the " +
			"University of Munich or LMU, is a university in Munich, Germany. A public " +
			"research university, it is among Germany's oldest universities.");
	public Document kit = new GenericDocument(null, "KIT", "The Karlsruhe Institute of Technology (KIT) " +
			"is one of the largest and most prestigious research and education institutions in Germany.");
	
	@Test
	public void testEmpty() throws IOException {
		InvertedIndex index = new InvertedIndex();
		
		assertEquals(0, index.getDocumentCount());
		assertEquals(Collections.EMPTY_SET, index.getWords());
	}
	
	@Test
	public void testQuery() throws IOException {
		InvertedIndex index = new InvertedIndex();
		
		index.addDocument(karlsruhe);
		index.addDocument(freiburg);
		index.addDocument(lmu);
		index.initialScoring();
		
		assertEquals(3, index.getDocumentCount());
		assertThat(getDocumentIds(index.queryWord("university")), containsInAnyOrder(0, 1, 2));
		assertThat(getDocumentIds(index.queryWord("research")), containsInAnyOrder(1, 2));
		assertThat(getDocumentIds(index.queryWord("karlsruhe")), containsInAnyOrder(0));
	}
	
	@Test
	public void testChange() throws IOException {
		InvertedIndex index = new InvertedIndex();
		
		assertEquals(0, index.addDocument(karlsruhe));
		assertEquals(1, index.addDocument(freiburg));
		assertEquals(2, index.addDocument(lmu));
		index.initialScoring();
		
		assertEquals(3, index.getDocumentCount());
		assertEquals(3, index.getMaxDocumentId());
		assertThat(getDocumentIds(index.queryWord("university")), containsInAnyOrder(0, 1, 2));
		assertThat(getDocumentIds(index.queryWord("research")), containsInAnyOrder(1, 2));
		assertThat(getDocumentIds(index.queryWord("karlsruhe")), containsInAnyOrder(0));
		
		index.removeDocument(0);
		
		assertEquals(2, index.getDocumentCount());
		assertEquals(3, index.getMaxDocumentId());
		assertThat(getDocumentIds(index.queryWord("university")), containsInAnyOrder(1, 2));
		assertThat(getDocumentIds(index.queryWord("research")), containsInAnyOrder(1, 2));
		assertThat(getDocumentIds(index.queryWord("karlsruhe")), empty());
		
		assertEquals(3, index.addDocument(kit));
		
		assertEquals(3, index.getDocumentCount());
		assertEquals(4, index.getMaxDocumentId());
		assertThat(getDocumentIds(index.queryWord("university")), containsInAnyOrder(1, 2));
		assertThat(getDocumentIds(index.queryWord("research")), containsInAnyOrder(1, 2, 3));
		assertThat(getDocumentIds(index.queryWord("karlsruhe")), containsInAnyOrder(3));
	}
	
	@Test
	@Ignore
	public void testWikipedia() throws IOException {
		ArrayList<WikipediaArticle> entries = WikipediaArticle.loadArticles();
		
		InvertedIndex documents = new InvertedIndex();
		for(WikipediaArticle entry: entries) {
			documents.addDocument(entry);
		}
		documents.initialScoring();
		
		QueryProcessor queryProcessor = new QueryProcessor(documents);
		List<ScoredDocument> query = queryProcessor.query("Schrödinger cat", 100);
		for(ScoredDocument doc: query) {
			System.out.println(doc.getScore() + " " + entries.get(doc.getId()).getTitle());
		}
		assertEquals(8, query.size());
		System.out.println("");
		
		query = queryProcessor.query("relativity theory", 100);
		for(ScoredDocument doc: query) {
			System.out.println(doc.getScore() + " " + entries.get(doc.getId()).getTitle());
		}
	}
	
	public static List<Integer> getDocumentIds(InvertedList list) {
		List<ScoredDocument> documents = list.rankResults(Integer.MAX_VALUE);
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(ScoredDocument doc: documents) {
			result.add(doc.getId());
		}
		return result;
	}
}
