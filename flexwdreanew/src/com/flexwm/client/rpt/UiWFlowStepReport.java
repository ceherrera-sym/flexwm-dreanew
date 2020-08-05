package com.flexwm.client.rpt;

import java.sql.Types;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiWFlowStepReport extends UiReport {	

	BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
	BmoDevelopment bmoDevelopment = new BmoDevelopment();
	BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
	BmoWFlowType bmoWFlowType = new BmoWFlowType();
	BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();	
	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	UiSuggestBox groupSuggestBox = new UiSuggestBox(new BmoProfile());
	UiSuggestBox developmentSuggestBox = new UiSuggestBox(new BmoDevelopment());
	UiSuggestBox developmentPhaseSuggestBox = new UiSuggestBox(new BmoDevelopmentPhase());
	UiListBox wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
	UiListBox wflowCategoryListBox = new UiListBox(getUiParams(), new BmoWFlowCategory());
	UiListBox wflowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase());
	UiListBox wflowFunnelListBox = new UiListBox(getUiParams(), new BmoWFlowFunnel());
	UiListBox enabledListBox = new UiListBox(getUiParams());
	UiListBox progressListBox = new UiListBox(getUiParams());
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox statusPropertyListBox = new UiListBox(getUiParams());
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox startEndDateBox = new UiDateBox();
	UiDateBox remindDateBox = new UiDateBox();
	UiDateBox remindDateEndBox = new UiDateBox();

	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
	BmField wflowTypeId;
	BmField userId;
	BmField developmentId;
	BmField developmentPhaseId;
	BmField startEndDate;
	BmField remindDateEnd;
	BmField remindDate;

	UiListBox reportTypeListBox;
	String generalSection = "Filtros Generales";
	String dateSection = "Filtros Fechas";
	String statusSection = "Filtros de Estatus";


	public UiWFlowStepReport(UiParams uiParams) {
		super(uiParams, new BmoWFlowStep(), "/rpt/wf/flex_wflowsteps_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoWFlowStep()));
		this.bmoWFlowStep = (BmoWFlowStep)getBmObject();
		
		wflowTypeId = new BmField("wflowtypeid", "", "Tipos de Flujo", 10, Types.INTEGER, false, BmFieldType.ID, false);
		userId = new BmField("userid", "", "Usuario", 10, Types.INTEGER, false, BmFieldType.ID, false);
		startEndDate = new BmField("startenddate", "", "Fecha Final", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		remindDate = new BmField("remindate","", "Recordar Inicio",10,Types.VARCHAR, false,BmFieldType.DATE,false);
		remindDateEnd = new BmField("remindateend","", "Recordar Fin",10,Types.VARCHAR, false,BmFieldType.DATE,false);
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte de Tareas de Flujos", "/rpt/wf/flex_wflowsteps_report.jsp");
		reportTypeListBox.addItem("Reporte de Tiempos por Fase", "/rpt/wf/flex_wflowphase_report.jsp");

		//Tarea Activa
		enabledListBox.addItem("Todos", "-1");
		enabledListBox.addItem("Si", "1");
		enabledListBox.addItem("No", "0");

		//Tarea Avance
		progressListBox.addItem("Todos", "-1");
		progressListBox.addItem("0%", "0");
		progressListBox.addItem("25%", "25");
		progressListBox.addItem("50%", "50");
		progressListBox.addItem("75%", "75");
		progressListBox.addItem("<100%", "99");
		progressListBox.addItem("100%", "100");

		formFlexTable.addSectionLabel(0, 0,  generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, wflowCategoryListBox, bmoWFlowStep.getBmoWFlowPhase().getWFlowCategoryId());
		formFlexTable.addField(3, 0, wflowTypeListBox, wflowTypeId);		
		formFlexTable.addField(4, 0, wflowPhaseListBox, bmoWFlowStep.getWFlowPhaseId());
		
		if (getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
			formFlexTable.addField(5, 0, wflowFunnelListBox, bmoWFlowStep.getWFlowFunnelId(), true);
		formFlexTable.addField(6, 0, groupSuggestBox, bmoWFlowStep.getProfileId());
		formFlexTable.addField(7, 0, userSuggestBox, userId);		
		formFlexTable.addField(8, 0, enabledListBox, bmoWFlowStep.getEnabled());
		formFlexTable.addField(9, 0, progressListBox, bmoWFlowStep.getProgress());
		formFlexTable.addField(10, 0, developmentSuggestBox, bmoDevelopment.getIdField());
		formFlexTable.addField(11, 0, developmentPhaseSuggestBox,bmoDevelopmentPhase.getIdField());
		formFlexTable.addSectionLabel(12, 0,  dateSection, 2);
		formFlexTable.addField(13, 0, startDateBox, bmoWFlowStep.getStartdate());
		formFlexTable.addField(14, 0, startEndDateBox, startEndDate);
		formFlexTable.addField(15, 0, remindDateBox, remindDate);
		formFlexTable.addField(16, 0, remindDateEndBox, remindDateEnd);
		
		formFlexTable.addSectionLabel(17, 0,  statusSection, 2);
		formFlexTable.addField(18, 0, statusListBox, bmoWFlowStep.getStatus());
		formFlexTable.addField(19, 0, statusPropertyListBox, bmoPropertySale.getStatus());

		formFlexTable.hideSection(statusSection);
		formFlexTable.hideSection(dateSection);
		formFlexTable.hideField(statusPropertyListBox);
		formFlexTable.hideField(developmentPhaseSuggestBox);
		formFlexTable.hideField(developmentSuggestBox);
		statusPropertyListBox.setItemSelected(0, true);
		
		}
	
	@Override
	public void formListChange(ChangeEvent event) {
		BmFilter bmFilter = new BmFilter();
		if(event.getSource()==reportTypeListBox && reportTypeListBox.getSelectedCode() == "/rpt/wf/flex_wflowphase_report.jsp") {
			formFlexTable.hideField(wflowFunnelListBox);
			formFlexTable.hideField(groupSuggestBox);
			formFlexTable.hideField(userSuggestBox);
			formFlexTable.hideField(remindDateBox);
			formFlexTable.hideField(remindDateEndBox);
			formFlexTable.showField(statusPropertyListBox);
			formFlexTable.showField(developmentSuggestBox);
			formFlexTable.showField(developmentPhaseSuggestBox);

			
		}
		if(event.getSource()==reportTypeListBox && reportTypeListBox.getSelectedCode() == "/rpt/wf/flex_wflowsteps_report.jsp") {
			formFlexTable.showField(wflowTypeListBox);
			formFlexTable.showField(wflowFunnelListBox);
			formFlexTable.showField(groupSuggestBox);
			formFlexTable.showField(userSuggestBox);
			formFlexTable.showField(remindDateBox);
			formFlexTable.showField(remindDateEndBox);	
			formFlexTable.hideField(statusPropertyListBox);

		}
		//Mostrar las categorias ligadas al tipo
		if (event.getSource() == wflowCategoryListBox) {
			wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getWFlowCategoryId(), wflowCategoryListBox.getSelectedId());
			wflowTypeListBox.addBmFilter(bmFilter);
			formFlexTable.addField(3, 0, wflowTypeListBox, wflowTypeId);
		}	 

		if (event.getSource() == wflowCategoryListBox) {
			wflowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase());
			bmFilter.setValueFilter(bmoWFlowPhase.getKind(), bmoWFlowPhase.getWFlowCategoryId(), wflowCategoryListBox.getSelectedId());
			wflowPhaseListBox.addBmFilter(bmFilter);
			formFlexTable.addField(4, 0, wflowPhaseListBox, bmoWFlowStep.getWFlowPhaseId());
		}
		
		formValueChange("");
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoWFlowStep.getBmoWFlowPhase().getWFlowCategoryId(), wflowCategoryListBox);		
		addFilter(bmoWFlowStep.getWFlowPhaseId(), wflowPhaseListBox);
		if (getSFParams().isFieldEnabled(bmoWFlowStep.getWFlowFunnelId()))
			addFilter(bmoWFlowStep.getWFlowFunnelId(), wflowFunnelListBox);
		addFilter(wflowTypeId, wflowTypeListBox);
		addFilter(bmoWFlowStep.getProfileId(), groupSuggestBox);
		addFilter(userId, userSuggestBox);
		addFilter(bmoWFlowStep.getEnabled(), enabledListBox);
		addFilter(bmoWFlowStep.getProgress(), progressListBox);
		addFilter(bmoWFlowStep.getStartdate(), startDateBox.getTextBox().getText());
		addFilter(startEndDate, startEndDateBox.getTextBox().getText());
		addFilter(remindDate, remindDateBox.getTextBox().getText());
		addFilter(remindDateEnd, remindDateEndBox.getTextBox().getText());
		addFilter(bmoWFlowStep.getStatus(),statusListBox);
		addFilter(bmoPropertySale.getStatus(),statusPropertyListBox);
		addFilter(bmoDevelopment.getIdField() ,developmentSuggestBox);
		addFilter(bmoDevelopmentPhase.getIdField(),developmentPhaseSuggestBox);
		
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
