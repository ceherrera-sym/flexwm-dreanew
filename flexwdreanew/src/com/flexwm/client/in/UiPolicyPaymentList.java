/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.in;


import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.in.BmoPolicyPayment;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiPolicyPaymentList extends UiList {

	public UiPolicyPaymentList(UiParams uiParams) {
		super(uiParams, new BmoPolicyPayment());
	}
	
	public UiPolicyPaymentList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoPolicyPayment());
	}
	
	@Override
	public void create() {
		UiPolicyPaymentForm uiPolicyPaymentForm = new UiPolicyPaymentForm(getUiParams(), 0);
		uiPolicyPaymentForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		BmoPolicyPayment bmoPolicyPayment = (BmoPolicyPayment)bmObject;
		if (getUiParams().getUiType(new BmoOpportunity().getProgramCode()) == UiParams.MINIMALIST) {
			UiPolicyDetail uiPolicyDetail = new UiPolicyDetail(getUiParams(), bmoPolicyPayment.getBmoPolicy().getId());
			uiPolicyDetail.show();
		} else {
			UiPolicyPaymentForm uiPolicyPaymentForm = new UiPolicyPaymentForm(getUiParams(), bmObject.getId());
			uiPolicyPaymentForm.show();
		}
	}
}


