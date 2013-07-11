package edu.kit.iti.algo2.panda.files;

public class FileSystemEntry {
	private int id;
	private final long size;
	private final long lastModified;
	
	public FileSystemEntry(int id, long size, long lastModified) {
		this.id = id;
		this.size = size;
		this.lastModified = lastModified;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public long getSize() {
		return size;
	}
	
	public long getLastModified() {
		return lastModified;
	}
}
