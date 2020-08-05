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
    	response.setHeader("Content-Disposition", "inline; username=symgf_report.xls");
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
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
		PmProfileUser pmProfileUser = new PmProfileUser(sFParams);
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
	
		PmWFlowUser pmWFlowUser = new PmWFlowUser(sFParams);
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getBmoProfile().getName(), "TEMP");
		
		Iterator userList = pmWFlowUser.list(bmFilter).iterator();
		int i = 0;
		while (userList.hasNext()) {
			i++;
			%>
			<tr>
				<td>
				<%= i %>
				</td>
				<td>
			<%		
		
			bmoWFlowUser = (BmoWFlowUser)userList.next();
			
				try {
					
					pmConn.doFetch("SELECT prof_profileid, prof_name FROM programprofiles  " + 
								" LEFT JOIN profiles ON (pfus_profileid = prof_profileid) " +
								" WHERE pfus_userid = " + bmoWFlowUser.getUserId().toInteger() + 
								" AND prof_profileid != 14 " 
								);
					if (pmConn.next()) {
						int profileId = pmConn.getInt("prof_profileid");
						
						if (profileId == 10 || profileId == 21)
							bmoWFlowUser.getAutoDate().setValue("0");
						else 
							bmoWFlowUser.getAutoDate().setValue("1");
							
						bmoWFlowUser.getLockStatus().setValue(BmoWFlowUser.LOCKSTATUS_OPEN);
						bmoWFlowUser.getRequired().setValue("0");
					
						bmoWFlowUser.getProfileId().setValue(profileId);
						BmUpdateResult bmUpdateResult = new BmUpdateResult();
						
						pmWFlowUser.save(bmoWFlowUser, bmUpdateResult);
						
						%>
							Usuario: <%= bmoWFlowUser.getBmoUser().getCode().toString() %> ----
							<%= pmConn.getString("prof_name") %> -----  AutoDate: <%= bmoWFlowUser.getAutoDate().toString() %>  ------ ; Resultado update: <%= bmUpdateResult.errorsToString() %><br>
						<%
					
					}
				} catch (Exception e) {
					%>
					<%= e.toString() %><br>
					<%	
				}	
				finally {
					pmConn.close();
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