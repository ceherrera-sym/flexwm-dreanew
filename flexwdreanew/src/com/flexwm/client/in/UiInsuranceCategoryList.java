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


import com.flexwm.shared.in.BmoInsuranceCategory;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;

public class UiInsuranceCategoryList extends UiList {

	public UiInsuranceCategoryList(UiParams uiParams) {
		super(uiParams, new BmoInsuranceCategory());
	}
	
	public UiInsuranceCategoryList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoInsuranceCategory());
	}
	
	@Override
	public void create() {
		UiInsuranceCategoryForm uiInsuranceCategoryForm = new UiInsuranceCategoryForm(getUiParams(), 0);
		uiInsuranceCategoryForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiInsuranceCategoryForm uiInsuranceCategoryForm = new UiInsuranceCategoryForm(getUiParams(), bmObject.getId());
		uiInsuranceCategoryForm.show();
	}
}


