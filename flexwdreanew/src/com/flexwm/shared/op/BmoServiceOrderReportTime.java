//
//package com.flexwm.shared.op;
//
//import java.io.Serializable;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.Arrays;
//import com.symgae.shared.BmField;
//import com.symgae.shared.BmFieldOption;
//import com.symgae.shared.BmFieldType;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmOrder;
//import com.symgae.shared.BmSearchField;
//
//
//public class BmoServiceOrderReportTime extends BmObject implements Serializable {
//	private static final long serialVersionUID = 1L;
//	private BmField serviceOrderId, type, comments, dateAndTime, realTime, childId;
//	BmoServiceOrder bmoServiceOrder = new BmoServiceOrder();
//	public static final char TYPE_START = 'I';
//	public static final char TYPE_STOP = 'O';
//	public static final char TYPE_MANUAL = 'M';
//	
//	public static String ACTION_MULTIPLEDELETE = "ACTION_MULTIPLEDELETE";
//	public static String ACTION_GETLASTRECORD = "ACTION_GETLASTRECORD";
//
//	public BmoServiceOrderReportTime() {
//		super("com.flexwm.server.op.PmServiceOrderReportTime", "serviceorderreporttimes", "serviceorderreporttimeid", "SRRT", "Reporte de Tiempos SRO");
//
//		serviceOrderId = setField("serviceorderid", "", "Orden de Servicio", 8, Types.INTEGER, false, BmFieldType.ID, false);
//		comments = setField("comments", "", "Comentarios", 500, Types.VARCHAR, false, BmFieldType.STRING, false);
//		dateAndTime = setField("dateandtime", "", "Fecha y Hora", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
//		realTime = setField("realtime", "", "H. Real", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
//		type = setField("type", "" + TYPE_START, "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
//		childId = setField("childid", "", "Registro superior", 8, Types.INTEGER, true, BmFieldType.ID, false);
//
//		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
//				new BmFieldOption(TYPE_START, "Iniciado", "./icons/srrt_type_play.png"),
//				new BmFieldOption(TYPE_STOP, "Detenido", "./icons/srrt_type_stop.png"),
//				new BmFieldOption(TYPE_MANUAL, "Manual", "./icons/srrt_type_manual.png")
//				)));
//	}
//
//	@Override
//	public ArrayList<BmField> getDisplayFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getComments(), 
//				getType(),
//				getDateAndTime(),
//				getRealTime()
//				));
//	}
//
//	@Override
//	public ArrayList<BmField> getListBoxFieldList() {
//		return new ArrayList<BmField>(Arrays.asList(
//				getBmoServiceOrder().getCode(),
//				getComments()
//				));
//	}
//
//	@Override
//	public ArrayList<BmSearchField> getSearchFields() {
//		return new ArrayList<BmSearchField>(Arrays.asList(
//				new BmSearchField(getBmoServiceOrder().getCode().getName(), getBmoServiceOrder().getCode().getLabel()), 
//				new BmSearchField(getComments().getName(), getComments().getLabel())));
//	}
//
//	@Override
//	public ArrayList<BmOrder> getOrderFields() {
//		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
//	}
//
//	public BmField getServiceOrderId() {
//		return serviceOrderId;
//	}
//
//	public void setServiceOrderId(BmField serviceOrderId) {
//		this.serviceOrderId = serviceOrderId;
//	}
//
//	public BmField getComments() {
//		return comments;
//	}
//
//	public void setComments(BmField comments) {
//		this.comments = comments;
//	}
//
//	public BmField getDateAndTime() {
//		return dateAndTime;
//	}
//
//	public void setDateAndTime(BmField dateAndTime) {
//		this.dateAndTime = dateAndTime;
//	}
//
//	public BmoServiceOrder getBmoServiceOrder() {
//		return bmoServiceOrder;
//	}
//
//	public void setBmoServiceOrder(BmoServiceOrder bmoServiceOrder) {
//		this.bmoServiceOrder = bmoServiceOrder;
//	}
//
//	public BmField getType() {
//		return type;
//	}
//
//	public void setType(BmField type) {
//		this.type = type;
//	}
//	
//	public BmField getRealTime() {
//		return realTime;
//	}
//
//	public void setRealTime(BmField realTime) {
//		this.realTime = realTime;
//	}
//
//	public BmField getChildId() {
//		return childId;
//	}
//
//	public void setChildId(BmField childId) {
//		this.childId = childId;
//	}
//
//}
