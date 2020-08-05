package com.flexwm.client.op;

import java.util.Date;

import com.flexwm.shared.op.BmoOrderDelivery;
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

public class UiOrderDeliveryFormatAction extends Ui implements UiFormatActionHandler {
	BmoOrderDelivery bmoOrderDelivery;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";
	
	public UiOrderDeliveryFormatAction(UiParams uiParams, BmoOrderDelivery bmoOrderDelivery) {
		super(uiParams);
		this.bmoOrderDelivery = bmoOrderDelivery;
	}
	
	public UiOrderDeliveryFormatAction(UiParams uiParams, BmoOrderDelivery bmoOrderDelivery, String formatName, String formatLink) {
		super(uiParams);
		this.bmoOrderDelivery = bmoOrderDelivery;
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
			bmoEmail.getSubject().setValue(getUiParams().getSFParams().getMainAppTitle() + ": #" + getUiParams().getSFParams().getAppCode()  + bmoOrderDelivery.getCode().toString() + " -  " + formatName);
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue("Se envía el Documento [" + formatName + "] de: [" + 
					bmoOrderDelivery.getCode().toString() + " - " + bmoOrderDelivery.getName().toString() + "] " +
										" Fecha de Envío: [" + bmoOrderDelivery.getDeliveryDate().toString() + "]. ");
			bmoEmail.getFixedBody().setValue("<br><br>Para revisar el documento, haz click <a href=\"" + getUiParams().getSFParams().getAppURL() + formatLink + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoOrderDelivery.getId()) + "&resource=oppo" + (new Date().getTime() * 456) +"\">Aqui</a>");
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}
		
		UiEmailForm uiOrderDeliveryEmailForm = new UiEmailForm(getUiParams(), 0, bmoEmail);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiOrderDeliveryEmailForm.show();
	}

	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoOrderDelivery.getId()), "_blank", "");
	}
	
}
