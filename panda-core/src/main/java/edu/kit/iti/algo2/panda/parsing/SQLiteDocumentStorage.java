package edu.kit.iti.algo2.panda.parsing;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import edu.kit.iti.algo2.panda.indexing.Document;

/**
 * Document storage that stores parsed documents in a SQLite database.
 * The document ids in the database start with 1.
 */
public class SQLiteDocumentStorage implements DocumentStorage {
	private static final Logger log = Logger.getLogger("SQLiteDocumentFactory");

	private Connection connection;
	private PreparedStatement addDocumentStmt;
	private PreparedStatement retrieveDocumentStmt;
	private PreparedStatement removeDocumentStmt;
	private PreparedStatement getDocumentCountStmt;
	
	public SQLiteDocumentStorage(String libraryFile) {
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + libraryFile);
			
			Statement statement = connection.createStatement();
			statement.executeUpdate("create table if not exists documents (id integer primary key, " +
					"file text, title text, content text) ");
			addDocumentStmt = connection.prepareStatement("insert into documents (id, file, title, content) values (? + 1, ?, ?, ?)");
			retrieveDocumentStmt = connection.prepareStatement("select file, title, content from documents where id=? + 1");
			removeDocumentStmt = connection.prepareStatement("delete from documents where id=? + 1");
			getDocumentCountStmt = connection.prepareStatement("select max(id) from documents");
		} catch (ClassNotFoundException e) {
			log.severe("SQLite JDBC driver not found.");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addDocument(int id, Document document) {
		try {
			addDocumentStmt.setInt(1, id);
			addDocumentStmt.setString(2, document.getFile().getAbsolutePath());
			addDocumentStmt.setString(3, document.getTitle());
			addDocumentStmt.setString(4, document.getContent());
			addDocumentStmt.executeUpdate();
		} catch(SQLException e) {
			log.warning("Could not store document with id=" + id + ".");
		}
	}

	@Override
	public void removeDocument(int id) {
		try {
			removeDocumentStmt.setInt(1, id);
			removeDocumentStmt.executeUpdate();
		} catch(SQLException e) {
			log.warning("Could not remove document with id=" + id + ".");
		}
	}

	public Document restoreDocument(int id) {
		try {
			retrieveDocumentStmt.setInt(1, id);
		
			ResultSet rs = retrieveDocumentStmt.executeQuery();
			if(rs.next()) {
				return new GenericDocument(new File(rs.getString(1)), rs.getString(2), rs.getString(3));
			}
		} catch (SQLException e) {
			log.warning("Could not retrieve document with id=" + id + ".");
		}
		
		return null;
	}

	@Override
	public int getMaxDocumentId() {
		try {
			ResultSet rs = getDocumentCountStmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			log.warning("Could not retrieve document count.");
		}
		
		return 0;
	}
}
