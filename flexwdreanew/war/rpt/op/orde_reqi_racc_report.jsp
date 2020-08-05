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

<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="java.util.Locale"%>
<%@include file="/inc/login.jsp"%>
<%@page import="com.symgae.shared.SQLUtil"%>

<%
    // Inicializar variables
    String title = "Reportes de Compras por Pedido";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoOrder bmoOrder = new BmoOrder();
	BmoWFlow bmoWFlow = new BmoWFlow();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	//Tipo de Pedido
	BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	bmoOrderType = (BmoOrderType)pmOrderType.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getDefaultOrderTypeId().toInteger());

    String sql = "", where = "", sqlCurrency = "";
    String ordeStatus = "", paymentStatus = "", deliveryStatus = "", lockStatus  = "", lockStartDate = "", lockEndDate = "";
    String filters = "", customer = "", whereExtra="", whereProduct="", whereProductFamily = "", whereProductGroup = "", productFamilyId = "", productGroupId = "";
    int programId = 0, customerId = 0, cols= 0, orderId = 0, industryId = 0, userId = 0, productId = 0, showProductExtra = 0, 
    		currencyId = 0 , wflowtypeId=0, dynamicColspan = 0, dynamicColspanMinus = 0, columnBudgets = 0, orderTypeId = 0, budgetId = -1, budgetItemId = -1, areaId = -1;
   	double nowParity = 0, defaultParity = 0;
   	
 	boolean enableBudgets = false;
   	
   	// se agrega 2 columnas para presupuestos  y 1 para dpto., para manejo de colspans
   	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
   		columnBudgets = 3;
   	}

    // Obtener parametros       
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("orde_orderid") != null) orderId = Integer.parseInt(request.getParameter("orde_orderid"));    
    if (request.getParameter("orde_customerid") != null) customerId = Integer.parseInt(request.getParameter("orde_customerid"));    
    if (request.getParameter("orde_status") != null) ordeStatus = request.getParameter("orde_status");
    if (request.getParameter("orde_lockstatus") != null) lockStatus = request.getParameter("orde_lockstatus");
    if (request.getParameter("orde_deliverystatus") != null) deliveryStatus = request.getParameter("orde_deliverystatus");
    if (request.getParameter("orde_paymentstatus") != null) paymentStatus = request.getParameter("orde_paymentstatus");
    if (request.getParameter("orde_lockstart") != null) lockStartDate = request.getParameter("orde_lockstart");
    if (request.getParameter("orde_lockend") != null) lockEndDate = request.getParameter("orde_lockend");
    if (request.getParameter("area_areaid") != null) areaId = Integer.parseInt(request.getParameter("area_areaid"));  
    if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid"));  
    if (request.getParameter("orde_userid") != null) userId = Integer.parseInt(request.getParameter("orde_userid"));   
   	if (request.getParameter("prod_productid") != null) productId = Integer.parseInt(request.getParameter("prod_productid"));
    if (request.getParameter("prod_productfamilyid") != null) productFamilyId = request.getParameter("prod_productfamilyid");
    if (request.getParameter("prod_productgroupid") != null) productGroupId = request.getParameter("prod_productgroupid");
   	if (request.getParameter("showProductExtra") != null) showProductExtra = Integer.parseInt(request.getParameter("showProductExtra"));
   	if (request.getParameter("orde_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("orde_currencyid"));
    if (request.getParameter("orde_ordertypeid") != null) orderTypeId = Integer.parseInt(request.getParameter("orde_ordertypeid"));    
    if (request.getParameter("bgit_budgetid") != null) budgetId = Integer.parseInt(request.getParameter("bgit_budgetid"));
    if (request.getParameter("orde_defaultbudgetitemid") != null) budgetItemId = Integer.parseInt(request.getParameter("orde_defaultbudgetitemid"));    
    if (request.getParameter("orde_defaultareaid") != null) areaId = Integer.parseInt(request.getParameter("orde_defaultareaid"));    
    if (request.getParameter("orde_wflowtypeid") != null) wflowtypeId = Integer.parseInt(request.getParameter("orde_wflowtypeid"));
    
	bmoProgram = (BmoProgram)pmProgram.get(programId);

    // Filtros listados
    if (orderTypeId > 0) {
    	where += " AND orde_ordertypeid = " + orderTypeId;
    	filters += "<i>Tipo Pedido: </i>" + request.getParameter("orde_ordertypeidLabel") + ", ";
    }
    
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
    	where += " AND cust_customerid = " + customerId;
    	filters += "<i>Cliente: </i>" + request.getParameter("orde_customeridLabel") + ", ";
    }
    
    if (industryId > 0) {
    	where += " AND cust_industryid = " + industryId;
    	filters += "<i>Giro: </i>" + request.getParameter("cust_industryidLabel") + ", ";
    }
    
    if (!ordeStatus.equals("")) {
   		//where += " AND orde_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("orde_status", ordeStatus);
   		filters += "<i>Estatus: </i>" + request.getParameter("orde_statusLabel") + ", ";
   	}
    
    if (!deliveryStatus.equals("")) {
        //where += " and orde_deliverystatus like '" + deliveryStatus + "'";
        where += SFServerUtil.parseFiltersToSql("orde_deliverystatus", deliveryStatus);
        filters += "<i>Entrega: </i>" + request.getParameter("orde_deliverystatusLabel") + ", ";
    }
    
    if (!paymentStatus.equals("")) {
        //where += " AND orde_paymentstatus like '" + paymentStatus + "'";
        where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
        filters += "<i>Pago: </i>" + request.getParameter("orde_paymentstatusLabel") + ", ";
    }
    
    if (wflowtypeId > 0) {
        where += " AND orde_wflowtypeid = " + wflowtypeId;
        filters += "<i>Tipo de Flujo: </i>" + request.getParameter("orde_wflowtypeidLabel") + ", ";
    }
    
    if (!lockStatus.equals("")) {
        where += " AND orde_lockstatus like '" + lockStatus + "'";
        filters += "<i>Apartado: </i>" + request.getParameter("orde_lockstatusLabel") + ", ";
    }
    
    if (!lockStartDate.equals("")) {
        where += " AND orde_lockstart >= '" + lockStartDate + "' ";
        filters += "<i>Apartado Inicio: </i>" + lockStartDate + ", ";
    }
    
    if (!lockEndDate.equals("")) {
        where += " AND orde_lockstart <= '" + lockEndDate + "' ";
        filters += "<i>Apartado Final: </i>" + lockEndDate + ", ";
    }
    
    if (productId > 0) {
    	whereProduct = " AND ordi_productid = " + productId;
        filters += "<i>Producto: </i>" + request.getParameter("prod_productidLabel") + ", ";
    }
    
    if (showProductExtra  != 2) {
    	if(showProductExtra == 1)
    		whereExtra += " AND ordi_productid IS NULL ";
    	else whereExtra += " AND ordi_productid > 0 ";
		filters += "<i>Mostrar Items: </i>" + request.getParameter("showProductExtraLabel") + ", ";
    } else {
    	filters += "<i>Mostrar Items: </i> Todos, ";
    }
    
    if (!productFamilyId.equals("")) {
   		whereProductFamily += SFServerUtil.parseFiltersToSql("prod_productfamilyid", productFamilyId);
   		filters += "<i>Familia: </i>" + request.getParameter("prod_productfamilyidLabel") + ", ";
   	}
   	
   	if (!productGroupId.equals("")) {
   		whereProductGroup += SFServerUtil.parseFiltersToSql("prod_productgroupid", productGroupId);
   		filters += "<i>Grupo Prod.: </i>" + request.getParameter("prod_productgroupidLabel") + ", ";
   	}
   	
   	if (enableBudgets) {
	    if (budgetId > 0) {
	        where += " AND bgit_budgetid = " + budgetId;
	        filters += "<i>Presupuesto: </i>" + request.getParameter("bgit_budgetidLabel") + ", ";
	    }
	
	    if (budgetItemId > 0) {
	        where += " AND orde_defaultbudgetitemid = " + budgetItemId;
	        filters += "<i>Item Presup.: </i>" + request.getParameter("orde_defaultbudgetitemidLabel") + ", ";
	    }
	    
	    if (areaId > 0) {
	        where += " AND orde_defaultareaid = " + areaId;
	        filters += "<i>Departamento: </i>" + request.getParameter("orde_defaultareaidLabel") + ", ";
	    }
    }
   	   	
  	if (currencyId > 0) {
  		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);
  		defaultParity = bmoCurrency.getParity().toDouble();

  		filters += "<i>Moneda: </i>" + request.getParameter("orde_currencyidLabel")
  				+ " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
  	} else {
  		filters += "<i>Moneda: </i> Todas ";
  		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, "orders") +     
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = orde_customerid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries")+" ON(indu_industryid = cust_industryid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = orde_userid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) ";
  				if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
  					sqlCurrency += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
  			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
  			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
  							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
  				}
  				sqlCurrency += " WHERE orde_orderid > 0 " +
  				where;
		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			sqlCurrency += " AND orde_orderid IN ( " +
					" SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, "orders") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups")+" ON (ordg_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orderitems")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON (prod_productid = ordi_productid) " +
					" WHERE orde_orderid = orde_orderid " +
					whereProduct +
					whereExtra +
					whereProductFamily +
					whereProductGroup +
					" ) ";
		}
	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
  	}
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmOrder(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    PmConn pmOrder = new PmConn(sFParams);
    pmOrder.open();
    
    PmConn pmConnReqi = new PmConn(sFParams);
    pmConnReqi.open();
   
    PmConn pmConnRacc = new PmConn(sFParams);
    pmConnRacc.open();
	
	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();

	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	 	pmConnCount.open();
	
	// colspan dinamico
	if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
		dynamicColspan++;
		dynamicColspan++;
	    dynamicColspanMinus++;
	    dynamicColspanMinus++;
	}
