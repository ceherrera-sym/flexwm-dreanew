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
<%@page import= "com.flexwm.shared.cm.BmoProject"%>
<%@page import= "com.flexwm.server.cm.PmProject"%>
<%@page import= "com.flexwm.shared.op.BmoOrder"%>
<%@page import= "com.flexwm.shared.fi.BmoRaccount"%>
<%@page import= "com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%
	// Inicializar variables
 	String title = "Encuesta Cliente";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoProject bmoProject = new BmoProject();
	BmoOrder bmoOrder = new BmoOrder();
	
   	String sql = "", where = "", groupFilter = "", filters = "";
   	String startDate = "",  paymentStatus = "", endDate = "", startDateCreate = "", endDateCreate = "";
   	String status = "";
   	int wflowTypeId = 0, projectId = 0, areaId = 0;
   	int wflowCategoryId = 0;
   	int venueId = 0;
   	int userId = 0;
   	int customerId = 0;
   	int referralId = 0;
   	int wflowPhaseId = 0;
   	int programId = 0;
   
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("proj_userid") != null) userId = Integer.parseInt(request.getParameter("proj_userid"));
   	if (request.getParameter("proj_customerid") != null) customerId = Integer.parseInt(request.getParameter("proj_customerid"));
   	if (request.getParameter("wfty_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfty_wflowcategoryid"));
   	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
   	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
   	if (request.getParameter("proj_venueid") != null) venueId = Integer.parseInt(request.getParameter("proj_venueid"));   
   	if (request.getParameter("proj_startdate") != null) startDate = request.getParameter("proj_startdate");
   	if (request.getParameter("proj_enddate") != null) endDate = request.getParameter("proj_enddate");
    if (request.getParameter("proj_status") != null) status = request.getParameter("proj_status");
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
    if (request.getParameter("startdatecreateproject") != null) startDateCreate = request.getParameter("startdatecreateproject");
   	if (request.getParameter("enddatecreateproject") != null) endDateCreate = request.getParameter("enddatecreateproject");
    if (request.getParameter("paymentStatus") != null) paymentStatus = request.getParameter("paymentStatus");
    if (request.getParameter("user_areaid") != null) areaId = Integer.parseInt(request.getParameter("user_areaid"));
    if (request.getParameter("proj_projectid") != null) projectId = Integer.parseInt(request.getParameter("proj_projectid"));

    // Construir filtros 
	// Filtro agrupacion
	if (customerId > 0) {
   		groupFilter += " AND proj_customerid = " + customerId;
   		filters += "<i>Cliente: </i>" + request.getParameter("proj_customeridLabel") + ", ";
   	}
	
	// Filtros listados
   	if (wflowCategoryId > 0) {
   		where += " AND wfty_wflowcategoryid = " + wflowCategoryId;
   		filters += "<i>Categor&iacute;a de Flujo: </i>" + request.getParameter("wfty_wflowcategoryidLabel") + ", ";
   	}
	
	if (userId > 0) {
		where += " AND proj_userid = " + userId;
		/*
		if (sFParams.restrictData(bmoProject.getProgramCode())) {
   			where += " AND proj_userid = " + userId;
		} else {
			where += " AND ( " +
						" proj_userid = " + userId +
						" OR proj_wflowid IN ( " +
							 " SELECT wflw_wflowid FROM wflowusers  " +
							 " LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
							 " WHERE wflu_userid = " + userId + 
							 " AND (wflw_callercode = '" + bmoProject.getProgramCode().toString() + "' OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "') " + 
						 " ) " + 
				 " )";
		}*/
   		filters += "<i>Vendedor: </i>" + request.getParameter("proj_useridLabel") + ", ";
   	}
	
	if (wflowTypeId > 0) {
		where += " AND wfty_wflowtypeid = " + wflowTypeId;
   		filters += "<i>Tipo de Flujo: </i>" + request.getParameter("wflw_wflowtypeidLabel") + ", ";
   	}
	
	if (wflowPhaseId > 0) {
   		where += " AND wflw_wflowphaseid = " + wflowPhaseId;
   		filters += "<i>Fase de Flujo: </i>" + request.getParameter("wflw_wflowphaseidLabel") + ", ";
   	}
	
	if (projectId > 0) {
   		where += " AND proj_projectid = " + projectId;
   		filters += "<i>Proyecto: </i>" + request.getParameter("proj_projectidLabel") + ", ";
   	}
	
	if (areaId > 0) {
   		where += " AND user_areaid = " + areaId;
		filters += "<i>Departamento: </i>" + request.getParameter("user_areaidLabel") + ", ";
	}
	
	if (referralId > 0) {
        where += " AND cust_referralid = " + referralId;
        filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
    }
	
   	if (venueId > 0) {
   		where += " AND proj_venueid = " + venueId;
   		filters += "<i>Lugar: </i>" + request.getParameter("proj_venueidLabel") + ", ";
   	}
   	
   	if (!startDate.equals("")) {
   		where += " AND proj_startdate >= '" + startDate + " 00:00'";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
   	if (!endDate.equals("")) {
   		where += " AND proj_startdate <= '" + endDate + " 23:59'";
   		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
   	}
   	
   	if (!status.equals("")) {
   		//where += " AND proj_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("proj_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("proj_statusLabel") + ", ";
   	}
   	
   	if (!paymentStatus.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
   		filters += "<i>Estatus Pago: </i>" + request.getParameter("paymentStatusLabel") + ", ";
   	}
   	
   	if (!startDateCreate.equals("")) {
   		where += " AND proj_datecreateproject >= '" + startDateCreate + " 00:00'";
   		filters += "<i>Fecha Inicio Sis.: </i>" + startDateCreate + ", ";
   	}
   	
   	if (!endDateCreate.equals("")) {
   		where += " AND proj_datecreateproject <= '" + endDateCreate + " 23:59'";
   		filters += "<i>Fecha Fin Sis.: </i>" + endDateCreate + ", ";
   	}
   	
   	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
   	
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmProject(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	sql = " SELECT COUNT(*) as contador FROM customerpolls " +
   			" LEFT JOIN projects ON (proj_projectid=cupo_projectid) " +
			" LEFT JOIN customers ON (proj_customerid = cust_customerid) " +
		    " LEFT JOIN venues ON (venu_venueid = proj_venueid) " +
		    " LEFT JOIN cities ON (city_cityid = venu_cityid) " +
		    " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) " +
		    " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) " +
		    " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) " +
		    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	        " LEFT JOIN orders ON (proj_orderid = orde_orderid) " +
		    " LEFT JOIN users ON (proj_userid = user_userid) " +
			" WHERE wfca_programid = " + programId + 
			where + groupFilter + 
			" ORDER BY proj_startdate ASC";
	int count =0;
	//ejecuto el sql DEL CONTADOR
	pmConnCount.doFetch(sql);
	if(pmConnCount.next())
		count=pmConnCount.getInt("contador");
	System.out.println("contador DE REGISTROS --> "+count);

	
	
   	sql = " SELECT cust_code, cust_displayname, cust_fatherlastname, cust_motherlastname, cust_firstname, proj_name, proj_code, proj_startdate, proj_enddate, wfca_name, " +
   			" cupo_polldate, cupo_service, cupo_image, cupo_performance, cupo_quality, cupo_general, cupo_recommendation, cupo_observations" +
   			" FROM customerpolls " +
   			" LEFT JOIN projects ON (proj_projectid=cupo_projectid) " +
			" LEFT JOIN customers ON (proj_customerid = cust_customerid) " +
		    " LEFT JOIN venues ON (venu_venueid = proj_venueid) " +
		    " LEFT JOIN cities ON (city_cityid = venu_cityid) " +
		    " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) " +
		    " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) " +
		    " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) " +
		    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	        " LEFT JOIN orders ON (proj_orderid = orde_orderid) " +
		    " LEFT JOIN users ON (proj_userid = user_userid) " +
			" WHERE wfca_programid = " + programId + 
			where + groupFilter + 
			" ORDER BY proj_startdate ASC";
			
	PmConn pmCustomerPoll = new PmConn(sFParams);
	pmCustomerPoll.open();
	pmCustomerPoll.doFetch(sql);
	
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
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
	
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>

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

<table width="100%">
	<tr>
		<td>&nbsp;</td>
		
	</tr>
</table>


<table class="report">
    <tr class="">
        <td class="reportHeaderCell">#</td>
        <td class="reportHeaderCellCenter" width = "19%">Fecha Evento</td>
        <td class="reportHeaderCell">Proyecto</td>
        <td class="reportHeaderCell">Cliente</td>
        <td class="reportHeaderCellCenter">Fecha Enc.</td>
        <td class="reportHeaderCell">Servicio</td>
        <td class="reportHeaderCell">Imagen</td>
        <td class="reportHeaderCell">Calidad</td>
        <td class="reportHeaderCell">Desempe&ntilde;o DJ </td>
        <td class="reportHeaderCellCenter">En General</td>
        <td class="reportHeaderCellCenter">Recomendaci&oacute;n</td>
        <td class="reportHeaderCell">Observaciones</td>
        <td class="reportHeaderCell">Calificaci&oacute;n</td>
    </tr>
    <%
	    int  i = 1, y = 0;
    	double serviceDoub = 0, imageDoub = 0, performanceDoub = 0, qualityDoub = 0, generalDoub = 0, recommendationDoub = 0, tot = 0, promedio = 0;
	          
	    while(pmCustomerPoll.next()) { 

	    	String customer="", service="", image="", perfomance="", quality="", recommendation="", general="";
	    	customer = pmCustomerPoll.getString("customers","cust_code") + " - " +
	    				pmCustomerPoll.getString("customers","cust_displayname");
	    	
	    	serviceDoub = pmCustomerPoll.getDouble("cupo_service");
	    	imageDoub = pmCustomerPoll.getDouble("cupo_image");
	    	performanceDoub = pmCustomerPoll.getDouble("cupo_performance");
	    	qualityDoub = pmCustomerPoll.getDouble("cupo_quality");
	    	generalDoub = pmCustomerPoll.getDouble("cupo_general");
	    	recommendationDoub = pmCustomerPoll.getDouble("cupo_recommendation");	    	

	    	if((pmCustomerPoll.getString("wfca_name").equals("Proyecto Corporativo"))){
	    		tot = (serviceDoub + imageDoub + qualityDoub + generalDoub + recommendationDoub) / 5;
	    	}else{
	    		if(performanceDoub == 0.1)
	    			tot = (serviceDoub + imageDoub + qualityDoub + generalDoub + recommendationDoub) / 5;
    			else
	    			tot = (serviceDoub + imageDoub + performanceDoub + qualityDoub + generalDoub + recommendationDoub) / 6;
	    			
	    	}
	    	promedio += tot;
	    	
    		if(serviceDoub == 10){
	    		service = "Superior a la expectativa";
	    	} else if(serviceDoub == 9.5){
	    		service = "Cumpli&oacute; con la expectativa";
	    	} else if(serviceDoub == 0){
	    		service = "Por debajo";
	    	}    	
    		
    		if(imageDoub == 10){
    			image = "Superior a la expectativa";
	    	} else if(imageDoub == 9.5){
	    		image = "Cumpli&oacute; con la expectativa";
	    	} else if(imageDoub == 0){
	    		image = "Por debajo";
	    	} else image = "";
    		
    		if(qualityDoub == 10){
    			quality = "Superior a la expectativa";
	    	} else if(qualityDoub == 9.5){
	    		quality = "Cumpli&oacute; con la expectativa";
	    	} else if(qualityDoub == 0){
	    		quality = "Por debajo";
	    	} 
    		
    		if((pmCustomerPoll.getString("wfca_name").equals("Proyecto Corporativo"))){
    			perfomance = "N/A Corp.";
    		}else{
	    		if(performanceDoub == 10){
	    			perfomance = "Superior a la expectativa";
		    	} else if(performanceDoub == 9.5){
		    		perfomance = "Cumpli&oacute; con la expectativa";
		    	} else if(performanceDoub == 0){
		    		perfomance = "Por debajo";
	    		} else if(performanceDoub == 0.1){
		    		perfomance = "N/A";
		    	}else perfomance = "N/A";
    		}
    		
    		if(generalDoub == 10){
    			general = "Superior a la expectativa";
	    	} else if(generalDoub == 9.5){
	    		general = "Cumpli&oacute; con la expectativa";
	    	} else if(generalDoub == 0){
	    		general = "Por debajo";
	    	} 
    		
    		if(recommendationDoub == 10){
    			recommendation = "Si";
	    	} else if(recommendationDoub == -5){
	    		recommendation = "No";
	    	} 
 			    	

	%>
			<tr class="reportCellEven">
        		<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, pmCustomerPoll.getString("projects", "proj_startdate") + " | " + pmCustomerPoll.getString("projects", "proj_enddate"), BmFieldType.STRING) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmCustomerPoll.getString("projects", "proj_code") + "&nbsp;&nbsp;" + pmCustomerPoll.getString("projects", "proj_name"), BmFieldType.STRING)) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)) %>
				<%= HtmlUtil.formatReportCell(sFParams, pmCustomerPoll.getString("customerpolls", "cupo_polldate"), BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, service, BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, image, BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, quality, BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, perfomance, BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, general, BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, recommendation, BmFieldType.STRING) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmCustomerPoll.getString("customerpolls", "cupo_observations"), BmFieldType.STRING)) %>
				<td class="reportCellEven" align="right">
		    		&nbsp;<%= SFServerUtil.roundCurrencyDecimals(tot) %>
		    	</td>
			</tr>
	<%
			i++; y++;
	    }

	    %>
    <tr class="reportCellEven reportCellCode">
		<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
    
    	<td colspan="11" align="right">
			<b>Promedio</b>&nbsp;
		</td>
    	<td align="right">
    		&nbsp;<b><%= SFServerUtil.roundCurrencyDecimals(promedio/i) %></b>&nbsp;&nbsp;
    	</td>
    </tr>

</table>    
     
<% 
	}// FIN DEL CONTADOR
System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
+ " Reporte: "+title
+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
}// Fin de if(no carga datos)
pmCustomerPoll.close();
pmConnCount.close();
%> 
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<%
	
	} %>
  

  </body>
</html>