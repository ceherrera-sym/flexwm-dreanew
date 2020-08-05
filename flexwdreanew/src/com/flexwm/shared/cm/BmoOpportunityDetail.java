/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.ev.BmoVenue;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoOpportunityDetail extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField description, deposit, extraHour, paymentDate, guests, customerEmailId, 
	opportunityId, showInContract, downPayment;

	BmoVenue bmoVenue = new BmoVenue();
	
	public BmoOpportunityDetail() {
		super("com.flexwm.server.cm.PmOpportunityDetail", "opportunitydetails", "opportunitydetailid", "OPDE", "Detalle");

		// Campo de datos
		description = setField("description", "", "Servicios Contratados", 60000, Types.VARCHAR, true, BmFieldType.STRING, false);
		deposit = setField("deposit", "0", "Depósito Garantía", 20, Types.FLOAT, true, BmFieldType.CURRENCY, false);
		extraHour = setField("extrahour", "", "Hora Extra", 20, Types.FLOAT, true, BmFieldType.CURRENCY, false);
		paymentDate = setField("paymentdate", "", "Fecha Límite Pago", 20, Types.DATE, true, BmFieldType.DATE, false);
//		customerAddressId = setField("customeraddressid", "", "Dirección Cliente", 20, Types.INTEGER, true, BmFieldType.ID, false);
		customerEmailId = setField("customeremailid", "", "Email Cliente", 20, Types.INTEGER, true, BmFieldType.ID, false);
		opportunityId = setField("opportunityid", "", "Oportunidad", 8, Types.INTEGER, false, BmFieldType.ID, false);
		showInContract = setField("showincontract", "1", "¿Cotización en Contrato?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		guests = setField("guests", "", "Asistentes", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
//		venueId = setField("venueid", "", "Lugar", 8, Types.INTEGER, true, BmFieldType.ID, false);
		downPayment = setField("downpayment", "0", "Anticipo Cotización", 20, Types.FLOAT, true, BmFieldType.CURRENCY, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getDescription()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getDescription())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getDescription(), BmOrder.ASC)));
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getDeposit() {
		return deposit;
	}

	public void setDeposit(BmField deposit) {
		this.deposit = deposit;
	}

	public BmField getExtraHour() {
		return extraHour;
	}

	public void setExtraHour(BmField extraHour) {
		this.extraHour = extraHour;
	}

	public BmField getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(BmField paymentDate) {
		this.paymentDate = paymentDate;
	}

	public BmField getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(BmField opportunityId) {
		this.opportunityId = opportunityId;
	}

	public BmField getCustomerEmailId() {
		return customerEmailId;
	}

	public void setCustomerEmailId(BmField customerEmailId) {
		this.customerEmailId = customerEmailId;
	}

	public BmField getShowInContract() {
		return showInContract;
	}

	public void setShowInContract(BmField showInContract) {
		this.showInContract = showInContract;
	}

	public BmField getGuests() {
		return guests;
	}

	public void setGuests(BmField guests) {
		this.guests = guests;
	}

	public BmoVenue getBmoVenue() {
		return bmoVenue;
	}

	public void setBmoVenue(BmoVenue bmoVenue) {
		this.bmoVenue = bmoVenue;
	}

	public BmField getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BmField downPayment) {
		this.downPayment = downPayment;
	}
	
	
}