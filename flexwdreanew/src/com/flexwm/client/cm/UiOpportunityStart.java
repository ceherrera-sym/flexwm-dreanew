package com.flexwm.client.cm;

import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowFormatDisplayList;
import com.flexwm.client.wf.UiWFlowUserLock;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowPhaseBar;
import com.flexwm.client.wf.UiWFlowTimeTrack;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowFormat;
import com.flexwm.shared.wf.BmoWFlowFormatCompany;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowTimeTrack;
import com.flexwm.shared.wf.BmoWFlowUser;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.sf.UiFormatDisplayList;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.UiProgramParams;
import com.symgae.shared.sf.BmoFormat;


public class UiOpportunityStart extends Ui {
	private FlexTable flexTable = new FlexTable();
	private BmoOpportunity bmoOpportunity;

	public UiOpportunityStart(UiParams uiParams, int programId, BmoOpportunity bmoOpportunity){
		super(uiParams, new BmoOpportunity());
		this.bmoOpportunity = bmoOpportunity;
		flexTable.setSize("100%", "100%");
	}

	public void show() {
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();

		// Fases de la oportunidad
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		if (getUiParams().getSFParams().hasRead(bmoWFlowPhase.getProgramCode())) {
			FlowPanel wFlowPhaseFlowPanel = new FlowPanel();
			wFlowPhaseFlowPanel.setSize("100%", "40px");
			UiWFlowPhaseBar uiWFlowPhaseBar = new UiWFlowPhaseBar(getUiParams(), wFlowPhaseFlowPanel, bmoOpportunity.getWFlowId().toInteger());
			uiWFlowPhaseBar.show();
			flexTable.setWidget(0, 0, wFlowPhaseFlowPanel);
		}

		// Colaboradores
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		if (getUiParams().getSFParams().hasRead(bmoWFlowUser.getProgramCode())) {
			FlowPanel wFlowUserFP = new FlowPanel();
			BmFilter filterWFlowUsers = new BmFilter();
			filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoOpportunity.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
			UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoOpportunity.getBmoWFlow());
			setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowUserLock.show();
			flexTable.setWidget(1, 0, wFlowUserFP);
		}

		// Documentos
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		if (getUiParams().getSFParams().hasRead(bmoWFlowDocument.getProgramCode())) {
			FlowPanel wFlowDocumentFP = new FlowPanel();
			BmFilter filterWFlowDocuments = new BmFilter();
			filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoOpportunity.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
			UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoOpportunity.getWFlowId().toInteger());
			setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowDocument.show();
			flexTable.setWidget(2, 0, wFlowDocumentFP);
		}
		// Formatos Milti-empresa o Por programa
		if((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())) {			
			if (getUiParams().getSFParams().hasRead(new BmoWFlowFormat().getProgramCode())) {
				// formatos G100
				BmoWFlowFormatCompany bmoWFlowFormatCompany = new BmoWFlowFormatCompany();
				FlowPanel formatFP = new FlowPanel();
				UiWFlowFormatDisplayList uiWFlowFormatDisplayList = new UiWFlowFormatDisplayList(getUiParams(), formatFP, 
						bmoOpportunity, bmoOpportunity.getId(), new UiOpportunityWflowFormatAction(getUiParams(), bmoOpportunity)
						,bmoOpportunity.getCompanyId().toInteger());		
				BmoWFlowFormat bmoWFlowFormat = new BmoWFlowFormat();
				getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).clearFilters();
				getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).removeForceFilter();
				BmFilter filterFormats = new BmFilter();
				filterFormats.setInFilter(bmoWFlowFormatCompany.getKind(), 
						new BmoWFlowFormat().getIdFieldName(),
						bmoWFlowFormatCompany.getWflowformatId().getName(), 
						bmoWFlowFormatCompany.getCompanyId().getName(), bmoOpportunity.getCompanyId().toString());		
				getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).addFilter(filterFormats);			
				BmFilter filterType = new BmFilter();		
				filterType.setValueFilter(bmoWFlowFormat.getKind(), bmoWFlowFormat.getWflowTypeId(), bmoOpportunity.getWFlowTypeId().toInteger());
				getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode()).addFilter(filterType);		
				setUiType(bmoWFlowFormat.getProgramCode(), UiParams.MINIMALIST);
				uiWFlowFormatDisplayList.show();
				flexTable.setWidget(3, 0, formatFP);	
			}
		} else {	
			// formatos normales
			BmoFormat bmoFormat = new BmoFormat();
			if (getUiParams().getSFParams().hasRead(bmoFormat.getProgramCode())) {
				FlowPanel formatFP = new FlowPanel();
				UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoOpportunity, bmoOpportunity.getId(), new UiOpportunityFormatAction(getUiParams(), bmoOpportunity));
				UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
				BmFilter filterFormats = new BmFilter();
				filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
				uiProgramParams.setForceFilter(filterFormats);
				setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
				uiFormatDisplayList.show();
				flexTable.setWidget(3, 0, formatFP);
			}
		}
	

		// Rastreo tiempo
		BmoWFlowTimeTrack bmoWFlowTimeTrack = new BmoWFlowTimeTrack();
		if (getUiParams().getSFParams().hasRead(bmoWFlowTimeTrack.getProgramCode())) {
			FlowPanel WFlowTimeTrackFP = new FlowPanel();
			BmFilter filterWFlowTimeTracks = new BmFilter();
			filterWFlowTimeTracks.setValueFilter(bmoWFlowTimeTrack.getKind(), bmoWFlowTimeTrack.getWFlowId(), bmoOpportunity.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowTimeTrack.getProgramCode(), filterWFlowTimeTracks);
			UiWFlowTimeTrack uiWFlowTimeTrack = new UiWFlowTimeTrack(getUiParams(), WFlowTimeTrackFP, bmoOpportunity.getBmoWFlow());
			setUiType(bmoWFlowTimeTrack.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowTimeTrack.show();
			flexTable.setWidget(4, 0, WFlowTimeTrackFP);
		}

		// Bitacora
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		if (getUiParams().getSFParams().hasRead(bmoWFlowLog.getProgramCode())) {
			FlowPanel WFlowLogFP = new FlowPanel();
			BmFilter filterWFlowLogs = new BmFilter();
			filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoOpportunity.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
			UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoOpportunity.getWFlowId().toInteger(), -1);
			setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowLog.show();
			flexTable.setWidget(5, 0, WFlowLogFP);
		}

		addToDP(flexTable);
	}
}
