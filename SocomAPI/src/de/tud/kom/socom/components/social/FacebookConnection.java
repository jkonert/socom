package de.tud.kom.socom.components.social;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.database.game.HSQLGameDatabase;
import de.tud.kom.socom.database.user.HSQLUserDatabase;
import de.tud.kom.socom.database.user.UserDatabase;
import de.tud.kom.socom.facebook.predef.FBDeletePredefs;
import de.tud.kom.socom.facebook.predef.FBIdentities;
import de.tud.kom.socom.facebook.predef.FBMediaType;
import de.tud.kom.socom.facebook.predef.FBPublishPredefs;
import de.tud.kom.socom.facebook.predef.FBReadPredefs;
import de.tud.kom.socom.util.Logger;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.attributemapping.AttributeMap;
import de.tud.kom.socom.util.attributemapping.networkparsing.AttributeParser;
import de.tud.kom.socom.util.attributemapping.networkparsing.FacebookAttributeParser;
import de.tud.kom.socom.util.datatypes.NetworkPost;
import de.tud.kom.socom.util.datatypes.NetworkPostSupport;
import de.tud.kom.socom.util.datatypes.Profile;
import de.tud.kom.socom.util.exceptions.ContentDeletedException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.MediaTypeNotSupportedException;
import de.tud.kom.socom.util.exceptions.MissingTokenException;
import de.tud.kom.socom.util.exceptions.NoSNConnectionException;
import de.tud.kom.socom.util.exceptions.NotImplementedException;
import de.tud.kom.socom.util.exceptions.PostNotAvailableException;
import de.tud.kom.socom.util.exceptions.SocialNetworkException;
import de.tud.kom.socom.util.exceptions.SocialNetworkUnsupportedException;
import de.tud.kom.socom.util.exceptions.UserNotFoundException;

/**
 * 
 * @author rhaban
 * 
 */
public class FacebookConnection implements SNConnection, GlobalConfig {

	private String OAUTH_URL;
	private String TOKEN_REQUEST_URL;
	
	private long gameinstid;

	private static Map<Long, FacebookConnection> connectionPerGame;
	// managing every client. every player needs to have its own FacebookClient
	// to connect
	private static UserDatabase user_db;
	private static Logger logger;

	public FacebookConnection(long gameinstid) throws NoSNConnectionException {
		if(user_db == null) user_db = HSQLUserDatabase.getInstance();
		if(logger == null) logger = LoggerFactory.getLogger();
		this.gameinstid = gameinstid;
		
		try {
			gatherAppInformation(gameinstid);
		} catch (SQLException e) {
			throw new NoSNConnectionException();
		}
	}

	private void gatherAppInformation(long gameinstid) throws NoSNConnectionException, SQLException {
		long appid = HSQLGameDatabase.getInstance().getSNAppId(SOCIALNETWORK_FACEBOOK, gameinstid);
		String appsecret = HSQLGameDatabase.getInstance().getSNAppSecret(SOCIALNETWORK_FACEBOOK, gameinstid);
		String token_redirect_url = HSQLGameDatabase.getInstance().getSNTokenRedirectUrl(SOCIALNETWORK_FACEBOOK, gameinstid);
		
		this.OAUTH_URL = "https://www.facebook.com/dialog/oauth?"
					+ "client_id=" + appid
					+ "&redirect_uri=" + token_redirect_url
					+ "&scope=user_about_me,publish_stream,read_stream,offline_access,read_friendlists,manage_friendlists,manage_pages,photo_upload&state=";
		this.TOKEN_REQUEST_URL = "https://graph.facebook.com/oauth/access_token?"
				+ "client_id=" + appid + "&redirect_uri=" + token_redirect_url + "&client_secret="
				+ appsecret + "&code=";
	}
	
	private String getFBIdentifier(boolean page, String id) {
		return this.gameinstid + (page?"_page_":"_user_") + id; 
	}
	
	/**
	 * @param uid
	 * @param page true if on behalf of a page or false if directly a user
	 * @return true if the user has a facebook client
	 */
	private boolean containsClient(boolean page, String uid) {
		return FBIdentities.getFBIdent(getFBIdentifier(page, uid)) != null;
	}

	/**
	 * creates a FacebookClient for the user
	 * 
	 * @param uid
	 * @param page true if on behalf of a page or false if directly a user
	 * @param token
	 */
	private synchronized void addClient(boolean page, String uid, String token) {
		FBIdentities.addFBIdent(getFBIdentifier(page, uid), token);
	}

