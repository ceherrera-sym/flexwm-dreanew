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
import java.util.Iterator;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoEquipmentUse;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderEquipment;


public class PmEquipmentUse extends PmObject {
	BmoEquipmentUse bmoEquipmentUse;

	public PmEquipmentUse(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoEquipmentUse = new BmoEquipmentUse();
		setBmObject(bmoEquipmentUse);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(				
				new PmJoin(bmoEquipmentUse.getEquipmentId(), bmoEquipmentUse.getBmoEquipment()), 
				new PmJoin(bmoEquipmentUse.getBmoEquipment().getUserId(), bmoEquipmentUse.getBmoEquipment().getBmoUser()),
				new PmJoin(bmoEquipmentUse.getBmoEquipment().getBmoUser().getAreaId(), bmoEquipmentUse.getBmoEquipment().getBmoUser().getBmoArea()),
				new PmJoin(bmoEquipmentUse.getBmoEquipment().getBmoUser().getLocationId(), bmoEquipmentUse.getBmoEquipment().getBmoUser().getBmoLocation()),
				new PmJoin(bmoEquipmentUse.getBmoEquipment().getEquipmentTypeId(), bmoEquipmentUse.getBmoEquipment().getBmoEquipmentType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoEquipmentUse = (BmoEquipmentUse)autoPopulate(pmConn, new BmoEquipmentUse());

		// Equipo
		BmoEquipment bmoEquipment = new BmoEquipment();
		int equipmentId = (int)pmConn.getInt(bmoEquipment.getIdFieldName());
		if (equipmentId > 0) bmoEquipmentUse.setBmoEquipment((BmoEquipment) new PmEquipment(getSFParams()).populate(pmConn));
		else bmoEquipmentUse.setBmoEquipment(bmoEquipment);	

		return bmoEquipmentUse;
	}

	public BmUpdateResult createFromOrder(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		// Elimina usos previamente creados
		deleteOrderEquipmentUses(pmConn, bmoOrder.getId());

		// Obtener los recursos del Pedido
		PmOrderEquipment pmOrderEquipment = new PmOrderEquipment(getSFParams());
		BmoOrderEquipment bmoOrderEquipment = new BmoOrderEquipment();
		BmFilter listByOrderFilter = new BmFilter();
		listByOrderFilter.setValueFilter(bmoOrderEquipment.getKind(), bmoOrderEquipment.getOrderId(), bmoOrder.getId());
		Iterator<BmObject> orderEquipmentIterator = pmOrderEquipment.list(pmConn, listByOrderFilter).iterator();
		while (orderEquipmentIterator.hasNext()) {
			bmoOrderEquipment = (BmoOrderEquipment)orderEquipmentIterator.next();
			if (bmoOrderEquipment.getEquipmentId().toInteger() > 0) {

				BmoEquipmentUse bmoEquipmentUse = new BmoEquipmentUse();
				bmoEquipmentUse.getDateTime().setValue(bmoOrder.getLockStart().toString());
				bmoEquipmentUse.getComments().setValue(bmoOrder.getDescription().toString() + ", Fin del Pedido: " + bmoOrder.getLockEnd().toString());
				bmoEquipmentUse.getOrderId().setValue(bmoOrder.getId());
				bmoEquipmentUse.getEquipmentId().setValue(bmoOrderEquipment.getEquipmentId().toInteger());

				super.save(pmConn, bmoEquipmentUse, bmUpdateResult);
			}
		}

		return bmUpdateResult;
	}

	// Revisa si existen usos
	public void deleteOrderEquipmentUses(PmConn pmConn, int orderId) throws SFException {	
		pmConn.doUpdate(" DELETE FROM equipmentuses WHERE eqis_orderid = " + orderId);
	}
}
