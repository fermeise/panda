package edu.kit.iti.algo2.panda.gui.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.kit.iti.algo2.panda.gui.model.QueryModel;

public class SearchView extends JFrame {
	private static final long serialVersionUID = 6854439490501728903L;

	private final static String TITLE = "PANDA Search";
	
	private final QueryModel model;
	
	private final WindowListener windowListener = new WindowAdapter() {
		public void windowClosing(WindowEvent evt) {
			model.close();
		}
	};
	
	private final JMenuBar topMenu = new JMenuBar();
	
	private final JTextField searchField = new JTextField(30);
	{
		searchField.getDocument().addDocumentListener(new QueryDocumentListener());
		searchField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
	}
	
	private final JPanel centerPanel = new JPanel(new BorderLayout());
	private final ResultPanel resultPanel;
	private final StatusPanel statusPanel;
	
	public SearchView(QueryModel model) {
		super(TITLE);
		this.model = model;
		this.resultPanel = new ResultPanel(model);
		this.statusPanel = new StatusPanel(model);
		initAndShowView();
	}
	
	private void initAndShowView() {
		initMenu();
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(windowListener);
		
		centerPanel.add(resultPanel, BorderLayout.CENTER);
		
		add(searchField, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);
		
		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}
	
	private void initMenu() {
		JMenu fileMenu = new JMenu("File");
		JMenu settingsMenu = new JMenu("Settings");
		fileMenu.add(new JMenuItem("Close"));
		topMenu.add(fileMenu);
		topMenu.add(settingsMenu);
		setJMenuBar(topMenu);
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
