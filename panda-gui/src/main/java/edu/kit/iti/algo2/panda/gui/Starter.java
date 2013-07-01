package edu.kit.iti.algo2.panda.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;

import edu.kit.iti.algo2.panda.gui.model.QueryModel;
import edu.kit.iti.algo2.panda.gui.view.SearchView;
import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.parsing.DocumentFactory;
import edu.kit.iti.algo2.panda.parsing.FileSystemCrawler;

public class Starter implements Runnable {
	
	private final QueryModel model;
	
	public static void main(String[] args) throws IOException {
		Path rootDirectory = Paths.get("../");
		SwingUtilities.invokeLater(new Starter(rootDirectory));
	}

	public Starter(Path rootDirectory) throws IOException {
		final DocumentFactory factory = new DocumentFactory("../library.db");
		final InvertedIndex index;
		
		File indexFile = new File("../library.pnd");
		if(indexFile.exists()) {
			System.out.println("Loading index...");
			index = InvertedIndex.loadFromFile(indexFile);
		} else {
			System.out.println("Building index...");
			index = new InvertedIndex();
			FileSystemCrawler crawler = new FileSystemCrawler(factory, index);
			crawler.crawl(rootDirectory);
			index.finish();
			System.out.println("Saving index...");
			index.saveToFile(indexFile);
		}
		
		this.model = new QueryModel(factory, index);
	}
	
	@Override
	public void run() {
		new SearchView(model);
	}

}
