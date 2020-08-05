package com.flexwm.client.in;

import com.flexwm.client.wf.UiWFlowUserLock;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlowPhaseBar;
import com.flexwm.shared.in.BmoPolicy;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.symgae.client.sf.UiFileDisplayList;
import com.symgae.client.sf.UiFormatDisplayList;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.UiProgramParams;
import com.symgae.shared.sf.BmoFile;
import com.symgae.shared.sf.BmoFormat;


public class UiPolicyStart extends Ui {
	private FlexTable flexTable = new FlexTable();
	private BmoPolicy bmoPolicy;
	Label policyLabel;
	
	
	public UiPolicyStart(UiParams uiParams, int programId, BmoPolicy bmoPolicy){
		super(uiParams, new BmoPolicy());
		this.bmoPolicy = bmoPolicy;

		getUiParams().getUiTemplate().showProgramExtrasPanel();
				
		policyLabel = new Label("Tablero Inicio");
		policyLabel.setStyleName("programSubtitle");
		
		flexTable.setSize("97%", "100%");
	}
	
	public void show() {
		clearDP();
		
		// Vista Poliza
		Label policyViewLabel = new Label("Póliza");
		policyViewLabel.setStyleName("programSubtitle");
		FlowPanel policyPanel = new FlowPanel();
		policyPanel.setSize("100%", "100%");
		ScrollPanel policyScroll = new ScrollPanel();
		policyScroll.setSize("97%", "300px");
		policyScroll.addStyleName("detailStart");
		policyScroll.add(policyPanel);
		UiPolicyForm uiPolicyForm = new UiPolicyForm(getUiParams(), policyPanel, bmoPolicy.getId());
		setUiType(new BmoPolicy().getProgramCode(), UiParams.VIEW);
		uiPolicyForm.show();

		// Colaboradores
		FlowPanel wflowUserPanel = new FlowPanel();
		wflowUserPanel.setSize("100%", "100%");
		ScrollPanel wflowUserScroll = new ScrollPanel();
		wflowUserScroll.setStyleName("detailStart");
		wflowUserScroll.setSize("97%", "300px");
		wflowUserScroll.add(wflowUserPanel);
		UiWFlowUserLock uiWFlowUser = new UiWFlowUserLock(getUiParams(), wflowUserPanel, bmoPolicy.getBmoWFlow());
		uiWFlowUser.show();
		
		// Formatos
		FlowPanel formatPanel = new FlowPanel();
		formatPanel.setSize("100%", "100%");
		ScrollPanel formatScroll = new ScrollPanel();
		formatScroll.setStyleName("detailStart");
		formatScroll.setSize("97%", "200px");
		formatScroll.add(formatPanel);
		UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatPanel, bmoPolicy, bmoPolicy.getId());
		BmoFormat bmoFormat = new BmoFormat();
		UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
		uiProgramParams.setForceFilter(bmFilter);
		setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
		uiFormatDisplayList.show();
		
		// Archivos
		FlowPanel filePanel = new FlowPanel();
		filePanel.setSize("100%", "100%");
		ScrollPanel fileScroll = new ScrollPanel();
		fileScroll.setStyleName("detailStart");
		fileScroll.setSize("97%", "200px");
		fileScroll.add(filePanel);
		UiFileDisplayList uiFileDisplayList = new UiFileDisplayList(getUiParams(), filePanel, bmoPolicy.getProgramCode(), bmObjectProgramId, bmoPolicy.getId());
		setUiType(new BmoFile().getProgramCode(), UiParams.MINIMALIST);
		uiFileDisplayList.show();
		
		// Bitacora
		Label wflowLogLabel = new Label("Bitacora de la Oportunidad");
		wflowLogLabel.setStyleName("programSubtitle");
		FlowPanel fp3 = new FlowPanel();
		fp3.setSize("100%", "100%");
		ScrollPanel sp3 = new ScrollPanel();
		sp3.setSize("97%", "400px");
		sp3.addStyleName("detailStart");
		sp3.add(fp3);
		UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), fp3, bmoPolicy.getWFlowId().toInteger(), -1);
		uiWFlowLog.show();
		
		// Asignar el subtitulo
		getUiParams().getUiTemplate().showProgramButtonPanel();
		getUiParams().getUiTemplate().getProgramButtonPanel().add(policyLabel);
		
		// Fases del proyecto
		getUiParams().getUiTemplate().showProgramExtrasPanel();
		UiWFlowPhaseBar uiWFlowPhaseBar = new UiWFlowPhaseBar(getUiParams(), getUiParams().getUiTemplate().getProgramExtrasPanel(), bmoPolicy.getWFlowId().toInteger());
		uiWFlowPhaseBar.show();
		
		
		flexTable.setWidget(0, 0, policyScroll);
		flexTable.setWidget(0,  1, new HTML("&nbsp;"));
		flexTable.setWidget(0, 2, wflowUserScroll);
		
		flexTable.setWidget(1, 0, new HTML("&nbsp;"));
		flexTable.getFlexCellFormatter().setColSpan(1, 0, 3);
		
		flexTable.setWidget(2, 0, formatScroll);
		flexTable.setWidget(2,  1, new HTML("&nbsp;"));
		flexTable.setWidget(2, 2, fileScroll);
		
		flexTable.setWidget(3, 0, new HTML("&nbsp;"));
		flexTable.getFlexCellFormatter().setColSpan(3, 0, 3);
		
		flexTable.setWidget(4, 0, sp3);
		flexTable.getFlexCellFormatter().setColSpan(4, 0, 3);
		
		addToDP(flexTable);
	}
	
}
