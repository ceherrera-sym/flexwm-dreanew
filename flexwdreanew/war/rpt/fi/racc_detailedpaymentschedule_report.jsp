<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author pguerra
 * @version 2017-12
 */ -->
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
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
<%@include file="/inc/login.jsp"%>
<%@page import="com.symgae.shared.SQLUtil"%>

<%
	// Inicializar variables
 	String title = "Reporte de Flujo por Proyecto";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	BmoRaccount bmoRaccount = new BmoRaccount();
   	BmoOrder bmoOrder = new BmoOrder();
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
   	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	BmoCustomer bmoCustomer = new BmoCustomer();
	// Conexion bd
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	   	
   	String sql = "", where = "", whereReceiveDate = "", whereOrderId = "", whereSupl = "", whereFailure = "", whereLinked = "", sqlCurrency = "";
   	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "";
   	String status = "", paymentStatus = "", paymentStatus2 = "", customerCategory ="";
   	String filters = "", areaId = "-1", budgetItemId = "";
   	int programId = 0, customerId = 0, orderId = 0, cols= 0, raccountTypeId = 0, companyId = 0, wflowCategoryId = 0, 
   			yearStart = 0, monthStart = 0, dayStart = 0, yearEnd = 0, monthEnd = 0, dayEnd = 0, 
   			userId = 0, collectorUserId = 0, currencyId = 0, failure = 0, linked = 0, budgetId = 0, activeBudgets = 0;
	boolean enableBudgets = false;
	// dynamicColspan incrementar por cada columna del reporte
	// dynamicColspanMinus incrementar por cada columna que vaya a mostrar totales(es decir, se va a restar al dynamicColspan si HAY FILA TOTALES)
	int dynamicColspan = 0, dynamicColspanMinus = 0;
	// se agrega 2 columnas para presupuestos, para manejo de colspans
   	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
   		activeBudgets = 2;
   	}
		
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));	
   	if (request.getParameter("racc_customerid") != null) customerId = Integer.parseInt(request.getParameter("racc_customerid"));
   	if (request.getParameter("racc_companyid") != null) companyId = Integer.parseInt(request.getParameter("racc_companyid"));
   	if (request.getParameter("racc_orderid") != null) orderId = Integer.parseInt(request.getParameter("racc_orderid"));
   	if (request.getParameter("racc_status") != null) status = request.getParameter("racc_status");
   	if (request.getParameter("racc_paymentstatus") != null) paymentStatus = request.getParameter("racc_paymentstatus");
   	if (request.getParameter("racc_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("racc_currencyid"));
   	if (request.getParameter("wfca_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfca_wflowcategoryid"));   	   	
   	if (request.getParameter("racc_receivedate") != null) receiveDate = request.getParameter("racc_receivedate");
   	if (request.getParameter("racc_duedate") != null) dueDate = request.getParameter("racc_duedate");
   	if (request.getParameter("receiveenddate") != null) receiveEndDate = request.getParameter("receiveenddate");    
    if (request.getParameter("dueenddate") != null) dueEndDate = request.getParameter("dueenddate");
    if (sFParams.isFieldEnabled(bmoRaccount.getFailure())) {
    	if (request.getParameter("racc_failure") != null) failure = Integer.parseInt(request.getParameter("racc_failure"));
    }
	if (sFParams.isFieldEnabled(bmoRaccount.getLinked())) {
    	if (request.getParameter("racc_linked") != null) linked = Integer.parseInt(request.getParameter("racc_linked"));
	}
    if (request.getParameter("userid") != null) userId = Integer.parseInt(request.getParameter("userid"));
    if (request.getParameter("racc_collectuserid") != null) collectorUserId = Integer.parseInt(request.getParameter("racc_collectuserid"));
    if (request.getParameter("cust_customercategory") != null) customerCategory = request.getParameter("cust_customercategory");
    if( enableBudgets){
 	   if (request.getParameter("bgit_budgetid") != null) budgetId = Integer.parseInt(request.getParameter("bgit_budgetid"));
 	   if (request.getParameter("racc_budgetitemid") != null) budgetItemId = request.getParameter("racc_budgetitemid");
 	   if (request.getParameter("racc_areaid") != null) areaId = request.getParameter("racc_areaid");
 	}
    
    bmoProgram = (BmoProgram)pmProgram.get(programId);
    
	// Filtros listados
	if (!customerCategory.equals("")) {
        where += SFServerUtil.parseFiltersToSql("cust_customercategory", customerCategory);
        filters += "<i>Categoría Cliente: </i>" + request.getParameter("cust_customercategoryLabel") + ", ";
    }
	if (customerId > 0) {
		where += " AND racc_customerid = " + customerId;
		filters += "<i>Cliente: </i>" + request.getParameter("racc_customeridLabel") + ", ";
	}
	
	if (orderId > 0) {
        whereOrderId += " AND racc_orderid = " + orderId;
        filters += "<i>Pedido: </i>" + request.getParameter("racc_orderidLabel") + ", ";
    }

	if (wflowCategoryId > 0) {
		where += " AND wfca_wflowcategoryid = " + wflowCategoryId;
		filters += "<i>Categoria: </i>" + request.getParameter("wfca_wflowcategoryidLabel") + ", ";
	}
	
	if (companyId > 0) {
        where += " AND racc_companyid = " + companyId;
        filters += "<i>Empresa: </i>" + request.getParameter("racc_companyidLabel") + ", ";
    }
	
    if (userId > 0) {
    	where += " AND orde_userid = " + userId;
    	filters += "<i>Usuario: </i>" + request.getParameter("useridLabel") + ", ";
	}
	
	if (collectorUserId > 0) {
    	where += " AND racc_collectuserid = " + collectorUserId;
    	filters += "<i>Cobranza: </i>" + request.getParameter("racc_collectuseridLabel") + ", ";
	}
	
	if (sFParams.isFieldEnabled(bmoRaccount.getFailure())) {
	  	if (failure >= 0) {
	  		whereFailure += " AND racc_failure = " + failure;
	  		filters += "<i>CxC Falla?: </i>" + ((failure == 1) ? "Si" : "No") + ", ";
	  	} else {
	  		filters += "<i>CxC Falla?: Todos</i>" + ", ";
	  	}
   	}
	
	if (sFParams.isFieldEnabled(bmoRaccount.getLinked())) {
	  	if (linked >= 0 && linked < 2) {
	  		whereLinked += " AND racc_linked = " + linked;
	  		filters += "<i>Externa: </i>" + ((linked == 1) ? "Si" : "No") + ", ";
	  	} else {
	  		filters += "<i>Externa: Todos</i>" + ", ";
	  	}
   	}
	
	if (enableBudgets) {
	    if (budgetId > 0) {
	        where += " AND bgit_budgetid = " + budgetId;
	        filters += "<i>Presup.: </i>" + request.getParameter("bgit_budgetidLabel") + ", ";
	    }  
	
	    if (!budgetItemId.equals("")) {
	        where += SFServerUtil.parseFiltersToSql("racc_budgetitemid", budgetItemId);
	        //where += " AND racc_budgetitemid = " + budgetItemId;
	        filters += "<i>Item Presup.: </i>" + request.getParameter("racc_budgetitemidLabel") + ", ";
	    }
	    
	    if (!areaId.equals("")) {
	        where += SFServerUtil.parseFiltersToSql("racc_areaid", areaId);
	        //where += " AND racc_areaid = " + areaId;
	        filters += "<i>Departamento.: </i>" + request.getParameter("racc_areaidLabel") + ", ";
	    }
    }

   	if (!receiveDate.equals("")) {
   		where += " AND racc_receivedate >= '" + receiveDate + "'";
   		filters += "<i>Recepci&oacute;n Inicio: </i>" + receiveDate + ", ";
   	}
   	
    if (!receiveEndDate.equals("")) {
        where += " AND racc_receivedate <= '" + receiveEndDate + "'";
        filters += "<i>Recepci&oacute;n Final: </i>" + receiveEndDate + ", ";
    }
   	   	
   	if (!dueDate.equals("")) {
   		whereReceiveDate += " AND racc_duedate >= '" + dueDate + "'";
        monthStart = FlexUtil.getMonth(sFParams, dueDate);
   		yearStart = FlexUtil.getYear(sFParams, dueDate);

        filters += "<i>F. Prog. Inicio: </i>" + dueDate + ", ";
   } else {
   	   	// Si esta vacio Tomar fecha programacion mas antigua de las cxc

   		String sqlDueDateMin = "SELECT MIN(racc_duedate) as dueDateMin FROM " + SQLUtil.formatKind(sFParams, "raccounts");
    	pmConn.doFetch(sqlDueDateMin);
    	if (pmConn.next()) dueDate = pmConn.getString("dueDateMin");

    	whereReceiveDate += " AND racc_duedate >= '" + dueDate + "'";
        monthStart = FlexUtil.getMonth(sFParams, dueDate);
   		yearStart = FlexUtil.getYear(sFParams, dueDate);

        //filters += "<i>F. Prog. Inicio: </i>" + dueDate + ", ";
    }
    
   	if (!dueEndDate.equals("")) {
   		whereReceiveDate += " AND racc_duedate <= '" + dueEndDate + "'";
        monthEnd = FlexUtil.getMonth(sFParams, dueEndDate);
   		yearEnd = FlexUtil.getYear(sFParams, dueEndDate);

        filters += "<i>F. Prog. Final: </i>" + dueEndDate + ", ";
    } else {
    	// Si esta vacio Tomar fecha programacion mas (alta)reciente de las cxc

   		String sqlDueDateMax = "SELECT MAX(racc_duedate) as dueDateMax FROM " + SQLUtil.formatKind(sFParams, "raccounts");
    	pmConn.doFetch(sqlDueDateMax);
    	if (pmConn.next()) dueEndDate = pmConn.getString("dueDateMax");

    	whereReceiveDate += " AND racc_duedate <= '" + dueEndDate + "'";
    	monthEnd = FlexUtil.getMonth(sFParams, dueEndDate);
    	yearEnd = FlexUtil.getYear(sFParams, dueEndDate);

        //filters += "<i>F. Prog. Final: </i>" + dueDate + ", ";
    }
    
    // tomar anio en curso por defecto
	if (dueDate.equals("") &&  dueEndDate.equals("")) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		String dueDateStart = "" + year + "-01-01";
		String dueDateEnd = "" + year + "-12-31";
		
		monthStart = FlexUtil.getMonth(sFParams, dueDateStart);
   		yearStart = FlexUtil.getYear(sFParams, dueDateStart);
   		
		monthEnd = FlexUtil.getMonth(sFParams, dueDateEnd);
   		yearEnd = FlexUtil.getYear(sFParams, dueDateEnd);

   		whereReceiveDate += " AND racc_duedate >= '" + year + "-01-01'";
		whereReceiveDate += " AND racc_duedate <= '" + year + "-12-31'";
		
	 	filters += "<i>Prog. Inicio Default: </i>" + dueDateStart + ", ";
	    filters += "<i>Prog. Final Default: </i>" + dueDateEnd + ", ";
	}
   	
   	if (!status.equals("")) {   		
   		//where += " AND racc_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("racc_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("racc_statusLabel") + ", ";   		
   	}
   	
   	if (!paymentStatus.equals("")) {
        //where += " AND racc_paymentstatus like '" + paymentStatus + "'";
        where += SFServerUtil.parseFiltersToSql("racc_paymentstatus", paymentStatus);
        filters += "<i>Estatus Pago: </i>" + request.getParameter("racc_paymentstatusLabel") + ", ";
    }

   	
   	//Obtener la paridad de la moneda
   	double nowParity = 0;
   	double defaultParity = 0;
   	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
   	if (currencyId > 0) {
   		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);   		
   		defaultParity = bmoCurrency.getParity().toDouble();
   		filters += "<i>Moneda: </i>" + request.getParameter("racc_currencyidLabel")  + " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
   	} else {
   		filters += "<i>Moneda: </i> Todas ";
   		// Se mete la consulta padre para agilizar el proceso
   		sqlCurrency = " SELECT DISTINCT(cure_currencyid), cure_code, cure_name, cure_parity " +
		    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
	    		if (enableBudgets) {
	    			sqlCurrency += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
	    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
	    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
	    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
	    	    }
   		sqlCurrency += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
	    			" WHERE racc_customerid > 0 " +
// 	    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
	    			whereOrderId +
	    			where +
	    			whereReceiveDate +
	    			whereFailure +
	    			whereLinked +
					" ORDER BY cure_currencyid ASC";
   	}
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmRaccount(sFParams).getDisclosureFilters();   
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    // Conexiones BD
   	
   	PmConn pmCustomer = new PmConn(sFParams);
   	pmCustomer.open();
   	
   	PmConn pmOrders = new PmConn(sFParams);
    pmOrders.open();
   	
   	PmConn pmRaccounts = new PmConn(sFParams);
   	pmRaccounts.open();
   	
    PmConn pmCurrencyWhile =new PmConn(sFParams);
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
	
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃº(clic-derecho).
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
<link rel="stylesheet" type="text/css"
	href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

	<table border="0" cellspacing="0" cellpading="0" width="100%">
		<tr>
			<td align="LEFT" width="80" rowspan="2" valign="top"><img
				border="0" width="<%= SFParams.LOGO_WIDTH %>"
				height="<%= SFParams.LOGO_HEIGHT %>"
				src="<%= sFParams.getMainImageUrl() %>"></td>
			<td class="reportTitle" colspan="2"><%= title %></td>
		</tr>
		<tr>
			<td class="reportSubTitle"><b>Filtros:</b> <%= filters %> <br>
				<b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Cliente" : "Cliente")%>.
				<b>Ordenado por:</b> Clave Cliente, Clave CxC</td>
			<td class="reportDate" align="right">Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %>
				por: <%= sFParams.getLoginInfo().getEmailAddress() %>
			</td>
		</tr>
	</table>
	<br>
	<table class="report" border="0">
	<%
	
	// Total IVA
	double taxT = 0;

	// contador cliente
    int c = 0;
	
	// Se guarda el mes fin  porque se va a modificar adelante 
	int monthEndFinal = monthEnd;
	int monthEndStart = monthStart;
	int countRaccsFile = 0, countMonthsColumn = 0;
	boolean moreYears = false;
	// Sacar meses de los años seleccionados
	for (int anio = yearStart; anio <= yearEnd; anio++) {
		// Si tiene mas de 1 anio, sacar los meses siguientes hasta el mes del anio fin
		// ej: de febrero de 2017 hasta febrero de 2019
		// entonces 2018 va a sacar todos los meses de ese anio 
		// y cuando llegue a 2019 lo dejara hasta el mes seleccionado
		if (anio < yearEnd) {
			if (moreYears) {
				monthStart = 1;
				monthEnd = 12;
			}
			else {
				moreYears = true;
				monthEnd = 12;
			}
		}
		
		// Si es el mismo anio, dejar como estaba el mes fin seleccionado
		if (anio == yearEnd) {
			monthEnd = monthEndFinal;
		}
		
		for (int month = monthStart; month <= monthEnd; month++) {
			countMonthsColumn++;
		}
	}
	// Regresar fechas origen
	monthEnd = monthEndFinal;
	monthStart = monthEndStart;
	

	sql = " SELECT COUNT(racc_customerid) AS contador  " +
    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
    		if (enableBudgets) {
    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
    	    }
	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
			" WHERE racc_raccountid > 0 " +