	/**
	 * removes the FacebookClient for the user
	 * @param uid
	 * @param page true if on behalf of a page or false if directly a user
	 */
	private synchronized void removeClient(boolean page, String uid) {
		FBIdentities.removeFBIdent(getFBIdentifier(page, uid));
	}

	/**
	 * @param uid
	 * @param page true if on behalf of a page or false if directly a user
	 * @return the facebook client
	 */
	private synchronized FBIdentities getClient(boolean page, String uid) {
		return FBIdentities.getFBIdent(getFBIdentifier(page, uid));
	}

	@Override
	public List<Profile> getFriends(long uid) throws NotImplementedException, JSONException {
		List<Profile> result = new LinkedList<Profile>();
		try {
			JSONObject fbresult = FBReadPredefs.executeReadFriends(getClient(false, String.valueOf(uid)));
			JSONArray friends = fbresult.getJSONArray("data");

			for (int i = 0; i < friends.length(); i++) {
				result.add(new Profile(friends.getJSONObject(i).getString("name"), SOCIALNETWORK_FACEBOOK,
						friends.getJSONObject(i).getString("id")));
			}
		} catch (IOException e) {
			logger.Error(e);
		}

		return result;
	}
	
	@Override
	public String getLoginURL(long uid, long gameinstid) throws NotImplementedException, UserNotFoundException {
		String secret = null;
		try {
			secret = HSQLUserDatabase.getInstance().getUsersSecretEncrypted(uid);
		} catch (SQLException e) {
			logger.Error(e);
		}
		String uidsecret = uid + "-" + secret;

		return OAUTH_URL + SOCIALNETWORK_FACEBOOK + ";" + gameinstid + ";" + uidsecret;
	}

	/**
	 * request with the given code an access token for the user with userID and
	 * finally saves all information in the database
	 */
	public void requestAccessToken(String code, long uid) {
		try {
			URL url = new URL(TOKEN_REQUEST_URL + code);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String readLine = reader.readLine();
			String token = readLine.substring(13); // "access_token=".length()
			token = token.split("&")[0];
			saveAccessToken(uid, token);
		} catch (IOException e) {
			logger.Error(e);
		} catch (JSONException e) {
			logger.Error(e);
		}
	}

	@Override
	/**
	 * see: https://developers.facebook.com/docs/reference/api/page/#page_access_tokens
	 */
	public String getGamePageToken(long uid, long gameinstid, String pageidentifier) throws IOException, JSONException, SQLException {
		String access_token = user_db.getSNToken(uid, gameinstid, "facebook");
		URL url = new URL("https://graph.facebook.com/me/accounts?access_token=" + access_token);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer responseBuffer = new StringBuffer();
		String line;
		while((line = reader.readLine()) != null)
			responseBuffer.append(line);
		JSONObject response = new JSONObject(responseBuffer.toString());
		JSONArray data = response.getJSONArray("data");
		for(int i = 0; i < data.length(); i++){
			JSONObject dataObject = data.getJSONObject(i);
			if(dataObject.getString("id").equals(pageidentifier)){
				String page_token = dataObject.getString("access_token");
				addClient(true, String.valueOf(uid), page_token);
				return page_token;
			}
		}
		return null;
	}

	@Override
	public void saveAccessToken(long uid, String token) throws IOException, JSONException {
		try {
			/* create FacebookClient for Connections */
			addClient(false, String.valueOf(uid), token);
			/* save token for userid */
			user_db.addNetworkIdentification(uid, SOCIALNETWORK_FACEBOOK, gameinstid, getUsersNetworkID(uid), token);

		} catch (SocialNetworkUnsupportedException e) {
			logger.Error(e);
		} catch (SQLException e) {
			logger.Error(e);
		}

	}

	@Override
	public void updateFriendList(long uid) throws NotImplementedException, JSONException {
		if (containsClient(false, String.valueOf(uid))) {
			List<Profile> friends = getFriends(uid);
			try {
				user_db.addSNFriends(uid, SOCIALNETWORK_FACEBOOK, friends, true);
				// friendships in facebook are two-way friendships
			} catch (SQLException e) {
				logger.Error(e);
			}
		}
	}

