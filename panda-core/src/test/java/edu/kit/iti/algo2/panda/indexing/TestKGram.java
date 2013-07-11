package edu.kit.iti.algo2.panda.indexing;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.kit.iti.algo2.panda.indexing.kgrams.KGramIndex;
import edu.kit.iti.algo2.panda.indexing.kgrams.LevinshteinDistance;
import edu.kit.iti.algo2.panda.indexing.mock.WikipediaArticle;

public class TestKGram {
	
	@Test
	public void testEmptyWord() {
		KGramIndex index = new KGramIndex(3);
		index.addWord("");
		assertThat(index.getWords(), empty());
	}
	
	@Test
	public void testSingleWord() {
		KGramIndex index = new KGramIndex(5);
		index.addWord("hello");
		String[] expected = new String[] { "h", "he", "hel", "hell", "hello", "ello", "llo", "lo", "o" };
		assertThat(index.getWords(), contains("hello")); 
		assertThat(index.getKeys(), containsInAnyOrder(expected));
	}
	
	@Test
	public void testOtherK() {
		List<List<String>> expectedKeys = new ArrayList<>();
		expectedKeys.add(Arrays.asList("t", "e", "s"));
		expectedKeys.add(Arrays.asList("t", "te", "es", "st"));
		expectedKeys.add(Arrays.asList("t", "te", "tes", "est", "st"));
		expectedKeys.add(Arrays.asList("t", "te", "tes", "test", "est", "st"));
		
		for (int i=0; i < expectedKeys.size(); i++) {
			KGramIndex index = new KGramIndex(i+1);
			index.addWord("test");
			assertThat(index.getWords(), contains("test"));
			assertThat(index.getKeys(), containsInAnyOrder(expectedKeys.get(i).toArray()));
		}
	}
	
	@Test
	public void testIndexing() {
		KGramIndex index = new KGramIndex(3);
		index.addWord("test");
		index.addWord("guest");
		String[] expected = new String[] { "t", "te", "tes", "est", "st", "g", "gu", "gue", "ues" };
		assertThat(index.getWords(), contains("test", "guest"));
		assertThat(index.getKeys(), containsInAnyOrder(expected));
	}
	
	@Test
	public void testEmptyQuery() {
		KGramIndex index = new KGramIndex(3);
		index.addWord("");
		index.addWord("retired");
		assertThat(index.fuzzySearch("", 0), empty());
		assertThat(index.fuzzySearch("", 1), empty());
		assertThat(index.fuzzySearch("", 3), empty());
	}

	@Test
	public void testQueryWithSingleWord() {
		KGramIndex index = new KGramIndex(3);
		index.addWord("retired");
		assertThat(index.fuzzySearch("red", 0), empty());
		assertThat(index.fuzzySearch("red", 1), empty());
		assertThat(index.fuzzySearch("red", 2), empty());
		assertThat(index.fuzzySearch("red", 3), empty());
		assertThat(index.fuzzySearch("red", 4), containsInAnyOrder("retired"));
	}
	
	@Test
	public void testQueryWithMultipleWords() {
		KGramIndex index = new KGramIndex(3);
		index.addWord("retired");
		index.addWord("tired");
		index.addWord("tire");
		assertThat(index.fuzzySearch("fire", 0), empty());
		assertThat(index.fuzzySearch("fire", 1), containsInAnyOrder("tire"));
		assertThat(index.fuzzySearch("fire", 2), containsInAnyOrder("tire", "tired"));
		assertThat(index.fuzzySearch("fire", 3), containsInAnyOrder("tire", "tired"));
		assertThat(index.fuzzySearch("fire", 4), containsInAnyOrder("retired", "tire", "tired"));
	}
	
	@Test
	@Ignore
	// Performance test on the Wikipedia data set.
	public void testWikipedia() throws IOException {
		final int iterations = 10;
		
		InvertedIndex documents = new InvertedIndex();
		for (WikipediaArticle article : WikipediaArticle.loadArticles()) {
			documents.addDocument(article);
		}

		double averageTime = 0.0;
		for (int i=0; i < iterations; i++) {
			long before = System.currentTimeMillis();
			KGramIndex index = new KGramIndex(3);
			index.addAllWords(documents.getWords());
			long timeTaken = System.currentTimeMillis() - before;
			averageTime += timeTaken / (double)iterations;
		}
		System.out.format("Wikipedia k-gram index average finishing time: %s ms.%n", averageTime);
	}
	
	@Test
	@Ignore
	// Another performance test on the Wikipedia data set.
	public void testSearchPerformance() throws IOException {
		final String query = "class";
		final int editDistance = 5;
		
		InvertedIndex documents = new InvertedIndex();
		for (WikipediaArticle article : WikipediaArticle.loadArticles()) {
			documents.addDocument(article);
		}
		KGramIndex index = new KGramIndex(3);
		index.addAllWords(documents.getWords());
		
		long before = System.currentTimeMillis();
		List<String> result = null;
		for(int i = 0; i < 1000; i++) {
			result = index.fuzzySearch(query, editDistance);
		}
		long timeTaken = System.currentTimeMillis() - before;
		
		System.out.format("Search for '%s' with %s results took %s ms.%n",
				query, result.size(), timeTaken);
	}
	
	@Test
	public void testCorrect() throws IOException {
		final String query = "class";
		final int editDistance = 4;
		
		InvertedIndex documents = new InvertedIndex();
		for (WikipediaArticle article : WikipediaArticle.loadArticles()) {
			documents.addDocument(article);
		}
		KGramIndex index = new KGramIndex(3);
		index.addAllWords(documents.getWords());
		
		HashSet<String> actual = new HashSet<String>(index.fuzzySearch(query, editDistance));
		
		HashSet<String> expected = new HashSet<String>();
		for(String word: documents.getWords()) {
			if(LevinshteinDistance.distance(query.toCharArray(), word.toCharArray()) <= editDistance) {
				if(!actual.contains(word)) {
					fail("Fuzzy search does not return '" + word + "'.");
				}
				expected.add(word);
			}
		}
		
		for(String word: actual) {
			if(!expected.contains(word)) {
				fail("Fuzzy search returns '" + word + "' but should not.");
			}
		}
	}
}
