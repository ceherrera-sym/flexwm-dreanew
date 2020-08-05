package com.flexwm.server.cm;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.flexwm.server.wf.IWFlowAction;
import com.flexwm.shared.cm.BmoCustomerEmail;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.sf.BmoUser;


public class WFlowProjectPollEmail implements IWFlowAction {

	@Override
	public void action(SFParams sFParams, PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {

		// Se autoriza el proyecto dependiendo avance de la tarea
		PmProject pmProject = new PmProject(sFParams);
		BmoProject bmoProject = new BmoProject();
		bmoProject = (BmoProject)pmProject.getBy(pmConn, 
				bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoProject.getOrderId().getName());

		sendPollMail(sFParams, bmoProject);
	}

	public void sendPollMail(SFParams sFParams, BmoProject bmoProject) throws SFException {
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();

		// Datos del correos del cliente
		PmCustomerEmail pmCustomerEmail = new PmCustomerEmail(sFParams);
		BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
		BmFilter customerFilter = new BmFilter();
		customerFilter.setValueFilter(bmoCustomerEmail.getKind(), bmoCustomerEmail.getCustomerId(), bmoProject.getCustomerId().toInteger());
		ArrayList<BmObject> customerEmailList = pmCustomerEmail.list(customerFilter);
		Iterator<BmObject> i = customerEmailList.iterator();
		int index = 0;
		while (i.hasNext()) {
			bmoCustomerEmail = (BmoCustomerEmail)i.next();
			SFMailAddress mailAddress = new SFMailAddress(bmoCustomerEmail.getEmail().toString(),
					bmoProject.getBmoCustomer().getFirstname().toString() + " " +
							bmoProject.getBmoCustomer().getFatherlastname().toString());
			mailList.add(mailAddress);
			index++;
		}
		if (index == 0) throw new SFException("El cliente no tiene cuentas de correo asignadas.");

		// Datos del usuario que está enviando el correo
		mailList.add(new SFMailAddress(sFParams.getLoginInfo().getEmailAddress(),
				sFParams.getLoginInfo().getEmailAddress()));

		// Datos del productor del proyecto
		// Volver a validar si el usuario esta activo
		if (bmoProject.getBmoUser().getStatus().toChar() == BmoUser.STATUS_ACTIVE) {
			mailList.add(new SFMailAddress(bmoProject.getBmoUser().getEmail().toString(),
					bmoProject.getBmoUser().getFirstname().toString() + " " +
							bmoProject.getBmoUser().getFatherlastname().toString()
					));
		}

		String subject = "#" + sFParams.getAppCode()  + bmoProject.getCode().toString() + " - Proyecto ";
		String link = "<a href=\"" + sFParams.getAppURL() + 
				"/frm/flex_customerpoll.jsp?h=" + new Date().getTime() + "proj&w=EXT&z=" + 
				SFServerUtil.encryptId(bmoProject.getId()) + "&r=interv" + (new Date().getTime() * 456) +"\">Aqui</a>";

		String msgBody = HtmlUtil.mailBodyFormat(sFParams, subject, 
				" El Proyecto " + bmoProject.getCode().toString() + " " +
						" ( " + bmoProject.getBmoWFlow().getBmoWFlowType().getName().toString() + ") " +
						" de Fecha: " + bmoProject.getStartDate().toString() + " ha sido Concluido. " +
						" Agradeceremos sus comentarios y opinión, contestando una encuesta. " +
						" Para visualizarla haz click " + link + ".<br><br>" +
				" No es necesario contestar (Reply) este Correo. ");

		// Si hay destinatarios, enviar los correos
		if (mailList.size() > 0) SFSendMail.send(sFParams,
				mailList, 
				sFParams.getBmoSFConfig().getEmail().toString(), 
				sFParams.getMainAppTitle(), 
				subject, 
				msgBody);
	}
}
