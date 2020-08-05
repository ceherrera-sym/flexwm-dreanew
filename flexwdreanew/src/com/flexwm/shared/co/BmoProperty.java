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

public class BmoProperty extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, description, lot, street, number, progress, cansell, available, landSize,
	finishDate, dueDate, publicLandSize, constructionSize, extraPrice, registryNumber, propertyModelId, developmentBlockId,habitability, 
	developmentRegistryId, adjoins, facade, blueprint, coordinates, companyId, customerId,
	neighborhood,zip,cityId,betweenStreets,contract,propertyReceipt,propertyTitle,constitutiveAct,renewOrder,
	certifiedWriting,demarcation,notaryQuotation,appraise,debtCertificate,taxCertificate,waterBill,electricityBill,otherDocuments,tags;

	BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();

	public static String CODE_PREFIX = "INM-";
	public static String ACCESS_CHANGECOMPANY = "PRTYCHCOMP";
	public static String ACCESS_ADDPROPERTYTA = "PRTYADDPRT";

	public BmoProperty() {
		super("com.flexwm.server.co.PmProperty", "properties", "propertyid", "PRTY", "Inmuebles");

		code = setField("code", "", "Clave Inm.", 50, Types.VARCHAR, true, BmFieldType.CODE, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		lot = setField("lot", "", "Lote", 15, Types.INTEGER, true, BmFieldType.NUMBER, false);
		street = setField("street", "", "Calle", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		number = setField("number", "", "# Oficial", 15, Types.VARCHAR, true, BmFieldType.NUMBER, false);
		progress = setField("progress", "", "% Avance", 11, Types.INTEGER, true, BmFieldType.PERCENTAGE, false);
		cansell = setField("cansell", "", "Inm. Abierto", 11, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		habitability = setField("habitability", "", "Habitable", 11, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		available = setField("available", "", "Disponible", 11, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		landSize = setField("landsize", "", "m2 Terr.", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		finishDate = setField("finishdate", "", "Fecha Term.", 11, Types.DATE, true, BmFieldType.DATE, false);
		dueDate = setField("duedate", "", "Fecha Prog.", 11, Types.DATE, true, BmFieldType.DATE, false);
		publicLandSize = setField("publiclandsize", "", "m2 T. Comun", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		constructionSize = setField("constructionsize", "", "m2 Const.", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		extraPrice = setField("extraprice", "", "$ Extra", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		registryNumber = setField("registrynumber", "", "# Predial", 16, Types.INTEGER, true, BmFieldType.NUMBER, false);
		propertyModelId = setField("propertymodelid", "", "Modelo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		adjoins = setField("adjoins", "", "Colindancias", 1500, Types.VARCHAR, true, BmFieldType.STRING, false);
		developmentBlockId = setField("developmentblockid", "", "Manzana", 8, Types.INTEGER, true, BmFieldType.ID, false);
		developmentRegistryId = setField("developmentregistryid", "", "Paquete", 8, Types.INTEGER, true, BmFieldType.ID, false);

		facade = setField("facade", "", "Fachada", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		blueprint = setField("blueprint", "", "Plano", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		
		contract = setField("contract", "", "Contrato", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		propertyReceipt = setField("propertyreceipt", "", "Recibo Predial", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		propertyTitle = setField("propertytitle", "", "Titulo Propiedad", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		constitutiveAct = setField("constitutiveact", "", "Acta Constitutiva", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);	
		
		coordinates = setField("coordinates", "", "Ubicación", 50, Types.VARCHAR, true, BmFieldType.GPSCOORDINATES, false);

		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, true, BmFieldType.ID, false);
		neighborhood = setField("neighborhood", "", "Colonia", 60, Types.VARCHAR, true, BmFieldType.STRING, false);
		zip = setField("zip", "", "C. P.", 7, Types.VARCHAR, true, BmFieldType.STRING, false);
		cityId = setField("cityid", "", "Ciudad", 5, Types.INTEGER, true, BmFieldType.ID, false);			
		betweenStreets = setField("betweenstreets", "", "Entre Calles", 200, Types.VARCHAR, true, BmFieldType.STRING, false);
		//Campos INADICO para renovar pedido
		renewOrder = setField("reneworder", "0", "Renovar Pedido", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);		
		customerId = setField("customerid", "", "Arrendador", 20, Types.INTEGER, true, BmFieldType.ID, false);
		
		certifiedWriting = setField("certifiedwriting", "", "Escritura Certificada ", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		demarcation = setField("demarcation", "", "Deslinde ", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		notaryQuotation = setField("notaryquotation", "", "Cotización Notaría ", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		appraise = setField("appraise", "", "Avalúo ", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		debtCertificate = setField("debtcertificate", "", "Certificado No Adeudo ", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		taxCertificate = setField("taxcertificate", "", "Certificado Gravamen", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		waterBill = setField("waterbill", "", "Recibo Agua ", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		electricityBill = setField("electricitybill", "", "Recibo Luz ", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		otherDocuments = setField("otherdocuments" , "", "Otros", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		tags = setField("tags", "", "Tags", 255, Types.VARCHAR, true, BmFieldType.TAGS, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getDescription(),
				//getBmoDevelopmentBlock().getCode(),
				//getLot(),
				getStreet(),
				//getNumber(),
				getLandSize(),
				getConstructionSize(),
				getFacade(),
				getBlueprint(),
				getCoordinates(),
				getCansell(),
				getAvailable(),
				getHabitability(),
				getProgress(),
				getTags()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getCansell(),
				getAvailable()
				));
	}


	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getDescription()),
				new BmSearchField(getTags())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoDevelopmentBlock().getBmoDevelopmentPhase().getCode(), BmOrder.ASC),
				new BmOrder(getKind(), getBmoDevelopmentBlock().getCode(), BmOrder.ASC),
				new BmOrder(getKind(), getLot(), BmOrder.ASC),
				new BmOrder(getKind(), getCode(), BmOrder.ASC)
				));
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
	 * @return the street
	 */
	public BmField getStreet() {
		return street;
	}


	/**
	 * @param street the street to set
	 */
	public void setStreet(BmField street) {
		this.street = street;
	}


	/**
	 * @return the number
	 */
	public BmField getNumber() {
		return number;
	}


	/**
	 * @param number the number to set
	 */
	public void setNumber(BmField number) {
		this.number = number;
	}


	/**
	 * @return the progess
	 */
	public BmField getProgress() {
		return progress;
	}


	/**
	 * @param progess the progess to set
	 */
	public void setProgress(BmField progress) {
		this.progress = progress;
	}


	/**
	 * @return the cansell
	 */
	public BmField getCansell() {
		return cansell;
	}


	/**
	 * @param cansell the cansell to set
	 */
	public void setCansell(BmField cansell) {
		this.cansell = cansell;
	}


	/**
	 * @return the landSize
	 */
	public BmField getLandSize() {
		return landSize;
	}


	/**
	 * @param landSize the landSize to set
	 */
	public void setLandSize(BmField landSize) {
		this.landSize = landSize;
	}


	/**
	 * @return the finishDate
	 */
	public BmField getFinishDate() {
		return finishDate;
	}


	/**
	 * @param finishDate the finishDate to set
	 */
	public void setFinishDate(BmField finishDate) {
		this.finishDate = finishDate;
	}


	/**
	 * @return the dueDate
	 */
	public BmField getDueDate() {
		return dueDate;
	}


	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(BmField dueDate) {
		this.dueDate = dueDate;
	}


	/**
	 * @return the publicLandSize
	 */
	public BmField getPublicLandSize() {
		return publicLandSize;
	}


	/**
	 * @param publicLandSize the publicLandSize to set
	 */
	public void setPublicLandSize(BmField publicLandSize) {
		this.publicLandSize = publicLandSize;
	}


	/**
	 * @return the constructionSize
	 */
	public BmField getConstructionSize() {
		return constructionSize;
	}


	/**
	 * @param constructionSize the constructionSize to set
	 */
	public void setConstructionSize(BmField constructionSize) {
		this.constructionSize = constructionSize;
	}


	/**
	 * @return the registryNumber
	 */
	public BmField getRegistryNumber() {
		return registryNumber;
	}


	/**
	 * @param registryNumber the registryNumber to set
	 */
	public void setRegistryNumber(BmField registryNumber) {
		this.registryNumber = registryNumber;
	}


	/**
	 * @return the propertyModelId
	 */
	public BmField getPropertyModelId() {
		return propertyModelId;
	}


	/**
	 * @param propertyModelId the propertyModelId to set
	 */
	public void setPropertyModelId(BmField propertyModelId) {
		this.propertyModelId = propertyModelId;
	}


	/**
	 * @return the developmentBlockId
	 */
	public BmField getDevelopmentBlockId() {
		return developmentBlockId;
	}


	/**
	 * @param developmentBlockId the developmentBlockId to set
	 */
	public void setDevelopmentBlockId(BmField developmentBlockId) {
		this.developmentBlockId = developmentBlockId;
	}


	/**
	 * @return the developmentRegistryId
	 */
	public BmField getDevelopmentRegistryId() {
		return developmentRegistryId;
	}


	/**
	 * @param developmentRegistryId the developmentRegistryId to set
	 */
	public void setDevelopmentRegistryId(BmField developmentRegistryId) {
		this.developmentRegistryId = developmentRegistryId;
	}

	public BmField getAvailable() {
		return available;
	}

	public void setAvailable(BmField available) {
		this.available = available;
	}

	public BmoDevelopmentBlock getBmoDevelopmentBlock() {
		return bmoDevelopmentBlock;
	}

	public void setBmoDevelopmentBlock(BmoDevelopmentBlock bmoDevelopmentBlock) {
		this.bmoDevelopmentBlock = bmoDevelopmentBlock;
	}

	public BmField getLot() {
		return lot;
	}

	public void setLot(BmField lot) {
		this.lot = lot;
	}

	public BmField getExtraPrice() {
		return extraPrice;
	}

	public void setExtraPrice(BmField extraPrice) {
		this.extraPrice = extraPrice;
	}

	/**
	 * @return the habitability
	 */
	public BmField getHabitability() {
		return habitability;
	}

	/**
	 * @param habitability the habitability to set
	 */
	public void setHabitability(BmField habitability) {
		this.habitability = habitability;
	}

	/**
	 * @return the adjoins
	 */
	public BmField getAdjoins() {
		return adjoins;
	}

	/**
	 * @param adjoins the adjoins to set
	 */
	public void setAdjoins(BmField adjoins) {
		this.adjoins = adjoins;
	}

	/**
	 * @return the facade
	 */
	public BmField getFacade() {
		return facade;
	}

	/**
	 * @return the blueprint
	 */
	public BmField getBlueprint() {
		return blueprint;
	}

	/**
	 * @return the coordinates
	 */
	public BmField getCoordinates() {
		return coordinates;
	}

	/**
	 * @param facade the facade to set
	 */
	public void setFacade(BmField facade) {
		this.facade = facade;
	}

	/**
	 * @param blueprint the blueprint to set
	 */
	public void setBlueprint(BmField blueprint) {
		this.blueprint = blueprint;
	}

	/**
	 * @param coordinates the coordinates to set
	 */
	public void setCoordinates(BmField coordinates) {
		this.coordinates = coordinates;
	}

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
	
	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	public BmField getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(BmField neighborhood) {
		this.neighborhood = neighborhood;
	}

	public BmField getZip() {
		return zip;
	}

	public void setZip(BmField zip) {
		this.zip = zip;
	}

	public BmField getCityId() {
		return cityId;
	}

	public void setCityId(BmField cityId) {
		this.cityId = cityId;
	}

	public BmField getBetweenStreets() {
		return betweenStreets;
	}

	public void setBetweenStreets(BmField betweenStreets) {
		this.betweenStreets = betweenStreets;
	}

	public BmField getContract() {
		return contract;
	}

	public void setContract(BmField contract) {
		this.contract = contract;
	}

	public BmField getPropertyReceipt() {
		return propertyReceipt;
	}

	public void setPropertyReceipt(BmField propertyReceipt) {
		this.propertyReceipt = propertyReceipt;
	}

	public BmField getPropertyTitle() {
		return propertyTitle;
	}

	public void setPropertyTitle(BmField propertyTitle) {
		this.propertyTitle = propertyTitle;
	}

	public BmField getConstitutiveAct() {
		return constitutiveAct;
	}

	public void setConstitutiveAct(BmField constitutiveAct) {
		this.constitutiveAct = constitutiveAct;
	}

	public BmField getRenewOrder() {
		return renewOrder;
	}

	public void setRenewOrder(BmField renewOrder) {
		this.renewOrder = renewOrder;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getCertifiedWriting() {
		return certifiedWriting;
	}

	public void setCertifiedWriting(BmField certifiedWriting) {
		this.certifiedWriting = certifiedWriting;
	}

	public BmField getDemarcation() {
		return demarcation;
	}

	public void setDemarcation(BmField demarcation) {
		this.demarcation = demarcation;
	}

	public BmField getNotaryQuotation() {
		return notaryQuotation;
	}

	public void setNotaryQuotation(BmField notaryQuotation) {
		this.notaryQuotation = notaryQuotation;
	}

	public BmField getAppraise() {
		return appraise;
	}

	public void setAppraise(BmField appraise) {
		this.appraise = appraise;
	}

	public BmField getDebtCertificate() {
		return debtCertificate;
	}

	public void setDebtCertificate(BmField debtCertificate) {
		this.debtCertificate = debtCertificate;
	}

	public BmField getTaxCertificate() {
		return taxCertificate;
	}

	public void setTaxCertificate(BmField taxCertificate) {
		this.taxCertificate = taxCertificate;
	}

	public BmField getWaterBill() {
		return waterBill;
	}

	public void setWaterBill(BmField waterBill) {
		this.waterBill = waterBill;
	}

	public BmField getElectricityBill() {
		return electricityBill;
	}

	public void setElectricityBill(BmField electricityBill) {
		this.electricityBill = electricityBill;
	}

	public BmField getOtherDocuments() {
		return otherDocuments;
	}

	public void setOtherDocuments(BmField otherDocuments) {
		this.otherDocuments = otherDocuments;
	}

	public BmField getTags() {
		return tags;
	}

	public void setTags(BmField tags) {
		this.tags = tags;
	}
	
}
