/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client;

import com.flexwm.shared.BmoCompanySalesman;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProfile;


public class UiCompanySalesmanLabelList extends UiFormLabelList {
	UiListBox profileListBox = new UiListBox(getUiParams(), new BmoProfile());
	UiListBox coordinatorProfileListBox = new UiListBox(getUiParams(), new BmoProfile());
	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());

	BmoCompanySalesman bmoCompanySalesman;
	int flexConfigId;

	public UiCompanySalesmanLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCompanySalesman());
		flexConfigId = id;
		bmoCompanySalesman = new BmoCompanySalesman();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCompanySalesman.getKind(), 
				bmoCompanySalesman.getFlexConfigId().getName(), 
				bmoCompanySalesman.getFlexConfigId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCompanySalesman.getFlexConfigId().getName());
	}

	@Override
	public void populateFields() {
		bmoCompanySalesman = (BmoCompanySalesman)getBmObject();
		
		// Obtener empresas que no esten asignadas
		if (!(bmoCompanySalesman.getId() > 0)) {
			BmoCompany bmoCompany = new BmoCompany();
			BmFilter bmCompanyListFilter = new BmFilter();
			bmCompanyListFilter.setNotInFilter(bmoCompanySalesman.getKind(), 
					bmoCompany.getIdFieldName(), 
					bmoCompanySalesman.getCompanyId().getName(),
					"1", "1");
			companyListBox.addFilter(bmCompanyListFilter);
		} else { 
			// Limpiar combo si ya existe
			companyListBox.clear();
			companyListBox.clearFilters();
		}
		
		formFlexTable.addField(1, 0, companyListBox, bmoCompanySalesman.getCompanyId());
		formFlexTable.addField(2, 0, profileListBox, bmoCompanySalesman.getProfileId());
		formFlexTable.addField(3, 0, coordinatorProfileListBox, bmoCompanySalesman.getCoordinatorProfileId());

		statusEffect();
	}

	private void statusEffect() {
		if (bmoCompanySalesman.getId() > 0) 
			companyListBox.setEnabled(false);
		else {
			companyListBox.setEnabled(true);
		}
	}

	@Override
	public BmObject populateBObject() throws BmException {

		bmoCompanySalesman = new BmoCompanySalesman();
		bmoCompanySalesman.setId(id);
		bmoCompanySalesman.getProfileId().setValue(profileListBox.getSelectedId());
		bmoCompanySalesman.getCoordinatorProfileId().setValue(coordinatorProfileListBox.getSelectedId());
		bmoCompanySalesman.getCompanyId().setValue(companyListBox.getSelectedId());
		bmoCompanySalesman.getFlexConfigId().setValue(flexConfigId);

		return bmoCompanySalesman;
	}
}
