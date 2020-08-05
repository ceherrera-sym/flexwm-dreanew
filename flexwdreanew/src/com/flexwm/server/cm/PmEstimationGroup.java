///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.server.cm;
//
//import com.symgae.server.PmObject;
//import com.symgae.server.PmConn;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.SFParams;
//import com.symgae.shared.SFPmException;
//import com.flexwm.shared.cm.BmoEstimation;
//import com.flexwm.shared.cm.BmoEstimationGroup;
//
//
//
//public class PmEstimationGroup extends PmObject {
//	BmoEstimationGroup bmoEstimationGroup;
//
//	public PmEstimationGroup(SFParams sfParams) throws SFPmException {
//		super(sfParams);
//		bmoEstimationGroup = new BmoEstimationGroup();
//		setBmObject(bmoEstimationGroup);
//	}
//
//	@Override
//	public BmObject populate(PmConn pmConn) throws SFException {
//		bmoEstimationGroup = (BmoEstimationGroup)autoPopulate(pmConn, new BmoEstimationGroup());
//		return bmoEstimationGroup;
//	}
//	
//	
//	public BmUpdateResult recaltulateQuantity(PmConn pmConn , int groupId,BmUpdateResult bmUpdateResult) throws SFPmException {		
//			//sumar la cantidad total de el item
//			String getQuantity = "SELECT SUM(esrf_quantity) AS quantityGroup FROM "
//					+ " estimationrfqitems WHERE  esrf_estimationgroupid =  " + 
//					"" +groupId;
//			pmConn.doFetch(getQuantity);
//			if(pmConn.next()) {
//				 int cantidad = pmConn.getInt("quantityGroup");
//				 String sumQuantity = "UPDATE  estimationgroups SET  esgp_quantity = "+cantidad+" "
//				 		+ " WHERE esgp_estimationgroupid = "+groupId;
//
//				 pmConn.doUpdate(sumQuantity);
//				 
//			}else {
//				 int cantidad = pmConn.getInt("quantityGroup");
//				 String sumQuantity = "UPDATE  estimationgroups SET  esgp_quantity = "+cantidad+" "
//				 		+ " WHERE esgp_estimationgroupid = "+groupId;
//				 pmConn.doUpdate(sumQuantity);
//			}
//				 
//		return bmUpdateResult;
//	}
//	
//	@Override
//	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		bmoEstimationGroup = (BmoEstimationGroup)bmObject;		
//
//		// Obten la cotización
//		PmEstimation pmEstimation = new PmEstimation(getSFParams());
//		BmoEstimation bmoEstimation = (BmoEstimation)pmEstimation.get(pmConn, bmoEstimationGroup.getEstimationId().toInteger());
//
//		// Si la cotización ya está autorizada, no se puede hacer movimientos
//		if (bmoEstimation.getStatus().toChar() == BmoEstimation.STATUS_AUTHORIZED) {
//			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Estimacion - está Completada.");
//		} 
//
//
//			super.save(pmConn, bmoEstimationGroup, bmUpdateResult);
//
//		return bmUpdateResult;
//	}
//	
//	@Override
//	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
//		bmoEstimationGroup = (BmoEstimationGroup)bmObject;
//
//		// Obten la cotización
//		PmEstimation pmEstimation= new PmEstimation(getSFParams());
//		BmoEstimation bmoEstimation = (BmoEstimation)pmEstimation.get(bmoEstimationGroup.getEstimationId().toInteger());
//
//		// Si la estimacion ya está autorizada, no se puede hacer movimientos
//		if (bmoEstimation.getStatus().toChar() == BmoEstimation.STATUS_AUTHORIZED) {
//			bmUpdateResult.addMsg("No se puede realizar movimientos sobre la Cotización - ya está Autorizada.");
//		} else {
//			if (action.equals(BmoEstimationGroup.ACTION_PRODUCTKIT)) {
//				PmConn pmConn = new PmConn(getSFParams());
//				try {
//					pmConn.open();
//					pmConn.disableAutoCommit();
//
//					// Crear el grupo de la cotización
//					super.save(pmConn, bmoEstimationGroup, bmUpdateResult);
//					bmoEstimationGroup.setId(bmUpdateResult.getId());
//					
//					if (!bmUpdateResult.hasErrors()) {
//						pmConn.commit();
//					}
//				} catch (SFException e) {
//					bmUpdateResult.addError(bmObject.getProgramCode(), "-Action() ERROR: " + e.toString());
//				} finally {
//					pmConn.close();
//				}
//			} 
//		}
//		return bmUpdateResult;
//	}
//	
//}
//
