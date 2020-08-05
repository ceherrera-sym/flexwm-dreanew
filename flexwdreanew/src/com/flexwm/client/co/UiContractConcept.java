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

import com.flexwm.shared.co.BmoConcept;
import com.flexwm.shared.co.BmoContractConcept;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;

/**
 * @author smuniz
 *
 */

public class UiContractConcept extends UiFormList{
	BmoContractConcept bmoContractConcept;

	UiSuggestBox conceptIdUiSuggestBox = new UiSuggestBox(new BmoConcept());
	int workContractId;
	
	public UiContractConcept(UiParams uiParams, Panel defaultPanel, int workContractId) {
		super(uiParams, defaultPanel, new BmoContractConcept());
		bmoContractConcept = (BmoContractConcept)getBmObject();

		this.workContractId = workContractId;
		forceFilter = new BmFilter();
		
		if (workContractId > 0) {
			forceFilter.setValueLabelFilter(bmoContractConcept.getKind(), 
					bmoContractConcept.getWorkContractid().getName(), 
					bmoContractConcept.getWorkContractid().getLabel(), 
					BmFilter.EQUALS, 
					workContractId,
					bmoContractConcept.getBmoWorkContract().getName().getName());	
		}
	}
	
	@Override
	public void populateFields(){
		bmoContractConcept = (BmoContractConcept)getBmObject();
		formFlexTable.addField(1, 0, conceptIdUiSuggestBox, bmoContractConcept.getConceptId());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoContractConcept.setId(id);
		if (workContractId > 0) bmoContractConcept.getWorkContractid().setValue(workContractId);
		bmoContractConcept.getConceptId().setValue(conceptIdUiSuggestBox.getSelectedId());

		return bmoContractConcept;
	}
	/*
	@Override
	public void close() {
		UiContractConceptList uiContractConceptList = new UiContractConceptList(getUiParams());
		uiContractConceptList.show();
	}
	*/
}


