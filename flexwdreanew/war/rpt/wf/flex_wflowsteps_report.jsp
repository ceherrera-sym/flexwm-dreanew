
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

<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="javax.script.*"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowStep"%>
<%@page import="com.flexwm.server.wf.PmWFlowStep"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>

<%
	// Inicializar variables
 	String title = "Reporte de Tareas de Flujo";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
   	String sql = "", where = "", startDate = "", startEndDate = "", filters = "", wflowFunnelId = "",remindateend = "", remindate = "",status = "";
   	int profileId = 0, wflowPhaseId = 0, wflowCategoryId = 0, wflowTypeId = 0, userId = 0, progress=0, programId = 0, enabled = 0;
    
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("wfsp_profileid") != null) profileId = Integer.parseInt(request.getParameter("wfsp_profileid"));
   	if (request.getParameter("wfsp_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wfsp_wflowphaseid"));
   	if (request.getParameter("wfph_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfph_wflowcategoryid"));
   	if (request.getParameter("wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflowtypeid"));
   	if (sFParams.isFieldEnabled(bmoWFlowStep.getWFlowFunnelId())) {
   		if (request.getParameter("wfsp_wflowfunnelid") != null) wflowFunnelId = request.getParameter("wfsp_wflowfunnelid");
   	}
   	if (request.getParameter("wfsp_enabled") != null) enabled = Integer.parseInt(request.getParameter("wfsp_enabled"));
   	if (request.getParameter("userid") != null) userId = Integer.parseInt(request.getParameter("userid"));
   	if (request.getParameter("wfsp_progress") != null) progress = Integer.parseInt(request.getParameter("wfsp_progress"));
   	if (request.getParameter("wfsp_startdate") != null) startDate = request.getParameter("wfsp_startdate");
   	if (request.getParameter("startenddate") != null) startEndDate = request.getParameter("startenddate");
   	if (request.getParameter("remindate") != null) remindate = request.getParameter("remindate");
	if (request.getParameter("remindateend") != null) remindateend = request.getParameter("remindateend");
	if (request.getParameter("wfsp_status") != null) status = request.getParameter("wfsp_status");
   	   	
   	if (profileId > 0) {
        where += " AND wfsp_profileid = " + profileId;
        filters += "<i>Grupo: </i>" + request.getParameter("wfsp_profileidLabel") + ", ";
    }
   	
   	if (wflowPhaseId > 0) {
        where += " AND wfsp_wflowphaseid = " + wflowPhaseId;
        filters += "<i>Fase: </i>" + request.getParameter("wfsp_wflowphaseidLabel") + ", ";
    }
   	
   	if (wflowCategoryId > 0) {
        where += " AND wfca_wflowcategoryid = " + wflowCategoryId;
        filters += "<i>Categoria: </i>" + request.getParameter("wfph_wflowcategoryidLabel") + ", ";
    }
   	
   	if (wflowTypeId > 0) {
        where += " AND wfty_wflowtypeid = " + wflowTypeId;
        filters += "<i>Tipo: </i>" + request.getParameter("wflowtypeidLabel") + ", ";
    }
   	
   	if (sFParams.isFieldEnabled(bmoWFlowStep.getWFlowFunnelId())) {
	   	if (!wflowFunnelId.equals("")) {
	    	where = SFServerUtil.parseFiltersToSql("wfsp_wflowfunnelid", wflowFunnelId);
	   		filters += "<i>Funnel: </i>" + request.getParameter("wfsp_wflowfunnelidLabel") + ", ";
	   	}
   	}

   	if (!(startDate.equals(""))) {
		where += " AND wfsp_startdate >= '" + startDate + " 00:00'";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
    
    if (!(startEndDate.equals(""))) {
		where += " AND wfsp_startdate <= '" + startEndDate + " 23:59'";
   		filters += "<i>Fecha Inicio Final: </i>" + startEndDate + ", ";
   	}
    if(!(remindate.equals(""))) {
    	where += " AND wfsp_reminddate >= '" + remindate + "'";
    	filters += "<i>Fecha Recordar Inicio: </i>" + remindate + ", ";
    }   
    if (!(remindateend.equals(""))) {
    	where += " AND wfsp_reminddate <= '" + remindateend + "'";
    	filters += "<i>Fecha Recordar Final: </i>" + remindateend + ", ";
    }
   	
   	if (userId > 0) {
        where += " AND user_userid = " + userId;
        filters += "<i>Usuario: </i>" + request.getParameter("useridLabel") + ", ";
    }
   	
   	if (enabled == 0) {
        where += " AND wfsp_enabled = " + enabled;
        filters += "<i>Activo: </i> No, ";
    } else if (enabled == 1) {
    	where += " AND wfsp_enabled = " + enabled;
    	filters += "<i>Activo: </i> Si, ";
    } else {
    	filters += "<i>Activo: </i> Todos, ";
    }
   	
   	if (progress >= 0) {
   		if (progress == 99) {
   			where += " AND wfsp_progress <= " + progress;
   			filters += "<i>Avance: </i>, < 100%";
   		}else {
   			where += " AND wfsp_progress = " + progress;
   			filters += "<i>Avance: </i>, " + progress + "%";
   		}
   		
   	} else {
   		filters += "<i>Avance: </i> Todos, ";
   	}
   	if (!(status.equals(""))){
   		where += " AND wfsp_status = '" + status + "'";
   		filters += "<i>Estatus </i>, " + request.getParameter("wfsp_statusLabel") ;
   	}
   	
 // Obtener disclosure de datos
    String disclosureFilters = new PmWFlowStep(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
   	
   	//Conexiones
   	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	sql = " SELECT COUNT(*) AS contador FROM wflowsteps " +
			  " LEFT JOIN wflows ON (wfsp_wflowid = wflw_wflowid) " +
			  " LEFT JOIN wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
			  "	LEFT JOIN wflowphases ON (wfsp_wflowphaseid = wfph_wflowphaseid) " +		  	
			  " LEFT JOIN wflowcategories ON (wfph_wflowcategoryid = wfca_wflowcategoryid) ";
			  if (sFParams.isFieldEnabled(bmoWFlowStep.getWFlowFunnelId())) {
				  sql += " LEFT JOIN wflowfunnels f1 ON (f1.wflf_wflowfunnelid = wfsp_wflowfunnelid) " +
		 				" LEFT JOIN wflowfunnels f2 ON (f2.wflf_wflowfunnelid = wflw_wflowfunnelid) ";
			  }
		sql += " LEFT JOIN profiles ON (wfsp_profileid = prof_profileid) " +		 
			  "	LEFT JOIN users ON (user_userid = wfsp_userid) " +		  		  
			  " WHERE wfsp_wflowstepid > 0" + 
			  where + 
			  " ORDER BY wflw_wflowid, wfph_sequence, wfsp_sequence, wfsp_progress";
			  
				int count =0;
				//ejecuto el sql DEL CONTADOR
				pmConnCount.doFetch(sql);
				if(pmConnCount.next())
					count=pmConnCount.getInt("contador");
				
				System.out.println("contador DE REGISTROS --> "+count);

	
	sql = " SELECT * FROM wflowsteps " +
		  " LEFT JOIN wflows ON (wfsp_wflowid = wflw_wflowid) " +
		  " LEFT JOIN wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
		  "	LEFT JOIN wflowphases ON (wfsp_wflowphaseid = wfph_wflowphaseid) " +		  	
		  " LEFT JOIN wflowcategories ON (wfph_wflowcategoryid = wfca_wflowcategoryid) ";
		  if (sFParams.isFieldEnabled(bmoWFlowStep.getWFlowFunnelId())) {
			  sql += " LEFT JOIN wflowfunnels f1 ON (f1.wflf_wflowfunnelid = wfsp_wflowfunnelid) " +
	 				" LEFT JOIN wflowfunnels f2 ON (f2.wflf_wflowfunnelid = wflw_wflowfunnelid) ";
		  }
	sql += " LEFT JOIN profiles ON (wfsp_profileid = prof_profileid) " +		 
		  "	LEFT JOIN users ON (user_userid = wfsp_userid) " +		  		  
		  " WHERE wfsp_wflowstepid > 0" + 
		  where + 
		  " ORDER BY wflw_wflowid, wfph_sequence, wfsp_sequence, wfsp_progress";
	
	pmConn.doFetch(sql);
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
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
	if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))){ %>
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
			
	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))){ %>
	<head>
		<title>:::<%= title %>:::</title>
		<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
	</head>
	
	<body class="default" <%= permissionPrint %>>
	
	<table border="0" cellspacing="0" cellpading="0" width="100%">
		<tr>
			<td align="left" width="80" rowspan="2" valign="top">	
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
			</td>
			<td class="reportTitle" align="left" colspan="2">
				<%= title %>
			</td>
		</tr>
		<tr>
			<td class="reportSubTitle">
				<b>Filtros:</b> <%= filters %>
				<br>			
			</td>
		<td class="reportDate" align="right">
				Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
			</td>
		</tr>
	</table>
	<%
	//if que muestra el mensajede error
	if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
		%>
		
				<%=messageTooLargeList %>
		<% 
	}else{
	%>
	<table class="report" border="0">
		<tr><td>&nbsp;</td></table>
	</table>
	<table class="report" border="0">                          
	        <tr class="">
	           <td class="reportHeaderCell">#</td>
	           <td class="reportHeaderCell">Tarea</td>
	           <td class="reportHeaderCell">Activa</td>
	           <td class="reportHeaderCell">Clave Flujo</td>
	           <td class="reportHeaderCell">Nombre Flujo</td>
	           <td class="reportHeaderCell">Estatus Flujo</td>
	           <td class="reportHeaderCell">Tipo Flujo</td>
	           <td class="reportHeaderCell">Categor&iacute;a Flujo</td>
	           <td class="reportHeaderCell">Fase</td>
	           <%	if (sFParams.isFieldEnabled(bmoWFlowStep.getWFlowFunnelId())) { %>
						<td class="reportHeaderCell">Funnel Tarea</td>
						<td class="reportHeaderCell">Funnel Flujo</td>
				<% 	} %>
	           <td class="reportHeaderCell">Grupo</td>
	           <td class="reportHeaderCell">Usuario</td>
	           <td class="reportHeaderCell">Inicio</td>
	           <td class="reportHeaderCell">Avance</td>
	           <td class="reportHeaderCell">Fecha Recordar</td>
	           <td class="reportHeaderCell">Estatus</td>
<!-- 	           <td class="reportHeaderCell">Activa</td>  -->
	          
	       </tr>
<%
	int i = 1;
	while(pmConn.next()) {
		
%>
	<TR class="reportCellEven">
		<%= HtmlUtil.formatReportCell(sFParams, "" + i,BmFieldType.NUMBER) %>		
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowphases", "wfph_sequence") + "." + pmConn.getString("wflowsteps", "wfsp_sequence") + " " + pmConn.getString("wflowsteps", "wfsp_name"), BmFieldType.STRING)) %>
		<%= HtmlUtil.formatReportCell(sFParams, ((pmConn.getBoolean("wfsp_enabled")) ? "Activa":"No Activa"), BmFieldType.STRING) %>
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflows", "wflw_code"),BmFieldType.CODE)) %>
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflows", "wflw_name"),BmFieldType.STRING)) %>		
		<%= HtmlUtil.formatReportCell(sFParams, ((pmConn.getString("wflows", "wflw_status").equals(""+BmoWFlow.STATUS_ACTIVE)) ? "Activo" : "Inactivo"), BmFieldType.CODE) %>
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowtypes", "wfty_name"),  BmFieldType.STRING)) %>		
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowcategories", "wfca_name"), BmFieldType.STRING)) %>
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "(" + pmConn.getString("wflowphases", "wfph_sequence") + ") " + pmConn.getString("wflowphases", "wfph_name"),BmFieldType.STRING)) %>
		<%	if (sFParams.isFieldEnabled(bmoWFlowStep.getWFlowFunnelId())) { %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("f1.wflf_name"),BmFieldType.STRING)) %>		
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("f2.wflf_name"),BmFieldType.STRING)) %>		
		<%	} %>
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("profiles", "prof_name"), BmFieldType.STRING)) %>
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_code"),   BmFieldType.CODE)) %>
		<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowsteps", "wfsp_startdate"),BmFieldType.DATETIME) %>
		<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowsteps", "wfsp_progress"),BmFieldType.PERCENTAGE) %>
		<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowsteps", "wfsp_reminddate"),BmFieldType.DATE) %>
		<%
		PmWFlowStep pmWFlowStep = new PmWFlowStep(sFParams);
		bmoWFlowStep = (BmoWFlowStep)pmWFlowStep.get(pmConn.getInt("wfsp_wflowstepid")); %>
		<%= HtmlUtil.formatReportCell(sFParams, bmoWFlowStep.getStatus().getSelectedOption().getLabel(),BmFieldType.STRING) %>

<%-- 		<%= HtmlUtil.formatReportCell(sFParams, ((pmConn.getBoolean("wfsp_enabled")) ? "Activa":"No Activa"), BmFieldType.STRING) %> --%>
	</TR>
<%
		i++;
	}
%>
	</TABLE>  
<%
	
}// Fin de if(no carga datos)
pmConnCount.close();
   pmConn.close();    
%>  
<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
<% }
System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
+ " Reporte: "+title
+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
}// FIN DEL CONTADOR
%>
  </body>
</html>