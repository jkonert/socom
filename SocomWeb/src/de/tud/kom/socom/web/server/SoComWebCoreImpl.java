package de.tud.kom.socom.web.server;

import de.tud.kom.socom.web.client.services.core.SocomWebCoreService;
import de.tud.kom.socom.web.client.sharedmodels.User;
import de.tud.kom.socom.web.server.database.user.HSQLUserDatabaseAccess;
import de.tud.kom.socom.web.server.database.user.UserDatabaseAccess;
import de.tud.kom.socom.web.server.util.Hasher;

@SuppressWarnings("serial")
public class SoComWebCoreImpl extends SoComService implements SocomWebCoreService {

	private final UserDatabaseAccess db = HSQLUserDatabaseAccess.getInstance();

	@Override
	public User getUser(long userId, long userId2) {
		return db.getUser(userId, userId2, false);
	}

	@Override
	public String getUserName(long uid) {
		return db.getUserName(uid);
	}

	@Override
	public boolean setProfileVisibility(long userId, int selectedIndex) {
		return db.setProfileVisibility(userId, selectedIndex);
	}

	@Override
	public String getSHA(String input) {
		return Hasher.getSHA(input);
	}

	@Override
	public boolean isFriendOf(long userId, long friendId) {
		return db.isFriendOf(userId, friendId);
	}
	
	
}