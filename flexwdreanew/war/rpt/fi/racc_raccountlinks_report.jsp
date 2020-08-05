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
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.symgae.shared.sf.BmoUser"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountLink"%>
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
 	String title = "Reporte de CxC Vinculadas";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	BmoRaccount bmoRaccount = new BmoRaccount();
   	BmoRaccount bmoRaccountLink = new BmoRaccount();
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoCustomer bmoCustomer = new BmoCustomer();
	BmoUser bmoUsers = new BmoUser();
	BmoOrder bmoOrder = new BmoOrder();
	
   	String sql = "", where = "", whereOrderId = "", whereSupl = "", whereFailure = "", whereLinked = "", sqlCurrency = "";
   	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "";
   	String status = "", paymentStatus = "", paymentStatus2 = "", customerCategory ="";
   	String filters = "", customer = "", areaId = "-1", budgetItemId = "";
   	int programId = 0, customerid = 0, orderId = 0, cols= 0, raccountTypeId = 0, companyId = 0, wflowCategoryId = 0,
   			userId = 0, collectorUserId = 0, currencyId = 0, failure = 0, budgetId = 0, activeBudgets = 0;
   		   	boolean enableBudgets = false;
   	
 	// dynamicColspan incrementar por cada columna del reporte
 	// dynamicColspanMinus incrementar por cada columna que vaya a mostrar totales(es decir, se va a restar al dynamicColspan si HAY FILA TOTALES)
 	int dynamicColspan = 0, dynamicColspanMinus = 0;
 	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
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
        	//where += " AND racc_budgetitemid = " + budgetItemId;
	        filters += "<i>Item Presup.: </i>" + request.getParameter("racc_budgetitemidLabel") + ", ";
	    }
	    
	    if (!areaId.equals("")) {
	        where += SFServerUtil.parseFiltersToSql("racc_areaid", areaId);
	        //where += " AND racc_areaid = " + areaId;
	        filters += "<i>Departamento.: </i>" + request.getParameter("racc_areaidLabel") + ", ";
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
		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM raccounts " ;
		if (enableBudgets) {
			sqlCurrency += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
	    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
	    		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
	    	}
		sqlCurrency += " LEFT JOIN currencies  ON (racc_currencyid = cure_currencyid) " +
    			" LEFT JOIN customers ON (racc_customerid = cust_customerid)" +                              
    			" LEFT JOIN orders ON (racc_orderid = orde_orderid)" +
    			" LEFT JOIN credits ON (orde_orderid = cred_orderid) " +
    			" LEFT JOIN users ON (orde_userid = user_userid) " +
    			" LEFT JOIN wflows ON (orde_wflowid = wflw_wflowid ) " +
    			" LEFT JOIN wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
    			" LEFT JOIN wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
    			" LEFT JOIN companies ON (racc_companyid = comp_companyid)" +
    			" LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid)" +
    			" WHERE racc_raccountid > 0 " +
    			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + 
    			whereOrderId +
    			where +
    			" GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
   	}
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmRaccount(sFParams).getDisclosureFilters();   
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
   	PmConn pmRaccountLinks = new PmConn(sFParams);
   	pmRaccountLinks.open();
   	
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
			<% 	if (!(currencyId > 0)) { %>
				<b>Agrupado por:</b> Moneda.
			<%	}%>
			<b>Ordenado por:</b> Factura
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<br>
<table class="report" border="0">               
	<%

	sql = " SELECT COUNT(*) AS contador FROM raccounts ";
	if (enableBudgets) {
    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
    		+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
    	}
	sql +=	" LEFT JOIN currencies cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) "
			+ " LEFT JOIN customers ON (racc_customerid = cust_customerid)"
			+ " LEFT JOIN orders ON (racc_orderid = orde_orderid)"
			+ " LEFT JOIN currencies cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) "
			+ " LEFT JOIN credits ON (orde_orderid = cred_orderid) "
			+ " LEFT JOIN users ON (orde_userid = user_userid) "
			+ " LEFT JOIN wflows ON (orde_wflowid = wflw_wflowid ) "
			+ " LEFT JOIN wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid) "
			+ " LEFT JOIN wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid) "
			+ " LEFT JOIN companies ON (racc_companyid = comp_companyid)"
			+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
			+ " WHERE racc_raccountid > 0 " 
			+ " AND racc_linked = 1 "
			+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
			+ whereOrderId
			+ where
			+ " ORDER BY racc_invoiceno ASC ";
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
   	
   	if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
   		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
   		currencyId = currencyIdWhile;
   		defaultParity = pmCurrencyWhile.getDouble("cure_parity");
   		dynamicColspan = 0;
   		dynamicColspanMinus = 0;
   		%>
   		<tr>
    		<td class="reportHeaderCellCenter" colspan="<%= (21	 + dynamicColspan)%>">
    			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
    		</td>
   		</tr>
        <%
        }
        %>	
		<tr class="">
			<td class="reportHeaderCellCenter">#</td><%dynamicColspan++; %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCode())) { dynamicColspan++; %>
				<td class="reportHeaderCell">CxC Externa</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCode())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Clave</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCode())) { dynamicColspan++; %>
				<td class="reportHeaderCell">CxC Relac.</td>
			<%	}%>	
			<%	if (enableBudgets) { dynamicColspan = dynamicColspan +2;%>
				<td class="reportHeaderCell">Partida Pres.</td>
		        <td class="reportHeaderCell">Departamento</td>
		    <% } %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getUserId())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Vendedor</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getInvoiceno())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Factura</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getOrderId())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Pedido</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCustomerId())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Cliente</td>
			<%	}%>	
			<%if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { dynamicColspan++; %>
				<td class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getCustomercategory()))%>
				</td>
			<%	}%> 
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getReceiveDate())) { dynamicColspan++; %>
				<td class="reportHeaderCell">F. Ingreso</td>
			<%	}%> 
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getDueDate())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Programaci&oacute;n</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getStatus())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Estatus</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getPaymentStatus())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Pago</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoCurrency.getCode())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Moneda</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoCurrency.getParity())) { dynamicColspan++; %>
				<td class="reportHeaderCellCenter">Tipo de Cambio</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getAmount())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">Monto</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getTax())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">IVA</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getTotal())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">Total</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getPayments())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">Pagos</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getBalance())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">Saldo</td>
			<%	}%>
		</tr>
		<%
			int x = 1;
			double amountTotal = 0, taxTotal = 0, totalTotal = 0, paymentsTotal = 0, balanceTotal = 0;
			sql = " SELECT * FROM raccounts ";
			if (enableBudgets) {
		    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
		    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
		    		+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
		    	}
			sql +=  " LEFT JOIN currencies cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) "
					+ " LEFT JOIN customers ON (racc_customerid = cust_customerid)"
					+ " LEFT JOIN orders ON (racc_orderid = orde_orderid)"
					+ " LEFT JOIN currencies cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) "
					+ " LEFT JOIN credits ON (orde_orderid = cred_orderid) "
					+ " LEFT JOIN users ON (orde_userid = user_userid) "
					+ " LEFT JOIN wflows ON (orde_wflowid = wflw_wflowid ) "
					+ " LEFT JOIN wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid) "
					+ " LEFT JOIN wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid) "
					+ " LEFT JOIN companies ON (racc_companyid = comp_companyid)"
					+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
					+ " WHERE racc_raccountid > 0 " 
					+ " AND racc_linked = 1 "
					+ " AND racc_currencyid = " + currencyId
					+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" 
					+ whereOrderId
					+ where
					+ " ORDER BY racc_invoiceno ASC ";
					
					System.out.println("sql1: "+sql);

			pmRaccounts.doFetch(sql);
			while (pmRaccounts.next()) {
				bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts", "racc_status"));
				bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts", "racc_paymentstatus"));
				if(sFParams.isFieldEnabled(bmoCustomer.getCustomercategory()))
					bmoCustomer.getCustomercategory().setValue(pmRaccounts.getString("customers", "cust_customercategory"));
				if (pmRaccounts.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
		    		customer = pmRaccounts.getString("cust_code") + " " + pmRaccounts.getString("cust_legalname");	
	   			else
	   				customer = pmRaccounts.getString("cust_code") + " " + pmRaccounts.getString("cust_displayname");
				
				//Conversion a la moneda destino(seleccion del filtro)
			   	int currencyIdOrigin = 0, currencyIdDestiny = 0;
			   	double parityOrigin = 0, parityDestiny = 0;
			   	currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
			   	parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
			   	currencyIdDestiny = currencyId;
			   	parityDestiny = defaultParity;
		%>
			   <tr class="reportCellEven">
				   <%= HtmlUtil.formatReportCell(sFParams, "" + x, BmFieldType.NUMBER) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
				   <%
				   	boolean hasRaccLinked = false; 
				   	sql = " SELECT ralk_raccountlinkid FROM raccountlinks "
						   + " WHERE ralk_foreignid = " + pmRaccounts.getInt("racc_raccountid");
				   
				   	pmRaccountLinks.doFetch(sql);
					if (pmRaccountLinks.next()) hasRaccLinked = true;
				   %>
				   
				   <%= HtmlUtil.formatReportCell(sFParams, ((hasRaccLinked) ? "Vinculada" : "Sin Vincular"), BmFieldType.STRING) %>
				   <%	if (enableBudgets) { %>
	        	   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
		           <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %>
		        	<%	} %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("users","user_code"), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_invoiceno")), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("orders","orde_code") + " " + pmRaccounts.getString("orders","orde_name")), BmFieldType.STRING) %>                    		   
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>
				   <%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoCustomer.getCustomercategory().getSelectedOption().getLabel()),BmFieldType.STRING) %>
				   <%	} %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_receivedate"), BmFieldType.DATE) %>                                                                                      
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_duedate"), BmFieldType.DATE) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getString("cureRacc","cure_code"), BmFieldType.CODE) %>
				   	<%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
		        		if (currencyIdOrigin != currencyIdDestiny) {
			    	%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
				    <%		
				    	} else { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getDouble("racc_currencyparity"), BmFieldType.NUMBER) %>
				    <%	}
			
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
			
				   amountTotal += amountRacc;
				   taxTotal += taxRacc; 
				   totalTotal += totalRacc;
				   paymentsTotal += paymentsRacc;
				   balanceTotal += balanceRacc;
				   %>
			
				   <%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + taxRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
	         </TR>
        	<%
        	// CXC RELACIONADAS
			int y = 1;
			//double amountTotal = 0, taxTotal = 0, totalTotal = 0, paymentsTotal = 0, balanceTotal = 0;
			sql = " SELECT * FROM raccountlinks "
					+ " LEFT JOIN raccounts r1 ON (r1.racc_raccountid = ralk_foreignid) "
					+ " LEFT JOIN raccounts r2 ON (r2.racc_raccountid = ralk_raccountid) "					
					+ " LEFT JOIN currencies cureRacc ON (r2.racc_currencyid = cureRacc.cure_currencyid) "
					+ " LEFT JOIN customers ON (r2.racc_customerid = cust_customerid)"
					+ " LEFT JOIN orders ON (r2.racc_orderid = orde_orderid)"
					+ " LEFT JOIN currencies cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) "
					+ " LEFT JOIN credits ON (orde_orderid = cred_orderid) "
					+ " LEFT JOIN users ON (orde_userid = user_userid) "
					+ " LEFT JOIN wflows ON (orde_wflowid = wflw_wflowid ) "
					+ " LEFT JOIN wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid) "
					+ " LEFT JOIN wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid) "
					+ " LEFT JOIN companies ON (r2.racc_companyid = comp_companyid)"
					+ " LEFT JOIN raccounttypes ON (r2.racc_raccounttypeid = ract_raccounttypeid)"
					+ " WHERE r2.racc_raccountid > 0 " 
					+ " AND ralk_foreignid = " + pmRaccounts.getInt("racc_raccountid")
					//+ where
					+ " ORDER BY r2.racc_invoiceno ASC ";
					System.out.println("sql2: "+sql);
					pmRaccountLinks.doFetch(sql);
			while (pmRaccountLinks.next()) {
				bmoRaccountLink.getStatus().setValue(pmRaccountLinks.getString("r2", "racc_status"));
				bmoRaccountLink.getPaymentStatus().setValue(pmRaccountLinks.getString("r2", "racc_paymentstatus"));
				
				if (pmRaccountLinks.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
		    		customer = pmRaccountLinks.getString("cust_code") + " " + pmRaccountLinks.getString("cust_legalname");	
	   			else
	   				customer = pmRaccountLinks.getString("cust_code") + " " + pmRaccountLinks.getString("cust_displayname");
				
				//Conversion a la moneda destino(seleccion del filtro)
			   	currencyIdOrigin = 0; currencyIdDestiny = 0;
			   	parityOrigin = 0; parityDestiny = 0;
			   	currencyIdOrigin = pmRaccountLinks.getInt("r2.racc_currencyid");
			   	parityOrigin = pmRaccountLinks.getDouble("r2.racc_currencyparity");
			   	currencyIdDestiny = currencyId;
			   	parityDestiny = defaultParity;
		%>
			   <tr class="reportCellEven">
				   <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccountLinks.getString("r2.racc_code"), BmFieldType.CODE) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccountLinks.getString("r1.racc_code"), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccountLinks.getString("users","user_code"), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccountLinks.getString("r2.racc_invoiceno")), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccountLinks.getString("orders","orde_code") + " " + pmRaccountLinks.getString("orders","orde_name")), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>   
