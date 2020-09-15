<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito. *
 * @version 2013-10
 */ -->

<%@include file="/inc/login.jsp" %>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.cr.BmoCredit"%>
<%@page import="com.flexwm.server.cr.PmCredit"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
	String title = "Reporte Credito Debe";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress());

   	String sql = "", where = "", whereUser = "", now = "", startDate = "", endDate = "";
   	String creditStatus = "", orderStatus = "", filters = "",creditTypeId = "", amountStart = "", amountEnd = "";
   	int  customerId = 0, salesmanId = 0, locationId = 0, programId = 0; 
   	
    BmoCredit bmoCredit = new BmoCredit();
    PmCredit pmCredit = new PmCredit(sFParams);
    BmoOrder bmoOrder = new BmoOrder();
    BmoRaccount bmoRaccount = new BmoRaccount();
	BmoCustomer bmoCustomer = new BmoCustomer();
	PmCustomer pmCustomer = new PmCustomer(sFParams);
   	
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));  	
   	if (request.getParameter("cred_credittypeid") != null) creditTypeId = request.getParameter("cred_credittypeid");
   	if (request.getParameter("cred_customerid") != null) customerId = Integer.parseInt(request.getParameter("cred_customerid"));
   	if (request.getParameter("salesUserId") != null) salesmanId = Integer.parseInt(request.getParameter("salesUserId"));
   	if (request.getParameter("cred_locationid") != null) locationId = Integer.parseInt(request.getParameter("cred_locationid"));
   	if (request.getParameter("cred_startdate") != null) startDate = request.getParameter("cred_startdate");
   	if (request.getParameter("cred_enddate") != null) endDate = request.getParameter("cred_enddate");   		
   	if (request.getParameter("cred_status") != null) creditStatus = request.getParameter("cred_status");
   	if (request.getParameter("orde_status") != null) orderStatus = request.getParameter("orde_status");
   	if (request.getParameter("cred_amountstart") != null) amountStart = request.getParameter("cred_amountstart");
   	if (request.getParameter("cred_amountend") != null) amountEnd = request.getParameter("cred_amountend");
   	// Filtros
   	// Tomar la semana en curso
   	//now = "2017-09-11"; // Ejemplo
   	now = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());

   	// Empieza el lunes de la semana en curso
	Calendar mondayWeek = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), now);
	mondayWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	mondayWeek.add(Calendar.WEEK_OF_YEAR, 0);
	//System.out.println("lunes: "+FlexUtil.calendarToString(sFParams, mondayWeek));
	
   	// Termina el domingo de la semana en curso
	Calendar sundayWeek = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), now);
	sundayWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	sundayWeek.add(Calendar.WEEK_OF_YEAR, 1);
	//System.out.println("domingo: "+FlexUtil.calendarToString(sFParams, sundayWeek));
	filters += "<i>Fecha Semana: </i>" + FlexUtil.calendarToString(sFParams, mondayWeek) + 
			" a " +  FlexUtil.calendarToString(sFParams, sundayWeek) + ", ";
   	
