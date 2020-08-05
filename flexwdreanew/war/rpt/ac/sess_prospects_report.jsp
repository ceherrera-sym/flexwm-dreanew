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
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>
<% 

	try {

	// Inicializar variables
 	String title = "Reporte de Sesiones Prueba";
	System.out.println("sadsadsadsadsadsadasdsa");
   	String sql = "", where = "", whereSess = "", whereDate = "";
   	String filters = "";
   	int programId = 0;
   	int rows = 0;   	
   	
   	PmSession pmSession = new PmSession(sFParams);
   	BmoSession bmoSession = new BmoSession();
   	String startDate = "", endDate = "";
   	int userId = 0, sessionTypeId = 0, customerId = 0;
   	
   
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("sess_startdate") != null) startDate = request.getParameter("sess_startdate");
	if (request.getParameter("sess_enddate") != null) endDate = request.getParameter("sess_enddate");
	if (request.getParameter("sess_userid") != null) userId = Integer.parseInt(request.getParameter("sess_userid"));
	if (request.getParameter("sess_sessiontypeid") != null) sessionTypeId = Integer.parseInt(request.getParameter("sess_sessiontypeid"));
	if (request.getParameter("customerid") != null) customerId = Integer.parseInt(request.getParameter("customerid"));
	
	// Construir filtros 	
   	if (userId > 0) {
   		whereSess += " AND sess_userid = " + userId;
   		filters += "<i>Instructor: </i>" + request.getParameter("sess_useridLabel") + ", ";
   	}
   	
   	if (customerId > 0) {
		where += " AND sesa_customerid = " + customerId;
   		filters += "<i>Cliente: </i>" + request.getParameter("customeridLabel") + ", ";
   	}
   	
   	if (sessionTypeId > 0) {
   		whereSess += " AND sess_sessiontypeid = " + sessionTypeId;
   		filters += "<i>Tipo Sesi&oacute;n: </i>" + request.getParameter("sess_sessiontypeidLabel") + ", ";
   	}
   	
   	
   	if (!startDate.equals("")) {
		whereDate += " AND sesa_startdate >= '" + startDate + " 00:00' ";
   		filters += "<i>Fecha Inicio: </i>" + request.getParameter("sess_startdate") + ", ";
   	}
   	
    if (!endDate.equals("")) {
		whereDate += " AND sesa_enddate <= '" + endDate + " 23:59' ";
   		filters += "<i>Fecha Fin: </i>" + request.getParameter("sess_enddate") + ", ";
   	}
    
    PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	PmConn pmConn2 = new PmConn(sFParams);
	pmConn2.open();
	 //abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
    pmConnCount.open();
   	
    sql = "SELECT COUNT(*) as contador FROM "+ SQLUtil.formatKind(sFParams,"sessionsales ") +
     	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (sesa_customerid = cust_customerid) " +
     	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (cust_referralid = refe_referralid) " +
     	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (sesa_salesuserid = user_userid) " +		 
     	      " WHERE sesa_sessiondemo = 1 " + 
     	      where +
     	      whereDate +
     	      " ORDER BY sesa_sessionsaleid ";
    
    int count =0;
	//ejecuto el sql
	pmConnCount.doFetch(sql);
	if(pmConnCount.next())
		count=pmConnCount.getInt("contador");
	System.out.println("Contador de reportes -"+count);
    
   	
	
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

<p>
<table class="report">
	<tr class="">
		<td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Venta</td>		
		<td class="reportHeaderCell">Cliente</td>
		<td class="reportHeaderCell">Fecha</td>
		<td class="reportHeaderCell">Edad</td>
		<td class="reportHeaderCell">Tutor</td>
		<td class="reportHeaderCell">Telefono</td>
		<td class="reportHeaderCell">Maestro</td>
		<td class="reportHeaderCell">Atendido Por</td>
		<td class="reportHeaderCell">Como se entero</td>
		<td class="reportHeaderCell">Observaci&oacute;n</td>
	</tr>
<%
//if que muestra el mensajede error
if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
	%>
	
<%=messageTooLargeList %>
	<% 
}else{
	
	sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"sessionsales ") +
	   	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (sesa_customerid = cust_customerid) " +
	   	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (cust_referralid = refe_referralid) " +
	   	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (sesa_salesuserid = user_userid) " +		 
	   	      " WHERE sesa_sessiondemo = 1 " + 
	   	      where +
	   	      whereDate +
	   	      " ORDER BY sesa_sessionsaleid ";
		pmConn.doFetch(sql);
		
	int i = 1;
	String start = "";
	pmConn.doFetch(sql);	
	while (pmConn.next()) {		
%>	
		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.CODE) %>
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("sessionsales","sesa_code"), BmFieldType.CODE) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers","cust_displayname"), BmFieldType.STRING)) %>
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("sessionsales","sesa_startdate").substring(0,10), BmFieldType.DATE) %>
			<%
				int age = 0;
				//Calcular edad
				if (!pmConn.getString("cust_birthdate").equals(""))
					age = SFServerUtil.daysBetween(sFParams.getDateFormat(), pmConn.getString("cust_birthdate"), SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()));
					age = age/365;
			%>
			<%= HtmlUtil.formatReportCell(sFParams, "" + age, BmFieldType.NUMBER) %>
			<%
				//Obtener el Padre o Tutor
				String relative = "";
				String phones = "";
				sql  = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"customerrelatives ") +
				       " WHERE curl_customerid = " + pmConn.getInt("cust_customerid") +
			           " AND curl_responsible = 1 ";
			    pmConn2.doFetch(sql);
			    if(pmConn2.next()) {
			    	relative = pmConn2.getString("customerrelatives", "curl_fullname") + " " + pmConn2.getString("customerrelatives", "curl_fatherlastname");
			        phones = pmConn2.getString("customerrelatives", "curl_number") + "/" +pmConn2.getString("customerrelatives", "curl_cellphone") ;
			    }      
			%>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, relative, BmFieldType.STRING)) %>
			<%= HtmlUtil.formatReportCell(sFParams, phones, BmFieldType.STRING) %>			
			<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users","user_code"), BmFieldType.CODE) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("referrals","refe_name"), BmFieldType.STRING)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("sessionsales","sesa_description"), BmFieldType.STRING)) %>
										
		</tr>
<%  i++;	
	}  
%>		
	<tr class="">
		<td colspan="4">&nbsp;</td>
	</tr>
			
</p>


<%
	
	}//Fin de validacion de 5 mil registros

	
	
%>
</table>
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% }
	}
pmConn2.close();
pmConnCount.close();
pmConn.close();
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