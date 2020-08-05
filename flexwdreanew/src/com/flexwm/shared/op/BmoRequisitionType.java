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


public class BmoRequisitionType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, type, createReceipt, stock, enableSend, viewFormat, createPaccount, devolutionBankmovTypeId, paymentBankmovTypeId;

	// Tipo
	public static final char TYPE_PURCHASE = 'P';
	public static final char TYPE_RENTAL = 'R';
	public static final char TYPE_SERVICE = 'S';
	public static final char TYPE_TRAVELEXPENSE = 'T';
	public static final char TYPE_COMMISION = 'C';
	public static final char TYPE_CONTRACTESTIMATION = 'E';
	public static final char TYPE_CREDIT = 'D';
	public static final char TYPE_PAYSHEET = 'Y';

	public BmoRequisitionType() {
		super("com.flexwm.server.op.PmRequisitionType","requisitiontypes", "requisitiontypeid", "RQTP", "Tipos de Orden Compra");

		//Campo de Datos
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		createReceipt = setField("createreceipt", "", "Crear Recibo OC ?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		createPaccount = setField("createpaccount", "", "Crear CXP ?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		viewFormat = setField("viewformat", "", "Visualizar Formato Sin Aut.", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		stock = setField("stock", "", "A. Almacén", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);

		type = setField("type", "", "Tipo OC", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_PURCHASE, "Compra", "./icons/rqtp_type_purchase.png"),
				new BmFieldOption(TYPE_RENTAL, "Renta", "./icons/rqtp_type_rental.png"),
				new BmFieldOption(TYPE_SERVICE, "Servicio", "./icons/rqtp_type_service.png"),
				new BmFieldOption(TYPE_TRAVELEXPENSE, "Viáticos", "./icons/rqtp_type_travelexpense.png"),
				new BmFieldOption(TYPE_COMMISION, "Comisiones", "./icons/rqtp_type_commision.png"),
				new BmFieldOption(TYPE_CONTRACTESTIMATION, "Estimaciones", "./icons/rqtp_type_estimation.png"),
				new BmFieldOption(TYPE_CREDIT, "Créditos", "./icons/rqtp_type_credit.png"),
				new BmFieldOption(TYPE_PAYSHEET, "Nóminas", "./icons/rqtp_type_paysheet.png")
				)));
		enableSend = setField("enablesend", "false", "Enviar Correo", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		paymentBankmovTypeId = setField("paymentbankmovtypeid", "", "MB tipo Pago Prov.", 8, Types.INTEGER, true, BmFieldType.ID, false);
		devolutionBankmovTypeId = setField("devolutionbankmovtypeid", "", "MB tipo Devolución", 8, Types.INTEGER, true, BmFieldType.ID, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),				
				getDescription(),
				getCreateReceipt(),
				getCreatePaccount(),
				getStock(),
				getType(),
				getEnableSend()
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

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getCreateReceipt() {
		return createReceipt;
	}

	public void setCreateReceipt(BmField createReceipt) {
		this.createReceipt = createReceipt;
	}

	public BmField getStock() {
		return stock;
	}

	public void setStock(BmField stock) {
		this.stock = stock;
	}

	public BmField getEnableSend() {
		return enableSend;
	}

	public void setEnableSend(BmField enableSend) {
		this.enableSend = enableSend;
	}

	public BmField getCreatePaccount() {
		return createPaccount;
	}

	public void setCreatePaccount(BmField createPaccount) {
		this.createPaccount = createPaccount;
	}

	public BmField getDevolutionBankmovTypeId() {
		return devolutionBankmovTypeId;
	}

	public void setDevolutionBankmovTypeId(BmField devolutionBankmovTypeId) {
		this.devolutionBankmovTypeId = devolutionBankmovTypeId;
	}

	public BmField getPaymentBankmovTypeId() {
		return paymentBankmovTypeId;
	}

	public void setPaymentBankmovTypeId(BmField paymentBankmovTypeId) {
		this.paymentBankmovTypeId = paymentBankmovTypeId;
	}

	public BmField getViewFormat() {
		return viewFormat;
	}

	public void setViewFormat(BmField viewFormat) {
		this.viewFormat = viewFormat;
	}
	

}
