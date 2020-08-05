<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Paulina Padilla
 * @version 2018-10
 */ -->
 
<%@page import="com.flexwm.server.ar.PmPropertyRental"%>
<%@page import="com.flexwm.shared.ar.BmoPropertyRental"%>
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
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes General de Contratos";
	BmoOrder bmoOrder = new BmoOrder();
	BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
	
	BmoWFlow bmoWFlow = new BmoWFlow();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	//Tipo de Pedido
	BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	
	PmPropertyRental pmPropertyRental = new PmPropertyRental(sFParams);
	bmoOrderType = (BmoOrderType)pmOrderType.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getDefaultOrderTypeId().toInteger());

    String sql = "", where = "", sqlCurrency = "";
    String startDate  = "", endDate = "", rentIncrease = "",rentEndIncrease = "", rentalScheduleDate ="",rentalEndScheduleDate ="";
   	String prrtStatus = "", paymentStatus = "", deliveryStatus = "";
    String filters = "", fullName = "",fullNameLessor = "", whereExtra="", productFamilyId = "", productGroupId = "";
    int programId = 0, customerId = 0,customerLessorId = 0, cols= 0, prrtId = 0, industryId = 0, userId = 0, productId = 0, showProductExtra = 0, 
    		currencyId = 0 ,dynamicColspan = 0, dynamicColspanMinus = 0, columnBudgets = 0, prrtTypeId = 0, areaId = -1;
   	double nowParity = 0, defaultParity = 0;
   	
    // Obtener parametros       
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("prrt_propertiesrentid") != null) prrtId = Integer.parseInt(request.getParameter("prrt_propertiesrentid"));    
    if (request.getParameter("prrt_customerid") != null) customerId = Integer.parseInt(request.getParameter("prrt_customerid"));    
    if (request.getParameter("prty_customerid") != null) customerLessorId = Integer.parseInt(request.getParameter("prty_customerid"));    
    if (request.getParameter("prrt_status") != null) prrtStatus = request.getParameter("prrt_status");
    if (request.getParameter("orde_paymentstatus") != null) paymentStatus = request.getParameter("orde_paymentstatus");
    if (request.getParameter("prrt_startdate") != null) startDate = request.getParameter("prrt_startdate");
    if (request.getParameter("prrt_enddate") != null) endDate = request.getParameter("prrt_enddate");
    if (request.getParameter("prrt_rentincrease") != null) rentIncrease = request.getParameter("prrt_rentincrease");
    if (request.getParameter("rentendincrease") != null) rentEndIncrease = request.getParameter("rentendincrease");
	if (request.getParameter("prrt_rentalscheduledate") != null) rentalScheduleDate = request.getParameter("prrt_rentalscheduledate");
    if (request.getParameter("rentalendscheduledate") != null) rentalEndScheduleDate = request.getParameter("rentalendscheduledate");
	if (request.getParameter("area_areaid") != null) areaId = Integer.parseInt(request.getParameter("area_areaid"));  
    if (request.getParameter("prrt_userid") != null) userId = Integer.parseInt(request.getParameter("prrt_userid"));   
   	if (request.getParameter("prrt_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("prrt_currencyid"));
    if (request.getParameter("prrt_ordertypeid") != null) prrtTypeId = Integer.parseInt(request.getParameter("prrt_ordertypeid"));    
 
	bmoProgram = (BmoProgram)pmProgram.get(programId);

    // Filtros listados  
    if (prrtId > 0) {
    	where += " AND prrt_propertiesrentid = " + prrtId;
    	filters += "<i>"+ HtmlUtil.stringToHtml("Contrato: ") + "</i>" + request.getParameter("prrt_propertiesrentidLabel") + ", ";
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
    	filters += "<i>"+ HtmlUtil.stringToHtml("Vendedor: ") + "</i>" + request.getParameter("prrt_useridLabel") + ", ";
    }
    
    if (customerId > 0) {
    	where += " AND a.cust_customerid = " + customerId;
    	filters += "<i>"+ HtmlUtil.stringToHtml("Cliente: ") + "</i>" + request.getParameter("prrt_customeridLabel") + ", ";
    }
    
    if (customerLessorId > 0) {
    	where += " AND prty_customerid = " + customerLessorId;
    	filters += "<i>"+ HtmlUtil.stringToHtml("Arrendador: ") + "</i>" + request.getParameter("prty_customeridLabel") + ", ";
    }
    
    if (!prrtStatus.equals("")) {
        where += SFServerUtil.parseFiltersToSql("prrt_status", prrtStatus);
   		filters += "<i>"+ HtmlUtil.stringToHtml("Estatus: ") + "</i>" + request.getParameter("prrt_statusLabel") + ", ";
   	}
    
    
    if (!paymentStatus.equals("")) {
        where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
        filters += "<i>"+ HtmlUtil.stringToHtml("Pago: ") + "</i>" + request.getParameter("orde_paymentstatusLabel") + ", ";
    }
    
    if (!startDate.equals("")) {
        where += " AND prrt_startdate >= '" + startDate + "'";
        filters += "<i>"+ HtmlUtil.stringToHtml("Inicio: ") + "</i>" + startDate+ ", ";
    }
    
    if (!endDate.equals("")) {
        where += " AND prrt_startdate <= '" + endDate + "'";
        filters += "<i>"+ HtmlUtil.stringToHtml("F. Fin: ") + "</i>" + endDate + ", ";
    }
    
    if (!rentIncrease.equals("")) {
        where += " AND prrt_rentincrease >= '" + rentIncrease + "'";
        filters += "<i>"+ HtmlUtil.stringToHtml("Incremento Renta: ") + "</i>" + rentIncrease + ", ";
    }
    
    if (!rentEndIncrease.equals("")) {
        where += " AND prrt_rentincrease <= '" + rentEndIncrease + "'";
        filters += "<i>"+ HtmlUtil.stringToHtml("Incremento Renta Final: ") + "</i>" + rentEndIncrease + ", ";
    }
    
    if (!rentalScheduleDate.equals("")) {
        where += " AND prrt_rentalscheduledate >= '" + rentalScheduleDate + "'";
        filters += "<i>"+ HtmlUtil.stringToHtml("F.1er. Renta : ") + "</i>" + rentalScheduleDate + ", ";
    }
    
    if (!rentalEndScheduleDate.equals("")) {
        where += " AND prrt_rentalscheduledate <= '" + rentalEndScheduleDate + "'";
        filters += "<i>"+ HtmlUtil.stringToHtml("F.1er Renta Inicial Final: ") + "</i>" + rentalEndScheduleDate + ", ";
    }
   	   	
  	if (currencyId > 0) {
  		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);
  		defaultParity = bmoCurrency.getParity().toDouble();

  		filters += "<i>"+ HtmlUtil.stringToHtml("Monerda: ") + "</i>" + request.getParameter("prrt_currencyidLabel")
  				+ " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
  	} else {

  		filters += "<i>"+ HtmlUtil.stringToHtml("Moneda: ") + "</i> Todas ";
  		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, "propertiesrent") +     
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders")+" ON(orde_orderid = prrt_orderid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid = orde_customerid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON(user_userid = orde_userid) " +
  				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies")+" ON (orde_currencyid = cure_currencyid) ";
  				sqlCurrency += " WHERE orde_orderid > 0 " +
  				where;
	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
  	}
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>"+ HtmlUtil.stringToHtml(" Empresa: ") + "</i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmPropertyRental(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    PmConn pmPropertyRentalConsult = new PmConn(sFParams);
    pmPropertyRentalConsult.open();
    
    PmConn pmConnOrdeGroup = new PmConn(sFParams);
   	pmConnOrdeGroup.open();
   
    PmConn pmConn = new PmConn(sFParams);
    pmConn.open();
       
	
	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();
	
//	if (sFParams.isFieldEnabled(bmoWFlow.getFunnel()))
//		dynamicColspan++;
   
	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity()))
		dynamicColspan++;
	
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
	
			int currencyIdWhile = 0, x = 1, y = 0;					
			pmCurrencyWhile.doFetch(sqlCurrency);
			while (pmCurrencyWhile.next()) {
				if (pmCurrencyWhile.getInt("cure_currencyid") != currencyIdWhile) {
					currencyIdWhile = pmCurrencyWhile.getInt("cure_currencyid");
					currencyId = currencyIdWhile;
			    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
					y = 0;
					%>
					<tr>
						<td class="reportHeaderCellCenter" colspan="<%= 22 + columnBudgets + dynamicColspan%>">
							<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
						</td>
					</tr>
					<%
				}
				%>
			<tr class="">                                                                                                       
	        	<td class="reportHeaderCellCenter">#</td>
	        	<% dynamicColspan++; %>
		        	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCode())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getCode()))%></td>
			<%	}%>
		               	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getName())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getName()))%></td>
			<%	}%>
		              	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getPropertyId())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getPropertyId()))%></td>
			<%	}%>
		               	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCustomerId())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getCustomerId()))%></td>
			<%	}%>
		                      <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getOwnerProperty())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getOwnerProperty()))%></td>
			<%	}%>
			
		              <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getUserId())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getUserId()))%></td>
			<%	}%>
			
		             <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getStatus())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getStatus()))%></td>
			<%	}%>
			
			 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getStartDate())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getStartDate()))%></td>
			<%	}%>
			
			 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getEndDate())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getEndDate()))%></td>
			<%	}%>
		            
		           	 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getRentalScheduleDate())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getRentalScheduleDate()))%></td>
			<%	}%>
			
	 	 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getRentIncrease())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getRentIncrease()))%></td>
			<%	}%>	   
			
				 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getInitialIconme())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getInitialIconme()))%></td>
			<%	}%>	
			     
		           	 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCurrentIncome())) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getCurrentIncome()))%></td>
			<%	}%>	
		               	 <%	if (!bmoOrder.getPaymentStatus().equals("")) {
					dynamicColspan++;
			%>
					<td class="reportHeaderCellCenter">Pago</td>
			<%	}%>	                                               
	        <%
		        
				double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0,
						sumRaccTotal = 0, sumRaccLinkedTotal = 0;;
		        
		        sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " propertiesrent ") +
		        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = prrt_orderid) " +
		        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON(prty_propertyid = prrt_propertyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON(indu_industryid = cust_industryid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) ";
		        
		        sql +=" WHERE orde_orderid > 0 " +
			   			" AND orde_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
						where;			
				sql += " ORDER by prrt_propertiesrentid ASC";
				pmPropertyRentalConsult.doFetch(sql); 
				
		
		        double sumProdTotal = 0, sumEquiTotal = 0, sumStaffTotal = 0, sumOCTotal = 0, gastosTotal = 0,saldoTotal = 0;
		
		        int i = 0;
		        while (pmPropertyRentalConsult.next()) {
		        	double sumProd = 0, sumEqui = 0, sumStaff = 0, sumOC = 0, gastos = 0;
		        	
			    	double amount = pmPropertyRentalConsult.getDouble("orde_amount");
			    	double discount = pmPropertyRentalConsult.getDouble("orde_discount");
			    	double tax = pmPropertyRentalConsult.getDouble("orde_tax");				          	
			    	double total = pmPropertyRentalConsult.getDouble("orde_total");
			    	double totalSinIva = pmPropertyRentalConsult.getDouble("orde_total") - tax;	
			    	double balance = pmPropertyRentalConsult.getDouble("orde_balance");
			    		
			    	// Conversion a la moneda destino(del Pedido)
			    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
			    	double parityOrigin = 0, parityDestiny = 0;
			    	currencyIdOrigin = pmPropertyRentalConsult.getInt("orde_currencyid");
			    	parityOrigin = pmPropertyRentalConsult.getDouble("orde_currencyparity");
			    	currencyIdDestiny = currencyId;
			    	parityDestiny = defaultParity;
		
			    	// Suma general
			    	amountTotal += amount;
			    	discountTotal += discount;
			    	taxTotal += tax;
			    	totalTotal += total;
			    	totalSinIvaTotal += totalSinIva;
			    	saldoTotal += balance;
		
			    	if (pmPropertyRentalConsult.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
			    		fullName = pmPropertyRentalConsult.getString("cust_code") + " " + pmPropertyRentalConsult.getString("cust_legalname");	
		   			else
		   				fullName = pmPropertyRentalConsult.getString("cust_code") + " " + pmPropertyRentalConsult.getString("cust_displayname");
			    		        	
		        	//Estatus
		        	bmoPropertyRental.getStatus().setValue(pmPropertyRentalConsult.getString("propertiesrent", "prrt_status"));
		        	bmoOrder.getStatus().setValue(pmPropertyRentalConsult.getString("orders", "orde_status"));
		        	bmoOrder.getDeliveryStatus().setValue(pmPropertyRentalConsult.getString("orders", "orde_deliverystatus"));              
		        	bmoOrder.getLockStatus().setValue(pmPropertyRentalConsult.getString("orders", "orde_lockstatus"));
		        	bmoOrder.getPaymentStatus().setValue(pmPropertyRentalConsult.getString("orders", "orde_paymentstatus"));
		     %>      
	    	<tr class="reportCellEven">
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + (i +1), BmFieldType.NUMBER) %>
	        	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCode())) {
	        		%>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_code"), BmFieldType.CODE) %>
	        	<%} %>
	        	 	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getName())) {
	        	 		%>	
	        	 		
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_name"), BmFieldType.STRING)) %>
	        	<%}%>
	        	       	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getPropertyId())) {
					
			%>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("properties", "prty_description"), BmFieldType.STRING)) %>
	        	<%} %>
	        	 	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCustomerId())) {
	        	 		%>
	        	 	
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
	        	<%} %>
	        	   <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getOwnerProperty())) {		
			%>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullNameLessor, BmFieldType.STRING)) %>
	        	<%} %>
	        	
		              <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getUserId())) {
			%>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("users", "user_code"), BmFieldType.STRING)) %>
	        	<% }%>
	        	          <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getStatus())) {
			%>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoPropertyRental.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
	        	<%} %>        
	        	          	 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getStartDate())) {
					
			%>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_startdate"), BmFieldType.DATETIME) %>
	        	<%} %>
	        	
			 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getEndDate())) {
					
			%>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_enddate"), BmFieldType.DATETIME) %>
	        	<%} %>
	        		 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getRentalScheduleDate())) {
			%>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_rentalscheduledate"), BmFieldType.DATETIME) %>
	        	<%}%>
	        		 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getRentIncrease())) {
					
			%>
		        <%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_rentincrease"), BmFieldType.DATETIME) %>
		        <%} %>
		         <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getInitialIconme())) {
			%>
		        <%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_initialIconme"), BmFieldType.BOOLEAN) %>
		        <%} %>
		        <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCurrentIncome())) {
					
			%>
		        <%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_currentIncome"), BmFieldType.BOOLEAN) %>
		        <%} %>
		        
		               	 <%	if (!bmoOrder.getPaymentStatus().equals("")) {
					
			%>
		        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		        <%} %>
	
		        	<%
			    		// Total de CXC 
			    		double sumRacc = 0, sumRaccLinked = 0;
			    		sql = "SELECT racc_currencyid, racc_currencyparity, racc_total, racc_linked " + 
			    				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
			    				" WHERE racc_orderid = " + pmPropertyRentalConsult.getInt("orde_orderid");
			    		
			    		pmConn.doFetch(sql);
			    		while (pmConn.next()) {
			    			// Conversion a la moneda destino(seleccion del filtro)
			  		    	currencyIdOrigin = 0; currencyIdDestiny = 0;
			  		    	parityOrigin = 0; parityDestiny = 0;
			  		    	currencyIdOrigin = pmConn.getInt("racc_currencyid");
			  		    	parityOrigin = pmConn.getDouble("racc_currencyparity");
			  		    	currencyIdDestiny = currencyId;
			  		    	parityDestiny = defaultParity;
		
			  		    	double totalRacc = pmConn.getDouble("racc_total");
			  		    	totalRacc = pmCurrencyExchange.currencyExchange(totalRacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
			  		    	
			  		   		// Sumar cxc
			  		    	sumRacc += totalRacc;
			  		    	sumRaccTotal += totalRacc;
			  		    	
			  		   		// Sumar cxc externas
			  		    	if (pmConn.getInt("racc_linked") == 1) {
			  		    		sumRaccLinked += totalRacc;
			  		    		sumRaccLinkedTotal += totalRacc;
			  		    	}
			    		}
			    	%>
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + sumRacc, BmFieldType.CURRENCY) %>
		         </tr>
		         <%
		         i++;
		        } //pmOrder
		        %> 
		
		        <tr class="reportCellEven"><td colspan="<%= 21 + columnBudgets + dynamicColspan%>">&nbsp;</td></tr>
		        
		        <tr class="reportCellEven reportCellCode">
			        	<td colspan="<%= (14 + columnBudgets + dynamicColspan) - dynamicColspanMinus%>">&nbsp;</td >
		        		<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
			        	<%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>		
				<%= HtmlUtil.formatReportCell(sFParams, "" + saldoTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccLinkedTotal, BmFieldType.CURRENCY) %>
		    	</tr>
		    	<tr><td colspan="<%= 20 + columnBudgets + dynamicColspan%>">&nbsp;</td></tr>
	
				<%
			} // Fin pmCurrencyWhile
			%>
			<%
		 
}else {
int i = 1;
%>
		<tr class="">                                                                                                       
        	<td class="reportHeaderCellCenter">#</td>
        	<% dynamicColspan++; %>
	        	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCode())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getCode()))%></td>
		<%	}%>
	               	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getName())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getName()))%></td>
		<%	}%>
	              	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getPropertyId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getPropertyId()))%></td>
		<%	}%>
	               	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCustomerId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getCustomerId()))%></td>
		<%	}%>
	                      <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getOwnerProperty())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getOwnerProperty()))%></td>
		<%	}%>
		
	              <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getUserId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getUserId()))%></td>
		<%	}%>
		
	             <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getStatus())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getStatus()))%></td>
		<%	}%>
		
		 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getStartDate())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getStartDate()))%></td>
		<%	}%>
		
		 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getEndDate())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getEndDate()))%></td>
		<%	}%>
	            
	           	 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getRentalScheduleDate())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getRentalScheduleDate()))%></td>
		<%	}%>
		
 	 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getRentIncrease())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getRentIncrease()))%></td>
		<%	}%>	
		        	 <%	if (!bmoOrder.getPaymentStatus().equals("")) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCellCenter">Pago</td>
		<%	}%>   
		
			 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getInitialIconme())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getInitialIconme()))%></td>
		<%	}%>	
		     
	           	 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCurrentIncome())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getCurrentIncome()))%></td>
		<%	}%>	
	       	     
		         	 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getDescription())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoPropertyRental.getProgramCode(), bmoPropertyRental.getDescription()))%></td>
		<%	}%>	                                  
        <%
        
		double amountTotal = 0, discountTotal = 0, taxTotal = 0, totalTotal = 0, subtotalProductTotal = 0, totalSinIvaTotal = 0;
   
        sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " propertiesrent ") +     
        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = prrt_orderid) " +
        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON(prty_propertyid = prrt_propertyid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" as a ON(a.cust_customerid = orde_customerid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" as lessor ON(lessor.cust_customerid = prty_customerid) " +				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) ";
		
		sql += " WHERE orde_orderid > 0 " +
				where;
		sql += " ORDER by orde_orderid ASC";
		pmPropertyRentalConsult.doFetch(sql); 
        double sumProdTotal = 0, sumEquiTotal = 0, sumStaffTotal = 0, sumOCTotal = 0, gastosTotal = 0, saldoTotal = 0, 
        		sumRaccTotal = 0, sumRaccLinkedTotal = 0;

       
        while (pmPropertyRentalConsult.next()) {
        	 double sumProd = 0, sumEqui = 0, sumStaff = 0, sumOC = 0, gastos = 0;
        	
	    	double amount = pmPropertyRentalConsult.getDouble("prrt_currentIncome");
	    	double discount = pmPropertyRentalConsult.getDouble("prrt_initialIconme");
	    	double tax = pmPropertyRentalConsult.getDouble("orde_tax");				          	
	    	double total = pmPropertyRentalConsult.getDouble("orde_total");
	    	double totalSinIva = pmPropertyRentalConsult.getDouble("orde_total") - tax;	
	    	double balance = pmPropertyRentalConsult.getDouble("orde_balance");
	    	
	    	//Conversion a la moneda destino(seleccion del filtro)
	    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	    	double parityOrigin = 0, parityDestiny = 0;
	    	currencyIdOrigin = pmPropertyRentalConsult.getInt("orde_currencyid");
	    	parityOrigin = pmPropertyRentalConsult.getDouble("orde_currencyparity");
	    	currencyIdDestiny = currencyId;
	    	parityDestiny = defaultParity;

	    	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	totalSinIva = pmCurrencyExchange.currencyExchange(totalSinIva, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	balance = pmCurrencyExchange.currencyExchange(balance, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	    	
	    	// Suma general
	    	amountTotal += amount;
	    	discountTotal += discount;
	    	taxTotal += tax;
	    	totalTotal += total;
	    	totalSinIvaTotal += totalSinIva;
	    	saldoTotal += balance;

	    	if (pmPropertyRentalConsult.getString("a.cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	    		if((pmPropertyRentalConsult.getString("a.cust_legalname").equals("")) || (pmPropertyRentalConsult.getString("a.cust_legalname") == null))
	    			fullName = pmPropertyRentalConsult.getString("a.cust_code") + " " + pmPropertyRentalConsult.getString("a.cust_displayname");
	    		else 
	    			fullName = pmPropertyRentalConsult.getString("a.cust_code") + " " + pmPropertyRentalConsult.getString("a.cust_legalname");	
   			else
   				fullName = pmPropertyRentalConsult.getString("a.cust_code") + " " + pmPropertyRentalConsult.getString("a.cust_displayname");
	    	
	    	if (pmPropertyRentalConsult.getString("lessor.cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	    		if((pmPropertyRentalConsult.getString("lessor.cust_legalname").equals("")) || (pmPropertyRentalConsult.getString("lessor.cust_legalname") == null)) 
	    			fullNameLessor = pmPropertyRentalConsult.getString("lessor.cust_code") + " " + pmPropertyRentalConsult.getString("lessor.cust_displayname");
	    		else
	    		fullNameLessor = pmPropertyRentalConsult.getString("lessor.cust_code") + " " + pmPropertyRentalConsult.getString("lessor.cust_legalname");	
   			else
   				fullNameLessor = pmPropertyRentalConsult.getString("lessor.cust_code") + " " + pmPropertyRentalConsult.getString("lessor.cust_displayname");
        	
        	//Estatus
        	bmoPropertyRental.getStatus().setValue(pmPropertyRentalConsult.getString("propertiesrent","prrt_status"));
        	bmoOrder.getStatus().setValue(pmPropertyRentalConsult.getString("orders", "orde_status"));
        	bmoOrder.getDeliveryStatus().setValue(pmPropertyRentalConsult.getString("orders", "orde_deliverystatus"));              
        	bmoOrder.getLockStatus().setValue(pmPropertyRentalConsult.getString("orders", "orde_lockstatus"));
        	bmoOrder.getPaymentStatus().setValue(pmPropertyRentalConsult.getString("orders", "orde_paymentstatus"));

    %>      
    	<tr class="reportCellEven">
        	<%= HtmlUtil.formatReportCell(sFParams, "" + (i), BmFieldType.NUMBER) %>
        	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCode())) {
        		%>
        	<%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_code"), BmFieldType.CODE) %>
        	<%} %>
        	 	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getName())) {
        	 		%>	
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_name"), BmFieldType.STRING)) %>
        	<%}%>
        	       	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getPropertyId())) {
				
		%>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("properties", "prty_description"), BmFieldType.STRING)) %>
        	<%} %>
        	 	<%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCustomerId())) {
        	 		%>
        	 	
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING)) %>
        	<%} %>
        	   <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getOwnerProperty())) {		
		%>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fullNameLessor, BmFieldType.STRING)) %>
        	<%} %>
        	
	              <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getUserId())) {
		%>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("users", "user_code"), BmFieldType.STRING)) %>
        	<% }%>
        	          <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getStatus())) {
		%>
        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoPropertyRental.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
        	<%} %>        
        	          	 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getStartDate())) {
				
		%>
        	<%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_startdate"), BmFieldType.DATETIME) %>
        	<%} %>
        	
		 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getEndDate())) {
				
		%>
        	<%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_enddate"), BmFieldType.DATETIME) %>
        	<%} %>
        		 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getRentalScheduleDate())) {
		%>
        	<%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_rentalscheduledate"), BmFieldType.DATETIME) %>
        	<%}%>
        		 <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getRentIncrease())) {
				
		%>
	        <%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_rentincrease"), BmFieldType.DATETIME) %>
	        <%} %>
	      	<%	if (!bmoOrder.getPaymentStatus().equals("")) {
				
		%>
	        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOrder.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
	        <%} %>
	         <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getInitialIconme())) {
		%>
	        <%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_initialIconme"), BmFieldType.CURRENCY) %>
	        <%} %>
	        <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getCurrentIncome())) {
				
		%>
	        <%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_currentIncome"), BmFieldType.CURRENCY	) %>
	        <%} %>
	        

     <%	if (sFParams.isFieldEnabled(bmoPropertyRental.getDescription())) {
				
		%>
	        <%= HtmlUtil.formatReportCell(sFParams, pmPropertyRentalConsult.getString("propertiesrent", "prrt_description"), BmFieldType.STRING) %>
	        <%} %>
	        	       
	        
         </tr>
         <%
i++;
        } 
        
        %>
         <tr class="reportCellEven"><td colspan="<%= 1 %>">&nbsp;</td></tr>
        <tr class="reportCellEven reportCellCode">
	        	<td colspan="<%= 13 %>">&nbsp;</td >
	        	          <%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
      					  <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
    	</tr>
        
        <%
	}
        %> 
</table>		
<%		
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmConn.close();
	pmConnOrdeGroup.close();
	pmPropertyRentalConsult.close();
%>  
<% 	if (print.equals("1")) { %>
    	<script>
        	//window.print();
    	</script>
<% 	} %>
  </body>
</html>
