package edu.kit.iti.algo2.panda.indexing.kgrams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
		this.words = new ArrayList<>();
		this.index = new HashMap<>();
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
		Iterator<String> iterator = new KGramIterator(word, k);
		while (iterator.hasNext()) {
			String kgram = iterator.next();
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
	
	/**
	 * Search for all words which are less or equal the edit distance.
	 * 
	 * @param word the query word to search for.
	 * @param editDistance the maximal number of transformations to perform.
	 * @return a list a of words that matches the edit distance.
	 */
	public List<String> fuzzySearch(String word, int editDistance) {
		Iterator<String> iterator = new KGramIterator(word, k);
		CountingSet allKgrams = new CountingSet();
		while (iterator.hasNext()) {
			String kgram = iterator.next();
			IntSet current = index.get(kgram);
			if (current != null) {
				allKgrams.addAll(current);
			}
		}
		List<String> result = new LinkedList<>();
		final int treshold = k - 1 - k * editDistance;
		for (int i=0; i < allKgrams.size(); i++) {
			int currentWordIndex = allKgrams.getElement(i);
			int commonSize = allKgrams.getCount(i);
			String candidate = words.get(currentWordIndex);
			if (commonSize >= Math.max(word.length(), candidate.length()) + treshold) {
				int actualDistance = LevinshteinDistance.distance(candidate.toCharArray(), word.toCharArray());
				if (actualDistance <= editDistance) {
					result.add(candidate);
				}
			}
		}
		return result;
	}
}
