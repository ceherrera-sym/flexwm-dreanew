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
import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoUnitPriceEquipment;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;

/**
 * @author smuniz
 *
 */
public class UiUnitPriceEquipmentList extends UiList {
	BmoUnitPriceEquipment bmoUnitPriceEquipement;

	public UiUnitPriceEquipmentList(UiParams uiParams) {
		super(uiParams, new BmoUnitPriceEquipment());
		bmoUnitPriceEquipement = (BmoUnitPriceEquipment)getBmObject();
	}
	
	@Override
	public void postShow(){
		addFilterListBox(new UiListBox(getUiParams(), new BmoUnitPrice()), bmoUnitPriceEquipement.getBmoUnitPrice());
	}
	
	@Override
	public void create() {
		UiUnitPriceEquipmentForm uiUnitPriceEquipementForm = new UiUnitPriceEquipmentForm(getUiParams(), 0);
		uiUnitPriceEquipementForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiUnitPriceEquipmentForm uiUnitPriceEquipementForm = new UiUnitPriceEquipmentForm(getUiParams(), bmObject.getId());
		uiUnitPriceEquipementForm.show();
	}
}
