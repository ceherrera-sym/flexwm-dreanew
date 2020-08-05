package com.flexwm.client.rpt;

import com.flexwm.shared.cm.BmoProject;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReportFilter;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.ItemSuggestion;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowType;

public class UiProjectSalesReport extends UiReportFilter {
	BmoProject bmoProject;
	
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
	public Hidden salesmanIdHidden = new Hidden("user_userid");
	DateBox startDateBox = new DateBox();
	DateBox endDateBox = new DateBox();
	UiListBox wflowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
	
	public UiProjectSalesReport(UiParams uiParams) {
		super(uiParams, new BmoProject(), "/rpt/cm/drea_sales.jsp", "Ventas por productor");
		this.bmoProject = (BmoProject)getBmObject();
		
		salesmanSuggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>(){
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				ItemSuggestion i = (ItemSuggestion)event.getSelectedItem();
				salesmanIdHidden.setValue("" + i.getId());
			} 
		});
	}

	@Override
	public void populateFields() {		
		formFlexTable.addField(1, 0, salesmanSuggestBox, bmoProject.getUserId());
		formFlexTable.addField(1, 2, wflowTypeListBox, bmoProject.getBmoWFlow().getWFlowTypeId());
		formFlexTable.setWidget(3, 0, salesmanIdHidden);
		formFlexTable.addField(2, 0, startDateBox, bmoProject.getStartDate());
		formFlexTable.addField(2, 2, endDateBox, bmoProject.getEndDate());
		
	}
}
