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

public class BmoWFlowFormatCompany extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private BmoCompany bmoCompany = new BmoCompany();
	private BmoWFlowFormat bmoWFlowFormat = new BmoWFlowFormat();
	
	private BmField companyId,wflowformatId;

	public static String ACTION_EXISTCOMPANY = "EXISTCOMPANY";
	public static String ACTION_FILTERBYTYPECOMP = "FILTERBYTYPECOMP";
	public static String ACTION_FILTERBYCATEGORYCOMP = "FILTERBYCATEGORYCOMP";
	
	public BmoWFlowFormatCompany() {
		super("com.flexwm.server.wf.PmWFlowFormatCompany", "wflowformatcompanies", "wflowformatcompanyid", "WFFC", "Empresa de Formatos Wflow");
		
		wflowformatId = setField("wflowformatid", "", "Formato ", 10, Types.INTEGER, false, BmFieldType.ID, false);
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
	
	

	public BmoWFlowFormat getBmoWFlowFormat() {
		return bmoWFlowFormat;
	}

	public void setBmoWFlowFormat(BmoWFlowFormat bmoWFlowFormat) {
		this.bmoWFlowFormat = bmoWFlowFormat;
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

	public BmField getWflowformatId() {
		return wflowformatId;
	}

	public void setWflowformatId(BmField wflowformatId) {
		this.wflowformatId = wflowformatId;
	}
	
	
	
}
