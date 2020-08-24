package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoQuoteMainGroup extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name,quoteId,total,discount,productionFee,amount,
	commission;

	public BmoQuoteMainGroup() {
		super("com.flexwm.server.cm.PmQuoteMainGroup", "quotemaingroups", "quotemaingroupid", "QMGR", "Grupo de Cotización");
	
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		//id de la Cotización
		quoteId = setField("quoteid", "", "Cotización", 20, Types.INTEGER, false, BmFieldType.ID, false);
		//totales
		total = setField("total", "0", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		discount = setField("discount", "0", "Descuento", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		productionFee = setField("productionfee", "0", "Fee de Prodcucción", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		amount = setField("amount", "0", "Subtotal", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		commission = setField("commission", "0", "Comisión", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	}
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(						
						getName()
						));
	}
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName())
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
	public BmField getQuoteId() {
		return quoteId;
	}
	public void setQuoteId(BmField quoteId) {
		this.quoteId = quoteId;
	}
	public BmField getTotal() {
		return total;
	}
	public void setTotal(BmField total) {
		this.total = total;
	}
	public BmField getDiscount() {
		return discount;
	}
	public void setDiscount(BmField discount) {
		this.discount = discount;
	}
	public BmField getProductionFee() {
		return productionFee;
	}
	public void setProductionFee(BmField productionFee) {
		this.productionFee = productionFee;
	}
	public BmField getAmount() {
		return amount;
	}
	public void setAmount(BmField amount) {
		this.amount = amount;
	}
	public BmField getCommission() {
		return commission;
	}
	public void setCommission(BmField commission) {
		this.commission = commission;
	}
	
	
}
