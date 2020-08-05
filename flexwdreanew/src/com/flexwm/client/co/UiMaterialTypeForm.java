/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;


import com.flexwm.shared.co.BmoMaterialType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;

/**
 * @author jhernandez
 *
 */

public class UiMaterialTypeForm extends UiForm{
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	


	BmoMaterialType bmoMaterialType;
	
	public UiMaterialTypeForm(UiParams uiParams, int id) {
		super(uiParams, new BmoMaterialType(), id); 
	}
	
	@Override
	public void populateFields(){
		bmoMaterialType = (BmoMaterialType)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoMaterialType.getCode());		 
		formFlexTable.addField(1, 2, nameTextBox, bmoMaterialType.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoMaterialType.getDescription());
		
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoMaterialType.setId(id);
		bmoMaterialType.getCode().setValue(codeTextBox.getText());		
		bmoMaterialType.getName().setValue(nameTextBox.getText());
		bmoMaterialType.getDescription().setValue(descriptionTextArea.getText());
		return bmoMaterialType;
	}
	
	@Override
	public void close() {
		UiMaterialTypeList uiMaterialTypeList = new UiMaterialTypeList(getUiParams());
		uiMaterialTypeList.show();
	}
}


