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
 
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
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
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes General de Pedidos";
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
    String filters = "", fullName = "", whereExtra="", whereProduct="", whereProductFamily = "", whereProductGroup = "", productFamilyId = "", productGroupId = "";
    int programId = 0, wflowtypeId=0, customerId = 0, cols= 0, orderId = 0, industryId = 0, userId = 0, productId = 0, showProductExtra = 0, 
    		currencyId = 0 ,dynamicColspan = 0, dynamicColspanMinus = 0, columnBudgets = 0, orderTypeId = 0, budgetId = -1, budgetItemId = -1, areaId = -1,wflowTypeId = 0;
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
//     if (request.getParameter("orde_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("orde_wflowtypeid"));
 
	bmoProgram = (BmoProgram)pmProgram.get(programId);
    
	
    // Filtros listados
    if (orderTypeId > 0) {
    	where += " AND orde_ordertypeid = " + orderTypeId;
    	filters += "<i>Tipo Pedido: </i>" + request.getParameter("orde_ordertypeidLabel") + ", ";
    }
    //Por tipo de flujo
    if (wflowTypeId > 0){
		where += " AND orde_wflowtypeid = " +wflowTypeId;
		filters +=  "<i>Tipo de Flujo: </i>" + request.getParameter("orde_wflowtypeidLabel");
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
   	
    if (wflowtypeId > 0) {
        where += " AND orde_wflowtypeid = " + wflowtypeId;
        filters += "<i>Tipo de Flujo: </i>" + request.getParameter("orde_wflowtypeidLabel") + ", ";
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
    
    PmConn pmConnOrdeGroup = new PmConn(sFParams);
   	pmConnOrdeGroup.open();
   
    PmConn pmConn = new PmConn(sFParams);
    pmConn.open();
    
    PmConn pmProduct = new PmConn(sFParams);
	pmProduct.open();
	
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
            <b>Filtros:</b> <%= filters %>
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report" border="0" cellspacing="0" cellpading="0" width="100%">
	<%

	sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " orders ") +     
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = orde_wflowtypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON(indu_industryid = cust_industryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) ";
	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = orde_defaultbudgetitemid)" +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" on (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (bgit_budgetid = budg_budgetid)" +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = orde_defaultareaid) ";
	}
	sql += " WHERE orde_orderid > 0 " +
			where;
	if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			sql += " AND orde_orderid IN ( " +
				" SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, " orders ") +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" on (ordg_orderid = orde_orderid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderitems")+" on (ordi_ordergroupid = ordg_ordergroupid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" on (prod_productid = ordi_productid) " +
				" WHERE orde_orderid = orde_orderid " +
				whereProduct +
				whereExtra +
				whereProductFamily +
				whereProductGroup +
				" ) ";
	}
	//" ORDER by orde_lockstart ASC, orde_orderid ASC";
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
					<td class="reportHeaderCellCenter" colspan="<%= 22 + columnBudgets + dynamicColspan%>">
						<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
					</td>
				</tr>
				<%
			}
			%>
			<tr class="">
	        	<td class="reportHeaderCellCenter">#</td>
	            <td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Tipo Pedido</td>
				<td class="reportHeaderCell">Tipo Flujo</td>
	            <td class="reportHeaderCell">Pedido</td>
	            <td class="reportHeaderCell">Cliente</td>
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
	        <%	if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) { %>
		    		<td class="reportHeaderCellRight">Subtotal Producto</td>
		    <%	} %>
	            <td class="reportHeaderCellRight">Subtotal</td>
		        <td class="reportHeaderCellRight">Descuentos</td>
	        <% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
			        <td class="reportHeaderCellRight">Total S/IVA</td>
	        <% 	} %>
		        <td class="reportHeaderCellRight">IVA</td>
	            <td class="reportHeaderCellRight">Total</td>
