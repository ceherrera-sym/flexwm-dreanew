<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito. *
 * @version 2013-10
 */ -->

<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.cr.BmoCredit"%>
<%@page import="com.flexwm.server.cr.PmCredit"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerPhone"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.server.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
 	String title = "Reporte Microcredito Fallas";	

	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
					+ " Reporte: "+title
					+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress());
   	
	String sql = "", where = "", groupFilter = "", whereGroup = "", whereRacc = "";
   	String startDate = "", endDate = "", userCollaborator = "", raccDueDateStart = "", raccDueDateEnd = "";
   	String creditStatus = "", orderStatus = "";
   	String filters = "", collaboratorProfile = "",creditTypeId = "", amountStart = "", amountEnd = "";
   	int  customerId = 0, salesmanId = 0, locationId = 0, programId = 0, profileId = 0; 
   	
    BmoCredit bmoCredit = new BmoCredit();
    PmCredit pmCredit = new PmCredit(sFParams);
    BmoOrder bmoOrder = new BmoOrder();
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
   	if (request.getParameter("profileid") != null) profileId = Integer.parseInt(request.getParameter("profileid"));
   	if (request.getParameter("racc_dueDateStart") != null) raccDueDateStart = request.getParameter("racc_dueDateStart");
   	if (request.getParameter("racc_dueDateEnd") != null) raccDueDateEnd = request.getParameter("racc_dueDateEnd");
   	if (request.getParameter("orde_status") != null) orderStatus = request.getParameter("orde_status");
   	if (request.getParameter("cred_amountstart") != null) amountStart = request.getParameter("cred_amountstart");
   	if (request.getParameter("cred_amountend") != null) amountEnd = request.getParameter("cred_amountend");
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
   				" SELECT wflw_wflowid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  ") +
   				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflu_wflowid = wflw_wflowid) " +
   				" WHERE wflu_userid = " + salesmanId + 
   				" AND wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "' " + 
   				" ) " + 
   				" )";
   		filters += "<i>Usuario: </i>" + request.getParameter("salesUserIdLabel") + ", ";
   	}
    
	if (locationId > 0) {
 		where += " AND cred_locationid = " + locationId;
		filters += "<i>Ubicaci&oacute;n: </i>" + request.getParameter("cred_locationidLabel") + ", ";
	}
	
	if (profileId > 0) {
		whereGroup += " AND prof_profileid = " + profileId;
		filters += request.getParameter("profileidLabel") + ", ";
		collaboratorProfile = request.getParameter("profileidLabel");
		// Saca nombre del grupo
    	String[] collaboratorProfileShort = collaboratorProfile.split(" | ");
    	collaboratorProfile = collaboratorProfileShort[0];
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
        where += " AND cred_startdate >= '" + startDate + " 00:00' ";
        filters += "<i>Fecha Inicial: </i>" + startDate + ", ";
    }
        
    if (!endDate.equals("")) {
        where += " AND cred_startdate <= '" + endDate + " 23:59' ";
        filters += "<i>Fecha Final: </i>" + endDate + ", ";
    }
    
    if (!raccDueDateStart.equals("")) {
    	whereRacc += " AND racc_duedate >= '" + raccDueDateStart + "'";
        filters += "<i>F. Inicio Falla: </i>" + raccDueDateStart + ", ";
    }
        
    if (!raccDueDateEnd.equals("")) {
    	whereRacc += " AND racc_duedate <= '" + raccDueDateEnd + "'";
        filters += "<i>F. Fin Falla: </i>" + raccDueDateEnd + ", ";
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
    
    String startDueDate = SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat());					
	Calendar nowWeek = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), startDueDate.substring(0,10));
	int lastWeek = nowWeek.get(Calendar.WEEK_OF_YEAR) - 1;
	nowWeek.set(Calendar.WEEK_OF_YEAR, lastWeek);
	nowWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);	
	
	startDueDate = FlexUtil.calendarToString(sFParams, nowWeek);
	
	//filters += "<i>Fecha Corte: </i>" + startDueDate;

    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	PmConn pmConn3 = new PmConn(sFParams);
	
	pmConn.open();
	pmConn2.open();
	pmConn3.open();
	
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
	<tr>
		<td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Fecha Cr&eacute;dito</td>
		<td class="reportHeaderCell">Cr&eacute;dito</td>
		<td class="reportHeaderCell">Pago</td>
		<td class="reportHeaderCell">Cliente</td>
		<td class="reportHeaderCell">Tel. Cliente</td>
		<td class="reportHeaderCell">Domicilio</td>
		<% 	if ((collaboratorProfile.length() > 0)) { %>
				<td class="reportHeaderCell"><%= collaboratorProfile%></td>
		<% 	} %>
		<td class="reportHeaderCell">Promotor</td>
		<td class="reportHeaderCell">Ubicaci&oacute;n</td>
		<td class="reportHeaderCell">No.Fallas</td>
		<!--<td class="reportHeaderCell">Fecha Falla</td>-->
		<td class="reportHeaderCellRight">Falla</td>
		<td class="reportHeaderCellRight">Fallas</td>
		<td class="reportHeaderCellRight">Acumulado</td>
		<td class="reportHeaderCell">Aval</td>
	</tr>
		 
