package edu.kit.iti.algo2.panda.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;

import edu.kit.iti.algo2.panda.gui.model.AsyncQueryModel;
import edu.kit.iti.algo2.panda.gui.model.QueryModel;
import edu.kit.iti.algo2.panda.gui.view.SearchView;
import edu.kit.iti.algo2.panda.management.IndexManager;

public class Starter implements Runnable {
	
	private final QueryModel model;
	
	public static void main(String[] args) throws IOException {
		Path rootDirectory = Paths.get("../");
		SwingUtilities.invokeLater(new Starter(rootDirectory));
	}

	public Starter(Path rootDirectory) throws IOException {
		final IndexManager manager = new IndexManager(
				new File("../library.db"),
				new File("../library.pnd"),
				rootDirectory);
		
		this.model = new AsyncQueryModel(manager);
	}
	
	@Override
	public void run() {
		new SearchView(model);
	}

}
