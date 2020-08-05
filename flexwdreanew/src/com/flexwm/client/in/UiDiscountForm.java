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

import com.flexwm.shared.in.BmoDiscount;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiDiscountForm extends UiForm {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	BmoDiscount bmoDiscount;
	
	public UiDiscountForm(UiParams uiParams, int id) {
		super(uiParams, new BmoDiscount(), id);		
	}
	
	@Override
	public void populateFields(){
		bmoDiscount = (BmoDiscount)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoDiscount.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoDiscount.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoDiscount.getDescription());
		formFlexTable.addFieldEmpty(2,2);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoDiscount.setId(id);
		bmoDiscount.getCode().setValue(codeTextBox.getText());
		bmoDiscount.getName().setValue(nameTextBox.getText());
		bmoDiscount.getDescription().setValue(descriptionTextArea.getText());
		return bmoDiscount;
	}
	
	@Override
	public void close() {
		UiDiscountList uiDiscountList = new UiDiscountList(getUiParams());
		uiDiscountList.show();
	}
}
