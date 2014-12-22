package de.tud.kom.socom.web.server.database.login;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.server.database.HSQLAccess;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

public class HSQLLoginDatabaseAccess implements LoginDatabaseAccess{
	
	private static LoginDatabaseAccess instance = new HSQLLoginDatabaseAccess();
	private Logger logger = LoggerFactory.getLogger();
	private static HSQLAccess db;

	private HSQLLoginDatabaseAccess() {
		db = HSQLAccess.getInstance();
	}

	public static LoginDatabaseAccess getInstance() {
		return instance;
	}
	
	@Override
	public LoginResult validateLogin(String username, String hashpassword) {
		String query = "SELECT uid, name, isadmin, deleted FROM users WHERE name = '" + username + "' AND password = '"+ hashpassword + "';";
		return getLoginResult(query);
	}

	@Override
	public LoginResult validateLogin(long uid, String hashpassword) {
		String query = "SELECT uid, name, isadmin, deleted FROM users WHERE uid = '" + uid + "' AND password = '"+ hashpassword + "';";
		return getLoginResult(query);
	}

	private LoginResult getLoginResult(String query) {
		try {
			ResultSet rs = db.execQueryWithResult(query);
			if(rs.next())
				return new LoginResult(true, rs.getBoolean("isadmin"), rs.getString("name"), rs.getLong("uid"), rs.getInt("deleted"));
			else return new LoginResult(false);
		} catch (SQLException e) {
			logger.Error(e);
			return new LoginResult(false);
		}
	}
}
