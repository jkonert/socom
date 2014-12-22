package de.tud.kom.socom;


/**
 * Global constants
 * @author creuter, rhaban
 *
 */
public interface GlobalConfig {
	
	public static final int USERSTATE_OFFLINE = 0;
	public static final int USERSTATE_ONLINE = 1;
	public static final int USERSTATE_PLAYING = 2;
	
	public static final int VISIBILITY_PRIVATE = 0; //visible for noone, except the creator
	public static final int VISIBILITY_FRIENDS = 1; //visible for friends
	public static final int VISIBILITY_PUBLIC = 2; // visible for everybody
	public static final int VISIBILITY_SOCOM = 3; //visible to socom-users
	public static final int VISIBILITY_GAME = 4; //visible to all socom-users playing the same game
	public static final int VISIBILITY_NON_USER = 5; // not visible to any user, only for the game itself
	
	public static final String SOCIALNETWORK_FACEBOOK = "Facebook";
	public static final String SOCIALNETWORK_GOOGLEPLUS = "Google+";
	public static final String[] SOCIALNETWORK_ALL = {SOCIALNETWORK_FACEBOOK};
	
	public static final String SOCIALNETWORK_CODE_DIRECT_TOKEN_SAVE = "g2013codeTKs4223";
	
	//SET DEBUG TO SEE INCOMING REQUESTS+PARAMETER AND THE OUTGOING JSON-RESULT
	public static final boolean DEBUG = true;

	
	//	public static final String WAR_DIRECTORY = "war";
	public static final String DATA_DIR = "data";
	public static final String INFLUENCE_DATA_DIR = "influence_data";
	public static final String GAME_IMAGE_DIR = "game_images";
	
}
