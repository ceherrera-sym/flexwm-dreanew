package com.flexwm.client.cm;

import java.util.Date;

import com.flexwm.shared.cm.BmoProject;
import com.google.gwt.user.client.Window;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormatActionHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoEmail;
import com.symgae.shared.sf.BmoFormat;

public class UiProjectFormatAction extends Ui implements UiFormatActionHandler {
	BmoProject bmoProject;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";
	
	public UiProjectFormatAction(UiParams uiParams, BmoProject bmoProject) {
		super(uiParams);
		this.bmoProject = bmoProject;
	}
	
	public UiProjectFormatAction(UiParams uiParams, BmoProject bmoProject, String formatName, String formatLink) {
		super(uiParams);
		this.bmoProject = bmoProject;
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
			bmoEmail.getTo().setValue(bmoProject.getBmoCustomer().getEmail().toString());
			bmoEmail.getToName().setValue(bmoProject.getBmoCustomer().getDisplayName().toString());
			bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			bmoEmail.getSubject().setValue(
					getUiParams().getSFParams().getMainAppTitle() 
					+ ": #" 
					+ bmoProject.getCode().toString() 
					+ " -  " + formatName);
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue(""
					+ "\n"
					+ "Oportunidad: " + bmoProject.getCode().toString() + " " + bmoProject.getName().toString() 
					+ "\n\n"
					+ "Descripción: " + bmoProject.getDescription().toString()
					+ "\n\n "
					+ "Fecha: " + bmoProject.getStartDate().toString()
					+ "\n\n "
					+ "Cliente: " + bmoProject.getBmoCustomer().getCode().toString() + " " + bmoProject.getBmoCustomer().getDisplayName().toString()
					+ "\n\n "
					+ " ");
			bmoEmail.getFixedBody().setValue("<br><br>Para visualizar el Documento, favor de hacer click <a href=\"" + getUiParams().getSFParams().getAppURL() + formatLink + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoProject.getId()) + "&resource=oppo" + (new Date().getTime() * 456) +"\">Aqui</a>");
			bmoEmail.getWFlowId().setValue(bmoProject.getBmoWFlow().getId());
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}
		
		UiProjectEmailForm uiQuoteEmailForm = new UiProjectEmailForm(getUiParams(), 0, bmoEmail, bmoProject);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiQuoteEmailForm.show();
	}

	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoProject.getId()), "_blank", "");
	}
}
