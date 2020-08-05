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
 
<%@page import="com.flexwm.shared.co.BmoProperty"%>
<%@page import="com.flexwm.shared.ar.BmoPropertyRental"%>
<%@page import="com.symgae.shared.SQLUtil"%>
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
<%@include file="/inc/login.jsp" %>

<%
    // Inicializar variables
    String title = "Reportes de Pedidos con Renovación Automática";
	BmoCustomer bmoCustomer = new BmoCustomer();
	BmoProperty bmoProperty = new BmoProperty();
	BmoOrder bmoOrder = new BmoOrder();
	BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
	BmoOrder bmoOrderRenew = new BmoOrder();
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
    String startDate  = "", endDate = "", rentIncrease = "",rentEndIncrease = "", rentalScheduleDate ="",rentalEndScheduleDate ="";
   	
    String prrtStatus = "", paymentStatus = "", deliveryStatus = "";
    String filters = "",whererentIncrease = "", whereOrder = "" ,fullName = "", fullNameLessor= "", whereExtra="", productFamilyId = "", productGroupId = "";
    
    
    int programId = 0, customerId = 0, customerLessorId = 0, cols= 0, prrtId = 0,areaId = 0, orderId = 0, industryId = 0, userId = 0, productId = 0, showProductExtra = 0, 
    		currencyId = 0 ,dynamicColspan = 0, dynamicColspanMinus = 0, columnBudgets = 0, prrtTypeId = 0, budgetId = 0, budgetItemId = 0;
   	double nowParity = 0, defaultParity = 0;
   	
	// Obtener parametros       
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("prrt_propertiesrentid") != null) prrtId = Integer.parseInt(request.getParameter("prrt_propertiesrentid"));    
    if (request.getParameter("prrt_customerid") != null) customerId = Integer.parseInt(request.getParameter("prrt_customerid"));    
    if (request.getParameter("prty_customerid") != null) customerLessorId = Integer.parseInt(request.getParameter("prty_customerid"));    
	if (request.getParameter("prrt_status") != null) prrtStatus = request.getParameter("prrt_status");
