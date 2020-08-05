/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoHelpDesk;
import com.flexwm.shared.op.BmoHelpDeskType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.sf.BmoUser;

/**
 * @author smuniz
 *
 */

public class UiHelpDeskForm extends UiForm{
	TextBox consecutiveTextBox = new TextBox();
	UiSuggestBox userIdUiSuggestBox = new UiSuggestBox(new BmoUser());
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	UiListBox prioridadUiListBox = new UiListBox(getUiParams());
	UiListBox helpDeskTypeIdUiListBox = new UiListBox(getUiParams(), new BmoHelpDeskType());
	TextArea solutionTextArea = new TextArea();
	DateBox statusDateDateBox = new DateBox();
	DateBox dateAttendedDateBox = new DateBox();
	UiSuggestBox ownerIdUiSuggestBox = new UiSuggestBox(new BmoUser());
	DateBox dateDateBox = new DateBox();
	UiListBox statusUiListBox = new UiListBox(getUiParams());
	UiSuggestBox equipmentIdUiSuggestBox = new UiSuggestBox(new BmoEquipment());
	
	BmoHelpDesk bmoHelpDesk;

	public UiHelpDeskForm(UiParams uiParams, int id) {
		super(uiParams, new BmoHelpDesk(), id); 
	}
	
	@Override
	public void populateFields(){
		bmoHelpDesk = (BmoHelpDesk)getBmObject();
		formFlexTable.addField(1, 0, consecutiveTextBox, bmoHelpDesk.getConsecutive());
		formFlexTable.addField(1, 2, userIdUiSuggestBox, bmoHelpDesk.getUserId());
		formFlexTable.addField(2, 0, nameTextBox, bmoHelpDesk.getName());
		formFlexTable.addField(2, 2, descriptionTextArea, bmoHelpDesk.getDescription());
		formFlexTable.addField(3, 0, prioridadUiListBox, bmoHelpDesk.getPrioridad());
		formFlexTable.addField(3, 2, helpDeskTypeIdUiListBox, bmoHelpDesk.getHelpDeskTypeId());
		formFlexTable.addField(4, 0, solutionTextArea, bmoHelpDesk.getSolution());
		formFlexTable.addField(4, 2, statusDateDateBox, bmoHelpDesk.getStatusDate());
		formFlexTable.addField(5, 0, dateAttendedDateBox, bmoHelpDesk.getDateAttended());
		formFlexTable.addField(5, 2, ownerIdUiSuggestBox, bmoHelpDesk.getOwnerId());
		formFlexTable.addField(6, 0, dateDateBox, bmoHelpDesk.getDate());
		formFlexTable.addField(6, 2, statusUiListBox, bmoHelpDesk.getStatus());
		formFlexTable.addField(7, 0, equipmentIdUiSuggestBox, bmoHelpDesk.getEquipmentId());

	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoHelpDesk.setId(id);
		bmoHelpDesk.getConsecutive().setValue(consecutiveTextBox.getText());
		bmoHelpDesk.getUserId().setValue(userIdUiSuggestBox.getSelectedId()); 
		bmoHelpDesk.getName().setValue(nameTextBox.getText());
		bmoHelpDesk.getDescription().setValue(descriptionTextArea.getText());
		bmoHelpDesk.getPrioridad().setValue(prioridadUiListBox.getSelectedCode());
		bmoHelpDesk.getHelpDeskTypeId().setValue(helpDeskTypeIdUiListBox.getSelectedId());
		bmoHelpDesk.getSolution().setValue(solutionTextArea.getText());
		bmoHelpDesk.getStatusDate().setValue(statusDateDateBox.getTextBox().getText());
		bmoHelpDesk.getDateAttended().setValue(dateAttendedDateBox.getTextBox().getText());
		bmoHelpDesk.getOwnerId().setValue(ownerIdUiSuggestBox.getSelectedId()); 
		bmoHelpDesk.getDate().setValue(dateDateBox.getTextBox().getText());
		bmoHelpDesk.getStatus().setValue(statusUiListBox.getSelectedCode());
		bmoHelpDesk.getEquipmentId().setValue(equipmentIdUiSuggestBox.getSelectedId()); 

		return bmoHelpDesk;
	}
	
	@Override
	public void close() {
		UiHelpDeskList uiHelpDeskList = new UiHelpDeskList(getUiParams());
		uiHelpDeskList.show();
	}
}


