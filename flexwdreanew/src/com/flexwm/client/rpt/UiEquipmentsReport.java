package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoEquipmentType;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoUser;


public class UiEquipmentsReport extends UiReport {

	BmoEquipment bmoEquipment;

	UiListBox reportTypeListBox;	
	UiListBox typeListBox = new UiListBox(getUiParams(), new BmoEquipmentType());
	UiSuggestBox userUiSuggestBox = new UiSuggestBox(new BmoUser());
	UiDateBox dateStartDateBox = new UiDateBox();
	UiDateBox dateEndDateBox = new UiDateBox();
	BmField dateStart;
	BmField dateEnd;

	String generalSection = "Filtros Generales";

	public UiEquipmentsReport(UiParams uiParams) {
		super(uiParams, new BmoEquipment(), "/rpt/op/equi_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoEquipment()));
		this.bmoEquipment = (BmoEquipment)getBmObject();
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("General de Recursos", "/rpt/op/equi_general_report.jsp");
		//reportTypeListBox.addItem("Utilización por Recursos", "/rpt/op/equi_byuse_report.jsp");
		reportTypeListBox.addItem("Consumo de Combustible", "/rpt/op/equi_consumefuel_report.jsp");
		reportTypeListBox.addItem("Por Mantenimiento", "/rpt/op/equi_maintenance_report.jsp");

		dateStart = new BmField("datestart", "", "Fecha Inicio", 20, Types.DATE, true, BmFieldType.DATE, false);
		dateEnd = new BmField("dateend", "", "Fecha Fin", 20, Types.DATE, true, BmFieldType.DATE, false);

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, typeListBox, bmoEquipment.getEquipmentTypeId());	
		formFlexTable.addField(3, 0, userUiSuggestBox, bmoEquipment.getUserId());
		formFlexTable.addField(4, 0, dateStartDateBox, dateStart);
		formFlexTable.addField(5, 0, dateEndDateBox, dateEnd);

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoEquipment.getEquipmentTypeId(), typeListBox);
		addFilter(bmoEquipment.getUserId(), userUiSuggestBox);
		addFilter(dateStart, dateStartDateBox.getTextBox().getText());
		addFilter(dateEnd, dateEndDateBox.getTextBox().getText());
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
