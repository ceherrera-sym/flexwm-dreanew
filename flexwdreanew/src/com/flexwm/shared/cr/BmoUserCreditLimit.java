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
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;
/**
 * @author jhernandez
 *
 */
public class BmoUserCreditLimit extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private BmField userId, creditLimit;
	private BmoUser bmoUser = new BmoUser();
	
	public static String CODE_PREFIX = "UC-";
	
	public BmoUserCreditLimit(){
		super("com.flexwm.server.cr.PmUserCreditLimit", "usercreditlimits", "usercreditlimitid", "USCL", "Limites de Crédito");
		
		//Campo de Datos
		userId = setField("userid", "", "Usuario", 11, Types.INTEGER, false, BmFieldType.ID, false);
		creditLimit = setField("creditlimit", "", "Limite Cŕedito", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
		
	}
	
	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoUser().getCode(),
				getCreditLimit()										
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoUser().getCode()) 
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BmField creditLimit) {
		this.creditLimit = creditLimit;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}
	
	
}
