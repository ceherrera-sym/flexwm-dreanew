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
import com.flexwm.shared.co.BmoContractProperty;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoWorkContract;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;

/**
 * @author smuniz
 *
 */
public class UiContractPropertyList extends UiList {
	BmoContractProperty bmoContractProperty;

	public UiContractPropertyList(UiParams uiParams) {
		super(uiParams, new BmoContractProperty());
		bmoContractProperty = (BmoContractProperty)getBmObject();
	}
	
	@Override
	public void postShow(){
		addFilterListBox(new UiListBox(getUiParams(), new BmoWorkContract()), bmoContractProperty.getBmoWorkContract());
		addFilterListBox(new UiListBox(getUiParams(), new BmoProperty()), bmoContractProperty.getBmoProperty());
	}
	
	@Override
	public void create() {
		UiContractPropertyForm uiContractPropertyForm = new UiContractPropertyForm(getUiParams(), 0);
		uiContractPropertyForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiContractPropertyForm uiContractPropertyForm = new UiContractPropertyForm(getUiParams(), bmObject.getId());
		uiContractPropertyForm.show();
	}
}
