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
 
<%@include file="/inc/login.jsp" %>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%@page import="com.symgae.shared.GwtUtil"%>


<%
	// Inicializar variables
 	String title = "Estatus de Pedidos del Cliente";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoCustomer bmoCustomer = new BmoCustomer();
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
		
   	String sql = "", sqlOrderTypes = "" ,where = "", filters = "", groupFilter = "", birthdate = "", birthdateEnd = "";
   	String dateNow = "", status = "",  customerType = "";
   	int programId = 0, customerId = 0, salesman = 0, referralId = 0, territoryId = 0, countOrderTypes = 0, countProductRenewOrder = 0,
   			industryId = 0, regionId = 0, birthdateByMonth = 0, maritalStatus = 0;
   	
   	String iconTrue = "", iconFalse = "";
	iconTrue = GwtUtil.getProperUrl(sFParams, "/icons/boolean_true.png"); 
	iconFalse = GwtUtil.getProperUrl(sFParams, "/icons/boolean_false.png"); 
   	
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("cust_customerid") != null) customerId = Integer.parseInt(request.getParameter("cust_customerid"));
    if (request.getParameter("cust_customertype") != null) customerType = request.getParameter("cust_customertype");
    if (request.getParameter("cust_salesmanid") != null) salesman = Integer.parseInt(request.getParameter("cust_salesmanid"));
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
    if (request.getParameter("cust_territoryid") != null) territoryId = Integer.parseInt(request.getParameter("cust_territoryid"));
    if (request.getParameter("cust_maritalstatusid") != null) maritalStatus = Integer.parseInt(request.getParameter("cust_maritalstatusid"));
    if (request.getParameter("cust_status") != null) status = request.getParameter("cust_status");
    if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid"));
    if (request.getParameter("cust_regionid") != null) regionId = Integer.parseInt(request.getParameter("cust_regionid"));
    if (request.getParameter("birthdateByMonth") != null) birthdateByMonth = Integer.parseInt(request.getParameter("birthdateByMonth"));
	if (request.getParameter("cust_birthdate") != null) birthdate = request.getParameter("cust_birthdate");
	if (request.getParameter("birthdateEnd") != null) birthdateEnd = request.getParameter("birthdateEnd");
    
	// Construir filtros 
    dateNow =  SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
	filters += "<i>Fecha: </i>" + dateNow + ", ";
    
	if (customerId > 0) {
        where += " AND cust_customerid = " + customerId;
        filters += "<i>Cliente/Prospecto: </i>" + request.getParameter("cust_customeridLabel") + ", ";
    }
	
    if (!customerType.equals("")) {
   		where += " AND cust_customertype = '" + customerType + "' ";
   		filters += "<i>Tipo de Cliente: </i>" + request.getParameter("cust_customertypeLabel") + ", ";
   	}
    
    if (salesman > 0) {
        where += " AND cust_salesmanid = " + salesman;
        filters += "<i>Vendedor: </i>" + request.getParameter("cust_salesmanidLabel") + ", ";
    }
    
    if (referralId > 0) {
        where += " AND cust_referralid = " + referralId;
        filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
    }
    
    if (territoryId > 0) {
        where += " AND cust_territoryid = " + territoryId;
        filters += "<i>Territorio: </i>" + request.getParameter("cust_territoryidLabel") + ", ";
    }
    
    if (maritalStatus > 0 ) {
   		where += " AND cust_maritalstatusid = '" + maritalStatus + "' ";
   		//filters += "<i>Estado Civil: </i>" + request.getParameter("cust_maritalstatusLabel") + ", ";
   		where += " AND cust_maritalstatusid = " + maritalStatus;
   				filters += "<i>Estado Civil: </i>" +	request.getParameter("cust_maritalstatusidLabel") + ", ";
    }  
    
    if (industryId > 0) {
        where += " AND cust_industryid = " + industryId;
        filters += "<i>SIC: </i>" + request.getParameter("cust_industryidLabel") + ", ";
    }
    
    if (regionId > 0) {
        where += " AND cust_regionid = " + regionId;
        filters += "<i>Regi&oacute;n: </i>" + request.getParameter("cust_regionidLabel") + ", ";
    }
   	
    if (!status.equals("")) {
        where += SFServerUtil.parseFiltersToSql("cust_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("cust_statusLabel") + ", ";
   	}
    
 // Para clientes por mes de fecha de nacimiento
 	if (sFParams.isFieldEnabled(bmoCustomer.getBirthdate())) {
 		if (birthdateByMonth == 1) {
 			filters += "<i>Fecha Nac. por mes: </i>" + request.getParameter("birthdateByMonthLabel") + ", ";
 			if (!birthdate.equals("")) {
 				where += " AND DATE_FORMAT(cust_birthdate, '%m-%d') >= '" + birthdate.substring(5, 10) + "'";
 				filters += "<i>F. Nacimiento: </i>" + request.getParameter("cust_birthdate") + ", ";
 			}
 	
 			if (!birthdateEnd.equals("")) {
 				where += " AND DATE_FORMAT(cust_birthdate, '%m-%d') <= '" + birthdateEnd.substring(5, 10) + "'";
 				filters += "<i>F. Nacimiento Fin: </i>" + request.getParameter("birthdateEnd") + ", ";
 			}
 		} else {
 			filters += "<i>Fecha Nac. por mes: </i>" + request.getParameter("birthdateByMonthLabel") + ", ";
 			if (!birthdate.equals("")) {
 				where += " AND cust_birthdate >= '" + birthdate + "'";
 				filters += "<i>F. Nacimiento: </i>" + request.getParameter("cust_birthdate") + ", ";
 			}
 	
 			if (!birthdateEnd.equals("")) {
 				where += " AND cust_birthdate <= '" + birthdateEnd + "'";
 				filters += "<i>F. Nacimiento Fin: </i>" + request.getParameter("birthdateEnd") + ", ";
 			}
 		}
 	}
    
   	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
   	
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmCustomer(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;

			
  	
  	
	//System.out.println("sql: "+sql);
	
	PmConn pmConnGroup = new PmConn(sFParams);
	pmConnGroup.open();
	
	PmConn pmConnListOp = new PmConn(sFParams);
	pmConnListOp.open();
	
	PmConn pmConnListPed = new PmConn(sFParams);
	pmConnListPed.open();
		
	PmConn pmConnOrderTypes = new PmConn(sFParams);
	pmConnOrderTypes.open();
	
    //abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
    pmConnCount.open();
    
  	sql = "  SELECT COUNT(*) as contador FROM " + SQLUtil.formatKind(sFParams, " customers ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " territories")+" ON (terr_territoryid = cust_territoryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " regions")+" ON (regi_regionid = cust_regionid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (refe_referralid = cust_referralid) " +
			" WHERE cust_customerid > 0 " +
			where + 
			" ORDER BY cust_customerid ASC";
			int count =0;
			//ejecuto el sql
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			System.out.println("contador"+count);	
	
  	sqlOrderTypes = "SELECT COUNT(*) FROM " + SQLUtil.formatKind(sFParams, "ordertypes");
	pmConnOrderTypes.doFetch(sqlOrderTypes);
	if (pmConnOrderTypes.next()) 
		countOrderTypes = pmConnOrderTypes.getInt(1);
		
	sqlOrderTypes = "SELECT * FROM  " + SQLUtil.formatKind(sFParams, "ordertypes") + " ORDER BY ortp_ordertypeid ASC";
	pmConnOrderTypes.doFetch(sqlOrderTypes);
		
	PmConn pmConnProductRenewOrder = new PmConn(sFParams);
	pmConnProductRenewOrder.open();
	
	sqlOrderTypes = "SELECT COUNT(*) FROM  " + SQLUtil.formatKind(sFParams, "products") + " WHERE prod_reneworder = 1 ORDER BY prod_productid ASC";
	pmConnProductRenewOrder.doFetch(sqlOrderTypes);
	if (pmConnProductRenewOrder.next()) 
		countProductRenewOrder = pmConnProductRenewOrder.getInt(1);
	
	sqlOrderTypes = "SELECT * FROM  " + SQLUtil.formatKind(sFParams, "products") + " WHERE prod_reneworder = 1 ORDER BY prod_productid ASC";
	pmConnProductRenewOrder.doFetch(sqlOrderTypes);
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
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
				<td align="left" rowspan="2" valign="top">	
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
					<b>Ordenado por:</b> Clave del Cliente
				</td>
			<td class="reportDate" align="right">
					Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
				</td>
			</tr>
		</table>
		<%
		//if que muestra el mensajede error
		if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
			%>
			
				<%=messageTooLargeList %>
			<% 
		}else{
			sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customers ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " territories")+" ON (terr_territoryid = cust_territoryid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " regions")+" ON (regi_regionid = cust_regionid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (refe_referralid = cust_referralid) " +
					" WHERE cust_customerid > 0 " +
					where + 
					" ORDER BY cust_customerid ASC";
		%>
		<br>
		<table class="report" width="100%">
			<tr>
				<td class="reportHeaderCellCenter" rowspan="2">#</td>
		    	<td class="reportHeaderCell" rowspan="2">Cliente</td>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getPhone())) { %>
		    			<td class="reportHeaderCell" rowspan="2">Tel&eacute;fono</td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getMobile())) { %>
		    			<td class="reportHeaderCell" rowspan="2">M&oacute;vil</td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getEmail())) { %>
		    			<td class="reportHeaderCell" rowspan="2">Email</td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getTerritoryId())) { %>
		    			<td class="reportHeaderCell" rowspan="2"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getTerritoryId()))%></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getRegionId())) { %>
		    			<td class="reportHeaderCell" rowspan="2"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getRegionId()))%></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getReferralId())) { %>
		    			<td class="reportHeaderCell" rowspan="2"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getReferralId()))%></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getNss())) { %>
		    			<td class="reportHeaderCell" rowspan="2"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getNss()))%></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getIndustryId())) { %>
		    			<td class="reportHeaderCell" rowspan="2"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getIndustryId()))%></td>
		    	<%	} %>
		    	
		    	<!--
		    	<td rowspan="2">&nbsp;</td>
		    	<td class="reportHeaderCellCenter" colspan="<%= countOrderTypes%>">Oportunidades Activas</td>
		    	-->
		    	<td rowspan="2">&nbsp;</td>
		    	<td class="reportHeaderCellCenter" colspan="<%= countOrderTypes%>">Pedidos Activos</td>
		    	<td rowspan="2">&nbsp;</td>
		    	<% 	if(pmConnProductRenewOrder.next()) { %>
				    	<td class="reportHeaderCellCenter" colspan="<%= countProductRenewOrder%>">Productos con Renovaci&oacute;n</td>
		    	<% 	} %>
			</tr>
			<tr>
		    	<%
