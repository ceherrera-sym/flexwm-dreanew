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


import com.flexwm.shared.cm.BmoCustomerWeb;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiCustomerWebLabelList extends UiFormLabelList {
	BmoCustomerWeb bmoCustomerWeb;

	TextBox webSiteTextBox = new TextBox();	
	int customerId;


	public UiCustomerWebLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerWeb());
		customerId = id;
		bmoCustomerWeb = new BmoCustomerWeb();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerWeb.getKind(), 
				bmoCustomerWeb.getCustomerId().getName(), 
				bmoCustomerWeb.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerWeb.getCustomerId().getName());
	}

	@Override
	public void populateFields(){
		bmoCustomerWeb = (BmoCustomerWeb)getBmObject();	
		formFlexTable.addField(1, 0, webSiteTextBox, bmoCustomerWeb.getWebsite());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerWeb = new BmoCustomerWeb();
		bmoCustomerWeb.setId(id);		
		bmoCustomerWeb.getWebsite().setValue(webSiteTextBox.getText());
		bmoCustomerWeb.getCustomerId().setValue(customerId);		
		return bmoCustomerWeb;
	}
}
