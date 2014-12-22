package de.tud.kom.socom.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.tud.kom.socom.util.exceptions.IllegalParameterException;

public abstract class HSQLDatabase {
	protected static HSQLAccess db;

	protected HSQLDatabase() {
		db = HSQLAccess.getInstance();
	}
	
	protected long getId(String table, String name) throws IllegalParameterException, SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT id FROM " + table  + " WHERE name = ?");
		statement.setString(1, name);
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
		{
			throw new IllegalParameterException(name, name + " not found in " + table);
		}
		return rs.getLong("id");
	}
	
	protected long lazyInsert(String table, String name) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT id FROM " + table  + " WHERE name = ?");
		statement.setString(1, name);
		ResultSet rs = statement.executeQuery();
		if (!rs.next()) {
			// Insert
			PreparedStatement insertStatement = db.getPreparedStatement("INSERT INTO " + table + " (name) " +
							"VALUES (?);");
			insertStatement.setString(1, name);
			insertStatement.executeUpdate();
			
//			statement = db.getPreparedStatement("SELECT id FROM " + table  + " WHERE name = ?");
//			statement.setString(1, name);
			ResultSet result = statement.executeQuery();
			
			if (!result.next())
				throw new SQLException("Not created");
			
			return result.getLong(1);
		}
		else {
			return rs.getLong("id");
		}
	}
}
