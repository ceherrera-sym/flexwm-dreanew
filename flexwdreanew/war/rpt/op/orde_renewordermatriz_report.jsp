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
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp"%>
<%@page import="com.symgae.shared.SQLUtil"%>

<%
	// Inicializar variables
	String title = "Reportes de Matriz de Renovación de Pedidos";
	BmoOrder bmoOrder = new BmoOrder();
	BmoOrder bmoOrderRenew = new BmoOrder();
	BmoWFlow bmoWFlow = new BmoWFlow();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	PmCurrency pmCurrencyExchange = new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram = new PmProgram(sFParams);
	
	String sql = "", where = "",whereOrderId = "", sqlCurrency = "";
	String ordeStatus = "", paymentStatus = "", deliveryStatus = "", lockStatus = "", lockStartDate = "",
			lockEndDate = "";
	String filters = "", customer = "", whereExtra = "", whereProduct = "", whereProductFamily = "",wherelockstart = "",wherelockEnd = "",
			whereProductGroup = "", productFamilyId = "", productGroupId = "";
	int programId = 0, customerId = 0,   wflowtypeId=0, cols = 0, orderId = 0, industryId = 0, userId = 0,
			productId = 0, showProductExtra = 0, currencyId = 0, dynamicColspan = 0, dynamicColspanMinus = 0,orderTypeId = 0,
			budgetId = -1,budgetItemId = -1, areaId = -1;
	double nowParity = 0, defaultParity = 0;
	
	boolean enableBudgets = false;
	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
   	}

	// Obtener parametros       
	if (request.getParameter("programId") != null)
		programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("orde_orderid") != null)
		orderId = Integer.parseInt(request.getParameter("orde_orderid"));
	if (request.getParameter("orde_customerid") != null)
		customerId = Integer.parseInt(request.getParameter("orde_customerid"));
	if (request.getParameter("orde_status") != null)
		ordeStatus = request.getParameter("orde_status");
	if (request.getParameter("orde_lockstatus") != null)
		lockStatus = request.getParameter("orde_lockstatus");
	if (request.getParameter("orde_deliverystatus") != null)
		deliveryStatus = request.getParameter("orde_deliverystatus");
	if (request.getParameter("orde_paymentstatus") != null)
		paymentStatus = request.getParameter("orde_paymentstatus");
	if (request.getParameter("orde_lockstart") != null)
		lockStartDate = request.getParameter("orde_lockstart");
	if (request.getParameter("orde_lockend") != null)
		lockEndDate = request.getParameter("orde_lockend");
	if (request.getParameter("cust_industryid") != null)
		industryId = Integer.parseInt(request.getParameter("cust_industryid"));
	if (request.getParameter("orde_userid") != null)
		userId = Integer.parseInt(request.getParameter("orde_userid"));
	if (request.getParameter("prod_productid") != null)
		productId = Integer.parseInt(request.getParameter("prod_productid"));
	if (request.getParameter("prod_productfamilyid") != null)
		productFamilyId = request.getParameter("prod_productfamilyid");
	if (request.getParameter("prod_productgroupid") != null)
		productGroupId = request.getParameter("prod_productgroupid");
	if (request.getParameter("showProductExtra") != null)
		showProductExtra = Integer.parseInt(request.getParameter("showProductExtra"));
	if (request.getParameter("orde_currencyid") != null)
		currencyId = Integer.parseInt(request.getParameter("orde_currencyid"));
	if (request.getParameter("orde_ordertypeid") != null)
		orderTypeId = Integer.parseInt(request.getParameter("orde_ordertypeid"));
	if (request.getParameter("bgit_budgetid") != null) 
		budgetId = Integer.parseInt(request.getParameter("bgit_budgetid"));
	if (request.getParameter("orde_defaultbudgetitemid") != null) 
		budgetItemId = Integer.parseInt(request.getParameter("orde_defaultbudgetitemid"));    
    if (request.getParameter("orde_defaultareaid") != null) 
    	areaId = Integer.parseInt(request.getParameter("orde_defaultareaid"));    
    if (request.getParameter("orde_wflowtypeid") != null)
		wflowtypeId = Integer.parseInt(request.getParameter("orde_wflowtypeid"));
    
	bmoProgram = (BmoProgram) pmProgram.get(programId);

	// Filtros listados
	if (orderTypeId > 0) {
		where += " AND orde_ordertypeid like '" + orderTypeId + "'";
		bmoOrderType = (BmoOrderType) pmOrderType.get(orderTypeId);
		filters += "<i>Tipo Pedido: </i>" + bmoOrderType.getName().toString() + ", ";
	}
	
	if (orderId > 0) {
		whereOrderId += " AND orde_orderid = " + orderId;
		filters += "<i>Pedido: </i>" + request.getParameter("orde_orderidLabel") + ", ";
	}

	if (userId > 0) {
		//if (sFParams.restrictData(bmoOrder.getProgramCode())) {
			where += " AND orde_userid = " + userId;
		/*} else {
			where += " AND ( " + " orde_userid = " + userId + " OR orde_wflowid IN ( "
					+ " SELECT wflw_wflowid FROM wflowusers  "
					+ " LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " + " WHERE wflu_userid = " + userId
					+ " AND wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "' " + " ) " + " )";
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
	
	if (!lockStartDate.equals("")) {
		wherelockstart += " AND orde_lockstart >= '" + lockStartDate + "' ";
		filters += "<i>Apartado Inicio: </i>" + lockStartDate + ", ";
	}

	if (!lockEndDate.equals("")) {
		wherelockEnd += " AND orde_lockend <= '" + lockEndDate + "' ";
		filters += "<i>Apartado Final: </i>" + lockEndDate + ", ";
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
	
	if (!ordeStatus.equals("")) {
		//where += " AND orde_status like '" + status + "'";
		where += SFServerUtil.parseFiltersToSql("orde_status", ordeStatus);
		filters += "<i>Estatus: </i>" + request.getParameter("orde_statusLabel") + ", ";
	}
	
	if (showProductExtra != 2) {
		if (showProductExtra == 1)
			whereExtra += " AND ordi_productid IS NULL ";
		else
			whereExtra += " AND ordi_productid > 0 ";
		filters += "<i>Mostrar Items: </i>" + request.getParameter("showProductExtraLabel") + ", ";
	} else {
		filters += "<i>Mostrar Items: </i> Todos, ";
	}
	
	if (productId > 0) {
		whereProduct = " AND ordi_productid = " + productId;
		filters += "<i>Producto: </i>" + request.getParameter("prod_productidLabel") + ", ";
	}
	
	if (!productFamilyId.equals("")) {
		whereProductFamily += SFServerUtil.parseFiltersToSql("prod_productfamilyid", productFamilyId);
		filters += "<i>Familia: </i>" + request.getParameter("prod_productfamilyidLabel") + ", ";
	}
	
	if (!productGroupId.equals("")) {
		whereProductGroup += SFServerUtil.parseFiltersToSql("prod_productgroupid", productGroupId);
		filters += "<i>Grupo Prod.: </i>" + request.getParameter("prod_productgroupidLabel") + ", ";
	}

	if (!lockStatus.equals("")) {
		where += " AND orde_lockstatus like '" + lockStatus + "'";
		filters += "<i>Apartado: </i>" + request.getParameter("orde_lockstatusLabel") + ", ";
	}
	

	if (currencyId > 0) {
		bmoCurrency = (BmoCurrency) pmCurrency.get(currencyId);
		defaultParity = bmoCurrency.getParity().toDouble();

		filters += "<i>Moneda: </i>" + request.getParameter("orde_currencyidLabel")
				+ " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
	} else {
		filters += "<i>Moneda: </i> Todas ";
		// Se mete la consulta padre para agilizar el proceso
		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, "orders")
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") + " ON (ortp_ordertypeid = orde_ordertypeid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers") + " ON (cust_customerid = orde_customerid) " 
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries") + " ON (indu_industryid = cust_industryid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (user_userid = orde_userid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (orde_currencyid = cure_currencyid) ";
				if (enableBudgets) {
					 sqlCurrency  += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
				    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (bgit_budgetid = budg_budgetid)" +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON (area_areaid = orde_defaultareaid) ";
				}
				 sqlCurrency  += " WHERE orde_orderid > 0 "
				+ " AND orde_reneworderid IS NULL " 
				+ where 
				+ " AND orde_orderid IN ( "
				+ " SELECT ordg_orderid FROM " + SQLUtil.formatKind(sFParams, "orderitems") 
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = ordi_productid) " 
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups") + " ON (ordg_ordergroupid = ordi_ordergroupid) " 
				+ " WHERE prod_reneworder = 1 " + " ) ";
		if (productId > 0 || showProductExtra != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			sqlCurrency += " AND orde_orderid IN ( " 
				+ " SELECT orde_orderid FROM " + SQLUtil.formatKind(sFParams, "orders") 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups") + " ON (ordg_orderid = orde_orderid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orderitems") + " ON (ordi_ordergroupid = ordg_ordergroupid) " 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = ordi_productid) "
					+ " WHERE orde_orderid = orde_orderid " 
					+ whereProduct 
					+ whereExtra 
					+ whereProductFamily
					+ whereProductGroup + " ) ";
		}
		sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";

	}

	if (sFParams.getSelectedCompanyId() > 0)
		filters += "<i>Empresa: </i>" + sFParams.getBmoSelectedCompany().getName().toString() + " | "
				+ sFParams.getBmoSelectedCompany().getName().toString() + ", ";

	// Obtener disclosure de datos
	String disclosureFilters = new PmOrder(sFParams).getDisclosureFilters();
	if (disclosureFilters.length() > 0)
		where += " AND " + disclosureFilters;

	PmConn pmOrder = new PmConn(sFParams);
	pmOrder.open();
	
	PmConn pmOrderRenov = new PmConn(sFParams);
	pmOrderRenov.open();
	
	PmConn pmOrderRenovDate = new PmConn(sFParams);
	pmOrderRenovDate.open();

	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	PmConn pmConnIfRenew = new PmConn(sFParams);
	pmConnIfRenew.open();

	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();

	// colspan dinamico

	//	if (sFParams.isFieldEnabled(bmoWFlow.getFunnel()))
	//		dynamicColspan++;
	if (productId > 0 || showProductExtra != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
		dynamicColspan++;
		dynamicColspanMinus++;
	}
// 	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity()))
// 		dynamicColspan++;

	//System.out.println("dynamicColspan: "+dynamicColspan);
%>
<html>
<%
	// Imprimir
	String print = "0", permissionPrint = "";
	if ((String) request.getParameter("print") != null)
		print = (String) request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String) request.getParameter("exportexcel") != null)
		exportExcel = (String) request.getParameter("exportexcel");
	if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=\"" + title + ".xls\"");
	}

	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃº(clic-derecho).
	//En caso de que mande a imprimir, deshabilita contenido
	if (!(sFParams.hasPrint(bmoProgram.getCode().toString()))) {
%>
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
		if (print.equals("1") || exportExcel.equals("1")) {
%>
<script>
	alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
</script>
<%
	}
	}

	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
	if (sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) {
%>
<head>
<title>:::<%=title%>:::
</title>
<link rel="stylesheet" type="text/css"
	href="<%=sFParams.getAppURL()%>/css/<%=defaultCss%>">
</head>

<body class="default" <%=permissionPrint%> style="padding-right: 10px">

	<table border="0" width="100%">
		<tr>
			<td align="left" width="80" rowspan="2" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>"
				src="<%=sFParams.getMainImageUrl()%>"></td>
			<td class="reportTitle" align="left" colspan="2"><%=title%></td>
		</tr>
		<tr>
			<td class="reportSubTitle"><b>Filtros:</b> <%=filters%><br>
				<% 	if (!(currencyId > 0)) {%>
						<b>Agrupado Por: </b>Moneda.
				<% 	}%>
				<b>Ordenador por:</b> ID Cliente, Fecha Inicio<br>	
				<b>Pedidos pagados en negrita</b> <br>				
			</td>
			<td class="reportDate" align="right">Creado: <%=SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat())%>
				por: <%=sFParams.getLoginInfo().getEmailAddress()%>
			</td>
		</tr>
	</table>
	<br>
	<table class="report" border="0" width="100%">
		<%
		if (!(currencyId > 0)) {
			int currencyIdWhile = 0, x = 1;
			pmCurrencyWhile.doFetch(sqlCurrency);
			while (pmCurrencyWhile.next()) {
				currencyIdWhile = pmCurrencyWhile.getInt("cure_currencyid");
				currencyId = currencyIdWhile;
		    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
		    	dynamicColspan = 0;
        	    dynamicColspanMinus = 0;
        	    
				int anioMin = 0, anioMax = 0, anioS=0, anioE=0,haveOrder=0,orde =0,orderFinal = 0;
				double amountTotal = 0;
				
				ArrayList<Integer> orderidOld = new ArrayList<Integer>();
				orderidOld.add(0);
				// Sacar el año mas bajo que encontro en la fecha de inicio de todos los pedidos
				sql = "SELECT YEAR(MIN(orde_lockstart)) AS anioMin FROM " + SQLUtil.formatKind(sFParams, "orders")
						+ " WHERE orde_originreneworderid IS NULL ";
						//+ " AND orde_currencyid =  " + currencyId;
				//System.out.println("año min: "+sql);
				pmConn.doFetch(sql);
				if (pmConn.next()) anioMin = pmConn.getInt("anioMin");
		
				// Sacar el año mas alto que encontro en la fecha fin de todos los pedidos
				sql = "SELECT YEAR(MAX(orde_lockend)) AS anioMax FROM " + SQLUtil.formatKind(sFParams, "orders")
						+ " WHERE orde_originreneworderid IS NOT NULL";
						//+ " AND orde_currencyid =  " + currencyId;
				//System.out.println("año max: "+sql);
				pmConn.doFetch(sql);
				if (pmConn.next()) anioMax = pmConn.getInt("anioMax");
				
				// Sacar numero de columnas para colspan
				for (int y = anioMin; y <= anioMax; y++) {
					dynamicColspan++;
				}
				%>
				<tr>
					<td class="reportHeaderCellCenter" colspan="<%= 6 +  dynamicColspan%>">
						<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
					</td>
				</tr>
				
				<tr class="">
					<td class="reportHeaderCellCenter">#</td>
					<% dynamicColspan++; %>
					<%	if (sFParams.isFieldEnabled(bmoOrder.getCustomerId())) {
							dynamicColspan++;
						%>
							<td class="reportHeaderCell">
								<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getCustomerId()))%>
							</td>
					<%	}%>
					<%	if (sFParams.isFieldEnabled(bmoOrder.getName())) {
							dynamicColspan++;
						%>
							<td class="reportHeaderCell">
								<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getName()))%>
							</td>
					<%	}%>
					<%	if (sFParams.isFieldEnabled(bmoOrder.getAmount())) {
							dynamicColspan++;
						%>
							<td class="reportHeaderCellRight">
								<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getAmount()))%>
							</td>
					<%	}%>
					<%	if (sFParams.isFieldEnabled(bmoOrder.getLockStart())) {
							dynamicColspan++;
						%>
							<td class="reportHeaderCell">
								<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getLockStart()))%>
							</td>
					<%	}%>
					<%	if (sFParams.isFieldEnabled(bmoOrder.getLockEnd())) {
							dynamicColspan++;
						%>
							<td class="reportHeaderCell">
								<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getLockEnd()))%>
							</td>
					<%	}%>
					<%	
						// Mostrar columnas de años
						for (int y = anioMin; y <= anioMax; y++) {
							%>
							<td class="reportHeaderCell"><%= y%></td>
							<%
						}
					%>
				</tr>
				<%
				//double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0;
				
				// Buscar los pedidos origen(iniciales)
				sql = " SELECT *  FROM " + SQLUtil.formatKind(sFParams, "orders")
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") + " ON (ortp_ordertypeid = orde_ordertypeid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers") + " ON (cust_customerid = orde_customerid) " 
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries") + " ON (indu_industryid = cust_industryid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (user_userid = orde_userid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (orde_currencyid = cure_currencyid) ";
						if (enableBudgets) {
							sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
					    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
					    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (bgit_budgetid = budg_budgetid)" +
									" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON (area_areaid = orde_defaultareaid) ";
						}
						sql += " WHERE orde_orderid > 0 "
						+ " AND orde_reneworderid IS NULL " 
						+ " AND orde_currencyid =  " + currencyId
						+ whereOrderId
						+ where 
						+ wherelockstart
						+ wherelockEnd
						+ " AND orde_orderid IN ( "
						+ " SELECT ordg_orderid FROM " + SQLUtil.formatKind(sFParams, "orderitems") 
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = ordi_productid) " 
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups") + " ON (ordg_ordergroupid = ordi_ordergroupid) " 
						+ " WHERE prod_reneworder = 1 " + " ) ";
								
				if (productId > 0 || showProductExtra != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
					sql += " AND orde_orderid IN ( " 
						+ " SELECT orde_orderid FROM " + SQLUtil.formatKind(sFParams, "orders") 
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups") + " ON (ordg_orderid = orde_orderid) "
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orderitems") + " ON (ordi_ordergroupid = ordg_ordergroupid) " 
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = ordi_productid) "
							+ " WHERE orde_orderid = orde_orderid " 
							+ whereProduct 
							+ whereExtra 
							+ whereProductFamily
							+ whereProductGroup + " ) ";
							
				}
				sql += " ORDER by cust_customerid ASC, orde_lockstart DESC; "; 
				//System.out.println("Consulta 1:"+sql);
				pmOrder.doFetch(sql);
				
				int i = 1;
				while (pmOrder.next()) {

					// Buscar los pedidos hijos y sacar el último
					sql = " SELECT * FROM orders "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") + " ON (ortp_ordertypeid = orde_ordertypeid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers") + " ON (cust_customerid = orde_customerid) " 
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries") + " ON (indu_industryid = cust_industryid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (user_userid = orde_userid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (orde_currencyid = cure_currencyid) " 
					    + " WHERE orde_originreneworderid = " +   pmOrder.getInt("orders", "orde_orderid") 
						+ " ORDER BY orde_orderid DESC LIMIT 1; "; 
					//System.out.println("Consulta 2:"+sql);
					pmOrderRenov.doFetch(sql);
					orderFinal = 0;
					// Mostrar el último pedido hijo si hay renovados
					if (pmOrderRenov.next()) {
						orderFinal = pmOrderRenov.getInt("orde_orderid");
						if (pmOrderRenov.getString("cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY))
							customer = pmOrderRenov.getString("cust_code") + " " + pmOrderRenov.getString("cust_legalname");
						else
							customer = pmOrderRenov.getString("cust_code") + " " + pmOrderRenov.getString("cust_displayname");
						
				    	// Conversion a la moneda destino(selección filtro)
				    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
				    	double parityOrigin = 0, parityDestiny = 0;
				    	currencyIdOrigin = pmOrderRenov.getInt("orde_currencyid");
				    	parityOrigin = pmOrderRenov.getDouble("orde_currencyparity");
				    	currencyIdDestiny = currencyId;
				    	parityDestiny = defaultParity;
				    	
				    	double amount = pmOrderRenov.getDouble("orde_amount");
				    	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	
				    	// Suma total
				    	amountTotal += amount;

					%>
						<tr class="reportCellEven">
							<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
							<%=HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)%>
							<%=HtmlUtil.formatReportCell(sFParams, pmOrderRenov.getString("orders", "orde_code") + " "+ pmOrderRenov.getString("orders", "orde_name"), BmFieldType.STRING)%>
							<%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY)%>
							<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrderRenov.getString("orders", "orde_lockstart").substring(0,10), BmFieldType.STRING))%>
							<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrderRenov.getString("orders", "orde_lockend").substring(0,10), BmFieldType.STRING))%>
					<%
					}
					// Mostrar el último pedido hijo, si no hay renovados mostrar el mismo origen
					else {
						if (pmOrder.getString("cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY))
							customer = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");
						else
							customer = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
						
						// Conversion a la moneda destino(selección filtro)
				    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
				    	double parityOrigin = 0, parityDestiny = 0;
				    	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
				    	parityOrigin = pmOrder.getDouble("orde_currencyparity");
				    	currencyIdDestiny = currencyId;
				    	parityDestiny = defaultParity;
				    	
				    	double amount = pmOrder.getDouble("orde_amount");
				    	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	
				    	// Suma total
				    	amountTotal += amount;
					%>
						<tr class="reportCellEven">
							<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
							<%=HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)%>
							<%=HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_code") + " "+ pmOrder.getString("orders", "orde_name"), BmFieldType.STRING)%>
							<%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY)%>
							<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockstart").substring(0,10), BmFieldType.STRING))%>
							<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockend").substring(0,10), BmFieldType.STRING))%>
							<%
					}
						// Ciclo de año en año
						for (int y = anioMin; y <= anioMax; y++) {
							
							int n = 0;
							String ordersRenewByYear = "";
							
							// Tomar año de inicio y fin del pedido origen
							anioS = Integer.parseInt(pmOrder.getString("orders", "orde_lockstart").substring(0,4));
							anioE = Integer.parseInt(pmOrder.getString("orders", "orde_lockend").substring(0,4));
							//System.out.println("--- "+y+" ---");
							//System.out.println("origen inicio: "+anioS);
							//System.out.println("origen fin: "+anioE);
							
							// Validar CxC
							boolean paymentStatusRacc = false; // total es true
							// Traer todas las cxc del pedido origen
							sql = "SELECT racc_paymentstatus FROM "+ SQLUtil.formatKind(sFParams, "raccounts")
									+ " WHERE racc_orderid = " + pmOrder.getInt("orde_orderid")
									+ " AND YEAR(racc_receivedate) = " + y
									+ " ORDER BY racc_receivedate ASC;";
									
									//System.out.println("sql_CXC_origen: ");
							pmConn.doFetch(sql);
							while (pmConn.next()) {
								if (pmConn.getString("racc_paymentstatus").equals("" + BmoRaccount.PAYMENTSTATUS_PENDING)) {
									//System.out.println("origen:pend");
									paymentStatusRacc = false;
									break;
								} else { 
									//System.out.println("origen:tota");
									paymentStatusRacc =  true;									
								}
							}

							// existe ultimo renovado // se va a quitar este if con else
//	 						if (orderFinal != 0) {
								//System.out.println("DIF de renovado");
								// año es igual al año fecha inicio del origen
								if (y >= anioS && y <= anioE) {
									//System.out.println("año igual");
									n++;
									if (n > 1) ordersRenewByYear += ", ";
									
									// Si fue renovado el origen en el año actual(while)
									sql = "SELECT count(*) as haveRenew  FROM "+ SQLUtil.formatKind(sFParams, "orders")
										      	+ " WHERE orde_originreneworderid = " + pmOrder.getInt("orde_orderid")
										      	+ " AND YEAR(orde_lockstart) = " +y;
									//System.out.println("renovados en año: "+sql);
									
									pmConn.doFetch(sql);
									if (pmConn.next()) {
										// No fue renovado en este año, colocarlo en el año
										if (!(pmConn.getInt("haveRenew") > 0)) {
											// Si el año esta pagado marcarlo en negrita
											if (paymentStatusRacc) {
												//System.out.println("REN_TOTAL1");
												ordersRenewByYear += "<b>"+pmOrder.getString("orde_code")+"</b>";
											}else{
												//System.out.println("REN PENDIENTE1");
												ordersRenewByYear += "<font color='#F2CF35'>"+pmOrder.getString("orde_code")+"</font>";
											}
										} 
									}
								}
//	 						// No hubo renovacion, por lo tanto es origen
//	 						else {
//	 							System.out.println("IGUAL A origen");
//	 							// año >= añoInicioPedOrigen y año <= añoFinPedOrigen
//	 							if (y >= anioS && y <= anioE) {
//	 								n++;
//	 								if (n > 1)ordersRenewByYear += ", ";
									
//	 								// Si el año esta pagado marcarlo en negrita
//	 								if (paymentStatusRacc) {
//	 									System.out.println("REN_TOTAL2");
//	 									ordersRenewByYear += "<b>"+pmOrder.getString("orde_code")+"</b>";
//	 								} else{
//	 									System.out.println("REN PENDIENTE2");
//	 									ordersRenewByYear += "<font color='#F2CF35'>"+pmOrder.getString("orde_code")+"</font>";
//	 								}
//	 							}
//	 						}
							
							// Buscar pedidos hijos entre fechas
							sql = "SELECT distinct orde_code, orde_paymentstatus, orde_orderid " 
									+ " FROM " + SQLUtil.formatKind(sFParams, "orders")
									+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") + " ON (ortp_ordertypeid = orde_ordertypeid) "
									+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers") + " ON (cust_customerid = orde_customerid) " 
									+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries") + " ON (indu_industryid = cust_industryid) "
									+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (user_userid = orde_userid) "
									+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (orde_currencyid = cure_currencyid) " 
									+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups") + " ON (ordg_orderid = orde_orderid) "
									+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orderitems") + " ON (ordi_ordergroupid = ordg_ordergroupid) " 
									+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = ordi_productid) ";
									if (enableBudgets) {
										sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
								    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
								    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (bgit_budgetid = budg_budgetid)" +
												" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON (area_areaid = orde_defaultareaid) ";
									}
									sql += " WHERE orde_originreneworderid = " + pmOrder.getInt("orders", "orde_orderid")
									+ whereProduct 
									+ whereExtra 
									+ whereProductFamily
									+ whereProductGroup
									+ where
									+ " AND	(orde_lockstart BETWEEN '" + y + "-01-01 00:00' AND '" + y + "-12-31 23:59'"
									+ " OR (orde_lockend BETWEEN '" + y + "-01-01 00:00' AND '" + y + "-12-31 23:59') "
									+ " OR ('" + y + "-01-01 00:00' BETWEEN orde_lockstart AND orde_lockend))";
										
							//System.out.println("Consulta 3: "+sql);
						
							int pay=0;
							 n = 0;
							pmOrderRenovDate.doFetch(sql);

							while (pmOrderRenovDate.next()) {
								// saber si el pedido ya fue renovado
								sql = "SELECT count(*) as haveRenew  FROM "+ SQLUtil.formatKind(sFParams, "orders")
									      	+ " WHERE orde_reneworderid = " + pmOrderRenovDate.getInt("orde_orderid")
									      ;
//	 								      	+ " AND YEAR(orde_lockstart) = " +y;
//	 							System.out.println("renovados en año: "+sql);
								pmConnIfRenew.doFetch(sql);
								if (pmConnIfRenew.next())
									haveOrder = pmConnIfRenew.getInt("haveRenew");

								// Si fue renovado
								if (haveOrder > 0) {
									//System.out.println("TIENE RENOVADO");
									// Si fue renovado solo ponerlo una vez
									orde = pmOrderRenovDate.getInt("orde_orderid");
									if (!(orderidOld.contains(orde))) {
										//System.out.println("111");
										orderidOld.add(pmOrderRenovDate.getInt("orde_orderid"));
										n++;
										if (n > 1)
											ordersRenewByYear += ", ";
										
										paymentStatusRacc = false; //  total es true
										// Traer todas las cxc del pedido renovado
										sql = "SELECT racc_paymentstatus FROM "+ SQLUtil.formatKind(sFParams, "raccounts")
												+ " WHERE racc_orderid = " + pmOrderRenovDate.getInt("orde_orderid")
												+ " AND YEAR(racc_receivedate) = " + y
												+ " ORDER BY racc_receivedate ASC;";
										pmConn.doFetch(sql);
										int countRacc = 0;
										while (pmConn.next()) {
											if (pmConn.getString("racc_paymentstatus").equals("" + BmoRaccount.PAYMENTSTATUS_PENDING)) {
												paymentStatusRacc = false;
												break;
											} else paymentStatusRacc =  true;
										}
											
										// Si el año esta pagado marcarlo en negrita
										if (paymentStatusRacc) {
												//System.out.println("222");
												ordersRenewByYear += "<b>" + pmOrderRenovDate.getString("orde_code") + "</b>";
											} else {
												//System.out.println("333");
												ordersRenewByYear += "<font color='#F2CF35'>"+ pmOrderRenovDate.getString("orde_code") + "</font>";
											}
										}
									}
									else { //No tiene renovado
										//System.out.println("NO TIENE RENOVADO");
										orderidOld.add(pmOrderRenovDate.getInt("orde_orderid"));
										n++;								
										if (n > 1)
											ordersRenewByYear += ", ";

										paymentStatusRacc = false; //  total es true
										// Traer todas las cxc del pedido renovado
										sql = "SELECT racc_paymentstatus FROM "+ SQLUtil.formatKind(sFParams, "raccounts")
												+ " WHERE racc_orderid = " + pmOrderRenovDate.getInt("orde_orderid")
												+ " AND YEAR(racc_receivedate) = " + y
												+ " ORDER BY racc_receivedate ASC;";
												
												//System.out.println("sql_CXC: ");
										pmConn.doFetch(sql);
										int countRacc = 0; // Contar cxc del año
										while (pmConn.next()) {
											countRacc++;
											if (pmConn.getString("racc_paymentstatus").equals("" + BmoRaccount.PAYMENTSTATUS_PENDING)) {
												//System.out.println("pend");
												paymentStatusRacc = false;
												break;
											} else { 
												//System.out.println("tota");
												paymentStatusRacc =  true;									
											}
										}
										
										// Si el año esta pagado marcarlo en negrita
										if (paymentStatusRacc) {
												//System.out.println("444");
												ordersRenewByYear += "<b>" + pmOrderRenovDate.getString("orde_code") + "</b>";
											} else{
												// Mostrar si hay cxc en el año
												if (countRacc > 0) {
													//System.out.println("555");
													ordersRenewByYear += "<font color='#F2CF35'>"+ pmOrderRenovDate.getString("orde_code") + "</font>";
												}
											}
										}
									}
							%>
						
								<%=HtmlUtil.formatReportCell(sFParams,ordersRenewByYear , BmFieldType.STRING)%>
								<%
						}
						i++;
				} //pmOrder
				%>
				<tr class="reportCellEven">
					<td colspan="<%= dynamicColspan%>">&nbsp;</td>
				</tr>
		<%
			} // Fin pmCurrencyWhile
		} // Fin de no existe moneda
		// Existe moneda destino
		else {

			int anioMin = 0, anioMax = 0, anioS=0, anioE=0,haveOrder=0,orde =0,orderFinal = 0;
			double amountTotal = 0;
			
			ArrayList<Integer> orderidOld = new ArrayList<Integer>();
			orderidOld.add(0);
			
			// Sacar el año mas bajo que encontro en la fecha de inicio de todos los pedidos
			sql = "SELECT YEAR(MIN(orde_lockstart)) AS anioMin FROM " + SQLUtil.formatKind(sFParams, "orders")
					+ " WHERE orde_originreneworderid IS NULL";
			pmConn.doFetch(sql);
			if (pmConn.next()) anioMin = pmConn.getInt("anioMin");
			
			// Sacar el año mas alto que encontro en la fecha fin de todos los pedidos
			sql = "SELECT YEAR(MAX(orde_lockend)) AS anioMax FROM " + SQLUtil.formatKind(sFParams, "orders")
					+ " WHERE orde_originreneworderid IS NOT NULL";
			pmConn.doFetch(sql);
			if (pmConn.next()) anioMax = pmConn.getInt("anioMax");

			%>
			<tr class="">
				<td class="reportHeaderCellCenter">#</td>
				<% dynamicColspan++; %>
				<%	if (sFParams.isFieldEnabled(bmoOrder.getCustomerId())) {
						dynamicColspan++;
					%>
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getCustomerId()))%>
						</td>
				<%	}%>
				<%	if (sFParams.isFieldEnabled(bmoOrder.getName())) {
						dynamicColspan++;
					%>
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getName()))%>
						</td>
				<%	}%>
				<%	if (sFParams.isFieldEnabled(bmoOrder.getAmount())) {
						dynamicColspan++;
					%>
						<td class="reportHeaderCellRight">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getAmount()))%>
						</td>
				<%	}%>
				<%	if (sFParams.isFieldEnabled(bmoOrder.getLockStart())) {
						dynamicColspan++;
					%>
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getLockStart()))%>
						</td>
				<%	}%>
				<%	if (sFParams.isFieldEnabled(bmoOrder.getLockEnd())) {
						dynamicColspan++;
					%>
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getLockEnd()))%>
						</td>
				<%	}%>
				<%	// Mostrar columnas de años
					for (int y = anioMin; y <= anioMax; y++) {
						dynamicColspan++;
					%>
						<td class="reportHeaderCell"><%= y%></td>
					<%
					}
				%>
			</tr>
			<%
			//double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0;
			
			// Buscar los pedidos origen(iniciales)
			sql =     "	SELECT *  FROM " + SQLUtil.formatKind(sFParams, "orders")
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") + " ON (ortp_ordertypeid = orde_ordertypeid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers") + " ON (cust_customerid = orde_customerid) " 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries") + " ON (indu_industryid = cust_industryid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (user_userid = orde_userid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (orde_currencyid = cure_currencyid) ";
					if (enableBudgets) {
						sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
					    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
					    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (bgit_budgetid = budg_budgetid)" +
									" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON (area_areaid = orde_defaultareaid) ";
					}
					sql += " WHERE orde_orderid > 0 "
					+ " AND orde_reneworderid IS NULL " 
					+ whereOrderId
					+ where 
					+ wherelockstart
					+ wherelockEnd
					+ " AND orde_orderid IN ( "
					+ " SELECT ordg_orderid FROM " + SQLUtil.formatKind(sFParams, "orderitems") 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = ordi_productid) " 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups") + " ON (ordg_ordergroupid = ordi_ordergroupid) " 
					+ " WHERE prod_reneworder = 1 " + " ) ";
					
							
			if (productId > 0 || showProductExtra != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
				sql += " AND orde_orderid IN ( " 
					+ " SELECT orde_orderid FROM " + SQLUtil.formatKind(sFParams, "orders") 
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups") + " ON (ordg_orderid = orde_orderid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orderitems") + " ON (ordi_ordergroupid = ordg_ordergroupid) " 
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = ordi_productid) "
						+ " WHERE orde_orderid = orde_orderid " 
						+ whereProduct 
						+ whereExtra 
						+ whereProductFamily
						+ whereProductGroup + " ) ";
						
			}
			sql += " ORDER by cust_customerid ASC, orde_lockstart DESC; "; 
			//System.out.println("Consulta 1:"+sql);
			pmOrder.doFetch(sql);
			
			int i = 1;
			while (pmOrder.next()) {

				// Buscar los pedidos hijos y sacar el último
				sql = " SELECT * FROM orders "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") + " ON (ortp_ordertypeid = orde_ordertypeid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers") + " ON (cust_customerid = orde_customerid) " 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries") + " ON (indu_industryid = cust_industryid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (user_userid = orde_userid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (orde_currencyid = cure_currencyid) " 
				    + " WHERE orde_originreneworderid = " +   pmOrder.getInt("orders", "orde_orderid") 
					+ " ORDER BY orde_orderid DESC LIMIT 1; "; 
				//System.out.println("Consulta 2:"+sql);
				pmOrderRenov.doFetch(sql);
				orderFinal = 0;
				// Mostrar el último pedido hijo si hay renovados
				if (pmOrderRenov.next()) {
					orderFinal = pmOrderRenov.getInt("orde_orderid");
					if (pmOrderRenov.getString("cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY))
						customer = pmOrderRenov.getString("cust_code") + " " + pmOrderRenov.getString("cust_legalname");
					else
						customer = pmOrderRenov.getString("cust_code") + " " + pmOrderRenov.getString("cust_displayname");
					
			    	// Conversion a la moneda destino(selección filtro)
			    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
			    	double parityOrigin = 0, parityDestiny = 0;
			    	currencyIdOrigin = pmOrderRenov.getInt("orde_currencyid");
			    	parityOrigin = pmOrderRenov.getDouble("orde_currencyparity");
			    	currencyIdDestiny = currencyId;
			    	parityDestiny = defaultParity;
			    	
			    	double amount = pmOrderRenov.getDouble("orde_amount");
			    	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			    	
			    	// Suma total
			    	amountTotal += amount;

				%>
					<tr class="reportCellEven">
						<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
						<%=HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams, pmOrderRenov.getString("orders", "orde_code") + " "+ pmOrderRenov.getString("orders", "orde_name"), BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY)%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrderRenov.getString("orders", "orde_lockstart").substring(0,10), BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrderRenov.getString("orders", "orde_lockend").substring(0,10), BmFieldType.STRING))%>
				<%
				}
				// Mostrar el último pedido hijo, si no hay renovados mostrar el mismo origen
				else {
					if (pmOrder.getString("cust_customertype").equals("" + BmoCustomer.TYPE_COMPANY))
						customer = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");
					else
						customer = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
					
					// Conversion a la moneda destino(selección filtro)
			    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
			    	double parityOrigin = 0, parityDestiny = 0;
			    	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
			    	parityOrigin = pmOrder.getDouble("orde_currencyparity");
			    	currencyIdDestiny = currencyId;
			    	parityDestiny = defaultParity;
			    	
			    	double amount = pmOrder.getDouble("orde_amount");
			    	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			    	
			    	// Suma total
			    	amountTotal += amount;
				%>
					<tr class="reportCellEven">
						<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
						<%=HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_code") + " "+ pmOrder.getString("orders", "orde_name"), BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY)%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockstart").substring(0,10), BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_lockend").substring(0,10), BmFieldType.STRING))%>
						<%
				}
					// Ciclo de año en año
					for (int y = anioMin; y <= anioMax; y++) {
						
						int n = 0;
						String ordersRenewByYear = "";
						
						// Tomar año de inicio y fin del pedido origen
						anioS = Integer.parseInt(pmOrder.getString("orders", "orde_lockstart").substring(0,4));
						anioE = Integer.parseInt(pmOrder.getString("orders", "orde_lockend").substring(0,4));
						//System.out.println("--- "+y+" ---");
						//System.out.println("origen inicio: "+anioS);
						//System.out.println("origen fin: "+anioE);
						
						// Validar CxC
						boolean paymentStatusRacc = false; // total es true
						// Traer todas las cxc del pedido origen
						sql = "SELECT racc_paymentstatus FROM "+ SQLUtil.formatKind(sFParams, "raccounts")
								+ " WHERE racc_orderid = " + pmOrder.getInt("orde_orderid")
								+ " AND YEAR(racc_receivedate) = " + y
								+ " ORDER BY racc_receivedate ASC;";
								
								//System.out.println("sql_CXC_origen: ");
						pmConn.doFetch(sql);
						while (pmConn.next()) {
							if (pmConn.getString("racc_paymentstatus").equals("" + BmoRaccount.PAYMENTSTATUS_PENDING)) {
								//System.out.println("origen:pend");
								paymentStatusRacc = false;
								break;
							} else { 
								//System.out.println("origen:tota");
								paymentStatusRacc =  true;									
							}
						}

						// existe ultimo renovado // se va a quitar este if con else
// 						if (orderFinal != 0) {
							//System.out.println("DIF de renovado");
							// año es igual al año fecha inicio del origen
							if (y >= anioS && y <= anioE) {
								//System.out.println("año igual");
								n++;
								if (n > 1) ordersRenewByYear += ", ";
								
								// Si fue renovado el origen en el año actual(while)
								sql = "SELECT count(*) as haveRenew  FROM "+ SQLUtil.formatKind(sFParams, "orders")
									      	+ " WHERE orde_originreneworderid = " + pmOrder.getInt("orde_orderid")
									      	+ " AND YEAR(orde_lockstart) = " +y;
								//System.out.println("renovados en año: "+sql);
								
								pmConn.doFetch(sql);
								if (pmConn.next()) {
									// No fue renovado en este año, colocarlo en el año
									if (!(pmConn.getInt("haveRenew") > 0)) {
										// Si el año esta pagado marcarlo en negrita
										if (paymentStatusRacc) {
											//System.out.println("REN_TOTAL1");
											ordersRenewByYear += "<b>"+pmOrder.getString("orde_code")+"</b>";
										}else{
											//System.out.println("REN PENDIENTE1");
											ordersRenewByYear += "<font color='#F2CF35'>"+pmOrder.getString("orde_code")+"</font>";
										}
									} 
								}
							}
// 						// No hubo renovacion, por lo tanto es origen
// 						else {
// 							System.out.println("IGUAL A origen");
// 							// año >= añoInicioPedOrigen y año <= añoFinPedOrigen
// 							if (y >= anioS && y <= anioE) {
// 								n++;
// 								if (n > 1)ordersRenewByYear += ", ";
								
// 								// Si el año esta pagado marcarlo en negrita
// 								if (paymentStatusRacc) {
// 									System.out.println("REN_TOTAL2");
// 									ordersRenewByYear += "<b>"+pmOrder.getString("orde_code")+"</b>";
// 								} else{
// 									System.out.println("REN PENDIENTE2");
// 									ordersRenewByYear += "<font color='#F2CF35'>"+pmOrder.getString("orde_code")+"</font>";
// 								}
// 							}
// 						}
						
						// Buscar pedidos hijos entre fechas
						sql = "SELECT distinct orde_code, orde_paymentstatus, orde_orderid " 
								+ " FROM " + SQLUtil.formatKind(sFParams, "orders")
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") + " ON (ortp_ordertypeid = orde_ordertypeid) "
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers") + " ON (cust_customerid = orde_customerid) " 
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "industries") + " ON (indu_industryid = cust_industryid) "
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users") + " ON (user_userid = orde_userid) "
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (orde_currencyid = cure_currencyid) " 
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordergroups") + " ON (ordg_orderid = orde_orderid) "
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orderitems") + " ON (ordi_ordergroupid = ordg_ordergroupid) " 
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = ordi_productid) ";
								if (enableBudgets) {
									sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (bgit_budgetitemid = orde_defaultbudgetitemid)" +
								    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
								    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (bgit_budgetid = budg_budgetid)" +
												" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON (area_areaid = orde_defaultareaid) ";
								}
								sql += " WHERE orde_originreneworderid = " + pmOrder.getInt("orders", "orde_orderid")
								+ whereProduct 
								+ whereExtra 
								+ whereProductFamily
								+ whereProductGroup
								+ where
								+ " AND	(orde_lockstart BETWEEN '" + y + "-01-01 00:00' AND '" + y + "-12-31 23:59'"
								+ " OR (orde_lockend BETWEEN '" + y + "-01-01 00:00' AND '" + y + "-12-31 23:59') "
								+ " OR ('" + y + "-01-01 00:00' BETWEEN orde_lockstart AND orde_lockend))";
									
						//System.out.println("Consulta 3: "+sql);
					
						int pay=0;
						 n = 0;
						pmOrderRenovDate.doFetch(sql);

						while (pmOrderRenovDate.next()) {
							// saber si el pedido ya fue renovado
							sql = "SELECT count(*) as haveRenew  FROM "+ SQLUtil.formatKind(sFParams, "orders")
								      	+ " WHERE orde_reneworderid = " + pmOrderRenovDate.getInt("orde_orderid")
								      ;
// 								      	+ " AND YEAR(orde_lockstart) = " +y;
// 							System.out.println("renovados en año: "+sql);
							pmConnIfRenew.doFetch(sql);
							if (pmConnIfRenew.next())
								haveOrder = pmConnIfRenew.getInt("haveRenew");

							// Si fue renovado
							if (haveOrder > 0) {
								//System.out.println("TIENE RENOVADO");
								// Si fue renovado solo ponerlo una vez
								orde = pmOrderRenovDate.getInt("orde_orderid");
								if (!(orderidOld.contains(orde))) {
									//System.out.println("111");
									orderidOld.add(pmOrderRenovDate.getInt("orde_orderid"));
									n++;
									if (n > 1)
										ordersRenewByYear += ", ";
									
									paymentStatusRacc = false; //  total es true
									// Traer todas las cxc del pedido renovado
									sql = "SELECT racc_paymentstatus FROM "+ SQLUtil.formatKind(sFParams, "raccounts")
											+ " WHERE racc_orderid = " + pmOrderRenovDate.getInt("orde_orderid")
											+ " AND YEAR(racc_receivedate) = " + y
											+ " ORDER BY racc_receivedate ASC;";
									pmConn.doFetch(sql);
									int countRacc = 0;
									while (pmConn.next()) {
										if (pmConn.getString("racc_paymentstatus").equals("" + BmoRaccount.PAYMENTSTATUS_PENDING)) {
											paymentStatusRacc = false;
											break;
										} else paymentStatusRacc =  true;
									}
										
									// Si el año esta pagado marcarlo en negrita
									if (paymentStatusRacc) {
											//System.out.println("222");
											ordersRenewByYear += "<b>" + pmOrderRenovDate.getString("orde_code") + "</b>";
										} else {
											//System.out.println("333");
											ordersRenewByYear += "<font color='#F2CF35'>"+ pmOrderRenovDate.getString("orde_code") + "</font>";
										}
									}
								}
								else { //No tiene renovado
									//System.out.println("NO TIENE RENOVADO");
									orderidOld.add(pmOrderRenovDate.getInt("orde_orderid"));
									n++;								
									if (n > 1)
										ordersRenewByYear += ", ";

									paymentStatusRacc = false; //  total es true
									// Traer todas las cxc del pedido renovado
									sql = "SELECT racc_paymentstatus FROM "+ SQLUtil.formatKind(sFParams, "raccounts")
											+ " WHERE racc_orderid = " + pmOrderRenovDate.getInt("orde_orderid")
											+ " AND YEAR(racc_receivedate) = " + y
											+ " ORDER BY racc_receivedate ASC;";
											
											//System.out.println("sql_CXC: ");
									pmConn.doFetch(sql);
									int countRacc = 0; // Contar cxc del año
									while (pmConn.next()) {
										countRacc++;
										if (pmConn.getString("racc_paymentstatus").equals("" + BmoRaccount.PAYMENTSTATUS_PENDING)) {
											//System.out.println("pend");
											paymentStatusRacc = false;
											break;
										} else { 
											//System.out.println("tota");
											paymentStatusRacc =  true;									
										}
									}
									
									// Si el año esta pagado marcarlo en negrita
									if (paymentStatusRacc) {
											//System.out.println("444");
											ordersRenewByYear += "<b>" + pmOrderRenovDate.getString("orde_code") + "</b>";
										}else{
											// Mostrar si hay cxc en el año
											if (countRacc > 0) {
												//System.out.println("555");
												ordersRenewByYear += "<font color='#F2CF35'>"+ pmOrderRenovDate.getString("orde_code") + "</font>";
											}
										}
									}
								}
						%>
					
							<%=HtmlUtil.formatReportCell(sFParams,ordersRenewByYear , BmFieldType.STRING)%>
							<%
						}
					i++;
				
			} //pmOrder
			%>
			<tr class="reportCellEven">
				<td colspan="<%= dynamicColspan%>">&nbsp;</td>
			</tr>
		<%
		} // Fin Existe moneda destino
		%>
	</table>
	<%
	} // Fin de if(no carga datos)
		pmCurrencyWhile.close();
		pmConn.close();
		pmOrder.close();
		pmOrderRenov.close();
		pmOrderRenovDate.close();
		pmConnIfRenew.close();
		%>
	<%
		if (print.equals("1")) {
		%>
			<script>
				//window.print();
			</script>
		<%
		}
	%>
</body>
</html>
