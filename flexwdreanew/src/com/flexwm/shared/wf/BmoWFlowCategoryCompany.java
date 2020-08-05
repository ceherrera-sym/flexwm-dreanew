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

public class BmoWFlowCategoryCompany extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private BmoCompany bmoCompany = new BmoCompany();
	
	private BmField companyId,wflowCategoryId;
	
	public BmoWFlowCategoryCompany() {
		super("com.flexwm.server.wf.PmWFlowCategoryCompany", "wflowcategorycompanies", "wflowcategorycompanyid", "WFCC", "Empresa Categoria");
		
		wflowCategoryId = setField("wflowcategoryid", "", "Categoria de flujo ", 10, Types.INTEGER, false, BmFieldType.ID, false);
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

	public BmField getWflowCategoryId() {
		return wflowCategoryId;
	}

	public void setWflowCategoryId(BmField wflowCategoryId) {
		this.wflowCategoryId = wflowCategoryId;
	}
	
	
}
