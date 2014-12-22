package de.tud.kom.socom.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tud.kom.socom.DeploySocom;

@RunWith(Suite.class)
@SuiteClasses({ GameComponentTest.class, UserComponentTest.class, ContentComponentTest.class })
public class SocomTests {

	private static Connection con;
	private static Statement query;

	private static final String USERNAME = "socom";
	private static final String PASSWORD = "socom4kom";
	private static final int DB_SERVER_PORT = 9001;

	private static Process databaseProcess;

//	@BeforeClass
	public static void init() {
		new Thread() {
			public void run() {
				startDatabase();
			}
		}.start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Clear Database..");
		if (!clearDatabase())
			return;
		startSocom();
	}

//	@AfterClass
	public static void teardown() {
		System.out.println("Stop Database..");
		stopDatabase();
	}

	private static void startDatabase() {
		File dir = new File("../SocomAPI/database");
		if (!dir.exists() || !dir.isDirectory()) {
			System.err.println("Could not find database.\nStop.");
			return;
		}
		try {
			databaseProcess = Runtime.getRuntime().exec("./database-server.sh", null, dir);

			BufferedReader reader = new BufferedReader(new InputStreamReader(databaseProcess.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
			}
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(databaseProcess.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(databaseProcess.getErrorStream()));
			String Error;

			while ((Error = stdError.readLine()) != null) {
				System.out.println(Error);
			}
			while ((Error = stdInput.readLine()) != null) {
				System.out.println(Error);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage() + "\nStop.");
			return;
		}
	}

	private static boolean clearDatabase() {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:" + DB_SERVER_PORT + "/socom", USERNAME, PASSWORD);
			query = con.createStatement();
			query.execute("DROP SCHEMA PUBLIC CASCADE");
			con.close();
		} catch (Exception e) {
			System.err.println("Could not clear database.\nStop.");
			return false;
		}
		return true;
	}

	private static void startSocom() {
		System.out.println("Start Socom..");
		DeploySocom.main(new String[0]);
	}

	private static void stopDatabase() {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:" + DB_SERVER_PORT + "/socom", USERNAME, PASSWORD);
			query = con.createStatement();
			query.execute("SHUTDOWN");
			con.close();
		} catch (Exception e) {
			System.err.println("Could not stop database.");
			return;
		}
		databaseProcess.destroy();
	}
}
