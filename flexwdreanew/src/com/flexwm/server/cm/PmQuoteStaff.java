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
import com.symgae.server.sf.PmProfile;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteStaff;


public class PmQuoteStaff extends PmObject {
	BmoQuoteStaff bmoQuoteStaff;

	public PmQuoteStaff(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoQuoteStaff = new BmoQuoteStaff();
		setBmObject(bmoQuoteStaff);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoQuoteStaff.getProfileId(), bmoQuoteStaff.getBmoProfile())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoQuoteStaff = (BmoQuoteStaff)autoPopulate(pmConn, new BmoQuoteStaff());

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		int equipmentId = (int)pmConn.getInt(bmoProfile.getIdFieldName());
		if (equipmentId > 0) bmoQuoteStaff.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else bmoQuoteStaff.setBmoProfile(bmoProfile);

		return bmoQuoteStaff;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoQuoteStaff = (BmoQuoteStaff)bmObject;

		// Obten la cotización
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteStaff.getQuoteId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
		} else {
			// Si esta ligado a los recursos, obten el registro del grupo y su precio vigente
			if (bmoQuoteStaff.getProfileId().toInteger() > 0) {
				// Si es nuevo registro, toma el precio del grupo
				if (!(bmoQuoteStaff.getId() > 0)) {
					PmProfile pmProfile = new PmProfile(getSFParams());
					BmoProfile bmoProfile = (BmoProfile)pmProfile.get(bmoQuoteStaff.getProfileId().toInteger());
					bmoQuoteStaff.getName().setValue(bmoProfile.getName().toString());

					//bmoQuoteStaff.getPrice().setValue(bmoProfile.getPrice().toDouble());
					bmoQuoteStaff.getBasePrice().setValue(bmoQuoteStaff.getPrice().toDouble());
				} else {
					/*
					// Se comenta por:
					// Quien tenia permiso y cambiaba el precio del grupo, ej: 100,
					// y quien no tiene permiso, y cambiaba los DIAS y la CANTIDAD, lo restablecia, o sea, quitaba el 100 de quien si tenia permiso
					
					// Restablece el precio de catalogo si no tiene permisos
					if (!getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE)) {
						if ((bmoQuoteStaff.getPrice().toDouble() > 0 && 
								bmoQuoteStaff.getPrice().toDouble() < bmoQuoteStaff.getBasePrice().toDouble())) {
							bmoQuoteStaff.getPrice().setValue(bmoQuoteStaff.getBasePrice().toDouble());
						}
					}
					*/
				}
			} else {
				bmoQuoteStaff.getBasePrice().setValue(0);
			}

			// Revisa que el precio no sea menor a 0
			if (bmoQuoteStaff.getPrice().toDouble() < 0)
				bmUpdateResult.addMsg("El Precio no puede ser menor a $0.00");

			// Revisa que los dias no sea menor a 0
			if (bmoQuoteStaff.getDays().toDouble() < 0)
				bmUpdateResult.addMsg("Los Días no pueden ser menores a 0.");

			// Revisa que la cantidad no sea menor a 0
			if (bmoQuoteStaff.getQuantity().toInteger() <= 0)
				bmUpdateResult.addMsg("La Cantidad no puede ser igual o menor a 0.");

			// Calcula el valor del item
			double amount = bmoQuoteStaff.getPrice().toDouble() * bmoQuoteStaff.getQuantity().toDouble();
			//Si los dias son mayores a 0 multiplicar por los dias
			if (bmoQuoteStaff.getDays().toDouble() > 0)
				amount = amount * bmoQuoteStaff.getDays().toDouble();
			bmoQuoteStaff.getAmount().setValue(amount);

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
		bmoQuoteStaff = (BmoQuoteStaff)bmObject;
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			// Obten la cotización
			PmQuote pmQuote = new PmQuote(getSFParams());
			BmoQuote bmoQuote = (BmoQuote)pmQuote.get(bmoQuoteStaff.getQuoteId().toInteger());

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
			System.out.println(e.toString());
		} finally {
			pmConn.close();
		}	
		return bmUpdateResult;
	}
}
