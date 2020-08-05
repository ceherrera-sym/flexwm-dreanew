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

import com.symgae.shared.BmObject;
import com.flexwm.shared.co.BmoMaterialType;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;

/**
 * @author smuniz
 *
 */

public class UiMaterialTypeList extends UiList {
	BmoMaterialType bmoMaterialType;

	public UiMaterialTypeList(UiParams uiParams) {
		super(uiParams, new BmoMaterialType());
		bmoMaterialType = (BmoMaterialType)getBmObject();
	}
	
	@Override
	public void create() {
		UiMaterialTypeForm uiMaterialTypeForm = new UiMaterialTypeForm(getUiParams(), 0);
		uiMaterialTypeForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiMaterialTypeForm uiMaterialTypeForm = new UiMaterialTypeForm(getUiParams(), bmObject.getId());
		uiMaterialTypeForm.show();
	}
}
