package edu.kit.iti.algo2.panda.indexing;

public class ScoredDocument implements Comparable<ScoredDocument> {
	private final int id;
	private final int score;
	
	public ScoredDocument(int id, int score) {
		this.id = id;
		this.score = score;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof ScoredDocument &&
				score == ((ScoredDocument)o).score;
	}

	@Override
	public int compareTo(ScoredDocument other) {
		if(score < other.score) return 1;
		if(score > other.score) return -1;
		return 0;
	}
	
	@Override
	public int hashCode() {
		return score;
	}

	public int getId() {
		return id;
	}

	public int getScore() {
		return score;
	}
}
