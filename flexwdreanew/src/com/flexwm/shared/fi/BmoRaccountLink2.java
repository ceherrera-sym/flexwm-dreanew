/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.op.BmoOrder;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoRaccountLink2 extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField raccountId, foreignId, orderCode, orderId;

	BmoRaccount bmoForeign = new BmoRaccount();
	BmoOrder bmoOrder = new BmoOrder();

	public BmoRaccountLink2() {
		super("com.flexwm.server.fi.PmRaccountLink2", "raccountlinks", "raccountlinkid", "RALK", "Relacionadas");

		raccountId = setField("raccountid", "", "Cuenta por Cobrar", 8, Types.INTEGER, true, BmFieldType.ID, false);
		foreignId = setField("foreignid", "", "Relacionada", 8, Types.INTEGER, true, BmFieldType.ID, false);
		orderCode = setField("ordercode", "", "Pedido", 10, Types.VARCHAR, true, BmFieldType.STRING, false);
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(						
				getBmoForeign().getCode(),
				getBmoForeign().getInvoiceno(),
				getBmoForeign().getDueDate(),
				getBmoOrder().getCode(),
				getBmoForeign().getTotal()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getForeignId().getName(), getForeignId().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getForeignId(), BmOrder.ASC)));
	}

	public BmField getRaccountId() {
		return raccountId;
	}

	public void setRaccountId(BmField raccountId) {
		this.raccountId = raccountId;
	}

	public BmField getForeignId() {
		return foreignId;
	}

	public void setForeignId(BmField foreignId) {
		this.foreignId = foreignId;
	}

	public BmoRaccount getBmoForeign() {
		return bmoForeign;
	}
	

	public void setBmoForeign(BmoRaccount bmoForeign) {
		this.bmoForeign = bmoForeign;
	}

	/**
	 * @return the orderCode
	 */
//	public BmField getOrderCode() {
//		return orderCode;
//	}

	/**
	 * @param orderCode the orderCode to set
	 */
//	public void setOrderCode(BmField orderCode) {
//		this.orderCode = orderCode;
//	}
	
	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}
	

	public BmField getOrderCode() {
		return orderCode;
	}
	
	public void setOrderCode(BmField orderCode) {
		this.orderCode = orderCode;
	}
	
	public BmoOrder getBmoOrder() {
		return bmoOrder;
	}


	public void setBmoOrder(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
	}

}