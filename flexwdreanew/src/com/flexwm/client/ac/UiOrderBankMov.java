/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ac;

import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BProgramService;
import com.symgae.shared.BProgramServiceAsync;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmObject;


public class UiOrderBankMov extends UiForm {
	UiListBox typeListBox = new UiListBox(getUiParams());
	BmoOrder bmoOrder;
	
	// Creacion de BmField
	DateBox dueDateBox = new DateBox();
	TextBox amountTextBox = new TextBox();
	
	BmField amountField;
	BmField dueDateField;		
	
	protected BProgramServiceAsync bObjectServiceAsync = GWT.create(BProgramService.class);
	UiSuggestBox foreignSuggestBox = new UiSuggestBox(new BmoRaccount());
	
	int orderId;

	public UiOrderBankMov(UiParams uiParams, Panel defaultPanel, int id) {
		super(uiParams, defaultPanel, new BmoOrder(), id);
		orderId = id;

	}
	
	@Override
	public void populateFields(){
		bmoOrder = (BmoOrder)getBmObject();		
	}
	
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoOrder = new BmoOrder();
		//bmoRaccount.setId(id);
		//bmoRaccount.getRaccountId().setValue(raccountId);
		//bmoRaccount.getForeignId().setValue(foreignSuggestBox.getSelectedId());
				
		return bmoOrder;
	}
	

}