package de.tud.kom.socom.web.client.sharedmodels;

import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.IsSerializable;

import de.tud.kom.socom.web.client.achievements.HorizontalAchievementsPanel;

public class Achievement implements IsSerializable {
	private long achievementID, gameID;
	private int currentRewardPoints;
	private String name, description, image, categoryname;
	private AchievementProgress progress;
	private List<AchievementLevel> levels;

	public Achievement() {
		
	}
	
	public Achievement(long achievementID, long gameID, long counterMax, String name,
			String description, String image, String categoryname, int currentRewardPoints,
			List<AchievementLevel> levels, AchievementProgress progress) {
		this.achievementID = achievementID;
		this.gameID = gameID;
		this.name = name;
		this.description = description;
		this.image = image;
		this.categoryname = categoryname;
		this.currentRewardPoints = currentRewardPoints;
		this.levels = levels;
		this.progress = progress;
	}
	
	public int getCurrentRewardPoints() {
		return currentRewardPoints;
	}
	
	public AchievementProgress getProgress() {
		return progress;
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
	
	public boolean hasRewards() {
		for(AchievementLevel level : levels) {
			if(level.getRewards() != null && level.getRewards().size() > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public int getCurrentPoints() {
		int maxPoints = 0;
		for(int level = 0; level < (progress.getCurrentLevel() - 1); level++) {
			maxPoints += levels.get(level).getRewardPoints();
		}
		
		if(progress.isCompleted()) {
			maxPoints += levels.get(levels.size() - 1).getRewardPoints();
		}
		
		return maxPoints;
	}
	
	public int getMaxPoints() {
		int maxPoints = 0;
		for(AchievementLevel level : levels) {
			maxPoints += level.getRewardPoints();
		}
		
		return maxPoints;
	}
	
	public SafeHtml toSafeHtml() {
		final Achievement achievement = this;
		final int currentLevel = (getProgress().isCompleted()) ? getProgress().getCurrentLevel() : (getProgress().getCurrentLevel() - 1);
		final String timeCompleted = ("" + achievement.getProgress().getTimeCompleted()).split("\\.")[0];
		final String achievementImagePath = HorizontalAchievementsPanel.getImagePath(achievement.getImage());
		
		@SuppressWarnings("serial")
		SafeHtml safeHtml = new SafeHtml() {
			
			@Override
			public String asString() {
				String html = "<table style='border:1px solid black'>" +
						"<tr>" +
							"<td style='vertical-align:top'>" +
								"<img src=" + achievementImagePath + " width='64px'>" +
							"</td>" +
							"<td style='border:1px inset black'>" +
								"<table style='width:500px;overflow:auto'>" +
									"<tr>" +
										"<td>" +
											achievement.getName() +
											"<hr style='color:#fff'>" +
										"</td>" +
									"</tr>" +
									"<tr>" +
										"<td>" +
											achievement.getDescription() +
										"</td>" +
									"</tr>";
				if(achievement.hasRewards()) {
					for(AchievementLevel level : achievement.getLevels()) {
						html +=		"<tr>" +
										"<td>" +
											"<table>";
						for(AchievementReward reward : level.getRewards()) {
							html +=				"<tr>" +
													"<td colspan='2'>" +
														"<hr style='color:#fff'>";
							if(achievement.getLevels().size() == 1) {
								html += 				"Rewards";
							}
							else {
								html +=					"Reward for Level " + level.getLevel();
							}
							html +=					"</td>" +
												"</tr>" +
												"<tr>" +
													"<td style='padding:5px'>" +
														reward.getName() +
													"</td>" +
													"<td style='padding:5px'>" +
														reward.getDescription() +
													"</td>" +
												"</tr>";
						}
						html += 			"</table>" +
										"</td>" +
									"</tr>";
					}					
				}
				html +=			"</table>" +
							"</td>" +
							"<td style='vertical-align:top'>" +
								"<table style='width:150px;overflow:auto'>" +
									"<tr>" +
										"<td>Points</td>" +
										"<td>" + achievement.getCurrentPoints() + "/" + achievement.getMaxPoints() + "</td>" +
									"</tr>" +
									"<tr>" +
										"<td>Level</td>" +
										"<td>" + currentLevel + "/" + achievement.getProgress().getMaxLevel() + "</td>" +
									"</tr>" +
									"<tr>" +
										"<td>Progress</td>" +
										"<td>" + achievement.getProgress().getCounter() + "/" + achievement.getProgress().getCounterMax() + "</td>" +
									"</tr>";
				if(achievement.getProgress().isCompleted()) {
					html +=			"<tr>" +
										"<td>Completed</td>" +
										"<td>" + timeCompleted + "</td>" +
									"</tr>";
				}
				html += 		"</table>" +
							"</td>" +
						"</tr>" +
						"</table>";
				
				return html;
			}
		};
		
		return safeHtml;
	}
}
