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
<%@page import="com.flexwm.shared.cm.BmoQuote"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 
String sql = "", chartSql = "";
try {
	// Inicializar variables
 	String title = "Oportunidades Perdidas Agrupadas por Motivo de Perder";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
 	BmoOpportunity bmoOpportunity = new BmoOpportunity();
 	BmoQuote bmoQuote = new BmoQuote();
	BmoWFlow bmoWFlow = new BmoWFlow();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
    //Tipo de Pedido
    BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
		
   	String where = "", startDate = "", endDate = "", status = "", groupFilter = "", filters = "", 
   			sqlCurrency ="", customer = "", wflowFunelId = "";
   	int wflowTypeId = 0, orderTypeId = 0, venueId = 0, userId = 0, customerId = 0, referralId = 0, loseMotiveId = 0, programId = 0,
   			currencyId = 0 ,dynamicColspan = 0, dynamicColspanMinus = 0;
   	double nowParity = 0, defaultParity = 0;
   	
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("oppo_ordertypeid") != null) orderTypeId = Integer.parseInt(request.getParameter("oppo_ordertypeid"));
   	if (request.getParameter("oppo_userid") != null) userId = Integer.parseInt(request.getParameter("oppo_userid"));
   	if (request.getParameter("oppo_customerid") != null) customerId = Integer.parseInt(request.getParameter("oppo_customerid"));
   	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
   	if (request.getParameter("wflw_wflowfunnelid") != null) wflowFunelId = request.getParameter("wflw_wflowfunnelid");
   	if (request.getParameter("oppo_venueid") != null) venueId = Integer.parseInt(request.getParameter("oppo_venueid"));   
   	if (request.getParameter("oppo_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("oppo_currencyid"));
   	if (request.getParameter("oppo_losemotiveid") != null) loseMotiveId = Integer.parseInt(request.getParameter("oppo_losemotiveid"));
   	if (request.getParameter("oppo_startdate") != null) startDate = request.getParameter("oppo_startdate");
   	if (request.getParameter("oppo_enddate") != null) endDate = request.getParameter("oppo_enddate");
    if (request.getParameter("oppo_status") != null) status = request.getParameter("oppo_status");
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
    
	// Construir filtros 
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	if (orderTypeId > 0) {
		bmoOrderType = (BmoOrderType)pmOrderType.get(orderTypeId);
        where += " AND oppo_ordertypeid = " + orderTypeId;
        filters += "<i>Tipo Pedido: </i>" + request.getParameter("oppo_ordertypeidLabel") + ", ";
	}
	
	if (wflowTypeId > 0) {
		where += " AND wfty_wflowtypeid = " + wflowTypeId;
   		filters += "<i>Tipo de Flujo: </i>" + request.getParameter("wflw_wflowtypeidLabel") + ", ";
   	}
	
	if (!wflowFunelId.equals("")) {
    	where = SFServerUtil.parseFiltersToSql("wflw_wflowfunnelid", wflowFunelId);
   		filters += "<i>Funnel: </i>" + request.getParameter("wflw_wflowfunnelidLabel") + ", ";
   	}
	
	// Filtro agrupacion
	if (loseMotiveId > 0) {
   		groupFilter += " AND lomt_losemotiveid = " + loseMotiveId;
   		filters += "<i>Motivo de Perder: </i>" + request.getParameter("oppo_losemotiveidLabel") + ", ";
   	}
	
	if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
	   	if (venueId > 0) {
	   		where += " AND oppo_venueid = " + venueId;
	   		filters += "<i>Lugar: </i>" + request.getParameter("oppo_venueidLabel") + ", ";
	   	}
    }
	
	if (userId > 0) {
		where += " AND oppo_userid = " + userId;
		/*
		if (sFParams.restrictData(bmoOpportunity.getProgramCode())) {
			where += " AND oppo_userid = " + userId;
		} else {
			where += " AND ( " +
						" oppo_userid = " + userId +
						" OR oppo_wflowid IN ( " +
							 " SELECT wflw_wflowid FROM " + SQLUtil.formatKind(sFParams, "wflowusers  ") +
							 " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflu_wflowid = wflw_wflowid) " +
							 " WHERE wflu_userid = " + userId + 
							 " AND wflw_callercode = '" + bmoOpportunity.getProgramCode().toString() + "' " + 
						 " ) " + 
					 " )";
		}*/
   		filters += "<i>Vendedor: </i>" + request.getParameter("oppo_useridLabel") + ", ";
   	}
   	
   	if (customerId > 0) {
   		where += " AND cust_customerid = " + customerId;
   		filters += "<i>Cliente: </i>" + request.getParameter("oppo_customeridLabel") + ", ";
   	}
   	
   	if (referralId > 0) {
        where += " AND cust_referralid = " + referralId;
        filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
    }
   	
   	if (!startDate.equals("")) {
   		where += " AND oppo_startdate >= '" + startDate + " 00:00'";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
   	
   	if (!endDate.equals("")) {
   		where += " AND oppo_startdate <= '" + endDate + " 23:59'";
   		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
   	}
   	
   	if (!status.equals("")) {
   		//where += " AND oppo_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("oppo_status", status);
   		filters += "<i>Estatus: </i> Perdida, ";
   	}
   	
   	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmOpportunity(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    if (currencyId > 0) {
   		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);   		
   		defaultParity = bmoCurrency.getParity().toDouble();
   		filters += "<i>Moneda: </i>" + request.getParameter("oppo_currencyidLabel") + ", <i>Tipo de Cambio Actual: </i>" + defaultParity;
   	} else {
   		// Se mete la consulta padre para agilizar el proceso
   		filters += "<i>Moneda: </i> Todas ";
		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity  " +
	   			" FROM " + SQLUtil.formatKind(sFParams, "opportunities") + 
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," referrals")+" ON (cust_referralid = refe_referralid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
	   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	   			" WHERE wfca_programid = " + programId + 
	   			groupFilter + where +
	   			" GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
   	}
       	   	
   	//Conexion a Base de Datos
	PmConn pmConnChartGroup = new PmConn(sFParams);
    
	PmConn pmConnChart = new PmConn(sFParams);
	//pmConnChart.open();
	
	PmConn pmConnGroup = new PmConn(sFParams);
	pmConnGroup.open();
	
	PmConn pmConnList = new PmConn(sFParams);
	pmConnList.open();
	
	PmConn pmCurrencyWhile =new PmConn(sFParams);
    pmCurrencyWhile.open();
    
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
		
	// colspan dinamico
	if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL))
		dynamicColspan++;
	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) 
		dynamicColspan++;
	if (sFParams.isFieldEnabled(bmoQuote.getCoverageParity()))
		dynamicColspan++;
	
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
			<b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Motivo Perder" : "Motivo Perder")%>
			<b>Ordenado por:</b> Motivo Perder, Clave Oportunidad
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
	<br>
	<%
	
		 sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, "opportunities") +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," referrals")+" ON (cust_referralid = refe_referralid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customeraddress")+" ON (cuad_customeraddressid = oppo_customeraddressid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
 				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
 				" WHERE oppo_opportunityid > 0 " +
 				groupFilter + 				where +
 				" ORDER BY oppo_code ASC";  
 				System.out.println("sadsadsadasdas----"+sql);
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
					<% pmConnCount.close(); %>
			<% 
		}else{
			pmConnCount.close();
	if (!(currencyId > 0)) {
		int currencyIdWhile = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) { %>
			<table class="report" width="100%">
				<%
				if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
	        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
	        		%>
	        		<tr>
	            		<td class="reportHeaderCellCenter" colspan="2">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
	        		</tr>
	        		<%
	        		pmConnChart.open();
	        	}
		
				chartSql = " SELECT lomt_losemotiveid, lomt_name, count(oppo_opportunityid) as c, sum(quot_total) as s  " +
						" FROM " + SQLUtil.formatKind(sFParams, "opportunities") + 
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," referrals")+" ON (cust_referralid = refe_referralid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
						" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
						" WHERE wfca_programid = " + programId + 
						" AND oppo_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
						groupFilter + where + 
						" GROUP BY lomt_losemotiveid ORDER BY lomt_name ASC";
				pmConnChart.doFetch(chartSql);
	   	%>
	   	
	   	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	   	<script type="text/javascript">
		   	google.load("visualization", "1", {packages:["corechart"]});
		   	google.setOnLoadCallback(drawChart);
		   	function drawChart() {
	        var data = google.visualization.arrayToDataTable([
	          ['Motivo', 'Oportunidades Perdidas por Motivo'],
	       <% 
	       		String statusCaptionDonutChart = "";
	       		while(pmConnChart.next()) {
	       %>
	       			<%= "['" + pmConnChart.getString("losemotives", "lomt_name") + "', " + pmConnChart.getInt("c") + "]," %>          
	       <%
	       		}
	       %>
	        ]);
	
	        var options = {
	          title: ' Porcentaje por Motivo',
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
	          ['Motivo', 'Valor'],
			<% 
				String statusCaptionTypeChart = "";
	          	pmConnChart.beforeFirst();
	       		while(pmConnChart.next()) {
	       %>
	       			<%= "['" + pmConnChart.getString("losemotives", "lomt_name") + "', " + pmConnChart.getDouble("s") + "]," %>          
	       <%
	       		}
	       		pmConnChart.close();
	       %>
	        ]);
	
	        var options = {
	          title: 'Valor por Motivo',
	          vAxis: {minValue: 0},
	          animation: {duration: 10000, easing: "inAndOut"},
	          legend: { position: "none"},
	          colors:['MediumSeaGreen']
	        };
	
	        var chart = new google.visualization.ColumnChart(document.getElementById('typechart<%= pmCurrencyWhile.getString("cure_code")%>'));
	        chart.draw(data, options);
	      }
	    </script>
	    <tr>
		    <td width="50%"> <div id="donutchart<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 100%; height: 400px;"></div></td>
		    <td width="50%"> <div id="typechart<%= pmCurrencyWhile.getString("cure_code")%>" style="width: 100%; height: 400px;"></div></td>
	    </tr>
	</table>
	<br>
	<table class="report" width="100%">
		<%
		double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0;
	
		// Agrupa oportunidades por el motivo de x moneda
		String sqlGroup = "SELECT lomt_losemotiveid, lomt_name FROM " + SQLUtil.formatKind(sFParams, "opportunities") + 
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
				" WHERE wfca_programid = " + programId + 
				" AND oppo_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
				groupFilter +
				" GROUP BY lomt_losemotiveid ORDER BY lomt_name ASC";
		pmConnGroup.doFetch(sqlGroup);
	    	
		int y = 0;
		double subtotalProductTotal = 0, subtotalProductTotalSum = 0;
		
        while(pmConnGroup.next()) {
			double amountSum = 0, discountSum = 0, taxSum = 0, totalSum = 0;

	    	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "opportunities") +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," referrals")+" ON (cust_referralid = refe_referralid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
		   			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
	       			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customeraddress")+" ON (cuad_customeraddressid = oppo_customeraddressid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
	    			" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	    			" WHERE " +
	    			" lomt_losemotiveid = " + pmConnGroup.getInt("losemotives", "lomt_losemotiveid") +
		   			" AND oppo_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	    			where +
	    			" ORDER BY oppo_code ASC";         
			pmConnList.doFetch(sql);
	   
			//System.out.println("consulta*: "+sql);
			int  i = 1, losemotiveid = 0;
			while(pmConnList.next()) { 

				double amount = pmConnList.getDouble("quot_amount");
				double discount = pmConnList.getDouble("quot_discount");
				double tax = pmConnList.getDouble("quot_tax");
				double total = pmConnList.getDouble("quot_total");

				//Conversion a la moneda destino(seleccion del filtro)
				int currencyIdOrigin = 0, currencyIdDestiny = 0;
				double parityOrigin = 0, parityDestiny = 0;
				currencyIdOrigin = pmConnList.getInt("quot_currencyid");
				parityOrigin = pmConnList.getDouble("quot_currencyparity");
				currencyIdDestiny = currencyId;
				parityDestiny = defaultParity;

				//Sumas por estatus
				amountSum += amount;
				discountSum += discount;
				taxSum += tax;
				totalSum += total;
				//Sumas general
				amountTotal += amount;
				discountTotal += discount;
				taxTotal += tax;
				totalTotal += total;
				
          		if (pmConnGroup.getInt("losemotives", "lomt_losemotiveid") != losemotiveid) {
          			losemotiveid = pmConnGroup.getInt("losemotives", "lomt_losemotiveid");
          			%>
			    	<tr class="reportGroupCell">
			 			<td colspan="<%= 18 + dynamicColspan%>" class="reportGroupCell">
			    	  		<%= HtmlUtil.stringToHtml(pmConnGroup.getString("lomt_name")) %>
						</td>
					</tr>
				    <tr class="">
					    <td class="reportHeaderCellCenter">#</td>
				        <td class="reportHeaderCell">Clave</td>
				        <td class="reportHeaderCell">Cliente</td>
				        <td class="reportHeaderCell">Nombre</td>
				        <td class="reportHeaderCell">Referencia</td>
				        <% if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) { %>
				        	<td class="reportHeaderCell">Lugar</td>
			        	<% } %>
				        <td class="reportHeaderCell">Tipo de Flujo</td>
				        <td class="reportHeaderCell">Fase</td>
				        <%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
								<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoWFlow.getProgramCode(), bmoWFlow.getWFlowFunnelId()) %></td>
						<% 	}%>
				        <td class="reportHeaderCell">Motivo Perder</td>
				        <td class="reportHeaderCell">Comentario Motivo</td>
				        <td class="reportHeaderCell">Avance</td>
				        <td class="reportHeaderCell">Vendedor</td>
				        <td class="reportHeaderCell">Fecha</td>
				        <td class="reportHeaderCell">Moneda</td>
				        <%	if (sFParams.isFieldEnabled(bmoQuote.getCoverageParity())) { %>
				        		<td class="reportHeaderCell">Cobertura T.C.</td>
				        <% 	}%>
				        <td class="reportHeaderCellCenter">Tipo de Cambio</td>
				        <td class="reportHeaderCellRight">Subtotal</td>
				        <td class="reportHeaderCellRight">Descuentos</td>
				        <td class="reportHeaderCellRight">IVA</td>
				        <td class="reportHeaderCellRight">Valor</td>
				    </tr>
		    <% 	} %>
		    	<tr class="reportCellEven">
					<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>

	                <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_code"), BmFieldType.CODE) %>
	                <% 
	                if (pmConnList.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	       				customer = pmConnList.getString("cust_code") + " "+ pmConnList.getString("cust_legalname");	
	       			else
	       				customer = pmConnList.getString("cust_code") + " "+ pmConnList.getString("cust_displayname");
	                %>
	                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + customer, BmFieldType.STRING)) %>
	            
                    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_name"), BmFieldType.STRING)) %>
                    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("referrals","refe_name"), BmFieldType.STRING)) %>
                    
                    <% if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
                    	if (pmConnList.getInt("opportunities","oppo_venueid") > 0 ) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("venues","venu_code") + " " + pmConnList.getString("venues","venu_name"), BmFieldType.STRING)) %>
						<% } else if (pmConnList.getInt("opportunities","oppo_customeraddressid") > 0 ) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("customeraddress", "cuad_street") + " #" + pmConnList.getString("customeraddress", "cuad_number") + " " +pmConnList.getString("customeraddress", "cuad_neighborhood"), BmFieldType.STRING)) %>
	                    <% } else { %>
							<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
					<% 	   } 
                		}
                    %>
                    							
				    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
				    
				    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowphases", "wfph_name"), BmFieldType.STRING)) %>
				    
				    <%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowfunnels", "wflf_name")), BmFieldType.STRING) %>
					<% 	}%>
				    	
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("losemotives", "lomt_name"), BmFieldType.STRING)) %>
					
				    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("oppo_losecomments"), BmFieldType.STRING)) %>
				    
				    <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflows", "wflw_progress"), BmFieldType.PERCENTAGE) %>
				    						
					<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("users", "user_code"), BmFieldType.STRING) %>
					
				    <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_startdate"), BmFieldType.DATE) %>
				    
				    <%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getString("currencies", "cure_code"), BmFieldType.CODE) %>
				    
				    <%	if (sFParams.isFieldEnabled(bmoQuote.getCoverageParity())) { %>
				    		<%= HtmlUtil.formatReportCell(sFParams, (pmConnList.getBoolean("quot_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
			        <% 	}%>
				    
				    <%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getDouble("quot_currencyparity"), BmFieldType.NUMBER) %>
				    
				    <%= HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
					
				    <%= HtmlUtil.formatReportCell(sFParams, "" + discount, BmFieldType.CURRENCY) %>

				    <%= HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
				    
				    <%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
			    </tr>
		    <%
	       		i++;
	    		y++;
			} // Fin pmConnList
			if (amountSum > 0 || discountSum > 0 || taxSum > 0 || totalSum > 0) {
				%>
				<tr class="reportCellEven reportCellCode">
					<td colspan="<%= (14 + dynamicColspan) - dynamicColspanMinus%>" align="right"> 
					&nbsp;
					</td>					     
					<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + discountSum, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + taxSum, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalSum, BmFieldType.CURRENCY) %>
				</tr>
				<%
			}
		} // Fin pmConnGroup
		if (amountTotal > 0 || discountTotal > 0 || taxTotal > 0 || totalTotal > 0) {
			%>
			<tr><td colspan="<%= 18 + dynamicColspan%>">&nbsp;</td></tr>
			<tr class="reportCellEven reportCellCode">
				<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
				<td colspan="<%= (13 + dynamicColspan) - dynamicColspanMinus%>" align="right">
					&nbsp;
				</td>
				<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
		</tr>
<%		} %>
		<tr><td colspan="<%= 18 + dynamicColspan%>">&nbsp;</td></tr>
	</table>
	<%	} // Fin pmCurrencyWhile
	}	// Fin de no existe moneda
		// Existe moneda destino
	else {
		int currencyIdOrigin = 0, currencyIdDestiny = 0;
      	double parityOrigin = 0, parityDestiny = 0;
      	pmConnChart.open();
      	pmConnChartGroup.open();
      	
	   	%>
	   	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	    <script type="text/javascript">
	      google.load("visualization", "1", {packages:["corechart"]});
	      google.setOnLoadCallback(drawChart);
	      function drawChart() {
	        var data = google.visualization.arrayToDataTable([
	          ['Motivo', 'Oportunidades Perdidas por Motivo'],
	       <% 
	       String statusCaptionDonutChart = "";
	          // Contar  las oportunidades perdidas por motivo para la grafica DONUT
	          chartSql = " SELECT lomt_losemotiveid, lomt_name, count(oppo_opportunityid) as c " +
	        		  " FROM " + SQLUtil.formatKind(sFParams, "opportunities") + 
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," referrals")+" ON (cust_referralid = refe_referralid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
	        		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	        		  " WHERE wfca_programid = " + programId + 
	        		  groupFilter + where +
	        		  " GROUP BY lomt_losemotiveid ORDER BY lomt_name ASC";

	          pmConnChartGroup.doFetch(chartSql);
	String motive =" Sin motivo ";
	          while (pmConnChartGroup.next()) {
					if(!pmConnChartGroup.getString("losemotives", "lomt_name").equals(""))
						motive = pmConnChartGroup.getString("losemotives", "lomt_name");
	        	  %>
		        	  <%= "['" + motive + "', " + pmConnChartGroup.getInt("c") + "]," %>
					
		        	<%
	          }
	          %>
	          ]);

	        var options = {
	        		title: ' Porcentaje por Motivo',
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
                      ['Motivo', 'Valor'],
                      <% 
                      // Sacar valores para hacer la conversion a la moneda destino para la grafica TYPE(BARRA)
                      String statusCaptionTypeChart = "";
                      chartSql = " SELECT lomt_losemotiveid, lomt_name " +
                    		  " FROM " + SQLUtil.formatKind(sFParams, "opportunities") + 
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," referrals")+" ON (cust_referralid = refe_referralid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
                    		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
                    		  " WHERE wfca_programid = " + programId + 
                    		  groupFilter + where +
                    		  " GROUP BY lomt_losemotiveid ORDER BY lomt_name ASC";

                      pmConnChartGroup.doFetch(chartSql);

                      while (pmConnChartGroup.next()) {
                    	  sql = " SELECT quot_quoteid, quot_total, quot_currencyid, quot_currencyparity " +
                    			  " FROM " + SQLUtil.formatKind(sFParams, "opportunities") + 
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," referrals")+" ON (cust_referralid = refe_referralid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
                    			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
                    			  " WHERE wfca_programid = " + programId + 
                    			  " AND lomt_losemotiveid = " + pmConnChartGroup.getInt("losemotives", "lomt_losemotiveid") + 
                    			  where;

                    	  pmConnChart.doFetch(sql);
                    	  int countOppoChart = 0;
                    	  double totalTotalChart = 0;
                    	  while (pmConnChart.next()) {
                    		  //Conversion a la moneda destino(seleccion del filtro)
                    		  currencyIdOrigin = 0; currencyIdDestiny = 0;
                    		  parityOrigin = 0; parityDestiny = 0;
                    		  currencyIdOrigin = pmConnChart.getInt("quot_currencyid");
                    		  parityOrigin = pmConnChart.getDouble("quot_currencyparity");
                    		  currencyIdDestiny = currencyId;
                    		  parityDestiny = defaultParity;

                    		  totalTotalChart += pmCurrencyExchange.currencyExchange(pmConnChart.getDouble("quot_total"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
                    	  }
                    	  %>
                    	  <%= "['" + pmConnChartGroup.getString("losemotives", "lomt_name") + "', " + totalTotalChart + "]," %>          
                    	  <%
                      }
                      pmConnChart.close();
                      pmConnChartGroup.close();
                      %>
                      ]);
	
		    	var options = {
		    			title: 'Valor por Motivo',
		    			vAxis: {minValue: 0},
		    			animation: {duration: 10000, easing: "inAndOut"},
		    			legend: { position: "none"},
		    			colors:['lightsalmon']
		    	};
	
		    	var chart = new google.visualization.ColumnChart(document.getElementById('typechart'));
		    	chart.draw(data, options);
		    }
	    </script>
	<table width="100%">
		<tr>
			<td width="50%"> <div id="donutchart" style="width: 100%; height: 400px;"></div> </td>
			<td width="50%"> <div id="typechart" style="width: 100%; height: 400px;"></div> </td>
		</tr>
	</table>
	<table class="report" width="100%">
    	<%
		double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0;

	    // Filtra oportunidades perdidas por motivo
	    String sqlGroup = "SELECT distinct(lomt_losemotiveid), lomt_name FROM " + SQLUtil.formatKind(sFParams, "opportunities") + 
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
  			  	" LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
	    		" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	    		" WHERE wfca_programid = " + programId + 
	    		groupFilter + where +
	    		" ORDER BY lomt_name  ASC";
    	pmConnGroup.doFetch(sqlGroup);
    	
    	int y = 0;
    	double subtotalProductTotal = 0, subtotalProductTotalSum = 0;
			
    	while(pmConnGroup.next()) {
    		double amountSum = 0, discountSum = 0, taxSum = 0, totalSum = 0;

    		sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "opportunities") +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customers")+" ON (cust_customerid = oppo_customerid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," referrals")+" ON (cust_referralid = refe_referralid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," users")+" ON (user_userid = oppo_userid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," quotes")+" ON (oppo_quoteid = quot_quoteid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," venues")+" ON (venu_venueid = oppo_venueid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," losemotives")+" ON (oppo_losemotiveid = lomt_losemotiveid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," currencies")+" ON (cure_currencyid = quot_currencyid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," customeraddress")+" ON (cuad_customeraddressid = oppo_customeraddressid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
    				" LEFT JOIN "+ SQLUtil.formatKind(sFParams," wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
    				" WHERE " +
    				" lomt_losemotiveid = " + pmConnGroup.getInt("losemotives", "lomt_losemotiveid") +
    				where +
    				" ORDER BY oppo_code ASC";         
    		pmConnList.doFetch(sql);
		   
    		//System.out.println("sql_pmConnList: "+sql);
    		int  i = 1, losemotiveid = 0;
    		while(pmConnList.next()) {

    			double amount = pmConnList.getDouble("quot_amount");
    			double discount = pmConnList.getDouble("quot_discount");
    			double tax = pmConnList.getDouble("quot_tax");
    			double total = pmConnList.getDouble("quot_total");

    			//Conversion a la moneda destino(seleccion del filtro)
    			currencyIdOrigin = 0; currencyIdDestiny = 0;
    			parityOrigin = 0; parityDestiny = 0;
    			currencyIdOrigin = pmConnList.getInt("quot_currencyid");
    			parityOrigin = pmConnList.getDouble("quot_currencyparity");
    			currencyIdDestiny = currencyId;
    			parityDestiny = defaultParity;

    			amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    			discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    			tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    			total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);

    			//Sumas por estatus
    			amountSum += amount;
    			discountSum += discount;
    			taxSum += tax;
    			totalSum += total;
    			//Sumas general
    			amountTotal += amount;
    			discountTotal += discount;
    			taxTotal += tax;
    			totalTotal += total;

    			if (pmConnGroup.getInt("losemotives", "lomt_losemotiveid") != losemotiveid) {
    				losemotiveid = pmConnGroup.getInt("losemotives", "lomt_losemotiveid");
    				%>
    				<tr  class="reportGroupCell" >
	    				<td colspan="<%= 18 + dynamicColspan%>" class="reportGroupCell">
	    					<%= HtmlUtil.stringToHtml(pmConnGroup.getString("lomt_name")) %>
	    				</td>
    				</tr>
    				<tr class="">
	    				<td class="reportHeaderCellCenter">#</td>
	    				<td class="reportHeaderCell">Clave</td>
	    				<td class="reportHeaderCell">Cliente</td>
	    				<td class="reportHeaderCell">Nombre</td>
	    				<td class="reportHeaderCell">Referencia</td>
	    				<% if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) { %>
	    						<td class="reportHeaderCell">Lugar</td>
	    				<% } %>
	    				<td class="reportHeaderCell">Tipo de Flujo</td>
	    				<td class="reportHeaderCell">Fase</td>
	    				<%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
								<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoWFlow.getProgramCode(), bmoWFlow.getWFlowFunnelId()) %></td>
						<% 	}%>
	    				<td class="reportHeaderCell">Motivo Perder</td>
	    				<td class="reportHeaderCell">Comentario Motivo</td>
	    				<td class="reportHeaderCell">Avance</td>
	    				<td class="reportHeaderCell">Vendedor</td>
	    				<td class="reportHeaderCell">Fecha</td>
	    				<td class="reportHeaderCell">Moneda</td>
	    				<%	if (sFParams.isFieldEnabled(bmoQuote.getCoverageParity())) { %>
	    						<td class="reportHeaderCell">Cobertura T.C.</td>
	    				<% 	}%>
	    				<td class="reportHeaderCellCenter">Tipo de Cambio</td>
	    				<td class="reportHeaderCellRight">Subtotal</td>
	    				<td class="reportHeaderCellRight">Descuentos</td>
	    				<td class="reportHeaderCellRight">IVA</td>
	    				<td class="reportHeaderCellRight">Valor</td>
    				</tr>
    				<% } %>
    				<tr class="reportCellEven">
	    				<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
	
	    				<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_code"), BmFieldType.CODE) %>
	    				<% 
	    				if (pmConnList.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	    					customer = pmConnList.getString("cust_code") + " "+ pmConnList.getString("cust_legalname");	
	    				else
	    					customer = pmConnList.getString("cust_code") + " "+ pmConnList.getString("cust_displayname");
	    				%>
	    				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + customer, BmFieldType.STRING)) %>
	
	    				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_name"), BmFieldType.STRING)) %>
	    				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("referrals","refe_name"), BmFieldType.STRING)) %>
	
	    				<% 	if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
	    						if (pmConnList.getInt("opportunities","oppo_venueid") > 0 ) { %>
	    							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("venues","venu_code") + " " + pmConnList.getString("venues","venu_name"), BmFieldType.STRING)) %>
	    					<% 	} else if (pmConnList.getInt("opportunities","oppo_customeraddressid") > 0 ) { %>
		    						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("customeraddress", "cuad_street") + " #" + pmConnList.getString("customeraddress", "cuad_number") + " " +pmConnList.getString("customeraddress", "cuad_neighborhood"), BmFieldType.STRING)) %>
	    					<% 	} else { %>
		    						<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
    					<% 	  	} 
	    					}
	    				%>
	
	    				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
	
	    				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowphases", "wfph_name"), BmFieldType.STRING)) %>
	
	    				<%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
								<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowfunnels", "wflf_name")), BmFieldType.STRING) %>
						<% 	}%>
	
	    				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("losemotives", "lomt_name"), BmFieldType.STRING)) %>
	
	    				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("oppo_losecomments"), BmFieldType.STRING)) %>
	
	    				<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflows", "wflw_progress"), BmFieldType.PERCENTAGE) %>
	
	    				<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("users", "user_code"), BmFieldType.STRING) %>
	
	    				<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_startdate"), BmFieldType.DATE) %>
	
	    				<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getString("currencies", "cure_code"), BmFieldType.CODE) %>
	
	    				<%	if (sFParams.isFieldEnabled(bmoQuote.getCoverageParity())) { %>
	    						<%= HtmlUtil.formatReportCell(sFParams, (pmConnList.getBoolean("quot_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
	    				<% 	}%>
	
	    				<%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
	    				if (currencyIdOrigin != currencyIdDestiny) {
	    					if (bmoCurrency.getCode().toString().equals("USD")) {
	    						%>
	    						<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
	    						<%		} else { %>
	    						<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getDouble("quot_currencyparity"), BmFieldType.NUMBER) %>
	    						<%		}
	    				} else { %>
	    					<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnList.getDouble("quot_currencyparity"), BmFieldType.NUMBER) %>
				<%		}%>
	
	    				<%= HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
	
	    				<%= HtmlUtil.formatReportCell(sFParams, "" + discount, BmFieldType.CURRENCY) %>
	
	    				<%= HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
	
	    				<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
    				</tr>
    				<%
    				i++;
    				y++;
    		} // Fin pmConnList
    		if (amountSum > 0 || discountSum > 0 || taxSum > 0 || totalSum > 0) {
    			%>
    			<tr class="reportCellEven reportCellCode">
	    			<td colspan="<%= (14 + dynamicColspan) - dynamicColspanMinus%>" align="right"> 
	    				&nbsp;
	    			</td>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + discountSum, BmFieldType.CURRENCY) %>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + taxSum, BmFieldType.CURRENCY) %>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + totalSum, BmFieldType.CURRENCY) %>
    			</tr>
    			<%
    		}
		} // Fin pmConnGroup
    	if (amountTotal > 0 || discountTotal > 0 || taxTotal > 0 || totalTotal > 0) {
    		%>
    		<tr><td colspan="<%= 18 + dynamicColspan%>">&nbsp;</td></tr>
    		<tr class="reportCellEven reportCellCode">
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
	    		<td colspan="<%= (13 + dynamicColspan) - dynamicColspanMinus%>" align="right">
	    			&nbsp;
	    		</td>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
    		</tr>
<%		}%>
		</table>
		<% 
		  
	}
		} // Fin (else) existe moneda
	}// Fin de if(no carga datos)
	pmConnList.close();
	pmConnGroup.close();
	pmConnChart.close();
    pmCurrencyWhile.close();
    pmConnCount.close();
  
%> 
	<% if (print.equals("1")) { %>
		<script>
			//window.print();
		</script>
	<% } %>
	
<% 	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
		+ " Reporte: "+title
		+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); } catch (Exception e) { 
	
		String errorLabel = "Error de Reporte";
		String errorText = "El reporte no pudo ser generado satisfactoriamente. ";
		String errorException = "Chart SQL:\n " + chartSql + 
				"\n\n List SQL:\n " + sql + 
				"\n\n Exception:\n" + e.toString();
	
		response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}
%>
  </body>
</html>