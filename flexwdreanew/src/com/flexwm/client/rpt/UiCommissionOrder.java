package com.flexwm.client.rpt;

import java.sql.Types;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoCommission;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoUser;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;


public class UiCommissionOrder extends UiReport {

	BmoOrder bmoOrder;
	UiListBox statusListBox = new UiListBox(getUiParams());	
	UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
	UiDateBox lockStartBox = new UiDateBox();
	UiDateBox lockEndBox = new UiDateBox();
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	BmField userId;

	String generalSection = "Filtros Generales";
	UiListBox reportTypeListBox;

	public UiCommissionOrder(UiParams uiParams) {
		super(uiParams, new BmoOrder(), "/rpt/op/orde_commission_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoCommission()));
		this.bmoOrder = (BmoOrder)getBmObject();
		
		userId = new BmField("userid", "", "Usuario", 10, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());		
		reportTypeListBox.addItem("Reporte Comisiones", "/rpt/op/orde_commission_report.jsp");

		try {
			bmoOrder.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		} catch (BmException e) {
			showSystemMessage("No se puede asignar moneda : " + e.toString());
		}

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, orderSuggestBox, bmoOrder.getIdField());
		formFlexTable.addField(3, 0, userSuggestBox, userId);
		formFlexTable.addField(4, 0, lockStartBox, bmoOrder.getLockStart());
		formFlexTable.addField(5, 0, lockEndBox, bmoOrder.getLockEnd());
		formFlexTable.addField(6, 0, currencyListBox, bmoOrder.getCurrencyId());
		formFlexTable.addField(7, 0, statusListBox, bmoOrder.getStatus(), true);
		statusListBox.setSelectedIndex(-1);
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoOrder.getIdField(), orderSuggestBox);
		addFilter(bmoOrder.getCurrencyId(), currencyListBox);
		addFilter(bmoOrder.getLockStart(), lockStartBox.getTextBox().getText());		
		addFilter(bmoOrder.getLockEnd(), lockEndBox.getTextBox().getText());
		addFilter(bmoOrder.getStatus(), statusListBox);
		addFilter(userId, userSuggestBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
