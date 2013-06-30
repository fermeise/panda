package edu.kit.iti.algo2.panda.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.kit.iti.algo2.panda.gui.model.QueryModel;

public class SearchView {
	
	private final JFrame root = new JFrame("PANDA Search");
	
	{
		root.setLayout(new BorderLayout(10, 10));
		root.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		root.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private final JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
	private final JTextField searchField = new JTextField(30);
	private final QueryModel model;
	private final JList<String> searchResult = new JList<>();
	
	{
		searchResult.setPreferredSize(new Dimension(100, 300));
		centerPanel.add(searchField, BorderLayout.NORTH);
		searchField.getDocument().addDocumentListener(new QueryDocumentListener());
		searchResult.setBorder(BorderFactory.createTitledBorder("Result:"));
		centerPanel.add(searchResult, BorderLayout.CENTER);
		root.add(centerPanel);
	}
	
	public SearchView(QueryModel model) {
		this.model = model;
		searchResult.setModel(model);
		root.pack();
		root.setLocationByPlatform(true);
		root.setVisible(true);
	}
	
	public void setVisible(boolean show) {
		root.setVisible(show);
	}
	
	private void processQuery() {
		this.model.setQuery(searchField.getText());
	}
	
	private class QueryDocumentListener implements DocumentListener {
		@Override
		public void removeUpdate(DocumentEvent e) { processQuery(); }
		@Override
		public void insertUpdate(DocumentEvent e) { processQuery(); }
		@Override
		public void changedUpdate(DocumentEvent e) { processQuery(); }
	}
}
