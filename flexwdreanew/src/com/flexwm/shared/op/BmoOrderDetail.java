/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.op;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoOrderDetail extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField status, closeDate, orderDate, desireDate, startDate, deliveryDate, 
	leaderUserId, assignedUserId, areaId, orderId;

	public static char STATUS_INITIAL = 'I';
	public static char STATUS_DOING = 'N';
	public static char STATUS_DONE = 'D';
	public static char STATUS_HOLD = 'H';

	public static String ACCESS_CHANGEDATA = "ORDTCHDATA"; // Cambiar datos
	public static String ACCESS_CHANGEDESIREDATE = "ORDTDESIRE"; // Cambiar fecha deseada
	public static String ACCESS_CHANGESTARTDATE = "ORDTSTARTD"; // Cambiar fecha inicio
	public static String ACCESS_CHANGEDELIVERYDATE = "ORDTDELIVD"; // Cambiar fecha pactada
	public static String ACCESS_CHANGESTATUS = "ORDTCHSTAT"; // Cambiar estatus

	BmoOrder bmoOrder = new BmoOrder();

	public BmoOrderDetail() {
		super("com.flexwm.server.op.PmOrderDetail", "orderdetails", "orderdetailid", "ORDT", "Detalle");

		status = setField("status", "" + STATUS_INITIAL, "Status", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_INITIAL, "Por Iniciar", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_DOING, "En Proceso", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_DONE, "Terminado", "./icons/status_finished.png"),
				new BmFieldOption(STATUS_HOLD, "Retenido", "./icons/status_cancelled.png")
				)));

		orderDate = setField("orderdate", "", "Fecha Pedido", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		desireDate = setField("desiredate", "", "Fecha Deseada", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		closeDate = setField("closedate", "", "Fecha Cierre", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		startDate = setField("startdate", "", "Fecha Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		deliveryDate = setField("deliverydate", "", "Fecha Pactada", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);

		leaderUserId = setField("leaderuserid", "", "Responsable", 8, Types.INTEGER, true, BmFieldType.ID, false);
		assignedUserId = setField("assigneduserid", "", "Consultor", 8, Types.INTEGER, true, BmFieldType.ID, false);
		areaId = setField("areaid", "", "Departamento", 8, Types.INTEGER, true, BmFieldType.ID, false);
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoOrder().getCode(),
				getBmoOrder().getName(),
				getStatus()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoOrder().getName())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoOrder().getCode(), BmOrder.ASC)));
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(BmField closeDate) {
		this.closeDate = closeDate;
	}

	public BmField getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(BmField orderDate) {
		this.orderDate = orderDate;
	}

	public BmField getDesireDate() {
		return desireDate;
	}

	public void setDesireDate(BmField desireDate) {
		this.desireDate = desireDate;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(BmField deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public BmField getLeaderUserId() {
		return leaderUserId;
	}

	public void setLeaderUserId(BmField leaderUserId) {
		this.leaderUserId = leaderUserId;
	}

	public BmField getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(BmField assignedUserId) {
		this.assignedUserId = assignedUserId;
	}

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}

	public BmoOrder getBmoOrder() {
		return bmoOrder;
	}

	public void setBmoOrder(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
	}
}