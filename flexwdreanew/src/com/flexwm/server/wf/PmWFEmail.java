/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.StringTokenizer;
import com.symgae.server.PmObject;
import com.flexwm.shared.wf.BmoWFEmail;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmWFEmail extends PmObject {
	BmoWFEmail bmoWFEmail;

	public PmWFEmail(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFEmail = new BmoWFEmail();
		setBmObject(bmoWFEmail);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoWFEmail());
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoWFEmail bmoWFEmail = (BmoWFEmail)bmObject;

		// Si es registro nuevo, enviar y guardar; si no, NO se guardan cambios
		if (!(bmoWFEmail.getId() > 0)) {
			bmoWFEmail.getSentDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

			// Enviar correo
			sendMail(bmoWFEmail);

			System.out.println("El WFlowId es (" + bmoWFEmail.getWFlowId().toInteger() + ")");

			// Crear bitacora wflow
			if (bmoWFEmail.getWFlowId().toInteger() > 0) {
				PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

				pmWFlowLog.addEmailLog(pmConn, bmoWFEmail, bmUpdateResult);
			}

			super.save(pmConn, bmoWFEmail, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	private void sendMail(BmoWFEmail bmoWFEmail) throws SFException {
		String body = HtmlUtil.mailBodyFormat(getSFParams(), bmoWFEmail.getSubject().toString(), bmoWFEmail.getBody().toString() + " " + bmoWFEmail.getFixedBody().toString());

		// Lista de distribución de correos
		ArrayList<SFMailAddress> toList = new ArrayList<SFMailAddress>();
		// Si el campo de To: tiene punto-comas, dividir los correos
		String to = bmoWFEmail.getTo().toString();
		if (to.indexOf(';') > 0) {
			StringTokenizer st = new StringTokenizer(to, ";");
			while (st.hasMoreTokens()) {
				toList.add(new SFMailAddress(st.nextToken(), bmoWFEmail.getToName().toString()));
			}
		} else {
			toList.add(new SFMailAddress(bmoWFEmail.getTo().toString(), bmoWFEmail.getToName().toString()));			
		}

		// Si hay a quien copiar
		if (!bmoWFEmail.getCp().toString().equals("")) {
			String cp = bmoWFEmail.getCp().toString();
			if (cp.indexOf(';') > 0) {
				StringTokenizer st = new StringTokenizer(cp, ";");
				while (st.hasMoreTokens()) {
					toList.add(new SFMailAddress(st.nextToken(), ""));
				}
			} else {
				toList.add(new SFMailAddress(bmoWFEmail.getCp().toString(), ""));			
			}
		}

		SFSendMail.send(getSFParams(), 
				toList, 
				bmoWFEmail.getFrom().toString(), 
				bmoWFEmail.getFromName().toString(),
				bmoWFEmail.getReplyTo().toString(),
				bmoWFEmail.getSubject().toString(), 
				body);
	}
}
