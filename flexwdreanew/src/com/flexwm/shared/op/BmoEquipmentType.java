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
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoEquipmentType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name,description;

	public BmoEquipmentType() {
		super("com.flexwm.server.op.PmEquipmentType","equipmenttypes", "equipmenttypeid", "EQTY","Tipos de Recursos");
		//Campo de Datos
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
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
				new BmSearchField(getName()),
				new BmSearchField(getDescription())
				));
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
