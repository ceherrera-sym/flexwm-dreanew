/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import java.util.ArrayList;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomerCompany;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;


public class UiCustomerCompanyLabelList extends UiFormLabelList {
	BmoCustomerCompany bmoCustomerCompany;
	UiListBox companyListBox;
	int customerId;
	BmoCompany bmoCompany;

	public UiCustomerCompanyLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerCompany());
		bmoCustomerCompany = (BmoCustomerCompany)getBmObject();
		this.customerId = id;

		// Lista solo grupos del usuario seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerCompany.getKind(), 
				bmoCustomerCompany.getCustomerId().getName(), 
				bmoCustomerCompany.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				customerId,
				bmoCustomerCompany.getCustomerId().getName());
		
		// Preparar filtro para mostrar solo grupos NO previamente asignados a este usuario
		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

		BmoCompany bmoCompany = new BmoCompany();
		BmFilter bmCompanyListFilter = new BmFilter();
		bmCompanyListFilter.setNotInFilter(bmoCustomerCompany.getKind(), 
				bmoCompany.getIdFieldName(), 
				bmoCustomerCompany.getCompanyId().getName(),
				bmoCustomerCompany.getCustomerId().getName(),
				"" + customerId
				);
		filterList.add(bmCompanyListFilter);
		
		// MUltiempresa: g100
		if(getSFParams().getSelectedCompanyId() > 0  
				&& ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean() ) {
			// Preparar filtro para mostrar solo grupos NO previamente asignados a este usuario
			BmFilter byCompanyListFilter = new BmFilter();
			byCompanyListFilter.setInFilter(bmoCustomerCompany.getKind(), 
					bmoCompany.getIdFieldName(), 
					bmoCustomerCompany.getCompanyId().getName(),
					bmoCustomerCompany.getCompanyId().getName(),
					"" + getSFParams().getSelectedCompanyId()
					);
			filterList.add(byCompanyListFilter);

		}

		
		companyListBox = new UiListBox(getUiParams(), bmoCompany, filterList);
	}
	
	@Override
	public void populateFields(){
		bmoCustomerCompany = (BmoCustomerCompany)getBmObject();
		if (id > 0) {
			formFlexTable.addLabelCell(1, 0, bmoCustomerCompany.getBmoCompany().getName().toString());
		} else {
			formFlexTable.addField(1, 0, companyListBox, bmoCustomerCompany.getCompanyId());	
		}
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerCompany = new BmoCustomerCompany();
		bmoCustomerCompany.setId(id);
		bmoCustomerCompany.getCompanyId().setValue(companyListBox.getSelectedId());
		bmoCustomerCompany.getCustomerId().setValue(customerId);		
		return bmoCustomerCompany;
	}
}
