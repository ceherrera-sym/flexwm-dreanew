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

import com.flexwm.shared.op.BmoOrder;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoOrderSession extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField attended, seriesApplyAll, type,
	seriesMonday, seriesTuesday, seriesWednesday, seriesThursday, seriesFriday, seriesSaturday, seriesSunday,
	orderId, sessionId,description;

	BmoSession bmoSession = new BmoSession();
	BmoOrder bmoOrder = new BmoOrder();

	public static char TYPE_NORMAL = 'N';	
	public static char TYPE_EXEMPT = 'X';
	public static char TYPE_REPLACEMENT = 'R';
	
	public static String ACTION_CHECKATTENDED = "CHECKATTENDED";

	public BmoOrderSession() {
		super("com.flexwm.server.ac.PmOrderSession","ordersessions", "ordersessionid", "ORSS", "Sesiones Pedido");

		attended = setField("attended", "0", "Asistio?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		type = setField("type", "" + TYPE_NORMAL, "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_NORMAL, "Normal", "./icons/status_authorized.png"),
				new BmFieldOption(TYPE_EXEMPT, "Exenta", "./icons/status_cancelled.png"),
				new BmFieldOption(TYPE_REPLACEMENT, "Reposición", "./icons/status_revision.png")				
				)));
		
		seriesApplyAll = setField("seriesapplyall", "", "Apartar Serie?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesMonday = setField("seriesmonday", "1", "Lunes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesTuesday = setField("seriestuesday", "1", "Martes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesWednesday = setField("serieswednesday", "1", "Miércoles", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesThursday = setField("seriesthursday", "1", "Jueves", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesFriday = setField("seriesFriday", "1", "Viernes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesSaturday = setField("seriessaturday", "", "Sábado", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesSunday = setField("seriessunday", "", "Domingo", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
		sessionId = setField("sessionid", "", "Sesión", 8, Types.INTEGER, false, BmFieldType.ID, false);
		description = setField("description", "", "Descripción", 512, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoSession().getBmoSessionType().getName(),
				getBmoOrder().getBmoCustomer().getCode(),
				getBmoOrder().getBmoCustomer().getDisplayName(),
				getBmoSession().getBmoUser().getCode(),
				getBmoSession().getStartDate(),
				getBmoSession().getBmoSessionType().getDuration(),
				getType(),
				getAttended()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(bmoSession.getBmoSessionType().getName())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoSession().getStartDate(), BmOrder.ASC))
				);
	}

	public BmField getAttended() {
		return attended;
	}

	public void setAttended(BmField attended) {
		this.attended = attended;
	}

	public BmField getSessionId() {
		return sessionId;
	}

	public void setSessionId(BmField sessionId) {
		this.sessionId = sessionId;
	}

	public BmoSession getBmoSession() {
		return bmoSession;
	}

	public void setBmoSession(BmoSession bmoSession) {
		this.bmoSession = bmoSession;
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

//	public BmoOrder getBmoOrder() {
//		return bmoOrder;
//	}
//
//	public void setBmoOrder(BmoOrder bmoOrder) {
//		this.bmoOrder = bmoOrder;
//	}

	public BmField getSeriesApplyAll() {
		return seriesApplyAll;
	}

	public void setSeriesApplyAll(BmField seriesApplyAll) {
		this.seriesApplyAll = seriesApplyAll;
	}

	public BmField getSeriesMonday() {
		return seriesMonday;
	}

	public void setSeriesMonday(BmField seriesMonday) {
		this.seriesMonday = seriesMonday;
	}

	public BmField getSeriesTuesday() {
		return seriesTuesday;
	}

	public void setSeriesTuesday(BmField seriesTuesday) {
		this.seriesTuesday = seriesTuesday;
	}

	public BmField getSeriesWednesday() {
		return seriesWednesday;
	}

	public void setSeriesWednesday(BmField seriesWednesday) {
		this.seriesWednesday = seriesWednesday;
	}

	public BmField getSeriesThursday() {
		return seriesThursday;
	}

	public void setSeriesThursday(BmField seriesThursday) {
		this.seriesThursday = seriesThursday;
	}

	public BmField getSeriesFriday() {
		return seriesFriday;
	}

	public void setSeriesFriday(BmField seriesFriday) {
		this.seriesFriday = seriesFriday;
	}

	public BmField getSeriesSaturday() {
		return seriesSaturday;
	}

	public void setSeriesSaturday(BmField seriesSaturday) {
		this.seriesSaturday = seriesSaturday;
	}

	public BmField getSeriesSunday() {
		return seriesSunday;
	}

	public void setSeriesSunday(BmField seriesSunday) {
		this.seriesSunday = seriesSunday;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmoOrder getBmoOrder() {
		return bmoOrder;
	}

	public void setBmoOrder(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}
	
	
}
