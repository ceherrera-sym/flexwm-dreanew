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
import com.symgae.shared.sf.BmoProfile;


public class BmoOrderCommissionAmount extends BmObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private BmField name, type, value, trigger, userAssignRequired, isPartial, profileId, parentId, orderCommissionId;
	BmoProfile bmoProfile = new BmoProfile();

	public static char TYPE_AMOUNT = 'A';
	public static char TYPE_PERCENTAGE = 'P';

	public static char TRIGGER_AUTHORIZED = 'A';
	public static char TRIGGER_PARTIALPAYMENT = 'R';
	public static char TRIGGER_FULLPAYMENT = 'P';
	public static char TRIGGER_DELIVERED = 'D';
	public static char TRIGGER_LOCK = 'L';

	public BmoOrderCommissionAmount(){
		super("com.flexwm.server.op.PmOrderCommissionAmount", "ordercommissionamounts", "ordercommissionamountid", "ORCA", "Montos Comisión");

		name = setField("name", "", "Concepto", 30, Types.VARCHAR, false, BmFieldType.STRING, false);

		type = setField("type", "" + TYPE_PERCENTAGE, "Tipo Cálculo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_PERCENTAGE, "Porcentaje", "./icons/percentage.png"),
				new BmFieldOption(TYPE_AMOUNT, "Monto Fijo", "./icons/amount.png")
				)));
		value = setField("value", "", "Valor", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		userAssignRequired = setField("userassignrequired", "", "Req. Asignación U.", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		trigger = setField("trigger", "", "Condición", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		trigger.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TRIGGER_AUTHORIZED, "Pedido Autorizado", "./icons/status_authorized.png"),
				new BmFieldOption(TRIGGER_PARTIALPAYMENT, "Pago Parcial", "./icons/paymentstatus_partial.png"),
				new BmFieldOption(TRIGGER_FULLPAYMENT, "Pago Total", "./icons/paymentstatus_total.png"),
				new BmFieldOption(TRIGGER_DELIVERED, "Entregado", "./icons/deliverystatus_total.png"),
				new BmFieldOption(TRIGGER_LOCK, "Bloqueado", "./icons/lockstatus_locked.png")
				)));
		isPartial = setField("ispartial", "", "Es Anticipo?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		parentId = setField("parentid", "", "Comisión Padre", 11, Types.INTEGER, true, BmFieldType.ID, false);

		profileId = setField("profileid", "", "Perfil", 11, Types.INTEGER, false, BmFieldType.ID, false);
		orderCommissionId = setField("ordercommissionid", "", "Tabulador", 11, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getBmoProfile().getName(),
				getType(),
				getValue()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getType())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getType(), BmOrder.ASC)));
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getValue() {
		return value;
	}

	public void setValue(BmField value) {
		this.value = value;
	}

	public BmField getTrigger() {
		return trigger;
	}

	public void setTrigger(BmField trigger) {
		this.trigger = trigger;
	}

	public BmField getUserAssignRequired() {
		return userAssignRequired;
	}

	public void setUserAssignRequired(BmField userAssignRequired) {
		this.userAssignRequired = userAssignRequired;
	}

	public BmField getIsPartial() {
		return isPartial;
	}

	public void setIsPartial(BmField isPartial) {
		this.isPartial = isPartial;
	}

	public BmField getParentId() {
		return parentId;
	}

	public void setParentId(BmField parentId) {
		this.parentId = parentId;
	}

	public BmField getOrderCommissionId() {
		return orderCommissionId;
	}

	public void setOrderCommissionId(BmField orderCommissionId) {
		this.orderCommissionId = orderCommissionId;
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setprofileId(BmField profileId) {
		this.profileId = profileId;
	}

	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}

	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}
}
