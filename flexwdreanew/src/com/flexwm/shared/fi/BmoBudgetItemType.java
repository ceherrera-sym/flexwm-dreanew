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

/**
 * @author jhernandez
 *
 */
public class BmoBudgetItemType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField  name, description, type, isGroup, parentId;
	
	public static String CODE_PREFIX = "TP-";
	
	public static final char TYPE_DEPOSIT = 'D';
	public static final char TYPE_WITHDRAW = 'W';	
	
	public BmoBudgetItemType() {
		super("com.flexwm.server.fi.PmBudgetItemType", "budgetitemtypes", "budgetitemtypeid", "BGTY", "Tipos Partidas Presup.");
		 
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);	
		
		type = setField("type", "" + TYPE_WITHDRAW, "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_WITHDRAW, "Cargo", "./icons/type_withdraw.png"),
				new BmFieldOption(TYPE_DEPOSIT, "Abono", "./icons/type_deposit.png")
				)));
		
		isGroup = setField("isgroup", "", "Es Grupo?", 11, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		parentId = setField("parentid", "", "Tipo Part. Padre", 4, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(				
				getName(),
				getDescription(),
				getType(),
				getIsGroup()
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
	
	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
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

	public BmField getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(BmField isGroup) {
		this.isGroup = isGroup;
	}

	public BmField getParentId() {
		return parentId;
	}

	public void setParentId(BmField parentId) {
		this.parentId = parentId;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}
}
