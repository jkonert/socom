package de.tud.kom.socom.components.social;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import de.tud.kom.socom.util.attributemapping.AttributeMap;
import de.tud.kom.socom.util.datatypes.NetworkPost;
import de.tud.kom.socom.util.datatypes.NetworkPostSupport;
import de.tud.kom.socom.util.datatypes.Profile;
import de.tud.kom.socom.util.exceptions.MediaTypeNotSupportedException;
import de.tud.kom.socom.util.exceptions.MissingTokenException;
import de.tud.kom.socom.util.exceptions.NotImplementedException;
import de.tud.kom.socom.util.exceptions.SocialNetworkUnsupportedException;

/**
 * 
 * @author rhaban
 * 
 */
public class GooglePlusConnection implements SNConnection {

	public GooglePlusConnection() {
	}

	@Override
	public List<Profile> getFriends(long uid) throws NotImplementedException {
		throw new NotImplementedException("getFriends", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public String getLoginURL(long uid, long gameinstid) throws NotImplementedException {
		throw new NotImplementedException("initiateLogin", SOCIALNETWORK_GOOGLEPLUS);
	}

	public void requestAccessToken(String code, long uid) {
	}

	@Override
	public boolean login(long uid) throws MissingTokenException, NotImplementedException {
		throw new NotImplementedException("login", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public void logout(long uid) throws SQLException {
	}

	public boolean isLoggedIn(boolean page, long uid) throws NotImplementedException {
		throw new NotImplementedException("isLoggedIn", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public boolean comment(long uid, String postID, String message) throws NotImplementedException {
		throw new NotImplementedException("comment", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public NetworkPost readPost(long uid, String postID) throws NotImplementedException {
		throw new NotImplementedException("readPost", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public List<NetworkPost> readComments(long uid, String postId) throws NotImplementedException {
		throw new NotImplementedException("readComments", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public NetworkPostSupport getSupports(long uid, String postId) throws NotImplementedException {
		throw new NotImplementedException("countLikes", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public void getAttributes(long uid, String snuid, AttributeMap atts) throws NotImplementedException {
		throw new NotImplementedException("getAttributes", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public String getPhotoThumbnailUrl(String snuid) throws NotImplementedException {
		throw new NotImplementedException("getPhotoThumbnailUrl", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public boolean deletePost(long uid, String postId) throws NotImplementedException {
		throw new NotImplementedException("deletePost", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public void updateFriendList(long uid) throws NotImplementedException {
		throw new NotImplementedException("updateFriendList", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public void saveAccessToken(long uid, String param) throws NotImplementedException 
	{
		throw new NotImplementedException("saveAccessToken", SOCIALNETWORK_GOOGLEPLUS);	
	}

	@Override
	public String publishOnFeed(boolean publishOnPage, String message, long uid) throws NotImplementedException,
			SocialNetworkUnsupportedException, SQLException {
		throw new NotImplementedException("publishOnFeed", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public String publishOnFeedWithUrl(boolean publishOnPage, String message, long uid, String url)
			throws NotImplementedException, SocialNetworkUnsupportedException, SQLException {
		throw new NotImplementedException("publishOnFeedWithUrl", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public String publishOnFeedWithMedia(boolean publishOnPage, String type, String message,
			InputStream is, long uid) throws NotImplementedException, MediaTypeNotSupportedException,
			SocialNetworkUnsupportedException, SQLException {
		throw new NotImplementedException("publishMediaOnFeed", SOCIALNETWORK_GOOGLEPLUS);
	}

	@Override
	public String getGamePageToken(long uid, long gameinstid, String pageidentifier) throws IOException, JSONException, NotImplementedException {
		throw new NotImplementedException("getGamePageToken", SOCIALNETWORK_GOOGLEPLUS);
	}
}
