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
import com.flexwm.shared.in.BmoCoverage;
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoInsuranceCoverage;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiCheckBoxList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiInsuranceCoverageCheckBoxList extends UiCheckBoxList {
	BmoInsuranceCoverage bmoInsuranceCoverage;

	UiListBox userListBox;
	BmoInsurance bmoInsurance;

	public UiInsuranceCoverageCheckBoxList(UiParams uiParams, Panel defaultPanel, BmoInsurance bmoInsurance) {
		super(uiParams, defaultPanel, new BmoCoverage(), new BmoInsuranceCoverage());
		bmoInsuranceCoverage = (BmoInsuranceCoverage)getBmObject();
		this.bmoInsurance = bmoInsurance;

		// Lista solo usuarios del grupo seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoInsuranceCoverage.getKind(), 
				bmoInsuranceCoverage.getInsuranceId().getName(), 
				bmoInsuranceCoverage.getInsuranceId().getLabel(), 
				BmFilter.EQUALS, 
				bmoInsurance.getId(),
				bmoInsuranceCoverage.getInsuranceId().getName());
	}

	@Override
	public void populateBmObject(boolean check, BmObject listBmObject) throws BmException {
		BmoCoverage bmoCoverage = (BmoCoverage)listBmObject;
		bmoInsuranceCoverage = new BmoInsuranceCoverage();
		bmoInsuranceCoverage.getInsuranceId().setValue(bmoInsurance.getId());
		bmoInsuranceCoverage.getCoverageId().setValue(bmoCoverage.getId());

		// Crear registro
		if (check) {
			save(bmoInsuranceCoverage);
		} else {
			// Eliminar registro
			deleteBy(bmoInsuranceCoverage, bmoInsuranceCoverage.getInsuranceId(), bmoInsuranceCoverage.getCoverageId());
		}	
	}

	@Override
	public void prepareCheckBoxHashMap(ArrayList<BmObject> checkBoxList) {
		Iterator<BmObject> iterator = checkBoxList.iterator();
		while (iterator.hasNext()) {
			bmoInsuranceCoverage = (BmoInsuranceCoverage)iterator.next();
			checkBoxHashMap.put(bmoInsuranceCoverage.getCoverageId().toString(), bmoInsuranceCoverage.getInsuranceId().toString());
		}
	}

	@Override
	public boolean isChecked(BmObject bmObject) {
		BmoCoverage bmoCoverage = (BmoCoverage)bmObject;
		if (checkBoxHashMap.get(bmoCoverage.getIdField().toString()) == null) return false;
		else return true;
	}

}
