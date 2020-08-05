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

import com.flexwm.shared.op.BmoSupplierPhone;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiSupplierPhoneLabelList extends UiFormLabelList {

	BmoSupplierPhone bmoSupplierPhone;
	UiListBox typeListBox = new UiListBox(getUiParams());
	TextBox numberTextBox = new TextBox();
	TextBox extensionTextBox = new TextBox();
	TextBox faxTextBox = new TextBox();
	int supplierId;

	public UiSupplierPhoneLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoSupplierPhone());
		supplierId = id;
		bmoSupplierPhone = new BmoSupplierPhone();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoSupplierPhone.getKind(), 
				bmoSupplierPhone.getSupplierId().getName(), 
				bmoSupplierPhone.getSupplierId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoSupplierPhone.getSupplierId().getName());
	}

	@Override
	public void populateFields() {
		bmoSupplierPhone = (BmoSupplierPhone)getBmObject();
		formFlexTable.addField(1, 0, typeListBox, bmoSupplierPhone.getType());
		formFlexTable.addField(2, 0, numberTextBox, bmoSupplierPhone.getNumber());
		formFlexTable.addField(3, 0, extensionTextBox, bmoSupplierPhone.getExtension());
		formFlexTable.addField(4, 0, faxTextBox, bmoSupplierPhone.getFax());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoSupplierPhone = new BmoSupplierPhone();
		bmoSupplierPhone.setId(id);
		bmoSupplierPhone.getType().setValue(typeListBox.getSelectedCode());
		bmoSupplierPhone.getNumber().setValue(numberTextBox.getText());
		bmoSupplierPhone.getExtension().setValue(extensionTextBox.getText());
		bmoSupplierPhone.getSupplierId().setValue(supplierId);	
		bmoSupplierPhone.getFax().setValue(faxTextBox.getText());
		return bmoSupplierPhone;
	}
}
