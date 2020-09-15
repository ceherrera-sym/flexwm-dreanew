/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.cr;

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


/**
 * @author jhernandez
 *
 */

public class BmoCreditType extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;

	private BmField name, description, deadLine, type, interest, creditLimit, guarantees, failure, locationId, amountFailure,
	paymentDayMonday, paymentDayTuesday, paymentDayWednesday, paymentDayThursday, paymentDayFriday, paymentDaySaturday, paymentDaySunday;

	public static final char TYPE_DAILY= 'D';
	public static final char TYPE_WEEKLY = 'W';
	public static final char TYPE_TWOWEEKS = 'T';
	public static final char TYPE_MONTHLY = 'M';
	
	public static String ACCESS_CHANGEAMOUNTFAILURE= "TCREDCHAF";

	public BmoCreditType(){
		super("com.flexwm.server.cr.PmCreditType", "credittypes", "credittypeid", "CRTY", "Tipo de Crédito");

		//Campo de Datos
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);

		deadLine = setField("deadline", "", "Plazo", 11, Types.INTEGER, false, BmFieldType.NUMBER, false);		
		interest = setField("interest", "", "Interes", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		creditLimit = setField("creditlimit", "", "Limite Crédito", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		guarantees = setField("guarantees", "", "No.Avales", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
		failure = setField("failure", "", "No.Fallos", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);

		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_DAILY, "Diario", "./icons/day.png"),
				new BmFieldOption(TYPE_WEEKLY, "Semanal", "./icons/week.png")
//				new BmFieldOption(TYPE_TWOWEEKS, "Quincenal", "./icons/week2.png"),
//				new BmFieldOption(TYPE_MONTHLY, "Mensual", "./icons/month.png")				
				)));		
		
		amountFailure = setField("amountFailure", "", "Monto Falla", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		locationId = setField("locationid", "", "Ubicación", 20, Types.INTEGER, true, BmFieldType.ID, false);
		
		// Esta funcionalidad esta desarrollada para SOLO TIPO DIARIO (para daCredito), ya que cobi no usa creditos diarios
		// deberia estar en todos los tipos, pero por urgencia solo es tipo diario
		paymentDayMonday = setField("paymentdaymonday", "1", "Lunes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		paymentDayTuesday = setField("paymentdaytuesday", "1", "Martes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		paymentDayWednesday = setField("paymentdaywednesday", "1", "Miércoles", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		paymentDayThursday = setField("paymentdaythursday", "1", "Jueves", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		paymentDayFriday = setField("paymentdayFriday", "1", "Viernes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		paymentDaySaturday = setField("paymentdaysaturday", "", "Sábado", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		paymentDaySunday = setField("paymentdaysunday", "", "Domingo", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getDeadLine(),
				getInterest(),
				getType()						
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()),
				new BmSearchField(getDescription())));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDeadLine(),
				getInterest(),
				getType()
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

	public BmField getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(BmField deadLine) {
		this.deadLine = deadLine;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getInterest() {
		return interest;
	}

	public void setInterest(BmField interest) {
		this.interest = interest;
	}

	public BmField getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BmField creditLimit) {
		this.creditLimit = creditLimit;
	}

	public BmField getGuarantees() {
		return guarantees;
	}

	public void setGuarantees(BmField guarantees) {
		this.guarantees = guarantees;
	}

	public BmField getFailure() {
		return failure;
	}

	public void setFailure(BmField failure) {
		this.failure = failure;
	}

	public BmField getLocationId() {
		return locationId;
	}

	public void setLocationId(BmField locationId) {
		this.locationId = locationId;
	}

	public BmField getAmountFailure() {
		return amountFailure;
	}

	public void setAmountFailure(BmField amountFailure) {
		this.amountFailure = amountFailure;
	}

	public BmField getPaymentDayMonday() {
		return paymentDayMonday;
	}

	public void setPaymentDayMonday(BmField paymentDayMonday) {
		this.paymentDayMonday = paymentDayMonday;
	}

	public BmField getPaymentDayTuesday() {
		return paymentDayTuesday;
	}

	public void setPaymentDayTuesday(BmField paymentDayTuesday) {
		this.paymentDayTuesday = paymentDayTuesday;
	}

	public BmField getPaymentDayWednesday() {
		return paymentDayWednesday;
	}

	public void setPaymentDayWednesday(BmField paymentDayWednesday) {
		this.paymentDayWednesday = paymentDayWednesday;
	}

	public BmField getPaymentDayThursday() {
		return paymentDayThursday;
	}

	public void setPaymentDayThursday(BmField paymentDayThursday) {
		this.paymentDayThursday = paymentDayThursday;
	}

	public BmField getPaymentDayFriday() {
		return paymentDayFriday;
	}

	public void setPaymentDayFriday(BmField paymentDayFriday) {
		this.paymentDayFriday = paymentDayFriday;
	}

	public BmField getPaymentDaySaturday() {
		return paymentDaySaturday;
	}

	public void setPaymentDaySaturday(BmField paymentDaySaturday) {
		this.paymentDaySaturday = paymentDaySaturday;
	}

	public BmField getPaymentDaySunday() {
		return paymentDaySunday;
	}

	public void setPaymentDaySunday(BmField paymentDaySunday) {
		this.paymentDaySunday = paymentDaySunday;
	}

}
