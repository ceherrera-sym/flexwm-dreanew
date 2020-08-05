package com.flexwm.client.fi;

import java.util.Date;

import com.flexwm.shared.fi.BmoPaccount;
import com.google.gwt.user.client.Window;
import com.symgae.client.sf.UiEmailForm;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormatActionHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoEmail;
import com.symgae.shared.sf.BmoFormat;

public class UiPaccountFormatAction extends Ui implements UiFormatActionHandler {
	BmoPaccount bmoPaccount;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";
	
	public UiPaccountFormatAction(UiParams uiParams, BmoPaccount bmoPaccount) {
		super(uiParams);
		this.bmoPaccount = bmoPaccount;
	}
	
	public UiPaccountFormatAction(UiParams uiParams, BmoPaccount bmoPaccount, String formatName, String formatLink) {
		super(uiParams);
		this.bmoPaccount = bmoPaccount;
		this.formatName = formatName;
		this.formatLink = formatLink;
	}

	@Override
	public void send(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		formatName = bmoFormat.getName().toString();
		formatLink = bmoFormat.getLink().toString();
		sendEmail();
	}

	public void sendEmail() {
		BmoEmail bmoEmail = new BmoEmail();
		
		try {
			bmoEmail.getToName().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			bmoEmail.getSubject().setValue(getUiParams().getSFParams().getMainAppTitle() + ": #" + getUiParams().getSFParams().getAppCode()  + bmoPaccount.getCode().toString() + " -  " + formatName);
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue("Se envía el Documento [" + formatName + "] de: [" + 
					bmoPaccount.getCode().toString() + " - " + bmoPaccount.getInvoiceno().toString() + "] " +
										" Fecha: [" + bmoPaccount.getReceiveDate().toString() + "]. ");
			bmoEmail.getFixedBody().setValue("<br><br>Para revisar el documento, haz click <a href=\"" + getUiParams().getSFParams().getAppURL() + formatLink + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoPaccount.getId()) + "&resource=oppo" + (new Date().getTime() * 456) +"\">Aqui</a>");
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}
		
		UiEmailForm uiPaccountEmailForm = new UiEmailForm(getUiParams(), 0, bmoEmail);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiPaccountEmailForm.show();
	}

	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoPaccount.getId()), "_blank", "");
	}
	
}
