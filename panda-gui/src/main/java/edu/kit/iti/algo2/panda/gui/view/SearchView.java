package edu.kit.iti.algo2.panda.gui.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		this.resultPanel = new ResultPanel(this, model);
		this.statusPanel = new StatusPanel(model);
		initAndShowView();
	}
	
	public JTextField getSearchField() {
		return this.searchField;
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
		topMenu.add(fileMenu);
		JMenuItem closeItem = new JMenuItem("Close");
		fileMenu.add(closeItem);
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchView.this.dispose();
			}
		});
		
		JMenu indexMenu = new JMenu("Index");
		topMenu.add(indexMenu);
		JMenuItem rebuildItem = new JMenuItem("Rebuild");
		indexMenu.add(rebuildItem);
		rebuildItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.rebuildIndex();
			}
		});
		
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
