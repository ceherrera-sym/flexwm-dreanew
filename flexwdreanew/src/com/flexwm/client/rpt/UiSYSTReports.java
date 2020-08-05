package com.flexwm.client.rpt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoProgramProfile;


public class UiSYSTReports extends UiDashboard {

	public UiSYSTReports(UiParams uiParams){
		super(uiParams, "Reportes Sistemas", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/syst.png"));
	}

	@Override
	public void populate() {
		//Reporte de Permisos
		if (getUiParams().getSFParams().hasRead(new BmoProgramProfile().getProgramCode()))
			westTable.addActionLabel(getSFParams().getProgramTitle(new BmoProgramProfile()), new BmoProgramProfile().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiProgramAccessReport uiProgramProfileReport = new UiProgramAccessReport(getUiParams());
					uiProgramProfileReport.show();
				}
			});

		// Reporte default 
		if (getUiParams().getSFParams().hasRead(new BmoProgramProfile().getProgramCode())){
			UiProgramAccessReport uiProgramProfileReport = new UiProgramAccessReport(getUiParams());
			uiProgramProfileReport.show();
		}
	}
}
