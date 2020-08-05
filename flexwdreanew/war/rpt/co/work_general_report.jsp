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
 
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.flexwm.shared.co.BmoWork"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 
	// Inicializar variables
 	String title = "Reportes de Obras";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 	
   	String sql = "", where = "", filters = "", status = "";
   	BmoWork bmoWork = new BmoWork();
   	int programId = 0, developmentPhaseId = 0, userId = 0, budgetItemId = 0, companyId = 0, isMaster = -1;
   
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("work_developmentphaseid") != null) developmentPhaseId = Integer.parseInt(request.getParameter("work_developmentphaseid"));
   	if (request.getParameter("work_userid") != null) userId = Integer.parseInt(request.getParameter("work_userid"));
   	if (request.getParameter("work_budgetitemid") != null) budgetItemId = Integer.parseInt(request.getParameter("work_budgetitemid"));
   	if (request.getParameter("work_companyid") != null) companyId = Integer.parseInt(request.getParameter("work_companyid"));
   	if (request.getParameter("work_ismaster") != null) isMaster = Integer.parseInt(request.getParameter("work_ismaster"));
   	if (request.getParameter("work_status") != null) status = request.getParameter("work_status");

	// Construir filtros 
    if (developmentPhaseId > 0) {
		where += " AND work_developmentphaseid = " + developmentPhaseId;
   		filters += "<i>Etapa Desarrollo: </i>" + request.getParameter("work_developmentphaseidLabel") + ", ";
   	}
    
    if (userId > 0) {
		where += " AND work_userid = " + userId;
   		filters += "<i>Responsable: </i>" + request.getParameter("work_useridLabel") + ", ";
   	}
    
    if (budgetItemId > 0) {
		where += " AND work_budgetitemid = " + budgetItemId;
   		filters += "<i>Item Pres.: </i>" + request.getParameter("work_budgetitemidLabel") + ", ";
   	}
    
    if (companyId > 0) {
		where += " AND work_companyid = " + companyId;
   		filters += "<i>Empresa: </i>" + request.getParameter("work_companyidLabel") + ", ";
   	}
    
    if (isMaster >= 0) {
		where += " AND work_ismaster = " + isMaster;
   		filters += "<i>Maestro: </i>" + request.getParameter("work_ismasterLabel") + ", ";
   	}
    
    if (!status.equals("")) {
        where += SFServerUtil.parseFiltersToSql("work_status", status);        
   		filters += "<i>Estatus: </i>" + request.getParameter("work_statusLabel") + ", ";
   	}
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
    sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " works ") +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = work_developmentphaseid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = work_userid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON(bgit_budgetitemid = work_budgetitemid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON(comp_companyid= work_companyid) " +
    		" WHERE work_workid > 0 " +
    		where + 
    		" ORDER BY dvph_code ASC, work_code ASC ";
			int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			
			System.out.println("contador DE REGISTROS --> "+count);
    
    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " works ") +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = work_developmentphaseid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = work_userid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON(bgit_budgetitemid = work_budgetitemid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON(comp_companyid= work_companyid) " +
    		" WHERE work_workid > 0 " +
    		where + 
    		" ORDER BY dvph_code ASC, work_code ASC ";
    
    System.out.println("sql: "+sql);
    PmConn pmWorks = new PmConn(sFParams);
    pmWorks.open();
    
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

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

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
			<!--<b>Agrupado Por:</b> Vendedor, <b>Ordenado por:</b> Clave -->
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
<p>
<table class="report">
			<tr class="">		
				<td class="reportHeaderCell">#</td>
				<td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Nombre</td>
				<td class="reportHeaderCell">Descripci&oacute;n</td>
				<td class="reportHeaderCell">Fecha</td>
				<td class="reportHeaderCell">Fase Des.</td>
				<td class="reportHeaderCell">Responsable</td>
				<td class="reportHeaderCell">Clave Item Pres.</td>
				<td class="reportHeaderCell">Item Presupuesto</td>
				<td class="reportHeaderCell">Maestro</td>
				<td class="reportHeaderCell">Empresa</td>
				<td class="reportHeaderCell">Factor Indirectos</td>
				<td class="reportHeaderCell">Estatus</td>
				<td class="reportHeaderCellRight">Total</td>
			</tr>
			<%
				int i = 1;
				double price = 0;
				pmWorks.doFetch(sql);
				while (pmWorks.next()) {
					
	          		bmoWork.getStatus().setValue(pmWorks.getString("works","work_status"));

			%>
			        <tr class="reportCellEven">
	                	<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>                                    
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorks.getString("works","work_code"), BmFieldType.CODE) %>         
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorks.getString("works","work_name"), BmFieldType.STRING)) %>                                    
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorks.getString("works","work_description"), BmFieldType.STRING)) %>                                    
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorks.getString("works","work_startdate"), BmFieldType.DATE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorks.getString("developmentphases","dvph_code"), BmFieldType.CODE) %>
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorks.getString("users","user_firstname") + " " + pmWorks.getString("users","user_fatherlastname") + " " + pmWorks.getString("users","user_motherlastname"), BmFieldType.CODE)) %>         
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorks.getString("budgetitemtypes","bgty_name"), BmFieldType.STRING) %>
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorks.getString("budgetitemtypes","bgty_name"), BmFieldType.STRING)) %>
		                <%= HtmlUtil.formatReportCell(sFParams, ((pmWorks.getBoolean("work_ismaster")) ? "Si" : "No"), BmFieldType.BOOLEAN) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorks.getString("companies","comp_code"), BmFieldType.CODE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorks.getString("works","work_indirects"), BmFieldType.STRING) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoWork.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %> 
		                <%= HtmlUtil.formatReportCell(sFParams, "" + pmWorks.getDouble("work_total"), BmFieldType.CURRENCY) %>
			        </tr>
	    <%
	       i++;
	    }
				
	}// FIN DEL CONTADOR
	
	%>

</table>    
     

	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	}// Fin de if(no carga datos)
	pmConnCount.close();
	pmWorks.close();
	%>
  </body>
</html>