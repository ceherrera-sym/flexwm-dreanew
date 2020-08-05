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


import com.flexwm.shared.in.BmoInsurance;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiInsuranceList extends UiList {

	public UiInsuranceList(UiParams uiParams) {
		super(uiParams, new BmoInsurance());
	}
	
	public UiInsuranceList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoInsurance());
	}
	
	@Override
	public void create() {
		UiInsuranceForm uiInsuranceForm = new UiInsuranceForm(getUiParams(), 0);
		uiInsuranceForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiInsuranceForm uiInsuranceForm = new UiInsuranceForm(getUiParams(), bmObject.getId());
		uiInsuranceForm.show();
	}
}


