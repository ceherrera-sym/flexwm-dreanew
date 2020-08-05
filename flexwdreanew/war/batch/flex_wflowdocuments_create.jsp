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
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.symgae.shared.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@include file="../inc/login_opt.jsp" %>

<% 
	// Imprimir
	String print = "0";
	if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
	if (exportExcel.equals("1")) {
		response.setContentType("application/vnd.ms-excel");
    	response.setHeader("Content-Disposition", "inline; filename=symgf_report.xls");
    }
%>

<%
	// Inicializar variables
	String title = "Procesos Symetria";
%>

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
			<%= title %>
		</td>
	</tr>
	<tr>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>



<table class="report" border="1">
	<TR>
		<TD colspan="2">Inicia Proceso</TD>
	</TR>
	<%
		PmProgram pmProgram = new PmProgram(sFParams);
		BmoProgram bmoProgram = new BmoProgram();
		
		PmProject pmProject = new PmProject(sFParams);
		BmoProject bmoProject = new BmoProject();
		
		PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		
		PmWFlowDocument pmWFlowDocument = new PmWFlowDocument(sFParams);
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
	
		PmFile pmFile = new PmFile(sFParams);
		BmoFile bmoFile = new BmoFile();
		Iterator fileList = pmFile.list().iterator();
		int i = 0;
		while (fileList.hasNext()) {
			i++;
			%>
			<tr>
				<td>
				<%= i %>
				</td>
				<td>
			<%		
		
			bmoFile = (BmoFile)fileList.next();
			
			bmoProgram = (BmoProgram)pmProgram.get(bmoFile.getProgramId().toInteger());
			
			if (bmoProgram.getCode().equals(bmoOpportunity.getProgramCode())) {
				try {
					bmoOpportunity = (BmoOpportunity)pmOpportunity.get(bmoFile.getForeignId().toInteger());
					
					bmoWFlowDocument = new BmoWFlowDocument();
					String name = bmoFile.getDescription().toString(29);
					if (name.equals("")) name = "Nombre Indefinido";
					bmoWFlowDocument.getName().setValue(name);
					bmoWFlowDocument.getFile().setValue(bmoFile.getBlobkey().toString());
					bmoWFlowDocument.getWFlowId().setValue(bmoOpportunity.getWFlowId().toInteger());
					bmoWFlowDocument.getIsUp().setValue(true);
					BmUpdateResult bmUpdateResult = new BmUpdateResult();
					pmWFlowDocument.save(bmoWFlowDocument, bmUpdateResult);
					
					%>
						Oportunidad: <%= bmoOpportunity.getCode().toString() %> <%= bmoOpportunity.getName().toString() %> ----
						<%= bmoFile.getDescription().toString() %>; Resultado update: <%= bmUpdateResult.errorsToString() %><br>
					<%
				} catch (Exception e) {
					%>
					<%= e.toString() %><br>
					<%	
				}	
			} else if (bmoProgram.getCode().equals(bmoProject.getProgramCode())) {
				try {
					bmoProject = (BmoProject)pmProject.get(bmoFile.getForeignId().toInteger());
					
					bmoWFlowDocument = new BmoWFlowDocument();
					String name = bmoFile.getDescription().toString(29);
					if (name.equals("")) name = "Nombre Indefinido";
					bmoWFlowDocument.getName().setValue(name);
					bmoWFlowDocument.getFile().setValue(bmoFile.getBlobkey().toString() + "." + bmoFile.getEncoding().toString());
					bmoWFlowDocument.getWFlowId().setValue(bmoProject.getWFlowId().toInteger());
					bmoWFlowDocument.getIsUp().setValue(true);
					BmUpdateResult bmUpdateResult = new BmUpdateResult();
					pmWFlowDocument.save(bmoWFlowDocument, bmUpdateResult);
					
					%>
						Oportunidad: <%= bmoProject.getCode().toString() %> <%= bmoProject.getName().toString() %> ----
						<%= bmoFile.getDescription().toString() %>; Resultado update: <%= bmUpdateResult.errorsToString() %><br>
					<%
				} catch (Exception e) {
					%>
					<%= e.toString() %><br>
					<%	
				}	
			} else {
					%>
						Archivo de otro tipo: <%= bmoProgram.getCode().toString() %>  ----
						<%= bmoFile.getDescription().toString() %>;<br>
					<%
			}
			
			%>
				</td>
			</tr>
			<%	
		}

	%>
	<TR>
		<TD colspan="2">Finaliza Proceso</TD>
	</TR>
		
</TABLE>


	<% if (print.equals("1")) { %>
	<script>
		window.print();
	</script>
	<% } %>
  </body>
</html>