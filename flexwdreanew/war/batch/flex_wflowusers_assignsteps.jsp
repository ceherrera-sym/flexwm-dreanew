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
<%@include file="../inc/login.jsp" %>

<html>
<% 		
		// Imprimir
		String print = "0";
		if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");
	
		// Exportar a Excel
		String exportExcel = "0";
		if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
		if (exportExcel.equals("1")) {
			response.setContentType("application/vnd.ms-excel");
	    	response.setHeader("Content-Disposition", "inline; username=symgf_report.xls");
	    }
		int idStart = 0, idEnd = 0;
		if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
		if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));
	%>

<%
		// Inicializar variables
		String title = "Proceso Asignar a tareas Usuarios de flujo";
	%>

<head>
<title>:::<%= appTitle %>:::
</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
</head>

<body class="default">

	<table border="0" cellspacing="0" cellpading="0" width="100%">
		<tr>
			<td align="left" rowspan="2" valign="top"><img border="0"
				width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>"
				src="<%= sFParams.getMainImageUrl() %>"></td>
			<td class="reportTitle" align="left"><%= title %></td>
		</tr>
		<tr>
			<td class="reportDate" align="right">Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %>
				por: <%= sFParams.getLoginInfo().getEmailAddress() %>
			</td>
		</tr>
	</table>

	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<TR>
			<th class="reportHeaderCellCenter" align="left" width="5%">#</th>
			<th class="reportHeaderCell" align="left">Usuario</th>
			<th class="reportHeaderCell" align="left">Grupo</th>
			<th class="reportHeaderCell" align="left">Flujo</th>
		</TR>
		<%
			BmUpdateResult bmUpdateResult = new BmUpdateResult();
			PmConn pmConn = new PmConn(sFParams);
			BmUpdateResult bmUpdateResult2 = new BmUpdateResult();
			PmConn pmConn2 = new PmConn(sFParams);

			
			PmWFlowUser pmWFlowUser = new PmWFlowUser(sFParams);
			BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
			
			BmoWFlow bmoWFlow = new BmoWFlow();
			PmWFlow pmWFlow = new PmWFlow(sFParams);
			
			PmWFlowStep pmWFlowStep = new PmWFlowStep(sFParams);
		try {
				
				pmConn.open();
				pmConn.disableAutoCommit();
				
				pmConn2.open();
				pmConn2.disableAutoCommit();

			
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				BmFilter filterA = new BmFilter();
				BmFilter filterB = new BmFilter();			
				BmFilter filterC = new BmFilter();
	
				filterA.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getAssignSteps(), 0);
				filterList.add(filterA);
				
				//filterB.setValueOperatorFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getUserId(), "" + BmFilter.MAYOR, 0);
				//filterList.add(filterB);
				
				filterB.setValueOperatorFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getIdField(), "" + BmFilter.MAYOR, "" + idStart);
				filterList.add(filterB);
				
				filterC.setValueOperatorFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getIdField(), "" + BmFilter.MINOR, "" + idEnd);
				filterList.add(filterC);
				
				//filterC.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), 3912);
				//filterList.add(filterC);
				
				Iterator<BmObject> userList = pmWFlowUser.list(filterList).iterator();
				int i = 0;
				while (userList.hasNext()) {
					i++;
					
					bmoWFlowUser = (BmoWFlowUser)userList.next();

					%>
					
						<%
															
								bmoWFlowUser.getAssignSteps().setValue(1);
								pmWFlowUser.saveSimple(pmConn, bmoWFlowUser, bmUpdateResult);
								
								bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoWFlowUser.getWFlowId().toInteger());
								
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + bmoWFlow.getCode().toString(), BmFieldType.STRING) %>
	
								<%
								pmWFlowStep.updateWFlowStepUsers(pmConn, bmoWFlow, bmoWFlowUser.getProfileId().toInteger(), bmoWFlowUser.getUserId().toInteger(), bmUpdateResult);
								
								pmConn.commit();
								//pmConn2.commit();	
			%>	
							<tr>
								<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + bmoWFlowUser.getBmoUser().getCode().toString(), BmFieldType.STRING) %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + bmoWFlowUser.getBmoProfile().getName().toString(), BmFieldType.STRING) %>
							</tr>
				<%				
								//if (i == 5000) break;
				}
				
				
				%>
				<%= bmUpdateResult.errorsToString() %>
				<%				
			} catch (Exception e) {		
				throw new SFException(e.toString());			
			} finally {
					pmConn.close();
					pmConn2.close();

			}
		%>
		



	</table>


	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } %>
</body>
</html>