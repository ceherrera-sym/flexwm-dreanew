/**
 * 
 */

package com.flexwm.client.co;

import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoWorkContractProperty;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiWorkContractProperty extends UiFormList {
	BmoWorkContractProperty bmoWorkContractProperty;

	UiListBox workContractListBox;
	int propertyId;
	BmoWorkContract bmoWorkContract;
	BmFilter bmWorkContractListFilter = new BmFilter();

	public UiWorkContractProperty(UiParams uiParams, Panel defaultPanel, int id) {
		super(uiParams, defaultPanel, new BmoWorkContract());
		bmoWorkContract = (BmoWorkContract)getBmObject();
		this.propertyId = id;

		// Lista solo grupos del usuario seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoWorkContractProperty.getKind(), 
				bmoWorkContractProperty.getPropertyId().getName(), 
				bmoWorkContractProperty.getPropertyId().getLabel(), 
				BmFilter.EQUALS, 
				propertyId,
				bmoWorkContractProperty.getPropertyId().getName());

		// Preparar filtro para mostrar solo grupos NO previamente asignados a este usuario
		BmoWorkContract bmoWorkContract = new BmoWorkContract();
		bmWorkContractListFilter.setNotInFilter(bmoWorkContractProperty.getKind(), 
				bmoWorkContract.getIdFieldName(), 
				bmoWorkContractProperty.getWorkContractId().getName(),
				bmoWorkContractProperty.getPropertyId().getName(),
				"" + propertyId
				);
		workContractListBox = new UiListBox(getUiParams(), bmoWorkContract, bmWorkContractListFilter);
	}

	@Override
	public void populateFields(){
		bmoWorkContractProperty = (BmoWorkContractProperty)getBmObject();
		if (id > 0) {
			formFlexTable.addLabelCell(1, 1, bmoWorkContractProperty.getBmoWorkContract().getName().toString());
		} else {
			formFlexTable.addField(1, 0, workContractListBox, bmoWorkContractProperty.getWorkContractId());	
		}
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoWorkContractProperty = new BmoWorkContractProperty();
		bmoWorkContractProperty.setId(id);
		bmoWorkContractProperty.getWorkContractId().setValue(workContractListBox.getSelectedId());
		bmoWorkContractProperty.getPropertyId().setValue(propertyId);		
		return bmoWorkContractProperty;
	}
}
