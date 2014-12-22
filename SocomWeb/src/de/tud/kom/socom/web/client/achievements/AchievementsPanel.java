package de.tud.kom.socom.web.client.achievements;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import de.tud.kom.socom.web.client.SoComWebEntryPoint;
import de.tud.kom.socom.web.client.sharedmodels.Achievement;
import de.tud.kom.socom.web.client.sharedmodels.AchievementGame;
import de.tud.kom.socom.web.client.sharedmodels.AchievementSummary;

/**
 * Panel to show a users achievements.
 * 
 * @author ngerwien
 * 
 */

public class AchievementsPanel extends HorizontalAchievementsPanel {
	private List<AchievementGame> games;
	private Widget lastAchievementWidget;
	private int lastMenuPanelIndex;
	private List<SingleSelectionModel<String>> selectionModels;
	
	public AchievementsPanel(SoComWebEntryPoint entryPoint) {
		super(entryPoint);		
		fetchContent();
		initListeners();
		
		lastAchievementWidget = null;
		this.setBorderWidth(2);
	}

	@Override
	protected void fetchContent() {
		entryPoint.getAchievementService().getGames(entryPoint.getUserId(), new AsyncCallback<List<AchievementGame>>() {
			
			@Override
			public void onSuccess(List<AchievementGame> result) {
				games = result;				
				showContent();
			}

			@Override
			public void onFailure(Throwable caught) {
				displayError(caught.toString());
			}
		});
	}

	@Override
	protected void showContent() {
		if (games.isEmpty()) {
			displayMessage("Keine Spiele mit achievements vorhanden.");
			return;
		}

		final StackPanel menuPanel = new StackPanel();
		selectionModels = new ArrayList<SingleSelectionModel<String>>();
		lastMenuPanelIndex = menuPanel.getSelectedIndex();
		
		int gameIndex = 0;
		for(AchievementGame game : games) {
			final CellList<String> categoryList = new CellList<String>(new TextCell());
			selectionModels.add(new SingleSelectionModel<String>());
			
			menuPanel.addHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(lastMenuPanelIndex != menuPanel.getSelectedIndex()) {
						lastMenuPanelIndex = menuPanel.getSelectedIndex();
						showAchievements(lastMenuPanelIndex, selectionModels.get(lastMenuPanelIndex).getSelectedObject());
					}				
				}
			}, ClickEvent.getType());
			
			categoryList.setSelectionModel(selectionModels.get(gameIndex));
			selectionModels.get(gameIndex).addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
				
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					int selectedIndex = menuPanel.getSelectedIndex();
					String categoryName = selectionModels.get(selectedIndex).getSelectedObject();
					if(categoryName != null) {
						showAchievements(selectedIndex, categoryName);
					}					
				}
			});
			
			categoryList.setRowCount(game.getAchievementCategories().size(), true);
			categoryList.setRowData(0, game.getAchievementCategories());			
			
			menuPanel.add(categoryList, game.getGameName());
			gameIndex++;
		}
		add(menuPanel);		
	}

	@Override
	protected void initListeners() {
		
	}
	
	private void showAchievements(int gameIndex, String categoryName) {
		if(lastAchievementWidget != null) {
			remove(lastAchievementWidget);
		}
		
		AchievementGame game = games.get(gameIndex);
		
		if(categoryName == AchievementGame.FirstCategory) {
			List<AchievementSummary> summary = new ArrayList<AchievementSummary>();
			summary.add(new AchievementSummary(game.getAchievements()));
			CellList<AchievementSummary> summaryList = new CellList<AchievementSummary>(new AchievementSummaryCell());
			summaryList.setRowCount(summary.size());
			summaryList.setRowData(0, summary);
			
			lastAchievementWidget = summaryList;
			add(summaryList);
		}
		else {
			List<Achievement> achievements = game.getAchievements(categoryName);
			CellList<Achievement> achievementList = new CellList<Achievement>(new AchievementCell());
			achievementList.setRowCount(achievements.size());
			achievementList.setRowData(0, achievements);
			
			lastAchievementWidget = achievementList;
			add(achievementList);
		}
	}
	
	private class AchievementCell extends AbstractCell<Achievement> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				Achievement value, SafeHtmlBuilder sb) {
			if(value != null) {
				sb.append(value.toSafeHtml());
			}
		}		
	}
	
	private class AchievementSummaryCell extends AbstractCell<AchievementSummary> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				AchievementSummary value, SafeHtmlBuilder sb) {
			if(value != null) {
				sb.append(value.toSafeHtml());
			}
		}		
	}
}