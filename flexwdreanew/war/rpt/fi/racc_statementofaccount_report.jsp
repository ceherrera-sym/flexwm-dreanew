<!--  
/**
 * SYMGF
 * Derechos Reservados Symetria Tecnologica S.A. de C.V.
 * Este software es propiedad de Symetria Tecnologia, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */ -->
<%@page import="com.flexwm.server.FlexUtil"%>
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
<%@include file="/inc/login.jsp"%>
<%@page import="com.symgae.shared.SQLUtil"%>

<%
	// Inicializar variables
 	String title = "Estado de Cuenta del Cliente";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	BmoRaccount bmoRaccount = new BmoRaccount();
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoCustomer bmoCustomer = new BmoCustomer();
	
   	String sql = "", where = "", whereOrderId = "", whereSupl = "", whereFailure = "", whereLinked = "", whereCustomer = "",  sqlCurrency = "";
   	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "";
   	String status = "", paymentStatus = "", paymentStatus2 = "";
   	String filters = "", customer = "", customerCategory ="", logoURL ="";;
   	int programId = 0, customerid = 0, orderId = 0, cols= 0, raccountTypeId = 0, companyId = 0, wflowCategoryId = 0, userId = 0, collectorUserId = 0, 
   			currencyId = 0, failure = 0, linked = 0, budgetId = 0, budgetItemId = 0, activeBudgets = 0,areaId = -1;
   		   	boolean enableBudgets = false;
   	double balanceLabel = 0;
   	
 	// dynamicColspan incrementar por cada columna del reporte
	// dynamicColspanMinus incrementar por cada columna que vaya a mostrar totales(es decir, se va a restar al dynamicColspan si HAY FILA TOTALES)
	int dynamicColspan = 0, dynamicColspanMinus = 0;
	// se agrega 2 columnas para presupuestos, para manejo de colspans
   	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		//enableBudgets = true;
   		//activeBudgets = 2;
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
	   if (request.getParameter("racc_budgetitemid") != null) budgetItemId = Integer.parseInt(request.getParameter("racc_budgetitemid"));
	   if (request.getParameter("racc_areaid") != null) areaId = Integer.parseInt(request.getParameter("racc_areaid"));
	}
   
	// Filtros listados
	 if (!customerCategory.equals("")) {
        where += SFServerUtil.parseFiltersToSql("cust_customercategory", customerCategory);
        filters += "<i>Categoría Cliente: </i>" + request.getParameter("cust_customercategoryLabel") + ", ";
    }
	if (customerid > 0) {
		whereCustomer += " AND racc_customerid = " + customerid;
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
        bmoCompany = (BmoCompany)pmCompany.get(companyId);
     	// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
    }
	
    if (userId > 0) {
    	where += " AND orde_userid = " + userId;
    	filters += "<i>Usuario: </i>" + request.getParameter("useridLabel") + ", ";
	}
    
    if (collectorUserId > 0) {
    	where += " AND racc_collectuserid = " + collectorUserId;
    	filters += "<i>Cobranza: </i>" + request.getParameter("racc_collectuseridLabel") + ", ";
	}
    
	if (enableBudgets) {
	    if (budgetId > 0) {
	        where += " AND bgit_budgetid = " + budgetId;
	        filters += "<i>Presup.: </i>" + request.getParameter("bgit_budgetidLabel") + ", ";
	    }  
	
	    if (budgetItemId > 0) {
	        where += " AND racc_budgetitemid = " + budgetItemId;
	        filters += "<i>Item Presup.: </i>" + request.getParameter("racc_budgetitemidLabel") + ", ";
	    }
	    
	    if (areaId > 0) {
	        where += " AND racc_areaid = " + areaId;
	        filters += "<i>Departamento.: </i>" + request.getParameter("racc_areaidLabel") + ", ";
	    }
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
	
   	if (!receiveDate.equals("")) {
   		where += " AND racc_receivedate >= '" + receiveDate + "'";
   		filters += "<i>Recepci&oacute;n Inicio: </i>" + receiveDate + ", ";
   	}
   	
    if (!receiveEndDate.equals("")) {
        where += " AND racc_receivedate <= '" + receiveEndDate + "'";
        filters += "<i>Recepci&oacute;n Final: </i>" + receiveEndDate + ", ";
    }
   	   	
   	if (!dueDate.equals("")) {
        where += " AND racc_duedate >= '" + dueDate + "'";
        filters += "<i>Programaci&oacute;n Inicio: </i>" + dueDate + ", ";
    }
    
   	if (!dueEndDate.equals("")) {
        where += " AND racc_duedate <= '" + dueEndDate + "'";
        filters += "<i>Programaci&oacute;n Final: </i>" + dueEndDate + ", ";
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
   		filters += "<i>Moneda: </i>" + request.getParameter("racc_currencyidLabel")  + " | <i>Paridad Actual : </i>" + defaultParity;
   	} else {
   		filters += "<i>Moneda: </i> Todas ";
   		// Se mete la consulta padre para agilizar el proceso
		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
		if (enableBudgets) {
			sqlCurrency += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
		                 + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
						 + " LEFT JOIN "+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
			             +" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
				
		}
		sqlCurrency += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")
				+ "  ON (racc_currencyid = cure_currencyid) " + " LEFT JOIN "
				+ SQLUtil.formatKind(sFParams, "customers") + " ON (racc_customerid = cust_customerid)"
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders") + " ON (racc_orderid = orde_orderid)"
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "credits") + " ON (orde_orderid = cred_orderid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (orde_userid = user_userid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows") + " ON (orde_wflowid = wflw_wflowid ) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")
				+ " ON (wflw_wflowtypeid = wfty_wflowtypeid) " + " LEFT JOIN "
				+ SQLUtil.formatKind(sFParams, "wflowcategories")
				+ " ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " + " LEFT JOIN "
				+ SQLUtil.formatKind(sFParams, "companies") + " ON (racc_companyid = comp_companyid)"
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")
				+ " ON (racc_raccounttypeid = ract_raccounttypeid)" + " WHERE racc_raccountid > 0 "
				+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + whereOrderId + where
				+ whereFailure + whereLinked + " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
	}

	// Obtener disclosure de datos
	String disclosureFilters = new PmRaccount(sFParams).getDisclosureFilters();
	if (disclosureFilters.length() > 0)
		where += " AND " + disclosureFilters;

	PmConn pmRaccounts = new PmConn(sFParams);
	pmRaccounts.open();

	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();

	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();

	boolean s = true;

	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram = new PmProgram(sFParams);
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
	<title>:::<%= title %>:::</title>
	<link rel="stylesheet" type="text/css"href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>