// 			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
			whereOrderId +
			where +
			whereReceiveDate +
			whereFailure +
			whereLinked +
			" ORDER BY racc_code ASC";
			System.out.println("sql:: "+sql);
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
			// Reiniciar suma total de iva por cada moneda
			taxT = 0;
			dynamicColspan = 0;
			// Regresar fechas origen para cada moneda
			monthEnd = monthEndFinal;
			monthStart = monthEndStart;

			if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
				currencyId = currencyIdWhile;
		    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
        		%>
        		<tr>
            		<td class="reportHeaderCellCenter" colspan="<%= 8 + countMonthsColumn%>">
            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
            		</td>
        		</tr>
        		<%
        	}
			
			%>
			<tr class="">
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCustomerId())) { dynamicColspan++; %>
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getCustomerId()))%></th>
			<%	} %>	
			<%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { dynamicColspan++; %>
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getCustomercategory()))%></th>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getOrderId())) { dynamicColspan++; %>
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getOrderId()))%></th>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCode())) { dynamicColspan++; %>	
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getCode()))%></th>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCustomerId())) { dynamicColspan++; %>	
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getInvoiceno()))%></th>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getReceiveDate())) { dynamicColspan++; %>		
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getReceiveDate()))%></th>	
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getInvoiceno())) { dynamicColspan++; %>		
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getDueDate()))%></th>	
			<%	} %>
			
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getReqPayTypeId())) { dynamicColspan++; %>
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getReqPayTypeId()))%></th>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getTax())) { dynamicColspan++; %>	
				<th class="reportHeaderCellRight"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getTax()))%></th>	
			<% }

				// Se guarda el mes fin  porque se va a modificar adelante 
				monthEndFinal = monthEnd;
				monthEndStart = monthStart;
				countRaccsFile = 0; 
				//countMonthsColumn = 0;
				moreYears = false;
				// Sacar meses de los años seleccionados
				for (int anio = yearStart; anio <= yearEnd; anio++) {
					// Si tiene mas de 1 anio, sacar los meses siguientes hasta el mes del anio fin
					// ej: de febrero de 2017 hasta febrero de 2019
					// entonces 2018 va a sacar todos los meses de ese anio 
					// y cuando llegue a 2019 lo dejara hasta el mes seleccionado
					if (anio <= yearEnd) {
						if (moreYears) {
							monthStart = 1;
							monthEnd = 12;
						}
						else {
							moreYears = true;
							monthEnd = 12;
						}
					}
					
					// Si es el mismo anio, dejar como estaba el mes fin seleccionado
					if (anio == yearEnd) {
						monthEnd = monthEndFinal;
					}

	 				for (int month = monthStart; month <= monthEnd; month++) {
						%>
						<th class="reportHeaderCell">
							<%= FlexUtil.getMonthName(sFParams, anio + "-" + month + "-01").substring(0, 3) %>(<%= anio %>)
						</th>
						
						<%
						//countMonthsColumn++;
	 				}
				}
				// Regresar fechas origen
				monthEnd = monthEndFinal;
				monthStart = monthEndStart;
				%>
			</tr>

			<%
			// Contar todas la cxc para armar la matriz y para rowspan del cliente
			sql = " SELECT COUNT(racc_customerid) AS countAllRacc  " +
			    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
			    		if (enableBudgets) {
			    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
			    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
			    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
			    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
			    	    }
			    		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
		    			" WHERE racc_customerid > 0 " +
