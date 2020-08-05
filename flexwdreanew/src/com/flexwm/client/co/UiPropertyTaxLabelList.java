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

import com.flexwm.shared.co.BmoPropertyTax;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;

/**
 * @author pguerra
 *
 */

public class UiPropertyTaxLabelList extends UiFormLabelList {
	UiListBox typeListBox = new UiListBox(getUiParams());
	BmoPropertyTax bmoPropertyTax;
	
	TextBox accountNoTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	UiListBox statusListBox = new UiListBox(getUiParams());
	int propertyId;
	

	public UiPropertyTaxLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoPropertyTax());
		propertyId = id;
		bmoPropertyTax = new BmoPropertyTax();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoPropertyTax.getKind(), 
				bmoPropertyTax.getPropertyId().getName(), 
				bmoPropertyTax.getPropertyId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoPropertyTax.getPropertyId().getName());
	}
	
	@Override
	public void populateFields(){
		bmoPropertyTax = (BmoPropertyTax)getBmObject();
		formFlexTable.addField(1, 0, accountNoTextBox, bmoPropertyTax.getAccountNo());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoPropertyTax.getDescription());
		formFlexTable.addField(3, 0, statusListBox, bmoPropertyTax.getStatus());
		
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoPropertyTax = new BmoPropertyTax();
		bmoPropertyTax.setId(id);
		bmoPropertyTax.getAccountNo().setValue(accountNoTextBox.getText());
		bmoPropertyTax.getDescription().setValue(descriptionTextArea.getText());
		bmoPropertyTax.getPropertyId().setValue(propertyId);
		bmoPropertyTax.getStatus().setValue(statusListBox.getSelectedValue());
			
		return bmoPropertyTax;
	}
}
