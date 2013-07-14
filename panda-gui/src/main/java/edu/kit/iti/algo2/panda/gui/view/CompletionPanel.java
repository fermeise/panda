package edu.kit.iti.algo2.panda.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JPanel;

import edu.kit.iti.algo2.panda.gui.model.CompletionModel;

public class CompletionPanel extends JPanel {
	private static final long serialVersionUID = 6239659716903900683L;
	
	private final JList<String> completionList;
	
	public CompletionPanel(CompletionModel model) {
		super(new BorderLayout());
		this.completionList = new JList<>(model);
		setPreferredSize(new Dimension(100, 0));
		add(completionList);
	}

}
