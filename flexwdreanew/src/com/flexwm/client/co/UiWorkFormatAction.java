package com.flexwm.client.co;

import com.flexwm.shared.co.BmoWork;
import com.google.gwt.user.client.Window;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormatActionHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoFormat;


public class UiWorkFormatAction extends Ui implements UiFormatActionHandler {
	BmoWork bmoWork;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";

	public UiWorkFormatAction(UiParams uiParams, BmoWork bmoWork) {
		super(uiParams);
		this.bmoWork = bmoWork;
	}

	public UiWorkFormatAction(UiParams uiParams, BmoWork bmoWork, String formatName, String formatLink) {
		super(uiParams);
		this.bmoWork = bmoWork;
		this.formatName = formatName;
		this.formatLink = formatLink;
	}

	@Override
	public void send(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		formatName = bmoFormat.getName().toString();
		formatLink = bmoFormat.getLink().toString();
	}


	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoWork.getId()), "_blank", "");
	}

}
