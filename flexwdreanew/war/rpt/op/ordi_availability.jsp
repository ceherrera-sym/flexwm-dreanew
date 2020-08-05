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
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
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
 	String title = "Uso de Productos en Pedidos";
	
   	String sql = "", where = "";
   	String filters = "";
   	int programId = 0;
   	int orderId = 0;
   	int rows = 0;
   	int productId = 0;

   	String startDate = "", startYear = "", startMonth = "", startDay = "", startHour = "", startMinute = "", startString = "";
	String endDate = "", endYear = "", endMonth = "", endDay = "", endHour = "", endMinute = "", endString = "";
   	
   	PmProduct pmProduct = new PmProduct(sFParams);
   	BmoProduct bmoProduct = new BmoProduct();
   	
   	PmOrder pmOrder = new PmOrder(sFParams);
   	BmoOrder bmoOrder = new BmoOrder();
   	String orderStartDate = "", orderStartYear = "", orderStartMonth = "", orderStartDay = "", orderStartHour = "", orderStartMinute = "", orderStartString = "";
	String orderEndDate = "", orderEndYear = "", orderEndMonth = "", orderEndDay = "", orderEndHour = "", orderEndMinute = "", orderEndString = "";
   
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("orde_orderid") != null) orderId = Integer.parseInt(request.getParameter("orde_orderid"));
   	if (request.getParameter("prod_productid") != null) productId = Integer.parseInt(request.getParameter("prod_productid"));
	if (request.getParameter("startDate") != null) startDate = request.getParameter("startDate");
	if (request.getParameter("endDate") != null) endDate = request.getParameter("endDate");
	
	// Asignar horas default a startDate y endDate
	if (!startDate.equals("")) startDate += " 00:00:00";
	if (!endDate.equals("")) endDate += " 23:59:00";

	// Construir filtros 
   	if (orderId > 0) {
   		bmoProduct = (BmoProduct)pmProduct.get(productId);
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
		filters += "<i>Producto: </i>" + bmoProduct.getCode().toString() + " - " + bmoProduct.getName().toString() + " <br> ";
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
	
	where += " AND prod_productid = " + productId;
	where += " AND ordi_lockstatus = '" + BmoOrderItem.LOCKSTATUS_LOCKED + "'";
   	
   	sql = " select * FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
   			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" on (ordi_ordergroupid = ordg_ordergroupid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" on (ordi_productid = prod_productid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (ordg_orderid = orde_orderid) " +
			" where ordi_orderitemid > 0 " + 
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
		  
			[ 'Intervalo', '  ', new Date(<%= startYear %>, <%= startMonth %>, <%= startDay %>, 00, 00, 00), new Date(<%= endYear %>, <%= endMonth %>, <%= endDay %>, 23, 59, 59) ],
		  
		  	<% if (orderId > 0) { %>
		  		[ 'Pedido <%= bmoOrder.getCode().toString() %>', ' <%= HtmlUtil.stringToHtml(bmoOrder.getName().toString()) %>', new Date(<%= orderStartYear %>, <%= orderStartMonth %>, <%= orderStartDay %>, <%= orderStartHour %>, <%= orderStartMinute %>, 00), new Date(<%= orderEndYear %>, <%= orderEndMonth %>, <%= orderEndDay %>, <%= orderEndHour %>, <%= orderEndMinute %>, 59) ],
		  	<% } %>
		  
		  <% while (pmConn.next()) { %>
		  		<%
		  			String equipmentStartYear = "", equipmentStartMonth = "", equipmentStartDay = "", equipmentStartHour = "", equipmentStartMinute = "";	
		  			String equipmentEndYear = "", equipmentEndMonth = "", equipmentEndDay = "", equipmentEndHour = "", equipmentEndMinute;	
		  			
					equipmentStartYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.YEAR);
					equipmentStartMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.MONTH) + 1);
					equipmentStartDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.DAY_OF_MONTH);		 	
					equipmentStartHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.HOUR_OF_DAY);		 	
					equipmentStartMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockstart")).get(Calendar.MINUTE);		 	
					
					equipmentEndYear = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.YEAR);
					equipmentEndMonth = "" + (SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.MONTH) + 1);
					equipmentEndDay = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.DAY_OF_MONTH);
					equipmentEndHour = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.HOUR_OF_DAY);		 	
					equipmentEndMinute = "" + SFServerUtil.stringToCalendar(sFParams.getDateTimeFormat(), pmConn.getString("orde_lockend")).get(Calendar.MINUTE);
		  		%>
		  
		  
		  	[ '<%= HtmlUtil.stringToHtml(pmConn.getString("prod_name")) %>', 'Cantidad: <%= pmConn.getString("ordi_quantity") %> para Pedido: <%= pmConn.getString("orde_code") %> ', new Date(<%= equipmentStartYear %>, <%= equipmentStartMonth %>, <%= equipmentStartDay %>, <%= equipmentStartHour %>, <%= equipmentStartMinute %>, 00), new Date(<%= equipmentEndYear %>, <%= equipmentEndMonth %>, <%= equipmentEndDay %>, <%= equipmentEndHour %>, <%= equipmentEndMinute %>, 00) ],
		  
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

