
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.indexing.mock.WikipediaArticle;

public class TestPerformance {
	public static void main(String[] args) throws IOException {
		long begin, end;
		
		begin = System.currentTimeMillis();
		ArrayList<WikipediaArticle> articles = WikipediaArticle.loadArticles();
		end = System.currentTimeMillis();
		System.out.println("Reading articles: " + (end - begin) + " ms");
		
		// Panda
		begin = System.currentTimeMillis();
		InvertedIndex documents = new InvertedIndex();
		for(WikipediaArticle article: articles) {
			documents.addDocument(article);
		}
		documents.initialScoring();
		end = System.currentTimeMillis();
		System.out.println("Panda: " + (end - begin) + " ms");
		
		// Lucene
		begin = System.currentTimeMillis();
		RAMDirectory dir = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_43, analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);
		for(WikipediaArticle article: articles) {
			org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
			doc.add(new StringField("title", article.getTitle(), Field.Store.NO));
			doc.add(new TextField("content", article.getContent(), Field.Store.YES));
			writer.addDocument(doc);
		}
		writer.close();
		end = System.currentTimeMillis();
		System.out.println("Lucene: " + (end - begin) + " ms");
	}
}