	@Override
	public boolean login(long uid) throws MissingTokenException, ContentDeletedException,
			IllegalAccessException {
		if (isLoggedIn(false, uid))
			return true;
		try {
			String token = user_db.getSNToken(uid, gameinstid, SOCIALNETWORK_FACEBOOK);
			if (token == null || token.length() <= 1)
				return false;

			addClient(false, String.valueOf(uid), token);
		} catch (SQLException e) {
			logger.Error(e);
		}
		return isLoggedIn(false, uid);
	}

	/**
	 * log user out: - delete access token & delete FacebookClient for this user
	 */
	public void logout(long uid) throws SQLException {
		try {
			user_db.removeNetworkToken(uid, gameinstid, SOCIALNETWORK_FACEBOOK);
		} catch (SocialNetworkUnsupportedException e) {
			logger.Error(e);
		}
		removeClient(false, String.valueOf(uid));
	}

	/**
	 * true if the user is logged in here
	 */
	public boolean isLoggedIn(boolean page, long uid) {
		return containsClient(page, String.valueOf(uid));
	}

	@Override
	public String publishOnFeed(boolean publishOnPage, String message, long uid) throws SocialNetworkUnsupportedException, SQLException, JSONException, IOException, SocialNetworkException { 
		if(publishOnPage && !isLoggedIn(publishOnPage, uid)) {
			reloginPage(uid);
		}
		
		FBIdentities ident = getClient(publishOnPage, String.valueOf(uid));
		JSONObject publishMessageResponse = FBPublishPredefs.executePublishMessage(ident, message);
		if(publishMessageResponse.has("error"))
		{
			throw new SocialNetworkException(publishMessageResponse);
		}
		return publishMessageResponse.getString("id");
	}

	private void reloginPage(long uid) throws SQLException, SocialNetworkUnsupportedException {
		String pageid = HSQLGameDatabase.getInstance().getSocialNetworkPageId("facebook", uid, gameinstid);
		String pagetoken = HSQLGameDatabase.getInstance().getSocialNetworkPageToken("facebook", uid, gameinstid);
		addClient(true, pageid, pagetoken);
	}

	/**
	 * take some time after upload to be processed by facebook
	 * @throws SQLException 
	 * @throws SocialNetworkUnsupportedException 
	 * @throws JSONException, IOException 
	 * @throws SocialNetworkException 
	 */	
	@Override
	public String publishOnFeedWithMedia(boolean publishOnPage, String type, String message, InputStream is, long uid) 
			throws NotImplementedException, MediaTypeNotSupportedException, SocialNetworkUnsupportedException, SQLException, JSONException, IOException, SocialNetworkException {
		if (!(type.equalsIgnoreCase("videos") || type.equalsIgnoreCase("photos")))
			throw new MediaTypeNotSupportedException(type);

		if(publishOnPage && !isLoggedIn(publishOnPage, uid)) {
			reloginPage(uid);
		}
	
		FBIdentities ident = getClient(publishOnPage, String.valueOf(uid));
		FBMediaType mediatype = FBMediaType.valueOf(type);
		JSONObject publishMessageResponse = FBPublishPredefs.executePublishLinkAndMediaMessage(ident, message, null, is, mediatype);
		if(publishMessageResponse.has("error"))
		{
			throw new SocialNetworkException(publishMessageResponse);
		}
		return publishMessageResponse.has("post_id") ? publishMessageResponse.getString("post_id") : publishMessageResponse.getString("id");
	}

	@Override
	public String publishOnFeedWithUrl(boolean publishOnPage, String message, long uid, String url) throws SocialNetworkUnsupportedException, SQLException, IOException, JSONException, SocialNetworkException {
		if(publishOnPage && !isLoggedIn(publishOnPage, uid)) {
			reloginPage(uid);
		}
	
		FBIdentities ident = getClient(publishOnPage, String.valueOf(uid));
		JSONObject publishMessageResponse = FBPublishPredefs.executePublishLinkMessage(ident, message, url);
		if(publishMessageResponse.has("error"))
		{
			throw new SocialNetworkException(publishMessageResponse);
		}
		return publishMessageResponse.getString("id");
	}

	@Override
	public boolean deletePost(long uid, String postId) throws NotImplementedException, IOException, JSONException, SocialNetworkException {
		JSONObject result = FBDeletePredefs.executeDeletePost(getClient(false, String.valueOf(uid)), postId);
		if(result.has("error"))
		{
			throw new SocialNetworkException(result);
		}
		return result.getBoolean("success");
	}

