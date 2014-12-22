package de.tud.kom.socom.web.client.baseelements.viewerrors;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import de.tud.kom.socom.web.client.util.ErrorCode;

/** a central Factory class that can be used to decide and retrieve an appropriate ErrorView for a JSON SERVER RESPONSE
 *  The given JSON is expected to have the field "error"  and "code" to determine which error to display
 *  If JSON is null, empty or no key can be found it returns a default ErrorView with a message that the server side error is unknown
 * @author jkonert
 *
 */
public final class ErrorViewFactory
{
	private ErrorViewFactory() {};
	
	public final static ErrorView getErrorView(String jsonResult)
	{
		JSONObject json = getJSONObject(jsonResult);
		ErrorCode code = ErrorCode.UNKNOWN_ERROR;
		if (json == null || !json.containsKey("error") || !json.containsKey("code")) 
			return getErrorView(ErrorCode.UNKNOWN_ERROR);		
		code = ErrorCode.valueOf(json.get("error").isString().stringValue());
		return getErrorView(code);		
	}
	

	public static ErrorView getErrorView(ErrorCode errorCode) {
		if (errorCode == null) errorCode = ErrorCode.UNKNOWN_ERROR;
		switch (errorCode)
		{
		case UNSUPPORTED_MEDIA: return new ErrorServerUnsupportedMediaView();
		case ILLEGAL_FILE_SIZE: return new ErrorMediaSizeView();
		case ILLEGAL_FILE_TYPE: return new ErrorServerUnsupportedMediaView();
		// FIXME JK/RH: add more ErrorView messages for ErrorCodes from server...
		//all these cases below lead to the unknown message
		case UNKNOWN_ERROR:
		default: 		
		}
		return new ErrorServerUnknownErrorView();
	}

	private static JSONObject getJSONObject(String jsonString) {
		JSONObject result = null;
		try
		{
			JSONValue v = JSONParser.parseStrict(jsonString);
			if (v != null) result = v.isObject();  // if it returns null, well...
		}
		catch (NullPointerException e) {}
		return result;
		
	}
}
