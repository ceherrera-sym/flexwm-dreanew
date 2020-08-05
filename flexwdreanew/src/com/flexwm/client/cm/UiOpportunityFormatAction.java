package com.flexwm.client.cm;

import java.util.Date;

import com.flexwm.shared.cm.BmoOpportunity;
import com.google.gwt.user.client.Window;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormatActionHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoEmail;
import com.symgae.shared.sf.BmoFormat;

public class UiOpportunityFormatAction extends Ui implements UiFormatActionHandler {
	BmoOpportunity bmoOpportunity;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";
	
	public UiOpportunityFormatAction(UiParams uiParams, BmoOpportunity bmoOpportunity) {
		super(uiParams);
		this.bmoOpportunity = bmoOpportunity;
	}
	
	public UiOpportunityFormatAction(UiParams uiParams, BmoOpportunity bmoOpportunity, String formatName, String formatLink) {
		super(uiParams);
		this.bmoOpportunity = bmoOpportunity;
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
			bmoEmail.getTo().setValue(bmoOpportunity.getBmoCustomer().getEmail().toString());
			bmoEmail.getToName().setValue(bmoOpportunity.getBmoCustomer().getDisplayName().toString());
			bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			bmoEmail.getSubject().setValue(
					getUiParams().getSFParams().getMainAppTitle() 
					+ ": #" 
					+ bmoOpportunity.getCode().toString() 
					+ " -  " + formatName);
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue(""
					+ "\n"
					+ "Oportunidad: " + bmoOpportunity.getCode().toString() + " " + bmoOpportunity.getName().toString() 
					+ "\n\n"
					+ "Descripción: " + bmoOpportunity.getDescription().toString()
					+ "\n\n "
					+ "Fecha: " + bmoOpportunity.getStartDate().toString()
					+ "\n\n "
					+ "Cliente: " + bmoOpportunity.getBmoCustomer().getCode().toString() + " " + bmoOpportunity.getBmoCustomer().getDisplayName().toString()
					+ "\n\n "
					+ " ");
			bmoEmail.getFixedBody().setValue("<br><br>Para visualizar el Documento, favor de hacer click <a href=\"" + getUiParams().getSFParams().getAppURL() + formatLink + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoOpportunity.getId()) + "&resource=oppo" + (new Date().getTime() * 456) +"\">Aqui</a>");
			bmoEmail.getWFlowId().setValue(bmoOpportunity.getBmoWFlow().getId());
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}
		
		UiOpportunityEmailForm uiQuoteEmailForm = new UiOpportunityEmailForm(getUiParams(), 0, bmoEmail, bmoOpportunity);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiQuoteEmailForm.show();
	}

	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoOpportunity.getId()), "_blank", "");
	}
	
}
