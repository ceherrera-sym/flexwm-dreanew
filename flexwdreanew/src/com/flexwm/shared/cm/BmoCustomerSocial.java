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
import com.symgae.shared.sf.BmoSocial;


public class BmoCustomerSocial extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField account, customerId, socialId;
	private BmoSocial bmoSocial;

	public BmoCustomerSocial() {
		super("com.flexwm.server.cm.PmCustomerSocial", "customersocials", "customersocialid", "CUSO", "Social");

		// Campo de datos
		account = setField("account", "", "Cuenta", 20, Types.VARCHAR, false, BmFieldType.STRING, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);	
		socialId = setField("socialid", "", "Tipo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		bmoSocial = new BmoSocial();
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoSocial().getIcon(),	
				getBmoSocial().getName(),
				getAccount()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoSocial().getName(),
				getAccount()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getAccount().getName(), getAccount().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getAccount(), BmOrder.ASC)));
	}

	public BmField getAccount() {
		return account;
	}

	public void setAccount(BmField account) {
		this.account = account;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getSocialId() {
		return socialId;
	}

	public void setSocialId(BmField socialId) {
		this.socialId = socialId;
	}

	public BmoSocial getBmoSocial() {
		return bmoSocial;
	}

	public void setBmoSocial(BmoSocial bmoSocial) {
		this.bmoSocial = bmoSocial;
	}


}
