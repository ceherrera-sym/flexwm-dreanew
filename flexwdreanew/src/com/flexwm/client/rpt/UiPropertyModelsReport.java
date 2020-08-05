package com.flexwm.client.rpt;

import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyType;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;


public class UiPropertyModelsReport extends UiReport {

	BmoPropertyModel bmoPropertyModel;
	UiListBox developmentIdUiListBox = new UiListBox(getUiParams(), new BmoDevelopment());
	UiListBox typeUiListBox = new UiListBox(getUiParams(), new BmoPropertyType());
	UiListBox reportTypeListBox;

	String generalSection = "Filtros Generales";

	public UiPropertyModelsReport(UiParams uiParams) {
		super(uiParams, new BmoPropertyModel(), "/rpt/co/ptym_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoPropertyModel()));
		this.bmoPropertyModel = (BmoPropertyModel)getBmObject();				
	}

	@Override
	public void populateFields() {		
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Modelos General", "/rpt/co/ptym_general_report.jsp");		

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, developmentIdUiListBox, bmoPropertyModel.getDevelopmentId());
		formFlexTable.addField(3, 0, typeUiListBox, bmoPropertyModel.getPropertyTypeId(), true);
		typeUiListBox.setSelectedIndex(-1);

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoPropertyModel.getDevelopmentId(), developmentIdUiListBox);
		addFilter(bmoPropertyModel.getPropertyTypeId(), typeUiListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
