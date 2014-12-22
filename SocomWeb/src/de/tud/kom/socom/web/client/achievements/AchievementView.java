package de.tud.kom.socom.web.client.achievements;

import com.google.gwt.user.client.ui.Composite;

import de.tud.kom.socom.web.client.SoComWebEntryPoint;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

/** 
 * @author jkonert
 *
 */
public class AchievementView extends Composite implements AchievementPresenter.AchievementViewInterface {

//	@UiTemplate("AchievementView.ui.xml")
//	interface AchievementViewUiBinder extends UiBinder<Widget, AchievementView> {
//	}
//	
//	private static AchievementViewUiBinder uiBinder = GWT.create(AchievementViewUiBinder.class);
//		
	private AchievementPresenter presenter;
	

	public AchievementView(AchievementPresenter presenter) {
		this.presenter = presenter;		

		AchievementsPanel panel = new AchievementsPanel(SoComWebEntryPoint.getInstance());
		
		
		initWidget(panel);
//		initWidget(uiBinder.createAndBindUi(this));
	}


	@Override
	public void showError(ErrorListItemView error) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void hideErrors() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void hideError(ErrorListItemView error) {
		// TODO Auto-generated method stub
		
	}
	
}
