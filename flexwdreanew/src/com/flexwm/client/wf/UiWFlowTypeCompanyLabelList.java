package com.flexwm.client.wf;


import java.util.ArrayList;

import com.flexwm.shared.wf.BmoWFlowCategoryCompany;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowTypeCompany;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;


public class UiWFlowTypeCompanyLabelList extends UiFormLabelList {

	BmoWFlowTypeCompany bmoWFlowTypeCompany;
	UiListBox companyListBox = new UiListBox(getUiParams(),new BmoCompany());;
	BmoWFlowType bmoWFlowType;


	public UiWFlowTypeCompanyLabelList(UiParams uiParams,BmoWFlowType bmoWFlowType) {
		super(uiParams, new BmoWFlowTypeCompany());
		bmoWFlowTypeCompany = (BmoWFlowTypeCompany)getBmObject();
		this.bmoWFlowType = bmoWFlowType;
		forceFilter = new BmFilter();
		forceFilter.setValueFilter(bmoWFlowTypeCompany.getKind(), bmoWFlowTypeCompany.getWflowTypeId(), bmoWFlowType.getId());

	}
	private void addFilters() {

		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
		BmoCompany bmoCompany = new BmoCompany();

	

		BmoWFlowCategoryCompany bmoWFlowCategoryCompany = new BmoWFlowCategoryCompany();
		BmFilter filterByCompanies = new BmFilter();		
		filterByCompanies.setInFilter(bmoWFlowCategoryCompany.getKind(), 
				bmoCompany.getIdFieldName(), 
				bmoWFlowCategoryCompany.getCompanyId().getName(), 
				bmoWFlowCategoryCompany.getWflowCategoryId().getName(), ""+bmoWFlowType.getBmoWFlowCategory().getId());
		
			filterList.add(filterByCompanies);


		BmFilter bmFilter = new BmFilter();
		bmFilter.setNotInFilter(bmoWFlowTypeCompany.getKind(), 
				bmoCompany.getIdFieldName(), 
				bmoWFlowTypeCompany.getCompanyId().getName(),
				bmoWFlowTypeCompany.getWflowTypeId().getName(),
				"" +  bmoWFlowType.getId()
				);
		filterList.add(bmFilter);

		companyListBox = new UiListBox(getUiParams(),bmoCompany,filterList);

		formFlexTable.addField(0, 0, companyListBox,bmoWFlowTypeCompany.getCompanyId());

	}


	@Override
	public void populateFields(){
		bmoWFlowTypeCompany = (BmoWFlowTypeCompany)getBmObject();
		if (id > 0)
			formFlexTable.addLabelField(0, 0,bmoWFlowTypeCompany.getBmoCompany().getName());
		else
			formFlexTable.addField(0, 0, companyListBox,bmoWFlowTypeCompany.getCompanyId());
		addFilters();
	}
	

	@Override
	public BmObject populateBObject() throws BmException {
		bmoWFlowTypeCompany.getCompanyId().setValue(companyListBox.getSelectedId());
		bmoWFlowTypeCompany.getWflowTypeId().setValue(bmoWFlowType.getId());

		return bmoWFlowTypeCompany;
	}
}
