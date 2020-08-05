package com.flexwm.client.wf;

import com.flexwm.shared.wf.BmoWFlowCategoryCompany;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;

public class UiWFlowCategoryCompanyLabelList extends UiFormLabelList{
	
	BmoWFlowCategoryCompany bmoWFlowCategoryCompany;
	UiListBox companyListBox;
	int wflowcategoryId;
	
	public UiWFlowCategoryCompanyLabelList(UiParams uiParams, int wflowcategoryId) {
		super(uiParams, new BmoWFlowCategoryCompany());
		bmoWFlowCategoryCompany = (BmoWFlowCategoryCompany)getBmObject();
		this.wflowcategoryId = wflowcategoryId;
		
		forceFilter = new BmFilter();
		forceFilter.setValueFilter(bmoWFlowCategoryCompany.getKind(), bmoWFlowCategoryCompany.getWflowCategoryId(), wflowcategoryId);
		
		BmoCompany bmoCompany = new BmoCompany();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setNotInFilter(bmoWFlowCategoryCompany.getKind(), 
				bmoCompany.getIdFieldName(), 
				bmoWFlowCategoryCompany.getCompanyId().getName(),
				bmoWFlowCategoryCompany.getWflowCategoryId().getName(),
				"" + wflowcategoryId
				);
		
		companyListBox = new UiListBox(uiParams,bmoCompany,bmFilter);
	}
	
	@Override
	public void populateFields(){
		bmoWFlowCategoryCompany = (BmoWFlowCategoryCompany)getBmObject();
		if (id > 0)
			formFlexTable.addLabelField(1, 0, bmoWFlowCategoryCompany.getBmoCompany().getName());
		else
			formFlexTable.addField(1, 0, companyListBox, bmoWFlowCategoryCompany.getCompanyId());	
		
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoWFlowCategoryCompany.getCompanyId().setValue(companyListBox.getSelectedId());
		bmoWFlowCategoryCompany.getWflowCategoryId().setValue(wflowcategoryId);
		
		return bmoWFlowCategoryCompany;
	}

}
