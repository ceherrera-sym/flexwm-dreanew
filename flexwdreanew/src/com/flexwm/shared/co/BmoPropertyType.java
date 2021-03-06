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
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author smuniz
 *
 */

public class BmoPropertyType extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code, name, description,orderTypeId,copyTag;
	public static String CODE_PREFIX = "DT-";
	private BmoOrderType bmoOrderType = new BmoOrderType();

	public BmoPropertyType(){
		super("com.flexwm.server.co.PmPropertyType", "propertytypes", "propertytypeid", "PTYP", "Tipos de Inmuebles");

		//Campo de Datos		
		code = setField("code", "", "Clave Tipo I.", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 8, Types.INTEGER, false, BmFieldType.ID, false);
		copyTag = setField("copytags", "0", "Copiar Tags?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),				
				getDescription()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()), 
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

	public BmField getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(BmField orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public BmoOrderType getBmoOrderType() {
		return bmoOrderType;
	}

	public void setBmoOrderType(BmoOrderType bmoOrderType) {
		this.bmoOrderType = bmoOrderType;
	}

	public BmField getCopyTag() {
		return copyTag;
	}

	public void setCopyTag(BmField copyTag) {
		this.copyTag = copyTag;
	}
	
	
	
	
}
