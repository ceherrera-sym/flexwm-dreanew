package com.flexwm.client.cm;

import com.flexwm.client.fi.UiRaccount;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;


public class UiCustomerStart extends Ui {
	private UiFormFlexTable formFlexTable;
	private BmoCustomer bmoCustomer;
	BmoWFlow bmoWFlow = new BmoWFlow();
	int wFlowId = 0;

	public UiCustomerStart(UiParams uiParams, int programId, BmoCustomer bmoCustomer){
		super(uiParams, new BmoCustomer());
		this.bmoCustomer = bmoCustomer;

		formFlexTable = new UiFormFlexTable(uiParams);
	}

	public void show() {
		clearDP();
		getUiParams().getUiTemplate().hideProgramButtonPanel();
		getUiParams().getUiTemplate().hideProgramExtrasPanel();
		getUiParams().getUiTemplate().hideEastPanel();

		// Formatos	
		//		if (getUiParams().getSFParams().hasRead(new BmoFormat().getProgramCode())) {
		//			FlowPanel formatFP = new FlowPanel();
		//			UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatFP, bmoCustomer, bmoCustomer.getId());
		//			BmoFormat bmoFormat = new BmoFormat();
		//			UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
		//			BmFilter bmFilter = new BmFilter();
		//			bmFilter.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
		//			uiProgramParams.setForceFilter(bmFilter);
		//			setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
		//			uiFormatDisplayList.show();
		//			formFlexTable.addPanel(1, 0, formatFP, 2);
		//		}

		// Cuentas x Cobrar	
		if (getUiParams().getSFParams().hasRead(new BmoRaccount().getProgramCode())) {
			FlowPanel raccountFP = new FlowPanel();
			BmoRaccount bmoRaccount = new BmoRaccount();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getCustomerId(), bmoCustomer.getId());
			getUiParams().setForceFilter(bmoRaccount.getProgramCode(), bmFilter);

			UiRaccount uiRaccountList = new UiRaccount(getUiParams(), raccountFP, bmoCustomer.getId());
			setUiType(bmoRaccount.getProgramCode(), UiParams.MINIMALIST);
			uiRaccountList.show();
			formFlexTable.addPanel(2, 0, raccountFP, 2);
		}

		// Bitacora
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		FlowPanel WFlowLogFP = new FlowPanel();
		BmFilter filterWFlowLogs = new BmFilter();
		filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getBmoWFlow().getCustomerId(), bmoCustomer.getId());
		getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
		UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, -1, bmoCustomer.getId());
		setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
		uiWFlowLog.show();
		formFlexTable.addPanel(3, 0, WFlowLogFP, 2);

		//		formFlexTable.addSectionLabel(4, 0, "Datos Relacionados", 2);
		//		formFlexTable.addField(5, 0, new UiCustomerEmailLabelList(getUiParams(), bmoCustomer.getId()));	
		//		formFlexTable.addField(6, 0, new UiCustomerPhoneLabelList(getUiParams(), bmoCustomer.getId()));	
		//		formFlexTable.addField(7, 0, new UiCustomerDateLabelList(getUiParams(), bmoCustomer.getId()));	
		//		formFlexTable.addField(8, 0, new UiCustomerRelativeLabelList(getUiParams(), bmoCustomer.getId()));
		//		formFlexTable.addField(9, 0, new UiCustomerAddressLabelList(getUiParams(), bmoCustomer.getId()));
		//		formFlexTable.addField(10, 0, new UiCustomerCompanyLabelList(getUiParams(), bmoCustomer.getId()));
		//		formFlexTable.addField(11, 0, new UiCustomerSocialLabelList(getUiParams(), bmoCustomer.getId()));
		//		formFlexTable.addField(12, 0, new UiCustomerContactLabelList(getUiParams(), bmoCustomer.getId()));
		//		formFlexTable.addField(13, 0, new UiCustomerWebLabelList(getUiParams(), bmoCustomer.getId()));
		//		formFlexTable.addField(14, 0, new UiCustomerBankAccountLabelList(getUiParams(), bmoCustomer.getId()));
		//		formFlexTable.addField(15, 0, new UiCustomerPaymentTypeLabelList(getUiParams(), bmoCustomer.getId()));
		//		formFlexTable.addField(16, 0, new UiCustomerNoteLabelList(getUiParams(), bmoCustomer.getId()));

		addToDP(formFlexTable);
	}
}
