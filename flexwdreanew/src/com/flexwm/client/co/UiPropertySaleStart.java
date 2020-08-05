package com.flexwm.client.co;

import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowFormatDisplayList;
import com.flexwm.client.wf.UiWFlowUserLock;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowPhaseBar;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowFormat;
import com.flexwm.shared.wf.BmoWFlowFormatCompany;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowUser;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.sf.UiFormatDisplayList;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.UiProgramParams;
import com.symgae.shared.sf.BmoFormat;


public class UiPropertySaleStart extends Ui {
	private FlexTable flexTable = new FlexTable();
	private BmoPropertySale bmoPropertySale;

	public UiPropertySaleStart(UiParams uiParams, int programId, BmoPropertySale bmoPropertySale) {
		super(uiParams, new BmoPropertySale());
		this.bmoPropertySale = bmoPropertySale;
		flexTable.setSize("100%", "100%");
	}

	public void show() {
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();

		// Fases de la venta de inmueble
		FlowPanel wFlowPhaseFlowPanel = new FlowPanel();
		wFlowPhaseFlowPanel.setSize("100%", "40px");
		UiWFlowPhaseBar uiWFlowPhaseBar = new UiWFlowPhaseBar(getUiParams(), wFlowPhaseFlowPanel, bmoPropertySale.getWFlowId().toInteger());
		uiWFlowPhaseBar.show();
		flexTable.setWidget(0, 0, wFlowPhaseFlowPanel);

		// Colaboradores
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		FlowPanel wFlowUserFP = new FlowPanel();
		BmFilter filterWFlowUsers = new BmFilter();
		filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoPropertySale.getWFlowId().toInteger());
		getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
		UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoPropertySale.getBmoWFlow());
		setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowUserLock.show();
		flexTable.setWidget(1, 0, wFlowUserFP);

		// Documentos
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		FlowPanel wFlowDocumentFP = new FlowPanel();
		BmFilter filterWFlowDocuments = new BmFilter();
		filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoPropertySale.getWFlowId().toInteger());
		getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
		UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoPropertySale.getWFlowId().toInteger());
		setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowDocument.show();
		flexTable.setWidget(2, 0, wFlowDocumentFP);
		
		// Formatos Milti-empresa o Por programa
		if((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())) {			
			if (getUiParams().getSFParams().hasRead(new BmoWFlowFormat().getProgramCode())) {
				// formatos G100
				BmoWFlowFormatCompany bmoWFlowFormatCompany = new BmoWFlowFormatCompany();
				FlowPanel formatFP = new FlowPanel();
				UiWFlowFormatDisplayList uiWFlowFormatDisplayList = new UiWFlowFormatDisplayList(getUiParams(), formatFP, 
						bmoPropertySale, bmoPropertySale.getId());		
				BmoWFlowFormat bmoWFlowFormat = new BmoWFlowFormat();
				getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).clearFilters();
				getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).removeForceFilter();
				BmFilter filterFormats = new BmFilter();
				filterFormats.setInFilter(bmoWFlowFormatCompany.getKind(), 
						new BmoWFlowFormat().getIdFieldName(),
						bmoWFlowFormatCompany.getWflowformatId().getName(), 
						bmoWFlowFormatCompany.getCompanyId().getName(), bmoPropertySale.getCompanyId().toString());		
				getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).addFilter(filterFormats);			
				BmFilter filterType = new BmFilter();		
				filterType.setValueFilter(bmoWFlowFormat.getKind(), bmoWFlowFormat.getWflowTypeId(), bmoPropertySale.getWFlowTypeId().toInteger());
				getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).addFilter(filterType);		
				setUiType(bmoWFlowFormat.getProgramCode(), UiParams.MINIMALIST);
				uiWFlowFormatDisplayList.show();
				flexTable.setWidget(3, 0, formatFP);	
			}
		} else {	

			FlowPanel formatFP = new FlowPanel();
			UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoPropertySale, bmoPropertySale.getId());
			BmoFormat bmoFormat = new BmoFormat();
			UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
			BmFilter filterFormats = new BmFilter();
			filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
			uiProgramParams.setForceFilter(filterFormats);
			setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
			uiFormatDisplayList.show();
			flexTable.setWidget(3, 0, formatFP);
		}

		// Bitacora
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		FlowPanel WFlowLogFP = new FlowPanel();
		BmFilter filterWFlowLogs = new BmFilter();
		filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoPropertySale.getWFlowId().toInteger());
		getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
		UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoPropertySale.getWFlowId().toInteger(), -1);
		setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowLog.show();
		flexTable.setWidget(4, 0, WFlowLogFP);

		addToDP(flexTable);
	}
}
