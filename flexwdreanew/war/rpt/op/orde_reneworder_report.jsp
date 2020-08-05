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
 
<%@page import="com.symgae.shared.SQLUtil"%>
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
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>

<%
    // Inicializar variables
    String title = "Reportes de Pedidos con Renovación Automática";
	BmoOrder bmoOrder = new BmoOrder();
	BmoOrder bmoOrderRenew = new BmoOrder();
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
    String filters = "", fullName = "", whereExtra="", whereProduct="", whereProductFamily = "", whereProductGroup = "", productFamilyId = "", productGroupId = "";
    int programId = 0, customerId = 0, cols= 0, areaId = 0, orderId = 0, industryId = 0, userId = 0, productId = 0, showProductExtra = 0, 
    		currencyId = 0 ,  wflowtypeId=0, dynamicColspan = 0, dynamicColspanMinus = 0, columnBudgets = 0, orderTypeId = 0, budgetId = 0, budgetItemId = 0;
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
							 " SELECT wflw_wflowid FROM wflowusers  " +
							 " LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
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
  		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM orders " +     
		" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
		" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
		" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
		" LEFT JOIN users ON(user_userid = orde_userid) " +
		" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
  		if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
				sqlCurrency += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
		}
  		sqlCurrency +=" WHERE orde_orderid > 0 " +
						" AND orde_reneworderid IS NULL " +
						where +
						" AND orde_willrenew = 1 ";
						// 		" AND orde_orderid IN ( " +
						// 				" SELECT ordg_orderid FROM orderitems " +
						// 				" LEFT JOIN products on (prod_productid = ordi_productid) " +
						// 				" LEFT JOIN ordergroups on (ordg_ordergroupid = ordi_ordergroupid) " +
						// 				" WHERE prod_reneworder = 1 " +
						// 				" ) ";
		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			sql += " AND orde_orderid IN ( " +
					" SELECT orde_orderid  FROM orders " +
					" LEFT JOIN ordergroups on (ordg_orderid = orde_orderid) " +
					" LEFT JOIN orderitems on (ordi_ordergroupid = ordg_ordergroupid) " +
					" LEFT JOIN products on (prod_productid = ordi_productid) " +
					" WHERE orde_orderid = orde_orderid " +
					whereProduct +
					whereExtra +
					whereProductFamily +
					whereProductGroup +
					" ) ";
		}
		sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";


  		
//   		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM orders " +     
//   				" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
//   				" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
//   				" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
//   				" LEFT JOIN users ON(user_userid = orde_userid) " +
//   				" LEFT JOIN areas ON(area_areaid = user_areaid) " +
//   				" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) " +
//   				" WHERE orde_orderid > 0 " +
//   				where;
// 		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
// 			sqlCurrency += " AND orde_orderid IN ( " +
// 					" SELECT orde_orderid  FROM orders " +
// 					" LEFT JOIN ordergroups on (ordg_orderid = orde_orderid) " +
// 					" LEFT JOIN orderitems on (ordi_ordergroupid = ordg_ordergroupid) " +
// 					" LEFT JOIN products on (prod_productid = ordi_productid) " +
// 					" WHERE orde_orderid = orde_orderid " +
// 					whereProduct +
// 					whereExtra +
// 					whereProductFamily +
// 					whereProductGroup +
// 					" ) ";
// 		}
// 	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
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
    
    PmConn pmConnOrdeGroup = new PmConn(sFParams);
   	pmConnOrdeGroup.open();
   
    PmConn pmConn = new PmConn(sFParams);
    pmConn.open();
	
	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();
	
	// colspan dinamico

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
        <td class="reportTitle" align="left" colspan="2">
            <%= title %>
        </td>
    </tr>
    <tr>
        <td class="reportSubTitle">
            <b>Filtros:</b> <%= filters %><br>
            <b>Ordenador por:</b> ID Cliente
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report" border="0" cellspacing="0" cellpading="0" width="100%">
	<%
	if (!(currencyId > 0)) {
		int currencyIdWhile = 0, x = 1;					
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			if (pmCurrencyWhile.getInt("cure_currencyid") != currencyIdWhile) {
				currencyIdWhile = pmCurrencyWhile.getInt("cure_currencyid");
				currencyId = currencyIdWhile;
		    	defaultParity = pmCurrencyWhile.getDouble("cure_parity");
				%>
				<tr>
					<td class="reportHeaderCellCenter" colspan="<%= 26 + dynamicColspan%>">
						<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
					</td>
				</tr>
				<tr class="">
					<td class="reportHeaderCellCenter">#</td>
				    <td class="reportHeaderCellCenter">Pedido Inicial</td>
		            <td class="reportHeaderCell">Clave</td>
		            <td class="reportHeaderCell">Tipo Pedido</td>
		            <td class="reportHeaderCell">Pedido</td>
		            <td class="reportHeaderCell">Cliente</td>
		            <td class="reportHeaderCell">Renovar?</td>
		            <td class="reportHeaderCell">Prod c/Renov.?</td>
		           	<td class="reportHeaderCell">Renovaci&oacute;n</td>
		            <td class="reportHeaderCell">Vendedor</td>
		            <td class="reportHeaderCell">Giro</td>
		            <% 	if (enableBudgets) { %>
				           	<td class="reportHeaderCell">Presupuesto</td>
				           	<td class="reportHeaderCell">Partida Presp.</td>
				           	<td class="reportHeaderCell">Departamento</td>
			         <%	} %>
		            <td class="reportHeaderCell">Estatus</td>
		            <td class="reportHeaderCell">Fecha Inicio</td>
		            <td class="reportHeaderCell">Fecha Fin</td>
		            <td class="reportHeaderCell">Apartado</td>
		            <td class="reportHeaderCell">Entrega</td>
		            <td class="reportHeaderCell">Pago</td>
		            <td class="reportHeaderCell">Moneda</td>
		            <%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
				    		<td class="reportHeaderCell">Cob. T.C.</td>
				    <% 	}%>
			 		<td class="reportHeaderCell">Tipo de Cambio</td>
		            <td class="reportHeaderCellRight">Subtotal</td>
			        <td class="reportHeaderCellRight">Descuentos</td>
		
			        <td class="reportHeaderCellRight">IVA</td>
		            <td class="reportHeaderCellRight">Total</td>
		        </tr>
				<%
				
				double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0;
		        
		        sql = " SELECT * FROM orders " +     
						" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
						" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
						" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
						" LEFT JOIN users ON(user_userid = orde_userid) " +
						" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
		        if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
		        	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
				}
		        sql += " WHERE orde_orderid > 0 " +
						" AND orde_reneworderid IS NULL " +
					   	" AND orde_currencyid =  " + currencyId +
						where +
						" AND orde_willrenew = 1 ";
