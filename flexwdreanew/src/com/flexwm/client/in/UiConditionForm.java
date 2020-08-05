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

import com.flexwm.shared.in.BmoCondition;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiConditionForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextBox valueTextBox = new TextBox();
	BmoCondition bmoCondition;
	
	public UiConditionForm(UiParams uiParams, int id) {
		super(uiParams, new BmoCondition(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoCondition = (BmoCondition)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoCondition.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoCondition.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoCondition.getDescription());
		formFlexTable.addField(2, 2, valueTextBox, bmoCondition.getValue());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoCondition.setId(id);
		bmoCondition.getCode().setValue(codeTextBox.getText());
		bmoCondition.getName().setValue(nameTextBox.getText());
		bmoCondition.getDescription().setValue(descriptionTextArea.getText());
		bmoCondition.getValue().setValue(valueTextBox.getText());
		return bmoCondition;
	}
	
	@Override
	public void close() {
		UiConditionList uiConditionList = new UiConditionList(getUiParams());
		uiConditionList.show();
	}
}
