package de.tud.kom.socom.web.client.administration;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.administration.AdministrationPresenter.AdministrationViewInterface;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorReportFilterRestrictionView;
import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;
import de.tud.kom.socom.web.client.sharedmodels.Report;

public class ReportAdministrationView extends Composite implements AdministrationViewInterface<Report> {

	private static AdministrationViewUiBinder uiBinder = GWT.create(AdministrationViewUiBinder.class);
	private AdministrationPresenter presenter;
	public static final DateTimeFormat DTF = DateTimeFormat.getFormat("d.MM.yy HH:mm");

	interface AdministrationViewUiBinder extends UiBinder<Widget, ReportAdministrationView> {
	}

	public ReportAdministrationView(AdministrationPresenter presenter) {
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));
	}

	interface CellCss extends CssResource {
		String reporttablecelleven();

		String reporttablecellodd();

		String reporttablecelltop();
		
		String boldonhover();
	}

	@UiField
	CellCss style;
	@UiField
	ErrorList errorList;
	@UiField
	Button unfoldButton;
	@UiField
	Button fetchButton;
	@UiField
	HTMLPanel filterPanel;
	@UiField
	HTMLPanel foldedFilterPanel;
	@UiField
	FlexTable reportTable;
	@UiField
	CheckBox userChk, contentChk, commentChk, inflansChk, showReviewedChk, useUserChk;
	@UiField
	TextBox informantBox;
	@UiField
	IntegerBox limitBox, offsetBox;
	@UiField
	ListBox orderbyList, orderbyAscDescList;

	public ReportAdministrationView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("fetchButton")
	public void onFetchClicked(ClickEvent e) {
		reportTable.removeAllRows();
		hideErrors();
		boolean user = userChk.getValue();
		boolean content = contentChk.getValue();
		boolean comment = commentChk.getValue();
		boolean inflans = inflansChk.getValue();
		String[] types = new String[0 + (user ? 1 : 0) + (content ? 1 : 0) + (comment ? 1 : 0) + (inflans ? 1 : 0)];

		Integer limit = limitBox.getValue();
		Integer offset = offsetBox.getValue();
		if (limit < 1 || offset < 0 || types.length == 0) {
			showError(new ErrorReportFilterRestrictionView());
			return;
		}
		int c = 0;
		if (user)
			types[c++] = "user";
		if (content)
			types[c++] = "content";
		if (comment)
			types[c++] = "contentcomment";
		if (inflans)
			types[c++] = "influenceanswer";

		boolean alreadyReviewed = showReviewedChk.getValue();
		String fromInformant = useUserChk.getValue() ? informantBox.getValue() : null;
		int sortPolicy = orderbyList.getSelectedIndex();
		boolean ascending = orderbyAscDescList.getSelectedIndex() == 0;

		presenter.loadReports(types, alreadyReviewed, fromInformant, limit, offset, sortPolicy, ascending);

		filterPanel.addStyleName("hidden");
		foldedFilterPanel.removeStyleName("hidden");
	}

	@UiHandler("unfoldButton")
	public void onUnfoldFilterPanelClicked(ClickEvent e) {
		foldedFilterPanel.addStyleName("hidden");
		filterPanel.removeStyleName("hidden");
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
	/**
	 * ignore parent (=null) since its always reportTable widget
	 */
	public void updateInformation(List<Report> res, UIObject parent) {
		int col = 0;
		reportTable.setText(0, col++, "ID");
		reportTable.setText(0, col++, "Datum");
		reportTable.setText(0, col++, "Typ");
		reportTable.setText(0, col++, "Informant (ID)");
		reportTable.setText(0, col++, "Grund");
		reportTable.setText(0, col++, "Geprüft");
		reportTable.setText(0, col, "Bearbeiten");

		for (int row = 1; row < res.size() + 1; row++) {
			col = 0;
			Report report = res.get(row - 1);
			reportTable.setText(row, col++, String.valueOf(report.getId()));
			reportTable.setText(row, col++, DTF.format(new Date(report.getDate())));
			reportTable.setText(row, col++, report.getTypeName());
			reportTable.setText(row, col++, report.getInformantName() + " (" + report.getInformant() + ")");
			reportTable.setText(row, col++, getShortenedReportText(report));
			reportTable.setWidget(row, col++, createCheckbox(report));
			reportTable.setWidget(row, col, createEditButton(report));
		}

		paintGrid();

		reportTable.removeStyleName("hidden");
	}

	private void paintGrid() {
		String[] widths = new String[]{"3%", "12%", "12%", "13%", "44%", "6%", "10%", 
				"10px", "10px", "10px", "10px"}; //last 4 are backup
		
		int cols = reportTable.getCellCount(0);
		for (int i = 0; i < cols; i++)
			reportTable.getCellFormatter().addStyleName(0, i, style.reporttablecelltop());

		for (int i = 1; i < reportTable.getRowCount(); i++){
			reportTable.getRowFormatter().addStyleName(i, style.boldonhover());
			for (int j = 0; j < cols; j++){
				reportTable.getCellFormatter().addStyleName(i, j,
						i % 2 == 0 ? style.reporttablecellodd() : style.reporttablecelleven());
				reportTable.getCellFormatter().setWidth(i, j, widths[j]);
			}
		}
	}

	private String getShortenedReportText(Report report) {
		String reportText = report.getReport();
		if (reportText.length() > 65)
			reportText = reportText.substring(0, 65) + "...";
		return reportText;
	}

	private Button createEditButton(final Report report) {
		Button b = new Button();
		b.addStyleName("button");
		b.setText(report.isReviewed() ? "Ändern" : "Prüfen");
		b.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				presenter.onReportClicked(report);
			}
		});
		return b;
	}

	private CheckBox createCheckbox(Report report) {
		CheckBox cb = new CheckBox();
		cb.setValue(report.isReviewed());
		cb.setEnabled(false);
		return cb;
	}

	public void checkReviewed(long id) {
		for (int j = 0; j < reportTable.getRowCount(); j++){
			if(reportTable.getText(j, 0).equals(String.valueOf(id))){
				Widget cb = reportTable.getWidget(j, 5);
				if(cb instanceof CheckBox)
					((CheckBox)cb).setValue(true);
				Widget b = reportTable.getWidget(j, 6);
				if(b instanceof Button)
					((Button)b).setText("Ändern");
				break;
			}
		}
	}
}
