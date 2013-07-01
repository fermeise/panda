package edu.kit.iti.algo2.panda.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple index on splits of length k.
 */
public class KGramIndex {
	private final ArrayList<String> words = new ArrayList<>();
	private final Map<String, List<Integer>> index = new HashMap<>();
	private final int k;
	
	/**
	 * Creates an empty data structure for indexing k-grams
	 * 
	 * @param k size of one token
	 */
	public KGramIndex(int k) {
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
		int index = this.words.size();
		List<String> kgrams = createFromWord(word);
		if (!kgrams.isEmpty()) {
			addToIndex(kgrams, index);
			this.words.add(word);			
		}
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
	
	private void addToIndex(Iterable<String> kgrams, int wordIndex) {
		for (String kgram : kgrams) {
			getWordList(kgram).add(wordIndex);
		}
	}
	
	private List<Integer> getWordList(String kgram) {
		List<Integer> result = index.get(kgram);
		if (result == null) {
			result = new LinkedList<>();
			index.put(kgram, result);
		}
		return result;
	}
	
	private List<String> createFromWord(String word) {
		int initialSize = word.length() - k;
		if (initialSize < 0)
			return new ArrayList<>();
		
		List<String> result = new ArrayList<>(initialSize);
		char[] chars = word.toCharArray();
		for (int i=0; i <= chars.length - k; i++) {
			char[] token = Arrays.copyOfRange(chars, i, i+k);
			result.add(new String(token));
		}
		return result;
	}
}
