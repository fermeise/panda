package edu.kit.iti.algo2.panda.indexing;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class QueryProcessor {
	private static Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
	private DocumentIndex index;
	
	public QueryProcessor(DocumentIndex index) {
		this.index = index;
	}

	public List<ScoredDocument> query(Query query, int maxResultCount) {
		if(query.getIncludedWords().isEmpty()) {
			return new ArrayList<ScoredDocument>();
		}
		
		Iterator<String> it = query.getIncludedWords().iterator();
		InvertedList result = index.queryWord(it.next());
		while(it.hasNext()) {
			result = result.intersect(index.queryWord(it.next()));
		}
		
		it = query.getExcludedWords().iterator();
		while(it.hasNext()) {
			result = result.remove(index.queryWord(it.next()));
		}
		
		return result.rankResults(maxResultCount);
	}

	public String extractSnippet(Document document, Query query, int maxSnippetSize, boolean html) {
		String content = document.getContent();
		
		int bestMatchBegin = 0;
		int bestMatchEnd = 0;
		int bestMatchScore = 0;

		HashSet<String> matchedWords = new HashSet<String>();
		LinkedList<TextOccurrence> match = new LinkedList<>();
		int matchScore = 0;
		
		// Determine best match (the one with maximum score)
		WordIterator it = new WordIterator(content);
		while(it.hasNext()) {
			TextOccurrence current = it.next();
			if(query.getIncludedWords().contains(current.getText())) {
				if(matchedWords.add(current.getText())) {
					match.add(current);
					matchScore += index.getWordScore(current.getText());
				}
			}
			while(!match.isEmpty() &&
					match.getLast().getPosition() + match.getLast().getText().length()
					- match.getFirst().getPosition() > maxSnippetSize) {
				matchedWords.remove(match.getFirst().getText());
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
				!(InvertedIndex.isCharacter(content.charAt(begin)) &&
						(begin == 0 || !InvertedIndex.isCharacter(content.charAt(begin - 1))))) {
			begin++;
		}
		
		while(end > bestMatchEnd &&
				!(InvertedIndex.isCharacter(content.charAt(end - 1)) &&
						(end == content.length() || !InvertedIndex.isCharacter(content.charAt(end))))) {
			end--;
		}
		
		String result = content.substring(begin, end);
		
		if(html) {
			StringBuilder snippet = new StringBuilder();
			int pos = 0;
			it = new WordIterator(result);
			while(it.hasNext()) {
				TextOccurrence current = it.next();
				if(query.getIncludedWords().contains(current.getText())) {
					int occPos = current.getPosition();
					int occLen = current.getText().length();
					snippet.append(result.substring(pos, occPos));
					snippet.append("<b>");
					snippet.append(result.substring(occPos, occPos + occLen));
					snippet.append("</b>");
					pos = occPos + occLen;
				}
			}
			snippet.append(result.substring(pos));
			
			result = snippet.toString();
		}
		
		return removeUnprintableCharacters(result).replace("\r\n", " ").replace("\n", " ");
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
