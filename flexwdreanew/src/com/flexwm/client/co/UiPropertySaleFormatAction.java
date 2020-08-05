package com.flexwm.client.co;

import java.util.Date;

import com.flexwm.shared.co.BmoPropertySale;
import com.google.gwt.user.client.Window;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormatActionHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoEmail;
import com.symgae.shared.sf.BmoFormat;

public class UiPropertySaleFormatAction extends Ui implements UiFormatActionHandler {
	BmoPropertySale bmoPropertySale;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";
	
	public UiPropertySaleFormatAction(UiParams uiParams, BmoPropertySale bmoPropertySale) {
		super(uiParams);
		this.bmoPropertySale = bmoPropertySale;
	}
	
	public UiPropertySaleFormatAction(UiParams uiParams, BmoPropertySale bmoPropertySale, String formatName, String formatLink) {
		super(uiParams);
		this.bmoPropertySale = bmoPropertySale;
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
//			bmoEmail.getTo().setValue();
			bmoEmail.getToName().setValue(bmoPropertySale.getBmoCustomer().getDisplayName().toString());
			bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			bmoEmail.getSubject().setValue(getUiParams().getSFParams().getMainAppTitle() + ": #" + getUiParams().getSFParams().getAppCode()  + bmoPropertySale.getCode().toString() + " -  " + formatName);
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue("Se envía el Documento [" + formatName + "] de la Venta de Inmueble: [" + 
					bmoPropertySale.getCode().toString() + "] " +
										" de Fecha: [" + bmoPropertySale.getStartDate().toString() + "]. ");
			bmoEmail.getFixedBody().setValue("<br><br>Para revisar el documento, haz click <a href=\"" + getUiParams().getSFParams().getAppURL() + formatLink + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoPropertySale.getId()) + "&resource=oppo" + (new Date().getTime() * 456) +"\">Aqui</a>");
			bmoEmail.getWFlowId().setValue(bmoPropertySale.getBmoWFlow().getId());
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}
		
		UiPropertySaleEmailForm uiQuoteEmailForm = new UiPropertySaleEmailForm(getUiParams(), 0, bmoEmail, bmoPropertySale);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiQuoteEmailForm.show();
	}

	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoPropertySale.getId()), "_blank", "");
	}
}
