
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.indexing.Query;
import edu.kit.iti.algo2.panda.indexing.QueryProcessor;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;
import edu.kit.iti.algo2.panda.indexing.mock.WikipediaArticle;

public class QueryWikipedia {
	public static void main(String[] args) throws IOException {
		System.out.println("Loading documents...");
		ArrayList<WikipediaArticle> entries = WikipediaArticle.loadArticles();
		
		InvertedIndex documents;
		
		File indexFile = new File("../wikipedia-sentences.pnd");
		System.out.println("Loading index...");
		try {
			documents = InvertedIndex.loadFromFile(indexFile);
		} catch (ParseException e) {
			System.out.println("Building index...");
			documents = new InvertedIndex();
			for(WikipediaArticle entry: entries) {
				documents.addDocument(entry);
			}
			documents.initialScoring();
			
			System.out.println("Saving index...");
			documents.saveToFile(indexFile);
		}
		
		System.out.println("Ready.");
		System.out.println();
		
		QueryProcessor queryProcessor = new QueryProcessor(documents);
		Scanner scanner = new Scanner(System.in);
		
		String queryString = scanner.nextLine();
		while(queryString != "") {
			Query query = new Query(queryString);
			List<ScoredDocument> result = queryProcessor.query(query, 50);
			for(int i = 0; i < 10 && i < result.size(); i++) {
				Document document = entries.get(result.get(i).getId());
				
				System.out.println((i + 1) + ". " + document.getTitle()
						+ " (" + result.get(i).getScore() + ")");
				System.out.println(queryProcessor.extractSnippet(document, query, 100));
			}
			System.out.println("");
			queryString = scanner.nextLine();
		}
		scanner.close();
	}
}
