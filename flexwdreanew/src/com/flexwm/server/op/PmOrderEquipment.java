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
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderEquipment;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoOrderItem;


public class PmOrderEquipment extends PmObject {
	BmoOrderEquipment bmoOrderEquipment;

	public PmOrderEquipment(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderEquipment = new BmoOrderEquipment();
		setBmObject(bmoOrderEquipment);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderEquipment.getEquipmentId(), bmoOrderEquipment.getBmoEquipment()),
				new PmJoin(bmoOrderEquipment.getBmoEquipment().getEquipmentTypeId(), bmoOrderEquipment.getBmoEquipment().getBmoEquipmentType()),
				new PmJoin(bmoOrderEquipment.getBmoEquipment().getUserId(), bmoOrderEquipment.getBmoEquipment().getBmoUser()),
				new PmJoin(bmoOrderEquipment.getBmoEquipment().getBmoUser().getAreaId(), bmoOrderEquipment.getBmoEquipment().getBmoUser().getBmoArea()),
				new PmJoin(bmoOrderEquipment.getBmoEquipment().getBmoUser().getLocationId(), bmoOrderEquipment.getBmoEquipment().getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderEquipment = (BmoOrderEquipment)autoPopulate(pmConn, new BmoOrderEquipment());

		// BmoEquipment
		BmoEquipment bmoEquipment = new BmoEquipment();
		int equipmentId = (int)pmConn.getInt(bmoEquipment.getIdFieldName());
		if (equipmentId > 0) bmoOrderEquipment.setBmoEquipment((BmoEquipment) new PmEquipment(getSFParams()).populate(pmConn));
		else bmoOrderEquipment.setBmoEquipment(bmoEquipment);

		return bmoOrderEquipment;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoOrderEquipment bmoOrderEquipment = (BmoOrderEquipment)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderEquipment.getOrderId().toInteger());

		PmEquipment pmEquipment = new PmEquipment(getSFParams());
		BmoEquipment bmoEquipment = new BmoEquipment();