<%-- 	        <% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %> --%>
<!-- 	        		<td class="reportHeaderCellRight">Gastos</td> -->
<%-- 	    	<% 	} %> --%>
	    		<td class="reportHeaderCellRight">Saldo</td>
	    		<td class="reportHeaderCellRight">Total CxC</td>
	    		<td class="reportHeaderCellRight">Total CxC Externas</td>
	        </tr>
	        <%
	        
			double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0,
					sumRaccTotal = 0, sumRaccLinkedTotal = 0;;
	        
	        sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " orders ") +     
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON(indu_industryid = cust_industryid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) ";
	        if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
				sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (bgit_budgetid = budg_budgetid)" +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = orde_defaultareaid) ";
			}
	        sql +=" WHERE orde_orderid > 0 " +
		   			" AND orde_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
					where;
			if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
				sql += " AND orde_orderid IN ( " +
						" SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, " orders ") +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" on (ordg_orderid = orde_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderitems")+" on (ordi_ordergroupid = ordg_ordergroupid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" on (prod_productid = ordi_productid) " +
						" WHERE orde_orderid = orde_orderid " +
						whereProduct +
						whereExtra +
						whereProductFamily +
						whereProductGroup +
						" ) ";
			}
			//" ORDER by orde_lockstart ASC, orde_orderid ASC";
			sql += " ORDER by orde_orderid ASC";
			pmOrder.doFetch(sql); 
			//double amountSum = 0, discountSum = 0, taxSum = 0, totalSum = 0, subtotalProductTotalSum = 0, orderTotalSinIvaSum = 0;
	
	        double sumProdTotal = 0, sumEquiTotal = 0, sumStaffTotal = 0, sumOCTotal = 0, gastosTotal = 0,saldoTotal = 0;
	
	        int i = 0;
	        while (pmOrder.next()) {
	        	double sumProd = 0, sumEqui = 0, sumStaff = 0, sumOC = 0, gastos = 0;
	        	
		    	double amount = pmOrder.getDouble("orde_amount");
		    	double discount = pmOrder.getDouble("orde_discount");
		    	double tax = pmOrder.getDouble("orde_tax");				          	
		    	double total = pmOrder.getDouble("orde_total");
		    	double totalSinIva = pmOrder.getDouble("orde_total") - tax;	
		    	double balance = pmOrder.getDouble("orde_balance");
		    		
		    	// Conversion a la moneda destino(del Pedido)
		    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
		    	double parityOrigin = 0, parityDestiny = 0;
		    	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
		    	parityOrigin = pmOrder.getDouble("orde_currencyparity");
		    	currencyIdDestiny = currencyId;
		    	parityDestiny = defaultParity;
	
		    	// Suma general
		    	amountTotal += amount;
		    	discountTotal += discount;
		    	taxTotal += tax;
		    	totalTotal += total;
		    	totalSinIvaTotal += totalSinIva;
		    	saldoTotal += balance;
	
		    	if (pmOrder.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
		    		fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");	
	   			else
	   				fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
		    	
	        	
	        	//Estatus
	        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
	        	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
	        	bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
	        	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
	
	        	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) {
	        		// Costo Productos
	        		String sqlOrdeGroup = " SELECT ordg_ordergroupid FROM " + SQLUtil.formatKind(sFParams, " ordergroups")+" WHERE ordg_orderid = " + pmOrder.getInt("orde_orderid");
	        		pmConnOrdeGroup.doFetch(sqlOrdeGroup);
	        		sumProd = 0;
	        		//System.out.println("sqlOrdeGroup: "+sqlOrdeGroup);

	        		while (pmConnOrdeGroup.next()) {
	        			// COSTO PRODUCTOS
	        			// String sqlProd = " SELECT SUM(ordi_quantity * ordi_basecost * ordi_days) as sumItemsCostExtra FROM " + SQLUtil.formatKind(sFParams, " orderitems " +
	        			String sqlProd = " SELECT ordi_quantity, ordi_basecost, ordi_days FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
	        					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = ordi_productid) " +
	        					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordg_ordergroupid = ordi_ordergroupid) " +
	        					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordg_orderid) " +	      
	        					" WHERE orde_orderid = "+ pmOrder.getInt("orde_orderid") +
	        					" AND ordi_ordergroupid = " + pmConnOrdeGroup.getInt("ordg_ordergroupid") +
	        					" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "' " +
	        		   			" AND orde_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid");
	        			if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
	        				sqlProd += " AND orde_orderid IN ( " +
	        						" SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, " orders ") +
	        						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" on (ordg_orderid = orde_orderid) " +
	        						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderitems")+" on (ordi_ordergroupid = ordg_ordergroupid) " +
	        						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" on (prod_productid = ordi_productid) " +
	        						" WHERE orde_orderid = orde_orderid " +
	        						whereProduct +
	        						whereExtra +
	        						whereProductFamily +
	        						whereProductGroup +
	        						" ) ";
	        			}
	        			//System.out.println(" sqlProd2: "+sqlProd);
	        			pmConn.doFetch(sqlProd);
	        			while (pmConn.next()) {
	        				double costProd = 0;
	        				costProd = (pmConn.getDouble("ordi_quantity") * pmConn.getDouble("ordi_basecost")) * pmConn.getDouble("ordi_days");
	        				//costProd = pmCurrencyExchange.currencyExchange(costProd, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	        				sumProd += costProd;
	        			}
	        		} //Fin de pmConnOrdeGroup

	        		
	  			// COSTO EQUIPOS
	      		// String sqlEqui = " SELECT SUM(ordq_quantity * ordq_basecost * ordq_days) as sumEquipmentsExtra FROM " + SQLUtil.formatKind(sFParams, " orderequipments " +
	  			String sqlEqui = " SELECT ordq_quantity, ordq_basecost, ordq_days FROM " + SQLUtil.formatKind(sFParams, " orderequipments ") +
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON (equi_equipmentid = ordq_orderequipmentid) " +			      
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordq_orderid) " +			      
	  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
	  					" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
	  			pmConn.doFetch(sqlEqui);
	  			while (pmConn.next()) {
	  				double costEqui = 0;
	  				costEqui = (pmConn.getDouble("ordq_quantity") * pmConn.getDouble("ordq_basecost")) * pmConn.getDouble("ordq_days");
	  				//costEqui = pmCurrencyExchange.currencyExchange(costEqui, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					sumEqui += costEqui;
	  				//sumEqui = pmConn.getDouble("sumEquipmentsExtra");
	  			}
	  			//System.out.println("sqlEqui22: "+sqlEqui);
	  			
	  			// COSTOS DE STAFF
	  			// String sqlStaff = " SELECT SUM(ords_quantity * ords_basecost * ords_days) as sumItemStaffExtra " + 
	  			String sqlStaff = " SELECT ords_quantity, ords_basecost, ords_days FROM " + SQLUtil.formatKind(sFParams, " orderstaff ") +
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ords_orderid) " +		
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " profiles")+" ON (prof_profileid = ords_profileid) " +			      
	  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
	  					" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
	  			//System.out.println("sqlStaff222: "+sqlStaff);
	  			pmConn.doFetch(sqlStaff);
	  			while (pmConn.next()) {
	  				double costStaff = 0;
	  				costStaff = (pmConn.getDouble("ords_quantity") * pmConn.getDouble("ords_basecost")) * pmConn.getDouble("ords_days");
	  				//costStaff = pmCurrencyExchange.currencyExchange(costStaff, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	  				sumStaff += costStaff;
	  				//sumStaff = pmConn.getDouble("sumItemStaffExtra");
	  			}
	  			
	  			// ORDENES DE COMPRA
  				// IMPORTANTE: Si una OC está el USD cambiarla a la moneda del pedido
	  			// Obtener costos de OC ligados a un producto
	  			String sqlOC = "";
	  			if (pmOrder.getString("ortp_type").equals(""+BmoOrderType.TYPE_RENTAL))
	  				sqlOC = " SELECT rqit_quantity, prod_rentalcost, rqit_days, reqi_currencyid, reqi_currencyparity ";
	  			else
	  				sqlOC = " SELECT rqit_quantity, prod_cost, rqit_days ";
	
	  			sqlOC += " FROM " + SQLUtil.formatKind(sFParams, " requisitionitems ") +
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (reqi_requisitionid = rqit_requisitionid) " +
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = rqit_productid) " +
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = reqi_orderid) " +			     
	  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
	  					" AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
	  					" AND NOT rqit_productid IS NULL";
	  			//System.out.println("sqlOC: "+sqlOC);      
	  			pmConn.doFetch(sqlOC);
	  			while (pmConn.next()) {
	  				// Si una OC está el USD cambiarla a la moneda del pedido
	  				// Conversion a la moneda destino(del Pedido)
	  		    	currencyIdOrigin = 0;
	  		    	parityOrigin = 0;
	  		    	currencyIdOrigin = pmConn.getInt("reqi_currencyid");
	  		    	parityOrigin = pmConn.getDouble("reqi_currencyparity");
	  		    	currencyIdDestiny = currencyId;
	  		    	parityDestiny = defaultParity;
	  		    	
	  				double costProdReqi = 0;
	  	  			if (pmOrder.getString("ortp_type").equals(""+BmoOrderType.TYPE_RENTAL))
	  	  				costProdReqi = (pmConn.getDouble("rqit_quantity") * pmConn.getDouble("prod_rentalcost")) * pmConn.getDouble("rqit_days");
	  	  			else
	  	  				costProdReqi = (pmConn.getDouble("rqit_quantity") * pmConn.getDouble("prod_cost")) * pmConn.getDouble("rqit_days");
	  	  			
	  				costProdReqi = pmCurrencyExchange.currencyExchange(costProdReqi, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	  				sumOC += costProdReqi;
	  				//sumOC = pmConn.getDouble("sumReqiItemCost");
	  			}
	  			
	  			// Obtener costos de OC no ligados a un producto
	  			sqlOC = " SELECT reqi_code, rqit_quantity, rqit_amount, rqit_days, reqi_currencyid, reqi_currencyparity" +
	  					//sqlOC = " SELECT SUM(rqit_quantity * rqit_price * rqit_days) as sumReqiItemOCExtra " +
	  					"	FROM " + SQLUtil.formatKind(sFParams, " requisitionitems ") +
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (reqi_requisitionid = rqit_requisitionid) " +	
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = rqit_productid) " +
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = reqi_orderid) " +			     
	  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
	  					" AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
	  					" AND rqit_productid IS NULL";
	  			//System.out.println("sqlOC22: "+sqlOC);      
	  			pmConn.doFetch(sqlOC);
	  			while (pmConn.next()) {
	  				// Si una OC está el USD cambiarla a la moneda del pedido
	  				// Conversion a la moneda destino(de Pedido)
	  		    	currencyIdOrigin = 0;
	  		    	parityOrigin = 0;
	  		    	currencyIdOrigin = pmConn.getInt("reqi_currencyid");
	  		    	parityOrigin = pmConn.getDouble("reqi_currencyparity");
	  		    	currencyIdDestiny = currencyId;
	  		    	parityDestiny = defaultParity;
	  		    	
	  		    	double costProdReqiNot = 0;
	  		    	costProdReqiNot = pmConn.getDouble("rqit_amount");
	  		    	costProdReqiNot = pmCurrencyExchange.currencyExchange(costProdReqiNot, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	  				sumOC += costProdReqiNot;
	  				//sumOC += pmConn.getDouble("sumReqiItemOCExtra");
	  			}
	  			//System.out.println("sqlOC22: "+sqlOC);
	  			
	  			// Obtener descuentos de OC
	  			sqlOC = " SELECT reqi_discount, reqi_currencyid, reqi_currencyparity " +
	  					" FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
	  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = reqi_orderid) " +			     
	  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
	  					" AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'";
	  			pmConn.doFetch(sqlOC);
	  			while (pmConn.next()) {
	  				// Si una OC está el USD cambiarla a la moneda del pedido
	  				// Conversion a la moneda destino(del Pedido)
	  		    	currencyIdOrigin = 0; parityOrigin = 0;
	  		    	currencyIdOrigin = pmConn.getInt("reqi_currencyid");
	  		    	parityOrigin = pmConn.getDouble("reqi_currencyparity");
	  		    	currencyIdDestiny = currencyId;
	  		    	parityDestiny = defaultParity;
	  		    	
	  				double reqiDiscount = 0;
	  				reqiDiscount = pmConn.getDouble("reqi_discount");
	  				reqiDiscount = pmCurrencyExchange.currencyExchange(reqiDiscount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	  				sumOC -= reqiDiscount;
	  				//sumOC -= pmConn.getDouble("sumReqiDiscount");
	  			}
	  			//System.out.println("sqlOCComDiscount: "+sqlOC);
	
	//  			sumProdTotal += sumProd;
	//  			sumEquiTotal += sumEqui;
	//  			sumStaffTotal += sumStaff;
	//  			sumOCTotal += sumOC;

	  			gastos = (sumProd + sumEqui + sumStaff + sumOC); 
	  			gastosTotal += gastos;
	          }
	    %>      
	  
	      
	    	<tr class="reportCellEven">
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + (i +1), BmFieldType.NUMBER) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_code"), BmFieldType.CODE) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmOrder.getString("ordertypes", "ortp_name")), BmFieldType.STRING) %>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmOrder.getString("orders", "orde_name")), BmFieldType.STRING) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(fullName), BmFieldType.STRING) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("users", "user_code"), BmFieldType.STRING) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmOrder.getString("industries", "indu_name")), BmFieldType.STRING) %>
	        	<%	if (enableBudgets) { %>
	        			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("budgets", "budg_name"), BmFieldType.STRING)) %>
		        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
		        		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("areas", "area_name"), BmFieldType.STRING)) %>
        		<%	} %>
	        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>                 
	        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockstart"), BmFieldType.DATETIME) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockend"), BmFieldType.DATETIME) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getLockStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getDeliveryStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("currencies", "cure_code"), BmFieldType.CODE) %>
	        	<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
			    		<%= HtmlUtil.formatReportCell(sFParams, (pmOrder.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
			    <% 	}%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmOrder.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
			    <%
	        	if (productId > 0 || showProductExtra != 2  || !productFamilyId.equals("") || !productGroupId.equals("")) {
	        		sql = " SELECT prod_code, prod_name, prod_description,cust_code, cust_displayname, " +
	        				" orde_orderid, orde_code, orde_name, ordi_quantity, " +
	        				" SUM(ordi_amount) as amounTotalByProd " +
	        				" FROM " + SQLUtil.formatKind(sFParams, "orderitems") +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON(orde_orderid = ordg_orderid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (ortp_ordertypeid = orde_ordertypeid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON(prod_productid = ordi_productid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "productfamilies")+" ON(prfm_productfamilyid = prod_productfamilyid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "productgroups")+" ON(prgp_productgroupid = prod_productgroupid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" ON(supl_supplierid = prod_supplierid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = orde_customerid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = orde_userid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (wflw_wflowid = orde_wflowid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wfty_wflowtypeid = orde_wflowtypeid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) ";
	        				if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
	        					sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
	        			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
	        			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" +
	        							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = orde_defaultareaid) ";
	        				}
	        				sql = " WHERE ordi_orderitemid > 0 " +
	        				" AND orde_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
	    		   			" AND orde_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	        				whereProduct +
	        				where +
	        				whereExtra + 
	        				whereProductFamily +
	        				whereProductGroup +
	        				" GROUP BY orde_code, prod_code " +
	        				" ORDER BY prod_productid ASC "; 
	        		//System.out.println("sql: "+sql);
	        		pmProduct.doFetch(sql);
	        		double subtotalProductSum = 0;
	        		while (pmProduct.next()) {
	        			double subtotalProduct = pmProduct.getDouble("amounTotalByProd");
		            	//subtotalProduct = pmCurrencyExchange.currencyExchange(subtotalProduct, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		            	subtotalProductSum += subtotalProduct;
		            	subtotalProductTotal += subtotalProduct;
	        		}
	        		%>
	
	        		<%= HtmlUtil.formatReportCell(sFParams, "" + subtotalProductSum, BmFieldType.CURRENCY) %>
	    	<%	} %>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + discount, BmFieldType.CURRENCY) %>
		    	<%	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
		    			<%= HtmlUtil.formatReportCell(sFParams, "" + totalSinIva, BmFieldType.CURRENCY) %>
		    	<% 	} %>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
