//package com.flexwm.client.cm;
//
//import com.flexwm.client.wf.UiWFlowDocument;
//import com.flexwm.client.wf.UiWFlowUserLock;
//import com.flexwm.client.wf.UiWFlowLog;
//import com.flexwm.client.wf.UiWFlowPhaseBar;
//import com.flexwm.client.wf.UiWFlowTimeTrack;
//import com.flexwm.shared.cm.BmoRFQU;
//import com.flexwm.shared.wf.BmoWFlowDocument;
//import com.flexwm.shared.wf.BmoWFlowLog;
//import com.flexwm.shared.wf.BmoWFlowPhase;
//import com.flexwm.shared.wf.BmoWFlowTimeTrack;
//import com.flexwm.shared.wf.BmoWFlowUser;
//import com.google.gwt.user.client.ui.FlexTable;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.symgae.client.ui.Ui;
//import com.symgae.client.ui.UiParams;
//import com.symgae.shared.BmFilter;
//
//
//public class UiRFQUStart extends Ui {
//	private FlexTable flexTable = new FlexTable();
//	private BmoRFQU bmoRFQU;
//
//	public UiRFQUStart(UiParams uiParams, int programId, BmoRFQU bmoRFQU){
//		super(uiParams, new BmoRFQU());
//		this.bmoRFQU= bmoRFQU;
//		flexTable.setSize("100%", "100%");
//	}
//
//	public void show() {
//		clearDP();
//		getUiParams().getUiTemplate().hideProgramButtonPanel();
//		getUiParams().getUiTemplate().hideProgramExtrasPanel();
//		getUiParams().getUiTemplate().hideEastPanel();
//		
//		
//		
////		BmoRFQU bmoRFQU = new BmoRFQU();
////		if (getUiParams().getSFParams().hasRead(bmoRFQU.getProgramCode())) {
////			FlowPanel RFQPanel = new FlowPanel();
////			RFQPanel.setSize("100%", "40px");
////			UiRFQU uiRFQU = new UiRFQU(getUiParams(), RFQPanel, bmoRFQU.getId());
////			uiRFQU.show();
////			flexTable.setWidget(0, 0, RFQPanel);
////		}
//
//		
//		
//		// Fases de la oportunidad
//		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
//		if (getUiParams().getSFParams().hasRead(bmoWFlowPhase.getProgramCode())) {
//			FlowPanel wFlowPhaseFlowPanel = new FlowPanel();
//			wFlowPhaseFlowPanel.setSize("100%", "40px");
//			UiWFlowPhaseBar uiWFlowPhaseBar = new UiWFlowPhaseBar(getUiParams(), wFlowPhaseFlowPanel, bmoRFQU.getwFlowId().toInteger());
//			uiWFlowPhaseBar.show();
//			flexTable.setWidget(0, 0, wFlowPhaseFlowPanel);
//		}
//
////		// Colaboradores
//		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
//		if (getUiParams().getSFParams().hasRead(bmoWFlowUser.getProgramCode())) {
//			FlowPanel wFlowUserFP = new FlowPanel();
//			BmFilter filterWFlowUsers = new BmFilter();
//			filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoRFQU.getwFlowId().toInteger());
//			getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
//			UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoRFQU.getBmoWFlow());
//			setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
//			uiWFlowUserLock.show();
//			flexTable.setWidget(1, 0, wFlowUserFP);
//		}
//
////		// Documentos
//		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
//		if (getUiParams().getSFParams().hasRead(bmoWFlowDocument.getProgramCode())) {
//			FlowPanel wFlowDocumentFP = new FlowPanel();
//			BmFilter filterWFlowDocuments = new BmFilter();
//			filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoRFQU.getwFlowId().toInteger());
//			getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
//			UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoRFQU.getwFlowId().toInteger());
//			setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
//			uiWFlowDocument.show();
//			flexTable.setWidget(2, 0, wFlowDocumentFP);
//		}
//
//////		// Formatos	
////		BmoFormat bmoFormat = new BmoFormat();
////		if (getUiParams().getSFParams().hasRead(bmoFormat.getProgramCode())) {
////			FlowPanel formatFP = new FlowPanel();
////			UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoRFQU, bmoRFQU.getId(), new UiOpportunityFormatAction(getUiParams(), bmoRFQU));
////			UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
////			BmFilter filterFormats = new BmFilter();
////			filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
////			uiProgramParams.setForceFilter(filterFormats);
////			setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
////			uiFormatDisplayList.show();
////			flexTable.setWidget(3, 0, formatFP);
////		}
////
////		// Rastreo tiempo
//		BmoWFlowTimeTrack bmoWFlowTimeTrack = new BmoWFlowTimeTrack();
//		if (getUiParams().getSFParams().hasRead(bmoWFlowTimeTrack.getProgramCode())) {
//			FlowPanel WFlowTimeTrackFP = new FlowPanel();
//			BmFilter filterWFlowTimeTracks = new BmFilter();
//			filterWFlowTimeTracks.setValueFilter(bmoWFlowTimeTrack.getKind(), bmoWFlowTimeTrack.getWFlowId(), bmoRFQU.getwFlowId().toInteger());
//			getUiParams().setForceFilter(bmoWFlowTimeTrack.getProgramCode(), filterWFlowTimeTracks);
//			UiWFlowTimeTrack uiWFlowTimeTrack = new UiWFlowTimeTrack(getUiParams(), WFlowTimeTrackFP, bmoRFQU.getBmoWFlow());
//			setUiType(bmoWFlowTimeTrack.getProgramCode(), UiParams.MINIMALIST);
//			uiWFlowTimeTrack.show();
//			flexTable.setWidget(4, 0, WFlowTimeTrackFP);
//		}
////
////		// Bitacora
//		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
//		if (getUiParams().getSFParams().hasRead(bmoWFlowLog.getProgramCode())) {
//			FlowPanel WFlowLogFP = new FlowPanel();
//			BmFilter filterWFlowLogs = new BmFilter();
//			filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoRFQU.getwFlowId().toInteger());
//			getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
//			UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoRFQU.getwFlowId().toInteger(), -1);
//			setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
//			uiWFlowLog.show();
//			flexTable.setWidget(5, 0, WFlowLogFP);
//		}
//
//		addToDP(flexTable);
//	}
//}
