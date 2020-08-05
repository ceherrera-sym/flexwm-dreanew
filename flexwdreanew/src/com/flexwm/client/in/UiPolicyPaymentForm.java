/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.in;

import com.flexwm.shared.in.BmoPolicyPayment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiPolicyPaymentForm extends UiForm {
	BmoPolicyPayment bmoPolicyPayment;
	int policyId;
	
	TextBox amountTextBox = new TextBox();
	TextArea commentsTextArea = new TextArea();
	DateBox dueDateBox = new DateBox();	
	DateBox payDateBox = new DateBox();
	UiListBox statusListBox = new UiListBox(getUiParams());
	
	BmFilter forceFilter;
	
	public UiPolicyPaymentForm(UiParams uiParams, int id) {
		super(uiParams, new BmoPolicyPayment(), id);
		bmoPolicyPayment = (BmoPolicyPayment)getBmObject();
	}
	
	@Override
	public void populateFields() {
		bmoPolicyPayment = (BmoPolicyPayment)getBmObject();
		
		formFlexTable.addField(1, 0, dueDateBox, bmoPolicyPayment.getDueDate());
		formFlexTable.addField(1, 2, amountTextBox, bmoPolicyPayment.getAmount());

		formFlexTable.addField(2, 0, commentsTextArea, bmoPolicyPayment.getComments());
		formFlexTable.addFieldEmpty(2, 2);
		
		formFlexTable.addField(3, 0, statusListBox, bmoPolicyPayment.getStatus());
		formFlexTable.addField(3, 2, payDateBox, bmoPolicyPayment.getPayDate());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoPolicyPayment.setId(id);
		
		policyId = bmoPolicyPayment.getBmoPolicy().getId();
		if (!(policyId > 0)) {
			forceFilter = getUiParams().getUiProgramParams(bmoPolicyPayment.getProgramCode()).getForceFilter();
			policyId = Integer.parseInt(forceFilter.getValue());
		}
		bmoPolicyPayment.getPolicyId().setValue(policyId);
		bmoPolicyPayment.getAmount().setValue(amountTextBox.getText());
		bmoPolicyPayment.getDueDate().setValue(dueDateBox.getTextBox().getText());
		bmoPolicyPayment.getPayDate().setValue(payDateBox.getTextBox().getText());		
		bmoPolicyPayment.getComments().setValue(commentsTextArea.getText());
		bmoPolicyPayment.getStatus().setValue(statusListBox.getSelectedCode());
		return bmoPolicyPayment;
	}
	
	@Override
	public void close() {
		UiPolicyPaymentList uiPolicyPaymentList = new UiPolicyPaymentList(getUiParams());
		uiPolicyPaymentList.show();
	}
}
