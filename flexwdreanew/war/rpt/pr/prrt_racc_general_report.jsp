<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */ -->
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>   
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
 	String title = "Reporte General de Cuentas por Cobrar";
   	BmoRaccount bmoRaccount = new BmoRaccount();
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoCustomer bmoCustomer = new BmoCustomer();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	
	
//    	String sql = "", where = "", whereOrderId = "", whereSupl = "", whereFailure = "", whereLinked = "", sqlCurrency = "";
//    	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "";
   	String sql = "", where = "", sqlCurrency = "",sqlG = "",lease = "";;
   	String startDate  = "", endDate = "",dueDate = "",dueEndDate = "", rentIncrease = "",rentEndIncrease = "", rentalScheduleDate ="",rentalEndScheduleDate ="";
   	//String status = "", paymentStatus = "", paymentStatus2 = "";
   	String orderStatus = "",  paymentStatus = "", deliveryStatus = "";
    //String filters = "", customer = "", customerCategory ="";
   	String filters = "", fullName = "",fullNameLessor = "", whereExtra="", productFamilyId = "", productGroupId = "";
    int programId = 0, customerid = 0,customerLessorId = 0, cols= 0, prrtId = 0, industryId = 0, userId = 0, productId = 0, showProductExtra = 0, 
    		currencyId = 0 ,dynamicColspan = 0, dynamicColspanMinus = 0, columnBudgets = 0, prrtTypeId = 0, areaId = -1;
	double nowParity = 0, defaultParity = 0;	
//    			customerid = 0, orderId = 0, cols= 0, raccountTypeId = 0, companyId = 0, wflowCategoryId = 0, userId = 0, collectorUserId = 0, 
//    			currencyId = 0, failure = 0, linked = 0, budgetId = 0, budgetItemId = 0, activeBudgets = 0,areaId = -1;
//    		   	boolean enableBudgets = false;
   	
 	// dynamicColspan incrementar por cada columna del reporte
	// dynamicColspanMinus incrementar por cada columna que vaya a mostrar totales(es decir, se va a restar al dynamicColspan si HAY FILA TOTALES)
	//int dynamicColspan = 0, dynamicColspanMinus = 0;
	// se agrega 2 columnas para presupuestos, para manejo de colspans
