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

import com.flexwm.shared.cm.BmoCustomerSocial;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoSocial;


public class UiCustomerSocialLabelList extends UiFormLabelList {
	UiListBox socialListBox = new UiListBox(getUiParams(), new BmoSocial());
	BmoCustomerSocial bmoCustomerSocial;
	
	TextBox accountTextBox = new TextBox();
	int customerId;
	

	public UiCustomerSocialLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerSocial());
		customerId = id;
		bmoCustomerSocial = new BmoCustomerSocial();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerSocial.getKind(), 
				bmoCustomerSocial.getCustomerId().getName(), 
				bmoCustomerSocial.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerSocial.getCustomerId().getName());
	}
	
	@Override
	public void populateFields() {
		bmoCustomerSocial = (BmoCustomerSocial)getBmObject();
		formFlexTable.addField(1, 0, socialListBox, bmoCustomerSocial.getSocialId());
		formFlexTable.addField(2, 0, accountTextBox, bmoCustomerSocial.getAccount());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerSocial = new BmoCustomerSocial();
		bmoCustomerSocial.setId(id);
		bmoCustomerSocial.getSocialId().setValue(socialListBox.getSelectedId());
		bmoCustomerSocial.getAccount().setValue(accountTextBox.getText());
		bmoCustomerSocial.getCustomerId().setValue(customerId);		
		return bmoCustomerSocial;
	}
}
