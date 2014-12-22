package de.tud.kom.socom.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.util.Logger;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.PlatformTools;
import de.tud.kom.socom.util.ResourceLoader;

/**
 * 
 * @author Rhaban Hark
 * 
 */
public class HSQLAccess implements GlobalConfig {

	private static final Logger logger = LoggerFactory.getLogger();

	/* connection and reuseable statement object */
	private Connection con;
	private static Statement query;
	private static HSQLAccess instance = new HSQLAccess();

	/* Database specific strings */
//	private static final String USERNAME = "socom";
//	private static final String PASSWORD = "socom4kom";

	// private static final String DATABASE_FILE = "database/socomdb";
	/* LONGVARCHAR = BLOB, save TextHints like Binary GameContent */
	// private static final String DATABASE_PROPERTIES =
	// "sql.longvar_is_lob=true";

	private HSQLAccess() {
		try {
			startDatabase();

			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			// con = DriverManager.getConnection("jdbc:hsqldb:file:" +
			// DATABASE_FILE + ";" + DATABASE_PROPERTIES,
			// USERNAME, PASSWORD);
			String db_host = ResourceLoader.getResource("db_host");
			int db_port = Integer.parseInt(ResourceLoader.getResource("db_port"));
			String username = ResourceLoader.getResource("db_user");
			String password = ResourceLoader.getResource("db_password");
			
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://" + db_host + ":"
					+ db_port + "/socom", username, password);

			query = con.createStatement();
			createTables();

		} catch (SQLException e) {
			e.printStackTrace();
			logger.Error(e);
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.Error(e);
			return;
		}
		logger.Info("***Database ready");
	}

	private void startDatabase() {
		String[] dbexec = PlatformTools.isWindows() ? new String[]{"cmd.exe","/c","database-server.bat"} : new String[]{"./database-server.sh"};
		logger.Info("Start Database Process");
		try {
			Process databaseProcess = new ProcessBuilder(dbexec).directory(new File("database")).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(databaseProcess.getInputStream()));
			String line = "";
			String startupLine = "Startup sequence completed";
			while((line = reader.readLine()) != null && !line.contains(startupLine)){
				logger.Info(line);
			}
			reader.close();
			
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run() {
					logger.Info("***Shutdown Database");
					try {
						execQuery("SHUTDOWN");
					} catch (Throwable e) {
					}
				}
			});
		} catch (IOException e) {
			logger.Error(e);
			e.printStackTrace();
		}
	}

	private void createTables() throws SQLException {
		try {
			for (String q : QueryStrings.allCreateQuerys) {
				query.execute(q);
			}
			for (String q : QueryStrings.allInsertQuerys) {
				query.execute(q);
			}

		} catch (SQLSyntaxErrorException e) {
			if (e.getMessage().contains("already exists"))
				logger.Info("Tables already exists - skip.");
			else
				throw e;
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
	
	public PreparedStatement getPreparedStatementGetKey(String query)
			throws SQLException {
		return con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	}
}
