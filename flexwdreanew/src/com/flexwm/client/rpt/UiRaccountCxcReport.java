package com.flexwm.client.rpt;

import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoRequisition;
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
import com.symgae.shared.sf.BmoCompany;


public class UiRaccountCxcReport extends UiReportFilter {
	BmoRaccount bmoRaccount;
	
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	public Hidden customerIdHidden = new Hidden("cust_customerid");
	UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoRequisition());
	public Hidden salesmanIdHidden = new Hidden("user_userid");
	DateBox startDateBox = new DateBox();
	DateBox endDateBox = new DateBox();
	UiListBox companiesListBox = new UiListBox(getUiParams(), new BmoCompany());
	
	public UiRaccountCxcReport(UiParams uiParams) {
		super(uiParams, new BmoRaccount(), "/rpt/fi/drea_cxc.jsp", "Cuentas por Cobrar");
		this.bmoRaccount = (BmoRaccount)getBmObject();
		
		customerSuggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>(){
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				ItemSuggestion i = (ItemSuggestion)event.getSelectedItem();
				customerIdHidden.setValue("" + i.getId());
			} 
		});
		
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
		formFlexTable.addField(1, 0, customerSuggestBox, bmoRaccount.getCustomerId());
		formFlexTable.addField(1, 2, salesmanSuggestBox, bmoRaccount.getUserId());
		formFlexTable.addField(2, 0, companiesListBox, bmoRaccount.getCompanyId());
		formFlexTable.setWidget(4, 0, customerIdHidden);
		formFlexTable.setWidget(4, 1, salesmanIdHidden);
		formFlexTable.addField(3, 0, startDateBox, bmoRaccount.getDueDate());
		formFlexTable.addField(3, 2, endDateBox, bmoRaccount.getDueDate());
		
	}
}
