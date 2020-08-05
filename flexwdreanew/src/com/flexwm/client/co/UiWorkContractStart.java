package com.flexwm.client.co;

import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowPhaseBar;
import com.flexwm.client.wf.UiWFlowUserLock;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowUser;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.sf.UiFormatDisplayList;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.UiProgramParams;
import com.symgae.shared.sf.BmoFormat;


public class UiWorkContractStart extends Ui {
	private FlexTable flexTable = new FlexTable();
	private BmoWorkContract bmoWorkContract;

	public UiWorkContractStart(UiParams uiParams, int programId, BmoWorkContract bmoWorkContract){
		super(uiParams, new BmoWorkContract());
		this.bmoWorkContract = bmoWorkContract;		
		flexTable.setSize("100%", "100%");
	}

	public void show() {
		clearDP();
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		if (getUiParams().getSFParams().hasRead(bmoWFlowPhase.getProgramCode())) {
			FlowPanel wFlowPhaseFlowPanel = new FlowPanel();
			wFlowPhaseFlowPanel.setSize("100%", "40px");
			UiWFlowPhaseBar uiWFlowPhaseBar = new UiWFlowPhaseBar(getUiParams(), wFlowPhaseFlowPanel, bmoWorkContract.getWFlowId().toInteger());
			uiWFlowPhaseBar.show();
			flexTable.setWidget(0, 0, wFlowPhaseFlowPanel);
		}
		// Colaboradores		
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		if (getUiParams().getSFParams().hasRead(bmoWFlowUser.getProgramCode())) {
			FlowPanel wFlowUserFP = new FlowPanel();
			BmFilter filterWFlowUsers = new BmFilter();
			filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoWorkContract.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
			UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoWorkContract.getBmoWFlow());
			setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowUserLock.show();
			flexTable.setWidget(1, 0, wFlowUserFP);
		}
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		if (getUiParams().getSFParams().hasRead(bmoWFlowDocument.getProgramCode())) {
			FlowPanel wFlowDocumentFP = new FlowPanel();
			BmFilter filterWFlowDocuments = new BmFilter();
			filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoWorkContract.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
			UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoWorkContract.getWFlowId().toInteger());
			setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowDocument.show();
			flexTable.setWidget(2, 0, wFlowDocumentFP);
		}
		// Foratos
		FlowPanel formatFP = new FlowPanel();
		UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoWorkContract, bmoWorkContract.getId());
		BmoFormat bmoFormat = new BmoFormat();
		UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
		BmFilter filterFormats = new BmFilter();
		filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
		uiProgramParams.setForceFilter(filterFormats);
		setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
		uiFormatDisplayList.show();
		flexTable.setWidget(3, 0, formatFP);
		
		
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		if (getUiParams().getSFParams().hasRead(bmoWFlowLog.getProgramCode())) {
			FlowPanel WFlowLogFP = new FlowPanel();
			BmFilter filterWFlowLogs = new BmFilter();
			filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoWorkContract.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
			UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoWorkContract.getWFlowId().toInteger(), -1);
			setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowLog.show();
			flexTable.setWidget(5, 0, WFlowLogFP);
		}

		

		//		// Archivos
		//		DecoratorPanel fileDP = new DecoratorPanel();
		//		fileDP.setSize("400px", "200px");
		//		FlowPanel fileFP = new FlowPanel();
		//		ScrollPanel fileSP = new ScrollPanel();
		//		fileSP.setSize("100%", "200px");
		//		fileSP.add(fileFP);
		//		fileDP.setWidget(fileSP);
		//		UiFileDisplayList uiFileDisplayList = new UiFileDisplayList(getUiParams(), fileFP, bmoWorkContract.getProgramCode(), bmObjectProgramId, bmoWorkContract.getId());
		//		setUiType(new BmoFile().getProgramCode(), UiParams.MINIMALIST);
		//		uiFileDisplayList.show();
		//		flexTable.setWidget(2, 0, fileFP);

		addToDP(flexTable);
	}
}
