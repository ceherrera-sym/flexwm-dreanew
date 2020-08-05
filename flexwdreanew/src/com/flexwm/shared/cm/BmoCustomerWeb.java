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


public class BmoCustomerWeb extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField customerId, website;


	public BmoCustomerWeb() {
		super("com.flexwm.server.cm.PmCustomerWeb", "customerwebs", "customerwebid", "CUWB", "Sitios Web");
		website = setField("website", "", "Sitio", 100, Types.VARCHAR, true, BmFieldType.WWW, false);		
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getWebsite()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getWebsite().getName(), getWebsite().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getWebsite(), BmOrder.ASC)));
	}

	/**
	 * @return the customerId
	 */
	public BmField getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the website
	 */
	public BmField getWebsite() {
		return website;
	}

	/**
	 * @param website the website to set
	 */
	public void setWebsite(BmField website) {
		this.website = website;
	}

}