//    	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//    		enableBudgets = true;
//    		activeBudgets = 2;
//    	}
	   	
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));	
   	if (request.getParameter("prrt_propertiesrentid") != null) prrtId = Integer.parseInt(request.getParameter("prrt_propertiesrentid"));    
    if (request.getParameter("prrt_customerid") != null) customerid = Integer.parseInt(request.getParameter("prrt_customerid"));    
    if (request.getParameter("prty_customerid") != null) customerLessorId = Integer.parseInt(request.getParameter("prty_customerid"));    
    if (request.getParameter("paymentstatus") != null) paymentStatus = request.getParameter("paymentstatus");
    if (request.getParameter("status") != null) orderStatus = request.getParameter("status");
    if (request.getParameter("prrt_startdate") != null) startDate = request.getParameter("prrt_startdate");
    if (request.getParameter("prrt_enddate") != null) endDate = request.getParameter("prrt_enddate");
    if (request.getParameter("prrt_rentincrease") != null) rentIncrease = request.getParameter("prrt_rentincrease");
    if (request.getParameter("rentendincrease") != null) rentEndIncrease = request.getParameter("rentendincrease");
    if (request.getParameter("prrt_rentalscheduledate") != null) rentalScheduleDate = request.getParameter("prrt_rentalscheduledate");
    if (request.getParameter("rentalendscheduledate") != null) rentalEndScheduleDate = request.getParameter("rentalendscheduledate");

    if (request.getParameter("racc_duedate") != null) dueDate = request.getParameter("racc_duedate");
   	if (request.getParameter("dueenddate") != null) dueEndDate = request.getParameter("dueenddate");
    if (request.getParameter("area_areaid") != null) areaId = Integer.parseInt(request.getParameter("area_areaid"));  
    if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid"));  
    if (request.getParameter("prrt_userid") != null) userId = Integer.parseInt(request.getParameter("prrt_userid"));   
   	if (request.getParameter("prrt_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("prrt_currencyid"));
    if (request.getParameter("prrt_ordertypeid") != null) prrtTypeId = Integer.parseInt(request.getParameter("prrt_ordertypeid"));    
    if (prrtTypeId > 0) {
    	where += " AND prrt_ordertypeid = " + prrtTypeId;
    	filters += "<i>Tipo Pedido: </i>" + request.getParameter("prrt_ordertypeidLabel") + ", ";
    }
    
    if (prrtId > 0) {
    	where += " AND orde_originreneworderid = " + prrtId;
    	filters += "<i>Contrato: </i>" + request.getParameter("prrt_propertiesrentidLabel") + ", ";
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
    	filters += "<i>Vendedor: </i>" + request.getParameter("prrt_useridLabel") + ", ";
    }
    
    if (customerid > 0) {
    	where += " AND cust_customerid = " + customerid;
    	filters += "<i>Cliente: </i>" + request.getParameter("prrt_customeridLabel") + ", ";
    }
    
    if (customerLessorId > 0) {
    	where += " AND prty_customerid = " + customerLessorId;
    	filters += "<i>Arrendador: </i>" + request.getParameter("prty_customeridLabel") + ", ";
    }   
    
    if (industryId > 0) {
    	where += " AND cust_industryid = " + industryId;
    	filters += "<i>Giro: </i>" + request.getParameter("cust_industryidLabel") + ", ";
    }
    
    if (!orderStatus.equals("")) {
        where += SFServerUtil.parseFiltersToSql("orde_status", orderStatus);
   		filters += "<i>Estatus Ped: </i>" + request.getParameter("statusLabel") + ", ";
   	}
  
    
    if (!paymentStatus.equals("")) {
        where += SFServerUtil.parseFiltersToSql("racc_paymentstatus", paymentStatus);
        filters += "<i>Estatus Pago cxc: </i>" + request.getParameter("paymentstatusLabel") + ", ";
    }
    
    if (!startDate.equals("")) {
        where += " AND prrt_startdate >= '" + startDate + "'";
        filters += "<i>F.Inicio: </i>" + startDate+ ", ";
    }
    
    if (!endDate.equals("")) {
        where += " AND prrt_startdate <= '" + endDate + "'";
        filters += "<i>F.Fin: </i>" + endDate + ", ";
    }
    
    if (!rentIncrease.equals("")) {
        where += " AND prrt_rentincrease >= '" + rentIncrease + "'";
        filters += "<i>F.Incremento Renta Inicio: </i>" + rentIncrease + ", ";
    }
    
    if (!rentEndIncrease.equals("")) {
        where += " AND prrt_rentincrease <= '" + rentEndIncrease + "'";
        filters += "<i>F.Incremento Renta Final: </i>" + rentEndIncrease + ", ";
    }
    
    if (!rentalScheduleDate.equals("")) {
        where += " AND prrt_rentalscheduledate >= '" + rentalScheduleDate + "'";
        filters += "<i>F.1er. Renta Inicio: </i>" + rentalScheduleDate + ", ";
    }
    
    if (!rentalEndScheduleDate.equals("")) {
        where += " AND prrt_rentalscheduledate <= '" + rentalEndScheduleDate + "'";
        filters += "<i>F.1er. Renta Inicio Final: </i>" + rentalEndScheduleDate + ", ";
    }
        
	if (!dueDate.equals("")) {
        where += " AND racc_duedate >= '" + dueDate + "'";
        filters += "<i>Programaci&oacute;n Inicio: </i>" + dueDate + ", ";
    }
    
   	if (!dueEndDate.equals("")) {
        where += " AND racc_duedate <= '" + dueEndDate + "'";
        filters += "<i>Programaci&oacute;n Final: </i>" + dueEndDate + ", ";
    }
   	   	
  	if (currencyId > 0) {
  		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);
  		defaultParity = bmoCurrency.getParity().toDouble();

  		filters += "<i>Moneda: </i>" + request.getParameter("prrt_currencyidLabel")
  				+ " | <i>Tipo de Cambio Actual : </i> " + defaultParity +", ";
  	} else {
  		filters += "<i>Moneda: </i> Todas ";
  		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, "propertiesrent") +     
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON(orde_orderid = prrt_orderid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = orde_customerid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries")+" ON(indu_industryid = cust_industryid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = orde_userid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) ";
  				sqlCurrency += " WHERE orde_orderid > 0 " +
  				where;
	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
  	}
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    

	// Obtener disclosure de datos
	String disclosureFilters = new PmRaccount(sFParams).getDisclosureFilters();
	if (disclosureFilters.length() > 0)
		where += " AND " + disclosureFilters;

	PmConn pmOrders = new PmConn(sFParams);
	pmOrders.open();
	
	PmConn pmConnG = new PmConn(sFParams);
	pmConnG.open();

	PmConn pmOrderRenew = new PmConn(sFParams);
	pmOrderRenew.open();
	
	PmConn pmRaccounts = new PmConn(sFParams);
	pmRaccounts.open();

	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();

	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();

	boolean s = true;
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	pmProgram = new PmProgram(sFParams);
	bmoProgram = (BmoProgram) pmProgram.get(programId);
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
	
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃº(clic-derecho).
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
		<td align="LEFT" width="80" rowspan="2" valign="top">	
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td class="reportTitle" colspan="2">
			<%= title %>
		</td>
	</tr>
	<tr>
		<td class="reportSubTitle">
			<b>Filtros:</b> <%= filters %>
			<br>
			<% 	if (!(currencyId > 0)) { %>
				<b>Agrupado por:</b> Moneda.
			<%	}%>
			<b>Ordenado por:</b> Clave CxC
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<br>
<table class="report" border="0">               
	<%  
	
	double withDrawTotal = 0;
    double depositTotal = 0;
    double totalW = 0;
    double totalD = 0;
    double balanceT = 0;
    double amountT = 0; 
    double taxT = 0;
    int y = 0, orderid = 0;
		

	if (!(currencyId > 0)) {
		String[] color = new String[21];
	  	color[1] = "#DF013A";
	  	color[2] = "#4000FF";
	  	color[3] = "#088A29";
	  	color[4] = "#F7D358";
	  	color[5] = "#F4FA58";
	  	color[6] = "#DA38C7";
	  	color[7] = "#98792F";
	  	color[8] = "#826969";
	  	color[9] = "#3E2C8C";
	  	color[10] = "#93B2A5";
	  	color[11] = "#F6820D";
	  	color[12] = "#2EBACA";
	  	color[13] = "#2805F0";
	  	color[14] = "#54462A";
	  	color[15] = "#4029A7";
	  	color[16] = "#B706F3";
	  	color[17] = "#FC8301";
	  	color[18] = "#EECDAB";
	  	color[19] = "#863232";
	  	color[20] = "#5F1212";
		
		int currencyIdWhile = 0;// , i = 1, y = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			sql = " SELECT DISTINCT(racc_orderid) AS pedido, orde_orderid, orde_currencyid, orde_currencyparity, orde_code, orde_name, " +
		    		" cust_customerid, cust_code, cust_legalname, cust_displayname, orde_total, cureOrder.cure_currencyid, cureOrder.cure_code, cust_customercategory" +
		    		" FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
// 		    		if (enableBudgets) {
// 				    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
// 				    		+ " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
// 				    		+ SQLUtil.formatKind(sFParams, "budgetitemtypes")
// 				    		+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
// 				    		+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
// 				    		+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
// 					        +" ON(area_areaid = racc_areaid) ";
// 				    	}
			sql +=" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credits")+" ON (orde_orderid = cred_orderid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
	    			" WHERE racc_orderid > 0 " +
	    			" AND racc_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
// 	    			whereOrderId +
// 	    			where +
// 	    			whereFailure +
// 	    			whereLinked +
					" ORDER BY orde_orderid ASC";


				 sqlG = " SELECT count(racc_customerid),sum(racc_total) As sum,cust_displayname  " +
    			    		" FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
    						 " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertiesrent")+" ON (prrt_orderid = orde_originreneworderid)"+
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON (prty_propertyid = prrt_propertyid)"+
    						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credits")+" ON (orde_orderid = cred_orderid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
    		    			" WHERE racc_orderid > 0 " +
    		    			" AND ract_type = 'W' AND ract_category = 'O' "+
    		    			//whereOrderId +
    		    			where +
    		    			" group by racc_customerid ";
			%>
    		    			
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

	<script type="text/javascript">
	google.charts.load('current', {'packages':['corechart']});
	google.charts.setOnLoadCallback(drawChart);

	function drawChart() {
		var data = google.visualization.arrayToDataTable([
		  	['Arrendador', 'Total'],
			<% 		
		  	pmConnG.doFetch(sqlG);	  	
		  			
		  	int c1=1;
		  	String colors = "";		  	
		  	while(pmConnG.next()){	 	
		  		
		  		
		  		colors += "'" + color[c1] + "',";
		  	%>
		  	<%= "['" + pmConnG.getString("cust_displayname") + "', " +  pmConnG.getDouble("sum") + "]," %>
		  	<%
		  	 c1++;
		  	}
		  	
		  	%>
			]);
		  	
			 var options = { 'width':400, 'height':400,is3D: true,colors:[<%=colors%>]};
			
		 	 var chart = new google.visualization.PieChart(document.getElementById('piechart'));	  
		 	 chart.draw(data, options);
		  	
	}
	</script>
	
      <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
      <script type="text/javascript">
      google.charts.load('current', {packages: ['corechart', 'bar']});
      google.charts.setOnLoadCallback(drawBasic);
      function drawBasic() {
    	  var data = google.visualization.arrayToDataTable([
  		  	['Arrendador', 'Total', { role: 'style' },{ role: 'annotation' } ],
  			<%   		  	
  		  	pmConnG.doFetch(sqlG);
  		   
		    int c=1;
		    
  		  	while(pmConnG.next()){  		  
  		  	
  		  	%>
  		  	<%= "['" + pmConnG.getString("cust_displayname") + "', " +  pmConnG.getDouble("sum") + 
  		  	      ",'color: "+color[c]+"',"+ pmConnG.getDouble("sum")+"]," %>
  		  	<%
  		    c++;
  		  	}
  		  	
  		  	%>
  			]);
  		  	
    	  var options = {
    		            		      
    		        hAxis: {
    		          title: 'Arrendatario',
    		          minValue: 0
    		        },
    		        vAxis: {
    		          title: 'Total'    		          
    		        },
    		        x: {
    		          0: { side: 'top'} 
    		        }
    		        
    		      };
  			
  		 	 var chart = new google.visualization.ColumnChart(document.getElementById('piechart2'));	 
  		 	 chart.draw(data, options);
    	  
      }
      </script>
 	 <tr>
 	 	<td colspan="4">
 	 		<div id="piechart" ></div>
 	 	</td>	
 	 	<td colspan="6">
	 		<div id="piechart2" ></div>
	 	</td>	 	
 	 </tr>
      
    	<%		
		    pmOrders.doFetch(sql);
		    while (pmOrders.next()) {
		    	 bmoCustomer.getCustomercategory().setValue(pmOrders.getString("customers","cust_customercategory"));

		    	if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
            		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
            		y = 0;
            		withDrawTotal = 0;
            	    depositTotal = 0;
            	    totalW = 0;
            	    totalD = 0;
            	    balanceT = 0;
            	    amountT = 0; 
            	    taxT = 0;
            		%>
            		<tr>
	            		<td class="reportHeaderCellCenter" colspan="19">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
            		</tr>
            		<%
            	}

		    	 if ((pmOrders.getInt("orde_orderid") != orderid)) {
		    		 orderid = pmOrders.getInt("orde_orderid");
		    		   
		    		   //Conversion del pedido a la moneda destino(seleccion del filtro) 
		    		   int currencyIdOrigin = 0, currencyIdDestiny = 0;
		               double parityOrigin = 0, parityDestiny = 0;
		               currencyIdOrigin = pmOrders.getInt("orde_currencyid");
		               parityOrigin = pmOrders.getDouble("orde_currencyparity");
		               currencyIdDestiny = pmCurrencyWhile.getInt("cure_currencyid");
		               parityDestiny = pmCurrencyWhile.getDouble("cure_parity");
		               
		    	%>       
		               <tr>
			               <td class="reportGroupCell" colspan="19">				                   		
				               <%= pmOrders.getString("orde_code")%>
				               <%= HtmlUtil.stringToHtml(pmOrders.getString("orde_name"))%> |
				               Cliente:
				            	   	<%= pmOrders.getString("cust_code")%>
				               		<%= HtmlUtil.stringToHtml(pmOrders.getString("cust_displayname"))%> |
				               	<% if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { %>
				              	Categoría Cliente:
				              		<%= HtmlUtil.stringToHtml(bmoCustomer.getCustomercategory().getSelectedOption().getLabel()) %> |
					           <%} %>	
				               Tipo de Cambio:
				               <% 	if (currencyIdOrigin != currencyIdDestiny) { %>
				               			<%= parityDestiny %> |
				               			<span title='Origen( Tipo de Cambio <%= pmOrders.getDouble("orde_currencyparity")%> | Total: <%= SFServerUtil.formatCurrency(pmOrders.getDouble("orde_total"))%> <%= pmOrders.getString("cureOrder.cure_code")%> )'>
				               				Total: <%=SFServerUtil.formatCurrency(
				               					pmCurrencyExchange.currencyExchange(pmOrders.getDouble("orde_total"), 
				               							currencyIdOrigin, 
				               							parityOrigin, 
				               							currencyIdDestiny, 
				               							parityDestiny)) %> 
				               							<%= pmCurrencyWhile.getString("cure_code")%>
	           							</span>
				               <%	} else { %>
				               			<%= pmOrders.getDouble("orde_currencyparity") %> |
					               		<span title='Origen( Tipo de Cambio <%= pmOrders.getDouble("orde_currencyparity")%> | Total: <%= SFServerUtil.formatCurrency(pmOrders.getDouble("orde_total"))%> <%= pmOrders.getString("cureOrder.cure_code")%> )'>
					               			Total: <%=SFServerUtil.formatCurrency(pmOrders.getDouble("orde_total")) %> <%= pmOrders.getString("cureOrder.cure_code") %>
					               		</span>
				               <% 	}  %>
				                
			               </td>
				        </tr>			        
			            <tr class="">
		    			   <td class="reportHeaderCellCenter">#</td>	
			               <td class="reportHeaderCell">Clave</td>
<%-- 			               <%	if (enableBudgets) { %> --%>
<!-- 				               <td class="reportHeaderCell">Partida Pres</td> -->
<!-- 		               		   <td class="reportHeaderCell">Departamento</td> -->
<%-- 		               		<% } %>						                --%>
			               <td class="reportHeaderCell">Vendedor</td>
			               <td class="reportHeaderCell">Fact</td>
			               <td class="reportHeaderCell">Referencia</td>
			               <td class="reportHeaderCell">Empresa</td>					               
			               <td class="reportHeaderCell">Descripci&oacute;n</td>                                    
			               <td class="reportHeaderCell">F. Registro</td>
			               <td class="reportHeaderCell">Programaci&oacute;n</td>
			               <%	if (sFParams.isFieldEnabled(bmoRaccount.getLinked())) {
							%>
								<td class="reportHeaderCell">
									<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getLinked()))%>
								</td>
							<%	}%>
			               <td class="reportHeaderCell">Estatus</td>					               
			               <td class="reportHeaderCell">Pago</td>
			               <td class="reportHeaderCell">Moneda</td>
			               <td class="reportHeaderCellCenter">Tipo de Cambio</td>
			               <td class="reportHeaderCellRight">Monto</td>
			               <td class="reportHeaderCellRight">IVA</td>
			               <td class="reportHeaderCellRight">Total</td>
			               <td class="reportHeaderCellRight">Pagos</td>
			               <td class="reportHeaderCellRight">Saldo</td>
			           </tr>
		   <% 		} 
		    	
				int x = 1;
		    	double amount = 0;                        
		    	double amountW = 0;
		    	double amountD = 0;    
		    	double balance = 0;
		    	double tax = 0;
		    	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "raccounts ");
