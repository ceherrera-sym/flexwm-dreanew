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
 	String title = "Reporte CxC Agrupadas por Pedido";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	BmoRaccount bmoRaccount = new BmoRaccount();
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoCustomer bmoCustomer = new BmoCustomer();
	
   	String sql = "", where = "", whereOrderId = "", whereSupl = "", whereFailure = "", whereLinked = "", sqlCurrency = "";
   	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "";
   	String status = "", paymentStatus = "", paymentStatus2 = "",customerCategory ="";
   	String filters = "", budgetItemId = "", areaId = "-1";
   	int programId = 0, customerid = 0, orderId = 0, cols= 0, raccountTypeId = 0, companyId = 0, wflowCategoryId = 0,
   			userId = 0, collectorUserId = 0, currencyId = 0, failure = 0, linked = 0, budgetId = 0, activeBudgets = 0;
   		   	boolean enableBudgets = false;
   	
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
    
	// Filtros listados
	 if (!customerCategory.equals("")) {
        where += SFServerUtil.parseFiltersToSql("cust_customercategory", customerCategory);
        filters += "<i>Categoría Cliente: </i>" + request.getParameter("cust_customercategoryLabel") + ", ";
    }
	if (customerid > 0) {
		where += " AND racc_customerid = " + customerid;
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
    
    if (enableBudgets) {
	    if (budgetId > 0) {
	        where += " AND bgit_budgetid = " + budgetId;
	        filters += "<i>Presup.: </i>" + request.getParameter("bgit_budgetidLabel") + ", ";
	    }  
	
	    if (!budgetItemId.equals("")) {
	        where += SFServerUtil.parseFiltersToSql("racc_budgetitemid", budgetItemId);
	       // where += " AND racc_budgetitemid = " + budgetItemId;
	        filters += "<i>Item Presup.: </i>" + request.getParameter("racc_budgetitemidLabel") + ", ";
	    }
	    
	    if (!areaId.equals("")) {
	        where += SFServerUtil.parseFiltersToSql("racc_areaid", areaId);
	       // where += " AND racc_areaid = " + areaId;
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
   	
//   	if (failure > 0) {
//   		whereFailure += " AND racc_failure = " + failure;
//   		filters += "<i>CxC Falla </i>" + ", ";
//   	}
   	
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
			sqlCurrency += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
						+ " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
						+ SQLUtil.formatKind(sFParams, "budgetitemtypes")
						+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
						+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
					    +" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
					    +" ON(area_areaid = racc_areaid) ";
						
				}
		sqlCurrency += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+"  ON (racc_currencyid = cure_currencyid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credits")+" ON (orde_orderid = cred_orderid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
    			" WHERE racc_raccountid > 0 " +
    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
    			whereOrderId +
    			where +
    			whereFailure +
    			whereLinked +
    			" GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
   	}
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmRaccount(sFParams).getDisclosureFilters();   
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
   	PmConn pmOrders = new PmConn(sFParams);
   	pmOrders.open();
   	
   	PmConn pmRaccounts = new PmConn(sFParams);
   	pmRaccounts.open();
   	
    PmConn pmConn = new PmConn(sFParams);
    pmConn.open();
    
    PmConn pmCurrencyWhile =new PmConn(sFParams);
    pmCurrencyWhile.open();
   	
   	boolean s = true;
   	
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
			<b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Pedido" : "Pedido")%>.
			<b>Ordenado por:</b> Clave Pedido, Clave CxC
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
    

	sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
	if (enableBudgets) {
		 sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
		 + " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
		 + SQLUtil.formatKind(sFParams, "budgetitemtypes")
		 + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
		 + SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
		 +" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
		+" ON(area_areaid = racc_areaid) ";
	}
	sql +=	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
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
			" WHERE racc_raccountid > 0 " +
			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
			where +
			whereFailure +
			whereLinked +
			" ORDER BY racc_raccountid ASC ";
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
		
		int currencyIdWhile = 0;// , i = 1, y = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			sql = " SELECT DISTINCT(racc_orderid) AS pedido, orde_orderid, orde_currencyid, orde_currencyparity, orde_code, orde_name, " +
		    		" cust_customerid, cust_code, cust_legalname, cust_displayname, orde_total, cureOrder.cure_currencyid, cureOrder.cure_code, cust_customercategory" +
		    		" FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
		    		if (enableBudgets) {
				    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
				    		+ " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
				    		+ SQLUtil.formatKind(sFParams, "budgetitemtypes")
				    		+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
				    		+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
				    		+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
					        +" ON(area_areaid = racc_areaid) ";
				    	}
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
	    			whereOrderId +
	    			where +
	    			whereFailure +
	    			whereLinked +
					" ORDER BY orde_orderid ASC";
			System.out.println("Consulta:" + sql);
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
	            		<td class="reportHeaderCellCenter" colspan="21">
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
			               <td class="reportGroupCell" colspan="21">				                   		
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
			               <%	if (enableBudgets) { %>
				               <td class="reportHeaderCell">Partida Pres</td>
		               		   <td class="reportHeaderCell">Departamento</td>
		               		<% } %>						               
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
		    	if (enableBudgets) {
			    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
			    		+ " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
			    		+ SQLUtil.formatKind(sFParams, "budgetitemtypes")
			    		+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
			    		+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
			    		+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
				        +" ON(area_areaid = racc_areaid) ";
			    	}
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
		    			whereFailure +
		    			whereLinked +
		    			" ORDER BY racc_raccountid ASC ";
			   pmRaccounts.doFetch(sql);                           
		       while (pmRaccounts.next()) {
		    	   bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts","racc_status"));
		           bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts","racc_paymentstatus"));
		   %>
				   <tr class="reportCellEven">
					   <%= HtmlUtil.formatReportCell(sFParams, "" + x, BmFieldType.NUMBER) %>
					   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
					   <% if (enableBudgets) { %>
	        		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
		        	   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %>
		        	   <%} %>
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
		                    <td colspan="<%=((enableBudgets) ? "16":"13")%>">&nbsp;</td>
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
		    if (enableBudgets) {
		    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
		    		+ " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
		    		+ SQLUtil.formatKind(sFParams, "budgetitemtypes")
		    		+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
		    		+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
		    		+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
			        +" ON(area_areaid = racc_areaid) ";
		    	}
		    
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
		        	  whereFailure +
		        	  whereLinked +
		        	  " ORDER BY racc_customerid ";
		    pmRaccounts.doFetch(sql);
		    if (pmRaccounts.next()) items = pmRaccounts.getInt("cxc");
		    
		    if (items > 0) {
		    %>
		    	<tr><td colspan="21">&nbsp;</td></tr>
			    <tr>
			       <td class="reportHeaderCell" colspan="21" align="left">
			       		Cuentas por Cobrar sin Pedido
			       </td>	                   
			    </tr>
			    <%
			    
			    double amountW = 0, amountD = 0, amount = 0,tax = 0;
			    int customerId = 0;
			    int i = 1;
			    //Listar las CxC que no estan ligadas a un pedido
			    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
			    if (enableBudgets) {
				sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
					    + " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
					    + SQLUtil.formatKind(sFParams, "budgetitemtypes")
					    + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
					    + SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
					    +" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
						+" ON(area_areaid = racc_areaid) ";
				}
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
			    	  whereFailure +
			    	  whereLinked +
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
			               <%if(enableBudgets){ %>
			               		<td class="reportHeaderCell">Partida Pres</td>
		               		   <td class="reportHeaderCell">Departamento</td>
			               <%} %>				               
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
				        <%	if (enableBudgets) { %>
	        		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
		        	   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %>
		        	   <%	} %>
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
		    	<tr><td colspan="21">&nbsp;</td></tr>
			    <tr class="reportCellEven reportCellCode">
				    <%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
				    <td colspan="<%=((enableBudgets) ? "15":"12")%>">&nbsp;</td>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + amountT, BmFieldType.CURRENCY) %>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + taxT, BmFieldType.CURRENCY) %>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + withDrawTotal, BmFieldType.CURRENCY) %>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + depositTotal, BmFieldType.CURRENCY) %>
				    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceT, BmFieldType.CURRENCY) %>
			    </tr>
			    <tr><td colspan="21">&nbsp;</td></tr>
		<%	}
		} // Fin pmCurrencyWhile
	}
	// Si existe moneda destino
	else {	
	    sql = " SELECT DISTINCT(racc_orderid) AS pedido, orde_orderid, orde_currencyid, orde_currencyparity, orde_code, orde_name, " +
	    		" cust_customerid, cust_code, cust_legalname, cust_displayname, orde_total, cureOrder.cure_currencyid, cureOrder.cure_code,cust_customercategory " +
	    		" FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
	    if (enableBudgets) {
			    sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
			    + " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
			    + SQLUtil.formatKind(sFParams, "budgetitemtypes")
			    + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
			    + SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
			    +" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
				+" ON(area_areaid = racc_areaid) ";
		}
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
    			whereOrderId +
    			where +
    			whereFailure +
    			whereLinked +
				" ORDER BY orde_orderid ASC";

	    System.out.println("consulta: "+sql);
	    pmOrders.doFetch(sql);
	    while (pmOrders.next()) {
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
	                   <td class="reportGroupCell" colspan="21">				                   		
	                   		<%= pmOrders.getString("orde_code")%>
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("orde_name"))%> |
	                   		Cliente:
	                   		<%= pmOrders.getString("cust_code")%>
	                   		<%= HtmlUtil.stringToHtml(pmOrders.getString("cust_displayname"))%> |
	                   		<%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { %>
	                   		Categoría Cliente:
				            <%= HtmlUtil.stringToHtml(bmoCustomer.getCustomercategory().getSelectedOption().getLabel()) %> |
					        <%} %>	
	                   				Tipo de Cambio:
	 				        <% 	if (currencyIdOrigin != currencyIdDestiny) { %>
	 				        		<%= parityDestiny%>
	 				        <% 	} else { %>
	                   		 		<%= pmOrders.getDouble("orde_currencyparity") %> |
	 				        <% 	} %>
	                   		<span title='Origen( Tipo de Cambio <%= pmOrders.getDouble("orde_currencyparity")%> | Total: <%= SFServerUtil.formatCurrency(pmOrders.getDouble("orde_total"))%> <%= pmOrders.getString("cureOrder.cure_code") %> )'>
		                   		Total: <%=SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(pmOrders.getDouble("orde_total"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny)) %>
		                   		<%= bmoCurrency.getCode().toString()%>
	                   		</span>                   		
	                   </td>
			        </tr>			        
		            <tr class="">
	    			   <td class="reportHeaderCellCenter">#</td>	
		               <td class="reportHeaderCell">Clave</td>
		               <%	if (enableBudgets) { %>
				               <td class="reportHeaderCell">Partida Pres</td>
		               		   <td class="reportHeaderCell">Departamento</td>
		               <% } %>					               
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
	    	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
	    	if (enableBudgets) {
	    		 sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
	    		 + " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
	    		 + SQLUtil.formatKind(sFParams, "budgetitemtypes")
	    		 + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
	    		 + SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
	    		 +" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
	    		+" ON(area_areaid = racc_areaid) ";
	    	}
	    	sql +=	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
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
	    			" WHERE racc_raccountid > 0 " +
	    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
	    			" AND racc_orderid = " + pmOrders.getInt("orde_orderid") +
	    			where +
	    			whereFailure +
	    			whereLinked +
	    			" ORDER BY racc_raccountid ASC ";
		   pmRaccounts.doFetch(sql);                           
	       while (pmRaccounts.next()) {
	    	   bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts","racc_status"));
	           bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts","racc_paymentstatus"));
	   %>
			   <tr class="reportCellEven">
				   <%= HtmlUtil.formatReportCell(sFParams, "" + x, BmFieldType.NUMBER) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
				   <%	if (enableBudgets) { %>
	        		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
		        	   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %>
		           <%} %>
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
				   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getString("cureRacc","cure_code"), BmFieldType.CODE) %>                    		   
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
	                    <td colspan="<%=((enableBudgets) ? "16":"13")%>">&nbsp;</td>
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
	    if (enableBudgets) {
			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
			+ " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
			+ SQLUtil.formatKind(sFParams, "budgetitemtypes")
			+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
			+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
			+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
			+" ON(area_areaid = racc_areaid) ";
		}
	    sql +=  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +                    	  
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (orde_userid = user_userid) " +
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (orde_wflowid = wflw_wflowid ) " +                       	      
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +                                      
	        	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
	        	  " WHERE racc_orderid is null " +
	        	  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " + 
	        	  where +
	        	  whereFailure +
	        	  whereLinked +
	        	  " ORDER BY racc_customerid ";
	    pmRaccounts.doFetch(sql);
	    if (pmRaccounts.next()) items = pmRaccounts.getInt("cxc");
	    
	    if (items > 0) {
	    %>
	    	<tr><td colspan="19">&nbsp;</td></tr>
		    <tr>
		       <td class="reportHeaderCell" colspan="<%=((enableBudgets) ? "21":"18") %>" align="left">
		       		Cuentas por Cobrar sin Pedido
		       </td>	                   
		    </tr>
		    <%
		    
		    double amountW = 0, amountD = 0, amount = 0,tax = 0;
		    int customerId = 0;
		    int i = 1;
		    //Listar las CxC que no estan ligadas a un pedido
		    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ") ;
		    if (enableBudgets) {
				sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")
				+ " ON (bgit_budgetitemid = racc_budgetitemid) " + " LEFT JOIN "
				+ SQLUtil.formatKind(sFParams, "budgetitemtypes")
				+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " + " LEFT JOIN "
				+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
				+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")
				+" ON(area_areaid = racc_areaid) ";
			}
		    sql +=  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) " +
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
		    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +
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
		    	  whereFailure +
		    	  whereLinked +
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
		               <td class="reportGroupCell" colspan="<%=((enableBudgets) ? "21":"18") %>">
		               		Cliente: <%= pmRaccounts.getString("cust_code") %><%= pmRaccounts.getString("cust_displayname")%>
		               		
		               </td>
			        </tr>	        
		            <tr>
					   <td class="reportHeaderCellCenter">#</td>	
		               <td class="reportHeaderCell">Clave</td>	
					    <%	if (enableBudgets) { %>
						<td class="reportHeaderCell">Partida Pres</td>
		       			<td class="reportHeaderCell">Departamento</td>
		   				<% } %>               
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
			       <%	if (enableBudgets) { %>
	        	   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
		           <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %>
		        	<%	} %>
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

			      <%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
			       <%= HtmlUtil.formatReportCell(sFParams, "" + taxRacc, BmFieldType.CURRENCY) %>
			       <%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc , BmFieldType.CURRENCY) %>
			       <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRacc, BmFieldType.CURRENCY) %>
			       <%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
		    </tr>
			     
			      			       <tr><td colspan="19">&nbsp;</td></tr>
