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

import com.flexwm.shared.in.BmoPolicyStatus;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiPolicyStatusForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	BmoPolicyStatus bmoPolicyStatus;
	
	public UiPolicyStatusForm(UiParams uiParams, int id) {
		super(uiParams, new BmoPolicyStatus(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoPolicyStatus = (BmoPolicyStatus)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoPolicyStatus.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoPolicyStatus.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoPolicyStatus.getDescription());
		formFlexTable.addFieldEmpty(2,2);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoPolicyStatus.setId(id);
		bmoPolicyStatus.getCode().setValue(codeTextBox.getText());
		bmoPolicyStatus.getName().setValue(nameTextBox.getText());
		bmoPolicyStatus.getDescription().setValue(descriptionTextArea.getText());
		return bmoPolicyStatus;
	}
	
	@Override
	public void close() {
		UiPolicyStatusList uiPolicyStatusList = new UiPolicyStatusList(getUiParams());
		uiPolicyStatusList.show();
	}
}
