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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;


public class BmoWhMovement extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, description, type, amount, status, toWhSectionId, companyId,
	datemov, autoCreateItems, requisitionReceiptId, orderDeliveryId;

	private BmoWhSection bmoToWhSection = new BmoWhSection();
	private BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
	private BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
	private BmoCompany bmoCompany = new BmoCompany();

	public static final char TYPE_IN = 'I';
	public static final char TYPE_IN_ADJUST = 'A';
	public static final char TYPE_IN_DEV = 'D';
	public static final char TYPE_OUT = 'O';
	public static final char TYPE_OUT_DEV = 'V';
	public static final char TYPE_OUT_ADJUST = 'J';
	public static final char TYPE_TRANSFER = 'T';
	public static final char TYPE_RENTAL_OUT = 'R';
	public static final char TYPE_RENTAL_IN = 'N';

	public static char STATUS_REVISION = 'R';
	public static char STATUS_AUTHORIZED = 'A';

	public static String ACCESS_ADJUST = "WHMADJ";
	public static final String ACCESS_VIEWAMOUNT= "ODYVAMOUNT";
	public static String ACTION_WHBOX = "WHMBOX";

	public BmoWhMovement() {
		super("com.flexwm.server.op.PmWhMovement", "whmovements", "whmovementid", "WHMV", "Tx Inventario");

		//Campo de Datos
		code = setField("code", "", "Clave Tx.", 10, 0, true, BmFieldType.CODE, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);		
		amount = setField("amount", "", "Monto", 30, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
		status = setField("status", "" + STATUS_REVISION, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_REVISION, "En Revisión", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_AUTHORIZED, "Autorizada", "./icons/status_authorized.png")
				)));
		datemov = setField("datemov", "", "Fecha/Hora", 30, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		autoCreateItems = setField("autocreateitems", "", "Items Auto.", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		type = setField("type", "", "Tipo Tx.", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_IN, "Recibo O. C.", "./icons/whmv_type_in.png"),
				new BmFieldOption(TYPE_IN_DEV, "Devolución Rec. O.C.", "./icons/whmv_type_in_dev.png"),
				new BmFieldOption(TYPE_IN_ADJUST, "Entrada Ajuste", "./icons/whmv_type_in_adjust.png"),
				new BmFieldOption(TYPE_OUT, "Salida Venta", "./icons/whmv_type_out.png"),
				new BmFieldOption(TYPE_OUT_ADJUST, "Salida Ajuste", "./icons/whmv_type_out_adjust.png"),
				new BmFieldOption(TYPE_OUT_DEV, "Devolución Salida", "./icons/whmv_type_out_dev.png"),
				new BmFieldOption(TYPE_TRANSFER, "Transferencia", "./icons/whmv_type_transfer.png"),
				new BmFieldOption(TYPE_RENTAL_OUT, "Salida Renta", "./icons/whmv_type_rental_out.png"),
				new BmFieldOption(TYPE_RENTAL_IN, "Entrada Renta", "./icons/whmv_type_rental_in.png")
				)));

		toWhSectionId = setField("towhsectionid", "", "A Sección A.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		requisitionReceiptId = setField("requisitionreceiptid", "", "Rec. O. C.", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		orderDeliveryId = setField("orderdeliveryid", "", "Envío Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);	
		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getDatemov(),
				getType(),
				getBmoToWhSection().getBmoWarehouse().getName(),
				getBmoToWhSection().getName(),
				getBmoRequisitionReceipt().getCode(),
				getBmoOrderDelivery().getCode(),
				getStatus()
				//getAmount()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getDatemov(),
				getType()
				));
	}
	
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getDatemov(),
				getType()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()),	
				new BmSearchField(getBmoToWhSection().getBmoWarehouse().getName()),
				new BmSearchField(getBmoToWhSection().getName()),
				new BmSearchField(getBmoOrderDelivery().getCode()),
				new BmSearchField(getBmoRequisitionReceipt().getCode()),
				new BmSearchField(getDescription()),
				new BmSearchField(getAmount())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.DESC)
				));
	}

	public String getCodeFormat() {
		if (getId() > 0) {
			return "TX-" + getId();
		}
		return "";
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getDatemov() {
		return datemov;
	}

	public void setDatemov(BmField datemov) {
		this.datemov = datemov;
	}

	public BmoWhSection getBmoToWhSection() {
		return bmoToWhSection;
	}

	public void setBmoToWhSection(BmoWhSection bmoToWhSection) {
		this.bmoToWhSection = bmoToWhSection;
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getToWhSectionId() {
		return toWhSectionId;
	}

	public void setToWhSectionId(BmField toWhSectionId) {
		this.toWhSectionId = toWhSectionId;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getAutoCreateItems() {
		return autoCreateItems;
	}

	public void setAutoCreateItems(BmField autoCreateItems) {
		this.autoCreateItems = autoCreateItems;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getRequisitionReceiptId() {
		return requisitionReceiptId;
	}

	public void setRequisitionReceiptId(BmField requisitionReceiptId) {
		this.requisitionReceiptId = requisitionReceiptId;
	}

	public BmoRequisitionReceipt getBmoRequisitionReceipt() {
		return bmoRequisitionReceipt;
	}

	public void setBmoRequisitionReceipt(BmoRequisitionReceipt bmoRequisitionReceipt) {
		this.bmoRequisitionReceipt = bmoRequisitionReceipt;
	}

	public BmField getOrderDeliveryId() {
		return orderDeliveryId;
	}

	public void setOrderDeliveryId(BmField orderDeliveryId) {
		this.orderDeliveryId = orderDeliveryId;
	}

	public BmoOrderDelivery getBmoOrderDelivery() {
		return bmoOrderDelivery;
	}

	public void setBmoOrderDelivery(BmoOrderDelivery bmoOrderDelivery) {
		this.bmoOrderDelivery = bmoOrderDelivery;
	}

	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

}