// 		    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
		    			" AND racc_currencyid = " + currencyId +
		    			whereOrderId +
		    			where +
		    			whereReceiveDate +
		    			whereFailure +
		    			whereLinked +
						" ORDER BY racc_code ASC";

		    			pmConn.doFetch(sql);
		    			if (pmConn.next()) countRaccsFile = pmConn.getInt("countAllRacc");
			
		    // Traer los clientes de las CXC
			sql = " SELECT DISTINCT(racc_customerid) AS cliente,  " +
		    		" cust_customerid, cust_code, cust_legalname, cust_displayname, cust_customertype" +
		    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
		    		if (enableBudgets) {
		    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
		    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
		    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
		    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
		    	    }
			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
	    			" WHERE racc_customerid > 0 " +
// 	    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
	    			" AND racc_currencyid = " + currencyId +
	    			whereOrderId +
	    			where +
	    			whereReceiveDate +
	    			whereFailure +
	    			whereLinked +
					" ORDER BY cliente ASC";
	    			
		    pmCustomer.doFetch(sql);
		    
//	 	    System.out.println("countRaccsFile: "+countRaccsFile);
//	 	    System.out.println("countMonthsColumn: " +countMonthsColumn);

			// Matriz para guardar los montos
		    double[][] sumTotal = new double[(countRaccsFile)][(countMonthsColumn)];
		    
			
		    // OJO: customerid NO es igual a la variable que se obtiene de los filtros
		    int customerid = 0, r = 0;
			// Clientes
	       	String customer = "";
		    while (pmCustomer.next()) {

		    	if (pmCustomer.getInt("cust_customerid") != customerid) {
		    		customerid = pmCustomer.getInt("cust_customerid");

	            	// Cliente
		        	if (pmCustomer.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	   					customer = pmCustomer.getString("cust_code") + " " + pmCustomer.getString("cust_legalname");	
	      			else
	      				customer = pmCustomer.getString("cust_code") + " " + pmCustomer.getString("cust_displayname");

		        	// Contar los clientes de las CXC para rowspan
	            	int countRaccsByCustomerFile = 0;
					sql = " SELECT COUNT(racc_raccountid) AS countRaccsByCustomer  " +
				    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
					if (enableBudgets) {
		    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
		    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
		    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
		    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
		    	    }
					sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
			    			" WHERE racc_customerid > 0 " +
// 			    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
			    			" AND racc_currencyid = " + currencyId +
			    			" AND racc_customerid = " + customerid +
			    			whereOrderId +
			    			where +
			    			whereReceiveDate +
			    			whereFailure +
			    			whereLinked +
							" ORDER BY racc_raccountid ASC";
			    			
				    pmConn.doFetch(sql);
	            	if (pmConn.next()) countRaccsByCustomerFile = pmConn.getInt("countRaccsByCustomer");
	            	
				   	%>
				   	<tr class="reportCellEven">
		       			<td class="reportCellText" rowspan="<%= countRaccsByCustomerFile%>">
		       				<%= HtmlUtil.stringToHtml(customer)%>
		       			</td>
		       			<%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { %>
		       			<td class="reportCellText" rowspan="<%= countRaccsByCustomerFile%>">
	       					<%= HtmlUtil.stringToHtml(bmoCustomer.getCustomercategory().getSelectedOption().getLabel())%>
	       				</td>
	       				<%}%>	
				   	<%
				}

		    	// CXC del cliente
		    	sql = " SELECT racc_raccountid, racc_code, racc_invoiceno,ract_category, ract_type,racc_description, racc_currencyid, racc_currencyparity, " +
		    			" racc_balance, racc_paymentstatus, racc_amount, racc_tax, racc_duedate, " +
		    			" racc_orderid, racc_autocreate, orde_code, orde_name, rqpt_name, racc_receivedate " +
			    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
			    		if (enableBudgets) {
			    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
			    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
			    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
			    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
			    	    }
		    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "reqpaytypes")+" ON (racc_reqpaytypeid = rqpt_reqpaytypeid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
		    			" WHERE racc_raccountid > 0 " +
		    			" AND racc_customerid = " + customerid +
// 		    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
		    			" AND racc_currencyid = " + currencyId +
		    			whereOrderId +
		    			where +
		    			whereReceiveDate +
		    			whereFailure +
		    			whereLinked +
						" ORDER BY racc_orderid ASC, racc_raccountid asc";
		       	pmRaccounts.doFetch(sql);
		       	 
		   		int	rInOrder = 1, 
		   		    // OJO: orderid NO es igual a la variable que se obtiene de los filtros
		   			orderid = 0;
			   	
			   	// firstInOrder; para controlar(cerrar y abrir correctamente) los etiquetas tr, td
			   	boolean firstInOrder = true;
			   	int rowspanOrder = 0;
		       	while (pmRaccounts.next()) {
		       		// Tomar mes y anio de la Fecha de programacion de la CXC
		       		int monthRaccDueDate = FlexUtil.getMonth(sFParams, pmRaccounts.getString("racc_duedate"));
		       		int yearRaccDueDate = FlexUtil.getYear(sFParams, pmRaccounts.getString("racc_duedate"));
		       		
		       	 	//Conversion a la moneda destino(seleccion del filtro)
					int currencyIdOrigin = 0, currencyIdDestiny = 0;
					double parityOrigin = 0, parityDestiny = 0;
					currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
					parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
					currencyIdDestiny = currencyId;
					parityDestiny = defaultParity;
		       		
					double amountRacc = pmRaccounts.getDouble("racc_amount");
					double taxRacc = pmRaccounts.getDouble("racc_tax");
					double balanceRacc = pmRaccounts.getDouble("racc_balance");

					amountRacc = pmCurrencyExchange.currencyExchange(amountRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);	       		
					taxRacc = pmCurrencyExchange.currencyExchange(taxRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);	       		
					balanceRacc = pmCurrencyExchange.currencyExchange(balanceRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);	       		

					//Si es de tipo Nota de Crédito no sumar las cantidades a los totales
					if(!(pmRaccounts.getString("ract_type").equals(""+BmoRaccountType.TYPE_DEPOSIT) && pmRaccounts.getString("ract_category").equals(""+BmoRaccountType.CATEGORY_CREDITNOTE))){
					// Total de IVA
					taxT += taxRacc;
					}
		       		// Si es el mismo pedido no colocarlo
		       		if (pmRaccounts.getInt("racc_orderid") != orderid) {
		       			orderid = pmRaccounts.getInt("racc_orderid");

		       			// Poner de nuevo en 1, porque aqui el contador lo hace POR PEDIDO
		       			rInOrder = 1;
			       		// Contar las cxc del pedido para Rowspan
				    	sql = " SELECT COUNT(racc_raccountid) AS countRaccByOrder " +
			       				" FROM " + SQLUtil.formatKind(sFParams, "raccounts ");
			       				if (enableBudgets) {
					    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
					    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
					    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
					    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
					    	    }
				    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
				    			" WHERE racc_raccountid > 0 " +
// 				    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
				    			" AND racc_orderid = " + orderid +
				    			" AND racc_currencyid = " + currencyId +
				    			where +
				    			whereReceiveDate +
				    			whereFailure +
				    			whereLinked +
				    			" ORDER BY racc_raccountid ASC";
					   	pmConn.doFetch(sql);

					   	if (pmConn.next()) rowspanOrder = pmConn.getInt("countRaccByOrder");

					   	if (firstInOrder) firstInOrder = false;
						else { %><tr class="reportCellEven"><% } %>
					   	<td class="reportCellText" rowspan="<%= rowspanOrder%>">
	       					<%= pmRaccounts.getString("orders", "orde_code") + " " + HtmlUtil.stringToHtml(pmRaccounts.getString("orders", "orde_name"))%>
	       				</td>
	       				<%
		       		} else {
		       			// Si la cxc NO es de un pedido
		       			if (!(orderid > 0)) {
			       			// Regresa el contador, porque NO HAY PEDIDO en la CXC
		       				rInOrder = 1;
		       				if (firstInOrder) firstInOrder = false;
							else { %><tr class="reportCellEven"><% } %>
		       		
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getString("racc_description"), BmFieldType.STRING) %>
		       		<%
		       			} else {
							// Meter tr si la cxc es de un pedido, 
							// la primera cxc no entra aqui, SOLO es para las demas
		       				%>
	       						<tr class="reportCellEven"> 
	       					<%
		       			}
		       		}
					%>
					<%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("racc_code"), BmFieldType.CODE) %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("racc_invoiceno")), BmFieldType.STRING) %>
					<%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("racc_receivedate").substring(0, 10), BmFieldType.DATE) %>
					<%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("racc_duedate"), BmFieldType.DATE) %>
					
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("rqpt_name")), BmFieldType.STRING) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + taxRacc, BmFieldType.CURRENCY) %>
					
					<%
					
					// Regresar fechas origen para reutilizarlas en cada cxc, ya que sufriran cambios
					monthEnd = monthEndFinal;
					monthStart = monthEndStart;

					int countYear = 0,
						countMonth = 0;
					boolean moreYears2 = false;
					// Sacar meses de los años seleccionados
					for (int anio = yearStart; anio <= yearEnd; anio++) {
						// Si tiene mas de 1 anio, sacar los meses siguientes hasta el mes del anio fin
						// ej: de febrero de 2017 hasta el febrero de 2019
						// entonces 2018 va a sacar todos los meses de ese anio 
						// y cuando llegue a 2019 lo dejara hasta el mes seleccionado
						if (anio <= yearEnd) {
							if (moreYears2) {
								monthStart = 1;
								monthEnd = 12;
							}
							else {
								moreYears2 = true;
								monthEnd = 12;
							}
						}
						
						// Si es el mismo anio, dejar como estaba el mes fin seleccionado
						if (anio == yearEnd) {
							monthEnd = monthEndFinal;
						}
						
//	 					System.out.println("*** "+r);
//	 					System.out.println("countYear: "+countYear);
		 				for (int month = monthStart; month <= monthEnd; month++) {
//	 	 					System.out.println("countMonth: "+countMonth);
		 					
		 					// Colocar monto de la cxc en la fecha de pago que corresponde
		 					if ((month == monthRaccDueDate) && (anio == yearRaccDueDate)) {
		 						if (pmRaccounts.getString("racc_paymentstatus").equals("" +  BmoRaccount.PAYMENTSTATUS_PENDING)) {
		 							// Anadir el saldo a la matriz
		 							// Si es de tipo Nota de Crédito no sumar las cantidades a los totales
									if(!(pmRaccounts.getString("ract_type").equals(""+BmoRaccountType.TYPE_DEPOSIT) && pmRaccounts.getString("ract_category").equals(""+BmoRaccountType.CATEGORY_CREDITNOTE))){
										sumTotal[r][countMonth] = balanceRacc;
									} else sumTotal[r][countMonth] = 0; %>
		 							<%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
		 						<%
								} else  {
									// Anadir el monto a la matriz
									// Si es de tipo Nota de Crédito no sumar las cantidades a los totales
									if(!(pmRaccounts.getString("ract_type").equals(""+BmoRaccountType.TYPE_DEPOSIT) && pmRaccounts.getString("ract_category").equals(""+BmoRaccountType.CATEGORY_CREDITNOTE))){
										sumTotal[r][countMonth] = amountRacc;
									} else sumTotal[r][countMonth] = 0; %>
									<%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
								<%
								}
		 					} else {
		 						// Anadir cero a la matriz para las celdas vacias
			 					sumTotal[r][countMonth] = 0.0;
		 						%>
		 						<td class="reportCellText" style="text-align: right">-</td>
								<%
		 					}
		 					countMonth++;
		 				}
		 				countYear++;
					}
					%>
					</tr>
				<%
					rInOrder++;
					r++;
		       	} // Fin while pmRaccounts

		       	c++;
		    } // Fin while pmCustomer
		    %>
		    <tr><td colspan="<%= dynamicColspan + countMonthsColumn%>">&nbsp;</td></tr>
		    <tr class="reportCellEven reportCellCode">
		    <td colspan="<%= dynamicColspan -1%>" class="reportCellText">&nbsp;</td>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + taxT, BmFieldType.CURRENCY) %>
		    
		    	<% 
		    	double sumByMonth = 0;
		    	int rows = sumTotal.length;
		    	// Si no hay cxc, no mostrar resultados
		    	if (rows > 0) {
		   		 	int columns = sumTotal[0].length;
	
		   		 	// Recorrer columnas para sumatoria de estas
		   		 	for (int j = 0; j < columns; j++) {
		   				sumByMonth = 0;
		       	        //Recorrer las filas
		        	 	for (int i = 0; i < rows; i++) {
		       		 		sumByMonth += sumTotal[i][j];
		       		 	}
		       	        %>
		       	        <%= HtmlUtil.formatReportCell(sFParams, "" + sumByMonth, BmFieldType.CURRENCY) %>
		       	        <%
		        	 }
		    	}
				%>
		    </tr>
		    <tr><td colspan="<%= dynamicColspan + countMonthsColumn%>">&nbsp;</td></tr>
		    <%
		} // Fin pmCurrencyWhile
	}
	// Si existe moneda destino
	else {	
		%>

		<tr class="">
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCustomerId())) { dynamicColspan++; %>
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getCustomerId()))%></th>
			<%	} %>	
			<%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { dynamicColspan++; %>
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getCustomercategory()))%></th>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getOrderId())) { dynamicColspan++; %>
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getOrderId()))%></th>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCode())) { dynamicColspan++; %>	
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getCode()))%></th>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCustomerId())) { dynamicColspan++; %>	
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getInvoiceno()))%></th>
			<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoRaccount.getReceiveDate())) { dynamicColspan++; %>		
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getReceiveDate()))%></th>	
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getInvoiceno())) { dynamicColspan++; %>		
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getDueDate()))%></th>	
			<%	} %>
		
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getReqPayTypeId())) { dynamicColspan++; %>
				<th class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getReqPayTypeId()))%></th>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getTax())) { dynamicColspan++; %>	
				<th class="reportHeaderCellRight"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getTax()))%></th>	
			<% }
			// Se guarda el mes fin  porque se va a modificar adelante 
			monthEndFinal = monthEnd;
			monthEndStart = monthStart;
			countRaccsFile = 0; countMonthsColumn = 0;
			moreYears = false;
			System.out.println("monthEndStart:"+monthEndStart + "|monthEndFinal:"+monthEndFinal);
			System.out.println("yearStart:"+yearStart + "|yearEnd:"+yearEnd);
			// Sacar meses de los años seleccionados
			for (int anio = yearStart; anio <= yearEnd; anio++) {
				// Si tiene mas de 1 anio, sacar los meses siguientes hasta el mes del anio fin
				// ej: de febrero de 2017 hasta febrero de 2019
				// entonces 2018 va a sacar todos los meses de ese anio 
				// y cuando llegue a 2019 lo dejara hasta el mes seleccionado
				if (anio < yearEnd) {
					if (moreYears) {
						monthStart = 1;
						monthEnd = 12;
					}
					else {
						moreYears = true;
						monthEnd = 12;
					}
				}
				// Si es el mismo anio, dejar como estaba el mes fin seleccionado
				if (anio == yearEnd) {
					monthEnd = monthEndFinal;
				}

 				for (int month = monthStart; month <= monthEnd; month++) {
					%>
					<th class="reportHeaderCell">
						<%= FlexUtil.getMonthName(sFParams, anio + "-" + month + "-01").substring(0, 3) %>(<%= anio %>)
					</th>
					
					<%
					countMonthsColumn++;
 				}
			}
			// Regresar fechas origen
			monthEnd = monthEndFinal;
			monthStart = monthEndStart;
			%>
		</tr>

		<%
		// Contar todas la cxc para armar la matriz y para rowspan del cliente
		sql = " SELECT COUNT(racc_customerid) AS countAllRacc  " +
		    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
		    		if (enableBudgets) {
		    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
		    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
		    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
		    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
		    	    }
		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
	    			" WHERE racc_raccountid > 0 " +
