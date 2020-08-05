/**
 * 
 */

package com.flexwm.client.co;

import java.util.ArrayList;
import java.util.Iterator;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoWorkContractProperty;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiCheckBoxList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


/**
 * @author jhernandez
 *
 */

public class UiWorkContractPropertiesCheckBox extends UiCheckBoxList {

	BmoWorkContractProperty bmoWorkContractProperty;

	BmoWorkContract bmoWorkContract;

	public UiWorkContractPropertiesCheckBox(UiParams uiParams, Panel defaultPanel, BmoWorkContract bmoWorkContract) {
		super(uiParams, defaultPanel, new BmoProperty(), new BmoWorkContractProperty());
		bmoWorkContractProperty = (BmoWorkContractProperty)getBmObject();
		this.bmoWorkContract = bmoWorkContract;

		// Lista solo usuarios del grupo seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoWorkContractProperty.getKind(), 
				bmoWorkContractProperty.getWorkContractId().getName(), 
				bmoWorkContractProperty.getWorkContractId().getLabel(), 
				BmFilter.EQUALS, 
				bmoWorkContract.getId(),
				bmoWorkContractProperty.getWorkContractId().getName());

		colDisplay = 5;
		if(isMobile())
			colCount = 1;
		else 
			colCount = 2;
	}

	@Override
	public void populateBmObject(boolean check, BmObject listBmObject) throws BmException {		
		BmoProperty bmoProperty = (BmoProperty)listBmObject;
		bmoWorkContractProperty = new BmoWorkContractProperty();
		bmoWorkContractProperty.getWorkContractId().setValue(bmoWorkContract.getId());
		bmoWorkContractProperty.getPropertyId().setValue(bmoProperty.getId());

		// Crear registro
		if (check) {
			save(bmoWorkContractProperty);
		} else {
			// Eliminar registro
			deleteBy(bmoWorkContractProperty, bmoWorkContractProperty.getWorkContractId(), bmoWorkContractProperty.getPropertyId());
		}	
	}

	@Override
	public void prepareCheckBoxHashMap(ArrayList<BmObject> checkBoxList) {
		Iterator<BmObject> iterator = checkBoxList.iterator();
		while (iterator.hasNext()) {
			bmoWorkContractProperty = (BmoWorkContractProperty)iterator.next();
			checkBoxHashMap.put(bmoWorkContractProperty.getPropertyId().toString(), bmoWorkContractProperty.getWorkContractId().toString());
		}
	}

	@Override
	public boolean isChecked(BmObject bmObject) {
		BmoProperty bmoProperty = (BmoProperty)bmObject;
		if (checkBoxHashMap.get(bmoProperty.getIdField().toString()) == null) return false;
		else return true;
	}

}
