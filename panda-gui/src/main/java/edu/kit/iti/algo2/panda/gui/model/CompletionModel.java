package edu.kit.iti.algo2.panda.gui.model;

import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;

public class CompletionModel extends AbstractListModel<String> {
	private static final long serialVersionUID = -8577495048910408277L;
	
	List<String> words = new LinkedList<>();
	
	public CompletionModel() {
		words.add("abc");
		words.add("cbe");
	}

	@Override
	public int getSize() {
		return words.size();
	}

	@Override
	public String getElementAt(int index) {
		return words.get(index);
	}

}
