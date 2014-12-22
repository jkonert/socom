package de.tud.kom.socom;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jetty.util.Utf8Appendable.NotUtf8Exception;
import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.components.achievements.AchievementsManager;
import de.tud.kom.socom.components.content.ContentManager;
import de.tud.kom.socom.components.game.GameManager;
import de.tud.kom.socom.components.influence.InfluenceManager;
import de.tud.kom.socom.components.report.ReportManager;
import de.tud.kom.socom.components.social.SocialNetworkManager;
import de.tud.kom.socom.components.statistics.StatisticsManager;
import de.tud.kom.socom.components.user.UserManager;
import de.tud.kom.socom.database.user.HSQLUserDatabase;
import de.tud.kom.socom.util.ErrorCodeUtils;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.Logger;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.ResourceLoader;
import de.tud.kom.socom.util.enums.ErrorCode;
import de.tud.kom.socom.util.exceptions.SocomException;
import de.tud.kom.socom.util.exceptions.ParseException;
import de.tud.kom.socom.util.exceptions.UIDNotIncludedException;
import de.tud.kom.socom.util.playerstate.ObservedUIDs;
import de.tud.kom.socom.util.playerstate.PlayerStateWatcher;

/**
 * 
 * @author rhaban
 * 
 */
public class SocomCore {

	private SocomComponent[] components;
	private Logger logger;
	private static SimpleDateFormat simple_date_format;

	public SocomCore() {
		simple_date_format = new SimpleDateFormat(ResourceLoader.getResource("simple_date_format"));
		loadComponents();
		runPlayerStateWachter();
		logger = LoggerFactory.getLogger();
	}

	protected void doRequest(SocomRequest req) {
		try {
			updateOnlineState(req.getUid());
		} catch (UIDNotIncludedException e) {
		}

		String handler;
		int code = -1;
		try {
			handler = req.getHandlerString();
			for (SocomComponent component : components) {
				if (component.getUrlPattern().equalsIgnoreCase(handler)) {
					code = component.handleRequest(req);
					break;
				}
			}
			if (code == -1)
				throw new ParseException("handler");
			if (code > 0) // no success?
				req.addOutput(ErrorCodeUtils.toJSONString(ErrorCode.fromInt(code)));
		} catch (NumberFormatException e) {		
			try {
				req.setOutput(JSONUtils.JSONToString(new JSONObject().put("error", ErrorCode.UNEXPECTED_OR_MISSING_PARAMETER.name())
						.put("code", ErrorCode.UNEXPECTED_OR_MISSING_PARAMETER.ordinal())));
			} catch (JSONException e1) {
				logger.Error(e);
			}
		}
		catch (NotUtf8Exception e)
		{// this will basically never happen as these are all catched and transformed to socom Exceptions in SocomRequest
			try {
				JSONObject json = new JSONObject().put("error", ErrorCode.UNEXPECTED_OR_MISSING_PARAMETER.name())
						.put("code", ErrorCode.UNEXPECTED_OR_MISSING_PARAMETER.ordinal()).put("message", "UTF8-Encoding Error: "+e.getMessage());
				req.setOutput(JSONUtils.JSONToString(json));
			} catch (JSONException e1) {
				logger.Error(e);
			}
		}catch (ParseException e) {
			req.setOutput(e.toJSON());
		} catch (SocomException e) {
			try {
				JSONObject json = new JSONObject();
				json.put("code", e.getErrorCode());
				json.put("error", ErrorCode.fromInt(e.getErrorCode()).name());
				json.put("message", e.getMessage());
				req.addOutput(JSONUtils.JSONToString(json));
			} catch (JSONException e1) {
				logger.Error(e1);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.Error(e);
			req.setOutput(e.getMessage());
		} finally {
			req.finish();
		}
	}

	private void updateOnlineState(long uid) {
		ObservedUIDs.getInstance().setOnline(uid);
	}

	private void loadComponents() {
		components = new SocomComponent[] { SocialNetworkManager.getInstance(), ContentManager.getInstance(), 
				UserManager.getInstance(), GameManager.getInstance(), InfluenceManager.getInstance(), 
				AchievementsManager.getInstance(), StatisticsManager.getInstance(), ReportManager.getInstance()};
	}

	private void runPlayerStateWachter() {
		try {
			HSQLUserDatabase.getInstance().setAllUsersOffline();
		} catch (SQLException e) {
			logger.Error(e);
		}
		PlayerStateWatcher.getInstance().start();
	}

	public static DateFormat getDateFormat() {
		return simple_date_format;
	}
}
