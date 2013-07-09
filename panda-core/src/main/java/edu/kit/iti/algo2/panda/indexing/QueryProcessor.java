package edu.kit.iti.algo2.panda.indexing;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class QueryProcessor {
	private DocumentIndex index;
	private Font font;
	
	public QueryProcessor(DocumentIndex index) {
		this.index = index;
		this.font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
	}

	public List<ScoredDocument> query(String query, int maxResultCount) {
		ArrayList<String> words = getWords(query);
		
		if(words.isEmpty()) {
			return new ArrayList<ScoredDocument>();
		}
		
		Iterator<String> it = words.iterator();
		InvertedList result = index.queryWord(it.next());
		while(it.hasNext()) {
			result = result.intersect(index.queryWord(it.next()));
		}
		return result.bestResults(maxResultCount);
	}

	public String extractSnippet(Document document, DocumentIndex index, String query, int maxSnippetSize) {
		ArrayList<String> words = getWords(query);
		String content = document.getContent();
		
		int bestMatchBegin = 0;
		int bestMatchEnd = 0;
		int bestMatchScore = 0;

		LinkedList<TextOccurrence> match = new LinkedList<>();
		int matchScore = 0;
		
		// Determine best match (the one with maximum score)
		WordIterator it = new WordIterator(content);
		while(it.hasNext()) {
			TextOccurrence current = it.next();
			if(words.contains(current.getText())) {
				match.add(current);
				matchScore += index.getWordScore(current.getText());
			}
			while(!match.isEmpty() &&
					match.getLast().getPosition() + match.getLast().getText().length()
					- match.getFirst().getPosition() > maxSnippetSize) {
				matchScore -= index.getWordScore(match.getFirst().getText());
				match.removeFirst();
			}
			if(matchScore > bestMatchScore) {
				bestMatchBegin = match.getFirst().getPosition();
				bestMatchEnd = match.getLast().getPosition() + match.getLast().getText().length();
				bestMatchScore = matchScore;
			}
		}
		
		int bestMatchLength = bestMatchEnd - bestMatchBegin;
		int begin = Math.max(bestMatchBegin - (maxSnippetSize - bestMatchLength) / 2, 0);
		int end = Math.min(bestMatchEnd + (maxSnippetSize - bestMatchLength) / 2, content.length());
		while(begin < bestMatchBegin &&
				!(Character.isAlphabetic(content.charAt(begin)) &&
						(begin == 0 || !Character.isAlphabetic(content.charAt(begin - 1))))) {
			begin++;
		}
		
		while(end > bestMatchEnd &&
				!(Character.isAlphabetic(content.charAt(end - 1)) &&
						(end == content.length() || !Character.isAlphabetic(content.charAt(end))))) {
			end--;
		}

		return removeUnprintableCharacters(content.substring(begin, end)).replace("\r\n", " ").replace("\n", " ");
	}
	
	private ArrayList<String> getWords(String query) {
		ArrayList<String> words = new ArrayList<>();
		
		Scanner scanner = new Scanner(query);
		scanner.useDelimiter(" ");
		while(scanner.hasNext()) {
			words.add(WordIterator.normalizeWord(scanner.next()));
		}
		scanner.close();
		return words;
	}
	
	private String removeUnprintableCharacters(String str) {
		char[] result = new char[str.length()];
		int len = 0;
		
		for(char c: str.toCharArray()) {
			if(font.canDisplay(c)) {
				result[len++] = c;
			}
		}
		
		return new String(result);
	}
}
