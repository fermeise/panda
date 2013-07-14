package edu.kit.iti.algo2.panda.parsing;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import org.junit.Test;

import edu.kit.iti.algo2.panda.indexing.Document;

public class TestStorage {

	public Document karlsruhe = new GenericDocument(Paths.get("."), "Karlsruhe", "The University of Karlsruhe, " +
			"also known as Fridericiana, was founded in 1825. It is located in the city of Karlsruhe, " +
			"Germany, and is one of the most prestigious technical universities in Germany");
	public Document freiburg = new GenericDocument(Paths.get("."), "Freiburg", "The University of Freiburg (German " +
			"Albert-Ludwigs-Universität Freiburg, colloquially Uni Freiburg), sometimes referred to " +
			"with its full title, the Albert Ludwig University of Freiburg, is a public research university " +
			"located in Freiburg im Breisgau, Baden-Württemberg, Germany.");
	public Document kit = new GenericDocument(Paths.get("."), "KIT", "The Karlsruhe Institute of Technology (KIT) " +
			"is one of the largest and most prestigious research and education institutions in Germany.");
	
	@Test
	public void testSQLiteStorage() {
		DocumentStorage storage = new SQLiteDocumentStorage(":memory:");
		
		storage.addDocument(0, karlsruhe);
		storage.addDocument(2, freiburg);
		storage.commitChanges();
		
		assertEquals(karlsruhe.getContent(), storage.restoreDocument(0, true).getContent());
		assertEquals(freiburg.getContent(), storage.restoreDocument(2, true).getContent());
		assertEquals(3, storage.getMaxDocumentId());
		
		storage.removeDocument(0);
		storage.commitChanges();
		assertEquals(3, storage.getMaxDocumentId());
		
		storage.addDocument(0, kit);
		storage.commitChanges();
		assertEquals(kit.getContent(), storage.restoreDocument(0, true).getContent());
	}

}
