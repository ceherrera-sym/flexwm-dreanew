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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoSessionTypePackage extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, type, sessions, salePrice, startDate, endDate, sessionTypeId, enabled, profileId;

	BmoSessionType bmoSessionType = new BmoSessionType();

	public static final char TYPE_SINGLE = 'I';
	public static final char TYPE_MONTHLY = 'M';
	public static final char TYPE_WEEKLY = 'W';
	public static final char TYPE_SESSION = 'S';

	public static String ACTION_NOSESSION = "NOSESSION";
	public static String ACTION_COSTSESSION = "COSTNOSESSION";

	public BmoSessionTypePackage() {
		super("com.flexwm.server.ac.PmSessionTypePackage","sessiontypepackages", "sessiontypepackageid", "SETP", "Paquetes Sesiones");

		name = setField("name", "", "Nombre Paquete", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		type = setField("type", "", "Tipo Cobro", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(				
				new BmFieldOption(TYPE_MONTHLY, "Mensual", "./icons/month.png"),
				new BmFieldOption(TYPE_WEEKLY, "Semanal", "./icons/week2.png"),
				new BmFieldOption(TYPE_SESSION, "Sesiones", "./icons/day.png"),
				new BmFieldOption(TYPE_SINGLE, "Único", "./icons/cal-unique.png")
				)));
		sessions = setField("sessions", "", "#Ses./Semana", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		salePrice = setField("saleprice", "", "Precio Venta", 20, Types.FLOAT, false, BmFieldType.CURRENCY, false);
		startDate = setField("startdate", "", "Inicio Vigencia", 20, Types.DATE, false, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Fin Vigencia", 20, Types.DATE, false, BmFieldType.DATE, false);

		enabled = setField("enabled", "", "Activo?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		sessionTypeId = setField("sessiontypeid", "", "Tipo Sesión", 8, Types.INTEGER, false, BmFieldType.ID, false);
		profileId = setField("profileid", "", "Perfil", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getType(),
				getSessions(),
				getSalePrice(),
				getStartDate(),
				getEndDate(),
				getEnabled()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoSessionType().getName(),
				getName()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoSessionType().getName(), BmOrder.ASC)));
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

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BmField salePrice) {
		this.salePrice = salePrice;
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

	public BmField getSessionTypeId() {
		return sessionTypeId;
	}

	public void setSessionTypeId(BmField sessionTypeId) {
		this.sessionTypeId = sessionTypeId;
	}

	public BmoSessionType getBmoSessionType() {
		return bmoSessionType;
	}

	public void setBmoSessionType(BmoSessionType bmoSessionType) {
		this.bmoSessionType = bmoSessionType;
	}

	public BmField getSessions() {
		return sessions;
	}

	public void setSessions(BmField sessions) {
		this.sessions = sessions;
	}

	public BmField getEnabled() {
		return enabled;
	}

	public void setEnabled(BmField enabled) {
		this.enabled = enabled;
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setGroupId(BmField profileId) {
		this.profileId = profileId;
	}


}
