package com.flexwm.client.cr;

import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowPhaseBar;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowUser;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.sf.UiFormatDisplayList;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowUserLock;
import com.symgae.shared.BmFilter;
import com.symgae.shared.UiProgramParams;
import com.symgae.shared.sf.BmoFormat;


public class UiCreditStart extends Ui {
	private FlexTable flexTable = new FlexTable();
	private BmoCredit bmoCredit;

	public UiCreditStart(UiParams uiParams, int programId, BmoCredit bmoCredit) {
		super(uiParams, new BmoCredit());
		this.bmoCredit = bmoCredit;		
		flexTable.setSize("100%", "100%");
	}

	@Override
	public void show() {
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();

		// Fases del credito
		FlowPanel wFlowPhaseFlowPanel = new FlowPanel();
		wFlowPhaseFlowPanel.setSize("100%", "40px");
		UiWFlowPhaseBar uiWFlowPhaseBar = new UiWFlowPhaseBar(getUiParams(), wFlowPhaseFlowPanel, bmoCredit.getWFlowId().toInteger());
		uiWFlowPhaseBar.show();
		flexTable.setWidget(0, 0, wFlowPhaseFlowPanel);

		// Colaboradores
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		FlowPanel wFlowUserFP = new FlowPanel();
		BmFilter filterWFlowUsers = new BmFilter();
		filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoCredit.getWFlowId().toInteger());
		getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
		UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoCredit.getBmoWFlow());
		setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowUserLock.show();
		flexTable.setWidget(1, 0, wFlowUserFP);

		// Avales
		//		FlowPanel custGuareenteDP = new FlowPanel();
		//		custGuareenteDP.setWidth("100%");
		//		FlowPanel custGuareenteFP = new FlowPanel();
		//		custGuareenteDP.add(custGuareenteFP);
		//		UiCreditGuarantee uiCreditGuaranteeForm = new UiCreditGuarantee(getUiParams(), custGuareenteFP, bmoCredit.getId());		
		//		uiCreditGuaranteeForm.show();
		//		flexTable.setWidget(2, 0, custGuareenteDP);

		// Documentos
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		FlowPanel wFlowDocumentFP = new FlowPanel();
		BmFilter filterWFlowDocuments = new BmFilter();
		filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoCredit.getWFlowId().toInteger());
		getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
		UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoCredit.getWFlowId().toInteger());
		setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowDocument.show();
		flexTable.setWidget(3, 0, wFlowDocumentFP);

		// Formatos
		FlowPanel formatFP = new FlowPanel();
		UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoCredit, bmoCredit.getId());
		BmoFormat bmoFormat = new BmoFormat();
		UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
		BmFilter filterFormats = new BmFilter();
		filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
		uiProgramParams.setForceFilter(filterFormats);
		setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
		uiFormatDisplayList.show();
		flexTable.setWidget(4, 0, formatFP);

		// Bitacora
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		FlowPanel WFlowLogFP = new FlowPanel();
		BmFilter filterWFlowLogs = new BmFilter();
		filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoCredit.getWFlowId().toInteger());
		getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
		UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoCredit.getWFlowId().toInteger(), -1);
		setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowLog.show();
		flexTable.setWidget(4, 0, WFlowLogFP);

		addToDP(flexTable);
	}

}