<style>
	.reportCellEvenNotBorderBottom {
		padding: 6px 8px;height: 20px;"
	}
 </style>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">
<table class="report" border="0">

<% if (customerid > 0 && companyId > 0 && currencyId > 0) { %>
	
	<%  
   	// Totales generales
    double amountTotal = 0;
	double taxTotal = 0;
	double totalTotal = 0;
	double paymentsTotal = 0;
	double balanceTotal = 0;
    int orderid = 0;
    
    sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, "raccounts");
	if (enableBudgets) {
			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
					+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
					+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
	}	
	sql +=	" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (racc_currencyid = cure_currencyid) " 
			+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "customers") + " ON (racc_customerid = cust_customerid)"
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders") + " ON (racc_orderid = orde_orderid)" 
			+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "credits") + " ON (orde_orderid = cred_orderid) "
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (orde_userid = user_userid) " 
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows") + " ON (orde_wflowid = wflw_wflowid ) "
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") + " ON (wflw_wflowtypeid = wfty_wflowtypeid) " 
			+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "wflowcategories") + " ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " 
			+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "companies") + " ON (racc_companyid = comp_companyid)"
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes") + " ON (racc_raccounttypeid = ract_raccounttypeid)" 
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "reqpaytypes") + " ON (rqpt_reqpaytypeid = racc_reqpaytypeid)" 
			+ " WHERE racc_raccountid > 0 "
			+ where
			+ whereOrderId 
			+ whereCustomer
			+ whereFailure + 
			whereLinked +
			" ORDER BY racc_invoiceno ASC ";

		int count =0;
		//ejecuto el sql DEL CONTADOR
		pmConnCount.doFetch(sql);
		if(pmConnCount.next())
			count=pmConnCount.getInt("contador");
		System.out.println("contador DE REGISTROS --> " + count);
		//if que muestra el mensajede error
		if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()) {
			%>
			<%=messageTooLargeList %>
			<% 
		} else {
	
	if (!(currencyId > 0)) {
		
		int currencyIdWhile = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
	    	if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
          		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
          	    amountTotal = 0;
          		taxTotal = 0;
          		paymentsTotal = 0;
          		balanceTotal = 0;
          	    dynamicColspan = 0;
          	    dynamicColspanMinus = 0;
          	    
           		%>
				<tr>
					<td class="reportHeaderCellCenter"
						colspan="10"><%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
					</td>
				</tr>
				<%
            	} %>
<!--             	Aqui va codigo sin filtro de moneda -->
		<%
		} // Fin pmCurrencyWhile
	}
	// Si existe moneda destino
	else {
		
		boolean first = true;
   		int x = 1;
		double amountSum = 0;
		double taxSum = 0;
		double totalSum = 0;
		double paymentsSum = 0;
		double balanceSum = 0;
		double tax = 0;
		sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "raccounts");
		if (enableBudgets) {
				sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
						+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
						+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
		}	
		sql +=	" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (racc_currencyid = cure_currencyid) " 
				+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "customers") + " ON (racc_customerid = cust_customerid)"
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders") + " ON (racc_orderid = orde_orderid)" 
				+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "credits") + " ON (orde_orderid = cred_orderid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (orde_userid = user_userid) " 
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows") + " ON (orde_wflowid = wflw_wflowid ) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") + " ON (wflw_wflowtypeid = wfty_wflowtypeid) " 
				+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "wflowcategories") + " ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " 
				+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "companies") + " ON (racc_companyid = comp_companyid)"
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes") + " ON (racc_raccounttypeid = ract_raccounttypeid)" 
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "reqpaytypes") + " ON (rqpt_reqpaytypeid = racc_reqpaytypeid)" 
				+ " WHERE racc_raccountid > 0 "
				+ " AND racc_linked = 0 " // NO MOSTRAR EXTERNAS
				//+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" 
				+ where
				+ whereOrderId 
				+ whereCustomer
				+ whereFailure + 
				whereLinked +
				" ORDER BY racc_invoiceno ASC ";
		pmRaccounts.doFetch(sql);
		while (pmRaccounts.next()) {
			bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts", "racc_status"));
			bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts", "racc_paymentstatus"));
			bmoCustomer.getCustomercategory().setValue(pmRaccounts.getString("customers","cust_customercategory"));

// 			if (pmRaccounts.getString("cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY))
				customer = pmRaccounts.getString("cust_code") + " "	+ pmRaccounts.getString("cust_legalname");
// 			else
// 				customer = pmRaccounts.getString("cust_code") + " "	+ pmRaccounts.getString("cust_displayname");
			
			// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
			bmoCompany.getLogo().setValue(pmRaccounts.getString("comp_logo"));
			if (!bmoCompany.getLogo().toString().equals(""))
				logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
			else 
				logoURL = sFParams.getMainImageUrl();
			
			// Calculo de dias vencidos
			//System.out.println("CC: "+ pmRaccounts.getString("racc_invoiceno"));
			String daysStartDate = pmRaccounts.getString("racc_duedatestart");
			//System.out.println("F.Reg1: "+daysStartDate);
			//int reqPayType = pmRaccounts.getInt("rqpt_days");
			//System.out.println("TP_RACC: "+reqPayType);
			//daysStartDate = SFServerUtil.addDays(sFParams.getDateFormat(), daysStartDate, reqPayType);
			//System.out.println("F.Reg2: "+daysStartDate);
			String daysEndDate = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
			//System.out.println("daysEndDate: "+daysEndDate);
			int diff = FlexUtil.daysDiff(sFParams, daysStartDate, daysEndDate);
			
			if (first) {
				first = false;
		   	%>
				<tr>
					<td class="topBar" align="left" width="5%" valign="top">
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
					</td>
					<td class="topBar topBarTitle" colspan="8">
						<a style="color:white">
						<b><%= pmRaccounts.getString("comp_name")%></b>
						</a>
					</td>
				</tr>
				<tr>
					<td class="programTitle" colspan="8">
						<b><%= title %></b>
					</td>
					<td class="documentTitleCaption" align="right" style="vertical-align: top">
						<b><%= SFServerUtil.nowToString(sFParams, "dd/MMMM/yyyy HH:mm") %></b>
					</td>
				</tr>
				<tr>       
					<th class="reportCellEvenNotBorderBottom" align="left" colspan="">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getDisplayName() ) ) %>:
					</th>
					<td class="reportCellEvenNotBorderBottom" align="left" colspan="4">
						<%= HtmlUtil.stringToHtml(pmRaccounts.getString("cust_displayname")) %>
					</td>
					<th class="reportCellEvenNotBorderBottom" align="left" colspan="" style="font-size:12pt">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getBalance()) ) %>
						Total por Pagar:
					</th>
					<td class="reportCellEvenNotBorderBottom" align="left" colspan="4">
						<div id="balance" style="font-size:12pt; font-weight: bold;"></div>
					</td>
				</tr>
	   			<tr>       
					<th class="reportCellEvenNotBorderBottom" align="left" colspan="">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getLegalname() ) ) %>:
					</th>
					<td class="reportCellEvenNotBorderBottom" align="left" colspan="4">
					<%	if (pmRaccounts.getString("cust_legalname").equals("")) { %>
							-
					<% 	} else { %>
							<%= HtmlUtil.stringToHtml(pmRaccounts.getString("cust_legalname")) %>
					<%	} %>
					</td>
					<th class="reportCellEvenNotBorderBottom" align="left" colspan="">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getReqPayTypeId()) ) %>:
					</th>
					<td class="reportCellEvenNotBorderBottom" align="left" colspan="4">
					<%	if (!(pmRaccounts.getInt("racc_reqpaytypeid") > 0)) { %>
							-
					<% 	} else { %>
							<%= HtmlUtil.stringToHtml(pmRaccounts.getString("rqpt_name"))%>
					<%	} %>
					</td>
				</tr>
				<tr>       
					<th class="reportCellEvenNotBorderBottom" align="left" colspan="">
						<% 	if (!receiveDate.equals("")) { %>
								Desde:
						<%	} %>
					</th>
					<td class="reportCellEvenNotBorderBottom" align="left" colspan="4">
						<%= receiveDate%>
					</td>
					<th class="reportCellEvenNotBorderBottom" align="left" colspan="">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getEmail() ) ) %>:
					</th>
					<td class="reportCellEvenNotBorderBottom" align="left" colspan="4">
					<%	if (pmRaccounts.getString("cust_email").equals("")) { %>
							-
					<% 	} else { %>
							<%= pmRaccounts.getString("cust_email")%>
					<%	} %>	
					</td>
				</tr>
				<tr>       
					<th class="reportCellEvenNotBorderBottom" align="left" colspan="">
					<% 	if (!receiveEndDate.equals("")) { %>
						Hasta:
					<%	} %>
					</th>
					<td class="reportCellEvenNotBorderBottom" align="left" colspan="4">
						<%= receiveEndDate%>
					</td>
					<th class="reportCellEvenNotBorderBottom" align="left" colspan="">
						&nbsp;
					</th>
					<td class="reportCellEvenNotBorderBottom" align="left" colspan="4">
						&nbsp;
					</td>
				</tr>
	   			<tr><td colspan="9">&nbsp;</td></tr>
	   			</table>
				<table class="report" border="0">
				   	<tr class="">
						<td class="listHeaderCell" style="text-align: center; font-weight: bold;">#</td>
						<% 	dynamicColspan++; %>
						<%	if (sFParams.isFieldEnabled(bmoRaccount.getInvoiceno())) {
								dynamicColspan++;
						%>
								<td class="listHeaderCell" style="font-weight: bold;">
									<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getInvoiceno()))%>
								</td>
						<%	}	%>
						<%	if (sFParams.isFieldEnabled(bmoRaccount.getReceiveDate())) {
								dynamicColspan++; 
						%>
								<td class="listHeaderCell" style="font-weight: bold;">
									<%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getReceiveDate()))%>
								</td>
						<%	} 	%>
						<%	if (sFParams.isFieldEnabled(bmoRaccount.getDueDateStart())) {
								dynamicColspan++; 
						%>
								<td class="listHeaderCell" style="font-weight: bold;">
									<%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getDueDateStart()))%>
								</td>
						<%	} 	%>
						
						<%	//if (sFParams.isFieldEnabled(bmoRaccount.getDueDate())) {
								dynamicColspan++; 
						%>
								<td class="listHeaderCell" style="font-weight: bold;">
									D&iacute;as Vencidos
								</td>
						<%	//} 	%>
						<%	if (sFParams.isFieldEnabled(bmoRaccount.getDescription())) {
								dynamicColspan++;
						%>
								<td class="listHeaderCell" style="font-weight: bold;">
									<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getDescription()))%>
								</td>
						<%	} 	%>
						<%	if (sFParams.isFieldEnabled(bmoRaccount.getCurrencyId())) {
								dynamicColspan++;
								dynamicColspanMinus++;
						%>
								<td class="listHeaderCell" style="font-weight: bold;" title="Paridad Actual (<%= bmoCurrency.getCode().toString()%>):<%= defaultParity%>">
									<%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(),	bmoRaccount.getCurrencyId()))%>
								</td>
						<%	}	%>
						<%	if (sFParams.isFieldEnabled(bmoRaccount.getAmount())) {
								dynamicColspan++;
								dynamicColspanMinus++;
						%>
								<td class="listHeaderCell" style="text-align: right; font-weight: bold;">
									<%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getAmount()))%>
								</td>
						<%	}	%>
						<%	if (sFParams.isFieldEnabled(bmoRaccount.getTax())) {
								dynamicColspan++;
								dynamicColspanMinus++;
						%>
								<td class="listHeaderCell" style="text-align: right; font-weight: bold;">
									<%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getTax()))%>
								</td>
						<%	}	%>
						<%	if (sFParams.isFieldEnabled(bmoRaccount.getTotal())) {
								dynamicColspan++;
								dynamicColspanMinus++;
						%>
								<td class="listHeaderCell" style="text-align: right; font-weight: bold;">
									<%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getTotal()))%>
								</td>
						<%	}	%>
