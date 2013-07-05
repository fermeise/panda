package edu.kit.iti.algo2.panda.indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WikipediaSentence implements Document {
	private static final String sentenceFile = "wikipedia-sentences.csv";
	private String article;
	private String content;
	
	public WikipediaSentence(String article, String content) {
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
	
	public static ArrayList<WikipediaSentence> loadSentences() throws IOException {
		ArrayList<WikipediaSentence> entries = new ArrayList<WikipediaSentence>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sentenceFile), "UTF-8"));
		
		String line;
		while((line = reader.readLine()) != null) {
			int tab = line.indexOf("\t");
			String uri = line.substring(0, tab);
			String content = line.substring(tab + 1);
			entries.add(new WikipediaSentence(uri, content));
		}
		reader.close();
		
		return entries;
	}
	
	public String toString() {
		return article + "\t" + content;
	}
}
