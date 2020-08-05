package com.flexwm.shared.op;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


/**
 * @author jhernandez
 *
 */

public class BmoProductGroup extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name,description;

	public BmoProductGroup() {
		super("com.flexwm.server.op.PmProductGroup","productgroups", "productgroupid", "PRGP","Grupo de Productos");

		//Campo de Datos
		name = setField("name", "", "Nombre Grupo", 30, 0, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, 0, true, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
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

}
