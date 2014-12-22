package de.tud.kom.socom.components.influence;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.fileupload.FileUploadException;
import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.SocomComponent;
import de.tud.kom.socom.components.social.SocialNetworkManager;
import de.tud.kom.socom.database.influence.HSQLInfluenceDatabase;
import de.tud.kom.socom.database.influence.InfluenceDatabase;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.datatypes.InfluenceConfiguration;
import de.tud.kom.socom.util.datatypes.InfluenceResult;
import de.tud.kom.socom.util.exceptions.CurrentGameInstanceNotIncludedException;
import de.tud.kom.socom.util.exceptions.SocomException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;
import de.tud.kom.socom.util.exceptions.InfluenceNotAvailableException;
import de.tud.kom.socom.util.exceptions.InfluenceTemplateException;
import de.tud.kom.socom.util.exceptions.ParseException;
import de.tud.kom.socom.util.exceptions.UIDNotIncludedException;

/**
 * 
 * @author rhaban
 * 
 */
public class InfluenceManager extends SocomComponent {

	private static final String URL_PATTERN = "influence";
	private static final InfluenceManager instance = new InfluenceManager();
	private static InfluenceDatabase db;

	private InfluenceManager() {
		db = HSQLInfluenceDatabase.getInstance();

		if (!InfluenceUploadHandler.DATA_DIR.exists() || !InfluenceUploadHandler.DATA_DIR.isDirectory()) {
			InfluenceUploadHandler.DATA_DIR.mkdir();
		}
	}

	public static InfluenceManager getInstance() {
		return instance;
	}

	@Override
	public String getUrlPattern() {
		return URL_PATTERN;
	}
	
	/**
	 * Prepares a template for polls, which can later be used in createInfluence inheriting 
	 * settings of the template
	 * 
	 * @param see createInfluence
	 * @return Influence-ID
	 * @throws JSONException
	 * @throws SQLException
	 * @throws SocomException
	 * @throws IOException 
	 */
	public int createInfluenceTemplate(SocomRequest req) throws JSONException, SQLException, SocomException, IOException {
		try {
			int vis = Integer.valueOf(req.getParam("visibility")); 
			return createInfluence(req, vis, true);
		} catch (NumberFormatException e) {
			throw new IllegalParameterException("visibility must be of type integer.");
		}
	}
	

	/**
	 * Prepares a poll which can be shown in the webapplication. Answeres with
	 * the id of the created Poll.
	 * 
	 * @param visibility
	 * @param question
	 * @param type
	 * @param allowfreeanswers (boolean) 
	 * @param freeanswersvotable (boolean)
	 * @param Optional
	 *            : publish (boolean if the influence should be published in the
	 *            SN's)
	 * @param Optional
	 *            : message If the influence should be published via SNs this is
	 *            the message for the post
	 * @param Optional
	 *            : time If the influence should be published the influence must
	 *            be started directly. This is the time when it should end
	 * 
	 * @return Influence-ID and Post-Ids if published
	 * @throws JSONException
	 * @throws SQLException
	 * @throws IOException 
	 */
	public int createInfluence(SocomRequest req) throws JSONException, SQLException, SocomException, IOException {
		try {
			if(req.containsParam("templateid")) {
				String templateid = req.getParam("templateid");
				return createInfluenceFromTemplate(req, templateid);
			}
			int vis = Integer.valueOf(req.getParam("visibility"));
			return createInfluence(req, vis, false);
		} catch (NumberFormatException e) {
			throw new IllegalParameterException("visibility must be of type integer.");
		}
	}

	private int createInfluenceFromTemplate(SocomRequest req, String templateid) throws JSONException, UIDNotIncludedException, SQLException, CurrentGameInstanceNotIncludedException, InfluenceNotAvailableException, InfluenceTemplateException {
		long uid = req.getUid();
		long gid = req.getCurrentGameInst();
		String externalId = db.copyInfluenceTemplate(uid, gid, templateid);
		JSONObject json = new JSONObject();
		json.put("id", externalId);
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}

