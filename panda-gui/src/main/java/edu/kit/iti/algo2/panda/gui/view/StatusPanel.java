package edu.kit.iti.algo2.panda.gui.view;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.kit.iti.algo2.panda.gui.model.QueryModel;
import edu.kit.iti.algo2.panda.management.StatusListener;

public class StatusPanel extends JPanel {
	private static final long serialVersionUID = -4175103243752135151L;
	
	private final JLabel statusLabel = new JLabel(" ");
	private final StatusListener listener = new StatusListener() {
		@Override
		public void statusUpdate(String status) {
			statusLabel.setText(status);
		}
	};
	
	public StatusPanel(QueryModel model) {
		initComponents();
		model.addStatusListener(listener);
	}
	
	private void initComponents() {
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(statusLabel);
	}

}
