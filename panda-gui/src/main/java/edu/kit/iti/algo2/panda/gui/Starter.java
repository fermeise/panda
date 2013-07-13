package edu.kit.iti.algo2.panda.gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
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
		Properties properties = new Properties();
		properties.load(new FileInputStream("config.properties"));
		
		final String libraryPath = properties.getProperty("library", "./library");
		
		ArrayList<Path> paths = new ArrayList<Path>();
		for(int i = 1;; i++) {
			String path = properties.getProperty("path" + i);
			if(path != null) {
				paths.add(Paths.get(path));
			} else {
				break;
			}
		}
		
		HashMap<String, Path> fileViewer = new HashMap<String, Path>();
		for(Entry<Object, Object> entry: properties.entrySet()) {
			final String key = (String)entry.getKey();
			final String value = (String)entry.getValue();
			if(key.endsWith("Viewer")) {
				fileViewer.put(key.substring(0, key.length() - 6).toLowerCase(), Paths.get(value));
			}
		}
		
		final IndexManager manager = new IndexManager(libraryPath, paths);
		
		this.model = new QueryModel(manager, fileViewer);
	}
	
	@Override
	public void run() {
		new SearchView(model);
	}

}
