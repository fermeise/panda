package edu.kit.iti.algo2.panda.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.kit.iti.algo2.panda.gui.model.QueryModel;
import edu.kit.iti.algo2.panda.management.StatusListener;

public class SearchView {
	
	private final static int RESULT_VERTICAL_SCROLLBAR = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
	private final static int RESULT_HORIZONTAL_SCROLLBAR = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
	
	private final JFrame root = new JFrame("PANDA Search");
	{
		root.setLayout(new BorderLayout());
		root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		root.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				model.close();
			}
		});
	}
	
	private final JMenuBar topMenu = new JMenuBar();
	{
		JMenu fileMenu = new JMenu("File");
		JMenu settingsMenu = new JMenu("Settings");
		fileMenu.add(new JMenuItem("Close"));
		topMenu.add(fileMenu);
		topMenu.add(settingsMenu);
		root.setJMenuBar(topMenu);
	}
	
	private final JPanel centerPanel = new JPanel(new BorderLayout());
	private final JTextField searchField = new JTextField(30);
	private final QueryModel model;
	private final JList<String> searchResult = new JList<>();
	{
		centerPanel.add(searchField, BorderLayout.NORTH);
		searchField.getDocument().addDocumentListener(new QueryDocumentListener());
		searchField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		searchResult.setBorder(BorderFactory.createTitledBorder("Result:"));
		JScrollPane searchResultWrapper = new JScrollPane(searchResult,
				RESULT_VERTICAL_SCROLLBAR, RESULT_HORIZONTAL_SCROLLBAR);
		searchResultWrapper.setPreferredSize(new Dimension(900, 320));
		centerPanel.add(searchResultWrapper, BorderLayout.CENTER);
		root.add(centerPanel);
	}
	
	private final JLabel statusLabel = new JLabel(" ");
	private final JPanel statusPanel = new JPanel();
	{
		statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusPanel.add(statusLabel);
		root.add(statusPanel, BorderLayout.SOUTH);
	}
	
	public SearchView(QueryModel model) {
		this.model = model;
		searchResult.setModel(model);
		searchResult.addMouseListener(new ResultMouseListener());
		model.addStatusListener(new StatusListener() {
			public void statusUpdate(String status) {
				statusLabel.setText(status);
			}
		});
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
	
	private class ResultMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				model.viewDocument(searchResult.getSelectedIndex());
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
}
