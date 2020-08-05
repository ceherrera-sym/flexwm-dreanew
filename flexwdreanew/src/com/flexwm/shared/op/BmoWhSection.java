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


public class BmoWhSection extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, warehouseId, orderId;

	BmoWarehouse bmoWarehouse = new BmoWarehouse();

	public BmoWhSection() {
		super("com.flexwm.server.op.PmWhSection","whsections", "whsectionid", "WHSE","Secciones de Almacén");

		name = setField("name", "", "Nombre Sección", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, 0, true, BmFieldType.STRING, false);
		warehouseId = setField("warehouseid", "", "Almacén", 8, Types.INTEGER, false, BmFieldType.ID, false);	
		orderId = setField("orderid", "", "Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);	
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getBmoWarehouse().getName(),
				getBmoWarehouse().getType()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWarehouse().getName(),
				getName()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getBmoWarehouse().getName()
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
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getName(), BmOrder.ASC)
				));
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

	public BmField getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(BmField warehouseId) {
		this.warehouseId = warehouseId;
	}

	public BmoWarehouse getBmoWarehouse() {
		return bmoWarehouse;
	}

	public void setBmoWarehouse(BmoWarehouse bmoWarehouse) {
		this.bmoWarehouse = bmoWarehouse;
	}

	public BmField getOrderId() {
		return orderId;
	}

	public void setOrderId(BmField orderId) {
		this.orderId = orderId;
	}
}
