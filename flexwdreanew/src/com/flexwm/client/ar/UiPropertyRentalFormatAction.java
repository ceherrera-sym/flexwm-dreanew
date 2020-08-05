package com.flexwm.client.ar;

import java.util.Date;

import com.flexwm.shared.ar.BmoPropertyRental;
import com.google.gwt.user.client.Window;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormatActionHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoEmail;
import com.symgae.shared.sf.BmoFormat;

public class UiPropertyRentalFormatAction extends Ui implements UiFormatActionHandler {
	BmoPropertyRental bmoPropertyRental;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";
	
	public UiPropertyRentalFormatAction(UiParams uiParams, BmoPropertyRental bmoPropertyRental) {
		super(uiParams);
		this.bmoPropertyRental = bmoPropertyRental;
	}
	
	public UiPropertyRentalFormatAction(UiParams uiParams, BmoPropertyRental bmoPropertyRental, String formatName, String formatLink) {
		super(uiParams);
		this.bmoPropertyRental = bmoPropertyRental;
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
			bmoEmail.getTo().setValue(bmoPropertyRental.getBmoCustomer().getEmail().toString());
			bmoEmail.getToName().setValue(bmoPropertyRental.getBmoCustomer().getDisplayName().toString());
			bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			bmoEmail.getSubject().setValue(
					getUiParams().getSFParams().getMainAppTitle() 
					+ ": #" 
					+ bmoPropertyRental.getCode().toString() 
					+ " -  " + formatName);
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue(""
					+ "\n"
					+ "Oportunidad: " + bmoPropertyRental.getCode().toString() + " " + bmoPropertyRental.getName().toString() 
					+ "\n\n"
					+ "Descripción: " + bmoPropertyRental.getDescription().toString()
					+ "\n\n "
					+ "Fecha: " + bmoPropertyRental.getStartDate().toString()
					+ "\n\n "
					+ "Cliente: " + bmoPropertyRental.getBmoCustomer().getCode().toString() + " " + bmoPropertyRental.getBmoCustomer().getDisplayName().toString()
					+ "\n\n "
					+ " ");
			bmoEmail.getFixedBody().setValue("<br><br>Para visualizar el Documento, favor de hacer click <a href=\"" + getUiParams().getSFParams().getAppURL() + formatLink + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoPropertyRental.getId()) + "&resource=oppo" + (new Date().getTime() * 456) +"\">Aqui</a>");
			bmoEmail.getWFlowId().setValue(bmoPropertyRental.getBmoWFlow().getId());
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}
		
		UiPropertyRentalEmailForm  uiQuoteEmailForm = new UiPropertyRentalEmailForm(getUiParams(), 0, bmoEmail, bmoPropertyRental);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiQuoteEmailForm.show();
	}

	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoPropertyRental.getId()), "_blank", "");
	}
}
