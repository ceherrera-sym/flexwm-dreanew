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
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoEquipmentUse extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField dateTime, dateTimeIn, meter, meterIn, fuel, fuelIn, comments, equipmentId, whTrackId, orderId, fuelPrice;
	BmoEquipment bmoEquipment = new BmoEquipment();

	public BmoEquipmentUse() {
		super("com.flexwm.server.op.PmEquipmentUse", "equipmentuses", "equipmentuseid", "EQIS", "Uso Recursos");
		
		dateTime = setField("datetime", "", "F. Salida", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		dateTimeIn = setField("datetimein", "", "F. Entrada", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		meter = setField("meter", "", "Kilometraje S.", 8, Types.FLOAT, true, BmFieldType.NUMBER, false);
		meterIn = setField("meterin", "", "Kilometraje E.", 8, Types.FLOAT, true, BmFieldType.NUMBER, false);
		fuel = setField("fuel", "", "Nivel Combustible S.", 8, Types.FLOAT, true, BmFieldType.NUMBER, false);
		fuelIn = setField("fuelin", "", "Nivel Combustible E.", 8, Types.FLOAT, true, BmFieldType.NUMBER, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);	
		fuelPrice = setField("fuelprice", "", "Precio por litro", 8, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		equipmentId = setField("equipmentid", "", "Recurso", 8, Types.INTEGER, true, BmFieldType.ID, false);
		whTrackId = setField("whtrackid", "", "Producto", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getDateTime(),
						getDateTimeIn(),
						getMeterIn(),
						getMeter(),
						getFuel(),
						getFuelIn()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getDateTime(), BmOrder.DESC)));
	}

	public BmField getDateTime() {
		return dateTime;
	}

	public void setDateTime(BmField dateTime) {
		this.dateTime = dateTime;
	}
	
	public BmField getDateTimeIn() {
		return dateTimeIn;
	}

	public void setDateTimeIn(BmField dateTimeIn) {
		this.dateTimeIn = dateTimeIn;
	}

	public BmField getMeter() {
		return meter;
	}

	public void setMeter(BmField meter) {
		this.meter = meter;
	}

	public BmField getFuel() {
		return fuel;
	}

	public void setFuel(BmField fuel) {
		this.fuel = fuel;
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

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
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
	
	public BmField getFuelPrice() {
		return fuelPrice;
	}

	public void setFuelPrice(BmField fuelPrice) {
		this.fuelPrice = fuelPrice;
	}
	
	public BmField getMeterIn() {
		return meterIn;
	}

	public void setMeterEnd(BmField meterIn) {
		this.meterIn = meterIn;
	}
	
	public BmField getFuelIn() {
		return fuelIn;
	}

	public void setFuelEnd(BmField fuelIn) {
		this.fuelIn = fuelIn;
	}
}
