package de.tud.kom.socom.web.client.util;

public class Visibility {

	//XXX keep in sync with /SocomAPI/src/de/tud/kom/socom/util/enums/Visibility.java
	public static final int PRIVATE = 0; //visible for noone
	public static final int FRIENDS = 1; //visible for friends
	public static final int PUBLIC = 2; // visible for everybody
	public static final int SOCOM_INTERN = 3; //visible to socom-users
	public static final int GAME_INTERN = 4; //visible to all socom-users playing the same game
	public static final int GAME_ONLY = 5; //not visible to any users, only accessable for the game itself
}
