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
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@include file="/inc/login.jsp"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%

	String title = "Proyectos Por Flujo";	
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoProject bmoProject = new BmoProject();
	BmoOrder bmoOrder = new BmoOrder();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	
	String sql = "", sqlCurrency = "", where = "", wherePhase = "", whereRacc = "", wherePacc = "", groupFilter = "", filters = "";
	String status = "", paymentStatus = "", startDate = "", endDate = "", startDateCreate = "", endDateCreate = "";
	int wflowTypeId = 0, projectId = 0, areaId = 0,	wflowCategoryId = 0, venueId = 0, userId = 0, customerId = 0,
	referralId = 0, wflowPhaseId = 0, programId = 0,	 dynamicColspan = 0, dynamicColspanMinus = 0,
	currencyId = 0, currencyIdOrigin = 0, currencyIdDestiny = 0;
   	double nowParity = 0, defaultParity = 0, parityOrigin = 0, parityDestiny = 0;
	
	//Obtener parametros
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("wfty_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfty_wflowcategoryid"));
	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
	if (request.getParameter("proj_venueid") != null) venueId = Integer.parseInt(request.getParameter("proj_venueid"));   
	if (request.getParameter("proj_startdate") != null) startDate = request.getParameter("proj_startdate");	
	if (request.getParameter("proj_enddate") != null) endDate = request.getParameter("proj_enddate");
	if (request.getParameter("proj_userid") != null) userId = Integer.parseInt(request.getParameter("proj_userid")); 
	if (request.getParameter("proj_customerid") != null) customerId = Integer.parseInt(request.getParameter("proj_customerid")); 
	if (request.getParameter("proj_status") != null) status = request.getParameter("proj_status");
	if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
	if (request.getParameter("startdatecreateproject") != null) startDateCreate = request.getParameter("startdatecreateproject");
	if (request.getParameter("enddatecreateproject") != null) endDateCreate = request.getParameter("enddatecreateproject");
    if (request.getParameter("paymentStatus") != null) paymentStatus = request.getParameter("paymentStatus");
    if (request.getParameter("user_areaid") != null) areaId = Integer.parseInt(request.getParameter("user_areaid"));
    if (request.getParameter("proj_projectid") != null) projectId = Integer.parseInt(request.getParameter("proj_projectid"));
    if (request.getParameter("proj_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("proj_currencyid"));

	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	// Filtros listados
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
		where += " AND cust_customerid = " + customerId;
		filters += "<i>Cliente: </i>" + request.getParameter("proj_customeridLabel") + ", ";
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
// 		whereRacc += " AND racc_paymentdate >= '" + startDate + "'";
// 		wherePacc += " AND pacc_paymentdate >= '" + startDate + "'";
		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
	}
	
	if (!endDate.equals("")) {
		where += " AND proj_startdate <= '" + endDate + " 23:59'";
// 		whereRacc += " AND racc_paymentdate <= '" + endDate + "'";
// 		wherePacc += " AND pacc_paymentdate <= '" + endDate + "'";
		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
	}
	
	if (!status.equals("")) {
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
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmProject(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
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
    
	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";

   //Conexion a Base de Datos
   PmConn pmConnProjects = new PmConn(sFParams);
   pmConnProjects.open();
   
   PmConn pmConn = new PmConn(sFParams);
   pmConn.open();
   
   PmConn pmCurrencyWhile = new PmConn(sFParams);
   pmCurrencyWhile.open();
   
	//abro conexion para inciar el conteo consulta general
   PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
 
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
body {
	user-select: none;
	-moz-user-select: none;
	-o-user-select: none;
	-webkit-user-select: none;
	-ie-user-select: none;
	-khtml-user-select: none;
	-ms-user-select: none;
	-webkit-touch-callout: none
}
</style>
<style type="text/css" media="print">
* {
	display: none;
}
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
<title>:::<%= title %>:::
</title>
<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

	<table border="0" width="100%">
		<tr>
			<td align="left" width="60" rowspan="2" valign="top"><img
				border="0" width="<%= SFParams.LOGO_WIDTH %>"
				height="<%= SFParams.LOGO_HEIGHT %>"
				src="<%= sFParams.getMainImageUrl() %>"></td>
			<td class="reportTitle" align="left"><%= title %></td>
			<td align="right">
				<% if (!exportExcel.equals("1")) { %> <a
				href="<%= request.getRequestURL().toString() + "?" + request.getQueryString() + "&exportexcel=1" %>"><input
					type="image" id="exportExcel" src="<%= GwtUtil.getProperUrl(sFParams, "/icons/vaadin/file-table.png")%>"
					name="exportExcel" title="Exportar a Excel"></a> <input
				type="image" id="printImage" src="<%= GwtUtil.getProperUrl(sFParams, "/icons/print.png")%>"
				name="image" onclick="doPrint()" title="Imprimir"> <% } %>
			</td>
		</tr>
		<tr>
			<td class="reportSubTitle"><b>Filtros:</b> <i>Pedido:</i>
				Autorizado, <i>O.C:</i> Autorizadas, <%= filters %></td>
			<td class="reportDate" align="right">Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %>
				por: <%= sFParams.getLoginInfo().getEmailAddress() %>
			</td>
		</tr>
	</table>
	<br>
	
	<%
	

	sql = " SELECT COUNT(*) as contador FROM projects "
   			+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
   			+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
   			+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
   			+ " LEFT JOIN states ON (stat_stateid = city_stateid) "
   			+ " LEFT JOIN countries ON (cont_countryid = stat_stateid ) "
   			+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
   			+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
   			+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
   			+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
   			+ " LEFT JOIN users ON (user_userid = proj_userid) "
   			+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
   			+ " WHERE proj_projectid > 0 "
   			+ where
		 	+ " ORDER BY proj_startdate ASC";
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
   			
	if (!(currencyId > 0)) {
		int currencyIdWhile = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			%>
			<table class="report" style="width: 100%;">
				<%
				if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
	        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
					currencyId = currencyIdWhile;
			    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
	        		%>
	        		<tr>
	            		<th class="reportHeaderCellCenter" colspan="7">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</th>
	        		</tr>
	        		<%
	        	}
				%>
				<tr class="">
					<th class="reportHeaderCellCenter">#</th>
					<th class="reportHeaderCell">Proyecto</th>
					<th class="reportHeaderCell">Cliente</th>
					<th class="reportHeaderCellRight">Pedido</th>
					<th class="reportHeaderCellRight">CXC</th>
					<th class="reportHeaderCellRight">O.C.</th>
					<th class="reportHeaderCellRight">CXP</th>
				</tr>
				<%
		
				//Listado de Proyectos
			   	sql = " SELECT proj_projectid, proj_code, proj_name, proj_orderid, cust_customertype, cust_code, cust_displayname, cust_legalname " 
			   			+ " FROM projects "
			   			+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
			   			+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
			   			+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
			   			+ " LEFT JOIN states ON (stat_stateid = city_stateid) "
			   			+ " LEFT JOIN countries ON (cont_countryid = stat_stateid ) "
			   			+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
			   			+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
			   			+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
			   			+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
			   			+ " LEFT JOIN users ON (user_userid = proj_userid) "
			   			+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
			   			+ " WHERE proj_projectid > 0 "
						+ " AND orde_currencyid =  " + currencyId
			   			+ where
					 	+ " ORDER BY proj_startdate ASC";
			         
			   	pmConnProjects.doFetch(sql);
			   
				int  i = 1, y = 0;
				double totalOrderTotal = 0;
				double totalReqiTotal = 0;
				double totalRaccTotal = 0;
				double totalPaccTotal = 0;
		
		       	while(pmConnProjects.next()) {
		
					// Pedido
		       		int orderId = 0;
					orderId = pmConnProjects.getInt("proj_orderid");
					
		 	  		// Cliente
					String customer = "";
					if (pmConnProjects.getString("customers", "cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY)) {
						customer = pmConnProjects.getString("customers", "cust_code") + " "
								+ pmConnProjects.getString("customers", "cust_legalname");
					} else {
						customer = pmConnProjects.getString("customers", "cust_code") + " "
								+ pmConnProjects.getString("customers", "cust_displayname");
					}
		
					double totalOrder = 0;
					// Obtener datos de pedido autorizado
					sql = " SELECT orde_currencyid, orde_currencyparity, orde_total FROM orders " 
							+ " WHERE orde_orderid = " 	+ orderId
							+ " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
					pmConn.doFetch(sql);
					if (pmConn.next()) {
						// Conversion a la moneda destino(seleccion del filtro)
						currencyIdOrigin = pmConn.getInt("orde_currencyid");
						parityOrigin = pmConn.getDouble("orde_currencyparity");
						currencyIdDestiny = currencyId;
						parityDestiny = defaultParity;
						
						double ordeTotal = pmConn.getDouble("orde_total");
						totalOrder = pmCurrencyExchange.currencyExchange(ordeTotal,
								currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					}
					
					// Obtener las CXC (pagos de MB) ligadas al pedido 
					// de tipo abono, porque es lo que entro
					double sumRacc = 0, sumRaccA = 0;
					sql = " SELECT bkmc_amount, bkac_currencyid, bkmc_currencyparity "
							+ " FROM bankmovconcepts "
							+ " LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid) "
							+ " LEFT JOIN bankmovtypes ON (bkmt_bankmovtypeid = bkmv_bankmovtypeid) "
							+ " LEFT JOIN bankaccounts ON (bkac_bankaccountid = bkmv_bankaccountid) "
							+ " LEFT JOIN raccounts ON (racc_raccountid = bkmc_raccountid) "
							+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
							+ " WHERE racc_orderid = " + orderId
							+ " AND bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "'";
		
					pmConn.doFetch(sql);
					while (pmConn.next()) {
		
						// Conversion a la moneda destino(seleccion del filtro)
						currencyIdOrigin = pmConn.getInt("bkac_currencyid");
						parityOrigin = pmConn.getDouble("bkmc_currencyparity");
						currencyIdDestiny = currencyId;
						parityDestiny = defaultParity;
						
						double bkmcAmount = pmConn.getDouble("bkmc_amount");
						bkmcAmount = pmCurrencyExchange.currencyExchange(bkmcAmount,
								currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						sumRacc += bkmcAmount;	
					}
					
					// Obtener las CXC autorizadas(cargos) ligadas al pedido 
					sql = " SELECT racc_currencyid, racc_currencyparity, racc_total FROM raccounts "
							+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
							+ " WHERE racc_orderid = " + orderId 
							+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
							+ " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_PENDING + "'"
							+ " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
					pmConn.doFetch(sql);
					while (pmConn.next()) {
						// Conversion a la moneda destino(seleccion del filtro)
						currencyIdOrigin = pmConn.getInt("racc_currencyid");
						parityOrigin = pmConn.getDouble("racc_currencyparity");
						currencyIdDestiny = currencyId;
						parityDestiny = defaultParity;
						
						double raccTotal = pmConn.getDouble("racc_total");
						raccTotal = pmCurrencyExchange.currencyExchange(raccTotal,
								currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						sumRaccA += raccTotal;
					}
		
					// Obtener las ordenes de compra autorizadas
					double totalReqi = 0;
					int reqiId = 0;
					sql = " SELECT reqi_total, reqi_currencyid, reqi_currencyparity FROM requisitions "
							+ " WHERE reqi_orderid = " + orderId 
							+ " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'";
					pmConn.doFetch(sql);
					while (pmConn.next()) {
						// Conversion a la moneda destino(seleccion del filtro)
						currencyIdOrigin = pmConn.getInt("reqi_currencyid");
						parityOrigin = pmConn.getDouble("reqi_currencyparity");
						currencyIdDestiny = currencyId;
						parityDestiny = defaultParity;
		
						double reqiTotal = pmConn.getDouble("reqi_total");
						reqiTotal = pmCurrencyExchange.currencyExchange(reqiTotal,
								currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						
						totalReqi += reqiTotal;
					}
		
					// Obtener las CXP(pagos de MB) ligadas a la OC del pedido
					// de tipo cargo, porque es lo que salio
					double sumPacc = 0, sumPaccA = 0;
					sql = " SELECT bkmc_amount, bkac_currencyid, bkmc_currencyparity, pacc_code "
							+ " FROM bankmovconcepts "
							+ " LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid) "
							+ " LEFT JOIN bankmovtypes ON (bkmt_bankmovtypeid = bkmv_bankmovtypeid) "
							+ " LEFT JOIN bankaccounts ON (bkac_bankaccountid = bkmv_bankaccountid) "
							+ " LEFT JOIN paccounts ON (pacc_paccountid = bkmc_paccountid) "
							+ " LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) "
		 					+ " LEFT JOIN requisitionreceipts ON(rerc_requisitionreceiptid = pacc_requisitionreceiptid) "
							+ " LEFT JOIN requisitions ON (reqi_requisitionid = pacc_requisitionid) "
							+ " WHERE reqi_orderid = " + orderId
							+ " AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "'";
		
					pmConn.doFetch(sql);
					while (pmConn.next()) {
						// Conversion a la moneda destino(seleccion del filtro)
						currencyIdOrigin = pmConn.getInt("bkac_currencyid");
						parityOrigin = pmConn.getDouble("bkmc_currencyparity");
						currencyIdDestiny = currencyId;
						parityDestiny = defaultParity;
						
						double bkmcAmount = pmConn.getDouble("bkmc_amount");
						bkmcAmount = pmCurrencyExchange.currencyExchange(bkmcAmount,
								currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						
						sumPacc += bkmcAmount;
					}
					// Obtener las CXP(cargo) ligadas a la OC del pedido
					sql = " SELECT pacc_total, pacc_currencyid, pacc_currencyparity FROM paccounts "
							+ " LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) "
							+ " LEFT JOIN requisitionreceipts ON(rerc_requisitionreceiptid = pacc_requisitionreceiptid) "
							+ " LEFT JOIN requisitions ON(reqi_requisitionid = rerc_requisitionid) "
							+ " WHERE reqi_orderid = " + orderId 
							+ " AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" 
							+ " AND pacc_paymentstatus = '" + BmoPaccount.PAYMENTSTATUS_PENDING + "' "	
							+ " AND pacc_status = '" + BmoPaccount.STATUS_AUTHORIZED + "'";
					pmConn.doFetch(sql);
					while (pmConn.next()) {
						// Conversion a la moneda destino(seleccion del filtro)
						currencyIdOrigin = pmConn.getInt("pacc_currencyid");
						parityOrigin = pmConn.getDouble("pacc_currencyparity");
						currencyIdDestiny = currencyId;
						parityDestiny = defaultParity;
		
						double paccTotal = pmConn.getDouble("pacc_total");
						sumPaccA += pmCurrencyExchange.currencyExchange(paccTotal,
								currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						
						sumPaccA += paccTotal;				
					}
				%>
		
				<tr class="reportCellEven">
					<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConnProjects.getString("projects", "proj_code") + " " + HtmlUtil.stringToHtml(pmConnProjects.getString("projects", "proj_name")), BmFieldType.STRING) %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalOrder, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + (sumRacc - sumRaccA), BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalReqi, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + (sumPacc - sumPaccA), BmFieldType.CURRENCY) %>
				</tr>
				<%
					//Obtener los totales
					totalOrderTotal += totalOrder;
					totalReqiTotal += totalReqi;
					totalRaccTotal += (sumRacc - sumRaccA);
					totalPaccTotal += (sumPacc - sumPaccA);
		
		       		i++;
		       		y++;
		   		}
		    %>
				<tr><td colspan="7">&nbsp;</td></tr>
				<TR class="reportCellEven reportCellCode">
					<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
					<td align="right" width="40" colspan="2">&nbsp;</td>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalOrderTotal, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalRaccTotal, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalReqiTotal, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalPaccTotal, BmFieldType.CURRENCY) %>
				</TR>
				<tr><td colspan="7">&nbsp;</td></tr>
			</table>	
			<%
		} // Fin de  pmCurrencyWhile%>
		</table>
		<%
	} else {
	%>
	<table class="report">
		<tr class="">
			<th class="reportHeaderCellCenter">#</th>
			<th class="reportHeaderCell">Proyecto</th>
			<th class="reportHeaderCell">Cliente</th>
			<th class="reportHeaderCellRight">Pedido</th>
			<th class="reportHeaderCellRight">CXC</th>
			<th class="reportHeaderCellRight">O.C.</th>
			<th class="reportHeaderCellRight">CXP</th>
		</tr>
		<%

		//Listado de Proyectos
	   	sql = " SELECT proj_projectid, proj_code, proj_name, proj_orderid, cust_customertype, cust_code, cust_displayname, cust_legalname " 
	   			+ " FROM projects "
	   			+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
	   			+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
	   			+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
	   			+ " LEFT JOIN states ON (stat_stateid = city_stateid) "
	   			+ " LEFT JOIN countries ON (cont_countryid = stat_stateid ) "
	   			+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
	   			+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
	   			+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
	   			+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
	   			+ " LEFT JOIN users ON (user_userid = proj_userid) "
	   			+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
	   			+ " WHERE proj_projectid > 0 "
	   			+ where
			 	+ " ORDER BY proj_startdate ASC";
	         
	   	pmConnProjects.doFetch(sql);
	   
		int  i = 1, y = 0;
		double totalOrderTotal = 0;
		double totalReqiTotal = 0;
		double totalRaccTotal = 0;
		double totalPaccTotal = 0;

       	while(pmConnProjects.next()) {

			// Pedido
       		int orderId = 0;
			orderId = pmConnProjects.getInt("proj_orderid");
			
 	  		// Cliente
			String customer = "";
			if (pmConnProjects.getString("customers", "cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY)) {
				customer = pmConnProjects.getString("customers", "cust_code") + " "
						+ pmConnProjects.getString("customers", "cust_legalname");
			} else {
				customer = pmConnProjects.getString("customers", "cust_code") + " "
						+ pmConnProjects.getString("customers", "cust_displayname");
			}

			double totalOrder = 0;
			// Obtener datos de pedido autorizado
			sql = " SELECT orde_currencyid, orde_currencyparity, orde_total FROM orders " 
					+ " WHERE orde_orderid = " 	+ orderId
					+ " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				// Conversion a la moneda destino(seleccion del filtro)
				currencyIdOrigin = pmConn.getInt("orde_currencyid");
				parityOrigin = pmConn.getDouble("orde_currencyparity");
				currencyIdDestiny = currencyId;
				parityDestiny = defaultParity;
				
				double ordeTotal = pmConn.getDouble("orde_total");
				totalOrder = pmCurrencyExchange.currencyExchange(ordeTotal,
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			}
			
			// Obtener las CXC (pagos de MB) ligadas al pedido 
			// de tipo abono, porque es lo que entro
			double sumRacc = 0, sumRaccA = 0;
			sql = " SELECT bkmc_amount, bkac_currencyid, bkmc_currencyparity "
					+ " FROM bankmovconcepts "
					+ " LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid) "
					+ " LEFT JOIN bankmovtypes ON (bkmt_bankmovtypeid = bkmv_bankmovtypeid) "
					+ " LEFT JOIN bankaccounts ON (bkac_bankaccountid = bkmv_bankaccountid) "
					+ " LEFT JOIN raccounts ON (racc_raccountid = bkmc_raccountid) "
					+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
					+ " WHERE racc_orderid = " + orderId
					+ " AND bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "'";

			pmConn.doFetch(sql);
			while (pmConn.next()) {

				// Conversion a la moneda destino(seleccion del filtro)
				currencyIdOrigin = pmConn.getInt("bkac_currencyid");
				parityOrigin = pmConn.getDouble("bkmc_currencyparity");
				currencyIdDestiny = currencyId;
				parityDestiny = defaultParity;
				
				double bkmcAmount = pmConn.getDouble("bkmc_amount");
				bkmcAmount = pmCurrencyExchange.currencyExchange(bkmcAmount,
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				sumRacc += bkmcAmount;	
			}
			
			// Obtener las CXC autorizadas(cargos) ligadas al pedido 
			sql = " SELECT racc_currencyid, racc_currencyparity, racc_total FROM raccounts "
					+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
					+ " WHERE racc_orderid = " + orderId 
					+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
					+ " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_PENDING + "' "
					+ " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				// Conversion a la moneda destino(seleccion del filtro)
				currencyIdOrigin = pmConn.getInt("racc_currencyid");
				parityOrigin = pmConn.getDouble("racc_currencyparity");
				currencyIdDestiny = currencyId;
				parityDestiny = defaultParity;
				
				double raccTotal = pmConn.getDouble("racc_total");
				raccTotal = pmCurrencyExchange.currencyExchange(raccTotal,
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				sumRaccA += raccTotal;
			}

			// Obtener las ordenes de compra autorizadas
			double totalReqi = 0;
			int reqiId = 0;
			sql = " SELECT reqi_total, reqi_currencyid, reqi_currencyparity FROM requisitions "
					+ " WHERE reqi_orderid = " + orderId 
					+ " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'";
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				// Conversion a la moneda destino(seleccion del filtro)
				currencyIdOrigin = pmConn.getInt("reqi_currencyid");
				parityOrigin = pmConn.getDouble("reqi_currencyparity");
				currencyIdDestiny = currencyId;
				parityDestiny = defaultParity;

				double reqiTotal = pmConn.getDouble("reqi_total");
				reqiTotal = pmCurrencyExchange.currencyExchange(reqiTotal,
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				
				totalReqi += reqiTotal;
			}

			// Obtener las CXP(pagos de MB) ligadas a la OC del pedido
			// de tipo cargo, porque es lo que salio
			double sumPacc = 0, sumPaccA = 0;
			sql = " SELECT bkmc_amount, bkac_currencyid, bkmc_currencyparity, pacc_code "
					+ " FROM bankmovconcepts "
					+ " LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid) "
					+ " LEFT JOIN bankmovtypes ON (bkmt_bankmovtypeid = bkmv_bankmovtypeid) "
					+ " LEFT JOIN bankaccounts ON (bkac_bankaccountid = bkmv_bankaccountid) "
					+ " LEFT JOIN paccounts ON (pacc_paccountid = bkmc_paccountid) "
					+ " LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) "
 					+ " LEFT JOIN requisitionreceipts ON(rerc_requisitionreceiptid = pacc_requisitionreceiptid) "
					+ " LEFT JOIN requisitions ON (reqi_requisitionid = pacc_requisitionid) "
					+ " WHERE reqi_orderid = " + orderId
					+ " AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "'";

			pmConn.doFetch(sql);
			while (pmConn.next()) {
				// Conversion a la moneda destino(seleccion del filtro)
				currencyIdOrigin = pmConn.getInt("bkac_currencyid");
				parityOrigin = pmConn.getDouble("bkmc_currencyparity");
				currencyIdDestiny = currencyId;
				parityDestiny = defaultParity;
				
				double bkmcAmount = pmConn.getDouble("bkmc_amount");
				bkmcAmount = pmCurrencyExchange.currencyExchange(bkmcAmount,
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				
				sumPacc += bkmcAmount;
			}
			// Obtener las CXP(cargo) ligadas a la OC del pedido
			sql = " SELECT pacc_total, pacc_currencyid, pacc_currencyparity FROM paccounts "
					+ " LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) "
					+ " LEFT JOIN requisitionreceipts ON(rerc_requisitionreceiptid = pacc_requisitionreceiptid) "
					+ " LEFT JOIN requisitions ON(reqi_requisitionid = rerc_requisitionid) "
					+ " WHERE reqi_orderid = " + orderId 
					+ " AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" 
					+ " AND pacc_paymentstatus = '" + BmoPaccount.PAYMENTSTATUS_PENDING + "' "	
					+ " AND pacc_status = '" + BmoPaccount.STATUS_AUTHORIZED + "'";
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				// Conversion a la moneda destino(seleccion del filtro)
				currencyIdOrigin = pmConn.getInt("pacc_currencyid");
				parityOrigin = pmConn.getDouble("pacc_currencyparity");
				currencyIdDestiny = currencyId;
				parityDestiny = defaultParity;

				double paccTotal = pmConn.getDouble("pacc_total");
				sumPaccA += pmCurrencyExchange.currencyExchange(paccTotal,
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				
				sumPaccA += paccTotal;				
			}
		%>

		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
			<%= HtmlUtil.formatReportCell(sFParams, pmConnProjects.getString("projects", "proj_code") + " " + HtmlUtil.stringToHtml(pmConnProjects.getString("projects", "proj_name")), BmFieldType.STRING) %>
			<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalOrder, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + (sumRacc - sumRaccA), BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalReqi, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + (sumPacc - sumPaccA), BmFieldType.CURRENCY) %>
		</tr>
		<%
			//Obtener los totales
			totalOrderTotal += totalOrder;
			totalReqiTotal += totalReqi;
			totalRaccTotal += (sumRacc - sumRaccA);
			totalPaccTotal += (sumPacc - sumPaccA);

       		i++;
       		y++;
   		}
    %>
		<tr>
			<td colspan="7">&nbsp;</td>
		</tr>
		<TR class="reportCellEven reportCellCode">
			<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
			<td align="right" width="40" colspan="2">&nbsp;</td>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalOrderTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalRaccTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalReqiTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalPaccTotal, BmFieldType.CURRENCY) %>
		</TR>
	</table>

	<%
	}
	}//Fin contador de registros
			
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 	
}// Fin de if(no carga datos)
pmConnCount.close();
	pmConn.close();
pmConnProjects.close(); 
pmCurrencyWhile.close();
%>
	<script>
		function doPrint() {
			var img = document.getElementById('printImage');
    		img.style.visibility = 'hidden';
    		
    		var img2 = document.getElementById('exportExcel');
    		img2.style.visibility = 'hidden';
    		
			window.print();
			
			img.style.visibility = 'visible';
			img2.style.visibility = 'visible';			
		}
	</script>
</body>
</html>