// 		    	if (enableBudgets) {
// 			    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
// 			    		+ " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
// 			    		+ SQLUtil.formatKind(sFParams, "budgetitemtypes")
// 			    		+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
// 			    		+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
// 			    		+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
// 				        +" ON(area_areaid = racc_areaid) ";
// 			    	}
		    	sql += 	" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "credits")+" ON (orde_orderid = cred_orderid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
		    			" WHERE racc_raccountid > 0 " +
		    			" AND racc_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
		    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
		    			" AND racc_orderid = " + pmOrders.getInt("orde_orderid") +
		    			where +
// 		    			whereFailure +
// 		    			whereLinked +
		    			" ORDER BY racc_raccountid ASC ";
			   pmRaccounts.doFetch(sql);                           
		       while (pmRaccounts.next()) {
		    	   bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts","racc_status"));
		           bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts","racc_paymentstatus"));
		   %>
				   <tr class="reportCellEven">
					   <%= HtmlUtil.formatReportCell(sFParams, "" + x, BmFieldType.NUMBER) %>
					   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
<%-- 					   <% if (enableBudgets) { %> --%>
<%-- 	        		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %> --%>
<%-- 		        	   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %> --%>
<%-- 		        	   <%} %> --%>
					   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("users","user_code"), BmFieldType.STRING) %>
					   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_invoiceno")), BmFieldType.STRING) %>
					   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_reference")), BmFieldType.STRING) %>
					   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("companies","comp_name")), BmFieldType.STRING) %>                    		   
					   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_description")), BmFieldType.STRING) %>                                                
					   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_receivedate"), BmFieldType.DATE) %>                                                                                      
					   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_duedate"), BmFieldType.DATE) %>
					   <%	if (sFParams.isFieldEnabled(bmoRaccount.getLinked())) { 
					    		String raccLinked = ((pmRaccounts.getBoolean("racc_linked") ? "Si" : "No"));
						%>	
					   			<%= HtmlUtil.formatReportCell(sFParams, raccLinked, BmFieldType.STRING) %>
				   		<%	} %>
					   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
					   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
					   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("cureRacc","cure_code"), BmFieldType.CODE) %>                    		   
					   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getDouble("racc_currencyparity"), BmFieldType.NUMBER) %>                    		   

					   <%
					   //Conversion a la moneda destino(seleccion del filtro)
					   int currencyIdOrigin = 0, currencyIdDestiny = 0;
					   double parityOrigin = 0, parityDestiny = 0;
					   currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
					   parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
					   currencyIdDestiny = currencyId;
					   parityDestiny = defaultParity;
				
					   double amountRacc = pmRaccounts.getDouble("racc_amount");
					   double taxRacc = pmRaccounts.getDouble("racc_tax");
					   double totalRacc = pmRaccounts.getDouble("racc_total");
					   double paymentsRacc = pmRaccounts.getDouble("racc_payments");					           		
					   double balanceRacc = pmRaccounts.getDouble("racc_balance");
				
