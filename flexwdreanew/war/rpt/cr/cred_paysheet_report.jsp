<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito. *
 * @version 2013-10
 */ -->
 
<%@page import="com.flexwm.shared.cm.BmoCustomerPhone"%>
<%@page import="com.flexwm.shared.cr.BmoCredit"%>
<%@page import="com.flexwm.server.cr.PmCredit"%>
<%@page import="com.flexwm.shared.cr.BmoCreditType"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.symgae.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%
	// Inicializar variables
 	String title = "Hoja de Pagos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress());

   	String sql = "", where = "";
   	String startDate = "", endDate = "", startDateGroup = "";
   	String creditStatus = "", orderStatus = "";
   	String filters = "",creditTypeId = "";
   	int  customerId = 0, salesmanId = 0, regionId = 0; 	
   	int programId = 0; 
   	
    BmoCredit bmoCredit = new BmoCredit();
    PmCredit pmCredit = new PmCredit(sFParams);
    BmoOrder bmoOrder = new BmoOrder();
   	
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));  	
   	if (request.getParameter("cred_credittypeid") != null) creditTypeId = request.getParameter("cred_credittypeid");
   	if (request.getParameter("cred_customerid") != null) customerId = Integer.parseInt(request.getParameter("cred_customerid"));
   	if (request.getParameter("salesUserId") != null) salesmanId = Integer.parseInt(request.getParameter("salesUserId"));
   	if (request.getParameter("cred_startdate") != null) startDate = request.getParameter("cred_startdate");
   	if (request.getParameter("cred_enddate") != null) endDate = request.getParameter("cred_enddate");   		
   	if (request.getParameter("cred_status") != null) creditStatus = request.getParameter("cred_status");
   	if (request.getParameter("cust_regionid") != null) regionId = Integer.parseInt(request.getParameter("cust_regionid"));
   	if (request.getParameter("orde_status") != null) orderStatus = request.getParameter("orde_status");
   	
   	//Filtros   	
