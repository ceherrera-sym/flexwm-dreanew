package com.flexwm.client.co;

import com.flexwm.shared.co.BmoWork;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.sf.UiFormatDisplayList;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.UiProgramParams;
import com.symgae.shared.sf.BmoFormat;


public class UiWorkStart extends Ui {
	private FlexTable flexTable = new FlexTable();
	private BmoWork bmoWork;	

	public UiWorkStart(UiParams uiParams, BmoWork bmoWork){
		super(uiParams, new BmoWork());
		this.bmoWork = bmoWork;				
		flexTable.setSize("100%", "100%");
	}

	public void show() {
		clearDP();

		//Formatos
		FlowPanel formatFP = new FlowPanel();
		UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoWork, bmoWork.getId(), new UiWorkFormatAction(getUiParams(), bmoWork));
		BmoFormat bmoFormat = new BmoFormat();
		UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
		BmFilter filterFormats = new BmFilter();
		filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
		uiProgramParams.setForceFilter(filterFormats);
		setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
		uiFormatDisplayList.show();
		flexTable.setWidget(1, 0, formatFP);

		addToDP(flexTable);	
	}
}
