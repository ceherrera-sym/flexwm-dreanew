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
import com.flexwm.shared.cr.BmoUserCreditLimit;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;


/**
 * @author jhernandez
 *
 */

public class UiUserCreditLimitList extends UiList {
	BmoUserCreditLimit bmoUserCreditLimit;

	public UiUserCreditLimitList(UiParams uiParams) {
		super(uiParams, new BmoUserCreditLimit());
		bmoUserCreditLimit = (BmoUserCreditLimit)getBmObject();
	}
	
	@Override
	public void postShow(){
		//addFilterListBox(new UiListBox(getUiParams(), new bmoUserCreditLimitGroup()), bmoUserCreditLimit.getbmoUserCreditLimitGroup());
	}
	
	@Override
	public void create() {
		UiUserCreditLimitForm uiTermForm = new UiUserCreditLimitForm(getUiParams(), 0);
		uiTermForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiUserCreditLimitForm uiTermForm = new UiUserCreditLimitForm(getUiParams(), bmObject.getId());
		uiTermForm.show();
	}
}
