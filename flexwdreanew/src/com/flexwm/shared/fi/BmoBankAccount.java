/**
 * 
 */
package com.flexwm.shared.fi;

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

/**
 * @author jhernandez
 *
 */

public class BmoBankAccount extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description,  companyId, openDate, accountNo, format, cutDate, bankName,
	branch, logo, repName, phone, checkNo, balance, profileId, currencyId, responsibleId, clabe, bankRfc, status, bankAccountTypeId;
	private BmoCompany bmoCompany = new BmoCompany();
	private BmoCurrency bmoCurrency = new BmoCurrency();
	private BmoBankAccountType bmoBankAccountType= new BmoBankAccountType();

	public static final char TYPE_MASTER = 'M';
	public static final char TYPE_CHECKS = 'C';
	public static final char TYPE_SAVINGS = 'S';
	public static final char TYPE_CASH = 'A';

	public static final char STATUS_OPEN = 'O';
	public static final char STATUS_CANCEL = 'C';
	
	public static final String ACCESS_CHANGENOCHECK = "BKACHS";
	public static final String ACTION_GETBALANCE= "BKABALANCE";

	public BmoBankAccount() {
		super("com.flexwm.server.fi.PmBankAccount","bankaccounts", "bankaccountid", "BKAC","Cuentas de Bancos");

		//Campo de Datos		
		name = setField("name", "", "Nombre Cta.", 30, Types.VARCHAR, true, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
//		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
//		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
//				new BmFieldOption(TYPE_MASTER, "Maestra", "./icons/bkac_type_master.png"),
//				new BmFieldOption(TYPE_CHECKS, "Cheques", "./icons/bkac_type_checks.png"),
//				new BmFieldOption(TYPE_SAVINGS, "Ahorro", "./icons/bkac_type_savings.png"),
//				new BmFieldOption(TYPE_CASH, "Efectivo", "./icons/bkac_type_cash.png")
//				)));
		status = setField("status", "" + STATUS_OPEN, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_OPEN, "Abierta", "./icons/wflu_lockstatus_open.png"),
				new BmFieldOption(STATUS_CANCEL, "Cancelada", "./icons/wflu_lockstatus_locked.png")
				)));

		openDate = setField("opendate", "", "Apertura", 12, Types.DATE, false, BmFieldType.DATE, false);
		accountNo = setField("accountno", "", "No.Cuenta", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		clabe = setField("clabe", "", "Clabe", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		cutDate = setField("cutdate", "", "Fecha de Corte", 12, Types.DATE, false, BmFieldType.DATE, false);		
		bankName = setField("bankname", "", "Banco", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		branch = setField("branch", "", "Sucursal", 20, Types.VARCHAR, false, BmFieldType.STRING, false);
		repName = setField("repname", "", "Representante", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		phone = setField("phone", "", "Telefóno", 30, Types.VARCHAR, false, BmFieldType.PHONE, false);
		checkNo = setField("checkno", "", "No.Cheque", 5, Types.INTEGER, true, BmFieldType.NUMBER, false);
		balance = setField("balance","", "Balance",20, Types.DOUBLE,true, BmFieldType.CURRENCY, false);
		companyId = setField("companyid", "", "Empresa", 4, Types.INTEGER, false, BmFieldType.ID, false);
		profileId = setField("profileid", "", "Perfil. Concil.", 4, Types.INTEGER, true, BmFieldType.ID, false);
		responsibleId = setField("responsibleid", "", "Responsable", 4, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda", 4, Types.INTEGER, true, BmFieldType.ID, false);
		bankRfc = setField("bankrfc", "", "RFC del Banco", 13, Types.VARCHAR, true, BmFieldType.RFC, false);
		bankAccountTypeId = setField("bankaccounttypeid", "", "Tipo de cuenta", 4, Types.INTEGER, true, BmFieldType.ID, false);

		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getBmoCompany().getName(),
				getBankName(),
				getAccountNo(),
				getBmoBankAccountType().getName(),
				getCutDate(),
				getBmoCurrency().getCode(),
				getStatus()
				//getBalance()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getAccountNo(),
				getBmoBankAccountType().getName(),
				getBalance()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName().getName(), getName().getLabel()), 
				new BmSearchField(getDescription().getName(), getDescription().getLabel()),
				new BmSearchField(getAccountNo().getName(), getAccountNo().getLabel())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
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
	 * @return the type
	 */
//	public BmField getType() {
//		return type;
//	}
//	/**
//	 * @param type the type to set
//	 */
//	public void setType(BmField type) {
//		this.type = type;
//	}
	/**
	 * @return the companyId
	 */
	public BmField getCompanyId() {
		return companyId;
	}
	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}
	/**
	 * @return the openDate
	 */
	public BmField getOpenDate() {
		return openDate;
	}
	/**
	 * @param openDate the openDate to set
	 */
	public void setOpenDate(BmField openDate) {
		this.openDate = openDate;
	}
	/**
	 * @return the accountNo
	 */
	public BmField getAccountNo() {
		return accountNo;
	}
	/**
	 * @param accountNo the accountNo to set
	 */
	public void setAccountNo(BmField accountNo) {
		this.accountNo = accountNo;
	}
	/**
	 * @return the format
	 */
	public BmField getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(BmField format) {
		this.format = format;
	}
	/**
	 * @return the cutDate
	 */
	public BmField getCutDate() {
		return cutDate;
	}
	/**
	 * @param cutDate the cutDate to set
	 */
	public void setCutDate(BmField cutDate) {
		this.cutDate = cutDate;
	}

	/**
	 * @return the bankName
	 */
	public BmField getBankName() {
		return bankName;
	}
	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(BmField bankName) {
		this.bankName = bankName;
	}
	/**
	 * @return the branch
	 */
	public BmField getBranch() {
		return branch;
	}
	/**
	 * @param branch the branch to set
	 */
	public void setBranch(BmField branch) {
		this.branch = branch;
	}
	/**
	 * @return the logo
	 */
	public BmField getLogo() {
		return logo;
	}
	/**
	 * @param logo the logo to set
	 */
	public void setLogo(BmField logo) {
		this.logo = logo;
	}
	/**
	 * @return the repName
	 */
	public BmField getRepName() {
		return repName;
	}
	/**
	 * @param repName the repName to set
	 */
	public void setRepName(BmField repName) {
		this.repName = repName;
	}
	/**
	 * @return the phone
	 */
	public BmField getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(BmField phone) {
		this.phone = phone;
	}
	/**
	 * @return the checkNo
	 */
	public BmField getCheckNo() {
		return checkNo;
	}
	/**
	 * @param checkNo the checkNo to set
	 */
	public void setCheckNo(BmField checkNo) {
		this.checkNo = checkNo;
	}
	/**
	 * @return the balance
	 */
	public BmField getBalance() {
		return balance;
	}
	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BmField balance) {
		this.balance = balance;
	}
	/**
	 * @return the bmoCompany
	 */
	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}
	/**
	 * @param bmoCompany the bmoCompany to set
	 */
	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

	/**
	 * @return the profileId
	 */
	public BmField getProfileId() {
		return profileId;
	}

	/**
	 * @param profileId the profileId to set
	 */
	public void setprofileId(BmField profileId) {
		this.profileId = profileId;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmoCurrency getBmoCurrency() {
		return bmoCurrency;
	}

	public void setBmoCurrency(BmoCurrency bmoCurrency) {
		this.bmoCurrency = bmoCurrency;
	}

	public BmField getResponsibleId() {
		return responsibleId;
	}

	public void setResponsibleId(BmField responsibleId) {
		this.responsibleId = responsibleId;
	}

	public BmField getClabe() {
		return clabe;
	}

	public void setClabe(BmField clabe) {
		this.clabe = clabe;
	}

	public BmField getBankRfc() {
		return bankRfc;
	}

	public void setBankRfc(BmField bankRfc) {
		this.bankRfc = bankRfc;
	}
	

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getBankAccountTypeId() {
		return bankAccountTypeId;
	}

	public void setBankAccountTypeId(BmField bankAccountTypeId) {
		this.bankAccountTypeId = bankAccountTypeId;
	}

	public BmoBankAccountType getBmoBankAccountType() {
		return bmoBankAccountType;
	}

	public void setBmoBankAccountType(BmoBankAccountType bmoBankAccountType) {
		this.bmoBankAccountType = bmoBankAccountType;
	}

}

