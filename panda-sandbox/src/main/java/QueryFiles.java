
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.indexing.QueryProcessor;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;
import edu.kit.iti.algo2.panda.parsing.DocumentFactory;
import edu.kit.iti.algo2.panda.parsing.FileSystemCrawler;

public class QueryFiles {
	public static void main(String[] args) throws IOException {
		final DocumentFactory factory = new DocumentFactory("../library.db");
		final InvertedIndex index;
		
		File indexFile = new File("../library.pnd");
		if(indexFile.exists()) {
			System.out.println("Loading index...");
			index = InvertedIndex.loadFromFile(indexFile);
		} else {
			System.out.println("Building index...");
			index = new InvertedIndex();
			FileSystemCrawler crawler = new FileSystemCrawler(factory, index);
			crawler.crawl(Paths.get("d:/Studium/KIT/"));
			index.finish();
			
			System.out.println("Saving index...");
			index.saveToFile(indexFile);
		}
		
		System.out.println("Ready.");
		System.out.println();
		
		QueryProcessor queryProcessor = new QueryProcessor(index);
		Scanner scanner = new Scanner(System.in);
		
		String query = scanner.nextLine();
		while(query != "") {
			List<ScoredDocument> result = queryProcessor.query(query);
			for(int i = 0; i < 10 && i < result.size(); i++) {
				Document document = factory.restoreDocument(result.get(i).getId());
				
				System.out.println((i + 1) + ". " + document.getFile().toString()
						+ " (" + result.get(i).getScore() + ")");
				System.out.println(queryProcessor.extractSnippet(document, query));
			}
			System.out.println("");
			query = scanner.nextLine();
		}
		scanner.close();
	}
}