<%-- 		    	<% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %> --%>
<%-- 		    			<%= HtmlUtil.formatReportCell(sFParams, "" + gastos, BmFieldType.CURRENCY) %> --%>
<%-- 		    	<% 	} %> --%>
		    	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_balance"), BmFieldType.CURRENCY) %>
	        	<%
		    		// Total de CXC 
		    		double sumRacc = 0, sumRaccLinked = 0;
		    		sql = "SELECT racc_currencyid, racc_currencyparity, racc_total, racc_linked " + 
		    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
		    				"  LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid)"+
		    				" WHERE racc_orderid = " + pmOrder.getInt("orde_orderid")+ " AND ract_category <> '"+BmoRaccountType.CATEGORY_CREDITNOTE+"'";
		    		
		    		pmConn.doFetch(sql);
		    		while (pmConn.next()) {
		    			// Conversion a la moneda destino(seleccion del filtro)
		  		    	currencyIdOrigin = 0; currencyIdDestiny = 0;
		  		    	parityOrigin = 0; parityDestiny = 0;
		  		    	currencyIdOrigin = pmConn.getInt("racc_currencyid");
		  		    	parityOrigin = pmConn.getDouble("racc_currencyparity");
		  		    	currencyIdDestiny = currencyId;
		  		    	parityDestiny = defaultParity;
	
		  		    	double totalRacc = pmConn.getDouble("racc_total");
		  		    	totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		  		    	
		  		   		// Sumar cxc
		  		    	sumRacc += totalRacc;
		  		    	sumRaccTotal += totalRacc;
		  		    	
		  		   		// Sumar cxc externas
		  		    	if (pmConn.getInt("racc_linked") == 1) {
		  		    		sumRaccLinked += totalRacc;
		  		    		sumRaccLinkedTotal += totalRacc;
		  		    	}
		    		}
		    	%>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + sumRacc, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccLinked, BmFieldType.CURRENCY) %>
	         </tr>
	         <%
	         i++;
	        } //pmOrder
	        %> 
	
	        <tr class="reportCellEven"><td colspan="<%= 23 + columnBudgets + dynamicColspan%>">&nbsp;</td></tr>
	        
	        <tr class="reportCellEven reportCellCode">
		        	<td colspan="<%= (16 + columnBudgets + dynamicColspan) - dynamicColspanMinus%>">&nbsp;</td >
	        <%	if (productId > 0 || showProductExtra != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) { %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + subtotalProductTotal, BmFieldType.CURRENCY) %>
	    	<%	} %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
	    	<% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalSinIvaTotal, BmFieldType.CURRENCY) %>
	    	<% 	} %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
		
