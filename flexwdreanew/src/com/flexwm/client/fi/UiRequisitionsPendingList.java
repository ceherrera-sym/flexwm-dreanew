package com.flexwm.client.fi;

import com.flexwm.client.op.UiRequisition;
import com.flexwm.shared.op.BmoRequisition;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiRequisitionsPendingList extends UiList {
	BmoRequisition bmoRequisition;	


	public UiRequisitionsPendingList(UiParams uiParams,Panel defaultPanel) {
		super(uiParams,defaultPanel,new BmoRequisition());
		bmoRequisition = (BmoRequisition)getBmObject();			

		BmFilter filterAuthorized = new BmFilter();		
		filterAuthorized.setValueFilter(bmoRequisition.getKind(), 
				bmoRequisition.getStatus(), "" +  BmoRequisition.STATUS_AUTHORIZED);

		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setForceFilter(filterAuthorized);

		//		BmFilter filterdeliveryStatus = new BmFilter();
		//		filterdeliveryStatus.setValueFilter(bmoRequisition.getKind(), 
		//				bmoRequisition.getPaymentStatus(), ""+BmoRequisition.PAYMENTSTATUS_PENDING);
	}
	@Override
	public void postShow() {

		//establecer Listbox
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoRequisition.getPaymentStatus()), bmoRequisition, bmoRequisition.getPaymentStatus(),""+BmoRequisition.PAYMENTSTATUS_PENDING);
		//ocultar ListBox
		extrasPanel.setVisible(false);

		//newImage.setVisible(false);
	}
	@Override

	public void create() {
		UiRequisition uiRequisition = new UiRequisition(getUiParams());
		getUiParams().setUiType(new BmoRequisition().getProgramCode(), UiParams.MINIMALIST);
		uiRequisition.create();
	}
	@Override
	public void open(BmObject bmObject) {
		bmoRequisition = (BmoRequisition)bmObject;
		getUiParams().setUiType(new BmoRequisition().getProgramCode(), UiParams.MINIMALIST);
		UiRequisition uiRequisition = new UiRequisition(getUiParams());
		uiRequisition.edit(bmoRequisition);
	}



}
