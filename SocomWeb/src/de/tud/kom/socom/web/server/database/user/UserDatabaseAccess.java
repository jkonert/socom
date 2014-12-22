package de.tud.kom.socom.web.server.database.user;

import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.GameContext;
import de.tud.kom.socom.web.client.sharedmodels.SimpleUser;
import de.tud.kom.socom.web.client.sharedmodels.User;

public interface UserDatabaseAccess {

	public User getUser(long userId, long userId2, boolean includeDeleted);

	public String getUserName(long uid);

	public boolean setProfileVisibility(long userId, int selectedIndex);

	public List<GameContext> getUserHistory(long userId, long gameInstId);

	public boolean userIsAdmin(long userId);
	
	public String[] getDeletedStates();
	
	public SimpleUser getSimpleUserByName(String name);
	
	public List<SimpleUser> getSimpleUsersByName(String startingWith);
	
	public boolean changeUserDeletionFlag(long uid, int flag);

	public boolean isFriendOf(long uid, long friendid);

	long getUserId(String userName);
	
	public String getUsersSecretEncrypted(long uid);

}
