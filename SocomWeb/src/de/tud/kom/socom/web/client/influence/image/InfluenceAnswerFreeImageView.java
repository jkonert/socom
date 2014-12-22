package de.tud.kom.socom.web.client.influence.image;

import org.moxieapps.gwt.uploader.client.File;
import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent;
import org.moxieapps.gwt.uploader.client.events.UploadErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessEvent;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.eventhandler.FormSubmitCompleteEventHandler;
import de.tud.kom.socom.web.client.eventhandler.FormSubmitEventHandler;
import de.tud.kom.socom.web.client.events.FormSubmitCompleteEvent;
import de.tud.kom.socom.web.client.events.FormSubmitEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent.ChangeType;
import de.tud.kom.socom.web.client.influence.InfluencePresenter;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;
import de.tud.kom.socom.web.client.util.InputDialogBox;
import de.tud.kom.socom.web.client.util.InputDialogBox.InputCallback;

/**
 * @author rhaban
 * 
 */
public class InfluenceAnswerFreeImageView extends InfluenceAnswerImageView implements
		InfluencePresenter.InfluenceFreeAnswerWithFileViewInterface<AnswerImage> {

	@UiTemplate("InfluenceAnswerImageView.ui.xml")
	interface InfluenceAnswerFreeTextViewUiBinder extends UiBinder<Widget, InfluenceAnswerFreeImageView> {
	}

	private static InfluenceAnswerFreeTextViewUiBinder uiBinder = GWT.create(InfluenceAnswerFreeTextViewUiBinder.class);

	// @UiField inherited plus:
	@UiField
	FocusPanel freeimg;
	@UiField
	FormPanel answerForm;
	@UiField
	FileUpload answerFileUpload;
	@UiField
	HTMLPanel answerFormHiddenContainer;

	//uploader for drag n drop uploads
	private Uploader uploader;
	
	/**
	 * @param isResultView
	 *            if set true, this view displays itself as a result view and
	 *            does not report/display any inputs for a form
	 * @param expectedAnswerLength
	 *            number of chars expected for answer length (helps view to
	 *            layout the input properly)
	 */
	public InfluenceAnswerFreeImageView(boolean isResultView, int expectedAnswerLength) {
		super(isResultView);
		this.itemReportView.addStyleName("hidden");
		this.itemAdministration.addStyleName("hidden");
		this.img.addStyleName("hidden");
		this.answerLabel.addStyleName("hidden");
		this.magnifier.addStyleName("hidden");

		this.freeimg.removeStyleName("hidden");
		this.answerFileUpload.setName("data"); // not possible via XML...but
												// server looks for this field
	}

	@Override
	void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		initDropHandler();
	}

	private void initDropHandler() {
		if (Uploader.isAjaxUploadWithProgressEventsSupported())
		{
			uploader = new Uploader();
			freeimg.addDragOverHandler(new DragOverHandler() {
				@Override
				public void onDragOver(DragOverEvent event) {
					event.preventDefault();
				}
			});
			freeimg.addDragEnterHandler(new DragEnterHandler() {
				@Override
				public void onDragEnter(DragEnterEvent event) {
					event.preventDefault();
				}
			});
			freeimg.addDragLeaveHandler(new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {
					event.preventDefault();
				}
			});

			freeimg.addDropHandler(new DropHandler() {
				@Override
				public void onDrop(DropEvent event) {
					@SuppressWarnings("unchecked")
					JsArray<File> droppedFiles = Uploader.getDroppedFiles(event.getNativeEvent());
					uploader.addFilesToQueue(droppedFiles);
					final String value = droppedFiles.get(0).getName();
					InputDialogBox inputDialogBox = new InputDialogBox("Gib eine Nachricht ein.", new InputCallback() {
						
						@Override
						public boolean isInputValid(String input) {
							return input.length() > 2 && input.length() < 200;
						}
						
						@Override
						public void callbackOK(String input) {
							fireEvent(new FormValueChangeEvent(InfluenceAnswerFreeImageView.this, ChangeType.file_drop, true, null,
									value, input));
						}

						@Override
						public void callbackCancel() {
							 // do nothing
						}
					});
					inputDialogBox.center();
					inputDialogBox.show();
					event.preventDefault();
				}
			});
		}
	}

	@UiHandler("freeimg")
	public void onAddImageClicked(ClickEvent e) {
		fakeAddImageFormClick(answerFileUpload.getElement());
	}

	// this native method fakes a click on the hidden upload form
	private static native void fakeAddImageFormClick(Element element) /*-{
		element.click();
	}-*/;

	@UiHandler("answerFileUpload")
	public void onValueChangeFileUpload(ChangeEvent event) {
		// start upload on select image
		final String value = this.answerFileUpload.getFilename();
		InputDialogBox inputDialogBox = new InputDialogBox("Gib eine Nachricht ein.", new InputCallback() {
			
			@Override
			public boolean isInputValid(String input) {
				return input.length() > 2 && input.length() < 200;
			}
			
			@Override
			public void callbackOK(String input) {
				fireEvent(new FormValueChangeEvent(InfluenceAnswerFreeImageView.this, ChangeType.file, true, null, value, input));
			}

			@Override
			public void callbackCancel() {
				 //do nothing
			}
		});
		inputDialogBox.center();
		inputDialogBox.show();
	}

	@UiHandler("answerForm")
	public void onFileFormSubmitComplete(SubmitCompleteEvent event) {
		this.fireEvent(new FormSubmitCompleteEvent(this, event.getResults()));
	}

	@UiHandler("answerForm")
	public void onFileFormSubmitComplete(SubmitEvent event) {
		this.fireEvent(new FormSubmitEvent(this));
	}

	@Override
	public void submitFile(SafeUri action, SimpleEntry<String, String>... hiddenParameters) {
		submitFile(ChangeType.file, action, hiddenParameters);
	}

	@Override
	public void submitFile(ChangeType type, SafeUri action, SimpleEntry<String, String>... hiddenParameters) {
		switch(type)
		{
		case file: 
			this.answerForm.setAction(action);
			// add new hidden params if given
			if (hiddenParameters != null)
			{
				for (int i = 0; i < hiddenParameters.length; i++)
				{
					SimpleEntry<String, String> e = hiddenParameters[i];
					Hidden ip = new Hidden(e.getKey(), e.getValue());
					this.answerFormHiddenContainer.add(ip);
				}
			}
			this.answerForm.submit();
			break;
		case file_drop: 
			uploader.setUploadURL(action.asString());
			JSONObject postParams = new JSONObject();
			for(SimpleEntry<String, String> se : hiddenParameters)
			{
				postParams.put(se.getKey(), new JSONString(se.getValue()));
			}
			uploader.setPostParams(postParams);
			uploader.startUpload();
			uploader.setUploadSuccessHandler(new UploadSuccessHandler() {
				
				@Override
				public boolean onUploadSuccess(UploadSuccessEvent uploadSuccessEvent) {
					fireEvent(new FormSubmitCompleteEvent(InfluenceAnswerFreeImageView.this, uploadSuccessEvent.getServerData()));
					return true;
				}
			});
			
			uploader.setUploadErrorHandler(new UploadErrorHandler() {
				@Override
				public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {
					fireEvent(new FormSubmitCompleteEvent(InfluenceAnswerFreeImageView.this, uploadErrorEvent.getMessage()));
					return true;
				}
			});
			break;
		default:
			break;
		}
	}

	@Override
	public HandlerRegistration addFormSubmitHandler(FormSubmitEventHandler handler) {
		return this.addHandler(handler, de.tud.kom.socom.web.client.events.FormSubmitEvent.TYPE);
	}

	@Override
	public HandlerRegistration addFormSubmitCompleteHandler(FormSubmitCompleteEventHandler handler) {
		return this.addHandler(handler, de.tud.kom.socom.web.client.events.FormSubmitCompleteEvent.TYPE);
	}

	@Override
	public void setSubmitEnabled(boolean enabled) {
		// no submit button
	}

	@Override
	public void disableAddAnswer() {
		// no "add answer" button
	}

	@Override
	public AnswerImage getFreeAnswerValue() {
		throw new RuntimeException("cant know answer here");
		// and also shouldnt be called
	}
}
