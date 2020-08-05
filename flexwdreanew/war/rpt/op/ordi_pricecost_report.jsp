<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba Valdez
 * @version 2013-10
 */ -->

<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reporte de Items en Pedidos";
	BmoProduct bmoProduct = new BmoProduct();
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
    
    String filters = "", sql = "",  sqlCurrency = "", sqlGroup = "", where = "", whereOrder = "", whereProduct = "", whereExtra = "", lockStartDate = "", lockEndDate = "", 
						ordeStatus = "", paymentStatus = "", deliveryStatus = "", lockStatus  = "", whereProductFamily = "", 
					   			productFamilyId = "", productGroupId = "", whereProductGroup = "", fullName = "";
   	int programId = 0, rows = 0, orderId = 0, ordeId = 0, industryId = 0, customerId = 0, productId = 0, areaId = 0, userId = 0, 
   			showProductExtra = 0,wflowtypeId=0, currencyId = 0, dynamicColspan = 0, dynamicColspanMinus = 0;
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
							 " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (wflu_wflowid = wflw_wflowid) " +
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
  		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, " orders ") +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = orde_customerid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = orde_userid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = user_areaid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
  				" WHERE orde_orderid > 0 " +
  				whereOrder + where;
  		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
  			sqlCurrency += " AND orde_orderid IN ( " +
  					" SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, " orders ") +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordg_orderid = orde_orderid) " +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderitems")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = ordi_productid) " +
  					" WHERE orde_orderid = orde_orderid " +
  					whereProduct +
  					whereExtra +
  					whereProductFamily +
  					whereProductGroup +
  					" ) ";
  		}
	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
  	}
    
    PmConn pmOrder = new PmConn(sFParams);
    pmOrder.open();
    
    PmConn pmOrderGroup = new PmConn(sFParams);
    pmOrderGroup.open();
    
    PmConn pmCountOrdiProd = new PmConn(sFParams);
    pmCountOrdiProd.open();
    
    PmConn pmProduct = new PmConn(sFParams);
    pmProduct.open();
    
    PmConn pmCurrencyWhile = new PmConn(sFParams);
   	pmCurrencyWhile.open();
       
   	// colspan dinamico
