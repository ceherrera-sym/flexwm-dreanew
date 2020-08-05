package com.flexwm.client.rpt;

import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoRaccount;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;


public class UiADRPReports extends UiDashboard {

	public UiADRPReports(UiParams uiParams){
		super(uiParams, "Reportes Administrativo", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/adrp.png"));
	}

	@Override
	public void populate() {

		
		
		// Reporte de cxp
		if (getUiParams().getSFParams().hasRead(new BmoPaccount().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoPaccount()), new BmoPaccount().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiPaccountReport uiPaccountReport = new UiPaccountReport(getUiParams());
					uiPaccountReport.show();
				}
			});

		if (getUiParams().getSFParams().hasRead("RCCG")){
			// Reporte de cxc
			if (getUiParams().getSFParams().hasRead(new BmoRaccount().getProgramCode()))
				addActionLabel(getSFParams().getProgramTitle(new BmoRaccount()), new BmoRaccount().getProgramCode(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						UiRaccountReportG100 uiRaccountReportG100 = new UiRaccountReportG100(getUiParams());
						uiRaccountReportG100.show();
					}
				});
		}else {
			// Reporte de cxc
			if (getUiParams().getSFParams().hasRead(new BmoRaccount().getProgramCode()))
				addActionLabel(getSFParams().getProgramTitle(new BmoRaccount()), new BmoRaccount().getProgramCode(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						UiRaccountReport uiRaccountReport = new UiRaccountReport(getUiParams());
						uiRaccountReport.show();
					}
				});
		}
		// Reporte de bancos
		if (getUiParams().getSFParams().hasRead(new BmoBankMovement().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoBankMovement()), new BmoBankMovement().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiBankMovementReport uiBankMovementReport = new UiBankMovementReport(getUiParams());
					uiBankMovementReport.show();
				}
			});

		// Reporte de presupuestos
		if (getUiParams().getSFParams().hasRead("RPBG"))
			addActionLabel(getSFParams().getProgramTitle(new BmoBudget()), new BmoBudget().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiBudgetReport uiBudgetReport = new UiBudgetReport(getUiParams());
					uiBudgetReport.show();
				}
			});

		// Reporte default 
		if (getUiParams().getSFParams().hasRead(new BmoPaccount().getProgramCode())) {
			UiPaccountReport uiPaccountReport = new UiPaccountReport(getUiParams());
			uiPaccountReport.show();
		}else{
			if (getUiParams().getSFParams().hasRead(new BmoRaccount().getProgramCode())){
			if (getUiParams().getSFParams().hasRead("RCCG")){
				UiRaccountReportG100 uiRaccountReportG100 = new UiRaccountReportG100(getUiParams());
				uiRaccountReportG100.show();
			}else{
				UiRaccountReport uiRaccountReport = new UiRaccountReport(getUiParams());
				uiRaccountReport.show();
			}
				
			}else{
				if (getUiParams().getSFParams().hasRead(new BmoBankMovement().getProgramCode())){
					UiBankMovementReport uiBankMovementReport = new UiBankMovementReport(getUiParams());
					uiBankMovementReport.show();
				}
			}
				
		}
		
	}
}
