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
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>
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
 	String title = "Uso de Recursos en Pedidos";
	
   	String sql = "", where = "";
   	String filters = "";
   	int programId = 0;
   	int orderId = 0;
   	int equipmentId = 0;
   	int rows = 0;

   	String startDate = "", startYear = "", startMonth = "", startDay = "", startHour = "", startMinute = "", startString = "";
	String endDate = "", endYear = "", endMonth = "", endDay = "", endHour = "", endMinute = "", endString = "";
   	
   	PmOrder pmOrder = new PmOrder(sFParams);
   	BmoOrder bmoOrder = new BmoOrder();
   	String orderStartDate = "", orderStartYear = "", orderStartMonth = "", orderStartDay = "", orderStartHour = "", orderStartMinute = "", orderStartString = "";
	String orderEndDate = "", orderEndYear = "", orderEndMonth = "", orderEndDay = "", orderEndHour = "", orderEndMinute = "", orderEndString = "";
   
   	// Obtener parametros
   	if (request.getParameter("orderId") != null) orderId = Integer.parseInt(request.getParameter("orderId"));
   	if (request.getParameter("equipmentId") != null) equipmentId = Integer.parseInt(request.getParameter("equipmentId"));
	if (request.getParameter("startDate") != null) startDate = request.getParameter("startDate");
	if (request.getParameter("endDate") != null) endDate = request.getParameter("endDate");
	
	// Asignar horas default a startDate y endDate
	if (!startDate.equals("")) startDate += " 00:00:00";
	if (!endDate.equals("")) endDate += " 23:59:00";

	// Construir filtros 
   	if (orderId > 0) {
   		bmoOrder = (BmoOrder)pmOrder.get(orderId);
   				 	
		// Fechas de intervalo
		if (startDate.equals("")) startDate = bmoOrder.getLockStart().toString();
		if (endDate.equals("")) endDate = bmoOrder.getLockEnd().toString();

		// Fechas ordero
		orderStartYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockStart().toString()).get(Calendar.YEAR);
		orderStartMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockStart().toString()).get(Calendar.MONTH) + 1);
		orderStartDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockStart().toString()).get(Calendar.DAY_OF_MONTH);		 	
		orderStartHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockStart().toString()).get(Calendar.HOUR_OF_DAY);		 	
		orderStartMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockStart().toString()).get(Calendar.MINUTE);		 	
		orderStartString = bmoOrder.getLockStart().toString();
		
		orderEndYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockEnd().toString()).get(Calendar.YEAR);
		orderEndMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockEnd().toString()).get(Calendar.MONTH) + 1);
		orderEndDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockEnd().toString()).get(Calendar.DAY_OF_MONTH);
		orderEndHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockEnd().toString()).get(Calendar.HOUR_OF_DAY);		 	
		orderEndMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), bmoOrder.getLockEnd().toString()).get(Calendar.MINUTE);
		orderEndString = bmoOrder.getLockEnd().toString();
   		
		filters += "<i>Pedido: </i>" + bmoOrder.getCode().toString() + " - " + bmoOrder.getName().toString() + " <br> ";
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
	endString = endYear + "-" + endMonth + "-"+ endDay + " 23:59:00";
	
	filters += "<i>Intervalo de Fechas: </i>" + startString + " a " + endString + " ";
	
	where += " AND ( "
			+ " ('" + startDate + "' BETWEEN orde_lockstart AND orde_lockend) " 
		 	+ " OR  ('" + endDate + "' BETWEEN orde_lockstart AND orde_lockend) " 
		 	+ " OR  (orde_lockstart BETWEEN '" + startDate + "' AND '" + endDate + "') " 
		 	+ " OR  (orde_lockend BETWEEN '" + startDate + "' AND '" + endDate + "') " 
		 	+ " ) ";
   	
   	sql = " select * FROM " + SQLUtil.formatKind(sFParams, " orderequipments ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" on (ordq_equipmentid = equi_equipmentid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (ordq_orderid = orde_orderid) " +
			" where ordq_orderequipmentid > 0 " + 
			where;
			 
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	pmConn.doFetch(sql);
%>


<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
	
		<script type="text/javascript" src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization', 'version':'1','packages':['timeline']}]}"></script>
		<script type="text/javascript">
		google.setOnLoadCallback(drawChart);
		
		function drawChart() {
		  var container = document.getElementById('example1');
		
		  var chart = new google.visualization.Timeline(container);
		
		  var dataTable = new google.visualization.DataTable();
		
		  dataTable.addColumn({ type: 'string', id: 'Recurso' });
		  dataTable.addColumn({ type: 'string', id: 'Pedido' });
		  dataTable.addColumn({ type: 'date', id: 'Inicio' });
		  dataTable.addColumn({ type: 'date', id: 'Fin' });
		
		  dataTable.addRows([
		  
			[ 'Intervalo', ' Ampliado', new Date(<%= startYear %>, <%= startMonth %>, <%= startDay %>, 00, 00, 00), new Date(<%= endYear %>, <%= endMonth %>, <%= endDay %>, 23, 59, 59) ],
		  
		  	<% if (orderId > 0) { %>
		  		[ 'Pedido <%= bmoOrder.getCode().toString() %>', ' <%= HtmlUtil.stringToHtml(bmoOrder.getName().toString()) %>', new Date(<%= orderStartYear %>, <%= orderStartMonth %>, <%= orderStartDay %>, <%= orderStartHour %>, <%= orderStartMinute %>, 00), new Date(<%= orderEndYear %>, <%= orderEndMonth %>, <%= orderEndDay %>, <%= orderEndHour %>, <%= orderEndMinute %>, 59) ],
		  	<% } %>
		  
		  <% while (pmConn.next()) { %>
		  		<%
		  			String equipmentStartYear = "", equipmentStartMonth = "", equipmentStartDay = "", equipmentStartHour = "", equipmentStartMinute = "";	
		  			String equipmentEndYear = "", equipmentEndMonth = "", equipmentEndDay = "", equipmentEndHour = "", equipmentEndMinute;	
		  			
					equipmentStartYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockstart")).get(Calendar.YEAR);
					equipmentStartMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockstart")).get(Calendar.MONTH) + 1);
					equipmentStartDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockstart")).get(Calendar.DAY_OF_MONTH);		 	
					equipmentStartHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockstart")).get(Calendar.HOUR_OF_DAY);		 	
					equipmentStartMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockstart")).get(Calendar.MINUTE);		 	
					
					equipmentEndYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockend")).get(Calendar.YEAR);
					equipmentEndMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockend")).get(Calendar.MONTH) + 1);
					equipmentEndDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockend")).get(Calendar.DAY_OF_MONTH);
					equipmentEndHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockend")).get(Calendar.HOUR_OF_DAY);		 	
					equipmentEndMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("ordq_lockend")).get(Calendar.MINUTE);	
		  				  		
		  		%>
		  
		  
		  	[ '<%= HtmlUtil.stringToHtml(pmConn.getString("equi_name")) %>', '<%= pmConn.getString("orde_code") %>', new Date(<%= equipmentStartYear %>, <%= equipmentStartMonth %>, <%= equipmentStartDay %>, <%= equipmentStartHour %>, <%= equipmentStartMinute %>, 00), new Date(<%= equipmentEndYear %>, <%= equipmentEndMonth %>, <%= equipmentEndDay %>, <%= equipmentEndHour %>, <%= equipmentEndMinute %>, 00) ],
		  
		  <%
		  			rows++; 
		  		} 
		  %>
		    ]);
		     

		
		  chart.draw(dataTable);
		}
		</script>
</head>

<body class="default" onload="self.focus();" style="padding-right: 10px">

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
			Uso de Recursos en el Intervalo de Fechas
		</td>
	</tr>

    <tr class="">
        <td class="reportHeaderCell">#</td>
        <td class="reportHeaderCell">Equipo</td>
        <td class="reportHeaderCell">Clave Pedido</td>
        <td class="reportHeaderCell">Pedido</td>
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
	          		<td align="left" width="40">
	          			&nbsp;<%= i %>
	          		</td>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("equi_name"), BmFieldType.STRING)) %>

						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_code"), BmFieldType.STRING)) %>
						
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_name"), BmFieldType.STRING)) %>
						
						<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("ordq_lockstart"), BmFieldType.STRING) %>
						
						<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("ordq_lockend"), BmFieldType.STRING) %>
	          </tr>
	    <%
	       i++;
	       }
	     %>

