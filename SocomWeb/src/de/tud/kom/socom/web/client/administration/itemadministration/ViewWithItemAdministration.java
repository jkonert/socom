package de.tud.kom.socom.web.client.administration.itemadministration;

import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration.ChangeVisibilityButtonCallback;

public interface ViewWithItemAdministration {
	
//	public void setState(ItemAdministrationState state);
//	public void setCallback(ItemAdministration.DeleteButtonCallback cb);
	public void setAdministrationState(ItemAdministrationState state, ItemAdministration.DeleteButtonCallback cb);
	public void setAdministrationState(ItemAdministrationState state);
	public void setItemAdministrationVisibility(int visibility);
	public void setItemAdministrationVisibilityChangeCallback(ChangeVisibilityButtonCallback cb);
}
