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
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoProfile;

public class BmoQuoteStaff extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, quantity, days, basePrice, price, amount, profileId, quoteId;
	BmoProfile bmoProfile = new BmoProfile();

	
	public BmoQuoteStaff() {
		super("com.flexwm.server.cm.PmQuoteStaff", "quotestaff", "quotestaffid", "QOST", "Personal");
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		quantity = setField("quantity", "", "Cantidad", 20, Types.INTEGER, false, BmFieldType.NUMBER, false);
		days = setField("days", "", "Días", 20, Types.FLOAT, true, BmFieldType.NUMBER, false);
		basePrice = setField("baseprice", "", "Precio Base", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		price = setField("price", "", "Precio", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		amount = setField("amount", "", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		profileId = setField("profileid", "", "Perfil", 20, Types.INTEGER, true, BmFieldType.ID, false);
		quoteId = setField("quoteid", "", "Cotizacion", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	public BmField getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(BmField quoteId) {
		this.quoteId = quoteId;
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getBmoProfile().getName(),
						getQuantity(), 
						getDescription()
						));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProfile().getName())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
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

	public BmField getDays() {
		return days;
	}

	public void setDays(BmField days) {
		this.days = days;
	}

	public BmField getPrice() {
		return price;
	}

	public void setPrice(BmField price) {
		this.price = price;
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BmField basePrice) {
		this.basePrice = basePrice;
	}
}
