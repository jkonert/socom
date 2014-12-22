package de.tud.kom.socom.web.client.sharedmodels;

import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AchievementSummary implements IsSerializable {
	private String achievementsCompleted;
	private String achievementPoints;
	
	public AchievementSummary(List<Achievement> achievements) {
		int achievementsCompletedCount = 0;
		int currentAchievementPoints = 0;
		int maxAchievementPoints = 0;
		
		for(Achievement achievement : achievements) {
			if(achievement.getProgress().isCompleted()) {
				achievementsCompletedCount++;
			}
			
			for(AchievementLevel level : achievement.getLevels()) {
				maxAchievementPoints += level.getRewardPoints();
				if(achievement.getProgress().isCompleted()
						|| achievement.getProgress().getCurrentLevel() > level.getLevel()) {
					currentAchievementPoints += level.getRewardPoints();
				}
			}
		}
		
		achievementsCompleted = "" + achievementsCompletedCount + "/" + achievements.size();
		achievementPoints = "" + currentAchievementPoints + "/" + maxAchievementPoints;
	}
	
	public SafeHtml toSafeHtml() {
		
		@SuppressWarnings("serial")
		SafeHtml safeHtlm = new SafeHtml() {
			
			@Override
			public String asString() {
				String html = "<table>" +
							"<tr>" +
								"<td style='padding:5px' colspan='2'>Summary<hr style='color:#fff'></td>" +
							"</tr>" +
							"<tr>" +
								"<td style='padding:5px'>Achievement points</td>" +
								"<td style='padding:5px'>" + achievementPoints +"</td>" +
							"</tr>" +
							"<tr>" +
								"<td style='padding:5px'>Achievements completed</td>" +
								"<td style='padding:5px'>" + achievementsCompleted +"</td>" +
							"</tr>" +
						"</table>";
				
				return html;
			}
		};
		
		return safeHtlm;
	}
}
