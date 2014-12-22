package de.tud.kom.socom.components.social;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.util.attributemapping.AttributeMap;
import de.tud.kom.socom.util.datatypes.NetworkPost;
import de.tud.kom.socom.util.datatypes.NetworkPostSupport;
import de.tud.kom.socom.util.datatypes.Profile;
import de.tud.kom.socom.util.exceptions.ContentDeletedException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.MediaTypeNotSupportedException;
import de.tud.kom.socom.util.exceptions.MissingTokenException;
import de.tud.kom.socom.util.exceptions.NotImplementedException;
import de.tud.kom.socom.util.exceptions.PostNotAvailableException;
import de.tud.kom.socom.util.exceptions.SocialNetworkException;
import de.tud.kom.socom.util.exceptions.SocialNetworkUnsupportedException;
import de.tud.kom.socom.util.exceptions.UserNotFoundException;
 
public interface SNConnection extends GlobalConfig {
	
	public abstract String getLoginURL(long uid, long gameinstid) throws NotImplementedException, UserNotFoundException;
	public abstract void requestAccessToken(String code, long uid);
	public abstract boolean login(long uid) throws MissingTokenException, NotImplementedException, ContentDeletedException, IllegalAccessException;
	public abstract void logout(long uid) throws SQLException;
	public abstract boolean isLoggedIn(boolean page, long uid) throws NotImplementedException;
	public abstract List<Profile> getFriends(long uid) throws NotImplementedException, JSONException;
	public abstract void getAttributes(long uid, String snuid, AttributeMap atts) throws NotImplementedException, IOException, JSONException;
	public abstract String getPhotoThumbnailUrl(String snuid) throws NotImplementedException, IOException;
	
	public abstract String publishOnFeed(boolean publishOnPage, String message, long uid) throws NotImplementedException, SocialNetworkUnsupportedException, SQLException, JSONException, IOException, SocialNetworkException;
	public abstract String publishOnFeedWithUrl(boolean publishOnPage, String message, long uid, String url) throws NotImplementedException, SocialNetworkUnsupportedException, SQLException, IOException, JSONException, SocialNetworkException;
	public abstract String publishOnFeedWithMedia(boolean publishOnPage, String type, String message, InputStream is, long uid) throws NotImplementedException, MediaTypeNotSupportedException, SocialNetworkUnsupportedException, SQLException, JSONException, IOException, SocialNetworkException;
	
	public abstract boolean comment(long userID, String postID, String message) throws NotImplementedException, IOException, SocialNetworkException;
	public abstract NetworkPost readPost(long uid, String postID) throws NotImplementedException, PostNotAvailableException, JSONException, IOException, SocialNetworkException;
	public abstract List<NetworkPost> readComments(long uid, String postId) throws NotImplementedException, PostNotAvailableException, JSONException, IOException, SocialNetworkException;
	public abstract NetworkPostSupport getSupports(long uid, String postId) throws NotImplementedException, PostNotAvailableException, JSONException, IOException, SocialNetworkException;
	public abstract boolean deletePost(long uid, String postId) throws NotImplementedException, IOException, JSONException, SocialNetworkException;
	public abstract void updateFriendList(long uid) throws NotImplementedException, JSONException;
	public abstract String getGamePageToken(long uid, long gameinstid, String pageidentifier) throws IOException, JSONException, NotImplementedException, SQLException;
	/** to be called directly by ClientGUI etc to store a AccessToken retrieved otherwise 
	 * @throws JSONException 
	 * @throws IOException */
	public abstract void saveAccessToken(long uid, String token) throws NotImplementedException, IOException, JSONException;

}
