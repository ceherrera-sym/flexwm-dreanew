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
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>
<% 
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
 	String title = "Asignaci&oacute;n y Disponibilidad de Colaboradores";
	
   	String sql = "", where = "";
   	String filters = "";
   	int programId = 0;
   	int wflowId = 0;
   	int rows = 0;
   	boolean hasEndDate = true;

   	String startDate = "", startYear = "", startMonth = "", startDay = "", startHour = "", startMinute = "", startString = "";
	String endDate = "", endYear = "", endMonth = "", endDay = "", endHour = "", endMinute = "", endString = "";
	
	//startDate = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
   	
   	PmWFlow pmWFlow = new PmWFlow(sFParams);
   	BmoWFlow bmoWFlow = new BmoWFlow();
   	String wflowStartDate = "", wflowStartYear = "", wflowStartMonth = "", wflowStartDay = "", wflowStartHour = "", wflowStartMinute = "", wflowStartString = "";
	String wflowEndDate = "", wflowEndYear = "", wflowEndMonth = "", wflowEndDay = "", wflowEndHour = "", wflowEndMinute = "", wflowEndString = "";
   
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("wflu_wflowid") != null) wflowId = Integer.parseInt(request.getParameter("wflu_wflowid"));
	if (request.getParameter("startDate") != null) startDate = request.getParameter("startDate");
	if (request.getParameter("endDate") != null) endDate = request.getParameter("endDate");
	
	// Asignar horas default a startDate y endDate
	if (!startDate.equals("")) startDate += " 00:00:00";
	if (!endDate.equals("")) endDate += " 23:59:59";

	// Construir filtros 
   	if (wflowId > 0) {
   		bmoWFlow = (BmoWFlow)pmWFlow.get(wflowId);

		// Fechas de intervalo
		if (startDate.equals("")) startDate = bmoWFlow.getStartDate().toString();
		if (endDate.equals("")) endDate = bmoWFlow.getEndDate().toString();
		
		if (bmoWFlow.getEndDate().toString().equals("")) {
		   	endDate = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()) + " 23:59:59";
			hasEndDate = false;
			bmoWFlow.getEndDate().setValue(endDate);
		}
//		System.out.println("inicio:" +startDate);
//		System.out.println("fin:" +endDate);

		// Fechas wflowo
		wflowStartYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getStartDate().toString()).get(Calendar.YEAR);
		wflowStartMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getStartDate().toString()).get(Calendar.MONTH) + 1);
		wflowStartDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getStartDate().toString()).get(Calendar.DAY_OF_MONTH);		 	
		wflowStartHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getStartDate().toString()).get(Calendar.HOUR_OF_DAY);		 	
		wflowStartMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getStartDate().toString()).get(Calendar.MINUTE);		 	
		wflowStartString = bmoWFlow.getStartDate().toString();
		wflowEndYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getEndDate().toString()).get(Calendar.YEAR);
		wflowEndMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getEndDate().toString()).get(Calendar.MONTH) + 1);
		wflowEndDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getEndDate().toString()).get(Calendar.DAY_OF_MONTH);
		wflowEndHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getEndDate().toString()).get(Calendar.HOUR_OF_DAY);		 	
		wflowEndMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoWFlow.getEndDate().toString()).get(Calendar.MINUTE);
		wflowEndString = bmoWFlow.getEndDate().toString();
		filters += "<i>Flujo: </i>" + bmoWFlow.getCode().toString() + " - " + bmoWFlow.getName().toString() + " <br> ";
   	}
   	
   	// Arreglo fechas de intervalo
	startYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), startDate).get(Calendar.YEAR);
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
	endString = endYear + "-" + endMonth + "-"+ endDay + " 23:59:59";
	
	filters += "<i>Intervalo de Fechas: </i>" + startString + " a " + endString + 
			((!hasEndDate) ? "<span font-color='red' title='(El Flujo no tiene fecha fin, se toma por defecto la fecha de hoy para fecha Fin)'><font color='red'><b> [?]</b></font></span>" : " ");
	
	where += " AND ( "
			+ " ('" + startDate + "' BETWEEN wflu_lockstart AND wflu_lockend) " 
		 	+ " OR  ('" + endDate + "' BETWEEN wflu_lockstart AND wflu_lockend) " 
		 	+ " OR  (wflu_lockstart BETWEEN '" + startDate + "' AND '" + endDate + "') " 
		 	+ " OR  (wflu_lockend BETWEEN '" + startDate + "' AND '" + endDate + "') " 
		 	+ " ) ";
   	
   	sql = " select * FROM " + SQLUtil.formatKind(sFParams, " wflowusers " )+
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (wflu_userid = user_userid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflu_wflowid = wflw_wflowid) " +
			" where wflu_wflowuserid > 0 " + 
			where;
   	
   	
			 
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	pmConn.doFetch(sql);

