/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.wf;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoWFEmail extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField to, toName, cp, from, fromName, replyTo, subject, body, fixedBody, sentDate, userId, programId, wFlowId;

	public BmoWFEmail() {
		super("com.flexwm.server.wf.PmWFEmail", "emails", "emailid", "EMAI", "Correos");
		to = setField("to", "", "Para", 255, Types.VARCHAR, false, BmFieldType.EMAIL, false);
		toName = setField("toname", "", "Nombre", 255, Types.VARCHAR, false, BmFieldType.STRING, false);
		cp = setField("cp", "", "Copia", 255, Types.VARCHAR, true, BmFieldType.EMAIL, false);
		from = setField("from", "", "De", 255, Types.VARCHAR, false, BmFieldType.EMAIL, false);
		fromName = setField("fromname", "", "Nombre", 255, Types.VARCHAR, false, BmFieldType.STRING, false);
		replyTo = setField("replyto", "", "Respoder A", 255, Types.VARCHAR, true, BmFieldType.EMAIL, false);
		
		subject = setField("subject", "", "Asunto", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		body = setField("body", "", "Texto", 10000, Types.VARCHAR, true, BmFieldType.STRING, false);
		fixedBody = setField("fixedbody", "", "Texto Fijo", 10000, Types.VARCHAR, true, BmFieldType.STRING, false);
		sentDate = setField("sentdate", "", "Fecha", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		userId = setField("userid", "", "Usuario", 10, Types.INTEGER, false, BmFieldType.ID, false);
		programId = setField("programid", "", "Programa", 10, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowId = setField("wflowid", "", "Flujo", 10, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getTo(), 
						getFrom(), 
						getSubject(),
						getSentDate()
						));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getTo()), 
				new BmSearchField(getSubject())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getSentDate(), BmOrder.DESC)));
	}

	public BmField getTo() {
		return to;
	}

	public void setTo(BmField to) {
		this.to = to;
	}

	public BmField getCp() {
		return cp;
	}

	public void setCp(BmField cp) {
		this.cp = cp;
	}

	public BmField getFrom() {
		return from;
	}

	public void setFrom(BmField from) {
		this.from = from;
	}

	public BmField getSubject() {
		return subject;
	}

	public void setSubject(BmField subject) {
		this.subject = subject;
	}

	public BmField getBody() {
		return body;
	}

	public void setBody(BmField body) {
		this.body = body;
	}

	public BmField getFixedBody() {
		return fixedBody;
	}

	public void setFixedBody(BmField fixedBody) {
		this.fixedBody = fixedBody;
	}

	public BmField getSentDate() {
		return sentDate;
	}

	public void setSentDate(BmField sentDate) {
		this.sentDate = sentDate;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getProgramId() {
		return programId;
	}

	public void setProgramId(BmField programId) {
		this.programId = programId;
	}

	public BmField getToName() {
		return toName;
	}

	public void setToName(BmField toName) {
		this.toName = toName;
	}

	public BmField getFromName() {
		return fromName;
	}

	public void setFromName(BmField fromName) {
		this.fromName = fromName;
	}

	public BmField getWFlowId() {
		return wFlowId;
	}

	public void setWFlowId(BmField wFlowId) {
		this.wFlowId = wFlowId;
	}

	public BmField getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(BmField replyTo) {
		this.replyTo = replyTo;
	}
	
}