//					   amountRacc = pmCurrencyExchange.currencyExchange(amountRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//					   taxRacc = pmCurrencyExchange.currencyExchange(taxRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//					   totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//					   paymentsRacc = pmCurrencyExchange.currencyExchange(paymentsRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//					   balanceRacc = pmCurrencyExchange.currencyExchange(balanceRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				
					   amount += amountRacc;
					   tax += taxRacc; 
					   amountW += totalRacc;
					   amountD += paymentsRacc;
					   balance += balanceRacc;
					   %>
				
					   <%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
					   <%= HtmlUtil.formatReportCell(sFParams, "" + taxRacc, BmFieldType.CURRENCY) %>
					   <%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc, BmFieldType.CURRENCY) %>
					   <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRacc, BmFieldType.CURRENCY) %>
					   <%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
		         </TR>
		         <%
		         x++;
		         y++;
		       }
		       
		   		if(amountW > 0 || amountD > 0) {
		   %>
		               <tr class="reportCellEven reportCellCode">
		                    <td colspan="16">&nbsp;</td>
		                    <%= HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
		                    <%= HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
		                    <%= HtmlUtil.formatReportCell(sFParams, "" + amountW, BmFieldType.CURRENCY) %>
		                    <%= HtmlUtil.formatReportCell(sFParams, "" + amountD, BmFieldType.CURRENCY) %>
		                    <%= HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>
		               </tr>
		   <%
		   		}
		   		amountT += amount;
		   		taxT += tax;
		   		withDrawTotal += amountW;
		   		depositTotal += amountD;
		   		balanceT += balance;
		          
		          
		    } //End while pmOrders
		    
		    %>
		    
		    <%
		    int items = 0;
		    sql = " SELECT COUNT(*) as cxc FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
// 		    if (enableBudgets) {
// 		    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
// 		    		+ " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
// 		    		+ SQLUtil.formatKind(sFParams, "budgetitemtypes")
// 		    		+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
// 		    		+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
// 		    		+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
// 			        +" ON(area_areaid = racc_areaid) ";
// 		    	}
		    
		    sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
		        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +                    	  
		        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
		        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +                       	      
		        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
		        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
		        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +                                      
		        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
		        	  " WHERE racc_orderid IS NULL " +
		        	  " AND racc_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
		        	  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
		        	  where +
// 		        	  whereFailure +
// 		        	  whereLinked +
		        	  " ORDER BY racc_customerid ";
		    pmRaccounts.doFetch(sql);
		    if (pmRaccounts.next()) items = pmRaccounts.getInt("cxc");
		    
		    if (items > 0) {
		    %>
		    	<tr><td colspan="19">&nbsp;</td></tr>
			    <tr>
			       <td class="reportHeaderCell" colspan="19" align="left">
			       		Cuentas por Cobrar sin Pedido
			       </td>	                   
			    </tr>
			    <%
			    
			    double amountW = 0, amountD = 0, amount = 0,tax = 0;
			    int customerId = 0;
			    int i = 1;
			    //Listar las CxC que no estan ligadas a un pedido
			    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
			    sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
			    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
			    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +   
			    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) " +
			    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
			    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +                       	      
			    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
			    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
			    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +                                      
			    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
			    	  " WHERE racc_orderid IS NULL " +
			    	  " AND racc_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
			    	  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " + 
			    	  where +
