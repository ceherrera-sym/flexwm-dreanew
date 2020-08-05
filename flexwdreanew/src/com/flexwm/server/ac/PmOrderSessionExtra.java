/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */

package com.flexwm.server.ac;

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
import com.flexwm.shared.ac.BmoOrderSessionExtra;
import com.flexwm.shared.ac.BmoSessionTypeExtra;
import com.flexwm.shared.op.BmoOrder;


public class PmOrderSessionExtra extends PmObject {
	BmoOrderSessionExtra bmoOrderSessionExtra;

	public PmOrderSessionExtra(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderSessionExtra = new BmoOrderSessionExtra();
		setBmObject(bmoOrderSessionExtra);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderSessionExtra.getSessionTypeExtraId(), bmoOrderSessionExtra.getBmoSessionTypeExtra()),
				new PmJoin(bmoOrderSessionExtra.getBmoSessionTypeExtra().getSessionTypeId(), bmoOrderSessionExtra.getBmoSessionTypeExtra().getBmoSessionType()),
				new PmJoin(bmoOrderSessionExtra.getBmoSessionTypeExtra().getBmoSessionType().getSessionDisciplineId(), bmoOrderSessionExtra.getBmoSessionTypeExtra().getBmoSessionType().getBmoSessionDiscipline()),
				new PmJoin(bmoOrderSessionExtra.getBmoSessionTypeExtra().getBmoSessionType().getCurrencyId(), bmoOrderSessionExtra.getBmoSessionTypeExtra().getBmoSessionType().getBmoCurrency())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderSessionExtra = (BmoOrderSessionExtra)autoPopulate(pmConn, new BmoOrderSessionExtra());

		// BmoSessionTypeExtra
		BmoSessionTypeExtra bmoSessionTypeExtra = new BmoSessionTypeExtra();
		int sessionExtraTypeId = (int)pmConn.getInt(bmoSessionTypeExtra.getIdFieldName());
		if (sessionExtraTypeId > 0) bmoOrderSessionExtra.setBmoSessionTypeExtra((BmoSessionTypeExtra) new PmSessionTypeExtra(getSFParams()).populate(pmConn));
		else bmoOrderSessionExtra.setBmoSessionTypeExtra(bmoSessionTypeExtra);

		return bmoOrderSessionExtra;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoOrderSessionExtra bmoOrderSessionExtra = (BmoOrderSessionExtra)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderSessionExtra.getOrderId().toInteger());

		PmSessionTypeExtra pmSessionTypeExtra = new PmSessionTypeExtra(getSFParams());
		BmoSessionTypeExtra bmoSessionTypeExtra = new BmoSessionTypeExtra();

		// Obten el registro del recurso y su precio vigente
		if (bmoOrderSessionExtra.getSessionTypeExtraId().toInteger() > 0) {
			bmoSessionTypeExtra = (BmoSessionTypeExtra)pmSessionTypeExtra.get(pmConn, bmoOrderSessionExtra.getSessionTypeExtraId().toInteger());

			// Si es nuevo registro o el precio del modelo es fijo, se obtiene del catalogo
			if (!(bmoOrderSessionExtra.getId() > 0) || (bmoSessionTypeExtra.getFixedPrice().toBoolean()))
				bmoOrderSessionExtra.getPrice().setValue(bmoSessionTypeExtra.getPrice().toDouble());
		}

		// Calcula el valor del item
		bmoOrderSessionExtra.getAmount().setValue(bmoOrderSessionExtra.getPrice().toDouble() 
				* bmoOrderSessionExtra.getQuantity().toDouble());

		super.save(pmConn, bmoOrderSessionExtra, bmUpdateResult);

		// Recalcula el subtotal
		pmOrder.updateBalance(pmConn, bmoOrder, bmUpdateResult);

		return bmUpdateResult;
	}
	
	
	
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos del pedido
		bmoOrderSessionExtra = (BmoOrderSessionExtra)bmObject;

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderSessionExtra = (BmoOrderSessionExtra)bmObject;

		// Obten el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderSessionExtra.getOrderId().toInteger());

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
