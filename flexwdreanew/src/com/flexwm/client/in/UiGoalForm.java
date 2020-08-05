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

import com.flexwm.shared.in.BmoGoal;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiGoalForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	BmoGoal bmoGoal;
	
	public UiGoalForm(UiParams uiParams, int id) {
		super(uiParams, new BmoGoal(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoGoal = (BmoGoal)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoGoal.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoGoal.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoGoal.getDescription());
		formFlexTable.addFieldEmpty(2,2);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoGoal.setId(id);
		bmoGoal.getCode().setValue(codeTextBox.getText());
		bmoGoal.getName().setValue(nameTextBox.getText());
		bmoGoal.getDescription().setValue(descriptionTextArea.getText());
		return bmoGoal;
	}
	
	@Override
	public void close() {
		UiGoalList uiGoalList = new UiGoalList(getUiParams());
		uiGoalList.show();
	}
}
