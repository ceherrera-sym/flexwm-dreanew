/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoCustomerDate;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiCustomerDateLabelList extends UiFormLabelList {
	UiListBox typeListBox = new UiListBox(getUiParams());
	BmoCustomerDate bmoCustomerDate;
	
	TextBox descriptionTextBox = new TextBox();
	UiDateBox relevantDateBox = new UiDateBox();
	TextBox remindDateDateBox = new TextBox();
	CheckBox emailReminderCheckBox = new CheckBox();
	int customerId;
	

	public UiCustomerDateLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerDate());
		customerId = id;
		bmoCustomerDate = new BmoCustomerDate();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerDate.getKind(), 
				bmoCustomerDate.getCustomerId().getName(), 
				bmoCustomerDate.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerDate.getCustomerId().getName());
	}
	
	@Override
	public void populateFields() {
		bmoCustomerDate = (BmoCustomerDate)getBmObject();
		formFlexTable.addField(1, 0, typeListBox, bmoCustomerDate.getType());
		formFlexTable.addField(2, 0, relevantDateBox, bmoCustomerDate.getRelevantDate());
		formFlexTable.addField(3, 0, descriptionTextBox, bmoCustomerDate.getDescription());
		formFlexTable.addField(4, 0, emailReminderCheckBox, bmoCustomerDate.getEmailReminder());
		formFlexTable.addField(5, 0, remindDateDateBox, bmoCustomerDate.getRemindDate());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerDate = new BmoCustomerDate();
		bmoCustomerDate.setId(id);
		bmoCustomerDate.getType().setValue(typeListBox.getSelectedCode());
		bmoCustomerDate.getRelevantDate().setValue(relevantDateBox.getTextBox().getText());
		bmoCustomerDate.getDescription().setValue(descriptionTextBox.getText());
		bmoCustomerDate.getCustomerId().setValue(customerId);
		bmoCustomerDate.getRemindDate().setValue(remindDateDateBox.getText());
		bmoCustomerDate.getEmailReminder().setValue(emailReminderCheckBox.getValue());

		return bmoCustomerDate;
	}
}
