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

import com.flexwm.shared.in.BmoInsuranceCategory;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiInsuranceCategoryForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	BmoInsuranceCategory bmoInsuranceCategory;
	
	public UiInsuranceCategoryForm(UiParams uiParams, int id) {
		super(uiParams, new BmoInsuranceCategory(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoInsuranceCategory = (BmoInsuranceCategory)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoInsuranceCategory.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoInsuranceCategory.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoInsuranceCategory.getDescription());
		formFlexTable.addFieldEmpty(2,2);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoInsuranceCategory.setId(id);
		bmoInsuranceCategory.getCode().setValue(codeTextBox.getText());
		bmoInsuranceCategory.getName().setValue(nameTextBox.getText());
		bmoInsuranceCategory.getDescription().setValue(descriptionTextArea.getText());
		return bmoInsuranceCategory;
	}
	
	@Override
	public void close() {
		UiInsuranceCategoryList uiInsuranceCategoryList = new UiInsuranceCategoryList(getUiParams());
		uiInsuranceCategoryList.show();
	}
}
