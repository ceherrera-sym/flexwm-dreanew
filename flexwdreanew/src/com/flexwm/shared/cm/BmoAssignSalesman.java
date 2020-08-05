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
import com.symgae.shared.sf.BmoUser;


public class BmoAssignSalesman extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField assingCoordinatorId, userId, assigned, countAssigned, queuedUser, enable;

	BmoUser bmoUser = new BmoUser();

	public BmoAssignSalesman() {
		super("com.flexwm.server.cm.PmAssignSalesman", "assignsalesmen", "assignsalesmanid", "ASSA", "Asignación Vendedores");
		assingCoordinatorId = setField("assingcoordinatorid", "", "Coordinador", 10, Types.INTEGER, false, BmFieldType.ID, false);
		userId = setField("userid", "", "Vendedor", 10, Types.INTEGER, false, BmFieldType.ID, false);
		assigned = setField("assigned", "0", "Asignado", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		countAssigned = setField("countassigned", "0", "Contador", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		queuedUser = setField("queueduser", "0", "Vendedor en espera?", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		enable = setField("enable", "0", "Activo", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoUser().getCode(),
				getAssigned(),
				getCountAssigned(),
				getQueuedUser(),
				getEnable()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoUser().getCode(),
				getCountAssigned()
				));
	}

	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoUser().getCode(),
				getCountAssigned()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoUser().getCode()) ));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoUser().getCode(), BmOrder.ASC)));
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmField getAssigned() {
		return assigned;
	}

	public void setAssigned(BmField assigned) {
		this.assigned = assigned;
	}

	public BmField getCountAssigned() {
		return countAssigned;
	}

	public void setCountAssigned(BmField countAssigned) {
		this.countAssigned = countAssigned;
	}

	public BmField getAssingCoordinatorId() {
		return assingCoordinatorId;
	}

	public void setAssingCoordinatorId(BmField assingCoordinatorId) {
		this.assingCoordinatorId = assingCoordinatorId;
	}

	public BmField getQueuedUser() {
		return queuedUser;
	}

	public void setQueuedUser(BmField queuedUser) {
		this.queuedUser = queuedUser;
	}

	public BmField getEnable() {
		return enable;
	}

	public void setEnable(BmField enable) {
		this.enable = enable;
	}

}
