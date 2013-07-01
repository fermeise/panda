
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.indexing.QueryProcessor;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;
import edu.kit.iti.algo2.panda.indexing.WikipediaArticle;

public class QueryWikipedia {
	public static void main(String[] args) throws IOException {
		System.out.println("Loading documents...");
		ArrayList<WikipediaArticle> entries = WikipediaArticle.loadArticles();
		
		InvertedIndex documents;
		
		File indexFile = new File("../wikipedia-sentences.pnd");
		if(indexFile.exists()) {
			System.out.println("Loading index...");
			documents = InvertedIndex.loadFromFile(indexFile);
		} else {
			System.out.println("Building index...");
			documents = new InvertedIndex();
			for(WikipediaArticle entry: entries) {
				documents.addDocument(entry);
			}
			documents.finish();
			
			System.out.println("Saving index...");
			documents.saveToFile(indexFile);
		}
		
		System.out.println("Ready.");
		System.out.println();
		
		QueryProcessor queryProcessor = new QueryProcessor(documents);
		Scanner scanner = new Scanner(System.in);
		
		String query = scanner.nextLine();
		while(query != "") {
			List<ScoredDocument> result = queryProcessor.query(query);
			for(int i = 0; i < 10 && i < result.size(); i++) {
				System.out.println((i + 1) + ". " + entries.get(result.get(i).getId()).getTitle()
						+ " (" + result.get(i).getScore() + ")");
			}
			System.out.println("");
			query = scanner.nextLine();
		}
		scanner.close();
	}
}