<%				
	int i = 0;
	double failureSum = 0;
	double sumFailureToday = 0;
	double amountFailure = 0;
	double sumNowFailure = 0;


	//consulta principal
	sql = " SELECT COUNT(*) as contador FROM " + SQLUtil.formatKind(sFParams, " credits ") +
			  	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
			  	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (cred_salesuserid = user_userid) " +
			  	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (cred_orderid = orde_orderid) " +
			  	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (cred_customerid = cust_customerid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" ON (loct_locationid = cred_locationid) " +
		      	" WHERE cred_creditid > 0 " +  
			  	" AND (cred_paymentstatus = '" + BmoCredit.PAYMENTSTATUS_PENALTY + "' OR cred_paymentstatus = '" + BmoCredit.PAYMENTSTATUS_INPROBLEM + "')" +
		      
		      where;
		      //whereGroup;
		      if (!raccDueDateStart.equals("") 
		    		  || !raccDueDateEnd.equals("")) {
		    	  sql += " AND orde_orderid IN ( " +
					      " SELECT racc_orderid FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
					      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid= ract_raccounttypeid) " +	
					      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid) " +	
					      " WHERE ract_category = '" + BmoRaccountType.CATEGORY_ORDER + "' " +
					      " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
					      " AND racc_failure = 0 " + whereRacc +
					      " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +
					      " AND racc_raccountid IN ( " +
					      "     SELECT ralk_foreignid FROM " + SQLUtil.formatKind(sFParams, " raccountlinks ") +
					      " ) " +
			      " ) ";
		      }
		      sql += " ORDER BY loct_locationid, cred_startdate, cred_salesuserid, cred_creditid" ;
		      System.out.println("sql-----------------------------------"+sql);
		      int count =0;
				//ejecuto el sql
				pmConnCount.doFetch(sql);
				if(pmConnCount.next())
					count=pmConnCount.getInt("contador");
				System.out.println("contador DE REGISTROS -->  "+count);
				//if que muestra el mensajede error
				if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
					%>
					
	<%=messageTooLargeList %>
					<% 
				}else{
	
	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " credits ") +
		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (cred_salesuserid = user_userid) " +
		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (cred_orderid = orde_orderid) " +
		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (cred_customerid = cust_customerid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" ON (loct_locationid = cred_locationid) " +
	      " WHERE cred_creditid > 0 " +  
		  " AND (cred_paymentstatus = '" + BmoCredit.PAYMENTSTATUS_PENALTY + "' OR cred_paymentstatus = '" + BmoCredit.PAYMENTSTATUS_INPROBLEM + "')" +
	      where;
	      if (!raccDueDateStart.equals("") 
	    		  || !raccDueDateEnd.equals("")) {
	    	  sql += " AND orde_orderid IN ( " +
				      " SELECT racc_orderid FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
				      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid= ract_raccounttypeid) " +	
				      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid) " +	
				      " WHERE ract_category = '" + BmoRaccountType.CATEGORY_ORDER + "' " +
				      " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
				      " AND racc_failure = 0 " + whereRacc +
				      " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +
				      " AND racc_raccountid IN ( " +
				      "     SELECT ralk_foreignid FROM " + SQLUtil.formatKind(sFParams, " raccountlinks ") +
				      " ) " +
				      
				      //" AND not racc_relatedraccountid is null " +
				      /*" AND racc_relatedraccountid IN ( " +
					      " SELECT racc_raccountid  FROM " + SQLUtil.formatKind(sFParams, " raccounts " +
					      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes ON (racc_raccounttypeid= ract_raccounttypeid) " +	
					      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders ON (racc_orderid = orde_orderid) " +	
					      " WHERE ract_category = '" + BmoRaccountType.CATEGORY_ORDER + "' " +
					      " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
					      whereRacc +
				      " ) "+*/
		      " ) ";
	      }
	      sql += " ORDER BY loct_locationid, cred_startdate, cred_salesuserid, cred_creditid" ;
	      
	      System.out.println("sql_general: "+sql);
	      
	pmConn.doFetch(sql);       
	while (pmConn.next()) {
		String phone = "";
		
		sql = "SELECT cuph_number, cuph_type FROM " + SQLUtil.formatKind(sFParams, " customerphones")+" WHERE cuph_customerid = " + pmConn.getInt("cust_customerid") + " ORDER BY cuph_type ASC";
		pmConn2.doFetch(sql);
		while (pmConn2.next()) {
			
			// Le da prioridad al telefono movil
			if (pmConn2.getString("cuph_type").equals("" + BmoCustomerPhone.TYPE_MOBILE)) {
				phone = pmConn2.getString("customerphones", "cuph_number");
				break;
			} else 
				phone = pmConn2.getString("customerphones", "cuph_number");
		}

%>	
		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + (i+1), BmFieldType.STRING)%>		
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("credits", "cred_startdate").substring(0, 10), BmFieldType.STRING) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("credits", "cred_code"), BmFieldType.CODE) %>
	    	<%
	    		bmoCredit.getPaymentStatus().setValue(pmConn.getString("cred_paymentstatus"));
	    	%>
	    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + bmoCredit.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
	    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("customers", "cust_code") + " " + pmConn.getString("customers", "cust_displayname"), BmFieldType.STRING)) %>
	    	
	    	
	    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + phone, BmFieldType.PHONE)) %>

	    	<%
				//Domicilio
				String custAddress = "Sin Domicilio";
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customeraddress ") +
				      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities")+" ON (cuad_cityid = city_cityid) " +
				      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " states")+" ON (city_stateid = stat_stateid) " +
				      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " countries")+" ON (stat_countryid = cont_countryid) " +
				      " WHERE cuad_customerid = " + pmConn.getInt("cust_customerid") +
				      " ORDER BY cuad_type ASC";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) {
					custAddress = pmConn2.getString("customeraddress","cuad_street") + " " + 
								"#" + pmConn2.getString("cuad_number") + " " + pmConn2.getString("customeraddress", "cuad_neighborhood") + ", " +
								pmConn2.getString("cities", "city_name") + " " + pmConn2.getString("states", "stat_name"); 
				}
			%>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, custAddress, BmFieldType.STRING))%>
			<%
				userCollaborator = "";

				if ((collaboratorProfile.length() > 0)) {

					//Obtener colaborador buscando por el Grupo
	
					if (profileId > 0) {
						sql = " SELECT user_code FROM " + SQLUtil.formatKind(sFParams, " wflowusers ") +
							  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " profiles")+" ON (wflu_profileid = prof_profileid) " +	
						      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (wflu_userid = user_userid) " +						  
						      " WHERE wflu_wflowid = " + pmConn.getInt("cred_wflowid") +
						      whereGroup;
						pmConn2.doFetch(sql);
						int moreUsers = 1;
						while(pmConn2.next()) { 
							if(moreUsers > 1 ) userCollaborator += "| ";
							userCollaborator += pmConn2.getString("users", "user_code");
							moreUsers++;
						}
					}
			%>
	    			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + userCollaborator, BmFieldType.CODE)) %>
	    	<% 	} %>
	    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("users", "user_code"), BmFieldType.CODE)) %>
	    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("locations", "loct_name"), BmFieldType.CODE)) %>
	    	<%	    	
	    	double sumFailure = 0;
	    	//Mostrar cxc
			sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid= ract_raccounttypeid) " +	
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid) " +	
					  " WHERE racc_orderid = " + pmConn.getInt("orde_orderid") +
					  " AND ract_category = '" + BmoRaccountType.CATEGORY_ORDER + "' " +
					  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
					  //" AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +
					  " AND racc_failure = 1 ";	    	
	    	//System.out.println("sql_: "+sql);
			pmConn2.doFetch(sql);
			String receiveDate = "";
			int countRacc = 0;
			if (pmConn2.next()) {
				sumFailure = pmConn2.getDouble("racc_balance");
				// buscamos la cxc que genero la penalizacion
				String sqlRaccRelated = " SELECT racc_balance, racc_receivedate FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid= ract_raccounttypeid) " +	
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid) " +	
						  " WHERE racc_orderid = " + pmConn.getInt("orde_orderid") +
						  " AND ract_category = '" + BmoRaccountType.CATEGORY_ORDER + "' " +
						  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' " +
						  " AND racc_raccountid = " + pmConn2.getInt("racc_relatedraccountid") ;
						  //" AND racc_duedate <= '" + startDueDate.substring(0,10) + "'";
				
				pmConn3.doFetch(sqlRaccRelated);
				double relatedRacc = 0; 
				if (pmConn3.next()) {
					relatedRacc = pmConn3.getDouble("racc_balance");
					receiveDate = pmConn3.getString("racc_receivedate");
				}
				
				amountFailure = relatedRacc;
				//failureSum += relatedRacc;
				countRacc++;
				
			}
		%>
		<%
			//Calcular el numero de fallas (CxC con saldo) con la fecha del �ltimo Cron			
			
			double nowFailure = 0;			
			sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid= ract_raccounttypeid) " +
				  " WHERE racc_orderid = " + pmConn.getInt("cred_orderid") +
				  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				  " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
				  " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +				  		  
				  //" AND racc_duedate = '" + startDueDate.substring(0,10) + "'" + 
				  " AND racc_failure = 1";
			pmConn3.doFetch(sql);					 
			if (pmConn3.next()) {				
				nowFailure = pmConn3.getDouble("racc_balance");
			}
			
			
			
			//Numero de Fallas (CxC Relacionadas)
			int noFailure = 0;
			sql = " SELECT count(*) AS nofailure FROM " + SQLUtil.formatKind(sFParams, " raccountlinks ") +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounts")+" ON (ralk_foreignid = racc_raccountid) " +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid= ract_raccounttypeid) " +
				  " WHERE racc_orderid = " + pmConn.getInt("cred_orderid") +
				  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				  " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
				  " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" + whereRacc +
				  " AND (racc_failure = 0 OR racc_failure is null)";
			System.out.println("sql " + sql);
			pmConn3.doFetch(sql);
			if (pmConn3.next()) {
				noFailure = pmConn3.getInt("nofailure");			
			}
			
			//sumNowFailure += nowFailure;
				  
			/*
			int noFailure = 0;			
			sql = " SELECT COUNT(*) AS nofailure FROM " + SQLUtil.formatKind(sFParams, " raccounts " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes ON (racc_raccounttypeid= ract_raccounttypeid) " +
				  " WHERE racc_orderid = " + pmConn.getInt("cred_orderid") +
				  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				  " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
				  " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +
				  " AND not racc_relatedraccountid is null " +		  
				  " AND racc_duedate <= '" + startDueDate.substring(0,10) + "'" + 
				  " AND (racc_failure = 0 OR racc_failure is null)";
			pmConn3.doFetch(sql);					 
			if (pmConn3.next()) {				
				noFailure = pmConn3.getInt("nofailure");
			}*/
			
			
			
			double sumBalanceFailures = 0;
			/*sql = " SELECT SUM(racc_total) AS failures FROM " + SQLUtil.formatKind(sFParams, " raccountlinks " +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounts ON (ralk_foreignid = racc_raccountid) " +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes ON (racc_raccounttypeid= ract_raccounttypeid) " +
			      " WHERE racc_orderid = " + pmConn.getInt("cred_orderid") +
				  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				  " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
				  " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +				  
				  " AND racc_duedate <= '" + startDueDate.substring(0,10) + "'" + 
				  " AND (racc_failure = 0 OR racc_failure is null)";*/
			  
			sql = " select SUM(f.racc_balance) AS failures FROM " + SQLUtil.formatKind(sFParams, " raccountlinks ") +
				  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounts")+" as p on (ralk_raccountid = p.racc_raccountid) " +
				  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" as prt on (p.racc_raccounttypeid = prt.ract_raccounttypeid) " +				  				
				  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounts")+" as f on (ralk_foreignid = f.racc_raccountid) " +
				  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" as cf on (f.racc_customerid = cf.cust_customerid) " +
				  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" as frt on (f.racc_raccounttypeid = frt.ract_raccounttypeid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (f.racc_orderid = orde_orderid) " +
				  "	where p.racc_failure = 1 " + 
				  " and p.racc_orderid = " + + pmConn.getInt("cred_orderid") + 
				  " AND frt.ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				  " AND f.racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
				  " AND f.racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'";
				  //" AND f.racc_duedate <= '" + startDueDate.substring(0,10) + "'" +
		  	if (!raccDueDateStart.equals("")) {
		    	sql += " AND f.racc_duedate >= '" + raccDueDateStart + "'";		        
		    }
				  
		  	if (!raccDueDateEnd.equals("")) {
		    	sql += " AND f.racc_duedate <= '" + raccDueDateEnd + "'";		        
		    }	  
				  
			sql += " AND (f.racc_failure = 0 OR f.racc_failure is null)";
		  	pmConn3.doFetch(sql);					 
			if (pmConn3.next()) {				
				sumBalanceFailures = pmConn3.getDouble("failures");
			}	
			
			//Calcular el monto de las fallas (CxC con saldo)
			/*sql = " SELECT SUM(racc_balance) AS failures FROM " + SQLUtil.formatKind(sFParams, " raccounts " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes ON (racc_raccounttypeid= ract_raccounttypeid) " +
				  " WHERE racc_orderid = " + pmConn.getInt("cred_orderid") +
				  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				  " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
				  " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +
				  " AND not racc_relatedraccountid is null " +
				  " AND racc_duedate <= '" + startDueDate.substring(0,10) + "'" + 
				  " AND (racc_failure = 0 OR racc_failure is null)";
			pmConn3.doFetch(sql);					 
			if (pmConn3.next()) {				
				sumBalanceFailures = pmConn3.getDouble("failures");
			}	*/  
			
			failureSum += sumBalanceFailures;
			sumNowFailure += nowFailure;
			sumFailureToday += (sumFailure + sumBalanceFailures);
		%>
		<!-- No.Falla -->
		<%= HtmlUtil.formatReportCell(sFParams, "" + noFailure, BmFieldType.NUMBER)%>
		<!-- Falla -->
		<%= HtmlUtil.formatReportCell(sFParams, "" + nowFailure, BmFieldType.CURRENCY)%>			
		<!-- Fallas -->
		<%= HtmlUtil.formatReportCell(sFParams, "" + sumBalanceFailures, BmFieldType.CURRENCY)%>				
		<!-- Acumlado -->						
	    <%= HtmlUtil.formatReportCell(sFParams, "" + (sumFailure + sumBalanceFailures), BmFieldType.CURRENCY)%>
	    <%
	    	
			//Domicilio aval, da preferencia al domicilio de tipo Personal
			String guareantees = "";
			sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " creditguarantees ") +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (crgu_customerid = cust_customerid) " +	
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customeraddress")+" ON (cuad_customerid = cust_customerid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities")+" ON (cuad_cityid = city_cityid) " +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " states")+" ON (city_stateid = stat_stateid) " +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " countries")+" ON (stat_countryid = cont_countryid) " +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customerphones")+ " ON (cuph_customerid = cust_customerid)" +
				  " WHERE crgu_creditid = " + pmConn.getInt("cred_creditid") + 
				  " GROUP BY crgu_customerid ORDER BY crgu_creditguaranteesid, cuad_type";
			
			pmConn2.doFetch(sql);
			int countGuarantees = 0;
			while (pmConn2.next()) {
				if (countGuarantees > 0) guareantees += "| ";
				
				guareantees += pmConn2.getString("cust_displayname") + " - ";
				
				if ((pmConn2.getString("cuad_street").length() > 0)
						|| (pmConn2.getString("cuad_number").length() > 0)
						|| (pmConn2.getString("cuad_neighborhood").length() > 0)) {
				guareantees += pmConn2.getString("cuad_street") + " #" + pmConn2.getString("cuad_number") +
						" " + pmConn2.getString("cuad_neighborhood") + ", " + pmConn2.getString("city_name") + 
						" " + pmConn2.getString("stat_name") +
						" <br>Tel&eacute;fono : " + pmConn2.getString("cuph_number"); 
 				} else guareantees += "Sin Domicilio";
				
				countGuarantees++;
			}						
				
			%>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, guareantees, BmFieldType.STRING))%>
    	</tr>
<%
		i++;
	} // fin pmConn
%>		
	<tr><td colspan="<%= ((profileId > 0) ? "15"  : "14")%>">&nbsp;</td></tr>

	<tr class="reportCellEven reportCellCode">
		<td colspan="<%= ((profileId > 0) ? "11" : "10")%>">
			&nbsp;
		</td>
		<%= HtmlUtil.formatReportCell(sFParams, "" + sumNowFailure, BmFieldType.CURRENCY) %>
		<%= HtmlUtil.formatReportCell(sFParams, "" + failureSum, BmFieldType.CURRENCY) %>
		<%= HtmlUtil.formatReportCell(sFParams, "" + sumFailureToday, BmFieldType.CURRENCY) %>
		<td >
			&nbsp;
		</td>
	</tr>

		 
 </table>
  <%		 
		}//Fin validacion 1000 registros			
	}// Fin de if(no carga datos)

	pmConn3.open();
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