//		    	while(pmConnOrderTypes.next()) {
	    		%>
		    		<!--
		    		<td class="reportHeaderCell">
		    			<%//= pmConnOrderTypes.getString("ortp_code")%>
		    		</td>
		    		-->
		    	<%
//		    	}
				pmConnOrderTypes.beforeFirst();
		    	while(pmConnOrderTypes.next()) {
	    		%>
		    		<td class="reportHeaderCell">
						<span title="<%= HtmlUtil.stringToHtml(pmConnOrderTypes.getString("ortp_name"))%>">
		    				<%= pmConnOrderTypes.getString("ortp_name")%>
		    			</span>
		    		</td>
		    	<%
		    	}
		    	pmConnProductRenewOrder.beforeFirst();
		    	while(pmConnProductRenewOrder.next()) {
		    		String prodCode = "", prodName = "";
		    		prodCode = pmConnProductRenewOrder.getString("prod_code");
		    		if (pmConnProductRenewOrder.getString("prod_displayname").equals("")) {
		    			prodName = pmConnProductRenewOrder.getString("prod_name");
		    		} else {
		    			prodName = pmConnProductRenewOrder.getString("prod_displayname");
		    		}
	    		%>
		    		<td class="reportHeaderCell">
						<span title="<%= HtmlUtil.stringToHtml(prodCode + " " + prodName) %>"><%= prodName%></span>
		    		</td>
		    	<%
		    	}
		    	%>
		    </tr>
		    
		    <% 
		    
		    pmConnGroup.doFetch(sql);
			int i = 1;
	        while(pmConnGroup.next()) {
				%>
				<tr>
					<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_code") + " - " + pmConnGroup.getString("cust_displayname"), BmFieldType.STRING)) %>
			    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getPhone())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_phone"), BmFieldType.PHONE) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getMobile())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_mobile"), BmFieldType.PHONE) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getEmail())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_email"), BmFieldType.EMAIL)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getTerritoryId())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("terr_name"), BmFieldType.STRING)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getRegionId())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("regi_name"), BmFieldType.STRING)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getReferralId())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("refe_name"), BmFieldType.STRING)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getNss())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_nss"), BmFieldType.STRING)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getIndustryId())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("indu_name"), BmFieldType.STRING)) %>
					<%	} %>

					<td>&nbsp;</td>
					<%
					// OPORTUNIDADES
