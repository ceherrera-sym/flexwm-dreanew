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
 
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.server.op.PmRequisition"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
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
    String title = "Reporte de O.C. con Anticipo Proveedor";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	BmoRequisition bmoRequisition = new BmoRequisition();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoBankMovement bmoBankMovement = new BmoBankMovement();
    BmoBankMovType bmoBankMovType = new BmoBankMovType();
	
	String filters = "", sql = "", sqlCurrency = "", where = "", reqiStatus = "", lockstatus = "", deliveryStatus = "", paymentStatus = "",
			deliveryStartDate = "", deliveryEndDate = "", requestStartDate = "", requestEndDate = "", dueStartDate = "", dueEndDate = "",
			userName = "", requestStatus = "", areaId = "";

    int programId = 0, userId = 0, supplierId = 0, cols = 0, type = 0, budgetId = 0, budgetItemId = 0, companyId = 0, orderId = 0, currencyId = 0, activeBudgets = 0;
    double  nowParity = 0, defaultParity = 0;
    boolean enableBudgets = false;
   	
   	// se agrega 2 columnas para presupuestos, para manejo de colspans
   	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
   		activeBudgets = 2;
   	}

    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("reqi_requestedby") != null) userId = Integer.parseInt(request.getParameter("reqi_requestedby"));
    if (request.getParameter("reqi_areaid") != null) areaId = request.getParameter("reqi_areaid");
    if (request.getParameter("reqi_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("reqi_supplierid"));
    if (request.getParameter("reqi_requisitiontypeid") != null) type =  Integer.parseInt(request.getParameter("reqi_requisitiontypeid"));
    if (request.getParameter("reqi_status") != null) reqiStatus = request.getParameter("reqi_status");    
    if (request.getParameter("reqi_deliverystatus") != null) deliveryStatus = request.getParameter("reqi_deliverystatus");
    if (request.getParameter("reqi_paymentstatus") != null) paymentStatus = request.getParameter("reqi_paymentstatus");    
    if (request.getParameter("deliverystart") != null) deliveryStartDate = request.getParameter("deliverystart");
    if (request.getParameter("deliveryEnd") != null) deliveryEndDate = request.getParameter("deliveryEnd");
    if (request.getParameter("requeststart") != null) requestStartDate = request.getParameter("requeststart");
    if (request.getParameter("requestEnd") != null) requestEndDate = request.getParameter("requestEnd");
    if (request.getParameter("duestart") != null) dueStartDate = request.getParameter("duestart");
    if (request.getParameter("dueEnd") != null) dueEndDate = request.getParameter("dueEnd");
    if (request.getParameter("reqi_companyid") != null) companyId = Integer.parseInt(request.getParameter("reqi_companyid"));
    if (request.getParameter("reqi_orderid") != null) orderId = Integer.parseInt(request.getParameter("reqi_orderid"));
    if (request.getParameter("reqi_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("reqi_currencyid"));
    if (enableBudgets) {
	    if (request.getParameter("bgit_budgetid") != null) budgetId = Integer.parseInt(request.getParameter("bgit_budgetid"));
	    if (request.getParameter("reqi_budgetitemid") != null) budgetItemId = Integer.parseInt(request.getParameter("reqi_budgetitemid"));
    }
    
    // Filtros listados
    if (userId > 0) {
    	where += " AND reqi_requestedby = " + userId;
    	filters += "<i>Solicitado Por: </i>" + request.getParameter("reqi_requestedbyLabel") + ", ";
    }
    
    if (supplierId > 0) {
        where += " AND reqi_supplierid = " + supplierId;
        filters += "<i>Proveedor: </i>" + request.getParameter("reqi_supplieridLabel") + ", ";
    }
    
    if (!areaId.equals("")) {
        //where += " and reqi_areaid = " + areaId;
        where += SFServerUtil.parseFiltersToSql("reqi_areaid", areaId);        
        filters += "<i>Depto.: </i>" + request.getParameter("reqi_areaidLabel") + ", ";
    }    
    
    if (type > 0) {
        where += " AND rqtp_requisitiontypeid = " + type ;
        filters += "<i>Tipo: </i>" + request.getParameter("reqi_requisitiontypeidLabel") + ", ";
    }
    
    if (!reqiStatus.equals("")) {
        //where += " and reqi_status like '" + reqiStatus + "'";
        where += SFServerUtil.parseFiltersToSql("reqi_status", reqiStatus);
        filters += "<i>Estatus: </i>" + request.getParameter("reqi_statusLabel") + ", ";
    }   
    
    if (!deliveryStatus.equals("")) {
        //where += " and reqi_deliverystatus like '" + deliveryStatus + "'";
        where += SFServerUtil.parseFiltersToSql("reqi_deliverystatus", deliveryStatus);
        filters += "<i>Entrega: </i>" + request.getParameter("reqi_deliverystatusLabel") + ", ";
    }
    
    if (!paymentStatus.equals("")) {
        //where += " and reqi_paymentstatus like '" + paymentStatus + "'";
        where += SFServerUtil.parseFiltersToSql("reqi_paymentstatus", paymentStatus);

        filters += "<i>Pago: </i>" + request.getParameter("reqi_paymentstatusLabel") + ", ";
    }
        
    if (!requestStartDate.equals("")) {
        where += " AND reqi_requestdate >= '" + requestStartDate + "'";
        filters += "<i>F. Solicitud Inicio: </i>" + requestStartDate + ", ";
    }
    
    if (!requestEndDate.equals("")) {
        where += " AND reqi_requestdate <= '" + requestEndDate + "'";
        filters += "<i>F. Solicitud Final: </i>" + requestEndDate + ", ";
    }
    
    if (!deliveryStartDate.equals("")) {
        where += " AND reqi_deliverydate >= '" + deliveryStartDate + "'";
        filters += "<i>F. Entrega Inicio: </i>" + deliveryStartDate + ", ";
    }
    
    if (!deliveryEndDate.equals("")) {
        where += " AND reqi_deliverydate <= '" + deliveryEndDate + "'";
        filters += "<i>F. Entrega Final: </i>" + deliveryEndDate + ", ";
    }
    
    if (!dueStartDate.equals("")) {
        where += " AND reqi_paymentdate >= '" + dueStartDate + "'";
        filters += "<i>F. Pago Inicio: </i>" + dueStartDate + ", ";
    }
    
    if (!dueEndDate.equals("")) {
        where += " AND reqi_paymentdate <= '" + dueEndDate + "'";
        filters += "<i>F. Pago Final: </i>" + dueEndDate + ", ";
    }
    
    if (enableBudgets) {
	    if (budgetId > 0) {
	        where += " AND bgit_budgetid = " + budgetId;
	        filters += "<i>Presup.: </i>" + request.getParameter("bgit_budgetidLabel") + ", ";
	    }  
	
	    if (budgetItemId > 0) {
	        where += " AND reqi_budgetitemid = " + budgetItemId;
	        filters += "<i>Item Presup.: </i>" + request.getParameter("reqi_budgetitemidLabel") + ", ";
	    }
    }
    
    if (companyId > 0) {
        where += " AND reqi_companyid = " + companyId;
        filters += "<i>Empresa: </i>" + request.getParameter("reqi_companyidLabel") + ", ";
    }
    
    if (orderId > 0) {
        where += " AND reqi_orderid = " + orderId;
        filters += "<i>Pedido: </i>" + request.getParameter("reqi_orderidLabel") + ", ";
    }
	
   	if (currencyId > 0) {
   		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);   		
   		defaultParity = bmoCurrency.getParity().toDouble();
   		filters += "<i>Moneda: </i>" + request.getParameter("reqi_currencyidLabel") + " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
   	} else {
   		//Obtener la paridad de la moneda del sistema
//   		bmoCurrency = (BmoCurrency)pmCurrency.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getCurrencyId().toInteger());
//   		defaultParity = bmoCurrency.getParity().toDouble();
//   		filters += "<i>Moneda: </i>" + bmoCurrency.getCode().toString() + " | " + bmoCurrency.getName().toString() + ", ";
   		filters += "<i>Moneda: </i> Todas ";
		sqlCurrency = "SELECT cure_currencyid, cure_code, cure_name FROM " + SQLUtil.formatKind(sFParams, " currencies ");
   	}
   	
	// Obtener disclosure de datos
    String disclosureFilters = new PmRequisition(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
	
    PmConn pmRequisition = new PmConn(sFParams);
    pmRequisition.open();
    
    PmConn pmBkmv = new PmConn(sFParams);
    pmBkmv.open();
    
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

<table breqir="0" cellspacing="0" cellpading="0" width="100%">
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
            <br>
			<b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Orden de Compra" : "Orden de Compra")%>
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report">
	<%
	

	sql = " SELECT count(contador) AS contador FROM(Select Count(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes")+" ON (bkmt_bankmovtypeid= bkmv_bankmovtypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts")+" ON (bkac_bankaccountid= bkmv_bankaccountid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" bkacCurrency ON (bkac_currencyid = bkacCurrency.cure_currencyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitions")+" ON (reqi_requisitionid = bkmv_requisitionid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" reqiCurrency ON (reqi_currencyid = reqiCurrency.cure_currencyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitiontypes")+" ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON (area_areaid = reqi_areaid)" +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (orde_orderid = reqi_orderid)" +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (comp_companyid = reqi_companyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (user_userid = reqi_requestedby) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" ON (supl_supplierid = reqi_supplierid) ";
	if (enableBudgets) {
		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid)" +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" ;
	}
	sql += " WHERE bkmv_requisitionid > 0 " +
			where + 
			" AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "' " +
			" GROUP BY reqi_requisitionid" +
			" ORDER BY reqi_requisitionid ASC)AS alias";
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
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			sql = " SELECT reqi_requisitionid, reqi_code, reqi_name, reqi_currencyid, reqi_currencyparity, reqi_total, " +
					" reqi_status, reqi_deliverystatus, reqi_paymentstatus, rqtp_type, " + 
					" reqiCurrency.cure_code, " +
					" supl_code, supl_name, user_code ";
			if (enableBudgets) {
				sql += ", budg_name, bgty_name ";
			}
			sql +=  " FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes")+" ON (bkmt_bankmovtypeid= bkmv_bankmovtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts")+" ON (bkac_bankaccountid= bkmv_bankaccountid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" bkacCurrency ON (bkac_currencyid = bkacCurrency.cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitions")+" ON (reqi_requisitionid = bkmv_requisitionid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" reqiCurrency ON (reqi_currencyid = reqiCurrency.cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitiontypes")+" ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON (area_areaid = reqi_areaid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (orde_orderid = reqi_orderid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (comp_companyid = reqi_companyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users ")+" ON (user_userid = reqi_requestedby) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" ON (supl_supplierid = reqi_supplierid) ";
			if (enableBudgets) {
				sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid)" +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" ;
			}
			sql += " WHERE bkmv_requisitionid > 0 " +
					" AND reqi_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
					where + 
					" AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "' " +
					" GROUP BY reqi_requisitionid" +
					" ORDER BY reqi_requisitionid ASC";
			
			pmRequisition.doFetch(sql);
			
			double withdrawTotal = 0;
			int i = 1, reqiId = 0;
			
			while (pmRequisition.next()) {
				
				if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
            		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
            		y = 0;
            		%>
            		<tr>
	            		<td class="reportHeaderCellCenter" colspan="10">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
            		</tr>
            		<%
            	}
				
				//Estatus OC
				bmoRequisition.getStatus().setValue(pmRequisition.getString("requisitions", "reqi_status"));
				bmoRequisition.getDeliveryStatus().setValue(pmRequisition.getString("requisitions", "reqi_deliverystatus"));
				bmoRequisition.getPaymentStatus().setValue(pmRequisition.getString("requisitions", "reqi_paymentstatus"));
				bmoRequisition.getBmoRequisitionType().getType().setValue(pmRequisition.getString("requisitiontypes", "rqtp_type"));
				
				//Conversion de la OC a la moneda destino(seleccion del filtro) 
		        int currencyIdOrigin = 0, currencyIdDestiny = 0;
		        double parityOrigin = 0, parityDestiny = 0;
		        currencyIdOrigin = pmRequisition.getInt("reqi_currencyid");
		        parityOrigin = pmRequisition.getDouble("reqi_currencyparity");
		        currencyIdDestiny = currencyId;
		        parityDestiny = defaultParity;
				%>
				<tr class="reportCellEven">
					<td colspan="10" class="reportGroupCell">
						#<%= i%> |
						<%= pmRequisition.getString("reqi_code") %>
						<%= HtmlUtil.stringToHtml(pmRequisition.getString("reqi_name")) %> |
						Prov: <%= pmRequisition.getString("supl_code") %> <%= HtmlUtil.stringToHtml(pmRequisition.getString("supl_name")) %> |
						Por: <%= HtmlUtil.stringToHtml(pmRequisition.getString("user_code")) %> |
						<%
						if (enableBudgets) {
						%>
							Presup: <%= pmRequisition.getString("budg_name") %> |
							Item Presup: <%= pmRequisition.getString("bgty_name") %> |
						<%
						}
						%>
						Estatus: <%= HtmlUtil.stringToHtml(bmoRequisition.getStatus().getSelectedOption().getLabel()) %> |
						<!-- Entrega: <%//= HtmlUtil.stringToHtml(bmoRequisition.getDeliveryStatus().getSelectedOption().getLabel()) %> |-->
						Pago: <%= HtmlUtil.stringToHtml(bmoRequisition.getPaymentStatus().getSelectedOption().getLabel()) %> |
						Tipo de Cambio: <%= pmRequisition.getString("reqi_currencyparity") %> |
						<span title='Origen: <%= SFServerUtil.formatCurrency(pmRequisition.getDouble("reqi_total"))%> <%= pmRequisition.getString("reqiCurrency.cure_code") %>'>
							Total: <%=SFServerUtil.formatCurrency(pmRequisition.getDouble("reqi_total")) %> <%= pmRequisition.getString("reqiCurrency.cure_code") %>
						</span>
					</td>
				</tr>
				<tr>
					<td class="reportHeaderCell">Clave MB</td>
					<td class="reportHeaderCell">Descripci&oacute;n</td>
					<td class="reportHeaderCell">Tipo Movimiento</td>
					<td class="reportHeaderCell">F. Registro</td>
					<td class="reportHeaderCell">F. Pago</td>
					<td class="reportHeaderCell">Estatus</td>
					<td class="reportHeaderCell">Cuenta de Banco</td>
					<td class="reportHeaderCell">Moneda</td>
					<td class="reportHeaderCellCenter">Tipo de Cambio</td>
					<td class="reportHeaderCellRight">Monto Anticipo</td>
				</tr>
				<%
				String sqlBkmv = " SELECT bkmv_code, bkmv_description, bkmt_name, bkmv_inputdate, bkmv_duedate, bkmv_status, bkac_name, bkacCurrency.cure_code, " +
						"bkmv_currencyparity, bkmv_withdraw, bkac_currencyid" +
		
						" FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes")+" ON (bkmt_bankmovtypeid= bkmv_bankmovtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts")+" ON (bkac_bankaccountid= bkmv_bankaccountid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" bkacCurrency ON (bkac_currencyid = bkacCurrency.cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitions")+" ON (reqi_requisitionid = bkmv_requisitionid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" reqiCurrency ON (reqi_currencyid = reqiCurrency.cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitiontypes")+" ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON (area_areaid = reqi_areaid)" +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (orde_orderid = reqi_orderid)" +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (comp_companyid = reqi_companyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (user_userid = reqi_requestedby) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" ON (supl_supplierid = reqi_supplierid) ";
				if (enableBudgets) {
					sqlBkmv += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid)" +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" ;
				}
				sqlBkmv += " WHERE bkmv_requisitionid > 0 " +
						" AND reqi_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
						" AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "' " +
						" AND reqi_requisitionid =  " + pmRequisition.getInt("requisitions", "reqi_requisitionid") +
						" ORDER BY reqi_requisitionid ASC";
			
				double withdrawSum = 0;
				pmBkmv.doFetch(sqlBkmv);
				while (pmBkmv.next()) {
					bmoBankMovement.getStatus().setValue(pmBkmv.getString("bkmv_status"));
					
					%>
					<tr class="reportCellEven">
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmv_code"), BmFieldType.CODE) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmv_description"), BmFieldType.STRING)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmt_name"), BmFieldType.STRING)) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmv_inputdate"), BmFieldType.DATE) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmv_duedate"), BmFieldType.DATE) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoBankMovement.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkac_name"), BmFieldType.STRING)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkacCurrency.cure_code"), BmFieldType.CODE)) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getDouble("bkmv_currencyparity"), BmFieldType.NUMBER) %>
						<%
						double withdraw = pmBkmv.getDouble("bkmv_withdraw");
			          	
			          	//Conversion a la moneda destino(seleccion del filtro)
			          	currencyIdOrigin = 0; currencyIdDestiny = 0;
			          	parityOrigin = 0; parityDestiny = 0;
			          	currencyIdOrigin = pmBkmv.getInt("bkac_currencyid");
			          	
			          	// la moneda de la cuenta de banco es igual a la de la oc, como no guarda paridad en anticipoProv si la moneda es igual
			          	// tomar la paridad actual de la moneda del banco
			          	if (currencyIdOrigin == pmRequisition.getInt("reqi_currencyid")) {
			          		BmoCurrency bmoCurrencyTemp = (BmoCurrency)pmCurrency.get(currencyIdOrigin);
			          		parityOrigin = bmoCurrencyTemp.getParity().toDouble();
			          	} else 
			          		parityOrigin = pmBkmv.getDouble("bkmv_currencyparity");
			          	
			          	currencyIdDestiny = currencyId;
			          	parityDestiny = defaultParity;
			          	
			          	%>
						<%=HtmlUtil.formatReportCell(sFParams, "" + withdraw, BmFieldType.CURRENCY) %>		        			
					</tr>
					<%
					//suma de anticipos a prov.
					withdrawSum += withdraw; 
					withdrawTotal += withdraw;
		
					i++;
				}//while pmBkmv
			
				%> 
				<tr class = "reportCellEven reportCellCode">
					<td colspan="9">&nbsp;</td>
					<%= HtmlUtil.formatReportCell(sFParams, "" + withdrawSum, BmFieldType.CURRENCY) %>
				</tr>
			<%
			} //while pmRequisition
			%>   
			<% 	
			if (withdrawTotal > 0) { %>
				<tr class=""><td colspan="10">&nbsp;</td></tr>
				<tr class="reportCellEven reportCellCode">
					<td colspan="9">&nbsp;</td>
					<%= HtmlUtil.formatReportCell(sFParams, "" + withdrawTotal, BmFieldType.CURRENCY) %>
				</tr>
		<%	} %>
		<tr class=""><td colspan="10">&nbsp;</td></tr>
		<%
		}	// Fin pmCurrencyWhile
	}	// Fin de no existe moneda 
	// Existe moneda destino
	else {
		sql = " SELECT reqi_requisitionid, reqi_code, reqi_name, reqi_currencyid, reqi_currencyparity, reqi_total, " +
				" reqi_status, reqi_deliverystatus, reqi_paymentstatus, rqtp_type, " + 
				" reqiCurrency.cure_code, " +
				" supl_code, supl_name, user_code ";
		if (enableBudgets) {
			sql += ", budg_name, bgty_name ";
		}
		sql +=  " FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes")+" ON (bkmt_bankmovtypeid= bkmv_bankmovtypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts")+" ON (bkac_bankaccountid= bkmv_bankaccountid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" bkacCurrency ON (bkac_currencyid = bkacCurrency.cure_currencyid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitions")+" ON (reqi_requisitionid = bkmv_requisitionid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" reqiCurrency ON (reqi_currencyid = reqiCurrency.cure_currencyid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitiontypes")+" ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON (area_areaid = reqi_areaid)" +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON (orde_orderid = reqi_orderid)" +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" ON (comp_companyid = reqi_companyid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (user_userid = reqi_requestedby) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" ON (supl_supplierid = reqi_supplierid) ";
		if (enableBudgets) {
			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets")+" ON (bgit_budgetid = budg_budgetid)" ;
		}
		sql += " WHERE bkmv_requisitionid > 0 " +
				where + 
				" AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "' " +
				" GROUP BY reqi_requisitionid" +
				" ORDER BY reqi_requisitionid ASC";
		
		pmRequisition.doFetch(sql);
		
		double withdrawTotal = 0;
		int i = 1, reqiId = 0;
		
		while (pmRequisition.next()) {
			//Estatus OC
			bmoRequisition.getStatus().setValue(pmRequisition.getString("requisitions", "reqi_status"));
			bmoRequisition.getDeliveryStatus().setValue(pmRequisition.getString("requisitions", "reqi_deliverystatus"));
			bmoRequisition.getPaymentStatus().setValue(pmRequisition.getString("requisitions", "reqi_paymentstatus"));
			bmoRequisition.getBmoRequisitionType().getType().setValue(pmRequisition.getString("requisitiontypes", "rqtp_type"));
			
			//Conversion de la OC a la moneda destino(seleccion del filtro) 
	        int currencyIdOrigin = 0, currencyIdDestiny = 0;
	        double parityOrigin = 0, parityDestiny = 0;
	        currencyIdOrigin = pmRequisition.getInt("reqi_currencyid");
	        parityOrigin = pmRequisition.getDouble("reqi_currencyparity");
	        currencyIdDestiny = currencyId;
	        parityDestiny = defaultParity;
			%>
			
			
			<tr class="reportCellEven">
				<td colspan="10" class="reportGroupCell">
					#<%= i%> |
					<%= pmRequisition.getString("reqi_code") %>
					<%= HtmlUtil.stringToHtml(pmRequisition.getString("reqi_name")) %> |
					Prov: <%= pmRequisition.getString("supl_code") %> <%= HtmlUtil.stringToHtml(pmRequisition.getString("supl_name")) %> |
					Por: <%= pmRequisition.getString("user_code") %> |
					<%
					if (enableBudgets) {
					%>
						Presup: <%= pmRequisition.getString("budg_name") %> |
						Item Presup: <%= pmRequisition.getString("bgty_name") %> |
					<%
					}
					%>
					Estatus: <%= HtmlUtil.stringToHtml(bmoRequisition.getStatus().getSelectedOption().getLabel()) %> |
					<!-- Entrega: <%//= HtmlUtil.stringToHtml(bmoRequisition.getDeliveryStatus().getSelectedOption().getLabel()) %> |-->
					Pago: <%= HtmlUtil.stringToHtml(bmoRequisition.getPaymentStatus().getSelectedOption().getLabel()) %> |
					Tipo de Cambio: <%= pmRequisition.getString("reqi_currencyparity") %> |
					<span title='Origen: <%= SFServerUtil.formatCurrency(pmRequisition.getDouble("reqi_total"))%> <%= pmRequisition.getString("reqiCurrency.cure_code") %>'>
						Total: <%=SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(pmRequisition.getDouble("reqi_total"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny)) %>
						<%= bmoCurrency.getCode().toString()%>
					</span>
				</td>
			</tr>
			<tr>
				<td class="reportHeaderCell">Clave MB</td>
				<td class="reportHeaderCell">Descripci&oacute;n</td>
				<td class="reportHeaderCell">Tipo Movimiento</td>
				<td class="reportHeaderCell">F. Registro</td>
				<td class="reportHeaderCell">F. Pago</td>
				<td class="reportHeaderCell">Estatus</td>
				<td class="reportHeaderCell">Cuenta de Banco</td>
				<td class="reportHeaderCell">Moneda</td>
				<td class="reportHeaderCellCenter">Tipo de Cambio</td>
				<td class="reportHeaderCellRight">Monto Anticipo</td>
			</tr>
			<%
			String sqlBkmv = " SELECT  bkmv_code, bkmv_description, bkmt_name, bkmv_inputdate, bkmv_duedate, bkmv_status, bkac_name, bkacCurrency.cure_code, " +
					"bkmv_currencyparity, bkmv_withdraw, bkac_currencyid" +
	
					" FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankmovtypes")+" ON (bkmt_bankmovtypeid= bkmv_bankmovtypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankaccounts")+" ON (bkac_bankaccountid= bkmv_bankaccountid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" bkacCurrency ON (bkac_currencyid = bkacCurrency.cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (reqi_requisitionid = bkmv_requisitionid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" reqiCurrency ON (reqi_currencyid = reqiCurrency.cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" on (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" on (area_areaid = reqi_areaid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (orde_orderid = reqi_orderid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (comp_companyid = reqi_companyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (user_userid = reqi_requestedby) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = reqi_supplierid) ";
			if (enableBudgets) {
				sqlBkmv += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (reqi_budgetitemid = bgit_budgetitemid)" +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" on (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (bgit_budgetid = budg_budgetid)" ;
			}
			sqlBkmv += " WHERE bkmv_requisitionid > 0 " +
					" AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "' " +
					" AND reqi_requisitionid =  " + pmRequisition.getInt("requisitions", "reqi_requisitionid") +
					" ORDER BY reqi_requisitionid ASC";
		
			double withdrawSum = 0;
			pmBkmv.doFetch(sqlBkmv);
			while (pmBkmv.next()) {
				bmoBankMovement.getStatus().setValue(pmBkmv.getString("bkmv_status"));
				
				%>
				<tr class="reportCellEven">
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmv_code"), BmFieldType.CODE) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmv_description"), BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmt_name"), BmFieldType.STRING)) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmv_inputdate"), BmFieldType.DATE) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkmv_duedate"), BmFieldType.DATE) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoBankMovement.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkac_name"), BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getString("bkacCurrency.cure_code"), BmFieldType.CODE)) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmBkmv.getDouble("bkmv_currencyparity"), BmFieldType.NUMBER) %>
					<%
					double withdraw = pmBkmv.getDouble("bkmv_withdraw");
		          	
		          	//Conversion a la moneda destino(seleccion del filtro)
		          	currencyIdOrigin = 0; currencyIdDestiny = 0;
		          	parityOrigin = 0; parityDestiny = 0;
		          	currencyIdOrigin = pmBkmv.getInt("bkac_currencyid");
		          	
		          	// la moneda de la cuenta de banco es igual a la de la oc, como no guarda paridad en anticipoProv si la moneda es igual
		          	// tomar la paridad actual de la moneda del banco
		          	if (currencyIdOrigin == pmRequisition.getInt("reqi_currencyid")) {
		          		BmoCurrency bmoCurrencyTemp = (BmoCurrency)pmCurrency.get(currencyIdOrigin);
		          		parityOrigin = bmoCurrencyTemp.getParity().toDouble();
		          	} else 
		          		parityOrigin = pmBkmv.getDouble("bkmv_currencyparity");
		          	
		          	currencyIdDestiny = currencyId;
		          	parityDestiny = defaultParity;
		          	
		          	withdraw = pmCurrencyExchange.currencyExchange(withdraw, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					%>
					<%=HtmlUtil.formatReportCell(sFParams, "" + withdraw, BmFieldType.CURRENCY) %>		        			
				</tr>
				<%
				//suma de anticipos a prov.
				withdrawSum += withdraw; 
				withdrawTotal += withdraw;
	
				i++;
			}//while pmBkmv
		
			%> 
			<tr class = "reportCellEven reportCellCode">
				<td colspan="9">&nbsp;</td>
				<%= HtmlUtil.formatReportCell(sFParams, "" + withdrawSum, BmFieldType.CURRENCY) %>
			</tr>
		<%
		} //while pmRequisition
		%>   
		<% 	
		if (withdrawTotal > 0) { %>
			<tr class=""><td colspan="10">&nbsp;</td></tr>
			<tr class="reportCellEven reportCellCode">
				<td colspan="9">&nbsp;</td>
				<%= HtmlUtil.formatReportCell(sFParams, "" + withdrawTotal, BmFieldType.CURRENCY) %>
			</tr>
	<%	} %>
<%	}%>
</table>
<%
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmConnCount.close();
	pmRequisition.close();
	pmBkmv.close();
	%>  
<% 	if (print.equals("1")) { %>
		<script>
			//window.print();
		</script>
<% 	} 
System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
+ " Reporte: "+title
+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
}
 %>
	</body>
</html>
