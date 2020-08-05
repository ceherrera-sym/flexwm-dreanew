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

import com.flexwm.shared.op.BmoSupplierEmail;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiSupplierEmailLabeList extends UiFormLabelList {

	BmoSupplierEmail bmoSupplierEmail;
	UiListBox typeListBox = new UiListBox(getUiParams());
	TextBox emailTextBox = new TextBox();
	int supplierId;

	public UiSupplierEmailLabeList(UiParams uiParams, int id) {
		super(uiParams, new BmoSupplierEmail());
		supplierId = id;
		bmoSupplierEmail = new BmoSupplierEmail();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoSupplierEmail.getKind(), 
				bmoSupplierEmail.getSupplierId().getName(), 
				bmoSupplierEmail.getSupplierId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoSupplierEmail.getSupplierId().getName());
	}

	@Override
	public void populateFields() {
		bmoSupplierEmail = (BmoSupplierEmail)getBmObject();
		formFlexTable.addField(1, 0, typeListBox, bmoSupplierEmail.getType());
		formFlexTable.addField(2, 0, emailTextBox, bmoSupplierEmail.getEmail());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoSupplierEmail = new BmoSupplierEmail();
		bmoSupplierEmail.setId(id);
		bmoSupplierEmail.getType().setValue(typeListBox.getSelectedCode());
		bmoSupplierEmail.getEmail().setValue(emailTextBox.getText());
		bmoSupplierEmail.getSupplierId().setValue(supplierId);		
		return bmoSupplierEmail;
	}
}
