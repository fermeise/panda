
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import edu.kit.iti.algo2.panda.indexing.Document;
import edu.kit.iti.algo2.panda.indexing.Query;
import edu.kit.iti.algo2.panda.management.IndexFacade;
import edu.kit.iti.algo2.panda.management.IndexManager;

public class QueryFiles {
	public static void main(String[] args) throws IOException {
		final IndexFacade facade = new IndexManager("../library");
		facade.addDirectory(Paths.get("d:/Development/Java/documents/"));
		
		Scanner scanner = new Scanner(System.in);
		
		String queryString = scanner.nextLine();
		while(queryString != "") {
			Query query = new Query(queryString);
			List<Document> result = facade.query(query, 10);
			for(int i = 0; i < result.size(); i++) {
				Document document = result.get(i);
				System.out.println((i + 1) + ". " + document.getFile().toString());
				System.out.println(facade.extractSnippet(document, query, 100));
			}
			System.out.println("");
			queryString = scanner.nextLine();
		}
		scanner.close();
	}
}