<!-- 		    <tr class="reportCellEven reportCellCode"> -->
<%-- <%-- 			    <%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %> --%> 
<%-- 			    <td colspan="<%=((enableBudgets) ? "16":"13")%>">&nbsp;</td> --%>
<%-- 			       <%= HtmlUtil.formatReportCell(sFParams, "" + amountT, BmFieldType.CURRENCY) %> --%>
<%-- 			    <%= HtmlUtil.formatReportCell(sFParams, "" + taxT, BmFieldType.CURRENCY) %> --%>
<%-- 			    <%= HtmlUtil.formatReportCell(sFParams, "" + withDrawTotal, BmFieldType.CURRENCY) %> --%>
<%-- 			    <%= HtmlUtil.formatReportCell(sFParams, "" + depositTotal, BmFieldType.CURRENCY) %> --%>
<%-- 			    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceT, BmFieldType.CURRENCY) %> --%>
			      
<!-- 		       </TR> -->
		       <%  
		       i++;
		       y++;
		    } //fin while
	    }// fin if  
	    %>
		
	    <%
	    if (withDrawTotal > 0 || depositTotal > 0) {
	    %>
	    <tr><td colspan="">&nbsp;</td></tr>
		    <tr class="reportCellEven reportCellCode">
			    <%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
			    <td colspan="<%=((enableBudgets) ? "15":"12")%>">&nbsp;</td>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + amountT, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + taxT, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + withDrawTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + depositTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceT, BmFieldType.CURRENCY) %>
		    </tr>
	<%	}
	}
	%>
	
</TABLE>  
<%
	}// FIN DEL CONTADOR
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmConn.close();
	pmRaccounts.close();
	pmOrders.close(); 
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