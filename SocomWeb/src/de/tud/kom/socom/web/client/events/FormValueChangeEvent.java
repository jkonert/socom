package de.tud.kom.socom.web.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.eventhandler.FormValueChangeEventHandler;
import de.tud.kom.socom.web.client.util.exceptions.InvalidCodeStateException;

/** can be thrown by View-Elements when the user selected/deselected a form-option and/or changed a value (e.g. of a input text field)
 * 
 * @author jkonert
 *
 */
public class FormValueChangeEvent extends GwtEvent<FormValueChangeEventHandler> {
	
	public static final Type<FormValueChangeEventHandler> TYPE = new Type<FormValueChangeEventHandler>();
	private Widget widget;
	private ChangeType changeType; // if false it was a value change
	private boolean selected;
	private String value;
	private String oldValue;
	private String message;
	
	public enum ChangeType
	{
		/** used as Type if the item changed the selected/deselected status **/
		 select,
		 /** used as Type if the items associated message changed (on each keyup event) **/
		message,
		 /** used as Type if the items associated file selection (if any!) changed (submit must still be triggered by Presenter) **/
		 file,
		 /* distinguish between a normal file upload and a drag n drop file upload since the upload mechanisms may be different */
		 file_drop
	}
	
	
	/**
	 * @deprecated better use constructor with ChangeType argument instead of booleans
	 * @param sourceWidget
	 * @param type
	 * @param selectedStatus
	 * @param oldValue
	 * @param currentValue
	 */
	@Deprecated
	public FormValueChangeEvent(Widget sourceWidget, boolean isSelectedChange, boolean selectedStatus, String oldValue, String currentValue)
	{
		this(sourceWidget,(isSelectedChange?ChangeType.select:ChangeType.message),selectedStatus,oldValue,currentValue, null);		
	}
	
	
	public FormValueChangeEvent(Widget sourceWidget, ChangeType type, boolean selectedStatus, String oldValue, String currentValue, String message)
	{
		if (type == null) throw new InvalidCodeStateException("ChangeType of event cannot be null");
		this.widget = sourceWidget;
		this.changeType = type;
		this.selected = selectedStatus;
		this.oldValue = oldValue;
		this.value = currentValue;
		this.message = message;
	}
	

	public Widget getSourceWidget() {
		return widget;
	}


	/** 
	 * @return true if the event change was a select status change and not a value change
	 */
	
	public boolean isSelectChange() {
		return changeType.equals(ChangeType.select);
	}
	
	/** 
	 * @return true if the event change was a change of the message associated with the (free) answer of a form (on each keyup)
	 */
	public boolean isMessageChange() {
		return changeType.equals(ChangeType.message);
	}
	
	/** 
	 * @return true if the event change was a change of the file selected by user for upload of the (free) answer of a form (submit has still to be handled)
	 */
	public boolean isFileChange() {
		return changeType.equals(ChangeType.file);
	}


	/** @return true if the corresponding checkbox (item) is selected */
	public boolean isSelected() {
		return selected;
	}

    /** the value corresponding to the current ChangeType. If ChangeType is "selected" this value has no meaning */
	public String getValue() {
		return value;
	}

	 /** the old value corresponding to the current ChangeType (before change). If ChangeType is "selected" this value has no meaning */
	public String getOldValue() {
		return oldValue;
	}
	
	/** @return  returns "select" if the item was selected or de-selected, "message" if the message associated changes (per keyup), "file" if the file selected changed (submit still has to be done) */
	public ChangeType getChangeType()
	{
		return this.changeType;
	}

	/** @return returns the additional message provided when the value is not the only "message" */
	public String getMessage(){
		return this.message;
	}

	@Override
	public Type<FormValueChangeEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(FormValueChangeEventHandler handler) {
		handler.onFormValueChangeEvent(this);
	}
	
}
