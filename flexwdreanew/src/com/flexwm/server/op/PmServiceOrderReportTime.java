//package com.flexwm.server.op;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.StringTokenizer;
//import java.util.concurrent.TimeUnit;
//import com.flexwm.server.FlexUtil;
//import com.flexwm.shared.op.BmoServiceOrder;
//import com.flexwm.shared.op.BmoServiceOrderReportTime;
//import com.symgae.server.PmConn;
//import com.symgae.server.PmJoin;
//import com.symgae.server.PmObject;
//import com.symgae.server.SFServerUtil;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.SFParams;
//import com.symgae.shared.SFPmException;
//
//
//public class PmServiceOrderReportTime extends PmObject {
//
//	BmoServiceOrderReportTime bmoServiceOrderReportTime;
//
//	public PmServiceOrderReportTime(SFParams sfParams) throws SFPmException {
//		super(sfParams);
//		bmoServiceOrderReportTime = new BmoServiceOrderReportTime();
//		setBmObject(bmoServiceOrderReportTime);
//
//		// Lista de joins
//		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
//				new PmJoin(bmoServiceOrderReportTime.getServiceOrderId(), bmoServiceOrderReportTime.getBmoServiceOrder()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getUserId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoUser()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoUser().getAreaId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoUser().getBmoArea()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoUser().getLocationId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoUser().getBmoLocation()),
////				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getRfquId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoRFQU()),
////				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoRFQU().getOrderTypeId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoRFQU().getBmoOrderType()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getProjectActivityId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getProfileId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProfile()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getStepProjectId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getCustomerId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoCustomer()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getTerritoryId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getBmoTerritory()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getReqPayTypeId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoCustomer().getBmoReqPayType()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getWFlowId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoWFlow()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getWFlowTypeId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowType()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getWFlowPhaseId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowPhase()),
//				new PmJoin(bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getWFlowFunnelId(), bmoServiceOrderReportTime.getBmoServiceOrder().getBmoProjectActivities().getBmoProjectStep().getBmoWFlow().getBmoWFlowFunnel())
//
//				)));
//	}
//
//	@Override
//	public BmObject populate(PmConn pmConn) throws SFException {
//		bmoServiceOrderReportTime = (BmoServiceOrderReportTime) autoPopulate(pmConn, new BmoServiceOrderReportTime());
//
//		// BmoServiceOrder
//		BmoServiceOrder bmoServiceOrder = new BmoServiceOrder();
//		if (pmConn.getInt(bmoServiceOrder.getIdFieldName()) > 0)
//			bmoServiceOrderReportTime.setBmoServiceOrder((BmoServiceOrder) new PmServiceOrder(getSFParams()).populate(pmConn));
//		else
//			bmoServiceOrderReportTime.setBmoServiceOrder(bmoServiceOrder);
//
//		return bmoServiceOrderReportTime;
//	}
//
//	@Override
//	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
//		BmoServiceOrderReportTime bmoServiceOrderReportTime = (BmoServiceOrderReportTime)bmObject;
//
//		if (bmoServiceOrderReportTime.getComments().toString().equals(""))
//			bmUpdateResult.addError(bmoServiceOrderReportTime.getComments().getName(), "<b>Debe añadir un comentario.</b>");
//	}
//
//	@Override
//	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		BmoServiceOrderReportTime bmoServiceOrderReportTime = (BmoServiceOrderReportTime)bmObject;
//
//		bmoServiceOrderReportTime.getIdField().setValue(0);
//
//		printDevLog("Tipo de reporte: "+bmoServiceOrderReportTime.getType());
//		// Obtener SRO
//		BmoServiceOrder bmoServiceOrder = new BmoServiceOrder();
//		PmServiceOrder pmServiceOrder = new PmServiceOrder(getSFParams());
//		bmoServiceOrder = (BmoServiceOrder)pmServiceOrder.get(pmConn, bmoServiceOrderReportTime.getServiceOrderId().toInteger());
//		printDevLog("Pasado: Hora reales del SRO: "+ bmoServiceOrder.getRealTime().toDouble());
//
//		// Obtener el penultimo Reporte(registro)
//		BmoServiceOrderReportTime lastBmoServiceOrderReportTime = new BmoServiceOrderReportTime();
//		lastBmoServiceOrderReportTime = getLastServiceOrderReportTime(pmConn, bmoServiceOrderReportTime.getServiceOrderId().toInteger(), "");
//
//		double realTimeSRO = 0;
//		realTimeSRO = bmoServiceOrder.getRealTime().toDouble();
//		bmoServiceOrderReportTime.getDateAndTime().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
//
//		// 
//		if (!(bmoServiceOrderReportTime.getId() > 0)) {
//			// Calcular Horas Reales 
//			if (bmoServiceOrderReportTime.getType().equals("" + BmoServiceOrderReportTime.TYPE_STOP)) {
//				printDevLog("fecha/hora anterior: " + lastBmoServiceOrderReportTime.getDateAndTime().toString());
//				printDevLog("fecha/hora actual: " + bmoServiceOrderReportTime.getDateAndTime().toString());
//
//				Date nowDate, lastDate;
//				try {
//					nowDate = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(bmoServiceOrderReportTime.getDateAndTime().toString());
//					Calendar calNow = Calendar.getInstance();
//					calNow.setTime(nowDate);
//					String a = SFServerUtil.dateToString(calNow.getTime(), "HH:mm:ss");
//					printDevLog("hora actual: "+a);
//
//					lastDate = new SimpleDateFormat(getSFParams().getDateTimeFormat()).parse(lastBmoServiceOrderReportTime.getDateAndTime().toString());
//					Calendar calLast = Calendar.getInstance();
//					calLast.setTime(lastDate);
//					String p = SFServerUtil.dateToString(calLast.getTime(), "HH:mm:ss");
//					printDevLog("hora anterior: "+p);
//
////					printDevLog("MM-i: "+nowDate.getTime());
////					printDevLog("MM-D: "+lastDate.getTime());
//
//					long mm = nowDate.getTime() - lastDate.getTime();
////					printDevLog("MM: "+mm);
//
//					// Ejemplo de conversion de mm a min
//					//					TimeUnit timeMin = TimeUnit.MINUTES;
//					//			        long diffmm = timeMin.convert(mm, TimeUnit.MILLISECONDS);
//					//					printDevLog("Conversion de mm a min: "+ diffmm);
//
//					// Conversion a horas
//					double diffHrs = FlexUtil.convertTimeUnit(mm, TimeUnit.MILLISECONDS, TimeUnit.HOURS);
//					diffHrs = Double.parseDouble(SFServerUtil.roundCurrencyDecimals((diffHrs)));
//					printDevLog("diferencia en Hrs: "+ diffHrs);
//
//					// Asignar horas reales en detenido
//					bmoServiceOrderReportTime.getRealTime().setValue(diffHrs);
//					//printDevLog("Hora real de la sro: "+bmoServiceOrder.getRealTime().toDouble());
//
//					// Sumar horas del reporte de horas al SRO
//					realTimeSRO = Double.parseDouble(SFServerUtil.roundCurrencyDecimals((diffHrs + bmoServiceOrder.getRealTime().toDouble())));
//					printDevLog("Actual: Total de SRO: "+ bmoServiceOrder.getRealTime().toDouble());
//					printDevLog("Total de sum horas: "+ realTimeSRO);
//
//				} catch (ParseException e) {
//					bmUpdateResult.addMsg("Error de Formato: " + e.toString());
//				}
//			} else if (bmoServiceOrderReportTime.getType().equals("" + BmoServiceOrderReportTime.TYPE_MANUAL)) {
//				// Si fue manual actualizar horas reales en SRO. Redondear
//				bmoServiceOrderReportTime.getRealTime().setValue(SFServerUtil.roundCurrencyDecimals(bmoServiceOrderReportTime.getRealTime().toDouble()));
//				realTimeSRO = bmoServiceOrderReportTime.getRealTime().toDouble();
//			} else if (bmoServiceOrderReportTime.getType().equals("" + BmoServiceOrderReportTime.TYPE_START)) {
////				printDevLog("ES START:"+bmoServiceOrderReportTime.getId());
//				// Si ya hay registros y Si solo esta actualizando el comentario y es Tipo Start, tomar la fecha del registro anterior
//				if (bmoServiceOrder.getHasReportTime().toBoolean()
//						&& lastBmoServiceOrderReportTime.getType().equals("" + BmoServiceOrderReportTime.TYPE_START)) {
////					printDevLog("EL ANTERIOR ES START:"+lastBmoServiceOrderReportTime.getId());
//					bmoServiceOrderReportTime.getDateAndTime().setValue(lastBmoServiceOrderReportTime.getDateAndTime().toString());
//				}
//			}
//			
//		}
//
//		// Guardar datos
//		if (!bmUpdateResult.hasErrors()) {
//			super.save(pmConn, bmoServiceOrderReportTime, bmUpdateResult);
//			// Actualizar registro hijo
//			updateChildServiceOrderReportTime(pmConn, lastBmoServiceOrderReportTime.getId(), bmoServiceOrderReportTime.getId(), bmoServiceOrder.getId());
//
//			// Actualizar el SRO si tiene registros de reportes de horas
//			pmServiceOrder.updateHasReportTime(pmConn, bmoServiceOrder.getId());
//
//			// Actualizar horas reales en SRO
//			pmServiceOrder.updateRealTime(pmConn, realTimeSRO, bmoServiceOrder.getId());
//
//			super.save(pmConn, bmoServiceOrderReportTime, bmUpdateResult);
//		}
//		return bmUpdateResult;
//	}
//
//	// Obtiene el tipo de registro anterior(por tipo)
//	private BmoServiceOrderReportTime getLastServiceOrderReportTime(PmConn pmConn, int serviceOrderId, String type) throws SFException {
//		BmoServiceOrderReportTime lastServiceOrderReportTime = new BmoServiceOrderReportTime();
//
//		String sql = " SELECT srrt_serviceorderreporttimeid FROM " + formatKind("serviceorderreporttimes")
//		+ " WHERE srrt_serviceorderid = " + serviceOrderId;
//		if (!type.equals(""))
//			sql += " AND srrt_type = '" + type + "'";
//		sql += " ORDER BY srrt_serviceorderreporttimeid DESC ";
//		
////		printDevLog("****getLastServiceOrderReportTime: "+sql);
//
//		pmConn.doFetch(sql);
//		if (pmConn.next()) {
//			PmServiceOrderReportTime pmServiceOrderReportTime = new PmServiceOrderReportTime(getSFParams());
//			lastServiceOrderReportTime = (BmoServiceOrderReportTime)pmServiceOrderReportTime.get(pmConn, pmConn.getInt("srrt_serviceorderreporttimeid"));
//		}
//
//		return lastServiceOrderReportTime;
//	}
//
//	// Obtiene el tipo de registro anterior
//	//	private BmoServiceOrderReportTime getLastServiceOrderReportTime(PmConn pmConn, int serviceOrderId) throws SFException {
//	//		BmoServiceOrderReportTime lastServiceOrderReportTime = new BmoServiceOrderReportTime();
//	//
//	//		String sql = " SELECT srrt_serviceorderreporttimeid FROM " + formatKind("serviceorderreporttimes")
//	//				+ " WHERE srrt_serviceorderid = " + serviceOrderId
//	//				+ " ORDER BY srrt_serviceorderreporttimeid DESC ";
//	//
//	//		pmConn.doFetch(sql);
//	//		if (pmConn.next()) {
//	//			PmServiceOrderReportTime pmServiceOrderReportTime = new PmServiceOrderReportTime(getSFParams());
//	//			lastServiceOrderReportTime = (BmoServiceOrderReportTime)pmServiceOrderReportTime.get(pmConn, pmConn.getInt("srrt_serviceorderreporttimeid"));
//	//		}
//	//
//	//		return lastServiceOrderReportTime;
//	//	}
//
//	public boolean hasServiceOrderReportTime(PmConn pmConn, int serviceOrderId) throws SFException {
//		boolean result = false;
//		pmConn.doFetch("SELECT * FROM " + formatKind("serviceorderreporttimes") + " WHERE srrt_serviceorderid = " + serviceOrderId);
//		if (pmConn.next()) result = true;
//		return result;
//	}
//
//	public int getParentServiceOrderReportTime(PmConn pmConn, int childId, int serviceOrderId) throws SFException {
//		int parentId = 0;
//		String sql = "SELECT srrt_serviceorderreporttimeid FROM " + formatKind("serviceorderreporttimes") 
//		+ " WHERE srrt_serviceorderid = " + serviceOrderId
//		+ " AND srrt_childid = " + childId;
//		//		printDevLog("getParentServiceOrderReportTime: "+sql);
//		pmConn.doFetch(sql);
//
//		if (pmConn.next()) parentId = pmConn.getInt("srrt_serviceorderreporttimeid");
//		return parentId;
//	}
//
//	// Actualizar hijo del registro padre
//	public void updateChildServiceOrderReportTime(PmConn pmConn, int parentId, int childId, int serviceOrderId) throws SFException {
//		String sql = "UPDATE " + formatKind("serviceorderreporttimes") ;
//		if (childId > 0) sql+= " SET srrt_childid = " + childId;
//		else sql += " SET srrt_childid = NULL ";
//		sql += " WHERE srrt_serviceorderid = " + serviceOrderId
//				+ " AND srrt_serviceorderreporttimeid = " + parentId;
////				printDevLog("updateChildServiceOrderReportTime: " + sql);
//		pmConn.doUpdate(sql);
//	}
//
//	// Obtener suma de horas del todos los registros
//	public double getSumReportTime(PmConn pmConn, int serviceOrderId) throws SFException {
//		double sumRealTime = 0;
//		pmConn.doFetch("SELECT SUM(srrt_realtime) AS realTime FROM " + formatKind("serviceorderreporttimes") + " WHERE srrt_serviceorderid = " + serviceOrderId);
//		if (pmConn.next()) sumRealTime = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(pmConn.getDouble("realTime")));
//		return sumRealTime;
//	}
//
//	@Override
//	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
//		// Obten registros
//		//		bmoServiceOrder = (BmoServiceOrder)this.get(bmObject.getId());
//
//		// Obtiene el ID del modulo de effecto effecto
//		if (action.equals(BmoServiceOrderReportTime.ACTION_MULTIPLEDELETE)) {
//			bmUpdateResult = multipeDelete(value, bmUpdateResult);
//		} else if (action.equals(BmoServiceOrderReportTime.ACTION_GETLASTRECORD)) {
//			printDevLog("*** ACTION_GETLASTRECORD");
//			PmConn pmConn = new PmConn(getSFParams());
//			pmConn.open();
//			bmUpdateResult.setBmObject(getLastServiceOrderReportTime(pmConn, Integer.parseInt(value), ""));
//			pmConn.close();
//		}
//		return bmUpdateResult;
//	}
//
//	// Eliminar multiples registros(No deberia poner eliminar varios)
//	private BmUpdateResult multipeDelete(String values, BmUpdateResult bmUpdateResult) throws SFException {
//		// Reporte de horas
//		PmServiceOrderReportTime pmServiceOrderReportTime = new PmServiceOrderReportTime(getSFParams());
//		BmoServiceOrderReportTime bmoServiceOrderReportTime = new BmoServiceOrderReportTime();
//
//		int serviceOrderReportTimeId = 0;
//		PmConn pmConn = new PmConn(getSFParams());
//		pmConn.open();
//
//		try {
//			pmConn.disableAutoCommit();
//
//			// Recorrer los registros seleccionados
//			StringTokenizer tabs = new StringTokenizer(values, "|");
//			while (tabs.hasMoreTokens()) {
//				serviceOrderReportTimeId = Integer.parseInt(tabs.nextToken());
//				bmoServiceOrderReportTime = (BmoServiceOrderReportTime)pmServiceOrderReportTime.get(pmConn, serviceOrderReportTimeId);
//				this.delete(pmConn, bmoServiceOrderReportTime, bmUpdateResult);
//			}
//			// Aplicar cambios
//			if(!bmUpdateResult.hasErrors()) pmConn.commit();
//		} catch (SFException e) {
//			pmConn.rollback();
//			bmUpdateResult.addMsg(this.getClass().getName() + " - multipeDelete(): Error " + bmUpdateResult.errorsToString() + " " + e.toString());
//		} finally {
//			pmConn.close();
//		}
//
//		return bmUpdateResult;
//	}
//
//	@Override
//	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		bmoServiceOrderReportTime = (BmoServiceOrderReportTime)bmObject;
////		printDevLog("TYPE: "+bmoServiceOrderReportTime.getType().toString());
//
//		// Obtener SRO
//		BmoServiceOrder bmoServiceOrder = new BmoServiceOrder();
//		PmServiceOrder pmServiceOrder = new PmServiceOrder(getSFParams());
//		bmoServiceOrder = (BmoServiceOrder)pmServiceOrder.get(pmConn, bmoServiceOrderReportTime.getServiceOrderId().toInteger());
//
//		// Validar que no se puede borrar si tiene un registro hijo
//		if (bmoServiceOrderReportTime.getChildId().toInteger() > 0)
//			bmUpdateResult.addError(bmoServiceOrderReportTime.getComments().getName(), "<b>No se puede borrar este registro, está ligado a un registro posterior.</b>");
//		else {
//			printDevLog(" SRO: Borrar la referencia del registro hijo del registro padre");
//			// Borrar el ID hijo en el registro padre
//			updateChildServiceOrderReportTime(
//					pmConn, 
//					getParentServiceOrderReportTime(pmConn, bmoServiceOrderReportTime.getId(), bmoServiceOrder.getId()), 
//					-1,
//					bmoServiceOrder.getId());
//		}
//		// Eliminar Reporte de horas
//		super.delete(pmConn, bmoServiceOrderReportTime, bmUpdateResult);
//
//		// Obtener la hora real del registro
//		double realTimeSRO = 0, realTimeRPT = 0;
//		if (bmoServiceOrderReportTime.getType().equals("" + BmoServiceOrderReportTime.TYPE_STOP)) {
//			realTimeRPT = bmoServiceOrderReportTime.getRealTime().toDouble();
//			realTimeSRO = bmoServiceOrder.getRealTime().toDouble() - realTimeRPT;
//		} else if (bmoServiceOrderReportTime.getType().equals("" + BmoServiceOrderReportTime.TYPE_MANUAL)) {
//			BmoServiceOrderReportTime bmoLastServiceOrderReportTime = (BmoServiceOrderReportTime)bmObject;
//
//			// Obtener ultimo registro
//			bmoLastServiceOrderReportTime = getLastServiceOrderReportTime(pmConn, bmoServiceOrder.getId(), "");
//			// Si es de tipo MANUAL cambiar las horas reales del registro al SRO
//			if (bmoLastServiceOrderReportTime.getType().equals(BmoServiceOrderReportTime.TYPE_MANUAL)) {
//				realTimeSRO += bmoLastServiceOrderReportTime.getRealTime().toDouble();
//			} else {
//				// Si eres detenido buscar el ultimo manual
//				if (bmoLastServiceOrderReportTime.getType().equals(BmoServiceOrderReportTime.TYPE_STOP)) {
//					// Si existe un manual anterior tomar la hora real de ese registro y sumar la hora real del detenido
//					BmoServiceOrderReportTime bmoLastTypeManual = getLastServiceOrderReportTime(pmConn, bmoServiceOrder.getId(), "" + BmoServiceOrderReportTime.TYPE_MANUAL);
//					if (bmoLastTypeManual.getId() > 0)
//						realTimeSRO = bmoLastTypeManual.getRealTime().toDouble() + bmoLastServiceOrderReportTime.getRealTime().toDouble() ;
//					else 
//						realTimeSRO += getSumReportTime(pmConn, bmoServiceOrder.getId());
//				}
//			}
//		} else {
//			realTimeSRO = bmoServiceOrder.getRealTime().toDouble();
//		}
//		
//		// Ultimo redondeo
//		realTimeSRO = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(realTimeSRO));
//
//		// Actualizar si tiene reportes
//		pmServiceOrder.updateHasReportTime(pmConn, bmoServiceOrder.getId());
//		// Actualizar horas reales en SRO
//		pmServiceOrder.updateRealTime(pmConn, realTimeSRO, bmoServiceOrder.getId());
//
//		return bmUpdateResult;
//	}
//}
