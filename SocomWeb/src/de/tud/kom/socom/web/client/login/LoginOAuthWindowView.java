package de.tud.kom.socom.web.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/** a View using a Popup-Window
 * 
 * @author jkonert
 *
 */
public class LoginOAuthWindowView extends Composite implements LoginOAuthWindowPresenter.LoginOAuthWindowViewInterface {

	
	@UiTemplate("LoginOAuthWindowViewEmpty.ui.xml")
	interface LoginOAuthWindowViewUiBinder extends UiBinder<Widget, LoginOAuthWindowView> {
	}
	private static LoginOAuthWindowViewUiBinder uiBinder = GWT.create(LoginOAuthWindowViewUiBinder.class);
	
	protected JavaScriptObject jsWindowReference; /* native */
	private Timer windowCloseDetectTimer;
	
	private LoginOAuthWindowPresenter presenter;
	private Window window;

	private SafeUri uri;

	public LoginOAuthWindowView(LoginOAuthWindowPresenter presenter) {
		this.presenter = presenter;
		//...
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setVisibility(boolean visibility)
	{		
		this.setVisible(visibility);
		try
		{
			if (visibility && jsWindowReference == null && uri != null)
			{
				openWindow(uri.asString());
				if (windowCloseDetectTimer != null)
				{
					windowCloseDetectTimer.cancel();
				}
				else
				{
					windowCloseDetectTimer = new Timer()
					{
						@Override
						public void run() {
							if (jsWindowReference == null) onJSWindowClosed();
							try
							{
								if (jsIsClosedWindow()) onJSWindowClosed();
							}
							catch (Exception e)
							{
									// in case we get violations during redirects...just ignore..
							}
						}
						
					};
				}
				windowCloseDetectTimer.scheduleRepeating(100);
				
			}
			else if (visibility && jsWindowReference != null) 
			{
				jsFocusWindow();
			}
			else if (visibility)
			{
				// cannot open a window; ignore...maybe warn.
			}
			else if (jsWindowReference != null)
			{
				clear();
			}
		}
		catch (Exception e) {}
	}
	
	@Override
	public Widget asWidget()
	{
		return this;
	}
		

	@Override
	public void clear() {
		if (jsWindowReference != null)
		{
			jsCloseWindow();
			jsWindowReference = null;
		}
	}
	
	
	@Override
	public void setOAuthLoginURL(SafeUri uri) {
		this.uri = uri;
		
		try
		{
			if (jsWindowReference != null) jsSetWindowUrl(jsWindowReference, uri.asString());
		}
		catch (Exception e) {}
	}
		

	protected void onJSWindowClosed()
	{
		jsWindowReference = null;
		if (windowCloseDetectTimer != null) windowCloseDetectTimer.cancel();
		presenter.onViewClosed();
	}
	
	protected native Object openWindow(String uri)
	/*-{ 
		var topMargin = (screen.height - 600) / 2;
		var leftMargin = (screen.width - 1200) / 2;
		var w = $wnd.open(uri,"Login", "width=1200,height=600,resizable=yes,scrollbars=yes,popup,top=" + topMargin + ",left=" + leftMargin);				
		this.@de.tud.kom.socom.web.client.login.LoginOAuthWindowView::jsWindowReference = w;
		
	}-*/; 
	
	protected native void jsCloseWindow()
	/*-{ this.@de.tud.kom.socom.web.client.login.LoginOAuthWindowView::jsWindowReference.close();}-*/; 
	
	protected native void jsFocusWindow()
	/*-{ this.@de.tud.kom.socom.web.client.login.LoginOAuthWindowView::jsWindowReference.focus(); }-*/; 
	
	protected native void jsSetWindowUrl(JavaScriptObject jsWIndowReference, String uri)
	/*-{ this.@de.tud.kom.socom.web.client.login.LoginOAuthWindowView::jsWindowReference.location.href = uri; }-*/; 
	
	protected native boolean jsIsClosedWindow()
	/*-{ return this.@de.tud.kom.socom.web.client.login.LoginOAuthWindowView::jsWindowReference.closed; }-*/; 
}
