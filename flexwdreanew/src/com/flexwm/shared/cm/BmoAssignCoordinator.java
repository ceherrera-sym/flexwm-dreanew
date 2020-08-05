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


public class BmoAssignCoordinator extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField userId, companyId, assigned, countAssigned, fullAssignment, queuedCoordinator, enable;
	BmoUser bmoUser = new BmoUser();
	public static String ACTION_ASSIGNLEAD = "ACTION_ASSIGNLEAD"; // Accion asignar un vendedor

	public BmoAssignCoordinator() {
		super("com.flexwm.server.cm.PmAssignCoordinator", "assigncoordinators", "assingcoordinatorid", "ASSC", "Asignación Coordinadores");
		assigned = setField("assigned", "0", "Asignado", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		fullAssignment = setField("fullassignment", "0", "Asignación Completa", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		countAssigned = setField("countassigned", "0", "Contador", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		queuedCoordinator = setField("queuedcoordinator", "0", "Asignar Vend. en espera?", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);

		companyId = setField("companyid", "", "Empresa", 10, Types.INTEGER, false, BmFieldType.ID, false);
		userId = setField("userid", "", "Coordinador", 10, Types.INTEGER, false, BmFieldType.ID, false);
		enable = setField("enable", "0", "Activo", 5, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoUser().getCode(),
				getAssigned(),
				getCountAssigned(),
				getFullAssignment(),
				getEnable()
				//				getQueuedCoordinator()
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

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getAssigned() {
		return assigned;
	}

	public void setAssigned(BmField assigned) {
		this.assigned = assigned;
	}

	public BmField getFullAssignment() {
		return fullAssignment;
	}

	public void setFullAssignment(BmField fullAssignment) {
		this.fullAssignment = fullAssignment;
	}

	public BmField getCountAssigned() {
		return countAssigned;
	}

	public void setCountAssigned(BmField countAssigned) {
		this.countAssigned = countAssigned;
	}

	public BmField getQueuedCoordinator() {
		return queuedCoordinator;
	}

	public void setQueuedCoordinator(BmField queuedCoordinator) {
		this.queuedCoordinator = queuedCoordinator;
	}

	public BmField getEnable() {
		return enable;
	}

	public void setEnable(BmField enable) {
		this.enable = enable;
	}

}
