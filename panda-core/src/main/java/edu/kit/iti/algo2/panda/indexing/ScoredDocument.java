package edu.kit.iti.algo2.panda.indexing;

public class ScoredDocument implements Comparable<ScoredDocument> {
	private final int id;
	private final float score;
	
	public ScoredDocument(int id, float score) {
		this.id = id;
		this.score = score;
	}

	@Override
	public int compareTo(ScoredDocument other) {
		if(score < other.score) return 1;
		if(score > other.score) return -1;
		return 0;
	}

	public int getId() {
		return id;
	}

	public float getScore() {
		return score;
	}
}
