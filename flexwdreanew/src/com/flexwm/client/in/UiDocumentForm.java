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

import com.flexwm.shared.in.BmoDocument;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiDocumentForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	BmoDocument bmoDocument;
	
	public UiDocumentForm(UiParams uiParams, int id) {
		super(uiParams, new BmoDocument(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoDocument = (BmoDocument)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoDocument.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoDocument.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoDocument.getDescription());
		formFlexTable.addFieldEmpty(2,2);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoDocument.setId(id);
		bmoDocument.getCode().setValue(codeTextBox.getText());
		bmoDocument.getName().setValue(nameTextBox.getText());
		bmoDocument.getDescription().setValue(descriptionTextArea.getText());
		return bmoDocument;
	}
	
	@Override
	public void close() {
		UiDocumentList uiDocumentList = new UiDocumentList(getUiParams());
		uiDocumentList.show();
	}
}
