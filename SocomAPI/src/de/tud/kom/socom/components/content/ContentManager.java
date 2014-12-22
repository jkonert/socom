package de.tud.kom.socom.components.content;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.SocomComponent;
import de.tud.kom.socom.SocomCore;
import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.database.content.GameContentDatabase;
import de.tud.kom.socom.database.content.HSQLGameContentDatabase;
import de.tud.kom.socom.database.game.GameDatabase;
import de.tud.kom.socom.database.game.HSQLGameDatabase;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.NumberParser;
import de.tud.kom.socom.util.datatypes.GameContent;
import de.tud.kom.socom.util.enums.ContentCategory;
import de.tud.kom.socom.util.exceptions.ContentNotAvailableException;
import de.tud.kom.socom.util.exceptions.SocomException;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;

/**
 * 
 * @author Rhaban Hark
 * 
 *         Game-Content-Manager is used to handle Game-Content e.g. Hints,
 *         Videos, Level and more. In this manager are methods to upload,
 *         download and fetch information of game-content.
 * 
 */
public class ContentManager extends SocomComponent {

	private static final String URL_PATTERN = "content";
	private static GameContentDatabase db;
	private static ContentManager instance = new ContentManager();

	private ContentManager() {
		db = HSQLGameContentDatabase.getInstance();
	}

	public static ContentManager getInstance() {
		return instance;
	}

