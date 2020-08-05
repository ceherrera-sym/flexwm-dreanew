<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.server.cv.PmMeeting"%>
<%@page import="com.flexwm.shared.cv.BmoMeeting"%>
<%@page import="com.flexwm.server.wf.PmWFlowUser"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowUser"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>
<%
try {
	String title = "Minuta";
	
	BmoMeeting bmoMeeting = new BmoMeeting();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoMeeting.getProgramCode());	
	
%>

<html>
		<% 
		// Imprimir
		String print = "0", permissionPrint = "";
		if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");
		
		// Exportar a Excel
		String exportExcel = "0";
		if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
		if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "inline; filename=\""+title+".xls\"");
		}
		
		//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
		//En caso de que mande a imprimir, deshabilita contenido
		if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
		<style> 
			body{
				user-select: none;
				-moz-user-select: none; 
				-o-user-select: none; 
				-webkit-user-select: none; 
				-ie-user-select: none; 
				-khtml-user-select:none; 
				-ms-user-select:none; 
				-webkit-touch-callout:none
			}
		</style>
		<style type="text/css" media="print">
		    * { display: none; }
		</style>
		<%
		permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
			//Mensaje 
			if(print.equals("1") || exportExcel.equals("1")) { %>
				<script>
					alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
				</script>
			<% }
		}
		 
		%>
			<head>
			<title>:::<%= title %>:::</title>
			<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
			 <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
			</head>
	<body class="default" <%= permissionPrint %>>
		<%
	
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		
	    // Obtener parametros
	    int meetingId = 0;
	    if (isExternal) meetingId = meetingId;
	    else meetingId = Integer.parseInt(request.getParameter("foreignId"));  
	    	
		PmMeeting  pmMeeting = new PmMeeting(sFParams);
		bmoMeeting = (BmoMeeting)pmMeeting.get(meetingId);
		
		%>
			</style>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<tr>
					<td align="left" width="5%" rowspan="6" valign="top">
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
					</td>
					<td class="reportTitleCell" width="90%" align="center" colspan="3">
						<%= title%>: <%= bmoMeeting.getCode().toHtml()%>
					</td>
					<td class="reportTitleCell" width="5%" align="right" style ="text-align: right;">
						Secci&oacute;n 05.5
					</td>
				</tr>
				<tr>       
					<th class="reportCellEven" align = "left" width="10%">
						&nbsp;Nombre:
					</th>
					<td class="reportCellEven" width="40%">
						&nbsp;<%= bmoMeeting.getName().toHtml()%>
					</td>
					<th class="reportCellEven" align = "left"width="10%">
						&nbsp;Objetivo: 
					</th>
					<td class="reportCellEven"width="40%">
						&nbsp;Seguimiento y control de los procesos operativos de la empresa.
					</td>
				</tr>
				<tr>
					<th  class="reportCellEven" align = "left">
						&nbsp;Descripci&oacute;n:
					</th>
					<td  class="reportCellEven" colspan="3">
						&nbsp;<%= bmoMeeting.getDescription().toHtml()%>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align = "left">
						&nbsp;Fecha: 
					</th>
					<td class="reportCellEven" >
						&nbsp;<%= SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()) %>
					</td>
					<th  class="reportCellEven" align = "left">
						&nbsp;Consecutivo: 
					</th>
					<td class="reportCellEven" >
						&nbsp;
					</td>
				</tr>
				<tr>
					<th  class="reportCellEven" align = "left">
						&nbsp;Hora de Inicio: 
					</th>
					<td class="reportCellEven" >
						&nbsp;<%= bmoMeeting.getStartdate().toHtml()%>
					</td>
					<th  class="reportCellEven" align = "left">
						&nbsp;Hora de Terminaci&oacute;n:
					</th>
					<td class="reportCellEven" >
						&nbsp;<%= bmoMeeting.getEnddate().toHtml()%>
					</td>
				</tr>
			</table>
			<br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<caption class="documentText"><b>Lista de Asistencia</b></caption>
				<tr>
					<td class="reportHeaderCell" width="50%">
						Nombre
					</td>
					<td class="reportHeaderCell" width="50%">
						Firma
					</td>
				</tr>
				<%
				BmoWFlowUser bmoWFlowUserList = new BmoWFlowUser();
				PmWFlowUser pmWFlowUserList = new PmWFlowUser(sFParams);
				BmFilter bmFilterUsersList = new BmFilter();
				bmFilterUsersList.setValueFilter(bmoWFlowUserList.getKind(), bmoWFlowUserList.getWFlowId().getName(), bmoMeeting.getWFlowId().toInteger());
				Iterator<BmObject> usersList = pmWFlowUserList.list(bmFilterUsersList).iterator();
				while(usersList.hasNext()) {
					bmoWFlowUserList = (BmoWFlowUser)usersList.next();	
					%>      
					<tr>	      
						<td class="reportCellEven"> 
							&nbsp;
							<% if(bmoWFlowUserList.getUserId().toInteger() > 0){ %>
								<%= bmoWFlowUserList.getBmoUser().getFirstname().toHtml() %>
								<%= bmoWFlowUserList.getBmoUser().getFatherlastname().toHtml() %>
								<%= bmoWFlowUserList.getBmoUser().getMotherlastname().toHtml() %>
							<% }else { %>
								<%= bmoWFlowUserList.getBmoProfile().getName().toHtml() %>
							<% } %>
						</td>
						<td class="reportCellEven"> 
							&nbsp;
						</td>
					<tr>
				<%	
					
				}
				%>	
			</table>
			<br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<tr>
					<td class="reportHeaderCell">
						Acuerdo/Compromiso
					</td>
					<td class="reportHeaderCell" width="20%">
						Responsable
					</td>
					<td class="reportHeaderCell" width="10%">
						Fecha Comp.
					</td>
					<td class="reportHeaderCell" width="5%">
						Efectividad
					</td>
				</tr>
			<%
			int i = 1;
			BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
			PmWFlowUser pmWFlowUser = new PmWFlowUser(sFParams);
			BmFilter bmFilterUsers = new BmFilter();
			bmFilterUsers.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId().getName(), bmoMeeting.getWFlowId().toInteger());
			Iterator<BmObject> users = pmWFlowUser.list(bmFilterUsers).iterator();
			while(users.hasNext()) {
				bmoWFlowUser = (BmoWFlowUser)users.next();	
				%>      
				<tr>	      
					<td class="reportCellEven" > 
						&nbsp;
						<b><%= bmoWFlowUser.getBmoProfile().getName().toHtml() %></b>
						
					</td>
					<td class="reportCellEven"> 
						&nbsp;
						<%= bmoWFlowUser.getBmoUser().getFirstname().toHtml() %>
						<%= bmoWFlowUser.getBmoUser().getFatherlastname().toHtml() %>
						<%= bmoWFlowUser.getBmoUser().getMotherlastname().toHtml() %>
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
				<tr>
				<tr>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>					
				</tr>
				<tr>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>					
				</tr>
				<tr>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>					
				</tr>
			<%	
				
			}
			%>	
		</table>
			<div class="documentComments" align="right">FO-05.5.3-2 (30-Jun-10)</div>
			<%
		} catch (Exception e) { 
				String errorLabel = "Error de Formato";
				String errorText = "La Minuta de Junta de Gobierno no puede ser desplegada.";
				String errorException = e.toString();		
		%>
			<%= errorException %>
		<%
			}
		%>
	</body>
</html>