package edu.kit.iti.algo2.panda.files;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import edu.kit.iti.algo2.panda.util.ObjectStringInputStream;

public class FileSystemWatcher {
	private static final String filePrefix = "%PND-FM-0.1%";
	
	private FileSystemHandler handler;
	
	private ArrayList<Path> directories;
	private HashMap<Path, FileSystemEntry> files;
	
	public FileSystemWatcher(FileSystemHandler handler) {
		this.handler = handler;
		
		this.directories = new ArrayList<Path>();
		this.files = new HashMap<Path, FileSystemEntry>();
	}
	
	public void addDirectory(Path directory) {
		directory = directory.toAbsolutePath().normalize();
		for(Path dir: directories) {
			if(dir.equals(directory)) {
				return;
			}
		}
		directories.add(directory);
	}
	
	public void removeDirectory(Path directory) {
		directories.remove(directory);
	}
	
	public void changeId(Path file, int id) {
		FileSystemEntry entry = files.get(file);
		entry.setId(id);
	}
	
	public void update() {
		final HashSet<Path> removedFiles = new HashSet<Path>(files.keySet());
		
		try {
			for(Path dir: directories) {
				Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
						super.visitFile(path, attrs);
						path = path.toAbsolutePath().normalize();
						if(handler.fileMatches(path)) {
							FileSystemEntry entry = files.get(path);
							if(entry != null) {
								removedFiles.remove(path);
								if(attrs.size() != entry.getSize() ||
										attrs.lastModifiedTime().toMillis() != entry.getLastModified()) {
									removeFile(path);
									addFile(path, attrs);
								}
							} else {
								addFile(path, attrs);
							}
						}
						
						return FileVisitResult.CONTINUE;
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Path path: removedFiles) {
			removeFile(path);
		}
	}
	
	public void saveToFile(File file) throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
		
		stream.writeChars(filePrefix);
		
		stream.writeInt(directories.size());
		for(Path dir: directories) {
			final String path = dir.toString();
			stream.writeInt(path.length());
			stream.writeChars(path);
		}
		
		for(Entry<Path, FileSystemEntry> entry: files.entrySet()) {
			final String path = entry.getKey().toString();
			stream.writeInt(path.length());
			stream.writeChars(path);
			
			stream.writeInt(entry.getValue().getId());
			stream.writeLong(entry.getValue().getSize());
			stream.writeLong(entry.getValue().getLastModified());
		}
		stream.close();
	}
	
	public static FileSystemWatcher createFromFile(FileSystemHandler handler, File file) throws IOException, ParseException {
		ObjectStringInputStream stream = new ObjectStringInputStream(new FileInputStream(file));
		
		if(!stream.readChars(filePrefix.length()).equals(filePrefix)) {
			stream.close();
			throw new ParseException("The file does not seem to be a correct panda filesystem file " +
					"or the version does not match.", 0);
		}
		
		FileSystemWatcher watcher = new FileSystemWatcher(handler);
		
		try {
			int directoryCount = stream.readInt();
			for(int i = 0; i < directoryCount; i++) {
				watcher.directories.add(Paths.get(stream.readString()));
			}
			
			while(true) {
				final String path = stream.readString();
				final int id = stream.readInt();
				final long size = stream.readLong();
				final long lastModified = stream.readLong();
				
				final FileSystemEntry entry = new FileSystemEntry(id, size, lastModified);
				watcher.files.put(Paths.get(path), entry);
			}
		} catch(EOFException e) {
		} finally {
			stream.close();
		}
		
		return watcher;
	}
	
	private void addFile(Path path, BasicFileAttributes attrs) {
		int id = handler.addDocument(path);
		files.put(path, new FileSystemEntry(id, attrs.size(), attrs.lastModifiedTime().toMillis()));
	}
	
	private void removeFile(Path path) {
		FileSystemEntry entry = files.get(path);
		handler.documentRemoved(entry.getId());
		files.remove(path);
	}
	
	public static boolean contains(Path base, Path child) throws IOException {
		base = base.toAbsolutePath().normalize();
		
		Path current = child.toAbsolutePath().normalize();
		while(current != null) {
			if(base.equals(current)) {
				return true;
			}
			current = current.getParent();
		}
		
		return false;
	}
}
