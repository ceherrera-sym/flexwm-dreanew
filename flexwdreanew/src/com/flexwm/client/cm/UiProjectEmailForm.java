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


import com.flexwm.shared.cm.BmoCustomerEmail;
import com.flexwm.shared.cm.BmoProject;
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


public class UiProjectEmailForm extends UiForm {
	TextBox toTextBox = new TextBox();
	TextBox toNameTextBox = new TextBox();
	TextBox cpTextBox = new TextBox();
	TextBox fromTextBox = new TextBox();
	TextBox fromNameTextBox = new TextBox();
	TextBox replyToTextBox = new TextBox();
	TextBox subjectTextBox = new TextBox();
	TextArea bodyTextArea = new TextArea();
	TextBox fixedBodyTextBox = new TextBox();
	
	UiListBox emailListBox;
	BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
	
	BmoEmail bmoEmail;
	int userId;
	int programId;
	int wFlowId;

	public UiProjectEmailForm(UiParams uiParams, int id) {
		super(uiParams, new BmoEmail(), id);
		this.bmoEmail = (BmoEmail)getBmObject();
	}
	
	public UiProjectEmailForm(UiParams uiParams, int id, BmoEmail bmoEmail, BmoProject bmoProject) {
		super(uiParams, bmoEmail, id);
		this.bmoEmail = (BmoEmail)getBmObject();
		

		BmFilter emailByCustomer = new BmFilter();
		emailByCustomer.setValueFilter(bmoCustomerEmail.getKind(), bmoCustomerEmail.getCustomerId(), bmoProject.getBmoCustomer().getId());
		emailListBox = new UiListBox(getUiParams(), bmoCustomerEmail, emailByCustomer);
	}

	@Override
	public void populateFields() {
		bmoEmail = (BmoEmail)getBmObject();		

		formFlexTable.addField(1, 0, emailListBox, bmoCustomerEmail.getEmail());
		formFlexTable.addField(2, 0, toTextBox, bmoEmail.getTo());
		formFlexTable.addField(3, 0, toNameTextBox, bmoEmail.getToName());
		formFlexTable.addField(4, 0, cpTextBox, bmoEmail.getCp());
		formFlexTable.addField(5, 0, replyToTextBox, bmoEmail.getReplyTo());
		
		// Asignar 
		if (bmoEmail.getFrom().toString().equals("")) {
			try {
				bmoEmail.getFrom().setValue(getUiParams().getSFParams().getBmoSFConfig().getEmail().toString());
				bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields() ERROR: " + e.toString());
			}
		}
		formFlexTable.addFieldReadOnly(6, 0, fromTextBox, bmoEmail.getFrom());
		formFlexTable.addFieldReadOnly(7, 0, fromNameTextBox, bmoEmail.getFromName());
		formFlexTable.addField(8, 0, subjectTextBox, bmoEmail.getSubject());
		formFlexTable.addFieldReadOnly(9, 0, fixedBodyTextBox, bmoEmail.getFixedBody());
		formFlexTable.addField(10, 0, bodyTextArea, bmoEmail.getBody());
		
		userId = bmoEmail.getUserId().toInteger();
		if (!(userId > 0)) userId = getUiParams().getSFParams().getLoginInfo().getUserId();
		
		programId = bmoEmail.getProgramId().toInteger();
		if (!(programId > 0)) programId = getBmObjectProgramId();
		
		wFlowId = bmoEmail.getWFlowId().toInteger();
	}
	
	@Override
	public void postShow(){
		saveButton.setText("Enviar");
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
		if (!emailListBox.getSelectedId().equals("")) toTextBox.setText(((BmoCustomerEmail)emailListBox.getSelectedBmObject()).getEmail().toString());
		else toTextBox.setText("");
	}
}
