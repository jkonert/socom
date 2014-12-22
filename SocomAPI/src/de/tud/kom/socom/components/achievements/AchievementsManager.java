package de.tud.kom.socom.components.achievements;

import java.sql.SQLException;

import org.json.JSONException;

import de.tud.kom.socom.SocomComponent;
import de.tud.kom.socom.database.achievements.AchievementDatabase;
import de.tud.kom.socom.database.achievements.HSQLAchievementDatabase;
import de.tud.kom.socom.database.game.GameDatabase;
import de.tud.kom.socom.database.game.HSQLGameDatabase;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.exceptions.SocomException;

public class AchievementsManager extends SocomComponent {

	private static final String URL_PATTERN = "achievements";
	private static AchievementsManager instance = new AchievementsManager();
	private static AchievementDatabase achievementDB;
	private static GameDatabase gameDB;

	private AchievementsManager() {
		achievementDB = HSQLAchievementDatabase.getInstance();
		gameDB = HSQLGameDatabase.getInstance();
	}

	public static AchievementsManager getInstance() {
		return instance;
	}

	@Override
	public String getUrlPattern() {
		return URL_PATTERN;
	}

	/**
	 * Add an achievement.
	 * 
	 * Answeres with json: success=true
	 * 
	 * @param req
	 *            Request with URL-Params: - achievementname (string) -
	 *            description (string) - image (string) - countermax (long) -
	 *            rewardpoints (int) - categoryname (string) - gamename (string)
	 *            - gamepassword (string)
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int addAchievement(SocomRequest req) throws SQLException, JSONException, SocomException {
		String name = req.getParam("achievementname");
		String description = req.getParam("description");
		String categoryname = req.getParam("categoryname");
		String image = req.getParam("image");
		String gamename = req.getParam("gamename");
		String gamepw = req.getParam("gamepassword");
		long counterMax = Long.parseLong(req.getParam("countermax"));
		int rewardPoints = Integer.parseInt(req.getParam("rewardpoints"));
		long gameID = gameDB.authenticateGame(gamename, gamepw);

		Achievement achievement = new Achievement(-1L, gameID, counterMax, name, description, image, categoryname, rewardPoints);
		achievementDB.addAchievement(achievement);

		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Get an achievement with all its rewards.
	 * 
	 * Answers with json: instance =
	 * 'components.achievements.achievement.toJSONString'
	 * 
	 * @param req
	 *            Request with URL-Params: - achievementname (string) - gamename
	 *            (string) - gamepassword (string)
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getAchievement(SocomRequest req) throws SQLException, JSONException, SocomException {
		String achievementname = req.getParam("achievementname");
		String gamepassword = req.getParam("gamepassword");
		String gamename = req.getParam("gamename");
		long gameID = gameDB.authenticateGame(gamename, gamepassword);

		Achievement achievement = achievementDB.getAchievement(achievementname, gameID);

		req.addOutput(achievement.toJSONString());
		return 0;
	}

	/**
	 * Delete achievement and all its relations.
	 * 
	 * Answeres with json: success=true
	 * 
	 * @param req
	 *            Request with URL-Params: - achievementname (string) - gamename
	 *            (string) - gamepassword (string)
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int removeAchievement(SocomRequest req) throws SQLException, JSONException, SocomException {
		String achievementname = req.getParam("achievementname");
		String gamepassword = req.getParam("gamepassword");
		String gamename = req.getParam("gamename");
		long gameID = gameDB.authenticateGame(gamename, gamepassword);

		achievementDB.removeAchievement(achievementname, gameID);

		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Add an achievement level.
	 * 
	 * param: countermax has to be higher than from the last level
	 * 
	 * Answeres with json: success=true
	 * 
	 * @param req
	 *            Request with URL-Params: - achievementname (string) - gamename
	 *            (string) - gamepassword (string) - - countermax (long) -
	 *            rewardpoints (int)
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int addAchievementLevel(SocomRequest req) throws SQLException, JSONException, SocomException {
		String achievementname = req.getParam("achievementname");
		String gamename = req.getParam("gamename");
		String gamepw = req.getParam("gamepassword");
		long counterMax = Long.parseLong(req.getParam("countermax"));
		int rewardPoints = Integer.parseInt(req.getParam("rewardpoints"));
		long gameID = gameDB.authenticateGame(gamename, gamepw);

		Achievement achievement = new Achievement(-1L, gameID, counterMax, achievementname, "", "", "", rewardPoints);
		achievementDB.addAchievementLevel(achievement);

		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Update the achievement progress.
	 * 
	 * Expects user id in session.
	 * 
	 * Counter will be summed up if non-zero (can be negative). No action if
	 * counter is zero.
	 * 
	 * Answers with json: instance =
	 * 'components.achievements.achievementprogressmessage.toJSONString'
	 * 
	 * @param req
	 *            Request with URL-Params: - achievementname (string) - gamename
	 *            (string) - gamepassword (string) - counter (long)
	 * 
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int updateAchievementProgress(SocomRequest req) throws SQLException, JSONException, SocomException {
		String achievementname = req.getParam("achievementname");
		String gamename = req.getParam("gamename");
		String gamepw = req.getParam("gamepassword");
		long counter = Long.parseLong(req.getParam("counter"));
		long gameID = gameDB.authenticateGame(gamename, gamepw);
		long userID = req.getUid();

		AchievementProgressMessage progressMessage = achievementDB.updateAchievementProgress(achievementname, gameID, counter, userID);

		req.addOutput(progressMessage.toJSONString());
		return 0;
	}

	/**
	 * Reset the achievement progress of the current achievement level if not
	 * completed yet.
	 * 
	 * Expects user id in session.
	 * 
	 * Answeres with json: success=true
	 * 
	 * @param req
	 *            Request with URL-Params: - achievementname (string) - gamename
	 *            (string) - gamepassword (string)
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int resetAchievementProgress(SocomRequest req) throws SQLException, JSONException, SocomException {
		String achievementname = req.getParam("achievementname");
		String gamename = req.getParam("gamename");
		String gamepw = req.getParam("gamepassword");
		long gameID = gameDB.authenticateGame(gamename, gamepw);
		long userID = req.getUid();

		achievementDB.resetAchievementProgress(achievementname, gameID, userID);

		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Get the achievement progress.
	 * 
	 * Expects user id in session.
	 * 
	 * Answers with json: instance =
	 * 'components.achievements.achievementprogress.toJSONString'
	 * 
	 * @param req
	 *            Request with URL-Params: - achievementname (string) - gamename
	 *            (string) - gamepassword (string)
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getAchievementProgress(SocomRequest req) throws SQLException, JSONException, SocomException {
		String achievementname = req.getParam("achievementname");
		String gamepassword = req.getParam("gamepassword");
		String gamename = req.getParam("gamename");
		long gameID = gameDB.authenticateGame(gamename, gamepassword);
		long userID = req.getUid();

		AchievementProgress progress = achievementDB.getAchievementProgress(achievementname, gameID, userID);

		req.addOutput(progress.toJSONString());
		return 0;
	}

	/**
	 * Add an achievement reward.
	 * 
	 * Afterwards, setAchievementReward() should be called to associate the
	 * reward with achievement(s).
	 * 
	 * Answeres with json: success=true
	 * 
	 * @param req
	 *            Request with URL-Params: - name (string) - description
	 *            (string) - value (long)
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int addAchievementReward(SocomRequest req) throws SQLException, JSONException, SocomException {
		String name = req.getParam("name");
		String description = req.getParam("description");
		long value = Long.parseLong(req.getParam("value"));

		AchievementReward reward = new AchievementReward(-1L, value, name, description);
		achievementDB.addReward(reward);

		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Relates a reward with an achievement.
	 * 
	 * Answeres with json: success=true
	 * 
	 * @param req
	 *            Request with URL-Params: - achievementname (string) - gamename
	 *            (string) - gamepassword (string) - achievementlevel (integer)
	 *            - rewardname (string)
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int setAchievementReward(SocomRequest req) throws SQLException, JSONException, SocomException {
		String achievementname = req.getParam("achievementname");
		String gamename = req.getParam("gamename");
		String gamepw = req.getParam("gamepassword");
		String rewardname = req.getParam("rewardname");
		int achievementlevel = Integer.valueOf(req.getParam("achievementlevel"));
		long gameID = gameDB.authenticateGame(gamename, gamepw);

		Achievement achievement = new Achievement(-1L, gameID, -1, achievementname, "", "", "", -1);
		AchievementReward reward = new AchievementReward(-1L, -1, rewardname, "");
		achievementDB.setAchievementReward(achievement, reward, achievementlevel);

		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}
}
