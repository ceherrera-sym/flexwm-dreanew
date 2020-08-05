package com.flexwm.client.cv;

import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowUserLock;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowTimeTrack;
import com.flexwm.shared.cv.BmoActivity;
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
import com.flexwm.shared.wf.BmoWFlowTimeTrack;
import com.flexwm.shared.wf.BmoWFlowUser;


public class UiActivityStart extends Ui {

	private FlexTable flexTable = new FlexTable();
	private BmoActivity bmoActivity;
	BmoWFlow bmoWFlow = new BmoWFlow();

	public UiActivityStart(UiParams uiParams, int programId, BmoActivity bmoActivity){
		super(uiParams, new BmoActivity());
		this.bmoActivity = bmoActivity;
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
				getUiParams().getBmObjectServiceAsync().get(bmoWFlow.getPmClass(), bmoActivity.getWFlowId().toInteger(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
		}
	}

	private void render(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
		bmoActivity.setBmoWFlow(bmoWFlow);
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();

		// Colaboradores
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		if (getUiParams().getSFParams().hasRead(bmoWFlowUser.getProgramCode())) {
			FlowPanel wFlowUserFP = new FlowPanel();
			BmFilter filterWFlowUsers = new BmFilter();
			filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoActivity.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
			UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoActivity.getBmoWFlow());
			setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowUserLock.show();
			flexTable.setWidget(1, 0, wFlowUserFP);
		}

		// Documentos
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		if (getUiParams().getSFParams().hasRead(bmoWFlowDocument.getProgramCode())) {
			FlowPanel wFlowDocumentFP = new FlowPanel();
			BmFilter filterWFlowDocuments = new BmFilter();
			filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoActivity.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
			UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoActivity.getWFlowId().toInteger());
			setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowDocument.show();
			flexTable.setWidget(2, 0, wFlowDocumentFP);
		}

		// Formatos	
		BmoFormat bmoFormat = new BmoFormat();
		if (getUiParams().getSFParams().hasRead(bmoFormat.getProgramCode())) {
			FlowPanel formatFP = new FlowPanel();
			UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoActivity, bmoActivity.getId());
			UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
			BmFilter filterFormats = new BmFilter();
			filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
			uiProgramParams.setForceFilter(filterFormats);
			setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
			uiFormatDisplayList.show();
			flexTable.setWidget(3, 0, formatFP);
		}

		// Rastreo tiempo
		BmoWFlowTimeTrack bmoWFlowTimeTrack = new BmoWFlowTimeTrack();
		if (getUiParams().getSFParams().hasRead(bmoWFlowTimeTrack.getProgramCode())) {
			FlowPanel WFlowTimeTrackFP = new FlowPanel();
			BmFilter filterWFlowTimeTracks = new BmFilter();
			filterWFlowTimeTracks.setValueFilter(bmoWFlowTimeTrack.getKind(), bmoWFlowTimeTrack.getWFlowId(), bmoActivity.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowTimeTrack.getProgramCode(), filterWFlowTimeTracks);
			UiWFlowTimeTrack uiWFlowTimeTrack = new UiWFlowTimeTrack(getUiParams(), WFlowTimeTrackFP, bmoActivity.getBmoWFlow());
			setUiType(bmoWFlowTimeTrack.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowTimeTrack.show();
			flexTable.setWidget(4, 0, WFlowTimeTrackFP);
		}

		// Bitacora
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		if (getUiParams().getSFParams().hasRead(bmoWFlowLog.getProgramCode())) {
			FlowPanel WFlowLogFP = new FlowPanel();
			BmFilter filterWFlowLogs = new BmFilter();
			filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoActivity.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
			UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoActivity.getWFlowId().toInteger(), -1);
			setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowLog.show();
			flexTable.setWidget(5, 0, WFlowLogFP);
		}

		addToDP(flexTable);
	}	
}
