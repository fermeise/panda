package edu.kit.iti.algo2.panda.files;

import java.nio.file.Path;

public interface FileSystemHandler {
	public boolean fileMatches(Path path);
	public int addDocument(Path path);
	public void documentRemoved(Path path, int documentId);
}