//    	if (creditTypeId > 0) {
//         where += " AND cred_credittypeid = " + creditTypeId;
//         filters += "<i>Tipo Cr&eacute;dito </i>" + request.getParameter("cred_credittypeidLabel") + ", ";
//     }
   	if(!creditTypeId.equals("")){
   		where += SFServerUtil.parseFiltersToSql("cred_credittypeid", creditTypeId);
   	   filters += "<i>Tipo Cr&eacute;dito </i>" + request.getParameter("cred_credittypeidLabel") + ", ";
   	}
   	
   	if (customerId > 0) {
        where += " AND cred_customerid = " + customerId;
        filters += "<i>Cliente: </i>" + request.getParameter("cred_customeridLabel") + ", ";
    }
    
    if (salesmanId > 0) {
    	where += " AND ( " +
					" cred_salesuserid = " + salesmanId +
					" OR cred_wflowid IN ( " +
						 " SELECT wflw_wflowid FROM "+ SQLUtil.formatKind(sFParams,"wflowusers  ") +
						 " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"wflows")+" on (wflu_wflowid = wflw_wflowid) " +
						 " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"credits")+" on (cred_wflowid = wflw_wflowid) " +
			   			 " WHERE wflu_userid = " + salesmanId + 
			   			 " AND cred_creditid > 0 " +
						 " AND wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "' " + 
					 " ) " + 
				 " )";
        filters += "<i>Usuario: </i>" + request.getParameter("salesUserIdLabel") + ", ";
    }
    
    if (regionId > 0) {
 		where += " AND cust_regionid = " + regionId;
		filters += "<i>Zona: </i>" + request.getParameter("cust_regionidLabel") + ", ";
	}
   	
    if (!startDate.equals("")) {
        where += " AND cred_startdate >= '" + startDate + " 00:00'";
        filters += "<i>Fecha Inicial: </i>" + startDate + ", ";
    }
        
    if (!endDate.equals("")) {
        where += " AND cred_startdate <= '" + endDate + " 23:59'";
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
    
    if (sFParams.getSelectedCompanyId() > 0) {
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    }
    
    //Obtener disclosure de datos
	String disclosureFilters = new PmCredit(sFParams).getDisclosureFilters();   
	if (disclosureFilters.length() > 0)
	   where += " AND " + disclosureFilters;
	
	BmoCustomer bmoCustomer = new BmoCustomer();
	PmCustomer pmCustomer = new PmCustomer(sFParams);
  		
    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	PmConn pmConn3 = new PmConn(sFParams);
	PmConn pmConn4 = new PmConn(sFParams);

	pmConn.open();
	pmConn2.open();
	pmConn3.open();
	pmConn4.open();
	
	//abro conexion para inciar el conteo
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
		<td class="reportTitle" colspan="2">
			<%= title %>
		</td>
	</tr>
	<tr>
		<td class="reportSubTitle">
			<b>Filtros:</b> <%= filters %>
			<br>
			<b>Agrupado por:</b> D&iacute;a, Promotor, <b>Ordenado:</b> Fecha, Promotor, Cr&eacute;dito 
		</td>
		<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<br>
<table class="report" border="0" cellspacing="0" cellpading="0" width="100%">
<%

		sql = " SELECT COUNT(*) as contador FROM "+ SQLUtil.formatKind(sFParams,"credits ") +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"users")+" ON (cred_salesuserid = user_userid) " +	
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customers")+" ON (cred_customerid = cust_customerid) " + 
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"orders")+" ON (cred_orderid = orde_orderid) " +
				" WHERE cred_creditid > 0 " + where +			  
				" ORDER BY cred_startdate ASC";
				int count =0;
				//ejecuto el sql
				pmConnCount.doFetch(sql);
				if(pmConnCount.next())
					count=pmConnCount.getInt("contador");
				System.out.println("contador de registros --> "+count);
				//if que muestra el mensajede error
				if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
					%>
					
						<%=messageTooLargeList %>
					<% 
				}else{


		sql = " SELECT cred_startdate, crty_deadline, crty_type FROM "+ SQLUtil.formatKind(sFParams,"credits ") +
		      " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"users")+" ON (cred_salesuserid = user_userid) " +	
			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customers")+" ON (cred_customerid = cust_customerid) " + 
			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"orders")+" ON (cred_orderid = orde_orderid) " +
			  " WHERE cred_creditid > 0 " + where +			  
			  " GROUP BY cred_startdate " +
			  " ORDER BY cred_startdate ASC";
		
			 System.out.println("sql_general: "+sql);
		
		pmConn.doFetch(sql);       
		while (pmConn.next()) {
			int deadLine = pmConn.getInt("crty_deadline");
			
			startDateGroup = pmConn.getString("cred_startdate").substring(0, 10);

			// Sumar pagos verticalmente(por fecha)
			int countCreditByUser = 0, iCountCreditByUser = 0;
			sql = " SELECT COUNT(cred_creditid) AS countCreditByUser FROM "+ SQLUtil.formatKind(sFParams,"credits ") +
				      " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
					  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"users")+" ON (cred_salesuserid = user_userid) " +	
					  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customers")+" ON (cred_customerid = cust_customerid) " +	
					  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"orders")+" ON (cred_orderid = orde_orderid) " +
					  " WHERE cred_creditid > 0 " + 
					  where +
					  " AND cred_startdate = '" + pmConn.getString("cred_startdate") + "' " +
					  " GROUP BY cred_salesuserid ASC" +
					  " ORDER BY cred_salesuserid, cred_creditid ASC";
			
//				System.out.println("sqlGroupByUser-COUNT: "+sql);
			
			pmConn2.doFetch(sql);
			if (pmConn2.next()) {
				countCreditByUser = pmConn2.getInt("countCreditByUser");
			}

			// Agrupacion por vendedor/promotor
			sql = " SELECT user_userid, user_code, user_parentid, cred_startdate FROM "+ SQLUtil.formatKind(sFParams,"credits ") +
				      " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
					  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"users")+" ON (cred_salesuserid = user_userid) " +	
					  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customers")+" ON (cred_customerid = cust_customerid) " +	
					  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"orders")+" ON (cred_orderid = orde_orderid) " +
					  " WHERE cred_creditid > 0 " + 
					  where +
					  " AND cred_startdate = '" + pmConn.getString("cred_startdate") + "' " +
					  " GROUP BY cred_salesuserid ASC" +
					  " ORDER BY cred_creditid ASC";
			
			System.out.println("sql_GroupByUser: "+sql);
			
			pmConn2.doFetch(sql);
			while (pmConn2.next()) {
				
				//Arreglo para sumas de pagos de cada fecha del vendedor
				double[][] sumByUser = new double[(countCreditByUser)][(deadLine + 1)];
				
				//Obtener el superior
				String parentName = "";
				sql = "SELECT user_code, user_firstname, user_fatherlastname, user_motherlastname " + 
						" FROM "+ SQLUtil.formatKind(sFParams,"users")+" WHERE user_userid = " + pmConn2.getInt("user_parentid");
				pmConn4.doFetch(sql);
				if (pmConn4.next()) parentName = pmConn4.getString("user_code");
				
		%>
				<tr>
					<td colspan="<%= deadLine + 7 %>" class="reportGroupCell">
					    Fecha Cr&eacute;dito: <%= startDateGroup%> | Promotor: <%= pmConn2.getString("user_code") %> | Superior: <%= parentName %>
					</td>
				</tr>
				<tr>
					<td class="reportHeaderCell">#</td>
					<td class="reportHeaderCell">Cr&eacute;dito</td>
					<td class="reportHeaderCell">Cliente/Tel/Domicilio</td>
					<td class="reportHeaderCellRight">Pr&eacute;stamo</td>
					<td class="reportHeaderCellRight">Pago</td>
					
					<%
					//EL primer dia de pago debe ser el lunes proximo
					Calendar nowWeek = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), startDateGroup);
					nowWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
					nowWeek.add(Calendar.WEEK_OF_YEAR, 0);
					
					
					//Calcular la primera fecha de pago
					String startDatePayment = FlexUtil.calendarToString(sFParams, nowWeek);
