package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoCompany;


public class UiWhMovementsReport extends UiReport {

	BmoWhMovement bmoWhMovement;
	BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
	UiListBox reportTypeListBox ;	
	UiListBox typeUiListBox = new UiListBox(getUiParams());
	UiListBox companyUiListBox = new UiListBox(getUiParams(), new BmoCompany());
	UiListBox statusUiListBox = new UiListBox(getUiParams());
	UiSuggestBox warehouseSuggestBox = new UiSuggestBox(new BmoWarehouse());	
	UiSuggestBox whSectionSuggestBox = new UiSuggestBox(new BmoWhSection());	
	UiDateBox dateMovStartDateBox = new UiDateBox();
	BmField dateMovEnd;
	UiDateBox dateMovEndDateBox = new UiDateBox();
	UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());

	String generalSection = "Filtros Generales";

	public UiWhMovementsReport(UiParams uiParams) {
		super(uiParams, new BmoWhMovement(), "/rpt/op/whmv_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoWhMovement()));
		this.bmoWhMovement = (BmoWhMovement)getBmObject();

		dateMovEnd = new BmField("datemovend", "", "Fecha/Hora Fin", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);		
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte Tx. de Inventarios", "/rpt/op/whmv_general_report.jsp");
		reportTypeListBox.addItem("Reporte Detallado Tx. de Inventarios", "/rpt/op/whmv_detail_report.jsp");
		reportTypeListBox.addItem("Rpt. Tx. de Inventarios por Mes", "/rpt/op/whmv_date_report.jsp");

		formFlexTable.addSectionLabel(0,  0,  generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");

		if (getSFParams().isFieldEnabled(bmoWhMovement.getType())) {
			formFlexTable.addField(2, 0, typeUiListBox, bmoWhMovement.getType(), true);
			typeUiListBox.setSelectedIndex(-1);
		}
		if (getSFParams().isFieldEnabled(bmoWhMovement.getCompanyId()))
			formFlexTable.addField(3, 0, companyUiListBox, bmoWhMovement.getCompanyId());

		if (getSFParams().isFieldEnabled(bmoWhMovement.getBmoToWhSection().getWarehouseId()))
			formFlexTable.addField(4, 0, warehouseSuggestBox, bmoWhMovement.getBmoToWhSection().getWarehouseId());

		if (getSFParams().isFieldEnabled(bmoWhMovement.getToWhSectionId()))
			formFlexTable.addField(5, 0, whSectionSuggestBox, bmoWhMovement.getToWhSectionId());

		if (getSFParams().isFieldEnabled(bmoWhMovement.getDatemov())) {
			formFlexTable.addField(6, 0, dateMovStartDateBox, bmoWhMovement.getDatemov());
			formFlexTable.addField(7, 0, dateMovEndDateBox, dateMovEnd);
		}

		if (getSFParams().isFieldEnabled(bmoWhMovItem.getProductId()))
			formFlexTable.addField(8, 0, productSuggestBox, bmoWhMovItem.getProductId());

		if (getSFParams().isFieldEnabled(bmoWhMovement.getStatus())) {
			formFlexTable.addField(9, 0, statusUiListBox, bmoWhMovement.getStatus(), true);
			statusUiListBox.setSelectedIndex(-1);
		}

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		if (getSFParams().isFieldEnabled(bmoWhMovement.getType())) 
			addFilter(bmoWhMovement.getType(), typeUiListBox);

		if (getSFParams().isFieldEnabled(bmoWhMovement.getCompanyId())) 
			addFilter(bmoWhMovement.getCompanyId(), companyUiListBox);

		if (getSFParams().isFieldEnabled(bmoWhMovement.getBmoToWhSection().getWarehouseId())) 
			addFilter(bmoWhMovement.getBmoToWhSection().getWarehouseId(), warehouseSuggestBox);

		if (getSFParams().isFieldEnabled(bmoWhMovement.getToWhSectionId())) 
			addFilter(bmoWhMovement.getToWhSectionId(), whSectionSuggestBox);

		if (getSFParams().isFieldEnabled(bmoWhMovement.getDatemov())) {
			addFilter(bmoWhMovement.getDatemov(), dateMovStartDateBox.getTextBox().getText());
			addFilter(dateMovEnd, dateMovEndDateBox.getTextBox().getText());
		}

		if (getSFParams().isFieldEnabled(bmoWhMovement.getStatus())) 
			addFilter(bmoWhMovement.getStatus(), statusUiListBox);

		if (getSFParams().isFieldEnabled(bmoWhMovItem.getProductId())) 
			addFilter(bmoWhMovItem.getProductId(), productSuggestBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
