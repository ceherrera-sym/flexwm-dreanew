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
 
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@include file="/inc/login.jsp" %>
<%@page import= "com.flexwm.shared.cm.BmoProject"%>
<%@page import= "com.flexwm.shared.op.BmoOrder"%>
<%@page import= "com.flexwm.server.cm.PmProject"%>
<%@page import= "com.flexwm.shared.op.BmoRequisition"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@page import="com.symgae.server.SFServerUtil"%>


<%
	// Inicializar variables
 	String title = "Proyectos por Proveedores";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoProject bmoProject = new BmoProject();
	BmoOrder bmoOrder = new BmoOrder();	
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	PmCurrency pmCurrencyExchange = new PmCurrency(sFParams);
	BmoRequisition bmoRequisition = new BmoRequisition();
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	
   	String sql = "",sqlCurrency = "", where = "", groupFilter = "",filters = "";
   	String startDate = "", endDate = "", startDateCreate = "", endDateCreate = "";
   	String status = "", paymentStatusOrde = "";
   	int wflowTypeId = 0, projectId = 0, areaId = 0;
   	int wflowCategoryId = 0;
   	int venueId = 0;
   	int userId = 0;
   	int customerId = 0;
   	int referralId = 0;
   	int wflowPhaseId = 0;
   	int programId = 0;
    int currencyId = 0;
   	int currencyIdOrigin = 0;
   	double sumTotal = 0, defaultParity = 0,reqiTotal = 0, currencyParityOrigin = 0;
   	
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("wfty_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfty_wflowcategoryid"));
   	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
   	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
   	if (request.getParameter("proj_venueid") != null) venueId = Integer.parseInt(request.getParameter("proj_venueid"));   
    if (request.getParameter("proj_status") != null) status = request.getParameter("proj_status");
   	if (request.getParameter("proj_userid") != null) userId = Integer.parseInt(request.getParameter("proj_userid"));
   	if (request.getParameter("proj_customerid") != null) customerId = Integer.parseInt(request.getParameter("proj_customerid"));
   	if (request.getParameter("proj_startdate") != null) startDate = request.getParameter("proj_startdate");
   	if (request.getParameter("proj_enddate") != null) endDate = request.getParameter("proj_enddate");
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
    if (request.getParameter("startdatecreateproject") != null) startDateCreate = request.getParameter("startdatecreateproject");
   	if (request.getParameter("enddatecreateproject") != null) endDateCreate = request.getParameter("enddatecreateproject");
    if (request.getParameter("paymentStatus") != null) paymentStatusOrde = request.getParameter("paymentStatus");
    if (request.getParameter("user_areaid") != null) areaId = Integer.parseInt(request.getParameter("user_areaid"));
    if (request.getParameter("proj_projectid") != null) projectId = Integer.parseInt(request.getParameter("proj_projectid"));
    if (request.getParameter("proj_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("proj_currencyid"));
	// Construir filtros 
	
	// Filtro listados	
   	if (wflowCategoryId > 0) {
   		where += " AND wfty_wflowcategoryid = " + wflowCategoryId;
   		filters += "<i>Categor&iacute;a de Flujo: </i>" + request.getParameter("wfty_wflowcategoryidLabel") + ", ";
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
   	
   	if (customerId > 0) {
   		where += " AND proj_customerid = " + customerId;
   		filters += "<i>Cliente: </i>" + request.getParameter("proj_customeridLabel") + ", ";
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
   		where += " AND venu_venueid = " + venueId;
   		filters += "<i>Lugar: </i>" + request.getParameter("proj_venueidLabel") + ", ";
   	}
   	
   	if (!status.equals("")) {
   		//where += " AND proj_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("proj_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("proj_statusLabel") + ", ";
   	}
   	
   	if (!paymentStatusOrde.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatusOrde);
   		filters += "<i>Estatus Pago: </i>" + request.getParameter("paymentStatusLabel") + ", ";
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
   	
   	if (!startDate.equals("")) {
   		where += " AND proj_startdate >= '" + startDate + " 00:00'";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
   	
   	if (!endDate.equals("")) {
   		where += " AND proj_startdate <= '" + endDate + " 23:59'";
   		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
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
    	if (currencyId > 0) {
    		bmoCurrency = (BmoCurrency) pmCurrency.get(currencyId);
    		defaultParity = bmoCurrency.getParity().toDouble();

    		filters += "<i>Moneda: </i>" + request.getParameter("proj_currencyidLabel")
    				+ " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
    	} else {
    		filters += "<i>Moneda: </i> Todas ";
    		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM projects "
    				+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
    				+ " LEFT JOIN referrals ON (refe_referralid = cust_referralid) "
    				+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
    				+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
    				+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
    				+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
    				+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
    				+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
    				+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
    				+ " LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) "
    				+ " LEFT JOIN users ON (user_userid = proj_userid) " 
    				+ " WHERE wfca_programid = " + programId
    				+ groupFilter
    				+ where 
    				+ " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
    	}
   	System.out.println("SQLCURRENCY "+ sqlCurrency);
   	// Obtener disclosure de datos
    String disclosureFilters = new PmProject(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;

	//Conexion a Base de Datos
	PmConn pmConnGroup = new PmConn(sFParams);
	pmConnGroup.open();
	
	PmConn pmConnListCount = new PmConn(sFParams);
	pmConnListCount.open();
	
	PmConn pmConnList = new PmConn(sFParams);
	pmConnList.open();
	
	  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
	PmConn pmCurrencyWhile = new PmConn(sFParams);
	
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
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>""> 
	
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
			<br>
			<b>Agrupado Por:</b> Proveedor, <b>Ordenado por:</b> Fecha de Evento
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<br>
<table width="100%">
	<tr>
		<!--<td width="50%"> <div id="chart_div" style="width: 100%; height: 400px;"></div> </td>
		<td width="50%"> <div id="chart_div2" style="width: 100%; height: 400px;"></div> </td>-->
	</tr>
</table>

<table class="report">
    
    <% 	

	sql = " SELECT COUNT(*) AS contador FROM projects " +
			" LEFT JOIN customers ON(cust_customerid = proj_customerid) " +
			" LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
			" LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
			" LEFT JOIN cities ON(city_cityid = venu_cityid) " +
			" LEFT JOIN users ON(user_userid = proj_userid) " +
			" LEFT JOIN orders ON(orde_orderid = proj_orderid) " +
			" LEFT JOIN requisitions ON(reqi_orderid = orde_orderid) " +
			//" LEFT JOIN suppliers ON(supl_supplierid = reqi_supplierid) " +
			" LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
			" LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
			" LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
		    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
			" WHERE wfca_programid = " + programId +
			" AND reqi_supplierid > 0" +
			where +
			" ORDER BY proj_startdate ASC";
			
	int count =0;
	//ejecuto el sql DEL CONTADOR
	pmConnCount.doFetch(sql);
	if(pmConnCount.next())
		count=pmConnCount.getInt("contador");
	System.out.println("contador DE REGISTROS --> "+count);
	//if que muestra el mensajede error
	if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
		%>
		
				<%=messageTooLargeList %>
		<% 
	}else{
    
	if (!(currencyId > 0)){
		
		int currencyIdWhile = 0;
		pmCurrencyWhile.open();
		pmCurrencyWhile.doFetch(sqlCurrency);
		
			while(pmCurrencyWhile.next()){%>
					<table class="report" style="width: 100%;">
				<%
				if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
	        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
					currencyId = currencyIdWhile;
			    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
			    	bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);
	        		%>
	        		<tr>
	            		<td class="reportHeaderCellCenter" colspan="24">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
	        		</tr>
	        		<%
	        	}
	        	pmConnGroup.doFetch("SELECT distinct(reqi_supplierid) as supplierid, supl_code, supl_name " +
									" FROM requisitions " +
									" LEFT JOIN suppliers ON(supl_supplierid = reqi_supplierid) " +
									" WHERE supl_supplierid in ( " +
									" SELECT reqi_supplierid FROM projects " +
									" LEFT JOIN customers ON(cust_customerid = proj_customerid) " +
									" LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
									" LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
									" LEFT JOIN cities ON(city_cityid = venu_cityid) " +
									" LEFT JOIN users ON(user_userid = proj_userid) " +
									" LEFT JOIN orders ON(orde_orderid = proj_orderid) " +
									" LEFT JOIN requisitions ON(reqi_orderid = orde_orderid) " +
									" LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
									" LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
									" LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
				    				" LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
									" WHERE wfca_programid = " + programId +
									where +
									" ORDER BY proj_startdate ASC )" +
									" ORDER BY supplierid");
									
									
	        	int n = 0, countPv = 0;
	        	double sumTotalPV = 0;	
	        	while(pmConnGroup.next()) {
	        		int reqiSupplierId = pmConnGroup.getInt("requisitions", "supplierid");	
	        	
	        		sql = " SELECT * FROM projects " +
						" LEFT JOIN customers ON(cust_customerid = proj_customerid) " +
						" LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
						" LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
						" LEFT JOIN cities ON(city_cityid = venu_cityid) " +
						" LEFT JOIN users ON(user_userid = proj_userid) " +
						" LEFT JOIN orders ON(orde_orderid = proj_orderid) " +
						" LEFT JOIN requisitions ON(reqi_orderid = orde_orderid) " +
						//" LEFT JOIN suppliers ON(supl_supplierid = reqi_supplierid) " +
						" LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
						" LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
						" LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
					    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
						" WHERE wfca_programid = " + programId +
						" AND reqi_supplierid = " + reqiSupplierId + " AND proj_currencyid = " + currencyId +
						where +
						" ORDER BY proj_startdate ASC";
						pmConnList.doFetch(sql);
						
						 int  i = 0, y =1;
				         double total = 0, totalProyect = 0;
				         int  supplierId = 0;
				         int projId = 0, projId2 = 0;
						 while(pmConnList.next()){
								String reqiStatus = "", deliveryStatus = "", paymentStatus = "", activeStatus = "", projStatuts = "";
								total += pmConnList.getDouble("reqi_total");  
				          		sumTotal += pmConnList.getDouble("reqi_total");
				          		
				          		
				          		if((reqiSupplierId != supplierId)){
				          			supplierId = reqiSupplierId;
				          			countPv++;%>
				          			<tr>
						        	   <td colspan="20" class="reportGroupCell">
							           <%= pmConnGroup.getString("supl_code") %> - <%= pmConnGroup.getString("supl_name") %>
							        </td>
						            </tr>
					                <tr class="">
					                   <td class="reportHeaderCellCenter">#</td>
					                   <td class="reportHeaderCell">Proyecto</td>
					                   <td class="reportHeaderCell">Cliente</td>
					           	       <td class="reportHeaderCell">Referencia</td>
					                   <td class="reportHeaderCell">Lugar</td>
					                   <td class="reportHeaderCell">Ciudad</td>
					                   <td class="reportHeaderCell">Categor&iacute;a</td>
					                   <td class="reportHeaderCell">Tipo</td>
					                   <td class="reportHeaderCell">Fase</td>
					                   <td class="reportHeaderCell">Avance</td>
					                   <td class="reportHeaderCell">Fecha</td>
					                   <td class="reportHeaderCell">Estatus</td>
					                   <td class="reportHeaderCell">Vendedor</td>
					                   <td class="reportHeaderCell">Fecha Sis.</td>
					                   <td class="reportHeaderCell">Moneda</td>
					                   <td class="reportHeaderCell">Tipo de Cambio</td>
					                   <!--<td class="reportHeaderCell">O.C.</td>-->
					                   <td class="reportHeaderCellCenter"># O.C.</td>
					                   <td class="reportHeaderCell">O.C.</td>
					                   <td class="reportHeaderCell">Est O.C.</td>
					                   <td class="reportHeaderCell">Entrega</td>
					                   <td class="reportHeaderCell">Pago</td>
<!-- 					                   <td class="reportHeaderCell">Moneda</td> -->
<!-- 					                   <td class="reportHeaderCellCenter">Paridad</td> -->
					                   <td class="reportHeaderCellRight">Valor O.C.</td>
					                </tr>
				          			
				          			
				          		<%}%>
				          					    <% 
			    
					    if((pmConnList.getInt("proj_projectid") != projId)){
			   			 	projId = pmConnList.getInt("proj_projectid");
			   			 	totalProyect = 0;
			    			currencyIdOrigin = pmConnList.getInt("reqi_currencyid");
			    			currencyParityOrigin = pmConnList.getDouble("reqi_currencyparity");
			    			y = 1;
			    			i++; n++;
          					%>
			          			<tr class="reportCellEven">
			          				<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>

			                		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_code") + "&nbsp;&nbsp;" + pmConnList.getString("projects", "proj_name"), BmFieldType.STRING)) %>
			                
			               		 <% if (pmConnList.getString("customers","cust_legalname").equals("NA")) {  
			                			String customer = pmConnList.getString("customers","cust_code") + " " +
			                						pmConnList.getString("customers","cust_displayname");
			                %>
			                		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)) %>
			              <% } else { %>
			                		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("customers","cust_code") + " " + pmConnList.getString("customers","cust_legalname"), BmFieldType.STRING)) %>
			               <%} %>
			                
			                   		 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("referrals","refe_name"), BmFieldType.STRING)) %>
			                    
									 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("venues","venu_code"), BmFieldType.CODE)) %>
								
						  			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("cities", "city_name"), BmFieldType.STRING)) %>
								
								     <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowcategories", "wfca_name"), BmFieldType.STRING)) %>

								     <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
							    
							         <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowphases", "wfph_name"), BmFieldType.STRING)) %>
							    	
							   		 <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflows", "wflw_progress"), BmFieldType.PERCENTAGE) %>
							    
							    	 <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_startdate"), BmFieldType.DATE) %>
							    
							    	 <% bmoProject.getStatus().setValue(pmConnList.getString("projects", "proj_status")); %>
									 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoProject.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
											
									 <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("users", "user_code"), BmFieldType.STRING) %>
								
							    	 <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_datecreateproject"), BmFieldType.DATE) %>
							    	 
							    	  <%= HtmlUtil.formatReportCell(sFParams,bmoCurrency.getCode().toString(), BmFieldType.CODE) %>
							    	  
							    	   <%= HtmlUtil.formatReportCell(sFParams, bmoCurrency.getParity().toString(), BmFieldType.NUMBER) %>
							    	 
									
							    		<td colspan="10">&nbsp;</td>
					 			</tr>
		
				<% }%>
								<tr class="reportCellEven">
									<td colspan="16">&nbsp;</td>
								
									<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
						    	
							 	   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("reqi_code") + " - " +pmConnList.getString("reqi_name"), BmFieldType.STRING)) %>

						    		<% bmoRequisition.getStatus().setValue(pmConnList.getString("requisitions", "reqi_status")); %>
									<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
								
									<% bmoRequisition.getDeliveryStatus().setValue(pmConnList.getString("requisitions", "reqi_deliverystatus")); %>
									<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getDeliveryStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
								
									<% bmoRequisition.getPaymentStatus().setValue(pmConnList.getString("requisitions", "reqi_paymentstatus")); %>
									<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
