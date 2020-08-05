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
 
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.server.op.PmRequisition"%>
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
    String title = "Reportes General de Ordenes de Compra";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
   	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	
    String filters = "", sql = "", where = "", sqlCurrency = "";
    String reqiStatus = "", lockstatus = "", deliveryStatus = "", paymentStatus = "",
    	   deliveryStartDate = "", deliveryEndDate = "", requestStartDate = "", requestEndDate = "", dueStartDate = "", dueEndDate = "",
    	   userName = "", requestStatus = "", areaId = "";

    int userId = 0, programId = 0, supplierId = 0, cols = 0, type = 0, budgetId = 0, budgetItemId = 0, companyId = 0, orderId = 0, 
    		currencyId = 0, activeBudgets = 0;
    double nowParity = 0, defaultParity = 0;
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
    
    if(enableBudgets){
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
   		//bmoCurrency = (BmoCurrency)pmCurrency.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getCurrencyId().toInteger());
   		//defaultParity = bmoCurrency.getParity().toDouble();
   		//filters += "<i>Moneda: </i>" + bmoCurrency.getCode().toString() + " | " + bmoCurrency.getName().toString() + ", ";
   		filters += "<i>Moneda: </i> Todas ";
		sqlCurrency = "SELECT cure_currencyid, cure_code, cure_name FROM " + SQLUtil.formatKind(sFParams, " currencies ");
   	}

   	// Obtener disclosure de datos
    String disclosureFilters = new PmRequisition(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    PmConn pmRequisition = new PmConn(sFParams);
    pmRequisition.open();
    
    PmConn pmBankMovConcep = new PmConn(sFParams);
    pmBankMovConcep.open();
   
    PmConn pmBankMov = new PmConn(sFParams);
    pmBankMov.open();
    
    PmConn pmCurrencyWhile =new PmConn(sFParams);
    pmCurrencyWhile.open();
    
    BmoRequisition bmoRequisition = new BmoRequisition();
    BmoBankMovement bmoBankMovement = new BmoBankMovement();
    
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
			<%= ((!(currencyId > 0)) ? "<b>Agrupado por:</b>  Moneda" : "")%>
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report">
	<%
	

	sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" on (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" on (area_areaid = reqi_areaid)" +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (orde_orderid = reqi_orderid)" +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (comp_companyid = reqi_companyid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (user_userid = reqi_requestedby) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on (reqi_currencyid = cure_currencyid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = reqi_supplierid) " ;

    if (enableBudgets) {
    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (reqi_budgetitemid = bgit_budgetitemid)" +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" on (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (bgit_budgetid = budg_budgetid)" ;
    }
    sql += " WHERE reqi_requisitionid > 0 " + 
    		where +
    		" ORDER by reqi_requestdate ASC, reqi_requisitionid ASC";
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
			sql = " SELECT reqi_requisitionid,reqi_code, reqi_name, rqtp_name, reqi_requestdate, reqi_deliverydate, reqi_paymentdate," +
					" reqi_status, reqi_deliverystatus, reqi_paymentstatus, reqi_currencyid, reqi_currencyparity, "+
					" reqi_amount, reqi_discount, reqi_tax, reqi_holdback, reqi_total, reqi_payments, reqi_balance, "+
					" rqtp_type, user_code, area_name, supl_code, supl_name, orde_code, orde_name, comp_name, cure_code,reqi_file ";
			if (enableBudgets) { 
				sql += ", budg_name, bgty_name ";
			}
			sql += " FROM " + SQLUtil.formatKind(sFParams, " requisitions  ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" on (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" on (area_areaid = reqi_areaid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (orde_orderid = reqi_orderid)" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (comp_companyid = reqi_companyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (user_userid = reqi_requestedby) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on (reqi_currencyid = cure_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = reqi_supplierid) "+
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankmovements")+" on (bkmv_requisitionid = reqi_requisitionid) ";
		    
			if (enableBudgets) {
		    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (reqi_budgetitemid = bgit_budgetitemid)" +
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" on (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (bgit_budgetid = budg_budgetid)" ;
		    }
		    sql += " WHERE reqi_requisitionid > 0 " + 
	   	  			" AND reqi_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
		    		where +
		    		" ORDER by reqi_requestdate ASC, reqi_requisitionid ASC";
		    
		    pmRequisition.doFetch(sql);
		    if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
        		y = 0;
        		%>
        		<tr>
            		<td class="reportHeaderCellCenter" colspan="<%= (25 + activeBudgets)%>">
            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
            		</td>
        		</tr>
        		<%
        	}
			%>
			<tr>
				<td class="reportHeaderCellCenter">#</td>
				<td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Nombre</td>
				<td class="reportHeaderCell">Tipo O.C</td>
				<td class="reportHeaderCell">Proveedor</td>
				<td class="reportHeaderCell">Solicita</td>
				<td class="reportHeaderCell">Departamento</td>
			<% 	if (enableBudgets) { %>
					<td class="reportHeaderCell">Presup.</td>
					<td class="reportHeaderCell">Item Presup.</td>	
			<% 	} %>
				<td class="reportHeaderCell">Empresa</td>
				<td class="reportHeaderCell">Pedido</td>
				<td class="reportHeaderCell">F. Solicitud</td>
				<td class="reportHeaderCell">F. Entrega</td>
				<td class="reportHeaderCell">F. Pago</td>
				<td class="reportHeaderCell">Estatus</td>
				<td class="reportHeaderCell">Estatus MB</td>
				<td class="reportHeaderCell">Entrega</td>  
				<td class="reportHeaderCell">Pago</td>
				<td class="reportHeaderCell">Archivo</td>
				<td class="reportHeaderCell">Moneda</td>
				<td class="reportHeaderCellCenter">Tipo de Cambio</td>
				<td class="reportHeaderCellRight">Monto</td>
				<td class="reportHeaderCellRight">Descuento</td>
				<td class="reportHeaderCellRight">IVA</td>
				<td class="reportHeaderCellRight">Retenciones</td>
				<td class="reportHeaderCellRight">Total</td>
				<td class="reportHeaderCellRight">Pagos</td>
				<td class="reportHeaderCellRight">Saldo</td>
			</tr>
			<%
			double amountTotal = 0, discountTotal = 0, taxTotal = 0, holdbackTotal = 0, 
					totalTotal = 0, paymentsTotal = 0, balanceTotal = 0;
			int i = 0;
			while(pmRequisition.next()) {
				//Estatus
				bmoRequisition.getStatus().setValue(pmRequisition.getString("requisitions", "reqi_status"));
				bmoRequisition.getDeliveryStatus().setValue(pmRequisition.getString("requisitions", "reqi_deliverystatus"));
				bmoRequisition.getPaymentStatus().setValue(pmRequisition.getString("requisitions", "reqi_paymentstatus"));
				bmoRequisition.getBmoRequisitionType().getType().setValue(pmRequisition.getString("requisitiontypes", "rqtp_type"));
				
								
				%>      
		        <tr class="reportCellEven">
			        <%= HtmlUtil.formatReportCell(sFParams, "" + (i + 1),BmFieldType.NUMBER) %>
			        <%= HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_code"),BmFieldType.CODE) %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_name"),BmFieldType.STRING)) %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitiontypes", "rqtp_name"),BmFieldType.STRING)) %>
			        <%//= HtmlUtil.formatReportCell(sFParams, bmoRequisition.getBmoRequisitionType().getType().getSelectedOption().getLabel(),BmFieldType.STRING) %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("suppliers", "supl_code") + " " + pmRequisition.getString("suppliers", "supl_name"),BmFieldType.STRING)) %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("users", "user_code"),BmFieldType.STRING)) %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("areas", "area_name"),BmFieldType.STRING)) %>
			        <%	if (enableBudgets) { %>
					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("budg_name"),BmFieldType.CODE)) %>
					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("bgty_name"),BmFieldType.CODE)) %>
			        <% 	} %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("companies", "comp_name"),BmFieldType.STRING)) %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("orders", "orde_code") + " " + pmRequisition.getString("orders", "orde_name"), BmFieldType.STRING)) %>
			        <%= HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_requestdate"),BmFieldType.DATETIME) %>
			        <%= HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_deliverydate"),BmFieldType.DATETIME) %>
			        <%= HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_paymentdate"),BmFieldType.DATETIME) %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getStatus().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
			       <td class="reportCellText">
					<%			       
			       		//Movimientos de banco pago proveedor
			       String sqlbankMovConcep = "SELECT bkmv_code, bkmv_status FROM bankmovconcepts"+ 
			    		 "  left join bankmovements on bkmc_bankmovementid = bkmv_bankmovementid " +
			    		 "  left join paccounts on bkmc_paccountid = pacc_paccountid" +
			    		 "  left join bankmovtypes on bkmt_bankmovtypeid = bkmv_bankmovtypeid" +
			    		 "  where pacc_requisitionid = "+ pmRequisition.getString("requisitions", "reqi_requisitionid") +
			    		 "  and bkmt_type = '"+ BmoBankMovType.TYPE_WITHDRAW+"'" +
			    		 "  and bkmt_category = "+ BmoBankMovType.CATEGORY_CXP;
			       
			       pmBankMovConcep.doFetch(sqlbankMovConcep);
			       String mb = "";
			       while (pmBankMovConcep.next()){
			    	   mb = pmBankMovConcep.getString("bkmv_code");
			    	   mb = ": "+pmBankMovConcep.getString("bkmv_status");
			    	   %>
						<%= HtmlUtil.stringToHtml(mb)%> |
					<%		
			       }
			      //Movimientos de banco Anticipo
			       String sqlbankMov = "SELECT  bkmv_code, bkmv_status FROM bankmovements WHERE bkmv_requisitionid ="+ pmRequisition.getString("requisitions", "reqi_requisitionid");
			       pmBankMov.doFetch(sqlbankMov);
			       while (pmBankMov.next()){
			    	   mb = pmBankMov.getString("bkmv_code");
			    	   mb = ": "+pmBankMov.getString("bkmv_status");
			    	   %>
			    	   <%= HtmlUtil.stringToHtml(mb)%> |
			      <%
			       }
			      
			      %>
			      	</td>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getDeliveryStatus().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getPaymentStatus().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
			         <%	String paccFile = ((pmRequisition.getBoolean("reqi_file") ? "Si" : "No")); %>
	              	 	
					<%= HtmlUtil.formatReportCell(sFParams, paccFile, BmFieldType.BOOLEAN) %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("cure_code"),BmFieldType.CODE)) %>				
			        <%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisition.getDouble("reqi_currencyparity"),BmFieldType.NUMBER) %>
			        <%
			        
		          	double amount = pmRequisition.getDouble("reqi_amount");
		          	double discount = pmRequisition.getDouble("reqi_discount");
		          	double tax = pmRequisition.getDouble("reqi_tax");
		          	double holdback = pmRequisition.getDouble("reqi_holdback");
		          	double total = pmRequisition.getDouble("reqi_total");
		          	double payments = pmRequisition.getDouble("reqi_payments");					           		
		          	double balance = pmRequisition.getDouble("reqi_balance");
		          	
		          	amountTotal += amount;
		          	discountTotal += discount;
		            taxTotal += tax;
		          	holdbackTotal += holdback;
		          	totalTotal += total;
		          	paymentsTotal += payments;
		          	balanceTotal += balance;
			        %>
			        <%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
			        <%=HtmlUtil.formatReportCell(sFParams, "" + discount,BmFieldType.CURRENCY) %>
			        <%=HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
			        <%=HtmlUtil.formatReportCell(sFParams, "" + holdback, BmFieldType.CURRENCY) %>
			        <%=HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
			        <%=HtmlUtil.formatReportCell(sFParams, "" + payments, BmFieldType.CURRENCY) %>
			        <%=HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>
		        </tr>
		    <%
		        i++;
			} //pmRequisition
		    %>   
		    <tr><td colspan="<%= (25 + activeBudgets)%>">&nbsp;</td></tr>
		    <tr class="reportCellEven reportCellCode">
			    <td colspan="<%= (19 + activeBudgets)%>">&nbsp;</td>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + holdbackTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
		    </tr>
		    <tr><td colspan="<%= (25 + activeBudgets)%>">&nbsp;</td></tr>
	    <%
		} //pmCurrencyWhile
	} // Fin de no existe moneda
	// Existe moneda destino
	else {
	%>
		<tr>
			<td class="reportHeaderCellCenter">#</td>
			<td class="reportHeaderCell">Clave</td>
			<td class="reportHeaderCell">Nombre</td>
			<td class="reportHeaderCell">Tipo O.C</td>
			<td class="reportHeaderCell">Proveedor</td>
			<td class="reportHeaderCell">Solicita</td>
			<td class="reportHeaderCell">Departamento</td>
		<% 	if (enableBudgets) { %>
				<td class="reportHeaderCell">Presup.</td>
				<td class="reportHeaderCell">Item Presup.</td>	
		<% 	} %>
			<td class="reportHeaderCell">Empresa</td>
			<td class="reportHeaderCell">Pedido</td>
			<td class="reportHeaderCell">F. Solicitud</td>
			<td class="reportHeaderCell">F. Entrega</td>
			<td class="reportHeaderCell">F. Pago</td>
			<td class="reportHeaderCell">Estatus</td>
			<td class="reportHeaderCell">Estatus MB</td>
			<td class="reportHeaderCell">Entrega</td>  
			<td class="reportHeaderCell">Pago</td>
			<td class="reportHeaderCell">Archivo</td>
			<td class="reportHeaderCell">Moneda</td>
			<td class="reportHeaderCellCenter">Tipo de Cambio</td>
			<td class="reportHeaderCellRight">Monto</td>
			<td class="reportHeaderCellRight">Descuento</td>
			<td class="reportHeaderCellRight">IVA</td>
			<td class="reportHeaderCellRight">Retenciones</td>
			<td class="reportHeaderCellRight">Total</td>
			<td class="reportHeaderCellRight">Pagos</td>
			<td class="reportHeaderCellRight">Saldo</td>
		</tr>
		<%
		double amountTotal = 0, discountTotal = 0, taxTotal = 0, holdbackTotal = 0, 
				totalTotal = 0, paymentsTotal = 0, balanceTotal = 0;
		int i = 0;
		sql = " SELECT reqi_requisitionid,reqi_code, reqi_name, rqtp_name, reqi_requestdate, reqi_deliverydate, reqi_paymentdate," +
				" reqi_status, reqi_deliverystatus, reqi_paymentstatus, reqi_currencyid, reqi_currencyparity, " +
				" reqi_amount, reqi_discount, reqi_tax, reqi_holdback, reqi_total, reqi_payments, reqi_balance, "+
				" rqtp_type, user_code, area_name, supl_code, supl_name, orde_code, orde_name, comp_name, cure_code,reqi_file ";
		if (enableBudgets) {
			sql += ", budg_name, bgty_name ";
		}
		sql += " FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" on (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" on (area_areaid = reqi_areaid)" +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (orde_orderid = reqi_orderid)" +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (comp_companyid = reqi_companyid) " +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (user_userid = reqi_requestedby) " +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on (reqi_currencyid = cure_currencyid) " +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = reqi_supplierid) " ;

	    if (enableBudgets) {
	    	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (reqi_budgetitemid = bgit_budgetitemid)" +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" on (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +  
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (bgit_budgetid = budg_budgetid)" ;
	    }
	    sql += " WHERE reqi_requisitionid > 0 " + 
	    		where +
	    		" ORDER by reqi_requestdate ASC, reqi_requisitionid ASC";
	    
	    pmRequisition.doFetch(sql);
		while(pmRequisition.next()) {
			
			//Estatus
			bmoRequisition.getStatus().setValue(pmRequisition.getString("requisitions", "reqi_status"));
			bmoRequisition.getDeliveryStatus().setValue(pmRequisition.getString("requisitions", "reqi_deliverystatus"));
			bmoRequisition.getPaymentStatus().setValue(pmRequisition.getString("requisitions", "reqi_paymentstatus"));
			bmoRequisition.getBmoRequisitionType().getType().setValue(pmRequisition.getString("requisitiontypes", "rqtp_type"));
			
			%>      
	        <tr class="reportCellEven">
		        <%= HtmlUtil.formatReportCell(sFParams, "" + (i + 1),BmFieldType.NUMBER) %>
		        <%= HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_code"), BmFieldType.CODE) %>
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_name"), BmFieldType.STRING)) %>
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitiontypes", "rqtp_name"), BmFieldType.STRING)) %>
		        <%//= HtmlUtil.formatReportCell(sFParams, bmoRequisition.getBmoRequisitionType().getType().getSelectedOption().getLabel(),BmFieldType.STRING) %>
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("suppliers", "supl_code") + "&nbsp;&nbsp;" + pmRequisition.getString("suppliers", "supl_name"), BmFieldType.STRING)) %>
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("users", "user_code"), BmFieldType.STRING)) %>
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("areas", "area_name"), BmFieldType.STRING)) %>
		        <%	if (enableBudgets) { %>
				        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("budg_name"), BmFieldType.CODE)) %>
				        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("bgty_name"), BmFieldType.CODE)) %>
		        <% 	} %>
		        <%= HtmlUtil.stringToHtml( HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("companies", "comp_name"), BmFieldType.STRING)) %>
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("orders", "orde_code") + " " + pmRequisition.getString("orders", "orde_name"), BmFieldType.STRING)) %>
		        <%= HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_requestdate"), BmFieldType.DATETIME) %>
		        <%= HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_deliverydate"), BmFieldType.DATETIME) %>
		        <%= HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("requisitions", "reqi_paymentdate"), BmFieldType.DATETIME) %>
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
				<td class="reportCellText">
				<%
			       
			       		//Movimientos de banco pago proveedor
			       String sqlbankMovConcep = "SELECT bkmv_code, bkmv_status FROM bankmovconcepts"+ 
			    		 "  left join bankmovements on bkmc_bankmovementid = bkmv_bankmovementid " +
			    		 "  left join paccounts on bkmc_paccountid = pacc_paccountid" +
			    		 "  left join bankmovtypes on bkmt_bankmovtypeid = bkmv_bankmovtypeid" +
			    		 "  where pacc_requisitionid = "+ pmRequisition.getString("requisitions", "reqi_requisitionid") +
			    		 "  and bkmt_type = '"+ BmoBankMovType.TYPE_WITHDRAW+"'" +
			    		 "  and bkmt_category = '"+ BmoBankMovType.CATEGORY_CXP+ "'" ;
			    		 
			       pmBankMovConcep.doFetch(sqlbankMovConcep);
			       String mb = "";
			       while (pmBankMovConcep.next()){
			    	   
			    	   bmoBankMovement.getStatus().setValue(pmBankMovConcep.getString("bkmv_status"));
						
			    	   mb = pmBankMovConcep.getString("bkmv_code");
			    	   mb = mb +": "+ bmoBankMovement.getStatus().getSelectedOption().getLabel();
			    	   %>
						<%= HtmlUtil.stringToHtml(mb)%> |
					<%		
			       }
			      //Movimientos de banco Anticipo
			       String sqlbankMov = "SELECT  bkmv_code, bkmv_status FROM bankmovements WHERE bkmv_requisitionid ="+ pmRequisition.getString("requisitions", "reqi_requisitionid");
			       pmBankMov.doFetch(sqlbankMov);
			       while (pmBankMov.next()){
			    	   bmoBankMovement.getStatus().setValue(pmBankMov.getString("bkmv_status"));
						
			    	   mb = pmBankMov.getString("bkmv_code");
			    	   mb = mb+": "+  bmoBankMovement.getStatus().getSelectedOption().getLabel();
			    	   %>
			    	   <%= HtmlUtil.stringToHtml(mb)%> |
			      <%
			       }
			      
			      %>
			    </td>			     
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getDeliveryStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>		       
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		          <%	String reqiFile = ((pmRequisition.getBoolean("reqi_file") ? "Si" : "No")); %>
	              	 	
					<%= HtmlUtil.formatReportCell(sFParams, reqiFile, BmFieldType.STRING) %>
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("cure_code"),BmFieldType.CODE)) %>				
		        <%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisition.getDouble("reqi_currencyparity"), BmFieldType.NUMBER) %>
		        <%
		        
	          	double amount = pmRequisition.getDouble("reqi_amount");
	          	double discount = pmRequisition.getDouble("reqi_discount");
	          	double tax = pmRequisition.getDouble("reqi_tax");
	          	double holdback = pmRequisition.getDouble("reqi_holdback");
	          	double total = pmRequisition.getDouble("reqi_total");
	          	double payments = pmRequisition.getDouble("reqi_payments");					           		
	          	double balance = pmRequisition.getDouble("reqi_balance");
	          	
	          	//Conversion a la moneda destino(seleccion del filtro)
	          	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	          	double parityOrigin = 0, parityDestiny = 0;
	          	currencyIdOrigin = pmRequisition.getInt("reqi_currencyid");
	          	parityOrigin = pmRequisition.getDouble("reqi_currencyparity");
	          	currencyIdDestiny = currencyId;
	          	parityDestiny = defaultParity;
	
	          	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	holdback = pmCurrencyExchange.currencyExchange(holdback, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	payments = pmCurrencyExchange.currencyExchange(payments, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	balance = pmCurrencyExchange.currencyExchange(balance, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	
	          	amountTotal += amount;
	          	discountTotal += discount;
	            taxTotal += tax;
	          	holdbackTotal += holdback;
	          	totalTotal += total;
	          	paymentsTotal += payments;
	          	balanceTotal += balance;
		        %>
		        <%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
		        <%=HtmlUtil.formatReportCell(sFParams, "" + discount,BmFieldType.CURRENCY) %>
		        <%=HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
		        <%=HtmlUtil.formatReportCell(sFParams, "" + holdback, BmFieldType.CURRENCY) %>
		        <%=HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
		        <%=HtmlUtil.formatReportCell(sFParams, "" + payments, BmFieldType.CURRENCY) %>
		        <%=HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>
	        </tr>
	    <%
	        i++;
		} //pmRequisition
	    %>   
	    <tr><td colspan="<%= (25 + activeBudgets)%>">&nbsp;</td></tr>
	    <tr class="reportCellEven reportCellCode">
		    <td colspan="<%= (19 + activeBudgets)%>">&nbsp;</td>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + holdbackTotal, BmFieldType.CURRENCY) %>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal, BmFieldType.CURRENCY) %>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
	    </tr>
<%	
	} %>
</table>
<%
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmRequisition.close();
	pmBankMovConcep.close();
	pmBankMov.close();
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
}%>
	</body>
</html>
