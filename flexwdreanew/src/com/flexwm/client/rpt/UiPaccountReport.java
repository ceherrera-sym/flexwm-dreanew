package com.flexwm.client.rpt;

import java.sql.Types;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoSupplierCategory;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCompany;


public class UiPaccountReport extends UiReport {

	BmoPaccount bmoPaccount;	
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
	UiListBox reportTypeListBox;
	UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
	UiListBox suplCategoryListBox = new UiListBox(getUiParams(), new BmoSupplierCategory());
	UiSuggestBox requisitionSuggestBox = new UiSuggestBox(new BmoRequisition());
	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
	UiListBox paccountTypeListBox = new UiListBox(getUiParams(), new BmoPaccountType());
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiSuggestBox budgetSuggestBox = new UiSuggestBox(new BmoBudget() );
	UiSuggestBox budgetItemSuggestBox = new UiSuggestBox(new BmoBudgetItem() );
	BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
	UiDateBox receiveDateBox = new UiDateBox();
	UiDateBox receiveEndDateBox = new UiDateBox();	
	UiDateBox dueDateBox = new UiDateBox();
	UiDateBox dueEndDateBox = new UiDateBox();	
	BmField receiveEndDate;
	BmField dueEndDate;
	UiDateBox paymentDateStartMbDateBox = new UiDateBox();
	BmField paymentDateStartMb;
	UiDateBox paymentDateEndMbDateBox = new UiDateBox();
	BmField paymentDateEndMb;
	UiListBox areaListBox;

	String generalSection = "Filtros Generales";
	String datesSection = "Filtros de Fechas";
	String statusSection = "Filtros de Estatus";

	public UiPaccountReport(UiParams uiParams) {
		super(uiParams, new BmoPaccount(), "/rpt/fi/pacc_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoPaccount()));
		this.bmoPaccount = (BmoPaccount)getBmObject();		
		receiveEndDate = new BmField("receiveenddate", "", "Recepción Final", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		dueEndDate = new BmField("dueenddate", "", "Programación Final", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		paymentDateStartMb = new BmField("paymentdatestartmb", "", "Fecha Pago", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		paymentDateEndMb = new BmField("paymentdateendmb", "", "Fecha Pago Fin", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte General de CxP", "/rpt/fi/pacc_general_report.jsp");
		reportTypeListBox.addItem("Reporte General de CxP Agrupado por Proveedor", "/rpt/fi/pacc_general_by_sup.jsp");
		reportTypeListBox.addItem("Reporte Detallado de CxP", "/rpt/fi/pacc_detail_report.jsp");
		reportTypeListBox.addItem("Reporte Antigüedad de Saldos de CxP", "/rpt/fi/pacc_paymentschedule_report.jsp"); 
		areaListBox = new UiListBox(getUiParams(), new BmoArea());
		// Si esta asignada la empresa maestra, la pone por defecto en los filtros
		try {
			if (getSFParams().getSelectedCompanyId() > 0)
				bmoPaccount.getCompanyId().setValue(getSFParams().getSelectedCompanyId());

		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar Empresa: " + e.toString());
		}

		try {
			bmoPaccount.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		} catch (BmException e) {
			showSystemMessage("No se puede asignar moneda : " + e.toString());
		}

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, supplierSuggestBox, bmoPaccount.getSupplierId());
		formFlexTable.addField(3, 0, suplCategoryListBox, bmoPaccount.getBmoSupplier().getSupplierCategoryId(), true);
		formFlexTable.addField(4, 0, companyListBox, bmoPaccount.getCompanyId());
		formFlexTable.addField(5, 0, paccountTypeListBox, bmoPaccount.getPaccountTypeId());
		
		// Si esta autorizado el manejo de presupuestos, mostrarlo
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			formFlexTable.addField(6, 0, budgetSuggestBox, bmoBudgetItem.getBudgetId());
			formFlexTable.addField(7, 0, budgetItemSuggestBox, bmoPaccount.getBudgetItemId());
			formFlexTable.addField(8, 0, areaListBox, bmoPaccount.getAreaId());
		}
		formFlexTable.addField(9, 0, currencyListBox, bmoPaccount.getCurrencyId());
		
		formFlexTable.addSectionLabel(10, 0, datesSection, 2);
		formFlexTable.addField(11, 0, receiveDateBox, bmoPaccount.getReceiveDate());
		formFlexTable.addField(12, 0, receiveEndDateBox, receiveEndDate);
		formFlexTable.addField(13, 0, dueDateBox, bmoPaccount.getDueDate());		
		formFlexTable.addField(14, 0, dueEndDateBox, dueEndDate);
		formFlexTable.addField(15, 0, paymentDateStartMbDateBox, paymentDateStartMb);		
		formFlexTable.addField(16, 0, paymentDateEndMbDateBox, paymentDateEndMb);
		
		formFlexTable.addSectionLabel(17, 0, statusSection, 2);
		formFlexTable.addField(18, 0, paymentStatusListBox, bmoPaccount.getPaymentStatus(), true);
		paymentStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(19, 0, statusListBox, bmoPaccount.getStatus(), true);
		statusListBox.setSelectedIndex(-1);

		statusEffect();
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoPaccount.getSupplierId(), supplierSuggestBox);		
		addFilter(bmoPaccount.getRequisitionId(), requisitionSuggestBox);
		addFilter(bmoPaccount.getCompanyId(), companyListBox);
		addFilter(bmoPaccount.getBmoSupplier().getSupplierCategoryId(), suplCategoryListBox);
		addFilter(bmoPaccount.getPaccountTypeId(), paccountTypeListBox);		
		addFilter(bmoPaccount.getStatus(), statusListBox);
		addFilter(bmoPaccount.getPaymentStatus(), paymentStatusListBox);
		addFilter(bmoPaccount.getCurrencyId(), currencyListBox);
		addFilter(bmoPaccount.getReceiveDate(), receiveDateBox.getTextBox().getText());		
		addFilter(receiveEndDate, receiveEndDateBox.getTextBox().getText());
		addFilter(bmoPaccount.getDueDate(), dueDateBox.getTextBox().getText());
		addFilter(dueEndDate, dueEndDateBox.getTextBox().getText());
		addFilter(paymentDateStartMb, paymentDateStartMbDateBox.getTextBox().getText());
		addFilter(paymentDateEndMb, paymentDateEndMbDateBox.getTextBox().getText());
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			addFilter(bmoBudgetItem.getBudgetId(), budgetSuggestBox);
			addFilter(bmoPaccount.getBudgetItemId(), budgetItemSuggestBox);
			addFilter(bmoPaccount.getAreaId(), areaListBox);
		}
	}

	private void statusEffect() {
		if (getSFParams().getSelectedCompanyId() > 0)
			companyListBox.setEnabled(false);
		
		formFlexTable.hideSection(datesSection);
		formFlexTable.hideSection(statusSection);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
