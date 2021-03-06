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
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes de Productos en Pedidos Listado por Cliente";
	BmoOrder bmoOrder = new BmoOrder();
	BmoCustomer bmoCustomer = new BmoCustomer();
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
    
	String filters = "", sql = "", sqlGroup = "", where = "", whereOrder = "", whereProduct = "", whereExtra = "", lockStartDate = "", lockEndDate = "", 
			ordeStatus = "", paymentStatus = "", deliveryStatus = "", lockStatus  = "", whereProductFamily = "", 
			productFamilyId = "", productGroupId = "", whereProductGroup = "", sqlCurrency = "", fullName = "";
   	int programId = 0, rows = 0, orderId = 0,wflowtypeId=0, ordeId = 0, industryId = 0, customerId = 0, productId = 0, areaId = 0, userId = 0, 
   			showProductExtra = 0, currencyId = 0, dynamicColspan = 0, dynamicColspanMinus = 0;
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
   	if (request.getParameter("showProductExtra") != null) showProductExtra = Integer.parseInt(request.getParameter("showProductExtra"));
   	if (request.getParameter("prod_productfamilyid") != null) productFamilyId = request.getParameter("prod_productfamilyid");
    if (request.getParameter("prod_productgroupid") != null) productGroupId = request.getParameter("prod_productgroupid");
   	if (request.getParameter("orde_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("orde_currencyid"));
    if (request.getParameter("orde_wflowtypeid") != null) wflowtypeId = Integer.parseInt(request.getParameter("orde_wflowtypeid"));
    
	bmoProgram = (BmoProgram)pmProgram.get(programId);

    // Filtros listados
    if (orderId > 0) {
    	whereOrder += " AND orde_orderid = " + orderId;
    	filters += "<i>Pedido: </i>" + request.getParameter("orde_orderidLabel") + ", ";
    }
    
    if (customerId > 0) {
    	where += " AND cust_customerid = " + customerId;
    	filters += "<i>Cliente: </i>" + request.getParameter("orde_customeridLabel") + ", ";
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
        where += " AND orde_lockstart >= '" + lockStartDate + "'";
        filters += "<i>Apartado Inicio: </i>" + lockStartDate + ", ";
    }
    
    if (!lockEndDate.equals("")) {
        where += " AND orde_lockstart <= '" + lockEndDate + "'";
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
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmOrder(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    if (currencyId > 0) {
  		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);
  		defaultParity = bmoCurrency.getParity().toDouble();

  		filters += "<i>Moneda: </i>" + request.getParameter("orde_currencyidLabel")
  				+ " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
  	} else {
  		filters += "<i>Moneda: </i> Todas ";
  		sqlCurrency = "SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, " customers") +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(cust_customerid = orde_customerid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
  				" WHERE orde_orderid > 0 " +
  				whereOrder + where;
		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			sqlCurrency += " AND orde_orderid IN ( " +
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
	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
  	}
    
    PmConn pmCustomer = new PmConn(sFParams);
    pmCustomer.open();
    
    PmConn pmCountOrdiProd = new PmConn(sFParams);
    pmCountOrdiProd.open();
    
    PmConn pmProduct = new PmConn(sFParams);
    pmProduct.open();
    
    PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();
    
	// colspan dinamico
//    if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
//    	dynamicColspan++;
//    	dynamicColspan++;
//    	dynamicColspanMinus++;
//    	dynamicColspanMinus++;
//    }
//     if (sFParams.isFieldEnabled(bmoWFlow.getFunnel()))
//     	dynamicColspan++;
//    if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
//    	dynamicColspan++;
//    	dynamicColspanMinus++;
//    }
//    if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity()))
//    	dynamicColspan++;
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
            <b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Producto." : "Producto.")%>
			<b>Ordenado por:</b> Clave Cliente, Clave Producto.  
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<p>
<table class="report">
	<%
	if (!(currencyId > 0)) {
		int currencyIdWhile = 0, x = 1, y = 0;					
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			
			if (pmCurrencyWhile.getInt("cure_currencyid") != currencyIdWhile) {
				currencyIdWhile = pmCurrencyWhile.getInt("cure_currencyid");
//				currencyId = currencyIdWhile;
//		    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
				y = 0;
				%>
				<tr>
					<td class="reportHeaderCellCenter" colspan="<%= 18 + dynamicColspan%>">
						<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
					</td>
				</tr>
				<%
			}
			%>
			<tr class="">
				<td class="reportHeaderCellCenter" width="1%">#</td>
				<td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Producto / Item</td>
				<td class="reportHeaderCell">Descripci&oacute;n</td>
				<td class="reportHeaderCell">Modelo</td>
				<td class="reportHeaderCell">Cliente</td>
				<td class="reportHeaderCell">Familia Prod.</td>
				<td class="reportHeaderCell">Grupo Prod.</td>
				<td class="reportHeaderCell">Proveedor</td>
				<td class="reportHeaderCellCenter">Cantidad</td>
			</tr>
		    <%
		    sqlGroup = " SELECT cust_customerid FROM " + SQLUtil.formatKind(sFParams, " customers") +
		    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(cust_customerid = orde_customerid) " +
		    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
		    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
		    		" WHERE orde_orderid > 0 " +
		   			" AND orde_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
		    		whereOrder + where;
		    if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
		    	sqlGroup += " AND orde_orderid IN ( " +
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
		    sqlGroup += " GROUP BY orde_customerid ORDER BY cust_code ASC";
		
			//System.out.println("sqlGroup: " + sqlGroup);
		    
			int countItem = 1, countCust = 1, i = 1;
	    	double quantityByCustTotal = 0;

			pmCustomer.doFetch(sqlGroup);
		    while (pmCustomer.next()) {
		    	// Si el pedido no tiene items de Productos, no muestra titulos
		    	sql = " SELECT COUNT(ordi_orderitemid) AS countItemProd  FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
		        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
		    		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordg_orderid) " +
		    		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = ordi_productid) " +
		    		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
		    		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
			    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
		    			" WHERE ordi_orderitemid > 0 " +
			   			" AND orde_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
		        		" AND orde_customerid = " + pmCustomer.getInt("customers", "cust_customerid") +
		        		whereOrder +
		        		where +
		        		whereExtra +
		        		whereProduct +
		        		whereProductFamily + 
		        		whereProductGroup;
		    	
		    	pmCountOrdiProd.doFetch(sql);
		    	if (pmCountOrdiProd.next())
		    		countItem = pmCountOrdiProd.getInt("countItemProd");
		    	
				if (countItem > 0) {
					//Estatus
					//bmoCustomer.getStatus().setValue(pmCustomer.getString("customers", "cust_status"));
					%>
					<!--
						<tr class="">
							<td class="reportGroupCell" colspan="10">
							<b>
								# <%//= countCust %> |
								<%//= pmCustomer.getString("customers", "cust_code") %>
								<%//= pmCustomer.getString("customers", "cust_displayname") %>
								| <%//= pmCustomer.getString("customers", "cust_legalname") %>
								| <%//= bmoCustomer.getStatus().getSelectedOption().getLabel()%>
							</b>
							</td>
						</tr>
					-->
					<%
					sql = " SELECT prod_code, prod_name, prod_description, prod_model, prfm_name, prgp_name, supl_code, supl_name, ordi_productid, ordi_name, ordi_description, " +
							" orde_orderid, orde_code, orde_name, orde_lockstart, orde_lockend,  orde_status, orde_lockstatus, orde_deliverystatus, orde_paymentstatus, ordi_quantity, " + 
							" cust_code, cust_displayname, cust_customertype, cust_legalname, supl_supplierid , SUM(ordi_quantity) as q " +
							" FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordg_orderid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = ordi_productid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" ON(prfm_productfamilyid = prod_productfamilyid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON(prgp_productgroupid = prod_productgroupid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = prod_supplierid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
							" WHERE ordi_orderitemid > 0 " +
				   			" AND orde_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
							" AND cust_customerid = " + pmCustomer.getInt("customers", "cust_customerid") +
							whereProduct +
							where + 
							whereExtra + 
							whereProductFamily +
							whereProductGroup +
							" GROUP BY prod_productid" +
							" ORDER BY prod_productid ASC "; 
					//System.out.println("sql-pmProduct: "+sql);
					pmProduct.doFetch(sql);
			            
		            while(pmProduct.next()) {
		            	if (pmProduct.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
		      		  		fullName = pmProduct.getString("cust_code") + " " + pmProduct.getString("cust_legalname");	
		      		  	else
		      		  		fullName = pmProduct.getString("cust_code") + " " + pmProduct.getString("cust_displayname");
		            
		            %>
			        	<tr class="reportCellEven">
				        	<%=HtmlUtil.formatReportCell(sFParams, i + "", BmFieldType.NUMBER) %>
				        	<% 	if (pmProduct.getInt("orderitems", "ordi_productid") > 0) { %>
						        	<%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_code"), BmFieldType.CODE) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("products", "prod_name")), BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("products", "prod_description")), BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("products", "prod_model")), BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + HtmlUtil.stringToHtml(fullName), BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("productfamilies", "prfm_name")), BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("productgroups", "prgp_name")), BmFieldType.STRING) %>
						        	<% 	if (pmProduct.getInt("suppliers", "supl_supplierid") > 0) { %>
					        				<%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getString("suppliers", "supl_code") + " " + HtmlUtil.stringToHtml(pmProduct.getString("suppliers", "supl_name")), BmFieldType.STRING) %>
						        	<% 	} else { %>
						        			<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
						        	<% 	} %>	
				        	<% 	} else { %>
					        		<td class="reportCellCode">Extras</td>
					        		<td class="reportCellText">
					        			Extras
					        		</td>
							        <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + HtmlUtil.stringToHtml(fullName), BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				        	<% 	} 
				        	quantityByCustTotal += pmProduct.getDouble("q");
				        	%>
				        	<%=HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getDouble("q"), BmFieldType.NUMBER) %>
			        	</tr>
		    	<%
		    			i++;
		            }//pmProduct
					countCust++;
		        } // fin if count
		    } // pmCustomer
		    %>
		    <tr><td colspan="10">&nbsp;</td></tr>
	        <tr class="reportCellEven reportCellCode">
		        <td class="ReportCellText" colspan="9">
		        	&nbsp;
		        </td>
		        <%= HtmlUtil.formatReportCell(sFParams, "" + quantityByCustTotal, BmFieldType.NUMBER) %>
	        </tr>
	        <tr><td colspan="10">&nbsp;</td></tr>

		    <%
		} // Fin pmCurrencyWhile
	} else { // Si existe moneda destino
		%>
		<tr class="">
			<td class="reportHeaderCellCenter" width="1%">#</td>
			<td class="reportHeaderCell">Clave</td>
			<td class="reportHeaderCell">Producto / Item</td>
			<td class="reportHeaderCell">Descripci&oacute;n</td>
			<td class="reportHeaderCell">Modelo</td>
			<td class="reportHeaderCell">Cliente</td>
			<td class="reportHeaderCell">Familia Prod.</td>
			<td class="reportHeaderCell">Grupo Prod.</td>
			<td class="reportHeaderCell">Proveedor</td>
			<td class="reportHeaderCellCenter">Cantidad</td>
		</tr>
	    <%
	    sqlGroup = " SELECT cust_customerid FROM " + SQLUtil.formatKind(sFParams, " customers") +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(cust_customerid = orde_customerid) " +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
	    		" WHERE orde_orderid > 0 " +
	    		whereOrder + where;
	    if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
	    	sqlGroup += " AND orde_orderid IN ( " +
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
	    sqlGroup += " GROUP BY orde_customerid ORDER BY cust_code ASC";
	
		//System.out.println("sqlGroup: " + sqlGroup);
	    
		int countItem = 1, countCust = 1, i = 1;
    	double quantityByCustTotal = 0;

		pmCustomer.doFetch(sqlGroup);
	    while (pmCustomer.next()) {
	    	// Si el pedido no tiene items de Productos, no muestra titulos
	    	sql = " SELECT COUNT(ordi_orderitemid) AS countItemProd  FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
	        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
	    		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordg_orderid) " +
	    		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = ordi_productid) " +
	    		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
	    		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
		    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
	    			" WHERE ordi_orderitemid > 0 " +
	        		" AND orde_customerid = " + pmCustomer.getInt("customers", "cust_customerid") +
	        		whereOrder +
	        		where +
	        		whereExtra +
	        		whereProduct +
	        		whereProductFamily + 
	        		whereProductGroup;
	    	
	    	pmCountOrdiProd.doFetch(sql);
	    	if (pmCountOrdiProd.next())
	    		countItem = pmCountOrdiProd.getInt("countItemProd");
	    	
			if (countItem > 0) {
				//Estatus
				//bmoCustomer.getStatus().setValue(pmCustomer.getString("customers", "cust_status"));
				%>
				<!--
					<tr class="">
						<td class="reportGroupCell" colspan="10">
						<b>
							# <%//= countCust %> |
							<%//= pmCustomer.getString("customers", "cust_code") %>
							<%//= pmCustomer.getString("customers", "cust_displayname") %>
							| <%//= pmCustomer.getString("customers", "cust_legalname") %>
							| <%//= bmoCustomer.getStatus().getSelectedOption().getLabel()%>
						</b>
						</td>
					</tr>
				-->
				<%
				sql = " SELECT prod_code, prod_name, prod_description, prod_model, prfm_name, prgp_name, supl_code, supl_name, ordi_productid, ordi_name, ordi_description, " +
						" orde_orderid, orde_code, orde_name, orde_lockstart, orde_lockend,  orde_status, orde_lockstatus, orde_deliverystatus, orde_paymentstatus, ordi_quantity, " + 
						" cust_code, cust_displayname, cust_customertype, cust_legalname, supl_supplierid , SUM(ordi_quantity) as q " +
						" FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordg_orderid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = ordi_productid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" ON(prfm_productfamilyid = prod_productfamilyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON(prgp_productgroupid = prod_productgroupid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = prod_supplierid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) " +
						" WHERE ordi_orderitemid > 0 " +
						" AND cust_customerid = " + pmCustomer.getInt("customers", "cust_customerid") +
						whereProduct +
						where + 
						whereExtra + 
						whereProductFamily +
						whereProductGroup +
						" GROUP BY prod_productid" +
						" ORDER BY prod_productid ASC "; 
				//System.out.println("sql: "+sql);
				pmProduct.doFetch(sql);
		            
	            while(pmProduct.next()) {
	            	if (pmProduct.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	      		  		fullName = pmProduct.getString("cust_code") + " " + pmProduct.getString("cust_legalname");	
	      		  	else
	      		  		fullName = pmProduct.getString("cust_code") + " " + pmProduct.getString("cust_displayname");
	            
	            %>
		        	<tr class="reportCellEven">
			        	<%=HtmlUtil.formatReportCell(sFParams, i + "", BmFieldType.NUMBER) %>
			        	<% 	if (pmProduct.getInt("orderitems", "ordi_productid") > 0) { %>
					        	<%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_code"), BmFieldType.CODE) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("products", "prod_name")), BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("products", "prod_description")), BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("products", "prod_model")), BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + HtmlUtil.stringToHtml(fullName), BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("productfamilies", "prfm_name")), BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmProduct.getString("productgroups", "prgp_name")), BmFieldType.STRING) %>
					        	<% 	if (pmProduct.getInt("suppliers", "supl_supplierid") > 0) { %>
				        				<%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getString("suppliers", "supl_code") + " " + HtmlUtil.stringToHtml(pmProduct.getString("suppliers", "supl_name")), BmFieldType.STRING) %>
					        	<% 	} else { %>
					        			<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
					        	<% 	} %>	
			        	<% 	} else { %>
				        		<td class="reportCellCode">Extras</td>
				        		<td class="reportCellText">
				        			Extras
				        		</td>
						        <%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + HtmlUtil.stringToHtml(fullName), BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
			        	<% 	} 
			        	quantityByCustTotal += pmProduct.getDouble("q");
			        	%>
			        	<%=HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getDouble("q"), BmFieldType.NUMBER) %>
		        	</tr>
	    	<%
	    			i++;
	            }//pmProduct
				countCust++;
	        } // fin if count
	    } // pmCustomer
	    %>
	    <tr><td colspan="10">&nbsp;</td></tr>
        <tr class="reportCellEven reportCellCode">
	        <td class="ReportCellText" colspan="9">
	        	&nbsp;
	        </td>
	        <%= HtmlUtil.formatReportCell(sFParams, "" + quantityByCustTotal, BmFieldType.NUMBER) %>
        </tr>
	    <%
	} // Fin existe moneda%>
</table>
<%
		
	}// Fin de if(no carga datos)
	pmCountOrdiProd.close();
	pmProduct.close();
	pmCustomer.close();
	pmCurrencyWhile.close();
   
%>  
<% 	if (print.equals("1")) { %>
	    <script>
	        //window.print();
	    </script>
<% 	} %>
  </body>
</html>