<%-- 	    	<% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %> --%>
<%-- 					<%= HtmlUtil.formatReportCell(sFParams, "" + gastosTotal, BmFieldType.CURRENCY) %> --%>
<%-- 			<%	} %> --%>
			
			<%= HtmlUtil.formatReportCell(sFParams, "" + saldoTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccLinkedTotal, BmFieldType.CURRENCY) %>
	    	</tr>
	    	<tr><td colspan="<%= 20 + columnBudgets + dynamicColspan%>">&nbsp;</td></tr>

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
            <td class="reportHeaderCell">Clave</td>
            <td class="reportHeaderCell">Tipo Pedido</td>
            <td class="reportHeaderCell">Tipo Flujo</td>
            <td class="reportHeaderCell">Pedido</td>
            <td class="reportHeaderCell">Cliente</td>
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
        <%	if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) { %>
	    		<td class="reportHeaderCellRight">Subtotal Producto</td>
	    <%	} %>
            <td class="reportHeaderCellRight">Subtotal</td>
	        <td class="reportHeaderCellRight">Descuentos</td>
        <% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
		        <td class="reportHeaderCellRight">Total S/IVA</td>
        <% 	} %>
	        <td class="reportHeaderCellRight">IVA</td>
            <td class="reportHeaderCellRight">Total</td>
