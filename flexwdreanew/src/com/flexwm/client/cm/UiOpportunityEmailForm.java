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


import com.flexwm.shared.cm.BmoCustomerContact;
import com.flexwm.shared.cm.BmoCustomerEmail;
import com.flexwm.shared.cm.BmoOpportunity;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.sf.BmoEmail;


public class UiOpportunityEmailForm extends UiForm {
	TextBox toTextBox = new TextBox();
	TextBox toNameTextBox = new TextBox();
	TextBox cpTextBox = new TextBox();
	TextBox fromTextBox = new TextBox();
	TextBox fromNameTextBox = new TextBox();
	TextBox replyToTextBox = new TextBox();
	TextBox subjectTextBox = new TextBox();
	TextArea bodyTextArea = new TextArea();
	TextBox fixedBodyTextBox = new TextBox();
	
	UiListBox customerEmailListBox;
	BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
	
	UiListBox customerContactListBox;
	BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
	
	BmoEmail bmoEmail;
	BmoOpportunity bmoOpportunity = new BmoOpportunity();
	int userId;
	int programId;
	int wFlowId;

	public UiOpportunityEmailForm(UiParams uiParams, int id) {
		super(uiParams, new BmoEmail(), id);
		this.bmoEmail = (BmoEmail)getBmObject();
	}
	
	public UiOpportunityEmailForm(UiParams uiParams, int id, BmoEmail bmoEmail, BmoOpportunity bmoOpportunity) {
		super(uiParams, bmoEmail, id);
		this.bmoEmail = (BmoEmail)getBmObject();
		this.bmoOpportunity = bmoOpportunity;
		
		BmFilter emailsByCustomer = new BmFilter();
		emailsByCustomer.setValueFilter(bmoCustomerEmail.getKind(), bmoCustomerEmail.getCustomerId(), bmoOpportunity.getBmoCustomer().getId());
		customerEmailListBox = new UiListBox(getUiParams(), bmoCustomerEmail, emailsByCustomer);

		BmFilter contactsByCustomer = new BmFilter();
		contactsByCustomer.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), bmoOpportunity.getBmoCustomer().getId());
		customerContactListBox = new UiListBox(getUiParams(), bmoCustomerContact, contactsByCustomer);
	}

	@Override
	public void populateFields() {
		bmoEmail = (BmoEmail)getBmObject();		

		// Asignar valores predeterminados
		bmoCustomerEmail.getEmail().setNullable(true);
		
		userId = bmoEmail.getUserId().toInteger();
		if (!(userId > 0)) userId = getUiParams().getSFParams().getLoginInfo().getUserId();
		
		programId = bmoEmail.getProgramId().toInteger();
		if (!(programId > 0)) programId = getBmObjectProgramId();
		
		wFlowId = bmoEmail.getWFlowId().toInteger();
		
		if (bmoEmail.getFrom().toString().equals("")) {
			try {
				bmoEmail.getFrom().setValue(getUiParams().getSFParams().getBmoSFConfig().getEmail().toString());
				bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields() ERROR: " + e.toString());
			}
		}

		formFlexTable.addField(1, 0, customerEmailListBox, bmoCustomerEmail.getEmail());
		formFlexTable.addLabelCell(1, 0, "Emails Cliente");		
		
		formFlexTable.addField(2, 0, customerContactListBox, bmoCustomerContact.getEmail());
		formFlexTable.addLabelCell(2, 0, "Emails Contactos");
		
		formFlexTable.addField(3, 0, toTextBox, bmoEmail.getTo());
		formFlexTable.addField(4, 0, toNameTextBox, bmoEmail.getToName());
		formFlexTable.addField(5, 0, cpTextBox, bmoEmail.getCp());
		formFlexTable.addField(6, 0, replyToTextBox, bmoEmail.getReplyTo());
		formFlexTable.addFieldReadOnly(7, 0, fromTextBox, bmoEmail.getFrom());
		formFlexTable.addFieldReadOnly(8, 0, fromNameTextBox, bmoEmail.getFromName());
		formFlexTable.addField(9, 0, subjectTextBox, bmoEmail.getSubject());
		formFlexTable.addField(10, 0, bodyTextArea, bmoEmail.getBody());
		formFlexTable.addFieldReadOnly(11, 0, fixedBodyTextBox, bmoEmail.getFixedBody());
	}
	
	@Override
	public void postShow(){
		saveButton.setText("ENVIAR");
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoEmail.setId(id);
		bmoEmail.getTo().setValue(toTextBox.getText());
		bmoEmail.getToName().setValue(toNameTextBox.getText());
		bmoEmail.getCp().setValue(cpTextBox.getText());
		bmoEmail.getFrom().setValue(fromTextBox.getText());
		bmoEmail.getReplyTo().setValue(replyToTextBox.getText());
		bmoEmail.getFromName().setValue(fromNameTextBox.getText());
		bmoEmail.getSubject().setValue(subjectTextBox.getText());
		bmoEmail.getBody().setValue(bodyTextArea.getText());
		bmoEmail.getFixedBody().setValue(fixedBodyTextBox.getText());
		bmoEmail.getUserId().setValue(userId);
		bmoEmail.getProgramId().setValue(programId);
		bmoEmail.getWFlowId().setValue(wFlowId);
		return bmoEmail;
	}
	
	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == customerContactListBox) {
			if (customerContactListBox.getSelectedId().equals("") || customerContactListBox.getSelectedId().equals("0")) {
				toTextBox.setText(bmoOpportunity.getBmoCustomer().getEmail().toString());
				toNameTextBox.setText(bmoOpportunity.getBmoCustomer().getDisplayName().toString());
			} else {
				BmoCustomerContact bmoCustomerContact = (BmoCustomerContact)customerContactListBox.getSelectedBmObject();
				toTextBox.setText(bmoCustomerContact.getEmail().toString());
				toNameTextBox.setText(bmoCustomerContact.getFullName().toString() + " " + bmoCustomerContact.getFatherLastName().toString());
			}
		} else if (event.getSource() == customerEmailListBox) {
			if (customerEmailListBox.getSelectedId().equals("") || customerEmailListBox.getSelectedId().equals("0")) {
				toTextBox.setText(bmoOpportunity.getBmoCustomer().getEmail().toString());
				toNameTextBox.setText(bmoOpportunity.getBmoCustomer().getDisplayName().toString());
			} else {
				BmoCustomerEmail bmoCustomerEmail = (BmoCustomerEmail)customerEmailListBox.getSelectedBmObject();
				toTextBox.setText(bmoCustomerEmail.getEmail().toString());
				toNameTextBox.setText(bmoOpportunity.getBmoCustomer().getDisplayName().toString());
			}
		} 
	}
}
