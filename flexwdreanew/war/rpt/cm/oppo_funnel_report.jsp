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
 
<%@page import="com.flexwm.shared.wf.BmoWFlowFunnel"%>
<%@include file="/inc/login.jsp"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
	String title = "Oportunidades Por Funnel";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoOpportunity bmoOpportunity = new BmoOpportunity();
	BmoQuote bmoQuote = new BmoQuote();
	BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
	BmoWFlow bmoWFlow = new BmoWFlow();
	BmoWFlowFunnel bmoWFlowFunnel = new BmoWFlowFunnel();
	BmoUser bmoUser1 = new BmoUser();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
    //Tipo de Pedido
    BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
		
   	String sql = "", where = "", groupFilter = "", filters = "", sqlCurrency = "", whereProduct = "", whereProductFamily = "", whereProductGroup = "";
   	String startDate = "", endDate = "", status = "", productFamilyId = "", productGroupId = "", wflowFunelId = "", saleStartDate = "", saleEndDate = "";
   	int wflowTypeId = 0, orderTypeId = 0, productId = 0,
   			venueId = 0, referralId = 0, wflowPhaseId = 0, programId = 0, userId = 0, customerId = 0, currencyId = 0, 
   			dynamicColspan = 0, dynamicColspanMinus = 0;
    double nowParity = 0, defaultParity = 0;

   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("oppo_ordertypeid") != null) orderTypeId = Integer.parseInt(request.getParameter("oppo_ordertypeid"));
   	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
   	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
   	if (request.getParameter("wflw_wflowfunnelid") != null) wflowFunelId = request.getParameter("wflw_wflowfunnelid");
   	if (request.getParameter("oppo_venueid") != null) venueId = Integer.parseInt(request.getParameter("oppo_venueid"));   
    if (request.getParameter("oppo_status") != null) status = request.getParameter("oppo_status");
   	if (request.getParameter("oppo_userid") != null) userId = Integer.parseInt(request.getParameter("oppo_userid"));
   	if (request.getParameter("oppo_customerid") != null) customerId = Integer.parseInt(request.getParameter("oppo_customerid"));
   	if (request.getParameter("oppo_startdate") != null) startDate = request.getParameter("oppo_startdate");
   	if (request.getParameter("oppo_enddate") != null) endDate = request.getParameter("oppo_enddate");
   	if (request.getParameter("oppo_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("oppo_currencyid"));
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
   	if (request.getParameter("prod_productid") != null) productId = Integer.parseInt(request.getParameter("prod_productid"));
    if (request.getParameter("prod_productfamilyid") != null) productFamilyId = request.getParameter("prod_productfamilyid");
    if (request.getParameter("prod_productgroupid") != null) productGroupId = request.getParameter("prod_productgroupid");
    if (request.getParameter("datestart") != null) saleStartDate = request.getParameter("datestart");
    if (request.getParameter("dateend") != null) saleEndDate = request.getParameter("dateend");    
  
	// Construir filtros 
	bmoProgram = (BmoProgram)pmProgram.get(programId);
    
    if (orderTypeId > 0) {
		bmoOrderType = (BmoOrderType)pmOrderType.get(orderTypeId);
        where += " AND oppo_ordertypeid = " + orderTypeId;
        filters += "<i>Tipo Pedido: </i>" + request.getParameter("oppo_ordertypeidLabel") + ", ";
    }
        
    if (wflowTypeId > 0) {
    	where += " AND wfty_wflowtypeid = " + wflowTypeId;
    	filters += "<i>Tipo de Flujo: </i>" + request.getParameter("wflw_wflowtypeidLabel") + ", ";
   	}
    	
    if (userId > 0) {
    	where += " AND oppo_userid = " + userId;
    	/*
    	if (sFParams.restrictData(bmoOpportunity.getProgramCode())) {
			where += " AND oppo_userid = " + userId;
		} else {
			where += " AND ( " +
						" oppo_userid = " + userId +
						" OR oppo_wflowid IN ( " +
							 " SELECT wflw_wflowid FROM " + SQLUtil.formatKind(sFParams, "wflowusers  ") +
							 " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (wflu_wflowid = wflw_wflowid) " +
							 " WHERE wflu_userid = " + userId + 
							 " AND wflw_callercode = '" + bmoOpportunity.getProgramCode().toString() + "' " + 
						 " ) " + 
					 " )";
		}*/
    	filters += "<i>Vendedor: </i>" + request.getParameter("oppo_useridLabel") + ", ";
    }
    
    if (customerId > 0) {
    	where += " AND cust_customerid = " + customerId;
    	filters += "<i>Cliente: </i>" + request.getParameter("oppo_customeridLabel") + ", ";
    }

   	if (wflowPhaseId > 0) {
   		where += " AND wflw_wflowphaseid = " + wflowPhaseId;
   		filters += "<i>Fase de Flujo: </i>" + request.getParameter("wflw_wflowphaseidLabel") + ", ";
   	}
   	
   	if (!wflowFunelId.equals("")) {
    	where = SFServerUtil.parseFiltersToSql("wflw_wflowfunnelid", wflowFunelId);
   		filters += "<i>Funnel: </i>" + request.getParameter("wflw_wflowfunnelidLabel") + ", ";
   	}
   	
   	if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
	   	if (venueId > 0) {
	   		where += " AND oppo_venueid = " + venueId;
	   		filters += "<i>Lugar: </i>" + request.getParameter("oppo_venueidLabel") + ", ";
	   	}
    }
   	
   	if (referralId > 0) {
        where += " AND cust_referralid = " + referralId;
        filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
    }
    
    if (!status.equals("")) {
    	where = SFServerUtil.parseFiltersToSql("oppo_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("oppo_statusLabel") + ", ";
   	}
   	
   	if (!startDate.equals("")) {
   		where += " AND oppo_startdate >= '" + startDate + " 00:00'";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
   	if (!endDate.equals("")) {
   		where += " AND oppo_startdate <= '" + endDate + " 23:59'";
   		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
   	}
   	if (!saleStartDate.equals("")) {
   		where+= " AND oppo_saledate >= '"+ saleStartDate + "'";
   		filters += " <i>Fecha Cierre Inicio: </i>" + saleStartDate + ", ";
   	}
   	if (!saleEndDate.equals("")) {
   		where += " AND oppo_saledate <= '"+ saleEndDate + "'";
   		filters += " <i>Fecha Cierre Fin: </i>" + saleEndDate + ", ";
   	}
   	if (!productFamilyId.equals("")) {
   		whereProductFamily += SFServerUtil.parseFiltersToSql("prod_productfamilyid", productFamilyId);
   		filters += "<i>Familia: </i>" + request.getParameter("prod_productfamilyidLabel") + ", ";
   	}
   	
   	if (productId > 0) {
   		whereProduct = " AND prod_productid = " + productId;
        filters += "<i>Producto: </i>" + request.getParameter("prod_productidLabel") + ", ";
    }
   	
   	if (!productGroupId.equals("")) {
   		whereProductGroup += SFServerUtil.parseFiltersToSql("prod_productgroupid", productGroupId);
   		filters += "<i>Grupo Prod.: </i>" + request.getParameter("prod_productgroupidLabel") + ", ";
   	}
   	
   	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmOpportunity(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    
   	if (currencyId > 0) {
   		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);   		
   		defaultParity = bmoCurrency.getParity().toDouble();
   		filters += "<i>Moneda: </i>" + request.getParameter("oppo_currencyidLabel") + ", <i>Tipo de Cambio Actual: </i>" + defaultParity;
   	} else {
   		// Se mete la consulta padre para agilizar el proceso
   		filters += "<i>Moneda: </i> Todas ";
   		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity " +
	   			" FROM " + SQLUtil.formatKind(sFParams, "opportunities ") + 
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (cust_customerid = oppo_customerid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "referrals")+" ON (cust_referralid = refe_referralid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (user_userid = oppo_userid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotes")+" ON (oppo_quoteid = quot_quoteid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "venues")+" ON (venu_venueid = oppo_venueid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (cure_currencyid = quot_currencyid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
	   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	   			" WHERE wfca_programid = " + programId + 
	   			groupFilter + where;
	   	if (productId > 0 || !productFamilyId.equals("") || !productGroupId.equals("")) {
	   		sqlCurrency += " AND quot_quoteid IN ( " +
	   				" SELECT quot_quoteid  FROM " + SQLUtil.formatKind(sFParams, "quotes") +
	   				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotegroups")+" ON (qogr_quoteid = quot_quoteid) " +
	   				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quoteitems")+" ON (qoit_quotegroupid = qogr_quotegroupid) " +
	   				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON (prod_productid = qoit_productid) " +
	   				" WHERE quot_quoteid = oppo_quoteid " +
	   				whereProduct +
	   				whereProductFamily +
	   				whereProductGroup +
	   				" ) ";
	   	}
	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
   	}
	
   	
	//Conexion a Base de Datos			
	PmConn pmConnList = new PmConn(sFParams);
	pmConnList.open();
	
	PmConn pmProduct = new PmConn(sFParams);
	pmProduct.open();
	
	PmConn pmCurrencyWhile =new PmConn(sFParams);
    pmCurrencyWhile.open();
    
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
	// colspan dinamico
	if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL))
		dynamicColspan++;
	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId()))
		dynamicColspan++;
	if (productId > 0) {
		dynamicColspan++;
        dynamicColspanMinus++;
	}
	if (sFParams.isFieldEnabled(bmoQuote.getCoverageParity()))
		dynamicColspan++;
	
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
<title>:::<%= title %>:::
</title>
<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

	<table border="0" cellspacing="0" cellpading="0" style="width: 100%">
		<tr>
			<td align="left" rowspan="2" valign="top"><img border="0"
				width="<%= SFParams.LOGO_WIDTH %>"
				height="<%= SFParams.LOGO_HEIGHT %>"
				src="<%= sFParams.getMainImageUrl() %>"></td>
			<td class="reportTitle" align="left" colspan="2"><%= title %></td>
		</tr>
		<tr>
			<td class="reportSubTitle"><b>Filtros:</b> <%= filters %> <br>
				<b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda" : "-")%>
				<b>Ordenado por:</b> Clave Oportunidad</td>
			<td class="reportDate" align="right">Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %>
				por: <%= sFParams.getLoginInfo().getEmailAddress() %>
			</td>
		</tr>
	</table>
	<br>
	<% 	  
	
	sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, "opportunities")+
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (cust_customerid = oppo_customerid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "referrals")+" ON (cust_referralid = refe_referralid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" u1 ON (u1.user_userid = oppo_userid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" u2 ON (u2.user_userid = u1.user_parentid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotes")+" ON (oppo_quoteid = quot_quoteid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "venues")+" ON (venu_venueid = oppo_venueid) " +
   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies ")+" ON (cure_currencyid = quot_currencyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customeraddress")+" ON (cuad_customeraddressid = oppo_customeraddressid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
			" WHERE oppo_opportunityid > 0 " +
			where;
			if (productId > 0 || !productFamilyId.equals("") || !productGroupId.equals("")) {
	    		sql += " AND quot_quoteid IN ( " +
	    				" SELECT quot_quoteid  FROM " + SQLUtil.formatKind(sFParams, "quotes") +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotegroups")+" ON (qogr_quoteid = quot_quoteid) " +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quoteitems")+" ON (qoit_quotegroupid = qogr_quotegroupid) " +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON (prod_productid = qoit_productid) " +
	    				" WHERE quot_quoteid = oppo_quoteid " +
	    				whereProduct +
		   				whereProductFamily +
		   				whereProductGroup +
	    				" ) ";
	    	}
		sql += " ORDER BY oppo_code ASC";   
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
		while (pmCurrencyWhile.next()) { %>
			<table class="report" style="width: 100%">
			<%
			if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
        		currencyId = currencyIdWhile;
		    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
        		%>
			<tr>
				<td class="reportHeaderCellCenter" colspan="14"><%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
				</td>
			</tr>
			<tr >
				<td class="reportHeaderCellCenter">#</td>
				<%	if (sFParams.isFieldEnabled(bmoOpportunity.getIdField())) { %>
			    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getIdField()) %></td>
		    	<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoOpportunity.getCustomerId())) { %>
			    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getCustomerId()) %></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getAmount())) { %>
			    			<td class="reportHeaderCellRight" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), "Valor Productos") %></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getWFlowTypeId())) { %>
			    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getWFlowTypeId()) %></td>
		    	<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
						<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoWFlow.getProgramCode(), bmoWFlow.getWFlowFunnelId()) %></td>
				<% 	}%>
				<%	if (sFParams.isFieldEnabled(bmoOpportunity.getSaleDate())) { %>
			    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getSaleDate()) %></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getSaleDate())) { %>
			    			<td class="reportHeaderCell" >A&ntilde;o</td>
		    	<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoQuoteItem.getProductId())) { %>
			    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoQuoteItem.getProgramCode(), bmoQuoteItem.getProductId()) %></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getUserId())) { %>
			    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getProgramCode(), bmoOpportunity.getUserId()) %></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoUser1.getParentId())) { %>
			    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoUser1.getProgramCode(), bmoUser1.getParentId()) %></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getCustomField1())) { %>
			    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getProgramCode(), bmoOpportunity.getCustomField1()) %></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getCustomField2())) { %>
			    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getProgramCode(), bmoOpportunity.getCustomField2()) %></td>
		    	<%	} %>
			</tr>
			<%
			}
			double amountTotal = 0; // discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0;
			//double amountSum = 0, discountSum = 0, taxSum = 0, totalSum = 0, subtotalProductTotalSum = 0;
	
	    	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "opportunities") +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (cust_customerid = oppo_customerid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "referrals")+" ON (cust_referralid = refe_referralid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" u1 ON (u1.user_userid = oppo_userid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" u2 ON (u2.user_userid = u1.user_parentid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotes")+" ON (oppo_quoteid = quot_quoteid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "venues")+" ON (venu_venueid = oppo_venueid) " +
	       			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (cure_currencyid = quot_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customeraddress")+" ON (cuad_customeraddressid = oppo_customeraddressid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	    			" WHERE oppo_opportunityid > 0 " +
					" AND quot_currencyid =  " + currencyId +
	    			where;
	    			if (productId > 0 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			    		sql += " AND quot_quoteid IN ( " +
			    				" SELECT quot_quoteid  FROM " + SQLUtil.formatKind(sFParams, "quotes") +
			    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotegroups")+" ON (qogr_quoteid = quot_quoteid) " +
			    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quoteitems")+" ON (qoit_quotegroupid = qogr_quotegroupid) " +
			    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON (prod_productid = qoit_productid) " +
			    				" WHERE quot_quoteid = oppo_quoteid " +
			    				whereProduct +
				   			whereProductFamily +
				   			whereProductGroup +
			    				" ) ";
			    	}
	   		sql += " ORDER BY oppo_code ASC";         
			pmConnList.doFetch(sql);
			
		    int  i = 1;
       		String oppoStatus= "";
         	while(pmConnList.next()) {
		        		
	       		// Cliente
		       	String customer = "";
		        	if (pmConnList.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	   					customer = pmConnList.getString("cust_code") + " " + pmConnList.getString("cust_legalname");	
	      			else
	      				customer = pmConnList.getString("cust_code") + " " + pmConnList.getString("cust_displayname");
			        	  
	        	  	// Vendedor
	        	  	String salesman = "";
	        	  	salesman = pmConnList.getString("u1", "user_firstname") 
				       			  + " " + pmConnList.getString("u1", "user_fatherlastname")
				       			  + " " + pmConnList.getString("u1", "user_motherlastname");
			        	  
	        	  	// Jefe inmediato del vendedor
				String salesmanParent = "";
				if (pmConnList.getInt("u1", "user_parentid") > 0) {
					salesmanParent = pmConnList.getString("u2", "user_firstname") 
		        			  + " " + pmConnList.getString("u2", "user_fatherlastname")
		        			  + " " + pmConnList.getString("u2", "user_motherlastname");
         		}
				
				// Valor de los productos seleccionados
				sql = "SELECT SUM(qoit_amount) as s FROM " + SQLUtil.formatKind(sFParams, "quoteitems") 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") + " ON (qoit_quotegroupid = qogr_quotegroupid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotes") + " ON (qogr_quoteid = quot_quoteid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (qoit_productid = prod_productid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productgroups") + " ON (prod_productgroupid = prgp_productgroupid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productfamilies") + " ON (prod_productfamilyid = prfm_productfamilyid) "
					+ " WHERE qoit_productid > 0 "
	        		    + whereProduct
	        		    + whereProductFamily
	        		    + whereProductGroup;
				pmProduct.doFetch(sql);
        		    double productQuoteSum = 0;
        		    if (pmProduct.next())
        		    		productQuoteSum = pmProduct.getDouble("s");
    	          	amountTotal += productQuoteSum;
	          	
	          	// Lista de Productos segun lista
	       	 	sql = " SELECT GROUP_CONCAT(prod_name SEPARATOR ', ') AS products"
	            		+ " FROM " + SQLUtil.formatKind(sFParams, "quoteitems")
	            		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotegroups")+" ON(qogr_quotegroupid = qoit_quotegroupid) "
	            		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotes")+" ON(quot_quoteid = qogr_quoteid) "
	            		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "opportunities")+" ON(oppo_quoteid = quot_quoteid) "
	       			 	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (ortp_ordertypeid = quot_ordertypeid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON(prod_productid = qoit_productid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productfamilies")+" ON(prfm_productfamilyid = prod_productfamilyid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productgroups")+" ON(prgp_productgroupid = prod_productgroupid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" ON(supl_supplierid = prod_supplierid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = quot_customerid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = quot_userid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = user_areaid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (wflw_wflowid = oppo_wflowid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) "
	        		    + " WHERE qoit_quoteitemid > 0 "
	        		    + " AND qoit_productid > 0 "
	        		    + " AND quot_quoteid = " + pmConnList.getInt("opportunities", "oppo_quoteid")
	        		    + where
	        		    + whereProduct
	        		    + whereProductFamily
	        		    + whereProductGroup
	        		    + " ORDER BY prod_name ASC";
	        		    pmProduct.doFetch(sql);
	        		    
	        		    String products = "";
	        		    if (pmProduct.next())
	        		   	 	products = pmProduct.getString("products");
       		%>
       	
			<tr class="reportCellEven">
				<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
				
				<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_code") + 
						" " +  pmConnList.getString("opportunities", "oppo_name"), BmFieldType.STRING) %>
	
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>
	
				<%= HtmlUtil.formatReportCell(sFParams, "" + 0, BmFieldType.CURRENCY) %>
	
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowtypes", "wfty_name")), BmFieldType.STRING) %>
	
				<%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
						<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowfunnels", "wflf_name")), BmFieldType.STRING) %>
				<% 	}%>
	
				<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_saledate"), BmFieldType.DATE) %>
	
				<% 	if (!pmConnList.getString("opportunities", "oppo_saledate").equals("")) { %> 
						<%= HtmlUtil.formatReportCell(sFParams, ("" + pmConnList.getString("opportunities", "oppo_saledate")).substring(0, 4), BmFieldType.STRING) %>
				<%	} else { %> 
						<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				<%	} %>
					
					
					<%= HtmlUtil.formatReportCell(sFParams, products, BmFieldType.STRING) %>
	
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(salesman), BmFieldType.STRING) %>
	
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(salesmanParent), BmFieldType.STRING) %>
	
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("opportunities", "oppo_customfield1")), BmFieldType.DATE) %>
	
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("opportunities", "oppo_customfield2")), BmFieldType.DATE) %>
			</tr>
			<%
	     			i++;
	     		} // Fin pmConnList 
			%>
			<tr><td colspan="14">&nbsp;</td></tr>
			<%
			if (amountTotal > 0) {
	     		%>		     
	    			<tr class="reportCellEven reportCellCode">
	    				<td colspan="3" align="right">&nbsp;</td>
	    				<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
					    <td colspan="10" align="right">&nbsp;</td>
	    			</tr>
	    		<%
       			}
			%>
			<tr><td colspan="13">&nbsp;</td></tr>
		</table>
		<%	
		} // Fin pmCurrencyWhile
	}	// Fin de no existe moneda
		// Existe moneda destino
	else {
		int currencyIdOrigin = 0, currencyIdDestiny = 0;
      	double parityOrigin = 0, parityDestiny = 0;
      	
	%>
	<table class="report" style="width:100%">
		<tr class="">
			<td class="reportHeaderCellCenter">#</td>
			<%	if (sFParams.isFieldEnabled(bmoOpportunity.getIdField())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getIdField()) %></td>
	    	<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoOpportunity.getCustomerId())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getCustomerId()) %></td>
	    	<%	} %>
	    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getAmount())) { %>
		    			<td class="reportHeaderCellRight" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), "Valor Productos") %></td>
	    	<%	} %>
	    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getWFlowTypeId())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getWFlowTypeId()) %></td>
	    	<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
						<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoWFlow.getProgramCode(), bmoWFlow.getWFlowFunnelId()) %></td>
			<% 	}%>
			<%	if (sFParams.isFieldEnabled(bmoOpportunity.getSaleDate())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getSaleDate()) %></td>
	    	<%	} %>
	    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getSaleDate())) { %>
		    			<td class="reportHeaderCell" >A&ntilde;o</td>
	    	<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoQuoteItem.getProductId())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoQuoteItem.getProgramCode(), bmoQuoteItem.getProductId()) %></td>
	    	<%	} %>
	    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getUserId())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getProgramCode(), bmoOpportunity.getUserId()) %></td>
	    	<%	} %>
	    	<%	if (sFParams.isFieldEnabled(bmoUser1.getParentId())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoUser1.getProgramCode(), bmoUser1.getParentId()) %></td>
	    	<%	} %>
	    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getCustomField1())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getProgramCode(), bmoOpportunity.getCustomField1()) %></td>
	    	<%	} %>
	    	<%	if (sFParams.isFieldEnabled(bmoOpportunity.getCustomField2())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getProgramCode(), bmoOpportunity.getCustomField2()) %></td>
	    	<%	} %>
		</tr>
		<%
			double amountTotal = 0; //, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0;
			//double amountSum = 0, discountSum = 0, taxSum = 0, totalSum = 0, subtotalProductTotalSum = 0;
	
	    		sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "opportunities")+
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (ortp_ordertypeid = oppo_ordertypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON (cust_customerid = oppo_customerid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "referrals")+" ON (cust_referralid = refe_referralid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" u1 ON (u1.user_userid = oppo_userid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" u2 ON (u2.user_userid = u1.user_parentid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotes")+" ON (oppo_quoteid = quot_quoteid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "venues")+" ON (venu_venueid = oppo_venueid) " +
	       			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies ")+" ON (cure_currencyid = quot_currencyid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customeraddress")+" ON (cuad_customeraddressid = oppo_customeraddressid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	 
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories")+" ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	    			" WHERE oppo_opportunityid > 0 " +
	    			where;
	    			if (productId > 0 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			    		sql += " AND quot_quoteid IN ( " +
			    				" SELECT quot_quoteid  FROM " + SQLUtil.formatKind(sFParams, "quotes") +
			    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotegroups")+" ON (qogr_quoteid = quot_quoteid) " +
			    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "quoteitems")+" ON (qoit_quotegroupid = qogr_quotegroupid) " +
			    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products")+" ON (prod_productid = qoit_productid) " +
			    				" WHERE quot_quoteid = oppo_quoteid " +
			    				whereProduct +
				   				whereProductFamily +
				   				whereProductGroup +
			    				" ) ";
			    	}
	   		sql += " ORDER BY oppo_code ASC";         
			pmConnList.doFetch(sql);
			
		    int  i = 1;
       		String oppoStatus= "";
         	while (pmConnList.next()) {
		        		
	       		// Cliente
		       	String customer = "";
	      	  	if (pmConnList.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
   					customer = pmConnList.getString("cust_code") + " " + pmConnList.getString("cust_legalname");	
      			else
      				customer = pmConnList.getString("cust_code") + " " + pmConnList.getString("cust_displayname");
		        	  
	        	  	// Vendedor
	        	  	String salesman = "";
	        	  	salesman = pmConnList.getString("u1", "user_firstname") 
				       			  + " " + pmConnList.getString("u1", "user_fatherlastname")
				       			  + " " + pmConnList.getString("u1", "user_motherlastname");
			        	  
        	  	// Jefe inmediato
				String salesmanParent = "";
				if (pmConnList.getInt("u1", "user_parentid") > 0) {
					salesmanParent = pmConnList.getString("u2", "user_firstname") 
		        			  + " " + pmConnList.getString("u2", "user_fatherlastname")
		        			  + " " + pmConnList.getString("u2", "user_motherlastname");
				}
          		double amount = pmConnList.getDouble("oppo_amount");
          		
				// Valor de los productos seleccionados
				sql = "SELECT SUM(qoit_amount) as s FROM " + SQLUtil.formatKind(sFParams, "quoteitems") 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") + " ON (qoit_quotegroupid = qogr_quotegroupid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotes") + " ON (qogr_quoteid = quot_quoteid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (qoit_productid = prod_productid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productgroups") + " ON (prod_productgroupid = prgp_productgroupid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productfamilies") + " ON (prod_productfamilyid = prfm_productfamilyid) "
					+ " WHERE quot_quoteid = " + pmConnList.getString("opportunities", "oppo_quoteid")
					+ " AND qoit_productid > 0 "
	        		    + whereProduct
	        		    + whereProductFamily
	        		    + whereProductGroup;
				pmProduct.doFetch(sql);
        		    double productQuoteSum = 0;
        		    if (pmProduct.next())
        		    		productQuoteSum = pmProduct.getDouble("s");
    	          	amountTotal += productQuoteSum;
 
          		// Conversion a la moneda destino(seleccion del filtro)
	          	currencyIdOrigin = 0; currencyIdDestiny = 0;
	          	parityOrigin = 0; parityDestiny = 0;
	          	currencyIdOrigin = pmConnList.getInt("quot_currencyid");
	          	parityOrigin = pmConnList.getDouble("quot_currencyparity");
	          	currencyIdDestiny = currencyId;
	          	parityDestiny = defaultParity;
	
	          	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	
	          	//Sumas por estatus
	          	//amountSum += amount;

	          	//Sumas general
	          	//amountTotal += amount;
       	%>
       	
		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>

			<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_code") + 
					" " +  pmConnList.getString("opportunities", "oppo_name"), BmFieldType.STRING) %>

			<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(customer), BmFieldType.STRING) %>

			<%= HtmlUtil.formatReportCell(sFParams, "" + productQuoteSum, BmFieldType.CURRENCY) %>

			<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowtypes", "wfty_name")), BmFieldType.STRING) %>

			<%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("wflowfunnels", "wflf_name")), BmFieldType.STRING) %>
			<% 	}%>

			<%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_saledate"), BmFieldType.DATE) %>

			<% 	if (!pmConnList.getString("opportunities", "oppo_saledate").equals("")) { %> 
					<%= HtmlUtil.formatReportCell(sFParams, ("" + pmConnList.getString("opportunities", "oppo_saledate")).substring(0, 4), BmFieldType.STRING) %>
			<%	} else { %> 
					<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
			<%	} %>
			
			<%
	        	sql = " SELECT GROUP_CONCAT(prod_name SEPARATOR ', ') AS products"
	            		+ " FROM " + SQLUtil.formatKind(sFParams, "quoteitems")
	            		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotegroups")+" ON(qogr_quotegroupid = qoit_quotegroupid) "
	            		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "quotes")+" ON(quot_quoteid = qogr_quoteid) "
	            		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "opportunities")+" ON(oppo_quoteid = quot_quoteid) "
	       			 	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (ortp_ordertypeid = quot_ordertypeid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products ")+" ON(prod_productid = qoit_productid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productfamilies")+" ON(prfm_productfamilyid = prod_productfamilyid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productgroups")+" ON(prgp_productgroupid = prod_productgroupid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" ON(supl_supplierid = prod_supplierid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = quot_customerid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = quot_userid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = user_areaid) "
	        		    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (wflw_wflowid = oppo_wflowid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) "
	        		    + " WHERE qoit_quoteitemid > 0 "
	        		    + " AND qoit_productid > 0 "
	        		    + " AND quot_quoteid = " + pmConnList.getInt("opportunities", "oppo_quoteid")
	        		    + where
	        		    + whereProduct
	        		    + whereProductFamily
	        		    + whereProductGroup
	        		    + " ORDER BY prod_name ASC";
	        		    pmProduct.doFetch(sql);
	        		    
	        		    String products = "";
	        		    if (pmProduct.next())
	        		    	products = pmProduct.getString("products");
      			%>
			
				<%= HtmlUtil.formatReportCell(sFParams, products, BmFieldType.STRING) %>

				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(salesman), BmFieldType.STRING) %>

				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(salesmanParent), BmFieldType.STRING) %>

				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("opportunities", "oppo_customfield1")), BmFieldType.DATE) %>

				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnList.getString("opportunities", "oppo_customfield2")), BmFieldType.DATE) %>
		</tr>
		<%
     			i++;
     		} // Fin pmConnList   
		%>
		<tr><td colspan="14">&nbsp;</td></tr>
		<%
			if (amountTotal > 0) {
	     		%>		     
	    			<tr class="reportCellEven reportCellCode">
	    				<td colspan="3" align="right">&nbsp;</td>
	    				<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
					    <td colspan="10" align="right">&nbsp;</td>
	    			</tr>
	    		<%
       			}
			%>
	</table>

	<% 
		} // Fin Existe moneda destino
	}// Fin de if(no carga datos)
	
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
	pmConnCount.close();
	pmConnList.close();
   	pmCurrencyWhile.close();
   	pmProduct.close();
   	%>
</body>
</html>