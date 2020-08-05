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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author jhernandez
 *
 */

public class BmoSupplier extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, legalName, type, description, supplierCategoryId, officePhone1, email, rfc, legalRep, imss, fiscalType, 
	lawyerName, lawyerNumber, lawyerDeed, lawyerDeedDate, cityDeedId, currencyId, sendEmail;

	private BmoSupplierCategory bmoSupplierCategory;

	// Tipo de Proveedor
	public static final char TYPE_SUPPLIER = 'S';
	public static final char TYPE_CONTRACTOR = 'C';	
	public static final char TYPE_BROKER = 'B';
	public static final char TYPE_FINANCIAL = 'F';

	// Tipo Fiscal
	public static final char FISCALTYPE_COMPANY = 'C'; 
	public static final char FISCALTYPE_PERSON = 'P';

	public static String CODE_PREFIX = "PV-";

	public BmoSupplier() {
		super("com.flexwm.server.op.PmSupplier","suppliers", "supplierid", "SUPL","Proveedores");

		//Campo de Datos		
		code = setField("code", "", "Clave Prov.", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		legalName = setField("legalname", "", "Razón Social", 200, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_SUPPLIER, "Proveedor", "./icons/supl_type_supplier.png"),
				new BmFieldOption(TYPE_CONTRACTOR, "Subcontratista", "./icons/supl_type_contractor.png"),
				new BmFieldOption(TYPE_BROKER, "Comisionista", "./icons/supl_type_broker.png")
				)));
		supplierCategoryId = setField("suppliercategoryid", "", "Categoría Proveedor", 8, Types.INTEGER, true, BmFieldType.ID, false);
		officePhone1 = setField("officephone1", "", "Tel.Oficina", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		email = setField("email", "", "Email", 50, Types.VARCHAR, true, BmFieldType.EMAIL, false);
		rfc = setField("rfc", "", "RFC", 13, Types.VARCHAR, true, BmFieldType.STRING, false);
		legalRep = setField("legalRep", "", "Representante Legal", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		imss = setField("imss", "", "IMSS", 15, Types.VARCHAR, true, BmFieldType.STRING, false);				

		fiscalType= setField("fiscaltype", "", "Régimen", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		fiscalType.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(FISCALTYPE_COMPANY, "Persona Moral", "./icons/supl_fiscaltype_company.png"),
				new BmFieldOption(FISCALTYPE_PERSON, "Persona Física", "./icons/supl_fiscaltype_person.png")
				)));
		//Datos Notario
		lawyerName = setField("lawyername", "", "Nom. Notario Acta", 40, Types.VARCHAR, true, BmFieldType.STRING, false);
		lawyerNumber = setField("lawyernumber", "", "Notaria No.", 40, Types.VARCHAR, true, BmFieldType.STRING, false);
		lawyerDeed = setField("lawyerdeed", "", "No.Escritura", 40, Types.VARCHAR, true, BmFieldType.STRING, false);
		lawyerDeedDate = setField("lawyerdeeddate", "", "Fecha Escritura", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		cityDeedId = setField("citydeedid", "", "Ciudad Escritura", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		currencyId = setField("currencyid", "", "Moneda", 8, Types.INTEGER, true, BmFieldType.ID, false);
		//Envio de email al conciliar un MB
		sendEmail = setField("sendemail", "", "Email al Conciliar", 8, Types.INTEGER, false, BmFieldType.BOOLEAN, false);

		bmoSupplierCategory = new BmoSupplierCategory();		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getOfficePhone1(),	
				getRfc(),	 	
				getEmail(),				
				bmoSupplierCategory.getName(),
				getType()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getOfficePhone1(),				
				getEmail()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
				new BmSearchField(getLegalName()), 
				new BmSearchField(getLegalRep()), 
				new BmSearchField(getDescription())));
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
	 * @return the type
	 */
	public BmField getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(BmField type) {
		this.type = type;
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
	 * @return the supplierCategoryId
	 */
	public BmField getSupplierCategoryId() {
		return supplierCategoryId;
	}

	/**
	 * @param supplierCategoryId the supplierCategoryId to set
	 */
	public void setSupplierCategoryId(BmField supplierCategoryId) {
		this.supplierCategoryId = supplierCategoryId;
	}

	/**
	 * @return the email
	 */
	public BmField getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(BmField email) {
		this.email = email;
	}

	/**
	 * @return the rfc
	 */
	public BmField getRfc() {
		return rfc;
	}

	/**
	 * @param rfc the rfc to set
	 */
	public void setRfc(BmField rfc) {
		this.rfc = rfc;
	}

	/**
	 * @return the legalRep
	 */
	public BmField getLegalRep() {
		return legalRep;
	}

	/**
	 * @param legalRep the legalRep to set
	 */
	public void setLegalRep(BmField legalRep) {
		this.legalRep = legalRep;
	}

	/**
	 * @return the imss
	 */
	public BmField getImss() {
		return imss;
	}

	/**
	 * @param imss the imss to set
	 */
	public void setImss(BmField imss) {
		this.imss = imss;
	}


	/**
	 * @return the fiscalType
	 */
	public BmField getFiscalType() {
		return fiscalType;
	}

	/**
	 * @param fiscalType the fiscalType to set
	 */
	public void setFiscalType(BmField fiscalType) {
		this.fiscalType = fiscalType;
	}

	/**
	 * @return the lawyerNumber
	 */
	public BmField getLawyerNumber() {
		return lawyerNumber;
	}

	/**
	 * @param lawyerNumber the lawyerNumber to set
	 */
	public void setLawyerNumber(BmField lawyerNumber) {
		this.lawyerNumber = lawyerNumber;
	}

	/**
	 * @return the bmoSupplierCategory
	 */
	public BmoSupplierCategory getBmoSupplierCategory() {
		return bmoSupplierCategory;
	}

	/**
	 * @param bmoSupplierCategory the bmoSupplierCategory to set
	 */
	public void setBmoSupplierCategory(BmoSupplierCategory bmoSupplierCategory) {
		this.bmoSupplierCategory = bmoSupplierCategory;
	}

	public BmField getLegalName() {
		return legalName;
	}

	public void setLegalName(BmField legalName) {
		this.legalName = legalName;
	}

	/**
	 * @return the lawyerDeed
	 */
	public BmField getLawyerDeed() {
		return lawyerDeed;
	}

	/**
	 * @param lawyerDeed the lawyerDeed to set
	 */
	public void setLawyerDeed(BmField lawyerDeed) {
		this.lawyerDeed = lawyerDeed;
	}

	/**
	 * @return the lawyerDeedDate
	 */
	public BmField getLawyerDeedDate() {
		return lawyerDeedDate;
	}

	/**
	 * @param lawyerDeedDate the lawyerDeedDate to set
	 */
	public void setLawyerDeedDate(BmField lawyerDeedDate) {
		this.lawyerDeedDate = lawyerDeedDate;
	}

	/**
	 * @return the cityDeedId
	 */
	public BmField getCityDeedId() {
		return cityDeedId;
	}

	/**
	 * @param cityDeedId the cityDeedId to set
	 */
	public void setCityDeedId(BmField cityDeedId) {
		this.cityDeedId = cityDeedId;
	}

	/**
	 * @return the lawyerName
	 */
	public BmField getLawyerName() {
		return lawyerName;
	}

	/**
	 * @param lawyerName the lawyerName to set
	 */
	public void setLawyerName(BmField lawyerName) {
		this.lawyerName = lawyerName;
	}

	/**
	 * @return the currencyId
	 */
	public BmField getCurrencyId() {
		return currencyId;
	}

	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	/**
	 * @return the officePhone1
	 */
	public BmField getOfficePhone1() {
		return officePhone1;
	}

	/**
	 * @param officePhone1 the officePhone1 to set
	 */
	public void setOfficePhone1(BmField officePhone1) {
		this.officePhone1 = officePhone1;
	}

	/**
	 * @return the sendEmail
	 */
	public BmField getSendEmail() {
		return sendEmail;
	}

	/**
	 * @param sendEmail the sendEmail to set
	 */
	public void setSendEmail(BmField sendEmail) {
		this.sendEmail = sendEmail;
	}
}
