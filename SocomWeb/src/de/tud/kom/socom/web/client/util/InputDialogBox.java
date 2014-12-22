package de.tud.kom.socom.web.client.util;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author rhaban
 *
 */
public class InputDialogBox extends DialogBox {

	public interface InputCallback{
		public void callbackOK(String input);
		public void callbackCancel();
		public boolean isInputValid(String input);
	}

	private InputCallback callback;
	
	private TextBox input;
	private Button okButton;
	private Button cancelButton;
	private Label label;
	
	public InputDialogBox(String label, InputCallback cb){
		super();
		this.setText(label);
		this.callback = cb;
		build();
	}

	private void build() {
		this.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
		this.label = new Label();
		this.input = new TextBox();
		this.okButton = new Button("OK");
		this.cancelButton = new Button("Abbrechen");
		VerticalPanel vp = new VerticalPanel();
		vp.add(label);
		vp.add(input);
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(okButton);
		hp.add(cancelButton);
		vp.add(hp);

		//apply css
		this.label.getElement().getStyle().setColor("#f00");
		this.input.getElement().getStyle().setMargin(10, Unit.PX);
		this.okButton.getElement().getStyle().setMarginRight(5, Unit.PX);
		this.okButton.getElement().getStyle().setMarginLeft(30, Unit.PX);
		vp.getElement().getStyle().setMargin(5, Unit.PX);
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		setWidget(vp);
		initHandler();		
	}
	
	@Override
	public void show(){
		super.show();
		this.input.setFocus(true);
	}

	private void initHandler() {
		cancelButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				hide();
				callback.callbackCancel();
			}
		});
		
		okButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				if(callback.isInputValid(input.getText()))
				{
					hide();
					callback.callbackOK(input.getText());
				} 
				else 
				{
					label.setText("Eingabe nicht erlaubt.");
				}
			}
		});
		input.addKeyPressHandler(new KeyPressHandler() {
			@Override public void onKeyPress(KeyPressEvent event) {
				label.setText("");
			}
		});
	}	
}