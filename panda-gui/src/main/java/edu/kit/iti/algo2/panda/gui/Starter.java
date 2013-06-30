package edu.kit.iti.algo2.panda.gui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;

import edu.kit.iti.algo2.panda.gui.model.QueryModel;
import edu.kit.iti.algo2.panda.gui.view.SearchView;
import edu.kit.iti.algo2.panda.indexing.InvertedIndex;
import edu.kit.iti.algo2.panda.parsing.FileSystemCrawler;

public class Starter implements Runnable {
	
	private final QueryModel model;
	
	public static void main(String[] args) throws IOException {
		Path rootDirectory = Paths.get("../");
		SwingUtilities.invokeLater(new Starter(rootDirectory));
	}

	public Starter(Path rootDirectory) throws IOException {
		InvertedIndex index = new InvertedIndex();
		FileSystemCrawler crawler = new FileSystemCrawler();
		crawler.crawl(rootDirectory);
		crawler.index(index);
		index.finish();
		this.model = new QueryModel(index, crawler);
	}
	
	@Override
	public void run() {
		new SearchView(model);
	}

}
