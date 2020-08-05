/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.co;

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmOrderType;
import com.flexwm.shared.co.BmoOrderProperty;
import com.flexwm.shared.co.BmoOrderPropertyModelExtra;
import com.flexwm.shared.co.BmoOrderPropertyTax;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelExtra;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;


public class PmOrderPropertyModelExtra extends PmObject {
	BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra;

	public PmOrderPropertyModelExtra(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
		setBmObject(bmoOrderPropertyModelExtra);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderPropertyModelExtra.getPropertyModelExtraId(), bmoOrderPropertyModelExtra.getBmoPropertyModelExtra()),
				new PmJoin(bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getPropertyModelId(), bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel()),
				new PmJoin(bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getDevelopmentId(), bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getBmoDevelopment()),
				new PmJoin(bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getPropertyTypeId(), bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getBmoPropertyType()),
				new PmJoin(bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getBmoPropertyType().getOrderTypeId(), bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getBmoPropertyType().getBmoOrderType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)autoPopulate(pmConn, new BmoOrderPropertyModelExtra());

		// BmoPropertyModelExtra
		BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();
		if ((int)pmConn.getInt(bmoPropertyModelExtra.getIdFieldName()) > 0) 
			bmoOrderPropertyModelExtra.setBmoPropertyModelExtra((BmoPropertyModelExtra) new PmPropertyModelExtra(getSFParams()).populate(pmConn));
		else 
			bmoOrderPropertyModelExtra.setBmoPropertyModelExtra(bmoPropertyModelExtra);

		return bmoOrderPropertyModelExtra;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderPropertyModelExtra.getOrderId().toInteger());

		PmPropertyModelExtra pmPropertyModelExtra = new PmPropertyModelExtra(getSFParams());
		BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();

		// Obten el registro del recurso y su precio vigente
		if (bmoOrderPropertyModelExtra.getPropertyModelExtraId().toInteger() > 0) {
			bmoPropertyModelExtra = (BmoPropertyModelExtra)pmPropertyModelExtra.get(pmConn, bmoOrderPropertyModelExtra.getPropertyModelExtraId().toInteger());
			// Si es precio fijo obtener precio del catalogo; tambien si se quiere modificar
			if (bmoPropertyModelExtra.getFixedPrice().toBoolean()) {
				bmoOrderPropertyModelExtra.getPrice().setValue(bmoPropertyModelExtra.getPrice().toDouble());
			}
		}

		// Calcula el valor del item
		bmoOrderPropertyModelExtra.getAmount().setValue(bmoOrderPropertyModelExtra.getPrice().toDouble() 
				* bmoOrderPropertyModelExtra.getQuantity().toDouble());

		super.save(pmConn, bmoOrderPropertyModelExtra, bmUpdateResult);

		// Recalcula el subtotal
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		return bmUpdateResult;
	}

	private void findPropertyModelByOrder(String orderId, BmUpdateResult bmUpdateResult) throws SFException {
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		BmoOrder bmoOrder = new BmoOrder();
		BmoOrderType bmoOrderType = new BmoOrderType();

		try {
			//Obtener el pedido
			PmOrder pmOrder = new PmOrder(getSFParams());
			PmOrderType pmOrderType = new PmOrderType(getSFParams());
			bmoOrder = (BmoOrder) pmOrder.get(Integer.parseInt(orderId));
			bmoOrderType = (BmoOrderType) pmOrderType.get(bmoOrder.getOrderTypeId().toInteger());
			
			if(bmoOrderType.getType().equals(""+BmoOrderType.TYPE_LEASE)) {
				// Obten la propiedad del pedido
				PmOrderPropertyTax pmOrderPropertyTax = new PmOrderPropertyTax(getSFParams());
				BmoOrderPropertyTax bmoOrderPropertyTax = new BmoOrderPropertyTax();
				bmoOrderPropertyTax = (BmoOrderPropertyTax)pmOrderPropertyTax.getBy(orderId, bmoOrderPropertyTax.getOrderId().getName());

				// Obten el model
				PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
				bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoOrderPropertyTax.getBmoProperty().getPropertyModelId().toInteger());
				bmUpdateResult.setBmObject(bmoPropertyModel);
			}
			else {
				// Obten la propiedad del pedido
				PmOrderProperty pmOrderProperty = new PmOrderProperty(getSFParams());
				BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
				bmoOrderProperty = (BmoOrderProperty) pmOrderProperty.getBy(orderId,
						bmoOrderProperty.getOrderId().getName());

				// Obten el model
				PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
				bmoPropertyModel = (BmoPropertyModel) pmPropertyModel
						.get(bmoOrderProperty.getBmoProperty().getPropertyModelId().toInteger());
				bmUpdateResult.setBmObject(bmoPropertyModel);
			}

		} catch (SFException e) {
			bmUpdateResult.addError(bmoOrderPropertyModelExtra.getQuantity().getName(), "No se ha seleccionado el Inmueble del Pedido");
		}
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos del pedido
		bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)bmObject;

		// Revisar cantidad de items apartados
		if (action.equals(BmoOrderPropertyModelExtra.ACTION_GETPROPERTYMODEL)) {
			findPropertyModelByOrder(value, bmUpdateResult);
		} 

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn ,bmoOrderPropertyModelExtra.getOrderId().toInteger());

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		} else {
			// Primero elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal tomando en cuenta el item eliminado
			pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);
		}
		return bmUpdateResult;
	}
}
