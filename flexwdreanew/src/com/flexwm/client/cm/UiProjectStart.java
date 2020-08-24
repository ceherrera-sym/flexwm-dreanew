package com.flexwm.client.cm;

import com.flexwm.client.op.UiProjectEquipment;
import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowUserLock;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowPhaseBar;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoProjectDetail;
import com.flexwm.shared.cm.BmoProjectStaff;
import com.flexwm.shared.op.BmoProjectEquipment;
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


public class UiProjectStart extends Ui {
	private FlexTable flexTable = new FlexTable();
	private BmoProject bmoProject;	

	public UiProjectStart(UiParams uiParams, int programId, BmoProject bmoProject) {
		super(uiParams, new BmoProject());
		this.bmoProject = bmoProject;		
		flexTable.setSize("100%", "100%");
	}


	public void show() {
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();

		// Fases del proyecto
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		if (getUiParams().getSFParams().hasRead(bmoWFlowPhase.getProgramCode())) {
			FlowPanel wFlowPhaseFlowPanel = new FlowPanel();
			wFlowPhaseFlowPanel.setSize("100%", "40px");
			UiWFlowPhaseBar uiWFlowPhaseBar = new UiWFlowPhaseBar(getUiParams(), wFlowPhaseFlowPanel, bmoProject.getWFlowId().toInteger());
			uiWFlowPhaseBar.show();
			flexTable.setWidget(0, 0, wFlowPhaseFlowPanel);
		}

		// Colaboradores
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		if (getUiParams().getSFParams().hasRead(bmoWFlowUser.getProgramCode())) {
			FlowPanel wFlowUserFP = new FlowPanel();
			BmFilter filterWFlowUsers = new BmFilter();
			filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoProject.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
			UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoProject.getBmoWFlow());
			setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowUserLock.show();
			flexTable.setWidget(1, 0, wFlowUserFP);

		}

		// Recursos
		BmoProjectEquipment bmoProjectEquipment = new BmoProjectEquipment();
		if (getUiParams().getSFParams().hasRead(bmoProjectEquipment.getProgramCode())) {
			FlowPanel ProjectEquipment = new FlowPanel();
			BmFilter filterEquipments = new BmFilter();
			filterEquipments.setValueFilter(bmoProjectEquipment.getKind(), bmoProjectEquipment.getProjectId(), bmoProject.getId());
			getUiParams().setForceFilter(bmoProjectEquipment.getProgramCode(), filterEquipments);
			UiProjectEquipment uiProjectEquipment = new UiProjectEquipment(getUiParams(), ProjectEquipment, bmoProject);
			setUiType(bmoProjectEquipment.getProgramCode(), UiParams.MINIMALIST);
			uiProjectEquipment.show();
			flexTable.setWidget(2, 0, ProjectEquipment);	
		}
		
		//Personal
		BmoProjectStaff bmoProjectStaff = new BmoProjectStaff();		
		if (getUiParams().getSFParams().hasRead(bmoProjectStaff.getProgramCode())) {
			FlowPanel projectStaff = new FlowPanel();
			BmFilter filterStaff = new BmFilter();
			filterStaff.setValueFilter(bmoProjectStaff.getKind(), bmoProjectStaff.getProjectId(), bmoProject.getId());
			getUiParams().setForceFilter(bmoProjectStaff.getProgramCode(), filterStaff);
			UiProjectStaff uiProjectStaff = new UiProjectStaff(getUiParams(), projectStaff, bmoProject);
			setUiType(bmoProjectStaff.getProgramCode(),UiParams.MINIMALIST);
			uiProjectStaff.show();
			flexTable.setWidget(3, 0, projectStaff);
		}

		// Vista detalle proyecto
		BmoProjectDetail bmoProjectDetail = new BmoProjectDetail();
		if (getUiParams().getSFParams().hasRead("ODDR")) {
			FlowPanel projectDetailDP = new FlowPanel();		
			BmFilter filterProjectDetail = new BmFilter();
			filterProjectDetail.setValueFilter(bmoProjectDetail.getKind(), bmoProjectDetail.getProjectId(), bmoProject.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoProjectDetail.getProgramCode(), filterProjectDetail);
			UiProjectDetailForm2 uiProjectDetailsForm = new UiProjectDetailForm2(getUiParams(), projectDetailDP, bmoProject);
			//setUiType(bmoProjectDetail.getProgramCode(), UiParams.SINGLESLAVE);		
			uiProjectDetailsForm.show();
			flexTable.setWidget(4, 0, projectDetailDP);
		}

		// Documentos
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		if (getUiParams().getSFParams().hasRead(bmoWFlowDocument.getProgramCode())) {
			FlowPanel wFlowDocumentFP = new FlowPanel();
			BmFilter filterWFlowDocuments = new BmFilter();
			filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoProject.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
			UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoProject.getWFlowId().toInteger());
			setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowDocument.show();
			flexTable.setWidget(5, 0, wFlowDocumentFP);
		}

		// Formatos
		BmoFormat bmoFormat = new BmoFormat();
		if (getUiParams().getSFParams().hasRead(bmoFormat.getProgramCode())) {
			FlowPanel formatFP = new FlowPanel();
			UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoProject, bmoProject.getId(), new UiProjectFormatAction(getUiParams(), bmoProject));
			UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
			BmFilter filterFormats = new BmFilter();
			filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
			uiProgramParams.setForceFilter(filterFormats);
			setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
			uiFormatDisplayList.show();
			flexTable.setWidget(6, 0, formatFP);
		}

		// Bitacora
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		if (getUiParams().getSFParams().hasRead(bmoWFlowLog.getProgramCode())) {
			FlowPanel WFlowLogFP = new FlowPanel();
			BmFilter filterWFlowLogs = new BmFilter();
			filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoProject.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
			UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoProject.getWFlowId().toInteger(), -1);
			setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowLog.show();
			flexTable.setWidget(7, 0, WFlowLogFP);
		}

		addToDP(flexTable);
	}
}
