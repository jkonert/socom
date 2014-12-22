package de.tud.kom.socom.components.social;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.SocomComponent;
import de.tud.kom.socom.database.game.GameDatabase;
import de.tud.kom.socom.database.game.HSQLGameDatabase;
import de.tud.kom.socom.database.influence.HSQLInfluenceDatabase;
import de.tud.kom.socom.database.user.HSQLUserDatabase;
import de.tud.kom.socom.util.EasyEncrypter;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.Logger;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.ResourceLoader;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.attributemapping.AttributeMap;
import de.tud.kom.socom.util.attributemapping.UniqueAttribute;
import de.tud.kom.socom.util.datatypes.NetworkPost;
import de.tud.kom.socom.util.datatypes.NetworkPostSupport;
import de.tud.kom.socom.util.datatypes.Profile;
import de.tud.kom.socom.util.exceptions.ContentDeletedException;
import de.tud.kom.socom.util.exceptions.CookieNotFoundException;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;
import de.tud.kom.socom.util.exceptions.MissingTokenException;
import de.tud.kom.socom.util.exceptions.NoSNConnectionException;
import de.tud.kom.socom.util.exceptions.NotImplementedException;
import de.tud.kom.socom.util.exceptions.SocialNetworkUnsupportedException;
import de.tud.kom.socom.util.exceptions.SocomException;

/**
 * 
 * @author rhaban
 * 
 */
public class SocialNetworkManager extends SocomComponent implements GlobalConfig {
	private static final Logger logger = LoggerFactory.getLogger();

	private static final String URL_PATTERN = "social";
	private static SocialNetworkManager instance = new SocialNetworkManager();

	private SocialNetworkManager() {
	}

	public static SocialNetworkManager getInstance() {
		return instance;
	}

	/**
	 * Receive an URL to log into a social network. Needs the users id from
	 * session.
	 * 
	 * @param network
	 *            (string of social network)
	 * @return The URL
	 * @throws JSONException
	 */
	public int loginURL(SocomRequest req) throws JSONException, SocomException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String network = req.getParam("network");
		SNConnection nwconn = getConnection(network, gameinstid);
		if (nwconn == null)
			return 4;

