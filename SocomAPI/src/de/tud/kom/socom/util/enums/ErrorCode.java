package de.tud.kom.socom.util.enums;

/**
 * 
 * @author rhaban
 * 
 */
public enum ErrorCode {
	SUCCESS/* 0 */, 
	UNKNOWN_ERROR/* 1 */, 
	UID_NOT_IN_SESSION/* 2 */,
	/** 3 */ UID_NOT_FOUND, 
	UNEXPECTED_OR_MISSING_PARAMETER/* 4 */, 
	USER_NOT_VALID/* 5 */, 
	USER_ALREADY_EXISTS/* 6 */, 
	/** 7 */ USER_NOT_FOUND , 
	NOT_IMPLEMENTED, /* 8 */
	IO_EXCEPTION, /* 9 */
	UNSUPPORTED_MEDIA, /* 10 */
	ALREADY_LOGGED_IN, /* 11 */
	CONTENT_NOT_AVAILABLE, /* 12 */
	MISSING_ACCESS_TOKEN, /* 13 */
	COOKIE_NOT_FOUND, /* 14 */
	CONTENT_SECRET_EXPIRED, /* 15 */
	GAME_VERSION_ALREADY_EXIST, /* 16 */
	SCENE_ALREADY_EXIST, /* 17 */
	INFLUENCE_NOT_AVAILABLE, /* 18 */
	ILLEGAL_ACCESS, /* 19 */
	ALREADY_RATED, /* 20 */
	POST_NOT_AVAILABLE, /* 21 */
	GAME_VERSION_NOT_FOUND, /* 22 */
	GAME_NOT_AUTHED, /* 23 */
	SCENE_NOT_FOUND, /* 24 */
	CURRENT_SCENE_NOT_FOUND, /* 25 */
	SCENE_RELATION_ALREADY_EXIST, /* 26 */
	COULD_NOT_DELETE_SCENE, /* 27 */
	GAME_ALREADY_EXIST, /* 28 */
	COULD_NOT_DELETE_GAME, /* 29 */
	COULD_NOT_DELETE_GAME_INSTANCE, /* 30 */
	CURRENT_GAME_INSTANCE_NOT_IN_SESSION /* 31 */,
	SOCIAL_NETWORK_UNSUPPORTED /* 32 */,
	USER_OR_CONTENT_DELETED /* 33 */,
	ILLEGAL_FILE_SIZE /* 34 */,
	ILLEGAL_FILE_TYPE /* 35 */,
	ACHIEVEMENT_ALREADY_EXIST /* 36 */,
	REWARD_ALREADY_EXIST /* 37 */,
	ACHIEVEMENT_NOT_FOUND /* 38 */,
	REWARD_NOT_FOUND /* 39 */,
	ACHIEVEMENT_LEVEL_NOT_FOUND /* 40 */,
	ACHIEVEMENT_LEVEL_COUNTERMAX_INVALID /* 41 */,
	ACHIEVEMENT_CATEGORY_NOT_FOUND /* 42 */,
	CONTENT_ALREADY_EXISTS /* 43 */,
	CONTENT_NOT_FOUND /* 44 */,
	INFLUENCE_TEMPLATE_OPERATION_NOT_SUPPORTED /* 45 */,
	NOT_A_TEMPLATE /* 46 */,
	ALREADY_STARTED /* 47 */, 
	NO_SN_CONNECTION /* 48 */,
	SN_EXCEPTION /* 49 */,
	
	;

	/**
	 * returns the ErrorCode Enum for this integer (first is 1)
	 * 
	 * @param code
	 * @return
	 */
	public static ErrorCode fromInt(int code) {
		if (code <= 0 || code >= ErrorCode.values().length)
			return UNKNOWN_ERROR;
		return ErrorCode.values()[code];
	}

	/**
	 * @deprecated(use ordinal() instead)
	 * 
	 * returns error code integer representation starting from 1
	 * 
	 * @param code
	 * @return integer representing the error code
	 */
	@Deprecated
	public static int toInt(ErrorCode code) {
		for (int i = 1; i < ErrorCode.values().length; i++)
			if (ErrorCode.values()[i].compareTo(code) == 0)
				return i;
		return 1;
	}

	
	public String toString(){
		return "ERROR: " + this.name() + "\nCODE: " + this.ordinal();
	}
}