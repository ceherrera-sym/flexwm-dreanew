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

import com.flexwm.shared.co.BmoConceptGroup;
import com.flexwm.shared.co.BmoContractConceptGroup;
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

public class UiContractConceptGroup extends UiFormList{
	BmoContractConceptGroup bmoContractConceptGroup;

	UiSuggestBox conceptGroupIdUiSuggestBox = new UiSuggestBox(new BmoConceptGroup());
	int workContractId;
	
	public UiContractConceptGroup(UiParams uiParams, Panel defaultPanel, int workContractId) {
		super(uiParams, defaultPanel, new BmoContractConceptGroup());
		bmoContractConceptGroup = (BmoContractConceptGroup)getBmObject();

		this.workContractId = workContractId;
		forceFilter = new BmFilter();
		
		if (workContractId > 0) {
			forceFilter.setValueLabelFilter(bmoContractConceptGroup.getKind(), 
					bmoContractConceptGroup.getWorkContractId().getName(), 
					bmoContractConceptGroup.getWorkContractId().getLabel(), 
					BmFilter.EQUALS, 
					workContractId,
					bmoContractConceptGroup.getBmoWorkContract().getName().getName());	
		}
	}
	
	@Override
	public void populateFields(){
		bmoContractConceptGroup = (BmoContractConceptGroup)getBmObject();
		formFlexTable.addField(1, 0, conceptGroupIdUiSuggestBox, bmoContractConceptGroup.getConceptGroupId());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoContractConceptGroup.setId(id);
		if (workContractId > 0) bmoContractConceptGroup.getWorkContractId().setValue(workContractId);
		bmoContractConceptGroup.getConceptGroupId().setValue(conceptGroupIdUiSuggestBox.getSelectedId());

		return bmoContractConceptGroup;
	}
	
	/*
	@Override
	public void close() {
		UiContractConceptList uiContractConceptList = new UiContractConceptList(getUiParams());
		uiContractConceptList.show();
	}
	*/
}


