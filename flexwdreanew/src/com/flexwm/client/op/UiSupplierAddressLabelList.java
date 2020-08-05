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

import com.flexwm.shared.op.BmoSupplierAddress;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCity;


public class UiSupplierAddressLabelList extends UiFormLabelList {

	BmoSupplierAddress bmoSupplierAddress;
	UiListBox typeListBox = new UiListBox(getUiParams());
	TextBox streetTextBox = new TextBox();
	TextBox numberTextBox = new TextBox();
	TextBox neighborhoodTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextBox zipTextBox = new TextBox();
	UiSuggestBox citySuggestBox = new UiSuggestBox(new BmoCity());

	int supplierId;

	public UiSupplierAddressLabelList(UiParams uiParams, int id) {
		super(uiParams,  new BmoSupplierAddress());
		supplierId = id;
		bmoSupplierAddress = new BmoSupplierAddress();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoSupplierAddress.getKind(), 
				bmoSupplierAddress.getSupplierId().getName(), 
				bmoSupplierAddress.getSupplierId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoSupplierAddress.getSupplierId().getName());
	}

	@Override
	public void populateFields() {
		bmoSupplierAddress = (BmoSupplierAddress)getBmObject();
		formFlexTable.addField(1, 0, typeListBox, bmoSupplierAddress.getType());
		formFlexTable.addField(2, 0, streetTextBox, bmoSupplierAddress.getStreet());
		formFlexTable.addField(3, 0, numberTextBox, bmoSupplierAddress.getNumber());
		formFlexTable.addField(4, 0, neighborhoodTextBox, bmoSupplierAddress.getNeighborhood());
		formFlexTable.addField(5, 0, zipTextBox, bmoSupplierAddress.getZip());
		formFlexTable.addField(6, 0, descriptionTextArea, bmoSupplierAddress.getDescription());
		formFlexTable.addField(7, 0, citySuggestBox, bmoSupplierAddress.getCityId());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoSupplierAddress = new BmoSupplierAddress();
		bmoSupplierAddress.setId(id);
		bmoSupplierAddress.getType().setValue(typeListBox.getSelectedCode());
		bmoSupplierAddress.getStreet().setValue(streetTextBox.getText());
		bmoSupplierAddress.getNumber().setValue(numberTextBox.getText());
		bmoSupplierAddress.getNeighborhood().setValue(neighborhoodTextBox.getText());
		bmoSupplierAddress.getZip().setValue(zipTextBox.getText());
		bmoSupplierAddress.getDescription().setValue(descriptionTextArea.getText());
		bmoSupplierAddress.getCityId().setValue(citySuggestBox.getSelectedId());
		bmoSupplierAddress.getSupplierId().setValue(supplierId);		
		return bmoSupplierAddress;
	}
}
