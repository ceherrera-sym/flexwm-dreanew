/**
 * 
 */

package com.flexwm.shared.co;

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

public class BmoWorkType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, type;

	public static final char TYPE_CONSTRUCTION = 'C';
	public static final char TYPE_URBANIZATION = 'U';
	public static final char TYPE_OTHER = 'O';
	public static final char TYPE_COMP_CONSTRUCTION = 'N';
	public static final char TYPE_COMP_URBANIZATION = 'R';
	public static final char TYPE_PLATFORMS = 'P';
	public static final char TYPE_HEADERS = 'H';
	public static final char TYPE_COMP_BUILDING = 'B';

	public BmoWorkType() {
		super("com.flexwm.server.co.PmWorkType", "worktypes", "worktypeid", "WKTY", "Tipo Obra");

		// Campo de datos
		name = setField("name", "", "Nombre", 30, 0, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, 0, true, BmFieldType.STRING, false);

		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_CONSTRUCTION, "Construcción / Edificación", "./icons/wkty_type_construction.png"),
				new BmFieldOption(TYPE_URBANIZATION, "Urbanización", "./icons/wkty_type_urbanization.png"),
				new BmFieldOption(TYPE_OTHER, "Otros", "./icons/wkty_type_other.png"),
				new BmFieldOption(TYPE_COMP_CONSTRUCTION, "Complementaria Construcción / Edificación", "./icons/wkty_type_comp_construction.png"),
				new BmFieldOption(TYPE_COMP_URBANIZATION, "Complementaria Urbanización", "./icons/wkty_type_comp_urbanization.png"),
				new BmFieldOption(TYPE_PLATFORMS, "Plataformas", "./icons/wkty_type_platforms.png"),
				new BmFieldOption(TYPE_HEADERS, "Obra de Cabecera", "./icons/wkty_type_headers.png"),
				new BmFieldOption(TYPE_COMP_BUILDING, "Obra Complementaria Edificación", "./icons/wkty_type_comp_building.png")
				)));
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(), 
				getDescription(),
				getType()
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
}