//    	if (creditTypeId > 0) {
//         where += " and cred_credittypeid = " + creditTypeId;
//         filters += "<i>Tipo Cr&eacute;dito </i>" + request.getParameter("cred_credittypeidLabel") + ", ";
//     }
   	if(!creditTypeId.equals("")){
   		where += SFServerUtil.parseFiltersToSql("cred_credittypeid", creditTypeId);
   	   filters += "<i>Tipo Cr&eacute;dito </i>" + request.getParameter("cred_credittypeidLabel") + ", ";
   	}
   	
   	if (customerId > 0) {
        where += " and cred_customerid = " + customerId;
        filters += "<i>Cliente: </i>" + request.getParameter("cred_customeridLabel") + ", ";
    }
    
    if (salesmanId > 0) {
    	whereUser += " AND ( " +
					" cred_salesuserid = " + salesmanId +
					" OR cred_wflowid IN ( " +
						 " SELECT wflw_wflowid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  ") +
						 " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflu_wflowid = wflw_wflowid) " +
						 " LEFT JOIN " + SQLUtil.formatKind(sFParams, " credits")+" on (cred_wflowid = wflw_wflowid) " +
						 " WHERE wflu_userid = " + salesmanId + 
						 " AND cred_creditid > 0 " +
						 " AND wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "' " + 
					 " ) " + 
				 " )";
        filters += "<i>Usuario: </i>" + request.getParameter("salesUserIdLabel") + ", ";
    }
    
    if (locationId > 0) {
 		where += " AND cred_locationid = " + locationId;
		filters += "<i>Ubicaci&oacute;n: </i>" + request.getParameter("cred_locationidLabel") + ", ";
	}
    
    if (!amountStart.equals("") || !amountEnd.equals("")) {
		double amountStartTmp = 0, amountEndTmp = 0;

		try {
			amountStartTmp = Double.parseDouble(amountStart);
		} catch (Exception e) {
			amountStartTmp = -1;
		}
		
		try {
			amountEndTmp = Double.parseDouble(amountEnd);
		} catch (Exception e) {
			amountEndTmp = -1;
		}
		
		if (amountStartTmp > 0 ) {
			where += " AND cred_amount >= " + amountStartTmp;
	        filters += "<i>R. Monto Inicial: >= </i>" + amountStartTmp + ", ";
		}
		
		if (amountEndTmp > 0) {
			where += " AND cred_amount <= " + amountEndTmp + "";
	        filters += "<i>R. Monto Final: <= </i>" + amountEndTmp + ", ";
		} 
	}
   	
    if (!startDate.equals("")) {
        where += " and cred_startdate >= '" + startDate + " 00:00' ";
        filters += "<i>Fecha Inicial: </i>" + startDate + ", ";
    }
        
    if (!endDate.equals("")) {
        where += " and cred_startdate <= '" + endDate + " 23:59'";
        filters += "<i>Fecha Final: </i>" + endDate + ", ";
    }
    
    if (!creditStatus.equals("")) {
        where += SFServerUtil.parseFiltersToSql("cred_status", creditStatus);
        filters += "<i>Estatus Cr&eacute;dito: </i>" + request.getParameter("cred_statusLabel") + ", ";
    }
    
    if (!orderStatus.equals("")) {
        where += SFServerUtil.parseFiltersToSql("orde_status", orderStatus);
        filters += "<i>Estatus Pedido: </i>" + request.getParameter("orde_statusLabel") + ", ";
    }
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmCredit(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;

    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	
	pmConn.open();
	pmConn2.open();
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
	
	// Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
	// En caso de que mande a imprimir, deshabilita contenido
	if (!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
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
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) { %>
    
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
<table class="report" border="0">
	<%		
	//abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	sql = " SELECT count(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid= ract_raccounttypeid)   " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credits")+" ON (cred_orderid = orde_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (cred_customerid = cust_customerid) " +
			" WHERE cred_salesuserid  > 0 "+ 
			whereUser + 
			where +
			" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
			" AND racc_failure = 0 " +
			" AND racc_duedate >= '" + FlexUtil.calendarToString(sFParams, mondayWeek) + "' " +
			" AND racc_duedate <= '" + FlexUtil.calendarToString(sFParams, sundayWeek) + "' " +
			" ORDER BY cred_startdate " ;
	
			int count =0;
			//ejecuto el sql
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			System.out.println("contador DE REGISTROS --->   "+count);
			//if que muestra el mensajede error
			if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
				%>
				
					<%=messageTooLargeList %>
				<% 
			}else{
	
	sql = " SELECT cred_salesuserid, user_code, user_firstname, user_fatherlastname, user_motherlastname, crty_deadline FROM " + SQLUtil.formatKind(sFParams, " credits ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (cred_orderid = orde_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (cred_salesuserid = user_userid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (cred_customerid = cust_customerid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" ON (loct_locationid = cred_locationid) " +
			" WHERE cred_creditid > 0 " + 
			whereUser + 
			where +
			" GROUP BY cred_salesuserid" ;
	
		System.out.println("sql_general: "+sql);
	double amountTotal = 0, paymentsTotal = 0, balanceTotal = 0;
	int y = 0;
	int credSalesUserId = 0;
	pmConn.doFetch(sql);       
	while (pmConn.next()) {
	
		int i = 1;
		double amountSum = 0, paymentsSum = 0, balanceSum = 0;
		sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid= ract_raccounttypeid)   " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credits")+" ON (cred_orderid = orde_orderid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (cred_customerid = cust_customerid) " +
				" WHERE cred_salesuserid = " + pmConn.getInt("cred_salesuserid") + 
				where +
				" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
				" AND racc_failure = 0 " +
				" AND racc_duedate >= '" + FlexUtil.calendarToString(sFParams, mondayWeek) + "' " +
				" AND racc_duedate <= '" + FlexUtil.calendarToString(sFParams, sundayWeek) + "' " +
				" ORDER BY cred_startdate" ;

		System.out.println("sql_cxcOnWeek: "+sql);
		pmConn2.doFetch(sql);   
		
		while (pmConn2.next()) {
			bmoRaccount.getPaymentStatus().setValue(pmConn2.getString("raccounts","racc_paymentstatus"));
			// Quitar filas sin registros
			if (pmConn.getInt("cred_salesuserid") != credSalesUserId) {
				credSalesUserId = pmConn.getInt("cred_salesuserid");
			%>
				<tr class="">
					<td class="reportGroupCell" colspan="11">
						Promotor:
						<%= HtmlUtil.stringToHtml(pmConn.getString("user_firstname")) %>
						<%= HtmlUtil.stringToHtml(pmConn.getString("user_fatherlastname")) %>
						<%= HtmlUtil.stringToHtml(pmConn.getString("user_motherlastname")) %> :
						(<%= HtmlUtil.stringToHtml(pmConn.getString("user_code")) %>)
					</td>	
				</tr>
				<tr>
					<td class="reportHeaderCellCenter">#</td>
					<td class="reportHeaderCell">Fecha Cr&eacute;dito</td>			
					<td class="reportHeaderCell">Cr&eacute;dito</td>
					<td class="reportHeaderCell">Cliente</td>
					<td class="reportHeaderCell">Tipo</td>
					<td class="reportHeaderCell">CxC</td>
					<td class="reportHeaderCell">Fecha de Prog.</td>
					<td class="reportHeaderCell">Estatus CxC</td>
					<td class="reportHeaderCellRight">Monto Sem</td>
					<td class="reportHeaderCellRight">Pagos Sem</td>
					<td class="reportHeaderCellRight">Saldo Sem</td>
				</tr>
		<%	} %>
			<tr class="reportCellEven">
				<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
				<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("credits", "cred_startdate").substring(0,10), BmFieldType.STRING)%>
				<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("credits", "cred_code"), BmFieldType.CODE)%>
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConn2.getString("cust_code") + " " + pmConn2.getString("cust_displayname")), BmFieldType.STRING)%>
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConn2.getString("credittypes", "crty_name")), BmFieldType.STRING)%>
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + pmConn2.getString("racc_invoiceno")), BmFieldType.STRING)%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn2.getString("racc_duedate"), BmFieldType.DATE)%>
				<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml("" + bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
				<%
				// Suma por promotor
				amountSum +=  pmConn2.getDouble("racc_amount");
				paymentsSum +=  pmConn2.getDouble("racc_payments");
				balanceSum +=  pmConn2.getDouble("racc_balance");
				// Total General
				amountTotal +=  pmConn2.getDouble("racc_amount");
				paymentsTotal +=  pmConn2.getDouble("racc_payments");
				balanceTotal +=  pmConn2.getDouble("racc_balance");
				%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn2.getDouble("racc_amount"), BmFieldType.CURRENCY)%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn2.getDouble("racc_payments"), BmFieldType.CURRENCY)%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn2.getDouble("racc_balance"), BmFieldType.CURRENCY)%>
			</tr>
			<%
			i++;
			y++;
		}	// fin While pmConn2

		if (amountSum > 0 || paymentsSum > 0 || balanceSum > 0) { %>
			<tr class="reportCellEven reportCellCode">   
				<td colspan="8"></td>
				<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY)%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsSum, BmFieldType.CURRENCY)%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + balanceSum, BmFieldType.CURRENCY)%>
			</tr>
		<%
		}
	}// fin While pmConn
	%>		
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	<%	if (amountTotal > 0 || paymentsTotal > 0 || balanceTotal > 0) { %>
			<tr class="reportCellEven reportCellCode">
				<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER)%>
				<td colspan="7" align="left">&nbsp;</td>
				<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY)%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal, BmFieldType.CURRENCY)%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY)%>
			</tr>
	<% 	} %>
	</table>
	<%
	
	} //Fin validacion x registros	
		pmConnCount.close();
	}// Fin de if(no carga datos)
	pmConn2.close();
	pmConn.close();
	%>  
	<%	if (print.equals("1")) { %>
			<script>
				//window.print();
			</script>
	<% 	} 
	System.out.println("\n Filtros Reporte: "+ filters);
	System.out.println("\n Fin reporte - Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
					+ " Reporte: "+title
					+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); %>
	</body>
</html>
