package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoDelegation extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField code, name, description, cityId;

	public BmoDelegation() {
		super("com.flexwm.server.cm.PmDelegation", "delegations", "delegationid", "DELE","Delegaciones");

		code = setField("code", "", "Clave Delegación", 10, Types.VARCHAR, false, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre Delegación", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);		
		cityId = setField("cityid", "", "Ciudad", 10, Types.INTEGER, true, BmFieldType.ID, false);
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
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName().getName(), getName().getLabel()), 
				new BmSearchField(getDescription().getName(), getDescription().getLabel())));
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

	public BmField getCityId() {
		return cityId;
	}

	public void setCityId(BmField cityId) {
		this.cityId = cityId;
	}
}
