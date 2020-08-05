package com.flexwm.client.co;

import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowUserLock;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowPhaseBar;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.sf.UiFormatDisplayList;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.UiProgramParams;
import com.symgae.shared.sf.BmoFormat;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowUser;


public class UiDevelopmentPhaseStart extends Ui {

	private FlexTable flexTable = new FlexTable();
	private BmoDevelopmentPhase bmoDevelopmentPhase;
	BmoWFlow bmoWFlow = new BmoWFlow();


	public UiDevelopmentPhaseStart(UiParams uiParams, int programId, BmoDevelopmentPhase bmoDevelopmentPhase) {
		super(uiParams, new BmoDevelopmentPhase());
		this.bmoDevelopmentPhase = bmoDevelopmentPhase;		
		flexTable.setSize("100%", "100%");
	}

	public void show() {
		getBmoWFlow();
	}

	public void getBmoWFlow() {


		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				stopLoading();
				render((BmoWFlow)result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoWFlow.getPmClass(), bmoDevelopmentPhase.getWFlowId().toInteger(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
		}
	}

	private void render(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();

		// Fases de la Etapa Desarrollo
		FlowPanel wFlowPhaseFlowPanel = new FlowPanel();
		wFlowPhaseFlowPanel.setSize("100%", "40px");
		UiWFlowPhaseBar uiWFlowPhaseBar = new UiWFlowPhaseBar(getUiParams(), wFlowPhaseFlowPanel, bmoDevelopmentPhase.getWFlowId().toInteger());
		uiWFlowPhaseBar.show();
		flexTable.setWidget(0, 0, wFlowPhaseFlowPanel);

		// Colaboradores
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		FlowPanel wFlowUserFP = new FlowPanel();
		BmFilter filterWFlowUsers = new BmFilter();
		filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoDevelopmentPhase.getWFlowId().toInteger());
		getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
		UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoWFlow);
		setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowUserLock.show();
		flexTable.setWidget(1, 0, wFlowUserFP);


		// Documentos
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		FlowPanel wFlowDocumentFP = new FlowPanel();
		BmFilter filterWFlowDocuments = new BmFilter();
		filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoDevelopmentPhase.getWFlowId().toInteger());
		getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
		UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoDevelopmentPhase.getWFlowId().toInteger());
		setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowDocument.show();
		flexTable.setWidget(2, 0, wFlowDocumentFP);

		// Formatos	
		FlowPanel formatFP = new FlowPanel();
		UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoDevelopmentPhase, bmoDevelopmentPhase.getId());
		BmoFormat bmoFormat = new BmoFormat();
		UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
		BmFilter filterFormats = new BmFilter();
		filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
		uiProgramParams.setForceFilter(filterFormats);
		setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
		uiFormatDisplayList.show();
		flexTable.setWidget(3, 0, formatFP);

		// Bitacora
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		FlowPanel WFlowLogFP = new FlowPanel();
		BmFilter filterWFlowLogs = new BmFilter();
		filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoDevelopmentPhase.getWFlowId().toInteger());
		getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
		UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoDevelopmentPhase.getWFlowId().toInteger(), -1);
		setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowLog.show();
		flexTable.setWidget(4, 0, WFlowLogFP);

		addToDP(flexTable);
	}	
}