// 			    	  whereFailure +
// 			    	  whereLinked +
			    	  " ORDER BY racc_customerid ";
			    pmRaccounts.doFetch(sql);                           
			    while (pmRaccounts.next()) {
			    	
			    	bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts","racc_status"));
			        bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts","racc_paymentstatus"));
			    	
			    	if ((pmRaccounts.getInt("racc_customerid") != customerId)) {
			    		customerId = pmRaccounts.getInt("racc_customerid");
			    		i = 1;
			%>
			           <tr>
			               <td class="reportGroupCell" colspan="19">
			               		Cliente:
			               		<%= pmRaccounts.getString("cust_code")%>
			               		<%= HtmlUtil.stringToHtml(pmRaccounts.getString("cust_displayname"))%>
			               </td>
				        </tr>	        
			            <tr>
						   <td class="reportHeaderCellCenter">#</td>	
			               <td class="reportHeaderCell">Clave</td>					               
			               <td class="reportHeaderCell">Vendedor</td>
			               <td class="reportHeaderCell">Fact</td>
			               <td class="reportHeaderCell">Referencia</td>
			               <td class="reportHeaderCell">Empresa</td>					               
			               <td class="reportHeaderCell">Descripci&oacute;n</td>                                    
			               <td class="reportHeaderCell">F. Registro</td>
			               <td class="reportHeaderCell">Programaci&oacute;n</td>
			               <%	if (sFParams.isFieldEnabled(bmoRaccount.getLinked())) {
						%>
								<td class="reportHeaderCell">
									<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getLinked()))%>
								</td>
						<%	}%>
			               <td class="reportHeaderCell">Estatus</td>					               
			               <td class="reportHeaderCell">Pago</td>
			               <td class="reportHeaderCell">Moneda</td>
				           <td class="reportHeaderCellCenter">Tipo de Cambio</td>               
			               <td class="reportHeaderCellRight">Monto</td>
			               <td class="reportHeaderCellRight">IVA</td>
			               <td class="reportHeaderCellRight">Total</td>
			               <td class="reportHeaderCellRight">Pagos</td>
			               <td class="reportHeaderCellRight">Saldo</td>
			           </tr>
			       <% } %>
			       <TR class="reportCellEven">
				       <%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
				       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
<%-- 				        <%	if (enableBudgets) { %> --%>
<%-- 	        		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %> --%>
<%-- 		        	   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %> --%>
<%-- 		        	   <%	} %> --%>
				       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("users","user_code"), BmFieldType.STRING) %>
				       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_invoiceno")), BmFieldType.STRING) %>
				       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_reference")), BmFieldType.STRING) %>
				       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("companies","comp_name")), BmFieldType.STRING) %>
				       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_description")), BmFieldType.STRING) %>
				       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_receivedate"), BmFieldType.DATE) %>
				       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_duedate"), BmFieldType.DATE) %>
				       <%	if (sFParams.isFieldEnabled(bmoRaccount.getLinked())) { 
					    		String raccLinked = ((pmRaccounts.getBoolean("racc_linked") ? "Si" : "No"));
						%>	
					   			<%= HtmlUtil.formatReportCell(sFParams, raccLinked, BmFieldType.STRING) %>
				   		<%	} %>
				       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("cureRacc", "cure_code"), BmFieldType.CODE) %>
				       <%= HtmlUtil.formatReportCell(sFParams, ""  + pmRaccounts.getDouble("racc_currencyparity"), BmFieldType.NUMBER) %>                               
				       <%
				       //Conversion a la moneda destino(seleccion del filtro)
				       int currencyIdOrigin = 0, currencyIdDestiny = 0;
				       double parityOrigin = 0, parityDestiny = 0;
				       currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
				       parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
				       currencyIdDestiny = currencyId;
				       parityDestiny = defaultParity;
				
				       double amountRacc = pmRaccounts.getDouble("racc_amount");
				       double taxRacc = pmRaccounts.getDouble("racc_tax");
				       double totalRacc = pmRaccounts.getDouble("racc_total");
				       double paymentsRacc = pmRaccounts.getDouble("racc_payments");					           		
				       double balanceRacc = pmRaccounts.getDouble("racc_balance");
				
