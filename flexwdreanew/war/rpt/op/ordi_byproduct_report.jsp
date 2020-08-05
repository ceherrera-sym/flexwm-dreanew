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
    String sql = "", where = "";
    String ordeStatus = "", paymentStatus = "", deliveryStatus = "", lockStatus  = "", lockStartDate = "", lockEndDate = "";
    String filters = "", fullName = "";
    int customerId = 0, supplierId = 0, orderId = 0, cols= 0;

    // Obtener parametros       
    //if (request.getParameter("prod_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("prod_supplierid"));
    if (request.getParameter("orde_orderid") != null) orderId = Integer.parseInt(request.getParameter("orde_orderid"));
    
    if (request.getParameter("orde_customerid") != null) customerId = Integer.parseInt(request.getParameter("orde_customerid"));    
    if (request.getParameter("orde_status") != null) ordeStatus = request.getParameter("orde_status");
    if (request.getParameter("orde_lockstatus") != null) lockStatus = request.getParameter("orde_lockstatus");
    if (request.getParameter("orde_deliverystatus") != null) deliveryStatus = request.getParameter("orde_deliverystatus");
    if (request.getParameter("orde_paymentstatus") != null) paymentStatus = request.getParameter("orde_paymentstatus");
    if (request.getParameter("orde_lockstart") != null) lockStartDate = request.getParameter("orde_lockstart");
    if (request.getParameter("orde_lockend") != null) lockEndDate = request.getParameter("orde_lockend");
    
    // Filtros listados
    /*
    if (supplierId > 0) {
          where += " and supl_supplierid = " + supplierId;
          filters += "<i>Proveedor: </i>" + request.getParameter("prod_supplieridLabel") + ", ";
    }
    */
    
    if (orderId > 0) {
        where += " and orde_orderid = " + orderId;
        filters += "<i>Pedido: </i>" + request.getParameter("orde_orderidLabel") + ", ";
    }
    
    if (customerId > 0) {
        where += " and cust_customerid = " + customerId;
        filters += "<i>Cliente: </i>" + request.getParameter("orde_customeridLabel") + ", ";
    }
  
    if (!ordeStatus.equals("")) {
    	where += " and orde_status like '" + ordeStatus + "'";
    	filters += "<i>Estatus: </i>" + request.getParameter("orde_statusLabel") + ", ";
    }
  
    if (!deliveryStatus.equals("")) {
    	where += " and orde_deliverystatus like '" + deliveryStatus + "'";
    	filters += "<i>Entrega: </i>" + request.getParameter("orde_deliverystatusLabel") + ", ";
    }
  
    if (!paymentStatus.equals("")) {
    	where += " and orde_paymentstatus like '" + paymentStatus + "'";
      	filters += "<i>Pago: </i>" + request.getParameter("orde_paymentstatusLabel") + ", ";
    }
  
    if (!lockStatus.equals("")) {
    	where += " and orde_lockstatus like '" + lockStatus + "'";
      	filters += "<i>Apartado: </i>" + request.getParameter("orde_lockstatusLabel") + ", ";
    }
  
    if (!lockStartDate.equals("")) {
    	where += " and orde_lockstart >= '" + lockStartDate + "'";
    	filters += "<i>Apartado Inicio: </i>" + lockStartDate + ", ";
    }
  
    if (!lockEndDate.equals("")) {
    	where += " and orde_lockend <= '" + lockEndDate + "'";
    	filters += "<i>Apartado Final: </i>" + lockEndDate + ", ";
    }

    sql = " SELECT cust_code, prod_name, supl_name, orde_code, orde_status,ordi_quantity, " +
          " orde_lockstart, MAX(ordi_quantity)  FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" on (orde_orderid = ordi_orderitemid) " + 
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" on (cust_customerid = orde_customerid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" on (prod_productid = ordi_productid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = prod_supplierid) " +
          " where ordi_orderitemid > 0 " + where +
          " GROUP BY ordi_quantity desc";
    System.out.println(" sql " + sql);
    PmConn pmOrderItem = new PmConn(sFParams);
    pmOrderItem.open();
    
    pmOrderItem.doFetch(sql);
   
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
<table class="report">
        <tr class="">
            <td class="reportHeaderCellCenter">#</td>
            <td class="reportHeaderCell">Producto</td>
            <td class="reportHeaderCell">Proveedor</td>
            <td class="reportHeaderCell">Pedido.</td>
            <td class="reportHeaderCell">F.Pedido.</td>
            <td class="reportHeaderCell">E.Pedido</td>
            <td class="reportHeaderCell">Cantidad</td>            
        </tr> 
        <%
           int i = 0;
           while(pmOrderItem.next()) {             
        %>      
        <tr class="reportCellEven">
                 <%= HtmlUtil.formatReportCell(sFParams, "" + (i+1), BmFieldType.NUMBER) %> 
                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("products", "prod_name"),BmFieldType.STRING))%>
                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("suppliers", "supl_name"),BmFieldType.STRING))%>
                 <%= HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_code"),BmFieldType.CODE)%>
                 <%= HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_lockstart"),BmFieldType.DATETIME)%>
                 <%= HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_status"),BmFieldType.CHAR)%>
                 <%= HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orderitems", "ordi_quantity"),BmFieldType.NUMBER)%>
             </tr>
        <%
           i++;
           } //pmOrderItem
        %>    
</table>
<%
   pmOrderItem.close();
%>  
<% if (print.equals("1")) { %>
    <script>
        window.print();
    </script>
    <% } %>
  </body>
</html>