%>

<!-- <%= sql %> -->

<html>
<head>
	<title>:::<%= title %>:::</title>
	<link rel="stylesheet" type="text/css" href="/css/<%= defaultCss %>"> 
	
		<script type="text/javascript" src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization', 'version':'1','packages':['timeline']}]}"></script>
		<script type="text/javascript">
		google.setOnLoadCallback(drawChart);
		
		function drawChart() {
		  var container = document.getElementById('example1');
		
		  var chart = new google.visualization.Timeline(container);
		
		  var dataTable = new google.visualization.DataTable();
		
		  dataTable.addColumn({ type: 'string', id: 'Recurso' });
		  dataTable.addColumn({ type: 'string', id: 'Flujo' });
		  dataTable.addColumn({ type: 'date', id: 'Inicio' });
		  dataTable.addColumn({ type: 'date', id: 'Fin' });
		
		  dataTable.addRows([
		  
			[ 'Intervalo', '  ', 
			  new Date(<%= startYear %>, <%= startMonth %>, <%= startDay %>, 00, 00, 00), 
			  new Date(<%= endYear %>, <%= endMonth %>, <%= endDay %>, 23, 59, 59) ],
		  
		  	<% if (wflowId > 0) { %>
		  		[ 'Flujo <%= bmoWFlow.getCode().toString() %>', ' <%= bmoWFlow.getName().toString() %>', new Date(<%= wflowStartYear %>, <%= wflowStartMonth %>, <%= wflowStartDay %>, <%= wflowStartHour %>, <%= wflowStartMinute %>, 00), new Date(<%= wflowEndYear %>, <%= wflowEndMonth %>, <%= wflowEndDay %>, <%= wflowEndHour %>, <%= wflowEndMinute %>, 59) ],
		  	<% } %>
		  
		  <% 
		  int h = 1;

		  while (pmConn.next()) { 
		  			String userStartYear = "", userStartMonth = "", userStartDay = "", userStartHour = "", userStartMinute = "";	
		  			String userEndYear = "", userEndMonth = "", userEndDay = "", userEndHour = "", userEndMinute;	
		  			
					userStartYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockstart")).get(Calendar.YEAR);
					userStartMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockstart")).get(Calendar.MONTH) + 1);
					userStartDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockstart")).get(Calendar.DAY_OF_MONTH);		 	
					userStartHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockstart")).get(Calendar.HOUR_OF_DAY);		 	
					userStartMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockstart")).get(Calendar.MINUTE);		 	
					if (!pmConn.getString("wflu_lockend").equals("")) {
						userEndYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockend")).get(Calendar.YEAR);
						userEndMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockend")).get(Calendar.MONTH) + 1);
						userEndDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockend")).get(Calendar.DAY_OF_MONTH);
						userEndHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockend")).get(Calendar.HOUR_OF_DAY);		 	
						userEndMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("wflu_lockend")).get(Calendar.MINUTE);	
						%>
						[ '<%= HtmlUtil.stringToHtml(pmConn.getString("user_firstname")) + " " + HtmlUtil.stringToHtml(pmConn.getString("user_fatherlastname")) %>', '<%= pmConn.getString("wflw_code") %>', 
						  new Date(<%= userStartYear %>, <%= userStartMonth %>, <%= userStartDay %>, <%= userStartHour %>, <%= userStartMinute %>, 00), 
						  new Date(<%= userEndYear %>, <%= userEndMonth %>, <%= userEndDay %>, <%= userEndHour %>, <%= userEndMinute %>, 00) ],

						<%
						rows++; 
					} 
					h++;
				}
		  %>
		    ]);
		     

		
		  chart.draw(dataTable);
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

	<div id="example1" style="width: 95%; height: <%= (rows * 35 + 150) %>px;" class="report"></div>

<p>&nbsp;</p>

