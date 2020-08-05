/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.fi;

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


public class BmoRaccountType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField type, name, description, category, visible, folioZeros;

	public static final char TYPE_WITHDRAW = 'W';
	public static final char TYPE_DEPOSIT = 'D';

	public static final char VISIBLE_TRUE = 'T';
	public static final char VISIBLE_FALSE = 'F';

	public static final char CATEGORY_ORDER = 'O';
	public static final char CATEGORY_OTHER = 'T';
	public static final char CATEGORY_CREDITNOTE = 'C';


	public BmoRaccountType() {
		super("com.flexwm.server.fi.PmRaccountType","raccounttypes", "raccounttypeid", "RACT","Tipos Cuentas x Cobrar");

		//Campo de Datos
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_WITHDRAW, "Cargo", "./icons/type_withdraw.png"),
				new BmFieldOption(TYPE_DEPOSIT, "Abono", "./icons/type_deposit.png")
				)));

		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		category = setField("category", "", "Categoría", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);		
		category.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(CATEGORY_ORDER, "Pedido", "./icons/ract_category_order.png"),
				//new BmFieldOption(CATEGORY_CREDITDEVOLUTION, "Devolucion", "./icons/ract_category_order.png"),
				new BmFieldOption(CATEGORY_CREDITNOTE, "Nota Credito", "./icons/ract_category_creditnote.png"),
				new BmFieldOption(CATEGORY_OTHER, "Otros", "./icons/ract_category_other.png")
				)));
		visible = setField("visible", "" + VISIBLE_TRUE, "Visible CXC", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		visible.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(VISIBLE_TRUE, "Si", "./icons/boolean_true.png"),
				new BmFieldOption(VISIBLE_FALSE, "No", "./icons/boolean_false.png")				
				)));
		
		folioZeros = setField("foliozeros", "", "Ceros en Folio", 2, Types.INTEGER, true, BmFieldType.NUMBER, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getType(),
				getCategory(),
				getVisible()
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

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
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

	public BmField getCategory() {
		return category;
	}

	public void setCategory(BmField category) {
		this.category = category;
	}

	/**
	 * @return the visible
	 */
	public BmField getVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(BmField visible) {
		this.visible = visible;
	}

	public BmField getFolioZeros() {
		return folioZeros;
	}

	public void setFolioZeros(BmField folioZeros) {
		this.folioZeros = folioZeros;
	}

}
