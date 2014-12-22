package de.tud.kom.socom.util;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.util.enums.ErrorCode;

/** we need ErrorCode to be seperate from any other class as it is used in client, too.
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
			LoggerFactory.getLogger().Error(e.getMessage());
		}
		return JSONUtils.JSONToString(result);
	}

}
