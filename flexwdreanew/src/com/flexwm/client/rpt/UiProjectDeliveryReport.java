package com.flexwm.client.rpt;

import com.flexwm.shared.cm.BmoProject;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReportFilter;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiProjectDeliveryReport extends UiReportFilter {
	BmoProject bmoProject;
		
	DateBox startDateBox = new DateBox();
	DateBox endDateBox = new DateBox();
	UiListBox wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
	
	public UiProjectDeliveryReport(UiParams uiParams) {
		super(uiParams, new BmoProject(), "/rpt/op/drea_deliveries.jsp", "Entregas");
		this.bmoProject = (BmoProject)getBmObject();
	}

	@Override
	public void populateFields() {
		formFlexTable.addField(1, 0, wflowTypeListBox, bmoProject.getBmoWFlow().getWFlowTypeId());		
		formFlexTable.addField(2, 0, startDateBox, bmoProject.getStartDate());
		formFlexTable.addField(2, 2, endDateBox, bmoProject.getEndDate());
		
	}
}
