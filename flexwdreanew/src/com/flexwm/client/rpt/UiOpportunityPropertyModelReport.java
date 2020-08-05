package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoPropertyModel;
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
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiOpportunityPropertyModelReport extends UiReport {
	BmoOpportunity bmoOpportunity;
	UiListBox reportTypeListBox;
	UiListBox wflowPhaseListBox;
	UiListBox wflowTypeListBox;
	UiListBox refeListBox;
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiDateBox startDateDateBox = new UiDateBox();
	//DateBox startDateEndDateBox = new DateBox();
	//BmField startDateEnd;
	UiDateBox endDateDateBox = new UiDateBox();
	//DateBox endDateEndDateBox = new DateBox();
	//BmField endDateEnd;
	UiSuggestBox customersSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());

	UiSuggestBox developmentSuggestBox = new UiSuggestBox(new BmoDevelopment());
	UiSuggestBox propertyModelSuggestBox = new UiSuggestBox(new BmoPropertyModel());
	BmField development;
	UiListBox profileListBox;
	BmField profileId;

	String generalSection = "Filtros Generales";

	public UiOpportunityPropertyModelReport(UiParams uiParams) {
		super(uiParams, new BmoOpportunity(), "/rpt/cm/oppo_type_propertymodel_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoOpportunity()));
		this.bmoOpportunity = (BmoOpportunity)getBmObject();

		development = new BmField("development", "", "Desarrollo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		//startDateEnd = new BmField("startDateEnd", "", "Inicio Termino", 20, Types.DATE, true, BmFieldType.DATE, false);
		//endDateEnd = new BmField("endDateEnd", "", "Fin Termino", 20, Types.DATE, true, BmFieldType.DATE, false);
		profileId = new BmField("profileid", "", "Perfil", 10, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public void populateFields() {
		refeListBox = new UiListBox(getUiParams(), new BmoReferral());
		profileListBox = new UiListBox(getUiParams(), new BmoProfile());

		// Filtros en los listbox
		// Tipo de flujo
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType);

		// Fase de flujo
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		BmFilter filterWFlowPhase = new BmFilter();
		filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
				bmoWFlowPhase.getWFlowCategoryId().getName(), 
				bmoWFlowCategory.getIdFieldName(),
				bmoWFlowCategory.getProgramId().getName(),
				"" + bmObjectProgramId);
		wflowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase);

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Por Tipo de Flujo", "/rpt/cm/oppo_type_propertymodel_report.jsp");
		reportTypeListBox.addItem("Por Fase de Flujo", "/rpt/cm/oppo_phase_propertymodel_report.jsp");
		reportTypeListBox.addItem("Por Vendedor", "/rpt/cm/oppo_user_propertymodel_report.jsp");
		reportTypeListBox.addItem("Por Cliente", "/rpt/cm/oppo_customer_propertymodel_report.jsp");
		reportTypeListBox.addItem("Por Estatus", "/rpt/cm/oppo_status_propertymodel_report.jsp");


		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, wflowTypeListBox, bmoOpportunity.getBmoWFlow().getWFlowTypeId());
		formFlexTable.addField(3, 0, wflowPhaseListBox, bmoOpportunity.getBmoWFlow().getWFlowPhaseId());
		formFlexTable.addField(4, 0, propertyModelSuggestBox, bmoOpportunity.getPropertyModelId());
		formFlexTable.addField(5, 0, developmentSuggestBox, development);
		formFlexTable.addField(6, 0, salesmanSuggestBox, bmoOpportunity.getUserId());
		formFlexTable.addField(7, 0, customersSuggestBox, bmoOpportunity.getCustomerId());
		refeListBox.setSelectedIndex(-1);
		formFlexTable.addField(8, 0, startDateDateBox, bmoOpportunity.getStartDate());
		//formFlexTable.addField(4, 0, startDateEndDateBox, startDateEnd);
		formFlexTable.addField(9, 0, endDateDateBox, bmoOpportunity.getEndDate());
		//formFlexTable.addField(5, 0, endDateEndDateBox, endDateEnd);
		formFlexTable.addField(10, 0, refeListBox, bmoOpportunity.getBmoCustomer().getReferralId(), true);
		formFlexTable.addField(11, 0, profileListBox, profileId);
		formFlexTable.addField(12, 0, statusListBox, bmoOpportunity.getStatus(), true);
		statusListBox.setSelectedIndex(-1);

	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoOpportunity.getBmoWFlow().getWFlowTypeId(), wflowTypeListBox);
		addFilter(bmoOpportunity.getBmoWFlow().getWFlowPhaseId(), wflowPhaseListBox);
		addFilter(bmoOpportunity.getStatus(), statusListBox);
		addFilter(bmoOpportunity.getUserId(), salesmanSuggestBox);
		addFilter(bmoOpportunity.getCustomerId(), customersSuggestBox);
		addFilter(bmoOpportunity.getStartDate(), startDateDateBox.getTextBox().getText());
		//addFilter(startDateEnd, startDateEndDateBox.getTextBox().getText());
		addFilter(bmoOpportunity.getEndDate(), endDateDateBox.getTextBox().getText());
		//addFilter(endDateEnd, endDateEndDateBox.getTextBox().getText());
		addFilter(bmoOpportunity.getBmoCustomer().getReferralId(), refeListBox);
		addFilter(bmoOpportunity.getPropertyModelId(), propertyModelSuggestBox);
		addFilter(development, developmentSuggestBox);
		addFilter(profileId, profileListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
