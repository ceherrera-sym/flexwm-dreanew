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
 	String title = "Reporte de Antiguedad de Saldos de CxC";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	BmoRaccount bmoRaccount = new BmoRaccount();
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
    BmoCustomer bmoCustomer = new BmoCustomer();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
   	String sql = "", where = "", whereCust = "", whereFailure = "", whereLinked = "", sqlCurrency = "", sqlOrder = "";
   	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "", customer = "";
   	String status = "", paymentStatus = "", paymentStatus2 = "", filtersDefault = "", customerCategory ="";
   	String filters = "";
   	int programId = 0, customerid = 0, orderId = 0, cols= 0, raccountTypeId = 0, companyId = 0, wflowCategoryId = 0,userId = 0, 
   			collectorUserId = 0, currencyId = 0, failure = -1, activeBudgets = 0, linked = 0;
   	boolean t = false, enableBudgets = false;
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
   	if (request.getParameter("racc_customerid") != null) customerid = Integer.parseInt(request.getParameter("racc_customerid"));
   	if (request.getParameter("racc_companyid") != null) companyId = Integer.parseInt(request.getParameter("racc_companyid"));
   	if (request.getParameter("racc_orderid") != null) orderId = Integer.parseInt(request.getParameter("racc_orderid"));
//   	if (request.getParameter("racc_raccounttypeid") != null) raccountTypeId = Integer.parseInt(request.getParameter("racc_raccounttypeid"));
  	if (request.getParameter("racc_status") != null) status = request.getParameter("racc_status");
  	if (request.getParameter("racc_paymentstatus") != null) paymentStatus = request.getParameter("racc_paymentstatus");
   	if (request.getParameter("racc_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("racc_currencyid"));
   	if (request.getParameter("wfca_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfca_wflowcategoryid"));   	   	
//   	if (request.getParameter("racc_receivedate") != null) receiveDate = request.getParameter("racc_receivedate");
   	//if (request.getParameter("racc_duedate") != null) dueDate = request.getParameter("racc_duedate");
//   	if (request.getParameter("receiveenddate") != null) receiveEndDate = request.getParameter("receiveenddate");    
   	// if (request.getParameter("dueenddate") != null) dueEndDate = request.getParameter("dueenddate");
   	if (sFParams.isFieldEnabled(bmoRaccount.getFailure())) {
    	if (request.getParameter("racc_failure") != null) failure = Integer.parseInt(request.getParameter("racc_failure"));
   	}
   	if (sFParams.isFieldEnabled(bmoRaccount.getLinked())) {
    	if (request.getParameter("racc_linked") != null) linked = Integer.parseInt(request.getParameter("racc_linked"));
	}
    if (request.getParameter("userid") != null) userId = Integer.parseInt(request.getParameter("userid"));
    if (request.getParameter("racc_collectuserid") != null) collectorUserId = Integer.parseInt(request.getParameter("racc_collectuserid"));

    
//     if (request.getParameter("racc_duedate") != null) {
//    		if(request.getParameter("racc_duedate").equals("")) {
//    			dueDate = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
//    			t = true;
//    		}else 
//    			dueDate = request.getParameter("racc_duedate");
//    	} else {
   		dueDate = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
//    		t = true;
//    	}

   if (request.getParameter("cust_customercategory") != null) customerCategory = request.getParameter("cust_customercategory");
	// Filtros listados	
	if (!customerCategory.equals("")) {
        where += SFServerUtil.parseFiltersToSql("cust_customercategory", customerCategory);
        filters += "<i>Categoría Cliente: </i>" + request.getParameter("cust_customercategoryLabel") + ", ";
    }
	if (customerid > 0) {
		whereCust += " AND racc_customerid = " + customerid;
		filters += "<i>Cliente: </i>" + request.getParameter("racc_customeridLabel") + ", ";
	}
	
	if (orderId > 0) {
        where += " AND racc_orderid = " + orderId;
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

//	if (raccountTypeId > 0) {
//        where += " AND racc_raccounttypeid = " + raccountTypeId;
//        filters += "<i>Tipo: </i>" + request.getParameter("racc_raccounttypeidLabel") + ", ";
//    }
	
	// Tipo de cxc de tipo cargo
    where += " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' ";
    filtersDefault += "<i>Tipo CxC: </i> Cargo, ";
	
//   	if (!receiveDate.equals("")) {
//   		where += " AND racc_receivedate >= '" + receiveDate + "'";
//   		filters += "<i>Recepci&oacute;n Inicio: </i>" + receiveDate + ", ";
//   	}
//   	
//    if (!receiveEndDate.equals("")) {
//        where += " AND racc_receivedate <= '" + receiveEndDate + "'";
//        filters += "<i>Recepci&oacute;n Final: </i>" + receiveEndDate + ", ";
//    }
    
	if (!status.equals("")) {   		
   		where += SFServerUtil.parseFiltersToSql("racc_status", status);
		filters += "<i>Estatus: </i>" + request.getParameter("racc_statusLabel") + ", ";   	
	    //where += " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "' ";
	    //filtersDefault += "<i>Estatus: </i> Autorizada, ";
	}
	
	if (!paymentStatus.equals("")) {
	   where += SFServerUtil.parseFiltersToSql("racc_paymentstatus", paymentStatus);
	   filters += "<i>Estatus Pago: </i>" + request.getParameter("racc_paymentstatusLabel") + ", ";
    
	    //where += " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_PENDING + "' ";
    //where += SFServerUtil.parseFiltersToSql("pacc_paymentstatus", paymentStatus);
    //filtersDefault += "<i>Estatus Pago: </i> Pendiente, ";
}
   	   	
	// NO MOSTRAR ESTO: Fechas Prog.
   	//if (!dueDate.equals("")) {
//        where += " AND racc_duedate >= '" + dueDate + "'";
//        filters += "<i>Programaci&oacute;n Inicio: </i>" + dueDate + ", ";
   		
    	//if(t) filtersDefault += "<i>F. Programaci&oacute;n Inicio: </i>" + dueDate + ", ";
    	//else filters += "<i>F. Programaci&oacute;n Inicio: </i>" + dueDate + ", ";
    //}
    
//   	if (!dueEndDate.equals("")) {
//        where += " AND racc_duedate <= '" + dueEndDate + "'";
//        filters += "<i>Programaci&oacute;n Final: </i>" + dueEndDate + ", ";
//    }
   	
   	
   	if (failure > 0) {
   		whereFailure += " AND racc_failure = " + failure;
   		filters += "<i>CxC Falla </i>" + ", ";
   	}
   	
    //Obtener la paridad de la moneda
    double defaultParity = 0;
   	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
   	if (currencyId > 0) {
   		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);   		
   		defaultParity = bmoCurrency.getParity().toDouble();   		
   		filters += "<i>Moneda: </i>" + request.getParameter("racc_currencyidLabel") + " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
   	} else {
   		//Obtener la paridad de la moneda del sistema
   		//bmoCurrency = (BmoCurrency)pmCurrency.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getCurrencyId().toInteger());
   		//defaultParity = bmoCurrency.getParity().toDouble();
   		filters += "<i>Moneda: </i> Todas";
   		sqlCurrency = "SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, "currencies");
   	}
   	
   	/*
   	if (!dueEndDate.equals("")) {
        where += " AND racc_duedate <= '" + dueEndDate + "'";
        filters += "<i>Programaci&oacute;n Final: </i>" + dueEndDate + ", ";
    }
    */
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmRaccount(sFParams).getDisclosureFilters();   
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;  
   	
   	PmConn pmConn = new PmConn(sFParams);
   	pmConn.open();
   	
   	PmConn pmCustomers = new PmConn(sFParams);
   	pmCustomers.open();
   	
   	PmConn pmRaccounts = new PmConn(sFParams);
   	pmRaccounts.open();
   	
   	PmConn pmCurrencyWhile =new PmConn(sFParams);
    pmCurrencyWhile.open();

   	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
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
		<td class="reportTitle" colspan="2">
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
<p>
<table class="report" border="0">	
    <%

    sql = " SELECT COUNT(*) as contador FROM ( " +
	    					" SELECT DISTINCT(racc_customerid), cust_customerid, cust_code, cust_legalname, cust_displayname, cust_customertype, cust_customercategory, ortp_type " +
							" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid) "+
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +   	      
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
							" WHERE racc_raccountid > 0 " + 
							whereCust +
							where + 
							whereFailure +
							whereLinked +
							"	ORDER BY cust_customerid ASC " +
						" ) AS t;";

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
		
        int currencyIdWhile = 0, y = 0;
        String orderType = "";
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			
			double 	sumDV030 = 0, sumDV3160 = 0, sumDV6190 = 0, sumDV91 = 0, sumTotal = 0, 
	        		sumDPV130 = 0, sumDPV3160 = 0, sumDPV6190 = 0, sumDPV91 = 0;
	        int i = 0;
	        
	        sqlOrder = 
//	         		" SELECT * FROM " + SQLUtil.formatKind(sFParams, "customers") +
//	 				" WHERE cust_customerid IN ( " +
						" SELECT DISTINCT(racc_customerid), cust_customerid, cust_code, cust_legalname, cust_displayname, cust_customertype, cust_customercategory, ortp_type " +
							" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
		        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
		        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid) "+
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +   	      
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
							" WHERE racc_raccountid > 0 " + 
							whereCust +
							where + 
							whereFailure +
							whereLinked +
							" AND racc_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
//	 					"	) " +
						"	ORDER BY cust_customerid ASC ";
					//System.out.println("cust: "+sqlOrder);
	        
	        pmCustomers.doFetch(sqlOrder);
	        while (pmCustomers.next()) {
	        	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory()))					   
	        		bmoCustomer.getCustomercategory().setValue(pmCustomers.getString("cust_customercategory"));
	        	if (pmCustomers.getString("cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY))
		    		customer = pmCustomers.getString("cust_code") + " " + pmCustomers.getString("cust_legalname");
	   			else
	   				customer = pmCustomers.getString("cust_code") + " " + pmCustomers.getString("cust_displayname");
	        	
	        	orderType = "" + pmCustomers.getString("ortp_type");
	        	
	        	if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
	        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
	        		y = 0;
	        		%>
	        		<tr>
	            		<td class="reportHeaderCellCenter" colspan="14">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
	        		</tr>
	        		<tr>                    
		        		<td class="reportHeaderCellCenter" rowspan="2">#</td>
		        		<%dynamicColspan++;%>
		        		<%	if (sFParams.isFieldEnabled(bmoRaccount.getCurrencyId())) {dynamicColspan++;%>
		        		<td class="reportHeaderCell" rowspan="2">Cliente</td>
		        		<%} %>
		        		<%if(sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { dynamicColspan++;%>
		        		<td class="reportHeaderCell" rowspan="2">Categoría Cliente</td>
		        		<%} %>
		        		<td rowspan="2">&nbsp;</td>
		        		<td class="reportHeaderCellCenter" colspan="4">C x C vencidas</td>
		        		
		        		<td rowspan="2">&nbsp;</td>
		        		<td class="reportHeaderCellCenter" colspan="4">Fecha Programada de Pago</td>
		        		
		        		<td rowspan="2">&nbsp;</td>
		        		<td class="reportHeaderCellRight" rowspan="2" >Total&nbsp;</td>                                                                           
		        	</tr>
		        	<tr>                    
		        		<td class="reportHeaderCellRight">0-30 D&iacute;as<br>Vencido</td>
		        		<td class="reportHeaderCellRight">31-60 D&iacute;as<br>Vencido</td>
		        		<td class="reportHeaderCellRight">61-90 D&iacute;as<br>Vencido</td>
		        		<td class="reportHeaderCellRight">+91 D&iacute;as<br>Vencido</td>
						
		        		<td class="reportHeaderCellRight">1-30 D&iacute;as Para<br>Vencimiento</td>                                                                           
		        		<td class="reportHeaderCellRight">31-60 D&iacute;as Para<br>Vencimiento</td>                                                                           
		        		<td class="reportHeaderCellRight">61-90 D&iacute;as Para<br>Vencimiento</td>                                                                           
		        		<td class="reportHeaderCellRight">+91 D&iacute;as Para<br>Vencimiento</td>                                                                           
	 					
		        	</tr>
	        		<%
	        	}
	        	
	        	double total = 0;
	        	// totales por cliente
	        	double 	DV030 = 0, DV3160 = 0, DV6190 = 0, DV91 = 0, 
	            		DPV130 = 0, DPV3160 = 0, DPV6190 = 0, DPV91 = 0,
	        	// suma totales por cliente por linea
	            		DV030T = 0, DV3160T = 0, DV6190T = 0, DV91T = 0,
	            		DPV130T = 0, DPV3160T = 0, DPV6190T = 0, DPV91T = 0;
	    		
	    		// 0-30 Dias Vencido
	    		sql = " SELECT SUM(racc_balance) AS DV030 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
						" WHERE racc_balance > 0 " + 
						" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
						" AND racc_currencyid = " + currencyIdWhile +
	    				where + 
	    				whereFailure +
	    				whereLinked;
						if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
	    					sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -30 DAY) " + 
		    						" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 0 DAY) " +
		    						" ORDER BY racc_duedate ASC";
	    				} else {
	    					sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL -30 DAY) " + 
	    		    				" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL 0 DAY) " +
	    		    				" ORDER BY racc_duedatestart ASC";
	    				}
	    		pmRaccounts.doFetch(sql);
	    		if (pmRaccounts.next()) {
	    			DV030 = pmRaccounts.getDouble("DV030");
	    		}
	    		
	    		sumDV030 += DV030;
	    		DV030T += DV030;
	    		total += DV030;
	    		
	    		// 31-60 Dias Vencido
	    		sql = " SELECT SUM(racc_balance) AS DV3160 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
						" WHERE racc_balance > 0 " + 
						" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
						" AND racc_currencyid = " + currencyIdWhile +
	    				where + 
	    				whereFailure +
	    				whereLinked;
						if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
	    					sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -60 DAY) " +
	    							" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL -31 DAY) " +
	    							" ORDER BY racc_duedate ASC";
	    				} else {
	    					sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL -60 DAY) " +
	    		    				" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL -31 DAY) " +
	    		    				" ORDER BY racc_duedatestart ASC";
	    				}
						
	    		pmRaccounts.doFetch(sql);
	    		if (pmRaccounts.next()) { 
	    			DV3160 = pmRaccounts.getDouble("DV3160");
	    		}
	    		
	    		sumDV3160 += DV3160;
	    		DV3160T += DV3160;
	    		total += DV3160;
	    		
	    		// 61-90 Dias Vencido
	    		sql = " SELECT SUM(racc_balance) AS DV6190 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
						" WHERE racc_balance > 0 " + 
						" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
						" AND racc_currencyid = " + currencyIdWhile +
	    				where + 
	    				whereFailure +
	    				whereLinked;
						if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
							sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -90 DAY) " +
	    							" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL -61 DAY) " +
	    							" ORDER BY racc_duedate ASC";
						} else {
							sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL -90 DAY) " +
	    							" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL -61 DAY) " +
	    							" ORDER BY racc_duedatestart ASC";
						}
						
	    		pmRaccounts.doFetch(sql);
	    		if (pmRaccounts.next()) { 
	    			DV6190 = pmRaccounts.getDouble("DV6190");
	    		}
	    		
	    		sumDV6190 += DV6190;
	    		DV6190T += DV6190;
	    		total += DV6190;
	    		
	    		// +91 Dias Vencido
	        	sql = " SELECT SUM(racc_balance) AS DV91 " +
	        			" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
						" WHERE racc_balance > 0 " + 
						" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
						" AND racc_currencyid = " + currencyIdWhile +
	        			where + 
	        			whereFailure +
	        			whereLinked;
						if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
							sql += " AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL -91 DAY) " +
									" ORDER BY racc_duedate ASC";
						} else {
							sql += " AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL -91 DAY) " +
									" ORDER BY racc_duedatestart ASC";
						}

	        	//System.out.println("DV91: "+sql);
	        	
	        	pmRaccounts.doFetch(sql);
	    		if (pmRaccounts.next()) {             	
	    			DV91 = pmRaccounts.getDouble("DV91");
	            }
	    		
	    		sumDV91 += DV91;
	    		DV91T += DV91;
	    		total += DV91;

	    		
	    		// 1-30 Dias Para Vencimiento
	    		sql = " SELECT SUM(racc_balance) AS DPV130 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
						" WHERE racc_balance > 0 " + 
						" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
						" AND racc_currencyid = " + currencyIdWhile +
	    				where + 
	    				whereFailure +
	    				whereLinked;
						if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
							sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 1 DAY) " +
	    							" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 30 DAY) " +
	    							" ORDER BY racc_duedate ASC";
						} else {
							sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL 1 DAY) " +
	    							" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL 30 DAY) " +
	    							" ORDER BY racc_duedatestart ASC";
						}
						
	    		pmRaccounts.doFetch(sql);
	    		if (pmRaccounts.next()) { 
	    			DPV130 = pmRaccounts.getDouble("DPV130");
	    		}
	    		
	    		sumDPV130 += DPV130;
	    		DPV130T += DPV130;
	    		total += DPV130;

	    		// 31-60 Dias Para Vencimiento
	    		sql = " SELECT SUM(racc_balance) AS DPV3160 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
						" WHERE racc_balance > 0 " + 
						" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
						" AND racc_currencyid = " + currencyIdWhile +
	    				where + 
	    				whereFailure +
	    				whereLinked;
						if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
	    					sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 31 DAY) " +
	    							" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 60 DAY) " +
	    							" ORDER BY racc_duedate ASC";
	    				} else {
	   						sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL 31 DAY) " +
	    							" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL 60 DAY) " +
	    							" ORDER BY racc_duedatestart ASC";
	   					}
						
	    		pmRaccounts.doFetch(sql);
	    		if (pmRaccounts.next()) {
	    			DPV3160 = pmRaccounts.getDouble("DPV3160");
	    		}
	    		
	    		sumDPV3160 += DPV3160;
	    		DPV3160T += DPV3160;
	    		total += DPV3160;
	    		
	    		// 61-90 Dias Para Vencimiento
	    		sql = " SELECT SUM(racc_balance) AS DPV6190 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
						" WHERE racc_balance > 0 " + 
						" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
						" AND racc_currencyid = " + currencyIdWhile +
	    				where + 
	    				whereFailure +
	    				whereLinked;
						if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
							sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 61 DAY) " +
		    						" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 90 DAY) " +
		    						" ORDER BY racc_duedate ASC";
						} else {
							sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL 61 DAY) " +
		    						" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL 90 DAY) " +
		    						" ORDER BY racc_duedatestart ASC";
						}
	    		pmRaccounts.doFetch(sql);
	    		if (pmRaccounts.next()) {
	    			DPV6190 = pmRaccounts.getDouble("DPV6190");
	    		}
	    		
	    		sumDPV6190 += DPV6190;
	    		DPV6190T += DPV6190;
	    		total += DPV6190;
	    		
	    		// +91 Dias Para Vencimiento
	    		sql = " SELECT SUM(racc_balance) AS DPV91 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
						" WHERE racc_balance > 0 " + 
						" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
						" AND racc_currencyid = " + currencyIdWhile +
	    				where + 
	    				whereFailure +
	    				whereLinked;
						if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
							sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 91 DAY) " +
									" ORDER BY racc_duedate ASC";
						} else {
							sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL 91 DAY) " +
									" ORDER BY racc_duedatestart ASC";
						}
	    		pmRaccounts.doFetch(sql);
	    		if (pmRaccounts.next()) {
	    			DPV91 = pmRaccounts.getDouble("DPV91");
	    		}

	    		sumDPV91 += DPV91;
	    		DPV91T = DPV91;
	    		total += DPV91;
	            
	    		
	        	sumTotal += total;
	           	i++;
	        %>
	            <tr class="reportCellEven">	
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
	           		<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>	
	           		<%	if (sFParams.isFieldEnabled(bmoRaccount.getBmoCustomer().getCustomercategory())) { %>
				    <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoCustomer.getCustomercategory().getSelectedOption().getLabel()), BmFieldType.STRING) %>
					<%	} %>
	           		<td style="border-style: hidden">&nbsp;</td>
	           		<%= HtmlUtil.formatReportCell(sFParams, "" + DV030T , BmFieldType.CURRENCY) %>
	           		<%= HtmlUtil.formatReportCell(sFParams, "" + DV3160T, BmFieldType.CURRENCY) %>
	           		<%= HtmlUtil.formatReportCell(sFParams, "" + DV6190T, BmFieldType.CURRENCY) %>
	    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DV91T, BmFieldType.CURRENCY) %>
	    	    	
	    	    	<td style="border-style: hidden">&nbsp;</td>
	    	    	 
	    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DPV130T, BmFieldType.CURRENCY) %>
	    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DPV3160T, BmFieldType.CURRENCY) %>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + DPV6190T, BmFieldType.CURRENCY) %>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + DPV91T, BmFieldType.CURRENCY) %>	
	    			<td style="border-style: hidden">&nbsp;</td>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
	    		</tr>
	    		
	        <%    
	    	}   //End while pmCustomers
	        if (sumDV91 > 0 || sumDV6190 > 0 || sumDV3160 > 0 || sumDV030 > 0 
	        		|| sumDPV130 > 0 || sumDPV3160 > 0 || sumDPV6190 > 0 || sumDPV91 > 0 
	        		|| sumTotal > 0) {
	        %>
		        <tr><td colspan="14">&nbsp;</td></tr>
		    	<tr class="reportCellEven reportCellCode">
		    		<td class="reportCellText" colspan="2">&nbsp;</td>
		    		<td style="border-style: hidden">&nbsp;</td>
		    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV030, BmFieldType.CURRENCY) %>
		    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV3160, BmFieldType.CURRENCY) %>
		    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV6190, BmFieldType.CURRENCY) %>
		    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV91, BmFieldType.CURRENCY) %>
		    		
		    		<td style="border-style: hidden">&nbsp;</td>
		    		
		    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV130, BmFieldType.CURRENCY) %>
		    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV3160, BmFieldType.CURRENCY) %>
		    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV6190, BmFieldType.CURRENCY) %>
		    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV91, BmFieldType.CURRENCY) %>
		    		<td style="border-style: hidden">&nbsp;</td>
		    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotal, BmFieldType.CURRENCY) %>
		    	</tr>
		    	<tr><td colspan="15">&nbsp;</td></tr>
        <%	}
		} // fin pmCurrencyWhile
		
    }// Existe moneda destino
    else {
	%>
	<tr>                    
		<td class="reportHeaderCellCenter" rowspan="2">#</td>
		<%dynamicColspan++;%>
		 <%	if (sFParams.isFieldEnabled(bmoRaccount.getCurrencyId())) {dynamicColspan++;%>
		    <td class="reportHeaderCell" rowspan="2">Cliente</td>
		 <%} %>
		<%if(sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) {dynamicColspan++;%>
		<td class="reportHeaderCell" rowspan="2">Categoría Cliente</td>
		<%} %>
		<td rowspan="2">&nbsp;</td>
		<td class="reportHeaderCellCenter" colspan="4">C x C vencidas</td>
		
		<td rowspan="2">&nbsp;</td>
		<td class="reportHeaderCellCenter" colspan="4">Fecha Programada de Pago</td>
		
		<td rowspan="2">&nbsp;</td>
		<td class="reportHeaderCellRight" rowspan="2" >Total&nbsp;</td>                                                                           
	</tr>
	<tr>
		<td class="reportHeaderCellRight">0-30 D&iacute;as<br>Vencido</td>
		<td class="reportHeaderCellRight">31-60 D&iacute;as<br>Vencido</td>
		<td class="reportHeaderCellRight">61-90 D&iacute;as<br>Vencido</td>
		<td class="reportHeaderCellRight">+91 D&iacute;as<br>Vencido</td>
   		 
		
		<td class="reportHeaderCellRight">1-30 D&iacute;as Para<br>Vencimiento</td>                                                                           
		<td class="reportHeaderCellRight">31-60 D&iacute;as Para<br>Vencimiento</td>                                                                           
		<td class="reportHeaderCellRight">61-90 D&iacute;as Para<br>Vencimiento</td>                                                                           
		<td class="reportHeaderCellRight">+91 D&iacute;as Para<br>Vencimiento</td>
		                                                                 
	
	</tr>
	<%
		// suma general de totales por cliente
    	double 	sumDV030 = 0, sumDV3160 = 0, sumDV6190 = 0, sumDV91 = 0, total = 0, sumTotal = 0,
        		sumDPV130 = 0, sumDPV3160 = 0, sumDPV6190 = 0, sumDPV91 = 0;
        int i = 0;
        String orderType = "";
        sqlOrder = 
//         		" SELECT * FROM " + SQLUtil.formatKind(sFParams, "customers") +
// 				" WHERE cust_customerid IN ( " +
						" SELECT DISTINCT(racc_customerid), cust_customerid, cust_code, cust_legalname, cust_displayname, cust_customertype, cust_customercategory, ortp_type " +
						" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (racc_companyid = comp_companyid) "+
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +   	      
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
						" WHERE racc_raccountid > 0 " + 
						whereCust +
						where + 
						whereFailure +
						whereLinked +
// 						" GROUP BY racc_customerid " +
// 					"	) " +
					"	ORDER BY cust_customerid ASC ";
				System.out.println("cust: "+sqlOrder);

        pmCustomers.doFetch(sqlOrder);
        while (pmCustomers.next()) {
        	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory()))					   
        		bmoCustomer.getCustomercategory().setValue(pmCustomers.getString("cust_customercategory"));
        	if (pmCustomers.getString("cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY))
	    		customer = pmCustomers.getString("cust_code") + " " + pmCustomers.getString("cust_legalname");
   			else
   				customer = pmCustomers.getString("cust_code") + " " + pmCustomers.getString("cust_displayname");
        	
        	orderType = "" + pmCustomers.getString("ortp_type");
        	
        	total = 0;
        	// totales por cliente
        	double 	DV030 = 0, DV3160 = 0, DV6190 = 0, DV91 = 0, 
            		DPV130 = 0, DPV3160 = 0, DPV6190 = 0, DPV91 = 0,
        	// suma totales por cliente por linea
            		DV030T = 0, DV3160T = 0, DV6190T = 0, DV91T = 0,
            		DPV130T = 0, DPV3160T = 0, DPV6190T = 0, DPV91T = 0;
            
        	// variables de conversion moneda-paridad
        	int currencyIdOrigin = 0, currencyIdDestiny = 0;
        	double parityOrigin = 0, parityDestiny = 0;
        	currencyIdDestiny = currencyId;
        	parityDestiny = defaultParity;

    		// 0-30 Dias Vencido
    		sql = " SELECT racc_balance AS DV030, racc_currencyid, racc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
					" WHERE racc_balance > 0 " + 
					" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
    				where + 
    				whereFailure +
    				whereLinked;
    				if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
    					sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -30 DAY) " + 
	    						" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 0 DAY) " +
	    						" ORDER BY racc_duedate ASC";
    				} else {
    					sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL -30 DAY) " + 
    		    				" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL 0 DAY) " +
    		    				" ORDER BY racc_duedatestart ASC";
    				}
    		pmRaccounts.doFetch(sql);
    		while (pmRaccounts.next()) {
    			//DV030 = pmRaccounts.getDouble("DV030");
    			currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
        		parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
        		DV030 += pmCurrencyExchange.currencyExchange(pmRaccounts.getDouble("DV030"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDV030 += DV030;
    		DV030T += DV030;
    		total += DV030;
    		
    		// 31-60 Dias Vencido
    		sql = " SELECT racc_balance AS DV3160, racc_currencyid, racc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
					" WHERE racc_balance > 0 " + 
					" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
    				where + 
    				whereFailure +
    				whereLinked;
    				if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
    					sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -60 DAY) " +
    							" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL -31 DAY) " +
    							" ORDER BY racc_duedate ASC";
    				} else {
    					sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL -60 DAY) " +
    		    				" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL -31 DAY) " +
    		    				" ORDER BY racc_duedatestart ASC";
    				}
    		pmRaccounts.doFetch(sql);
    		while (pmRaccounts.next()) { 
    			//DV3160 = pmRaccounts.getDouble("DV3160");
    			currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
        		parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
        		DV3160 += pmCurrencyExchange.currencyExchange(pmRaccounts.getDouble("DV3160"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDV3160 += DV3160;
    		DV3160T += DV3160;
    		total += DV3160;
    		
    		// 61-90 Dias Vencido
    		sql = " SELECT racc_balance AS DV6190, racc_currencyid, racc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
					" WHERE racc_balance > 0 " + 
					" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
    				where + 
    				whereFailure +
    				whereLinked;
					if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
						sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -90 DAY) " +
    							" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL -61 DAY) " +
    							" ORDER BY racc_duedate ASC";
					} else {
						sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL -90 DAY) " +
    							" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL -61 DAY) " +
    							" ORDER BY racc_duedatestart ASC";
					}
    		pmRaccounts.doFetch(sql);
    		while (pmRaccounts.next()) { 
    			//DV6190 = pmRaccounts.getDouble("DV6190");
    			currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
        		parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
        		DV6190 += pmCurrencyExchange.currencyExchange(pmRaccounts.getDouble("DV6190"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDV6190 += DV6190;
    		DV6190T += DV6190;
    		total += DV6190;
    		
    		// +91 Dias Vencido
        	sql = " SELECT racc_balance AS DV91, racc_currencyid, racc_currencyparity " +
        			//" SELECT SUM(racc_balance) AS DV91 " +
        			" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
					" WHERE racc_balance > 0 " + 
					" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
        			where + 
        			whereFailure +
        			whereLinked;
					if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
						sql += " AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL -91 DAY) " +
								" ORDER BY racc_duedate ASC";
					} else {
						sql += " AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL -91 DAY) " +
								" ORDER BY racc_duedatestart ASC";
					}
        	
        	//System.out.println("DV91: "+sql);
        	
        	pmRaccounts.doFetch(sql);
    		while (pmRaccounts.next()) {             	
    			//DV91 = pmRaccounts.getDouble("DV91");
              	currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
        		parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
    			DV91 += pmCurrencyExchange.currencyExchange(pmRaccounts.getDouble("DV91"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDV91 += DV91;
    		DV91T += DV91;
    		total += DV91;
    		
    		
    		// 1-30 Dias Para Vencimiento
    		sql = " SELECT racc_balance AS DPV130, racc_currencyid, racc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
					" WHERE racc_balance > 0 " + 
					" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
    				where + 
    				whereFailure +
    				whereLinked;
					if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
						sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 1 DAY) " +
    							" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 30 DAY) " +
    							" ORDER BY racc_duedate ASC";
					} else {
						sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL 1 DAY) " +
    							" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL 30 DAY) " +
    							" ORDER BY racc_duedatestart ASC";
					}
    		pmRaccounts.doFetch(sql);
    		while (pmRaccounts.next()) { 
    			//DPV130 = pmRaccounts.getDouble("DPV130");
    			currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
        		parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
        		DPV130 += pmCurrencyExchange.currencyExchange(pmRaccounts.getDouble("DPV130"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDPV130 += DPV130;
    		DPV130T += DPV130;
    		total += DPV130;

    		// 31-60 Dias Para Vencimiento
    		sql = " SELECT racc_balance AS DPV3160, racc_currencyid, racc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
					" WHERE racc_balance > 0 " + 
					" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
    				where + 
    				whereFailure +
    				whereLinked;
    				if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
    					sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 31 DAY) " +
    							" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 60 DAY) " +
    							" ORDER BY racc_duedate ASC";
    				} else {
   						sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL 31 DAY) " +
    							" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL 60 DAY) " +
    							" ORDER BY racc_duedatestart ASC";
   					}
    		pmRaccounts.doFetch(sql);
    		while (pmRaccounts.next()) {
    			//DPV3160 = pmRaccounts.getDouble("DPV3160");
    			currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
        		parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
        		DPV3160 += pmCurrencyExchange.currencyExchange(pmRaccounts.getDouble("DPV3160"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDPV3160 += DPV3160;
    		DPV3160T += DPV3160;
    		total += DPV3160;
    		
    		// 61-90 Dias Para Vencimiento
    		sql = " SELECT racc_balance AS DPV6190, racc_currencyid, racc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
					" WHERE racc_balance > 0 " + 
					" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
    				where + 
    				whereFailure +
    				whereLinked;
					if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
						sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 61 DAY) " +
	    						" AND racc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 90 DAY) " +
	    						" ORDER BY racc_duedate ASC";
					} else {
						sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL 61 DAY) " +
	    						" AND racc_duedatestart <= DATE_ADD('" + dueDate + "', INTERVAL 90 DAY) " +
	    						" ORDER BY racc_duedatestart ASC";
					}
    		pmRaccounts.doFetch(sql);
    		while (pmRaccounts.next()) {
    			//DPV6190 = pmRaccounts.getDouble("DPV6190");
    			currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
        		parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
        		DPV6190 += pmCurrencyExchange.currencyExchange(pmRaccounts.getDouble("DPV6190"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDPV6190 += DPV6190;
    		DPV6190T += DPV6190;
    		total += DPV6190;
    		
    		// +91 Dias Para Vencimiento
    		sql = " SELECT racc_balance AS DPV91, racc_currencyid, racc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") + 
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (racc_customerid = cust_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (racc_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (orde_ordertypeid = ortp_ordertypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (racc_companyid = comp_companyid) "+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
					" WHERE racc_balance > 0 " + 
					" AND racc_customerid = " + pmCustomers.getInt("cust_customerid") +
    				where + 
    				whereFailure +
    				whereLinked;
					if (orderType.equals("" + BmoOrderType.TYPE_PROPERTY)) {
						sql += " AND racc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 91 DAY) " +
								" ORDER BY racc_duedate ASC";
					} else {
						sql += " AND racc_duedatestart >= DATE_ADD('" + dueDate + "', INTERVAL 91 DAY) " +
								" ORDER BY racc_duedatestart ASC";
					}
    		pmRaccounts.doFetch(sql);
    		while (pmRaccounts.next()) {
    			//DPV91 = pmRaccounts.getDouble("DPV91");
    			currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
        		parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
        		DPV91 += pmCurrencyExchange.currencyExchange(pmRaccounts.getDouble("DPV91"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}

    		sumDPV91 += DPV91;
    		DPV91T = DPV91;
    		total += DPV91;
            
        	sumTotal += total;
           	i++;
        %>
            <tr class="reportCellEven">	
    			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
           		<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>	
           		<%	if (sFParams.isFieldEnabled(bmoRaccount.getBmoCustomer().getCustomercategory())) { %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoCustomer.getCustomercategory().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				<%	} %>
           		<td style="border-style: hidden">&nbsp;</td>
           		<%= HtmlUtil.formatReportCell(sFParams, "" + DV030T , BmFieldType.CURRENCY) %>
           		<%= HtmlUtil.formatReportCell(sFParams, "" + DV3160T, BmFieldType.CURRENCY) %>
           		<%= HtmlUtil.formatReportCell(sFParams, "" + DV6190T, BmFieldType.CURRENCY) %>
           		<%= HtmlUtil.formatReportCell(sFParams, "" + DV91T, BmFieldType.CURRENCY) %>
    	    	
    	    	<td style="border-style: hidden">&nbsp;</td>
    	    	
    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DPV130T, BmFieldType.CURRENCY) %>
    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DPV3160T, BmFieldType.CURRENCY) %>
    			<%= HtmlUtil.formatReportCell(sFParams, "" + DPV6190T, BmFieldType.CURRENCY) %>
    			<%= HtmlUtil.formatReportCell(sFParams, "" + DPV91T, BmFieldType.CURRENCY) %>	
    			<td style="border-style: hidden">&nbsp;</td>
    			<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
    		</tr>
        <%    
    	}   //End while pmCustomers
        if (sumDV91 > 0 || sumDV6190 > 0 || sumDV3160 > 0 || sumDV030 > 0 
        		|| sumDPV130 > 0 || sumDPV3160 > 0 || sumDPV6190 > 0 || sumDPV91 > 0 
        		|| sumTotal > 0) {
	        %>
	        <tr><td colspan="14">&nbsp;</td></tr>      
	    	<tr class="reportCellEven reportCellCode">
	    		<td class="reportCellText" colspan="<%= (dynamicColspan)%>">&nbsp;</td>
	    		<td style="border-style: hidden">&nbsp;</td>	    		
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV030, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV3160, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV6190, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV91, BmFieldType.CURRENCY) %>
	    		
	    		<td style="border-style: hidden">&nbsp;</td>
	    		
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV130, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV3160, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV6190, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV91, BmFieldType.CURRENCY) %>
	    		<td style="border-style: hidden">&nbsp;</td>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotal, BmFieldType.CURRENCY) %>
	    		
	    	</tr>
    <%	}
    }// fin de SI existe moneda
    } // FIN DEL CONTADOR
    %>
    
    
</TABLE>

<%
        } 
	pmCurrencyWhile.close();
	pmConn.close();	
	pmRaccounts.close();
   	pmCustomers.close(); 
   	pmConnCount.close();
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