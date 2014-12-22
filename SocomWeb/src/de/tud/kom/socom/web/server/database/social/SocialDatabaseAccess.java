package de.tud.kom.socom.web.server.database.social;

import java.sql.SQLException;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;

public interface SocialDatabaseAccess {

	public LoginResult getLoginInformationUsingNetworkId(String network, String snuid) throws SQLException;

	public long getAppId(String network, String game) throws SQLException;
	
	public String getGeneralRedirectUrl(String network, String game) throws SQLException;
	
	public String getAppSecret(String network, String game) throws SQLException;
}
