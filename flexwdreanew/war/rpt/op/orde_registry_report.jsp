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
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes Registro de Pedidos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoOrder bmoOrder = new BmoOrder();
	BmoCustomer bmoCustomer = new BmoCustomer();
	BmoWFlow bmoWFlow = new BmoWFlow();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoRaccount bmoRaccount = new BmoRaccount();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	//Tipo de Pedido
	BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	bmoOrderType = (BmoOrderType)pmOrderType.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getDefaultOrderTypeId().toInteger());
    
    String where = "", sql = "", sqlCurrency = "", ordeStatus = "", paymentStatus = "", deliveryStatus = "", lockStatus  = "", lockStartDate = "", lockEndDate = "";
    String filters = "", customer = "", whereExtra="", whereProduct="", whereProductFamily = "", whereProductGroup = "", productFamilyId = "", productGroupId = "";
    int programId = 0, customerId = 0, cols= 0, areaId = 0, orderId = 0, industryId = 0, userId = 0, 
    		productId = 0,wflowtypeId=0, showProductExtra = 0, currencyId = 0;
    
    double nowParity = 0, defaultParity = 0;
	
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
    if (request.getParameter("orde_wflowtypeid") != null) wflowtypeId = Integer.parseInt(request.getParameter("orde_wflowtypeid"));
    
	bmoProgram = (BmoProgram)pmProgram.get(programId);

    // Filtros listados
    if (orderId > 0) {
    	where += " AND orde_orderid = " + orderId;
    	filters += "<i>Pedido: </i>" + request.getParameter("orde_orderidLabel") + ", ";
    }
    
    if (userId > 0) {
    	//if (sFParams.restrictData(bmoOrder.getProgramCode())) {
			where += " AND orde_userid = " + userId;
		/* } else {
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
    
    if (areaId > 0) {
        where += " AND area_areaid = " + areaId;
        filters += "<i>Dpto.: </i>" + request.getParameter("area_areaidLabel") + ", ";
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
    
    if (wflowtypeId > 0) {
        where += " AND orde_wflowtypeid = " + wflowtypeId;
        filters += "<i>Tipo de Flujo: </i>" + request.getParameter("orde_wflowtypeidLabel") + ", ";
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
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
    if (currencyId > 0) {
   		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);	
   		defaultParity = bmoCurrency.getParity().toDouble();
   		filters += "<i>Moneda: </i>" + request.getParameter("orde_currencyidLabel") + " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
   	} else {
   		//Obtener la paridad de la moneda del sistema
   		//bmoCurrency = (BmoCurrency)pmCurrency.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getCurrencyId().toInteger());
   		//defaultParity = bmoCurrency.getParity().toDouble();
   		//filters += "<i>Moneda: </i>" + bmoCurrency.getCode().toString() + " | " + bmoCurrency.getName().toString() + ", ";
   		filters += "<i>Moneda: </i> Todas ";
		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, " orders ") +     
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON(cure_currencyid = orde_currencyid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " opportunities")+" ON(oppo_opportunityid = orde_opportunityid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = orde_wflowtypeid) " +
				" WHERE orde_orderid > 0 " + 
				where;
		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			sqlCurrency += " AND orde_orderid IN ( " +
					" SELECT orde_orderid FROM " + SQLUtil.formatKind(sFParams, " orders ") +
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
		sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
   	}
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmOrder(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    

    // Conexiones a BD
    PmConn pmOrder = new PmConn(sFParams);
    pmOrder.open();

    PmConn pmConnRaccount = new PmConn(sFParams);
    pmConnRaccount.open();
    
    PmConn pmCurrencyWhile =new PmConn(sFParams);
    pmCurrencyWhile.open();

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
            <br>            
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report">
	<%
	
	sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " orders ") +     
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON(cure_currencyid = orde_currencyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " opportunities")+" ON(oppo_opportunityid = orde_opportunityid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = orde_wflowtypeid) " +
			" WHERE orde_orderid > 0 " + 
			where;
	if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
		sql += " AND orde_orderid IN ( " +
				" SELECT orde_orderid FROM " + SQLUtil.formatKind(sFParams, " orders ") +
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
		int currencyIdWhile = 0, y = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {

			if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
	        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
	        		currencyId = currencyIdWhile;
			    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
	        		y = 0;
	        		%>
	        		<tr>
	            		<td class="reportHeaderCellCenter" colspan="19">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
	        		</tr>
       		<%
        	}
		    %>
			<tr class="">
				<td class="reportHeaderCellCenter">#</td>
				<td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Pedido</td>
				<td class="reportHeaderCell">#Oportunidad</td>
				<td class="reportHeaderCell">O.C. Cliente</td>
				<td class="reportHeaderCell">Cliente</td>
				<td class="reportHeaderCell">&Aacute;rea</td>
				<td class="reportHeaderCell">Vendedor</td>
				<td class="reportHeaderCell">Moneda</td>
				<td class="reportHeaderCell">Estatus Pago</td>
				<td class="reportHeaderCellRight">Monto S/IVA</td>
				<td class="reportHeaderCellRight">Saldo</td>
				<td class="reportHeaderCell">Condiciones</td>
				<td class="reportHeaderCell">Cobertura</td>
				<td class="reportHeaderCell">Facturas</td>
				<td class="reportHeaderCell">Estatus Pago</td>
				<td class="reportHeaderCell">Moneda</td>
				<td class="reportHeaderCellCenter">T.C.</td>
				<td class="reportHeaderCellRight">Monto</td>
			</tr>
			<% 
			sql = " SELECT cust_code, cust_displayname, cust_legalname, cust_customertype, orde_orderid, orde_code, orde_name, orde_description, orde_tax, orde_total, " +
					" orde_currencyid, orde_currencyparity, cons_customerrequisition, orde_coverageparity, orde_paymentstatus, " + 
					" oppo_code, wfty_name, user_code, cure_code FROM " + SQLUtil.formatKind(sFParams, " orders ") +     
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON(cure_currencyid = orde_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " consultancies")+" ON(cons_orderid = orde_orderid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " opportunities")+" ON(oppo_opportunityid = orde_opportunityid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = orde_wflowtypeid) " +
					" WHERE orde_orderid > 0 " + 
					" AND orde_currencyid = " + currencyId +
					where;
			if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
				sql += " AND orde_orderid IN ( " +
						" SELECT orde_orderid FROM " + SQLUtil.formatKind(sFParams, " orders ") +
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
			sql += " ORDER by orde_orderid ASC";
		
		    pmOrder.doFetch(sql);
		
			double sumOrderTotal = 0, subTotalSum = 0, descuentoSum = 0, ivaSum = 0, balanceTotal = 0, sumOrderTotalSinIva = 0, raccAmountTotal = 0;
	//	
//			double sumProd = 0, sumProdTotal = 0,
//					sumEqui = 0, sumEquiTotal = 0,
//					sumStaff = 0, sumStaffTotal = 0,
//					sumOC = 0, sumOCTotal = 0,
//					gastos = 0, sumGastos = 0;
		
//			double subtotalProductTotal = 0, subtotalProductTotalSum = 0;
		
			int i = 1;
			while(pmOrder.next()) {
				
				// Cliente (si es empresa tomar razon social)
				if (pmOrder.getString("customers", "cust_customertype").equals("" + BmoCustomer.TYPE_PERSON)) {
					customer = pmOrder.getString("customers", "cust_code") + " " +
							pmOrder.getString("customers", "cust_displayname");
				} else {
					customer = pmOrder.getString("customers", "cust_code") + " " +
							pmOrder.getString("customers", "cust_legalname");
				}
				
				// Estatus
//				bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
//				bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
//				bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
				bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
		
				// Contar las cxc para el rowspan
				int countRaccOrder = 0;
				String racc = "SELECT COUNT(*) AS countRacc FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
						" WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
						" AND racc_orderid = " + pmOrder.getInt("orders", "orde_orderid");
		
				pmConnRaccount.doFetch(racc);
		
				if (pmConnRaccount.next()) 
					// +1 por la fila de la suma de las cxc de cada pedido
					countRaccOrder = pmConnRaccount.getInt("countRacc") + 1;
				%>
				<tr class="reportCellEven">
					<td class="reportCellNumber" rowspan="<%= countRaccOrder%>"><%= i%></td>			
					<td class="reportCellCode" rowspan="<%= countRaccOrder%>"><%= pmOrder.getString("orders", "orde_code")%></td>
					<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(pmOrder.getString("orders", "orde_name"))%></td>
					<td class="reportCellCode" rowspan="<%= countRaccOrder%>"><%= pmOrder.getString("opportunities", "oppo_code")%></td>
					<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(pmOrder.getString("consultancies", "cons_customerrequisition"))%></td>
					<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(customer)%></td>
					<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(pmOrder.getString("wflowtypes", "wfty_name"))%></td>
					<td class="reportCellCode" rowspan="<%= countRaccOrder%>"><%= pmOrder.getString("users", "user_code")%></td>
					<td class="reportCellCode" rowspan="<%= countRaccOrder%>"><%= pmOrder.getString("currencies", "cure_code")%></td>
					<td class="reportCellCurrency" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(bmoOrder.getPaymentStatus().getSelectedOption().getLabel())%></td>

					<%
					double orderTotal = pmOrder.getDouble("orde_total");
					double orderTotalSinIva = pmOrder.getDouble("orde_total") - pmOrder.getDouble("orde_tax");
		          	
		          	//Conversion a la moneda destino(seleccion del filtro)
		          	int currencyIdOrigin = 0, currencyIdDestiny = 0;
		          	double parityOrigin = 0, parityDestiny = 0;
		          	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
		          	parityOrigin = pmOrder.getDouble("orde_currencyparity");
		          	currencyIdDestiny = currencyId;
		          	parityDestiny = defaultParity;
		
		          	orderTotal = pmCurrencyExchange.currencyExchange(orderTotal, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		          	orderTotalSinIva = pmCurrencyExchange.currencyExchange(orderTotalSinIva, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		          	
		          	sumOrderTotalSinIva += orderTotalSinIva;
		          	%>
					
		          	<td class="reportCellCurrency" rowspan="<%= countRaccOrder%>"><%= SFServerUtil.formatCurrency(orderTotalSinIva)%></td>
		          	<%
		          	double saldo = 0, totalRacc = 0;
		          	//CXC Saldo Cliente
		          	String sqlCXCSaldo = " SELECT racc_code, racc_currencyid, racc_currencyparity, racc_total AS saldoSum " + 
		          			" FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
		          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
		          			" WHERE racc_orderid = " + pmOrder.getInt("orde_orderid") +
		          			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW  + "'" +
		          			" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' ";

		          	pmConnRaccount.doFetch(sqlCXCSaldo);
		          	while (pmConnRaccount.next()) {
		          		//Conversion a la moneda destino(del filtro)
		          		currencyIdOrigin = 0; currencyIdDestiny = 0;
		          		parityOrigin = 0; parityDestiny = 0;
		          		currencyIdOrigin = pmConnRaccount.getInt("racc_currencyid");
		          		parityOrigin = pmConnRaccount.getDouble("racc_currencyparity");
		          		currencyIdDestiny = currencyId;
		          		parityDestiny = defaultParity;

		          		totalRacc += pmCurrencyExchange.currencyExchange(pmConnRaccount.getDouble("saldoSum"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		          	}
	          		saldo = orderTotal - totalRacc;
	          		balanceTotal += saldo;
		          	%>
					<td class="reportCellCurrency" rowspan="<%= countRaccOrder%>"><%= SFServerUtil.formatCurrency(saldo)%></td>
					<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(pmOrder.getString("orders", "orde_description"))%></td>
					<%	if (pmOrder.getBoolean("orde_coverageparity")) { %>
							<td class="reportCellNumber" rowspan="<%= countRaccOrder%>"><%= pmOrder.getDouble("orde_currencyparity")%></td>
					<%	} else { %>
							<td class="reportCellNumber" rowspan="<%= countRaccOrder%>">No</td>
					<%	} %>
					<%
					racc = "SELECT racc_invoiceno, racc_paymentstatus, racc_currencyid, racc_currencyparity, racc_amount, cure_code FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid) " +
		          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = racc_currencyid) " +
							" WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
							" AND racc_orderid = " + pmOrder.getInt("orders", "orde_orderid");
		
					pmConnRaccount.doFetch(racc);
		
					double amountSum = 0;
					boolean first = true;
					while (pmConnRaccount.next()) {
						bmoRaccount.getPaymentStatus().setValue(pmConnRaccount.getString("raccounts", "racc_paymentstatus"));
						
						//Conversion a la moneda destino(del filtro)
		          		currencyIdOrigin = 0; currencyIdDestiny = 0;
		          		parityOrigin = 0; parityDestiny = 0;
		          		currencyIdOrigin = pmConnRaccount.getInt("racc_currencyid");
		          		parityOrigin = pmConnRaccount.getDouble("racc_currencyparity");
		          		currencyIdDestiny = currencyId;
		          		parityDestiny = defaultParity;
		          		
		          		double amountRacc = pmConnRaccount.getDouble("racc_amount");
		          		amountRacc = pmCurrencyExchange.currencyExchange(amountRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		          		amountSum += amountRacc;
		          		raccAmountTotal += amountRacc;
		          		
						if (first) first = false;
						else {	%> <tr class="reportCellEven">	<%	} %>
							<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnRaccount.getString("racc_invoiceno")), BmFieldType.STRING) %>
							<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnRaccount.getString("cure_code"), BmFieldType.CODE) %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnRaccount.getString("racc_currencyparity"), BmFieldType.NUMBER) %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
						</tr>
					<%
					}
					%>
					<tr class="reportCellCode">
						<td colspan="4">&nbsp;</td>
						<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
					</tr>
					<%
					if (countRaccOrder == 0) {
					%>
						<td class="reportCellText">&nbsp;</td>
					</tr>
				<%
					}
				i++;
		
			} // fin pmOrder
			%> 
		    <tr><td colspan="19">&nbsp;</td></tr>
			<tr class="reportCellEven reportCellCode">
			    <td colspan="10">&nbsp;</td>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + sumOrderTotalSinIva, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
			    
			    <td colspan="6">&nbsp;</td>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + raccAmountTotal, BmFieldType.CURRENCY) %>
			</tr>
			<tr><td colspan="19">&nbsp;</td></tr>
			<%
		} // Fin pmCurrencyWhile
	}  // Fin de no existe moneda
	// Existe moneda destino
	else {
	%>
		<tr class="">
			<td class="reportHeaderCellCenter">#</td>
			<td class="reportHeaderCell">Clave</td>
			<td class="reportHeaderCell">Pedido</td>
			<td class="reportHeaderCell">#Oportunidad</td>
			<td class="reportHeaderCell">O.C. Cliente</td>
			<td class="reportHeaderCell">Cliente</td>
			<td class="reportHeaderCell">&Aacute;rea</td>
			<td class="reportHeaderCell">Vendedor</td>
			<td class="reportHeaderCell">Moneda</td>
			<td class="reportHeaderCell">Estatus Pago</td>
			<td class="reportHeaderCellRight">Monto S/IVA</td>
			<td class="reportHeaderCellRight">Saldo</td>
			<td class="reportHeaderCell">Condiciones</td>
			<td class="reportHeaderCell">Cobertura</td>
			<td class="reportHeaderCell">Facturas</td>
			<td class="reportHeaderCell">Estatus Pago</td>
			<td class="reportHeaderCell">Moneda</td>
			<td class="reportHeaderCellCenter">T.C.</td>
			<td class="reportHeaderCellRight">Monto</td>
			
		</tr>
		<% 
		sql = " SELECT cust_code, cust_displayname, cust_legalname, cust_customertype, orde_orderid, orde_code, orde_name, orde_description, orde_tax, orde_total, " +
				" orde_currencyid, orde_currencyparity, cons_customerrequisition, orde_coverageparity, orde_paymentstatus, " + 
				" oppo_code, wfty_name, user_code, cure_code FROM " + SQLUtil.formatKind(sFParams, " orders ") +     
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON(cure_currencyid = orde_currencyid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " opportunities")+" ON(oppo_opportunityid = orde_opportunityid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " consultancies")+" ON(cons_orderid = orde_orderid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = orde_wflowtypeid) " +
				" WHERE orde_orderid > 0 " + 
				where;
		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			sql += " AND orde_orderid IN ( " +
					" SELECT orde_orderid FROM " + SQLUtil.formatKind(sFParams, " orders ") +
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
		sql += " ORDER by orde_orderid ASC";
	
	    pmOrder.doFetch(sql);
	
		double sumOrderTotal = 0, subTotalSum = 0, descuentoSum = 0, ivaSum = 0, balanceTotal = 0, sumOrderTotalSinIva = 0, raccAmountTotal = 0;
//	
//		double sumProd = 0, sumProdTotal = 0,
//				sumEqui = 0, sumEquiTotal = 0,
//				sumStaff = 0, sumStaffTotal = 0,
//				sumOC = 0, sumOCTotal = 0,
//				gastos = 0, sumGastos = 0;
	
//		double subtotalProductTotal = 0, subtotalProductTotalSum = 0;
	
		int i = 1;
		while(pmOrder.next()) {
			
			// Cliente (si es empresa tomar razon social)
			if (pmOrder.getString("customers", "cust_customertype").equals("" + BmoCustomer.TYPE_PERSON)) {
				customer = pmOrder.getString("customers", "cust_code") + " " +
						pmOrder.getString("customers", "cust_displayname");
			} else {
				customer = pmOrder.getString("customers", "cust_code") + " " +
						pmOrder.getString("customers", "cust_legalname");
			}
			
			// Estatus
//			bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
//			bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
//			bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
			bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
	
			// Contar las cxc para el rowspan
			int countRaccOrder = 0;
			String racc = "SELECT COUNT(*) AS countRacc FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
					" WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
					" AND racc_orderid = " + pmOrder.getInt("orders", "orde_orderid");
	
			pmConnRaccount.doFetch(racc);
	
			if (pmConnRaccount.next()) 
				// +1 por la fila de la suma de las cxc de cada pedido
				countRaccOrder = pmConnRaccount.getInt("countRacc") + 1;
			%>
			<tr class="reportCellEven">
				<td class="reportCellNumber" rowspan="<%= countRaccOrder%>"><%= i%></td>			
				<td class="reportCellCode" rowspan="<%= countRaccOrder%>"><%= pmOrder.getString("orders", "orde_code")%></td>
				<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(pmOrder.getString("orders", "orde_name"))%></td>
				<td class="reportCellCode" rowspan="<%= countRaccOrder%>"><%= pmOrder.getString("opportunities", "oppo_code")%></td>
				<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(pmOrder.getString("consultancies", "cons_customerrequisition"))%></td>
				<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(customer)%></td>
				<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(pmOrder.getString("wflowtypes", "wfty_name"))%></td>
				<td class="reportCellCode" rowspan="<%= countRaccOrder%>"><%= pmOrder.getString("users", "user_code")%></td>
				<td class="reportCellCode" rowspan="<%= countRaccOrder%>"><%= pmOrder.getString("currencies", "cure_code")%></td>
				<td class="reportCellCurrency" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(bmoOrder.getPaymentStatus().getSelectedOption().getLabel())%></td>

				<%
				double orderTotal = pmOrder.getDouble("orde_total");
				double orderTotalSinIva = pmOrder.getDouble("orde_total") - pmOrder.getDouble("orde_tax");
	          	
	          	//Conversion a la moneda destino(seleccion del filtro)
	          	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	          	double parityOrigin = 0, parityDestiny = 0;
	          	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
	          	parityOrigin = pmOrder.getDouble("orde_currencyparity");
	          	currencyIdDestiny = currencyId;
	          	parityDestiny = defaultParity;
	
	          	orderTotal = pmCurrencyExchange.currencyExchange(orderTotal, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	orderTotalSinIva = pmCurrencyExchange.currencyExchange(orderTotalSinIva, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	
	          	sumOrderTotalSinIva += orderTotalSinIva;
	          	%>
				
	          	<td class="reportCellCurrency" rowspan="<%= countRaccOrder%>"><%= SFServerUtil.formatCurrency(orderTotalSinIva)%></td>
	          	<%
	          	double saldo = 0, totalRacc = 0;
	          	//CXC Saldo Cliente
	          	String sqlCXCSaldo = " SELECT racc_code, racc_currencyid, racc_currencyparity, racc_total AS saldoSum " + 
	          			" FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
	          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
	          			" WHERE racc_orderid = " + pmOrder.getInt("orde_orderid") +
	          			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW  + "'" +
	          			" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' ";

	          	pmConnRaccount.doFetch(sqlCXCSaldo);
	          	while (pmConnRaccount.next()) {
	          		//Conversion a la moneda destino(del filtro)
	          		currencyIdOrigin = 0; currencyIdDestiny = 0;
	          		parityOrigin = 0; parityDestiny = 0;
	          		currencyIdOrigin = pmConnRaccount.getInt("racc_currencyid");
	          		parityOrigin = pmConnRaccount.getDouble("racc_currencyparity");
	          		currencyIdDestiny = currencyId;
	          		parityDestiny = defaultParity;

	          		totalRacc += pmCurrencyExchange.currencyExchange(pmConnRaccount.getDouble("saldoSum"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	}
          		saldo = orderTotal - totalRacc;
          		balanceTotal += saldo;
	          	%>
				<td class="reportCellCurrency" rowspan="<%= countRaccOrder%>"><%= SFServerUtil.formatCurrency(saldo)%></td>
				<td class="reportCellText" rowspan="<%= countRaccOrder%>"><%= HtmlUtil.stringToHtml(pmOrder.getString("orders", "orde_description"))%></td>
				<%	if (pmOrder.getBoolean("orde_coverageparity")) { %>
						<td class="reportCellNumber" rowspan="<%= countRaccOrder%>"><%= pmOrder.getDouble("orde_currencyparity")%></td>
				<%	} else { %>
						<td class="reportCellNumber" rowspan="<%= countRaccOrder%>">No</td>
				<%	} %>
				<%
				racc = "SELECT racc_invoiceno, racc_paymentstatus, racc_currencyid, racc_currencyparity, racc_amount, cure_code FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid) " +
	          			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = racc_currencyid) " +
						" WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
						" AND racc_orderid = " + pmOrder.getInt("orders", "orde_orderid");
	
				pmConnRaccount.doFetch(racc);
	
				double amountSum = 0;
				boolean first = true;
				while (pmConnRaccount.next()) {
					bmoRaccount.getPaymentStatus().setValue(pmConnRaccount.getString("raccounts", "racc_paymentstatus"));
					
					//Conversion a la moneda destino(del filtro)
	          		currencyIdOrigin = 0; currencyIdDestiny = 0;
	          		parityOrigin = 0; parityDestiny = 0;
	          		currencyIdOrigin = pmConnRaccount.getInt("racc_currencyid");
	          		parityOrigin = pmConnRaccount.getDouble("racc_currencyparity");
	          		currencyIdDestiny = currencyId;
	          		parityDestiny = defaultParity;
	          		
	          		double amountRacc = pmConnRaccount.getDouble("racc_amount");
	          		amountRacc = pmCurrencyExchange.currencyExchange(amountRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          		amountSum += amountRacc;
	          		raccAmountTotal += amountRacc;
	          		
					if (first) first = false;
					else {	%> <tr class="reportCellEven">	<%	} %>
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConnRaccount.getString("racc_invoiceno")), BmFieldType.STRING) %>
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnRaccount.getString("cure_code"), BmFieldType.CODE) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnRaccount.getString("racc_currencyparity"), BmFieldType.NUMBER) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + amountRacc, BmFieldType.CURRENCY) %>
					</tr>
				<%
				}
				if ((countRaccOrder -1 ) > 0) {

				%>
				<tr class="reportCellCode">
					<td colspan="4">&nbsp;</td>
					<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
				</tr>
				<%
				} else {
				%>
					<td class="reportCellText" colspan="5">&nbsp;</td>
				</tr>
			<%
				}
			i++;
	
		} // fin pmOrder
		%> 
	    <tr><td colspan="19">&nbsp;</td></tr>
		<tr class="reportCellEven reportCellCode">
		    <td colspan="10">&nbsp;</td>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + sumOrderTotalSinIva, BmFieldType.CURRENCY) %>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
		    
		    <td colspan="6">&nbsp;</td>
		    <%= HtmlUtil.formatReportCell(sFParams, "" + raccAmountTotal, BmFieldType.CURRENCY) %>
		    
		</tr>
<%	}	%>
</table>
<%
	}// FIN DEL CONTADOR
	}// Fin de if(no carga datos)
	pmConnCount.close();
	pmCurrencyWhile.close();
	pmOrder.close();
	pmConnRaccount.close();
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