	private int createInfluence(SocomRequest req, int visibility, boolean template) throws JSONException, SQLException, SocomException, IOException {
		try {
			long uid = req.getUid();
			long gid = req.getCurrentGameInst();
			String question = req.getParam("question");
			String type = req.getParam("type");

			String externalId = InfluenceFactory.createInfluence(uid, gid, question, type, visibility, req, template);
			if (externalId == null)
				return 1; // unknown error

			JSONObject json = new JSONObject();
			json.put("id", externalId);

			if (req.containsParam("publish") && !template) {
				boolean publish = Boolean.parseBoolean(req.getParam("publish"));
				if (publish) {
					String message = req.getParam("message");
					boolean publishOnPage = req.getParam("publishonpage", false);
					int time = Integer.parseInt(req.getParam("time"));
					db.startInfluence(uid, externalId, time);
					
					
					json.put("postids", SocialNetworkManager.getInstance().publishInfluence(publishOnPage, uid, gid, message, externalId));
				}
			}			
			req.addOutput(JSONUtils.JSONToString(json));
		} catch (NumberFormatException e) {
			throw new IllegalParameterException("time must be of type long");
		}
		return 0;
	}

	/**
	 * Creates a predefined answer for a text-influence.
	 * 
	 * @param id
	 * @param answer
	 * 
	 * @return success boolean
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int addPredefinedAnswer(SocomRequest req) throws JSONException, SQLException, SocomException {
		long uid = req.getUid();
		String externalid = req.getParam("id");
		String answer = req.getParam("answer");

		long id = db.addPredefinedAnswer(uid, externalid, answer);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("answerid", id)));
		return 0;
	}

	/**
	 * HTTP-POST-Method
	 * Creates a predefined answer with binary content
	 *  
	 * All Parameters are expected as cookies. Furthermore the stream should contain the content
	 *  
	 * @param id Influence-ID
	 * @param answer
	 * @param fileextension
	 * @return success boolean
	 * @throws IOException
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int addPredefinedAnswerWithData(SocomRequest req) throws IOException, SQLException, JSONException {
		return InfluenceUploadHandler.addPredefinedData(req);
	}

	/**
	 * (Hard-)Removes a predefined answer with the given id in the given influence 
	 * if the current user id administrator or the owner
	 * @param influenceid external-id of the influence
	 * @param answerid id of the answer
	 * @return success boolean
	 * @throws NumberFormatException
	 * @throws SocomException
	 * @throws SQLException
	 */
	public int removePredefinedAnswer(SocomRequest req) throws NumberFormatException, SocomException, SQLException {
		long uid = req.getUid();
		String externalid = req.getParam("influenceid");
		long answerid = Long.parseLong(req.getParam("answerid"));
		boolean success = db.removePredefinedAnswer(uid, externalid, answerid);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}
	