//				       amountRacc = pmCurrencyExchange.currencyExchange(amountRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//				       taxRacc = pmCurrencyExchange.currencyExchange(taxRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//				       totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//				       paymentsRacc = pmCurrencyExchange.currencyExchange(paymentsRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//				       balanceRacc = pmCurrencyExchange.currencyExchange(balanceRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				       
				       amountT += amountRacc;
				       taxT += taxRacc;
				       withDrawTotal += totalRacc;
				       depositTotal += paymentsRacc;
				       balanceT += balanceRacc;
				       %>
				       <%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
				       <%= HtmlUtil.formatReportCell(sFParams, "" + taxRacc, BmFieldType.CURRENCY) %>
				       <%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc , BmFieldType.CURRENCY) %>
				       <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRacc, BmFieldType.CURRENCY) %>
				       <%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
			       </TR>
			       <%  
			       i++;
			       y++;
			    } //fin while
		    }// fin if  
		    %>
			
		    <%
		    if (withDrawTotal > 0 || depositTotal > 0) {
		    %>
		    	<tr><td colspan="19">&nbsp;</td></tr>
			    <tr class="reportCellEven reportCellCode">
				    <%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
				    <td colspan="15">&nbsp;</td>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + amountT, BmFieldType.CURRENCY) %>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + taxT, BmFieldType.CURRENCY) %>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + withDrawTotal, BmFieldType.CURRENCY) %>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + depositTotal, BmFieldType.CURRENCY) %>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceT, BmFieldType.CURRENCY) %>
			    </tr>
			    <tr><td colspan="19">&nbsp;</td></tr>
		<%	}
		} // Fin pmCurrencyWhile
	}
	// Si existe moneda destino--------------------------------------------------------------------------------------------
	else {	
		String[] color = new String[21];
	  	color[1] = "#DF013A";
	  	color[2] = "#4000FF";
	  	color[3] = "#088A29";
	  	color[4] = "#F7D358";
	  	color[5] = "#F4FA58";
	  	color[6] = "#DA38C7";
	  	color[7] = "#98792F";
	  	color[8] = "#826969";
	  	color[9] = "#3E2C8C";
	  	color[10] = "#93B2A5";
	  	color[11] = "#F6820D";
	  	color[12] = "#2EBACA";
	  	color[13] = "#2805F0";
	  	color[14] = "#54462A";
	  	color[15] = "#4029A7";
	  	color[16] = "#B706F3";
	  	color[17] = "#FC8301";
	  	color[18] = "#EECDAB";
	  	color[19] = "#863232";
	  	color[20] = "#5F1212";
	  	
	    sql = " SELECT DISTINCT(racc_orderid) AS pedido,cust_customertype,orde_reneworderid, orde_originreneworderid , orde_orderid,prrt_enddate,prrt_rentincrease, orde_currencyid, orde_currencyparity, orde_code, orde_name, prrt_rentalscheduledate, " +
	    		" cust_customerid, cust_code, cust_legalname, cust_displayname,prrt_initialIconme,prrt_currentIncome,orde_datecreate,orde_datemodify,orde_lockstart,orde_lockend, orde_total, cureOrder.cure_currencyid, cureOrder.cure_code,cust_customercategory " +
	    		" FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
				 " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertiesrent")+" ON (prrt_orderid = orde_originreneworderid)"+
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON (prty_propertyid = prrt_propertyid)"+
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credits")+" ON (orde_orderid = cred_orderid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
    			" WHERE racc_orderid > 0 " +
    			" AND ract_type = 'W' AND ract_category = 'O' "+
    			//whereOrderId +
    			where +
    			" ORDER BY orde_originreneworderid, orde_orderid ";
    			
    			 sqlG = " SELECT count(racc_customerid),sum(racc_total) As sum,cust_displayname,racc_currencyid,racc_currencyparity  " +
    			    		" FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
    						 " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertiesrent")+" ON (prrt_orderid = orde_originreneworderid)"+
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON (prty_propertyid = prrt_propertyid)"+
    						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credits")+" ON (orde_orderid = cred_orderid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +
    		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
    		    			" WHERE racc_orderid > 0 " +
    		    			" AND ract_type = 'W' AND ract_category = 'O' "+
    		    			//whereOrderId +
    		    			where +
    		    			" group by racc_customerid ";
    		    			
    		    			%>
    		    			
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

	<script type="text/javascript">
	google.charts.load('current', {'packages':['corechart']});
	google.charts.setOnLoadCallback(drawChart);

	function drawChart() {
		var data = google.visualization.arrayToDataTable([
		  	['Arrendador', 'Total'],
			<% 					  	
		  	pmConnG.doFetch(sqlG);		  	
		  			
		  	int c1=1;
		  	String colors = "";
		  	
		  	while(pmConnG.next()){		 		
		  				  		
		  		colors += "'" + color[c1] + "',";
		  	%>
		  	<%= "['" + pmConnG.getString("cust_displayname") + "', " +  pmConnG.getDouble("sum") + "]," %>
		  	<%
			if(c1 <=20){
  		    	c1++;
  		  	}else{
  		  		c1 = 1;
  		  	}
		  	}
		  	
		  	%>
			]);
		  	
			 var options = { 'width':400, 'height':400,is3D: true,colors:[<%=colors%>]};
			
		 	 var chart = new google.visualization.PieChart(document.getElementById('piechart'));	  
		 	 chart.draw(data, options);
		  	
	}
	</script>
	
      <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
      <script type="text/javascript">
      google.charts.load('current', {packages: ['corechart', 'bar']});
      google.charts.setOnLoadCallback(drawBasic);
      
      function drawBasic() {
    	  var data = google.visualization.arrayToDataTable([
  		  	['Arrendador', 'Total', { role: 'style' },{ role: 'annotation' } ],
  			<%   		  	
  		  	pmConnG.doFetch(sqlG);
  		   
		    int c=1;
		    
  		  	while(pmConnG.next()){  		  
  		  	
  		  	%>
  		  	<%= "['" + pmConnG.getString("cust_displayname") + "', " +  pmConnG.getDouble("sum") + 
  		  	      ",'color: "+color[c]+"',"+ pmConnG.getDouble("sum")+"]," %>
  		  	<%
  		  	if(c <=20){
  		    	c++;
  		  	}else{
  		  		c = 1;
  		  	}
  		  	}
  		  	
  		  	%>
  			]);
  		  	
    	  var options = {
    			  'width':900, 'height':400,
    		            		      
    		        hAxis: {
    		          title: 'Arrendatario',
    		          minValue: 0
    		        },
    		        vAxis: {
    		          title: 'Total'    		          
    		        },
    		        x: {
    		          0: { side: 'top'} 
    		        }
    		        
    		      };
  			
  		 	 var chart = new google.visualization.ColumnChart(document.getElementById('piechart2'));	 
  		 	 chart.draw(data, options);
    	  
      }
      </script>
 	 <tr>
 	 	<td colspan="2">
 	 		<div id="piechart" ></div>
 	 	</td>	
 	 	<td colspan="6"  >
	 		<div id="piechart2" ></div>
	 	</td>	 	
 	 </tr>
 	
      
    	<%		
	    pmOrders.doFetch(sql);
	    while (pmOrders.next()) {
       	   	if(!pmOrders.getString("orde_reneworderid").equals("")){
       	   	String id = (pmOrders.getString("orde_originreneworderid"));
           		sql = " select orde_code,prrt_rentalscheduledate,prrt_enddate,prrt_rentincrease,prrt_currentIncome,prrt_initialIconme FROM orders LEFT JOIN  "+
           				" propertiesrent ON (prrt_orderid = orde_orderid) LEFT JOIN "+
           				" properties ON (prty_propertyid = prrt_propertyid)  where "+
           				" orde_originreneworderid = "+id+" LIMIT 1;";
           				
                   		pmOrderRenew.doFetch(sql);
	    	}
       	   	
	    	if (pmOrders.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	    		fullName = pmOrders.getString("cust_code") + " " + pmOrders.getString("cust_legalname");	
   			else
   				fullName = pmOrders.getString("cust_code") + " " + pmOrders.getString("cust_displayname");
	    	
	    	bmoCustomer.getCustomercategory().setValue(pmOrders.getString("customers","cust_customercategory"));

	    	 if ((pmOrders.getInt("orde_orderid") != orderid)) {
	    		 orderid = pmOrders.getInt("orde_orderid");
 	    		   
	    		   //Conversion del pedido a la moneda destino(seleccion del filtro) 
	               int currencyIdOrigin = 0, currencyIdDestiny = 0;
	               double parityOrigin = 0, parityDestiny = 0;
	               currencyIdOrigin = pmOrders.getInt("orde_currencyid");
	               parityOrigin = pmOrders.getDouble("orde_currencyparity");
	               currencyIdDestiny = currencyId;
	               parityDestiny = defaultParity;
	               
	    	%>       
	               <tr>
	                   <td class="reportGroupCell" colspan="19">
	                   <%
	                   if(!pmOrders.getString("orde_reneworderid").equals("")){
	                	   if(pmOrderRenew.next()){
	                   %>
	            		<%= pmOrderRenew.getString("orde_code")%>|
	                    <%= pmOrders.getString("orde_code")+" "+ pmOrders.getString("orde_name")%><br>		
	                   <%
	                    }
	                   }else{
	                	   %>
	                	   <%=pmOrders.getString("orde_code")+" "+ pmOrders.getString("orde_name")%><br>	
	                	   <% 
	                   }
	                   pmOrderRenew.doFetch(sql);
	                   %>				                   		
	                   		
	                   		<%= (fullName)%> <br>
	                   		Arrendatario:
	                   		<%= pmOrders.getString("cust_code")%>
	                   		<%= (pmOrders.getString("cust_displayname"))%> |
	                   		<% 	                   		
	                   		if(!pmOrders.getString("orde_reneworderid").equals("")){
	                   			while(pmOrderRenew.next()){
	                   				%>
									F.1er. Renta:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_rentalscheduledate"))%> <br>
	    	                   		F. Vencimiento:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_enddate")) %>|
	    	                   		F. Incremento:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_rentincrease")) %><br>
	    	                   		R. Vigente:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_currentIncome")) %>|
	    	                   		R. Inicial:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_initialIconme")) %>|<%
	                   		}
	                   		}else{
	                   		%>F.1er. Renta:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_rentalscheduledate"))%> <br>
	                   		F. Vencimiento:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_enddate")) %>|
	                   		F. Incremento:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_rentincrease")) %><br>
	                   		R. Vigente:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_currentIncome")) %>|
	                   		R. Inicial:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_initialIconme")) %>
              		<%} %>
	                   </td>
			        </tr>		
			          
		            <tr class="">
	    			   <td class="reportHeaderCellCenter">#</td>	
		               <td class="reportHeaderCell">Clave</td>
		               <td class="reportHeaderCell">Vendedor</td>
		               <td class="reportHeaderCell">Programaci&oacute;n</td>		        
		               <td class="reportHeaderCell">Pago</td>
		               <td class="reportHeaderCellRight">Total</td>
		               <td class="reportHeaderCellRight">Pagos</td>
		               <td class="reportHeaderCellRight">Saldo</td>
		           </tr>
	   <% 		} 
	    	
			int x = 1;
	    	double amount = 0;                        
	    	double amountW = 0;
	    	double amountD = 0;    
	    	double balance = 0;
	    	double tax = 0;
	    	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
// 	    	if (enableBudgets) {
// 	    		 sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
// 	    		 + " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
// 	    		 + SQLUtil.formatKind(sFParams, "budgetitemtypes")
// 	    		 + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
// 	    		 + SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
// 	    		 +" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
// 	    		+" ON(area_areaid = racc_areaid) ";
// 	    	}
	    	sql +=	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertiesrent")+" ON (prrt_orderid = orde_originreneworderid)"+
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON (prty_propertyid = prrt_propertyid)"+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credits")+" ON (orde_orderid = cred_orderid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
	    			" WHERE racc_raccountid > 0 " +
	    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
	    			" AND racc_orderid = " + pmOrders.getInt("orde_orderid") +
	    			where +
// 	    			whereFailure +
// 	    			whereLinked +
	    			" ORDER BY racc_raccountid ASC ";
		   pmRaccounts.doFetch(sql);                           
	       while (pmRaccounts.next()) {
	    	   bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts","racc_status"));
	           bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts","racc_paymentstatus"));
	   %>
			   <tr class="reportCellEven">
				   <%= HtmlUtil.formatReportCell(sFParams, "" + x, BmFieldType.NUMBER) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
<%-- 				   <%	if (enableBudgets) { %> --%>
<%-- 	        		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %> --%>
<%-- 		        	   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %> --%>
<%-- 		           <%} %> --%>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("users","user_code"), BmFieldType.STRING) %>
<%-- 				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("companies","comp_name")), BmFieldType.STRING) %>                    		    --%>
<%-- 				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_description")), BmFieldType.STRING) %>                                                 --%>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_duedate"), BmFieldType.DATE) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
<%-- 				   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getString("cureRacc","cure_code"), BmFieldType.CODE) %>                    		    --%>
<%-- 				   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getDouble("racc_currencyparity"), BmFieldType.NUMBER) %>                    		    --%>

				   <%
				   //Conversion a la moneda destino(seleccion del filtro)
				   int currencyIdOrigin = 0, currencyIdDestiny = 0;
				   double parityOrigin = 0, parityDestiny = 0;
				   currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
				   parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
				   currencyIdDestiny = currencyId;
				   parityDestiny = defaultParity;
			
				   double amountRacc = pmRaccounts.getDouble("racc_amount");
				   double taxRacc = pmRaccounts.getDouble("racc_tax");
				   double totalRacc = pmRaccounts.getDouble("racc_total");
				   double paymentsRacc = pmRaccounts.getDouble("racc_payments");					           		
				   double balanceRacc = pmRaccounts.getDouble("racc_balance");
			
				   amountRacc = pmCurrencyExchange.currencyExchange(amountRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   taxRacc = pmCurrencyExchange.currencyExchange(taxRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   paymentsRacc = pmCurrencyExchange.currencyExchange(paymentsRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   balanceRacc = pmCurrencyExchange.currencyExchange(balanceRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			
				   amount += amountRacc;
				   tax += taxRacc; 
				   amountW += totalRacc;
				   amountD += paymentsRacc;
				   balance += balanceRacc;
				   %>	      
	                    <%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
	               </tr>
					
	         </TR>
	         <%
	         x++;
	         y++;
	       }
	       
	   		if(amountW > 0 || amountD > 0) {
	   %>
	               <tr class="reportCellEven reportCellCode">
	                    <td colspan="5">&nbsp;</td>
	                    <%= HtmlUtil.formatReportCell(sFParams, "" + amountW, BmFieldType.CURRENCY) %>
	                    <%= HtmlUtil.formatReportCell(sFParams, "" + amountD, BmFieldType.CURRENCY) %>
	                    <%= HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>
	               </tr>
	   <%
	   		}
	   		amountT += amount;
	   		taxT += tax;
	   		withDrawTotal += amountW;
	   		depositTotal += amountD;
	   		balanceT += balance;
	          
	          
	    } //End while pmOrders
	    
	    %>
	    
	    <%
	    int items = 0;
	    sql = " SELECT COUNT(*) as cxc FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
	    sql +=  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +    
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertiesrent")+" ON (prrt_orderid = orde_orderid)"+
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON (prty_propertyid = prrt_propertyid)"+
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +                       	      
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +                                      
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
	        	  " WHERE racc_orderid is null " +
	        	  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " + 
	        	  where +
// 	        	  whereFailure +
// 	        	  whereLinked +
	        	  " ORDER BY racc_customerid ";
	    pmRaccounts.doFetch(sql);
	    if (pmRaccounts.next()) items = pmRaccounts.getInt("cxc");
	    
	    if (items > 0) {
	    %>
	    	<tr><td colspan="19">&nbsp;</td></tr>
		    <tr>
		       <td class="reportHeaderCell" colspan="19" align="left">
		       		Cuentas por Cobrar sin Pedido
		       </td>	                   
		    </tr>
		    <%
		    
		    double amountW = 0, amountD = 0, amount = 0,tax = 0;
		    int customerId = 0;
		    int i = 1;
		    //Listar las CxC que no estan ligadas a un pedido
		    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ")+
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertiesrent")+" ON (prrt_orderid = orde_orderid)"+
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON (prty_propertyid = prrt_propertyid)"+
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) " +
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +                       	      
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +                                      
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
		    	  " WHERE racc_orderid is null " +
		    	  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " + 
		    	  where +
// 		    	  whereFailure +
// 		    	  whereLinked +
		    	  " ORDER BY racc_customerid ";
		    pmRaccounts.doFetch(sql);                           
		    while (pmRaccounts.next()) {
		    	if(pmOrders.getString("prrt_enddate").equals("")){
		       	   	String id = (pmOrders.getString("orde_originreneworderid"));
		           		sql = " select prrt_rentalscheduledate,prrt_enddate,prrt_rentincrease,prrt_currentIncome,prrt_initialIconme FROM orders LEFT JOIN  "+
		           				" propertiesrent ON (prrt_orderid = orde_orderid) LEFT JOIN "+
		           				" properties ON (prty_propertyid = prrt_propertyid)  where "+
		           				" orde_originreneworderid = "+id+" LIMIT 1;";
		                   		pmOrderRenew.doFetch(sql);
			    	}

		    	if (pmRaccounts.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
		    		fullName = pmRaccounts.getString("cust_code") + " " + pmRaccounts.getString("cust_legalname");	
	   			else
	   				fullName = pmRaccounts.getString("cust_code") + " " + pmRaccounts.getString("cust_displayname");
		    	
		    	bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts","racc_status"));
		        bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts","racc_paymentstatus"));
		    	
		    	if ((pmRaccounts.getInt("racc_customerid") != customerId)) {
		    		customerId = pmRaccounts.getInt("racc_customerid");
		    		i = 1;
		    		//Conversion del pedido a la moneda destino(seleccion del filtro) 
		               int currencyIdOrigin = 0, currencyIdDestiny = 0;
		               double parityOrigin = 0, parityDestiny = 0;
		               currencyIdOrigin = pmOrders.getInt("orde_currencyid");
		               parityOrigin = pmOrders.getDouble("orde_currencyparity");
		               currencyIdDestiny = currencyId;
		               parityDestiny = defaultParity;
		%>
		                  <tr>
	                   <td class="reportGroupCell" colspan="19">				                   		
	                   		<%= pmOrders.getString("orde_code")+" "+ pmOrders.getString("orde_name")%>|
	                   		<%= (fullName)%> |
	                   		Arrendatario:
	                   		<%= pmOrders.getString("orde_code")+" "+ pmOrders.getString("orde_name")%>
	                   		<%= (pmOrders.getString("cust_displayname"))%> |
	                   		<% 	                   		
	                   		if(pmOrders.getString("prrt_enddate").equals("")){
	                   			while(pmOrderRenew.next()){
	                   			
	                   			%>F.1er. Renta:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_rentalscheduledate"))%> |
	    	                   		F. Vencimiento:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_enddate")) %>|
	    	                   		F. Incremento:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_rentincrease")) %>|
	    	                   		R. Vigente:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_currentIncome")) %>|
	    	                   		R. Inicial:
	    	                   		<%= HtmlUtil.stringToHtml(pmOrderRenew.getString("prrt_initialIconme")) %>|<%
	                   		}
	                   		}else{
	                   		%>F.1er. Renta:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_rentalscheduledate"))%> |
	                   		F. Vencimiento:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_enddate")) %>|
	                   		F. Incremento:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_rentincrease")) %>|
	                   		R. Vigente:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_currentIncome")) %>|
	                   		R. Inicial:
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("prrt_initialIconme")) %>|
              		<%} %>
	                   </td>
			        </tr>			        
		              <tr class="">
	    			   <td class="reportHeaderCellCenter">#</td>	
		               <td class="reportHeaderCell">Clave</td>
		               <td class="reportHeaderCell">Vendedor</td>
		               <td class="reportHeaderCell">Programaci&oacute;n</td>		        
		               <td class="reportHeaderCell">Pago</td>
		               <td class="reportHeaderCellRight">Total</td>
		               <td class="reportHeaderCellRight">Pagos</td>
		               <td class="reportHeaderCellRight">Saldo</td>
		           </tr>
		       <% } %>
		       <TR class="reportCellEven">
			       <%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
			       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
<%-- 			       <%	if (enableBudgets) { %> --%>
<%-- 	        	   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %> --%>
<%-- 		           <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %> --%>
<%-- 		        	<%	} %> --%>
			       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("users","user_code"), BmFieldType.STRING) %>
			       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_invoiceno")), BmFieldType.STRING) %>
			       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_reference")), BmFieldType.STRING) %>
<%-- 			       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("companies","comp_name")), BmFieldType.STRING) %> --%>
<%-- 			       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_description")), BmFieldType.STRING) %> --%>
			       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_receivedate"), BmFieldType.DATE) %>
			       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_duedate"), BmFieldType.DATE) %>
			        <%	if (sFParams.isFieldEnabled(bmoRaccount.getLinked())) { 
					    		String raccLinked = ((pmRaccounts.getBoolean("racc_linked") ? "Si" : "No"));
					%>	
					   			<%= HtmlUtil.formatReportCell(sFParams, raccLinked, BmFieldType.STRING) %>
				   <%	} %>
			       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
			       <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
