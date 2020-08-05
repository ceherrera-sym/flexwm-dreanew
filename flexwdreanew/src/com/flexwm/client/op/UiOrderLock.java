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

import com.flexwm.shared.op.BmoOrderLock;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoWhSection;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiOrderLock extends UiFormList {	

	BmoOrderLock bmoOrderLock;	
	UiListBox typeListBox = new UiListBox(getUiParams());
	UiSuggestBox requisitionSuggestBox = new UiSuggestBox(new BmoRequisition());
	UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
	UiListBox whSectionListBox = new UiListBox(getUiParams(), new BmoWhSection());
	TextBox quantityTextBox = new TextBox();
	int orderId;


	public UiOrderLock(UiParams uiParams, Panel defaultPanel, int id) {
		super(uiParams, defaultPanel, new BmoOrderLock());
		orderId = id;
		bmoOrderLock = new BmoOrderLock();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoOrderLock.getKind(), 
				bmoOrderLock.getOrderId().getName(), 
				bmoOrderLock.getOrderId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoOrderLock.getOrderId().getName());
	}

	@Override
	public void populateFields() {
		bmoOrderLock = (BmoOrderLock)getBmObject();
		formFlexTable.addField(1, 0, typeListBox, bmoOrderLock.getType());
		formFlexTable.addField(1, 2, productSuggestBox, bmoOrderLock.getProductId());

		formFlexTable.addField(2, 0, requisitionSuggestBox, bmoOrderLock.getRequisitionId());
		formFlexTable.addField(2, 2, whSectionListBox, bmoOrderLock.getWhSectionId());

		formFlexTable.addField(3, 0, quantityTextBox, bmoOrderLock.getQuantity());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoOrderLock = new BmoOrderLock();
		bmoOrderLock.setId(id);
		bmoOrderLock.getOrderId().setValue(orderId);
		bmoOrderLock.getType().setValue(typeListBox.getSelectedCode());
		bmoOrderLock.getProductId().setValue(productSuggestBox.getSelectedId());		
		bmoOrderLock.getRequisitionId().setValue(requisitionSuggestBox.getSelectedId());	
		bmoOrderLock.getWhSectionId().setValue(whSectionListBox.getSelectedId());
		bmoOrderLock.getQuantity().setValue(quantityTextBox.getText());	
		return bmoOrderLock;
	}
}
