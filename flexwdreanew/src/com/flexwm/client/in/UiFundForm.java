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

import com.flexwm.shared.in.BmoFund;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiFundForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	BmoFund bmoFund;
	
	public UiFundForm(UiParams uiParams, int id) {
		super(uiParams, new BmoFund(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoFund = (BmoFund)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoFund.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoFund.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoFund.getDescription());
		formFlexTable.addFieldEmpty(2,2);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoFund.setId(id);
		bmoFund.getCode().setValue(codeTextBox.getText());
		bmoFund.getName().setValue(nameTextBox.getText());
		bmoFund.getDescription().setValue(descriptionTextArea.getText());
		return bmoFund;
	}
	
	@Override
	public void close() {
		UiFundList uiFundList = new UiFundList(getUiParams());
		uiFundList.show();
	}
}
