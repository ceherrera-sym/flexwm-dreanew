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

public class BmoPropertyModel extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField developmentId, code, name, description, propertyTypeId, rooms, dorms, landsize, constructionsize, highlights, details, 
	finishing, other, publiclandsize, monthlygoal, blueprint, image, garage, roofGarden, bonusRG;

	private BmoDevelopment bmoDevelopment = new BmoDevelopment();
	private BmoPropertyType bmoPropertyType = new BmoPropertyType();

	public static String CODE_PREFIX = "PM-";


	public BmoPropertyModel(){
		super("com.flexwm.server.co.PmPropertyModel", "propertymodels", "propertymodelid", "PTYM", "Modelos");

		//Campo de Datos	
		developmentId = setField("developmentid", "", "Desarrollo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		code = setField("code", "", "Clave Modelo", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		propertyTypeId = setField("propertytypeid", "", "Tipo Inmueble", 8, Types.INTEGER, false, BmFieldType.ID, false);
		rooms = setField("rooms", "", "Espacios Habitables", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
		dorms = setField("dorms", "", "Dormitorios", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
		landsize = setField("landsize", "", "M2 Terr.", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		constructionsize = setField("constructionsize", "", "M2 Const.", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		highlights = setField("highlights", "", "Resumen", 1500, Types.VARCHAR, true, BmFieldType.STRING, false);
		details = setField("details", "", "Características", 1500, Types.VARCHAR, true, BmFieldType.STRING, false);
		finishing = setField("finishing", "", "Acabados", 1500, Types.VARCHAR, true, BmFieldType.STRING, false);
		other = setField("other", "", "Varios", 1500, Types.VARCHAR, true, BmFieldType.STRING, false);
		publiclandsize = setField("publiclandsize", "", "M2 T. Comun", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		monthlygoal = setField("monthlygoal", "", "Meta Mensual", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);

		blueprint = setField("blueprint", "", "Plano", 255, Types.VARCHAR, true, BmFieldType.FILEUPLOAD, false);
		image = setField("image", "", "Fachada", 255, Types.VARCHAR, true, BmFieldType.FILEUPLOAD, false);
		garage = setField("garage", "", "Ampliación Cochera", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		roofGarden = setField("roofgarden", "", "Ampliación Roof Garden", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		bonusRG = setField("bonusrg", "", "Bono RG", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),				
				bmoDevelopment.getCode(),
				getBlueprint(),
				getImage(),
				getLandSize(),
				getConstructionSize(),
				getRooms(),
				getDorms()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}


	/**
	 * @return the developmentid
	 */
	public BmField getDevelopmentId() {
		return developmentId;
	}


	/**
	 * @param developmentid the developmentid to set
	 */
	public void setDevelopmentId(BmField developmentId) {
		this.developmentId = developmentId;
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
	 * @return the rooms
	 */
	public BmField getRooms() {
		return rooms;
	}


	/**
	 * @param rooms the rooms to set
	 */
	public void setRooms(BmField rooms) {
		this.rooms = rooms;
	}


	/**
	 * @return the dorms
	 */
	public BmField getDorms() {
		return dorms;
	}


	/**
	 * @param dorms the dorms to set
	 */
	public void setDorms(BmField dorms) {
		this.dorms = dorms;
	}


	/**
	 * @return the landsize
	 */
	public BmField getLandSize() {
		return landsize;
	}


	/**
	 * @param landsize the landsize to set
	 */
	public void setLandSize(BmField landsize) {
		this.landsize = landsize;
	}


	/**
	 * @return the constructionsize
	 */
	public BmField getConstructionSize() {
		return constructionsize;
	}


	/**
	 * @param constructionsize the constructionsize to set
	 */
	public void setConstructionSize(BmField constructionsize) {
		this.constructionsize = constructionsize;
	}


	/**
	 * @return the highlights
	 */
	public BmField getHighLights() {
		return highlights;
	}


	/**
	 * @param highlights the highlights to set
	 */
	public void setHighLights(BmField highlights) {
		this.highlights = highlights;
	}


	/**
	 * @return the details
	 */
	public BmField getDetails() {
		return details;
	}


	/**
	 * @param details the details to set
	 */
	public void setDetails(BmField details) {
		this.details = details;
	}


	/**
	 * @return the finishing
	 */
	public BmField getFinishing() {
		return finishing;
	}


	/**
	 * @param finishing the finishing to set
	 */
	public void setFinishing(BmField finishing) {
		this.finishing = finishing;
	}


	/**
	 * @return the other
	 */
	public BmField getOther() {
		return other;
	}


	/**
	 * @param other the other to set
	 */
	public void setOther(BmField other) {
		this.other = other;
	}

	/**
	 * @return the publiclandsize
	 */
	public BmField getPublicLandSize() {
		return publiclandsize;
	}


	/**
	 * @param publiclandsize the publiclandsize to set
	 */
	public void setPublicLandSize(BmField publiclandsize) {
		this.publiclandsize = publiclandsize;
	}


	/**
	 * @return the monthlygoal
	 */
	public BmField getMonthlyGoal() {
		return monthlygoal;
	}


	/**
	 * @param monthlygoal the monthlygoal to set
	 */
	public void setMonthlyGoal(BmField monthlygoal) {
		this.monthlygoal = monthlygoal;
	}


	/**
	 * @return the bmoDevelopment
	 */
	public BmoDevelopment getBmoDevelopment() {
		return bmoDevelopment;
	}


	/**
	 * @param bmoDevelopment the bmoDevelopment to set
	 */
	public void setBmoDevelopment(BmoDevelopment bmoDevelopment) {
		this.bmoDevelopment = bmoDevelopment;
	}


	public BmField getBlueprint() {
		return blueprint;
	}


	public void setBlueprint(BmField blueprint) {
		this.blueprint = blueprint;
	}


	public BmField getPropertyTypeId() {
		return propertyTypeId;
	}


	public void setPropertyTypeId(BmField propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}


	/**
	 * @return the bmoPropertyType
	 */
	public BmoPropertyType getBmoPropertyType() {
		return bmoPropertyType;
	}


	/**
	 * @param bmoPropertyType the bmoPropertyType to set
	 */
	public void setBmoPropertyType(BmoPropertyType bmoPropertyType) {
		this.bmoPropertyType = bmoPropertyType;
	}


	/**
	 * @return the image
	 */
	public BmField getImage() {
		return image;
	}


	/**
	 * @param image the image to set
	 */
	public void setImage(BmField image) {
		this.image = image;
	}

	/**
	 * @return the garage
	 */
	public BmField getGarage() {
		return garage;
	}

	/**
	 * @return the roofGarden
	 */
	public BmField getRoofGarden() {
		return roofGarden;
	}

	/**
	 * @param garage the garage to set
	 */
	public void setGarage(BmField garage) {
		this.garage = garage;
	}

	/**
	 * @param roofGarden the roofGarden to set
	 */
	public void setRoofGarden(BmField roofGarden) {
		this.roofGarden = roofGarden;
	}

	/**
	 * @return the bonusRG
	 */
	public BmField getBonusRG() {
		return bonusRG;
	}

	/**
	 * @param bonusRG the bonusRG to set
	 */
	public void setBonusRG(BmField bonusRG) {
		this.bonusRG = bonusRG;
	}
}
