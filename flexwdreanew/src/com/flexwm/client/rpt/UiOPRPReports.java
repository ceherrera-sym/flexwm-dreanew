package com.flexwm.client.rpt;

import com.flexwm.shared.fi.BmoCommission;
import com.flexwm.shared.op.BmoCustomerService;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoSupplier;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;


public class UiOPRPReports extends UiDashboard {

	public UiOPRPReports(UiParams uiParams){
		super(uiParams, "Reportes Operaciones", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/oprp.png"));
	}

	@Override
	public void populate() {
		// Reporte de Pedidos drea
		if (getUiParams().getSFParams().hasRead("ODDR")) {
					addActionLabel(getSFParams().getProgramTitle(new BmoOrder()), new BmoOrder().getProgramCode(), new ClickHandler() {
						public void onClick(ClickEvent event) {
							UiOrderProjReport UiOrderProjReport = new UiOrderProjReport(getUiParams());
							UiOrderProjReport.show();
						}
					});
		}else {
			if (getUiParams().getSFParams().hasRead(new BmoOrder().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoOrder()), new BmoOrder().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiOrderReport UiOrdersReport = new UiOrderReport(getUiParams());
					UiOrdersReport.show();
				}
			});		
		}

		// Reporte de quejas de Pedidos
		if (getUiParams().getSFParams().hasRead("CUSR"))
			addActionLabel(getSFParams().getProgramTitle(new BmoCustomerService()), new BmoCustomerService().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiCustomerServiceReport UiCustomerServiceReport = new UiCustomerServiceReport(getUiParams());
					UiCustomerServiceReport.show();
				}
			});

		// Reporte de quejas de Pedidos, para PROPERTY
		if (getUiParams().getSFParams().hasRead("CSRP"))
			addActionLabel(getSFParams().getProgramTitle(new BmoCustomerService()), new BmoCustomerService().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiCustomerServicePropertyReport UiCustomerServicePropertyReport = new UiCustomerServicePropertyReport(getUiParams());
					UiCustomerServicePropertyReport.show();
				}
			});

		// Reporte de Ordenes de Compra
		if (getUiParams().getSFParams().hasRead(new BmoRequisition().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoRequisition()), new BmoRequisition().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiRequisitionsReport uiRequisitionsReport = new UiRequisitionsReport(getUiParams());
					uiRequisitionsReport.show();
				}
			});

		// Reporte de Recibos de Ordenes de Compra
		if (getUiParams().getSFParams().hasRead(new BmoRequisitionReceipt().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoRequisitionReceipt()), new BmoRequisitionReceipt().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiRequisitionReceiptsReport uiRequisitionReceiptsReport = new UiRequisitionReceiptsReport(getUiParams());
					uiRequisitionReceiptsReport.show();
				}
			});

		// Reporte de Proveedores
		if (getUiParams().getSFParams().hasRead(new BmoSupplier().getProgramCode()))
			addActionLabel(getSFParams().getProgramTitle(new BmoSupplier()), new BmoSupplier().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiSuppliersReport uiSuppliersReport = new UiSuppliersReport(getUiParams());
					uiSuppliersReport.show();
				}
			});

		if (getUiParams().getSFParams().hasRead("OCOM"))
			addActionLabel(getSFParams().getProgramTitle(new BmoCommission()), new BmoCommission().getProgramCode(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					UiCommissionOrder uiCommissionOrder = new UiCommissionOrder(getUiParams());
					uiCommissionOrder.show();
				}
			});

		// Reporte default 
		if (getUiParams().getSFParams().hasRead(new BmoOrder().getProgramCode())){
			UiOrderReport UiOrdersReport = new UiOrderReport(getUiParams());
			UiOrdersReport.show();
		}
	}
}