		// Si el pedido ya está autorizada, no se puede hacer movimientos
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		} else {
			// Asigna bloqueo abierto por default
			bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_OPEN);

			// Obten el registro del recurso y su precio vigente
			if (bmoOrderEquipment.getEquipmentId().toInteger() > 0) {

				// Si es nuevo registro, toma el precio del recurso
				if (!(bmoOrderEquipment.getId() > 0)) {
					bmoEquipment = (BmoEquipment)pmEquipment.get(bmoOrderEquipment.getEquipmentId().toInteger());
					bmoOrderEquipment.getName().setValue(bmoEquipment.getName().toString());

					//bmoOrderEquipment.getPrice().setValue(bmoEquipment.getPrice().toDouble());
					bmoOrderEquipment.getBasePrice().setValue(bmoEquipment.getPrice().toDouble());
					bmoOrderEquipment.getBaseCost().setValue(bmoEquipment.getCost().toDouble());
				} else {
					/*
					// Se comenta por:
					// Quien tenia permiso y cambiaba el precio del grupo, ej: 100,
					// y quien no tiene permiso, y cambiaba los DIAS y la CANTIDAD, lo restablecia, o sea, quitaba el 100 de quien si tenia permiso

					// Restablece el precio de catalogo si no tiene permisos
//					if (!getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE)) {
//						if ((bmoOrderEquipment.getPrice().toDouble() > 0 && 
//								bmoOrderEquipment.getPrice().toDouble() < bmoOrderEquipment.getBasePrice().toDouble())) {
//							bmoOrderEquipment.getPrice().setValue(bmoOrderEquipment.getBasePrice().toDouble());
//						}
//					}
					*/
				}

				// Si esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
					if (!isLocked(pmConn, bmoOrderEquipment)) 
						bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_LOCKED);
					else 
						bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_CONFLICT);
				} else {
					bmoOrderEquipment.getLockStatus().setValue(BmoOrderItem.LOCKSTATUS_LOCKED);
				}
			} else {
				//bmoOrderEquipment.getBasePrice().setValue(bmoOrderEquipment.getPrice().toDouble());
				bmoOrderEquipment.getBaseCost().setValue(0);
			}

			// Revisa que el precio no sea menor a 0
			if (bmoOrderEquipment.getPrice().toDouble() < 0)
				bmUpdateResult.addMsg("El Precio no puede ser menor a $0.00");

			// Revisa que los dias no sea menor a 0
			if (bmoOrderEquipment.getDays().toDouble() < 0)
				bmUpdateResult.addMsg("Los Días no pueden ser menores a 0.");

			// Revisa que la cantidad no sea menor a 0
			if (bmoOrderEquipment.getQuantity().toInteger() <= 0)
				bmUpdateResult.addMsg("La Cantidad no puede ser igual o menor a 0.");

			// Calcula el valor del item
			bmoOrderEquipment.getAmount().setValue(
					bmoOrderEquipment.getPrice().toDouble() * 
					bmoOrderEquipment.getQuantity().toDouble() * 
					bmoOrderEquipment.getDays().toDouble()
					);

			// Si esta apartado se asigna como conflicto
			if (isLocked(pmConn, bmoOrderEquipment)) bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_CONFLICT);

			super.save(pmConn, bmoOrderEquipment, bmUpdateResult);

			// Recalcula el subtotal
			pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

			// Asigna estatus de bloqueo
			pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Solo actualiza estatus de bloqueo
	public BmUpdateResult updateLockStatus(PmConn pmConn, BmoOrderEquipment bmoOrderEquipment, BmUpdateResult bmUpdateResult) throws SFException {

		bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_OPEN);

		// Obten el registro del recurso y su precio vigente
		if (bmoOrderEquipment.getEquipmentId().toInteger() > 0) {
			PmEquipment pmEquipment = new PmEquipment(getSFParams());
			BmoEquipment bmoEquipment = (BmoEquipment)pmEquipment.get(bmoOrderEquipment.getEquipmentId().toInteger());
			bmoOrderEquipment.getName().setValue(bmoEquipment.getName().toString());

			// Si esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
			if (!isLocked(pmConn, bmoOrderEquipment)) 
				bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_LOCKED);
			else 
				bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_CONFLICT);
		}

		super.save(pmConn, bmoOrderEquipment, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult createFromQuote(PmConn pmConn, BmObject bmObject, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoOrderEquipment bmoOrderEquipment = (BmoOrderEquipment)bmObject;

		PmEquipment pmEquipment = new PmEquipment(getSFParams());
		BmoEquipment bmoEquipment = new BmoEquipment();

		// Asigna bloqueo abierto por default
		bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_OPEN);

		// Obten el registro del recurso y su precio vigente
		if (bmoOrderEquipment.getEquipmentId().toInteger() > 0) {
			bmoEquipment = (BmoEquipment)pmEquipment.get(bmoOrderEquipment.getEquipmentId().toInteger());
			bmoOrderEquipment.getName().setValue(bmoEquipment.getName().toString());

			// Si esta apartado se asigna como conflicto; si es de clase se asigna como conflicto
			if (!isLocked(pmConn, bmoOrderEquipment)) bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_LOCKED);
			else bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_CONFLICT);
		}

		// Calcula el valor del item
		bmoOrderEquipment.getAmount().setValue(
				bmoOrderEquipment.getPrice().toDouble() * 
				bmoOrderEquipment.getQuantity().toDouble() * 
				bmoOrderEquipment.getDays().toDouble()
				);

		// Si esta apartado se asigna como conflicto
		if (isLocked(pmConn, bmoOrderEquipment)) bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_CONFLICT);

		super.save(pmConn, bmoOrderEquipment, bmUpdateResult);

		return bmUpdateResult;
	}

	public boolean isLocked(PmConn pmConn, BmoOrderEquipment bmoOrderEquipment) throws SFPmException{

		String sql = "SELECT ordq_orderequipmentid FROM orderequipments "
				+ " WHERE "
				+ " ordq_orderequipmentid <> " + bmoOrderEquipment.getId() 
				+ " AND ordq_equipmentid = " + bmoOrderEquipment.getEquipmentId().toInteger()
				+ " AND ordq_lockstatus = '" + BmoOrderEquipment.LOCKSTATUS_LOCKED + "'" 
				+ " AND ("
				+ "		('" + bmoOrderEquipment.getLockStart().toString() + "' BETWEEN ordq_lockstart AND ordq_lockend)"
				+ "		OR"
				+ "		('" + bmoOrderEquipment.getLockEnd().toString() + "' BETWEEN ordq_lockstart AND ordq_lockend)"
				+ "		)";

		pmConn.doFetch(sql);

		return pmConn.next();		
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		bmoOrderEquipment = (BmoOrderEquipment)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderEquipment.getOrderId().toInteger());

		// Si la cotización ya está autorizada, no se puede hacer movimientos
		//		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
		//			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		//		} else {
		if (action.equals(BmoOrderEquipment.ACTION_CHANGEORDEREQUIPMENT)) {
			PmConn pmConn = new PmConn(getSFParams());
			try {
				pmConn.open();
				pmConn.disableAutoCommit();

				// Modifica unicamente el id y nombre del item, no el precio
				int equipmentId = Integer.parseInt(value);
				PmEquipment pmEquipment = new PmEquipment(getSFParams());
				BmoEquipment bmoEquipment = (BmoEquipment)pmEquipment.get(equipmentId);

				bmoOrderEquipment.getName().setValue(bmoEquipment.getName().toString());
				bmoOrderEquipment.getEquipmentId().setValue(equipmentId);

				// Modificar sin efectos adicionales el personal
				super.saveSimple(pmConn, bmoOrderEquipment, bmUpdateResult);

				if (!isLocked(pmConn, bmoOrderEquipment)) 
					bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_LOCKED);
				else 
					bmoOrderEquipment.getLockStatus().setValue(BmoOrderEquipment.LOCKSTATUS_CONFLICT);

				// Modificar sin efectos adicionales el personal
				super.saveSimple(pmConn, bmoOrderEquipment, bmUpdateResult);

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
		bmoOrderEquipment = (BmoOrderEquipment)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderEquipment.getOrderId().toInteger());

		// Si la pedido ya está autorizada, no se puede hacer movimientos
		//		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
		//			bmUpdateResult.addMsg("No se puede realizar movimientos sobre el Pedido - ya está Autorizado.");
		//		} else {
		// Primero elimina el item
		super.delete(pmConn, bmObject, bmUpdateResult);

		// Recalcula el subtotal tomando en cuenta el item eliminado
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		// Asigna estatus de bloqueo
		pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);
		//		}
		return bmUpdateResult;
	}
}
