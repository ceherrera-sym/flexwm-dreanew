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
<% 

	try {

	// Imprimir
	String print = "0";
	if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
	if (exportExcel.equals("1")) {
		response.setContentType("application/vnd.ms-excel");
    	response.setHeader("Content-Disposition", "inline; filename=symgae_report.xls");
    }
%>

<%
	// Inicializar variables
 	String title = "Calendario de Sesiones";
	
   	String sql = "", where = "";
   	String filters = "";
   	int programId = 0;
   	int sessionId = 0;
   	int equipmentId = 0;
   	int rows = 0;

   	String startDate = "", startYear = "", startMonth = "", startDay = "", startHour = "", startMinute = "", startString = "";
	String endDate = "", endYear = "", endMonth = "", endDay = "", endHour = "", endMinute = "", endString = "";
   	
   	PmSession pmSession = new PmSession(sFParams);
   	BmoSession bmoSession = new BmoSession();
   	String sessionStartDate = "", sessionStartYear = "", sessionStartMonth = "", sessionStartDay = "", sessionStartHour = "", sessionStartMinute = "", sessionStartString = "";
	String sessionEndDate = "", sessionEndYear = "", sessionEndMonth = "", sessionEndDay = "", sessionEndHour = "", sessionEndMinute = "", sessionEndString = "";
   
   	// Obtener parametros
   	if (request.getParameter("equipmentId") != null) equipmentId = Integer.parseInt(request.getParameter("equipmentId"));
	if (request.getParameter("startDate") != null) startDate = request.getParameter("startDate");
	if (request.getParameter("endDate") != null) endDate = request.getParameter("endDate");
	
	// Asignar horas default a startDate y endDate
	if (!startDate.equals("")) startDate += " 00:00:00";
	if (!endDate.equals("")) endDate += " 23:59:00";

	// Construir filtros 
/*    	if (orderId > 0) {
   		bmoSession = (BmoSession)pmSession.get(sessionId);
   				 	
		// Fechas de intervalo
		if (startDate.equals("")) startDate = bmoSession.getLockStart().toString();
		if (endDate.equals("")) endDate = bmoSession.getLockEnd().toString();

		// Fechas sessiono
		sessionStartYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockStart().toString()).get(Calendar.YEAR);
		sessionStartMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockStart().toString()).get(Calendar.MONTH) + 1);
		sessionStartDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockStart().toString()).get(Calendar.DAY_OF_MONTH);		 	
		sessionStartHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockStart().toString()).get(Calendar.HOUR_OF_DAY);		 	
		sessionStartMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockStart().toString()).get(Calendar.MINUTE);		 	
		sessionStartString = bmoSession.getLockStart().toString();
		
		sessionEndYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockEnd().toString()).get(Calendar.YEAR);
		sessionEndMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockEnd().toString()).get(Calendar.MONTH) + 1);
		sessionEndDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockEnd().toString()).get(Calendar.DAY_OF_MONTH);
		sessionEndHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockEnd().toString()).get(Calendar.HOUR_OF_DAY);		 	
		sessionEndMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoSession.getLockEnd().toString()).get(Calendar.MINUTE);
		sessionEndString = bmoSession.getLockEnd().toString();
   		
		filters += "<i>Pedido: </i>" + bmoSession.getCode().toString() + " - " + bmoSession.getName().toString() + " <br> ";
   	} */
   	
   	// Arreglo fechas de intervalo
	/* startYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.YEAR);
	startMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.MONTH) + 1);
	startDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.DAY_OF_MONTH);		 	
	startHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.HOUR_OF_DAY);		 	
	startMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.MINUTE);		 	
	startString = startYear + "-" + startMonth + "-" + startDay + " 00:00:00";
	
	endYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.YEAR);
	endMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.MONTH) + 1);
	endDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.DAY_OF_MONTH);
	endHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.HOUR_OF_DAY);		 	
	endMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), endDate).get(Calendar.MINUTE);
	endString = endYear + "-" + endMonth + "-"+ endDay + " 23:59:00";
	
	filters += "<i>Intervalo de Fechas: </i>" + startString + " a " + endString + " "; */
	
	where += " AND ( "
			+ " ('" + startDate + "' BETWEEN orde_lockstart AND orde_lockend) " 
		 	+ " OR  ('" + endDate + "' BETWEEN orde_lockstart AND orde_lockend) " 
		 	+ " OR  (orde_lockstart BETWEEN '" + startDate + "' AND '" + endDate + "') " 
		 	+ " OR  (orde_lockend BETWEEN '" + startDate + "' AND '" + endDate + "') " 
		 	+ " ) ";
   	
   	sql = 	" select * from users ";
			 
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	pmConn.doFetch(sql);
%>


<html>
<head>
	<title>:::<%= appTitle %>:::</title>
		<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
	
	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load("current", {packages:["calendar"]});
      google.charts.setOnLoadCallback(drawChart);

		   function drawChart() {
		       var dataTable = new google.visualization.DataTable();
		       dataTable.addColumn({ type: 'date', id: 'Fecha' });
		       dataTable.addColumn({ type: 'number', id: 'Sesiones' });
		       dataTable.addRows([
		          [ new Date(2016, 9, 4), 2 ],
		          [ new Date(2016, 9, 5), 3 ],
		          [ new Date(2016, 9, 12), 4 ],
		          [ new Date(2016, 9, 13), 3 ],
		          [ new Date(2016, 9, 19), 2 ],
		          [ new Date(2016, 9, 23), 4 ],
		          [ new Date(2016, 9, 24), 3 ],
		          [ new Date(2016, 9, 30), 4 ]
		        ]);
		
		       var chart = new google.visualization.Calendar(document.getElementById('calendar_basic'));
		
		       var options = {
		         title: "Sesiones por Dia"
		       };
		
		       chart.draw(dataTable, options);
		   }
    </script>
</head>

<body class="default" onload="self.focus();">

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
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>

<p>&nbsp;</p>

	<div id="calendar_basic" style="width: 100%; height: 100%;"></div>

<p>&nbsp;</p>


<% 
	pmConn.close();
%>
	<% if (print.equals("1")) { %>
	<script>
		window.print();
	</script>
	<% } %>

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