	/**
	 * Starts an influence with the given timeout.
	 * 
	 * @param id of the Influence
	 * @param time in s ('permanent' or negative value for unlimited time)
	 * 
	 * @return success boolean
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int startInfluence(SocomRequest req) throws JSONException, SQLException, SocomException {
		long uid = req.getUid();
		String externalid = req.getParam("id");
		String timeParam = req.getParam("time");
		if(timeParam.equalsIgnoreCase("permanent"))
			timeParam = "-1";
		int time = Integer.parseInt(timeParam);
		
		db.startInfluence(uid, externalid, time);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Stops an influence.
	 * 
	 * @param id Influence-ID
	 * @return success boolean
	 * 
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int stopInfluence(SocomRequest req) throws JSONException, SQLException, SocomException {
		long uid = req.getUid();
		String externalid = req.getParam("id");

		db.stopInfluence(uid, externalid);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Shows the current results of a voting or inquiry.
	 * 
	 * @param id Influence-Id
	 * 
	 * @return error code
	 */
	public int fetchResult(SocomRequest req) throws SQLException, SocomException {
		long uid = req.getUid();
		String externalid = req.getParam("id");

		InfluenceResult result = db.fetchResult(uid, externalid);
		req.addOutput(result.toJSONString());
		return 0;
	}

	
	/**
	 * shows the configuration of the correspoding influence
	 * 
	 * @param id externalid of influence to see
	 * @return configuration (de.tud.kom.socom.util.datatypes.InfluenceConfiguration)
	 * @throws SocomException
	 * @throws SQLException
	 */
	public int getInfluence(SocomRequest req) throws SocomException, SQLException {
		long uid = req.getUid();
		String externalid = req.getParam("id");
		
		InfluenceResult result = db.fetchResult(uid, externalid);
		InfluenceConfiguration config = db.readConfiguration(uid, externalid);
		JSONObject infl = config.getJSON();
		try {
			infl.put("result", result.getJSON());
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		req.addOutput(JSONUtils.JSONToString(infl));
		return 0;
	}
	
	
	/**
	 * change the configuration of an influence or an influence template using the given parameter
	 * 
	 * all parameter (except externalid) are optional and only the specified are overwritten
	 * 
	 * @param externalid
	 * @param question
	 * @param type
	 * @param minchoices
	 * @param maxchoices
	 * @param maxdigits
	 * @param maxlines
	 * @param maxbyes
	 * @param visibility
	 * @param allowfreeanswers
	 * @param freeanswersvotable
	 * 
	 * @return success boolean 
	 * @throws ParseException
	 * @throws IllegalParameterException
	 * @throws UIDNotIncludedException
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws InfluenceNotAvailableException 
	 */
	public int changeInfluence(SocomRequest req) throws SocomException, SQLException {
		String externalid = req.getParam("id");
		long uid = req.getUid();
		
		String question = req.containsParam("question") ? req.getParam("question") : null;
		String type = req.containsParam("type") ? req.getParam("type") : null;
		boolean success = false;
		try{
			int minchoices = req.containsParam("minchoices") ? Integer.parseInt(req.getParam("minchoices")) : -1;
			int maxchoices = req.containsParam("maxchoices") ? Integer.parseInt(req.getParam("maxchoices")) : -1;
			int maxdigits = req.containsParam("maxdigits") ? Integer.parseInt(req.getParam("maxdigits")) : -1;
			int maxlines = req.containsParam("maxlines") ? Integer.parseInt(req.getParam("maxlines")) : -1;
			int maxBytes = req.containsParam("maxbytes") ? Integer.parseInt(req.getParam("maxbytes")) : -1;
			int visibility = req.containsParam("visibility") ? Integer.parseInt(req.getParam("visibility")) : -1;
		
			boolean containsAllowFree = req.containsParam("allowfreeanswers");
			boolean containsFreeVotable = req.containsParam("freeanswersvotable");
			boolean allowFree = containsAllowFree ? Boolean.parseBoolean(req.getParam("allowfreeanswers")) : false;
			boolean freeVotable = containsFreeVotable? Boolean.parseBoolean(req.getParam("freeanswersvotable")) : false;		
		
			success = db.changeConfiguration(uid, externalid, question, type, minchoices, maxchoices, maxdigits, maxlines,
					maxBytes, visibility, containsAllowFree, allowFree, containsFreeVotable, freeVotable);
		}catch(NumberFormatException e) {
			throw new IllegalParameterException(e.getMessage());
		}
		
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}
	
	/**
	 * Only used from Web-Application to upload an Influence Image
	 */
	public int uploadInfluenceData(SocomRequest req) throws FileUploadException, IOException, SQLException, JSONException {
		if (!req.isMultipartContent()) return 19;
		return InfluenceUploadHandler.addFreeData(req);
	}
}