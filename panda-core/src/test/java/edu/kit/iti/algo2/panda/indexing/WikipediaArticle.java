package edu.kit.iti.algo2.panda.indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class WikipediaArticle implements Document {
	private static final String sentenceFile = "../wikipedia-sentences.csv";
	private String article;
	private String content;
	
	public WikipediaArticle(String article, String content) {
		this.article = article;
		this.content = content;
	}
	
	@Override
	public File getFile() {
		return new File(sentenceFile);
	}
	
	@Override
	public String getTitle() {
		return article;
	}

	@Override
	public String getContent() {
		return content;
	}
	
	public static ArrayList<WikipediaArticle> loadArticles() throws IOException {
		ArrayList<WikipediaArticle> articles = new ArrayList<WikipediaArticle>();
		BufferedReader reader = new BufferedReader(new FileReader(sentenceFile));
		
		String articleUri = null;
		StringBuilder content = null;
		
		String line;
		while((line = reader.readLine()) != null) {
			int tab = line.indexOf("\t");
			String uri = new String(line.substring(0, tab).getBytes(), "UTF-8");
			String sentence = new String(line.substring(tab + 1).getBytes(), "UTF-8");
			
			if(uri.equals(articleUri)) {
				content.append(sentence);
				content.append(System.lineSeparator());
			} else {
				if(articleUri != null) {
					articles.add(new WikipediaArticle(articleUri, content.toString()));
				}
				articleUri = uri;
				content = new StringBuilder(sentence);
			}
		}
		articles.add(new WikipediaArticle(articleUri, content.toString()));
		reader.close();
		
		return articles;
	}
	
	public String toString() {
		return article + System.lineSeparator() + content + System.lineSeparator();
	}
}
