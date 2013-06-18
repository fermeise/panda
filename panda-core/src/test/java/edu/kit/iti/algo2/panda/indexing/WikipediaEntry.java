package edu.kit.iti.algo2.panda.indexing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class WikipediaEntry implements Document {
	private static final String sentenceFile = "wikipedia-sentences.csv";
	private String location;
	private String content;
	
	public WikipediaEntry(String location, String content) {
		this.location = location;
		this.content = content;
	}
	
	public String getURI() {
		return location;
	}

	@Override
	public String getContent() {
		return content;
	}
	
	public static ArrayList<WikipediaEntry> loadSentences() throws IOException {
		ArrayList<WikipediaEntry> entries = new ArrayList<WikipediaEntry>();
		BufferedReader reader = new BufferedReader(new FileReader(sentenceFile));
		String line;
		while((line = reader.readLine()) != null) {
			int tab = line.indexOf("\t");
			String uri = new String(line.substring(0, tab).getBytes(), "UTF-8");
			String content = new String(line.substring(tab + 1).getBytes(), "UTF-8");
			entries.add(new WikipediaEntry(uri, content));
		}
		reader.close();
		
		return entries;
	}
	
	public String toString() {
		return location + "\t" + content;
	}

}
