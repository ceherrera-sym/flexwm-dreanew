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

import java.util.ArrayList;
import java.util.Iterator;
import com.flexwm.shared.in.BmoDiscount;
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoInsuranceDiscount;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiCheckBoxList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiInsuranceDiscountCheckBoxList extends UiCheckBoxList {
	BmoInsuranceDiscount bmoInsuranceDiscount;

	UiListBox userListBox;
	BmoInsurance bmoInsurance;

	public UiInsuranceDiscountCheckBoxList(UiParams uiParams, Panel defaultPanel, BmoInsurance bmoInsurance) {
		super(uiParams, defaultPanel, new BmoDiscount(), new BmoInsuranceDiscount());
		bmoInsuranceDiscount = (BmoInsuranceDiscount)getBmObject();
		this.bmoInsurance = bmoInsurance;

		// Lista solo usuarios del grupo seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoInsuranceDiscount.getKind(), 
				bmoInsuranceDiscount.getInsuranceId().getName(), 
				bmoInsuranceDiscount.getInsuranceId().getLabel(), 
				BmFilter.EQUALS, 
				bmoInsurance.getId(),
				bmoInsuranceDiscount.getInsuranceId().getName());

		colCount = 1;
		colDisplay = 3;
	}

	@Override
	public void populateBmObject(boolean check, BmObject listBmObject) throws BmException {
		BmoDiscount bmoDiscount = (BmoDiscount)listBmObject;
		bmoInsuranceDiscount = new BmoInsuranceDiscount();
		bmoInsuranceDiscount.getInsuranceId().setValue(bmoInsurance.getId());
		bmoInsuranceDiscount.getDiscountId().setValue(bmoDiscount.getId());

		// Crear registro
		if (check) {
			save(bmoInsuranceDiscount);
		} else {
			// Eliminar registro
			deleteBy(bmoInsuranceDiscount, bmoInsuranceDiscount.getInsuranceId(), bmoInsuranceDiscount.getDiscountId());
		}	
	}

	@Override
	public void prepareCheckBoxHashMap(ArrayList<BmObject> checkBoxList) {
		Iterator<BmObject> iterator = checkBoxList.iterator();
		while (iterator.hasNext()) {
			bmoInsuranceDiscount = (BmoInsuranceDiscount)iterator.next();
			checkBoxHashMap.put(bmoInsuranceDiscount.getDiscountId().toString(), bmoInsuranceDiscount.getInsuranceId().toString());
		}
	}

	@Override
	public boolean isChecked(BmObject bmObject) {
		BmoDiscount bmoDiscount = (BmoDiscount)bmObject;
		if (checkBoxHashMap.get(bmoDiscount.getIdField().toString()) == null) return false;
		else return true;
	}

}
