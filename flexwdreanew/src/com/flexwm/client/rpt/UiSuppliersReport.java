package com.flexwm.client.rpt;

import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoSupplierCategory;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;


public class UiSuppliersReport extends UiReport {

	BmoSupplier bmoSupplier;
	UiListBox reportTypeListBox ;	
	UiListBox typeListBox = new UiListBox(getUiParams());
	UiListBox fiscalTypeListBox = new UiListBox(getUiParams());
	UiListBox categoryListBox = new UiListBox(getUiParams(), new BmoSupplierCategory());

	String generalSection = "Filtros Generales";

	public UiSuppliersReport(UiParams uiParams) {
		super(uiParams, new BmoSupplier(), "/rpt/op/supl_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoSupplier()));
		this.bmoSupplier = (BmoSupplier)getBmObject();		
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Proveedor General", "/rpt/op/supl_general_report.jsp");

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, typeListBox, bmoSupplier.getType());
		formFlexTable.addField(3, 0, fiscalTypeListBox, bmoSupplier.getFiscalType());
		formFlexTable.addField(4, 0, categoryListBox, bmoSupplier.getSupplierCategoryId());

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoSupplier.getType(), typeListBox);
		addFilter(bmoSupplier.getFiscalType(), fiscalTypeListBox);
		addFilter(bmoSupplier.getSupplierCategoryId(), categoryListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
