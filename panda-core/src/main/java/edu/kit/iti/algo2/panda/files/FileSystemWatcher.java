package edu.kit.iti.algo2.panda.files;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import edu.kit.iti.algo2.panda.util.ObjectStringInputStream;

/**
 * The FileSystemWatcher watches a number of directories and notifies a handler
 * of file changes in those directories. The FileSystemWatcher does this by
 * tracking file size and last modified date. These infos can be persisted in a
 * file.
 */
public class FileSystemWatcher implements Runnable {
	private static final String filePrefix = "%PND-FM-0.2%";
	public static boolean liveWatching = true;
	
	private final FileSystemHandler handler;
	private final ArrayList<Path> directories;
	
	private Thread watchThread;
	private WatchService watcher;
	private HashMap<WatchKey, Path> watchKeys;
	
	private HashMap<Path, FileSystemEntry> files;
	
	public FileSystemWatcher(FileSystemHandler handler, List<Path> directories) {
		this.handler = handler;
		this.directories = new ArrayList<Path>();
		for(Path dir: directories) {
			this.directories.add(dir.toAbsolutePath().normalize());
		}
		
		this.watchThread = null;
		this.watcher = null;
		this.watchKeys = new HashMap<WatchKey, Path>();
		
		this.files = new HashMap<Path, FileSystemEntry>();
	}
	
	/**
	 * Start the thread that updates all files that changed since the last
	 * time and watches the filesystem for changes.
	 */
	public void start() {
		if(watchThread != null) {
			return;
		}
		
		watchThread = new Thread(this);
		watchThread.start();
	}
	
	/**
	 * Stops the thread and waits until it finishes.
	 */
	public void stop() {
		if(watchThread == null) {
			return;
		}
		
		watchThread.interrupt();
		try {
			watchThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		watchThread = null;
		
		if(watcher != null) {
			try {
				watcher.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			watcher = null;
		}
		watchKeys.clear();
	}
	
	/**
	 * Change the unique id of a file.
	 * @param file The path of the file.
	 * @param id The new unique id.
	 */
	public synchronized void changeId(Path file, int id) {
		FileSystemEntry entry = files.get(file);
		if(entry != null) {
			entry.setId(id);
		}
	}
	
	@Override
	public void run() {
		try {
			if(liveWatching) {
				try {
					watcher = FileSystems.getDefault().newWatchService();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			synchronized(this) {
				update(watcher != null);
				handler.signalInitialUpdateComplete();
			}
			
			if(watcher == null) {
				return;
			}
			
			while(true) {
				WatchKey key;
				key = watcher.take();
				
	            Path dir = watchKeys.get(key);
	            if(dir == null) {
	                continue;
	            }
				
				synchronized(this) {
					for(WatchEvent<?> event: key.pollEvents()) {
						@SuppressWarnings("rawtypes")
						WatchEvent.Kind kind = event.kind();
						
						if(kind == OVERFLOW) {
							continue;
						}
						
						@SuppressWarnings("unchecked")
						WatchEvent<Path> evt = (WatchEvent<Path>)event;
						Path path = dir.resolve(evt.context()).toAbsolutePath().normalize();
											
						try {
							if(kind == ENTRY_CREATE) {
								if(Files.isDirectory(path, NOFOLLOW_LINKS)) {
									scanDirectory(path, null, true);
								} else {
									checkFile(path, Files.readAttributes(path, BasicFileAttributes.class));
								}
							} else if(kind == ENTRY_MODIFY) {
								if(!Files.isDirectory(path, NOFOLLOW_LINKS)) {
									checkFile(path, Files.readAttributes(path, BasicFileAttributes.class));
								}
							} else if(kind == ENTRY_DELETE) {
								if(!Files.isDirectory(path, NOFOLLOW_LINKS)) {
									removeFile(path);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						boolean valid = key.reset();
						if(!valid) {
							watchKeys.remove(key);
							
							if(watchKeys.isEmpty()) {
								return;
							}
						}
					}
				}
			}
		} catch(InterruptedException e) {
			return;
		}
	}
	
	private void update(boolean register) throws InterruptedException {
		final HashSet<Path> removedFiles = new HashSet<Path>(files.keySet());
		
		for(Path dir: directories) {
			scanDirectory(dir, removedFiles, register);
		}
		
		for(Path path: removedFiles) {
			removeFile(path);
			if(Thread.interrupted()) {
				throw new InterruptedException();
			}
		}
	}
	
	private void scanDirectory(Path dir, final HashSet<Path> removedFiles,
			final boolean register) throws InterruptedException {
		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			    	if(register) {
			    		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			    		watchKeys.put(key, dir);
			    	}
			    	if(Thread.interrupted()) {
						watchThread.interrupt();
						return FileVisitResult.TERMINATE;
					}
			    	return FileVisitResult.CONTINUE;
			    }
			    
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					path = path.toAbsolutePath().normalize();
					if(checkFile(path, attrs) && removedFiles != null) {
						removedFiles.remove(path);
					}
					if(Thread.interrupted()) {
						watchThread.interrupt();
						return FileVisitResult.TERMINATE;
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
	}
	
	private boolean checkFile(Path path, BasicFileAttributes attrs) {
		if(handler.fileMatches(path)) {
			FileSystemEntry entry = files.get(path);
			if(entry != null) {
				if(attrs.size() != entry.getSize() ||
						attrs.lastModifiedTime().toMillis() != entry.getLastModified()) {
					removeFile(path);
					addFile(path, attrs);
				}
				return true;
			} else {
				addFile(path, attrs);
			}
		}
		return false;
	}

	private void addFile(Path path, BasicFileAttributes attrs) {
		int id = handler.addDocument(path);
		files.put(path, new FileSystemEntry(id, attrs.size(), attrs.lastModifiedTime().toMillis()));
	}
	
	private void removeFile(Path path) {
		FileSystemEntry entry = files.get(path);
		if(entry != null) {
			handler.documentRemoved(path, entry.getId());
			files.remove(path);
		}
	}
	
	public synchronized void saveToFile(File file) throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
		
		stream.writeChars(filePrefix);
		
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
	
	public static FileSystemWatcher createFromFile(FileSystemHandler handler,
			List<Path> directories, File file) throws IOException, ParseException {
		ObjectStringInputStream stream = new ObjectStringInputStream(new FileInputStream(file));
		
		if(!stream.readChars(filePrefix.length()).equals(filePrefix)) {
			stream.close();
			throw new ParseException("The file does not seem to be a correct panda filesystem file " +
					"or the version does not match.", 0);
		}
		
		FileSystemWatcher watcher = new FileSystemWatcher(handler, directories);
		
		try {
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
	
	public static boolean contains(Path base, Path child) {
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