	/**
	 * With this method you can allocate space for your game-content which you
	 * can upload after allocating the space. Here you have to specify all
	 * information for the content.
	 * 
	 * The method puts a "contentident"-cookie in the response, containing a key
	 * you need when uploading the real content.
	 * 
	 * @param visibility
	 * @param title
	 * @param description
	 * @param type
	 *            (text | image | audio | binary)
	 * @param category: (QUESTION, INFORMATION, HINT, SOLUTION)
	 * @param Optional
	 *            : contextid To which context do this content relate
	 * @param all
	 *            other parameters will be handled as metadata for this content
	 * @return success boolean with contentident cookie
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int createUserContent(SocomRequest req) throws SQLException, JSONException, SocomException {
		try {
			int visibility = Integer.parseInt(req.getParam("visibility"));
			return createGameContent(req, visibility);
		} catch (NumberFormatException e) {
			throw new IllegalParameterException("visibility must be of type integer between 0 and 4.");
		}
	}

	private int createGameContent(SocomRequest req, int visibility) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		long contextid = HSQLGameDatabase.getInstance().getGameContextId(req.getParam("contextid"), gameinstid);

		String title = req.getParam("title");
		String description = req.getParam("description");
		// type must be either 'text' or 'binary'
		String type = req.getParam("type");
		String cat = req.getParam("category");
		ContentCategory category = null;
		try {
			category = ContentCategory.valueOf(cat.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalParameterException("category");
		}
		if (!(type.equals("text") || type.equals("binary") || type.equals("audio") || type.equals("image")))
			return 10;

		Map<String, String> attributes = new HashMap<String, String>(req.getParams());
		// Remove known values
		attributes.remove("contextid");
		attributes.remove("type");
		attributes.remove("title");
		attributes.remove("description");
		attributes.remove("visibility");
		attributes.remove("category");

		String ident = db.createGameContent(uid, contextid, title, description, category, attributes, type, visibility);
		req.addCookie("contentident", ident);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Equal method to createUserContent. Difference is the visibility is
	 * restricted to the game (non-user content).
	 * 
	 * @see(createUserContent)
	 * 
	 * @param title
	 * @param description
	 * @param type
	 *            (text | image | audio | binary)
	 * @param category
	 * @param Optional
	 *            : contextid To which context do this content relate
	 * @param all
	 *            other parameters will be handled as metadata for this content
	 * @return success boolean with contentident cookie
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int createGameContent(SocomRequest req) throws SQLException, JSONException, SocomException {
		int visibility = GlobalConfig.VISIBILITY_NON_USER;
		return createGameContent(req, visibility);
	}

	/**
	 * HTTP-POST-Method
	 * 
	 * After allocating space for a content you can upload the content using the
	 * key sent in the "contentident" cookie. The cookie must be sent back (as
	 * cookie) here for identification.
	 * 
	 * @param contentident
	 *            as cookie
	 * @return The new contentid
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLException
	 */
	public int uploadContent(SocomRequest req) throws JSONException, IOException, SQLException, SocomException {
		long uid = req.getUid();
		String identifier = req.getCookieVal("contentident");
		InputStream is = req.getInputStream();
		
		long contentid = db.uploadGameContent(uid, identifier, is);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("contentid", contentid)));
		return 0;
	}
	
	/**
	 * Fetch information about content in the given context.
	 * 
	 * @param context
	 *            The context-id where the content should be related to
	 * @param Optional
	 *            : since Either a date in the form "yyyy-MM-dd HH:mm:ss" or the
	 *            date in ms from 01.01.1970 00:00
	 * @return List of Content
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int getContentInfoForContext(SocomRequest req) throws IOException, JSONException, SQLException, SocomException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		long contextid = HSQLGameDatabase.getInstance().getGameContextId(req.getParam("context"), gameinstid);

		Date sinceDate = null;
		if (req.getParams().containsKey("since")) {
			sinceDate = determineSinceDate(req.getParam("since"));
		}
		List<GameContent> contentList = db.fetchContent(uid, contextid, sinceDate);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("content", contentList)));
		return 0;
	}

	/**
	 * Fetch content-informations for the given filter options.
	 * 
	 * @param Optional
	 *            : contexts Comma-Separated list of contextids where the
	 *            content must relate to
	 * @param Optional
	 *            : sincle Either a date in the form "yyyy-MM-dd HH:mm:ss" or
	 *            the date in ms from 01.01.1970 00:00
	 * @param Optional
	 *            : type Comma-Separated list of types the should could be of
	 *            one of it
	 * @param Optional
	 *            : keywords Comma-Separated list of keywords the description
	 *            must contain
	 * @param Optional
	 *            : metadata Form: key1:value1,key2:value2,... which the content
	 *            MUST contain, escape double-points with /: and commatas with
	 *            /,
	 * @return List of contents
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getContentInfo(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		long gameinstid = req.getCurrentGameInst();
		String[] contexts = req.containsParam("contexts") ? req.getParam("contexts").split(",") : null;
		String[] contextids = null;
		if (contexts != null) {
			contextids = new String[contexts.length];
			GameDatabase gamedb = HSQLGameDatabase.getInstance();
			for (int i = 0; i < contexts.length; i++) {
				contextids[i] = String.valueOf(gamedb.getGameContextId(contexts[i], gameinstid));
			}
		}
		Date since = req.containsParam("since") ? determineSinceDate(req.getParam("since")) : null;
		String[] types = req.containsParam("type") ? req.getParam("type").split(",") : null;
		String[] titles = req.containsParam("title") ? req.getParam("title").split(",") : null;
		String[] keywords = req.containsParam("keywords") ? req.getParam("keywords").split(",") : null;
		String[] metadata = req.containsParam("metadata") ? req.getParam("metadata").split("(?<!/),") : null;
		// regex "(?<!/)," explains that a comma (,) but not a comma with a
		// leading / (/,) is selected (negative lookbehind)

		List<GameContent> contentList = db.fetchContent(uid, gameinstid, contextids, since, types, titles, keywords, metadata);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("content", contentList)));
		return 0;
	}

	private Date determineSinceDate(String time) throws SocomException {
		Date sinceDate;
		try {
			long sinceInMillis = Long.parseLong(time);
			sinceDate = new Date(sinceInMillis);
		} catch (NumberFormatException e) {
			try {
				sinceDate = SocomCore.getDateFormat().parse(time);
			} catch (ParseException e1) {
				e1.printStackTrace();
				throw new IllegalParameterException("since");
			}
		}
		return sinceDate;
	}

	/**
	 * Download the content specified with its id, you could have received using
	 * the getContentInfo method.
	 * 
	 * @param contentid
	 * @return a File
	 * @throws IOException
	 * @throws SQLException
	 */
	public int downloadContent(SocomRequest req) throws IOException, SQLException, SocomException {
		try {
			long uid = req.getUid();
			long contentid = Long.valueOf(req.getParam("contentid"));
			byte[] result = db.downloadContent(uid, contentid, true);

			if (result == null)
				throw new ContentNotAvailableException();

			String type = db.getType(contentid);
			String extension = type.equalsIgnoreCase("image") ? ".png" : type.equalsIgnoreCase("audio") ? ".ogg" : 
				type.equalsIgnoreCase("text") ? ".txt" :  "";
			// TODO what about other download file types
			req.setOutputFilename("socom_" + contentid + extension);
			req.getOutputStream().write(result);
			req.getOutputStream().flush();
			req.getOutputStream().close();
		} catch (NumberFormatException e) {
			throw new IllegalParameterException("contentid must be of type long");
		}
		return 0;
	}

	/**
	 * Rates a content.
	 * 
	 * @param contentid
	 * @param rating
	 *            (Double between 0 and 1)
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int rateContent(SocomRequest req) throws JSONException, SQLException, SocomException {
		String param = "contentid", type = "long";
		try {
			long uid = req.getUid();
			long contentid = Long.valueOf(req.getParam("contentid"));
			param = "rating";
			type = "double between 0 and 1";
			double rating = NumberParser.parseDouble(req.getParam("rating"));

			if (rating < 0 || rating > 1) {
				throw new IllegalParameterException("rating has to be between 0 and 1 or");
			}

			db.rateContent(uid, contentid, rating);
			req.addOutput(JSONUtils.getSuccessJsonString());
		} catch (NumberFormatException e) {
			throw new IllegalParameterException(param + " (of type " + type + ") has wrong format or");
		}
		return 0;
	}

	/**
	 * Adds a comment to a specific content
	 * 
	 * @param contentid
	 * @param message
	 * @return the commentid
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int addComment(SocomRequest req) throws SQLException, JSONException, SocomException {
		try {
			long uid = req.getUid();
			long contentid = Long.valueOf(req.getParam("contentid"));
			String message = req.getParam("message");

			long commentid = db.addComment(uid, contentid, message);
			req.addOutput(JSONUtils.JSONToString(new JSONObject().put("commentid", commentid)));
		} catch (NumberFormatException e) {
			throw new IllegalParameterException("contentid must be of type long.");
		}
		return 0;
	}

	/**
	 * Deletes a specific comment
	 * 
	 * @param commentid
	 * @param Optional
	 *            : delete value
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int deleteComment(SocomRequest req) throws SQLException, JSONException, SocomException {
		String param = "commentid", type = "long";
		try {
			long uid = req.getUid();
			long contentid = Long.valueOf(req.getParam("commentid"));
			param = "delete";
			type = "integer between 0 and 2";
			int delete = req.getParams().containsKey("delete") ? Integer.parseInt(req.getParam("delete")) : 1;

			boolean success = db.deleteComment(uid, contentid, delete);
			req.addOutput(JSONUtils.getSuccessJsonString(success));
		} catch (NumberFormatException e) {
			throw new IllegalParameterException(param + " must be of type " + type + ".");
		}
		return 0;
	}
	
	/**
	 * URL PATTERN IS "content"
	 */
	@Override
	public String getUrlPattern() {
		return URL_PATTERN;
	}
}
