package com.flexwm.shared.wf;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.sf.BmoCompany;

public class BmoWFlowTypeCompany extends BmObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private BmoCompany bmoCompany = new BmoCompany();
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	private BmField companyId,wflowTypeId;
	public static String ACTION_EXISTCOMPANY = "EXISTCOMPANY";

	public BmoWFlowTypeCompany() {
		super("com.flexwm.server.wf.PmWFlowTypeCompany", "wflowtypecompanies", "wflowtypecompanyid", "WFTC", "Empresa Tipos de Flujo");
		
		wflowTypeId = setField("wflowtypeid", "", "Tipo de flujo ", 10, Types.INTEGER, false, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 10, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCompany().getName()
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	
	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
	}

	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getWflowTypeId() {
		return wflowTypeId;
	}

	public void setWflowTypeId(BmField wflowTypeId) {
		this.wflowTypeId = wflowTypeId;
	}
	
	
}
