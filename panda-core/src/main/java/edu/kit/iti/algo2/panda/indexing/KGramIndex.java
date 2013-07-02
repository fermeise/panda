package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Simple index on splits of length k.
 */
public class KGramIndex {
	private final ArrayList<String> words;
	private final HashMap<String, IntSet> index;
	private final int k;
	
	/**
	 * Creates an empty data structure for indexing k-grams
	 * 
	 * @param k size of one token
	 */
	public KGramIndex(int k) {
		this.words = new ArrayList<String>();
		this.index = new HashMap<String, IntSet>();
		this.k = k;
	}
	
	/**
	 * Adds and indexes all words from <code>other</code>.
	 * 
	 * @param other is the collection of words to index.
	 */
	public void addAllWords(Collection<String> other) {
		this.words.ensureCapacity(this.words.size() + other.size());
		for (String word : other) {
			addWord(word);
		}
	}
	
	/**
	 * Adds and indexes a single word to this index.
	 * 
	 * <p>To index multiple words use {@link KGramIndex#addAllWords(Collection)}.</p>
	 * 
	 * @param word
	 */
	public void addWord(String word) {
		if(word.length() < k)
			return;
		
		int wordIdx = words.size();
		for(int i = 0; i <= word.length() - k; i++) {
			String kgram = word.substring(i, i + k);
			IntSet wordList = index.get(kgram);
			if (wordList == null) {
				wordList = new IntSet();
				index.put(kgram, wordList);
			}
			wordList.add(wordIdx);
		}
		words.add(word);
	}
	
	/**
	 * @return a list of currently indexed words.
	 */
	public List<String> getWords() {
		return this.words;
	}
	
	/**
	 * @return a list of all unique k-grams currently used.
	 */
	public Set<String> getKeys() {
		return this.index.keySet();
	}
}
