/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.op.BmoReqPayType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;

public class BmoCustomer extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private BmField code, customertype, status, legalname, firstname, fatherlastname, motherlastname, displayName, birthdate, 
	rfc, www, referralComments, logo, position, titleId, currencyId, territoryId, establishmentDate, phone, extension, 
	mobile, email, consultingServiceId, regionId, rating, accountOwnerId, salesmanId, referralId, tags, 
	curp, nss, parentId, income, recommendedBy, industryId, creditLimit, reqPayTypeId, customercategory, rateTypeId, 
	rate, user, passw, passwconf,lessorMasterId ,cfdiid, paymethodid, payConditionId, developmentRate, lead,
	nationalityId,oficialIdentify, maritalStatusId, marketId;

	private BmoUser bmoUser = new BmoUser();
	private BmoTerritory bmoTerritory = new BmoTerritory();
	private BmoReqPayType bmoReqPayType = new BmoReqPayType();
	private BmoMaritalStatus bmoMaritalStatus = new BmoMaritalStatus();

	// CustomerTypes
	public static char TYPE_PERSON = 'P';
	public static char TYPE_COMPANY = 'C';
	// CustomerCategory
	public static char CATEGORY_LESSOR = 'R';
	public static char CATEGORY_LESSEE = 'E';

	public static char STATUS_PROSPECT = 'P';
	public static char STATUS_INACTIVE = 'I';
	public static char STATUS_ACTIVE = 'A';

	public static char MARITALSTATUS_SINGLE = 'S';
	public static char MARITALSTATUS_MARRIED = 'M';
	public static char MARITALSTATUS_UNION = 'U';

	public static String STATUSCHANGE = "CUSTCH";
	public static String RFCCHANGE = "CUSTRFCCH";
	public static String TYPECHANGE = "CUSTTYPECH";
	public static String SALESMANCHANGE = "CUSTSMCH";

	public static char RATING_ONE = 'A';
	public static char RATING_TWO = 'B';
	public static char RATING_THREE = 'C';

	public static String ACTION_SHOWOPPO = "SHOWOPPO";
	public static String ACTION_SHOWPROJ = "SHOWPROJ";

	public static String CODE_PREFIX = "CL-";


	public static String ACTION_GETCUSTOMERCONTACT = "CUSTOMERCONTACT";
	public BmoCustomer() {
		super("com.flexwm.server.cm.PmCustomer", "customers", "customerid", "CUST", "Clientes / Prospectos");

		code = setField("code", "", "Clave Cliente", 10, Types.VARCHAR, true, BmFieldType.CODE, false);

		customertype = setField("customertype", "" + BmoCustomer.TYPE_PERSON, "Tipo Cliente", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		customertype.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_PERSON, "Persona", "./icons/cust_type_person.png"),
				new BmFieldOption(TYPE_COMPANY, "Empresa", "./icons/cust_type_company.png")
				)));

		status = setField("status", "" + STATUS_PROSPECT, "Status", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_PROSPECT, "Prospecto", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_ACTIVE, "Activo", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_INACTIVE, "Inactivo", "./icons/status_closed.png")
				)));
		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);
		position = setField("position", "", "Cargo", 50, Types.VARCHAR, true, BmFieldType.STRING, false);

		legalname = setField("legalname", "", "Razón Social", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		displayName = setField("displayname", "", "Nom. Cliente", 200, Types.VARCHAR, true, BmFieldType.STRING, false);
		firstname = setField("firstname", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		fatherlastname = setField("fatherlastname", "", "Apellido Paterno", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		motherlastname = setField("motherlastname", "", "Apellido Materno", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		birthdate = setField("birthdate", "", "Fecha Nac.", 12, Types.DATE, true, BmFieldType.DATE, false);		
		establishmentDate = setField("establishmentdate", "", "Fecha Creación", 12, Types.DATE, true, BmFieldType.DATE, false);
		rfc = setField("rfc", "", "RFC", 13, Types.VARCHAR, true, BmFieldType.RFC, true);
		curp = setField("curp", "", "CURP", 25, Types.VARCHAR, true, BmFieldType.STRING, true);
		nss = setField("nss", "", "NSS", 25, Types.VARCHAR, true, BmFieldType.STRING, true);
		income = setField("income", "", "Ingresos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

		email = setField("email", "", "Email", 50, Types.VARCHAR, true, BmFieldType.EMAIL, false);
		phone = setField("phone", "", "Teléfono", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		extension = setField("extension", "", "Ext.", 5, Types.VARCHAR, true, BmFieldType.STRING, false);
		mobile = setField("mobile", "", "Móvil", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);

		referralComments = setField("referralcomments", "", "Notas Ref.", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		logo = setField("logo", "", "Logo", 500, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		www = setField("www", "", "Sitio Web", 50, Types.VARCHAR, true, BmFieldType.WWW, false);

		//		maritalStatus = setField("maritalstatus", "", "Estado Civil", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		//		maritalStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
		//				new BmFieldOption(MARITALSTATUS_SINGLE, "Soltero", "./icons/cust_maritalstatus_single.png"),
		//				new BmFieldOption(MARITALSTATUS_MARRIED, "Casado", "./icons/cust_maritalstatus_married.png"),
		//				new BmFieldOption(MARITALSTATUS_UNION, "Unión Libre", "./icons/cust_maritalstatus_union.png")
		//				)));

		parentId = setField("parentid", "", "Empleador", 8, Types.INTEGER, true, BmFieldType.ID, false);
		accountOwnerId = setField("accountownerid", "", "Representante Comercial", 8, Types.INTEGER, true, BmFieldType.ID, false);
		salesmanId = setField("salesmanid", "", "Vendedor", 8, Types.INTEGER, false, BmFieldType.ID, false);
		referralId = setField("referralid", "", "Referencia", 8, Types.INTEGER, true, BmFieldType.ID, false);
		titleId = setField("titleid", "", "Título", 8, Types.INTEGER, true, BmFieldType.ID, false);
		currencyId = setField("currencyid", "", "Moneda Predet.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		recommendedBy = setField("recommendedby", "", "Recom. Por?:", 8, Types.INTEGER, true, BmFieldType.ID, false);
		industryId = setField("industryid", "", "Giro", 8, Types.INTEGER, true, BmFieldType.ID, false);
		territoryId = setField("territoryid", "", "Territorio", 8, Types.INTEGER, true, BmFieldType.ID, false);
		consultingServiceId = setField("consultingserviceid", "", "Servicios Por", 8, Types.INTEGER, true, BmFieldType.ID, false);
		regionId = setField("regionid", "", "Región", 8, Types.INTEGER, true, BmFieldType.ID, false);
		rating = setField("rating", "", "Rating", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		rating.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(RATING_ONE, "A", "./icons/cust_rating_one.png"),
				new BmFieldOption(RATING_TWO, "B", "./icons/cust_rating_two.png"),
				new BmFieldOption(RATING_THREE, "C", "./icons/cust_rating_three.png")
				)));

		//Limite de Credito
		creditLimit = setField("creditlimit", "", "Limite Crédito", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		reqPayTypeId = setField("reqpaytypeid", "", "Término de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		//section facturacion

		paymethodid = setField("paymethodid", "", "Método de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		cfdiid = setField("cfdiid", "", "Uso CFDI", 8, Types.INTEGER, true, BmFieldType.ID, false);

		//Campo inadico
		customercategory = setField("customercategory", "", "Categoría Cliente", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		customercategory.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(CATEGORY_LESSEE, "Arrendador", "./icons/cust_category_lessee.png"),
				new BmFieldOption(CATEGORY_LESSOR, "Arrendatario", "./icons/cust_category_leesor.png")
				)));	
		rateTypeId = setField("ratetypeid", "", "Tipo de Tarifa", 8, Types.INTEGER, true, BmFieldType.ID, false);
		rate = setField("rate", "", "Tarifa", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		user = setField("user", "", "Usuario",10,Types.VARCHAR,true, BmFieldType.STRING,true	);
		passw = setField("passw", "", "Contraseña", 255, Types.VARCHAR, true,BmFieldType.PASSWORD, false);
		passwconf = setField("passwconf", "", "Conf. Contraseña", 255, Types.VARCHAR, true,BmFieldType.PASSWORD, false);
		lessorMasterId = setField("lessormasterid", "", "Arrendador Maestro",8 , Types.INTEGER, true, BmFieldType.ID, false);

		payConditionId = setField("payconditionid", "", "Condición de Pago", 8, Types.INTEGER, true, BmFieldType.ID, false);
		developmentRate = setField("developmentrate", "", "Tarifa Desarrollo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		lead = setField("lead", "", "Lead", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		nationalityId = setField("nationalityid", "", "Nacionalidad", 10, Types.INTEGER, true, BmFieldType.ID, false);
		oficialIdentify = setField("oficialidentify", "", "Identificacion Oficial", 255, Types.VARCHAR, true,BmFieldType.STRING, false);
		maritalStatusId = setField("maritalstatusid", "", "Estado Civil", 10, Types.INTEGER, true, BmFieldType.ID, false);
		marketId = setField("marketid", "", "Mercado Predet.", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}


	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getDisplayName(),
				getLogo(),
				getBmoUser().getCode(),
				getRfc(),
				getCustomertype(),
				getEmail(),
				getMobile(),
				getBmoTerritory().getName(),
				getBmoReqPayType().getName(),
				getTags()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getDisplayName(),
				getEmail(),
				getPhone()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getFirstname()),
				new BmSearchField(getFatherlastname()),
				new BmSearchField(getMotherlastname()),
				new BmSearchField(getLegalname()),
				new BmSearchField(getDisplayName()),
				new BmSearchField(getRfc()),
				new BmSearchField(getNss()),
				new BmSearchField(getMobile()),
				new BmSearchField(getPhone()),
				new BmSearchField(getEmail()),
				new BmSearchField(getTags())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	public BmField getCustomertype() {
		return customertype;
	}

	public void setCustomertype(BmField customertype) {
		this.customertype = customertype;
	}

	public BmField getLegalname() {
		return legalname;
	}

	public void setLegalname(BmField legalname) {
		this.legalname = legalname;
	}

	public BmField getFirstname() {
		return firstname;
	}

	public void setFirstname(BmField firstname) {
		this.firstname = firstname;
	}

	public BmField getFatherlastname() {
		return fatherlastname;
	}

	public void setFatherlastname(BmField fatherlastname) {
		this.fatherlastname = fatherlastname;
	}

	public BmField getMotherlastname() {
		return motherlastname;
	}

	public void setMotherlastname(BmField motherlastname) {
		this.motherlastname = motherlastname;
	}

	public BmField getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(BmField birthdate) {
		this.birthdate = birthdate;
	}

	public BmField getRfc() {
		return rfc;
	}

	public void setRfc(BmField rfc) {
		this.rfc = rfc;
	}

	public BmField getWww() {
		return www;
	}

	public void setWww(BmField www) {
		this.www = www;
	}

	public BmField getSalesmanId() {
		return salesmanId;
	}

	public void setSalesmanId(BmField salesmanId) {
		this.salesmanId = salesmanId;
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getReferralComments() {
		return referralComments;
	}

	public void setReferralComments(BmField referralComments) {
		this.referralComments = referralComments;
	}

	public BmField getReferralId() {
		return referralId;
	}

	public void setReferralId(BmField referralId) {
		this.referralId = referralId;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getDisplayName() {
		return displayName;
	}

	public void setDisplayName(BmField displayName) {
		this.displayName = displayName;
	}

	public BmField getTitleId() {
		return titleId;
	}

	public void setTitleId(BmField titleId) {
		this.titleId = titleId;
	}

	public BmField getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(BmField currencyId) {
		this.currencyId = currencyId;
	}

	public BmField getLogo() {
		return logo;
	}

	public void setLogo(BmField logo) {
		this.logo = logo;
	}

	public BmField getEstablishmentDate() {
		return establishmentDate;
	}

	public void setEstablishmentDate(BmField establishmentDate) {
		this.establishmentDate = establishmentDate;
	}

	public BmField getTags() {
		return tags;
	}

	public void setTags(BmField tags) {
		this.tags = tags;
	}

	public BmField getPosition() {
		return position;
	}

	public void setPosition(BmField position) {
		this.position = position;
	}

	//	public BmField getMaritalStatus() {
	//		return maritalStatus;
	//	}
	//
	//	public void setMaritalStatus(BmField maritalStatus) {
	//		this.maritalStatus = maritalStatus;
	//	}

	public BmField getCurp() {
		return curp;
	}

	public void setCurp(BmField curp) {
		this.curp = curp;
	}

	public BmField getNss() {
		return nss;
	}

	public void setNss(BmField nss) {
		this.nss = nss;
	}

	public BmField getParentId() {
		return parentId;
	}

	public void setParentId(BmField parentId) {
		this.parentId = parentId;
	}

	public BmField getIncome() {
		return income;
	}

	public void setIncome(BmField income) {
		this.income = income;
	}

	public BmField getRecommendedBy() {
		return recommendedBy;
	}

	public void setRecommendedBy(BmField recommendedBy) {
		this.recommendedBy = recommendedBy;
	}

	public BmField getIndustryId() {
		return industryId;
	}

	public void setIndustryId(BmField industryId) {
		this.industryId = industryId;
	}

	public BmField getPhone() {
		return phone;
	}

	public void setPhone(BmField phone) {
		this.phone = phone;
	}

	public BmField getExtension() {
		return extension;
	}

	public void setExtension(BmField extension) {
		this.extension = extension;
	}

	public BmField getEmail() {
		return email;
	}

	public void setEmail(BmField email) {
		this.email = email;
	}

	public BmField getMobile() {
		return mobile;
	}

	public void setMobile(BmField mobile) {
		this.mobile = mobile;
	}

	public BmField getTerritoryId() {
		return territoryId;
	}

	public void setTerritoryId(BmField territoryId) {
		this.territoryId = territoryId;
	}

	public BmField getConsultingServiceId() {
		return consultingServiceId;
	}

	public void setConsultingServiceId(BmField consultingServiceId) {
		this.consultingServiceId = consultingServiceId;
	}

	public BmField getRegionId() {
		return regionId;
	}

	public void setRegionId(BmField regionId) {
		this.regionId = regionId;
	}

	public BmField getRating() {
		return rating;
	}

	public void setRating(BmField rating) {
		this.rating = rating;
	}

	public BmField getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BmField creditLimit) {
		this.creditLimit = creditLimit;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmoTerritory getBmoTerritory() {
		return bmoTerritory;
	}

	public void setBmoTerritory(BmoTerritory bmoTerritory) {
		this.bmoTerritory = bmoTerritory;
	}

	public BmField getReqPayTypeId() {
		return reqPayTypeId;
	}

	public void setReqPayTypeId(BmField reqPayTypeId) {
		this.reqPayTypeId = reqPayTypeId;
	}

	public BmoReqPayType getBmoReqPayType() {
		return bmoReqPayType;
	}

	public void setBmoReqPayType(BmoReqPayType bmoReqPayType) {
		this.bmoReqPayType = bmoReqPayType;
	}

	public BmField getCustomercategory() {
		return customercategory;
	}

	public void setCustomercategory(BmField customercategory) {
		this.customercategory = customercategory;
	}	

	public BmField getRateTypeId() {
		return rateTypeId;
	}

	public void setRateTypeId(BmField rateTypeId) {
		this.rateTypeId = rateTypeId;
	}

	public BmField getRate() {
		return rate;
	}

	public void setRate(BmField rate) {
		this.rate = rate;
	}

	public BmField getUser() {
		return user;
	}

	public void setUser(BmField user) {
		this.user = user;
	}

	public BmField getPassw() {
		return passw;
	}

	public void setPassw(BmField passw) {
		this.passw = passw;
	}

	public BmField getPasswconf() {
		return passwconf;
	}

	public void setPasswconf(BmField passwconf) {
		this.passwconf = passwconf;
	}

	public BmField getLessorMasterId() {
		return lessorMasterId;
	}

	public void setLessorMasterId(BmField lessorMasterId) {
		this.lessorMasterId = lessorMasterId;
	}

	public BmField getCfdiid() {
		return cfdiid;
	}

	public void setCfdiid(BmField cfdiid) {
		this.cfdiid = cfdiid;
	}

	public BmField getPaymethodid() {
		return paymethodid;
	}

	public void setPaymethodid(BmField paymethodid) {
		this.paymethodid = paymethodid;
	}

	public BmField getPayConditionId() {
		return payConditionId;
	}

	public void setPayConditionId(BmField payConditionId) {
		this.payConditionId = payConditionId;
	}

	public BmField getAccountOwnerId() {
		return accountOwnerId;
	}

	public void setAccountOwnerId(BmField accountOwnerId) {
		this.accountOwnerId = accountOwnerId;
	}

	public BmField getDevelopmentRate() {
		return developmentRate;
	}

	public void setDevelopmentRate(BmField developmentRate) {
		this.developmentRate = developmentRate;
	}

	public BmField getLead() {
		return lead;
	}

	public void setLead(BmField lead) {
		this.lead = lead;
	}

	public BmField getNationalityId() {
		return nationalityId;
	}

	public void setNationalityId(BmField nationalityId) {
		this.nationalityId = nationalityId;
	}

	public BmField getOficialIdentify() {
		return oficialIdentify;
	}

	public void setOficialIdentify(BmField oficialIdentify) {
		this.oficialIdentify = oficialIdentify;
	}

	public BmField getMaritalStatusId() {
		return maritalStatusId;
	}

	public void setMaritalStatusId(BmField maritalStatusId) {
		this.maritalStatusId = maritalStatusId;
	}

	public BmoMaritalStatus getBmoMaritalStatus() {
		return bmoMaritalStatus;
	}

	public void setBmoMaritalStatus(BmoMaritalStatus bmoMaritalStatus) {
		this.bmoMaritalStatus = bmoMaritalStatus;
	}

	public BmField getMarketId() {
		return marketId;
	}

	public void setMarketId(BmField marketId) {
		this.marketId = marketId;
	}

}
