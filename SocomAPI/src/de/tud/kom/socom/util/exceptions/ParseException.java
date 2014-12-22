package de.tud.kom.socom.util.exceptions;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.enums.ErrorCode;

public class ParseException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static ErrorCode ERROR_CODE = ErrorCode.UNEXPECTED_OR_MISSING_PARAMETER;
	
	public String parse;

	public ParseException(String parse) {
		super("Was not able to parse " + parse + ".");
		this.parse = parse;
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE.ordinal();
	}

	public String toJSON() {
		try {
			JSONObject res = new JSONObject();
			res.put("error",
					ERROR_CODE.toString());
			if (parse != null)
				res.put("param", parse);
			res.put("code", ERROR_CODE.ordinal());
			return JSONUtils.JSONToString(res);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e.getMessage());
		}
		return null;
	}
}