<br>

	<div id="example1" style="width: 95%; height: <%= (rows * 35 + 150) %>px;" class="report"></div>

<br>

<table class="report">
	<tr>
		<td colspan="12" class="reportGroupCell">
			Items bloqueados del Producto "<%= bmoProduct.getCode().toString() %> - <%= HtmlUtil.stringToHtml(bmoProduct.getName().toString()) %>" en el Intervalo de Fechas
		</td>
	</tr>

    <tr class="">
        <td class="reportHeaderCellCenter">#</td>
        <td class="reportHeaderCell">Producto</td>
		<td class="reportHeaderCell">Marca</td>
        <td class="reportHeaderCell">Modelo</td>
        <td class="reportHeaderCell">Clave Pedido</td>
        <td class="reportHeaderCell">Pedido</td>
        <td class="reportHeaderCell">Inicio</td>
        <td class="reportHeaderCell">Fin</td>
        <td class="reportHeaderCell">Cantidad</td>
    </tr>
    <%
			pmConn.beforeFirst();
	   
	          int  i = 1;
	          double total = 0;
	          int totalLocked = 0;
	          while(pmConn.next()) {  
	          	totalLocked += pmConn.getDouble("ordi_quantity");
	    %>
	          <tr class="reportCellEven">
	          			<%= HtmlUtil.formatReportCell(sFParams, "" + (i), BmFieldType.NUMBER) %>
	          			
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("prod_name"), BmFieldType.STRING)) %>
						
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("prod_brand"), BmFieldType.STRING)) %>
						
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("prod_model"), BmFieldType.STRING)) %>

						<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_code"), BmFieldType.STRING) %>
						
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_name"), BmFieldType.STRING)) %>
						
						<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_lockstart"), BmFieldType.STRING) %>
						
						<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_lockend"), BmFieldType.STRING) %>
						
						<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("ordi_quantity"), BmFieldType.STRING) %>
	          </tr>
	    <%
	       i++;
	       }
	     %>
		<tr>
 				<td class="reportHeaderCell" colspan="8">Total</td>
 				<td class="reportHeaderCell"><%= totalLocked %></td>
		</tr>
</table>    

<br>

