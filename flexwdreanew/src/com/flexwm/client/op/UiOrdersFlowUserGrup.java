package com.flexwm.client.op;

import com.flexwm.shared.op.BmoOrderFlowUserGrup;
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;

public class UiOrdersFlowUserGrup extends UiFormLabelList {
	BmoOrderFlowUserGrup bmoOrderFlowUserGrup;
	UiListBox grupListBox =  new UiListBox(getUiParams(), new BmoProfile());
	UiListBox ordeTypeListBox =  new UiListBox(getUiParams(), new BmoOrderType());
	BmoProfile bmoProfile ;
	public UiOrdersFlowUserGrup(UiParams uiParams) {
		super(uiParams, new BmoOrderFlowUserGrup());
		bmoOrderFlowUserGrup = (BmoOrderFlowUserGrup)getBmObject();
		

				
	}
	@Override
	public void populateFields() {
		bmoOrderFlowUserGrup = (BmoOrderFlowUserGrup)getBmObject();
		BmoProfile bmoProfile = new BmoProfile();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setNotInFilter(bmoOrderFlowUserGrup.getKind(), 
				bmoProfile.getIdFieldName(),
				bmoOrderFlowUserGrup.getProfileId().getName(),
				"1"," 1"
				);
		grupListBox.addFilter(bmFilter);
		formFlexTable.addField(1, 0, grupListBox, bmoOrderFlowUserGrup.getProfileId());
		
	}
	@Override
	public BmObject populateBObject() throws BmException {
		bmoOrderFlowUserGrup = new BmoOrderFlowUserGrup();
		bmoOrderFlowUserGrup.setId(id);
		bmoOrderFlowUserGrup.getProfileId().setValue(grupListBox.getSelectedId());		
		

		return bmoOrderFlowUserGrup;
	}


}
