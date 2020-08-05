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
import com.flexwm.shared.fi.BmoCurrency;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoSessionType extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField name, description, duration, capacity, gCalendarSync, gCalendarId, sessionDisciplineId, currencyId;

	BmoSessionDiscipline bmoSessionDiscipline = new BmoSessionDiscipline();
	BmoCurrency bmoCurrency = new BmoCurrency();

	public BmoSessionType() {
		super("com.flexwm.server.ac.PmSessionType","sessiontypes", "sessiontypeid", "SETY", "Tipos Sesión");

		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		duration = setField("duration", "", "Duración", 8, Types.INTEGER, false, BmFieldType.NUMBER, false);
		capacity = setField("capacity", "", "Capacidad", 8, Types.INTEGER, false, BmFieldType.NUMBER, false);
		gCalendarSync = setField("gcalendarsync", "", "Calendario Google", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		gCalendarId = setField("gcalendarid", "", "ID Cal. Google", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		sessionDisciplineId = setField("sessiondisciplineid", "", "Disciplina", 8, Types.INTEGER, false, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getDuration(),
				getCapacity(),
				getBmoCurrency().getCode()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()),
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getSessionDisciplineId() {
		return sessionDisciplineId;
	}

	public void setSessionDisciplineId(BmField sessionDisciplineId) {
		this.sessionDisciplineId = sessionDisciplineId;
	}

	public BmoSessionDiscipline getBmoSessionDiscipline() {
		return bmoSessionDiscipline;
	}

	public void setBmoSessionDiscipline(BmoSessionDiscipline bmoSessionDiscipline) {
		this.bmoSessionDiscipline = bmoSessionDiscipline;
	}

	public BmField getCapacity() {
		return capacity;
	}

	public void setCapacity(BmField capacity) {
		this.capacity = capacity;
	}

	public BmField getDuration() {
		return duration;
	}

	public void setDuration(BmField duration) {
		this.duration = duration;
	}

	public BmField getgCalendarSync() {
		return gCalendarSync;
	}

	public void setgCalendarSync(BmField gCalendarSync) {
		this.gCalendarSync = gCalendarSync;
	}

	public BmField getgCalendarId() {
		return gCalendarId;
	}

	public void setgCalendarId(BmField gCalendarId) {
		this.gCalendarId = gCalendarId;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
