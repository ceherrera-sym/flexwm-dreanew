<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */ -->

<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowStep"%>
<%@page import="com.flexwm.server.wf.PmWFlowStep"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlow"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.server.op.PmRequisition"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionItem"%>
<%@page import="com.flexwm.server.op.PmRequisitionItem"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionReceipt"%>
<%@page import="com.flexwm.server.op.PmRequisitionReceipt"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionReceiptItem"%>
<%@page import="com.flexwm.server.op.PmRequisitionReceiptItem"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.server.fi.PmPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountItem"%>
<%@page import="com.flexwm.server.fi.PmPaccountItem"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.server.fi.PmPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountItem"%>
<%@page import="com.flexwm.server.fi.PmPaccountItem"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>

<%@page import="java.util.Iterator"%>

<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.BmObject"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>


<%@page import="java.util.Iterator"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.BmObject"%>

<%@include file="../inc/login.jsp" %>



<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
</head>

<body class="default">

<table border="0" cellspacing="0" cellpading="0" width="100%">
	<tr>
		<td align="left" width="80" rowspan="2" valign="top">	
			<img src="<%= sFParams.getMainImageUrl() %>">
		</td>
		<td class="reportTitle" align="left">
			&nbsp;
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
	<tr>
		<td>
			<b>Proceso Oportunidades perdidas, se llena la primera tarea y la ultima(perder): SOCIALES</b><br>
			<%			
			PmConn pmConn = new PmConn(sFParams);
			BmUpdateResult bmUpdateResult = new BmUpdateResult();
			
			PmConn pmConn2 = new PmConn(sFParams);
			
			
			try {
				pmConn.open();
				pmConn.disableAutoCommit();
				
				pmConn2.open();
				pmConn2.disableAutoCommit();
				
				
				PmWFlow pmWFlow = new PmWFlow(sFParams);
				BmoWFlow bmoWFlow = new BmoWFlow();
				
				String sql = " SELECT * FROM opportunities " +
								" LEFT JOIN wflows on(wflw_wflowid = oppo_wflowid) " +
								" LEFT JOIN wflowtypes on(wfty_wflowtypeid = oppo_wflowtypeid) " +
								" LEFT JOIN wflowsteps on(wfsp_wflowid = wflw_wflowid)" +
								" WHERE oppo_status = '" + BmoOpportunity.STATUS_LOST + "'" + 
								" AND wfsp_progress < 100 " +
								" AND wfty_code = 'OPRSOC' ";
				
				pmConn.doFetch(sql);
				System.out.println("sql: "+sql);
				int i = 1, wflowId = 0;
				
				while(pmConn.next()){
					// fase (1)OPRSOC
					if(pmConn.getString("wfsp_code").equals("PERDER") || pmConn.getInt("wfsp_wflowphaseid") == 1){
						if(pmConn.getString("wfsp_code").equals("PERDER") || pmConn.getInt("wfsp_sequence") == 1){
							
							if(wflowId != pmConn.getInt("wflw_wflowid")){
								wflowId = pmConn.getInt("wflw_wflowid");
								%>
								<br><br>
								<%= pmConn.getString("oppo_code") %> / <%= pmConn.getString("oppo_name") %><br>
								<%
							}
							%>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<%= i%>
							&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
							<%= pmConn.getString("wfsp_code") %> / <%= pmConn.getString("wfsp_name") %>
		
							<br>
							<%
							
							PmWFlowStep pmWFlowStep = new PmWFlowStep(sFParams);
							BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)pmWFlowStep.get(pmConn.getInt("wfsp_wflowstepid"));
							
							String startDate = "", endDate = "", now = "";
							
							startDate = bmoWFlowStep.getStartdate().toString();
							endDate = bmoWFlowStep.getEnddate().toString();
							now = SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat());
							
							bmoWFlowStep.getProgress().setValue(100);
							bmoWFlowStep.getEnabled().setValue(1);
							if(startDate.equals("")){
									bmoWFlowStep.getStartdate().setValue(now);
									bmoWFlowStep.getEnddate().setValue(now);
							}else bmoWFlowStep.getEnddate().setValue(startDate);
		
							System.out.println("antes de saveSimple");
							pmWFlowStep.saveSimple(pmConn2, bmoWFlowStep, bmUpdateResult);
							System.out.println("despues de saveSimple");
						
							//--------------------------------------------ACTUALIZAR EL FLUJO------------------------------------------------
							// Obtener el flujo
							bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn.getInt("wfsp_wflowid"));
							//pmWFlow.calculate(pmConn, bmoWFlow, bmUpdateResult);
							
							// Obtener tareas creadas
							BmoWFlowStep bmoWFlowStep2 = new BmoWFlowStep();
							PmWFlowStep pmWFlowStep2 = new PmWFlowStep(sFParams);
							BmFilter filterWFlow = new BmFilter();
							filterWFlow.setValueFilter(bmoWFlowStep2.getKind(), bmoWFlowStep2.getWFlowId(), bmoWFlow.getId());
							ArrayList<BmObject> wFlowStepList = pmWFlowStep2.list(pmConn2, filterWFlow);
							Iterator<BmObject> wFlowStepListIterator = wFlowStepList.iterator();
							
							int wFlowPhaseId = 0, lastWFlowPhaseId = 0, countSteps = wFlowStepList.size(), finished = 0;
		
							while (wFlowStepListIterator.hasNext()) {
								bmoWFlowStep2 = (BmoWFlowStep)wFlowStepListIterator.next();
								boolean wasEnabled = bmoWFlowStep2.getEnabled().toBoolean();
		
								if (bmoWFlowStep2.getProgress().toInteger() == 100) finished++;
		
								// Si el paso se puede habilitar, asignarlo como habilitado
								if (pmWFlowStep2.shallEnable(pmConn2, bmoWFlow.getId(), bmoWFlowStep2.getId())) {
		
									bmoWFlowStep2.getEnabled().setValue(true);
		
									// Si no esta asignada la fase, asignarla
									if (bmoWFlowStep2.getProgress().toInteger() < 100 && wFlowPhaseId == 0) 
										wFlowPhaseId = bmoWFlowStep2.getWFlowPhaseId().toInteger();
		
									// Asignar la ultima fase encontrada 
									lastWFlowPhaseId = bmoWFlowStep2.getWFlowPhaseId().toInteger();
		
									// Acciones de avisar al responsable, no estaba activo y ahora si
									if (!wasEnabled) {
										//pmWFlowStep2.sendMailNotification(pmConn2, bmoWFlowStep2);
										
										bmoWFlowStep2.getStartdate().setValue(SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()));
		
										// Establecer la fecha de recordatorio
										bmoWFlowStep2.getRemindDate().setValue(SFServerUtil.addDays(sFParams.getDateFormat(), 
												bmoWFlowStep2.getStartdate().toString(), bmoWFlowStep2.getDaysRemind().toInteger()));
									}
								} else {
									// Deshabilitar si el paso no debe ser habilitado
									bmoWFlowStep2.getEnabled().setValue(false);
								}
		
								pmWFlowStep2.saveSimple(pmConn2, bmoWFlowStep2, bmUpdateResult);
							}
		
							// Actualiza la informacion del avance y de fase del wflow con el wFlowId
							if (wFlowPhaseId == 0) 
								wFlowPhaseId = lastWFlowPhaseId;
							if (wFlowPhaseId > 0)
								bmoWFlow.getWFlowPhaseId().setValue(wFlowPhaseId);
							int progress = 0;
							if (countSteps > 0) progress = ((finished * 100 / countSteps));
							else progress = 100;
							bmoWFlow.getProgress().setValue(progress);
		
							pmWFlow.saveSimple(pmConn2, bmoWFlow, bmUpdateResult);
							
						 i++;	
						} // Fin de fase.paso(1.1)
					} //Fin de -if:paso es igual a PERDER
				} // Fin de -while
				pmConn.commit();
				pmConn2.commit();
				%>
				<br>
				<b>Proceso Terminado</b>
			</td>
		
		</tr>
	</table>
		<%
				
	
}catch (Exception e) {
	pmConn.rollback();
	pmConn2.rollback();
	throw new SFException("Proceso Item en conceptos: "+e.toString());
}finally {
	pmConn.close();
	pmConn2.close();
}
%>
		


  </body>
</html>