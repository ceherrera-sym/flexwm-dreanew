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


import com.flexwm.shared.cm.BmoCustomerContact;
import com.flexwm.shared.cm.BmoCustomerEmail;
import com.flexwm.shared.op.BmoOrder;
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


public class UiOrderEmailForm extends UiForm {
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
	BmoOrder bmoOrder = new BmoOrder();
	int userId;
	int programId;
	int wFlowId;

	public UiOrderEmailForm(UiParams uiParams, int id) {
		super(uiParams, new BmoEmail(), id);
		this.bmoEmail = (BmoEmail)getBmObject();
	}
	
	public UiOrderEmailForm(UiParams uiParams, int id, BmoEmail bmoEmail, BmoOrder bmoOrder) {
		super(uiParams, bmoEmail, id);
		this.bmoEmail = (BmoEmail)getBmObject();
		this.bmoOrder = bmoOrder;
		
		BmFilter emailsByCustomer = new BmFilter();
		emailsByCustomer.setValueFilter(bmoCustomerEmail.getKind(), bmoCustomerEmail.getCustomerId(), bmoOrder.getBmoCustomer().getId());
		customerEmailListBox = new UiListBox(getUiParams(), bmoCustomerEmail, emailsByCustomer);

		BmFilter contactsByCustomer = new BmFilter();
		contactsByCustomer.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), bmoOrder.getBmoCustomer().getId());
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
				toTextBox.setText(bmoOrder.getBmoCustomer().getEmail().toString());
				toNameTextBox.setText(bmoOrder.getBmoCustomer().getDisplayName().toString());
			} else {
				BmoCustomerContact bmoCustomerContact = (BmoCustomerContact)customerContactListBox.getSelectedBmObject();
				toTextBox.setText(bmoCustomerContact.getEmail().toString());
				toNameTextBox.setText(bmoCustomerContact.getFullName().toString() + " " + bmoCustomerContact.getFatherLastName().toString());
			}
		} else if (event.getSource() == customerEmailListBox) {
			if (customerEmailListBox.getSelectedId().equals("") || customerEmailListBox.getSelectedId().equals("0")) {
				toTextBox.setText(bmoOrder.getBmoCustomer().getEmail().toString());
				toNameTextBox.setText(bmoOrder.getBmoCustomer().getDisplayName().toString());
			} else {
				BmoCustomerEmail bmoCustomerEmail = (BmoCustomerEmail)customerEmailListBox.getSelectedBmObject();
				toTextBox.setText(bmoCustomerEmail.getEmail().toString());
				toNameTextBox.setText(bmoOrder.getBmoCustomer().getDisplayName().toString());
			}
		} 
	}
}