		JSONObject json = new JSONObject().put("url", nwconn.getLoginURL(uid, gameinstid));
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}

	/**
	 * Method used from Facebook to send users access token. Do not use.
	 * @throws IOException 
	 */
	public int requestToken(SocomRequest req) throws JSONException, SQLException, SocomException, IOException {
		String code = req.getParam("code");
		String identifier = req.getParam("state");

		String[] parts = identifier.split(";");
		if (parts.length != 3)
			return 4;

		String network = parts[0];
		long gameinstid = Long.valueOf(parts[1]);
		String[] idParts = parts[2].split("-");
		long uid = Long.valueOf(idParts[0]);

		byte[] b = new byte[idParts.length - 1];
		for (int i = 1; i < idParts.length; i++) {
			b[i - 1] = (byte) Integer.parseInt(idParts[i], 16);
		}
		String password = EasyEncrypter.getInstance().decryptString(b);
		boolean valid = HSQLUserDatabase.getInstance().validateUser(uid, password);

		if (!valid)
			throw new ContentDeletedException();

		SNConnection nw = getConnection(network, gameinstid);
		if (uid < 0 || nw == null)
			return 4;

		// special case implementation to directly add tokens to Database
		if (code.equals(GlobalConfig.SOCIALNETWORK_CODE_DIRECT_TOKEN_SAVE)) {
			nw.saveAccessToken(uid, req.getParam("token"));
		} else {
			nw.requestAccessToken(code, uid);
		}
		nw.updateFriendList(uid);

		String gameident = HSQLGameDatabase.getInstance().getGameIdentifier(gameinstid);
		req.addOutput("<head>\n<meta http-equiv=\"refresh\" content=\"0; URL=../../web/#/" + gameident + "/profiles/nwsucc-"
				+ network + "\">\n</head>");
		return 0;
	}

	/**
	 * Creates a Connection-Client-Object for the current user, used for example
	 * when the middleware was restarted. Normally you do not need to use this
	 * method
	 */
	public int relogin(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String network = req.getParam("network");
		if (network == null)
			return 4; // unexpected parameter

		SNConnection nw = getConnection(network, gameinstid);
		if (nw == null)
			return 4;

		boolean success = nw.login(uid);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}

	/**
	 * Logs the current user out of given network. Means: deletes the token and
	 * Connection-Client-Object.
	 * 
	 * @param network
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int logout(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String network = req.getParam("network");
		if (network == null)
			return 4; // unexpected parameter

		SNConnection nw = getConnection(network, gameinstid);
		if (nw == null)
			return 4;

		nw.logout(uid);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Show whether the user is logged in into the given network, so that its
	 * possible to use network-methods. Needs the users id from session.
	 * 
	 * @param network
	 * @return Network-boolean connection if the user is logged in in the
	 *         specific network
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int isLoggedIn(SocomRequest req) throws JSONException, SQLException, SocomException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String network = req.getParam("network");

		if (network == null)
			return 4; // unexpected parameter

		SNConnection nw = getConnection(network, gameinstid);
		if (nw == null)
			return 4;

		if (!nw.isLoggedIn(false, uid))
			nw.login(uid);

		JSONObject json = new JSONObject();
		json.put("loggedin", nw.isLoggedIn(false, uid));
		json.put("network", network.toString());
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}
	
	/**
	 * Connect a socialnetwork application to a socom-game
	 * 
	 * @param network
	 * @param game name of the game
	 * @param password games password
	 * @param app_id socialnetwork specific application id
	 * @param app_secret socielnetwork specific application secret
	 * @param autofill_redirects optional (default true): if true the 
	 * 			token_redirect_url and general_redirect_url are filled 
	 * 			auto automatically which may cause unusability when different
	 * 			token-request methods are used
	 * @param token_redirect_url optional (only when autofill_.. is false) default: http://<SOCOM_HOST>/servlet/social/requestToken
	 * @param general_redirect_url optional (only when autofill_.. is false) default: http://<SOCOM_HOST>/web
	 * @return success json
	 * @throws IllegalParameterException
	 * @throws SocomException
	 * @throws SQLException
	 */
	public int connectGameApp(SocomRequest req) throws IllegalParameterException, SocomException, SQLException {
		String network = req.getParam("network");
		boolean networkValid = false;
		for(String nw : SOCIALNETWORK_ALL)
		{
			if(network.equalsIgnoreCase(nw))
			{
				networkValid = true;
				break;
			}
		}
		
		if(!networkValid)
			throw new IllegalParameterException("network");
		
		String game = req.getParam("game");
		String gamepassword = req.getParam("password");
		long appId = Long.parseLong(req.getParam("app_id"));
		String appSecret = req.getParam("app_secret");
		boolean autofillRedirects = req.getParam("autofill_redirects", true);
		String token_redirect = null, general_redirect = null;
		if(!autofillRedirects)
		{
			token_redirect = req.getParam("token_redirect_url");
			general_redirect = req.getParam("general_redirect_url");
		}
		
		GameDatabase gameDB = HSQLGameDatabase.getInstance();
		long gameId = gameDB.authenticateGame(game, gamepassword);
		boolean success = gameDB.addSNApp(network, gameId, appId, appSecret, autofillRedirects, token_redirect, general_redirect);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}
	
	/**
	 * Connects a page managed by the current user to the current game so that 
	 * postings to the pages feed are possible 
	 * 
	 * The current user has to be connected with the network in the specific game
	 * 
	 * @param network
	 * @param gamepassword
	 * @param pageidentifier socialnetwork-specific identifier of the page
	 * @return success boolean
	 * @throws SocomException
	 * @throws SQLException
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public int connectGamePage(SocomRequest req) throws SocomException, SQLException, IOException, JSONException {
		long uid = req.getUid();
		String network = req.getParam("network");
		String gamepassword = req.getParam("gamepassword");
		String pageidentifier = req.getParam("pageidentifier");
		long gameinstid = req.getCurrentGameInst();
		
		HSQLGameDatabase.getInstance().authenticateGameInstance(gameinstid, gamepassword);
		SNConnection sn = getConnection(network, gameinstid);
		
		boolean isLoggedIn = sn.login(uid);
		if(!isLoggedIn)
		{
			throw new SocialNetworkUnsupportedException(network + " (user not authenticated) ");
		}
		String token = sn.getGamePageToken(uid, gameinstid, pageidentifier);
		if(token == null)
		{
			throw new SocialNetworkUnsupportedException("pageidentifier not visible to user");
		}
		
		boolean success = HSQLGameDatabase.getInstance().connectSocialNetworkPage(uid, gameinstid, network, pageidentifier, token);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}
	
	/**
	 * Shows network friends of every network he is logged in with their snuids.
	 * Needs the users id from session.
	 * 
	 * @return List of Network-Profiles
	 * @throws JSONException
	 */
	public int getNetworkFriends(SocomRequest req) throws JSONException, SocomException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		List<Profile> result = new LinkedList<Profile>();
		for (String nw : SOCIALNETWORK_ALL) {
			SNConnection nwconn = getConnection(nw, gameinstid);
			if (nwconn == null)
				return 4;
			try {
				nwconn.login(uid);
				if (nwconn.isLoggedIn(false, uid))
					for (Profile p : nwconn.getFriends(uid))
						result.add(p);
			} catch (NotImplementedException e) {
				continue;
			}
		}
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("profiles", result)));
		return 0;
	}

	/**
	 * Shows the networks, which are currently supported by Socom.
	 * 
	 * @return List of Networks
	 * @throws JSONException
	 * @throws
	 */
	public int getSupportedNetworks(SocomRequest req) throws JSONException, SocomException {
		JSONObject json = new JSONObject();
		json.put("networks", SOCIALNETWORK_ALL);
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}

	/**
	 * Shows user Information collected from the social networks. Needs the
	 * users id from session.
	 * 
	 * @return Attribute-Map
	 * @throws JSONException
	 * @throws IOException 
	 */
	public int getProfileData(SocomRequest req) throws JSONException, SocomException, IOException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		AttributeMap map = new AttributeMap();
		boolean atLeastOneNetwork = false;
		for (String nw : SOCIALNETWORK_ALL) {
			try {
				SNConnection nwconn = getConnection(nw, gameinstid);
				nwconn.login(uid);
				if (nwconn.isLoggedIn(false, uid)) {
					atLeastOneNetwork = true;
					nwconn.getAttributes(uid, "me", map);
					int friendCount = nwconn.getFriends(uid).size();
					map.addAttribute("friendCount", new UniqueAttribute(String.valueOf(friendCount)));
				}
			} catch (NoSNConnectionException e) {
				continue;
			} catch (NotImplementedException e) {
				continue;
			}
		}
		if(atLeastOneNetwork)
		{
			req.addOutput(JSONUtils.JSONToString(new JSONObject().put("data", map)));
		}
		else
		{
			req.addOutput(JSONUtils.JSONToString(new JSONObject().put("error", "no social network connected")));
		}
		return 0;
	}

	/**
	 * Shows user information collected from the specific network from the given
	 * usersnid.
	 * 
	 * @param network
	 * @param usersnid
	 * @return Attribute-Map
	 * @throws JSONException
	 * @throws SocomException
	 * @throws IOException 
	 */
	public int getProfileDataOf(SocomRequest req) throws JSONException, SocomException, IOException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String snuid = req.getParam("usersnid");
		String network = req.getParam("network");

		AttributeMap map = new AttributeMap();
		SNConnection nwconn = getConnection(network, gameinstid);

		nwconn.login(uid);
		if (nwconn.isLoggedIn(false, uid)) {
			nwconn.getAttributes(uid, snuid, map);
			req.addOutput(JSONUtils.JSONToString(new JSONObject().put("data", map)));
		}
		else
		{
			req.addOutput(JSONUtils.JSONToString(new JSONObject().put("error", "social network not connected")));
		}

		return 0;
	}

	/**
	 * Publish a text-message on a) the users sn-walls if somebody is logged in
	 * b) on games sn-walls
	 * 
	 * @param message
	 * @param publishonpage whether the post should be done on the games page instead 
	 * 				of users feed (see connectGamePage in UserManager)
	 * @return post-ids of posts made
	 * @throws JSONException
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public int publishOnFeed(SocomRequest req) throws JSONException, SocomException, SQLException, IOException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		boolean publishOnPage = req.getParam("publishonpage", false);
		String message = req.getParam("message");

		Map<String, String> result = new HashMap<String, String>();
		for (String nw : SOCIALNETWORK_ALL) {
			try {
				SNConnection nwconn = getConnection(nw, gameinstid);
				nwconn.login(uid);
				if (nwconn.isLoggedIn(publishOnPage, uid))
					result.put(nw, nwconn.publishOnFeed(publishOnPage, message, uid));
			} catch (NotImplementedException e) {
				continue;
			}
			//XXX maybe catch sn-network exception and let other networks allow continuing (may be executed even before)
		}
		JSONObject json = new JSONObject();
		json.put("ids", result);
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}

	/**
	 * Publish a Message (only text) on users walls including a url to the given
	 * influence.
	 * 
	 * @param message
	 * @param influence
	 *            Influence-ID
	 * @param publishonpage whether the post should be done on the games page instead 
	 * 				of users feed (see connectGamePage in UserManager)
	 * @return Post-Ids
	 * @throws JSONException
	 * @throws SQLException
	 * @throws IOException 
	 */
	public int publishInfluenceOnfeed(SocomRequest req) throws JSONException, SocomException,
			SQLException, IOException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String message = req.getParam("message");
		String influenceId = req.getParam("influence");
		boolean publishOnPage = req.getParam("publishonpage", false);
		// filter invisible or unfound
		HSQLInfluenceDatabase.getInstance().getInfluenceId(uid, influenceId);
		Map<String, String> result = publishInfluence(publishOnPage, uid, gameinstid, message, influenceId);
		JSONObject json = new JSONObject();
		json.put("ids", result);
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}

	/*
	 * should not be called from outside (not even possible since
	 * parameter-length != 1)
	 */
	public Map<String, String> publishInfluence(boolean publishOnPage, long uid, long gameinstid, String message, String influenceId)
			throws SocomException, SQLException, IOException, JSONException {
		Map<String, String> result = new HashMap<String, String>();
		String gameident = HSQLGameDatabase.getInstance().getGameIdentString(gameinstid);
		String inflUrl = ResourceLoader.buildPublicServerUrl() + "/web/#" + gameident + "/influence/" + influenceId;
		for (String nw : SOCIALNETWORK_ALL) {
			SNConnection nwconn = getConnection(nw, gameinstid);
			try {
				if(!publishOnPage) nwconn.login(uid);
				if (nwconn.isLoggedIn(publishOnPage, uid))
					result.put(nw, nwconn.publishOnFeedWithUrl(publishOnPage, message, uid, inflUrl));
			} catch (NotImplementedException e) {
				continue;
			}
		}
		return result;
	}

	/**
	 * POST-Method
	 * 
	 * Publish media-based content on users walls.
	 * 
	 * All Parameters must be given as cookies. Furthermore the HTTP-POST must
	 * include a stream containing the file which should be uploaded
	 * 
	 * @param message
	 * @param type
	 *            (photos/videos)
	 * @param extension
	 *            The file's extension
	 * @param publishonpage whether the post should be done on the games page instead 
	 * 				of users feed (see connectGamePage in UserManager)
	 * @return Post-Ids
	 * @throws JSONException
	 * @throws SQLException 
	 */
	public int publishMediaOnFeed(SocomRequest req) throws JSONException, SocomException, SQLException {
		try {
			long uid = req.getUid();
			long gameinstid = req.getCurrentGameInst();
			boolean publishOnPage = false;
			try{
				publishOnPage = Boolean.parseBoolean(req.getCookieVal("publishonpage"));
			} catch (CookieNotFoundException e) {	/* ok, use false */	}
			String message = req.getCookieVal("message");
			// type must be 'videos' or 'photos'
			String type = req.getCookieVal("type");
			InputStream is = req.getInputStream();
			
			Map<String, String> result = new HashMap<String, String>();

			for (String nw : SOCIALNETWORK_ALL) {
				SNConnection nwconn = getConnection(nw, gameinstid);
				try {
					if(!publishOnPage) nwconn.login(uid);
					if (nwconn.isLoggedIn(publishOnPage, uid)) {
						//FIXME: this might become a problem to read the stream multiple times when supporting multiple networks
						result.put(nw, nwconn.publishOnFeedWithMedia(publishOnPage, type, message, is, uid));
					}
				} catch (MissingTokenException e) {
					return e.getErrorCode();
				} catch (NotImplementedException e) {
					continue;
				}
			}
			JSONObject json = new JSONObject();
			json.put("ids", result);
			req.addOutput(JSONUtils.JSONToString(json));
			return 0;
		} catch (IOException e1) {
			logger.Error(e1);
			return 2;
		}
	}

	/**
	 * Deletes a post from the feed
	 * 
	 * @param post
	 *            postId
	 * @param network
	 *            sn
	 * @return success boolean
	 * @throws JSONException
	 * @throws SocomException
	 * @throws IOException 
	 */
	public int deletePost(SocomRequest req) throws JSONException, SocomException, IOException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String postId = req.getParam("post");
		String network = req.getParam("network");
		SNConnection nw = getConnection(network, gameinstid);

		boolean success = nw.deletePost(uid, postId);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}

	/**
	 * Shows a post and its comments (without containing media) and comments
	 * posted to the post. Needs the users id from session.
	 * 
	 * @param post
	 *            The id of the post to read
	 * @param network
	 * @return Post with its comments
	 * @throws JSONException
	 * @throws IOException 
	 */
	public int readPost(SocomRequest req) throws JSONException, SocomException, IOException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String postid = req.getParam("post");
		String nw = req.getParam("network");

		NetworkPost post = null;
		List<NetworkPost> comments = null;
		SNConnection nwconn = getConnection(nw, gameinstid);
		nwconn.login(uid);
		if (nwconn.isLoggedIn(false, uid)) {
			post = nwconn.readPost(uid, postid);
			comments = nwconn.readComments(uid, postid);
		}
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("post", post).put("comments", comments)));
		return 0;
	}

	/**
	 * Publish a comment on a post.
	 * 
	 * @param post
	 *            The Post id to comment on
	 * @param message
	 * @param network
	 * @return success boolean
	 * @throws JSONException
	 * @throws IOException 
	 */
	public int comment(SocomRequest req) throws JSONException, SocomException, IOException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String postid = req.getParam("post");
		String message = req.getParam("message");
		String nw = req.getParam("network");

		boolean success = false;
		SNConnection nwconn = getConnection(nw, gameinstid);
		nwconn.login(uid);
		if (nwconn.isLoggedIn(false, uid))
			success = nwconn.comment(uid, postid, message);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}

	/**
	 * Gets the count of Supporters (e.g. likes on facebook) to a specific post
	 * and gives information about supporters
	 * 
	 * @param post
	 * @param network
	 * @return count
	 * @throws JSONException
	 * @throws IOException 
	 */
	public int getSupporter(SocomRequest req) throws JSONException, SocomException, IOException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String postid = req.getParam("post");
		String nw = req.getParam("network");
		if (postid == null || postid.equals(""))
			throw new IllegalParameterException("post must be a longer string identifier than empty.");

		SNConnection nwconn = getConnection(nw, gameinstid);
		nwconn.login(uid);
		if (nwconn.isLoggedIn(false, uid)) {
			NetworkPostSupport support = nwconn.getSupports(uid, postid);
			req.addOutput(JSONUtils.JSONToString(new JSONObject().put("support", support)));
		} else {
			req.addOutput(JSONUtils.getSuccessJsonString(false));
		}
		return 0;
	}

	/**
	 * lookup a person from a social network, show his socom-id if he has a
	 * socom account
	 * 
	 * @param network
	 * @param snuid
	 * @return uid
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getSocomId(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		if (uid < 0)
			return 2; // missing uid in session
		String network = req.getParam("network");
		String snuid = req.getParam("snuid");

		int id = HSQLUserDatabase.getInstance().getIDOf(network, snuid);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("uid", id)));
		return 0;
	}

	/**
	 * Returns a link to a thumbnail of the users photo
	 * 
	 * @param usersnid
	 * @param network
	 * @return The thumbnail-URL
	 * @throws IOException
	 * @throws JSONException
	 */
	public int getPictureThumbnail(SocomRequest req) throws IOException, JSONException, SocomException {
		String snuid = req.getParam("usersnid");
		String network = req.getParam("network");
		String url = getConnection(network, 0).getPhotoThumbnailUrl(snuid);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("thumbnail_url", url)));
		return 0;
	}

	/**
	 * returns NetworkConnection for given network
	 * 
	 * @param network
	 *            Network, whose connection you need
	 * @param gameid
	 * 			  Gameinstance to identify game-dependent sn-app
	 * @return network connection
	 * @throws NoSNConnectionException
	 * @throws NotImplementedException
	 */
	public SNConnection getConnection(String network, long gameid) throws NoSNConnectionException, NotImplementedException {
		if (network.equalsIgnoreCase(SOCIALNETWORK_FACEBOOK))
		{
			return FacebookConnection.getConnection(gameid);
		}
		else if (network.equalsIgnoreCase(SOCIALNETWORK_GOOGLEPLUS))
			return null;
		else
			return null;
	}

	/**
	 * URL PATTERN IS "social"
	 */
	@Override
	public String getUrlPattern() {
		return URL_PATTERN;
	}
}