//     if (request.getParameter("orde_lockstatus") != null) lockStatus = request.getParameter("orde_lockstatus");
//     if (request.getParameter("orde_deliverystatus") != null) deliveryStatus = request.getParameter("orde_deliverystatus");
    if (request.getParameter("orde_paymentstatus") != null) paymentStatus = request.getParameter("orde_paymentstatus");
    if (request.getParameter("prrt_startdate") != null) startDate = request.getParameter("prrt_startdate");
    if (request.getParameter("prrt_enddate") != null) endDate = request.getParameter("prrt_enddate");
    if (request.getParameter("prrt_rentincrease") != null) rentIncrease = request.getParameter("prrt_rentincrease");
    if (request.getParameter("rentendincrease") != null) rentEndIncrease = request.getParameter("rentendincrease");
	if (request.getParameter("prrt_rentalscheduledate") != null) rentalScheduleDate = request.getParameter("prrt_rentalscheduledate");
	if (request.getParameter("rentalendscheduledate") != null) rentalEndScheduleDate = request.getParameter("rentalendscheduledate");
	if (request.getParameter("area_areaid") != null) areaId = Integer.parseInt(request.getParameter("area_areaid"));  
    if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid"));  
    if (request.getParameter("prrt_userid") != null) userId = Integer.parseInt(request.getParameter("prrt_userid"));   
	if (request.getParameter("prrt_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("prrt_currencyid"));
    if (request.getParameter("prrt_ordertypeid") != null) prrtTypeId = Integer.parseInt(request.getParameter("prrt_ordertypeid"));    
      
	bmoProgram = (BmoProgram)pmProgram.get(programId);

	// Filtros listados
	if (prrtTypeId > 0) {
    	where += " AND prrt_ordertypeid = " + prrtTypeId;
    	filters += "<i>Tipo Pedido: </i>" + request.getParameter("prrt_ordertypeidLabel") + ", ";
    }

	if (prrtId > 0) {
    	where += " AND prrt_propertiesrentid = " + prrtId;
    	filters += "<i>Contrato: </i>" + request.getParameter("prrt_propertiesrentidLabel") + ", ";
    }
    
	if (userId > 0) {
    	//if (sFParams.restrictData(bmoOrder.getProgramCode())) {
			where += " AND prrt_userid = " + userId;
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
    	filters += "<i>Vendedor: </i>" + request.getParameter("prrt_useridLabel") + ", ";
    }
    
	if (customerId > 0) {
    	where += " AND a.cust_customerid = " + customerId;
    	filters += "<i>Cliente: </i>" + request.getParameter("prrt_customeridLabel") + ", ";
    }
	
	 if (customerLessorId > 0) {
	    	where += " AND prty_customerid = " + customerLessorId;
	    	filters += "<i>Arrendador: </i>" + request.getParameter("prty_customeridLabel") + ", ";
	 }
    
	if (industryId > 0) {
    	where += " AND a.cust_industryid = " + industryId;
    	filters += "<i>Giro: </i>" + request.getParameter("cust_industryidLabel") + ", ";
    }
    
	if (!prrtStatus.equals("")) {
   		//where += " AND orde_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("prrt_status", prrtStatus);
   		filters += "<i>Estatus: </i>" + request.getParameter("prrt_statusLabel") + ", ";
   	}
    
//     if (!deliveryStatus.equals("")) {
//         //where += " and orde_deliverystatus like '" + deliveryStatus + "'";
//         where += SFServerUtil.parseFiltersToSql("orde_deliverystatus", deliveryStatus);
//         filters += "<i>Entrega: </i>" + request.getParameter("orde_deliverystatusLabel") + ", ";
//     }
    
//     if (!paymentStatus.equals("")) {
//         //where += " AND orde_paymentstatus like '" + paymentStatus + "'";
//         whereOrder += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
//         filters += "<i>Pago: </i>" + request.getParameter("orde_paymentstatusLabel") + ", ";
//     }
    
    if (!startDate.equals("")) {
        where += " AND prrt_startdate >= '" + startDate + "'";
        filters += "<i>F.Inicio: </i>" + startDate + ", ";
    }
    
    if (!endDate.equals("")) {
        where += " AND prrt_startdate <= '" + endDate + "'";
        filters += "<i>F.Fin: </i>" + endDate + ", ";
    }  
    
    if (!rentIncrease.equals("")) {
    	whererentIncrease += " AND prrt_rentincrease >= '" + rentIncrease + "'";
        filters += "<i>F.Incremento Renta: </i>" + rentIncrease+ ", ";
    }
    
    if (!rentalEndScheduleDate.equals("")) {
        where += " AND prrt_rentalscheduledate <= '" + rentalEndScheduleDate + "'";
        filters += "<i>F.1er. Renta Inicio Final: </i>" + rentalEndScheduleDate + ", ";
    }
    
    if (!rentalScheduleDate.equals("")) {
    	whererentIncrease += " AND prrt_rentalscheduledate >= '" + rentIncrease + "'";
        filters += "<i>1er. Renta: </i>" + rentalScheduleDate+ ", ";
    }
    
    if (!rentalScheduleDate.equals("")) {
        where += " AND prrt_rentalscheduledate >= '" + rentalScheduleDate + "'";
        filters += "<i>F.1er. Renta : </i>" + rentalScheduleDate + ", ";
    }
    
  	if (currencyId > 0) {
  		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);
  		defaultParity = bmoCurrency.getParity().toDouble();

  		filters += "<i>Moneda: </i>" + request.getParameter("prrt_currencyidLabel")
  				+ " | <i>Paridad Actual : </i>" + defaultParity;
  	} else {
  		filters += "<i>Moneda: </i> Todas ";
  		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM propertiesrent " +     
  		" LEFT JOIN orders ON(orde_orderid = prrt_orderid) " +
  		" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
		" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
		" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
		" LEFT JOIN users ON(user_userid = orde_userid) " +
		" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
  		
  		sqlCurrency +=" WHERE prrt_propertiesrentid > 0 " +
						" AND orde_reneworderid IS NULL " +
						where +
						whererentIncrease +
						" AND orde_willrenew = 1 ";
						// 		" AND orde_orderid IN ( " +
						// 				" SELECT ordg_orderid FROM orderitems " +
						// 				" LEFT JOIN products on (prod_productid = ordi_productid) " +
						// 				" LEFT JOIN ordergroups on (ordg_ordergroupid = ordi_ordergroupid) " +
						// 				" WHERE prod_reneworder = 1 " +
						// 				" ) ";
		
		sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";


  		
//   		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM orders " +     
//   				" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
//   				" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
//   				" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
//   				" LEFT JOIN users ON(user_userid = orde_userid) " +
//   				" LEFT JOIN areas ON(area_areaid = user_areaid) " +
//   				" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) " +
//   				" WHERE orde_orderid > 0 " +
//   				where;
// 		if (productId > 0 || showProductExtra  != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
// 			sqlCurrency += " AND orde_orderid IN ( " +
// 					" SELECT orde_orderid  FROM orders " +
// 					" LEFT JOIN ordergroups on (ordg_orderid = orde_orderid) " +
// 					" LEFT JOIN orderitems on (ordi_ordergroupid = ordg_ordergroupid) " +
// 					" LEFT JOIN products on (prod_productid = ordi_productid) " +
// 					" WHERE orde_orderid = orde_orderid " +
// 					whereProduct +
// 					whereExtra +
// 					whereProductFamily +
// 					whereProductGroup +
// 					" ) ";
// 		}
// 	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
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
	
	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();
	
	// colspan dinamico

//	if (sFParams.isFieldEnabled(bmoWFlow.getFunnel()))
//		dynamicColspan++;
    
	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity()))
		
	
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
            <b>Ordenador por:</b> ID Cliente
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report" border="0" cellspacing="0" cellpading="0" width="100%">
	<%
	if (!(currencyId > 0)) {
		int currencyIdWhile = 0, x = 1;					
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			if (pmCurrencyWhile.getInt("cure_currencyid") != currencyIdWhile) {
				currencyIdWhile = pmCurrencyWhile.getInt("cure_currencyid");
				currencyId = currencyIdWhile;
		    	defaultParity = pmCurrencyWhile.getDouble("cure_parity");
				%>
				<tr>
					<td class="reportHeaderCellCenter" colspan="<%= 26 + dynamicColspan%>">
						<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
					</td>
				</tr>
				<tr class="">
		
			<td class="reportHeaderCellCenter">#</td>
			<%	if (sFParams.isFieldEnabled(bmoOrder.getCode())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getCode()))%></td>
		<%	}%>
				<%	if (sFParams.isFieldEnabled(bmoOrder.getName())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getName()))%></td>
		<%	}%>
           <%	if (sFParams.isFieldEnabled(bmoCustomer.getDisplayName())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode(), bmoCustomer.getDisplayName()))%></td>
		<%	}%>
		    <%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell">Arrendador</td>
		<%	}%>

 <%	if (sFParams.isFieldEnabled(bmoOrder.getWillRenew())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode(), bmoOrder.getWillRenew()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoProperty.getRenewOrder())) {
			dynamicColspan++;
		  	%><td class="reportHeaderCell">Inm. c/Renov.?</td>
		  	<%
			}%>
			
         
         
            <td class="reportHeaderCell">Renovaci&oacute;n</td>
            <%
            dynamicColspan++;
            %>
               <%	if (sFParams.isFieldEnabled(bmoCustomer.getSalesmanId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode(), bmoCustomer.getSalesmanId()))%></td>
		<%	}%>
             <%	if (sFParams.isFieldEnabled(bmoOrder.getStatus())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getStatus()))%></td>
		<%	}%>
            <%	if (sFParams.isFieldEnabled(bmoOrder.getLockStart())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getLockStart()))%></td>
		<%	}%>
            <%	if (sFParams.isFieldEnabled(bmoOrder.getLockEnd())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getLockEnd()))%></td>
		<%	}%>
             <%	if (sFParams.isFieldEnabled(bmoOrderRenew.getPayments())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrderRenew.getPayments()))%></td>
		<%	}%>
            <%	if (sFParams.isFieldEnabled(bmoOrder.getAmount())) {
            	dynamicColspan++;dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getAmount()))%></td>
		<%	}else{//dynamicColspanMinus++;
		}%>
	           <%	
            if (sFParams.isFieldEnabled(bmoOrder.getDiscount())) {
            	dynamicColspan++;dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getDiscount()))%></td>
		<%	}else{ //dynamicColspanMinus++;
		}%>
				
	      <%	
            if (sFParams.isFieldEnabled(bmoOrder.getTax())) {
            	dynamicColspan++;dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getTax()))%></td>
		<%	}
            if (sFParams.isFieldEnabled(bmoOrder.getTotal())) {
            	dynamicColspan++;dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getTotal()))%></td>
		<%	}%>
        </tr>
				<%
				
				double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0;
		        
		        sql = " SELECT * FROM propertiesrent " +     
		        		" LEFT JOIN orders ON(orde_orderid = prrt_orderid) " +
						" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
						" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
						" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
						" LEFT JOIN users ON(user_userid = orde_userid) " +
						" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
		       
		        sql += " WHERE orde_orderid > 0 " +
						" AND orde_reneworderid IS NULL " +
					   	" AND orde_currencyid =  " + currencyId +
						where +
						whererentIncrease +
						" AND orde_willrenew = 1 ";