<%-- 									<%= HtmlUtil.formatReportCell(sFParams, bmoCurrency.getCode().toString(), BmFieldType.CODE) %> --%>
<%-- 									<%= HtmlUtil.formatReportCell(sFParams, bmoCurrency.getParity().toString(), BmFieldType.NUMBER) %> --%>
								    <%= HtmlUtil.formatReportCell(sFParams,""+ pmCurrencyExchange.currencyExchange(pmConnList.getDouble("reqi_total"),currencyIdOrigin ,currencyParityOrigin ,
							    		currencyId, defaultParity) , BmFieldType.CURRENCY) %>
		
						</tr>
									 <%	totalProyect += pmConnList.getDouble("reqi_total");
							    
							    int countReqi = 0;
							    	
							    String countProject = " SELECT proj_projectid, count(proj_projectid) as con FROM projects " +
										" LEFT JOIN customers ON(cust_customerid = proj_customerid) " +
										" LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
										" LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
										" LEFT JOIN cities ON(city_cityid = venu_cityid) " +
										" LEFT JOIN users ON(user_userid = proj_userid) " +
										" RIGHT JOIN orders ON(orde_orderid = proj_orderid) " +
										" LEFT JOIN requisitions ON(reqi_orderid = orde_orderid) " +
										" LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
										" LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
										" LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
									    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
										" WHERE wfca_programid = " + programId +
										" AND reqi_supplierid = " + reqiSupplierId +
										" AND proj_projectid = " + pmConnList.getInt("proj_projectid") +
										where +
										" GROUP BY proj_projectid " +
										" ORDER BY proj_projectid ASC";
							    
							    //System.out.println("countProject: "+ countProject);
							    
							    pmConnListCount.doFetch(countProject);
							    
							    if(pmConnListCount.next()) countReqi = pmConnListCount.getInt("con");
							    
							    if(countReqi == y){
							    %>
									<tr>
										<td class="reportCellCurrency" colspan="21" >&nbsp;<b>Total Proy.</b></td>
										<td class="reportCellCurrency" >&nbsp;<b><%= SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(totalProyect, currencyIdOrigin, pmConnList.getDouble("reqi_currencyparity"),
												currencyId, defaultParity) )%></b></td>
									</tr>
				          		
				          		<%}%>
				          		
							<%y++;
							} //pmConnList
						 sumTotalPV += total;
							 if(total > 0){%>
							 	 <tr class="reportCellEven reportCellCode">
    								<td class="reportCellCurrency" colspan="21" align="right"> 
    								&nbsp;<b>Total PV.</b>
    								</td >
									<td class="reportCellCurrency" >
									&nbsp;<b><%= SFServerUtil.formatCurrency(pmCurrency.currencyExchange(total, currencyIdOrigin, currencyParityOrigin, currencyId, defaultParity))%></b>
									</td>
    							</tr>
							 <%} %>
	        		
	        	<%}//pmConnGroup%>
	        					<tr><td colspan="20">&nbsp;</td></tr>
								<tr class="reportCellEven reportCellCode" height="30">
								<%= HtmlUtil.formatReportCell(sFParams, "" + n, BmFieldType.NUMBER) %>
		
									<td colspan="20" align="right"> 
									&nbsp;<b>TOTAL PROY.</b>
									</td >
									<td align="right">
									&nbsp;<b><%= SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(sumTotal, currencyIdOrigin, currencyParityOrigin, currencyId, defaultParity)) %></b>
									</td>
								</tr>
								<tr class="reportCellEven reportCellCode" height="30">
									<%= HtmlUtil.formatReportCell(sFParams, "" + countPv, BmFieldType.NUMBER) %>
									<td colspan="20" align="right"> 
									&nbsp;<b>TOTAL PV.</b>
									</td >
									<td align="right"> 
									&nbsp;<b><%= SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(sumTotalPV, currencyIdOrigin, currencyParityOrigin, currencyId, defaultParity)) %></b>
									</td>
								</tr>
							</table>    	        	
	        		
	        	
			
		 <%} //Fin CurrencyWhile%>
		
	<%}else{
	pmConnGroup.doFetch("SELECT distinct(reqi_supplierid) as supplierid, supl_code, supl_name " +
			" FROM requisitions " +
			" LEFT JOIN suppliers ON(supl_supplierid = reqi_supplierid) " +
			" WHERE supl_supplierid in ( " +
					" SELECT reqi_supplierid FROM projects " +
					" LEFT JOIN customers ON(cust_customerid = proj_customerid) " +
					" LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
					" LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
					" LEFT JOIN cities ON(city_cityid = venu_cityid) " +
					" LEFT JOIN users ON(user_userid = proj_userid) " +
					" LEFT JOIN orders ON(orde_orderid = proj_orderid) " +
					" LEFT JOIN requisitions ON(reqi_orderid = orde_orderid) " +
					" LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
					" LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
					" LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
				    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
					" WHERE wfca_programid = " + programId +
					where +
					" ORDER BY proj_startdate ASC )" +
			" ORDER BY supplierid");
	int n = 0, countPv = 0;
	double sumTotalPV = 0;
        while(pmConnGroup.next()) {
			int reqiSupplierId = pmConnGroup.getInt("requisitions", "supplierid");	

			
			
			sql = " SELECT * FROM projects " +
					" LEFT JOIN customers ON(cust_customerid = proj_customerid) " +
					" LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
					" LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
					" LEFT JOIN cities ON(city_cityid = venu_cityid) " +
					" LEFT JOIN users ON(user_userid = proj_userid) " +
					" LEFT JOIN orders ON(orde_orderid = proj_orderid) " +
					" LEFT JOIN requisitions ON(reqi_orderid = orde_orderid) " +
					//" LEFT JOIN suppliers ON(supl_supplierid = reqi_supplierid) " +
					" LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
					" LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
					" LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
				    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
					" WHERE wfca_programid = " + programId +
					" AND reqi_supplierid = " + reqiSupplierId +
					where +
					" ORDER BY proj_startdate ASC";
			         
			System.out.println("pmConnList----: "+sql);
			pmConnList.doFetch(sql);
	   
	          int  i = 0, y =1;
	          double total = 0, totalProyect = 0;
	          int  supplierId = 0;
	          int projId = 0, projId2 = 0;
	          while(pmConnList.next()) {
	        	  String reqiStatus = "", deliveryStatus = "", paymentStatus = "", activeStatus = "", projStatuts = "";

	          		total += pmConnList.getDouble("reqi_total");  
	          		sumTotal += pmConnList.getDouble("reqi_total");
	
	          		if((reqiSupplierId != supplierId)){
	          			supplierId = reqiSupplierId;
	          			countPv++;
	    %>
				    	<tr>
							<td colspan="20" class="reportGroupCell">
							    <%= pmConnGroup.getString("supl_code") %> - <%= pmConnGroup.getString("supl_name") %>
							</td>
						</tr>
					    <tr class="">
					        <td class="reportHeaderCellCenter">#</td>
					        <td class="reportHeaderCell">Proyecto</td>
					        <td class="reportHeaderCell">Cliente</td>
					        <td class="reportHeaderCell">Referencia</td>
					        <td class="reportHeaderCell">Lugar</td>
					        <td class="reportHeaderCell">Ciudad</td>
					        <td class="reportHeaderCell">Categor&iacute;a</td>
					        <td class="reportHeaderCell">Tipo</td>
					        <td class="reportHeaderCell">Fase</td>
					        <td class="reportHeaderCell">Avance</td>
					        <td class="reportHeaderCell">Fecha</td>
					        <td class="reportHeaderCell">Estatus</td>
					        <td class="reportHeaderCell">Vendedor</td>
					        <td class="reportHeaderCell">Fecha Sis.</td>
					         <td class="reportHeaderCell">Moneda</td>
					        <td class="reportHeaderCellCenter">Tipo de Cambio</td>
					        <!--<td class="reportHeaderCell">O.C.</td>-->
					        <td class="reportHeaderCellCenter"># O.C.</td>
					        <td class="reportHeaderCell">O.C.</td>
					        <td class="reportHeaderCell">Est O.C.</td>
					        <td class="reportHeaderCell">Entrega</td>
					        <td class="reportHeaderCell">Pago</td>
<!-- 					        <td class="reportHeaderCell">Moneda</td> -->
<!-- 					        <td class="reportHeaderCellCenter">Paridad</td> -->
					        <td class="reportHeaderCellRight">Valor O.C.</td>
					    </tr>
					   
			    
			    <% } %>
			    <% 
			    
			    if((pmConnList.getInt("proj_projectid") != projId)){
			    	projId = pmConnList.getInt("proj_projectid");
			    	totalProyect = 0;
			    	currencyIdOrigin = pmConnList.getInt("reqi_currencyid");
			    	currencyParityOrigin = pmConnList.getDouble("reqi_currencyparity");
			    	y = 1;
			    	i++; n++;
          			%>
			          <tr class="reportCellEven">
			          		<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>

			                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_code") + "&nbsp;&nbsp;" + pmConnList.getString("projects", "proj_name"), BmFieldType.STRING)) %>
			                
			                <% if (pmConnList.getString("customers","cust_legalname").equals("NA")) {  
			                	String customer = pmConnList.getString("customers","cust_code") + " " +
			                						pmConnList.getString("customers","cust_displayname");
			                %>
			                	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)) %>
			                <% } else { %>
			                	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("customers","cust_code") + " " + pmConnList.getString("customers","cust_legalname"), BmFieldType.STRING)) %>
			                <% } %>
			                
			                    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("referrals","refe_name"), BmFieldType.STRING)) %>
			                    
								<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("venues","venu_code"), BmFieldType.CODE)) %>
								
								<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("cities", "city_name"), BmFieldType.STRING)) %>
								
							    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowcategories", "wfca_name"), BmFieldType.STRING)) %>

							    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
							    
							    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowphases", "wfph_name"), BmFieldType.STRING)) %>
							    	
							    <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflows", "wflw_progress"), BmFieldType.PERCENTAGE) %>
							    
							    <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_startdate"), BmFieldType.DATE) %>
							    
							    <% bmoProject.getStatus().setValue(pmConnList.getString("projects", "proj_status")); %>
								<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoProject.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
											
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("users", "user_code"), BmFieldType.STRING) %>
								
							    <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_datecreateproject"), BmFieldType.DATE) %>
							    <%= HtmlUtil.formatReportCell(sFParams, bmoCurrency.getCode().toString(), BmFieldType.CODE) %>
								<%= HtmlUtil.formatReportCell(sFParams, bmoCurrency.getParity().toString(), BmFieldType.NUMBER) %>

							    <td colspan="8">&nbsp;</td>
						</tr>
		
				<% }%>
						<tr class="reportCellEven">
								<td colspan="16">&nbsp;</td>
								
								<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
						    	
							    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("reqi_code") + " - " +pmConnList.getString("reqi_name"), BmFieldType.STRING)) %>

						    	<% bmoRequisition.getStatus().setValue(pmConnList.getString("requisitions", "reqi_status")); %>
								<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
								
								<% bmoRequisition.getDeliveryStatus().setValue(pmConnList.getString("requisitions", "reqi_deliverystatus")); %>
								<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getDeliveryStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
								
								<% bmoRequisition.getPaymentStatus().setValue(pmConnList.getString("requisitions", "reqi_paymentstatus")); %>
								<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
