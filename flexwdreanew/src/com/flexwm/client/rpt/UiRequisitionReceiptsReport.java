package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoWhSection;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoCompany;


public class UiRequisitionReceiptsReport extends UiReport {

	BmoRequisitionReceipt bmoRequisitionReceipt;

	UiListBox reportTypeListBox ;	
	UiListBox typeListBox = new UiListBox(getUiParams());
	UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
	UiDateBox receiptDateStartDateBox = new UiDateBox();
	UiDateBox receiptDateEndDateBox = new UiDateBox();
	BmField receiptDateEnd;
	UiSuggestBox whsectionSuggestBox = new UiSuggestBox(new BmoWhSection());	
	UiListBox qualityListBox;
	UiListBox punctualityListBox;
	UiListBox serviceListBox;
	UiListBox reliabilityListBox;
	UiListBox handlingListBox;
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
	UiListBox companyListBox;
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());

	String generalSection = "Filtros Generales";
	String cualifySupplierSection = "Filtros Valoración Proveedor";

	public UiRequisitionReceiptsReport(UiParams uiParams) {
		super(uiParams, new BmoRequisitionReceipt(), "/rpt/op/rerc_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoRequisitionReceipt()));
		this.bmoRequisitionReceipt = (BmoRequisitionReceipt)getBmObject();
		receiptDateEnd = new BmField("receiptDateEnd", "", "F. Fin Recibo", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Recibos Ordenes de Compra", "/rpt/op/rerc_general_report.jsp");

		companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		qualityListBox = new UiListBox(getUiParams());
		punctualityListBox = new UiListBox(getUiParams());
		serviceListBox = new UiListBox(getUiParams());
		reliabilityListBox = new UiListBox(getUiParams());
		handlingListBox = new UiListBox(getUiParams());

		// Calidad
		qualityListBox.addItem("", "0");
		qualityListBox.addItem("1", "1");
		qualityListBox.addItem("2", "2");
		qualityListBox.addItem("3", "3");
		qualityListBox.addItem("4", "4");
		qualityListBox.addItem("5", "5");

		// Puntualidad
		punctualityListBox.addItem("", "0");
		punctualityListBox.addItem("1", "1");
		punctualityListBox.addItem("2", "2");
		punctualityListBox.addItem("3", "3");
		punctualityListBox.addItem("4", "4");
		punctualityListBox.addItem("5", "5");

		// Atencion
		serviceListBox.addItem("", "0");
		serviceListBox.addItem("1", "1");
		serviceListBox.addItem("2", "2");
		serviceListBox.addItem("3", "3");
		serviceListBox.addItem("4", "4");
		serviceListBox.addItem("5", "5");

		// Veracidad
		reliabilityListBox.addItem("", "0");
		reliabilityListBox.addItem("1", "1");
		reliabilityListBox.addItem("2", "2");
		reliabilityListBox.addItem("3", "3");
		reliabilityListBox.addItem("4", "4");
		reliabilityListBox.addItem("5", "5");

		// Manejo Documentos
		handlingListBox.addItem("", "0");
		handlingListBox.addItem("1", "1");
		handlingListBox.addItem("2", "2");
		handlingListBox.addItem("3", "3");
		handlingListBox.addItem("4", "4");
		handlingListBox.addItem("5", "5");


		// Si esta asignada la empresa maestra, la pone por defecto en los filtros
		try {
			if (getSFParams().getSelectedCompanyId() > 0)
				bmoRequisitionReceipt.getBmoRequisition().getCompanyId().setValue(getSFParams().getSelectedCompanyId());
		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar Empresa: " + e.toString());
		}

		try {
			bmoRequisitionReceipt.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar moneda : " + e.toString());
		}


		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, typeListBox, bmoRequisitionReceipt.getType());
		formFlexTable.addField(3, 0, supplierSuggestBox, bmoRequisitionReceipt.getSupplierId());
		formFlexTable.addField(4, 0, receiptDateStartDateBox, bmoRequisitionReceipt.getReceiptDate());
		formFlexTable.addField(5, 0, receiptDateEndDateBox, receiptDateEnd);
		formFlexTable.addField(6, 0, whsectionSuggestBox, bmoRequisitionReceipt.getWhSectionId());
		statusListBox.setSelectedIndex(-1);
		formFlexTable.addField(7, 0, paymentStatusListBox, bmoRequisitionReceipt.getPayment(), true);
		paymentStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(8, 0, companyListBox, bmoRequisitionReceipt.getBmoRequisition().getCompanyId());
		formFlexTable.addField(9, 0, currencyListBox, bmoRequisitionReceipt.getCurrencyId());
		formFlexTable.addField(10, 0, statusListBox, bmoRequisitionReceipt.getStatus(), true);

		formFlexTable.addSectionLabel(11, 0, cualifySupplierSection, 2);
		formFlexTable.addField(12, 0, qualityListBox, bmoRequisitionReceipt.getQuality());
		formFlexTable.addField(13, 0, punctualityListBox, bmoRequisitionReceipt.getPunctuality());
		formFlexTable.addField(14, 0, serviceListBox, bmoRequisitionReceipt.getService());
		formFlexTable.addField(15, 0, reliabilityListBox, bmoRequisitionReceipt.getReliability());
		formFlexTable.addField(16, 0, handlingListBox, bmoRequisitionReceipt.getHandling());

		formFlexTable.hideSection(cualifySupplierSection);
		
		statusEffect();
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoRequisitionReceipt.getType(), typeListBox);
		addFilter(bmoRequisitionReceipt.getSupplierId(), supplierSuggestBox);
		addFilter(bmoRequisitionReceipt.getReceiptDate(), receiptDateStartDateBox.getTextBox().getText());
		addFilter(receiptDateEnd, receiptDateEndDateBox.getTextBox().getText());
		addFilter(bmoRequisitionReceipt.getWhSectionId(), whsectionSuggestBox);
		addFilter(bmoRequisitionReceipt.getWhSectionId(), whsectionSuggestBox);
		addFilter(bmoRequisitionReceipt.getQuality(), qualityListBox);
		addFilter(bmoRequisitionReceipt.getPunctuality(), punctualityListBox);
		addFilter(bmoRequisitionReceipt.getService(), serviceListBox);
		addFilter(bmoRequisitionReceipt.getReliability(), reliabilityListBox);
		addFilter(bmoRequisitionReceipt.getHandling(), handlingListBox);
		addFilter(bmoRequisitionReceipt.getStatus(), statusListBox);
		addFilter(bmoRequisitionReceipt.getPayment(), paymentStatusListBox);	
		addFilter(bmoRequisitionReceipt.getBmoRequisition().getCompanyId(), companyListBox);
		addFilter(bmoRequisitionReceipt.getCurrencyId(), currencyListBox);
	}

	private void statusEffect() {
		if (getSFParams().getSelectedCompanyId() > 0)
			companyListBox.setEnabled(false);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
