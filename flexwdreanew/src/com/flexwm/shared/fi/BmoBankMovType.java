/**
 * 
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

public class BmoBankMovType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, type, category, paccountTypeId, raccountTypeId, bankMovTypeChildId, visible;
	private BmoPaccountType bmoPaccountType;
	private BmoRaccountType bmoRaccountType;

	public static final char TYPE_WITHDRAW = 'W';
	public static final char TYPE_DEPOSIT = 'D';

	public static final char VISIBLE_TRUE = 'T';
	public static final char VISIBLE_FALSE = 'F';	

	public static final char CATEGORY_CXC = 'C';
	public static final char CATEGORY_CXP = 'P';
	public static final char CATEGORY_REQUISITIONADVANCE = 'A';	
	public static final char CATEGORY_DEPOSITFREE = 'O';
	public static final char CATEGORY_TRANSFER = 'T';
	public static final char CATEGORY_DISPOSALFREE = 'D';
	public static final char CATEGORY_DEVOLUTIONCXC = 'E';
	public static final char CATEGORY_MULTIPLECXC = 'M';
	public static final char CATEGORY_LOANDISPOSAL = 'L';
	public static final char CATEGORY_CREDITDISPOSAL = 'I';


	public BmoBankMovType() {
		super("com.flexwm.server.fi.PmBankMovType","bankmovtypes", "bankmovtypeid", "BKMT","Tipos de Movimientos");

		//Campo de Datos		
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 50, Types.VARCHAR, true, BmFieldType.STRING, false);		 
		visible = setField("visible", "" + VISIBLE_TRUE, "Visible en Bancos", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		visible.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(VISIBLE_TRUE, "Si", "./icons/boolean_true.png"),
				new BmFieldOption(VISIBLE_FALSE, "No", "./icons/boolean_false.png")				
				)));
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_WITHDRAW, "Cargo", "./icons/type_withdraw.png"),
				new BmFieldOption(TYPE_DEPOSIT, "Abono", "./icons/type_deposit.png")
				)));
		category = setField("category", "", "Categoría", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		category.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(CATEGORY_CXC, "Cuentas x Cobrar", "./icons/bkmt_category_cxc.png"),
				new BmFieldOption(CATEGORY_CXP, "Cuentas x Pagar", "./icons/bkmt_category_cxp.png"),
				new BmFieldOption(CATEGORY_REQUISITIONADVANCE, "Anticipo Proveedor", "./icons/bkmt_category_supl.png"),				
				new BmFieldOption(CATEGORY_TRANSFER, "Traspaso", "./icons/bkmt_category_transfer.png"),
				new BmFieldOption(CATEGORY_DEVOLUTIONCXC, "Devoluciones CxC", "./icons/bkmt_category_devcxc.png"),
				new BmFieldOption(CATEGORY_DISPOSALFREE, "Disposiciones", "./icons/bkmt_category_disposal.png"),
				new BmFieldOption(CATEGORY_DEPOSITFREE, "Deposito Libre", "./icons/bkmt_category_depositfree.png"),
				new BmFieldOption(CATEGORY_MULTIPLECXC, "Multiple CxC", "./icons/bkmt_category_mcxc.png"),
				new BmFieldOption(CATEGORY_LOANDISPOSAL, "Disposicion Credito", "./icons/bkmt_category_loandisposal.png"),
				new BmFieldOption(CATEGORY_CREDITDISPOSAL, "Entrega Credito", "./icons/bkmt_category_creditdisposal.png")
				)));
		paccountTypeId = setField("paccounttypeid", "", "Crea CxP", 4, Types.INTEGER, true, BmFieldType.CHAR, false);
		bmoPaccountType = new BmoPaccountType();
		raccountTypeId = setField("raccounttypeid", "", "Crea CxC", 4, Types.INTEGER, true, BmFieldType.CHAR, false);
		bmoRaccountType = new BmoRaccountType();

		bankMovTypeChildId = setField("bankmovtypechildid", "", "Crea Mov Bancario", 4, Types.INTEGER, true, BmFieldType.CHAR, false);

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

	/**
	 * @return the category
	 */
	public BmField getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(BmField category) {
		this.category = category;
	}

	/**
	 * @return the paccountTypeId
	 */
	public BmField getPaccountTypeId() {
		return paccountTypeId;
	}

	/**
	 * @param paccountTypeId the paccountTypeId to set
	 */
	public void setPaccountTypeId(BmField paccountTypeId) {
		this.paccountTypeId = paccountTypeId;
	}


	/**
	 * @return the bmoPaccountType
	 */
	public BmoPaccountType getBmoPaccountType() {
		return bmoPaccountType;
	}

	/**
	 * @param bmoPaccountType the bmoPaccountType to set
	 */
	public void setBmoPaccountType(BmoPaccountType bmoPaccountType) {
		this.bmoPaccountType = bmoPaccountType;
	}

	/**
	 * @return the raccountTypeId
	 */
	public BmField getRaccountTypeId() {
		return raccountTypeId;
	}

	/**
	 * @param raccountTypeId the raccountTypeId to set
	 */
	public void setRaccountTypeId(BmField raccountTypeId) {
		this.raccountTypeId = raccountTypeId;
	}

	/**
	 * @return the bmoRaccountType
	 */
	public BmoRaccountType getBmoRaccountType() {
		return bmoRaccountType;
	}

	/**
	 * @param bmoRaccountType the bmoRaccountType to set
	 */
	public void setBmoRaccountType(BmoRaccountType bmoRaccountType) {
		this.bmoRaccountType = bmoRaccountType;
	}

	/**
	 * @return the bankMovTypeChildId
	 */
	public BmField getBankMovTypeChildId() {
		return bankMovTypeChildId;
	}

	/**
	 * @param bankMovTypeChildId the bankMovTypeChildId to set
	 */
	public void setBankMovTypeChildId(BmField bankMovTypeChildId) {
		this.bankMovTypeChildId = bankMovTypeChildId;
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
}
