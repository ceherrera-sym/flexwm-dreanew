package com.flexwm.client.ar;

import com.flexwm.client.op.UiOrderList;
import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowUserLock;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowUser;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;


public class UiPropertyRentalStart extends Ui {
	private FlexTable flexTable = new FlexTable();
	private BmoPropertyRental bmoPropertyRental;	

	public UiPropertyRentalStart(UiParams uiParams, int programId, BmoPropertyRental bmoPropertyRental) {
		super(uiParams, new BmoPropertyRental());
		this.bmoPropertyRental = bmoPropertyRental;		
		flexTable.setSize("100%", "100%");
	}

	public void show() {
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();

		// Contratos Renovados
		BmoOrder bmoOrder = new BmoOrder();
		if (getUiParams().getSFParams().hasRead(bmoOrder.getProgramCode())) {
			FlowPanel orderRenewFP = new FlowPanel();
			BmFilter bmFilterPropertyRent = new BmFilter();
			bmFilterPropertyRent.setInFilter(bmoPropertyRental.getKind(),
					bmoOrder.getOriginRenewOrderId().getName(), 
					bmoPropertyRental.getOrderId().getName(),
					bmoPropertyRental.getIdFieldName(),
					""+bmoPropertyRental.getId());
			getUiParams().setForceFilter(bmoOrder.getProgramCode(), bmFilterPropertyRent);
			UiOrderList uiOrderList = new UiOrderList(getUiParams(), orderRenewFP, bmoOrder);
			setUiType(bmoOrder.getProgramCode(), UiParams.MINIMALIST);
			uiOrderList.show();
			flexTable.setWidget(0, 0, orderRenewFP);

		}
		// Contratos Renovados
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		if (getUiParams().getSFParams().hasRead(bmoWFlowUser.getProgramCode())) {
			FlowPanel wFlowUserFP = new FlowPanel();
			BmFilter filterWFlowUsers = new BmFilter();
			filterWFlowUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoPropertyRental.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowUser.getProgramCode(), filterWFlowUsers);
			UiWFlowUserLock uiWFlowUserLock = new UiWFlowUserLock(getUiParams(), wFlowUserFP, bmoPropertyRental.getBmoWFlow());
			setUiType(bmoWFlowUser.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowUserLock.show();
			flexTable.setWidget(2, 0, wFlowUserFP);
		}

		// Documentos
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		if (getUiParams().getSFParams().hasRead(bmoWFlowDocument.getProgramCode())) {
			FlowPanel wFlowDocumentFP = new FlowPanel();
			BmFilter filterWFlowDocuments = new BmFilter();
			filterWFlowDocuments.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoPropertyRental.getWFlowId().toInteger());
			getUiParams().setForceFilter(bmoWFlowDocument.getProgramCode(), filterWFlowDocuments);
			UiWFlowDocument uiWFlowDocument = new UiWFlowDocument(getUiParams(), wFlowDocumentFP, bmoPropertyRental.getWFlowId().toInteger());
			setUiType(bmoWFlowDocument.getProgramCode(), UiParams.MINIMALIST);
			uiWFlowDocument.show();
			flexTable.setWidget(3, 0, wFlowDocumentFP);
		}

		// Formatos
		//		BmoFormat bmoFormat = new BmoFormat();
		//		if (getUiParams().getSFParams().hasRead(bmoFormat.getProgramCode())) {
		//			FlowPanel formatFP = new FlowPanel();
		//			UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoPropertyRental, bmoPropertyRental.getId(), new UiPropertyRentalFormatAction (getUiParams(), bmoPropertyRental));
		//			UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
		//			BmFilter filterFormats = new BmFilter();
		//			filterFormats.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
		//			uiProgramParams.setForceFilter(filterFormats);
		//			setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
		//			uiFormatDisplayList.show();
		//			flexTable.setWidget(2, 0, formatFP);
		//		}

		addToDP(flexTable);
	}
}
