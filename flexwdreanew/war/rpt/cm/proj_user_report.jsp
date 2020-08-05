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
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@include file="/inc/login.jsp"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%
	// Inicializar variables
	String title = "Proyectos por Vendedor";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoProject bmoProject = new BmoProject();
	BmoOrder bmoOrder = new BmoOrder();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange = new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram = new PmProgram(sFParams);

	String sql = "", sqlCurrency = "", where = "", groupFilter = "", filters = "", status = "", startDate = "",
			paymentStatus = "", endDate = "", startDateCreate = "", endDateCreate = "", customer = "";
	int wflowTypeId = 0, projectId = 0, areaId = 0, wflowCategoryId = 0, venueId = 0, userId = 0,
			customerId = 0, referralId = 0, wflowPhaseId = 0, programId = 0, currencyId = 0, dynamicColspan = 0,
			dynamicColspanMinus = 0;

	double nowParity = 0, defaultParity = 0;

	// Obtener parametros
	if (request.getParameter("programId") != null)
		programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("proj_userid") != null)
		userId = Integer.parseInt(request.getParameter("proj_userid"));
	if (request.getParameter("proj_customerid") != null)
		customerId = Integer.parseInt(request.getParameter("proj_customerid"));
	if (request.getParameter("wfty_wflowcategoryid") != null)
		wflowCategoryId = Integer.parseInt(request.getParameter("wfty_wflowcategoryid"));
	if (request.getParameter("wflw_wflowtypeid") != null)
		wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
	if (request.getParameter("wflw_wflowphaseid") != null)
		wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
	if (request.getParameter("proj_venueid") != null)
		venueId = Integer.parseInt(request.getParameter("proj_venueid"));
	if (request.getParameter("proj_startdate") != null)
		startDate = request.getParameter("proj_startdate");
	if (request.getParameter("proj_enddate") != null)
		endDate = request.getParameter("proj_enddate");
	if (request.getParameter("proj_status") != null)
		status = request.getParameter("proj_status");
	if (request.getParameter("cust_referralid") != null)
		referralId = Integer.parseInt(request.getParameter("cust_referralid"));
	if (request.getParameter("startdatecreateproject") != null)
		startDateCreate = request.getParameter("startdatecreateproject");
	if (request.getParameter("enddatecreateproject") != null)
		endDateCreate = request.getParameter("enddatecreateproject");
	if (request.getParameter("paymentStatus") != null)
		paymentStatus = request.getParameter("paymentStatus");
	if (request.getParameter("user_areaid") != null)
		areaId = Integer.parseInt(request.getParameter("user_areaid"));
	if (request.getParameter("proj_projectid") != null)
		projectId = Integer.parseInt(request.getParameter("proj_projectid"));
	if (request.getParameter("proj_currencyid") != null)
		currencyId = Integer.parseInt(request.getParameter("proj_currencyid"));

	bmoProgram = (BmoProgram) pmProgram.get(programId);

	// Construir filtros 
	// Filtro agrupacion
	if (userId > 0) {
		groupFilter = " AND proj_userid = " + userId;
		/*
		if (sFParams.restrictData(bmoProject.getProgramCode())) {
			groupFilter += " AND proj_userid = " + userId;
		} else {
			groupFilter += " AND ( " + " proj_userid = " + userId + " OR proj_wflowid IN ( "
					+ " SELECT wflw_wflowid FROM wflowusers  "
					+ " LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " + " WHERE wflu_userid = " + userId
					+ " AND (wflw_callercode = '" + bmoProject.getProgramCode().toString()
					+ "' OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "') " + " ) " + " )";
		}
*/
		filters += "<i>Vendedor: </i>" + request.getParameter("proj_useridLabel") + ", ";
	}

	// Filtros listados
	if (wflowCategoryId > 0) {
		where += " AND wfty_wflowcategoryid = " + wflowCategoryId;
		filters += "<i>Categor&iacute;a de Flujo: </i>" + request.getParameter("wfty_wflowcategoryidLabel")
				+ ", ";
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
		filters += "<i>Lugar: </i>" + request.getParameter("venu_venueidLabel") + ", ";
	}

	if (!startDate.equals("")) {
		where += " AND proj_startdate >= '" + startDate + " 00:00'";
		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
	}

	if (!endDate.equals("")) {
		where += " AND proj_startdate <= '" + endDate + " 23:59'";
		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
	}

	if (customerId > 0) {
		where += " AND proj_customerid = " + customerId;
		filters += "<i>Cliente: </i>" + request.getParameter("proj_customeridLabel") + ", ";
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
				+ " LEFT JOIN users ON (user_userid = proj_userid) " + " WHERE wfca_programid = " + programId
				+ groupFilter + where + " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
	}

	if (sFParams.getSelectedCompanyId() > 0)
		filters += "<i>Empresa: </i>" + sFParams.getBmoSelectedCompany().getName().toString() + " | "
				+ sFParams.getBmoSelectedCompany().getName().toString() + ", ";

	//Conexion a Base de Datos
	PmConn pmConnChartGroup = new PmConn(sFParams);
	PmConn pmConnChart = new PmConn(sFParams);

	PmConn pmConnCXC = new PmConn(sFParams);
	pmConnCXC.open();

	PmConn pmConnGroup = new PmConn(sFParams);
	pmConnGroup.open();

	PmConn pmConnList = new PmConn(sFParams);
	pmConnList.open();

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
	if ((String) request.getParameter("print") != null)
		print = (String) request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String) request.getParameter("exportexcel") != null)
		exportExcel = (String) request.getParameter("exportexcel");
	if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=\"" + title + ".xls\"");
	}

	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
	//En caso de que mande a imprimir, deshabilita contenido
	if (!(sFParams.hasPrint(bmoProgram.getCode().toString()))) {
		%>
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
			if (print.equals("1") || exportExcel.equals("1")) {
			%>
				<script>
					alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
				</script>
			<%
			}
	}

	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
	if (sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) {
%>
<head>
	<title>:::<%=appTitle%>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>

<body class="default" <%=permissionPrint%> style="padding-right: 10px">

	<table border="0" cellspacing="0" cellpading="0" width="100%">
		<tr>
			<td align="left" width="80" rowspan="2" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>"
				src="<%=sFParams.getMainImageUrl()%>"></td>
			<td class="reportTitle" align="left" colspan="2"><%=title%></td>
		</tr>
		<tr>
			<td class="reportSubTitle"><b>Filtros:</b> <%=filters%> <br>
				<b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Vendedor" : "Vendedor")%>,
				<b>Ordenado por:</b> Fecha de Evento
			</td>
			<td class="reportDate" align="right">Creado: <%=SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat())%>
				por: <%=sFParams.getLoginInfo().getEmailAddress()%>
			</td>
		</tr>
	</table>

	<%

	sql = " SELECT COUNT(*) AS contador FROM projects "
			+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
			+ " LEFT JOIN referrals ON (refe_referralid = cust_referralid) "
			+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
			+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
			+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
			+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
			+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
			+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
			+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
			+ " LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "
			+ " LEFT JOIN users ON (user_userid = proj_userid) " + " WHERE proj_projectid > 0 "
			+ groupFilter
			+ where
			+ " ";
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
				pmConnChartGroup.open();
				%>
				<table class="report" style="width: 100%;">
					<%
					if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
		        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
						currencyId = currencyIdWhile;
				    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
		        		%>
		        		<tr>
		            		<td class="reportHeaderCellCenter" colspan="2">
		            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
		            		</td>
		        		</tr>
		        		<%
		        	}
					%>
					<script type="text/javascript" src="https://www.google.com/jsapi"></script>
					<script type="text/javascript">
				    	google.load("visualization", "1", {packages:["corechart"]});
				      	google.setOnLoadCallback(drawChart);
				      	function drawChart() {
				        	var data = google.visualization.arrayToDataTable([
				          	['Vendedor', 'Proyectos'],
							<%
							sql = " SELECT user_userid, user_email, user_code, COUNT(proj_projectid) AS c, SUM(orde_total) AS s FROM projects "
									+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
									+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
									+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
									+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
									+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
									+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
									+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
									+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
									+ " LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "
									+ " LEFT JOIN users ON (proj_userid = user_userid) " 
									+ " WHERE wfca_programid = " + programId 
									+ " AND orde_currencyid =  " + currencyId
									+ where
									+ groupFilter 
									+ " GROUP BY user_userid ORDER BY user_email";

					        pmConnChartGroup.doFetch(sql);
							while (pmConnChartGroup.next()) { %>
					       		<%= "['" + pmConnChartGroup.getString("user_code") + "', " + pmConnChartGroup.getInt("c") + "],"%>          
					       	<%
					       	}
					       	%>
				        	]);
				
					        var options = {
				        		title: 'Porcentaje por Vendedor',
				          		pieHole: 0.4,
					        };
					
					        var chart = new google.visualization.PieChart(document.getElementById('donutchart<%= pmCurrencyWhile.getString("cure_code")%>'));
					        chart.draw(data, options);
				      	}
			    	</script>
				
					<script type="text/javascript">
				    	google.load("visualization", "1", {packages:["corechart"]});
				      	google.setOnLoadCallback(drawChart);
				     	function drawChart() {
				        	var data = google.visualization.arrayToDataTable([
				          	['Vendedor', 'Valor'],
							<%

							pmConnChartGroup.beforeFirst();
						
							while (pmConnChartGroup.next()) {
							%>
			       				<%= "['" + pmConnChartGroup.getString("users", "user_code") + "', " + pmConnChartGroup.getDouble("s") + "]," %> 
					      	<%
							}
					       	%>
				        	]);
				
				        	var options = {
				          		title: 'Valor por Vendedor',
				          		vAxis: {minValue: 0},
				          		animation: {duration: 10000, easing: "inAndOut"},
				          		legend: { position: "none"},
				          		colors:['MediumSeaGreen']
				        	};
				
				        	var chart = new google.visualization.ColumnChart(document.getElementById('typechart<%= pmCurrencyWhile.getString("cure_code")%>'));
				        	chart.draw(data, options);
				      }
				      <%
						pmConnChartGroup.close();
				      %>
			    </script>
			    <table style="width: 100%;">
					<tr>
						<td width="50%" align="left">
							<div id="donutchart<%= pmCurrencyWhile.getString("cure_code")%>" align="left" style="width: 100%; height: 300px;"></div>
						</td>
						<td width="50%" align="left">
							<div id="typechart<%= pmCurrencyWhile.getString("cure_code")%>" align="left" style="width: 100%; height: 300px;"></div>
						</td>
					</tr>
				</table>
				<table class="report" style="width: 100%;">
				<%
		    		double amountTotal = 0, totalTotal = 0, discountTotal = 0, taxTotal = 0, paymentsTotal = 0, balanceTotal = 0;

					// Para este filtro de vendedor considerar el id del modulo proyectos
					pmConnGroup.doFetch(" SELECT user_userid, user_firstname, user_fatherlastname, user_motherlastname, user_email " 
										+ " FROM projects "
										+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
										+ " LEFT JOIN referrals ON (refe_referralid = cust_referralid) "
										+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
										+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
										+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
										+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
										+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
										+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
										+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
										+ " LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "
										+ " LEFT JOIN users ON (proj_userid = user_userid) "
										+ " WHERE wfca_programid = " + programId 
										+ " AND orde_currencyid =  " + currencyId
										+ groupFilter
										+ where 
										+ " GROUP BY user_userid ORDER BY user_email");

					int i = 1, y = 0;
					while (pmConnGroup.next()) {
						int newUserId = pmConnGroup.getInt("user_userid");

						sql = " SELECT * FROM projects "
								+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
								+ " LEFT JOIN referrals ON (refe_referralid = cust_referralid) "
								+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
								+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
								+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
								+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
								+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
								+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
								+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
								+ " LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "
								+ " LEFT JOIN users ON (user_userid = proj_userid) " 
								+ " WHERE proj_projectid > 0 "
								+ " AND orde_currencyid =  " + currencyId
								+ " AND proj_userid = " + newUserId 
								+ " AND (proj_userid = "+ ((userId > 0) ? "" + userId : "" + newUserId) 
								+ " OR proj_wflowid IN ( "
								+ " SELECT wflw_wflowid FROM wflowusers  "
								+ " LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " 
								+ " WHERE wflu_userid = " + ((userId > 0) ? "" + userId : "" + newUserId) 
								+ " AND (wflw_callercode = '" + bmoProject.getProgramCode().toString() + "'"
								+ " OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "') " 
								+ " ) " + " )" 
								+ where
								+ " ORDER BY proj_startdate ASC";
						pmConnList.doFetch(sql);

						i = 1;
						double amountSum = 0, totalSum = 0, discountSum = 0, taxSum = 0, paymentsSum = 0, balanceSum = 0;
						int userid = 0;
						while (pmConnList.next()) {
							// Estatus
							bmoProject.getStatus().setValue(pmConnList.getString("projects", "proj_status"));
						    bmoOrder.getPaymentStatus().setValue(pmConnList.getString("orders", "orde_paymentstatus"));
							// Valores
		   					double amount = pmConnList.getDouble("orde_amount");
		   					double discount = pmConnList.getDouble("orde_discount");
		   					double tax = pmConnList.getDouble("orde_tax");
		   					double total = pmConnList.getDouble("orde_total");

		   					//Conversion a la moneda destino(seleccion del filtro)
		   					int currencyIdOrigin = 0;
		   					int currencyIdDestiny = 0;
		   					double parityOrigin = 0;
		   					double parityDestiny = 0;
// 		   					currencyIdOrigin = pmConnList.getInt("orde_currencyid");
// 		   					parityOrigin = pmConnList.getDouble("orde_currencyparity");
// 		   					currencyIdDestiny = currencyId;
// 		   					parityDestiny = defaultParity;

// 		   					amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
// 		   					discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
// 		   					tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
// 		   					total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);

		   					//Sumas por tipo
		   					amountSum += amount;
		   					discountSum += discount;
		   					taxSum += tax;
		   					totalSum += total;
		   					//Sumas general
		   					amountTotal += amount;
		   					discountTotal += discount;
		   					taxTotal += tax;
		   					totalTotal += total;

							if ((pmConnGroup.getInt("users", "user_userid") != userid)) {
								userid = pmConnGroup.getInt("users", "user_userid");
								%>
								<tr>
									<td colspan="23" class="reportGroupCell">
										<%=HtmlUtil.stringToHtml(pmConnGroup.getString("user_firstname"))%>
										<%=HtmlUtil.stringToHtml(pmConnGroup.getString("user_fatherlastname"))%>
										<%=HtmlUtil.stringToHtml(pmConnGroup.getString("user_motherlastname"))%>
									</td>
								</tr>
								<tr class="">
									<td class="reportHeaderCell">#</td>
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
									<td class="reportHeaderCellRight">Subtotal</td>
									<td class="reportHeaderCellRight">Descuentos</td>
									<td class="reportHeaderCellRight">IVA</td>
									<td class="reportHeaderCellRight">Valor</td>
									<td class="reportHeaderCellRight">Estatus Pago</td>
									<td class="reportHeaderCellRight">Pagos</td>
									<td class="reportHeaderCellRight">Saldo</td>
								</tr>
							<%
							}
							%>
							<tr class="reportCellEven">
								<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_code") + " "+ HtmlUtil.stringToHtml(pmConnList.getString("projects", "proj_name")),
																BmFieldType.STRING)%>
								<%
								if (pmConnList.getString("customers","cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY)) {
			                		customer = pmConnList.getString("customers","cust_code") + " " +
			                				pmConnList.getString("customers","cust_legalname");
			                	} else { 
			                		customer = pmConnList.getString("customers","cust_code") + " " +
			                				pmConnList.getString("customers","cust_displayname");
			                	}
								%>
								<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>
					
								<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("referrals", "refe_name")), BmFieldType.STRING) %>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("venues", "venu_code") + " " 
										+ HtmlUtil.stringToHtml(pmConnList.getString("venues","venu_name")), BmFieldType.STRING) %>
					
								<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("cities", "city_name")), BmFieldType.STRING)%>
					
								<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowcategories", "wfca_name")), BmFieldType.STRING)%>
					
								<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowtypes", "wfty_name")), BmFieldType.STRING)%>
					
								<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowphases", "wfph_name")), BmFieldType.STRING)%>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflows", "wflw_progress"), BmFieldType.PERCENTAGE)%>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_startdate"), BmFieldType.STRING)%>
					
								<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoProject.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("users", "user_code"), BmFieldType.STRING)%>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_datecreateproject"), BmFieldType.STRING)%>
								
								<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getString("currencies", "cure_code"), BmFieldType.CODE) %>
							    
							    <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
							    	if (currencyIdOrigin != currencyIdDestiny) {
						        		if (bmoCurrency.getCode().toString().equals("USD")) {
					        	%>
					    					<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
							    <%		} else { %>
					    					<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
							    <%		}
							    	} else { %>
					    				<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
							    <%	}%>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("orde_amount"), BmFieldType.CURRENCY) %>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("orde_discount"), BmFieldType.CURRENCY) %>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("orde_tax"), BmFieldType.CURRENCY) %>
					
								<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("orde_total"), BmFieldType.CURRENCY) %>
					
								<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
								<%
							    //CXC Pagadas
								String sqlCXCPagadas = " SELECT racc_currencyid, racc_currencyparity, racc_total AS pagosSum " + 
												  " FROM raccounts " +
												  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
												  " WHERE racc_orderid = " + pmConnList.getInt("proj_orderid") +
												  " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT  + "'" +
											      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
								
								//System.out.println("sqlCXCPagadas: "+sqlCXCPagadas);
								double payments = 0;
							    pmConnCXC.doFetch(sqlCXCPagadas);
								while (pmConnCXC.next()) {
									
									// Conversion a la moneda destino(del filtro)
					          		currencyIdOrigin = 0; currencyIdDestiny = 0;
					          		parityOrigin = 0; parityDestiny = 0;
					          		currencyIdOrigin = pmConnCXC.getInt("racc_currencyid");
					          		parityOrigin = pmConnCXC.getDouble("racc_currencyparity");
					          		currencyIdDestiny = currencyId;
					          		parityDestiny = defaultParity;

					          		payments += pmCurrencyExchange.currencyExchange(pmConnCXC.getDouble("pagosSum"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
								}
								paymentsSum += payments;
								paymentsTotal += payments;
							    %>
							    <%= HtmlUtil.formatReportCell(sFParams, "" + payments, BmFieldType.CURRENCY) %>
							    <%
							  	//CXC Saldo Cliente
								String sqlCXCSaldo = " SELECT racc_currencyid, racc_currencyparity, racc_total AS saldoSum " + 
												  " FROM raccounts " +
												  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
												  " WHERE racc_orderid = " + pmConnList.getInt("proj_orderid") +
												  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW  + "'" +
												  " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' ";
								
								//System.out.println("sqlCXCSaldo: "+sqlCXCSaldo);
								double balance = 0, totalRacc = 0;
							    pmConnCXC.doFetch(sqlCXCSaldo);
								while (pmConnCXC.next()) {
									
									// Conversion a la moneda destino(del filtro)
					          		currencyIdOrigin = 0; currencyIdDestiny = 0;
					          		parityOrigin = 0; parityDestiny = 0;
					          		currencyIdOrigin = pmConnCXC.getInt("racc_currencyid");
					          		parityOrigin = pmConnCXC.getDouble("racc_currencyparity");
					          		currencyIdDestiny = currencyId;
					          		parityDestiny = defaultParity;
					          		
					          		totalRacc += pmCurrencyExchange.currencyExchange(pmConnCXC.getDouble("saldoSum"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);				          		
								}
								balance = total - totalRacc;
								balanceSum += balance;
								balanceTotal += balance;
							    %>
						    
						    	<%= HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>
							</tr>
							<%
							i++;
							y++;
						} // Fin pmConnList
						
		     			if (amountSum > 0 || discountSum > 0 || taxSum > 0 || totalSum > 0 || paymentsSum > 0 || balanceSum > 0) {
							%>
							<tr class="reportCellEven reportCellCode">
								<td colspan="16" align="right">&nbsp;</td>
								<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
							    <%= HtmlUtil.formatReportCell(sFParams, "" + discountSum, BmFieldType.CURRENCY) %>
							    <%= HtmlUtil.formatReportCell(sFParams, "" + taxSum, BmFieldType.CURRENCY) %>
							    <%= HtmlUtil.formatReportCell(sFParams, "" + totalSum, BmFieldType.CURRENCY) %>
								<%= HtmlUtil.formatReportCell(sFParams, "" , BmFieldType.STRING) %>
							    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsSum, BmFieldType.CURRENCY) %>
							    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceSum, BmFieldType.CURRENCY) %>
							</tr>
							<%
		     			}
					} // FIn pmConnGroup
					%>
					<tr><td colspan="23">&nbsp;</td></tr>
					<TR class="reportCellEven reportCellCode">
						<%=HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER)%>
						<td align="right" colspan="15">&nbsp;</td>
						<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
					</TR>
					<tr><td colspan="23">&nbsp;</td></tr>
				<%
			} // Fin de  pmCurrencyWhile %>
		</table>
			<%
	} 
	// Existe moneda destino
	else {
		int currencyIdOrigin = 0, currencyIdDestiny = 0;
      	double parityOrigin = 0, parityDestiny = 0;

      	pmConnChartGroup.open();
      	pmConnChart.open();
	%>
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<script type="text/javascript">
	      google.load("visualization", "1", {packages:["corechart"]});
	      google.setOnLoadCallback(drawChart);
	      function drawChart() {
	        var data = google.visualization.arrayToDataTable([
	          ['Vendedor', 'Proyectos'],
				<%
				sql = " SELECT user_userid, user_email, user_code, COUNT(proj_projectid) AS c FROM projects "
						+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
						+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
						+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
						+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
						+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
						+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
						+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
						+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
						+ " LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "
						+ " LEFT JOIN users ON (proj_userid = user_userid) " 
						+ " WHERE wfca_programid = " + programId 
						+ where
						+ groupFilter 
						+ " GROUP BY user_userid ORDER BY user_email";

		        pmConnChartGroup.doFetch(sql);
				while (pmConnChartGroup.next()) { %>
		       		<%= "['" + pmConnChartGroup.getString("user_code") + "', " + pmConnChartGroup.getInt("c") + "],"%>          
		       	<%
		       	}
		       	%>
	        ]);
	
	        var options = {
        		title: 'Porcentaje por Vendedor',
          		pieHole: 0.4,
	        };
	
	        var chart = new google.visualization.PieChart(document.getElementById('donutchart'));
	        chart.draw(data, options);
	      }
    </script>
	
	<script type="text/javascript">
	      google.load("visualization", "1", {packages:["corechart"]});
	      google.setOnLoadCallback(drawChart);
	      function drawChart() {
	        var data = google.visualization.arrayToDataTable([
	          ['Vendedor', 'Valor'],
			<%
			sql = " SELECT user_userid, user_email, user_code FROM projects "
					+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
					+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
					+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
					+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
					+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
					+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
					+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
					+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
					+ " LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "
					+ " LEFT JOIN users ON (proj_userid = user_userid) " 
					+ " WHERE wfca_programid = " + programId 
					+ where
					+ groupFilter 
					+ " GROUP BY user_userid ORDER BY user_email";

			pmConnChartGroup.doFetch(sql);
			
			while (pmConnChartGroup.next()) { 
				sql = " SELECT orde_currencyid, orde_currencyparity, orde_total FROM projects "
						+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
						+ " LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "
						+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
						+ " LEFT JOIN referrals ON (refe_referralid = cust_referralid) "
						+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
						+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
						+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
						+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
						+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
						+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
						+ " LEFT JOIN users ON (user_userid = proj_userid) " 
						+ " WHERE wfca_programid = " + programId 
						+ " AND proj_userid = " + pmConnChartGroup.getInt("users", "user_userid") 
						+ where
						//+ groupFilter 
						+ " ORDER BY user_email";

				pmConnChart.doFetch(sql);
				double totalTotalChart = 0;
				while (pmConnChart.next()) {
					//Conversion a la moneda destino(seleccion del filtro)
					currencyIdOrigin = 0;
					currencyIdDestiny = 0;
					parityOrigin = 0;
					parityDestiny = 0;
					currencyIdOrigin = pmConnChart.getInt("orde_currencyid");
					parityOrigin = pmConnChart.getDouble("orde_currencyparity");
					currencyIdDestiny = currencyId;
					parityDestiny = defaultParity;

					totalTotalChart += pmCurrencyExchange.currencyExchange(pmConnChart.getDouble("orde_total"),
							currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				}
				%>
       			<%= "['" + pmConnChartGroup.getString("users", "user_code") + "', " + totalTotalChart + "]," %> 
	      	<%
			}
	       	%>
	        ]);
	
	        var options = {
	          title: 'Valor por Vendedor',
	          vAxis: {minValue: 0},
	          animation: {duration: 10000, easing: "inAndOut"},
	          legend: { position: "none"},
	          colors:['MediumSeaGreen']
	        };
	
	        var chart = new google.visualization.ColumnChart(document.getElementById('typechart'));
	        chart.draw(data, options);
	      }
	      <%
	      	pmConnChart.close();
			pmConnChartGroup.close();
	      %>
	    </script>
	    <table style="width: 100%;">
			<tr>
				<td width="50%" align="left">
					<div id="donutchart" align="left" style="width: 100%; height: 300px;"></div>
				</td>
				<td width="50%" align="left">
					<div id="typechart" align="left" style="width: 100%; height: 300px;"></div>
				</td>
			</tr>
		</table>
		<table class="report" style="width: 100%;">
		<%
    		double amountTotal = 0, totalTotal = 0, discountTotal = 0, taxTotal = 0, paymentsTotal = 0, balanceTotal = 0;

			// Para este filtro de vendedor considerar el id del modulo proyectos
			pmConnGroup.doFetch(" SELECT user_userid, user_firstname, user_fatherlastname, user_motherlastname, user_email " 
								+ " FROM projects "
								+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
								+ " LEFT JOIN referrals ON (refe_referralid = cust_referralid) "
								+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
								+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
								+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
								+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
								+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
								+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
								+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
								+ " LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "
								+ " LEFT JOIN users ON (proj_userid = user_userid) "
								+ " WHERE wfca_programid = " + programId 
								+ groupFilter
								+ where 
								+ " GROUP BY user_userid ORDER BY user_email");

			int i = 1, y = 0;
			while (pmConnGroup.next()) {
				int newUserId = pmConnGroup.getInt("user_userid");

				sql = " SELECT * FROM projects "
						+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
						+ " LEFT JOIN referrals ON (refe_referralid = cust_referralid) "
						+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
						+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
						+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
						+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
						+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
						+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
						+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
						+ " LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "
						+ " LEFT JOIN users ON (user_userid = proj_userid) " + " WHERE proj_projectid > 0 "
						+ " AND proj_userid = " + newUserId 
						+ " AND (proj_userid = "+ ((userId > 0) ? "" + userId : "" + newUserId) 
						+ " OR proj_wflowid IN ( "
						+ " SELECT wflw_wflowid FROM wflowusers  "
						+ " LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " 
						+ " WHERE wflu_userid = " + ((userId > 0) ? "" + userId : "" + newUserId) 
						+ " AND (wflw_callercode = '" + bmoProject.getProgramCode().toString() + "'"
						+ " OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "') " 
						+ " ) " + " )" 
						+ where
						+ " ORDER BY proj_startdate ASC";
				pmConnList.doFetch(sql);

				i = 1;
				double amountSum = 0, totalSum = 0, discountSum = 0, taxSum = 0, paymentsSum = 0, balanceSum = 0;
				int userid = 0;
				while (pmConnList.next()) {
					// Estatus
					bmoProject.getStatus().setValue(pmConnList.getString("projects", "proj_status"));
				    bmoOrder.getPaymentStatus().setValue(pmConnList.getString("orders", "orde_paymentstatus"));
					// Valores
   					double amount = pmConnList.getDouble("orde_amount");
   					double discount = pmConnList.getDouble("orde_discount");
   					double tax = pmConnList.getDouble("orde_tax");
   					double total = pmConnList.getDouble("orde_total");

   					//Conversion a la moneda destino(seleccion del filtro)
   					currencyIdOrigin = 0;
   					currencyIdDestiny = 0;
   					parityOrigin = 0;
   					parityDestiny = 0;
   					currencyIdOrigin = pmConnList.getInt("orde_currencyid");
   					parityOrigin = pmConnList.getDouble("orde_currencyparity");
   					currencyIdDestiny = currencyId;
   					parityDestiny = defaultParity;

   					amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
   					discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
   					tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
   					total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);

   					//Sumas por tipo
   					amountSum += amount;
   					discountSum += discount;
   					taxSum += tax;
   					totalSum += total;
   					//Sumas general
   					amountTotal += amount;
   					discountTotal += discount;
   					taxTotal += tax;
   					totalTotal += total;

					if ((pmConnGroup.getInt("users", "user_userid") != userid)) {
						userid = pmConnGroup.getInt("users", "user_userid");
						%>
						<tr>
							<td colspan="23" class="reportGroupCell">
								<%=HtmlUtil.stringToHtml(pmConnGroup.getString("user_firstname"))%>
								<%=HtmlUtil.stringToHtml(pmConnGroup.getString("user_fatherlastname"))%>
								<%=HtmlUtil.stringToHtml(pmConnGroup.getString("user_motherlastname"))%>
							</td>
						</tr>
						<tr class="">
							<td class="reportHeaderCell">#</td>
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
							<td class="reportHeaderCellRight">Subtotal</td>
							<td class="reportHeaderCellRight">Descuentos</td>
							<td class="reportHeaderCellRight">IVA</td>
							<td class="reportHeaderCellRight">Valor</td>
							<td class="reportHeaderCellRight">Estatus Pago</td>
							<td class="reportHeaderCellRight">Pagos</td>
							<td class="reportHeaderCellRight">Saldo</td>
						</tr>
					<%
					}
					%>
					<tr class="reportCellEven">
						<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
			
						<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_code") + " "+ HtmlUtil.stringToHtml(pmConnList.getString("projects", "proj_name")),
														BmFieldType.STRING)%>
						<%
						if (pmConnList.getString("customers","cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY)) {
	                		customer = pmConnList.getString("customers","cust_code") + " " +
	                				pmConnList.getString("customers","cust_legalname");
	                	} else { 
	                		customer = pmConnList.getString("customers","cust_code") + " " +
	                				pmConnList.getString("customers","cust_displayname");
	                	}
						%>
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>
			
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("referrals", "refe_name")), BmFieldType.STRING) %>
			
						<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("venues", "venu_code") + " " 
								+ HtmlUtil.stringToHtml(pmConnList.getString("venues","venu_name")), BmFieldType.STRING) %>
			
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("cities", "city_name")), BmFieldType.STRING)%>
			
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowcategories", "wfca_name")), BmFieldType.STRING)%>
			
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowtypes", "wfty_name")), BmFieldType.STRING)%>
			
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowphases", "wfph_name")), BmFieldType.STRING)%>
			
						<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflows", "wflw_progress"), BmFieldType.PERCENTAGE)%>
			
						<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_startdate"), BmFieldType.STRING)%>
			
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoProject.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
			
						<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("users", "user_code"), BmFieldType.STRING)%>
			
						<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("projects", "proj_datecreateproject"), BmFieldType.STRING)%>
						
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getString("currencies", "cure_code"), BmFieldType.CODE) %>
					    
					    <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
					    	if (currencyIdOrigin != currencyIdDestiny) {
				        		if (bmoCurrency.getCode().toString().equals("USD")) {
			        	%>
			    					<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
					    <%		} else { %>
			    					<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
					    <%		}
					    	} else { %>
			    				<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
					    <%	}%>
			
						<%= HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
			
						<%= HtmlUtil.formatReportCell(sFParams, "" + discount, BmFieldType.CURRENCY) %>
			
						<%= HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
			
						<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
			
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
						<%
					    //CXC Pagadas
						String sqlCXCPagadas = " SELECT racc_currencyid, racc_currencyparity, racc_total AS pagosSum " + 
										  " FROM raccounts " +
										  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
										  " WHERE racc_orderid = " + pmConnList.getInt("proj_orderid") +
										  " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT  + "'" +
									      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
						
						//System.out.println("sqlCXCPagadas: "+sqlCXCPagadas);
						double payments = 0;
					    pmConnCXC.doFetch(sqlCXCPagadas);
						while (pmConnCXC.next()) {
							
							// Conversion a la moneda destino(del filtro)
			          		currencyIdOrigin = 0; currencyIdDestiny = 0;
			          		parityOrigin = 0; parityDestiny = 0;
			          		currencyIdOrigin = pmConnCXC.getInt("racc_currencyid");
			          		parityOrigin = pmConnCXC.getDouble("racc_currencyparity");
			          		currencyIdDestiny = currencyId;
			          		parityDestiny = defaultParity;

			          		payments += pmCurrencyExchange.currencyExchange(pmConnCXC.getDouble("pagosSum"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						}
						paymentsSum += payments;
						paymentsTotal += payments;
					    %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + payments, BmFieldType.CURRENCY) %>
					    <%
					  	//CXC Saldo Cliente
						String sqlCXCSaldo = " SELECT racc_currencyid, racc_currencyparity, racc_total AS saldoSum " + 
										  " FROM raccounts " +
										  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
										  " WHERE racc_orderid = " + pmConnList.getInt("proj_orderid") +
										  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW  + "'" +
										  " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' ";
						
						//System.out.println("sqlCXCSaldo: "+sqlCXCSaldo);
						double balance = 0, totalRacc = 0;
					    pmConnCXC.doFetch(sqlCXCSaldo);
						while (pmConnCXC.next()) {
							
							// Conversion a la moneda destino(del filtro)
			          		currencyIdOrigin = 0; currencyIdDestiny = 0;
			          		parityOrigin = 0; parityDestiny = 0;
			          		currencyIdOrigin = pmConnCXC.getInt("racc_currencyid");
			          		parityOrigin = pmConnCXC.getDouble("racc_currencyparity");
			          		currencyIdDestiny = currencyId;
			          		parityDestiny = defaultParity;
			          		
			          		totalRacc += pmCurrencyExchange.currencyExchange(pmConnCXC.getDouble("saldoSum"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);				          		
						}
						balance = total - totalRacc;
						balanceSum += balance;
						balanceTotal += balance;
					    %>
				    
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>
					</tr>
					<%
					i++;
					y++;
				} // Fin pmConnList
				
     			if (amountSum > 0 || discountSum > 0 || taxSum > 0 || totalSum > 0 || paymentsSum > 0 || balanceSum > 0) {
					%>
					<tr class="reportCellEven reportCellCode">
						<td colspan="16" align="right">&nbsp;</td>
						<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + discountSum, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + taxSum, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + totalSum, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" , BmFieldType.STRING) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsSum, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceSum, BmFieldType.CURRENCY) %>
					</tr>
					<%
     			}
			} // FIn pmConnGroup
			%>
			<tr><td colspan="23">&nbsp;</td></tr>
			<TR class="reportCellEven reportCellCode">
				<%=HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER)%>
				<td align="right" colspan="15">&nbsp;</td>
				<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
			</TR>
		</table>

	<%
	}// FIN DEL CONTADOR
	} // Fin else, Fin de moneda
} // Fin de if(no carga datos
	pmConnList.close();
	pmConnCXC.close();
	pmConnGroup.close();
    pmCurrencyWhile.close();
    pmConnCount.close();
	
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