// 	    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
	    			whereOrderId +
	    			where +
	    			whereReceiveDate +
	    			whereFailure +
	    			whereLinked +
					" ORDER BY racc_code ASC";

	    			pmConn.doFetch(sql);
	    			if (pmConn.next()) countRaccsFile = pmConn.getInt("countAllRacc");
		
	    // Traer los clientes de las CXC
		sql = " SELECT DISTINCT(racc_customerid) AS cliente,  " +
	    		" cust_customerid, cust_code, cust_legalname, cust_displayname, cust_customertype" +
	    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
	    		if (enableBudgets) {
	    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
	    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
	    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
	    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
	    	    }
		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
    			" WHERE racc_raccountid > 0 " +
//     			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
    			whereOrderId +
    			where +
    			whereReceiveDate +
    			whereFailure +
    			whereLinked +
				" ORDER BY cliente ASC";
    			//System.out.println("sql:: "+sql);
	    pmCustomer.doFetch(sql);
	    
// 	    System.out.println("countRaccsFile: "+countRaccsFile);
// 	    System.out.println("countMonthsColumn: " +countMonthsColumn);

		// Matriz para guardar los montos
	    double[][] sumTotal = new double[(countRaccsFile)][(countMonthsColumn)];
	    
	    // OJO: customerid NO es igual a la variable que se obtiene de los filtros
	   
	    int customerid = 0,  r = 0;
		// Clientes
       	String customer = "";
	    while (pmCustomer.next()) {

	    	if (pmCustomer.getInt("cust_customerid") != customerid) {
	    		customerid = pmCustomer.getInt("cust_customerid");

            	// Cliente
	        	if (pmCustomer.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
   					customer = pmCustomer.getString("cust_code") + " " + pmCustomer.getString("cust_legalname");	
      			else
      				customer = pmCustomer.getString("cust_code") + " " + pmCustomer.getString("cust_displayname");

	        	int countRaccsByCustomerFile = 0;
	        	// Contar los clientes de las CXC para rowspan
				sql = " SELECT COUNT(racc_raccountid) AS countRaccsByCustomer " +
			    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
			    		if (enableBudgets) {
			    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
			    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
			    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
			    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
			    	    }
				sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
		    			" WHERE racc_customerid > 0 " +
// 		    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
		    			" AND racc_customerid = " + customerid +
		    			whereOrderId +
		    			where +
		    			whereReceiveDate +
		    			whereFailure +
		    			whereLinked +
						" ORDER BY racc_raccountid ASC";
		    			
			    pmConn.doFetch(sql);
            	if (pmConn.next()) countRaccsByCustomerFile = pmConn.getInt("countRaccsByCustomer");
            	
			   	%>
			   	<tr class="reportCellEven">
	       			<td class="reportCellText" rowspan="<%= countRaccsByCustomerFile%>">
	       				<%= HtmlUtil.stringToHtml(customer)%>
	       			</td>
	       			<%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) {%>
	       			<td class="reportCellText" rowspan="<%= countRaccsByCustomerFile%>">
	       				<%= HtmlUtil.stringToHtml(bmoCustomer.getCustomercategory().getSelectedOption().getLabel())%>
	       			</td>
	       			<%	}%>	
			   	<%
			}

	    	// CXC del cliente
	    	sql = " SELECT racc_raccountid, ract_type, ract_category, racc_code, racc_invoiceno, racc_description, racc_currencyid, racc_currencyparity, " +
	    			" racc_balance, racc_paymentstatus, racc_amount, racc_tax, racc_duedate, racc_orderid, racc_autocreate, orde_code, orde_name, rqpt_name, racc_receivedate " +
		    		" FROM " + SQLUtil.formatKind(sFParams, "raccounts");
		    		if (enableBudgets) {
		    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
		    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
		    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
		    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
		    	    }
    		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "reqpaytypes")+" ON (racc_reqpaytypeid = rqpt_reqpaytypeid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
	    			" WHERE racc_raccountid > 0 " +
	    			" AND racc_customerid = " + customerid +
