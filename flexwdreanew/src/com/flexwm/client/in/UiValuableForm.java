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

import com.flexwm.shared.in.BmoValuable;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiValuableForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	BmoValuable bmoValuable;
	
	public UiValuableForm(UiParams uiParams, int id) {
		super(uiParams, new BmoValuable(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoValuable = (BmoValuable)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoValuable.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoValuable.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoValuable.getDescription());
		formFlexTable.addFieldEmpty(2,2);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoValuable.setId(id);
		bmoValuable.getCode().setValue(codeTextBox.getText());
		bmoValuable.getName().setValue(nameTextBox.getText());
		bmoValuable.getDescription().setValue(descriptionTextArea.getText());
		return bmoValuable;
	}
	
	@Override
	public void close() {
		UiValuableList uiValuableList = new UiValuableList(getUiParams());
		uiValuableList.show();
	}
}
