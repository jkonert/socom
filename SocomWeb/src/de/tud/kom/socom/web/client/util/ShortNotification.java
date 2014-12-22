package de.tud.kom.socom.web.client.util;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class ShortNotification {
	
	public static final int STANDARD_DELAY = 3000;
	
	public static void show(String text) {
		show(text, STANDARD_DELAY);
	}
	
	public static void show(String text, int delayMillis){
		show(text, delayMillis, false);
	}
	
	public static void show(String text, boolean doReload){
		show(text, STANDARD_DELAY, doReload);
	}
	
	public static void show(String text, int delayMillis, final boolean doReload){
		final PopupPanel notificationPopup = new PopupPanel(false);
		final Label label = new Label(text);
		notificationPopup.setWidget(label);
		notificationPopup.setPopupPosition(50, 20);
		notificationPopup.setVisible(true);
		notificationPopup.show();

		Timer t = new Timer() {
			@Override
			public void run() {
				Animation a = new Animation() {
					@Override
					protected void onUpdate(double progress) {
						notificationPopup.getElement().getStyle().setProperty("opacity", String.valueOf(1-progress));
						if(progress == 1) {
							notificationPopup.hide();
							if(doReload)
								Location.reload();
						}
					}
				};
				a.run(500);
			}
		};
		
		t.schedule(delayMillis);
	}
}
