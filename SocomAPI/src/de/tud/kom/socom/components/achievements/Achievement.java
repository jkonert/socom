package de.tud.kom.socom.components.achievements;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

/**
 * 
 * @author ngerwien
 * 
 */
public class Achievement implements JSONString {
	private long achievementID, gameID;
	private String name, description, image, categoryname;
	private List<AchievementLevel> levels;

	public Achievement(long achievementID, long gameID, long counterMax, String name,
			String description, String image, String categoryname, int rewardPoints) {
		this.achievementID = achievementID;
		this.gameID = gameID;
		this.name = name;
		this.description = description;
		this.image = image;
		this.categoryname = categoryname;
		this.levels = new ArrayList<AchievementLevel>();
		
		AchievementLevel firstLevel = new AchievementLevel(-1L, achievementID, counterMax, 1, rewardPoints);
		this.levels.add(firstLevel);
	}
	
	public Achievement(long achievementID, long gameID, String name, String description,
			String image, String categoryname) {
		this.achievementID = achievementID;
		this.gameID = gameID;
		this.name = name;
		this.description = description;
		this.image = image;
		this.categoryname = categoryname;
		this.levels = new ArrayList<AchievementLevel>();
	}
	
	public long getAchievementID() {
		return achievementID;
	}

	public long getGameID() {
		return gameID;
	}

	public String getName() {
		return name;
	}
	
	public String getCategoryName() {
		return categoryname;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}
	
	public List<AchievementLevel> getLevels() {
		return levels;
	}	

	public void addLevel(AchievementLevel level) {
		levels.add(level);
	}
	
	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("achievementid", achievementID);
			json.put("gameid", gameID);
			json.put("categoryname", categoryname);
			json.put("name", name);
			json.put("description", description);
			json.put("image", image);
			json.put("levels", levels);

			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}

}
