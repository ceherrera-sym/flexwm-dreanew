/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

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
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderStaff;


public class PmOrderStaff extends PmObject {
	BmoOrderStaff bmoOrderStaff;

	public PmOrderStaff(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderStaff = new BmoOrderStaff();
		setBmObject(bmoOrderStaff);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderStaff.getProfileId(), bmoOrderStaff.getBmoProfile())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderStaff = (BmoOrderStaff)autoPopulate(pmConn, new BmoOrderStaff());

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		if ((int)pmConn.getInt(bmoProfile.getIdFieldName()) > 0) 
			bmoOrderStaff.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else 
			bmoOrderStaff.setBmoProfile(bmoProfile);

		return bmoOrderStaff;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoOrderStaff bmoOrderStaff = (BmoOrderStaff)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderStaff.getOrderId().toInteger());

		PmProfile pmProfile = new PmProfile(getSFParams());
		BmoProfile bmoProfile = new BmoProfile();

		// Asigna bloqueo abierto por default
		bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_OPEN);

		// Si el pedido ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		} else {
			// Obten el registro del grupo y su precio vigente
			if (bmoOrderStaff.getProfileId().toInteger() > 0) {
				// Si es nuevo registro, toma el precio del grupo
				if (!(bmoOrderStaff.getId() > 0)) {
					bmoProfile = (BmoProfile)pmProfile.get(bmoOrderStaff.getProfileId().toInteger());
					bmoOrderStaff.getName().setValue(bmoProfile.getName().toString());

					//bmoOrderStaff.getPrice().setValue(bmoProfile.getPrice().toDouble());
					bmoOrderStaff.getBasePrice().setValue(bmoProfile.getPrice().toDouble());
					bmoOrderStaff.getBaseCost().setValue(bmoProfile.getCost().toDouble());
				} else {
					/*
					// Se comenta por:
					// Quien tenia permiso y cambiaba el precio del grupo, ej: 100,
					// y quien no tiene permiso, y cambiaba los DIAS y la CANTIDAD, lo restablecia, o sea, quitaba el 100 de quien si tenia permiso

					// Restablece el precio de catalogo si no tiene permisos
//					if (!getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE)) {
//						if ((bmoOrderStaff.getPrice().toDouble() > 0 && 
//								bmoOrderStaff.getPrice().toDouble() < bmoOrderStaff.getBasePrice().toDouble())) {
//							bmoOrderStaff.getPrice().setValue(bmoOrderStaff.getBasePrice().toDouble());
//						}
//					}
					*/
				}

				// Si esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
				if (isLocked(pmConn, bmoOrderStaff, bmoProfile)) 
					bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_LOCKED);
				else 
					bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_CONFLICT);
			} else {
				//bmoOrderStaff.getBasePrice().setValue(bmoOrderStaff.getPrice().toDouble());
				bmoOrderStaff.getBaseCost().setValue(0);
			}

			// Revisa que el precio no sea menor a 0
			if (bmoOrderStaff.getPrice().toDouble() < 0)
				bmUpdateResult.addMsg("El Precio no puede ser menor a $0.00");

			// Revisa que los dias no sea menor a 0
			if (bmoOrderStaff.getDays().toDouble() < 0)
				bmUpdateResult.addMsg("Los Días no pueden ser menores a 0.");

			// Revisa que la cantidad no sea menor a 0
			if (bmoOrderStaff.getQuantity().toInteger() <= 0)
				bmUpdateResult.addMsg("La Cantidad no puede ser igual o menor a 0.");

			// Calcula el valor del item
			bmoOrderStaff.getAmount().setValue(bmoOrderStaff.getPrice().toDouble() 
					* bmoOrderStaff.getDays().toDouble()
					* bmoOrderStaff.getQuantity().toInteger());

			super.save(pmConn, bmoOrderStaff, bmUpdateResult);

			// Recalcula el subtotal
			pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

			// Asigna estatus de bloqueo
			pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Solo actualiza estatus de bloqueo
	public BmUpdateResult updateLockStatus(PmConn pmConn, BmoOrderStaff bmoOrderStaff, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_OPEN);

		PmProfile pmProfile = new PmProfile(getSFParams());
		BmoProfile bmoProfile = new BmoProfile();

		// Asigna bloqueo abierto por default
		bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_OPEN);

		// Obten el registro del recurso y su precio vigente
		if (bmoOrderStaff.getProfileId().toInteger() > 0) {
			bmoProfile = (BmoProfile)pmProfile.get(pmConn, bmoOrderStaff.getProfileId().toInteger());

			bmoOrderStaff.getName().setValue(bmoProfile.getName().toString());

			// Si esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
			if (isLocked(pmConn, bmoOrderStaff, bmoProfile)) 
				bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_LOCKED);
			else 
				bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_CONFLICT);
		}

		super.save(pmConn, bmoOrderStaff, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult createFromQuote(PmConn pmConn, BmObject bmObject, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoOrderStaff bmoOrderStaff = (BmoOrderStaff)bmObject;

		PmProfile pmProfile = new PmProfile(getSFParams());
		BmoProfile bmoProfile = new BmoProfile();

		// Asigna bloqueo abierto por default
		bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_OPEN);

		// Obten el registro del recurso y su precio vigente
		if (bmoOrderStaff.getProfileId().toInteger() > 0) {
			bmoProfile = (BmoProfile)pmProfile.get(pmConn, bmoOrderStaff.getProfileId().toInteger());

			bmoOrderStaff.getName().setValue(bmoProfile.getName().toString());

			// Si esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
			if (isLocked(pmConn, bmoOrderStaff, bmoProfile)) 
				bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_LOCKED);
			else 
				bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_CONFLICT);
		}

		// Calcula el valor del item
		bmoOrderStaff.getAmount().setValue(bmoOrderStaff.getPrice().toDouble() 
				* bmoOrderStaff.getDays().toDouble()
				* bmoOrderStaff.getQuantity().toInteger());

		super.save(pmConn, bmoOrderStaff, bmUpdateResult);

		return bmUpdateResult;
	}


	private boolean isLocked(PmConn pmConn, BmoOrderStaff bmoOrderStaff, BmoProfile bmoProfile) throws SFPmException{
		//		String sql = "SELECT ords_orderstaffid FROM orderstaff "
		//				+ " LEFT JOIN programprofiles ON (ords_usergroupid = pfus_usergroupid) "
		//				+ " WHERE "
		//				+ " ords_orderstaffid <> " + bmoOrderStaff.getId() 
		//				+ " AND pfus_userid = " + bmoProfileUser.getUserId().toInteger()
		//				+ " AND ords_lockstatus = '" + BmoOrderStaff.LOCKSTATUS_LOCKED + "'" 
		//				+ " AND ("
		//				+ "		('" + bmoOrderStaff.getLockStart().toString() + "' BETWEEN ords_lockstart AND ords_lockend)"
		//				+ "		OR"
		//				+ "		('" + bmoOrderStaff.getLockEnd().toString() + "' BETWEEN ords_lockstart AND ords_lockend)"
		//				+ "		)";
		//
		//		if (!getSFParams().isProduction()) System.out.println("PmOrderStaff-isLocked(): " + sql);
		//
		//		pmConn.doFetch(sql);

		return true;		
	}

	//	private void validateQuoteStaff(PmConn pmConn, BmoOrder bmoOrder, BmoProfileUser bmoProfileUser, BmUpdateResult bmUpdateResult) throws SFException {
	//		// Si el proyecto viene de una oportunidad, revisar
	//		if (bmoOrder.getQuoteId().toInteger() > 0) {
	//			BmoQuote bmoQuote = new BmoQuote();
	//			PmQuote pmQuote = new PmQuote(getSFParams());
	//			bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoOrder.getQuoteId().toInteger());
	//
	//			PmOrder pmOrder = new PmOrder(getSFParams());
	//
	//			int quoteStaffQuantity = pmQuote.getQuoteStaffQuantity(pmConn, bmoQuote.getId(), bmoProfileUser.getProfileId().toString());
	//			int orderStaffQuantity = pmOrder.getOrderStaffQuantity(pmConn, bmoOrder.getId(), bmoProfileUser.getProfileId().toString());
	//
	//			if (orderStaffQuantity > quoteStaffQuantity)
	//				bmUpdateResult.addMsg("El Personal del Pedido supera a la Cotización: Grupo: "
	//						+ " " + bmoProfileUser.getBmoProfile().getName().toString() + " "
	//						+ " Cotización: " + quoteStaffQuantity + ", "
	//						+ " Pedido: " + orderStaffQuantity);
	//		}
	//	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoOrderStaff = (BmoOrderStaff)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderStaff.getOrderId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		//		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
		//			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		//		} else {
		if (action.equals(BmoOrderStaff.ACTION_CHANGEORDERSTAFF)) {
			PmConn pmConn = new PmConn(getSFParams());
			try {
				pmConn.open();
				pmConn.disableAutoCommit();

				// Modifica unicamente el id y nombre del item, no el precio
				int profileId = Integer.parseInt(value);
				PmProfile pmProfile = new PmProfile(getSFParams());
				BmoProfile bmoProfile = (BmoProfile)pmProfile.get(profileId);

				bmoOrderStaff.getName().setValue(bmoProfile.getName().toString());
				bmoOrderStaff.getProfileId().setValue(profileId);

				// Modificar sin efectos adicionales el personal
				super.saveSimple(pmConn, bmoOrderStaff, bmUpdateResult);

				if (!isLocked(pmConn, bmoOrderStaff, bmoProfile)) 
					bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_LOCKED);
				else 
					bmoOrderStaff.getLockStatus().setValue(BmoOrderStaff.LOCKSTATUS_CONFLICT);

				// Modificar sin efectos adicionales el personal
				super.saveSimple(pmConn, bmoOrderStaff, bmUpdateResult);

				// Asigna estatus de bloqueo
				pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);

				if (!bmUpdateResult.hasErrors()) {
					pmConn.commit();
				}
			} catch (SFPmException e) {
				bmUpdateResult.addError(bmObject.getProgramCode(), "-Action() ERROR: " + e.toString());
			} finally {
				pmConn.close();
			}
		}
		//		}
		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderStaff = (BmoOrderStaff)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderStaff.getOrderId().toInteger());

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		} else {
			// Primero elimina el item
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Recalcula el subtotal tomando en cuenta el item eliminado
			pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

			// Asigna estatus de bloqueo
			pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);
		}
		return bmUpdateResult;
	}
}