<%-- 				   <%= HtmlUtil.formatReportCell(sFParams, " ", BmFieldType.STRING) %>       		    --%>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccountLinks.getString("r2.racc_receivedate"), BmFieldType.DATE) %>                                                                                      
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccountLinks.getString("r2.racc_duedate"), BmFieldType.DATE) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccountLink.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccountLink.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccountLinks.getString("cureRacc","cure_code"), BmFieldType.CODE) %>                    		   
				   
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccountLinks.getDouble("r2.racc_currencyparity"), BmFieldType.NUMBER) %>
				    <%
				   
			
				   double amountRaccLink = pmRaccountLinks.getDouble("r2.racc_amount");
				   double taxRaccLink = pmRaccountLinks.getDouble("r2.racc_tax");
				   double totalRaccLink = pmRaccountLinks.getDouble("r2.racc_total");
				   double paymentsRaccLink = pmRaccountLinks.getDouble("r2.racc_payments");					           		
				   double balanceRaccLink = pmRaccountLinks.getDouble("r2.racc_balance");
			
				   amountRaccLink = pmCurrencyExchange.currencyExchange(amountRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   taxRaccLink = pmCurrencyExchange.currencyExchange(taxRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   totalRaccLink = pmCurrencyExchange.currencyExchange(totalRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   paymentsRaccLink = pmCurrencyExchange.currencyExchange(paymentsRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   balanceRaccLink = pmCurrencyExchange.currencyExchange(balanceRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			
				   amountTotal += amountRaccLink;
				   taxTotal += taxRaccLink; 
				   totalTotal += totalRaccLink;
				   paymentsTotal += paymentsRaccLink;
				   balanceTotal += balanceRaccLink;
				   %>
			
				   <%= HtmlUtil.formatReportCell(sFParams, "" + amountRaccLink, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + taxRaccLink, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + totalRaccLink, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRaccLink, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + balanceRaccLink, BmFieldType.CURRENCY) %>
	         </TR>
	         
	         <%
			} //pmRaccountLinks
	         x++;
	       } // pmRaccounts
	       
	   		if(amountTotal > 0 || taxTotal > 0 || totalTotal > 0 || paymentsTotal > 0 || balanceTotal > 0) {
	   %>
	   			<tr><td colspan="<%=dynamicColspan + dynamicColspanMinus%>">&nbsp;</td></tr>
               <tr class="reportCellEven reportCellCode">
                    <td colspan="<%= (dynamicColspan)%>">&nbsp;</td>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal, BmFieldType.CURRENCY) %>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
               </tr>
	   <%
	   		}

    	%>
	    <tr><td colspan="<%=dynamicColspan %>">&nbsp;</td></tr>
<%
		} // Fin pmCurrencyWhile
	}
	// Si existe moneda destino
	else {

	    %>
		<tr class="">
			<td class="reportHeaderCellCenter">#</td><%dynamicColspan++; %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCode())) { dynamicColspan++; %>
				<td class="reportHeaderCell">CxC Externa</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCode())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Clave</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCode())) { dynamicColspan++; %>
				<td class="reportHeaderCell">CxC Relac.</td>
			<%	}%>	
			<%	if (enableBudgets) { dynamicColspan = dynamicColspan +2;%>
				<td class="reportHeaderCell">Partida Pres.</td>
		        <td class="reportHeaderCell">Departamento</td>
		    <% } %>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getUserId())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Vendedor</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getInvoiceno())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Factura</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getOrderId())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Pedido</td>
			<%	}%>	
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getCustomerId())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Cliente</td>
			<%	}%>	
			<%if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { dynamicColspan++; %>
				<td class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getCustomercategory()))%>
				</td>
			<%	}%> 
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getReceiveDate())) { dynamicColspan++; %>
				<td class="reportHeaderCell">F. Ingreso</td>
			<%	}%> 
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getDueDate())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Programaci&oacute;n</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getStatus())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Estatus</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getPaymentStatus())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Pago</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoCurrency.getCode())) { dynamicColspan++; %>
				<td class="reportHeaderCell">Moneda</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoCurrency.getParity())) { dynamicColspan++; %>
				<td class="reportHeaderCellCenter">Tipo de Cambio</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getAmount())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">Monto</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getTax())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">IVA</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getTotal())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">Total</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getPayments())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">Pagos</td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoRaccount.getBalance())) { dynamicColspanMinus++; %>
				<td class="reportHeaderCellRight">Saldo</td>
			<%	}%>
		</tr>
		<%
			int x = 1;
			double amountTotal = 0, taxTotal = 0, totalTotal = 0, paymentsTotal = 0, balanceTotal = 0;
			sql = " SELECT * FROM raccounts ";
			if (enableBudgets) {
		    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bgit_budgetitemid = racc_budgetitemid) " 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")	+ " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
		    		+ " LEFT JOIN "	+ SQLUtil.formatKind(sFParams, "budgets") + " ON (budg_budgetid = bgit_budgetid) "
		    		+" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = racc_areaid) ";
		    	}
			sql +=	" LEFT JOIN currencies cureRacc ON (racc_currencyid = cureRacc.cure_currencyid) "
					+ " LEFT JOIN customers ON (racc_customerid = cust_customerid)"
					+ " LEFT JOIN orders ON (racc_orderid = orde_orderid)"
					+ " LEFT JOIN currencies cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) "
					+ " LEFT JOIN credits ON (orde_orderid = cred_orderid) "
					+ " LEFT JOIN users ON (orde_userid = user_userid) "
					+ " LEFT JOIN wflows ON (orde_wflowid = wflw_wflowid ) "
					+ " LEFT JOIN wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid) "
					+ " LEFT JOIN wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid) "
					+ " LEFT JOIN companies ON (racc_companyid = comp_companyid)"
					+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
					+ " WHERE racc_raccountid > 0 " 
					+ " AND racc_linked = 1 "
					+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
					+ whereOrderId
					+ where
					+ " ORDER BY racc_invoiceno ASC ";
					
					System.out.println("sql1: "+sql);

			pmRaccounts.doFetch(sql);
			while (pmRaccounts.next()) {
				
				bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts", "racc_status"));
				bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts", "racc_paymentstatus"));
				bmoCustomer.getCustomercategory().setValue(pmRaccounts.getString("customers", "cust_customercategory"));
				if (pmRaccounts.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
		    		customer = pmRaccounts.getString("cust_code") + " " + pmRaccounts.getString("cust_legalname");	
	   			else
	   				customer = pmRaccounts.getString("cust_code") + " " + pmRaccounts.getString("cust_displayname");
				
				//Conversion a la moneda destino(seleccion del filtro)
			   	int currencyIdOrigin = 0, currencyIdDestiny = 0;
			   	double parityOrigin = 0, parityDestiny = 0;
			   	currencyIdOrigin = pmRaccounts.getInt("racc_currencyid");
			   	parityOrigin = pmRaccounts.getDouble("racc_currencyparity");
			   	currencyIdDestiny = currencyId;
			   	parityDestiny = defaultParity;
		%>
			   <tr class="reportCellEven">
				   <%= HtmlUtil.formatReportCell(sFParams, "" + x, BmFieldType.NUMBER) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_code"), BmFieldType.CODE) %>
				   <%
				   	boolean hasRaccLinked = false; 
				   	sql = " SELECT ralk_raccountlinkid FROM raccountlinks "
						   + " WHERE ralk_foreignid = " + pmRaccounts.getInt("racc_raccountid");
				   
				   	pmRaccountLinks.doFetch(sql);
					if (pmRaccountLinks.next()) hasRaccLinked = true;
				   %>
				   
				   <%= HtmlUtil.formatReportCell(sFParams, ((hasRaccLinked) ? "Vinculada" : "Sin Vincular"), BmFieldType.STRING) %>
				   <%	if (enableBudgets) { %>
	        	   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("budgets", "budg_name")+" : "+ pmRaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
		           <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("areas","area_name"), BmFieldType.CODE) %>
		        	<%	} %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("users","user_code"), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("raccounts","racc_invoiceno")), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccounts.getString("orders","orde_code") + " " + pmRaccounts.getString("orders","orde_name")), BmFieldType.STRING) %>                    		   
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>
				   <%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) { %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoCustomer.getCustomercategory().getSelectedOption().getLabel()),BmFieldType.STRING) %>
				   <%	} %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_receivedate"), BmFieldType.DATE) %>                                                                                      
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_duedate"), BmFieldType.DATE) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getString("cureRacc","cure_code"), BmFieldType.CODE) %>
				   	<%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
		        		if (currencyIdOrigin != currencyIdDestiny) {
			    	%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
				    <%		
				    	} else { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccounts.getDouble("racc_currencyparity"), BmFieldType.NUMBER) %>
				    <%	}
			
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
			
				   amountTotal += amountRacc;
				   taxTotal += taxRacc; 
				   totalTotal += totalRacc;
				   paymentsTotal += paymentsRacc;
				   balanceTotal += balanceRacc;
				   %>
			
				   <%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + taxRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + totalRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRacc, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + balanceRacc, BmFieldType.CURRENCY) %>
	         </TR>
        	<%
        	// CXC RELACIONADAS
			int y = 1;
			//double amountTotal = 0, taxTotal = 0, totalTotal = 0, paymentsTotal = 0, balanceTotal = 0;
			sql = " SELECT * FROM raccountlinks "
					+ " LEFT JOIN raccounts r1 ON (r1.racc_raccountid = ralk_foreignid) "
					+ " LEFT JOIN raccounts r2 ON (r2.racc_raccountid = ralk_raccountid) "					
					+ " LEFT JOIN currencies cureRacc ON (r1.racc_currencyid = cureRacc.cure_currencyid) "
					+ " LEFT JOIN customers ON (r2.racc_customerid = cust_customerid)"
					+ " LEFT JOIN orders ON (r2.racc_orderid = orde_orderid)"
					+ " LEFT JOIN currencies cureOrder ON (orde_currencyid = cureOrder.cure_currencyid) "
					+ " LEFT JOIN credits ON (orde_orderid = cred_orderid) "
					+ " LEFT JOIN users ON (orde_userid = user_userid) "
					+ " LEFT JOIN wflows ON (orde_wflowid = wflw_wflowid ) "
					+ " LEFT JOIN wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid) "
					+ " LEFT JOIN wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid) "
					+ " LEFT JOIN companies ON (r2.racc_companyid = comp_companyid)"
					+ " LEFT JOIN raccounttypes ON (r2.racc_raccounttypeid = ract_raccounttypeid)"
					+ " WHERE r2.racc_raccountid > 0 " 
					+ " AND ralk_foreignid = " + pmRaccounts.getInt("racc_raccountid")
					//+ where
					+ " ORDER BY r2.racc_invoiceno ASC ";
					System.out.println("sql2: "+sql);
					pmRaccountLinks.doFetch(sql);
			while (pmRaccountLinks.next()) {
				bmoRaccountLink.getStatus().setValue(pmRaccountLinks.getString("r2", "racc_status"));
				bmoRaccountLink.getPaymentStatus().setValue(pmRaccountLinks.getString("r2", "racc_paymentstatus"));
				
				if (pmRaccountLinks.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
		    		customer = pmRaccountLinks.getString("cust_code") + " " + pmRaccountLinks.getString("cust_legalname");	
	   			else
	   				customer = pmRaccountLinks.getString("cust_code") + " " + pmRaccountLinks.getString("cust_displayname");
				
				//Conversion a la moneda destino(seleccion del filtro)
			   	currencyIdOrigin = 0; currencyIdDestiny = 0;
			   	parityOrigin = 0; parityDestiny = 0;
			   	currencyIdOrigin = pmRaccountLinks.getInt("r2.racc_currencyid");
			   	parityOrigin = pmRaccountLinks.getDouble("r2.racc_currencyparity");
			   	currencyIdDestiny = currencyId;
			   	parityDestiny = defaultParity;
		%>
			   <tr class="reportCellEven">
				   <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccountLinks.getString("r2.racc_code"), BmFieldType.CODE) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccountLinks.getString("r1.racc_code"), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccountLinks.getString("users","user_code"), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccountLinks.getString("r2.racc_invoiceno")), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmRaccountLinks.getString("orders","orde_code") + " " + pmRaccountLinks.getString("orders","orde_name")), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>       
