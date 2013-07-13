package edu.kit.iti.algo2.panda.indexing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class StopWords {
	private static final String resourceName="stopwords.txt";
	
	private static StopWords instance = null;
	
	private HashSet<String> words;
	
	private StopWords(InputStream stream) {
		this.words = new HashSet<String>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		String word;
		try {
			while((word = reader.readLine()) != null) {
				words.add(word);
			}
			
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static StopWords getInstance() {
		if(instance == null) {
			instance = new StopWords(StopWords.class.getClassLoader().getResourceAsStream(resourceName));
		}
		return instance;
	}
	
	public HashSet<String> getWords() {
		return words;
	}
}
