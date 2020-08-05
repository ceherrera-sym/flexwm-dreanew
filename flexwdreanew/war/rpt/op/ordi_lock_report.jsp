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
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
 	String title = "Uso de Productos en Pedidos";
	
   	String sql = "", where = "", sqlCurrency = "", filters = "", ordeStatus = "", paymentStatus = "", deliveryStatus = "", lockStatus  = "", fullName = "";
   	int programId = 0,wflowtypeId=0, orderId = 0, industryId = 0, rows = 0, customerId = 0, productId = 0, areaId = 0, userId = 0, 
   			showProductExtra = 0, currencyId = 0 ,dynamicColspan = 0, dynamicColspanMinus = 0;
   	double nowParity = 0, defaultParity = 0;

	String startDate = "", endDate = "", startString = "", endString = "";
   	int startYear = 0, startMonth = 0, startDay = 0, startHour = 0, startMinute = 0;
	int endYear = 0, endMonth = 0, endDay = 0, endHour = 0, endMinute = 0;	
   	
   	startDate = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
   	endDate = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
   	
   	PmProduct pmProduct = new PmProduct(sFParams);
   	BmoProduct bmoProduct = new BmoProduct();
   	
   	PmOrder pmOrder = new PmOrder(sFParams);
   	BmoOrder bmoOrder = new BmoOrder();
   	BmoOrderItem bmoOrderItem = new BmoOrderItem();
   	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	//Tipo de Pedido
	BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	bmoOrderType = (BmoOrderType)pmOrderType.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getDefaultOrderTypeId().toInteger());

   	String orderStartDate = "", orderStartYear = "", orderStartMonth = "", orderStartDay = "", orderStartHour = "", orderStartMinute = "", orderStartString = "";
	String orderEndDate = "", orderEndYear = "", orderEndMonth = "", orderEndDay = "", orderEndHour = "", orderEndMinute = "", orderEndString = "";
	String productFamilyId = "", productGroupId = "",  whereExtra="";
	
   	// Obtener parametros
    if (request.getParameter("orde_orderid") != null) orderId = Integer.parseInt(request.getParameter("orde_orderid"));    
   	if (request.getParameter("orde_customerid") != null) customerId = Integer.parseInt(request.getParameter("orde_customerid"));
   	if (request.getParameter("prod_productid") != null) productId = Integer.parseInt(request.getParameter("prod_productid"));
   	if (request.getParameter("showProductExtra") != null) showProductExtra = Integer.parseInt(request.getParameter("showProductExtra"));
   	if (request.getParameter("prod_productfamilyid") != null) productFamilyId = request.getParameter("prod_productfamilyid");
    if (request.getParameter("prod_productgroupid") != null) productGroupId = request.getParameter("prod_productgroupid");
   	if (request.getParameter("orde_lockstart") != null && request.getParameter("orde_lockstart").length() > 0) startDate = request.getParameter("orde_lockstart");
   	if (request.getParameter("orde_lockend") != null && request.getParameter("orde_lockend").length() > 0) endDate = request.getParameter("orde_lockend");
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("startDate") != null) startDate = request.getParameter("startDate");
	if (request.getParameter("endDate") != null) endDate = request.getParameter("endDate");
    if (request.getParameter("area_areaid") != null) areaId = Integer.parseInt(request.getParameter("area_areaid"));    
    if (request.getParameter("orde_status") != null) ordeStatus = request.getParameter("orde_status");
    if (request.getParameter("orde_lockstatus") != null) lockStatus = request.getParameter("orde_lockstatus");
    if (request.getParameter("orde_deliverystatus") != null) deliveryStatus = request.getParameter("orde_deliverystatus");
    if (request.getParameter("orde_paymentstatus") != null) paymentStatus = request.getParameter("orde_paymentstatus");
    if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid")); 
    if (request.getParameter("orde_userid") != null) userId = Integer.parseInt(request.getParameter("orde_userid"));
    if (request.getParameter("orde_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("orde_currencyid"));
    if (request.getParameter("orde_wflowtypeid") != null) wflowtypeId = Integer.parseInt(request.getParameter("orde_wflowtypeid"));
    
	bmoProgram = (BmoProgram)pmProgram.get(programId);
    
	// Asignar horas default a startDate y endDate
	if (!startDate.equals("")) startDate += " 00:00";
	if (!endDate.equals("")) endDate += " 23:59";
	
   	// Arreglo fechas de intervalo
	startYear = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.YEAR);
	startMonth = (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.MONTH) + 1);
	startDay = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.DAY_OF_MONTH);		 	
	startHour = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.HOUR_OF_DAY);		 	
	startMinute = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.MINUTE);		 	
	startString = startYear + "-" + startMonth + "-" + startDay + " 00:00";
	
	endYear = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.YEAR);
	endMonth = (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.MONTH) + 1);
	endDay = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.DAY_OF_MONTH);
	endHour = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.HOUR_OF_DAY);		 	
	endMinute = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.MINUTE);
	endString = endYear + "-" + endMonth + "-"+ endDay + " 23:59";
	
	// Filtros listados
	filters += "<i>Intervalo de Fechas: </i>" + startString + " a " + endString + ", ";
	
	if (orderId > 0) {
    	where += " AND orde_orderid = " + orderId;
    	filters += "<i>Pedido: </i>" + request.getParameter("orde_orderidLabel") + ", ";
    }
	
	if (userId > 0) {
    	//if (sFParams.restrictData(bmoOrder.getProgramCode())) {
			where += " AND orde_userid = " + userId;
		/*} else {
	    	where += " AND ( " +
						" orde_userid = " + userId +
						" OR orde_wflowid IN ( " +
							 " SELECT wflw_wflowid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  ") +
							 " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflu_wflowid = wflw_wflowid) " +
							 " WHERE wflu_userid = " + userId + 
							 " AND wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "' " + 
						 " ) " + 
					 " )";
		}*/
    	filters += "<i>Vendedor: </i>" + request.getParameter("orde_useridLabel") + ", ";
    }
	
	if (customerId > 0) {
        where += " AND orde_customerid = " + customerId;
        filters += "<i>Cliente: </i>" + request.getParameter("orde_customeridLabel") + ", ";
    }
	
	if (industryId > 0) {
    	where += " AND cust_industryid = " + industryId;
    	filters += "<i>Giro: </i>" + request.getParameter("cust_industryidLabel") + ", ";
    }
    
	if (areaId > 0) {
        where += " AND area_areaid = " + areaId;
        filters += "<i>Dpto.: </i>" + request.getParameter("area_areaidLabel") + ", ";
    }
	
    if (productId > 0) {
        where += " AND prod_productid = " + productId;
        filters += "<i>Producto: </i>" + request.getParameter("prod_productidLabel") + ", ";
    }

    if (wflowtypeId > 0) {
         where += " AND orde_wflowtypeid = " + wflowtypeId;
         filters += "<i>Tipo de Flujo: </i>" + request.getParameter("orde_wflowtypeidLabel") + ", ";
     }
    
    if (showProductExtra  != 2) {
    	if(showProductExtra == 1)
    		where += " AND ordi_productid IS NULL ";
    	else where += " AND ordi_productid > 0 ";
		filters += "<i>Mostrar Items: </i>" + request.getParameter("showProductExtraLabel") + ", ";
    } else {
    	filters += "<i>Mostrar Items: </i> Todos, ";
    }
    
    if (!productFamilyId.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("prod_productfamilyid", productFamilyId);
   		filters += "<i>Familia: </i>" + request.getParameter("prod_productfamilyidLabel") + ", ";
   	}
   	
   	if (!productGroupId.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("prod_productgroupid", productGroupId);
   		filters += "<i>Grupo Prod.: </i>" + request.getParameter("prod_productgroupidLabel") + ", ";
   	}
    
    if (!ordeStatus.equals("")) {
   		//where += " AND orde_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("orde_status", ordeStatus);
   		filters += "<i>Estatus: </i>" + request.getParameter("orde_statusLabel") + ", ";
   	}
    
    if (!deliveryStatus.equals("")) {
        //where += " AND orde_deliverystatus like '" + deliveryStatus + "'";
        where += SFServerUtil.parseFiltersToSql("orde_deliverystatus", deliveryStatus);
        filters += "<i>Entrega: </i>" + request.getParameter("orde_deliverystatusLabel") + ", ";
    }
    
    if (!paymentStatus.equals("")) {
        //where += " AND orde_paymentstatus like '" + paymentStatus + "'";
        where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
        filters += "<i>Pago: </i>" + request.getParameter("orde_paymentstatusLabel") + ", ";
    }
    
    where += " AND ( "
			+ " ('" + startDate + "' BETWEEN orde_lockstart AND orde_lockend) " 
		 	+ " OR  ('" + endDate + "' BETWEEN orde_lockstart AND orde_lockend) " 
		 	+ " OR  (orde_lockstart BETWEEN '" + startDate + "' AND '" + endDate + "') " 
		 	+ " OR  (orde_lockend BETWEEN '" + startDate + "' AND '" + endDate + "') " 
		 	+ " ) ";
		 	
	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
	
	// Obtener disclosure de datos
    String disclosureFilters = new PmOrder(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
	
	if (currencyId > 0) {
  		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);
  		defaultParity = bmoCurrency.getParity().toDouble();

  		filters += "<i>Moneda: </i>" + request.getParameter("orde_currencyidLabel")
  				+ " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
  	} else {
  		filters += "<i>Moneda: </i> Todas ";
  		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +     
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (ordi_productid = prod_productid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (ordg_orderid = orde_orderid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (orde_customerid = cust_customerid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON(cure_currencyid = orde_currencyid) " +
  				" WHERE ordi_orderitemid > 0 " + 
  				where + 
  				" GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
  	}
			 
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();
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
	<script type="text/javascript" src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization', 'version':'1','packages':['timeline']}]}"></script>
</head>

<body class="default" onload="self.focus();" <%= permissionPrint %> style="padding-right: 10px">

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
<br>
<%
if (!(currencyId > 0)) {
	int currencyIdWhile = 0, x = 1, y = 0;					
	pmCurrencyWhile.doFetch(sqlCurrency);
	while (pmCurrencyWhile.next()) {
		rows = 0;
		if (pmCurrencyWhile.getInt("cure_currencyid") != currencyIdWhile) {
			currencyIdWhile = pmCurrencyWhile.getInt("cure_currencyid");
			currencyId = currencyIdWhile;
	    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
			y = 0;
			%>
			<table class="report" width="100%">
				<tr>
					<td class="reportHeaderCellCenter">
						<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
					</td>
				</tr>
			</table>
			<br>
			<%
		}
		sql = " SELECT ordi_orderitemid, ordi_quantity, ordi_lockstatus, ordi_productid, ordi_name, orde_code, orde_name, orde_lockstart, " +
				" orde_lockend, orde_status, cust_code, cust_legalname, cust_displayname, indu_name, cust_customertype, user_code, " +
				" prod_code, prod_name, prod_displayname, prod_brand, prod_model, cure_code FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (ordi_productid = prod_productid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (ordg_orderid = orde_orderid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (orde_customerid = cust_customerid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON(cure_currencyid = orde_currencyid) " +
				" WHERE ordi_orderitemid > 0 " + 
	   			" AND orde_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
				where + 
				" ORDER BY orde_code";
		
		pmConn.doFetch(sql);
	%>
		<script type="text/javascript">
		google.setOnLoadCallback(drawChart);
		
		function drawChart() {
		  var container = document.getElementById('example1<%= pmCurrencyWhile.getString("cure_code")%>');
		
		  var chart = new google.visualization.Timeline(container);
		
		  var dataTable = new google.visualization.DataTable();
		
		  dataTable.addColumn({ type: 'string', id: 'Producto' });
		  dataTable.addColumn({ type: 'string', id: 'Pedido' });
		  dataTable.addColumn({ type: 'date', id: 'Inicio' });
		  dataTable.addColumn({ type: 'date', id: 'Fin' });
		
		  dataTable.addRows([
		                     [ 'Intervalo', '  ', new Date(<%= startYear %>, <%= startMonth - 1 %>, <%= startDay %>, 00, 00, 00), 
		                       new Date(<%= endYear %>, <%= endMonth - 1 %>, <%= endDay %>, 23, 59, 59) ],
		  /*
		  	<%// if (orderId > 0) { %>
		  		[ 'Pedido <%= bmoOrder.getCode().toString() %>', ' <%= HtmlUtil.stringToHtml(bmoOrder.getName().toString()) %>', 
		  		  new Date(<%= orderStartYear %>, <%= orderStartMonth %>, <%= orderStartDay %>, <%= orderStartHour %>, <%= orderStartMinute %>, 00), 
		  		  new Date(<%= orderEndYear %>, <%= orderEndMonth %>, <%= orderEndDay %>, <%= orderEndHour %>, <%= orderEndMinute %>, 59) ],
		  	<%// } %>
		  */
		     <%	while (pmConn.next()) { %>
		         <%
		         // Si no es producto de catalgo, colocar que es un extra
		         String prod = "";
		         if ( pmConn.getInt("ordi_productid") > 0)
		        	 prod = pmConn.getString("prod_name");
		         else 
		        	 prod = "Extra";
		         
		         int productStartYear = 0, productStartMonth = 0, productStartDay = 0, productStartHour = 0, productStartMinute = 0;	
		         int productEndYear = 0, productEndMonth = 0, productEndDay = 0, productEndHour = 0, productEndMinute;	
		
		         productStartYear = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.YEAR);
		         productStartMonth = (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.MONTH));
		         productStartDay = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.DAY_OF_MONTH);		 	
		         productStartHour = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.HOUR_OF_DAY);		 	
		         productStartMinute = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.MINUTE);		 	
		         
		         if (!pmConn.getString("orde_lockend").equals("")) {
		        	 productEndYear = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.YEAR);
		        	 productEndMonth = (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.MONTH));
		        	 productEndDay = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.DAY_OF_MONTH);
		        	 productEndHour = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.HOUR_OF_DAY);		 	
		        	 productEndMinute = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.MINUTE);
		        	 %>
		        	 [ '<%= HtmlUtil.stringToHtml(prod) %>', 'Cantidad: <%= pmConn.getString("ordi_quantity") %> para Pedido: <%= pmConn.getString("orde_code") %> ', 
		        	   new Date(<%= productStartYear %>, <%= productStartMonth %>, <%= productStartDay %>, <%= productStartHour %>, <%= productStartMinute %>, 00), 
		        	   new Date(<%= productEndYear %>, <%= productEndMonth %>, <%= productEndDay %>, <%= productEndHour %>, <%= productEndMinute %>, 00) ],
		        	 <%
		        	 rows++; 
		         } 
		     }
		  %>
		    ]);
		  chart.draw(dataTable);
		}
		</script>
		<% if(rows <= 15){%>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 33 + 150) %>px; class="default"></div>
		<%}
		else if(rows <= 30){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 42) %>px; class="default"></div>
		<%}
		else if(rows <= 45){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 38) %>px; class="default"></div>
		<%}
		else if(rows <= 75){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 34) %>px; class="default"></div>
		<%}
		else if(rows <= 90){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 30) %>px; class="default"></div>
		<%}
		else if(rows <= 100 || rows >= 100){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 28) %>px;  class="default"></div>
		<%} %>
		
				
