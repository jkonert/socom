package de.tud.kom.socom.database.user;

import java.sql.SQLException;
import java.util.List;

import de.tud.kom.socom.components.game.GameInstance;
import de.tud.kom.socom.util.datatypes.Profile;
import de.tud.kom.socom.util.datatypes.User;
import de.tud.kom.socom.util.datatypes.UserMetadata;
import de.tud.kom.socom.util.exceptions.ContentAlreadyExistsException;
import de.tud.kom.socom.util.exceptions.ContentDeletedException;
import de.tud.kom.socom.util.exceptions.ContentNotFoundException;
import de.tud.kom.socom.util.exceptions.SocomException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.SocialNetworkUnsupportedException;
import de.tud.kom.socom.util.exceptions.UIDOrSecretNotValidException;
import de.tud.kom.socom.util.exceptions.UserAlreadyExistsException;
import de.tud.kom.socom.util.exceptions.UserNotFoundException;

public interface UserDatabase {

	public boolean validateUser(long uid, String password) throws SQLException, UIDOrSecretNotValidException;

	public long[] validateUser(long gameinstanceid, String username, String password) throws SQLException, UIDOrSecretNotValidException,
			ContentDeletedException;

	public long[] createUser(long gameinstanceid, String username, String password, int visibility) throws SQLException, UserAlreadyExistsException;

	public boolean deleteUser(long uid, String password) throws SQLException, SocomException;

	public User fetchUser(long idSelf, long uid) throws SQLException, UserNotFoundException, ContentDeletedException, IllegalAccessException;

	public String getUsersSecretEncrypted(long uid) throws SQLException, UserNotFoundException;

	public void becomeAdmin(long uid, String password) throws SQLException, UIDOrSecretNotValidException;

	public void updateUsersGame(long uid, long gameInstanceId) throws SQLException;

	public List<GameInstance> getUsersGames(long uid) throws SQLException;

	public int getIDOf(String networkname, String networkID) throws SQLException, UserNotFoundException, SocialNetworkUnsupportedException;

	public void addNetworkIdentification(long uid, String networkname, long gameinstid, String networkID, String networkToken) throws SQLException,
			SocialNetworkUnsupportedException;

	public String getSNToken(long uid, long gameinstid, String networkname) throws SQLException;

	public void removeNetworkToken(long uid, long gameinstid, String networkname) throws SQLException, SocialNetworkUnsupportedException;

	public void setUserOffline(long uid) throws SQLException;

	public void setUserOnline(long uid) throws SQLException;

	public void createMetadata(long uid, String key, String value, int visibility) throws SQLException, ContentAlreadyExistsException;

	public void updateMetadata(long uid, String key, String value, int visibility) throws SQLException, ContentNotFoundException;

	public void deleteMetadata(long uid, String key, int deletedId) throws SQLException, ContentNotFoundException;

	public List<UserMetadata> fetchMetadata(long currentUid, long ofUid) throws SQLException;

	public String getNextGeneratableUserName() throws SQLException;

	public void addSNFriends(long uid, String socialnetworkFacebook, List<Profile> friends, boolean twoway) throws SQLException;

	public boolean isFriendOf(long uid, long friendid) throws SQLException;

	public void changeUsername(long uid, String password, String newUsername) throws SQLException, UserAlreadyExistsException, UIDOrSecretNotValidException;

	public void changePassword(long uid, String password, String newPassword) throws SQLException, UIDOrSecretNotValidException;

	public void changeVisibility(long uid, String password, int visibility) throws SQLException, UIDOrSecretNotValidException;

}
