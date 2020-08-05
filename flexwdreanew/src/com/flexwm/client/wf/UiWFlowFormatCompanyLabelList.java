package com.flexwm.client.wf;

import java.util.ArrayList;

import com.flexwm.shared.wf.BmoWFlowFormat;
import com.flexwm.shared.wf.BmoWFlowFormatCompany;
import com.flexwm.shared.wf.BmoWFlowTypeCompany;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;

public class UiWFlowFormatCompanyLabelList extends UiFormLabelList {
	BmoWFlowFormatCompany bmoWFlowFormatCompany;
	UiListBox companyListBox = new UiListBox(getUiParams(),new BmoCompany());
	BmoWFlowFormat bmoWFlowFormat;

	public UiWFlowFormatCompanyLabelList(UiParams uiParams, BmoWFlowFormat  bmoWFlowFormat) {
		super(uiParams, new BmoWFlowFormatCompany());
		bmoWFlowFormatCompany = (BmoWFlowFormatCompany)getBmObject();
		this.bmoWFlowFormat = bmoWFlowFormat;

		// Lista solo empresas de el formatos
		forceFilter = new BmFilter();
		forceFilter.setValueFilter(bmoWFlowFormatCompany.getKind(), bmoWFlowFormatCompany.getWflowformatId(), bmoWFlowFormat.getId());

	}	

	@Override
	public void populateFields(){
		bmoWFlowFormatCompany = (BmoWFlowFormatCompany)getBmObject();
		if (id > 0)	
			formFlexTable.addLabelField(1, 0, bmoWFlowFormatCompany.getBmoCompany().getName());
		else
			formFlexTable.addField(1, 0, companyListBox, bmoWFlowFormatCompany.getCompanyId());	

		addFilters();
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoWFlowFormatCompany.getCompanyId().setValue(companyListBox.getSelectedId());
		bmoWFlowFormatCompany.getWflowformatId().setValue( bmoWFlowFormat.getId());

		return bmoWFlowFormatCompany;
	}

	private void addFilters( ) {

		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

		BmoCompany bmoCompany = new BmoCompany();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setNotInFilter(bmoWFlowFormatCompany.getKind(), 
				bmoCompany.getIdFieldName(), 
				bmoWFlowFormatCompany.getCompanyId().getName(),
				bmoWFlowFormatCompany.getWflowformatId().getName(),
				"" +  bmoWFlowFormat.getId()
				);				
		filterList.add(bmFilter);		
		
			BmoWFlowTypeCompany bmoWFlowTypeCompany = new BmoWFlowTypeCompany();
			BmFilter filterByCompanies = new BmFilter();		
			filterByCompanies.setInFilter(bmoWFlowTypeCompany.getKind(), 
					bmoCompany.getIdFieldName(), 
					bmoWFlowTypeCompany.getCompanyId().getName(), 
					bmoWFlowTypeCompany.getWflowTypeId().getName(), ""+bmoWFlowFormat.getBmoWFlowType().getId());

			filterList.add(filterByCompanies);
		
		companyListBox = new UiListBox(getUiParams(),bmoCompany,filterList);

		formFlexTable.addField(1, 0, companyListBox, bmoWFlowFormatCompany.getCompanyId());		
	}
}
