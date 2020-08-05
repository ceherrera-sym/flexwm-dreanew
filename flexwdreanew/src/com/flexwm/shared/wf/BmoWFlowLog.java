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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


public class BmoWFlowLog extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField type, comments, data, link, logdate, wflowId, userId;
	private BmoUser bmoUser = new BmoUser();;
	private BmoWFlow bmoWFlow = new BmoWFlow();

	public static char TYPE_SYS = 'S';
	public static char TYPE_WFLOW = 'W';
	public static char TYPE_EMAIL = 'E';
	public static char TYPE_PHONE = 'P';
	public static char TYPE_VISIT = 'V';
	public static char TYPE_DATA = 'D';
	public static char TYPE_OTHER = 'O';

	public BmoWFlowLog() {
		super("com.flexwm.server.wf.PmWFlowLog", "wflowlogs", "wflowlogid", "WFLG", "Bitácora WFlow");

		// Campo de datos
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_WFLOW, "WFlow", "./icons/wflw.png"),
				new BmFieldOption(TYPE_EMAIL, "Email", "./icons/email.png"),
				new BmFieldOption(TYPE_PHONE, "Teléfono", "./icons/phone.png"),
				new BmFieldOption(TYPE_VISIT, "Visita", "./icons/users.png"),
				new BmFieldOption(TYPE_DATA, "Archivo", "./icons/file-text-o.png"),
				new BmFieldOption(TYPE_SYS, "Sistema", "./icons/window.png"),
				new BmFieldOption(TYPE_OTHER, "Otros", "./icons/ellipsis-circle-o.png")			
				)));

		logdate = setField("logdate", "", "Fecha", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		comments = setField("comments", "", "Comentarios", 2048, Types.VARCHAR, true, BmFieldType.STRING, false);
		link = setField("link", "", "Liga", 500, Types.VARCHAR, true, BmFieldType.WWW, false);
		data = setField("data", "", "Datos", 16000000, Types.BLOB, true, BmFieldType.HTML, false);
		userId = setField("userid", "", "Usuario", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wflowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWFlow().getCode(),
				getBmoWFlow().getName(),
				getLogdate(),
				getType(), 
				getBmoUser().getCode(),
				getComments(),
				getData(),
				getLink()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getComments()),
				new BmSearchField(getBmoWFlow().getCode()),
				new BmSearchField(getBmoWFlow().getName()),
				new BmSearchField(getBmoUser().getCode())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getLogdate(), BmOrder.DESC), 
				new BmOrder(getKind(), getIdField(), BmOrder.DESC)));
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getLogdate() {
		return logdate;
	}

	public void setLogdate(BmField logdate) {
		this.logdate = logdate;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getWFlowId() {
		return wflowId;
	}

	public void setWFlowId(BmField wflowId) {
		this.wflowId = wflowId;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmField getData() {
		return data;
	}

	public void setData(BmField data) {
		this.data = data;
	}

	public BmField getLink() {
		return link;
	}

	public void setLink(BmField link) {
		this.link = link;
	}

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}
}