<%--         <% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %> --%>
<!--         		<td class="reportHeaderCellRight">Gastos</td> -->
<%--     	<% 	} %> --%>
	    	<td class="reportHeaderCellRight">Saldo</td>
	    	<td class="reportHeaderCellRight">Total CxC</td>
	    	<td class="reportHeaderCellRight">Total CxC Externas</td>
        </tr>
        <%
        
		double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0;
        
        sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " orders ") +     
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = orde_wflowtypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON(indu_industryid = cust_industryid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) ";
		if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = orde_defaultbudgetitemid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" on (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (bgit_budgetid = budg_budgetid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = orde_defaultareaid) ";
		}
		sql += " WHERE orde_orderid > 0 " +
				where;
		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
				sql += " AND orde_orderid IN ( " +
					" SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, " orders ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" on (ordg_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderitems")+" on (ordi_ordergroupid = ordg_ordergroupid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" on (prod_productid = ordi_productid) " +
					" WHERE orde_orderid = orde_orderid " +
					whereProduct +
					whereExtra +
					whereProductFamily +
					whereProductGroup +
					" ) ";
		}
		//" ORDER by orde_lockstart ASC, orde_orderid ASC";
		sql += " ORDER by orde_orderid ASC";
		pmOrder.doFetch(sql); 
		//double amountSum = 0, discountSum = 0, taxSum = 0, totalSum = 0, subtotalProductTotalSum = 0, orderTotalSinIvaSum = 0;

        double sumProdTotal = 0, sumEquiTotal = 0, sumStaffTotal = 0, sumOCTotal = 0, gastosTotal = 0, saldoTotal = 0, 
        		sumRaccTotal = 0, sumRaccLinkedTotal = 0;

        int i = 0;
        while (pmOrder.next()) {
        	 double sumProd = 0, sumEqui = 0, sumStaff = 0, sumOC = 0, gastos = 0;
        	
	    	double amount = pmOrder.getDouble("orde_amount");
	    	double discount = pmOrder.getDouble("orde_discount");
	    	double tax = pmOrder.getDouble("orde_tax");				          	
	    	double total = pmOrder.getDouble("orde_total");
	    	double totalSinIva = pmOrder.getDouble("orde_total") - tax;	
	    	double balance = pmOrder.getDouble("orde_balance");
	    	
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
	    	balance = pmCurrencyExchange.currencyExchange(balance, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	
	    	// Suma general
	    	amountTotal += amount;
	    	discountTotal += discount;
	    	taxTotal += tax;
	    	totalTotal += total;
	    	totalSinIvaTotal += totalSinIva;
	    	saldoTotal += balance;

	    	if (pmOrder.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	    		fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");	
   			else
   				fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
        	
        	//Estatus
        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
        	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
        	bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
        	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));

        	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) {
        		//Costo Productos
        		String sqlOrdeGroup = " SELECT ordg_ordergroupid FROM " + SQLUtil.formatKind(sFParams, " ordergroups")+" WHERE ordg_orderid = " + pmOrder.getInt("orde_orderid");
        		pmConnOrdeGroup.doFetch(sqlOrdeGroup);
        		sumProd = 0;
        		//System.out.println("sqlOrdeGroup: "+sqlOrdeGroup);

        		while (pmConnOrdeGroup.next()) {
        			//COSTO PRODUCTOS
        			//String sqlProd = " SELECT SUM(ordi_quantity * ordi_basecost * ordi_days) as sumItemsCostExtra FROM " + SQLUtil.formatKind(sFParams, " orderitems " +
        			String sqlProd = " SELECT ordi_quantity, ordi_basecost, ordi_days FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
        					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = ordi_productid) " +
        					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordg_ordergroupid = ordi_ordergroupid) " +
        					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordg_orderid) " +	      
        					" WHERE orde_orderid = "+ pmOrder.getInt("orde_orderid") +
        					" AND ordi_ordergroupid = " + pmConnOrdeGroup.getInt("ordg_ordergroupid") +
        					" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "' ";
        			if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
        				sqlProd += " AND orde_orderid IN ( " +
        						" SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, " orders ") +
        						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" on (ordg_orderid = orde_orderid) " +
        						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderitems")+" on (ordi_ordergroupid = ordg_ordergroupid) " +
        						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" on (prod_productid = ordi_productid) " +
        						" WHERE orde_orderid = orde_orderid " +
        						whereProduct +
        						whereExtra +
        						whereProductFamily +
        						whereProductGroup +
        						" ) ";
        			}
        			//System.out.println(" sqlProd2: "+sqlProd);
        			pmConn.doFetch(sqlProd);
        			while (pmConn.next()) {
        				double costProd = 0;
        				costProd = (pmConn.getDouble("ordi_quantity") * pmConn.getDouble("ordi_basecost")) * pmConn.getDouble("ordi_days");
        				costProd = pmCurrencyExchange.currencyExchange(costProd, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
        				sumProd += costProd;
        			}
        		} //Fin de pmConnOrdeGroup

  			
  			//COSTO EQUIPOS
      		//String sqlEqui = " SELECT SUM(ordq_quantity * ordq_basecost * ordq_days) as sumEquipmentsExtra FROM " + SQLUtil.formatKind(sFParams, " orderequipments " +
  			String sqlEqui = " SELECT ordq_quantity, ordq_basecost, ordq_days FROM " + SQLUtil.formatKind(sFParams, " orderequipments ") +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON (equi_equipmentid = ordq_orderequipmentid) " +			      
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordq_orderid) " +			      
  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
  					" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
  			pmConn.doFetch(sqlEqui);
  			while (pmConn.next()) {
  				double costEqui = 0;
  				costEqui = (pmConn.getDouble("ordq_quantity") * pmConn.getDouble("ordq_basecost")) * pmConn.getDouble("ordq_days");
  				costEqui = pmCurrencyExchange.currencyExchange(costEqui, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				sumEqui += costEqui;
  				//sumEqui = pmConn.getDouble("sumEquipmentsExtra");
  			}
  			//System.out.println("sqlEqui22: "+sqlEqui);
  			
  			//COSTOS DE STAFF
  			//String sqlStaff = " SELECT SUM(ords_quantity * ords_basecost * ords_days) as sumItemStaffExtra " + 
  			String sqlStaff = " SELECT ords_quantity, ords_basecost, ords_days FROM " + SQLUtil.formatKind(sFParams, " orderstaff ") +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ords_orderid) " +		
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " profiles")+" ON (prof_profileid = ords_profileid) " +			      
  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
  					" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
  			//System.out.println("sqlStaff222: "+sqlStaff);
  			pmConn.doFetch(sqlStaff);
  			while (pmConn.next()) {
  				double costStaff = 0;
  				costStaff = (pmConn.getDouble("ords_quantity") * pmConn.getDouble("ords_basecost")) * pmConn.getDouble("ords_days");
  				costStaff = pmCurrencyExchange.currencyExchange(costStaff, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
  				sumStaff += costStaff;
  				//sumStaff = pmConn.getDouble("sumItemStaffExtra");
  			}
  			
  			//ORDENES DE COMPRA
  			//Obtener costos de OC ligados a un producto
  			String sqlOC = "";
  			if (pmOrder.getString("ortp_type").equals(""+BmoOrderType.TYPE_RENTAL))
  				sqlOC = " SELECT rqit_quantity, prod_rentalcost, rqit_days, reqi_currencyid, reqi_currencyparity ";
  			else
  				sqlOC = " SELECT rqit_quantity, prod_cost, rqit_days ";

  			sqlOC += " FROM " + SQLUtil.formatKind(sFParams, " requisitionitems ") +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (reqi_requisitionid = rqit_requisitionid) " +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = rqit_productid) " +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = reqi_orderid) " +			     
  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
  					" AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
  					" AND NOT rqit_productid IS NULL";
  			//System.out.println("sqlOC: "+sqlOC);      
  			pmConn.doFetch(sqlOC);
  			while (pmConn.next()) {
  				//Conversion a la moneda destino(seleccion del filtro)
  		    	currencyIdOrigin = 0; currencyIdDestiny = 0;
  		    	parityOrigin = 0; parityDestiny = 0;
  		    	currencyIdOrigin = pmConn.getInt("reqi_currencyid");
  		    	parityOrigin = pmConn.getDouble("reqi_currencyparity");
  		    	currencyIdDestiny = currencyId;
  		    	parityDestiny = defaultParity;
  		    	
  				double costProdReqi = 0;
  	  			if (pmOrder.getString("ortp_type").equals(""+BmoOrderType.TYPE_RENTAL))
  	  				costProdReqi = (pmConn.getDouble("rqit_quantity") * pmConn.getDouble("prod_rentalcost")) * pmConn.getDouble("rqit_days");
  	  			else
  	  				costProdReqi = (pmConn.getDouble("rqit_quantity") * pmConn.getDouble("prod_cost")) * pmConn.getDouble("rqit_days");
  	  			
  				costProdReqi = pmCurrencyExchange.currencyExchange(costProdReqi, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
  				sumOC += costProdReqi;
  				//sumOC = pmConn.getDouble("sumReqiItemCost");
  			}
  			
  			//Obtener costos de OC no ligados a un producto
  			sqlOC = " SELECT rqit_quantity, rqit_amount, rqit_days, reqi_currencyid, reqi_currencyparity" +
  					//sqlOC = " SELECT SUM(rqit_quantity * rqit_price * rqit_days) as sumReqiItemOCExtra " +
  					"	FROM " + SQLUtil.formatKind(sFParams, " requisitionitems ") +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (reqi_requisitionid = rqit_requisitionid) " +	
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = rqit_productid) " +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = reqi_orderid) " +			     
  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
  					" AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
  					" AND rqit_productid IS NULL";
  			//System.out.println("sqlOC22: "+sqlOC);      
  			pmConn.doFetch(sqlOC);
  			while (pmConn.next()) {
  				// Conversion a la moneda destino(seleccion del filtro)
  		    	currencyIdOrigin = 0; currencyIdDestiny = 0;
  		    	parityOrigin = 0; parityDestiny = 0;
  		    	currencyIdOrigin = pmConn.getInt("reqi_currencyid");
  		    	parityOrigin = pmConn.getDouble("reqi_currencyparity");
  		    	currencyIdDestiny = currencyId;
  		    	parityDestiny = defaultParity;
  		    	
  		    	double costProdReqiNot = 0;
  		    	costProdReqiNot = pmConn.getDouble("rqit_amount");
  		    	costProdReqiNot = pmCurrencyExchange.currencyExchange(costProdReqiNot, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);

  				sumOC += costProdReqiNot;
  				//sumOC += pmConn.getDouble("sumReqiItemOCExtra");
  			}
  			//System.out.println("sqlOC22: "+sqlOC);
  			
  			//Obtener descuentos de OC
  			sqlOC = " SELECT reqi_discount, reqi_currencyid, reqi_currencyparity " +
  					" FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = reqi_orderid) " +			     
  					" WHERE orde_orderid = " + pmOrder.getInt("orde_orderid") +
  					" AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'";
  			pmConn.doFetch(sqlOC);
  			while (pmConn.next()) {
  			// Conversion a la moneda destino(seleccion del filtro)
  		    	currencyIdOrigin = 0; currencyIdDestiny = 0;
  		    	parityOrigin = 0; parityDestiny = 0;
  		    	currencyIdOrigin = pmConn.getInt("reqi_currencyid");
  		    	parityOrigin = pmConn.getDouble("reqi_currencyparity");
  		    	currencyIdDestiny = currencyId;
  		    	parityDestiny = defaultParity;
  		    	
  				double reqiDiscount = 0;
  				reqiDiscount = pmConn.getDouble("reqi_discount");
  				reqiDiscount = pmCurrencyExchange.currencyExchange(reqiDiscount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
  				sumOC -= reqiDiscount;
  				//sumOC -= pmConn.getDouble("sumReqiDiscount");
  			}
  			//System.out.println("sqlOCComDiscount: "+sqlOC);

//  			sumProdTotal += sumProd;
//  			sumEquiTotal += sumEqui;
//  			sumStaffTotal += sumStaff;
//  			sumOCTotal += sumOC;

  			gastos = (sumProd + sumEqui + sumStaff + sumOC); 
  			gastosTotal += gastos;
          }
    %>      
    
    	<tr class="reportCellEven">
        	<%= HtmlUtil.formatReportCell(sFParams, "" + (i +1), BmFieldType.NUMBER) %>
        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_code"), BmFieldType.CODE) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("ordertypes", "ortp_name"), BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_name"), BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("users", "user_code"), BmFieldType.STRING)) %>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("industries", "indu_name"), BmFieldType.STRING)) %>
        	<%
        		if (enableBudgets) { %>
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
		    <%
        	if (productId > 0 || showProductExtra != 2  || !productFamilyId.equals("") || !productGroupId.equals("")) {
        		sql = " SELECT prod_code, prod_name, prod_description,cust_code, cust_displayname, " +
        				" orde_orderid, orde_code, orde_name, ordi_quantity, " +
        				"SUM(ordi_amount) as amounTotalByProd " +
        				" FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordg_orderid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" on (ortp_ordertypeid = orde_ordertypeid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = ordi_productid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" ON(prfm_productfamilyid = prod_productfamilyid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON(prgp_productgroupid = prod_productgroupid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = prod_supplierid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflw_wflowid = orde_wflowid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" on (wfty_wflowtypeid = orde_wflowtypeid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowphases")+" on (wfph_wflowphaseid = wflw_wflowphaseid) ";
        				if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
        					sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = orde_defaultbudgetitemid)" +
        			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" on (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
        			    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (bgit_budgetid = budg_budgetid)" +
        							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = orde_defaultareaid) ";
        				}
        				sql += " WHERE ordi_orderitemid > 0 " +
        				" AND orde_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
        				whereProduct +
        				where +
        				whereExtra + 
        				whereProductFamily +
        				whereProductGroup +
        				" GROUP BY orde_code, prod_code " +
        				" ORDER BY prod_productid ASC"; 
        		//System.out.println("sql: "+sql);
        		pmProduct.doFetch(sql);
        		double subtotalProductSum = 0;
        		while (pmProduct.next()) {
        			double subtotalProduct = pmProduct.getDouble("amounTotalByProd");
	            	subtotalProduct = pmCurrencyExchange.currencyExchange(subtotalProduct, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	            	subtotalProductSum += subtotalProduct;
	            	subtotalProductTotal += subtotalProduct;
        			//subtotalProductTotalSum += pmProduct.getDouble("amounTotalByProd");;
        		}
        		%>
        		<%= HtmlUtil.formatReportCell(sFParams, "" + subtotalProductSum, BmFieldType.CURRENCY) %>
    	<%	} %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + discount, BmFieldType.CURRENCY) %>
	    	<%	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + totalSinIva, BmFieldType.CURRENCY) %>
	    	<% 	} %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
