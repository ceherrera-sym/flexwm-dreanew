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
import com.symgae.shared.sf.BmoUser;


public class BmoWFlowUserBlockDate extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField startDate, endDate, comments, userId;
	
	BmoUser bmoUser = new BmoUser();

	public BmoWFlowUserBlockDate() {
		super("com.flexwm.server.wf.PmWFlowUserBlockDate", "wflowuserblockdates", "wflowuserblockdateid", "WFUB", "Fecha Usuar. Bloq.");
		
		startDate = setField("startdate", "", "Inicio Bloq.", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin Bloq.", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, false, BmFieldType.STRING, false);
		
		userId = setField("userid", "", "Colaborador", 20, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoUser().getCode(),
				getBmoUser().getFirstname(),
				getBmoUser().getFatherlastname(),
				getBmoUser().getMotherlastname(),
				getStartDate(), 
				getEndDate(), 
				getComments()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getComments())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getStartDate(), BmOrder.ASC)));
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getEndDate() {
		return endDate;
	}

	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}
}
