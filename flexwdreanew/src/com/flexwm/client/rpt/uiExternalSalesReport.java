package com.flexwm.client.rpt;



import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoExternalSales;
import com.flexwm.shared.op.BmoProduct;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.sf.BmoArea;



public class uiExternalSalesReport extends UiReport {

	BmoExternalSales bmoExternalSales;
	
	UiListBox areaListBox = new UiListBox(getUiParams(), new BmoArea());
	UiSuggestBox custumerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
	UiListBox reportTypeListBox;

	String generalSection = "Filtros Generales";

	public uiExternalSalesReport(UiParams uiParams) {
		super(uiParams, new BmoExternalSales(), "/rpt/cm/exts_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoExternalSales()));
		this.bmoExternalSales = (BmoExternalSales)getBmObject();

	}	

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte General Venas Externas", "/rpt/cm/exts_general_report.jsp");

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, custumerSuggestBox, bmoExternalSales.getCustomerId());
		formFlexTable.addField(3, 0, productSuggestBox, bmoExternalSales.getProductId());
	

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoExternalSales.getCustomerId(), custumerSuggestBox);
		addFilter(bmoExternalSales.getProductId(), productSuggestBox);
		
	}
	
	
}