<% 
	if (orderId > 0) { 
%>

<table class="report">
	<tr>
		<td colspan="12" class="reportGroupCell">
			Existencia del Producto "<%= bmoProduct.getCode().toString() %> - <%= HtmlUtil.stringToHtml(bmoProduct.getName().toString()) %> - Marca: <%= HtmlUtil.stringToHtml(bmoProduct.getBrand().toString()) %> - Modelo: <%= HtmlUtil.stringToHtml(bmoProduct.getModel().toString()) %>" en Almacenes
		</td>
	</tr>
    <tr class="">
        <td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Clave Almac&eacute;n</td>
        <td class="reportHeaderCell">Almac&eacute;n</td>
        <td class="reportHeaderCell">Secci&oacute;n</td>
        <td class="reportHeaderCell">Cantidad</td>
    </tr>
	<%
		String availableSql = "SELECT ware_code, ware_name, whse_name, SUM(whst_quantity) as q FROM " + SQLUtil.formatKind(sFParams, " whstocks ") +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON (whst_whsectionid = whse_whsectionid) " + 
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " warehouses")+" ON (whse_warehouseid = ware_warehouseid) " +
							" WHERE whst_productid = " + productId +
							" GROUP BY ware_code, ware_name, whse_name";
		pmConn.doFetch(availableSql);
		i = 1;
		double totalQuantity = 0;
		while (pmConn.next()) {
			totalQuantity += pmConn.getDouble("q");
	%>
		<tr class="reportCellEven">
	  		<td align="left" width="40">
	  			&nbsp;<%= i %>
	  		</td>
		
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("ware_code"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("ware_name"), BmFieldType.STRING)) %>
			
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("whse_name"), BmFieldType.STRING)) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("q"), BmFieldType.STRING) %>
		</tr>
	<% } %>
		<tr>
 				<td class="reportHeaderCell" colspan="4">Total</td>
 				<td class="reportHeaderCell"><%= totalQuantity %></td>
		</tr>
</table>   

<% 
	// Si el pedido pertenece a un proyecto, evaluar ordenes de compra ligadas
	int requisitionPurchaseQuantity = 0;
	int requisitionRentalQuantity = 0;
%>

<br>

<table class="report">
	<tr>
		<td colspan="12" class="reportGroupCell">
			Ordenes de Compra de tipo COMPRA Autorizadas No Entregadas, del Producto "<%= bmoProduct.getCode().toString() %> - <%= HtmlUtil.stringToHtml(bmoProduct.getName().toString()) %> - Marca: <%= HtmlUtil.stringToHtml(bmoProduct.getBrand().toString()) %> - Modelo: <%= HtmlUtil.stringToHtml(bmoProduct.getModel().toString()) %>"
		</td>
	</tr>
    <tr class="">
        <td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Clave O. C.</td>
        <td class="reportHeaderCell">Nombre</td>
        <td class="reportHeaderCell">Cantidad</td>
    </tr>
	<%
		String requisitionPurchaseSql = "SELECT reqi_code, reqi_name, rqit_quantity FROM " + SQLUtil.formatKind(sFParams, " requisitionitems ") 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (rqit_requisitionid = reqi_requisitionid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" ON (reqi_requisitiontypeid = rqty_requisitiontypeid) "
					+ " WHERE rqit_productid = " + bmoProduct.getId() + " "
					+ " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "' "
					+ " AND rqty_type = '" + BmoRequisitionType.TYPE_PURCHASE + "' "
					+ " AND reqi_deliverystatus <> '" + BmoRequisition.DELIVERYSTATUS_TOTAL + "' ";
		pmConn.doFetch(requisitionPurchaseSql);
		i = 1;
		while (pmConn.next()) {
			requisitionPurchaseQuantity += pmConn.getInt("rqit_quantity");
	%>
		<tr class="reportCellEven">
	  		<td align="left" width="40">
	  			&nbsp;<%= i %>
	  		</td>
		
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("reqi_code"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("reqi_name"), BmFieldType.STRING)) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("rqit_quantity"), BmFieldType.STRING) %>
		</tr>
	<% } %>
		<tr>
 				<td class="reportHeaderCell" colspan="3">Total</td>
 				<td class="reportHeaderCell"><%= requisitionPurchaseQuantity %></td>
		</tr>
