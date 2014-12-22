package de.tud.kom.socom.web.client.administration;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.GraphPanel;
import de.tud.kom.socom.web.client.administration.AdministrationPresenter.AdministrationViewInterface;
import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;
import de.tud.kom.socom.web.client.sharedmodels.GameInstance;

public class GameStatisticAdministrationView extends Composite implements AdministrationViewInterface<GameInstance> {

	private static AdministrationViewUiBinder uiBinder = GWT.create(AdministrationViewUiBinder.class);
	private AdministrationPresenter presenter;

	interface AdministrationViewUiBinder extends UiBinder<Widget, GameStatisticAdministrationView> {
	}

	public GameStatisticAdministrationView(AdministrationPresenter presenter) {
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));
		presenter.loadGames(gameList);
	}

	@UiField
	ListBox gameList;
	@UiField
	SimplePanel statisticPanel;
	@UiField
	ErrorList errorList;

	public GameStatisticAdministrationView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void showError(ErrorListItemView error) {
		this.errorList.addError(error);
	}

	@Override
	public void hideErrors() {
		this.errorList.clear();
	}

	@Override
	public void hideError(ErrorListItemView error) {
		this.errorList.removeError(error);
	}

	@Override
	public void updateInformation(List<GameInstance> res, UIObject parent) {
		gameList.addItem("-", "none");
		for(GameInstance instance:  res) {
			String name = instance.getName() + " " + instance.getVersion();
			String value = String.valueOf(instance.getId());
			gameList.addItem(name, value);
		}
		gameList.removeStyleName("hidden");
	}
	
	@UiHandler("gameList")
	void onValueChanged(ChangeEvent ce) {
		showLoad(true);
		String gameid = gameList.getValue(gameList.getSelectedIndex());
		if(gameid.equals("none")){
		} else {
			presenter.onGameGraphSelection(Long.parseLong(gameid));
		}
	}

	public void setGraphData(JSONObject graph) {
		int offX = statisticPanel.getAbsoluteLeft();
		int offY = statisticPanel.getAbsoluteTop();
		GraphPanel panel = GraphPanel.get(offX, offY);
		panel.setGraph(graph);
		panel.showFullGraph();
		panel.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		showLoad(false);
		statisticPanel.setWidget(panel);
	}
	
	private void showLoad(boolean show){
		statisticPanel.clear();
		if(show)
			statisticPanel.add(new Label("Lade..."));
	}
}