<%-- 						<%	if (sFParams.isFieldEnabled(bmoRaccount.getBalance())) { --%>
<!--  								dynamicColspan++; -->
<!--  								dynamicColspanMinus++; -->
<%-- 						%> --%>
<!-- 								<td class="listHeaderCellRight"> -->
<%-- 									<%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoModule.getCode().toString(), bmoRaccount.getBalance()))%> --%>
<!-- 								</td> -->
<%-- 						<%	}	%> --%>
					</tr>
	<%		}
			
			//if (diff >= 0) {

				// Pintar filas
				if (x % 2 == 0) { %>
					<tr class="listCellOdd">
			<% 	} else { %>
					<tr class="listCellEven">
			<%	} 			
		%>
					<%= HtmlUtil.formatReportCell(sFParams, "" + x, BmFieldType.NUMBER) %>
					<%	if (sFParams.isFieldEnabled(bmoRaccount.getInvoiceno())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_invoiceno")), BmFieldType.STRING) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoRaccount.getReceiveDate())) { %>
							<td class="reportCellText" 
							title = "<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoRaccount.getReqPayTypeId()))%>: <%= pmRaccounts.getString("rqpt_name")%>">
								<%= pmRaccounts.getString("raccounts","racc_receivedate")%>
							</td>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoRaccount.getDueDate())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_duedatestart"), BmFieldType.DATE) %>
					<%	} %>
					
					<% if (diff >= 0) { %>
							<%= HtmlUtil.formatReportCell(sFParams, ""+diff, BmFieldType.STRING) %>
					<%	} else { %>
							<%= HtmlUtil.formatReportCell(sFParams, ""+0, BmFieldType.STRING) %>
					<%	} %>
					<%	//} %>
					<%	if (sFParams.isFieldEnabled(bmoRaccount.getDescription())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_description")), BmFieldType.STRING) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoRaccount.getCurrencyId())) { %>
							<td class="reportCellCode" title="Paridad CC:<%= pmRaccounts.getDouble("racc_currencyparity")%>">
								<%= bmoCurrency.getCode().toString()%> 
					<%	} %>
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
	// 				   	double paymentsRacc = pmRaccounts.getDouble("racc_payments");					           		
					   	double balanceRacc = pmRaccounts.getDouble("racc_balance");
				
					   	amountRacc = pmCurrencyExchange.currencyExchange(amountRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					   	taxRacc = pmCurrencyExchange.currencyExchange(taxRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					   	totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	// 					paymentsRacc = pmCurrencyExchange.currencyExchange(paymentsRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						balanceRacc = pmCurrencyExchange.currencyExchange(balanceRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					
						amountSum += amountRacc;
						taxSum += taxRacc; 
						totalSum += totalRacc;
	// 					amountSum += paymentsRacc;
						balanceSum += balanceRacc;
		
						amountTotal += amountRacc;
					   	taxTotal += taxRacc;
						totalTotal += totalRacc;
	// 				   	paymentsTotal += totalRacc;
					   	balanceTotal += balanceRacc;
					%>
					<%	if (sFParams.isFieldEnabled(bmoRaccount.getAmount())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoRaccount.getTax())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + taxRacc, BmFieldType.CURRENCY) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoRaccount.getTotal())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc, BmFieldType.CURRENCY) %>
					<%	} %>
	<%-- 				<%	if (sFParams.isFieldEnabled(bmoRaccount.getBalance())) { %> --%>
	<%-- 						<%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %> --%>
	<%-- 				<%	} %> --%>
				</TR>
				<%
				x++;
			//}
	        
        }
		// Sumas por cliente
   		if (amountSum > 0) { %>
			<tr class="reportCellEven reportCellCode">
				<td colspan="<%= (dynamicColspan - dynamicColspanMinus)%>">&nbsp;</td>
				<td >&nbsp;</td>
				
				<%	if (sFParams.isFieldEnabled(bmoRaccount.getAmount())) { %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
				<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoRaccount.getTax())) { %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + taxSum, BmFieldType.CURRENCY) %>
				<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoRaccount.getTotal())) { %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + totalSum, BmFieldType.CURRENCY) %>
				<%	} %>
<%-- 				<%	if (sFParams.isFieldEnabled(bmoRaccount.getBalance())) { %> --%>
<%-- 						<%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %> --%>
<%-- 				<%	} %> --%>
			</tr>
			<% 
			balanceLabel = balanceTotal;
		}

		// Mandar mensaje de que no hay registros
		if ((x == 1)) { %>
	    	<tr><td>Sin Registros.</td></tr>
	<%	}
	} // Fin else moneda
	} // FIN DEL CONTADOR
	} // Fin de existe Cliente, Empresa, Moneda o las Fechas de Registro
    else { %>
		<script type="text/javascript">
			alert("Verifique que haya filtrado un Cliente, Empresa o Moneda.");
		</script>
<% 	} %>
</TABLE>
<%
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmConn.close();
	pmRaccounts.close();
	pmConnCount.close();
%>
<script>
	document.getElementById("balance").innerHTML = "<%= SFServerUtil.formatCurrency(balanceLabel) %> <%= bmoCurrency.getCode().toString()%>";
</script>
	<% if (print.equals("1")) { %>
	<script>
	
			//window.print();
		</script>
	<% } 
	System.out.println("\n  Fin reporte - Fecha: " + SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
					+ " Reporte: " + title
					+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); %>
</body>
</html>