<%-- 	    	<% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %> --%>
<%-- 	    			<%= HtmlUtil.formatReportCell(sFParams, "" + gastos, BmFieldType.CURRENCY) %> --%>
<%-- 	    	<% 	} %> --%>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>
	    	<%
	    		// Total de CXC 
	    		double sumRacc = 0, sumRaccLinked = 0;
	    		sql = "SELECT racc_currencyid, racc_currencyparity, racc_total, racc_linked " + 
    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
    				"  LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid)"+
    				" WHERE racc_orderid = " + pmOrder.getInt("orde_orderid")+ " AND ract_category <> '"+BmoRaccountType.CATEGORY_CREDITNOTE+"'";
    		
	    		pmConn.doFetch(sql);
	    		while (pmConn.next()) {
	    			// Conversion a la moneda destino(seleccion del filtro)
	  		    	currencyIdOrigin = 0; currencyIdDestiny = 0;
	  		    	parityOrigin = 0; parityDestiny = 0;
	  		    	currencyIdOrigin = pmConn.getInt("racc_currencyid");
	  		    	parityOrigin = pmConn.getDouble("racc_currencyparity");
	  		    	currencyIdDestiny = currencyId;
	  		    	parityDestiny = defaultParity;

	  		    	double totalRacc = pmConn.getDouble("racc_total");
	  		    	totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	  		    	
	  		   		// Sumar cxc
	  		    	sumRacc += totalRacc;
	  		    	sumRaccTotal += totalRacc;
	  		    	
	  		   		// Sumar cxc externas
	  		    	if (pmConn.getInt("racc_linked") == 1) {
	  		    		sumRaccLinked += totalRacc;
	  		    		sumRaccLinkedTotal += totalRacc;
	  		    	}
	    		}
	    	%>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + sumRacc, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccLinked, BmFieldType.CURRENCY)%>
         </tr>
         <%
         i++;
        } //pmOrder
        %> 

        <tr class="reportCellEven"><td colspan="<%= 23 + columnBudgets + dynamicColspan%>">&nbsp;</td></tr>
        
        <tr class="reportCellEven reportCellCode">
	        	<td colspan="<%= (16 + columnBudgets + dynamicColspan) - dynamicColspanMinus%>">&nbsp;</td >
        <%	if (productId > 0 || showProductExtra != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) { %>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + subtotalProductTotal, BmFieldType.CURRENCY) %>
    	<%	} %>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
    	<% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + totalSinIvaTotal, BmFieldType.CURRENCY) %>
    	<% 	} %>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
	
<%--     	<% 	if (bmoOrderType.getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %> --%>
<%-- 				<%= HtmlUtil.formatReportCell(sFParams, "" + gastosTotal, BmFieldType.CURRENCY) %> --%>
<%-- <%			} %> --%>
			<%= HtmlUtil.formatReportCell(sFParams, "" + saldoTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccLinkedTotal, BmFieldType.CURRENCY) %>
    	</tr>
<%			
	} // Fin Existe moneda destino
%>		
</table>		
<%		
	}// FIN DEL CONTADOR

	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmConn.close();
	pmConnOrdeGroup.close();
   	pmOrder.close();
   	pmProduct.close();
	pmConnCount.close();
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