<%-- 								<%= HtmlUtil.formatReportCell(sFParams, bmoCurrency.getCode().toString(), BmFieldType.CODE) %> --%>
<%-- 								<%= HtmlUtil.formatReportCell(sFParams, bmoCurrency.getParity().toString(), BmFieldType.NUMBER) %> --%>
							    <%= HtmlUtil.formatReportCell(sFParams,""+ pmCurrencyExchange.currencyExchange(pmConnList.getDouble("reqi_total"),currencyIdOrigin ,currencyParityOrigin ,
							    		currencyId, defaultParity) , BmFieldType.CURRENCY) %>
		
						</tr>
							    
							    <% 
							    totalProyect += pmConnList.getDouble("reqi_total");
							    
							    int countReqi = 0;
							    	
							    String countProject = " SELECT proj_projectid, count(proj_projectid) as con FROM projects " +
										" LEFT JOIN customers ON(cust_customerid = proj_customerid) " +
										" LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
										" LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
										" LEFT JOIN cities ON(city_cityid = venu_cityid) " +
										" LEFT JOIN users ON(user_userid = proj_userid) " +
										" RIGHT JOIN orders ON(orde_orderid = proj_orderid) " +
										" LEFT JOIN requisitions ON(reqi_orderid = orde_orderid) " +
										" LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
										" LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
										" LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
									    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
										" WHERE wfca_programid = " + programId +
										" AND reqi_supplierid = " + reqiSupplierId +
										" AND proj_projectid = " + pmConnList.getInt("proj_projectid") +
										where +
										" GROUP BY proj_projectid " +
										" ORDER BY proj_projectid ASC";
							    
							    //System.out.println("countProject: "+ countProject);
							    
							    pmConnListCount.doFetch(countProject);
							    
							    if(pmConnListCount.next()) countReqi = pmConnListCount.getInt("con");
							    
							    if(countReqi == y){
							    %>
									<tr>
										<td class="reportCellCurrency" colspan="21" >&nbsp;<b>Total Proy.</b></td>
										<td class="reportCellCurrency" >&nbsp;<b><%= SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(totalProyect, currencyIdOrigin, pmConnList.getDouble("reqi_currencyparity"),
												currencyId, defaultParity) )%></b></td>
									</tr>
							  <%  } 
							    
							    %>
							    
							    
	    <% y++;
	       //i++;
	       } //pmConnList
	          sumTotalPV += total;
	       
	       if(total > 0){
	     %>
	     <tr class="reportCellEven reportCellCode">
    		<td class="reportCellCurrency" colspan="21" align="right"> 
    			&nbsp;<b>Total PV.</b>
    		</td >
			<td class="reportCellCurrency" >
				&nbsp;<b><%= SFServerUtil.formatCurrency(pmCurrency.currencyExchange(total, currencyIdOrigin, currencyParityOrigin, currencyId, defaultParity))%></b>
			</td>
    	</tr>
	     
	     <%
	       }
	} //pmConnGroup
	%>
	<tr><td colspan="22">&nbsp;</td></tr>
	<tr class="reportCellEven reportCellCode" height="30">
		<%= HtmlUtil.formatReportCell(sFParams, "" + n, BmFieldType.NUMBER) %>
		
		<td colspan="20" align="right"> 
			&nbsp;<b>TOTAL PROY.</b>
		</td >
		<td align="right">
			&nbsp;<b><%= SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(sumTotal, currencyIdOrigin, currencyParityOrigin, currencyId, defaultParity)) %></b>
		</td>
	</tr>
	<tr class="reportCellEven reportCellCode" height="30">
		<%= HtmlUtil.formatReportCell(sFParams, "" + countPv, BmFieldType.NUMBER) %>
		<td colspan="20" align="right"> 
			&nbsp;<b>TOTAL PV.</b>
		</td >
		<td align="right"> 
			&nbsp;<b><%= SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(sumTotalPV, currencyIdOrigin, currencyParityOrigin, currencyId, defaultParity)) %></b>
		</td>
	</tr>
</table>    
<%} %> 
<% 
	}// FIN DEL CONTADOR
	}// Fin de if(no carga datos
	pmConnList.close();
   	pmConnGroup.close();
	pmConnListCount.close();
	pmConnCount.close();
	pmCurrencyWhile.close();
%> 
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
					+ " Reporte: "+title
					+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); %>
  

  </body>
</html>