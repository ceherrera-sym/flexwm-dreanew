package com.flexwm.client.rpt;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.fi.BmoCurrency;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.symgae.client.chart.UiChart;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiProjectReport extends UiReport {

	BmoProject bmoProject;
	UiSuggestBox projectSuggestBox = new UiSuggestBox(new BmoProject());
	UiListBox reportTypeListBox;
	UiListBox wflowPhaseListBox;
	UiListBox wflowTypeListBox;
	UiListBox wflowCategoryListBox;
	UiListBox refeListBox;
	UiListBox areaListBox;
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox endDateBox = new UiDateBox();
	UiSuggestBox venueSuggestBox = new UiSuggestBox(new BmoVenue());
	UiSuggestBox customersSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	UiChart uiChart;
	UiDateBox startDateCreateProjectDateBox = new UiDateBox();
	BmField startDateCreateProject;
	UiDateBox endDateCreateProjectDateBox = new UiDateBox();
	BmField endDateCreateProject;
	UiDateBox startDueDateDateBox = new UiDateBox();
	BmField startDueDate;
	UiDateBox endDueDateDateBox = new UiDateBox();
	BmField endDueDate;
	BmoWFlowType bmoWFlowType;
	BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();	
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
	BmField paymentStatus;

	String generalSection = "Filtros Generales";
	String datesSection = "Filtros de Fechas";
	String commissionSection = "Filtros Comisiones";

	public static final char PAYMENTSTATUS_REVISION = 'R';
	public static final char PAYMENTSTATUS_PARTIAL = 'P';	
	public static final char PAYMENTSTATUS_TOTAL = 'T';

	public UiProjectReport(UiParams uiParams) {
		super(uiParams, new BmoProject(), "/rpt/cm/proj_type_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoProject()));
		this.bmoProject = (BmoProject)getBmObject();
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Por Tipo de Flujo", "/rpt/cm/proj_type_report.jsp");
		reportTypeListBox.addItem("Por Fase de Flujo", "/rpt/cm/proj_phase_report.jsp");
		reportTypeListBox.addItem("Por Cliente", "/rpt/cm/proj_customer_report.jsp");
		reportTypeListBox.addItem("Por Vendedor", "/rpt/cm/proj_user_report.jsp");
		reportTypeListBox.addItem("Por Encuesta", "/rpt/cm/proj_customerpoll_report.jsp");
		reportTypeListBox.addItem("Calendario de Proyectos por Area", "/rpt/cm/proj_calendarbyarea_report.jsp");
		reportTypeListBox.addItem("Por Flujo de Efectivo", "/rpt/cm/proj_flow_report.jsp");
		reportTypeListBox.addItem("Utilidad de Proyectos", "/rpt/cm/proj_utilities_report.jsp");
		reportTypeListBox.addItem("Proyectos por Proveedor", "/rpt/cm/proj_supplier_report.jsp");
		reportTypeListBox.addItem("OC por Proyecto", "/rpt/cm/proj_requisitions_report.jsp");
		//reportTypeListBox.addItem("Comisión Ejecutivo", "/rpt/cm/proj_commission_salesman_report.jsp");

		refeListBox = new UiListBox(getUiParams(), new BmoReferral());
		areaListBox = new UiListBox(getUiParams(), new BmoArea());

		startDateCreateProject = new BmField("startdatecreateproject", "", "Fecha en Sistema", 20, Types.DATE, true, BmFieldType.DATE, false);
		endDateCreateProject = new BmField("enddatecreateproject", "", "Fecha en Sistema Fin", 20, Types.DATE, true, BmFieldType.DATE, false);
		startDueDate = new BmField("startduedate", "", "Fecha Pago", 20, Types.DATE, true, BmFieldType.DATE, false);
		endDueDate = new BmField("endduedate", "", "Fecha Pago Fin", 20, Types.DATE, true, BmFieldType.DATE, false);
		endDueDate = new BmField("endduedate", "", "Fecha Pago Fin", 20, Types.DATE, true, BmFieldType.DATE, false);
		paymentStatus = new BmField("paymentStatus", "", "Estatus Pago", 10, Types.CHAR, false, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_REVISION, "Pendiente", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/paymentstatus_revision.png")),
				new BmFieldOption(PAYMENTSTATUS_PARTIAL, "Pago Parcial", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/paymentstatus_partial.png")),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Pago Total", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/paymentstatus_total.png"))				
				)));

		try {
			bmoProject.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		} catch (BmException e) {
			showSystemMessage("No se puede asignar moneda : " + e.toString());
		}

		// Filtros en los listbox

		// Categoria de flujos
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		BmFilter filterWFlowCategory= new BmFilter();
		filterWFlowCategory.setValueFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), bmObjectProgramId);		
		wflowCategoryListBox = new UiListBox(getUiParams(), new BmoWFlowCategory(), filterWFlowCategory);

		// Tipo de flujo
		bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType);

		// Fase de flujo
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		BmFilter filterWFlowPhase = new BmFilter();
		filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
				bmoWFlowPhase.getWFlowCategoryId().getName(), 
				bmoWFlowCategory.getIdFieldName(),
				bmoWFlowCategory.getProgramId().getName(),
				"" + bmObjectProgramId);
		wflowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase);

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, wflowCategoryListBox, bmoProject.getBmoWFlow().getBmoWFlowType().getWFlowCategoryId());	
		formFlexTable.addField(3, 0, wflowTypeListBox, bmoProject.getBmoWFlow().getWFlowTypeId());
		formFlexTable.addField(4, 0, wflowPhaseListBox, bmoProject.getBmoWFlow().getWFlowPhaseId());
		formFlexTable.addField(5, 0, projectSuggestBox, bmoProject.getIdField());
		formFlexTable.addField(6, 0, salesmanSuggestBox, bmoProject.getUserId());
		formFlexTable.addField(7, 0, customersSuggestBox, bmoProject.getCustomerId());
		formFlexTable.addField(8, 0, areaListBox, bmoProject.getBmoUser().getAreaId());	
		formFlexTable.addField(9, 0, refeListBox, bmoProject.getBmoCustomer().getReferralId());
		formFlexTable.addField(10, 0, venueSuggestBox, bmoProject.getVenueId());
		formFlexTable.addField(11, 0, currencyListBox, bmoProject.getCurrencyId());
		formFlexTable.addField(12, 0, paymentStatusListBox, paymentStatus, true);
		paymentStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(13, 0, statusListBox, bmoProject.getStatus(), true);
		statusListBox.setSelectedIndex(-1);

		formFlexTable.addSectionLabel(14,  0, datesSection, 2);
		formFlexTable.addField(15, 0, startDateBox, bmoProject.getStartDate());
		formFlexTable.addField(16, 0, endDateBox, bmoProject.getEndDate());
		formFlexTable.addField(17, 0, startDateCreateProjectDateBox, startDateCreateProject);
		formFlexTable.addField(18, 0, endDateCreateProjectDateBox, endDateCreateProject);

		formFlexTable.addSectionLabel(19,  0,  commissionSection, 2);
		formFlexTable.addField(20, 0, startDueDateDateBox, startDueDate);
		formFlexTable.addField(21, 0, endDueDateDateBox, endDueDate);

		//		formFlexTable.hideSection(generalSection);
		formFlexTable.hideSection(datesSection);
		formFlexTable.hideSection(commissionSection);
	}

	@Override
	public void formListChange(ChangeEvent event) {
		//Mostrar las categorias ligadas al tipo
		if (event.getSource() == wflowCategoryListBox) {
			populateWFlowType(Integer.parseInt(wflowCategoryListBox.getSelectedId()));

			populateWFlowPhase(Integer.parseInt(wflowCategoryListBox.getSelectedId()));
		}	 

		//Se pierde la url, se manda a llamar de nuevo
		formValueChange("");
	}

	// Actualiza combo de tipos de flujo
	private void populateWFlowType(int wflowCategoryId) {
		wflowTypeListBox.clear();
		wflowTypeListBox.clearFilters();
		setWFlowTypeListBoxFilters(wflowCategoryId);
		formFlexTable.addField(3, 0, wflowTypeListBox, bmoProject.getWFlowTypeId());
		//wflowTypeListBox.populate(bmoProject.getBmoWFlowType().getName());
	}

	// Asigna filtros al listado tipos de flujo
	private void setWFlowTypeListBoxFilters(int wflowCategoryId) {
		// Tipo de flujo
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter bmFilter = new BmFilter();
		//wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
		bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getWFlowCategoryId(), wflowCategoryListBox.getSelectedId());
		wflowTypeListBox.addBmFilter(bmFilter);
	}

	// Actualiza combo de fases del flujo
	private void populateWFlowPhase(int wflowCategoryId) {
		wflowPhaseListBox.clear();
		wflowPhaseListBox.clearFilters();
		setWFlowPhaseListBoxFilters(wflowCategoryId);
		formFlexTable.addField(4, 0, wflowPhaseListBox, bmoProject.getBmoWFlow().getWFlowPhaseId());
		//wflowTypeListBox.populate(bmoProject.getBmoWFlowType().getName());
	}

	// Asigna filtros al listado de fases del flujo
	private void setWFlowPhaseListBoxFilters(int wflowCategoryId) {
		// Tipo de flujo
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		BmFilter bmFilter = new BmFilter();
		//wflowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowType());
		bmFilter.setValueFilter(bmoWFlowPhase.getKind(), bmoWFlowPhase.getWFlowCategoryId(), wflowCategoryListBox.getSelectedId());
		wflowPhaseListBox.addBmFilter(bmFilter);
	}

	@Override
	public void setFilters(){
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoProject.getBmoWFlowType().getWFlowCategoryId(), wflowCategoryListBox);
		addFilter(bmoProject.getBmoWFlow().getWFlowTypeId(), wflowTypeListBox);
		addFilter(bmoProject.getBmoWFlow().getWFlowPhaseId(), wflowPhaseListBox);
		addFilter(bmoProject.getIdField(), projectSuggestBox);
		addFilter(bmoProject.getVenueId(), venueSuggestBox);
		addFilter(bmoProject.getStatus(), statusListBox);
		addFilter(bmoProject.getUserId(), salesmanSuggestBox);
		addFilter(bmoProject.getCustomerId(), customersSuggestBox);
		addFilter(bmoProject.getStartDate(), startDateBox.getTextBox().getText());
		addFilter(bmoProject.getEndDate(), endDateBox.getTextBox().getText());
		addFilter(startDateCreateProject, startDateCreateProjectDateBox.getTextBox().getText());
		addFilter(endDateCreateProject, endDateCreateProjectDateBox.getTextBox().getText());
		addFilter(bmoProject.getBmoCustomer().getReferralId(), refeListBox);
		addFilter(bmoProject.getBmoUser().getAreaId(), areaListBox);
		addFilter(startDueDate, startDueDateDateBox.getTextBox().getText());
		addFilter(endDueDate, endDueDateDateBox.getTextBox().getText());
		addFilter(paymentStatus, paymentStatusListBox);
		addFilter(bmoProject.getCurrencyId(), currencyListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}	
}