<!-- 		else if(rows <= 60){ %> -->
<%-- 		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 30) %>px;  class="default"></div> --%>
<%-- 		<%}else if(rows <= 90){ %> --%>
<%-- 		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 17) %>px;  class="default"></div> --%>
<%-- 		<%}else if(rows <= 100 || rows >= 100){ %> --%>
<%-- 		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 2) %>px;  class="default"></div> --%>
<%-- 		<%} %> --%>
		<table class="report">
			<tr>
				<td colspan="14" class="reportGroupCell">
					Uso de Productos en el Intervalo de Fechas
				</td>
			</tr>
			<tr class="">
				<td class="reportHeaderCell">#</td>
				<td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Nombre</td>
				<td class="reportHeaderCell">Marca</td>
				<td class="reportHeaderCell">Modelo</td>
				<td class="reportHeaderCell">Pedido</td>
				<td class="reportHeaderCell">Moneda</td>
				<td class="reportHeaderCell">Estatus</td>
				<td class="reportHeaderCell">Cliente</td>
				<td class="reportHeaderCell">Vendedor</td>
				<td class="reportHeaderCell">Giro</td>
				<td class="reportHeaderCell">Inicio</td>
				<td class="reportHeaderCell">Fin</td>
				<td class="reportHeaderCellCenter">Cantidad</td>
			</tr>
			<%
			pmConn.beforeFirst();
			
			int  i = 1;
			double total = 0;
			while(pmConn.next()) {
				bmoOrder.getStatus().setValue(pmConn.getString("orders", "orde_status"));
		
				if (pmConn.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
					fullName = pmConn.getString("cust_code") + " " + pmConn.getString("cust_legalname");	
				else
					fullName = pmConn.getString("cust_code") + " " + pmConn.getString("cust_displayname");
				%>
				
				<tr class="reportCellEven">
					<%= HtmlUtil.formatReportCell(sFParams, "" + (i), BmFieldType.NUMBER) %>
				
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("prod_code"), BmFieldType.CODE) %>
				
					<%	if (pmConn.getInt("ordi_productid") > 0) { %>
							<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("prod_name")), BmFieldType.STRING) %>
					<% 	} else { %>
							<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("ordi_name")), BmFieldType.STRING) %>
					<%	}%>
				
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("prod_brand")), BmFieldType.STRING) %>
				
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("prod_model")), BmFieldType.STRING) %>
										
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_code") + 
							" " + HtmlUtil.stringToHtml(pmConn.getString("orde_name")), BmFieldType.STRING) %>
					
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("cure_code")), BmFieldType.CODE) %>
					
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
				
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(fullName), BmFieldType.STRING) %>
					
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("user_code")), BmFieldType.CODE) %>
		
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("indu_name")), BmFieldType.STRING) %>
				
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_lockstart"), BmFieldType.DATETIME) %>
				
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_lockend"), BmFieldType.DATETIME) %>
				
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("ordi_quantity"), BmFieldType.NUMBER) %>
				</tr>
				<%
				i++;
			}
			%>
	    	<tr><td colspan="<%= 14%>" height="50" >&nbsp;</td></tr>
		</table>
		<%
	} // Fin pmCurrencyWhile
} // Fin de no existe moneda
else { // Existe moneda destino 
	sql = " SELECT ordi_orderitemid, ordi_quantity, ordi_lockstatus, ordi_productid, ordi_name, orde_code, orde_name, orde_lockstart, " +
			" orde_lockend, orde_status, cust_code, cust_legalname, cust_displayname, indu_name, cust_customertype, user_code, " +
			" prod_code, prod_name, prod_displayname, prod_brand, prod_model, cure_code FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (ordi_productid = prod_productid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (ordg_orderid = orde_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (orde_customerid = cust_customerid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON(cure_currencyid = orde_currencyid) " +
			" WHERE ordi_orderitemid > 0 " + 
			where + 
			" ORDER BY orde_code";
	
	pmConn.doFetch(sql);
%>
	<script type="text/javascript">
	google.setOnLoadCallback(drawChart);
	
	function drawChart() {
	  var container = document.getElementById('example1');
	
	  var chart = new google.visualization.Timeline(container);
	
	  var dataTable = new google.visualization.DataTable();
	
	  dataTable.addColumn({ type: 'string', id: 'Producto' });
	  dataTable.addColumn({ type: 'string', id: 'Pedido' });
	  dataTable.addColumn({ type: 'date', id: 'Inicio' });
	  dataTable.addColumn({ type: 'date', id: 'Fin' });
	
	  dataTable.addRows([
	                     [ 'Intervalo', '  ', new Date(<%= startYear %>, <%= startMonth - 1 %>, <%= startDay %>, 00, 00, 00), 
	                       new Date(<%= endYear %>, <%= endMonth - 1 %>, <%= endDay %>, 23, 59, 59) ],
  /*
  	<%// if (orderId > 0) { %>
  		[ 'Pedido <%= bmoOrder.getCode().toString() %>', ' <%= HtmlUtil.stringToHtml(bmoOrder.getName().toString()) %>', 
  		  new Date(<%= orderStartYear %>, <%= orderStartMonth %>, <%= orderStartDay %>, <%= orderStartHour %>, <%= orderStartMinute %>, 00), 
  		  new Date(<%= orderEndYear %>, <%= orderEndMonth %>, <%= orderEndDay %>, <%= orderEndHour %>, <%= orderEndMinute %>, 59) ],
  	<%// } %>
  */
	     <%	while (pmConn.next()) { %>
	         <%
	         // Si no es producto de catalgo, colocar que es un extra
	         String prod = "";
	         if ( pmConn.getInt("ordi_productid") > 0)
	        	 prod = pmConn.getString("prod_name");
	         else 
	        	 prod = "Extra";
	         
	         int productStartYear = 0, productStartMonth = 0, productStartDay = 0, productStartHour = 0, productStartMinute = 0;	
	         int productEndYear = 0, productEndMonth = 0, productEndDay = 0, productEndHour = 0, productEndMinute;	
	
	         productStartYear = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.YEAR);
	         productStartMonth = (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.MONTH));
	         productStartDay = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.DAY_OF_MONTH);		 	
	         productStartHour = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.HOUR_OF_DAY);		 	
	         productStartMinute = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.MINUTE);		 	
	
			if (!pmConn.getString("orde_lockend").equals("")) {
		         productEndYear = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.YEAR);
		         productEndMonth = (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.MONTH));
		         productEndDay = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.DAY_OF_MONTH);
		         productEndHour = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.HOUR_OF_DAY);		 	
		         productEndMinute = SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.MINUTE);
		         %>
		         [ '<%= HtmlUtil.stringToHtml(prod) %>', 'Cantidad: <%= pmConn.getString("ordi_quantity") %> para Pedido: <%= pmConn.getString("orde_code") %> ', 
		           new Date(<%= productStartYear %>, <%= productStartMonth %>, <%= productStartDay %>, <%= productStartHour %>, <%= productStartMinute %>, 00), 
		           new Date(<%= productEndYear %>, <%= productEndMonth %>, <%= productEndDay %>, <%= productEndHour %>, <%= productEndMinute %>, 00) ],
		
		         <%
		         rows++; 
		  		} 
	     	}
	  %>
	    ]);
	  chart.draw(dataTable);
	}
	</script>
	<br>
	
		<% if(rows <= 15){%>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 33 + 150) %>px; class="default"></div>
		<%}
		else if(rows <= 30){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 42) %>px; class="default"></div>
		<%}
		else if(rows <= 45){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 38) %>px; class="default"></div>
		<%}
		else if(rows <= 75){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 34) %>px; class="default"></div>
		<%}
		else if(rows <= 90){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 30) %>px; class="default"></div>
		<%}
		else if(rows <= 100 || rows >= 100){ %>
		<div id="example1<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 95%; height: <%= (rows * 28) %>px;  class="default"></div>
		<%} %>
	
