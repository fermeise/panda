package edu.kit.iti.algo2.panda.indexing;

import java.io.File;

public interface Document {
	public File getFile();
	public String getTitle();
	public String getContent();
}