//					pmConnOrderTypes.beforeFirst();
//					int activeOP = 0;
//	   				while (pmConnOrderTypes.next()) {
//	   					
//				    	String sqlOppo = "SELECT COUNT(oppo_opportunityid) AS op FROM opportunities " +
//											 " WHERE oppo_status = '" + BmoOrder.STATUS_REVISION + "' " +
//											 " AND oppo_customerid = " + pmConnGroup.getInt("cust_customerid") + 
//											 " AND oppo_ordertypeid = " + pmConnOrderTypes.getInt("ortp_ordertypeid") +
//											 " AND ('" + dateNow + "' BETWEEN oppo_startdate AND oppo_enddate) ";
//				    	pmConnListOp.doFetch(sqlOppo);
//				   
//				    	if(pmConnListOp.next()) {
//							activeOP = pmConnListOp.getInt("op");
//				    	} // pmConnListOp
//				    	if (activeOP > 0){
				    		%>
				    			<%//= HtmlUtil.formatReportCell(sFParams, "<img src=\"/icons/boolean_true.png\"> Si", BmFieldType.STRING) %>
					    	<%
//				    	} else{
				    		%>
			    				<%//= HtmlUtil.formatReportCell(sFParams, "<img src=\"/icons/boolean_false.png\"> No", BmFieldType.STRING) %>
			    			<%
