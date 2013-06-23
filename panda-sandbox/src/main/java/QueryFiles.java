
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.indexing.QueryProcessor;
import edu.kit.iti.algo2.panda.indexing.ScoredDocument;
import edu.kit.iti.algo2.panda.parsing.FileSystemCrawler;

public class QueryFiles {
	public static void main(String[] args) throws IOException {
		System.out.println("Loading documents...");
		FileSystemCrawler crawler = new FileSystemCrawler();
		crawler.crawl(Paths.get("d:/Studium/KIT/Sonstiges/Information Retrieval/"));
		InvertedIndex index = new InvertedIndex();
		crawler.index(index);
		index.finish();
		
		System.out.println("Ready.");
		System.out.println();
		
		QueryProcessor queryProcessor = new QueryProcessor(index);
		Scanner scanner = new Scanner(System.in);
		
		String query = scanner.nextLine();
		while(query != "") {
			List<ScoredDocument> result = queryProcessor.query(query);
			for(int i = 0; i < 10 && i < result.size(); i++) {
				System.out.println((i + 1) + ". " + crawler.getDocuments().get(result.get(i).getId()).getFile().toString()
						+ " (" + result.get(i).getScore() + ")");
			}
			System.out.println("");
			query = scanner.nextLine();
		}
		scanner.close();
	}
}
