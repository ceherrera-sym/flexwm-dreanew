package com.flexwm.client.rpt;

import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhTrack;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.GwtUtil;


public class UiWARPReports extends UiDashboard {

	public UiWARPReports(UiParams uiParams) {
		super(uiParams, "Reportes Almacenes", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/warp.png"));
	}

	@Override
	public void populate() {

		// Reporte de Productos
		addActionLabel(new BmoProduct(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiProductsReport uiProductsReport = new UiProductsReport(getUiParams());
				uiProductsReport.show();
			}
		});

		// Reporte de Recursos
		addActionLabel(new BmoEquipment(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiEquipmentsReport uiEquipmentsReport = new UiEquipmentsReport(getUiParams());
				uiEquipmentsReport.show();
			}
		});

		// Reporte de Almacenes
		addActionLabel(new BmoWarehouse(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiWareHouseReport uiWareHouseReport = new UiWareHouseReport(getUiParams());
				uiWareHouseReport.show();
			}
		});

		// Reporte de Tx. Inventario
		addActionLabel(new BmoWhMovement(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiWhMovementsReport uiWhMovementsReport = new UiWhMovementsReport(getUiParams());
				uiWhMovementsReport.show();
			}
		});

		// Reporte de Rastreo de Productos
		addActionLabel(new BmoWhTrack(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiWhTracksReport uiWhTracksReport = new UiWhTracksReport(getUiParams());
				uiWhTracksReport.show();
			}
		});

		// Reporte default 
		if (getUiParams().getSFParams().hasRead(new BmoProduct().getProgramCode())){
			UiProductsReport uiProductsReport = new UiProductsReport(getUiParams());
			uiProductsReport.show();
		}
	}
}
