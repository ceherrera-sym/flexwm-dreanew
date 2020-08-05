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
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoInsuranceValuable;
import com.flexwm.shared.in.BmoValuable;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiCheckBoxList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiInsuranceValuableCheckBoxList extends UiCheckBoxList {
	BmoInsuranceValuable bmoInsuranceValuable;

	UiListBox userListBox;
	BmoInsurance bmoInsurance;

	public UiInsuranceValuableCheckBoxList(UiParams uiParams, Panel defaultPanel, BmoInsurance bmoInsurance) {
		super(uiParams, defaultPanel, new BmoValuable(), new BmoInsuranceValuable());
		bmoInsuranceValuable = (BmoInsuranceValuable)getBmObject();
		this.bmoInsurance = bmoInsurance;

		// Lista solo usuarios del grupo seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoInsuranceValuable.getKind(), 
				bmoInsuranceValuable.getInsuranceId().getName(), 
				bmoInsuranceValuable.getInsuranceId().getLabel(), 
				BmFilter.EQUALS, 
				bmoInsurance.getId(),
				bmoInsuranceValuable.getInsuranceId().getName());

		colCount = 1;
		colDisplay = 3;
	}

	@Override
	public void populateBmObject(boolean check, BmObject listBmObject) throws BmException {
		BmoValuable bmoValuable = (BmoValuable)listBmObject;
		bmoInsuranceValuable = new BmoInsuranceValuable();
		bmoInsuranceValuable.getInsuranceId().setValue(bmoInsurance.getId());
		bmoInsuranceValuable.getValuableId().setValue(bmoValuable.getId());

		// Crear registro
		if (check) {
			save(bmoInsuranceValuable);
		} else {
			// Eliminar registro
			deleteBy(bmoInsuranceValuable, bmoInsuranceValuable.getInsuranceId(), bmoInsuranceValuable.getValuableId());
		}	
	}

	@Override
	public void prepareCheckBoxHashMap(ArrayList<BmObject> checkBoxList) {
		Iterator<BmObject> iterator = checkBoxList.iterator();
		while (iterator.hasNext()) {
			bmoInsuranceValuable = (BmoInsuranceValuable)iterator.next();
			checkBoxHashMap.put(bmoInsuranceValuable.getValuableId().toString(), bmoInsuranceValuable.getInsuranceId().toString());
		}
	}

	@Override
	public boolean isChecked(BmObject bmObject) {
		BmoValuable bmoValuable = (BmoValuable)bmObject;
		if (checkBoxHashMap.get(bmoValuable.getIdField().toString()) == null) return false;
		else return true;
	}

}