// 						" AND orde_orderid IN ( " +
// 								" SELECT ordg_orderid FROM orderitems " +
// 								" LEFT JOIN products on (prod_productid = ordi_productid) " +
// 								" LEFT JOIN ordergroups on (ordg_ordergroupid = ordi_ordergroupid) " +
// 								" WHERE prod_reneworder = 1 " +
// 								" ) ";
				
				sql += " ORDER by cust_customerid ASC, prrt_propertiesrentid ASC";
				
				pmOrder.doFetch(sql); 
		
		        double sumProdTotal = 0, sumEquiTotal = 0, sumStaffTotal = 0, sumOCTotal = 0, gastosTotal = 0;
		
		        int i = 1;
		        while (pmOrder.next()) {
		        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
		        	bmoPropertyRental.getStatus().setValue(pmOrder.getString("propertiesrent", "prrt_status"));
		        	
		        	double sumProd = 0, sumEqui = 0, sumStaff = 0, sumOC = 0, gastos = 0;
		        	
			    	double amount = pmOrder.getDouble("orde_amount");
			    	double discount = pmOrder.getDouble("orde_discount");
			    	double tax = pmOrder.getDouble("orde_tax");				          	
			    	double total = pmOrder.getDouble("orde_total");
			    	double totalSinIva = pmOrder.getDouble("orde_total") - tax;				          						           		
		
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
		
			    	// Suma general
			    	amountTotal += amount;
			    	discountTotal += discount;
			    	taxTotal += tax;
			    	totalTotal += total;
			    	totalSinIvaTotal += totalSinIva;
		
			    	if (pmOrder.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
			    		fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_legalname");	
		   			else
		   				fullName = pmOrder.getString("cust_code") + " " + pmOrder.getString("cust_displayname");
		        	
		        	//Estatus
		        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
		        	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
		        	bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
		        	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
		        	bmoPropertyRental.getStatus().setValue(pmOrder.getString("propertiesrent", "prrt_status"));
		    %>      
		    	<tr class="reportCellEven">
		        	<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("propertiesrent", "prrt_code"), BmFieldType.CODE) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("propertiesrent", "prrt_code"), BmFieldType.CODE) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("ordertypes", "ortp_name"), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("orders", "orde_name"), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
		        	<%
		        		String renew = "No";
		        		if (pmOrder.getInt("orde_willrenew") == 1)
		        			renew = "Si";
		        	%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + renew, BmFieldType.STRING)) %>
					<%
					// Si el Pedido tiene productos con Renovacion
					String orderHasProductRenew =  "";
					int countOrderHasProductRenew = 0;
		
					sql = " SELECT COUNT(*) AS countOrderHasProductRenew FROM orders " +  
							" LEFT JOIN orders ON(orde_orderid = prr_orderid) " +
							" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
							" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
							" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
							" LEFT JOIN users ON(user_userid = orde_userid) " +
							" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
			       sql += " WHERE orde_orderid > 0 " +
			        		" AND orde_currencyid =  " + currencyId +
							" AND orde_orderid = " + pmOrder.getInt("orde_orderid") +
							where +
							whererentIncrease +
			 				" AND orde_orderid IN ( " +
			 						" SELECT ordg_orderid FROM orderitems " +
			 						" LEFT JOIN products on (prod_productid = ordi_productid) " +
			 						" LEFT JOIN ordergroups on (ordg_ordergroupid = ordi_ordergroupid) " +
			 						" WHERE prod_reneworder = 1 " +
			 						" ) ";
					
					sql += " ORDER by cust_customerid ASC, orde_orderid ASC";
					pmConn.doFetch(sql);
					if (pmConn.next()) countOrderHasProductRenew = pmConn.getInt("countOrderHasProductRenew");
						
					if (countOrderHasProductRenew > 0) 
						orderHasProductRenew = "Si";
					else 
						orderHasProductRenew = "No";
		
					%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + orderHasProductRenew, BmFieldType.STRING)) %>
					
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING)) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("users", "user_code"), BmFieldType.STRING)) %>
<%-- 		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrder.getString("industries", "indu_name"), BmFieldType.STRING)) %> --%>
		        	
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>                 
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("propertiesrent", "prrt_startdate"), BmFieldType.DATETIME) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("propertiesrent", "prrt_enddate"), BmFieldType.DATETIME) %>
					<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("propertiesrent", "prrt_rentincrease"), BmFieldType.DATETIME) %>
		        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		        	<%= HtmlUtil.formatReportCell(sFParams, pmOrder.getString("currencies", "cure_code"), BmFieldType.CODE) %>
		        	<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
				    		<%= HtmlUtil.formatReportCell(sFParams, (pmOrder.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
				    <% 	}%>
				    <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
		        		if (currencyIdOrigin != currencyIdDestiny) {
			    	%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
				    <%		
				    	} else { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmOrder.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
				    <%	}%>
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + discount, BmFieldType.CURRENCY) %>
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
					
					<%
		        	sql = " SELECT * FROM propertiesrent " +   
									" LEFT JOIN orders  ON(orde_orderid = prrt_orderid) " +
									" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
									" LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
									" LEFT JOIN industries ON(indu_industryid = cust_industryid) " +
									" LEFT JOIN users ON(user_userid = orde_userid) " +
									" LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) ";
					
					sql += " WHERE orde_orderid > 0 " +
								where +
								" AND orde_originreneworderid = " + pmOrder.getInt("orde_orderid") +
								" ORDER by cust_customerid ASC, orde_orderid ASC";
									
							pmConnOrdeGroup.doFetch(sql);
							int y = 1;
							while (pmConnOrdeGroup.next()) {
					        	
						    	double amountOrderRenew = pmConnOrdeGroup.getDouble("orde_amount");
						    	double discountOrderRenew = pmConnOrdeGroup.getDouble("orde_discount");
						    	double taxOrderRenew = pmConnOrdeGroup.getDouble("orde_tax");				          	
						    	double totalOrderRenew = pmConnOrdeGroup.getDouble("orde_total");
						    	double totalSinIvaOrderRenew = pmConnOrdeGroup.getDouble("orde_total") - tax;				          						           		
					
						    	//Conversion a la moneda destino(del while)
						    	currencyIdOrigin = 0; currencyIdDestiny = 0;
						    	parityOrigin = 0; parityDestiny = 0;
						    	currencyIdOrigin = pmConnOrdeGroup.getInt("orde_currencyid");
						    	parityOrigin = pmConnOrdeGroup.getDouble("orde_currencyparity");
						    	currencyIdDestiny = currencyId;
						    	parityDestiny = defaultParity;
					
						    	amountOrderRenew = pmCurrencyExchange.currencyExchange(amountOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						    	discountOrderRenew = pmCurrencyExchange.currencyExchange(discountOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						    	taxOrderRenew = pmCurrencyExchange.currencyExchange(taxOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						    	totalOrderRenew = pmCurrencyExchange.currencyExchange(totalOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						    	totalSinIvaOrderRenew = pmCurrencyExchange.currencyExchange(totalSinIvaOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					
						    	// Suma general
						    	amountTotal += amountOrderRenew;
						    	discountTotal += discountOrderRenew;
						    	taxTotal += taxOrderRenew;
						    	totalTotal += totalOrderRenew;
						    	totalSinIvaTotal += totalSinIvaOrderRenew;
					
						    	if (pmConnOrdeGroup.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
						    		fullName = pmConnOrdeGroup.getString("cust_code") + " " + pmConnOrdeGroup.getString("cust_legalname");	
					   			else
					   				fullName = pmConnOrdeGroup.getString("cust_code") + " " + pmConnOrdeGroup.getString("cust_displayname");
					        	
					        	//Estatus
					        	bmoOrderRenew.getStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_status"));
					        	bmoOrderRenew.getDeliveryStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_deliverystatus"));              
					        	bmoOrderRenew.getLockStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_lockstatus"));
					        	bmoOrderRenew.getPaymentStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_paymentstatus"));
					
					        	
					    %>      
					    	<tr class="reportCellEven">
					    	<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
			    		<%	if (sFParams.isFieldEnabled(bmoOrder.getCode())) {
				
		%>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_code"), BmFieldType.CODE) %>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoOrder.getName())) {
				
		%>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_name"), BmFieldType.STRING)) %>
		<%	}%>
		   <%	if (sFParams.isFieldEnabled(bmoCustomer.getDisplayName())) {
				
		%>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
		<%	}%>
				    <%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) {
				
		%>
			       	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullNameLessor, BmFieldType.STRING)) %> 				        	
		<%	}%>
			        	<%
			        		renew = "No";
			        		if (pmOrder.getInt("orde_willrenew") == 1)
			        			renew = "Si";
			        	%>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + renew, BmFieldType.STRING)) %>
						<%
						// Si el Pedido tiene productos con Renovacion
						
						orderHasProductRenew =  "";
						countOrderHasProductRenew = 0;
			
						sql = " SELECT COUNT(*) AS countOrderHasProductRenew FROM propertiesrent " + 
								" LEFT JOIN orders ON(orde_orderid = prrt_orderid) " +
								" LEFT JOIN properties ON(prty_propertyid = prrt_propertyid) " +
								" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
								" LEFT JOIN customers as a ON(a.cust_customerid = orde_customerid) " +
								" LEFT JOIN customers as lessor ON(lessor.cust_customerid = prty_customerid) " +
								" LEFT JOIN industries ON(indu_industryid = a.cust_industryid) " +
								" LEFT JOIN users ON(user_userid = orde_userid) " +
								" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
				        
				        sql +=" WHERE prrt_propertiesrentid > 0 " +
				        		" AND orde_orderid = " + pmConnOrdeGroup.getInt("orde_orderid") +
				        		" AND prty_reneworder = 1"+
								where ;
								
								
				 				
						sql += " ORDER by a.cust_customerid ASC, prrt_propertiesrentid ASC";
						System.out.println
						("aqui es el segundpo count ---"+sql);
						pmConn.doFetch(sql);
						if (pmConn.next()) countOrderHasProductRenew = pmConn.getInt("countOrderHasProductRenew");
							
						if (countOrderHasProductRenew > 0) 
							orderHasProductRenew = "Si";
						else 
							orderHasProductRenew = "No";
			
						%>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + orderHasProductRenew, BmFieldType.STRING)) %>
			        	<%
			        	sql = " SELECT orde_code FROM orders WHERE orde_orderid = " + pmConnOrdeGroup.getInt("orde_reneworderid");
			        	String orderRenewCode = "";
			        	pmConn.doFetch(sql);
			        	if (pmConn.next()) orderRenewCode = pmConn.getString("orde_code");
			        	%>
			        			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, orderRenewCode, BmFieldType.CODE)) %>
			        	
			        	   <%	if (sFParams.isFieldEnabled(bmoCustomer.getSalesmanId())) {
				
		%>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("users", "user_code"), BmFieldType.STRING)) %>
		<%	}%>
			        	  <%	if (sFParams.isFieldEnabled(bmoOrder.getStatus())) {
				
		%>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>                 
		<%	}%><%	if (sFParams.isFieldEnabled(bmoOrder.getLockStart())) {

		%>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orderMain", "orde_lockstart"), BmFieldType.DATETIME) %>

		<%	}%>
            <%	if (sFParams.isFieldEnabled(bmoOrder.getLockEnd())) {
				
		%>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orderMain", "orde_lockend"), BmFieldType.DATETIME) %>

		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoOrder.getPayments())) {
			
		%>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>

		<%	}%>
			        	<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
					    		<%= HtmlUtil.formatReportCell(sFParams, (pmConnOrdeGroup.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
					    <% 	}%>
					    <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
			        	if (currencyIdOrigin != currencyIdDestiny) {
			        		if (bmoCurrency.getCode().toString().equals("USD")) {
				    	%>
<%-- 									<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %> --%>
					    <%		} else { %>
<%-- 									<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnOrdeGroup.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %> --%>
					    <%		}
					    	} else { %>
<%-- 								<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnOrdeGroup.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %> --%>
					    <%	}%>
					     <%	if (sFParams.isFieldEnabled(bmoOrder.getAmount())) {
		%>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + amountOrderRenew, BmFieldType.CURRENCY) %>
		<%}	%>
	           <%	
            if (sFParams.isFieldEnabled(bmoOrder.getDiscount())) {            	
		%>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + discountOrderRenew, BmFieldType.CURRENCY) %>
		<%	}%>				
	      <%	
            if (sFParams.isFieldEnabled(bmoOrder.getTax())) {            	
		%>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + taxOrderRenew, BmFieldType.CURRENCY) %>
		<%	}
		%>
             <%	
            if (sFParams.isFieldEnabled(bmoOrder.getTotal())) {            	
		%>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + totalOrderRenew, BmFieldType.CURRENCY) %>
		<%	}
		%>
					
					         </tr>
					         <% 
					         y++;
							} // Fin PmOrderGroup
		         i++;
		        } //pmOrder
		        %> 
		<tr class="reportCellEven"><td colspan="<%= 1 %>">&nbsp;</td></tr>
        <tr class="reportCellEven reportCellCode">
	    <td colspan="<%= (1+(dynamicColspan)) -dynamicColspanMinus %>">&nbsp;</td >
			        <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
			        <%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
			        <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
			     	<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
		    	</tr>
				<%
			}
			%>
			
	    	<tr><td colspan="<%= 25 + dynamicColspan%>">&nbsp;</td></tr>

			<%
		} // Fin pmCurrencyWhile
		%>
		<%
	 } 	// Fin de no existe moneda
		// Existe moneda destino
	else {

	  //aqui estaban las columnas 
		
		double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0;
        
        sql = " SELECT * FROM propertiesrent " + 
        		" LEFT JOIN orders ON(orde_orderid = prrt_orderid) " +
				" LEFT JOIN properties ON(prty_propertyid = prrt_propertyid) " +
        		" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
				" LEFT JOIN customers as a ON(a.cust_customerid = orde_customerid) " +
				" LEFT JOIN customers as lessor ON(lessor.cust_customerid = prty_customerid) " +
				" LEFT JOIN industries ON(indu_industryid = a.cust_industryid) " +
				" LEFT JOIN users ON(user_userid = orde_userid) " +
				" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
       
        sql +=" WHERE prrt_propertiesrentid > 0 " +
				" AND orde_reneworderid IS NULL " +
				where +
				whererentIncrease +
			" AND orde_willrenew = 1 ";
		pmOrder.doFetch(sql); 
		//double amountSum = 0, discountSum = 0, taxSum = 0, totalSum = 0, subtotalProductTotalSum = 0, orderTotalSinIvaSum = 0;

        double sumProdTotal = 0, sumEquiTotal = 0, sumStaffTotal = 0, sumOCTotal = 0, gastosTotal = 0;

        
        while (pmOrder.next()) {
        	dynamicColspan =0;
        	dynamicColspanMinus=0; 
        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
        	bmoPropertyRental.getStartDate().setValue(pmOrder.getString("propertiesrent", "prrt_status"));
        	
        	double sumProd = 0, sumEqui = 0, sumStaff = 0, sumOC = 0, gastos = 0;
        	
	    	double amount = pmOrder.getDouble("orde_amount");
	    	double discount = pmOrder.getDouble("orde_discount");
	    	double tax = pmOrder.getDouble("orde_tax");				          	
	    	double total = pmOrder.getDouble("orde_total");
	    	double totalSinIva = pmOrder.getDouble("orde_total") - tax;			
	    	double amountSum = 0,discountSum= 0,taxSum = 0,totalSum = 0;

	    	//Conversion a la moneda destino(seleccion del filtro)
	    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	    	double parityOrigin = 0, parityDestiny = 0;
	    	currencyIdOrigin = pmOrder.getInt("orde_currencyid");
	    	parityOrigin = pmOrder.getDouble("orde_currencyparity");
	    	currencyIdDestiny = currencyId;
	    	parityDestiny = defaultParity;

	    	if (pmOrder.getString("a.cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	    		if(pmOrder.getString("a.cust_legalname").equals("")) 
	    			fullName = pmOrder.getString("a.cust_code") + " " + pmOrder.getString("a.cust_displayname");
	    		else 
	    			fullName = pmOrder.getString("a.cust_code") + " " + pmOrder.getString("a.cust_legalname");	
   			else
   				fullName = pmOrder.getString("a.cust_code") + " " + pmOrder.getString("a.cust_displayname");
	    	
	    	if (pmOrder.getString("lessor.cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	    		if(pmOrder.getString("lessor.cust_legalname").equals("")) 
	    			fullNameLessor = pmOrder.getString("lessor.cust_code") + " " + pmOrder.getString("lessor.cust_displayname");
	    		else
	    		fullNameLessor = pmOrder.getString("lessor.cust_code") + " " + pmOrder.getString("lessor.cust_legalname");	
   			else
   				fullNameLessor = pmOrder.getString("lessor.cust_code") + " " + pmOrder.getString("lessor.cust_displayname");
        	
        	//Estatus
        	bmoPropertyRental.getStatus().setValue(pmOrder.getString("propertiesrent", "prrt_status"));
        	bmoOrder.getStatus().setValue(pmOrder.getString("orders", "orde_status"));
        	bmoOrder.getDeliveryStatus().setValue(pmOrder.getString("orders", "orde_deliverystatus"));              
        	bmoOrder.getLockStatus().setValue(pmOrder.getString("orders", "orde_lockstatus"));
        	bmoOrder.getPaymentStatus().setValue(pmOrder.getString("orders", "orde_paymentstatus"));
			
        	
    %>      
   						<tr class="reportCellEven">
			</tr>
    	<tr >
	
    	<td class="reportGroupCell" colspan="19">
			  <%=  pmOrder.getString("propertiesrent", "prrt_code")%>|
        	  <%=  pmOrder.getString("propertiesrent", "prrt_name") %>  |
        	 Fecha Inicio  :<%= pmOrder.getString("propertiesrent", "prrt_startdate") %>   |
        	 Fecha Fin  :<%=  pmOrder.getString("propertiesrent", "prrt_enddate") %>
        	 </td>    
			</tr>   	 

		<tr class="">
		
			<td class="reportHeaderCellCenter">#</td>
			<%	if (sFParams.isFieldEnabled(bmoOrder.getCode())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getCode()))%></td>
		<%	}%>
				<%	if (sFParams.isFieldEnabled(bmoOrder.getName())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getName()))%></td>
		<%	}%>
           <%	if (sFParams.isFieldEnabled(bmoCustomer.getDisplayName())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode(), bmoCustomer.getDisplayName()))%></td>
		<%	}%>
		    <%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell">Arrendador</td>
		<%	}%>

 <%	if (sFParams.isFieldEnabled(bmoOrder.getWillRenew())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode(), bmoOrder.getWillRenew()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoProperty.getRenewOrder())) {
			dynamicColspan++;
		  	%><td class="reportHeaderCell">Inm. c/Renov.?</td>
		  	<%
			}%>
			
         
         
            <td class="reportHeaderCell">Renovaci&oacute;n</td>
            <%
            dynamicColspan++;
            %>
               <%	if (sFParams.isFieldEnabled(bmoCustomer.getSalesmanId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode(), bmoCustomer.getSalesmanId()))%></td>
		<%	}%>
             <%	if (sFParams.isFieldEnabled(bmoOrder.getStatus())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getStatus()))%></td>
		<%	}%>
            <%	if (sFParams.isFieldEnabled(bmoOrder.getLockStart())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getLockStart()))%></td>
		<%	}%>
            <%	if (sFParams.isFieldEnabled(bmoOrder.getLockEnd())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getLockEnd()))%></td>
		<%	}%>
             <%	if (sFParams.isFieldEnabled(bmoOrderRenew.getPayments())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrderRenew.getPayments()))%></td>
		<%	}%>
            <%	if (sFParams.isFieldEnabled(bmoOrder.getAmount())) {
            	dynamicColspan++;dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getAmount()))%></td>
		<%	}else{//dynamicColspanMinus++;
		}%>
	           <%	
            if (sFParams.isFieldEnabled(bmoOrder.getDiscount())) {
            	dynamicColspan++;dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getDiscount()))%></td>
		<%	}else{ //dynamicColspanMinus++;
		}%>
				
	      <%	
            if (sFParams.isFieldEnabled(bmoOrder.getTax())) {
            	dynamicColspan++;dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getTax()))%></td>
		<%	}
            if (sFParams.isFieldEnabled(bmoOrder.getTotal())) {
            	dynamicColspan++;dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOrder.getProgramCode(), bmoOrder.getTotal()))%></td>
		<%	}%>
        </tr>
        	       	
        	<%
        		String renew = "No";
        		if (pmOrder.getInt("orde_willrenew") == 1)
        			renew = "Si";
        	%>
			
			<%
			
			// Si el Pedido tiene productos con Renovacion
			String orderHasProductRenew =  "";
			int countOrderHasProductRenew = 0;

			sql = " SELECT COUNT(*) AS countOrderHasProductRenew FROM  orders " + 
					" LEFT JOIN propertiesrent ON(orde_orderid = prrt_orderid) " +
					" LEFT JOIN properties ON(prty_propertyid = prty_propertyid) " +
					" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
					" LEFT JOIN customers as a ON(a.cust_customerid = orde_customerid) " +
					" LEFT JOIN customers as lessor ON(lessor.cust_customerid = prty_customerid) " +
					" LEFT JOIN industries ON(indu_industryid = a.cust_industryid) " +
					" LEFT JOIN users ON(user_userid = orde_userid) " +
					" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";	      
	        sql += " WHERE prrt_propertiesrentid > 0 " +
					" AND orde_orderid = " + pmOrder.getInt("orde_orderid") +
					" AND prty_reneworder = 1" +
					where +
					whererentIncrease ;
			sql += " ORDER by a.cust_customerid ASC, orde_orderid ASC";
			pmConn.doFetch(sql);
			if (pmConn.next()) countOrderHasProductRenew = pmConn.getInt("countOrderHasProductRenew");
			

        	sql = " SELECT * FROM  orders " +   
							" LEFT JOIN propertiesrent ON(orde_originreneworderid = prrt_propertiesrentid) " +
							" LEFT JOIN properties ON(prty_propertyid = prrt_propertyid) " +
							" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
							" LEFT JOIN customers as a ON(a.cust_customerid = orde_customerid) " +
							" LEFT JOIN  customers as lessor ON(lessor.cust_customerid = prty_customerid) " +
							" LEFT JOIN industries ON(indu_industryid = a.cust_industryid) " +
							" LEFT JOIN users ON(user_userid = orde_userid) " +
							" LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) ";
			sql += " WHERE orde_orderid > 0 " +
						//whereOrder +
						" AND orde_originreneworderid = " + pmOrder.getInt("orde_orderid") +
						" ORDER by a.cust_customerid ASC, orde_orderid ASC";
							System.out.println("------"+sql);
					pmConnOrdeGroup.doFetch(sql);
					int y = 1;
					int i = 1;
					while (pmConnOrdeGroup.next()) {
			        	
				    	double amountOrderRenew = pmConnOrdeGroup.getDouble("orde_amount");
				    	double discountOrderRenew = pmConnOrdeGroup.getDouble("orde_discount");
				    	double taxOrderRenew = pmConnOrdeGroup.getDouble("orde_tax");				          	
				    	double totalOrderRenew = pmConnOrdeGroup.getDouble("orde_total");
				    	double totalSinIvaOrderRenew = pmConnOrdeGroup.getDouble("orde_total") - tax;		
				    	
				    	amountSum += amountOrderRenew;
						discountSum += discountOrderRenew;
						taxSum += taxOrderRenew;
						totalSum += totalOrderRenew;
			
				    	//Conversion a la moneda destino(seleccion del filtro)
				    	currencyIdOrigin = 0; currencyIdDestiny = 0;
				    	parityOrigin = 0; parityDestiny = 0;
				    	currencyIdOrigin = pmConnOrdeGroup.getInt("orde_currencyid");
				    	parityOrigin = pmConnOrdeGroup.getDouble("orde_currencyparity");
				    	currencyIdDestiny = currencyId;
				    	parityDestiny = defaultParity;
			
				    	amountOrderRenew = pmCurrencyExchange.currencyExchange(amountOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	discountOrderRenew = pmCurrencyExchange.currencyExchange(discountOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	taxOrderRenew = pmCurrencyExchange.currencyExchange(taxOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	totalOrderRenew = pmCurrencyExchange.currencyExchange(totalOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				    	totalSinIvaOrderRenew = pmCurrencyExchange.currencyExchange(totalSinIvaOrderRenew, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			
				    	// Suma general
				    	amountTotal += amountOrderRenew;
				    	discountTotal += discountOrderRenew;
				    	taxTotal += taxOrderRenew;
				    	totalTotal += totalOrderRenew;
				    	totalSinIvaTotal += totalSinIvaOrderRenew;
			
				    	if (pmConnOrdeGroup.getString("a.cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
				    		if(pmConnOrdeGroup.getString("a.cust_legalname").equals("")) 
					    		fullName = pmConnOrdeGroup.getString("a.cust_code") + " " + pmConnOrdeGroup.getString("a.cust_displayname");	
				    		else
				    			fullName = pmConnOrdeGroup.getString("a.cust_code") + " " + pmConnOrdeGroup.getString("a.cust_legalname");	
				    	else
			   				fullName = pmConnOrdeGroup.getString("cust_code") + " " + pmConnOrdeGroup.getString("a.cust_displayname");
				    	
				    	if (pmConnOrdeGroup.getString("lessor.cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
				    		if(pmConnOrdeGroup.getString("lessor.cust_legalname").equals("")) 
				    			fullNameLessor = pmConnOrdeGroup.getString("lessor.cust_code") + " " + pmConnOrdeGroup.getString("lessor.cust_displayname");	
				    		else
				    			fullNameLessor = pmConnOrdeGroup.getString("lessor.cust_code") + " " + pmConnOrdeGroup.getString("lessor.cust_legalname");	
			   			else
			   				fullNameLessor = pmConnOrdeGroup.getString("lessor.cust_code") + " " + pmConnOrdeGroup.getString("lessor.cust_displayname");
			        	
				    	
			        	//Estatus
			        	bmoOrderRenew.getStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_status"));
			        	bmoOrderRenew.getDeliveryStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_deliverystatus"));              
			        	bmoOrderRenew.getLockStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_lockstatus"));
			        	bmoOrderRenew.getPaymentStatus().setValue(pmConnOrdeGroup.getString("orders", "orde_paymentstatus"));	
			    %>      
			   
			    	<tr class="reportCellEven">
			    		<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
			    		<%	if (sFParams.isFieldEnabled(bmoOrder.getCode())) {
				
		%>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_code"), BmFieldType.CODE) %>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoOrder.getName())) {
				
		%>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orders", "orde_name"), BmFieldType.STRING)) %>
		<%	}%>
		   <%	if (sFParams.isFieldEnabled(bmoCustomer.getDisplayName())) {
				
		%>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
		<%	}%>
				    <%	if (sFParams.isFieldEnabled(bmoCustomer.getCustomercategory())) {
				
		%>
			       	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullNameLessor, BmFieldType.STRING)) %> 				        	
		<%	}%>
			        	<%
			        		renew = "No";
			        		if (pmOrder.getInt("orde_willrenew") == 1)
			        			renew = "Si";
			        	%>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + renew, BmFieldType.STRING)) %>
						<%
						// Si el Pedido tiene productos con Renovacion
						
						orderHasProductRenew =  "";
						countOrderHasProductRenew = 0;
			
						sql = " SELECT COUNT(*) AS countOrderHasProductRenew FROM propertiesrent " + 
								" LEFT JOIN orders ON(orde_orderid = prrt_orderid) " +
								" LEFT JOIN properties ON(prty_propertyid = prrt_propertyid) " +
								" LEFT JOIN ordertypes ON(ortp_ordertypeid = orde_ordertypeid) " +
								" LEFT JOIN customers as a ON(a.cust_customerid = orde_customerid) " +
								" LEFT JOIN customers as lessor ON(lessor.cust_customerid = prty_customerid) " +
								" LEFT JOIN industries ON(indu_industryid = a.cust_industryid) " +
								" LEFT JOIN users ON(user_userid = orde_userid) " +
								" LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) ";
				        
				        sql +=" WHERE prrt_propertiesrentid > 0 " +
				        		" AND orde_orderid = " + pmConnOrdeGroup.getInt("orde_orderid") +
				        		" AND prty_reneworder = 1"+
								where ;
								
								
				 				
						sql += " ORDER by a.cust_customerid ASC, prrt_propertiesrentid ASC";

						pmConn.doFetch(sql);
						if (pmConn.next()) countOrderHasProductRenew = pmConn.getInt("countOrderHasProductRenew");
							
						if (countOrderHasProductRenew > 0) 
							orderHasProductRenew = "Si";
						else 
							orderHasProductRenew = "No";
			
						%>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + orderHasProductRenew, BmFieldType.STRING)) %>
			        	<%
			        	sql = " SELECT orde_code FROM orders WHERE orde_orderid = " + pmConnOrdeGroup.getInt("orde_reneworderid");
			        	String orderRenewCode = "";
			        	pmConn.doFetch(sql);
			        	if (pmConn.next()) orderRenewCode = pmConn.getString("orde_code");
			        	%>
			        			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, orderRenewCode, BmFieldType.CODE)) %>
			        	
			        	   <%	if (sFParams.isFieldEnabled(bmoCustomer.getSalesmanId())) {
				
		%>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("users", "user_code"), BmFieldType.STRING)) %>
		<%	}%>
			        	  <%	if (sFParams.isFieldEnabled(bmoOrder.getStatus())) {
				
		%>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>                 
		<%	}%><%	if (sFParams.isFieldEnabled(bmoOrder.getLockStart())) {

		%>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orderMain", "orde_lockstart"), BmFieldType.DATETIME) %>

		<%	}%>
            <%	if (sFParams.isFieldEnabled(bmoOrder.getLockEnd())) {
				
		%>
			        	<%= HtmlUtil.formatReportCell(sFParams, pmConnOrdeGroup.getString("orderMain", "orde_lockend"), BmFieldType.DATETIME) %>

		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoOrder.getPayments())) {
			
		%>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrderRenew.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>

		<%	}%>
			        	<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
					    		<%= HtmlUtil.formatReportCell(sFParams, (pmConnOrdeGroup.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
					    <% 	}%>
					    <%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
			        	if (currencyIdOrigin != currencyIdDestiny) {
			        		if (bmoCurrency.getCode().toString().equals("USD")) {
				    	%>
<%-- 									<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %> --%>
					    <%		} else { %>
<%-- 									<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnOrdeGroup.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %> --%>
					    <%		}
					    	} else { %>
<%-- 								<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnOrdeGroup.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %> --%>
					    <%	}%>
					     <%	if (sFParams.isFieldEnabled(bmoOrder.getAmount())) {
		%>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + amountOrderRenew, BmFieldType.CURRENCY) %>
		<%}	%>
	           <%	
            if (sFParams.isFieldEnabled(bmoOrder.getDiscount())) {            	
		%>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + discountOrderRenew, BmFieldType.CURRENCY) %>
		<%	}%>				
	      <%	
            if (sFParams.isFieldEnabled(bmoOrder.getTax())) {            	
		%>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + taxOrderRenew, BmFieldType.CURRENCY) %>
		<%	}
		%>
             <%	
            if (sFParams.isFieldEnabled(bmoOrder.getTotal())) {            	
		%>
				    	<%= HtmlUtil.formatReportCell(sFParams, "" + totalOrderRenew, BmFieldType.CURRENCY) %>
		<%	}
		%>	
			         </tr>
			         <% 
			         y++;
			         i++;
					} // Fin PmOrderGroup%>
		<tr class="reportCellEven reportCellCode">
			<td  class="reportCellEven" colspan="<%= (1+(dynamicColspan)) -dynamicColspanMinus %>">&nbsp;</td >
			
			<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + discountSum, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + taxSum, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalSum, BmFieldType.CURRENCY) %>
		</tr>
         
      <%  } //pmOrder
  
        %> 

        <tr class="reportCellEven"><td colspan="<%=dynamicColspan+1%>">&nbsp;</td></tr>
        
        <tr class="reportCellEven reportCellCode">
	        	<td colspan="<%= (1+(dynamicColspan)) -dynamicColspanMinus %>">&nbsp;</td >
	        	          <%	if (sFParams.isFieldEnabled(bmoOrder.getAmount())) {%>
	        		        	<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
	        		<%}	%>
	        		 <%	if (sFParams.isFieldEnabled(bmoOrder.getDiscount())) {%>
	        		 	<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
	        		 <%}	%>
	         <%	if (sFParams.isFieldEnabled(bmoOrder.getTax())) {%>
	         <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
	          <%}	%>
	        	  <%	if (sFParams.isFieldEnabled(bmoOrder.getTotal())) {%>
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
	        	<%}	%>
    	</tr>
<%			
	} // Fin Existe moneda destino
%>		
</table>		
<%		
	
%>  
<% 	if (print.equals("1")) { %>
    	<script>
        	//window.print();
    	</script>
<% 	}

}// Fin de if(no carga datos)
	
	pmCurrencyWhile.close();
	pmConn.close();
	pmConnOrdeGroup.close();
   	pmOrder.close();%>
  </body>
</html>
