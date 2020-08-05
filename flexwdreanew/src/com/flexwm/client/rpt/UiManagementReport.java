
package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.co.BmoPropertyType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;


public class UiManagementReport extends UiReport {
	BmoPropertySale bmoPropertySale;

	UiListBox wFlowCategory = new UiListBox(getUiParams(), new BmoWFlowCategory());
	UiListBox wFlowTypeListBox;
	UiListBox propertyTypeListBox = new UiListBox(getUiParams(), new BmoPropertyType());
	UiListBox wflowPhasesListBox = new UiListBox(getUiParams());
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());	
	UiListBox yearListBox;

	int year = 2020;
	BmField yearField;
	String generalSection = "Filtros Generales";	

	UiListBox reportTypeListBox;

	public UiManagementReport(UiParams uiParams) {
		super(uiParams, new BmoPropertySale(), "/rpt/co/flex_management_report.jsp", "Gerencial");
		this.bmoPropertySale = (BmoPropertySale)getBmObject();

		yearField = new BmField("yearfield", "", "Año", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
	}

	@Override
	public void populateFields() {

		yearListBox = new UiListBox(getUiParams());
		for (int x = year; x > (year - 4); x--) {
			yearListBox.addItem("" + x, "" + x);
		}		

		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType);

		// Fase de flujo
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		BmFilter filterWFlowPhase = new BmFilter();
		filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), bmoWFlowPhase.getWFlowCategoryId().getName(), 	bmoWFlowCategory.getIdFieldName(),
				bmoWFlowCategory.getProgramId().getName(), "" + bmObjectProgramId);
		wflowPhasesListBox = new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase);		

		reportTypeListBox = new UiListBox(getUiParams());		
		reportTypeListBox.addItem("Reporte Comercial", "/rpt/co/flex_management_report.jsp");

		formFlexTable.addSectionLabel(0, 0,  generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, yearListBox, yearField);
		formFlexTable.addField(3, 0, wflowPhasesListBox, bmoPropertySale.getBmoWFlow().getWFlowPhaseId());
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoPropertySale.getBmoWFlow().getWFlowPhaseId(), wflowPhasesListBox);
		addFilter(bmoPropertySale.getSalesUserId(), userSuggestBox);
		addFilter(yearField, yearListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}

