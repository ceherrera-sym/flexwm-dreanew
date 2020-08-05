/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.ac;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


public class BmoSessionReview extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField sessionSaleId, userId, dateReview, comments, programSessionId, programSessionLevelId;

	BmoUser bmoUser = new BmoUser();
	BmoProgramSessionLevel bmoProgramSessionLevel = new BmoProgramSessionLevel();

	public BmoSessionReview() {
		super("com.flexwm.server.ac.PmSessionReview","sessionreviews", "sessionreviewid", "SERW", "Evaluación");

		dateReview = setField("datereview", "", "Fecha", 20, Types.DATE, false, BmFieldType.DATE, false);
		comments = setField("comments", "", "Comentario", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
		userId = setField("userid", "", "Instructor", 8, Types.INTEGER, false, BmFieldType.ID, false);
		sessionSaleId = setField("sessionsaleid", "", "Venta Sesión", 8, Types.INTEGER, false, BmFieldType.ID, false);
		programSessionId = setField("programsessionid", "", "Programa", 8, Types.INTEGER, false, BmFieldType.ID, false);
		programSessionLevelId = setField("programsessionlevelid", "", "Nivel Programa", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoUser().getCode(),
				getDateReview(),
				getBmoProgramSessionLevel().getSequence(),
				getBmoProgramSessionLevel().getName(),
				getBmoProgramSessionLevel().getBmoProgramSession().getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getDateReview())				
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getDateReview(), BmOrder.ASC)));
	}

	public BmField getSessionSaleId() {
		return sessionSaleId;
	}

	public void setSessionSaleId(BmField sessionSaleId) {
		this.sessionSaleId = sessionSaleId;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getDateReview() {
		return dateReview;
	}

	public void setDateReview(BmField dateReview) {
		this.dateReview = dateReview;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmField getProgramSessionId() {
		return programSessionId;
	}

	public void setProgramSessionId(BmField programSessionId) {
		this.programSessionId = programSessionId;
	}

	public BmField getProgramSessionLevelId() {
		return programSessionLevelId;
	}

	public void setProgramSessionLevelId(BmField programSessionLevelId) {
		this.programSessionLevelId = programSessionLevelId;
	}

	public BmoProgramSessionLevel getBmoProgramSessionLevel() {
		return bmoProgramSessionLevel;
	}

	public void setBmoProgramSessionLevel(BmoProgramSessionLevel bmoProgramSessionLevel) {
		this.bmoProgramSessionLevel = bmoProgramSessionLevel;
	}

}
