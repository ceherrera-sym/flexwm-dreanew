package com.flexwm.client.op;

import java.util.Date;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.user.client.Window;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormatActionHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoEmail;
import com.symgae.shared.sf.BmoFormat;

public class UiOrderFormatAction extends Ui implements UiFormatActionHandler {
	BmoOrder bmoOrder;
	BmoFormat bmoFormat;
	String formatName = "";
	String formatLink = "";
	
	public UiOrderFormatAction(UiParams uiParams, BmoOrder bmoOrder) {
		super(uiParams);
		this.bmoOrder = bmoOrder;
	}
	
	public UiOrderFormatAction(UiParams uiParams, BmoOrder bmoOrder, String formatName, String formatLink) {
		super(uiParams);
		this.bmoOrder = bmoOrder;
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
			bmoEmail.getTo().setValue(bmoOrder.getBmoCustomer().getEmail().toString());
			bmoEmail.getToName().setValue(bmoOrder.getBmoCustomer().getDisplayName().toString());
			bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			bmoEmail.getSubject().setValue(
					getUiParams().getSFParams().getMainAppTitle() 
					+ ": #" 
					+ bmoOrder.getCode().toString() 
					+ " -  " + formatName);
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue(""
					+ "\n"
					+ "Pedido: " + bmoOrder.getCode().toString() + " " + bmoOrder.getName().toString() 
					+ "\n\n"
					+ "Descripción: " + bmoOrder.getDescription().toString()
					+ "\n\n "
					+ "Fecha: " + bmoOrder.getLockStart().toString()
					+ "\n\n "
					+ "Cliente: " + bmoOrder.getBmoCustomer().getCode().toString() + " " + bmoOrder.getBmoCustomer().getDisplayName().toString()
					+ "\n\n "
					+ " ");
			bmoEmail.getFixedBody().setValue("<br><br>Para visualizar el Documento, favor de hacer click <a href=\"" + getUiParams().getSFParams().getAppURL() + formatLink + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoOrder.getId()) + "&resource=oppo" + (new Date().getTime() * 456) +"\">Aqui</a>");
			bmoEmail.getWFlowId().setValue(bmoOrder.getBmoWFlow().getId());
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}
		
		UiOrderEmailForm uiQuoteEmailForm = new UiOrderEmailForm(getUiParams(), 0, bmoEmail, bmoOrder);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiQuoteEmailForm.show();
	}

	@Override
	public void view(BmObject bmObject) {
		bmoFormat = (BmoFormat)bmObject;
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), bmoFormat.getLink().toString() + "?foreignId=" + bmoOrder.getId()), "_blank", "");
	}
	
}