//       if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
//       	dynamicColspan++;
//       	dynamicColspan++;
//       	dynamicColspanMinus++;
//       	dynamicColspanMinus++;
//       }
//        if (sFParams.isFieldEnabled(bmoWFlow.getFunnel()))
//        	dynamicColspan++;
//       if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
//       	dynamicColspan++;
//       	dynamicColspanMinus++;
//       }
//       if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity()))
//       	dynamicColspan++;
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
            <b>Filtros:</b> <%= filters %><br>
            <b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Pedido." : "Pedido.")%>
			<b>Ordenado por:</b> Clave Pedido, Id Grupo, Id Item.
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
			sqlGroup = " SELECT orde_orderid, orde_code, orde_name, orde_amount, orde_total, orde_currencyid, orde_currencyparity, " +
		    		" orde_status, orde_lockstatus, orde_deliverystatus, orde_paymentstatus, orde_lockstart, orde_lockend, " + 
		    		" cust_customertype, cust_code, cust_displayname, cust_legalname, cure_code " +
		    		" FROM " + SQLUtil.formatKind(sFParams, " orders ") +
		    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = orde_customerid) " +
		    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = orde_userid) " +
		    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON(area_areaid = user_areaid) " +
		    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) " +
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
		    sqlGroup += " ORDER BY orde_code ASC";
		
			//System.out.println("sqlOrders: " + sqlGroup);
		    
		    pmOrder.doFetch(sqlGroup);
		    int iTotalGroup = 0, iTotalItems = 0, countOrder = 1;
		    double priceTotal = 0, priceTotalA = 0,
		    		basePriceTotal = 0, baseCostTotal = 0, 
		    		basePriceTotalA = 0, baseCostTotalA= 0,
		    		iquantityProductOrderTotal = 0;
		
		    while(pmOrder.next()) {
		    	
		    	// Estatus
		    	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
		    	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));              
		    	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
		    	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
		    	
		    	if (pmOrder.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
				  		fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");	
				  	else
				  		fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
		    	
		    	// Contar los items del todo el pedido
		    	String sqlCountItemTotal = " SELECT COUNT(ordi_orderitemid) AS countItem " +
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
		    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
		    			" WHERE ordi_orderitemid > 0 " +
		    			" AND orde_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
			   			" AND orde_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
		    			whereProduct +
		    			where + 
		    			whereExtra +
		    			whereProductFamily + 
		    			whereProductGroup +
		    			" ORDER BY ordi_orderitemid ASC ";
		    	
		    	//System.out.println("sqlCount-ItemTotal: " + sqlCountItemTotal);
		
		    	int countItem = 0;
		    	pmCountOrdiProd.doFetch(sqlCountItemTotal);
		    	if (pmCountOrdiProd.next())
		    		countItem = pmCountOrdiProd.getInt("countItem");
		    	
		    	if (countItem > 0) {
		    		
		    		if (pmCurrencyWhile.getInt("cure_currencyid") != currencyIdWhile) {
						currencyIdWhile = pmCurrencyWhile.getInt("cure_currencyid");
						currencyId = currencyIdWhile;
				    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
						y = 0;
						%>
						<tr>
							<td class="reportHeaderCellCenter" colspan="<%= 18 + dynamicColspan%>">
								<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
							</td>
						</tr>
						<%
					}

		            sql = " SELECT ordg_ordergroupid, orde_orderid " +
		            		" FROM " + SQLUtil.formatKind(sFParams, " ordergroups ") +
		            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "  orders")+" ON(orde_orderid = ordg_orderid) " +
		        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
		        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
		        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
		        		    " WHERE ordg_ordergroupid > 0 " +
		        		    " AND ordg_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
		        		    // where + 
		        		    " ORDER BY ordg_ordergroupid ASC"; 
		            
		            //System.out.println("sql-Group: "+sql);
		            
		            pmOrderGroup.doFetch(sql);
		            boolean firstOrder = true;
		            int iGroup = 0, iItemsTotal = 0;
		            double priceSumTotalGroupTotal = 0, priceSumGroupTotal = 0,
		            		basePriceSumGroupTotal = 0, baseCostSumGroupTotal = 0, 
		            		basePriceSumTotalGroupTotal = 0, baseCostSumTotalGroupTotal = 0,
		            		iquantityProductOrder = 0;
		            
		            while(pmOrderGroup.next()) {
		            	
		            	// Contar los items  del grupo
		    			int countItemGroup = 0;
			        	String sqlQ = " SELECT COUNT(ordi_orderitemid) AS countItemGroup  FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
			            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
			        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordg_orderid) " +
			        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = ordi_productid) " +
			        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
			        			" WHERE ordi_orderitemid > 0 " +
			            		" AND ordg_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
			            		" AND ordg_ordergroupid = " + pmOrderGroup.getInt("ordergroups", "ordg_ordergroupid") +
			            		whereExtra +
			            		whereProduct +
			            		whereProductFamily + 
		            			whereProductGroup;
			        	
			        	//System.out.println("sqlQ: " + sqlQ);
		
			        	pmCountOrdiProd.doFetch(sqlQ);
			        	if (pmCountOrdiProd.next())
			        		countItemGroup = pmCountOrdiProd.getInt("countItemGroup");
			            
			        	if (countItemGroup > 0 ) {
			        		iTotalGroup++;
			        		iGroup++;
			        		
		    	        	// Items del grupo
				            sql = " SELECT prod_code, prod_name, prod_description, prod_model, supl_supplierid, supl_code, supl_name, cust_code, cust_displayname, ordi_productid, ordi_name, ordi_description,  " +
				            		" orde_orderid, orde_code, orde_name, orde_lockstart, orde_lockend,  orde_status, orde_lockstatus, orde_deliverystatus, orde_paymentstatus, " + 
				            		" ordi_quantity, ordi_price, ordi_baseprice, ordi_basecost, ordi_days, ordi_orderitemid, ordg_ordergroupid, ordg_name, ordi_ordergroupid " +
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
				        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
				        		    " WHERE ordi_orderitemid > 0 " +
				        		    " AND orde_orderid = " + pmOrderGroup.getInt("orders", "orde_orderid") +
				        		    " AND ordi_ordergroupid = " + pmOrderGroup.getInt("ordergroups", "ordg_ordergroupid") +
				        		    whereProduct +
				        		    where + 
				        		    whereExtra +
				        		    whereProductFamily + 
				        		    whereProductGroup +
				        		    " ORDER BY ordi_ordergroupid ASC, ordi_orderitemid ASC "; 
				            
				            //System.out.println("sql-A: "+sql);
				            
				            pmProduct.doFetch(sql);
				            
				            boolean firstGroup = true;
				            int iItem = 0, prodId = 0;
				            double priceSumGroup = 0, priceSumTotalGroup = 0,
				            		basePriceSumGroup = 0, baseCostSumGroup = 0, 
				            		basePriceSumTotalGroup = 0, baseCostSumTotalGroup = 0;
				            while(pmProduct.next()) {
				            	double quantity = pmProduct.getDouble("ordi_quantity");
				            	double days = pmProduct.getDouble("ordi_days");
				            	double price = pmProduct.getDouble("ordi_price");
				            	double baseprice = pmProduct.getDouble("ordi_baseprice");
				            	double basecost = pmProduct.getDouble("ordi_basecost");
				            	
//				            	// Conversion a la moneda destino(seleccion del filtro)
//				            	price = pmCurrencyExchange.currencyExchange(price, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//				            	baseprice = pmCurrencyExchange.currencyExchange(baseprice, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
//				            	basecost = pmCurrencyExchange.currencyExchange(basecost, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
		
				            	iItem++;
			        			iItemsTotal++;
			        			iTotalItems++;
			        			iquantityProductOrder += quantity;
			        			iquantityProductOrderTotal += quantity;
			        			
			        			// Totales de grupo
				            	priceSumGroup += price;
				            	basePriceSumGroup += baseprice;
				            	baseCostSumGroup += basecost;
				            	
				            	priceSumTotalGroup += (quantity * price * days);
				            	basePriceSumTotalGroup += (quantity * baseprice * days);
				            	baseCostSumTotalGroup += (quantity * basecost * days);
				            	
				            	// Totales del pedido
				            	priceSumGroupTotal += price;
				            	basePriceSumGroupTotal += baseprice;
				            	baseCostSumGroupTotal += basecost;
				            	
				            	priceSumTotalGroupTotal += (quantity * price * days);
				            	basePriceSumTotalGroupTotal += (quantity * baseprice * days);
				            	baseCostSumTotalGroupTotal += (quantity * basecost * days);
				            	
				                // Total general
				                priceTotal += price;
				                basePriceTotal += baseprice;
				                baseCostTotal += basecost;
				                
				                priceTotalA += (quantity * price * days);
				                basePriceTotalA += (quantity * baseprice * days);
				                baseCostTotalA += (quantity * basecost * days);
			
			        			// Contar los productos 
			        			double countItemQuantity = 0;
    							int countItemProd = 0;
			    	        	sqlQ = " SELECT SUM(ordi_quantity) AS countItemQuantity, COUNT(ordi_orderitemid) AS countItemProd  FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
			    	            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
					        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = ordi_productid) " +
					        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordg_orderid) " +
					        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = orde_currencyid) " +
			    	        			" WHERE ordi_orderitemid > 0 " +
			    	            		" AND ordg_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
			    	            		" AND ordg_ordergroupid = " + pmOrderGroup.getInt("ordergroups", "ordg_ordergroupid") +
			    	            		whereProduct +
			        					whereExtra +
			    		   				whereProductFamily +
			    		   				whereProductGroup;
			    	        	
			    	        	//System.out.println("sql-Ext: " + sqlQ);
			    	        	
			    	        	pmCountOrdiProd.doFetch(sqlQ);
			    	        	if(pmCountOrdiProd.next()) {
			    	        		countItemQuantity = pmCountOrdiProd.getDouble("countItemQuantity");
			    	        		countItemProd = pmCountOrdiProd.getInt("countItemProd");
			    	        	}
			    	        	
				            	if (firstOrder) {
				            		firstOrder = false;
				            		%>
						            <tr class="">
						        		<td class="reportGroupCell" colspan="15">
					        				# <%= countOrder%> |
					        				<%= pmOrder.getString("orders", "orde_code") %>
					        				<%= HtmlUtil.stringToHtml(pmOrder.getString("orders", "orde_name")) %> |
								        	Cliente: <%= HtmlUtil.stringToHtml(fullName) %> | 
								        	Fechas: 
								        	<%= pmOrder.getString("orders", "orde_lockstart") %>
								        	-
								        	<%= pmOrder.getString("orders", "orde_lockend") %> |
						        			Tipo de Cambio: <%= pmOrder.getDouble("orde_currencyparity") %> |					                
					                		<span title='Origen: <%= SFServerUtil.formatCurrency(pmOrder.getDouble("orde_total"))%> <%= pmOrder.getString("cure_code") %>'>
						                   		Total: <%=SFServerUtil.formatCurrency(pmOrder.getDouble("orde_total")) %>
					                			<%= pmOrder.getString("cure_code") %>
					                   		</span>
					        			</td>
					                </tr>
					                <tr class="">
						                <td class="reportHeaderCell">Grupo</td>
						                <td class="reportHeaderCellCenter" width="1%">#</td>
						                <td class="reportHeaderCell">Clave</td>
						                <td class="reportHeaderCell">Producto / Item</td>
						                <td class="reportHeaderCell">Descripci&oacute;n</td>
						                <td class="reportHeaderCell">Modelo</td>
						                <td class="reportHeaderCell">Proveedor</td>
						                <td class="reportHeaderCellCenter">D&iacute;as</td>
						                <td class="reportHeaderCellCenter">Cantidad</td>
						                <td class="reportHeaderCellRight">Precio <br>en Pedido</td>
						                <td class="reportHeaderCellRight">Precio Total<br>en Pedido</td>
						                <td class="reportHeaderCellRight">Precio Base</td>
						                <td class="reportHeaderCellRight">Precio Total</td>
						                <td class="reportHeaderCellRight">Costo Base</td>
						                <td class="reportHeaderCellRight">Costo Total</td>
						            </tr>
			            	<% 	} %>
				        		<tr class="reportCellEven">
					        		<% 	if (firstGroup) {
							        		firstGroup = false;
						        		%>
								        	<td class="reportCellText" rowspan="<%= countItemGroup + 1 %>">
								        		<%= iGroup%>. <%= HtmlUtil.stringToHtml(pmProduct.getString("ordergroups", "ordg_name"))%>
								        	</td>
						        	<% 	} %>
						        	<%=HtmlUtil.formatReportCell(sFParams, iItem + "", BmFieldType.NUMBER) %>
						        	
						        	<% 	if (pmProduct.getInt("orderitems", "ordi_productid") > 0) { %>
								        	<%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_code"), BmFieldType.CODE) %>
								        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_name"), BmFieldType.STRING)) %>
								        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_description"), BmFieldType.STRING)) %>
								        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_model"), BmFieldType.STRING)) %>
								        	<% 	if (pmProduct.getInt("suppliers", "supl_supplierid") > 0) { %>
							        				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + (pmProduct.getString("suppliers", "supl_code") + " " + pmProduct.getString("suppliers", "supl_name")), BmFieldType.STRING)) %>
								        	<% 	} else { %>
								        			<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
								        	<% 	} %>
				
						        	<% 	} else { %>
							        		<td class="reportCellCode">Extra</td>
							        		<td class="reportCellText">
							        			<%= HtmlUtil.stringToHtml(pmProduct.getString("orderitems", "ordi_name"))%>
							        		</td>
							        		<td class="reportCellText" >
							        			<%= HtmlUtil.stringToHtml(pmProduct.getString("orderitems", "ordi_description"))%>
							        		</td>
								        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
								        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
						        	<% 	} %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getDouble("ordi_days"), BmFieldType.NUMBER) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getDouble("ordi_quantity"), BmFieldType.NUMBER) %>
		
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + price, BmFieldType.CURRENCY) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + (quantity * price * days), BmFieldType.CURRENCY) %>
						        	
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseprice, BmFieldType.CURRENCY) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + (quantity * baseprice * days), BmFieldType.CURRENCY) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + basecost, BmFieldType.CURRENCY) %>
						        	<%= HtmlUtil.formatReportCell(sFParams, "" + (quantity * basecost * days), BmFieldType.CURRENCY) %>
					        	</tr>
					        	<%
				            } //pmProduct
				            %>
				            <tr class="reportCellCode reportCellEven">
				        		<td class="reportCellText" colspan="8">&nbsp;</td>
				        		<%= HtmlUtil.formatReportCell(sFParams, "" + priceSumGroup, BmFieldType.CURRENCY) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + priceSumTotalGroup, BmFieldType.CURRENCY) %>
					        	
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceSumGroup, BmFieldType.CURRENCY) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceSumTotalGroup, BmFieldType.CURRENCY) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostSumGroup, BmFieldType.CURRENCY) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostSumTotalGroup, BmFieldType.CURRENCY) %>
				        	</tr>
				        	<%  
		        		} // if Si tiene items en grupo
		            } // pmProfile
		            %>
			        <tr class="reportCellCode reportCellEven">
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + iGroup, BmFieldType.STRING) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + iItemsTotal, BmFieldType.NUMBER) %>
			        	
			    		<td class="reportCellText" colspan="6">&nbsp;</td>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + iquantityProductOrder, BmFieldType.NUMBER) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + priceSumGroupTotal, BmFieldType.CURRENCY) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + priceSumTotalGroupTotal, BmFieldType.CURRENCY) %>
			        	
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceSumGroupTotal, BmFieldType.CURRENCY) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceSumTotalGroupTotal, BmFieldType.CURRENCY) %>
			
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostSumGroupTotal, BmFieldType.CURRENCY) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostSumTotalGroupTotal, BmFieldType.CURRENCY) %>
			    	</tr>
			    	<%	
					countOrder++;
		    	}	// if si tiene item en todo el pedido
		    } 	// pmOrder
		        %>
		    <tr ><td colspan="15">&nbsp;</td></tr>
		    <tr class="reportCellCode reportCellEven">
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + iTotalGroup, BmFieldType.STRING) %>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + iTotalItems, BmFieldType.NUMBER) %>
		    	
				<td class="reportCellText" colspan="6">&nbsp;</td>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + iquantityProductOrderTotal, BmFieldType.NUMBER) %>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + priceTotal, BmFieldType.CURRENCY) %>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + priceTotalA, BmFieldType.CURRENCY) %>
		    	
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceTotal, BmFieldType.CURRENCY) %>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceTotalA, BmFieldType.CURRENCY) %>
		
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostTotal, BmFieldType.CURRENCY) %>
		    	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostTotalA, BmFieldType.CURRENCY) %>
			</tr>
		    <tr ><td colspan="15">&nbsp;</td></tr>
			<%	
		} // Fin de no existe moneda
	} else { // Existe moneda destino
	    sqlGroup = " SELECT orde_orderid, orde_code, orde_name, orde_amount, orde_total, orde_currencyid, orde_currencyparity, " +
	    		" orde_status, orde_lockstatus, orde_deliverystatus, orde_paymentstatus, orde_lockstart, orde_lockend, " + 
	    		" cust_customertype, cust_code, cust_displayname, cust_legalname, cure_code " +
	    		" FROM " + SQLUtil.formatKind(sFParams, " orders") +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
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
	    sqlGroup += " ORDER BY orde_code ASC";
	
		//System.out.println("sqlOrders: " + sqlGroup);
	    
	    pmOrder.doFetch(sqlGroup);
	    int iTotalGroup = 0, iTotalItems = 0, countOrder = 1;
	    double priceTotal = 0, priceTotalA = 0,
	    		basePriceTotal = 0, baseCostTotal = 0, 
	    		basePriceTotalA = 0, baseCostTotalA= 0,
	    		iquantityProductOrderTotal = 0;
	
	    while(pmOrder.next()) {
	    	double total = pmOrder.getDouble("orde_total");
	
	    	// Datos de la moneda del pedido
	    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	    	double parityOrigin = 0, parityDestiny = 0;
	    	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
	    	parityOrigin = pmOrder.getDouble("orde_currencyparity");
	    	currencyIdDestiny = currencyId;
	    	parityDestiny = defaultParity;
	    	
	    	// Estatus
	    	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
	    	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));              
	    	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
	    	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
	    	
	    	if (pmOrder.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
			  		fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");	
			  	else
			  		fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
	    	
	    	// Contar los items del todo el pedido
	    	String sqlCountItemTotal = " SELECT prod_productid, cust_code, " +
	    			" orde_orderid, orde_lockstart, orde_lockend, orde_status, orde_lockstatus, orde_deliverystatus, orde_paymentstatus, " + 
	    			" ordi_orderitemid, ordi_ordergroupid, " +
	    			" COUNT(ordi_orderitemid) AS countItem " +
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
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
	    			" WHERE ordi_orderitemid > 0 " +
	    			" AND orde_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
	    			whereProduct +
	    			where + 
	    			whereExtra +
	    			whereProductFamily + 
	    			whereProductGroup +
	    			" ORDER BY ordi_orderitemid ASC ";
	    	
	    	//System.out.println("sqlCount-ItemTotal: " + sqlCountItemTotal);
	
	    	int countItem = 0;
	    	pmCountOrdiProd.doFetch(sqlCountItemTotal);
	    	if (pmCountOrdiProd.next())
	    		countItem = pmCountOrdiProd.getInt("countItem");
	    	
	    	if (countItem > 0) {
	    	
	            sql = " SELECT ordg_ordergroupid, orde_orderid " +
	            		" FROM " + SQLUtil.formatKind(sFParams, " ordergroups ") +
	            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "  orders")+" ON(orde_orderid = ordg_orderid) " +
	        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
	        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
	        		    " WHERE ordg_ordergroupid > 0 " +
	        		    " AND ordg_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
	        		    // where + 
	        		    " ORDER BY ordg_ordergroupid ASC"; 
	            
	            //System.out.println("sql-Group: "+sql);
	            
	            pmOrderGroup.doFetch(sql);
	            boolean firstOrder = true;
	            int iGroup = 0, iItemsTotal = 0;
	            double priceSumTotalGroupTotal = 0, priceSumGroupTotal = 0,
	            		basePriceSumGroupTotal = 0, baseCostSumGroupTotal = 0, 
	            		basePriceSumTotalGroupTotal = 0, baseCostSumTotalGroupTotal = 0,
	            		iquantityProductOrder = 0;
	            
	            while(pmOrderGroup.next()) {
	            	
	            	// Contar los items  del grupo
	    			int countItemGroup = 0;
		        	String sqlQ = " SELECT COUNT(ordi_orderitemid) AS countItemGroup  FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
		            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
		        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordg_orderid) " +
		        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = ordi_productid) " +
		        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
		        			" WHERE ordi_orderitemid > 0 " +
		            		" AND ordg_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
		            		" AND ordg_ordergroupid = " + pmOrderGroup.getInt("ordergroups", "ordg_ordergroupid") +
		            		whereExtra +
		            		whereProduct +
		            		whereProductFamily + 
	            			whereProductGroup;
		        	
		        	//System.out.println("sqlQ: " + sqlQ);
	
		        	pmCountOrdiProd.doFetch(sqlQ);
		        	if (pmCountOrdiProd.next())
		        		countItemGroup = pmCountOrdiProd.getInt("countItemGroup");
		            
		        	if (countItemGroup > 0 ) {
		        		iTotalGroup++;
		        		iGroup++;
		        		
	    	        	// Items del grupo
			            sql = " SELECT prod_code, prod_name, prod_description, prod_model, supl_supplierid, supl_code, supl_name, cust_code, cust_displayname, ordi_productid, ordi_name, ordi_description,  " +
			            		" orde_orderid, orde_code, orde_name, orde_lockstart, orde_lockend,  orde_status, orde_lockstatus, orde_deliverystatus, orde_paymentstatus, " + 
			            		" ordi_quantity, ordi_price, ordi_baseprice, ordi_basecost, ordi_days, ordi_orderitemid, ordg_ordergroupid, ordg_name, ordi_ordergroupid " +
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
			        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
			        		    " WHERE ordi_orderitemid > 0 " +
			        		    " AND orde_orderid = " + pmOrderGroup.getInt("orders", "orde_orderid") +
			        		    " AND ordi_ordergroupid = " + pmOrderGroup.getInt("ordergroups", "ordg_ordergroupid") +
			        		    whereProduct +
			        		    where + 
			        		    whereExtra +
			        		    whereProductFamily + 
			        		    whereProductGroup +
			        		    " ORDER BY ordi_ordergroupid ASC, ordi_orderitemid ASC "; 
			            
			            //System.out.println("sql-A: "+sql);
			            
			            pmProduct.doFetch(sql);
			            
			            boolean firstGroup = true;
			            int iItem = 0, prodId = 0;
			            double priceSumGroup = 0, priceSumTotalGroup = 0,
			            		basePriceSumGroup = 0, baseCostSumGroup = 0, 
			            		basePriceSumTotalGroup = 0, baseCostSumTotalGroup = 0;
			            while(pmProduct.next()) {
			            	double quantity = pmProduct.getDouble("ordi_quantity");
			            	double days = pmProduct.getDouble("ordi_days");
			            	double price = pmProduct.getDouble("ordi_price");
			            	double baseprice = pmProduct.getDouble("ordi_baseprice");
			            	double basecost = pmProduct.getDouble("ordi_basecost");
			            	
			            	// Conversion a la moneda destino(seleccion del filtro)
			            	price = pmCurrencyExchange.currencyExchange(price, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			            	baseprice = pmCurrencyExchange.currencyExchange(baseprice, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			            	basecost = pmCurrencyExchange.currencyExchange(basecost, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	
			            	iItem++;
		        			iItemsTotal++;
		        			iTotalItems++;
		        			iquantityProductOrder += quantity;
		        			iquantityProductOrderTotal += quantity;
		        			
		        			// Totales de grupo
			            	priceSumGroup += price;
			            	basePriceSumGroup += baseprice;
			            	baseCostSumGroup += basecost;
			            	
			            	priceSumTotalGroup += (quantity * price * days);
			            	basePriceSumTotalGroup += (quantity * baseprice * days);
			            	baseCostSumTotalGroup += (quantity * basecost * days);
			            	
			            	// Totales del pedido
			            	priceSumGroupTotal += price;
			            	basePriceSumGroupTotal += baseprice;
			            	baseCostSumGroupTotal += basecost;
			            	
			            	priceSumTotalGroupTotal += (quantity * price * days);
			            	basePriceSumTotalGroupTotal += (quantity * baseprice * days);
			            	baseCostSumTotalGroupTotal += (quantity * basecost * days);
			            	
			                // Total general
			                priceTotal += price;
			                basePriceTotal += baseprice;
			                baseCostTotal += basecost;
			                
			                priceTotalA += (quantity * price * days);
			                basePriceTotalA += (quantity * baseprice * days);
			                baseCostTotalA += (quantity * basecost * days);
		
		        			// Contar los productos 
		        			double countItemQuantity = 0;
       	 					int countItemProd = 0;
		    	        	sqlQ = " SELECT SUM(ordi_quantity) AS countItemQuantity, COUNT(ordi_orderitemid) AS countItemProd  FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
		    	            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid = ordi_ordergroupid) " +
				        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = ordi_productid) " +
				        		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordg_orderid) " +
				        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = orde_currencyid) " +
		    	        			" WHERE ordi_orderitemid > 0 " +
		    	            		" AND ordg_orderid = " + pmOrder.getInt("orders", "orde_orderid") +
		    	            		" AND ordg_ordergroupid = " + pmOrderGroup.getInt("ordergroups", "ordg_ordergroupid") +
		    	            		whereProduct +
		        					whereExtra +
		    		   				whereProductFamily +
		    		   				whereProductGroup;
		    	        	
		    	        	//System.out.println("sql-Ext: " + sqlQ);
		    	        	
		    	        	pmCountOrdiProd.doFetch(sqlQ);
		    	        	if(pmCountOrdiProd.next()) {
		    	        		countItemQuantity = pmCountOrdiProd.getDouble("countItemQuantity");
		    	        		countItemProd = pmCountOrdiProd.getInt("countItemProd");
		    	        	}
		    	        	
			            	if (firstOrder) {
			            		firstOrder = false;
			            		%>
					            <tr class="">
					        		<td class="reportGroupCell" colspan="15">
				        				# <%= countOrder%> |
				        				<%= pmOrder.getString("orders", "orde_code") %>
				        				<%= HtmlUtil.stringToHtml(pmOrder.getString("orders", "orde_name")) %> |
							        	Cliente: <%= HtmlUtil.stringToHtml(fullName) %> | 
							        	Fechas: 
							        	<%= pmOrder.getString("orders", "orde_lockstart") %>
							        	-
							        	<%= pmOrder.getString("orders", "orde_lockend") %> |
					        			Tipo de Cambio: 
						                	<%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
						                	if (currencyIdOrigin != currencyIdDestiny) {
						                		if (bmoCurrency.getCode().toString().equals("USD")) {
						                			%>
						                			<%= defaultParity %> |
				                			<%	} else { %>
						                			<%= pmOrder.getDouble("orde_currencyparity")%> |
						                			<%		}
					                		} else { %>
						                		<%= pmOrder.getDouble("orde_currencyparity") %> |
					                	<%	}%>
						                
				                		<span title='Origen: <%= SFServerUtil.formatCurrency(pmOrder.getDouble("orde_total"))%> <%= pmOrder.getString("cure_code") %>'>
					                   		Total: <%=SFServerUtil.formatCurrency(pmCurrencyExchange.currencyExchange(pmOrder.getDouble("orde_total"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny)) %>
					                   		<%= bmoCurrency.getCode().toString()%>
				                   		</span>
				        			</td>
				                </tr>
				                <tr class="">
					                <td class="reportHeaderCell">Grupo</td>
					                <td class="reportHeaderCellCenter" width="1%">#</td>
					                <td class="reportHeaderCell">Clave</td>
					                <td class="reportHeaderCell">Producto / Item</td>
					                <td class="reportHeaderCell">Descripci&oacute;n</td>
					                <td class="reportHeaderCell">Modelo</td>
					                <td class="reportHeaderCell">Proveedor</td>
					                <td class="reportHeaderCellCenter">D&iacute;as</td>
					                <td class="reportHeaderCellCenter">Cantidad</td>
					                <td class="reportHeaderCellRight">Precio <br>en Pedido</td>
					                <td class="reportHeaderCellRight">Precio Total<br>en Pedido</td>
					                <td class="reportHeaderCellRight">Precio Base</td>
					                <td class="reportHeaderCellRight">Precio Total</td>
					                <td class="reportHeaderCellRight">Costo Base</td>
					                <td class="reportHeaderCellRight">Costo Total</td>
					            </tr>
		            	<% 	} %>
			        		<tr class="reportCellEven">
				        		<% 	if (firstGroup) {
						        		firstGroup = false;
					        		%>
							        	<td class="reportCellText" rowspan="<%= countItemGroup + 1 %>">
							        		<%= iGroup%>. <%= HtmlUtil.stringToHtml(pmProduct.getString("ordergroups", "ordg_name"))%>
							        	</td>
					        	<% 	} %>
					        	<%=HtmlUtil.formatReportCell(sFParams, iItem + "", BmFieldType.NUMBER) %>
					        	
					        	<% 	if (pmProduct.getInt("orderitems", "ordi_productid") > 0) { %>
							        	<%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_code"), BmFieldType.CODE) %>
							        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_name"), BmFieldType.STRING)) %>
							        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_description"), BmFieldType.STRING)) %>
							        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_model"), BmFieldType.STRING)) %>
							        	<% 	if (pmProduct.getInt("suppliers", "supl_supplierid") > 0) { %>
						        				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + (pmProduct.getString("suppliers", "supl_code") + " " + pmProduct.getString("suppliers", "supl_name")), BmFieldType.STRING)) %>
							        	<% 	} else { %>
							        			<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
							        	<% 	} %>
			
					        	<% 	} else { %>
						        		<td class="reportCellCode">Extra</td>
						        		<td class="reportCellText">
						        			<%= HtmlUtil.stringToHtml(pmProduct.getString("orderitems", "ordi_name"))%>
						        		</td>
						        		<td class="reportCellText" >
						        			<%= HtmlUtil.stringToHtml(pmProduct.getString("orderitems", "ordi_description"))%>
						        		</td>
							        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
							        	<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
					        	<% 	} %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getDouble("ordi_days"), BmFieldType.NUMBER) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getDouble("ordi_quantity"), BmFieldType.NUMBER) %>
	
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + price, BmFieldType.CURRENCY) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + (quantity * price * days), BmFieldType.CURRENCY) %>
					        	
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseprice, BmFieldType.CURRENCY) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + (quantity * baseprice * days), BmFieldType.CURRENCY) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + basecost, BmFieldType.CURRENCY) %>
					        	<%= HtmlUtil.formatReportCell(sFParams, "" + (quantity * basecost * days), BmFieldType.CURRENCY) %>
				        	</tr>
				        	<%
			            } //pmProduct
			            %>
			            <tr class="reportCellCode reportCellEven">
			        		<td class="reportCellText" colspan="8">&nbsp;</td>
			        		<%= HtmlUtil.formatReportCell(sFParams, "" + priceSumGroup, BmFieldType.CURRENCY) %>
				        	<%= HtmlUtil.formatReportCell(sFParams, "" + priceSumTotalGroup, BmFieldType.CURRENCY) %>
				        	
				        	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceSumGroup, BmFieldType.CURRENCY) %>
				        	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceSumTotalGroup, BmFieldType.CURRENCY) %>
				        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostSumGroup, BmFieldType.CURRENCY) %>
				        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostSumTotalGroup, BmFieldType.CURRENCY) %>
			        	</tr>
			        	<%  
	        		} // if Si tiene items en grupo
	            } // pmProfile
	            %>
		        <tr class="reportCellCode reportCellEven">
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + iGroup, BmFieldType.STRING) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + iItemsTotal, BmFieldType.NUMBER) %>
		        	
		    		<td class="reportCellText" colspan="6">&nbsp;</td>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + iquantityProductOrder, BmFieldType.NUMBER) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + priceSumGroupTotal, BmFieldType.CURRENCY) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + priceSumTotalGroupTotal, BmFieldType.CURRENCY) %>
		        	
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceSumGroupTotal, BmFieldType.CURRENCY) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceSumTotalGroupTotal, BmFieldType.CURRENCY) %>
		
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostSumGroupTotal, BmFieldType.CURRENCY) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostSumTotalGroupTotal, BmFieldType.CURRENCY) %>
		    	</tr>
		    	<%	
				countOrder++;
	    	}	// if si tiene item en todo el pedido
	    } 	// pmOrder
	        %>
	    <tr ><td colspan="15">&nbsp;</td></tr>
	    <tr class="reportCellCode reportCellEven">
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + iTotalGroup, BmFieldType.STRING) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + iTotalItems, BmFieldType.NUMBER) %>
	    	
			<td class="reportCellText" colspan="6">&nbsp;</td>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + iquantityProductOrderTotal, BmFieldType.NUMBER) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + priceTotal, BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + priceTotalA, BmFieldType.CURRENCY) %>
	    	
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceTotal, BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + basePriceTotalA, BmFieldType.CURRENCY) %>
	
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostTotal, BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + baseCostTotalA, BmFieldType.CURRENCY) %>
		</tr>
		<%	
	} %>
</table> 
<%
	}// Fin de if(no carga datos)
	pmCountOrdiProd.close();
	pmOrderGroup.close();
	pmProduct.close();
	pmOrder.close();
   	pmCurrencyWhile.close();
%>  
<% 	if (print.equals("1")) { %>
    	<script>
        	//window.print();
    	</script>
<% 	} %>
  </body>
</html>
