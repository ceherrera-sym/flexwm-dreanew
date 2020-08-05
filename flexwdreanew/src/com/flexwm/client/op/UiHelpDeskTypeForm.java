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

import com.flexwm.shared.op.BmoHelpDeskType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.sf.BmoUser;

/**
 * @author smuniz
 *
 */

public class UiHelpDeskTypeForm extends UiForm{
	UiSuggestBox userIdUiSuggestBox = new UiSuggestBox(new BmoUser());
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();

	BmoHelpDeskType bmoHelpDeskType;
	
	public UiHelpDeskTypeForm(UiParams uiParams, int id) {
		super(uiParams, new BmoHelpDeskType(), id); 
	}
	
	@Override
	public void populateFields(){
		bmoHelpDeskType = (BmoHelpDeskType)getBmObject();
		formFlexTable.addField(1, 0, nameTextBox, bmoHelpDeskType.getName());
		formFlexTable.addField(1, 2, userIdUiSuggestBox, bmoHelpDeskType.getUserId());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoHelpDeskType.getDescription());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoHelpDeskType.setId(id);
		bmoHelpDeskType.getName().setValue(nameTextBox.getText());
		bmoHelpDeskType.getUserId().setValue(userIdUiSuggestBox.getSelectedId()); 
		bmoHelpDeskType.getDescription().setValue(descriptionTextArea.getText());
		
		return bmoHelpDeskType;
	}
	
	@Override
	public void close() {
		UiHelpDeskTypeList uiHelpDeskTypeList = new UiHelpDeskTypeList(getUiParams());
		uiHelpDeskTypeList.show();
	}
}


