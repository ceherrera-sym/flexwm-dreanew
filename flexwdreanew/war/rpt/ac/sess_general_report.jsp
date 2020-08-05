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
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>
<% 

	try {

	// Inicializar variables
 	String title = "Reporte de Sesiones General";
	
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
		whereDate += " AND sess_startdate >= '" + startDate + " 00:00' ";
   		filters += "<i>Fecha Inicio: </i>" + request.getParameter("sess_startdate") + ", ";
   	}
   	
    if (!endDate.equals("")) {
		whereDate += " AND sess_enddate <= '" + endDate + " 23:59' ";
   		filters += "<i>Fecha Fin: </i>" + request.getParameter("sess_enddate") + ", ";
   	}
    
    PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	PmConn pmConn2 = new PmConn(sFParams);
	pmConn2.open();
   	
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
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
		<td class="reportHeaderCellCenter">#</td>
		<td class="reportHeaderCell">Cliente</td>
		<td class="reportHeaderCell">Inscripci&oacute;n</td>
		<td class="reportHeaderCell">Tipo Sesi&oacute;n</td>
		<td class="reportHeaderCell">Sesiones</td>
		<td class="reportHeaderCell">Horarios</td>
		<td class="reportHeaderCell">Nacimiento</td>
		<td class="reportHeaderCell">Edad</td>		
		<td class="reportHeaderCell">Padre</td>
		<td class="reportHeaderCell">Madre</td>
		<td class="reportHeaderCell">Celular</td>
		<td class="reportHeaderCell">Tel&eacute;fono</td>
		<td class="reportHeaderCell">Email</td>
		<td class="reportHeaderCellRight">Mesualidad</td>
		<td class="reportHeaderCell">Referencia</td>
		<td class="reportHeaderCell">Firmo</td>
		<td class="reportHeaderCell">Im&aacute;genes</td>
		
	</tr>
<%

//abro conexion para inciar el conteo

sql = 	"  SELECT COUNT(*) as contador FROM  "+ SQLUtil.formatKind(sFParams,"sessionsales ") +   			
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (sesa_customerid = cust_customerid) " +
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (cust_referralid = refe_referralid) " +
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " sessiontypepackages")+" ON (sesa_sessiontypepackageid = setp_sessiontypepackageid) " + 
" WHERE sesa_status <> '" + BmoSessionSale.STATUS_CANCELLED + "'" +
where +
" AND (sesa_sessiondemo <> 1 OR sesa_sessiondemo is null) " +   	          
" ORDER BY sesa_startdate";	

int count =0;
//ejecuto el sql
pmConnCount.doFetch(sql);
if(pmConnCount.next())
	count=pmConnCount.getInt("contador");
System.out.println("contador"+count);	
//if que muestra el mensajede error
if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
	%>
	
