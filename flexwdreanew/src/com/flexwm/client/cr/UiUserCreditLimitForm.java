/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */

package com.flexwm.client.cr;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cr.BmoUserCreditLimit;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;

/**
 * @author jhernandez
 *
 */

public class UiUserCreditLimitForm extends UiForm{
	
	
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());	
	TextBox creditLimitTextBox = new TextBox();
	
	
	BmoUserCreditLimit bmoUserCreditLimit;
	
	public UiUserCreditLimitForm(UiParams uiParams, int id) {
		super(uiParams, new BmoUserCreditLimit(), id); 
	}
	
	@Override
	public void populateFields(){
		bmoUserCreditLimit = (BmoUserCreditLimit)getBmObject();
		
		// Filtrar por vendedores activos
		BmoUser bmoUser = new BmoUser();
		BmFilter filterSalesmenActive = new BmFilter();
		filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter filterSalesman = new BmFilter();
		int instructorGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
		filterSalesman.setInFilter(bmoProfileUser.getKind(), 
				bmoUser.getIdFieldName(),
				bmoProfileUser.getUserId().getName(),
				bmoProfileUser.getProfileId().getName(),
				"" + instructorGroupId);	
		userSuggestBox.addFilter(filterSalesman);
		
		userSuggestBox.addFilter(filterSalesmenActive);
		formFlexTable.addField(1, 0, userSuggestBox, bmoUserCreditLimit.getUserId());
		formFlexTable.addField(1, 2, creditLimitTextBox, bmoUserCreditLimit.getCreditLimit());
				
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoUserCreditLimit.setId(id);
		bmoUserCreditLimit.getUserId().setValue(userSuggestBox.getSelectedId());
		bmoUserCreditLimit.getCreditLimit().setValue(creditLimitTextBox.getText());
		
		return bmoUserCreditLimit;
	}
	
	@Override
	public void close() {
		UiUserCreditLimitList uiTermList = new UiUserCreditLimitList(getUiParams());
		uiTermList.show();
	}
}


