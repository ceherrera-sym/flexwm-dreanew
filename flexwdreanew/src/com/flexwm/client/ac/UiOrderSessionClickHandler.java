package com.flexwm.client.ac;


import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionSale;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public class UiOrderSessionClickHandler implements ClickHandler {
	int row, col;
	BmoSessionSale bmoSessionSale; 
	BmoSession bmoSession;
	BmoOrderSession bmoOrderSession;
	String sessionDate;
	Label label;
	
	public UiOrderSessionClickHandler(int row, int col, BmoSessionSale bmoSessionSale, BmoSession bmoSession, BmoOrderSession bmoOrderSession, String sessionDate, Label label) {
		this.row = row;
		this.col = col;
		this.bmoSessionSale = bmoSessionSale;
		this.bmoSession = bmoSession;
		this.bmoOrderSession = bmoOrderSession;
		this.sessionDate = sessionDate;
		this.label = label;
	}
	
	public UiOrderSessionClickHandler(int row, int col, BmoSessionSale bmoSessionSale, String sessionDate, Label label) {
		this.row = row;
		this.col = col;
		this.bmoSessionSale = bmoSessionSale;
		this.bmoSession = new BmoSession();
		this.bmoOrderSession = new BmoOrderSession();
		this.sessionDate = sessionDate;
		this.label = label;
	}

	@Override
	public void onClick(ClickEvent arg0) {
		// Debe ser sobrecargado
	}

}