<%-- 			       <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("cureRacc", "cure_code"), BmFieldType.CODE) %> --%>
<%-- 			       <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getDouble("racc_currencyparity"), BmFieldType.NUMBER) %>                                --%>
			       <%
			       //Conversion a la moneda destino(seleccion del filtro)
			       int currencyIdOrigin = 0, currencyIdDestiny = 0;
			       double parityOrigin = 0, parityDestiny = 0;
			       currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
			       parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
			       currencyIdDestiny = currencyId;
			       parityDestiny = defaultParity;
			
			       double amountRacc = pmRaccounts.getDouble("racc_amount");
			       double taxRacc = pmRaccounts.getDouble("racc_tax");
			       double totalRacc = pmRaccounts.getDouble("racc_total");
			       double paymentsRacc = pmRaccounts.getDouble("racc_payments");					           		
			       double balanceRacc = pmRaccounts.getDouble("racc_balance");
			
			       amountRacc = pmCurrencyExchange.currencyExchange(amountRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			       taxRacc = pmCurrencyExchange.currencyExchange(taxRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			       totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			       paymentsRacc = pmCurrencyExchange.currencyExchange(paymentsRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			       balanceRacc = pmCurrencyExchange.currencyExchange(balanceRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			       
			       amountT += amountRacc;
			       taxT += taxRacc;
			       withDrawTotal += totalRacc;
			       depositTotal += paymentsRacc;
			       balanceT += balanceRacc;
			       %>
			       <%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc , BmFieldType.CURRENCY) %>
			       <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRacc, BmFieldType.CURRENCY) %>
			       <%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
		       </TR>
		       <%  
		       i++;
		       y++;
		    } //fin while
	    }// fin if  
	    %>
		
	    <%
	    if (withDrawTotal > 0 || depositTotal > 0) {
	    %>
	    <tr><td colspan="15">&nbsp;</td></tr>
		    <tr class="reportCellEven reportCellCode">
			    <%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
			    <td colspan="4">&nbsp;</td>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + withDrawTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + depositTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceT, BmFieldType.CURRENCY) %>
		    </tr>
	<%	}
	} // Fin else moneda
	%>
</TABLE>  
<%
	
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmConn.close();
	pmRaccounts.close();
	pmOrders.close(); 
	pmOrderRenew.close();
	pmConnG.close();
%>  
	<% if (print.equals("1")) { %>
		<script>
			//window.print();
		</script>
	<% } %>
  </body>
</html>