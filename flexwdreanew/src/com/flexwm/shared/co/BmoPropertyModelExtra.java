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

public class BmoPropertyModelExtra extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, price, fixedPrice, startDate, endDate, propertyModelId, file;

	BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();

	public BmoPropertyModelExtra(){
		super("com.flexwm.server.co.PmPropertyModelExtra", "propertymodelextras", "propertymodelextraid", "PRMX", "Extras Modelos");

		code = setField("code", "", "Clave Extra.", 10, Types.VARCHAR, true, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		price = setField("price", "", "Precio ", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		fixedPrice = setField("fixedprice", "", "Precio Fijo", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);

		startDate = setField("startdate", "", "Fecha Inicio", 20, Types.DATE, false, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Fecha Fin", 20, Types.DATE, false, BmFieldType.DATE, false);
		propertyModelId = setField("propertymodelid", "", "Modelo", 8, Types.INTEGER, true, BmFieldType.ID, false);

		//Archivo adjunto de autorización del un extra
		file = setField("file", "", "Documento", 512, Types.VARCHAR, true, BmFieldType.IMAGE, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getBmoPropertyModel().getBmoDevelopment().getCode(),
				getBmoPropertyModel().getCode(),
				getStartDate(),
				getEndDate(),
				getFixedPrice(),
				getFile(),
				getPrice()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getPrice() {
		return price;
	}

	public void setPrice(BmField price) {
		this.price = price;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getEndDate() {
		return endDate;
	}

	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
	}

	public BmField getPropertyModelId() {
		return propertyModelId;
	}

	public void setPropertyModelId(BmField propertyModelId) {
		this.propertyModelId = propertyModelId;
	}

	public BmoPropertyModel getBmoPropertyModel() {
		return bmoPropertyModel;
	}

	public void setBmoPropertyModel(BmoPropertyModel bmoPropertyModel) {
		this.bmoPropertyModel = bmoPropertyModel;
	}

	public BmField getFixedPrice() {
		return fixedPrice;
	}

	public void setFixedPrice(BmField fixedPrice) {
		this.fixedPrice = fixedPrice;
	}

	public BmField getFile() {
		return file;
	}

	public void setFile(BmField file) {
		this.file = file;
	}

}
