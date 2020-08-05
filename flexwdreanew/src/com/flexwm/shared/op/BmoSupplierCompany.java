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
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.sf.BmoCompany;

public class BmoSupplierCompany extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmoSupplier bmoSupplier = new BmoSupplier();
	private BmoCompany bmoCompany = new BmoCompany();
	private BmField supplierId, companyId;

	public BmoSupplierCompany() {
		super("com.flexwm.server.op.PmSupplierCompany", "suppliercompanies", "suppliercompanyid", "SUCP", "Empresas Proveedor");
		supplierId = setField("supplierid", "", "Proveedor", 10, Types.INTEGER, false, BmFieldType.ID, false);
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

	public BmoSupplier getBmoSupplier() {
		return bmoSupplier;
	}

	public void setBmoSupplier(BmoSupplier bmoSupplier) {
		this.bmoSupplier = bmoSupplier;
	}

	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

	public BmField getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField CompanyId) {
		this.companyId = CompanyId;
	}
}
