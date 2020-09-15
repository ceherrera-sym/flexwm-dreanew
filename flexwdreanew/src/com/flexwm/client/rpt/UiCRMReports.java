package com.flexwm.client.rpt;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoExternalSales;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.fi.BmoRaccount;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;


public class UiCRMReports extends UiDashboard {

	public UiCRMReports(UiParams uiParams){
		super(uiParams, "Reportes Comerciales", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/crmr.png"));
	}

	@Override
	public void populate() {

		// Reporte de Clientes
		if (getUiParams().getSFParams().hasRead("RPCU"))
			addActionLabel(getSFParams().getProgramTitle(new BmoCustomer()), new BmoCustomer().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiCustomerReport uiCustomerReport = new UiCustomerReport(getUiParams());
					uiCustomerReport.show();
				}
			});
		// Reporte de Clientes (para flotis)
		if (getUiParams().getSFParams().hasRead("RPSC"))
			addActionLabel(getSFParams().getProgramTitle(new BmoCustomer()), new BmoCustomer().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiCustomerSessionReport uiCustomerSessionReport = new UiCustomerSessionReport(getUiParams());
					uiCustomerSessionReport.show();
				}
			});

		// Reporte de oportunidades
		if (getUiParams().getSFParams().hasRead(new BmoOpportunity().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoOpportunity()), new BmoOpportunity().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiOpportunityReport uiOpportunityReport = new UiOpportunityReport(getUiParams());
					uiOpportunityReport.show();
				}
			});

		// Reporte de oportunidades perdidas
		if (getUiParams().getSFParams().hasRead("RPOP"))
			addActionLabel(getSFParams().getProgramTitle( new BmoOpportunity()) + " Perdidas", new BmoOpportunity().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiOpportunityLoseReport uiOpportunityLoseReport = new UiOpportunityLoseReport(getUiParams());
					uiOpportunityLoseReport.show();
				}
			});

		// Reporte de proyectos
		if (getUiParams().getSFParams().hasRead("RPPJ"))
			addActionLabel(getSFParams().getProgramTitle(new BmoProject()), new BmoProject().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiProjectReport uiProjectReport = new UiProjectReport(getUiParams());
					uiProjectReport.show();
				}
			});

		// Reporte de proyectos sociales
		if (getUiParams().getSFParams().hasRead("PRSO"))
			addActionLabel(getSFParams().getProgramTitle(new BmoProject()) + " Sociales", new BmoProject().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiProjectSocialReport uiProjectSocialReport = new UiProjectSocialReport(getUiParams());
					uiProjectSocialReport.show();
				}
			});

		// reportes de sesiones
		if (getUiParams().getSFParams().hasRead("PRSE")) {
			addActionLabel(getSFParams().getProgramTitle(new BmoSession()), new BmoSession().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiSessionReport uiSessionReport = new UiSessionReport(getUiParams());
					uiSessionReport.show();
				}
			});	
		}
		
		// Reporte Creditos
		if (getUiParams().getSFParams().hasRead(new BmoCredit().getProgramCode())) {
			// Reportes de DaCredito
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
				addActionLabel(getSFParams().getProgramTitle(new BmoCustomer()), new BmoCustomer().getProgramCode(), new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						UiCustomerDaCreditoReport uiCustomerDaCreditoReport = new UiCustomerDaCreditoReport(getUiParams());
						uiCustomerDaCreditoReport.show();
					}
				});
				
				addActionLabel(getSFParams().getProgramTitle(new BmoCredit()), new BmoCredit().getProgramCode(), new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						UiCreditDaCreditoReport uiCreditDaCreditoReport = new UiCreditDaCreditoReport(getUiParams());
						uiCreditDaCreditoReport.show();
					}
				});
			} else {
			// Reporte de COBI (el de clientes es RPCU)
				addActionLabel(getSFParams().getProgramTitle(new BmoCredit()), new BmoCredit().getProgramCode(), new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						UiCreditReport uiCreditReport = new UiCreditReport(getUiParams());
						uiCreditReport.show();
					}
				});	
			}
		}		
		
		//Reporte Contratos
		if (getUiParams().getSFParams().hasRead(new BmoPropertyRental().getProgramCode())) {
			addActionLabel(getSFParams().getProgramTitle(new BmoPropertyRental()), new BmoPropertyRental().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiContractReport uiContractReport = new UiContractReport(getUiParams());
					uiContractReport.show();
				}
			});	
		}
		//Reporte cxc (para inadico)
		if (getUiParams().getSFParams().hasRead("PRCC")) {
			addActionLabel(getSFParams().getProgramTitle(new BmoRaccount()), new BmoRaccount().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UiContractRaccountReport uiContractReport = new UiContractRaccountReport(getUiParams());
					uiContractReport.show();
				}
			});	
		}
		// Ventas externas
		if (getUiParams().getSFParams().hasRead(new BmoExternalSales().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoExternalSales()), new BmoExternalSales().getProgramCode(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					uiExternalSalesReport uiExternalSalesReport = new uiExternalSalesReport(getUiParams());
					uiExternalSalesReport.show();
				}
		});	

		// Reporte default 
		if (getUiParams().getSFParams().hasRead("RPCU")) {
			UiCustomerReport uiCustomerReport = new UiCustomerReport(getUiParams());
			uiCustomerReport.show();
		} else if (getUiParams().getSFParams().hasRead("RPSC")) {
			UiCustomerSessionReport uiCustomerSessionReport = new UiCustomerSessionReport(getUiParams());
			uiCustomerSessionReport.show();
		} else {
			// Reportes de clientes para DaCredito
			if (getUiParams().getSFParams().hasRead(new BmoCredit().getProgramCode())) {
				if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
					UiCustomerDaCreditoReport uiCustomerDaCreditoReport = new UiCustomerDaCreditoReport(getUiParams());
					uiCustomerDaCreditoReport.show();
				}
			}
		}
	
	}

}
