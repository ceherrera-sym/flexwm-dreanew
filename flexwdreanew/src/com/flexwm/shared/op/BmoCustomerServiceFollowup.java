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
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;


/**
 * @author smuniz
 *
 */

public class BmoCustomerServiceFollowup extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField description, userId, followupDate, customerServiceId;
	BmoUser bmoUser = new BmoUser();
	BmoCustomerService bmoCustomerService = new BmoCustomerService();

	public BmoCustomerServiceFollowup(){
		super("com.flexwm.server.op.PmCustomerServiceFollowup", "customerservicefollowups", "customerservicefollowupid", "CSFO", "Seguimiento de Atención a Clientes");

		//Campo de Datos	
		customerServiceId = setField("customerserviceid", "", "Atención a Cliente", 11, Types.INTEGER, false, BmFieldType.ID, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, false, BmFieldType.STRING, false);
		userId = setField("userid", "", "Usuario", 11, Types.INTEGER, false, BmFieldType.ID, false);
		followupDate = setField("followupdate", "", "Fecha de Seguimiento", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getFollowupDate(),
				getDescription()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getFollowupDate()), 
				new BmSearchField(bmoUser.getEmail()), 
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	/**
	 * @return the description
	 */
	public BmField getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(BmField description) {
		this.description = description;
	}

	/**
	 * @return the userId
	 */
	public BmField getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	/**
	 * @return the followupDate
	 */
	public BmField getFollowupDate() {
		return followupDate;
	}

	/**
	 * @param followupDate the followupDate to set
	 */
	public void setFollowupDate(BmField followupDate) {
		this.followupDate = followupDate;
	}

	/**
	 * @return the bmoUser
	 */
	public BmoUser getBmoUser() {
		return bmoUser;
	}

	/**
	 * @param bmoUser the bmoUser to set
	 */
	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	/**
	 * @return the customerServiceId
	 */
	public BmField getCustomerServiceId() {
		return customerServiceId;
	}

	/**
	 * @param customerServiceId the customerServiceId to set
	 */
	public void setCustomerServiceId(BmField customerServiceId) {
		this.customerServiceId = customerServiceId;
	}

	/**
	 * @return the bmoCustomerService
	 */
	public BmoCustomerService getBmoCustomerService() {
		return bmoCustomerService;
	}

	/**
	 * @param bmoCustomerService the bmoCustomerService to set
	 */
	public void setBmoBmoCustomerService(BmoCustomerService bmoCustomerService) {
		this.bmoCustomerService = bmoCustomerService;
	}



}
