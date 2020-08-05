package com.flexwm.client.rpt;

import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhSection;
import com.flexwm.shared.op.BmoWhStock;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmFilter;


public class UiWareHouseReport extends UiReport {

	BmoWhStock bmoWhStock;
	UiListBox reportTypeListBox;	
	UiSuggestBox wareHouseUiSuggestBox = new UiSuggestBox(new BmoWarehouse());
	UiSuggestBox whSectionUiSuggestBox = new UiSuggestBox(new BmoWhSection());
	UiSuggestBox productUiSuggestBox = new UiSuggestBox(new BmoProduct());
	UiListBox typeUiListBox = new UiListBox(getUiParams());

	String generalSection = "Filtros Generales";

	public UiWareHouseReport(UiParams uiParams) {
		super(uiParams, new BmoWhStock(), "/rpt/op/ware_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoWhStock()));
		this.bmoWhStock = (BmoWhStock)getBmObject();
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("General de Almacenes", "/rpt/op/ware_general_report.jsp");
		reportTypeListBox.addItem("Existencias en Almacén", "/rpt/op/whst_whstock_report.jsp");

		// Tipo de Almacen
		BmoWhSection bmoWhSection = new BmoWhSection();
		BmFilter filterWhSection = new BmFilter();
		filterWhSection.setValueFilter(bmoWhSection.getKind(), bmoWhSection.getBmoWarehouse().getType(), ""+BmoWarehouse.TYPE_NORMAL);		
		whSectionUiSuggestBox = new UiSuggestBox(new BmoWhSection(), filterWhSection);
		whSectionUiSuggestBox.addFilter(filterWhSection);

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, wareHouseUiSuggestBox, bmoWhStock.getBmoWhSection().getWarehouseId());	
		formFlexTable.addField(3, 0, whSectionUiSuggestBox, bmoWhStock.getWhSectionId());
		formFlexTable.addField(4, 0, typeUiListBox, bmoWhStock.getBmoWhSection().getBmoWarehouse().getType());
		formFlexTable.addField(5, 0, productUiSuggestBox, bmoWhStock.getProductId());

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoWhStock.getBmoWhSection().getWarehouseId(), wareHouseUiSuggestBox);
		addFilter(bmoWhStock.getWhSectionId(), whSectionUiSuggestBox);
		addFilter(bmoWhStock.getBmoWhSection().getBmoWarehouse().getType(), typeUiListBox);
		addFilter(bmoWhStock.getProductId(), productUiSuggestBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
