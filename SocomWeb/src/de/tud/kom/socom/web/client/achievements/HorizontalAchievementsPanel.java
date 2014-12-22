package de.tud.kom.socom.web.client.achievements;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;

import de.tud.kom.socom.web.client.SoComWebEntryPoint;
import de.tud.kom.socom.web.client.sharedmodels.GlobalConfig;

public abstract class HorizontalAchievementsPanel extends HorizontalPanel implements GlobalConfig {

	protected SoComWebEntryPoint entryPoint;

	public HorizontalAchievementsPanel(SoComWebEntryPoint ep) {
		super();
		this.entryPoint = ep;
	}

	public void displayError(String error) {
		clear();
		add(new InlineHTML("Fehler: " + error));
	}

	public void displayMessage(String message) {
		clear();
		add(new InlineHTML(message));
	}
	
	protected abstract void fetchContent();

	protected abstract void showContent();
	
	protected abstract void initListeners();
	
	protected String generateProfileLink(long id, String name) {
		return "<a href='" + entryPoint.getCurrentPath() + "?site=profiles&user=" + id + "'>" +
					name + 
				"</a>";
	}
	
	static public String getImagePath(String imageName) {
		if (imageName != null && imageName != "")
			return "data/achievements/" + imageName;
		else
			return "data/achievements/test.png";
	}

}
