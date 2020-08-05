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

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoProjectDetail;
import com.flexwm.shared.op.BmoOrder;


public class PmProjectDetail extends PmObject {
	BmoProjectDetail bmoProjectDetail;

	public PmProjectDetail(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProjectDetail = new BmoProjectDetail();
		setBmObject(bmoProjectDetail);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoProjectDetail());		
	}

	// Revisar si existe la orden de algún proyecto
	public boolean projectDetailExists(PmConn pmConn, int projectId) throws SFPmException {
		pmConn.doFetch("SELECT pjde_projectdetailid FROM projectdetails WHERE pjde_projectid = " + projectId);
		return pmConn.next();
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoProjectDetail bmoProjectDetail = (BmoProjectDetail)bmObject;

		// Revisar estatus del proyecto
		PmProject pmProject = new PmProject(getSFParams());
		BmoProject bmoProject = (BmoProject)pmProject.get(pmConn, bmoProjectDetail.getProjectId().toInteger());
		if (bmoProject.getStatus().toChar() != BmoProject.STATUS_REVISION)
			bmUpdateResult.addError(bmoProjectDetail.getLoadStartDate().getName(), "No se puede Modificar el Proyecto - está Autorizado");

		// Revisa secuencia de las fechas
		if (!bmUpdateResult.hasErrors()) {
			// Revisa fecha de Salida
			if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
					bmoProjectDetail.getExitDate().toString(), bmoProjectDetail.getPrepDate().toString()))
				bmUpdateResult.addError(bmoProjectDetail.getExitDate().getName(), 
						"No puede ser Anterior a " + bmoProjectDetail.getPrepDate().getLabel());

			// Revisa fecha de Inicio Montaje
			if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
					bmoProjectDetail.getLoadStartDate().toString(), bmoProjectDetail.getExitDate().toString()))
				bmUpdateResult.addError(bmoProjectDetail.getLoadStartDate().getName(), 
						"No puede ser Anterior a " + bmoProjectDetail.getExitDate().getLabel());

			// Revisa fecha de Entrega
			if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
					bmoProjectDetail.getDeliveryDate().toString(), bmoProjectDetail.getLoadStartDate().toString()))
				bmUpdateResult.addError(bmoProjectDetail.getDeliveryDate().getName(), 
						"No puede ser Anterior a " + bmoProjectDetail.getLoadStartDate().getLabel());

			// Revisa fecha de Inicio Desmontaje
			if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
					bmoProjectDetail.getUnloadStartDate().toString(), bmoProjectDetail.getDeliveryDate().toString()))
				bmUpdateResult.addError(bmoProjectDetail.getUnloadStartDate().getName(), 
						"No puede ser Anterior a " + bmoProjectDetail.getDeliveryDate().getLabel());

			// Revisa fecha de Regreso
			if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
					bmoProjectDetail.getReturnDate().toString(), bmoProjectDetail.getUnloadStartDate().toString()))
				bmUpdateResult.addError(bmoProjectDetail.getReturnDate().getName(), 
						"No puede ser Anterior a " + bmoProjectDetail.getUnloadStartDate().getLabel());
		}

		// Revisa si existe pedido relacionada con el proyecto
		if (!bmUpdateResult.hasErrors()){
			if (bmoProject.getOrderId().toInteger() > 0) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoProject.getOrderId().toInteger());

				// Actualiza fechas de bloqueo
				bmoOrder.getLockStart().setValue(bmoProjectDetail.getPrepDate().toString());
				bmoOrder.getLockEnd().setValue(bmoProjectDetail.getReturnDate().toString());

				pmOrder.save(pmConn, bmoOrder, bmUpdateResult);
			}
		}

		//		if (!bmUpdateResult.hasErrors()) {
		//			
		//			// Actualiza fechas del proyecto
		//			bmoProject.getStartDate().setValue(bmoProjectDetail.getPrepDate().toString());
		//			bmoProject.getEndDate().setValue(bmoProjectDetail.getReturnDate().toString());
		//			
		//			pmProject.saveSimple(pmConn, bmoProject, bmUpdateResult);
		//		}

		// Modificar las fechas de los colaboradores del proyecto
		// Revisa si existe pedido relacionada con el proyecto
		if (!bmUpdateResult.hasErrors() && bmoProject.getWFlowId().toInteger() > 0){
			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoProject.getWFlowId().toInteger());

			// Actualiza fechas de bloqueo
			bmoWFlow.getStartDate().setValue(bmoProjectDetail.getPrepDate().toString());
			bmoWFlow.getEndDate().setValue(bmoProjectDetail.getReturnDate().toString());

			pmWFlow.changeDate(pmConn, bmoWFlow, bmUpdateResult);
		}

		if (!bmUpdateResult.hasErrors()) 
			super.save(pmConn, bmoProjectDetail, bmUpdateResult);

		return bmUpdateResult;
	}
}