<table class="report">
	<tr>
		<td colspan="12" class="reportGroupCell">
			Asignaci&oacute;n de Colaboradores en el Intervalo de Fechas
		</td>
	</tr>

    <tr class="">
        <td class="reportHeaderCellCenter">#</td>
        <td class="reportHeaderCell">Colaborador</td>
        <td class="reportHeaderCell">Clave Flujo</td>
        <td class="reportHeaderCell">Flujo</td>
        <td class="reportHeaderCell">Inicio</td>
        <td class="reportHeaderCell">Fin</td>
    </tr>
    <%
			pmConn.beforeFirst();
	   
	          int  i = 1;
	          double total = 0;
	          while(pmConn.next()) {  
	    %>
	          <tr class="reportCellEven">
	          			<%= HtmlUtil.formatReportCell(sFParams, "" + (i), BmFieldType.NUMBER) %>
	          			
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("user_firstname") + " " + pmConn.getString("user_fatherlastname"), BmFieldType.STRING)) %>

						<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflw_code"), BmFieldType.STRING) %>
						
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflw_name"), BmFieldType.STRING)) %>
						
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("wflu_lockstart"), BmFieldType.STRING) %>
						
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("wflu_lockend"), BmFieldType.STRING) %>
	          </tr>
	    <%
	       i++;
	       }
	     %>

</table>    
<p>&nbsp;</p>

<% if (wflowId > 0) { %>
<table class="report">
	<tr>
		<td colspan="12" class="reportGroupCell">
			Colaboradores Disponibles en el Intervalo de Fecha y Hora exclusivos del Flujo "<%= bmoWFlow.getCode().toString() %> <%= bmoWFlow.getName().toString() %>"
		</td>
	</tr>
    <tr class="">
        <td class="reportHeaderCell">#</td>
        <td class="reportHeaderCell">Colaborador</td>
		<td class="reportHeaderCell">Email</td>
        <td class="reportHeaderCell">Departamento</td>
    </tr>
	<%
		String availableSql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " users ") 
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON (user_areaid = area_areaid)"
							+ " WHERE user_userid NOT IN ( "
								+ " select wflu_userid FROM " + SQLUtil.formatKind(sFParams, " wflowusers ") +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (wflu_userid = user_userid) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflu_wflowid = wflw_wflowid) " +
								" where wflu_wflowuserid > 0 " + 
								 " AND ( "
								   	+ " ('" + bmoWFlow.getStartDate().toString() + "' BETWEEN wflu_lockstart AND wflu_lockend )" 
								   	+ " OR "
									+ " ('" + bmoWFlow.getEndDate().toString() + "' BETWEEN wflu_lockstart AND wflu_lockend )" 
								   	+ " ) " 
							+ ")";
		pmConn.doFetch(availableSql);
		i = 1;
		while (pmConn.next()) {
	%>
		<tr class="reportCellEven">
	  		<td align="left" width="40">
	  			&nbsp;<%= i %>
	  		</td>
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("user_firstname") + " " + pmConn.getString("user_fatherlastname"), BmFieldType.STRING) %>
		
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("user_email"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("area_name"), BmFieldType.STRING) %>
			
		</tr>
	<% } %>
</table>   
<% } %>

<p>&nbsp;</p>

<table class="report">
	<tr>
		<td colspan="12" class="reportGroupCell">
			Colaboradores Disponibles en el Intervalo de Fecha y Hora Ampliado
		</td>
	</tr>
    <tr class="">
        <td class="reportHeaderCell">#</td>
        <td class="reportHeaderCell">Colaborador</td>
        <td class="reportHeaderCell">Email</td>
        <td class="reportHeaderCell">Departamento</td>
    </tr>
	<%
		String availableSql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " users ") 
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas")+" ON (user_areaid = area_areaid)"
							+ " WHERE user_userid NOT IN ( "
								+ " select wflu_userid FROM " + SQLUtil.formatKind(sFParams, " wflowusers ") +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" ON (wflu_userid = user_userid) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows")+" ON (wflu_wflowid = wflw_wflowid) " +
								" where wflu_wflowuserid > 0 " + 
								" AND ( "
								+ " (wflu_lockstart BETWEEN '" + startString + "' AND '" + endString + "' )" 
							 	+ " OR "
								+ " (wflu_lockend BETWEEN '" + startString + "' AND '" + endString + "' )" 
							 	+ " ) "
							+ ")";
		pmConn.doFetch(availableSql);
		i = 1;
		while (pmConn.next()) {
	%>
		<tr class="reportCellEven">
	  		<td align="left" width="40">
	  			&nbsp;<%= i %>
	  		</td>
		
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("user_firstname") + " " + pmConn.getString("user_fatherlastname"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("user_email"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("area_name"), BmFieldType.STRING) %>
	
		</tr>
	<% } %>
</table>   

<p>&nbsp;</p>
 <%
 	pmConn.close();
 %>
	<% if (print.equals("1")) { %>
	<script>
		window.print();
	</script>
	<% } %>
  </body>
</html>