<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Javier Alberto Hernandez
 * @version 2013-10
 */ -->

<%@page import="com.flexwm.shared.wf.BmoWFlowStep"%>
<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlow"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowUser"%>
<%@page import="com.flexwm.server.wf.PmWFlowUser"%>
<%@page import="com.flexwm.server.wf.PmWFlowStep"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.shared.SFException"%>
<%@include file="../inc/login.jsp"%>

<html>
<%
	// Imprimir
	String print = "0";
	if ((String) request.getParameter("print") != null)
		print = (String) request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String) request.getParameter("exportexcel") != null)
		exportExcel = (String) request.getParameter("exportexcel");
	if (exportExcel.equals("1")) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; username=symgf_report.xls");
	}
	int idStart = 0, idEnd = 0;
	if (request.getParameter("startwflowtypeid") != null)
		idStart = Integer.parseInt(request.getParameter("startwflowtypeid"));
	if (request.getParameter("endwflowtypeid") != null)
		idEnd = Integer.parseInt(request.getParameter("endwflowtypeid"));
%>

<%
	// Inicializar variables
	String title = "Proceso Pasar Funnel de Tareas al Flujo(por tipo de flujo)";
%>

<head>
<title>:::<%=appTitle%>:::
</title>
<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%=defaultCss%>">
</head>

<body class="default">

	<table border="0"  width="100%">
		<tr>
			<td align="left" rowspan="2" valign="top"><img border="0"
				width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>"
				src="<%=sFParams.getMainImageUrl()%>"></td>
			<td class="reportTitle" align="left"><%=title%></td>
		</tr>
		<tr>
			<td class="reportDate" align="right">Creado: <%=SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat())%>
				por: <%=sFParams.getLoginInfo().getEmailAddress()%>
			</td>
		</tr>
	</table>

	<br>
	<table border="0"  width="100%" style="font-size: 12px">
		<TR>
			<td>
				<%
					/*
					
					-- Pasar los funnels de los Tipos de Tareas a las Tareas de los Flujos; POR TIPO DE FLUJO
					-- comparamos que el grupo, fase y la tarea sean las mismas para descartar todo lo demas:
						
					UPDATE wflowsteps
					LEFT JOIN wflows on (wflw_wflowid = wfsp_wflowid)
					LEFT JOIN wflowsteptypes on(wfst_wflowtypeid = wflw_wflowtypeid)
					SET wfsp_wflowfunnelid = wfst_wflowfunnelid
					WHERE wflw_wflowtypeid = 12
					AND wfsp_profileid = wfst_profileid
					AND wfsp_wflowphaseid = wfst_wflowphaseid
					AND wfsp_sequence = wfst_sequence
					;
					*/

					BmUpdateResult bmUpdateResult = new BmUpdateResult();
					PmConn pmConn = new PmConn(sFParams);

					PmWFlowStep pmWFlowStep = new PmWFlowStep(sFParams);
					BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();

					BmoWFlow bmoWFlow = new BmoWFlow();
					PmWFlow pmWFlow = new PmWFlow(sFParams);

					try {
						pmConn.open();
						pmConn.disableAutoCommit();

						ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
						BmFilter filterA = new BmFilter();
// 						BmFilter filterB = new BmFilter();

						filterA.setValueOperatorFilter(bmoWFlow.getKind(), bmoWFlow.getWFlowTypeId(), "" + BmFilter.EQUALS, idStart);
						filterList.add(filterA);

// 						filterB.setValueOperatorFilter(bmoWFlow.getKind(), bmoWFlow.getWFlowTypeId(), "" + BmFilter.MINOREQUAL, "" + idEnd);
// 						filterList.add(filterB);

						Iterator<BmObject> flowList = pmWFlow.list(filterList).iterator();
						int i = 0;
						while (flowList.hasNext()) {
							
							bmoWFlow = (BmoWFlow) flowList.next();

							//if (!(bmoWFlow.getWFlowFunnelId().toInteger() > 0)) {
								i++;
								%> 
								<br> -------------------------------------<br> 
								<%=i%>
								- <%=bmoWFlow.getCode().toString()%>: <br> <%
				 				ArrayList<BmFilter> filterStepList = new ArrayList<BmFilter>();
				 				BmFilter filterC = new BmFilter();
				 				BmFilter filterWFlow = new BmFilter();
				
				 				// Tareas de flujo que estan al 100
				 				filterWFlow.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getWFlowId(), bmoWFlow.getId());
				 				filterStepList.add(filterWFlow);
				 				filterC.setValueOperatorFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getProgress(), "" + BmFilter.EQUALS, 100);
				 				filterStepList.add(filterC);
				
				 				Iterator<BmObject> wFlowStepListIterator = pmWFlowStep.list(filterStepList).iterator();
				
				 				int funnelId = 0, lastFunnelId = 0;
				
				 				// La ultima tarea pasarle el funnel que tiene la tarea al Flujo
				 				while (wFlowStepListIterator.hasNext()) {
				 					bmoWFlowStep = (BmoWFlowStep) wFlowStepListIterator.next();
				
				 					// Asignar el ultimo funnel encontrado 
				 					if (bmoWFlowStep.getWFlowFunnelId().toInteger() > 0)
				 						lastFunnelId = bmoWFlowStep.getWFlowFunnelId().toInteger();
				
				 					bmoWFlow.getWFlowFunnelId().setValue(lastFunnelId); %> 
				 					
				 					<%=bmoWFlowStep.getBmoWFlowPhase().getSequence().toString()%>.<%=bmoWFlowStep.getSequence().toString()%>
									| <%=bmoWFlowStep.getName().toString()%> 
									| Funnel: <%=bmoWFlowStep.getBmoWFlowFunnel().getName().toString()%>(id:<%=bmoWFlowStep.getWFlowFunnelId().toString()%>) 
									<br> <%
				 				}
								%> 
								Funnel_Flujo: <%=bmoWFlow.getWFlowFunnelId().toString()%> <br>
								<%
								pmWFlow.saveSimple(bmoWFlow, bmUpdateResult);

								if (!bmUpdateResult.hasErrors())
									pmConn.commit();

							//}
						}
				%> <%=bmUpdateResult.errorsToString()%> <%
 	} catch (Exception e) {
 		throw new SFException(e.toString());
 	} finally {
 		pmConn.close();
 	}
 %>

			</td>
		</TR>

	</table>


	<%
		if (print.equals("1")) {
	%>
	<script>
		//window.print();
	</script>
	<%
		}
	%>
</body>
</html>