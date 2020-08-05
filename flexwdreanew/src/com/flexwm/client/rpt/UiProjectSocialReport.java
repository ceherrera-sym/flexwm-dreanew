package com.flexwm.client.rpt;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoProject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoUser;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;


public class UiProjectSocialReport extends UiReport {

	BmoProject bmoProject;
	UiListBox reportTypeListBox;
	UiListBox areaListBox;
	UiListBox statusListBox;
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox endDateBox = new UiDateBox();
	UiListBox paymentStatusListBox;
	BmField paymentStatus;
	UiSuggestBox customersSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());

	String generalSection = "Filtros Generales";

	public static final char PAYMENTSTATUS_REVISION = 'R';
	public static final char PAYMENTSTATUS_PARTIAL = 'P';	
	public static final char PAYMENTSTATUS_TOTAL = 'T';

	public UiProjectSocialReport(UiParams uiParams) {
		super(uiParams, new BmoProject(), "/rpt/cm/proj_type_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoProject()) + " Sociales");
		this.bmoProject = (BmoProject)getBmObject();
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Calendario de Proyectos por Área", "/rpt/cm/proj_calendarbyarea_report.jsp");

		paymentStatus = new BmField("paymentStatus", "", "Estatus Pago", 10, Types.CHAR, false, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_REVISION, "Pendiente", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/racc_paymentstatus_revision.png")),
				new BmFieldOption(PAYMENTSTATUS_PARTIAL, "Pago Parcial", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/racc_paymentstatus_partialpayment.png")),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Pago Total", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/racc_paymentstatus_totalpayment.png"))			
				)));
		areaListBox = new UiListBox(getUiParams(), new BmoArea());
		statusListBox = new UiListBox(getUiParams());
		paymentStatusListBox = new UiListBox(getUiParams());

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, areaListBox, bmoProject.getBmoUser().getAreaId());	
		formFlexTable.addField(3, 0, salesmanSuggestBox, bmoProject.getUserId());
		formFlexTable.addField(4, 0, startDateBox, bmoProject.getStartDate());
		formFlexTable.addField(5, 0, endDateBox, bmoProject.getEndDate());
		formFlexTable.addField(6, 0, customersSuggestBox, bmoProject.getCustomerId());
		formFlexTable.addField(7, 0, paymentStatusListBox, paymentStatus, true);
		paymentStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(8, 0, statusListBox, bmoProject.getStatus(), true);
		statusListBox.setItemSelected(1, true);
		statusListBox.setItemSelected(2, true);
		
	}

	@Override
	public void setFilters(){
		addUrlFilter("programId", "" + bmObjectProgramId);
		addUrlFilter("isProjectSocial", "" + 1);
		addFilter(bmoProject.getStatus(), statusListBox);
		addFilter(bmoProject.getUserId(), salesmanSuggestBox);
		addFilter(bmoProject.getCustomerId(), customersSuggestBox);
		addFilter(bmoProject.getStartDate(), startDateBox.getTextBox().getText());
		addFilter(bmoProject.getEndDate(), endDateBox.getTextBox().getText());
		addFilter(bmoProject.getBmoUser().getAreaId(), areaListBox);
		addFilter(paymentStatus, paymentStatusListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}	
}
