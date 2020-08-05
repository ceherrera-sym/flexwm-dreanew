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
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoOrderTypeWFlowCategory extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField orderTypeId, wFlowCategoryId;
	private BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();

	public BmoOrderTypeWFlowCategory() {
		super("com.flexwm.server.op.PmOrderTypeWFlowCategory", "ordertypewflowcategories", "ordertypewflowcategoryid", "ORTW", "Cat. Tipos WFlow");

		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 20, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowCategoryId = setField("wflowcategoryid", "", "Categoría WFlow", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWFlowCategory().getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoWFlowCategory().getName())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoWFlowCategory().getName(), BmOrder.ASC)));
	}

	public BmField getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(BmField orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public BmField getWFlowCategoryId() {
		return wFlowCategoryId;
	}

	public void setWFlowCategoryId(BmField wFlowCategoryId) {
		this.wFlowCategoryId = wFlowCategoryId;
	}

	public BmoWFlowCategory getBmoWFlowCategory() {
		return bmoWFlowCategory;
	}

	public void setBmoWFlowCategory(BmoWFlowCategory bmoWFlowCategory) {
		this.bmoWFlowCategory = bmoWFlowCategory;
	}
}
