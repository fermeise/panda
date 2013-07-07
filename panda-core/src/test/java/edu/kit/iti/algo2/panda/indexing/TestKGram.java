package edu.kit.iti.algo2.panda.indexing;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.*;

import edu.kit.iti.algo2.panda.indexing.kgrams.KGramIndex;
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
		assertThat(index.getWords(), contains("hello")); 
		assertThat(index.getKeys(), contains("hello"));
	}
	
	@Test
	public void testOtherK() {
		List<List<String>> expectedKeys = new ArrayList<>();
		expectedKeys.add(Arrays.asList("t", "e", "s"));
		expectedKeys.add(Arrays.asList("te", "es", "st"));
		expectedKeys.add(Arrays.asList("tes", "est"));
		expectedKeys.add(Arrays.asList("test"));
		
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
		assertThat(index.getWords(), contains("test", "guest"));
		assertThat(index.getKeys(), containsInAnyOrder("tes", "est", "gue", "ues"));
	}
	
	@Test
	@Ignore
	// Performance test on the Wikipedia data set.
	public void testWikipedia() throws IOException {
		InvertedIndex documents = new InvertedIndex();
		for (WikipediaArticle article : WikipediaArticle.loadArticles()) {
			documents.addDocument(article);
		}
		
		long before = System.currentTimeMillis();
		KGramIndex index = new KGramIndex(3);
		index.addAllWords(documents.getWords());
		long timeTaken = System.currentTimeMillis() - before;
		System.out.format("Wikipedia k-gram index finished in %s ms.", timeTaken);
	}

}