<%-- 	<% if(rows <= 26){%> --%>
<%-- 		<div id="example1" style="width: 95%; height: <%= (rows * 33 + 150) %>px;  class="default"></div> --%>
<%-- 	<%}else if(rows <= 60){ %> --%>
<%-- 		<div id="example1" style="width: 95%; height: <%= (rows * 20) %>px;  class="default"></div> --%>
<%-- 	<%}else if(rows <= 90){ %> --%>
<%-- 		<div id="example1" style="width: 95%; height: <%= (rows * 17) %>px;  class="default"></div> --%>
<%-- 	<%}else if(rows <= 100 || rows >= 100){ %> --%>
<%-- 		<div id="example1" style="width: 95%; height: <%= (rows * 2) %>px;  class="default"></div> --%>
<%-- 	<%}	%> --%>
	<br>
	<table class="report">
		<tr>
			<td colspan="14" class="reportGroupCell">
				Uso de Productos en el Intervalo de Fechas
			</td>
		</tr>
		<tr class="">
			<td class="reportHeaderCell">#</td>
			<td class="reportHeaderCell">Clave</td>
			<td class="reportHeaderCell">Nombre</td>
			<td class="reportHeaderCell">Marca</td>
			<td class="reportHeaderCell">Modelo</td>
			<td class="reportHeaderCell">Pedido</td>
			<td class="reportHeaderCell">Moneda</td>
			<td class="reportHeaderCell">Estatus</td>
			<td class="reportHeaderCell">Cliente</td>
			<td class="reportHeaderCell">Vendedor</td>
			<td class="reportHeaderCell">Giro</td>
			<td class="reportHeaderCell">Inicio</td>
			<td class="reportHeaderCell">Fin</td>
			<td class="reportHeaderCellCenter">Cantidad</td>
		</tr>
		<%
		pmConn.beforeFirst();
		
		int  i = 1;
		double total = 0;
		while(pmConn.next()) {
			bmoOrder.getStatus().setValue(pmConn.getString("orders", "orde_status"));
	
			if (pmConn.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
				fullName = pmConn.getString("cust_code") + " " + pmConn.getString("cust_legalname");	
			else
				fullName = pmConn.getString("cust_code") + " " + pmConn.getString("cust_displayname");
			%>
			<tr class="reportCellEven">
				<%= HtmlUtil.formatReportCell(sFParams, "" + (i), BmFieldType.NUMBER) %>
			
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("prod_code"), BmFieldType.CODE) %>
			
				<%	if (pmConn.getInt("ordi_productid") > 0) { %>
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("prod_name")), BmFieldType.STRING) %>
				<% 	} else { %>
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("ordi_name")), BmFieldType.STRING) %>
				<%	}%>
			
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("prod_brand")), BmFieldType.STRING) %>
			
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("prod_model")), BmFieldType.STRING) %>
								
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_code") + 
						" " + HtmlUtil.stringToHtml(pmConn.getString("orde_name")), BmFieldType.STRING) %>
				
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("cure_code")), BmFieldType.CODE) %>
				
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
			
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(fullName), BmFieldType.STRING) %>
				
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("user_code")), BmFieldType.CODE) %>
	
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConn.getString("indu_name")), BmFieldType.STRING) %>
			
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_lockstart"), BmFieldType.DATETIME) %>
			
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_lockend"), BmFieldType.DATETIME) %>
			
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("ordi_quantity"), BmFieldType.NUMBER) %>
			</tr>
			<%
			i++;
		}
		%>
	</table>
<%	
 }
	}// Fin de if(no carga datos)
 	pmConn.close();
	pmCurrencyWhile.close();

%>
<%	if (print.equals("1")) { %>
	 	<script>
	 		//window.print();
	 	</script>
 <% } %>
  </body>
</html>