</table>   


<br>

<table class="report">
	<tr>
		<td colspan="12" class="reportGroupCell">
			Ordenes de Compra de tipo RENTA Autorizadas del Producto "<%= bmoProduct.getCode().toString() %> - <%= HtmlUtil.stringToHtml(bmoProduct.getName().toString()) %> - Marca: <%= HtmlUtil.stringToHtml(bmoProduct.getBrand().toString()) %> - Modelo: <%= HtmlUtil.stringToHtml(bmoProduct.getModel().toString()) %>" en el Intervalo de Fechas del Proyecto
		</td>
	</tr>
    <tr class="">
        <td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Clave O. C.</td>
        <td class="reportHeaderCell">Nombre</td>
        <td class="reportHeaderCell">Cantidad</td>
    </tr>
	<%
		String requisitionRentalSql = "SELECT reqi_code, reqi_name, rqit_quantity FROM " + SQLUtil.formatKind(sFParams, " requisitionitems ") 
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (rqit_requisitionid = reqi_requisitionid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" ON (reqi_requisitiontypeid = rqty_requisitiontypeid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (reqi_orderid = orde_orderid) "
					+ " WHERE rqit_productid = " + bmoProduct.getId()
					+ " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "' "
					+ " AND rqty_type = '" + BmoRequisitionType.TYPE_RENTAL + "' "
					+ " AND ( "
					+ " ('" + bmoOrder.getLockStart().toString() + "' BETWEEN orde_lockstart AND orde_lockend) " 
				 	+ " OR  ('" + bmoOrder.getLockEnd().toString() + "' BETWEEN orde_lockstart AND orde_lockend) " 
				 	+ " OR  (orde_lockstart BETWEEN '" + bmoOrder.getLockStart().toString() + "' AND '" + bmoOrder.getLockEnd().toString() + "') " 
				 	+ " OR  (orde_lockend BETWEEN '" + bmoOrder.getLockStart().toString() + "' AND '" + bmoOrder.getLockEnd().toString() + "') " 
				 	+ " ) ";
		pmConn.doFetch(requisitionRentalSql);
		i = 1;
		while (pmConn.next()) {
			requisitionRentalQuantity += pmConn.getInt("rqit_quantity");
	%>
		<tr class="reportCellEven">
	  		<td align="left" width="40">
	  			&nbsp;<%= i %>
	  		</td>
		
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("reqi_code"), BmFieldType.STRING) %>
			
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("reqi_name"), BmFieldType.STRING)) %>
			
			<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("rqit_quantity"), BmFieldType.STRING) %>
		</tr>
	<% } %>
		<tr>
 				<td class="reportHeaderCell" colspan="3">Total</td>
 				<td class="reportHeaderCell"><%= requisitionRentalQuantity %></td>
		</tr>
</table>   

<br>

<table class="report">
	<tr>
		<td colspan="5" class="reportGroupCell">
			Totales
		</td>
	</tr>
    <tr>
        <td class="reportHeaderCell" width="20%">Total en Almac&eacute;n: <%= totalQuantity %></td>
        <td class="reportHeaderCell" width="20%">Total Apartado: <%= totalLocked %></td>
        <td class="reportHeaderCell" width="20%">Total en O. C. Tipo Compra: <%= requisitionPurchaseQuantity %></td>
        <td class="reportHeaderCell" width="20%">Total en O. C. Tipo Renta: <%= requisitionRentalQuantity %></td>
        <td class="reportHeaderCell" width="20%">Disponible: <%= totalQuantity + requisitionPurchaseQuantity + requisitionRentalQuantity - totalLocked %></td>
    </tr>
</table>

<% } %>
<%
	pmConn.close();
%>
<p>&nbsp;</p>
 
	<% if (print.equals("1")) { %>
	<script>
		window.print();
	</script>
	<% } %>
  </body>
</html>