</table>    
<p>&nbsp;</p>

<% if (orderId > 0) { %>
<table class="report">
	<tr>
		<td colspan="12" class="reportGroupCell">
			Recursos Disponibles en el Intervalo de Fecha y Hora exclusivos del Pedido "<%= bmoOrder.getCode().toString() %> <%= bmoOrder.getName().toString() %>"
		</td>
	</tr>
    <tr class="">
        <td class="reportHeaderCellCenter">#</td>
        <td class="reportHeaderCell">Clave Recurso</td>
        <td class="reportHeaderCell">Recurso</td>
        <td class="reportHeaderCell">Descripci&oacute;n</td>
        <td class="reportHeaderCell">Tipo Recurso</td>
    </tr>
	<%
		String availableSql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " equipments ") 
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON (equi_equipmenttypeid = eqty_equipmenttypeid)"
							+ " WHERE equi_equipmentid NOT IN ( "
								+ " select ordq_equipmentid FROM " + SQLUtil.formatKind(sFParams, " orderequipments ") +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" on (ordq_equipmentid = equi_equipmentid) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (ordq_orderid = orde_orderid) " +
								" where ordq_orderequipmentid > 0 " + 
								 " AND ( "
								   	+ " ('" + bmoOrder.getLockStart().toString() + "' BETWEEN ordq_lockstart AND ordq_lockend )" 
								   	+ " OR "
									+ " ('" + bmoOrder.getLockEnd().toString() + "' BETWEEN ordq_lockstart AND ordq_lockend )" 
								   	+ " ) " 
							+ ")";
		pmConn.doFetch(availableSql);
		i = 1;
		while (pmConn.next()) {
	%>
		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + (i), BmFieldType.NUMBER) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("equi_code"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("equi_name"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("equi_description"), BmFieldType.STRING) %>
	
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("eqty_code"), BmFieldType.STRING) %>	
		</tr>
	<% } %>
