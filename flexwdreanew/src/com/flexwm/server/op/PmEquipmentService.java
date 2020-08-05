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
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoEquipmentService;


public class PmEquipmentService extends PmObject {
	BmoEquipmentService bmoEquipmentService;

	public PmEquipmentService(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoEquipmentService = new BmoEquipmentService();
		setBmObject(bmoEquipmentService);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(			
				new PmJoin(bmoEquipmentService.getEquipmentId(), bmoEquipmentService.getBmoEquipment()), 
				new PmJoin(bmoEquipmentService.getBmoEquipment().getUserId(), bmoEquipmentService.getBmoEquipment().getBmoUser()),
				new PmJoin(bmoEquipmentService.getBmoEquipment().getBmoUser().getAreaId(), bmoEquipmentService.getBmoEquipment().getBmoUser().getBmoArea()),
				new PmJoin(bmoEquipmentService.getBmoEquipment().getBmoUser().getLocationId(), bmoEquipmentService.getBmoEquipment().getBmoUser().getBmoLocation()),
				new PmJoin(bmoEquipmentService.getBmoEquipment().getEquipmentTypeId(), bmoEquipmentService.getBmoEquipment().getBmoEquipmentType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoEquipmentService = (BmoEquipmentService)autoPopulate(pmConn, new BmoEquipmentService());

		// Tipo de Equipo
		BmoEquipment bmoEquipment = new BmoEquipment();
		int EquipmentGroupId = (int)pmConn.getInt(bmoEquipment.getIdFieldName());
		if (EquipmentGroupId > 0) bmoEquipmentService.setBmoEquipment((BmoEquipment) new PmEquipment(getSFParams()).populate(pmConn));
		else bmoEquipmentService.setBmoEquipment(bmoEquipment);

		return bmoEquipmentService;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoEquipmentService bmoEquipmentService = (BmoEquipmentService)bmObject;

		if(bmoEquipmentService.getEquipmentId().toInteger() > 0){
			//Si el equipo entra a mantenimiento, dejar inactivo el equipo		
			this.enabledEquipment(pmConn, bmoEquipmentService, bmUpdateResult);
		}

		super.save(pmConn, bmoEquipmentService, bmUpdateResult);

		return bmUpdateResult;
	}

	private void enabledEquipment(PmConn pmConn, BmoEquipmentService bmoEquipmentService, BmUpdateResult bmUpdateResult) throws SFException {

		PmEquipment pmEquipment = new PmEquipment(getSFParams());
		BmoEquipment bmoEquipment = (BmoEquipment)pmEquipment.get(pmConn, bmoEquipmentService.getEquipmentId().toInteger());

		if (bmoEquipmentService.getStatus().equals(BmoEquipmentService.STATUS_MAINTENANCE)) {
			bmoEquipment.getStatus().setValue(BmoEquipment.STATUS_INACTIVE);
		} else {
			bmoEquipment.getStatus().setValue(BmoEquipment.STATUS_ACTIVE);
		}

		super.save(pmConn, bmoEquipment, bmUpdateResult);
	}

	public boolean equipmentInMantenace(PmConn pmConn, int equipmentId, BmUpdateResult bmUpdateResult) throws SFException {
		boolean result = false;
		String sql = "";
		int items = 0;
		sql = " SELECT COUNT(eqsv_equipmentserviceid) as items FROM equipmentservices " +
				" WHERE eqsv_equipmentid = " + equipmentId + 
				" AND eqsv_status = '" +  BmoEquipmentService.STATUS_MAINTENANCE + "'";
		pmConn.doFetch(sql);

		if (pmConn.next()) items = pmConn.getInt("items");

		if (items > 0) result = true;

		return result;		
	}
}
