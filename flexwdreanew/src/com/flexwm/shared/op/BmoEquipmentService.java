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

import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoEquipmentService extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField date, nextDate, nextMeter, comments, equipmentId, whTrackId, status, cost,liberateDate;
	BmoEquipment bmoEquipment = new BmoEquipment();
	
	public static char STATUS_MAINTENANCE = 'M';
	public static char STATUS_LIBERATE = 'L';

	public BmoEquipmentService() {
		super("com.flexwm.server.op.PmEquipmentService", "equipmentservices", "equipmentserviceid", "EQSV", "Mantenimiento Recursos");
		
		date = setField("date", "", "Fecha Mto.", 20, Types.DATE, false, BmFieldType.DATE, false);
		nextDate = setField("nextdate", "", "Fecha Sig. Mto.", 20, Types.DATE, true, BmFieldType.DATE, false);
		nextMeter = setField("nextmeter", "", "Sig. Medidor", 8, Types.FLOAT, true, BmFieldType.NUMBER, false);
		cost = setField("cost", "", "Costo", 8, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);	
		status = setField("status", "", "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_MAINTENANCE, "En Mantenimiento", "./icons/status_maintenance.png"),
				new BmFieldOption(STATUS_LIBERATE, "Liberado", "./icons/status_authorized.png")
				)));
		
		equipmentId = setField("equipmentid", "", "Recurso", 8, Types.INTEGER, true, BmFieldType.ID, false);
		whTrackId = setField("whtrackid", "", "Producto", 8, Types.INTEGER, true, BmFieldType.ID, false);
		liberateDate = setField("liberatedate", "", "Fecha Liberación", 20, Types.DATE, true, BmFieldType.DATE, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getDate(), 
						getNextDate(),
//						getNextMeter(),
						getLiberateDate(),
						getComments(),
						getCost(),
						getStatus()
						));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getComments()), 
				new BmSearchField(getBmoEquipment().getCode()), 
				new BmSearchField(getBmoEquipment().getName())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getDate(), BmOrder.DESC)));
	}

	public BmField getDate() {
		return date;
	}

	public void setDate(BmField date) {
		this.date = date;
	}

	public BmField getNextDate() {
		return nextDate;
	}

	public void setNextDate(BmField nextDate) {
		this.nextDate = nextDate;
	}

	public BmField getNextMeter() {
		return nextMeter;
	}

	public void setNextMeter(BmField nextMeter) {
		this.nextMeter = nextMeter;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(BmField equipmentId) {
		this.equipmentId = equipmentId;
	}

	public BmoEquipment getBmoEquipment() {
		return bmoEquipment;
	}

	public void setBmoEquipment(BmoEquipment bmoEquipment) {
		this.bmoEquipment = bmoEquipment;
	}

	public BmField getWhTrackId() {
		return whTrackId;
	}

	public void setWhTrackId(BmField whTrackId) {
		this.whTrackId = whTrackId;
	}
	
	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}
	
	public BmField getCost() {
		return cost;
	}

	public void setCost(BmField cost) {
		this.cost = cost;
	}

	public BmField getLiberateDate() {
		return liberateDate;
	}

	public void setLiberateDate(BmField liberateDate) {
		this.liberateDate = liberateDate;
	}
	
}
