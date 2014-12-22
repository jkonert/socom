package de.tud.kom.socom.web.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.tud.kom.socom.web.client.sharedmodels.GlobalConfig;
import de.tud.kom.socom.web.server.util.ResourceLoader;

/**
 * 
 * @author Rhaban
 * 
 */
public class HSQLAccess implements GlobalConfig {

	/* connection and reuseable statement object */
	private Connection con;
	private static Statement query;
	private static HSQLAccess instance = new HSQLAccess();

	/* Database specific strings */
//	private static final String USERNAME = "socom";
//	private static final String PASSWORD = "socom4kom";
	//private static final String DATABASE_FILE = "database/socomdb";
	/* LONGVARCHAR = BLOB, save TextHints like Binary GameContent */
	public static final String DATABASE_PROPERTIES = "sql.longvar_is_lob=true";

	private HSQLAccess() {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			//con = DriverManager.getConnection("jdbc:hsqldb:file:"
					//+ DATABASE_FILE + ";" + DATABASE_PROPERTIES, USERNAME,
					//PASSWORD);
			String username = ResourceLoader.getResource("db_user");
			String password = ResourceLoader.getResource("db_password");
			String port = ResourceLoader.getResource("db_port");
			String host = ResourceLoader.getResource("db_host");

			con = DriverManager.getConnection("jdbc:hsqldb:hsql://" + host + ":" + port + "/socom", username, password);

			query = con.createStatement();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static HSQLAccess getInstance() {
		return instance;
	}

	/**
	 * execute the sql queries without result, like INSERT
	 * 
	 * @param query
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 * @throws SQLException
	 */
	public synchronized int execQuery(String query) throws SQLException {
		return HSQLAccess.query.executeUpdate(query);
	}

	/**
	 * execute the sql queries with result
	 * 
	 * @param query
	 * @return a ResultSet object that contains the data produced by the given
	 *         query; never null
	 * @throws SQLException
	 */
	public synchronized ResultSet execQueryWithResult(String query)
			throws SQLException {
		return HSQLAccess.query.executeQuery(query);
	}

	public PreparedStatement getPreparedStatement(String query)
			throws SQLException {
		return con.prepareStatement(query);
	}
}
