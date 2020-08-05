package com.flexwm.shared.cm;

/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Paulina Padilla Guerra
 * @version 2018-09
 */



import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;


public class BmoCustomerStatus extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BmField customerId,status,companyId;
	
//	private BmoCustomer bmoCustomer = new BmoCustomer();
	private BmoCompany bmoCompany = new BmoCompany();
	
	public static char STATUS_PROSPECT = 'P';
	public static char STATUS_INACTIVE = 'I';
	public static char STATUS_ACTIVE = 'A';
	
	
	public static String STATUSCHANGE = "CUSTCH";
	public static String RFCCHANGE = "CUSTRFCCH";
	public static String TYPECHANGE = "CUSTTYPECH";
	public static String SALESMANCHANGE = "CUSTSMCH";

	public BmoCustomerStatus() {
		super("com.flexwm.server.cm.PmCustomerStatus", "customerstatus", "customerstatusid", "CSTA", "Estatus Clientes Por Empresa");

		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, false, BmFieldType.ID, false);
		status = setField("status", "" + STATUS_PROSPECT, "Status", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_PROSPECT, "Prospecto", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_ACTIVE, "Activo", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_INACTIVE, "Inactivo", "./icons/status_closed.png")
				)));
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCompany().getName(),
				getStatus()
			));
	}
				
				
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getStatus()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getStatus())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}
	
}
