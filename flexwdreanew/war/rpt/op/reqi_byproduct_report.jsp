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
 
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
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
 	String title = "Productos por Pedido";
	
   	String sql = "", where = "", deliveryStartDate = "", deliveryEndDate = "";
   	
   	String reqiStatus = "";
   	String filters = "";
   	int supplierId = 0, requisitionId = 0, orderId = 0, productId = 0, warehouseId = 0, cols= 0;
   	
   	
   	// Obtener parametros   	
   	if (request.getParameter("prod_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("prod_supplierid"));
   	if (request.getParameter("rqit_requisitionid") != null) requisitionId = Integer.parseInt(request.getParameter("rqit_requisitionid"));
   	if (request.getParameter("orderid") != null) orderId = Integer.parseInt(request.getParameter("orderid"));
   	if (request.getParameter("productid") != null) productId = Integer.parseInt(request.getParameter("productid"));
   	if (request.getParameter("warehouseid") != null) warehouseId = Integer.parseInt(request.getParameter("warehouseid"));
   	if (request.getParameter("deliverystart") != null) deliveryStartDate = request.getParameter("deliverystart");
   	if (request.getParameter("deliveryend") != null) deliveryEndDate = request.getParameter("deliveryend");
   
   	
   	
	
	// Filtros listados
	if (supplierId > 0) {
		  where += " and supl_supplierid = " + supplierId;
		  filters += "<i>Proveedor: </i>" + request.getParameter("prod_supplieridLabel") + ", ";
	}
	
	if (orderId > 0) {
        where += " and reqi_orderid = " + orderId;
        filters += "<i>Pedido: </i>" + request.getParameter("orderidLabel") + ", ";
    }
	
	if (productId > 0) {
        where += " and rqit_productid = " + productId;
        filters += "<i>Producto: </i>" + request.getParameter("productidLabel") + ", ";
    }
	
	if (requisitionId > 0) {
        where += " and reqi_requisitionid = " + requisitionId;
        filters += "<i>Orden Compra: </i>" + request.getParameter("rqit_requisitionidLabel") + ", ";
    }
	
	if (warehouseId > 0) {
        where += " and reqi_warehouseid = " + warehouseId;
        filters += "<i>Almacen: </i>" + request.getParameter("warehouseidLabel") + ", ";
	}    
	
   	if (!reqiStatus.equals("")) {
   		where += " and reqi_status like '" + reqiStatus + "'";
   		filters += "<i>Estatus: </i>" + request.getParameter("reqi_statusLabel") + ", ";
   	}
   	
   	if (!deliveryStartDate.equals("")) {
        where += " and reqi_deliverydate >= '" + deliveryStartDate + "'";
        filters += "<i>Entrega Inicio: </i>" + deliveryStartDate + ", ";
    }
    
    if (!deliveryEndDate.equals("")) {
        where += " and reqi_deliverydate <= '" + deliveryEndDate + "'";
        filters += "<i>Entrega Final: </i>" + deliveryEndDate + ", ";
    }
   	
   	sql = " SELECT prod_name, supl_name, orde_code, reqi_code, reqi_status,rqit_quantity, " +
   		  "	reqi_deliverydate, MAX(rqit_quantity)  FROM " + SQLUtil.formatKind(sFParams, " requisitionitems ") +
   		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" on (reqi_requisitionid = rqit_requisitionid) " +
   		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " warehouses")+" on (ware_warehouseid = reqi_warehouseid)" + 
		  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (reqi_orderid = orde_orderid) " +
		  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" on (prod_productid = rqit_productid) " +
		  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = prod_supplierid) " +
		  " where rqit_requisitionitemid > 0 " + where +
		  "	GROUP BY rqit_quantity desc";
   	
   	PmConn pmRequisitionItem = new PmConn(sFParams);
   	pmRequisitionItem.open();
   	
   	
   	
   
%>

<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
</head>

<body class="default" style="padding-right: 10px">

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

<!--<table width="100%" border="1">
	<tr>
		<td width="50%"> <div id="chart_div" style="width: 100%; height: 400px;"></div> </td>
		<td width="50%"> <div id="chart_div2" style="width: 100%; height: 400px;"></div> </td>
	</tr>
</table>-->

<table class="report">
        <tr class="">
            <td class="reportHeaderCell" width="2">#</td>
            <td class="reportHeaderCell">Producto</td>
            <td class="reportHeaderCell">Proveedor</td>
            <td class="reportHeaderCell">Almancen</td>
            <td class="reportHeaderCell">OC</td>
            <td class="reportHeaderCell">Entrega</td>
            <td class="reportHeaderCell">Pedido</td>            
            <td class="reportHeaderCell">Cantidad</td>            
        </tr> 
        <%
           int i = 0;
           pmRequisitionItem.doFetch(sql);
           while(pmRequisitionItem.next()) {        	   
        %>   	
        <tr class="reportCellEven">
                 <td align="left" width="40">
                        &nbsp;<%= i +1 %>
                 </td>
			     <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisitionItem.getString("prod_name"),BmFieldType.STRING))%>
			     <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisitionItem.getString("supl_name"),BmFieldType.STRING))%>
			     <%= HtmlUtil.formatReportCell(sFParams, pmRequisitionItem.getString("ware_code"),BmFieldType.CODE)%>
			     <%= HtmlUtil.formatReportCell(sFParams, pmRequisitionItem.getString("reqi_code"),BmFieldType.CODE)%>
			     <%= HtmlUtil.formatReportCell(sFParams, pmRequisitionItem.getString("reqi_deliverydate"),BmFieldType.DATE)%>
			     <%= HtmlUtil.formatReportCell(sFParams, pmRequisitionItem.getString("orde_code"),BmFieldType.STRING)%>
			     <%= HtmlUtil.formatReportCell(sFParams, pmRequisitionItem.getString("rqit_quantity"),BmFieldType.NUMBER)%>
		     </tr>
        <%
           i++;
           } //pmRequisitionItem
        %>    
</table>  
<%
   pmRequisitionItem.close();
%>  
<% if (print.equals("1")) { %>
	<script>
		window.print();
	</script>
	<% } %>
  </body>
</html>