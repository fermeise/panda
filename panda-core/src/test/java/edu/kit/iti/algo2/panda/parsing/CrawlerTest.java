package edu.kit.iti.algo2.panda.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import edu.kit.iti.algo2.panda.indexing.DocumentIndex;
import edu.kit.iti.algo2.panda.indexing.QueryProcessor;
import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;

public class CrawlerTest {
	
	private static final Path SEARCH_DIR = Paths.get("src/main/java");
	private static final Path PREFIX = SEARCH_DIR.resolve("edu/kit/iti/algo2/panda/indexing/");

	@Test
	public void test() throws IOException {
		Logger.getGlobal().setLevel(Level.ALL);
		DocumentIndex index = new InvertedIndex();
		FilesystemCrawler crawler = new FilesystemCrawler(index);
		crawler.crawl(SEARCH_DIR);
		QueryProcessor processor = new QueryProcessor(index);
		List<ScoredDocument> queryResult = processor.query("int Document");
		
		List<Path> actual = crawler.getDocuments(queryResult);
		System.out.println(actual);
		assertEquals(actual.size(), 2);
		assertThat(actual, hasItem(PREFIX.resolve("InvertedIndex.java")));
		assertThat(actual, hasItem(PREFIX.resolve("DocumentIndex.java")));
	}

}
