package edu.kit.iti.algo2.panda.parsing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import edu.kit.iti.algo2.panda.files.FileSystemWatcher;

public class TestFileSystemWatcher {

	@Test
	public void testContains() throws IOException {
		assertTrue(FileSystemWatcher.contains(Paths.get("C:\\Temp"), Paths.get("C:\\Temp")));		
		assertTrue(FileSystemWatcher.contains(Paths.get("C:\\Temp"), Paths.get("C:\\Temp\\")));
		assertTrue(FileSystemWatcher.contains(Paths.get("C:\\Temp\\"), Paths.get("C:\\Temp")));
		assertTrue(FileSystemWatcher.contains(Paths.get("C:\\temp"), Paths.get("C:\\Temp")));
		assertTrue(FileSystemWatcher.contains(Paths.get("C:\\Temp"), Paths.get("C:/Temp")));
		assertTrue(FileSystemWatcher.contains(Paths.get("C:\\Temp"), Paths.get("C:\\Temp\\..\\Temp")));
		assertTrue(FileSystemWatcher.contains(Paths.get("C:\\Temp\\..\\Temp"), Paths.get("C:\\Temp")));
		
		assertEquals(Paths.get("C:/Temp").normalize(), Paths.get("C:\\Temp\\..\\Temp").normalize());
	}

}
