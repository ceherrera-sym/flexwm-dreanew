/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.in;

import com.flexwm.shared.in.BmoCoverage;
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoInsuranceCoverage;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiInsuranceCoverage extends UiFormList {
	BmoInsuranceCoverage bmoInsuranceCoverage;

	UiListBox coverageListBox;
	BmoInsurance bmoInsurance;

	public UiInsuranceCoverage(UiParams uiParams, Panel defaultPanel, BmoInsurance bmoInsurance) {
		super(uiParams, defaultPanel, new BmoInsuranceCoverage());
		bmoInsuranceCoverage = (BmoInsuranceCoverage)getBmObject();
		this.bmoInsurance = bmoInsurance;

		// Lista solo usuarios del grupo seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoInsuranceCoverage.getKind(), 
				bmoInsuranceCoverage.getInsuranceId().getName(), 
				bmoInsuranceCoverage.getInsuranceId().getLabel(), 
				BmFilter.EQUALS, 
				bmoInsurance.getId(),
				bmoInsuranceCoverage.getInsuranceId().getName());

		// Preparar filtro para mostrar solo coberturas NO previamente asignados a este producto
		BmoCoverage bmoCoverage = new BmoCoverage();
		//		BmFilter bmFilter = new BmFilter();
		//		bmFilter.setNotInFilter(bmoInsuranceCoverage.getKind(), 
		//				bmoCoverage.getIdFieldName(), 
		//				bmoInsuranceCoverage.getCoverageId().getName(),
		//				bmoInsuranceCoverage.getInsuranceId().getName(),
		//				"" + bmoInsurance.getId()
		//				);
		coverageListBox = new UiListBox(getUiParams(), bmoCoverage);


	}

	@Override
	public void populateFields(){
		bmoInsuranceCoverage = (BmoInsuranceCoverage)getBmObject();
		formFlexTable.addField(1, 0, coverageListBox, bmoInsuranceCoverage.getCoverageId());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoInsuranceCoverage = new BmoInsuranceCoverage();
		bmoInsuranceCoverage.setId(id);
		bmoInsuranceCoverage.getCoverageId().setValue(coverageListBox.getSelectedId());
		bmoInsuranceCoverage.getInsuranceId().setValue(bmoInsurance.getId());		
		return bmoInsuranceCoverage;
	}
}