//					System.out.println("datePayout: "+startDatePayment);
					
					//Obtener los dias de pago de acuerdo el tipo de credito
					int daysPayout = 0;
					if (pmConn.getString("credittypes", "crty_type").equals("" + BmoCreditType.TYPE_WEEKLY)) {
						daysPayout = 7;
					} else if (pmConn.getString("crty_type").equals("" + BmoCreditType.TYPE_TWOWEEKS)) {
						daysPayout = 15;
					} else if (pmConn.getString("crty_type").equals("" + BmoCreditType.TYPE_MONTHLY)) { 
						daysPayout = 30;
					} else {
						throw new Exception("El tipo de crédito no cuenta con un Tipo(Semanal, Quincenal, Mensual) definido");
					}
					
					//Mostrar fechas de pagos +1 por si tiene falla
					for (int x = 1; x <= (deadLine + 1); x++) {
						startDatePayment = SFServerUtil.addDays(sFParams.getDateFormat(), startDatePayment, daysPayout);
					%>					
							<td class="reportHeaderCellRight">
								<%= x%><br>
								<%= startDatePayment %>
							</td>
					<%	
						}
					%>		
					<!--
					<td class="reportHeaderCell">Pagos<br>Cliente</td>
					<td class="reportHeaderCellRight">Total Pagar</td>
					-->
					<td class="reportHeaderCell">Avales</td>
				</tr>
		<%
		sql = " SELECT cred_creditid, cred_code, cust_customerid, cust_code, cust_displayname, cred_amount, " + 
				" orde_orderid, orde_total, orde_status, crty_deadline " + 
				" FROM "+ SQLUtil.formatKind(sFParams,"credits ") +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"orders")+" ON (cred_orderid = orde_orderid) " +
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"users")+" ON (cred_salesuserid = user_userid) " +	
				" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customers")+" ON (cred_customerid = cust_customerid) " +	
				" WHERE cred_creditid > 0 " + 
				where +
				" AND cred_startdate = '" + pmConn2.getString("cred_startdate") + "' " +
				" AND cred_salesuserid = " +  pmConn2.getInt("user_userid") +
				" ORDER BY cred_creditid ASC";
				
				System.out.println("sql_UserStartDateCredit: "+sql);
				pmConn3.doFetch(sql);
				
				// sumas por promotor
				double credAmountByUser = 0;
				double ordeTotalByUser = 0;
				double paymentWeekByUser = 0;
				double paymentWeekCustByUser = 0;

				int i = 1;
				while (pmConn3.next()) {
					double paymentWeekByCust = 0;
					
					//Domicilio cliente
					// Se le da prioridad el Tel Movil, luego a Casa, al final otros
					String custPhone = "Sin Tel.";
					sql = " SELECT cuph_type, cuph_number FROM " + SQLUtil.formatKind(sFParams, "customerphones") +
					      " WHERE cuph_customerid = " + pmConn3.getInt("cust_customerid") +
					      " ORDER BY cuph_type ASC";
					pmConn4.doFetch(sql);
					while (pmConn4.next()) {
						if (pmConn4.getString("cuph_type").equals("" + BmoCustomerPhone.TYPE_MOBILE)) {
							custPhone = pmConn4.getString("cuph_number");
							break;
						} else {
							custPhone = pmConn4.getString("cuph_number");									
						}
					}
					
					
					//Domicilio cliente
					String custAddress = "Sin Domicilio";
					sql = " SELECT cuad_street, cuad_number, cuad_neighborhood, city_name, stat_name  FROM "+ SQLUtil.formatKind(sFParams,"customeraddress ") +
					      " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"cities")+" ON (cuad_cityid = city_cityid) " +
					      " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"states")+" ON (city_stateid = stat_stateid) " +
					      " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"countries")+" ON (stat_countryid = cont_countryid) " +
					      " WHERE cuad_customerid = " + pmConn3.getInt("cust_customerid");
					pmConn4.doFetch(sql);
					if (pmConn4.next()) {
						custAddress = HtmlUtil.stringToHtml(pmConn4.getString("customeraddress","cuad_street")) + 
								" #" + pmConn4.getString("cuad_number") + ", " 
								+ HtmlUtil.stringToHtml(pmConn4.getString("customeraddress", "cuad_neighborhood")) + 
								", " + HtmlUtil.stringToHtml(pmConn4.getString("cities", "city_name")) + " " + HtmlUtil.stringToHtml(pmConn4.getString("states", "stat_name")); 
					}
					
					//Domicilio aval, da preferencia al domicilio de tipo Personal
					String guareantees = "";
					sql = " SELECT cust_displayname, cuph_number,cuad_street, cuad_number, cuad_neighborhood, city_name, stat_name " + 
							" FROM "+ SQLUtil.formatKind(sFParams,"creditguarantees ") +
							" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customers")+" ON (crgu_customerid = cust_customerid) " +	
							" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customeraddress")+" ON (cuad_customerid = cust_customerid) " +
							" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"cities")+" ON (cuad_cityid = city_cityid) " +
							" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"states")+" ON (city_stateid = stat_stateid) " +
							" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"countries")+" ON (stat_countryid = cont_countryid) " +
							" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customerphones")+" ON (cuph_customerid = cust_customerid) " +
							" WHERE crgu_creditid = " + pmConn3.getInt("cred_creditid") + 
							" GROUP BY crgu_customerid ORDER BY crgu_creditguaranteesid, cuad_type";

					pmConn4.doFetch(sql);
					int countGuarantees = 0;
					while (pmConn4.next()) {
						if (countGuarantees > 0) guareantees += " : ";

						guareantees += pmConn4.getString("cust_displayname") + " - ";
						guareantees += pmConn4.getString("cuph_number") + " - ";
						if ((pmConn4.getString("cuad_street").length() > 0)
								|| (pmConn4.getString("cuad_number").length() > 0)
								|| (pmConn4.getString("cuad_neighborhood").length() > 0)) {
							guareantees += HtmlUtil.stringToHtml(pmConn4.getString("cuad_street")) + " #" + pmConn4.getString("cuad_number") +
										" " + HtmlUtil.stringToHtml(pmConn4.getString("cuad_neighborhood")) + ", " + HtmlUtil.stringToHtml(pmConn4.getString("city_name")) + 
										" " + HtmlUtil.stringToHtml(pmConn4.getString("stat_name")); 
						} else guareantees += "Sin Domicilio";
						countGuarantees++;
					}	
	%>
					<TR class="reportCellEven">
						<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
						
						<%= HtmlUtil.formatReportCell(sFParams, pmConn3.getString("credits", "cred_code"), BmFieldType.CODE)%>

						<%= HtmlUtil.formatReportCell(sFParams, pmConn3.getString("customers", "cust_code") + 
														" " + HtmlUtil.stringToHtml(pmConn3.getString("customers", "cust_displayname")) +
														" - " + custPhone +
														" - " + custAddress, 
														BmFieldType.STRING)%>

						<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, custAddress, BmFieldType.STRING))%>
						<%= HtmlUtil.formatReportCell(sFParams, pmConn3.getString("credits", "cred_amount"), BmFieldType.CURRENCY)%>
						<%= HtmlUtil.formatReportCell(sFParams, "" + (pmConn3.getDouble("orde_total") / pmConn3.getDouble("crty_deadline")), BmFieldType.CURRENCY)%>

						<%
							// Si no esta autorizado el pedido crear celda vacia para rellenar
							if (pmConn3.getString("orde_status").equals("" + BmoOrder.STATUS_REVISION)
									|| pmConn3.getString("orde_status").equals("" + BmoOrder.STATUS_CANCELLED)) {
							
								String statusOrde = "En Revisi&oacute;n";
								if (pmConn3.getString("orde_status").equals("" + BmoOrder.STATUS_CANCELLED))
										statusOrde = "Cancelado";
						%>
								<td colspan="<%= (deadLine + 1) %>" class="reportCellText">El Pedido est&aacute; <%= statusOrde%></td>
						<%	
							} else {
								
								//Mostrar cxc
								sql = " SELECT racc_payments FROM "+ SQLUtil.formatKind(sFParams,"raccounts ") +
										  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"raccounttypes")+" ON (racc_raccounttypeid= ract_raccounttypeid) " +	
										  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"orders")+" ON (racc_orderid = orde_orderid) " +	
										  " WHERE racc_orderid = " + pmConn3.getInt("orde_orderid") +
										  " AND ract_category = '" + BmoRaccountType.CATEGORY_ORDER + "' " +
										  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' ";