<%=messageTooLargeList %>
	<% 
}else{

	sql = 	" SELECT * FROM "+ SQLUtil.formatKind(sFParams,"sessionsales ") +   			
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (sesa_customerid = cust_customerid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (cust_referralid = refe_referralid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " sessiontypepackages")+" ON (sesa_sessiontypepackageid = setp_sessiontypepackageid) " + 
	        " WHERE sesa_status <> '" + BmoSessionSale.STATUS_CANCELLED + "'" +
			where +
	        " AND (sesa_sessiondemo <> 1 OR sesa_sessiondemo is null) " +   	          
	        " ORDER BY sesa_startdate";	
	pmConn.doFetch(sql);


	int i = 1;
	String start = "";
	pmConn.doFetch(sql);	
	while (pmConn.next()) {
%>
		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers","cust_code") + " " + pmConn.getString("customers","cust_displayname"), BmFieldType.STRING)) %>
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("sessionsales","sesa_startdate").substring(0,10), BmFieldType.DATETIME) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("sessiontypepackages","setp_name"), BmFieldType.STRING)) %>
			<%
				Calendar cal = Calendar.getInstance();
				//Obtener los dias de las sessiones
				String sessionDays = "";
				String sessionTime = "";
				sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"ordersessions ") +
				      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " sessions")+" ON (orss_sessionid = sess_sessionid) " +
					  " WHERE orss_orderid = " + pmConn.getInt("sesa_orderid");
				pmConn2.doFetch(sql);
				while (pmConn2.next()) {
					cal = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), pmConn2.getString("sessions", "sess_startdate"));
					if (cal.get(cal.DAY_OF_WEEK) == 0) {
						sessionDays += "Dom,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 1) {
						sessionDays += "Lun,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 2) {
						sessionDays += "Mar,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 3) {
						sessionDays += "Mie,";					 
					} else if (cal.get(cal.DAY_OF_WEEK) == 4) {
						sessionDays += "Jue,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 5) {
						sessionDays += "Vie,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 6) {
						sessionDays += "Sab,";
					}	
					
					sessionTime += pmConn2.getString("sessions", "sess_startdate").substring(10); 
				}		
			%>
			<%= HtmlUtil.formatReportCell(sFParams, sessionDays, BmFieldType.STRING) %>
			<%= HtmlUtil.formatReportCell(sFParams, sessionTime, BmFieldType.STRING) %>
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers","cust_birthdate"), BmFieldType.DATETIME) %>
			<%	
				//Calcular la edad
				int custAge = 0;
				if (!pmConn.getString("customers","cust_birthdate").equals(""))
					custAge = SFServerUtil.daysBetween(sFParams.getDateFormat(), pmConn.getString("customers","cust_birthdate") , SFServerUtil.nowToString(sFParams, sFParams.getDateFormat())) / 365; 
			
			%>
			<%= HtmlUtil.formatReportCell(sFParams, "" + custAge, BmFieldType.NUMBER) %>
			<%
				//Obtener el padre y Madre
				String fatherName = "";
				String motherName = "";
				
				sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"customerrelatives ") + 
				      " WHERE curl_customerid = " + pmConn.getInt("cust_customerid") +
				      " AND curl_type = '" + BmoCustomerRelative.TYPE_FATHER + "'";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) { 
					fatherName = pmConn2.getString("customerrelatives", "curl_fullName") + " " + pmConn2.getString("customerrelatives", "curl_fatherlastname"); 
				}
				
				sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"customerrelatives ") + 
				      " WHERE curl_customerid = " + pmConn.getInt("cust_customerid") +
				      " AND curl_type = '" + BmoCustomerRelative.TYPE_MOTHER + "'";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) { 
					motherName = pmConn2.getString("customerrelatives", "curl_fullName") + " " + pmConn2.getString("customerrelatives", "curl_fatherlastname"); 
				}
				
				//Reponsible
				String cellPhone = "";
				String phone = "";
				String email = "";
				sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"customerrelatives ") + 
					  " WHERE curl_customerid = " + pmConn.getInt("cust_customerid") +
					  " AND curl_responsible = 1 ";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) { 
					cellPhone = pmConn2.getString("customerrelatives", "curl_cellphone");
					phone = pmConn2.getString("customerrelatives", "curl_number");
					email = pmConn2.getString("customerrelatives", "curl_email");
				}
			%>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, fatherName, BmFieldType.STRING)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, motherName, BmFieldType.STRING)) %>
			<%= HtmlUtil.formatReportCell(sFParams, cellPhone, BmFieldType.STRING) %>
			<%= HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, email, BmFieldType.STRING)) %>
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("sessiontypepackages","setp_saleprice"), BmFieldType.CURRENCY) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers","cust_referralcomments"), BmFieldType.STRING)) %>
			<%
				String letter = "No", photo = "No";
				if (pmConn.getInt("sesa_signletter") > 0) letter = "Si";
				if (pmConn.getInt("sesa_takephoto") > 0) photo = "Si";
			
			%>
			<%= HtmlUtil.formatReportCell(sFParams, letter, BmFieldType.STRING) %>
			<%= HtmlUtil.formatReportCell(sFParams, photo, BmFieldType.STRING) %>
		</tr>
<%		i++;
	}
%>	
<tr class="">
  <td colspan="4">&nbsp;</td>
</tr>
  
</p>


<%
	}//Fin de validacion de  contador x  registros
	
%>
</table>
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
}
pmConn2.open();
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