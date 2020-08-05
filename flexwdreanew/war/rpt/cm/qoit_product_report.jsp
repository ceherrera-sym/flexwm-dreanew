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
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoQuote"%>
<%@page import="com.flexwm.server.cm.PmQuote"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@include file="/inc/login.jsp" %>

<%
    // Inicializar variables
    String title = "Reportes de Productos en Cotizaciones";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoProduct bmoProduct = new BmoProduct();
	BmoOpportunity bmoOpportunity = new BmoOpportunity();
    BmoQuote bmoQuote = new BmoQuote();
    BmoWFlow bmoWFlow = new BmoWFlow();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
    //Tipo de Pedido
    BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	
	String startDate = "", endDate = "", status = "", sql = "", sqlCurrency ="",
			where = "", filters = "", groupFilter = "", sqlGroup = "", whereProduct = "", whereProductFamily = "", saleEndDate = "",
			productFamilyId = "", productGroupId = "", whereProductGroup = "", customer = "", wflowFunelId = "", saleStartDate = "";
    
    int wflowTypeId = 0, orderTypeId = 0, productId = 0,
   	venueId = 0, referralId = 0, wflowPhaseId = 0, programId = 0, userId = 0, customerId = 0, 
   	currencyId = 0, dynamicColspan = 0, dynamicColspanMinus = 0;
        
   	double nowParity = 0, defaultParity = 0;

   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("oppo_ordertypeid") != null) orderTypeId = Integer.parseInt(request.getParameter("oppo_ordertypeid"));
   	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
   	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
   	if (request.getParameter("wflw_wflowfunnelid") != null) wflowFunelId = request.getParameter("wflw_wflowfunnelid");
   	if (request.getParameter("oppo_venueid") != null) venueId = Integer.parseInt(request.getParameter("oppo_venueid"));   
   	if (request.getParameter("oppo_startdate") != null) startDate = request.getParameter("oppo_startdate");
   	if (request.getParameter("oppo_enddate") != null) endDate = request.getParameter("oppo_enddate");
    if (request.getParameter("oppo_status") != null) status = request.getParameter("oppo_status");
   	if (request.getParameter("oppo_userid") != null) userId = Integer.parseInt(request.getParameter("oppo_userid"));
   	if (request.getParameter("oppo_customerid") != null) customerId = Integer.parseInt(request.getParameter("oppo_customerid"));
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
   	if (request.getParameter("oppo_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("oppo_currencyid"));
   	if (request.getParameter("prod_productid") != null) productId = Integer.parseInt(request.getParameter("prod_productid"));
    if (request.getParameter("prod_productfamilyid") != null) productFamilyId = request.getParameter("prod_productfamilyid");
    if (request.getParameter("prod_productgroupid") != null) productGroupId = request.getParameter("prod_productgroupid");
    if (request.getParameter("datestart") != null) saleStartDate = request.getParameter("datestart");
    if (request.getParameter("dateend") != null) saleEndDate = request.getParameter("dateend");    
  
       
	// Construir filtros 
	bmoProgram = (BmoProgram)pmProgram.get(programId);
    // Filtro agrupacion
	if (wflowTypeId > 0) {
		where += " AND wfty_wflowtypeid = " + wflowTypeId;
   		filters += "<i>Tipo de Flujo: </i>" + request.getParameter("wflw_wflowtypeidLabel") + ", ";
   	}
	
	if (orderTypeId > 0) {
		bmoOrderType = (BmoOrderType)pmOrderType.get(orderTypeId);	
        where += " AND oppo_ordertypeid = " + orderTypeId;
        filters += "<i>Tipo Pedido: </i>" + request.getParameter("oppo_ordertypeidLabel") + ", ";
    }
	
	if (wflowPhaseId > 0) {
   		where += " AND wflw_wflowphaseid = " + wflowPhaseId;
   		filters += "<i>Fase de Flujo: </i>" + request.getParameter("wflw_wflowphaseidLabel") + ", ";
   	}
   	
   	if (!wflowFunelId.equals("")) {
    	where = SFServerUtil.parseFiltersToSql("wflw_wflowfunnelid", wflowFunelId);
   		filters += "<i>Funnel: </i>" + request.getParameter("wflw_wflowfunnelidLabel") + ", ";
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
							 " SELECT wflw_wflowid FROM wflowusers  " +
							 " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflows on (wflu_wflowid = wflw_wflowid) " +
							 " WHERE wflu_userid = " + userId + 
							 " AND wflw_callercode = '" + bmoOpportunity.getProgramCode().toString() + "' " + 
						 " ) " + 
					 " )";
		}
    	*/
    	filters += "<i>Vendedor: </i>" + request.getParameter("oppo_useridLabel") + ", ";
    }
    
    if (customerId > 0) {
    	where += " AND cust_customerid = " + customerId;
    	filters += "<i>Cliente: </i>" + request.getParameter("oppo_customeridLabel") + ", ";
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
   	
   	if (!status.equals("")) {
   		//where += " and oppo_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("oppo_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("oppo_statusLabel") + ", ";
   	}
   	
   	if (productId > 0) {
    	whereProduct = " AND qoit_productid = " + productId;
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
   		filters += "<i>Moneda: </i>" + request.getParameter("oppo_currencyidLabel") + " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
   	} else {
   		// Se mete la consulta padre para agilizar el proceso
   		filters += "<i>Moneda: </i> Todas ";
		//sqlCurrency = "SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM currencies ";
   		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity " +
	   			" FROM " + SQLUtil.formatKind(sFParams, "opportunities") + 
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") +" ON(ortp_ordertypeid = oppo_ordertypeid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "customers") +" ON(cust_customerid = oppo_customerid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "referrals") +" ON(cust_referralid = refe_referralid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "users") +" ON(user_userid = oppo_userid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotes") +" ON(oppo_quoteid = quot_quoteid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "venues") +" ON(venu_venueid = oppo_venueid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "currencies") +" ON(cure_currencyid = quot_currencyid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflows") +" ON(wflw_wflowid = oppo_wflowid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels") +" ON(wflf_wflowfunnelid = wflw_wflowfunnelid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") +" ON(wfty_wflowtypeid = oppo_wflowtypeid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowphases") +" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +	 
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories") +" ON(wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	   			" WHERE wfca_programid = " + programId + 
	   			groupFilter + where;
	   	if (productId > 0 || !productFamilyId.equals("") || !productGroupId.equals("")) {
	   		sqlCurrency += " AND quot_quoteid IN ( " +
	   				" SELECT quot_quoteid  FROM " + SQLUtil.formatKind(sFParams, "quotes") +
	   				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") +" ON(qogr_quoteid = quot_quoteid) " +
	   				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quoteitems") +" ON(qoit_quotegroupid = qogr_quotegroupid) " +
	   				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "products") +" ON(prod_productid = qoit_productid) " +
	   				" WHERE quot_quoteid = oppo_quoteid " +
	   				whereProduct +
	   				whereProductFamily +
	   				whereProductGroup +
	   				" ) ";
	   	}
	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
   	}
    
    PmConn pmQuote = new PmConn(sFParams);
    pmQuote.open();
    
    PmConn pmCountOrdiProd = new PmConn(sFParams);
    pmCountOrdiProd.open();
    
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