//										  " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
								pmConn4.doFetch(sql);
//								System.out.println("sqlRACC: "+sql);
								int countRacc = 0;
								while (pmConn4.next()) {
									
									paymentWeekByCust += pmConn4.getDouble("racc_payments");
									paymentWeekCustByUser += pmConn4.getDouble("racc_payments");
									sumByUser[iCountCreditByUser][countRacc] += pmConn4.getDouble("racc_payments");
									countRacc++;
									
									if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {

										if (pmConn4.getDouble("racc_payments") == 0) { %>
											<td>&nbsp;</td>
									<% 	} else { %>
											<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn4.getDouble("racc_payments"), BmFieldType.CURRENCY)%>
									<%	}
							
									} else { %>
										<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn4.getDouble("racc_payments"), BmFieldType.CURRENCY)%>
							<%		}
								}
								
								if (countRacc == deadLine) {					
									// aregamos el 13-falla
									sumByUser[iCountCreditByUser][countRacc++] += 0;
						%>		
									<%= HtmlUtil.formatReportCell(sFParams, "NA", BmFieldType.STRING, "reportCellCurrency")%>
	
						<% 		} 
							}
						%>
							<%//= HtmlUtil.formatReportCell(sFParams, "" + paymentWeekByCust, BmFieldType.CURRENCY)%>
							<%//= HtmlUtil.formatReportCell(sFParams, pmConn3.getString("orders", "orde_total"), BmFieldType.CURRENCY)%>

						<%= HtmlUtil.formatReportCell(sFParams, "" + guareantees, BmFieldType.STRING)%>
					</TR>	
				   
	<%
					credAmountByUser += pmConn3.getDouble("cred_amount");
					ordeTotalByUser += pmConn3.getDouble("orde_total");
					paymentWeekByUser += (pmConn3.getDouble("orde_total") / pmConn3.getDouble("crty_deadline"));
	
					i++;
				} // fin while pmConn3
	%>
				<tr class="reportCellEven reportCellCode">   
					<td colspan="3">&nbsp;</td>			
					<%= HtmlUtil.formatReportCell(sFParams, "" + credAmountByUser, BmFieldType.CURRENCY)%>
					<%= HtmlUtil.formatReportCell(sFParams, "" + paymentWeekByUser, BmFieldType.CURRENCY)%>
					<%
					
