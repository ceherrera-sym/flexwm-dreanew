
package com.flexwm.client.rpt;

import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertySale;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;


public class UiVIRPReports extends UiDashboard {

	public UiVIRPReports(UiParams uiParams) {
		super(uiParams, "Reportes " + uiParams.getSFParams().getProgramTitle(new BmoPropertySale()),
				GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/virp.png"));
	}

	@Override
	public void populate() {
		// Reporte de clientes
		if (getUiParams().getSFParams().hasRead("CRPG")) {
			addActionLabel(getSFParams().getProgramTitle(new BmoCustomer()), new BmoCustomer().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiCustomerG100Report uiCustomerG100Report = new UiCustomerG100Report(getUiParams());
					uiCustomerG100Report.show();
				}
			});
		} else {
			if (getUiParams().getSFParams().hasRead(new BmoCustomer().getProgramCode()))
				addActionLabel(getSFParams().getProgramTitle(new BmoCustomer()), new BmoCustomer().getProgramCode(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						UiCustomerReport uiCustomerReport = new UiCustomerReport(getUiParams());
						uiCustomerReport.show();
					}
				});
		}
		// Reporte de oportunidades
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoOpportunity()), new BmoOpportunity().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiOpportunityPropertyModelReport uiOpportunityPropertyModelReport = new UiOpportunityPropertyModelReport(getUiParams());
					uiOpportunityPropertyModelReport.show();
				}
			});

		// Reporte de oportunidades perdidas
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoOpportunity()) + " Perdidas", new BmoOpportunity().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiOpportunityLosePropertyModelReport uiOpportunityLosePropertyModelReport = new UiOpportunityLosePropertyModelReport(getUiParams());
					uiOpportunityLosePropertyModelReport.show();
				}
			});

		// Reporte de clientes y ventas
		
		if (getUiParams().getSFParams().hasRead(new BmoPropertySale().getProgramCode())) {
			try {
				if(getUiParams().getSFParams().getProgramId("PSRG") > 0) {
					addActionLabel(getSFParams().getProgramTitle(new BmoPropertySale()), new BmoPropertySale().getProgramCode(), new ClickHandler() {
						public void onClick(ClickEvent event) {
							UiPropertySalesReportG100 uiPropertySalesReportG100 = new UiPropertySalesReportG100(getUiParams());
							uiPropertySalesReportG100.show();
						}
					});
				}
			} catch (SFException e) {
				addActionLabel(getSFParams().getProgramTitle(new BmoPropertySale()), new BmoPropertySale().getProgramCode(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						UiPropertySalesReport uiPropertySalesReport = new UiPropertySalesReport(getUiParams());
						uiPropertySalesReport.show();
					}
				});
			}
			
		}
		// Reporte de viviendas disponibles 
		if (getUiParams().getSFParams().hasRead(new BmoProperty().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoProperty()), new BmoProperty().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiPropertyAvailableReport uiPropertyAvailableReport = new UiPropertyAvailableReport(getUiParams());
					uiPropertyAvailableReport.show();
				}
			});

		// Reporte de gerencial
		if (getUiParams().getSFParams().hasRead("DIRP"))
			addActionLabel("Gerenciales", new BmoPropertySale().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiManagementReport uiManagementReport = new UiManagementReport(getUiParams());
					uiManagementReport.show();
				}
			});			

		// Reporte default 
		if (getUiParams().getSFParams().hasRead("CRPG")) {
			UiCustomerG100Report uiCustomerG100Report = new UiCustomerG100Report(getUiParams());
			uiCustomerG100Report.show();
		}else {
			if (getUiParams().getSFParams().hasRead(new BmoCustomer().getProgramCode())){
				UiCustomerReport uiCustomerReport = new UiCustomerReport(getUiParams());
				uiCustomerReport.show();
			}
		}
	}
}
