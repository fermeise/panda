package edu.kit.iti.algo2.panda.parsing;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import edu.kit.iti.algo2.panda.indexing.Document;

public class DocumentFactory {
	private static final Logger log = Logger.getLogger("DocumentFactory");
	private static final Tika tika = new Tika();

	private Connection connection;
	private PreparedStatement addDocumentStmt;
	private PreparedStatement retrieveDocumentStmt;
	
	public DocumentFactory(String libraryFile) {
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + libraryFile);
			
			Statement statement = connection.createStatement();
			statement.executeUpdate("create table if not exists documents (id integer primary key autoincrement, " +
					"file text, title text, content text) ");
			addDocumentStmt = connection.prepareStatement("insert into documents (file, title, content) values (?, ?, ?)");
			retrieveDocumentStmt = connection.prepareStatement("select file, title, content from documents where id=? + 1");
		} catch (ClassNotFoundException e) {
			log.severe("SQLite JDBC driver not found.");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Document createDocument(File file) throws IOException, TikaException {
		String title = file.getName();
		String content = tika.parseToString(file);

		return new GenericDocument(file, title, content);
	}
	
	public Document restoreDocument(int id) {
		try {
			retrieveDocumentStmt.setInt(1, id);
		
			ResultSet rs = retrieveDocumentStmt.executeQuery();
			if(rs.next()) {
				return new GenericDocument(new File(rs.getString(1)), rs.getString(2), rs.getString(3));
			}
		} catch (SQLException e) {
			log.warning("Could not retrieve document with id=" + id);
		}
		
		return null;
	}
	
	protected void addToLibrary(Document document) {
		try {
			addDocumentStmt.setString(1, document.getFile().getAbsolutePath());
			addDocumentStmt.setString(2, document.getTitle());
			addDocumentStmt.setString(3, document.getContent());
			addDocumentStmt.executeUpdate();
		} catch(SQLException e) {
			log.warning("Could not store document.");
		}
	}
}
