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

<%@page import="com.flexwm.shared.wf.BmoWFlowCategory"%>
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
	String print = "0", sql = "";
	BmUpdateResult bmUpdateResult = new BmUpdateResult();


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

	int programId = 0;
	String stepName = "", programCode = "", programName = "";
	if (request.getParameter("programCode") != null)
		programCode = request.getParameter("programCode");
	if (request.getParameter("stepName") != null)
		stepName = request.getParameter("stepName");
	
	if (programCode.equals("") || stepName.equals("")) {
		bmUpdateResult.addMsg("No existen parametros: Clave del Módulo(programCode) y Nombre de la Tarea(stepName) <br>" +
			" ej: En la URL: miApp.flexwm.com/batch/flexwm_wflowsteps_delete.jsp<b>?programCode=OPPO&stepName=Perder Oportunidad</b>");
	}

	PmConn pmConnSql = new PmConn(sFParams);
	pmConnSql.open();
	sql = "SELECT prog_name, prog_programid FROM programs WHERE prog_code = '" + programCode + "'";
	pmConnSql.doFetch(sql);
	if (pmConnSql.next()) {
		programId = pmConnSql.getInt("prog_programid");
		programName = pmConnSql.getString("prog_name");
	}

%>

<%
	// Inicializar variables
	String title = "Proceso de Eliminar las tareas '"+ stepName+ "' del Programa " + programName+"";
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
					PmConn pmConn = new PmConn(sFParams);
					
					PmWFlowStep pmWFlowStep = new PmWFlowStep(sFParams);
					BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();

					BmoWFlow bmoWFlow = new BmoWFlow();
					PmWFlow pmWFlow = new PmWFlow(sFParams);
					
					BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
					

					try {
						
						pmConn.open();
						pmConn.disableAutoCommit();

						ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
						BmFilter filterA = new BmFilter();
						BmFilter filterB = new BmFilter();

						// ID del Modulo
						filterA.setValueOperatorFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), "" + BmFilter.EQUALS, programId);
						filterList.add(filterA);

						filterB.setValueOperatorFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getName(), "LIKE", "" + stepName);
						filterList.add(filterB);

						Iterator<BmObject> flowStepsList = pmWFlowStep.list(filterList).iterator();
						int i = 0, finished = 0;
						while (flowStepsList.hasNext()) {

							bmoWFlowStep = (BmoWFlowStep) flowStepsList.next();
								i++;
								%> 
								<br>
								<!-- Mostrar cuales se borraron -->
								<%=i%>
								- 
								<%= bmoWFlowStep.getBmoWFlow().getCode().toString()%>
								<%= bmoWFlowStep.getBmoWFlow().getName().toString()%> 
								(<%= bmoWFlowStep.getBmoWFlow().getStatus().getSelectedOption().getLabel() %>)<br>
								Fase: (<%= bmoWFlowStep.getBmoWFlowPhase().getSequence().toString()%>)
								<%= bmoWFlowStep.getBmoWFlowPhase().getName().toString()%> <br>
								Tarea: (<%= bmoWFlowStep.getSequence().toString()%>)
								<%= bmoWFlowStep.getName().toString()%><br> 
								<%
																
								pmWFlowStep.delete(pmConn, bmoWFlowStep, bmUpdateResult);
						}
						
						
						if (!bmUpdateResult.hasErrors())
						pmConn.commit();
				%> <%=bmUpdateResult.errorsToString()%> <%
 	} catch (Exception e) {
 		throw new SFException(e.toString());
 	} finally {
 		pmConn.close();
 		pmConnSql.close();
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