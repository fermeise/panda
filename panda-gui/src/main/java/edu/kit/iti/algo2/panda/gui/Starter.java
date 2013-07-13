package edu.kit.iti.algo2.panda.gui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import edu.kit.iti.algo2.panda.gui.model.QueryModel;
import edu.kit.iti.algo2.panda.gui.view.SearchView;
import edu.kit.iti.algo2.panda.management.IndexManager;

public class Starter implements Runnable {
	private static final Logger LOG = Logger.getLogger("GUI");
	
	private final QueryModel model;
	
	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOG.info("Cannot change Look & Feel.");
		}
		SwingUtilities.invokeLater(new Starter());
	}

	public Starter() throws IOException {
		ArrayList<Path> paths = new ArrayList<Path>();
		paths.add(Paths.get("d:/studium/"));
		final IndexManager manager = new IndexManager("../library", paths);
		
		this.model = new QueryModel(manager);
	}
	
	@Override
	public void run() {
		new SearchView(model);
	}

}
