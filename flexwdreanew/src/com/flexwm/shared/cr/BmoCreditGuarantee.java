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
import com.flexwm.shared.cm.BmoCustomer;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author jhernandez
 *
 */

public class BmoCreditGuarantee extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private BmField customerId, creditId;
	private BmoCustomer bmoCustomer = new BmoCustomer();

	public BmoCreditGuarantee(){
		super("com.flexwm.server.cr.PmCreditGuarantee", "creditguarantees", "creditguaranteesid", "CRGU", "Avales");

		//Campo de Datos
		customerId = setField("customerid", "", "Cliente", 11, Types.INTEGER, false, BmFieldType.ID, false);
		creditId = setField("creditid", "", "Crédito", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoCustomer().getCode()) 
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getCreditId() {
		return creditId;
	}

	public void setCreditId(BmField creditId) {
		this.creditId = creditId;
	}

	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}

}
