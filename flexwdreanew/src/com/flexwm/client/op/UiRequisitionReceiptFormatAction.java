package com.flexwm.client.op;

import java.util.Date;

import com.flexwm.shared.op.BmoRequisitionReceipt;
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

public class UiRequisitionReceiptFormatAction extends Ui implements UiFormatActionHandler {
	BmoRequisitionReceipt bmoRequisitionReceipt;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";
	
	public UiRequisitionReceiptFormatAction(UiParams uiParams, BmoRequisitionReceipt bmoRequisitionReceipt) {
		super(uiParams);
		this.bmoRequisitionReceipt = bmoRequisitionReceipt;
	}
	
	public UiRequisitionReceiptFormatAction(UiParams uiParams, BmoRequisitionReceipt bmoRequisitionReceipt, String formatName, String formatLink) {
		super(uiParams);
		this.bmoRequisitionReceipt = bmoRequisitionReceipt;
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
			bmoEmail.getSubject().setValue(getUiParams().getSFParams().getMainAppTitle() + ": #" + getUiParams().getSFParams().getAppCode()  + bmoRequisitionReceipt.getCode().toString() + " -  " + formatName);
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue("Se envía el Documento [" + formatName + "] de: [" + 
					bmoRequisitionReceipt.getCode().toString() + " - " + bmoRequisitionReceipt.getName().toString() + "] " +
										" Fecha de Recibo: [" + bmoRequisitionReceipt.getReceiptDate().toString() + "]. ");
			bmoEmail.getFixedBody().setValue("<br><br>Para revisar el documento, haz click <a href=\"" + getUiParams().getSFParams().getAppURL() + formatLink + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoRequisitionReceipt.getId()) + "&resource=oppo" + (new Date().getTime() * 456) +"\">Aqui</a>");
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}
		
		UiEmailForm uiRequisitionReceiptEmailForm = new UiEmailForm(getUiParams(), 0, bmoEmail);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiRequisitionReceiptEmailForm.show();
	}

	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoRequisitionReceipt.getId()), "_blank", "");
	}
	
}
