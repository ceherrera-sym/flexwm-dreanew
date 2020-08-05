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
import com.flexwm.shared.in.BmoFund;
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoInsuranceFund;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiCheckBoxList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiInsuranceFundCheckBoxList extends UiCheckBoxList {
	BmoInsuranceFund bmoInsuranceFund;

	UiListBox userListBox;
	BmoInsurance bmoInsurance;

	public UiInsuranceFundCheckBoxList(UiParams uiParams, Panel defaultPanel, BmoInsurance bmoInsurance) {
		super(uiParams, defaultPanel, new BmoFund(), new BmoInsuranceFund());
		bmoInsuranceFund = (BmoInsuranceFund)getBmObject();
		this.bmoInsurance = bmoInsurance;

		// Lista solo usuarios del grupo seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoInsuranceFund.getKind(), 
				bmoInsuranceFund.getInsuranceId().getName(), 
				bmoInsuranceFund.getInsuranceId().getLabel(), 
				BmFilter.EQUALS, 
				bmoInsurance.getId(),
				bmoInsuranceFund.getInsuranceId().getName());

		colCount = 1;
		colDisplay = 3;
	}

	@Override
	public void populateBmObject(boolean check, BmObject listBmObject) throws BmException {
		BmoFund bmoFund = (BmoFund)listBmObject;
		bmoInsuranceFund = new BmoInsuranceFund();
		bmoInsuranceFund.getInsuranceId().setValue(bmoInsurance.getId());
		bmoInsuranceFund.getFundId().setValue(bmoFund.getId());

		// Crear registro
		if (check) {
			save(bmoInsuranceFund);
		} else {
			// Eliminar registro
			deleteBy(bmoInsuranceFund, bmoInsuranceFund.getInsuranceId(), bmoInsuranceFund.getFundId());
		}	
	}

	@Override
	public void prepareCheckBoxHashMap(ArrayList<BmObject> checkBoxList) {
		Iterator<BmObject> iterator = checkBoxList.iterator();
		while (iterator.hasNext()) {
			bmoInsuranceFund = (BmoInsuranceFund)iterator.next();
			checkBoxHashMap.put(bmoInsuranceFund.getFundId().toString(), bmoInsuranceFund.getInsuranceId().toString());
		}
	}

	@Override
	public boolean isChecked(BmObject bmObject) {
		BmoFund bmoFund = (BmoFund)bmObject;
		if (checkBoxHashMap.get(bmoFund.getIdField().toString()) == null) return false;
		else return true;
	}

}
