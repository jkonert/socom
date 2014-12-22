package de.tud.kom.socom.web.client.administration;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.sharedmodels.Report;

public class ReportReviewView extends Composite {

	private static UserPropertiesViewUiBinder uiBinder = GWT.create(UserPropertiesViewUiBinder.class);

	interface UserPropertiesViewUiBinder extends UiBinder<Widget, ReportReviewView> {
	}

	@UiField Label id, date, statusText, reviewInfo;
	@UiField InlineHyperlink informant, reference;
	@UiField TextArea reportArea, reviewArea;
	
	@UiField Button saveButton;
	@UiField Button closeButton;
	
	private AdministrationPresenter presenter;
	private Report report;

	public ReportReviewView(AdministrationPresenter presenter, Report report) {
		initWidget(uiBinder.createAndBindUi(this));
		this.presenter = presenter;
		this.report = report;
		
		init();
	}

	private void init() {
		id.setText(String.valueOf(report.getId()));
		date.setText(ReportAdministrationView.DTF.format(new Date(report.getDate())));
		informant.setText(report.getInformantName() + " (" + report.getInformant() + ")");
		informant.setTargetHistoryToken("./profiles/" + report.getInformant());
		reference.setText(createReferenceString());
		reference.setTargetHistoryToken(createReferenceTargetHistoryToken());
		reportArea.setText(report.getReport());
		
		if(report.isReviewed()){
			reviewArea.setText(report.getReview());
			reviewInfo.setText("Admin #" + report.getReviewedby() + " on " + ReportAdministrationView.DTF.format(new Date(report.getReviewedon())));
		}
	}

	private String createReferenceTargetHistoryToken() {
		if(report.getTypeName().equals("influenceanswer"))
			return "./influence/" + report.getReference2();
		else if(report.getTypeName().equals("user"))
			return "./profiles/" + report.getReference();
		else if(report.getTypeName().equals("content"))
			return "./content/" + report.getReference(); //FIXME change suitable to corresponding history token format when implemented
		else if(report.getTypeName().equals("comment"))
			return "./content/" + report.getReference2(); //FIXME
		else 
			return "./admin/report"; //fallback
	}

	private String createReferenceString() {
		if(report.getTypeName().equals("influenceanswer"))
			return "Influence " + report.getReference2();
		else if(report.getTypeName().equals("user"))
			return "Profil " + report.getReference();
		else if(report.getTypeName().equals("content"))
			return "Inhalt " + report.getReference();
		else if(report.getTypeName().equals("comment"))
			return "Kommentar " + report.getReference() + " bei Inhalt " + report.getReference2();
		else 
			return String.valueOf(report.getReference()); //fallback
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		presenter.onSaveReportReviewClicked(report, reviewArea.getText());
	}

	@UiHandler("closeButton")
	void onCloseButtonClick(ClickEvent event) {
		presenter.hideDialog();
	}

	public void showUnsuccess() {
		statusText.setText("Fehler beim Speichern!");
		Timer timer = new Timer() {
			@Override
			public void run() {
				statusText.setText("");
			}
		};
		timer.schedule(2000);
	}
}