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
 

<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="javax.script.*"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%@include file="/inc/login.jsp" %>
<%
	// Inicializar variables
 	String title = "Reporte de Cumpleaños Clientes";
	//bmoUser = new BmoUser();
	BmoCustomer bmoCustomer =new BmoCustomer();

   	String sql = "", where = "", filters = "", customerType = "";
   	String status = "", birthdate = "", birthdateEnd = "", areaId = "", locationdId = "", startDate = "", endDate = "";   	
   	int programId = 0, companyId = 0, parentId = 0, startprogramId = 0, birthdateByMonth = 0, industryId = 0, salesman = 0, referralId = 0, territoryId = 0;

   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("cust_status") != null) status = request.getParameter("cust_status");
   	if (request.getParameter("cust_birthdate") != null) birthdate = request.getParameter("cust_birthdate");
   	if (request.getParameter("birthdateByMonth") != null) birthdateByMonth = Integer.parseInt(request.getParameter("birthdateByMonth"));
   	if (request.getParameter("birthdateEnd") != null) birthdateEnd = request.getParameter("birthdateEnd");
   	if (request.getParameter("cust_customertype") != null) customerType = request.getParameter("cust_customertype");
    if (request.getParameter("cust_salesmanid") != null) salesman = Integer.parseInt(request.getParameter("cust_salesmanid"));
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
    if (request.getParameter("cust_territoryid") != null) territoryId = Integer.parseInt(request.getParameter("cust_territoryid"));    
    if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid"));
    if (request.getParameter("cust_datecreate") != null) startDate = request.getParameter("cust_datecreate");
    if (request.getParameter("dateCreateEnd") != null) endDate = request.getParameter("dateCreateEnd");
  
	// Filtros listados
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
	if (!status.equals("")) {
		where += SFServerUtil.parseFiltersToSql("cust_status", status);
		filters += "<i>Estatus: </i>" + request.getParameter("cust_statusLabel") + ", ";
   	}
	if (!startDate.equals("")) {
    	where += " AND cust_datecreate >= '" + startDate + " 00:00'";
    	filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
    }
    
    if (!endDate.equals("")) {
    	where += " AND cust_datecreate <= '" + endDate + " 23:59'";
    	filters += "<i>Fecha Fin: </i>" + endDate + ", ";
    }
	// Para usuarios por mes de fecha de nacimiento
 	filters += "<i>Fecha Nac. por mes: </i>" + request.getParameter("birthdateByMonthLabel") + ", ";
	if (birthdateByMonth == 1){
 		if (!birthdate.equals("")) {
 			where += " AND DATE_FORMAT(cust_birthdate, '%m-%d') >= '" + birthdate.substring(5, 10) + "'";
 			filters += "<i>F. Nacimiento: </i>" + request.getParameter("cust_birthdate") + ", ";
 	    }
 	    
 	    if (!birthdateEnd.equals("")) {
 	    	where += " AND DATE_FORMAT(cust_birthdate, '%m-%d') <= '" + birthdateEnd.substring(5, 10) + "'";
 	    	filters += "<i>F. Nacimiento Fin: </i>" + request.getParameter("birthdateEnd") + ", ";
 	    }
 	}else{
 		if (!birthdate.equals("")) {
 			where += " AND cust_birthdate >= '" + birthdate + "'";
 			filters += "<i>F. Nacimiento: </i>" + request.getParameter("cust_birthdate") + ", ";
 	    }
 	    
 	    if (!birthdateEnd.equals("")) {
 	    	where += " AND cust_birthdate <= '" + birthdateEnd + "'";
 	    	filters += "<i>F. Nacimiento Fin: </i>" + request.getParameter("birthdateEnd") + ", ";
 	    }
     }

	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
	
	
    // Obtener disclosure de datos
    String disclosureFilters = new PmUser(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
  
		  
	
	
	
	//Conexiones
   	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	pmConn.doFetch(sql);
	
	//PmCustomer pmCustomer = new PmCustomer(sFParams);
	PmConn pmConnParent = new PmConn(sFParams);
	pmConnParent.open();
	
	  //abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
    pmConnCount.open();
		  //consulta SQL
		  sql = " SELECT COUNT(*) as contador FROM " + SQLUtil.formatKind(sFParams, "customers") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on(user_userid = cust_salesmanid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " titles")+" on(titl_titleid = cust_titleid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " territories")+" on(terr_territoryid = cust_territoryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " consultingservices")+" on(cose_consultingserviceid = cust_consultingserviceid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " regions")+" on(regi_regionid = cust_regionid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" on(refe_referralid = cust_referralid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on(cure_currencyid = cust_currencyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" on(indu_industryid = cust_industryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " reqpaytypes")+" ON (rqpt_reqpaytypeid = cust_reqpaytypeid) " +
		    " WHERE cust_customerid > 0 " +
			where +
			" ORDER BY cust_customerid ASC";
		    
		    int count =0;
			//ejecuto el sql
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			System.out.println("contador de reportes - "+count);
	
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
					<b>Filtros:</b> <%= filters %>
					<br>			
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
			sql = "SELECT * " +
					" FROM  " + SQLUtil.formatKind(sFParams, "customers") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on(user_userid = cust_salesmanid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " titles")+" on(titl_titleid = cust_titleid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " territories")+" on(terr_territoryid = cust_territoryid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " consultingservices")+" on(cose_consultingserviceid = cust_consultingserviceid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " regions")+" on(regi_regionid = cust_regionid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" on(refe_referralid = cust_referralid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on(cure_currencyid = cust_currencyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" on(indu_industryid = cust_industryid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " reqpaytypes")+" ON (rqpt_reqpaytypeid = cust_reqpaytypeid) " +
				    " WHERE cust_customerid > 0 " +
					where +
					" ORDER BY cust_customerid ASC";
				    pmConn.doFetch(sql);
		%>
		<br>
		<TABLE class="report" border="0">
		<tr class= "">
		<td class="reportHeaderCell">#</td>
		<%if(sFParams.isFieldEnabled(bmoCustomer.getCode())) {%>
		<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getCode()))%></td>
		<% }%>
		<td class="reportHeaderCell">Nombre</td>
		
		<%if(sFParams.isFieldEnabled(bmoCustomer.getBirthdate())){%>
		<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getBirthdate()))%></td>
		<% }%>
		<td class="reportHeaderCellCenter">Edad</td>	
		<%if(sFParams.isFieldEnabled(bmoCustomer.getEmail())){%>
		<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getEmail()))%></td>
		<% }%>
		<%if(sFParams.isFieldEnabled(bmoCustomer.getPhone())){%>
		<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getPhone()))%></td>
		<% }%>
		<%if(sFParams.isFieldEnabled(bmoCustomer.getMobile())){%>
		<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getMobile()))%></td>
		<% }%>
		
		<%if(sFParams.isFieldEnabled(bmoCustomer.getStatus())){%>
		<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getStatus()))%></td>
		<% }%>
		
		</tr>
		
		<%
		int i= 1;
		while(pmConn.next()){
			//bmoCustomer.getStatus().setValue(pmConn.getString("customers, cust_status"));
		%>
		<tr class ="reporCellEven">
		<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
		<%if(sFParams.isFieldEnabled(bmoCustomer.getCode())){%>
		<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers", "cust_code"), BmFieldType.CODE) %>
		<% }%>
		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("cust_displayname"), BmFieldType.STRING)) %>

		<%if(sFParams.isFieldEnabled(bmoCustomer.getBirthdate())){
		%>
		<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers", "cust_birthdate"), BmFieldType.STRING) %>
		<% }%>
		<%	
			//Calcular la edad
			int custAge = 0;
			if (!pmConn.getString("customers","cust_birthdate").equals(""))
				custAge = SFServerUtil.daysBetween(sFParams.getDateFormat(), pmConn.getString("customers","cust_birthdate") , SFServerUtil.nowToString(sFParams, sFParams.getDateFormat())) / 365; 
		%>
		<%= HtmlUtil.formatReportCell(sFParams, "" + custAge, BmFieldType.NUMBER) %>
		<%if(sFParams.isFieldEnabled(bmoCustomer.getEmail())){%>
		<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers", "cust_email"), BmFieldType.STRING) %>
		<% }%>
		<%if(sFParams.isFieldEnabled(bmoCustomer.getPhone())){%>
		<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers", "cust_phone"), BmFieldType.STRING) %>
		<% }%>
		<%if(sFParams.isFieldEnabled(bmoCustomer.getMobile())){%>
		<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers", "cust_mobile"), BmFieldType.STRING) %>
		<% }%>
	
		<%if(sFParams.isFieldEnabled(bmoCustomer.getStatus())){%>
		<% bmoCustomer.getStatus().setValue(pmConn.getString("customers", "cust_status")); %>
		<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoCustomer.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
		
		<% }%>
		
		<tr>
		<%
		i++;
		}
		%>
		</TABLE>
<%

	}//Fin de validacion de 5 mil registros
 
  
%>  
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	}// Fin de if(no carga datos)
	 pmConn.close();
	   pmConnParent.close();
	   pmConnCount.close();
	%>
  </body>
</html>