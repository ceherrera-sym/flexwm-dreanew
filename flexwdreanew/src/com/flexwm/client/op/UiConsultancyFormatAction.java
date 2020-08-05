package com.flexwm.client.op;

import java.util.Date;
import com.flexwm.shared.op.BmoConsultancy;
import com.google.gwt.user.client.Window;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormatActionHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoEmail;
import com.symgae.shared.sf.BmoFormat;


public class UiConsultancyFormatAction extends Ui implements UiFormatActionHandler {

	BmoConsultancy bmoConsultancy;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";

	public UiConsultancyFormatAction(UiParams uiParams, BmoConsultancy bmoConsultancy) {
		super(uiParams);
		this.bmoConsultancy = bmoConsultancy;
	}

	public UiConsultancyFormatAction(UiParams uiParams, BmoConsultancy bmoConsultancy, String formatName, String formatLink) {
		super(uiParams);
		this.bmoConsultancy = bmoConsultancy;
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
			bmoEmail.getTo().setValue(bmoConsultancy.getBmoCustomer().getEmail().toString());
			bmoEmail.getToName().setValue(bmoConsultancy.getBmoCustomer().getDisplayName().toString());
			bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			bmoEmail.getSubject().setValue(
					getUiParams().getSFParams().getMainAppTitle() 
					+ ": #" 
					+ bmoConsultancy.getCode().toString() 
					+ " -  " + formatName);
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue(""
					+ "\n"
					+ "Venta: " + bmoConsultancy.getCode().toString() + " " + bmoConsultancy.getName().toString() 
					+ "\n\n"
					+ "Descripción: " + bmoConsultancy.getDescription().toString()
					+ "\n\n "
					+ "Fecha: " + bmoConsultancy.getStartDate().toString()
					+ "\n\n "
					+ "Cliente: " + bmoConsultancy.getBmoCustomer().getCode().toString() + " " + bmoConsultancy.getBmoCustomer().getDisplayName().toString()
					+ "\n\n "
					+ " ");
			bmoEmail.getFixedBody().setValue("<br><br>Para visualizar el Documento, favor de hacer click <a href=\"" + getUiParams().getSFParams().getAppURL() + formatLink + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoConsultancy.getId()) + "&resource=oppo" + (new Date().getTime() * 456) +"\">Aqui</a>");
			bmoEmail.getWFlowId().setValue(bmoConsultancy.getBmoWFlow().getId());
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}

		UiConsultancyEmailForm uiQuoteEmailForm = new UiConsultancyEmailForm(getUiParams(), 0, bmoEmail, bmoConsultancy);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiQuoteEmailForm.show();
	}

	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoConsultancy.getId()), "_blank", "");
	}

}