//				    	}
//	   				} // pmConnOrderTypesOppo
	   				%>
	   				<!--
	   				<td>&nbsp;</td>
	   				-->
	   				<%
   					// PEDIDOS
					pmConnOrderTypes.beforeFirst();
					int activePed = 0;

	   				while (pmConnOrderTypes.next()) {
	
				    	String sqlOrder = "SELECT COUNT(orde_orderid) as orde FROM  " + SQLUtil.formatKind(sFParams, "orders ") +
											 " WHERE orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "' " +
											 " AND orde_customerid = " + pmConnGroup.getInt("cust_customerid") + 
											 " AND orde_ordertypeid = " + pmConnOrderTypes.getInt("ortp_ordertypeid") +
											 " AND ('" + dateNow + "' BETWEEN orde_lockstart AND orde_lockend) ";
						pmConnListPed.doFetch(sqlOrder);
				   
				    	if(pmConnListPed.next()) {
							activePed = pmConnListPed.getInt("orde");
				    	} // pmConnListPed
				    	if (activePed > 0){
				    		%>   	
				    			<%= HtmlUtil.formatReportCell(sFParams, "<img src=\"" + iconTrue + "\"> Si", BmFieldType.STRING) %>
					    	<%
				    	} else{
				    		%>			
			    				<%= HtmlUtil.formatReportCell(sFParams, "<img src=\"" + iconFalse + "\"> No", BmFieldType.STRING) %>
			    			<%
				    	}
	   				} // pmConnOrderTypesOrde
	   				%>
	   				<td>&nbsp;</td>
	   				<%
   					// PRODUCTOS