<%-- 				   <%= HtmlUtil.formatReportCell(sFParams, " ", BmFieldType.STRING) %>    --%>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccountLinks.getString("r2.racc_receivedate"), BmFieldType.DATE) %>                                                                                      
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccountLinks.getString("r2.racc_duedate"), BmFieldType.DATE) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccountLink.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccountLink.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccountLinks.getString("cureRacc","cure_code"), BmFieldType.CODE) %>                    		   
				   <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
		        		if (currencyIdOrigin != currencyIdDestiny) {
			    	%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
				    <%		
				    	} else { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmRaccountLinks.getDouble("r2.racc_currencyparity"), BmFieldType.NUMBER) %>
				    <%	}
				   
			
				   double amountRaccLink = pmRaccountLinks.getDouble("r2.racc_amount");
				   double taxRaccLink = pmRaccountLinks.getDouble("r2.racc_tax");
				   double totalRaccLink = pmRaccountLinks.getDouble("r2.racc_total");
				   double paymentsRaccLink = pmRaccountLinks.getDouble("r2.racc_payments");					           		
				   double balanceRaccLink = pmRaccountLinks.getDouble("r2.racc_balance");
			
				   amountRaccLink = pmCurrencyExchange.currencyExchange(amountRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   taxRaccLink = pmCurrencyExchange.currencyExchange(taxRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   totalRaccLink = pmCurrencyExchange.currencyExchange(totalRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   paymentsRaccLink = pmCurrencyExchange.currencyExchange(paymentsRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				   balanceRaccLink = pmCurrencyExchange.currencyExchange(balanceRaccLink, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			
				   amountTotal += amountRaccLink;
				   taxTotal += taxRaccLink; 
				   totalTotal += totalRaccLink;
				   paymentsTotal += paymentsRaccLink;
				   balanceTotal += balanceRaccLink;
				   %>
			
				   <%= HtmlUtil.formatReportCell(sFParams, "" + amountRaccLink, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + taxRaccLink, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + totalRaccLink, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsRaccLink, BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, "" + balanceRaccLink, BmFieldType.CURRENCY) %>
	         </TR>
	         
	         <%
			}
	         x++;
	       }
	       
	   		if(amountTotal > 0 || taxTotal > 0 || totalTotal > 0 || paymentsTotal > 0 || balanceTotal > 0) {
	   %>
	   			<tr><td colspan="<%= (dynamicColspan + dynamicColspanMinus)%>">&nbsp;</td></tr>
               <tr class="reportCellEven reportCellCode">
                    <td colspan="<%= (dynamicColspan)%>">&nbsp;</td>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal, BmFieldType.CURRENCY) %>
                    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
               </tr>
	   <%
	   		}
	} // Fin else moneda
	%>
</TABLE>  
<%
	
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmConn.close();
	pmRaccounts.close();
	pmRaccountLinks.close(); 
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
	}// FIN DEL CONTADOR%>
  </body>
</html>