// 						" AND orde_orderid IN ( " +
// 								" SELECT ordg_orderid FROM orderitems " +
// 								" LEFT JOIN products on (prod_productid = ordi_productid) " +
// 								" LEFT JOIN ordergroups on (ordg_ordergroupid = ordi_ordergroupid) " +
// 								" WHERE prod_reneworder = 1 " +
// 								" ) ";
				if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
					sql += " AND orde_orderid IN ( " +
							" SELECT orde_orderid  FROM orders " +
							" LEFT JOIN ordergroups on (ordg_orderid = orde_orderid) " +
							" LEFT JOIN orderitems on (ordi_ordergroupid = ordg_ordergroupid) " +
							" LEFT JOIN products on (prod_productid = ordi_productid) " +
							" WHERE orde_orderid = orde_orderid " +
							whereProduct +
							whereExtra +
							whereProductFamily +
							whereProductGroup +
							" ) ";
				}
				sql += " ORDER by cust_customerid ASC, orde_orderid ASC";
				
				pmOrder.doFetch(sql); 
		
		        double sumProdTotal = 0, sumEquiTotal = 0, sumStaffTotal = 0, sumOCTotal = 0, gastosTotal = 0;
		
		        int i = 1;
		        while (pmOrder.next()) {
		        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
		        	
		        	double sumProd = 0, sumEqui = 0, sumStaff = 0, sumOC = 0, gastos = 0;
		        	
			    	double amount = pmOrder.getDouble("orde_amount");
			    	double discount = pmOrder.getDouble("orde_discount");
			    	double tax = pmOrder.getDouble("orde_tax");				          	
			    	double total = pmOrder.getDouble("orde_total");
			    	double totalSinIva = pmOrder.getDouble("orde_total") - tax;				          						           		
		
			    	//Conversion a la moneda destino(seleccion del filtro)
			    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
			    	double parityOrigin = 0, parityDestiny = 0;
			    	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
			    	parityOrigin = pmOrder.getDouble("orde_currencyparity");
			    	currencyIdDestiny = currencyId;
			    	parityDestiny = defaultParity;
		
			    	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			    	discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			    	tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			    	total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			    	totalSinIva = pmCurrencyExchange.currencyExchange(totalSinIva, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		
			    	// Suma general
			    	amountTotal += amount;
			    	discountTotal += discount;
			    	taxTotal += tax;
			    	totalTotal += total;
			    	totalSinIvaTotal += totalSinIva;
		
			    	if (pmOrder.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
			    		fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");	
		   			else
		   				fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
		        	
		        	//Estatus
		        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
		        	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
		        	bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
		        	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
		    %>      
		    	<tr class="reportCellEven">
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_code"), BmFieldType.CODE) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_code"), BmFieldType.CODE) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("ordertypes", "ortp_name"), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_name"), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
		        	<%
		        		String renew = "No";
		        		if (pmOrder.getInt("orde_willrenew") == 1)
		        			renew = "Si";
		        	%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + renew, BmFieldType.STRING)) %>
					<%
					// Si el Pedido tiene productos con Renovacion
					String orderHasProductRenew =  "";
					int countOrderHasProductRenew = 0;
		
					sql = " SELECT COUNT(*) AS countOrderHasProductRenew FROM orders " +     
							" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
							" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
							" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
							" LEFT JOIN users ON(user_userid = orde_userid) " +
							" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
			        if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
						sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
					}
			        sql += " WHERE orde_orderid > 0 " +
			        		" AND orde_currencyid =  " + currencyId +
							" AND orde_orderid = " + pmOrder.getInt("orde_orderid") +
							where +
			 				" AND orde_orderid IN ( " +
			 						" SELECT ordg_orderid FROM orderitems " +
			 						" LEFT JOIN products on (prod_productid = ordi_productid) " +
			 						" LEFT JOIN ordergroups on (ordg_ordergroupid = ordi_ordergroupid) " +
			 						" WHERE prod_reneworder = 1 " +
			 						" ) ";
					if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
						sql += " AND orde_orderid IN ( " +
								" SELECT orde_orderid FROM orders " +
								" LEFT JOIN ordergroups ON (ordg_orderid = orde_orderid) " +
								" LEFT JOIN orderitems ON (ordi_ordergroupid = ordg_ordergroupid) " +
								" LEFT JOIN products ON (prod_productid = ordi_productid) " +
								" WHERE orde_orderid = orde_orderid " +
								whereProduct +
								whereExtra +
								whereProductFamily +
								whereProductGroup +
								" ) ";
					}
					sql += " ORDER by cust_customerid ASC, orde_orderid ASC";
					pmConn.doFetch(sql);
					if (pmConn.next()) countOrderHasProductRenew = pmConn.getInt("countOrderHasProductRenew");
						
					if (countOrderHasProductRenew > 0) 
						orderHasProductRenew = "Si";
					else 
						orderHasProductRenew = "No";
		
					%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + orderHasProductRenew, BmFieldType.STRING)) %>
					
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("users", "user_code"), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("industries", "indu_name"), BmFieldType.STRING)) %>
		        	<%	if (enableBudgets) { %>
		        			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("budgets", "budg_name"), BmFieldType.STRING)) %>
			        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
			        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("areas", "area_name"), BmFieldType.STRING)) %>
       				<%	} %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>                 
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockstart"), BmFieldType.DATETIME) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockend"), BmFieldType.DATETIME) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getLockStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getDeliveryStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("currencies", "cure_code"), BmFieldType.CODE) %>
		        	<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
				    		<%= HtmlUtil.formatReportCell(sFParams, (pmOrder.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
				    <% 	}%>
				    <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
		        		if (currencyIdOrigin != currencyIdDestiny) {
			    	%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
				    <%		
				    	} else { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmOrder.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
				    <%	}%>
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + discount, BmFieldType.CURRENCY) %>
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
					
					<%
		        	sql = " SELECT * FROM orders " +   
									//" LEFT JOIN orders orderRenew ON(orderRenew.orde_orderid = orderMain.orde_reneworderid) " +
									" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
									" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
									" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
									" LEFT JOIN users ON(user_userid = orde_userid) " +
									" LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) ";
					if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			        	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
					}
					sql += " WHERE orde_orderid > 0 " +
								where +
								" AND orde_originreneworderid = " + pmOrder.getInt("orde_orderid") +
								" ORDER by cust_customerid ASC, orde_orderid ASC";
									
							pmConnOrdeGroup.doFetch(sql);
							int y = 1;
							while (pmConnOrdeGroup.next()) {
					        	
						    	double amountOrderRenew = pmConnOrdeGroup.getDouble("orde_amount");
						    	double discountOrderRenew = pmConnOrdeGroup.getDouble("orde_discount");
						    	double taxOrderRenew = pmConnOrdeGroup.getDouble("orde_tax");				          	
						    	double totalOrderRenew = pmConnOrdeGroup.getDouble("orde_total");
						    	double totalSinIvaOrderRenew = pmConnOrdeGroup.getDouble("orde_total") - tax;				          						           		
					
						    	//Conversion a la moneda destino(del while)
						    	currencyIdOrigin = 0; currencyIdDestiny = 0;
						    	parityOrigin = 0; parityDestiny = 0;
						    	currencyIdOrigin = pmConnOrdeGroup.getInt("orde_currencyid");
						    	parityOrigin = pmConnOrdeGroup.getDouble("orde_currencyparity");
						    	currencyIdDestiny = currencyId;
						    	parityDestiny = defaultParity;
					
						    	amountOrderRenew = pmCurrencyExchange.currencyExchange(amountOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						    	discountOrderRenew = pmCurrencyExchange.currencyExchange(discountOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						    	taxOrderRenew = pmCurrencyExchange.currencyExchange(taxOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						    	totalOrderRenew = pmCurrencyExchange.currencyExchange(totalOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						    	totalSinIvaOrderRenew = pmCurrencyExchange.currencyExchange(totalSinIvaOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					
						    	// Suma general
						    	amountTotal += amountOrderRenew;
						    	discountTotal += discountOrderRenew;
						    	taxTotal += taxOrderRenew;
						    	totalTotal += totalOrderRenew;
						    	totalSinIvaTotal += totalSinIvaOrderRenew;
					
						    	if (pmConnOrdeGroup.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
						    		fullName = pmConnOrdeGroup.getString("cust_code") + " " + pmConnOrdeGroup.getString("cust_legalname");	
					   			else
					   				fullName = pmConnOrdeGroup.getString("cust_code") + " " + pmConnOrdeGroup.getString("cust_displayname");
					        	
					        	//Estatus
					        	bmoOrderRenew.getStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_status"));
					        	bmoOrderRenew.getDeliveryStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_deliverystatus"));              
					        	bmoOrderRenew.getLockStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_lockstatus"));
					        	bmoOrderRenew.getPaymentStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_paymentstatus"));
					
					        	
					    %>      
					    	<tr class="reportCellEven">
					    		<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
					    		<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_code"), BmFieldType.CODE) %>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("ordertypes", "ortp_name"), BmFieldType.STRING)) %>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_name"), BmFieldType.STRING)) %>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
					        	<%
					        		renew = "No";
					        		if (pmOrder.getInt("orde_willrenew") == 1)
					        			renew = "Si";
					        	%>
								<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + renew, BmFieldType.STRING)) %>
								<%
								// Si el Pedido tiene productos con Renovacion
								
								orderHasProductRenew =  "";
								countOrderHasProductRenew = 0;
					
								sql = " SELECT COUNT(*) AS countOrderHasProductRenew FROM orders " +     
										" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
										" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
										" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
										" LEFT JOIN users ON(user_userid = orde_userid) " +
										" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
						        if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
									sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
							    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
							    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
											" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
								}
						        sql +=" WHERE orde_orderid > 0 " +
						        		" AND orde_currencyid =  " + currencyId +
						        		" AND orde_orderid = " + pmConnOrdeGroup.getInt("orde_orderid") +
										where +
						 				" AND orde_orderid IN ( " +
						 						" SELECT ordg_orderid FROM orderitems " +
						 						" LEFT JOIN products ON (prod_productid = ordi_productid) " +
						 						" LEFT JOIN ordergroups ON (ordg_ordergroupid = ordi_ordergroupid) " +
						 						" WHERE prod_reneworder = 1 " +
						 						" ) ";
								if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
									sql += " AND orde_orderid IN ( " +
											" SELECT orde_orderid  FROM orders " +
											" LEFT JOIN ordergroups ON (ordg_orderid = orde_orderid) " +
											" LEFT JOIN orderitems ON (ordi_ordergroupid = ordg_ordergroupid) " +
											" LEFT JOIN products ON (prod_productid = ordi_productid) " +
											" WHERE orde_orderid = orde_orderid " +
											whereProduct +
											whereExtra +
											whereProductFamily +
											whereProductGroup +
											" ) ";
								}
								sql += " ORDER by cust_customerid ASC, orde_orderid ASC";
								pmConn.doFetch(sql);
								if (pmConn.next()) countOrderHasProductRenew = pmConn.getInt("countOrderHasProductRenew");
									
								if (countOrderHasProductRenew > 0) 
									orderHasProductRenew = "Si";
								else 
									orderHasProductRenew = "No";
					
								%>
								<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + orderHasProductRenew, BmFieldType.STRING)) %>
					        	<%
					        	sql = " SELECT orde_code FROM orders WHERE orde_orderid = " + pmConnOrdeGroup.getInt("orde_reneworderid");
					        	String orderRenewCode = "";
					        	pmConn.doFetch(sql);
					        	if (pmConn.next()) orderRenewCode = pmConn.getString("orde_code");
					        	%>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, orderRenewCode, BmFieldType.CODE)) %>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("users", "user_code"), BmFieldType.STRING)) %>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("industries", "indu_name"), BmFieldType.STRING)) %>
					        	<%	if (enableBudgets) { %>
					        			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("budgets", "budg_name"), BmFieldType.STRING)) %>
						        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
						        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("areas", "area_name"), BmFieldType.STRING)) %>
			       				<%	} %>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>                 
					        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_lockstart"), BmFieldType.DATETIME) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_lockend"), BmFieldType.DATETIME) %>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getLockStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getDeliveryStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
					        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("currencies", "cure_code"), BmFieldType.CODE) %>
					        	<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
							    		<%= HtmlUtil.formatReportCell(sFParams, (pmConnOrdeGroup.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
							    <% 	}%>
							    <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
				        		if (currencyIdOrigin != currencyIdDestiny) {
					    	%>
									<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
						    <%		
						    	} else { %>
									<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnOrdeGroup.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
						    <%	}%>
						    
						    	<%= HtmlUtil.formatReportCell(sFParams, "" + amountOrderRenew, BmFieldType.CURRENCY) %>
						    	<%= HtmlUtil.formatReportCell(sFParams, "" + discountOrderRenew, BmFieldType.CURRENCY) %>
						    	<%= HtmlUtil.formatReportCell(sFParams, "" + taxOrderRenew, BmFieldType.CURRENCY) %>
						    	<%= HtmlUtil.formatReportCell(sFParams, "" + totalOrderRenew, BmFieldType.CURRENCY) %>
					
					         </tr>
					         <% 
					         y++;
							} // Fin PmOrderGroup
		         i++;
		        } //pmOrder
		        %> 
		
		        <tr class="reportCellEven"><td colspan="<%= 26 + dynamicColspan%>">&nbsp;</td></tr>
		        
		        <tr class="reportCellEven reportCellCode">
			        	<td colspan="<%= (22 + dynamicColspan) - dynamicColspanMinus%>">&nbsp;</td >
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
		    	</tr>
				<%
			}
			%>
			
	    	<tr><td colspan="<%= 26 + dynamicColspan%>">&nbsp;</td></tr>

			<%
		} // Fin pmCurrencyWhile
		%>
		<%
	 } 	// Fin de no existe moneda
		// Existe moneda destino
	else {

		%>
		<tr class="">
			<td class="reportHeaderCellCenter">#</td>
		    <td class="reportHeaderCellCenter">Pedido Inicial</td>
            <td class="reportHeaderCell">Clave</td>
            <td class="reportHeaderCell">Tipo Pedido</td>
            <td class="reportHeaderCell">Pedido</td>
            <td class="reportHeaderCell">Cliente</td>
           	<td class="reportHeaderCell">Renovar?</td>
           	<td class="reportHeaderCell">Prod c/Renov.?</td>
            <td class="reportHeaderCell">Renovaci&oacute;n</td>
            <td class="reportHeaderCell">Vendedor</td>
            <td class="reportHeaderCell">Giro</td>
        <% 	if (enableBudgets) { %>
	           	<td class="reportHeaderCell">Presupuesto</td>
	           	<td class="reportHeaderCell">Partida Presp.</td>
	           	<td class="reportHeaderCell">Departamento</td>
         <%	} %>
            <td class="reportHeaderCell">Estatus</td>
            <td class="reportHeaderCell">Fecha Inicio</td>
            <td class="reportHeaderCell">Fecha Fin</td>
            <td class="reportHeaderCell">Apartado</td>
            <td class="reportHeaderCell">Entrega</td>
            <td class="reportHeaderCell">Pago</td>
            <td class="reportHeaderCell">Moneda</td>
            <%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
		    		<td class="reportHeaderCell">Cob. T.C.</td>
		    <% 	}%>
	 		<td class="reportHeaderCell">Tipo de Cambio</td>
            <td class="reportHeaderCellRight">Subtotal</td>
	        <td class="reportHeaderCellRight">Descuentos</td>

	        <td class="reportHeaderCellRight">IVA</td>
            <td class="reportHeaderCellRight">Total</td>
        </tr>
		<%
		
		double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0;
        
        sql = " SELECT * FROM orders " +     
				" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
				" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
				" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
				" LEFT JOIN users ON(user_userid = orde_userid) " +
				" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
        if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
		}
        sql +=" WHERE orde_orderid > 0 " +
				" AND orde_reneworderid IS NULL " +
				where +
				" AND orde_willrenew = 1 ";
