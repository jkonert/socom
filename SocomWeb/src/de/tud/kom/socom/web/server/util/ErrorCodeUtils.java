package de.tud.kom.socom.web.server.util;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.web.client.util.ErrorCode;

/** we need ErrorCode to be seperate from any other class as it is used in client, too.  (which cannot use the same JSON implementation)
 * 
 * @author jkonert
 *
 */
public class ErrorCodeUtils 
{

	public static String toJSONString(ErrorCode errorCode)
	{
		JSONObject result = new JSONObject();
		try {
			result.put("error", errorCode.name());
			result.put("code", errorCode.ordinal());
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		try
		{
			return result.toString(1);
		}
		catch (JSONException e)
		{
			LoggerFactory.getLogger().Error(e);
		}
		return null;
	}

}