// 	    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
	    			whereOrderId +
	    			where +
	    			whereReceiveDate +
	    			whereFailure +
	    			whereLinked +
					" ORDER BY racc_orderid ASC, racc_raccountid asc";
	       	pmRaccounts.doFetch(sql);
	       	
		   	int rInOrder = 1, 
	   		    // OJO: orderid NO es igual a la variable que se obtiene de los filtros
	   			orderid = 0;
		   	
		   	// firstInOrder; para controlar(cerrar y abrir correctamente) los etiquetas tr, td
		   	boolean firstInOrder = true;
		   	int rowspanOrder = 0;
	       	while (pmRaccounts.next()) {
	       		// Tomar mes y anio de la Fecha de programacion de la CXC
	       		int monthRaccDueDate = FlexUtil.getMonth(sFParams, pmRaccounts.getString("racc_duedate"));
	       		int yearRaccDueDate = FlexUtil.getYear(sFParams, pmRaccounts.getString("racc_duedate"));
	       		
	       	 	//Conversion a la moneda destino(seleccion del filtro)
				int currencyIdOrigin = 0, currencyIdDestiny = 0;
				double parityOrigin = 0, parityDestiny = 0;
				currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
				parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
				currencyIdDestiny = currencyId;
				parityDestiny = defaultParity;
	       		
				double amountRacc = pmRaccounts.getDouble("racc_amount");
				double taxRacc = pmRaccounts.getDouble("racc_tax");
				double balanceRacc = pmRaccounts.getDouble("racc_balance");

				//Si es de tipo Nota de Crédito no sumar las cantidades a los totales
				if(!(pmRaccounts.getString("ract_type").equals(""+BmoRaccountType.TYPE_DEPOSIT) && pmRaccounts.getString("ract_category").equals(""+BmoRaccountType.CATEGORY_CREDITNOTE))){
					amountRacc = pmCurrencyExchange.currencyExchange(amountRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					taxRacc = pmCurrencyExchange.currencyExchange(taxRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);	       		
					balanceRacc = pmCurrencyExchange.currencyExchange(balanceRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);	       		
				}
				//Si es de tipo Nota de Crédito no sumar las cantidades a los totales
				if(!(pmRaccounts.getString("ract_type").equals(""+BmoRaccountType.TYPE_DEPOSIT) && pmRaccounts.getString("ract_category").equals(""+BmoRaccountType.CATEGORY_CREDITNOTE))){
				// Total de IVA
				taxT += taxRacc;
				}
	       		// Si es el mismo pedido no colocarlo
	       		if (pmRaccounts.getInt("racc_orderid") != orderid) {
	       			orderid = pmRaccounts.getInt("racc_orderid");

	       			// Poner de nuevo en 1, porque aqui el contador lo hace POR PEDIDO
	       			rInOrder = 1;
		       		// Contar las cxc del pedido para Rowspan
			    	sql = " SELECT COUNT(racc_raccountid) AS countRaccByOrder " +
		       				" FROM " + SQLUtil.formatKind(sFParams, "raccounts ");
		       				if (enableBudgets) {
				    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
				    				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
				    	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
				    	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
				    	    }
			    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (racc_currencyid = cure_currencyid) " +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid)" +                              
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid)" +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (orde_userid = user_userid) " +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid)" +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
			    			" WHERE racc_raccountid > 0 " +
// 			    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
			    			" AND racc_orderid = " + orderid +
			    			where +
			    			whereReceiveDate +
			    			whereFailure +
			    			whereLinked +
			    			" ORDER BY racc_raccountid ASC";
				   	pmConn.doFetch(sql);

				   	if (pmConn.next()) rowspanOrder = pmConn.getInt("countRaccByOrder");

				   	if (firstInOrder) firstInOrder = false;
					else { %><tr class="reportCellEven"><% } %>
				   	<td class="reportCellText" rowspan="<%= rowspanOrder%>">
       					<%= pmRaccounts.getString("orders", "orde_code") + " " + HtmlUtil.stringToHtml(pmRaccounts.getString("orders", "orde_name"))%>
       				</td>
       				<%
	       		} else {
	       			// Si la cxc NO es de un pedido
	       			if (!(orderid > 0)) {
		       			// Regresa el contador, porque NO HAY PEDIDO en la CXC
	       				rInOrder = 1;
	       				if (firstInOrder) firstInOrder = false;
						else { %><tr class="reportCellEven"><% } %>
	       		
						<%= HtmlUtil.formatReportCell(sFParams, ""  + pmRaccounts.getString("racc_description"), BmFieldType.STRING) %>
	       		<%
	       			} else {
						// Meter tr si la cxc es de un pedido, 
						// la primera cxc no entra aqui, SOLO es para las demas
	       				%>
       						<tr class="reportCellEven"> 
       					<%
	       			}
	       		}

				%>
				<%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("racc_code"), BmFieldType.CODE) %>
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("racc_invoiceno")), BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("racc_receivedate").substring(0, 10), BmFieldType.DATE) %>
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("racc_duedate")), BmFieldType.DATE) %>				
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("rqpt_name")), BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + taxRacc, BmFieldType.CURRENCY) %>
				<%
				// Regresar fechas origen para reutilizarlas en cada cxc, ya que sufriran cambios
				monthEnd = monthEndFinal;
				monthStart = monthEndStart;

				int countYear = 0,
					countMonth = 0;
				boolean moreYears2 = false;
				// Sacar meses de los años seleccionados
				for (int anio = yearStart; anio <= yearEnd; anio++) {
					// Si tiene mas de 1 anio, sacar los meses siguientes hasta el mes del anio fin
					// ej: de febrero de 2017 hasta el febrero de 2019
					// entonces 2018 va a sacar todos los meses de ese anio 
					// y cuando llegue a 2019 lo dejara hasta el mes seleccionado
					if (anio < yearEnd) {
						if (moreYears2) {
							monthStart = 1;
							monthEnd = 12;
						}
						else {
							moreYears2 = true;
							monthEnd = 12;
						}
					}
					
					// Si es el mismo anio, dejar como estaba el mes fin seleccionado
					if (anio == yearEnd) {
						monthEnd = monthEndFinal;
					}
					
// 					System.out.println("*** "+r);
// 					System.out.println("countYear: "+countYear);
	 				for (int month = monthStart; month <= monthEnd; month++) {
// 	 					System.out.println("countMonth: "+countMonth);
	 					
	 					// Colocar monto de la cxc en la fecha de pago que corresponde
	 					if ((month == monthRaccDueDate) && (anio == yearRaccDueDate)) {
	 						if (pmRaccounts.getString("racc_paymentstatus").equals("" +  BmoRaccount.PAYMENTSTATUS_PENDING)) {
	 							// Anadir el saldo a la matriz
	 							// Si es de tipo Nota de Crédito no sumar las cantidades a los totales
								if(!(pmRaccounts.getString("ract_type").equals(""+BmoRaccountType.TYPE_DEPOSIT) && pmRaccounts.getString("ract_category").equals(""+BmoRaccountType.CATEGORY_CREDITNOTE))){
									sumTotal[r][countMonth] = balanceRacc;
								} else sumTotal[r][countMonth] = 0; %>
	 							<%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
	 						<%
							} else  {
								// Anadir el monto a la matriz
								// Si es de tipo Nota de Crédito no sumar las cantidades a los totales
								if(!(pmRaccounts.getString("ract_type").equals(""+BmoRaccountType.TYPE_DEPOSIT) && pmRaccounts.getString("ract_category").equals(""+BmoRaccountType.CATEGORY_CREDITNOTE))){
									sumTotal[r][countMonth] = amountRacc;
								} else sumTotal[r][countMonth] = 0; %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
							<%
							}
	 					} else {
	 						// Anadir cero a la matriz para las celdas vacias
		 					sumTotal[r][countMonth] = 0.0;
	 						%>
	 						<td class="reportCellText" style="text-align: right">-</td>
							<%
	 					}
	 					//System.out.println("---");
	        	 		//System.out.println("matrizPrin: ["+r+"]["+countMonth+"]" + " = "+sumTotal[r][countMonth]);

	 					countMonth++;
	 				}
	 				countYear++;
				}
				%>
				</tr>
			<%
				rInOrder++;
				r++;
	       	} // Fin while pmRaccounts

	       	c++;
	    } // Fin while pmCustomer
	    %>
	    <tr><td colspan="<%= dynamicColspan + countMonthsColumn%>">&nbsp;</td></tr>
	    <tr class="reportCellEven reportCellCode">
	   		<td colspan="<%= dynamicColspan - 1%>" class="reportCellText">&nbsp;</td>
	   		
	   		<%= HtmlUtil.formatReportCell(sFParams, "" + taxT, BmFieldType.CURRENCY) %>
	    	<% 
	    	double sumByMonth = 0;
	    	int rows = sumTotal.length;
	    	// Si no hay cxc, no mostrar resultados
	    	if (rows > 0) {
   		 		int columns = sumTotal[0].length;
	   			//System.out.println("filas: "+rows);
		   		//System.out.println("columnas: "+columns);	   		
		   		
		   		// Recorrer columnas para sumatoria de estas
	   		 	for (int j = 0; j < columns; j++) {
	   		 		//System.out.println("---------columna---------: "+j);
	   				sumByMonth = 0;
	       	        //Recorrer las filas
	        	 	for (int i = 0; i < rows; i++) {
	        	 		//System.out.println("matriz: ["+i+"]["+j+"]" + " = "+sumTotal[i][j]);
	       		 		sumByMonth = sumByMonth + sumTotal[i][j];
	       		 	}
	       	        %>
	       	        <%= HtmlUtil.formatReportCell(sFParams, "" + sumByMonth, BmFieldType.CURRENCY) %>
	       	        <%
        		}
			}
			%>
	    </tr>
	    <%
	}
	%>

	</TABLE>
	<%
	}// FIN DEL CONTADOR
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmOrders.close();
	pmRaccounts.close();
	pmCustomer.close();
	pmConn.close();
	pmConnCount.close();
%>
	<% if (print.equals("1")) { %>
	<script>
			//window.print();
		</script>
	<% }
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	%>
</body>
</html>