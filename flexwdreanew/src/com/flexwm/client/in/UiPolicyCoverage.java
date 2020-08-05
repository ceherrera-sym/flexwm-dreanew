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
import com.flexwm.shared.in.BmoInsuranceCoverage;
import com.flexwm.shared.in.BmoPolicy;
import com.flexwm.shared.in.BmoPolicyCoverage;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;

public class UiPolicyCoverage extends UiFormList {
	TextBox amountTextBox = new TextBox();
	UiListBox coverageListBox = new UiListBox(getUiParams(), new BmoCoverage());
	BmoPolicyCoverage bmoPolicyCoverage;
	BmoPolicy bmoPolicy;
	BmoCoverage bmoCoverage;
	
	// Coverage
	TextBox minCoverageTextBox = new TextBox();
	TextBox maxCoverageTextBox = new TextBox();
	
	public UiPolicyCoverage(UiParams uiParams, Panel defaultPanel, BmoPolicy bmoPolicy) {
		super(uiParams, defaultPanel, new BmoPolicyCoverage());
		this.bmoPolicy = bmoPolicy;
		
		// Filtro forzado de coberturas de la póliza
		bmoPolicyCoverage = new BmoPolicyCoverage();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoPolicyCoverage.getKind(), 
				bmoPolicyCoverage.getPolicyId().getName(), 
				bmoPolicyCoverage.getPolicyId().getLabel(), 
				BmFilter.EQUALS, 
				bmoPolicy.getId(),
				bmoPolicyCoverage.getPolicyId().getName());
		
		// Filtro para mostrar solo coverturas del seguro
		BmFilter insuranceCoveragesFilter = new BmFilter();
		BmoInsuranceCoverage bmoInsuranceCoverage = new BmoInsuranceCoverage();
		bmoCoverage = new BmoCoverage();
		insuranceCoveragesFilter.setInFilter(
					bmoInsuranceCoverage.getKind(),
					bmoCoverage.getIdFieldName(),
					bmoInsuranceCoverage.getCoverageId().getName(),
					bmoInsuranceCoverage.getInsuranceId().getName(),
					bmoPolicy.getBmoInsurance().getIdField().toString()
				);
		coverageListBox.addBmFilter(insuranceCoveragesFilter);
	}
	
	@Override
	public void populateFields(){
		bmoPolicyCoverage = (BmoPolicyCoverage)getBmObject();
		formFlexTable.addField(1, 0, coverageListBox, bmoPolicyCoverage.getCoverageId());
		formFlexTable.addField(1, 2, amountTextBox, bmoPolicyCoverage.getAmount());
		
		formFlexTable.addFieldReadOnly(2, 0, minCoverageTextBox, bmoCoverage.getMinAmount());
		formFlexTable.addFieldReadOnly(2, 2, maxCoverageTextBox, bmoCoverage.getMaxAmount());
		
		changeCoverages();
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoPolicyCoverage = new BmoPolicyCoverage();
		bmoPolicyCoverage.setId(id);
		bmoPolicyCoverage.getAmount().setValue(amountTextBox.getText());
		bmoPolicyCoverage.getCoverageId().setValue(coverageListBox.getSelectedId());
		bmoPolicyCoverage.getPolicyId().setValue(bmoPolicy.getId());		
		return bmoPolicyCoverage;
	}
	
	@Override
	public void formListChange(ChangeEvent event) {
		changeCoverages();
	}
	
	private void changeCoverages() {
		// Resetear coberturas maximas y minimas
		minCoverageTextBox.setText("");
		maxCoverageTextBox.setText("");
		
		if (coverageListBox.getSelectedBmObject() != null) {
			bmoCoverage = (BmoCoverage)coverageListBox.getSelectedBmObject();
			if (bmoCoverage.getMinUnlimited().toBoolean()) minCoverageTextBox.setText("Sin Límite");
			else minCoverageTextBox.setText(bmoCoverage.getMinAmount().toString());
			
			if (bmoCoverage.getMaxUnlimited().toBoolean()) maxCoverageTextBox.setText("Sin Límite");
			else maxCoverageTextBox.setText(bmoCoverage.getMaxAmount().toString());
		}
	}
	
}
