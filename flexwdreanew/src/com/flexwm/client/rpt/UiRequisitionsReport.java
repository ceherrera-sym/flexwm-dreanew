package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionType;
import com.flexwm.shared.op.BmoSupplier;
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
import com.symgae.shared.sf.BmoUser;


public class UiRequisitionsReport extends UiReport {

	BmoRequisition bmoRequisition;

	UiListBox reportTypeListBox ;	
	UiListBox requistionTypeListBox;
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
	UiListBox deliveryStatusListBox = new UiListBox(getUiParams());
	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
	UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
	UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());
	UiListBox areaListBox;
	UiDateBox deliveryStartBox = new UiDateBox();
	UiDateBox deliveryEndBox = new UiDateBox();	
	UiDateBox requestStartBox = new UiDateBox();
	UiDateBox requestEndBox = new UiDateBox();
	UiDateBox dueStartBox = new UiDateBox();
	UiDateBox dueEndBox = new UiDateBox();
	UiSuggestBox budgetSuggestBox = new UiSuggestBox(new BmoBudget());
	UiSuggestBox budgetItemSuggestBox = new UiSuggestBox(new BmoBudgetItem());
	BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

	BmField deliveryStart;
	BmField deliveryEnd;
	BmField requestStart;
	BmField requestEnd;
	BmField dueStart;
	BmField dueEnd;
	BmField currencyId;

	String generalSection = "Filtros Generales";
	String datesSection = "Filtros de Fechas";
	String statusSection = "Filtros de Estatus";

	public UiRequisitionsReport(UiParams uiParams) {
		super(uiParams, new BmoRequisition(), "/rpt/op/reqi_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoRequisition()));
		this.bmoRequisition = (BmoRequisition)getBmObject();

		deliveryStart = new BmField("deliverystart", "", "Inicio Entrega", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		deliveryEnd = new BmField("deliveryEnd", "", "Fin Entrega", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		requestStart = new BmField("requeststart", "", "Inicio Solicitud", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		requestEnd = new BmField("requestEnd", "", "Fin Solicitud", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		dueStart = new BmField("duestart", "", "Inicio Pago", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		dueEnd = new BmField("dueEnd", "", "Fin Pago", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		currencyId = new BmField("currencyid", "", "Moneda", 10, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte General de Ordenes de Compra", "/rpt/op/reqi_general_report.jsp");
		reportTypeListBox.addItem("Reporte O.C. Agrupado por Proveedor", "/rpt/op/reqi_bysupplier_report.jsp");
		reportTypeListBox.addItem("Reporte O.C. Agrupado por Usuario", "/rpt/op/reqi_requestedby_report.jsp");
		reportTypeListBox.addItem("Reporte O.C. Agrupado por Departamento", "/rpt/op/reqi_byarea_report.jsp");
		reportTypeListBox.addItem("Reporte O.C. con Anticipo Prov.", "/rpt/op/reqi_requisitionadvance_report.jsp");

		requistionTypeListBox = new UiListBox(getUiParams(), new BmoRequisitionType());
		areaListBox = new UiListBox(getUiParams(), new BmoArea());

		// Si esta asignada la empresa maestra, la pone por defecto en los filtros
		try {
			if (getSFParams().getSelectedCompanyId() > 0)
				bmoRequisition.getCompanyId().setValue(getSFParams().getSelectedCompanyId());

		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar Empresa: " + e.toString());
		}

		try {
			bmoRequisition.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		} catch (BmException e) {
			showSystemMessage("No se puede asignar moneda : " + e.toString());
		}

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");		
		formFlexTable.addField(2, 0, supplierSuggestBox, bmoRequisition.getSupplierId());
		formFlexTable.addField(3, 0, userSuggestBox, bmoRequisition.getRequestedBy());
		formFlexTable.addField(4, 0, areaListBox, bmoRequisition.getAreaId(), true);
		formFlexTable.addField(5, 0, requistionTypeListBox, bmoRequisition.getRequisitionTypeId());
		// Si esta autorizado el manejo de presupuestos, mostrarlo
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			formFlexTable.addField(6, 0, budgetSuggestBox, bmoBudgetItem.getBudgetId());
			formFlexTable.addField(7, 0, budgetItemSuggestBox, bmoRequisition.getBudgetItemId());
		}
		formFlexTable.addField(8, 0, companyListBox, bmoRequisition.getCompanyId());
		formFlexTable.addField(9, 0, orderSuggestBox, bmoRequisition.getOrderId());
		formFlexTable.addField(10, 0, currencyListBox, bmoRequisition.getCurrencyId());

		formFlexTable.addSectionLabel(11, 0, datesSection, 2);		
		formFlexTable.addField(12, 0, requestStartBox, requestStart);
		formFlexTable.addField(13, 0, requestEndBox, requestEnd);
		formFlexTable.addField(14, 0, deliveryStartBox, deliveryStart);
		formFlexTable.addField(15, 0, deliveryEndBox, deliveryEnd);
		formFlexTable.addField(16, 0, dueStartBox, dueStart);
		formFlexTable.addField(17, 0, dueEndBox, dueEnd);

		formFlexTable.addSectionLabel(18, 0, statusSection, 2);		
		formFlexTable.addField(19, 0, deliveryStatusListBox, bmoRequisition.getDeliveryStatus(), true);
		deliveryStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(20, 0, paymentStatusListBox, bmoRequisition.getPaymentStatus(), true);		
		paymentStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(21, 0, statusListBox, bmoRequisition.getStatus(), true);
		statusListBox.setSelectedIndex(-1);

		formFlexTable.hideSection(datesSection);
		formFlexTable.hideSection(statusSection);

		statusEffect();
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoRequisition.getSupplierId(), supplierSuggestBox);
		addFilter(bmoRequisition.getAreaId(), areaListBox);
		addFilter(bmoRequisition.getRequestedBy(), userSuggestBox);
		addFilter(bmoRequisition.getCompanyId(), companyListBox);
		addFilter(bmoRequisition.getOrderId(), orderSuggestBox);
		addFilter(bmoRequisition.getCurrencyId(), currencyListBox);
		addFilter(bmoRequisition.getRequisitionTypeId(), requistionTypeListBox);
		addFilter(bmoRequisition.getStatus(), statusListBox);
		addFilter(bmoRequisition.getPaymentStatus(), paymentStatusListBox);
		addFilter(bmoRequisition.getDeliveryStatus(), deliveryStatusListBox);
		addFilter(deliveryStart, deliveryStartBox.getTextBox().getText());		
		addFilter(deliveryEnd, deliveryEndBox.getTextBox().getText());
		addFilter(requestStart, requestStartBox.getTextBox().getText());		
		addFilter(requestEnd, requestEndBox.getTextBox().getText());
		addFilter(dueStart, dueStartBox.getTextBox().getText());		
		addFilter(dueEnd, dueEndBox.getTextBox().getText());

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			addFilter(bmoBudgetItem.getBudgetId(), budgetSuggestBox);
			addFilter(bmoRequisition.getBudgetItemId(), budgetItemSuggestBox);
		}
	}

	private void statusEffect(){
		if (getSFParams().getSelectedCompanyId() > 0)
			companyListBox.setEnabled(false);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
