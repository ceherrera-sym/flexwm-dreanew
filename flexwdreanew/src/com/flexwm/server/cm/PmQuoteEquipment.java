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
import com.flexwm.server.op.PmEquipment;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteEquipment;
import com.flexwm.shared.op.BmoEquipment;


public class PmQuoteEquipment extends PmObject {
	BmoQuoteEquipment bmoQuoteEquipment;

	public PmQuoteEquipment(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoQuoteEquipment = new BmoQuoteEquipment();
		setBmObject(bmoQuoteEquipment);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoQuoteEquipment.getEquipmentId(), bmoQuoteEquipment.getBmoEquipment()),
				new PmJoin(bmoQuoteEquipment.getBmoEquipment().getEquipmentTypeId(), bmoQuoteEquipment.getBmoEquipment().getBmoEquipmentType()),
				new PmJoin(bmoQuoteEquipment.getBmoEquipment().getUserId(), bmoQuoteEquipment.getBmoEquipment().getBmoUser()),
				new PmJoin(bmoQuoteEquipment.getBmoEquipment().getBmoUser().getAreaId(), bmoQuoteEquipment.getBmoEquipment().getBmoUser().getBmoArea()),
				new PmJoin(bmoQuoteEquipment.getBmoEquipment().getBmoUser().getLocationId(), bmoQuoteEquipment.getBmoEquipment().getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoQuoteEquipment = (BmoQuoteEquipment)autoPopulate(pmConn, new BmoQuoteEquipment());

		// BmoEquipment
		BmoEquipment bmoEquipment = new BmoEquipment();
		int equipmentId = (int)pmConn.getInt(bmoEquipment.getIdFieldName());
		if (equipmentId > 0) bmoQuoteEquipment.setBmoEquipment((BmoEquipment) new PmEquipment(getSFParams()).populate(pmConn));
		else bmoQuoteEquipment.setBmoEquipment(bmoEquipment);

		return bmoQuoteEquipment;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoQuoteEquipment = (BmoQuoteEquipment)bmObject;
		// Obten la cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteEquipment.getQuoteId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
		} else {
			// Si esta ligado a los recursos, obten el registro del recurso y su precio vigente
			if (bmoQuoteEquipment.getEquipmentId().toInteger() > 0) {
				// Si es nuevo registro, toma el precio del recurso
				if (!(bmoQuoteEquipment.getId() > 0)) {
					PmEquipment pmEquipment = new PmEquipment(getSFParams());
					BmoEquipment bmoEquipment = (BmoEquipment)pmEquipment.get(bmoQuoteEquipment.getEquipmentId().toInteger());
					bmoQuoteEquipment.getName().setValue(bmoEquipment.getName().toString());

					//bmoQuoteEquipment.getPrice().setValue(bmoEquipment.getPrice().toDouble());
					bmoQuoteEquipment.getBasePrice().setValue(bmoEquipment.getPrice().toDouble());
				} else {
					/*
					// Se comenta por:
					// Quien tenia permiso y cambiaba el precio del grupo, ej: 100,
					// y quien no tiene permiso, y cambiaba los DIAS y la CANTIDAD, lo restablecia, o sea, quitaba el 100 de quien si tenia permiso

					// Restablece el precio de catalogo si no tiene permisos
//					if (!getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE)) {
//						if ((bmoQuoteEquipment.getPrice().toDouble() > 0 && 
//								bmoQuoteEquipment.getPrice().toDouble() < bmoQuoteEquipment.getBasePrice().toDouble())) {
//							bmoQuoteEquipment.getPrice().setValue(bmoQuoteEquipment.getBasePrice().toDouble());
//						}
//					}
					*/
				}
			} else {
				bmoQuoteEquipment.getBasePrice().setValue(0);
			}

			// Revisa que el precio no sea menor a 0
			if (bmoQuoteEquipment.getPrice().toDouble() < 0)
				bmUpdateResult.addMsg("El Precio no puede ser menor a $0.00");

			// Revisa que los dias no sea menor a 0
			if (bmoQuoteEquipment.getDays().toDouble() < 0)
				bmUpdateResult.addMsg("Los Días no pueden ser menores a 0.");

			// Revisa que la cantidad no sea menor a 0
			if (bmoQuoteEquipment.getQuantity().toInteger() <= 0)
				bmUpdateResult.addMsg("La Cantidad no puede ser igual o menor a 0.");

			// Calcula el valor del item
			double amount = bmoQuoteEquipment.getPrice().toDouble() * bmoQuoteEquipment.getQuantity().toDouble();
			//Si los dias son mayores a 0 multiplicar por los dias
			if (bmoQuoteEquipment.getDays().toDouble() > 0)
				amount = amount * bmoQuoteEquipment.getDays().toDouble();
			bmoQuoteEquipment.getAmount().setValue(amount);

			// Primero agrega el ultimo valor
			super.save(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal
			pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		bmoQuoteEquipment = (BmoQuoteEquipment)bmObject;
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			// Obten la cotización
			PmQuote pmQuote = new PmQuote(getSFParams());
			BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteEquipment.getQuoteId().toInteger());

			// Si la cotización ya está autorizada, no se puede hacer movimientos
			if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
				bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
			} else {

				// Primero elimina el item
				super.delete(pmConn, bmObject, bmUpdateResult);

				// Recalcula el subtotal tomando en cuenta el item eliminado
				pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);

				if (!bmUpdateResult.hasErrors()) pmConn.commit();
			}
		} catch (SFPmException e) {
		} finally {
			pmConn.close();
		}	
		return bmUpdateResult;
	}
}
