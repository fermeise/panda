package edu.kit.iti.algo2.panda.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import edu.kit.iti.algo2.panda.gui.model.QueryModel;

public class ResultPanel extends JPanel {
	private static final long serialVersionUID = -8345079086452477714L;
	
	private final QueryModel model;
	private final JList<String> searchResult = new JList<>();
	private final JScrollPane searchResultWrapper = new JScrollPane();
	
	private final MouseListener resultMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				model.viewDocument(searchResult.getSelectedIndex());
			}
		}
	};
	
	public ResultPanel(QueryModel model) {
		super(new BorderLayout());
		this.model = model;
		initComponents();
	}
	
	private void initComponents() {
		searchResult.setModel(model);
		searchResult.setBorder(BorderFactory.createTitledBorder("Result:"));
		searchResult.setCellRenderer(new ResultRenderer());
		searchResult.addMouseListener(resultMouseListener);
		searchResult.setPreferredSize(new Dimension(600, 0));
		
		searchResultWrapper.setViewportView(searchResult);
		searchResultWrapper.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		searchResultWrapper.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		searchResultWrapper.setPreferredSize(new Dimension(900, 500));
		
		add(searchResultWrapper);
	}
}
