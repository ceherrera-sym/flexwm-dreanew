package com.flexwm.client.cv;

import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowUserLock;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowTimeTrack;
import com.flexwm.shared.cv.BmoMeeting;
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


public class UiMeetingStart extends Ui {

	private FlexTable flexTable = new FlexTable();
	private BmoMeeting bmoMeeting;
	BmoWFlow bmoWFlow = new BmoWFlow();

	public UiMeetingStart(UiParams uiParams, int programId, BmoMeeting bmoMeeting){
		super(uiParams, new BmoMeeting());
		this.bmoMeeting = bmoMeeting;
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
				getUiParams().getBmObjectServiceAsync().get(bmoWFlow.getPmClass(), bmoMeeting.getWFlowId().toInteger(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
		}
	}

	private void render(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
		bmoMeeting.setBmoWFlow(bmoWFlow);
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();

		// Colaboradores
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		if (getUiParams().getSFParams().hasRead(bmoWFlowUser.getProgramCode())) {
			FlowPanel wFlowUserFP = new FlowPanel();
			BmFilter filterWFlowUsers = new BmFilter();
			filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoMeeting.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
			UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoMeeting.getBmoWFlow());
			setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowUserLock.show();
			flexTable.setWidget(1, 0, wFlowUserFP);
		}

		// Documentos
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		if (getUiParams().getSFParams().hasRead(bmoWFlowDocument.getProgramCode())) {
			FlowPanel wFlowDocumentFP = new FlowPanel();
			BmFilter filterWFlowDocuments = new BmFilter();
			filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoMeeting.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
			UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoMeeting.getWFlowId().toInteger());
			setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowDocument.show();
			flexTable.setWidget(2, 0, wFlowDocumentFP);
		}

		// Formatos	
		BmoFormat bmoFormat = new BmoFormat();
		if (getUiParams().getSFParams().hasRead(bmoFormat.getProgramCode())) {
			FlowPanel formatFP = new FlowPanel();
			UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoMeeting, bmoMeeting.getId());
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
			filterWFlowTimeTracks.setValueFilter(bmoWFlowTimeTrack.getKind(), bmoWFlowTimeTrack.getWFlowId(), bmoMeeting.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowTimeTrack.getProgramCode(), filterWFlowTimeTracks);
			UiWFlowTimeTrack uiWFlowTimeTrack = new UiWFlowTimeTrack(getUiParams(), WFlowTimeTrackFP, bmoMeeting.getBmoWFlow());
			setUiType(bmoWFlowTimeTrack.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowTimeTrack.show();
			flexTable.setWidget(4, 0, WFlowTimeTrackFP);
		}

		// Bitacora
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		if (getUiParams().getSFParams().hasRead(bmoWFlowLog.getProgramCode())) {
			FlowPanel WFlowLogFP = new FlowPanel();
			BmFilter filterWFlowLogs = new BmFilter();
			filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoMeeting.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
			UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoMeeting.getWFlowId().toInteger(), -1);
			setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowLog.show();
			flexTable.setWidget(5, 0, WFlowLogFP);
		}

		addToDP(flexTable);
	}	
}