//	if (sFParams.isFieldEnabled(bmoWFlow.getFunnel()))
//		dynamicColspan++;
    if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
		dynamicColspan++;
	    dynamicColspanMinus++;
	}
	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity()))
		dynamicColspan++;
	
	//System.out.println("dynamicColspan: "+dynamicColspan);
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
	if (!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
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
	if (sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) { %>
<head>
	<title>:::<%= title %>:::
</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

	<table border="0" cellspacing="0" cellpading="0" width="100%">
		<tr>
			<td align="left" width="80" rowspan="2" valign="top"><img
				border="0" width="<%= SFParams.LOGO_WIDTH %>"
				height="<%= SFParams.LOGO_HEIGHT %>"
				src="<%= sFParams.getMainImageUrl() %>"></td>
			<td class="reportTitle" align="left" colspan="2"><%= title %></td>
		</tr>
		<tr>
			<td class="reportSubTitle"><b>Filtros:</b> <%= filters %></td>
			<td class="reportDate" align="right">Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %>
				por: <%= sFParams.getLoginInfo().getEmailAddress() %>
			</td>
		</tr>
	</table>
	<br>
	<table class="report" border="0" cellspacing="0" cellpading="0" width="100%">
		<%
	
  	 	
        sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, "orders") +     
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = orde_customerid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries")+" ON(indu_industryid = cust_industryid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = orde_userid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) ";
		if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
		}
		sql += " WHERE orde_orderid > 0 " +
				where;
		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
				sql += " AND orde_orderid IN ( " +
						" SELECT ordg_orderid  FROM " + SQLUtil.formatKind(sFParams, "orderitems") +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups")+" ON (ordg_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON (prod_productid = ordi_productid) " +
						" WHERE ordg_orderid = orde_orderid " +
						whereProduct +
						whereExtra +
						whereProductFamily +
						whereProductGroup +
					" ) ";
		}
		sql += " ORDER by orde_orderid ASC";
		
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
			int currencyIdWhile = 0, x = 1, y = 0;					
			pmCurrencyWhile.doFetch(sqlCurrency);
			while (pmCurrencyWhile.next()) {
				if (pmCurrencyWhile.getInt("cure_currencyid") != currencyIdWhile) {
					currencyIdWhile = pmCurrencyWhile.getInt("cure_currencyid");
					currencyId = currencyIdWhile;
			    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
					y = 0;
					%>
				<tr>
					<td class="reportHeaderCellCenter"
						colspan="5"><%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
					</td>
				</tr>
				<tr class="">
					<th class="reportHeaderCellCenter" colspan="">Pedidos</th>
					<th>&nbsp;</th>
					<th class="reportHeaderCellCenter" colspan="">Cuentas por Cobrar</th>
					<th>&nbsp;</th>
					<th class="reportHeaderCellCenter" colspan="">Ordenes de Compra</th>
				</tr>
				<%
		        // variables de sumas totales
				//double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0, balanceTotal = 0;
		        
		        sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "orders") +     
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = orde_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries")+" ON(indu_industryid = cust_industryid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = orde_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) ";
				if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
					sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
				}
				sql += " WHERE orde_orderid > 0 " +
						" AND orde_currencyid = " + currencyId +
						where;
				if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
						sql += " AND orde_orderid IN ( " +
								" SELECT ordg_orderid  FROM " + SQLUtil.formatKind(sFParams, "orderitems") +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups")+" ON (ordg_orderid = orde_orderid) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON (prod_productid = ordi_productid) " +
								" WHERE ordg_orderid = orde_orderid " +
								whereProduct +
								whereExtra +
								whereProductFamily +
								whereProductGroup +
							" ) ";
				}
				sql += " ORDER by orde_orderid ASC";
				pmOrder.doFetch(sql); 
		
		        int i = 1;
		        while (pmOrder.next()) {
		        	
		        	// Datos de Pedidos
		// 	    	double amount = pmOrder.getDouble("orde_amount");
		// 	    	double discount = pmOrder.getDouble("orde_discount");
		// 	    	double tax = pmOrder.getDouble("orde_tax");				          	
		// 	    	double total = pmOrder.getDouble("orde_total");
		// 	    	double totalSinIva = pmOrder.getDouble("orde_total") - tax;	
		// 	    	double balance = pmOrder.getDouble("orde_balance");
			    	
		// 	    	//Conversion a la moneda destino(seleccion del filtro)
		// 	    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
		// 	    	double parityOrigin = 0, parityDestiny = 0;
		// 	    	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
		// 	    	parityOrigin = pmOrder.getDouble("orde_currencyparity");
		// 	    	currencyIdDestiny = currencyId;
		// 	    	parityDestiny = defaultParity;
		
		// 	    	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		// 	    	discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		// 	    	tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		// 	    	total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		// 	    	totalSinIva = pmCurrencyExchange.currencyExchange(totalSinIva, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		// 	    	balance = pmCurrencyExchange.currencyExchange(balance, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			    	
		// 	    	// Suma total
		// 	    	amountTotal += amount;
		// 	    	discountTotal += discount;
		// 	    	taxTotal += tax;
		// 	    	totalTotal += total;
		// 	    	totalSinIvaTotal += totalSinIva;
		// 	    	balanceTotal += balance;
		
		// 	    	if (pmOrder.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
		// 	    		customer = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");	
		//    			else
		//    				customer = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
		        	
		        	// Estatus
		//         	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
		//         	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
		//         	bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
		//         	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
		
					// Contar las cxc del pedido
					int countRowRacc = 0;
					String racc = "SELECT COUNT(*) AS countRowRacc " + 
								" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid) " +
			          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = racc_currencyid) " +
								" WHERE ract_type <> '" + BmoRaccountType.TYPE_DEPOSIT + "' " +
			          			" AND ract_category <> '" + BmoRaccountType.CATEGORY_OTHER + "' "+
								" AND racc_orderid = " + pmOrder.getInt("orders", "orde_orderid");
			
					pmConnRacc.doFetch(racc);
					
					// Suma linea de total
					if (pmConnRacc.next()) countRowRacc = (pmConnRacc.getInt("countRowRacc"));
					
					// Contar las oc del pedido
					int countRowReqi = 0;
					String reqi = "SELECT COUNT(*) AS countRowReqi " + 
								" FROM " + SQLUtil.formatKind(sFParams, "requisitions") +
		// 						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
		// 	          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = reqi_currencyid) " +
								" WHERE reqi_orderid = " + pmOrder.getInt("orders", "orde_orderid");
		
					pmConnReqi.doFetch(reqi);
					
					// Suma linea de total
					if (pmConnReqi.next()) countRowReqi = (pmConnReqi.getInt("countRowReqi") );
					
					// Comparar contadores racc vs reqi, y colocar el mas alto
					int countRow = 0;			
		// 			if (countRowRacc > countRowReqi) countRow = countRowRacc;
		// 			else countRow = countRowReqi;
		
		    	%>
					<tr class="reportCellEven">
						<td valign="top" align="left">
							<table style="width:100%; border-spacing: 0px;">
								<tr>
						    		<th class="reportHeaderCellCenter">#</th>
									<th class="reportHeaderCell">Clave</th>
									<th class="reportHeaderCell">Nombre</th>
								</tr>
								<tr>
									<td class="reportCellNumber" rowspan="<%= countRow +1%>"><%= i %></td>
									<td class="reportCellCode" rowspan="<%= countRow +1%>"><%= pmOrder.getString("orders", "orde_code") %></td>
									<td class="reportCellText" rowspan="<%= countRow +1%>"><%= pmOrder.getString("orders", "orde_name") %></td>
								</tr>
								
							</table>
						</td>
						<td rowspan="<%= countRow +1%>">&nbsp;</td>
						<%	if (countRowRacc > 0) { %>
								<td valign="top" align="left">
									<table style="width:100%; border-spacing: 0px;">
										<tr>
											<th class="reportHeaderCell">Clave</th>
											<th class="reportHeaderCell">Referencia</th>
											<th class="reportHeaderCellRight">Total</th>
											<th class="reportHeaderCellRight">Pagos</th>
											<th class="reportHeaderCellRight">Saldo</th>
										</tr>
										<%
										// ----- CXC -----
										racc = "SELECT racc_code, racc_reference, racc_currencyid, racc_currencyparity, racc_total, racc_payments, racc_balance " + 
												" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
												" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid) " +
							          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = racc_currencyid) " +
												" WHERE ract_type <> '" + BmoRaccountType.TYPE_DEPOSIT + "' " +
							          			" AND ract_category <> '" + BmoRaccountType.CATEGORY_OTHER + "' "+
												" AND racc_orderid = " + pmOrder.getInt("orders", "orde_orderid");
		
										pmConnRacc.doFetch(racc);
		
										double totalRaccSum = 0, paymentsRaccSum = 0, balanceRaccSum = 0;
										boolean first = true;
										while (pmConnRacc.next()) {
											
											//Conversion a la moneda destino(del filtro)
							          		int currencyIdOrigin = 0, currencyIdDestiny = 0;
							          		double parityOrigin = 0, parityDestiny = 0;
							          		currencyIdOrigin = pmConnRacc.getInt("racc_currencyid");
							          		parityOrigin = pmConnRacc.getDouble("racc_currencyparity");
							          		currencyIdDestiny = currencyId;
							          		parityDestiny = defaultParity;
							          		
							          		double totalRacc = pmConnRacc.getDouble("racc_total");
							          		double paymentsRacc = pmConnRacc.getDouble("racc_payments");
							          		double balanceRacc = pmConnRacc.getDouble("racc_balance");
						
							          		totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
							          		paymentsRacc = pmCurrencyExchange.currencyExchange(paymentsRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
							          		balanceRacc = pmCurrencyExchange.currencyExchange(balanceRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						
							          		// Suma general por pedido
							          		totalRaccSum += totalRacc;
							          		paymentsRaccSum += paymentsRacc;
							          		balanceRaccSum += balanceRacc;
							          		%>
											<tr class="reportCellEven">
												<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnRacc.getString("racc_code")), BmFieldType.CODE) %>
												<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnRacc.getString("racc_reference")), BmFieldType.STRING) %>
												<%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc, BmFieldType.CURRENCY) %>
												<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRacc, BmFieldType.CURRENCY) %>
												<%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
											</tr>
									<%	} // Fin while(pmConnRacc) %>
										<tr class="reportCellCode">
											<td class="reportCellText" colspan="2" >&nbsp;</td>
											<%= HtmlUtil.formatReportCell(sFParams, "" + totalRaccSum, BmFieldType.CURRENCY) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRaccSum, BmFieldType.CURRENCY) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + balanceRaccSum, BmFieldType.CURRENCY) %>
										</tr>
									
									</table>
								</td>
						<%	} else { %>
								<td colspan="">&nbsp;</td>
						<%	} %>
							<td rowspan="<%= countRow +1%>">&nbsp;</td>
						<%
							//  ----- OC -----
							if (countRowReqi > 0) { %>
								<td valign="top" align="right">
									<table style="width:100%; border-spacing: 0px;">
										<tr>
											<th class="reportHeaderCell">Clave</th>
											<th class="reportHeaderCell">Nombre</th>
											<th class="reportHeaderCellRight">Total</th>
											<th class="reportHeaderCellRight">Pagos</th>
											<th class="reportHeaderCellRight">Saldo</th>
										</tr>
										<%
										reqi = "SELECT reqi_requisitionid, reqi_code, reqi_name, reqi_currencyid, reqi_currencyparity, reqi_total, reqi_payments, reqi_balance " + 
											" FROM " + SQLUtil.formatKind(sFParams, "requisitions") +
					// 						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
					// 	          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = reqi_currencyid) " +
											" WHERE reqi_orderid = " + pmOrder.getInt("orders", "orde_orderid");
						
										pmConnReqi.doFetch(reqi);
					
										double totalReqiSum = 0, paymentsReqiSum = 0, balanceReqiSum = 0;
										boolean first = true;
										while (pmConnReqi.next()) {
											
											//Conversion a la moneda destino(del filtro)
							          		int currencyIdOrigin = 0, currencyIdDestiny = 0;
							          		double parityOrigin = 0, parityDestiny = 0;
							          		currencyIdOrigin = pmConnReqi.getInt("reqi_currencyid");
							          		parityOrigin = pmConnReqi.getDouble("reqi_currencyparity");
							          		currencyIdDestiny = currencyId;
							          		parityDestiny = defaultParity;
							          		
							          		double totalReqi = pmConnReqi.getDouble("reqi_total");
							          		double paymentsReqi = pmConnReqi.getDouble("reqi_payments");
							          		double balanceReqi = pmConnReqi.getDouble("reqi_balance");
						
							          		totalReqi = pmCurrencyExchange.currencyExchange(totalReqi, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
							          		paymentsReqi = pmCurrencyExchange.currencyExchange(paymentsReqi, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
							          		balanceReqi = pmCurrencyExchange.currencyExchange(balanceReqi, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						
							          		// Suma general por pedido
							          		totalReqiSum += totalReqi;
							          		paymentsReqiSum += paymentsReqi;
							          		balanceReqiSum += balanceReqi;
							          		
											%> 
											
											<tr class="reportCellEven">
												<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnReqi.getString("reqi_code")), BmFieldType.CODE) %>
												<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnReqi.getString("reqi_name")), BmFieldType.STRING) %>
												<%= HtmlUtil.formatReportCell(sFParams, "" + totalReqi, BmFieldType.CURRENCY) %>
												<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsReqi, BmFieldType.CURRENCY) %>
												<%= HtmlUtil.formatReportCell(sFParams, "" + balanceReqi, BmFieldType.CURRENCY) %>
											</tr>
										<%
										} // Fin while(pmConnReqi)
										%>
				
										<tr class="reportCellCode">
											<td  class="reportCellText" colspan="2">&nbsp;</td>
											<%= HtmlUtil.formatReportCell(sFParams, "" + totalReqiSum, BmFieldType.CURRENCY) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsReqiSum, BmFieldType.CURRENCY) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + balanceReqiSum, BmFieldType.CURRENCY) %>
										</tr>
									</table>
								</td>
						<%	} else { %>
								<td colspan="">&nbsp;</td>
						<%	} %>
					</tr>
					<tr><td colspan="5">&nbsp;</td></tr>
				<%
					i++;
		        } // Fin while pmOrder
				} %>	
			<%
			} // Fin pmCurrencyWhile
			%>
			<%
		 } 	// Fin de no existe moneda
		// Existe moneda destino
		else {
	%>
			<tr class="">
				<th class="reportHeaderCellCenter" colspan="">Pedidos</th>
				<th>&nbsp;</th>
				<th class="reportHeaderCellCenter" colspan="">Cuentas por Cobrar</th>
				<th>&nbsp;</th>
				<th class="reportHeaderCellCenter" colspan="">Ordenes de Compra</th>
			</tr>
			<%
	        // variables de sumas totales
			//double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0, balanceTotal = 0;
	        
	        sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "orders") +     
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = orde_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries")+" ON(indu_industryid = cust_industryid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = orde_userid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) ";
			if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
				sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
			}
			sql += " WHERE orde_orderid > 0 " +
					where;
			if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
					sql += " AND orde_orderid IN ( " +
							" SELECT ordg_orderid  FROM " + SQLUtil.formatKind(sFParams, "orderitems") +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups")+" ON (ordg_orderid = orde_orderid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON (prod_productid = ordi_productid) " +
							" WHERE ordg_orderid = orde_orderid " +
							whereProduct +
							whereExtra +
							whereProductFamily +
							whereProductGroup +
						" ) ";
			}
			sql += " ORDER by orde_orderid ASC";
			pmOrder.doFetch(sql); 
	
	        int i = 1;
	        while (pmOrder.next()) {
	        	
	        	// Datos de Pedidos
	// 	    	double amount = pmOrder.getDouble("orde_amount");
	// 	    	double discount = pmOrder.getDouble("orde_discount");
	// 	    	double tax = pmOrder.getDouble("orde_tax");				          	
	// 	    	double total = pmOrder.getDouble("orde_total");
	// 	    	double totalSinIva = pmOrder.getDouble("orde_total") - tax;	
	// 	    	double balance = pmOrder.getDouble("orde_balance");
		    	
	// 	    	//Conversion a la moneda destino(seleccion del filtro)
	// 	    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	// 	    	double parityOrigin = 0, parityDestiny = 0;
	// 	    	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
	// 	    	parityOrigin = pmOrder.getDouble("orde_currencyparity");
	// 	    	currencyIdDestiny = currencyId;
	// 	    	parityDestiny = defaultParity;
	
	// 	    	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	// 	    	discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	// 	    	tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	// 	    	total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	// 	    	totalSinIva = pmCurrencyExchange.currencyExchange(totalSinIva, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	// 	    	balance = pmCurrencyExchange.currencyExchange(balance, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		    	
	// 	    	// Suma total
	// 	    	amountTotal += amount;
	// 	    	discountTotal += discount;
	// 	    	taxTotal += tax;
	// 	    	totalTotal += total;
	// 	    	totalSinIvaTotal += totalSinIva;
	// 	    	balanceTotal += balance;
	
	// 	    	if (pmOrder.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	// 	    		customer = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");	
	//    			else
	//    				customer = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
	        	
	        	// Estatus
	//         	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
	//         	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
	//         	bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
	//         	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
	
				// Contar las cxc del pedido
				int countRowRacc = 0;
				String racc = "SELECT COUNT(*) AS countRowRacc " + 
							" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid) " +
		          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = racc_currencyid) " +
							" WHERE ract_type <> '" + BmoRaccountType.TYPE_DEPOSIT + "' " +
		          			" AND ract_category <> '" + BmoRaccountType.CATEGORY_OTHER + "' "+
							" AND racc_orderid = " + pmOrder.getInt("orders", "orde_orderid");
		
				pmConnRacc.doFetch(racc);
				
				// Suma linea de total
				if (pmConnRacc.next()) countRowRacc = (pmConnRacc.getInt("countRowRacc"));
				
				// Contar las oc del pedido
				int countRowReqi = 0;
				String reqi = "SELECT COUNT(*) AS countRowReqi " + 
							" FROM " + SQLUtil.formatKind(sFParams, "requisitions") +
	// 						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
	// 	          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = reqi_currencyid) " +
							" WHERE reqi_orderid = " + pmOrder.getInt("orders", "orde_orderid");
	
				pmConnReqi.doFetch(reqi);
				
				// Suma linea de total
				if (pmConnReqi.next()) countRowReqi = (pmConnReqi.getInt("countRowReqi") );
				
				// Comparar contadores racc vs reqi, y colocar el mas alto
				int countRow = 0;			
	// 			if (countRowRacc > countRowReqi) countRow = countRowRacc;
	// 			else countRow = countRowReqi;
	
	    	%>
				<tr class="reportCellEven">
					<td valign="top" align="left">
						<table style="width:100%; border-spacing: 0px;">
							<tr>
					    		<th class="reportHeaderCellCenter">#</th>
								<th class="reportHeaderCell">Clave</th>
								<th class="reportHeaderCell">Nombre</th>
							</tr>
							<tr>
								<td class="reportCellNumber" rowspan="<%= countRow +1%>"><%= i %></td>
								<td class="reportCellCode" rowspan="<%= countRow +1%>"><%= pmOrder.getString("orders", "orde_code") %></td>
								<td class="reportCellText" rowspan="<%= countRow +1%>"><%= pmOrder.getString("orders", "orde_name") %></td>
							</tr>
							
						</table>
					</td>
					<td rowspan="<%= countRow +1%>">&nbsp;</td>
					<%	if (countRowRacc > 0) { %>
							<td valign="top" align="left">
								<table style="width:100%; border-spacing: 0px;">
									<tr>
										<th class="reportHeaderCell">Clave</th>
										<th class="reportHeaderCell">Referencia</th>
										<th class="reportHeaderCellRight">Total</th>
										<th class="reportHeaderCellRight">Pagos</th>
										<th class="reportHeaderCellRight">Saldo</th>
									</tr>
									<%
									// ----- CXC -----
									racc = "SELECT racc_code, racc_reference, racc_currencyid, racc_currencyparity, racc_total, racc_payments, racc_balance " + 
											" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
											" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid) " +
						          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = racc_currencyid) " +
											" WHERE ract_type <> '" + BmoRaccountType.TYPE_DEPOSIT + "' " +
						          			" AND ract_category <> '" + BmoRaccountType.CATEGORY_OTHER + "' "+
											" AND racc_orderid = " + pmOrder.getInt("orders", "orde_orderid");
	
									pmConnRacc.doFetch(racc);
	
									double totalRaccSum = 0, paymentsRaccSum = 0, balanceRaccSum = 0;
									boolean first = true;
									while (pmConnRacc.next()) {
										
										//Conversion a la moneda destino(del filtro)
						          		int currencyIdOrigin = 0, currencyIdDestiny = 0;
						          		double parityOrigin = 0, parityDestiny = 0;
						          		currencyIdOrigin = pmConnRacc.getInt("racc_currencyid");
						          		parityOrigin = pmConnRacc.getDouble("racc_currencyparity");
						          		currencyIdDestiny = currencyId;
						          		parityDestiny = defaultParity;
						          		
						          		double totalRacc = pmConnRacc.getDouble("racc_total");
						          		double paymentsRacc = pmConnRacc.getDouble("racc_payments");
						          		double balanceRacc = pmConnRacc.getDouble("racc_balance");
					
						          		totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						          		paymentsRacc = pmCurrencyExchange.currencyExchange(paymentsRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						          		balanceRacc = pmCurrencyExchange.currencyExchange(balanceRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					
						          		// Suma general por pedido
						          		totalRaccSum += totalRacc;
						          		paymentsRaccSum += paymentsRacc;
						          		balanceRaccSum += balanceRacc;
						          		%>
										<tr class="reportCellEven">
											<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnRacc.getString("racc_code")), BmFieldType.CODE) %>
											<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnRacc.getString("racc_reference")), BmFieldType.STRING) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc, BmFieldType.CURRENCY) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRacc, BmFieldType.CURRENCY) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
										</tr>
								<%	} // Fin while(pmConnRacc) %>
									<tr class="reportCellCode">
										<td class="reportCellText" colspan="2" >&nbsp;</td>
										<%= HtmlUtil.formatReportCell(sFParams, "" + totalRaccSum, BmFieldType.CURRENCY) %>
										<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRaccSum, BmFieldType.CURRENCY) %>
										<%= HtmlUtil.formatReportCell(sFParams, "" + balanceRaccSum, BmFieldType.CURRENCY) %>
									</tr>
								
								</table>
							</td>
					<%	} else { %>
							<td colspan="">&nbsp;</td>
					<%	} %>
						<td rowspan="<%= countRow +1%>">&nbsp;</td>
					<%
						//  ----- OC -----
						if (countRowReqi > 0) { %>
							<td valign="top" align="right">
								<table style="width:100%; border-spacing: 0px;">
									<tr>
										<th class="reportHeaderCell">Clave</th>
										<th class="reportHeaderCell">Nombre</th>
										<th class="reportHeaderCellRight">Total</th>
										<th class="reportHeaderCellRight">Pagos</th>
										<th class="reportHeaderCellRight">Saldo</th>
									</tr>
									<%
									reqi = "SELECT reqi_requisitionid, reqi_code, reqi_name, reqi_currencyid, reqi_currencyparity, reqi_total, reqi_payments, reqi_balance " + 
										" FROM " + SQLUtil.formatKind(sFParams, "requisitions") +
				// 						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
				// 	          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = reqi_currencyid) " +
										" WHERE reqi_orderid = " + pmOrder.getInt("orders", "orde_orderid");
					
									pmConnReqi.doFetch(reqi);
				
									double totalReqiSum = 0, paymentsReqiSum = 0, balanceReqiSum = 0;
									boolean first = true;
									while (pmConnReqi.next()) {
										
										//Conversion a la moneda destino(del filtro)
						          		int currencyIdOrigin = 0, currencyIdDestiny = 0;
						          		double parityOrigin = 0, parityDestiny = 0;
						          		currencyIdOrigin = pmConnReqi.getInt("reqi_currencyid");
						          		parityOrigin = pmConnReqi.getDouble("reqi_currencyparity");
						          		currencyIdDestiny = currencyId;
						          		parityDestiny = defaultParity;
						          		
						          		double totalReqi = pmConnReqi.getDouble("reqi_total");
						          		double paymentsReqi = pmConnReqi.getDouble("reqi_payments");
						          		double balanceReqi = pmConnReqi.getDouble("reqi_balance");
					
						          		totalReqi = pmCurrencyExchange.currencyExchange(totalReqi, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						          		paymentsReqi = pmCurrencyExchange.currencyExchange(paymentsReqi, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						          		balanceReqi = pmCurrencyExchange.currencyExchange(balanceReqi, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					
						          		// Suma general por pedido
						          		totalReqiSum += totalReqi;
						          		paymentsReqiSum += paymentsReqi;
						          		balanceReqiSum += balanceReqi;
						          		
										%> 
										
										<tr class="reportCellEven">
											<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnReqi.getString("reqi_code")), BmFieldType.CODE) %>
											<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnReqi.getString("reqi_name")), BmFieldType.STRING) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + totalReqi, BmFieldType.CURRENCY) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsReqi, BmFieldType.CURRENCY) %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + balanceReqi, BmFieldType.CURRENCY) %>
										</tr>
									<%
									} // Fin while(pmConnReqi)
									%>
			
									<tr class="reportCellCode">
										<td  class="reportCellText" colspan="2">&nbsp;</td>
										<%= HtmlUtil.formatReportCell(sFParams, "" + totalReqiSum, BmFieldType.CURRENCY) %>
										<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsReqiSum, BmFieldType.CURRENCY) %>
										<%= HtmlUtil.formatReportCell(sFParams, "" + balanceReqiSum, BmFieldType.CURRENCY) %>
									</tr>
								</table>
							</td>
					<%	} else { %>
							<td colspan="">&nbsp;</td>
					<%	} %>
				</tr>
				<tr><td colspan="5">&nbsp;</td></tr>
			<%
				i++;
	        } // Fin while pmOrder
		} // Fin Existe moneda destino
%>
	</table>
	<%
		}// FIN DEL CONTADOR
	}// Fin de if(no carga datos)
	pmConnCount.close();
	pmCurrencyWhile.close();
	pmConnRacc.close();
	pmConnReqi.close();
	pmOrder.close();
%>
	<% 	if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% 	} 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	%>
</body>
</html>