//					pmConnOrderTypes.beforeFirst();
//					int activePro = 0;
//	   				while (pmConnOrderTypes.next()) {
//	
//				    	String sqlOrder = "SELECT COUNT(ordi_orderitemid) as orderItem FROM orderitems " +
//				    						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups ON (ordg_ordergroupid = ordi_ordergroupid) " +
//				    						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders ON (orde_orderid = ordg_orderid) " +
//				    						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products ON (prod_productid = ordi_productid) " +
//							    			" WHERE orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "' " +
//							    			" AND orde_customerid = " + pmConnGroup.getInt("cust_customerid") + 
//							    			" AND orde_ordertypeid = " + pmConnOrderTypes.getInt("ortp_ordertypeid") +
//							    			" AND ordi_productid > 0 " +
//							    			" AND prod_reneworder = 1 "+
//							    			" AND ('" + dateNow + "' BETWEEN orde_lockstart AND orde_lockend) ";
//				    	
////				    	System.out.println("cust: "+pmConnGroup.getString("cust_code"));
////				    	System.out.println("sqlOrder: "+sqlOrder);
//				    	
//				    	
//						pmConnListPed.doFetch(sqlOrder);
//				   
//				    	if(pmConnListPed.next()) {
//				    		activePro = pmConnListPed.getInt("orderItem");
//				    	} // pmConnListPed
//				    	if (activePro > 0){
				    		%>
				    			<%//= HtmlUtil.formatReportCell(sFParams, "<img src=\"/icons/boolean_true.png\"> Si", BmFieldType.STRING) %>
					    	<%
				    	//} else{
				    		%>
			    				<%//= HtmlUtil.formatReportCell(sFParams, "<img src=\"/icons/boolean_false.png\"> No", BmFieldType.STRING) %>
			    			<%
				    	//}
	   				//} // pmConnOrderTypesOrde
	   				%>
    				<%
   					// PRODUCTOS RENOVAR PEDIDO
    				pmConnProductRenewOrder.beforeFirst();
					int activePro = 0;
	   				while (pmConnProductRenewOrder.next()) {
	
				    	String sqlOrder = "SELECT COUNT(ordi_orderitemid) as orderItem FROM  " + SQLUtil.formatKind(sFParams, "orderitems ") +
				    						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordg_ordergroupid = ordi_ordergroupid) " +
				    						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordg_orderid) " +
				    						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = ordi_productid) " +
							    			" WHERE ordi_productid > 0 " +
							    			" AND prod_reneworder = 1 " +
							    			" AND prod_productid = " + pmConnProductRenewOrder.getInt("prod_productid") +
							    			" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "' " +
							    			" AND orde_customerid = " + pmConnGroup.getInt("cust_customerid") + 
							    			" AND ('" + dateNow + "' BETWEEN orde_lockstart AND orde_lockend) ";
				    	
//							    	System.out.println("cust: "+pmConnGroup.getString("cust_code"));
//							    	System.out.println("sqlOrder: "+sqlOrder);
				    	
				    	
						pmConnListPed.doFetch(sqlOrder);
				   
				    	if(pmConnListPed.next()) {
				    		activePro = pmConnListPed.getInt("orderItem");
				    	} // pmConnListPed
				    	if (activePro > 0){	
				    		%>
				    			<%= HtmlUtil.formatReportCell(sFParams, "<img src=\"" + iconTrue + "\"> Si", BmFieldType.STRING) %>
					    	<%
				    	} else{
				    		%>
			    				<%= HtmlUtil.formatReportCell(sFParams, "<img src=\"" + iconFalse + "\"> No", BmFieldType.STRING) %>
			    			<%
				    	}
	   				} // pmConnOrderTypesOrde
	   				%>
	   			</tr>
	   			<%	
   				i++;
	        } // pmConnGroup
			          %>
			
		</table> 
	<% 
	}//Fin de validacion de 5 mil registros
	}// Fin de if(no cargar datos)
	pmConnListOp.close();
	pmConnListPed.close();
	pmConnGroup.close();
	pmConnOrderTypes.close();
	pmConnProductRenewOrder.close();
	pmConnCount.close();
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