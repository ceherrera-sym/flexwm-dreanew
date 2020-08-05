/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.co;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author smuniz
 *
 */

public class BmoDevelopmentRegistry extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, legalnumber, legalnumberdate, account, registryDate, duedateWork, duedateSale, developmentPhaseId, supplierId, bankAccountId;

	public static String CODE_PREFIX = "DR-";

	public BmoDevelopmentRegistry(){
		super("com.flexwm.server.co.PmDevelopmentRegistry", "developmentregistry", "developmentregistryid", "DVRG", "Reg. Paquetes");

		//Campo de Datos		
		code = setField("code", "", "Clave Paquete", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "# Registro", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		legalnumber = setField("legalnumber", "", "# Escritura", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		legalnumberdate = setField("legalnumberdate", "", "Fecha Escritura", 20, Types.DATE, true, BmFieldType.DATE, false);
		account = setField("account", "", "Clabe", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		registryDate = setField("registrydate", "", "Fecha Registro", 20, Types.DATE, true, BmFieldType.DATE, false);
		duedateWork = setField("duedatework", "", "Venc. Obra", 20, Types.DATE, true, BmFieldType.DATE, false);
		duedateSale = setField("duedatesale", "", "Venc. Venta", 20, Types.DATE, true, BmFieldType.DATE, false);
		developmentPhaseId = setField("developmentphaseid", "", "Etapa Des.", 8, Types.INTEGER, false, BmFieldType.ID, false);
		supplierId = setField("supplierid", "", "Acreedor", 8, Types.INTEGER, false, BmFieldType.ID, false);
		bankAccountId = setField("bankaccountid", "", "Cuenta Banco", 20, Types.VARCHAR, false, BmFieldType.NUMBER, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getLegalNumber(),
				getLegalNumberDate(),
				getRegistryDate(),
				getDuedateWork(),
				getDuedateSale()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}


	/**
	 * @return the code
	 */
	public BmField getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(BmField code) {
		this.code = code;
	}
	/**
	 * @return the name
	 */
	public BmField getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(BmField name) {
		this.name = name;
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
	 * @return the legalnumber
	 */
	public BmField getLegalNumber() {
		return legalnumber;
	}
	/**
	 * @param legalnumber the legalnumber to set
	 */
	public void setLegalNumber(BmField legalnumber) {
		this.legalnumber = legalnumber;
	}
	/**
	 * @return the legalnumberdate
	 */
	public BmField getLegalNumberDate() {
		return legalnumberdate;
	}
	/**
	 * @param legalnumberdate the legalnumberdate to set
	 */
	public void setLegalNumberDate(BmField legalnumberdate) {
		this.legalnumberdate = legalnumberdate;
	}
	/**
	 * @return the account
	 */
	public BmField getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(BmField account) {
		this.account = account;
	}

	public BmField getLegalnumber() {
		return legalnumber;
	}

	public void setLegalnumber(BmField legalnumber) {
		this.legalnumber = legalnumber;
	}

	public BmField getLegalnumberdate() {
		return legalnumberdate;
	}

	public void setLegalnumberdate(BmField legalnumberdate) {
		this.legalnumberdate = legalnumberdate;
	}

	public BmField getRegistryDate() {
		return registryDate;
	}

	public void setRegistryDate(BmField registryDate) {
		this.registryDate = registryDate;
	}

	public BmField getDuedateWork() {
		return duedateWork;
	}

	public void setDuedateWork(BmField duedateWork) {
		this.duedateWork = duedateWork;
	}

	public BmField getDuedateSale() {
		return duedateSale;
	}

	public void setDuedateSale(BmField duedateSale) {
		this.duedateSale = duedateSale;
	}

	public BmField getDevelopmentPhaseId() {
		return developmentPhaseId;
	}

	public void setDevelopmentPhaseId(BmField developmentPhaseId) {
		this.developmentPhaseId = developmentPhaseId;
	}

	public BmField getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}

	public BmField getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(BmField bankAccountId) {
		this.bankAccountId = bankAccountId;
	}
}