<table bprodr="0" cellspacing="0" cellpading="0" width="100%">
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
            <b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Oportunidad, Producto" : "Oportunidad, Producto")%>
			<b>Ordenado por:</b> Clave Oportunidad, Clave Producto.
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<p>
<table class="report">
<%


sql = " SELECT COUNT(*) AS contador  FROM " + SQLUtil.formatKind(sFParams, "quoteitems") +
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") +" ON(qogr_quotegroupid = qoit_quotegroupid) " +
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotes") +" ON(quot_quoteid = qogr_quoteid) " +
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "opportunities") +" ON(oppo_quoteid = quot_quoteid) " +
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "currencies") +" ON(cure_currencyid = quot_currencyid) " +
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflows") +" ON(wflw_wflowid = oppo_wflowid) " +
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels") +" ON(wflf_wflowfunnelid = wflw_wflowfunnelid) " +
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") +" ON(wfty_wflowtypeid = oppo_wflowtypeid) " +
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowphases") +" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +	
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "products") +" ON(prod_productid = qoit_productid) " +
" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "customers") +" ON(cust_customerid = quot_customerid) " +
" WHERE qoit_productid > 0 " +
" " +
			
where +
whereProduct +
whereProductFamily + 
whereProductGroup;

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
	int currencyIdWhile = 0, sumTotalQuantityProduct = 0;
	double sumTotalAmounTotalGroupByProd = 0;
	pmCurrencyWhile.doFetch(sqlCurrency);
	while (pmCurrencyWhile.next()) {
		
		sqlGroup = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "opportunities") +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "customers") +" ON(cust_customerid = oppo_customerid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "users") +" ON(user_userid = oppo_userid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = user_areaid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") +" ON(ortp_ordertypeid = oppo_ordertypeid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotes") +" ON(oppo_quoteid = quot_quoteid) " +
	   			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "venues") +" ON(venu_venueid = oppo_venueid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "currencies") +" ON(cure_currencyid = quot_currencyid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflows") +" ON(wflw_wflowid = oppo_wflowid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels") +" ON(wflf_wflowfunnelid = wflw_wflowfunnelid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") +" ON(wfty_wflowtypeid = oppo_wflowtypeid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowphases") +" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +	     
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories") +" ON(wfca_wflowcategoryid = wfph_wflowcategoryid) " +
				" WHERE oppo_opportunityid > 0 " +
				" AND oppo_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
				where;
		if (productId > 0 
				|| !productFamilyId.equals("")
				|| !productGroupId.equals("")) {
			sqlGroup += " AND quot_quoteid IN ( " +
					" SELECT quot_quoteid  FROM " + SQLUtil.formatKind(sFParams, "quotes") +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") +" ON(qogr_quoteid = quot_quoteid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quoteitems") +" ON(qoit_quotegroupid = qogr_quotegroupid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "products") +" ON(prod_productid = qoit_productid) " +
					" WHERE quot_quoteid = oppo_quoteid " +
					whereProduct +
					whereProductFamily +
					whereProductGroup +
					" ) ";
		}
		sqlGroup += " ORDER BY oppo_code ASC";       

		//System.out.println("sqlGroup: " + sqlGroup);

		int countItem = 1, countOppo = 1;
		pmQuote.doFetch(sqlGroup);
		while(pmQuote.next()) {
			
			if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
        		%>
        		<tr>
            		<td class="reportHeaderCellCenter" colspan="10">
            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
            		</td>
        		</tr>
        		<%
        	}
			
			// Si el pedido no tiene items de Productos, no muestra titulos
			sql = " SELECT COUNT(qoit_quoteitemid) AS countItemProd  FROM " + SQLUtil.formatKind(sFParams, "quoteitems") +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") +" ON(qogr_quotegroupid = qoit_quotegroupid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotes") +" ON(quot_quoteid = qogr_quoteid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "opportunities") +" ON(oppo_quoteid = quot_quoteid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "currencies") +" ON(cure_currencyid = quot_currencyid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflows") +" ON(wflw_wflowid = oppo_wflowid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels") +" ON(wflf_wflowfunnelid = wflw_wflowfunnelid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") +" ON(wfty_wflowtypeid = oppo_wflowtypeid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowphases") +" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +	
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "products") +" ON(prod_productid = qoit_productid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "customers") +" ON(cust_customerid = quot_customerid) " +
					" WHERE qoit_productid > 0 " +
					" AND qogr_quoteid = " + pmQuote.getInt("opportunities", "oppo_quoteid") +
					" AND oppo_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
					where +
					whereProduct +
					whereProductFamily + 
					whereProductGroup;

			pmCountOrdiProd.doFetch(sql);
			if(pmCountOrdiProd.next())
				countItem = pmCountOrdiProd.getInt("countItemProd");

			if(countItem > 0) {
				//Estatus
				bmoOpportunity.getStatus().setValue(pmQuote.getString("opportunities", "oppo_status"));
				bmoQuote.getStatus().setValue(pmQuote.getString("quotes", "quot_status"));
				
				//Conversion de la cotizacion a la moneda destino(seleccion del filtro) 
	            int currencyIdOrigin = 0, currencyIdDestiny = 0;
	            double parityOrigin = 0, parityDestiny = 0;
	            currencyIdOrigin = pmQuote.getInt("quot_currencyid");
	            parityOrigin = pmQuote.getDouble("quot_currencyparity");
	            currencyIdDestiny = pmCurrencyWhile.getInt("cure_currencyid");
	            parityDestiny = pmCurrencyWhile.getDouble("cure_parity");
	    	%>	
	        	<tr class="">
	        		<td class="reportGroupCell" colspan="10">
	        			<b>
	    					#<%= countOppo %> |
	        				<%= pmQuote.getString("quotes", "quot_code") %>
	        				<%= HtmlUtil.stringToHtml(pmQuote.getString("quotes", "quot_name")) %> |
    						<%
			                if (pmQuote.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
			       				customer = pmQuote.getString("cust_code") + " " + pmQuote.getString("cust_legalname");	
			       			else
			       				customer = pmQuote.getString("cust_code") + " " + pmQuote.getString("cust_displayname");
			                %>	    			                
				        	<%= HtmlUtil.stringToHtml(customer) %> |
				        	Estatus: 
							<%= HtmlUtil.stringToHtml(bmoOpportunity.getStatus().getSelectedOption().getLabel()) %> 
				        		|
				        	<%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
									<%= sFParams.getFieldFormTitle(bmoWFlow.getProgramCode(), bmoWFlow.getWFlowFunnelId()) %>:
									<%= HtmlUtil.stringToHtml(pmQuote.getString("wflowfunnels", "wflf_name")) %> |
							<% 	}%>						
				        	Fechas: 
				        	<%= pmQuote.getString("oportunities", "oppo_startdate") %>
				        	-
				        	<%= pmQuote.getString("oportunities", "oppo_enddate") %> |
				        	Fecha Cierre:
				        	<%= pmQuote.getString("oportunities", "oppo_saledate") %> |
		        			<%	if (sFParams.isFieldEnabled(bmoQuote.getCoverageParity())) { %>
					        		Cobertura T.C.: <%= (pmQuote.getBoolean("quot_coverageparity") ? "Si" : "No")%> |
					        <% 	}%>
		        			Tipo de Cambio: <%= pmQuote.getDouble("quot_currencyparity") %> |
        					<% 	if (currencyIdOrigin != currencyIdDestiny) { %>
		                   			<span title='Origen: <%= SFServerUtil.formatCurrency(pmQuote.getDouble("quot_total"))%> <%= pmQuote.getString("cure_code")%>'>
			                   			Total: <%=SFServerUtil.formatCurrency(
			                   					pmCurrencyExchange.currencyExchange(pmQuote.getDouble("quot_total"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny)) %> 
		                   						<%= pmCurrencyWhile.getString("cure_code")%>
			                   		</span>
	                   		<%	} else { %>
		                   			<span title='Origen: <%= SFServerUtil.formatCurrency(pmQuote.getDouble("quot_total"))%> <%= pmQuote.getString("cure_code")%>'>
			                   			Total: <%=SFServerUtil.formatCurrency(pmQuote.getDouble("quot_total")) %> <%= pmQuote.getString("cure_code") %>
		                   			</span>
	                   		<% 	} %>
	        			</b>
	    			</td>
	            </tr>
	            <tr class="">
	                <td class="reportHeaderCellCenter" width="1%">#</td>
	                <td class="reportHeaderCell">Clave</td>
	                <td class="reportHeaderCell">Producto</td>
	                <td class="reportHeaderCell">Descripci&oacute;n</td>
	                <td class="reportHeaderCell">Modelo</td>
	                <td class="reportHeaderCell">Familia Prod.</td>
	                <td class="reportHeaderCell">Grupo Prod.</td>
	                <td class="reportHeaderCell">Proveedor</td>
	                <td class="reportHeaderCellCenter">Cantidad</td>
	                <td class="reportHeaderCellRight">Subtotal</td>
	            </tr>
	            <%
	            sql = " SELECT prod_code, prod_name, prod_description, prod_model, prfm_name, prgp_name, supl_supplierid, supl_code, supl_name, cust_code, cust_displayname, " +
	            		" quot_quoteid, quot_code, quot_name, quot_startdate, quot_enddate, quot_status, qoit_quantity, quot_currencyid, quot_currencyparity, " +
	            		" SUM(qoit_quantity) as q, SUM(qoit_amount) as amounTotalGroupByProd " +
	            		" FROM " + SQLUtil.formatKind(sFParams, "quoteitems") +
	            		" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") +" ON(qogr_quotegroupid = qoit_quotegroupid) " +
	            		" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotes") +" ON(quot_quoteid = qogr_quoteid) " +
	            		" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "opportunities") +" ON(oppo_quoteid = quot_quoteid) " +
	       			 	" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") +" ON(ortp_ordertypeid = quot_ordertypeid) " +
	        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "products") +" ON(prod_productid = qoit_productid) " +
	        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "productfamilies") +" ON(prfm_productfamilyid = prod_productfamilyid) " +
	        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "productgroups") +" ON(prgp_productgroupid = prod_productgroupid) " +
	        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "suppliers") +" ON(supl_supplierid = prod_supplierid) " +
	        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "customers") +" ON(cust_customerid = quot_customerid) " +
	        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "users") +" ON(user_userid = quot_userid) " +
	        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = user_areaid) " +
	        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflows") +" ON(wflw_wflowid = oppo_wflowid) " +
	        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels") +" ON(wflf_wflowfunnelid = wflw_wflowfunnelid) " +
						" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") +" ON(wfty_wflowtypeid = oppo_wflowtypeid) " +
						" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowphases") +" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
	        		    " WHERE qoit_quoteitemid > 0 " +
	        		    " AND qoit_productid > 0 " +
	        		    " AND quot_quoteid = " + pmQuote.getInt("opportunities", "oppo_quoteid") +
	        		    " AND oppo_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
	        		    whereProduct +
	        		    where +
	        		    whereProductFamily +
	        		    whereProductGroup +
	        		    " GROUP BY quot_code, prod_code " +
	        		    " ORDER BY quot_code ASC, prod_productid ASC";
	            
	            //System.out.println("sql: "+sql);
	            pmProduct.doFetch(sql);
	            int i = 1, sumQuantityProduct = 0;
	            double sumAmounTotalGroupByProd = 0;
	            while(pmProduct.next()) {
	            	
	  	  			double amounTotalGroupByProd = pmProduct.getDouble("amounTotalGroupByProd");	            	
	  	  			
	  	  			sumAmounTotalGroupByProd += amounTotalGroupByProd;
	            	sumQuantityProduct += pmProduct.getDouble("q");
	            	sumTotalAmounTotalGroupByProd += amounTotalGroupByProd;
	            	sumTotalQuantityProduct += pmProduct.getDouble("q");
	            	
	            %>
		        	<tr class="reportCellEven">
			        	<%= HtmlUtil.formatReportCell(sFParams, i + "", BmFieldType.NUMBER) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_code"), BmFieldType.CODE) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_name"), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_description"), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_model"), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("productfamilies", "prfm_name"), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("productgroups", "prgp_name"), BmFieldType.STRING)) %>
			        	<% if(pmProduct.getInt("suppliers", "supl_supplierid") > 0) { %>
	        				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + (pmProduct.getString("suppliers", "supl_code") + " " + pmProduct.getString("suppliers", "supl_name")), BmFieldType.STRING)) %>
			        	<% } else { %>
			        		<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
			        	<% } %>	
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getDouble("q"), BmFieldType.NUMBER) %>
			        	
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + amounTotalGroupByProd, BmFieldType.CURRENCY) %>
		        	</tr>
	    	<%
	    			i++;
	            }//pmProduct
	        %>
	          	<tr class="reportCellEven reportCellCode">
		          	<td colspan="8">&nbsp;</td>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + sumQuantityProduct, BmFieldType.NUMBER) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + sumAmounTotalGroupByProd, BmFieldType.CURRENCY) %>
	          	</tr>
	            <%
		            countOppo++;
	        } //if count	
	    } // pmQuote
		%>		
		<tr><td colspan="10">&nbsp;</td></tr>
	    <tr class="reportCellEven reportCellCode">
		  	<td colspan="8">&nbsp;</td>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotalQuantityProduct, BmFieldType.NUMBER) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotalAmounTotalGroupByProd, BmFieldType.CURRENCY) %>
		</tr>
		<tr><td colspan="10">&nbsp;</td></tr>

		<%
	}	// Fin de no existe moneda
	%>
    </table>
    <%
}	// Si existe moneda destino
else {
	double sumTotalAmounTotalGroupByProd = 0;
	int sumTotalQuantityProduct = 0;
	sqlGroup = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "opportunities") +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "customers") +" ON(cust_customerid = oppo_customerid) " +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "users") +" ON(user_userid = oppo_userid) " +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = user_areaid) " +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") +" ON(ortp_ordertypeid = oppo_ordertypeid) " +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflows") +" ON(wflw_wflowid = oppo_wflowid) " +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels") +" ON(wflf_wflowfunnelid = wflw_wflowfunnelid) " +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") +" ON(wfty_wflowtypeid = oppo_wflowtypeid) " +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowphases") +" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +	     
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotes") +" ON(oppo_quoteid = quot_quoteid) " +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "currencies") +" ON(cure_currencyid = quot_currencyid) " +
			" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowcategories") +" ON(wfca_wflowcategoryid = wfph_wflowcategoryid) " +
			" WHERE oppo_opportunityid > 0 " +
			where;
	if (productId > 0 
			|| !productFamilyId.equals("")
			|| !productGroupId.equals("")) {
		sqlGroup += " AND quot_quoteid IN ( " +
				" SELECT quot_quoteid  FROM " + SQLUtil.formatKind(sFParams, "quotes") +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") +" ON(qogr_quoteid = quot_quoteid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quoteitems") +" ON(qoit_quotegroupid = qogr_quotegroupid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "products") +" ON(prod_productid = qoit_productid) " +
				" WHERE quot_quoteid = oppo_quoteid " +
				whereProduct +
				whereProductFamily +
				whereProductGroup +
				" ) ";
	}
	sqlGroup += " ORDER BY oppo_code ASC";       

	//System.out.println("sqlGroup: " + sqlGroup);

	int countItem = 1, countOppo = 1;
	pmQuote.doFetch(sqlGroup);
	while (pmQuote.next()) {
		// Si el pedido no tiene items de Productos, no muestra titulos
		sql = " SELECT COUNT(qoit_quoteitemid) AS countItemProd  FROM " + SQLUtil.formatKind(sFParams, "quoteitems") +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") +" ON(qogr_quotegroupid = qoit_quotegroupid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotes") +" ON(quot_quoteid = qogr_quoteid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "opportunities") +" ON(oppo_quoteid = quot_quoteid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "currencies") +" ON(cure_currencyid = quot_currencyid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflows") +" ON(wflw_wflowid = oppo_wflowid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels") +" ON(wflf_wflowfunnelid = wflw_wflowfunnelid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") +" ON(wfty_wflowtypeid = oppo_wflowtypeid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowphases") +" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +	
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "products") +" ON(prod_productid = qoit_productid) " +
				" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "customers") +" ON(cust_customerid = quot_customerid) " +
				" WHERE qoit_productid > 0 " +
				" AND qogr_quoteid = " + pmQuote.getInt("opportunities", "oppo_quoteid") +
				where +
				whereProduct +
				whereProductFamily + 
				whereProductGroup;

		pmCountOrdiProd.doFetch(sql);
		if (pmCountOrdiProd.next())
			countItem = pmCountOrdiProd.getInt("countItemProd");

		if (countItem > 0) {
			//Estatus
			bmoOpportunity.getStatus().setValue(pmQuote.getString("opportunities", "oppo_status"));
			bmoQuote.getStatus().setValue(pmQuote.getString("quotes", "quot_status"));
			
			//Conversion de la cotizacion a la moneda destino(seleccion del filtro) 
            int currencyIdOrigin = 0, currencyIdDestiny = 0;
            double parityOrigin = 0, parityDestiny = 0;
            currencyIdOrigin = pmQuote.getInt("quot_currencyid");
            parityOrigin = pmQuote.getDouble("quot_currencyparity");
            currencyIdDestiny = currencyId;
            parityDestiny = defaultParity;
    	%>	
        	<tr class="">
        		<td class="reportGroupCell" colspan="10">
        			<b>
    					#<%= countOppo %> |
        				<%= pmQuote.getString("quotes", "quot_code") %>
        				<%= HtmlUtil.stringToHtml(pmQuote.getString("quotes", "quot_name")) %> |
        				<%= pmQuote.getString("customers", "cust_code") %>
			        	<%= HtmlUtil.stringToHtml(pmQuote.getString("customers", "cust_displayname")) %> | 
			        	Estatus: 
						<%= HtmlUtil.stringToHtml(bmoOpportunity.getStatus().getSelectedOption().getLabel()) %> 
			        		|
			        		<%	if (sFParams.isFieldEnabled(bmoWFlow.getWFlowFunnelId())) { %>
									<%= sFParams.getFieldFormTitle(bmoWFlow.getProgramCode(), bmoWFlow.getWFlowFunnelId()) %>:
									<%= HtmlUtil.stringToHtml(pmQuote.getString("wflowfunnels", "wflf_name"))%> |
							<% 	}%>
			        	Fechas:
			        	<%= pmQuote.getString("oportunities", "oppo_startdate") %>
			        	-
			        	<%= pmQuote.getString("oportunities", "oppo_enddate") %> |
			        	Fecha Cierre:
				        	<%= pmQuote.getString("oportunities", "oppo_saledate") %> |
			        	
				        <% 	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
				        	if (currencyIdOrigin != currencyIdDestiny) {
				        		if (bmoCurrency.getCode().toString().equals("USD")) {
			        	%>
			    					Tipo de Cambio: <%= defaultParity %> |
					    <%		} else { %>
					    			Tipo de Cambio: <%= pmQuote.getDouble("quot_currencyparity") %> |
					    <%		}
					    	} else { %>
					    		Tipo de Cambio: <%= pmQuote.getDouble("quot_currencyparity") %> |
					    <%	}%>
			        	<span title='Origen: <%= SFServerUtil.formatCurrency(pmQuote.getDouble("quot_total"))%> <%= pmQuote.getString("cure_code") %>'>
	                   		Total: <%=SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(pmQuote.getDouble("quot_total"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny)) %>
	                   		<%= bmoCurrency.getCode().toString()%>
                   		</span>
        			</b>
    			</td>
            </tr>
            <tr class="">
                <td class="reportHeaderCellCenter" width="1%">#</td>
                <td class="reportHeaderCell">Clave</td>
                <td class="reportHeaderCell">Producto</td>
                <td class="reportHeaderCell">Descripci&oacute;n</td>
                <td class="reportHeaderCell">Modelo</td>
                <td class="reportHeaderCell">Familia Prod.</td>
                <td class="reportHeaderCell">Grupo Prod.</td>
                <td class="reportHeaderCell">Proveedor</td>
                <td class="reportHeaderCellCenter">Cantidad</td>
                <td class="reportHeaderCellRight">Subtotal</td>
            </tr>
            <%
            sql = " SELECT prod_code, prod_name, prod_description, prod_model, prfm_name, prgp_name, supl_supplierid, supl_code, supl_name, cust_code, cust_displayname, " +
            		" quot_quoteid, quot_code, quot_name, quot_startdate, quot_enddate, quot_status, qoit_quantity, quot_currencyid, quot_currencyparity, " +
            		" SUM(qoit_quantity) as q, SUM(qoit_amount) as amounTotalGroupByProd " +
            		" FROM " + SQLUtil.formatKind(sFParams, "quoteitems") +
            		" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotegroups") +" ON(qogr_quotegroupid = qoit_quotegroupid) " +
            		" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "quotes") +" ON(quot_quoteid = qogr_quoteid) " +
            		" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "opportunities") +" ON(oppo_quoteid = quot_quoteid) " +
       			 	" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") +" ON(ortp_ordertypeid = quot_ordertypeid) " +
        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "products") +" ON(prod_productid = qoit_productid) " +
        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "productfamilies") +" ON(prfm_productfamilyid = prod_productfamilyid) " +
        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "productgroups") +" ON(prgp_productgroupid = prod_productgroupid) " +
        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "suppliers") +" ON(supl_supplierid = prod_supplierid) " +
        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "customers") +" ON(cust_customerid = quot_customerid) " +
        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "users") +" ON(user_userid = quot_userid) " +
        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = user_areaid) " +
        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflows") +" ON(wflw_wflowid = oppo_wflowid) " +
        		    " LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowfunnels") +" ON(wflf_wflowfunnelid = wflw_wflowfunnelid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") +" ON(wfty_wflowtypeid = oppo_wflowtypeid) " +
					" LEFT  JOIN " + SQLUtil.formatKind(sFParams, "wflowphases") +" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
        		    " WHERE qoit_quoteitemid > 0 " +
        		    " AND qoit_productid > 0 " +
        		    " AND quot_quoteid = " + pmQuote.getInt("opportunities", "oppo_quoteid") +
        		    whereProduct +
        		    where +
        		    whereProductFamily +
        		    whereProductGroup +
        		    " GROUP BY quot_code, prod_code " +
        		    " ORDER BY quot_code ASC, prod_productid ASC, quot_quoteid ASC  ";
            
            //System.out.println("sqlpmProduct: "+sql);
            pmProduct.doFetch(sql);
            int i = 1, sumQuantityProduct = 0;
            double sumAmounTotalGroupByProd = 0;
            while(pmProduct.next()) {
            	
  	  			double amounTotalGroupByProd = pmProduct.getDouble("amounTotalGroupByProd");

            	//Conversion a la moneda destino(seleccion del filtro)
  	  			currencyIdOrigin = 0; currencyIdDestiny = 0;
  	  			parityOrigin = 0; parityDestiny = 0;
  	  			currencyIdOrigin = pmProduct.getInt("quot_currencyid");
  	  			parityOrigin = pmProduct.getDouble("quot_currencyparity");
  	  			currencyIdDestiny = currencyId;
  	  			parityDestiny = defaultParity;
            	
  	  			amounTotalGroupByProd = pmCurrencyExchange.currencyExchange(amounTotalGroupByProd, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);            	
//            	sumAmounTotalGroupByProd += pmProduct.getDouble("amounTotalGroupByProd");
  	  			
  	  			sumAmounTotalGroupByProd += amounTotalGroupByProd;
            	sumQuantityProduct += pmProduct.getDouble("q");
            	sumTotalAmounTotalGroupByProd += amounTotalGroupByProd;
            	sumTotalQuantityProduct += pmProduct.getDouble("q");

            %>
	        	<tr class="reportCellEven">
		        	<%= HtmlUtil.formatReportCell(sFParams, i + "", BmFieldType.NUMBER) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_code"), BmFieldType.CODE) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_name"), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_description"), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_model"), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("productfamilies", "prfm_name"), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("productgroups", "prgp_name"), BmFieldType.STRING)) %>
		        	<% if(pmProduct.getInt("suppliers", "supl_supplierid") > 0) { %>
        				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + (pmProduct.getString("suppliers", "supl_code") + " " + pmProduct.getString("suppliers", "supl_name")), BmFieldType.STRING)) %>
		        	<% } else { %>
		        		<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
		        	<% } %>	
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getDouble("q"), BmFieldType.NUMBER) %>
		        	
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + amounTotalGroupByProd, BmFieldType.CURRENCY) %>
	        	</tr>
    	<%
    			i++;
            }//pmProduct
        %>
          	<tr class="reportCellEven reportCellCode">
	          	<td colspan="8">&nbsp;</td>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + sumQuantityProduct, BmFieldType.NUMBER) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + sumAmounTotalGroupByProd, BmFieldType.CURRENCY) %>
          	</tr>
            <%
	            countOppo++;
        } //if count	
    } // pmQuote
    %>    
    	<tr><td colspan="10">&nbsp;</td></tr>
	    <tr class="reportCellEven reportCellCode">
		  	<td colspan="8">&nbsp;</td>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotalQuantityProduct, BmFieldType.NUMBER) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotalAmounTotalGroupByProd, BmFieldType.CURRENCY) %>
		</tr>
    </table>
	<%	
}%>
    
<%	
	}
	
%>  
<% if (print.equals("1")) { %>
    <script>
        //window.print();
    </script>
    <% } 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
    }// Fin de if(no carga datos)
	pmCountOrdiProd.close();
	pmProduct.close();
	pmQuote.close();
	pmConnCount.close();
	pmConnCount.close();
    %>
  </body>
</html>
