/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import com.flexwm.shared.wf.BmoWFEmail;
import com.flexwm.shared.wf.BmoWFlowUser;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiWFEmailForm extends UiForm {
	TextBox toTextBox = new TextBox();
	TextBox toNameTextBox = new TextBox();
	TextBox cpTextBox = new TextBox();
	TextBox fromTextBox = new TextBox();
	TextBox fromNameTextBox = new TextBox();
	TextBox replyToTextBox = new TextBox();
	TextBox subjectTextBox = new TextBox();
	TextArea bodyTextArea = new TextArea();
	TextBox fixedBodyTextBox = new TextBox();

	UiListBox wFlowUserListBox;
	BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();

	BmoWFEmail bmoWFEmail;
	int userId;
	int programId;
	int wFlowId;

	public UiWFEmailForm(UiParams uiParams, int id) {
		super(uiParams, new BmoWFEmail(), id);
		this.bmoWFEmail = (BmoWFEmail)getBmObject();
	}

	public UiWFEmailForm(UiParams uiParams, int id, BmoWFEmail bmoWFEmail) {
		super(uiParams, bmoWFEmail, id);
		this.bmoWFEmail = (BmoWFEmail)getBmObject();

		wFlowId = bmoWFEmail.getWFlowId().toInteger();

		BmFilter usersByWFlow = new BmFilter();
		usersByWFlow.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoWFEmail.getWFlowId().toInteger());
		wFlowUserListBox = new UiListBox(getUiParams(), bmoWFlowUser, usersByWFlow);
	}

	@Override
	public void populateFields() {
		bmoWFEmail = (BmoWFEmail)getBmObject();		

		// Asignar valores predeterminados
		userId = bmoWFEmail.getUserId().toInteger();
		if (!(userId > 0)) userId = getUiParams().getSFParams().getLoginInfo().getUserId();

		programId = bmoWFEmail.getProgramId().toInteger();
		if (!(programId > 0)) programId = getBmObjectProgramId();
		
		if (bmoWFEmail.getFrom().toString().equals("")) {
			try {
				bmoWFEmail.getFrom().setValue(getUiParams().getSFParams().getBmoSFConfig().getEmail().toString());
				bmoWFEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields() ERROR: " + e.toString());
			}
		}

		formFlexTable.addField(1, 0, wFlowUserListBox, bmoWFlowUser.getBmoUser().getEmail());
		formFlexTable.addLabelCell(1, 0, "Emails Usuarios");		
		formFlexTable.addField(2, 0, toTextBox, bmoWFEmail.getTo());
		formFlexTable.addField(3, 0, toNameTextBox, bmoWFEmail.getToName());
		formFlexTable.addField(4, 0, cpTextBox, bmoWFEmail.getCp());
		formFlexTable.addField(5, 0, replyToTextBox, bmoWFEmail.getReplyTo());
		formFlexTable.addField(6, 0, fromTextBox, bmoWFEmail.getFrom());
		formFlexTable.addField(7, 0, fromNameTextBox, bmoWFEmail.getFromName());
		formFlexTable.addField(8, 0, subjectTextBox, bmoWFEmail.getSubject());
		formFlexTable.addField(9, 0, bodyTextArea, bmoWFEmail.getBody());
		formFlexTable.addFieldReadOnly(10, 0, fixedBodyTextBox, bmoWFEmail.getFixedBody());
	}

	@Override
	public void postShow(){
		saveButton.setText("ENVIAR");
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoWFEmail.setId(id);
		bmoWFEmail.getTo().setValue(toTextBox.getText());
		bmoWFEmail.getToName().setValue(toNameTextBox.getText());
		bmoWFEmail.getCp().setValue(cpTextBox.getText());
		bmoWFEmail.getFrom().setValue(fromTextBox.getText());
		bmoWFEmail.getReplyTo().setValue(replyToTextBox.getText());
		bmoWFEmail.getFromName().setValue(fromNameTextBox.getText());
		bmoWFEmail.getSubject().setValue(subjectTextBox.getText());
		bmoWFEmail.getBody().setValue(bodyTextArea.getText());
		bmoWFEmail.getFixedBody().setValue(fixedBodyTextBox.getText());
		bmoWFEmail.getUserId().setValue(userId);
		bmoWFEmail.getProgramId().setValue(programId);
		bmoWFEmail.getWFlowId().setValue(wFlowId);
		return bmoWFEmail;
	}

	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == wFlowUserListBox) {
			if (wFlowUserListBox.getSelectedId().equals("") || wFlowUserListBox.getSelectedId().equals("0")) {
				toTextBox.setText("");
				toNameTextBox.setText("");
			} else {
				BmoWFlowUser bmoWFlowUser = (BmoWFlowUser)wFlowUserListBox.getSelectedBmObject();
				toTextBox.setText(bmoWFlowUser.getBmoUser().getEmail().toString());
				toNameTextBox.setText(bmoWFlowUser.getBmoUser().getFirstname().toString() + " " + bmoWFlowUser.getBmoUser().getFatherlastname().toString());
			}
		}
	}
}