//					System.out.println("tamanio array: "+sumByUser.length);
					// Sacamos valores del array, saco solo UNA fila ya que viene el acumulado de las otras filas
					for (int n=0; n < 1; n++) {
						for(int j=0; j < sumByUser[0].length; j++) {
					%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + sumByUser[n][j], BmFieldType.CURRENCY)%>
							
					<%
						}
					}
					%>
					<td>&nbsp;</td>
					<!--
					<%//= HtmlUtil.formatReportCell(sFParams, "" + paymentWeekCustByUser, BmFieldType.CURRENCY)%>
					<%//= HtmlUtil.formatReportCell(sFParams, "" + ordeTotalByUser, BmFieldType.CURRENCY)%>

					-->
				</tr>
				<tr><td colspan="<%= deadLine + 7 %>">&nbsp;</td></tr>
	<%
			
			} // fin while pmConn2
%>		
			
<%
	}// fin While pmConn
%>	 
 </table>
  <%
  	
} //Fin validacion x registros
				
			  	
	}// Fin de if(no carga datos)
	pmConn4.close();
  	pmConn3.close();
  	pmConn2.close();
  	pmConn.close();
  	pmConnCount.close();
  %>  

	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	System.out.println("\n Filtros Reporte: "+ filters);
	System.out.println("\n Fin reporte - Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); %>
  </body>
</html>
