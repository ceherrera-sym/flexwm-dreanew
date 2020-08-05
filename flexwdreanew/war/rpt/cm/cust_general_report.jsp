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

<%
	// Inicializar variables
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	
 	String title = "Reportes General de Clientes";
	BmoCustomer bmoCustomer = new BmoCustomer();
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
		String mensajeDeError=null;
   	String sql = "", sqlOrderTypes = "" ,where = "", filters = "", groupFilter = "", birthdate = "", birthdateEnd = "", dateCreateStart = "", dateCreateEnd = "";
   	String dateNow = "", status = "",  customerType = "";
   	int programId = 0, customerId = 0, salesman = 0, referralId = 0, territoryId = 0, countOrderTypes = 0, birthdateByMonth = 0,
   			industryId = 0, regionId = 0,maritalStatus = 0;
   	
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
	
	if (request.getParameter("cust_datecreate") != null) dateCreateStart = request.getParameter("cust_datecreate");
	if (request.getParameter("dateCreateEnd") != null) dateCreateEnd = request.getParameter("dateCreateEnd");
    
    bmoProgram = (BmoProgram)pmProgram.get(programId);
	// Construir filtros 
    
    if (customerId > 0) {
        where += " AND cust_customerid = " + customerId;
        filters += "<i>Cliente/Prospecto: </i>" + request.getParameter("cust_customeridLabel") + ", ";
    }
    
    if (!customerType.equals("")) {
   	//	where += " AND cust_customertype = '" + customerType + "' ";
   	//	filters += "<i>Tipo de Cliente: </i>" + request.getParameter("cust_customertypeLabel") + ", ";
    	where += SFServerUtil.parseFiltersToSql("cust_customertype", customerType);
		filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getCustomertype())) + ": </i>" +
				request.getParameter("cust_customertypeLabel") + ", ";
    }
    
    if (salesman > 0) {
      where += " AND cust_salesmanid = " + salesman;
    //    filters += "<i>Vendedor: </i>" + request.getParameter("cust_salesmanidLabel") + ", ";
		filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getSalesmanId())) + ": </i>" +
				request.getParameter("cust_salesmanidLabel") + ", ";
    }
    
    if (referralId > 0) {
        where += " AND cust_referralid = " + referralId;
     //   filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
        filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getReferralId())) + ": </i>" +
				request.getParameter("cust_referralidLabel") + ", ";
    }
    
    if (territoryId > 0) {
        where += " AND cust_territoryid = " + territoryId;
      //  filters += "<i>Territorio: </i>" + request.getParameter("cust_territoryidLabel") + ", ";
        filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getTerritoryId())) + ": </i>" +
				request.getParameter("cust_territoryidLabel") + ", ";
    }
    
    if (maritalStatus > 0 ) {
   		where += " AND cust_maritalstatusid = '" + maritalStatus + "' ";
   		//filters += "<i>Estado Civil: </i>" + request.getParameter("cust_maritalstatusLabel") + ", ";
   		where += " AND cust_maritalstatusid = " + maritalStatus;
   		filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getMaritalStatusId())) + ": </i>" +
				request.getParameter("cust_maritalstatusidLabel") + ", ";
    }    
    
    if (industryId > 0) {
        where += " AND cust_industryid = " + industryId;
       // filters += "<i>SIC: </i>" + request.getParameter("cust_industryidLabel") + ", ";
        filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getIndustryId())) + ": </i>" +
				request.getParameter("cust_industryidLabel") + ", ";
    }
    
    if (regionId > 0) {
        where += " AND cust_regionid = " + regionId;
     //   filters += "<i>Regi&oacute;n: </i>" + request.getParameter("cust_regionidLabel") + ", ";
        filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getRegionId())) + ": </i>" +
				request.getParameter("cust_regionidLabel") + ", ";
    }
    
    if (!status.equals("")) {
        where += SFServerUtil.parseFiltersToSql("cust_status", status);
   		//filters += "<i>Estatus: </i>" + request.getParameter("cust_statusLabel") + ", ";
   		filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getStatus())) + ": </i>" +
				request.getParameter("cust_statusLabel") + ", ";
    }
    
    if (!dateCreateStart.equals("")) {
   		where += " AND cust_datecreate >= '" + dateCreateStart + " 00:00'";
   		filters += "<i>Inicio Fecha Creación: </i>" + dateCreateStart + ", ";
   	}
   	if (!dateCreateEnd.equals("")) {
   		where += " AND cust_datecreate <= '" + dateCreateEnd + " 23:59'";
   		filters += "<i>Fin Fecha Creación: </i>" + dateCreateEnd + ", ";
   	}

	// clientes por mes de fecha de nacimiento
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
	
	PmConn pmConnExtra = new PmConn(sFParams);
	pmConnExtra.open();
	
	//abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
    pmConnCount.open();
    //guardo la variable count para saber cuantos registros hay
    sql = " SELECT COUNT(*) as contador FROM" + SQLUtil.formatKind(sFParams, " customers ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (user_userid = cust_salesmanid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " territories")+" ON (terr_territoryid = cust_territoryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " regions")+" ON (regi_regionid = cust_regionid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (refe_referralid = cust_referralid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " titles")+" ON (titl_titleid = cust_titleid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = cust_currencyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " consultingservices")+" ON (cose_consultingserviceid = cust_consultingserviceid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " reqpaytypes")+" ON (rqpt_reqpaytypeid = cust_reqpaytypeid) " +
			" WHERE cust_customerid > 0 " +
			where + 
			" ORDER BY cust_customerid ASC";
		
			int count =0;
			//ejecuto el sql
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			pmConnCount.close();
			System.out.println("contador de reportes "+count);
	
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
		<br>
		<table class="report" width="100%">
			<tr class="reportCellEven">
			<%
			//if que muestra el mensajede error
			if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
				%>
				<%=messageTooLargeList %>
				<% 
			}else{
				
				sql = " SELECT * FROM" + SQLUtil.formatKind(sFParams, " customers ") +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (user_userid = cust_salesmanid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " territories")+" ON (terr_territoryid = cust_territoryid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " regions")+" ON (regi_regionid = cust_regionid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (refe_referralid = cust_referralid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " titles")+" ON (titl_titleid = cust_titleid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = cust_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " consultingservices")+" ON (cose_consultingserviceid = cust_consultingserviceid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " reqpaytypes")+" ON (rqpt_reqpaytypeid = cust_reqpaytypeid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " maritalstatus")+" ON (mast_maritalstatusid = cust_maritalstatusid) " +
						" WHERE cust_customerid > 0 " +
						where + 
						" ORDER BY cust_customerid ASC";
						
						
			%>
				<td class="reportHeaderCellCenter">#</td>
		    	<td class="reportHeaderCell" >Tipo</td>
		    	<td class="reportHeaderCell" >Clave/Cliente</td>
		    	<td class="reportHeaderCell" >Raz&oacute;n Social</td>
		    	<td class="reportHeaderCell" ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getTitleId()))%></td>
			    	<!--
		    	<td class="reportHeaderCell" >Nombre</td>
		    	<td class="reportHeaderCell" >Apellido Paterno</td>
		    	<td class="reportHeaderCell" >Apellido Materno</td>
		    	-->
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getPhone())) { %>
		    			<td class="reportHeaderCell" ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getPhone())) %></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getMobile())) { %>
		    			<td class="reportHeaderCell" ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getMobile())) %></td>
		    	<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getEmail())) { %>
		    			<td class="reportHeaderCell" ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getEmail())) %></td>
	    		<%	} %>
	    		<%	if (sFParams.isFieldEnabled(bmoCustomer.getBirthdate())) { %>
		    			<td class="reportHeaderCell" ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getBirthdate())) %></td>
				<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getPosition())) { %>
		    			<td class="reportHeaderCell" ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getPosition())) %></td>
	    		<%	} %>
	    		<!-- 
	    		Tel&eacute;fono
	    		M&oacute;vil
	    		Email
	    		Fecha Nac.
	    		Cargo
	    		 -->
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getSalesmanId())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getSalesmanId())%></td>
	    		<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getRfc())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getRfc())%></td>
	    		<%	} %>
	    		<!--
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getEstablishmentDate())) { %>
		    			<td class="reportHeaderCell" >Fecha Creaci&oacute;n</td>
	    		<%	//} %>
	    		-->
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getCurrencyId())) { %>
		    			<td class="reportHeaderCell" >Moneda</td>
	    		<%	} %>
	    		<!--
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getTerritoryId())) { %>
		    			<td class="reportHeaderCell" >Territorio</td>
		    	<%	//} %>
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getRegionId())) { %>
		    			<td class="reportHeaderCell" >Regi&oacute;n</td>-->
	    		<%	//} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getReferralId())) { %>
		    			<td class="reportHeaderCell" ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getReferralId())) %></td>
	    		<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getReferralComments())) { %>
		    			<td class="reportHeaderCell" ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getReferralComments())) %></td>
	    		<%	} %>
	    		<!--
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getRecommendedBy())) { %>
		    			<td class="reportHeaderCell" >Recomendado Por</td>
	    		<%	//} %>
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getIndustryId())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getIndustryId())%></td>
	    		<%	//} %>
	    		-->
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getCurp())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getCurp())%></td>
	    		<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getNss())) { %>
		    			<td class="reportHeaderCell" ><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getNss())%></td>
	    		<%	} %>
	    		<%	if (sFParams.isFieldEnabled(bmoCustomer.getDateCreate())) { %>
	    		 		<td class="reportHeaderCell" ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getDateCreate()))%></td>
	    		<%	} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getMaritalStatusId())) { %>
		    			<td class="reportHeaderCell" >Est. Civil</td>
	    		<%	} %>
	    		<!--
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getParentId())) { %>
		    			<td class="reportHeaderCell" >Empleador</td>
	    		<%	//} %>
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getIncome())) { %>
		    			<td class="reportHeaderCell" >Ingresos</td>
	    		<%	//} %>
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getConsultingServiceId())) { %>
		    			<td class="reportHeaderCell" >Servicios Por.</td>
	    		<%	//} %>
		    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getRating())) { %>
		    			<td class="reportHeaderCell" >Rating</td>
	    		<%	} %>
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getCreditLimit())) { %>
		    			<td class="reportHeaderCell" >L&iacute;mite cr&eacute;dito</td>
	    		<%	//} %>
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getWww())) { %>
		    			<td class="reportHeaderCell" >Sitio Web</td>
	    		<%	//} %>
		    	<%	//if (sFParams.isFieldEnabled(bmoCustomer.getReqPayTypeId())) { %>
		    			<td class="reportHeaderCell" >T&eacute;rmino Pago</td>  
	    		<%	//} %>
	    		-->
		    	<td class="reportHeaderCell" >Estatus</td>
			</tr>

		    <% 
		    
		    pmConnGroup.doFetch(sql);
			int i = 1;
	        while(pmConnGroup.next()) {
	        	bmoCustomer.getMaritalStatusId().setValue(pmConnGroup.getString("cust_maritalstatusid"));
	        	bmoCustomer.getCustomertype().setValue(pmConnGroup.getString("cust_customertype"));
	        	bmoCustomer.getStatus().setValue(pmConnGroup.getString("cust_status"));
				%>
				<tr class="reportCellEven">
					<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoCustomer.getCustomertype().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_code") + " " + pmConnGroup.getString("cust_displayname"), BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_legalname"), BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("titl_name"), BmFieldType.STRING)) %>
					<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_firstname"), BmFieldType.STRING)) %>
					<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_fatherlastname"), BmFieldType.STRING)) %>
					<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_motherlastname"), BmFieldType.STRING)) %>
					
			    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getPhone())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_phone"), BmFieldType.PHONE) %>
					<%	} %>
			    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getMobile())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_mobile"), BmFieldType.PHONE) %>
					<%	} %>
			    	<%	if (sFParams.isFieldEnabled(bmoCustomer.getEmail())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_email"), BmFieldType.EMAIL)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getBirthdate())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_birthdate"), BmFieldType.DATE) %>
				<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getPosition())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_position"), BmFieldType.STRING)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getSalesmanId())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("user_code"), BmFieldType.CODE)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getRfc())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_rfc"), BmFieldType.RFC)) %>
					<%	} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getEstablishmentDate())) { %>
							<%//= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_establishmentdate"), BmFieldType.DATE) %>
					<%	//} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getCurrencyId())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cure_code"), BmFieldType.CODE)) %>
					<%	} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getTerritoryId())) { %>
							<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("terr_name"), BmFieldType.STRING)) %>
					<%	//} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getRegionId())) { %>
							<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("regi_name"), BmFieldType.STRING)) %>
					<%	//} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getReferralId())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("refe_name"), BmFieldType.STRING)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getReferralComments())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_referralcomments"), BmFieldType.STRING)) %>
					<%	} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getRecommendedBy())) { %>
							<%
//							String recommendedBy = "";
//							sql = "SELECT cust_displayname FROM customers WHERE cust_customerid = " +pmConnGroup.getInt("cust_recommendedby");
//							pmConnExtra.doFetch(sql);
//							if (pmConnExtra.next()) recommendedBy = pmConnExtra.getString("cust_displayname");
							%>
							<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + recommendedBy, BmFieldType.STRING)) %>
					<%	//} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getIndustryId())) { %>
							<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("indu_name"), BmFieldType.STRING)) %>
					<%	//} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getCurp())) { %>
							<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_curp"), BmFieldType.STRING)) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getNss())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_nss"), BmFieldType.STRING) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getDateCreate())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_datecreate").substring(0, 10), BmFieldType.STRING) %>
					<%	} %>
					<%	if (sFParams.isFieldEnabled(bmoCustomer.getMaritalStatusId())) { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("mast_name"), BmFieldType.STRING) %>
					<%	} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getParentId())) { %>
							<%
//							String parent = "";
//							sql = "SELECT cust_code, cust_displayname FROM customers WHERE cust_customerid = " + pmConnGroup.getInt("cust_parentid");
//							pmConnExtra.doFetch(sql);
//							if (pmConnExtra.next()) parent = pmConnExtra.getString("cust_code") + " " + pmConnExtra.getString("cust_displayname");
							%>
							<%//= HtmlUtil.formatReportCell(sFParams, "" + parent, BmFieldType.STRING) %>
					<%	//} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getIncome())) { %>
							<%//= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_income"), BmFieldType.CURRENCY) %>
					<%	//} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getConsultingServiceId())) { %>
							<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cose_name"), BmFieldType.STRING)) %>
					<%	//} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getRating())) { %>
							<%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_rating"), BmFieldType.STRING)) %>
					<%	//} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getCreditLimit())) { %>
							<%//= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_creditlimit"), BmFieldType.CURRENCY) %>
					<%	//} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getWww())) { %>
							<%//= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("cust_www"), BmFieldType.WWW) %>
					<%	//} %>
					<%	//if (sFParams.isFieldEnabled(bmoCustomer.getReqPayTypeId())) { %>
							<%//= HtmlUtil.formatReportCell(sFParams, "" + pmConnGroup.getString("rqpt_name"), BmFieldType.STRING) %>
					<%	//} %>
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoCustomer.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		</tr>
	   			<%	
   				i++;
	        } // pmConnGroup
	
			          %>
			
		</table> 
	<% 
		
	}//Fin de validacion de 5 mil registros
	
			
%> 
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% }
			}// Fin de if(no cargar datos)
	pmConnExtra.close();
	pmConnGroup.close();
	pmConnCount.close();
	%>
	
  </body>
</html>