	@Override
	public boolean comment(long uid, String postID, String message) throws NotImplementedException, IOException, SocialNetworkException {
		try {
			JSONObject result = FBPublishPredefs.executePublishComment(getClient(false, String.valueOf(uid)), postID, message);
			if(result.has("error"))
			{
				throw new SocialNetworkException(result);
			}
			return result.has("id");
		} catch (com.restfb.exception.FacebookNetworkException e) {
			return false;
		}
	}

	@Override
	public NetworkPost readPost(long uid, String postID) throws NotImplementedException,
			PostNotAvailableException, JSONException, IOException, SocialNetworkException {
		FBIdentities ident = getClient(false, String.valueOf(uid));
		JSONObject result = FBReadPredefs.executeReadPost(ident, postID);
		if(result.has("error"))
		{
			throw new SocialNetworkException(result);
		}
		if(!result.has("id"))
			throw new PostNotAvailableException();
		String from = result.getJSONObject("from").getString("name");
		String fromid = result.getJSONObject("from").getString("id");
		String msg = result.has("message") ? result.getString("message") : null;
		String img = result.has("picture") ? result.getString("picture") : null;
		long likecount = FBReadPredefs.executeReadLikes(ident, postID).getLong("count");
		return new NetworkPost(fromid, from, msg, img, likecount);
	}

	@Override
	public List<NetworkPost> readComments(long uid, String postId) throws NotImplementedException,
			PostNotAvailableException, JSONException, IOException, SocialNetworkException {
		List<NetworkPost> res = new LinkedList<NetworkPost>();
		JSONObject result = FBReadPredefs.executeReadComments(getClient(false, String.valueOf(uid)), postId);
		if(result.has("error"))
		{
			throw new SocialNetworkException(result);
		}
		if(!result.has("data"))	
			throw new PostNotAvailableException();
		
		JSONArray comments = result.getJSONArray("data");
		for(int i = 0; i < comments.length(); i++){
			JSONObject comment = comments.getJSONObject(i);
			String from = comment.getJSONObject("from").getString("name");
			String fromId = comment.getJSONObject("from").getString("id");
			String msg = comment.getString("message");
			long likecount = comment.getLong("like_count");
			res.add(new NetworkPost(fromId, from, msg, likecount));
		}
		return res;
	}

	@Override
	public NetworkPostSupport getSupports(long uid, String postId) throws NotImplementedException,
			PostNotAvailableException, JSONException, IOException, SocialNetworkException {
		JSONObject result = FBReadPredefs.executeReadLikes(getClient(false, String.valueOf(uid)), postId);
		if(result.has("error"))
		{
			throw new SocialNetworkException(result);
		}
		if(!result.has("count"))
			throw new PostNotAvailableException();

		NetworkPostSupport support = new NetworkPostSupport(result.getLong("count"));
		for(int i = 0; i < result.getJSONArray("data").length(); i++) {
			JSONObject like = result.getJSONArray("data").getJSONObject(i);
			String id = like.getString("id");
			String name = like.getString("name");
			support.addSupporter(support.new PostSupporters(name, id));
		}
		return support;
	}

	@Override
	public void getAttributes(long uid, String snuid, AttributeMap atts) throws NotImplementedException, IOException, JSONException {
		JSONObject result = FBReadPredefs.executeReadUser(getClient(false, String.valueOf(uid)), snuid);
		AttributeParser parser = new FacebookAttributeParser(atts);
		parser.parseAttributes(result);
	}

	@Override
	public String getPhotoThumbnailUrl(String snuid) throws NotImplementedException, IOException {
		URL url = new URL("http://graph.facebook.com/" + snuid + "/picture?type=normal");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			String thumbUrl = conn.getURL().toString();
			conn.disconnect();
			return thumbUrl;
		}
		conn.disconnect();
		throw new IOException();
	}

	/**
	 * @param uid
	 * @return the network-specific id of the user.
	 * @throws IOException 
	 * @throws JSONException 
	 */
	private String getUsersNetworkID(long uid) throws IOException, JSONException {
		JSONObject user = FBReadPredefs.executeReadUser(getClient(false, String.valueOf(uid)));
		return user.getString("id");
	}
	
	public static FacebookConnection getConnection(long gameinstid) throws NoSNConnectionException {
		if(connectionPerGame == null)
			connectionPerGame = new HashMap<Long, FacebookConnection>();
		
		if(!connectionPerGame.containsKey(gameinstid)){
			System.out.println("game instance not found");
			connectionPerGame.put(gameinstid, new FacebookConnection(gameinstid));
		}
		
		return connectionPerGame.get(gameinstid);
	}

}
