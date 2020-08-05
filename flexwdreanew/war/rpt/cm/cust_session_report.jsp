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
<%@page import="com.flexwm.server.ac.*"%>
<%@page import="com.flexwm.shared.ac.*"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>
<% 

	try {

	// Inicializar variables
 	String title = "Reportes de Clientes General";
	
   	String filters = "", sql = "", where = "", whereDate = "", customerType = "", status = "", startDate = "", endDate = "", birthdate = "", birthdateEnd = "";
	int rows = 0, programId = 0, salesman = 0, referralId = 0, territoryId = 0, countOrderTypes = 0, industryId = 0, birthdateByMonth = 0;
   	
   	   	
   	
   	PmSession pmSession = new PmSession(sFParams);
   	BmoSession bmoSession = new BmoSession();
   	
   	int userId = 0, sessionTypeId = 0;
   	
   
 // Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("cust_customertype") != null) customerType = request.getParameter("cust_customertype");
    if (request.getParameter("cust_salesmanid") != null) salesman = Integer.parseInt(request.getParameter("cust_salesmanid"));
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
    if (request.getParameter("cust_territoryid") != null) territoryId = Integer.parseInt(request.getParameter("cust_territoryid"));    
    if (request.getParameter("cust_status") != null) status = request.getParameter("cust_status");
    if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid"));
    if (request.getParameter("cust_datecreate") != null) startDate = request.getParameter("cust_datecreate");
    if (request.getParameter("dateCreateEnd") != null) endDate = request.getParameter("dateCreateEnd");
    if (request.getParameter("cust_birthdate") != null) birthdate = request.getParameter("cust_birthdate");
   	if (request.getParameter("birthdateByMonth") != null) birthdateByMonth = Integer.parseInt(request.getParameter("birthdateByMonth"));
   	if (request.getParameter("birthdateEnd") != null) birthdateEnd = request.getParameter("birthdateEnd");
   	
   	
    //Filtros Listados
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
    String disclosureFilters = new PmCustomer(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
   
    
  	
    
    PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	 //abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
    pmConnCount.open();
    
    sql = "  SELECT COUNT(*) as contador FROM  " + SQLUtil.formatKind(sFParams, "customers ") +  	      
    		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customeremails")+" ON (cuem_customerid = cust_customerid) " +
    		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customerphones")+" ON (cuph_customerid = cust_customerid) " +
    	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (cust_referralid = refe_referralid) " +  		  	
  		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
  		  " WHERE cust_customerid > 0 " + where + 
  		  " ORDER BY cust_customerid ASC";
  		  
  		int count =0;
		//ejecuto el sql
		pmConnCount.doFetch(sql);
		if(pmConnCount.next())
			count=pmConnCount.getInt("contador");
		System.out.println("contador de reporetes --> "+count);
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
%>

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
if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
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
		<td class="reportTitle" align="left">
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
<%//if que muestra el mensajede error
if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
	%>
	
<%=messageTooLargeList %>
	<% 
}else{ 
	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "customers ") +  	      
	  		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customeremails")+" ON (cuem_customerid = cust_customerid) " +
	  		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customerphones")+" ON (cuph_customerid = cust_customerid) " +
	  	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (cust_referralid = refe_referralid) " +  		  	
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
			  " WHERE cust_customerid > 0 " + where + 
			  " ORDER BY cust_customerid ASC";
%>
<p>
<table class="report">

	<tr class="">
		<td class="reportHeaderCellCenter">#</td>
		<td class="reportHeaderCell">Cliente</td>
		<td class="reportHeaderCell">Email</td>
		<td class="reportHeaderCell">Tel&eacute;fono</td>
		<td class="reportHeaderCell">Como se Entero</td>
		<td class="reportHeaderCell">Fecha</td>
		<td class="reportHeaderCell">Observaci&oacute;n</td>					
	</tr>
		
<%
	int i = 1;
	String start = "";
	pmConn.doFetch(sql);	
	while (pmConn.next()) { 
%>

		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers","cust_displayname"), BmFieldType.STRING)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("customeremails","cuem_email"), BmFieldType.STRING)) %>
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("customerphones","cuph_number"), BmFieldType.STRING) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("referrals","refe_name"), BmFieldType.STRING)) %>
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers","cust_datecreate").substring(0,10), BmFieldType.DATE) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers","cust_referralcomments"), BmFieldType.STRING)) %>
		</tr>
<%	
	i++;
	}

%>	
	<tr class="">
		<td colspan="4">&nbsp;</td>
	</tr>
	
<%  
	}//Fin de validacion de 5 mil registros
%>
</p>


<%
	
%>
</table>
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
}
pmConn.close();
pmConnCount.close();
	%>

<%
	} catch (Exception e) {
		
	%>
	
		<%= e.toString() %>
	
	<%
		
	}
%>

<p>&nbsp;</p>
 
  </body>
</html>