package edu.kit.iti.algo2.panda.indexing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.kit.iti.algo2.panda.indexing.kgrams.LevenshteinDistance;
import edu.kit.iti.algo2.panda.indexing.mock.WikipediaArticle;

public class TestLevenshteinDistance {
	
	private static final String TRUE_MESSAGE = "Distance between '%s' and '%s' is <= %d.";
	private static final String FALSE_MESSAGE = "Distance between '%s' and '%s' is not <= %d.";
	
	private void testWithComputedDistance(String a, String b, int distance) {
		String msg = "";
		char[] s = a.toCharArray(), t = b.toCharArray();
		
		int actual = LevenshteinDistance.distance(s, t);
		msg = String.format(TRUE_MESSAGE, a, b, distance);
		assertEquals(msg,distance, actual);
		
		msg = String.format(TRUE_MESSAGE, a, b, distance);
		assertTrue(msg, LevenshteinDistance.smallerOrEqualThan(s, t, distance));
		msg = String.format(FALSE_MESSAGE, a, b, distance-1);
		assertFalse(msg, LevenshteinDistance.smallerOrEqualThan(s, t, distance-1));

		msg = String.format(TRUE_MESSAGE, b, a, distance);
		assertTrue(LevenshteinDistance.smallerOrEqualThan(t, s, distance));
		msg = String.format(FALSE_MESSAGE, b, a, distance-1);
		assertFalse(LevenshteinDistance.smallerOrEqualThan(t, s, distance-1));
	}
	
	@Test
	public void testEmpty() {
		testWithComputedDistance("", "", 0);
		testWithComputedDistance("", "a", 1);
		testWithComputedDistance("", "abc", 3);
	}
	
	@Test
	public void testEqual() {
		testWithComputedDistance("abc", "abc", 0);
		testWithComputedDistance("test", "test", 0);
		
	}
	
	@Test
	public void testDeletion() {
		testWithComputedDistance("ab", "abc", 1);
		testWithComputedDistance("ac", "abc", 1);
		testWithComputedDistance("bc", "abc", 1);
	}
	

	@Test
	public void testInsertion() {
		testWithComputedDistance("adbc", "abc", 1);
		testWithComputedDistance("abec", "abc", 1);
		testWithComputedDistance("abcde", "abc", 2);
	}
	
	@Test
	public void testReplacement() {
		testWithComputedDistance("abc", "abe", 1);
		testWithComputedDistance("abc", "cba", 2);
		testWithComputedDistance("abc", "fche", 4);
	}
	
	@Test
	public void testRealWords() {
		testWithComputedDistance("red", "retired", 4);
		testWithComputedDistance("class", "cold", 4);
		testWithComputedDistance("bread", "bed", 2);
	}
	
	private char[][] loadRandomWordsFromWikipedia(int maxWords) throws IOException {
		List<WikipediaArticle> articles = WikipediaArticle.loadArticles();
		InvertedIndex index = new InvertedIndex();
		for (WikipediaArticle article : articles) {
			index.addDocument(article);
		}
		
		char[][] words = new char[maxWords][];
		List<String> wordList = new ArrayList<>(index.getWords());
		Collections.shuffle(wordList);
		Iterator<String> wordIterator = wordList.iterator();
		for (int i=0; i < words.length && wordIterator.hasNext(); i++) {
			words[i] = wordIterator.next().toCharArray();
		}
		return words;
	}
	
	@Test
	@Ignore
	// Brute force correctness test for partial Levenshtein distance.
	public void testPartialCorrectness() throws IOException {
		char[][] words = loadRandomWordsFromWikipedia(1000);
		System.out.format("Finished reading random word list of size %d.%n", words.length);
		long timeTaken = 0;
		long iterations = 0;
		for (int i=0; i < words.length; i++) {
			char[] s = words[i];
			for (int j=i; j < words.length; j++) {
				char[] t = words[j];
				int actualDistance = LevenshteinDistance.distance(s, t);
				long before = System.currentTimeMillis();
				assertTrue(LevenshteinDistance.smallerOrEqualThan(s, t, actualDistance));
				timeTaken += System.currentTimeMillis() - before;
				iterations++;
			}
		}
		String msg = "Partial distance computation took %d ms for %d iterations.%n";
		System.out.format(msg, timeTaken, iterations);
	}
}
