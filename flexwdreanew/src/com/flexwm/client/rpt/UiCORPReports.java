package com.flexwm.client.rpt;

import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.co.BmoWorkContract;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;


public class UiCORPReports extends UiDashboard {

	public UiCORPReports(UiParams uiParams){
		super(uiParams, "Reportes Obra", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/corp.png"));
	}

	@Override
	public void populate() {

		// Reporte de Viviendas
		if (getUiParams().getSFParams().hasRead(new BmoProperty().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoProperty()), new BmoProperty().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiPropertyReport uiPropertyReport = new UiPropertyReport(getUiParams());
					uiPropertyReport.show();
				}
			});

		// Reporte de Modelos
		if (getUiParams().getSFParams().hasRead(new BmoPropertyModel().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoPropertyModel()), new BmoPropertyModel().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiPropertyModelsReport uiPropertyModelsReport = new UiPropertyModelsReport(getUiParams());
					uiPropertyModelsReport.show();
				}
			});

		// Reporte de Manzanas
		if (getUiParams().getSFParams().hasRead(new BmoDevelopmentBlock().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoDevelopmentBlock()), new BmoDevelopmentBlock().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiDevelopmentBlocksReport uiDevelopmentBlocksReport = new UiDevelopmentBlocksReport(getUiParams());
					uiDevelopmentBlocksReport.show();
				}
			});

		// Reporte de Obras
		if (getUiParams().getSFParams().hasRead(new BmoWork().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoWork()), new BmoWork().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiWorksReport uiWorksReport = new UiWorksReport(getUiParams());
					uiWorksReport.show();
				}
			});

		// Reporte de Contratos de Obra
		if (getUiParams().getSFParams().hasRead(new BmoWorkContract().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoWorkContract()), new BmoWorkContract().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiWorkContractsReport uiWorkContractsReport = new UiWorkContractsReport(getUiParams());
					uiWorkContractsReport.show();
				}
			});

		// Reporte de Etapas de Desarrollo
		if (getUiParams().getSFParams().hasRead(new BmoDevelopmentPhase().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoDevelopmentPhase()), new BmoDevelopmentPhase().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiDevelopmentPhaseReport uiDevelopmentPhaseReport = new UiDevelopmentPhaseReport(getUiParams());
					uiDevelopmentPhaseReport.show();
				}
			});


		// Reporte default 
		if (getUiParams().getSFParams().hasRead(new BmoProperty().getProgramCode())) {
			UiPropertyReport uiPropertyReport = new UiPropertyReport(getUiParams());
			uiPropertyReport.show();
		}
	}

}
