/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoCustomerAddress;
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


public class UiCustomerAddressLabelList extends UiFormLabelList {
	UiListBox typeListBox = new UiListBox(getUiParams());
	BmoCustomerAddress bmoCustomerAddress;
	
	TextBox streetTextBox = new TextBox();
	TextBox numberTextBox = new TextBox();
	TextBox neighborhoodTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextBox zipTextBox = new TextBox();
	UiSuggestBox citySuggestBox = new UiSuggestBox(new BmoCity());
	int customerId;
	

	public UiCustomerAddressLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerAddress());
		customerId = id;
		bmoCustomerAddress = new BmoCustomerAddress();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerAddress.getKind(), 
				bmoCustomerAddress.getCustomerId().getName(), 
				bmoCustomerAddress.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerAddress.getCustomerId().getName());
	}
	
	@Override
	public void populateFields(){
		bmoCustomerAddress = (BmoCustomerAddress)getBmObject();
		formFlexTable.addField(1, 0, typeListBox, bmoCustomerAddress.getType());
		formFlexTable.addField(2, 0, streetTextBox, bmoCustomerAddress.getStreet());
		formFlexTable.addField(3, 0, numberTextBox, bmoCustomerAddress.getNumber());
		formFlexTable.addField(4, 0, neighborhoodTextBox, bmoCustomerAddress.getNeighborhood());
		formFlexTable.addField(5, 0, zipTextBox, bmoCustomerAddress.getZip());
		formFlexTable.addField(6, 0, descriptionTextArea, bmoCustomerAddress.getDescription());
		formFlexTable.addField(7, 0, citySuggestBox, bmoCustomerAddress.getCityId());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerAddress = new BmoCustomerAddress();
		bmoCustomerAddress.setId(id);
		bmoCustomerAddress.getType().setValue(typeListBox.getSelectedCode());
		bmoCustomerAddress.getStreet().setValue(streetTextBox.getText());
		bmoCustomerAddress.getNumber().setValue(numberTextBox.getText());
		bmoCustomerAddress.getNeighborhood().setValue(neighborhoodTextBox.getText());
		bmoCustomerAddress.getZip().setValue(zipTextBox.getText());
		bmoCustomerAddress.getDescription().setValue(descriptionTextArea.getText());
		bmoCustomerAddress.getCityId().setValue(citySuggestBox.getSelectedId());
		bmoCustomerAddress.getCustomerId().setValue(customerId);		
		return bmoCustomerAddress;
	}
}
