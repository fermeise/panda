
import java.io.IOException;
import java.util.ArrayList;

import edu.kit.iti.algo2.panda.indexing.mock.WikipediaArticle;
import edu.kit.iti.algo2.panda.parsing.SQLiteDocumentStorage;

public class TestSQLite {
	public static void main(String[] args) throws IOException {
		long begin, end;
		
		begin = System.currentTimeMillis();
		ArrayList<WikipediaArticle> articles = WikipediaArticle.loadArticles();
		end = System.currentTimeMillis();
		System.out.println("Reading articles from csv: " + (end - begin) + " ms");
		
		SQLiteDocumentStorage storage = new SQLiteDocumentStorage("../test.db");
		begin = System.currentTimeMillis();
		int id = 0;
		for(WikipediaArticle article: articles) {
			storage.addDocument(id++, article);
		}
		storage.commitChanges();
		end = System.currentTimeMillis();
		System.out.println("Storing articles in db: " + (end - begin) + " ms");
		
		begin = System.currentTimeMillis();
		id = 0;
		for(int i = 0; i < articles.size(); i++) {
			storage.restoreDocument(id++, true);
		}
		end = System.currentTimeMillis();
		System.out.println("Reading articles from db: " + (end - begin) + " ms");
	}
}