// 				" AND orde_orderid IN ( " +
// 						" SELECT ordg_orderid FROM orderitems " +
// 						" LEFT JOIN products on (prod_productid = ordi_productid) " +
// 						" LEFT JOIN ordergroups on (ordg_ordergroupid = ordi_ordergroupid) " +
// 						" WHERE prod_reneworder = 1 " +
// 						" ) ";
		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			sql += " AND orde_orderid IN ( " +
					" SELECT orde_orderid  FROM orders " +
					" LEFT JOIN ordergroups on (ordg_orderid = orde_orderid) " +
					" LEFT JOIN orderitems on (ordi_ordergroupid = ordg_ordergroupid) " +
					" LEFT JOIN products on (prod_productid = ordi_productid) " +
					" WHERE orde_orderid = orde_orderid " +
					whereProduct +
					whereExtra +
					whereProductFamily +
					whereProductGroup +
					" ) ";
		}
		sql += " ORDER by cust_customerid ASC, orde_orderid ASC";
		
		pmOrder.doFetch(sql); 
		//double amountSum = 0, discountSum = 0, taxSum = 0, totalSum = 0, subtotalProductTotalSum = 0, orderTotalSinIvaSum = 0;

        double sumProdTotal = 0, sumEquiTotal = 0, sumStaffTotal = 0, sumOCTotal = 0, gastosTotal = 0;

        int i = 1;
        while (pmOrder.next()) {
        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
        	
        	double sumProd = 0, sumEqui = 0, sumStaff = 0, sumOC = 0, gastos = 0;
        	
	    	double amount = pmOrder.getDouble("orde_amount");
	    	double discount = pmOrder.getDouble("orde_discount");
	    	double tax = pmOrder.getDouble("orde_tax");				          	
	    	double total = pmOrder.getDouble("orde_total");
	    	double totalSinIva = pmOrder.getDouble("orde_total") - tax;				          						           		

	    	//Conversion a la moneda destino(seleccion del filtro)
	    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	    	double parityOrigin = 0, parityDestiny = 0;
	    	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
	    	parityOrigin = pmOrder.getDouble("orde_currencyparity");
	    	currencyIdDestiny = currencyId;
	    	parityDestiny = defaultParity;

	    	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	totalSinIva = pmCurrencyExchange.currencyExchange(totalSinIva, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);

	    	// Suma general
	    	amountTotal += amount;
	    	discountTotal += discount;
	    	taxTotal += tax;
	    	totalTotal += total;
	    	totalSinIvaTotal += totalSinIva;

	    	if (pmOrder.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	    		fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");	
   			else
   				fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
        	
        	//Estatus
        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
        	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
        	bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
        	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));

        	
    %>      
    	<tr class="reportCellEven">
        	<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_code"), BmFieldType.CODE) %>
        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_code"), BmFieldType.CODE) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("ordertypes", "ortp_name"), BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_name"), BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
        	<%
        		String renew = "No";
        		if (pmOrder.getInt("orde_willrenew") == 1)
        			renew = "Si";
        	%>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + renew, BmFieldType.STRING)) %>
			<%
			// Si el Pedido tiene productos con Renovacion
			String orderHasProductRenew =  "";
			int countOrderHasProductRenew = 0;

			sql = " SELECT COUNT(*) AS countOrderHasProductRenew FROM orders " +     
					" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
					" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
					" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
					" LEFT JOIN users ON(user_userid = orde_userid) " +
					" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
	        if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
				sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
			}
	        sql += " WHERE orde_orderid > 0 " +
					" AND orde_orderid = " + pmOrder.getInt("orde_orderid") +
					where +
	 				" AND orde_orderid IN ( " +
	 						" SELECT ordg_orderid FROM orderitems " +
	 						" LEFT JOIN products on (prod_productid = ordi_productid) " +
	 						" LEFT JOIN ordergroups on (ordg_ordergroupid = ordi_ordergroupid) " +
	 						" WHERE prod_reneworder = 1 " +
	 						" ) ";
			if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
				sql += " AND orde_orderid IN ( " +
						" SELECT orde_orderid FROM orders " +
						" LEFT JOIN ordergroups ON (ordg_orderid = orde_orderid) " +
						" LEFT JOIN orderitems ON (ordi_ordergroupid = ordg_ordergroupid) " +
						" LEFT JOIN products ON (prod_productid = ordi_productid) " +
						" WHERE orde_orderid = orde_orderid " +
						whereProduct +
						whereExtra +
						whereProductFamily +
						whereProductGroup +
						" ) ";
			}
			sql += " ORDER by cust_customerid ASC, orde_orderid ASC";
			pmConn.doFetch(sql);
			if (pmConn.next()) countOrderHasProductRenew = pmConn.getInt("countOrderHasProductRenew");
				
			if (countOrderHasProductRenew > 0) 
				orderHasProductRenew = "Si";
			else 
				orderHasProductRenew = "No";

			%>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + orderHasProductRenew, BmFieldType.STRING)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING)) %>
			
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("users", "user_code"), BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("industries", "indu_name"), BmFieldType.STRING)) %>
        	<%	if (enableBudgets) { %>
	        			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("budgets", "budg_name"), BmFieldType.STRING)) %>
		        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
		        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("areas", "area_name"), BmFieldType.STRING)) %>
       		<%	} %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>                 
        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockstart"), BmFieldType.DATETIME) %>
        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockend"), BmFieldType.DATETIME) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getLockStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getDeliveryStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("currencies", "cure_code"), BmFieldType.CODE) %>
        	<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
		    		<%= HtmlUtil.formatReportCell(sFParams, (pmOrder.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
		    <% 	}%>
		    <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
        		if (currencyIdOrigin != currencyIdDestiny) {
        			if (bmoCurrency.getCode().toString().equals("USD")) {
	    	%>
						<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
		    <%		} else { %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmOrder.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
		    <%		}
		    	} else { %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmOrder.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
		    <%	}%>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + discount, BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
			
			<%
        	sql = " SELECT * FROM orders " +   
							//" LEFT JOIN orders orderRenew ON(orderRenew.orde_orderid = orderMain.orde_reneworderid) " +
							" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
							" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
							" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
							" LEFT JOIN users ON(user_userid = orde_userid) " +
							" LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) ";
			if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
				sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
			}
			sql += " WHERE orde_orderid > 0 " +
						where +
						" AND orde_originreneworderid = " + pmOrder.getInt("orde_orderid") +
						" ORDER by cust_customerid ASC, orde_orderid ASC";
							
					pmConnOrdeGroup.doFetch(sql);
					int y = 1;
					while (pmConnOrdeGroup.next()) {
			        	
				    	double amountOrderRenew = pmConnOrdeGroup.getDouble("orde_amount");
				    	double discountOrderRenew = pmConnOrdeGroup.getDouble("orde_discount");
				    	double taxOrderRenew = pmConnOrdeGroup.getDouble("orde_tax");				          	
				    	double totalOrderRenew = pmConnOrdeGroup.getDouble("orde_total");
				    	double totalSinIvaOrderRenew = pmConnOrdeGroup.getDouble("orde_total") - tax;				          						           		
			
				    	//Conversion a la moneda destino(seleccion del filtro)
				    	currencyIdOrigin = 0; currencyIdDestiny = 0;
				    	parityOrigin = 0; parityDestiny = 0;
				    	currencyIdOrigin = pmConnOrdeGroup.getInt("orde_currencyid");
				    	parityOrigin = pmConnOrdeGroup.getDouble("orde_currencyparity");
				    	currencyIdDestiny = currencyId;
				    	parityDestiny = defaultParity;
			
				    	amountOrderRenew = pmCurrencyExchange.currencyExchange(amountOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	discountOrderRenew = pmCurrencyExchange.currencyExchange(discountOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	taxOrderRenew = pmCurrencyExchange.currencyExchange(taxOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	totalOrderRenew = pmCurrencyExchange.currencyExchange(totalOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	totalSinIvaOrderRenew = pmCurrencyExchange.currencyExchange(totalSinIvaOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			
				    	// Suma general
				    	amountTotal += amountOrderRenew;
				    	discountTotal += discountOrderRenew;
				    	taxTotal += taxOrderRenew;
				    	totalTotal += totalOrderRenew;
				    	totalSinIvaTotal += totalSinIvaOrderRenew;
			
				    	if (pmConnOrdeGroup.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
				    		fullName = pmConnOrdeGroup.getString("cust_code") + " " + pmConnOrdeGroup.getString("cust_legalname");	
			   			else
			   				fullName = pmConnOrdeGroup.getString("cust_code") + " " + pmConnOrdeGroup.getString("cust_displayname");
			        	
			        	//Estatus
			        	bmoOrderRenew.getStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_status"));
			        	bmoOrderRenew.getDeliveryStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_deliverystatus"));              
			        	bmoOrderRenew.getLockStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_lockstatus"));
			        	bmoOrderRenew.getPaymentStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_paymentstatus"));
        	
			    %>      
			    	<tr class="reportCellEven">
			    		<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
			    		<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_code"), BmFieldType.CODE) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("ordertypes", "ortp_name"), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_name"), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
			        	<%
			        		renew = "No";
			        		if (pmOrder.getInt("orde_willrenew") == 1)
			        			renew = "Si";
			        	%>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + renew, BmFieldType.STRING)) %>
						<%
						// Si el Pedido tiene productos con Renovacion
						
						orderHasProductRenew =  "";
						countOrderHasProductRenew = 0;
			
						sql = " SELECT COUNT(*) AS countOrderHasProductRenew FROM orders " +     
								" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
								" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
								" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
								" LEFT JOIN users ON(user_userid = orde_userid) " +
								" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
				        if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
							sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
					    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
					    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
									" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
						}
				        sql +=" WHERE orde_orderid > 0 " +
				        		" AND orde_orderid = " + pmConnOrdeGroup.getInt("orde_orderid") +
								where +
				 				" AND orde_orderid IN ( " +
				 						" SELECT ordg_orderid FROM orderitems " +
				 						" LEFT JOIN products ON (prod_productid = ordi_productid) " +
				 						" LEFT JOIN ordergroups ON (ordg_ordergroupid = ordi_ordergroupid) " +
				 						" WHERE prod_reneworder = 1 " +
				 						" ) ";
						if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
							sql += " AND orde_orderid IN ( " +
									" SELECT orde_orderid  FROM orders " +
									" LEFT JOIN ordergroups ON (ordg_orderid = orde_orderid) " +
									" LEFT JOIN orderitems ON (ordi_ordergroupid = ordg_ordergroupid) " +
									" LEFT JOIN products ON (prod_productid = ordi_productid) " +
									" WHERE orde_orderid = orde_orderid " +
									whereProduct +
									whereExtra +
									whereProductFamily +
									whereProductGroup +
									" ) ";
						}
						sql += " ORDER by cust_customerid ASC, orde_orderid ASC";
						pmConn.doFetch(sql);
						if (pmConn.next()) countOrderHasProductRenew = pmConn.getInt("countOrderHasProductRenew");
							
						if (countOrderHasProductRenew > 0) 
							orderHasProductRenew = "Si";
						else 
							orderHasProductRenew = "No";
			
						%>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + orderHasProductRenew, BmFieldType.STRING)) %>
			        	<%
			        	sql = " SELECT orde_code FROM orders WHERE orde_orderid = " + pmConnOrdeGroup.getInt("orde_reneworderid");
			        	String orderRenewCode = "";
			        	pmConn.doFetch(sql);
			        	if (pmConn.next()) orderRenewCode = pmConn.getString("orde_code");
			        	%>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, orderRenewCode, BmFieldType.CODE)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("users", "user_code"), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("industries", "indu_name"), BmFieldType.STRING)) %>
			        	<%	if (enableBudgets) { %>
			        			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("budgets", "budg_name"), BmFieldType.STRING)) %>
				        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
				        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("areas", "area_name"), BmFieldType.STRING)) %>
			       		<%	} %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>                 
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orderMain", "orde_lockstart"), BmFieldType.DATETIME) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orderMain", "orde_lockend"), BmFieldType.DATETIME) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getLockStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getDeliveryStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("currencies", "cure_code"), BmFieldType.CODE) %>
			        	<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
					    		<%= HtmlUtil.formatReportCell(sFParams, (pmConnOrdeGroup.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
					    <% 	}%>
					    <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
			        	if (currencyIdOrigin != currencyIdDestiny) {
			        		if (bmoCurrency.getCode().toString().equals("USD")) {
				    	%>
									<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
					    <%		} else { %>
									<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnOrdeGroup.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
					    <%		}
					    	} else { %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnOrdeGroup.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
					    <%	}%>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + amountOrderRenew, BmFieldType.CURRENCY) %>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + discountOrderRenew, BmFieldType.CURRENCY) %>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + taxOrderRenew, BmFieldType.CURRENCY) %>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + totalOrderRenew, BmFieldType.CURRENCY) %>
			
			         </tr>
			         <% 
			         y++;
					} // Fin PmOrderGroup
         i++;
        } //pmOrder
        %> 

        <tr class="reportCellEven"><td colspan="<%= 26 + dynamicColspan%>">&nbsp;</td></tr>
        
        <tr class="reportCellEven reportCellCode">
	        	<td colspan="<%= (22 + dynamicColspan) - dynamicColspanMinus%>">&nbsp;</td >
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
    	</tr>
<%			
	} // Fin Existe moneda destino
%>		
</table>		
<%		
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmConn.close();
	pmConnOrdeGroup.close();
   	pmOrder.close();
%>  
<% 	if (print.equals("1")) { %>
    	<script>
        	//window.print();
    	</script>
<% 	} %>
  </body>
</html>
