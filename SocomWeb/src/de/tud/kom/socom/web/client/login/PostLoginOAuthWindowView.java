package de.tud.kom.socom.web.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;
import de.tud.kom.socom.web.client.sharedmodels.SocialMediaApplications;

/** the view showing the result of token processing in the popup window
 *  It may as well automatically close the window
 * @author jkonert
 *
 */
public class PostLoginOAuthWindowView extends Composite implements LoginOAuthWindowPresenter.PostLoginOAuthWindowViewInterface {

        @UiTemplate("PostLoginOAuthWindowView.ui.xml")
        interface NetworkLoginViewUiBinder extends UiBinder<Widget, PostLoginOAuthWindowView> {
        }
        
        interface style extends CssResource
        {
                String invisible();
        }
        
        @UiField style style;
        @UiField SpanElement networkName;
        @UiField DivElement pleaseWait;
        @UiField DivElement success;
        @UiField SpanElement seconds;
        @UiField DivElement failure;
        @UiField Button retryButton;
        @UiField Button closeButton;
        
        @UiField ErrorList errorList;
        
        private static NetworkLoginViewUiBinder uiBinder = GWT.create(NetworkLoginViewUiBinder.class);
                
        private LoginOAuthWindowPresenter presenter;
        private String oldNetworkName = null;
        private String oldSeconds = null;

        public PostLoginOAuthWindowView(LoginOAuthWindowPresenter presenter) {
                this.presenter = presenter;             

                initWidget(uiBinder.createAndBindUi(this));
        }
        
        @Override
        public void setNetworkName(SocialMediaApplications app)
        {               
                if (this.oldNetworkName == null) this.oldNetworkName = networkName.getInnerText();
                networkName.setInnerText(app.getDisplayname());
        }
        
        @Override
        public void setSecconds(int sec)
        {
                if (this.oldSeconds  == null) this.oldSeconds = seconds.getInnerText();
                seconds.setInnerText(String.valueOf(sec));
        }
        
        @Override
        public void showSuccess()
        {
                pleaseWait.addClassName(style.invisible());
                failure.addClassName(style.invisible());                
                success.removeClassName(style.invisible());
                
        }
        
        @Override
        public void showFailure()
        {               
                pleaseWait.addClassName(style.invisible());
                success.addClassName(style.invisible());
                failure.removeClassName(style.invisible());
        }
        
        @Override
        public void clear()
        {
                networkName.setInnerText(oldNetworkName);
                pleaseWait.removeClassName(style.invisible());
                success.addClassName(style.invisible());
                failure.addClassName(style.invisible());
        }
        
        @Override
        public native void close()
        /*-{  
                        $wnd.close();  
        }-*/ ;
                        
        
        @UiHandler("retryButton")
        public void onRetryButtonClick(ClickEvent event)
        {
                presenter.onRetryButtonClick();
        }
        
        @UiHandler("closeButton")
        public void onCloseButtonClick(ClickEvent event)
        {
                presenter.onCloseButtonClick();
        }

		@Override
		public void showError(ErrorListItemView error) {
			errorList.addError(error);
			
		}

		@Override
		public void hideErrors() {
			errorList.clear();			
		}

		@Override
		public void hideError(ErrorListItemView error) {
			errorList.removeError(error);
			
		}        
}