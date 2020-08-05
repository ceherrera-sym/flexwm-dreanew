/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

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
import com.flexwm.server.co.PmPropertyModel;
import com.flexwm.server.co.PmPropertyModelExtra;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelExtra;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteProperty;
import com.flexwm.shared.cm.BmoQuotePropertyModelExtra;


public class PmQuotePropertyModelExtra extends PmObject {
	BmoQuotePropertyModelExtra bmoQuotePropertyModelExtra;

	public PmQuotePropertyModelExtra(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoQuotePropertyModelExtra = new BmoQuotePropertyModelExtra();
		setBmObject(bmoQuotePropertyModelExtra);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoQuotePropertyModelExtra.getPropertyModelExtraId(), bmoQuotePropertyModelExtra.getBmoPropertyModelExtra()),
				new PmJoin(bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getPropertyModelId(), bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel()),
				new PmJoin(bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getDevelopmentId(), bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getBmoDevelopment()),
				new PmJoin(bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getPropertyTypeId(), bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getBmoPropertyType()),
				new PmJoin(bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getBmoPropertyType().getOrderTypeId(), bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getBmoPropertyModel().getBmoPropertyType().getBmoOrderType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoQuotePropertyModelExtra = (BmoQuotePropertyModelExtra)autoPopulate(pmConn, new BmoQuotePropertyModelExtra());

		// BmoPropertyModelExtra
		BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();
		if ((int)pmConn.getInt(bmoPropertyModelExtra.getIdFieldName()) > 0) 
			bmoQuotePropertyModelExtra.setBmoPropertyModelExtra((BmoPropertyModelExtra) new PmPropertyModelExtra(getSFParams()).populate(pmConn));
		else 
			bmoQuotePropertyModelExtra.setBmoPropertyModelExtra(bmoPropertyModelExtra);

		return bmoQuotePropertyModelExtra;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoQuotePropertyModelExtra bmoQuotePropertyModelExtra = (BmoQuotePropertyModelExtra)bmObject;

		// Obten el pedido
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoQuotePropertyModelExtra.getQuoteId().toInteger());

		PmPropertyModelExtra pmPropertyModelExtra = new PmPropertyModelExtra(getSFParams());
		BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();

		// Obten el registro del recurso y su precio vigente
		if (bmoQuotePropertyModelExtra.getPropertyModelExtraId().toInteger() > 0) {
			bmoPropertyModelExtra = (BmoPropertyModelExtra)pmPropertyModelExtra.get(pmConn, bmoQuotePropertyModelExtra.getPropertyModelExtraId().toInteger());

			// Si es precio fijo obtener precio del catalogo; tambien si se quiere modificar
			if (bmoPropertyModelExtra.getFixedPrice().toBoolean()) {
				bmoQuotePropertyModelExtra.getPrice().setValue(bmoPropertyModelExtra.getPrice().toDouble());
			}
		}

		// Calcula el valor del item
		bmoQuotePropertyModelExtra.getAmount().setValue(bmoQuotePropertyModelExtra.getPrice().toDouble() 
				* bmoQuotePropertyModelExtra.getQuantity().toDouble());

		super.save(pmConn, bmoQuotePropertyModelExtra, bmUpdateResult);

		// Recalcula el subtotal
		pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);

		return bmUpdateResult;
	}

	private void findPropertyModelByQuote(String quoteId, BmUpdateResult bmUpdateResult) throws SFException {
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();

		try {
			// Obten la propiedad de la cotizacion
			PmQuoteProperty pmQuoteProperty = new PmQuoteProperty(getSFParams());
			BmoQuoteProperty bmoQuoteProperty = new BmoQuoteProperty();
			bmoQuoteProperty = (BmoQuoteProperty)pmQuoteProperty.getBy(quoteId, bmoQuoteProperty.getQuoteId().getName());

			// Obten el model
			PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
			bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoQuoteProperty.getBmoProperty().getPropertyModelId().toInteger());
			bmUpdateResult.setBmObject(bmoPropertyModel);

		} catch (SFException e) {
			bmUpdateResult.addError(bmoQuotePropertyModelExtra.getQuantity().getName(), "No se ha seleccionado el Inmueble de la Cotización");
		}
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos de la cotización
		bmoQuotePropertyModelExtra = (BmoQuotePropertyModelExtra)bmObject;

		// Revisar cantidad de items apartados
		if (action.equals(BmoQuotePropertyModelExtra.ACTION_GETPROPERTYMODEL)) {
			findPropertyModelByQuote(value, bmUpdateResult);
		} 

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoQuotePropertyModelExtra = (BmoQuotePropertyModelExtra)bmObject;

		// Obten el pedido
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuotePropertyModelExtra.getQuoteId().toInteger());

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
		} else {
			// Primero elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal tomando en cuenta el item eliminado
			pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);
		}
		return bmUpdateResult;
	}
}
