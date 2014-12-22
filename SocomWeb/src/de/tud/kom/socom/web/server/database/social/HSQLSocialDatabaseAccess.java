package de.tud.kom.socom.web.server.database.social;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.server.database.HSQLAccess;

public class HSQLSocialDatabaseAccess implements SocialDatabaseAccess {

	private static SocialDatabaseAccess instance = new HSQLSocialDatabaseAccess();
	private static HSQLAccess db;

	private HSQLSocialDatabaseAccess() {
		db = HSQLAccess.getInstance();
	}

	public static SocialDatabaseAccess getInstance() {
		return instance;
	}

	@Override
	public LoginResult getLoginInformationUsingNetworkId(String network, String snuid) throws SQLException {
		String query = "SELECT " +
					"uid, name, isadmin, password " +
				"FROM ((users INNER JOIN usersnaccounts ON usersnaccounts.uid = users.uid) " +
					"INNER JOIN socialnetworks ON socialnetworks.id = usersnaccounts.snid) " +
				"WHERE " +
					"(deleted = 0 OR isadmin) " +
					"AND UPPER(socialnetworks.name) = UPPER('" + network + "') " +
					"AND usersnaccounts.username = '" + snuid + "';";

		ResultSet rs = db.execQueryWithResult(query);
		if(!rs.next())
			return new LoginResult(false);
		
		LoginResult result = new LoginResult(true, rs.getBoolean("isadmin"), rs.getString("name"), rs.getLong("uid"), 0);
		return result;
	}

	@Override
	public long getAppId(String network, String game) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT appid FROM socialnetworkapps WHERE " +
				"gameid = (SELECT gameid FROM games WHERE UPPER(name) = UPPER(?)) " +
				"AND " +
				"sn = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?));");
		statement.setString(1, game);
		statement.setString(2, network);
		System.out.println(statement);
		ResultSet rs = statement.executeQuery();
		rs.next(); //FIXME if false?
		return rs.getLong(1);
	}

	@Override
	public String getGeneralRedirectUrl(String network, String game) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT general_redirect_url FROM socialnetworkapps WHERE " +
				"gameid = (SELECT gameid FROM games WHERE UPPER(name) = UPPER(?)) " +
				"AND " +
				"sn = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?));");
		statement.setString(1, game);
		statement.setString(2, network);
		ResultSet rs = statement.executeQuery();
		rs.next();
		return rs.getString(1);
	}

	@Override
	public String getAppSecret(String network, String game) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT appsecret FROM socialnetworkapps WHERE " +
				"gameid = (SELECT gameid FROM games WHERE UPPER(name) = UPPER(?)) " +
				"AND " +
				"sn = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?));");
		statement.setString(1, game);
		statement.setString(2, network);
		ResultSet rs = statement.executeQuery();
		rs.next();
		return rs.getString(1);
	}
}
