package edu.kit.iti.algo2.panda.indexing;

public class TextOccurrence {
	private final String text;
	private final int position;
	
	public TextOccurrence(String text, int position) {
		this.text = text;
		this.position = position;
	}
	
	public String getText() {
		return text;
	}
	
	public int getPosition() {
		return position;
	}
}
