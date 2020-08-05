//package com.flexwm.server.op;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import com.flexwm.server.FlexUtil;
//import com.flexwm.server.cm.PmProjectActivities;
////import com.flexwm.server.cm.PmRFQU;
//import com.flexwm.shared.cm.BmoProjectActivities;
////import com.flexwm.shared.cm.BmoRFQU;
//import com.flexwm.shared.op.BmoServiceOrder;
//import com.symgae.server.PmConn;
//import com.symgae.server.PmJoin;
//import com.symgae.server.PmObject;
//import com.symgae.server.SFServerUtil;
//import com.symgae.server.sf.PmUser;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.SFParams;
//import com.symgae.shared.SFPmException;
//import com.symgae.shared.sf.BmoUser;
//
//
//public class PmServiceOrder extends PmObject {
//
//	BmoServiceOrder bmoServiceOrder;
//
//	public PmServiceOrder(SFParams sfParams) throws SFPmException {
//		super(sfParams);
//		bmoServiceOrder = new BmoServiceOrder();
//		setBmObject(bmoServiceOrder);
//
//		// Lista de joins
//		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
//				new PmJoin(bmoServiceOrder.getUserId(), bmoServiceOrder.getBmoUser()),
//				new PmJoin(bmoServiceOrder.getBmoUser().getAreaId(), bmoServiceOrder.getBmoUser().getBmoArea()),
//				new PmJoin(bmoServiceOrder.getBmoUser().getLocationId(), bmoServiceOrder.getBmoUser().getBmoLocation()),
////				new PmJoin(bmoServiceOrder.getRfquId(), bmoServiceOrder.getBmoRFQU()),
////				new PmJoin(bmoServiceOrder.getBmoRFQU().getOrderTypeId(), bmoServiceOrder.getBmoRFQU().getBmoOrderType()),
//				new PmJoin(bmoServiceOrder.getProjectActivityId(), bmoServiceOrder.getBmoProjectActivities()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getProfileId(), bmoServiceOrder.getBmoProjectActivities().getBmoProfile()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getStepProjectId(), bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getCustomerId(), bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getTerritoryId(), bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getBmoTerritory()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getReqPayTypeId(), bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getBmoReqPayType()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getWFlowId(), bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getWFlowTypeId(), bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowType()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getWFlowPhaseId(), bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowPhase()),
//				new PmJoin(bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getWFlowFunnelId(), bmoServiceOrder.getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowFunnel())
//				)));
//	}
//
//	@Override
//	public BmObject populate(PmConn pmConn) throws SFException {
//		BmoServiceOrder bmoServiceOrder = (BmoServiceOrder) autoPopulate(pmConn, new BmoServiceOrder());
//
//
//		// BmoUser
//		BmoUser bmoUser = new BmoUser();
//		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
//		if (userId > 0) bmoServiceOrder.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
//		else bmoServiceOrder.setBmoUser(bmoUser);
//
//
//		// BmoRFQU
////		BmoRFQU bmoRFQU = new BmoRFQU();
////		int rfquId = (int)pmConn.getInt(bmoRFQU.getIdFieldName());
////		if (rfquId > 0) bmoServiceOrder.setBmoRFQU((BmoRFQU) new PmRFQU(getSFParams()).populate(pmConn));
////		else bmoServiceOrder.setBmoRFQU(bmoRFQU);
//
//		// BmoRFQU
//		BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//		int projectActivityId = (int)pmConn.getInt(bmoProjectActivities.getIdFieldName());
//		if (projectActivityId > 0) bmoServiceOrder.setBmoProjectActivities((BmoProjectActivities) new PmProjectActivities(getSFParams()).populate(pmConn));
//		else bmoServiceOrder.setBmoProjectActivities(bmoProjectActivities);
//
//		return bmoServiceOrder;
//	}
//
//	@Override
//	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult)  {
//		BmoServiceOrder bmoServiceOrder = (BmoServiceOrder)bmObject;
//
//		// Validaciones generales
//		if (bmoServiceOrder.getStartDate().toString().equals(""))
//			bmUpdateResult.addError(bmoServiceOrder.getStartDate().getName(), 
//					"El campo " + getSFParams().getFieldFormTitle(bmoServiceOrder.getStartDate()) + " no debe estar vacío.");
//
//		if (bmoServiceOrder.getStartDate().toString().equals(""))
//			bmUpdateResult.addError(bmoServiceOrder.getEndDate().getName(), 
//					"El campo " + getSFParams().getFieldFormTitle(bmoServiceOrder.getEndDate()) + " no debe estar vacío.");
//		
//		try {
//			if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
//					bmoServiceOrder.getEndDate().toString(), bmoServiceOrder.getStartDate().toString()))
//				bmUpdateResult.addError(bmoServiceOrder.getEndDate().getName(), 
//						"No puede ser menor a la  " + bmoServiceOrder.getStartDate().getLabel() + ".");
//		} catch (SFException e) {
//			bmUpdateResult.addError(bmoServiceOrder.getEndDate().toString(), "Error al obtener las fechas: " + e.toString());
//		}
//
//		// Validaciones por Tipo de SRO
//		if (bmoServiceOrder.getType().equals(BmoServiceOrder.TYPE_SERVICE)) {
//			if (!(bmoServiceOrder.getProjectActivityId().toInteger() > 0))
//				bmUpdateResult.addError(bmoServiceOrder.getProjectActivityId().getName(), 
//						"El campo " + getSFParams().getFieldFormTitle(bmoServiceOrder.getProjectActivityId()) + " no debe estar vacío.");
//			else {
//				// Validar si ya existe la actividad de un proyecto
//				try {
//					PmConn pmConn =  new PmConn(getSFParams());
//					pmConn.open();
//					if (existActivityId(pmConn, bmoServiceOrder.getProjectActivityId().toInteger())) {
//						printDevLog("entrar a mandar error");
//						bmUpdateResult.addError(bmoServiceOrder.getProjectActivityId().getName(), 
//								"La " + bmoServiceOrder.getProjectActivityId().getLabel() + " ya existe en " 
//										+ getSROByActivityId(pmConn, bmoServiceOrder.getProjectActivityId().toInteger()).getCode().toString());
//					}
//					pmConn.close();
//				}catch (SFException e) {
//					bmUpdateResult.addError(bmoServiceOrder.getType().getName(), "-validate() - Error al abrir la conexión: " + e.toString());
//				}
//			}
//		} else if (bmoServiceOrder.getType().equals(BmoServiceOrder.TYPE_DEMO)) {
//			if (!(bmoServiceOrder.getRfquId().toInteger() > 0))
//				bmUpdateResult.addError(bmoServiceOrder.getRfquId().getName(), 
//						"El campo " + getSFParams().getFieldFormTitle(bmoServiceOrder.getRfquId()) + " no debe estar vacío.");
//		}
//		
//		
//
//	}
//
//	@Override
//	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
//		BmoServiceOrder bmoServiceOrder = (BmoServiceOrder)bmObject;
//		printDevLog("sro_actividad: "+bmoServiceOrder.getProjectActivityId().toString());
//		//		boolean newRecord = false;
//		// Se almacena de forma preliminar para asignar ID
//		if (!(bmoServiceOrder.getId() > 0)) {
//			//			newRecord = true;
//			super.save(pmConn, bmoServiceOrder, bmUpdateResult);
//			bmoServiceOrder.setId(bmUpdateResult.getId());
//
//			//La clave debe tener 4 digitos
//			String code = FlexUtil.codeFormatDigits(bmUpdateResult.getId(), 4, BmoServiceOrder.CODE_PREFIX);			
//			bmoServiceOrder.getCode().setValue(code);
//			
//		}
//
//		super.save(pmConn, bmoServiceOrder, bmUpdateResult);
//		return bmUpdateResult;
//	}
//	
//	// Actualizar horas reales en SRO
//	public void updateRealTime(PmConn pmConn, double realTime, int serviceOrderId) throws SFException {
//		pmConn.doUpdate("UPDATE " + formatKind("serviceorders") 
//		+ " SET sror_realtime = " + realTime
//		+ " WHERE sror_serviceorderid = " + serviceOrderId);
//	}
//	
//	// Si existe un registro de reporte de horas, activar en SRO
//	public void updateHasReportTime(PmConn pmConn, int serviceOrderId) throws SFException {
//		PmServiceOrderReportTime pmServiceOrderReportTime = new PmServiceOrderReportTime(getSFParams());
//		if (pmServiceOrderReportTime.hasServiceOrderReportTime(pmConn, serviceOrderId)) {
//			pmConn.doUpdate("UPDATE " + formatKind("serviceorders" 
//					+ " SET sror_hasreporttime = 1 "
//					+ " WHERE sror_serviceorderid = " + serviceOrderId));
//		} else {
//			pmConn.doUpdate("UPDATE " + formatKind("serviceorders" 
//					+ " SET sror_hasreporttime = 0 "
//					+ " WHERE sror_serviceorderid = " + serviceOrderId));
//		}
//	}
//
//	// Obtener la tarifa del usuario
//	private double getUserRate(int userId) throws SFException {
//		double rate = 0;
//		// Existe el usuario
//		if (userId > 0) {
//			BmoUser bmoUser = new BmoUser();
//			PmUser pmUser = new PmUser(getSFParams());
//			bmoUser = (BmoUser)pmUser.get(userId);
//			rate = bmoUser.getRate().toDouble();
//		} 
//		printDevLog("Tarifa del usuario: "+rate);
//		return rate;
//	}
//	
//	// Existe la actividad
//	public boolean existActivityId(PmConn pmConn, int projectActivityId) throws SFException {
//		boolean result = false;
//		printDevLog("existActivityId");
//
//		pmConn.doFetch("SELECT * FROM " + formatKind("serviceorders") + " WHERE sror_projectactivitiesid = " + projectActivityId);
//		if (pmConn.next()) result = true;
//		printDevLog("existActivityId_result: "+result);
//		return result;
//	}
//	
//	// Buscar SRO por la actividad
//	public BmoServiceOrder getSROByActivityId(PmConn pmConn, int projectActivityId) throws SFException {
//		BmoServiceOrder bmoServiceOrder= new BmoServiceOrder();
//		PmServiceOrder pmServiceOrder = new PmServiceOrder(getSFParams());
//		bmoServiceOrder = (BmoServiceOrder)pmServiceOrder.getBy(projectActivityId, bmoServiceOrder.getProjectActivityId().getName());
//		
//		return bmoServiceOrder;
//	}
//
//	@Override
//	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
//		// Actualiza datos de la cotización
//		//		bmoServiceOrder = (BmoServiceOrder)this.get(bmObject.getId());
//
//		// Obtiene el ID del modulo de effecto effecto
//		if (action.equals(BmoServiceOrder.ACTION_GETUSERRATE)) {
//			String result = "" + getUserRate(Integer.parseInt(value));
//			bmUpdateResult.setMsg("" + result);
//		}
//		return bmUpdateResult;
//	}
//
//	@Override
//	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		bmoServiceOrder = (BmoServiceOrder)bmObject;
//
////		PmServiceOrderReportTime pmServiceOrderReportTime = new PmServiceOrderReportTime(getSFParams());
//		
//		pmConn.doUpdate("DELETE FROM " + formatKind("serviceorderreporttimes") 
//					+ " WHERE srrt_serviceorderid = " + bmoServiceOrder.getId());
//
//		// YA NO APLICA
////		if (pmServiceOrderReportTime.hasServiceOrderReportTime(pmConn, bmoServiceOrder.getId()))
////			bmUpdateResult.addError(bmoServiceOrder.getCode().getName(), 
////					"<b>Existen Registros ligados del programa " + getSFParams().getProgramTitle(new BmoServiceOrderReportTime()) + ".</b>");
//
//		// Eliminar SRO
//		super.delete(pmConn, bmoServiceOrder, bmUpdateResult);
//
//		return bmUpdateResult;
//	}
//	
//	
//
//}