</table>   
<% } %>

<p>&nbsp;</p>

<table class="report">
	<tr>
		<td colspan="12" class="reportGroupCell">
			Recursos Disponibles en el Intervalo de Fecha y Hora Ampliado
		</td>
	</tr>
    <tr class="">
        <td class="reportHeaderCell">#</td>
        <td class="reportHeaderCell">Clave Recurso</td>
        <td class="reportHeaderCell">Recurso</td>
        <td class="reportHeaderCell">Descripci&oacute;n</td>
        <td class="reportHeaderCell">Tipo Recurso</td>
    </tr>
	<%
		String availableSql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " equipments ") 
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON (equi_equipmenttypeid = eqty_equipmenttypeid)"
							+ " WHERE equi_equipmentid NOT IN ( "
								+ " select ordq_equipmentid FROM " + SQLUtil.formatKind(sFParams, " orderequipments ") +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" on (ordq_equipmentid = equi_equipmentid) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (ordq_orderid = orde_orderid) " +
								" where ordq_orderequipmentid > 0 " + 
								" AND ( "
								+ " (ordq_lockstart BETWEEN '" + startString + "' AND '" + endString + "' )" 
							 	+ " OR "
								+ " (ordq_lockend BETWEEN '" + startString + "' AND '" + endString + "' )" 
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
		
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("equi_code"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("equi_name"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("equi_description"), BmFieldType.STRING) %>
	
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("eqty_code"), BmFieldType.STRING) %>	
		</tr>
	<% } %>
</table>   
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