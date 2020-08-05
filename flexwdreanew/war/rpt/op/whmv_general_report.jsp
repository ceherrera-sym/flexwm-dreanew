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

<%@page import="com.flexwm.shared.op.BmoWhMovement"%>
<%@page import="com.flexwm.server.op.PmWhMovement"%>
<%@page import="com.flexwm.shared.op.BmoWhMovItem"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%@include file="/inc/login.jsp" %>

<% 
    // Inicializar variables
    String title = "Reportes de Tx. de Inventarios";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoWhMovement bmoWhMovement = new BmoWhMovement();
	BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	FlexUtil flexUtil = new FlexUtil();
    String sql = "", where = "", filters = "", whereProduct = "", filtersOrderBy = "", orderBy = "", type = "", whmvStatus = "", dateMovStart = "", dateMovEnd = "";
    int programId = 0, companyId = 0, warehouseId = 0, toWhSectionId = 0, productId = 0,
    		// dynamicColspan incrementar por cada columna del reporte
    		// dynamicColspanMinus incrementar por cada columna que vaya a mostrar totales(es decir, se va a restar al dynamicColspan si HAY FILA TOTALES)
    		dynamicColspan = 0, dynamicColspanMinus = 0;
    
    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("whmv_type") != null) type = request.getParameter("whmv_type");
   	if (request.getParameter("whmv_companyid") != null) companyId = Integer.parseInt(request.getParameter("whmv_companyid"));
   	if (request.getParameter("whse_warehouseid") != null) warehouseId = Integer.parseInt(request.getParameter("whse_warehouseid"));
   	if (request.getParameter("whmv_towhsectionid") != null) toWhSectionId = Integer.parseInt(request.getParameter("whmv_towhsectionid"));
    if (request.getParameter("whmv_datemov") != null) dateMovStart = request.getParameter("whmv_datemov");
    if (request.getParameter("datemovend") != null) dateMovEnd = request.getParameter("datemovend");
    if (request.getParameter("whmv_status") != null) whmvStatus = request.getParameter("whmv_status");
    if (request.getParameter("whmi_productid") != null) productId = Integer.parseInt(request.getParameter("whmi_productid"));
    
    // Modulo principal de Reportes
	bmoProgram = (BmoProgram)pmProgram.get(programId);

    // Filtros listados
    if (!type.equals("")) {
		where += SFServerUtil.parseFiltersToSql("whmv_type", type);
		filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getType())) + ": </i>" +
				request.getParameter("whmv_typeLabel") + ", ";
	}
    
    if (companyId > 0) {
    	where += " AND whmv_companyid = " + companyId;
    	filters += "<i>" + HtmlUtil.stringToHtml(
    			sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getCompanyId())) + ": </i>" + 
    			request.getParameter("whmv_companyidLabel") + ", ";
    }
    
    if (warehouseId > 0) {
        where += " AND whse_warehouseid = " + warehouseId;
        filters += "<i>" + HtmlUtil.stringToHtml(
        		sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getBmoToWhSection().getWarehouseId())) + ": </i>" + 
        		request.getParameter("whse_warehouseidLabel") + ", ";
    }
    
    if (toWhSectionId > 0) {
    	where += " AND whmv_towhsectionid = " + toWhSectionId;
    	filters += "<i>" + HtmlUtil.stringToHtml(
    			sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getToWhSectionId())) + ": </i>" + 
    			request.getParameter("whmv_towhsectionidLabel") + ", ";
    }

    if (!dateMovStart.equals("")) {
    	where += " AND whmv_datemov >= '" + dateMovStart + " 00:00' ";
    	filters += "<i>" + HtmlUtil.stringToHtml(
    			sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getDatemov())) + " Inicio: </i>" + 
    			dateMovStart + ", ";
    }

    if (!dateMovEnd.equals("")) {
    	where += " AND whmv_datemov <= '" + dateMovEnd + " 23:59' ";
    	filters += "<i> "+ HtmlUtil.stringToHtml(
    			sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getDatemov())) + " Fin: </i>" 
    			+ dateMovEnd + ", ";
    }
    
   	if (!whmvStatus.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("whmv_status", whmvStatus);
   		filters += "<i>" + HtmlUtil.stringToHtml(
   				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getStatus())) + ": </i>" + 
   		request.getParameter("whmv_statusLabel") + ", ";
   	}
 
    if (productId > 0) {
    	whereProduct += " AND whmi_productid = " + productId;
        filters += "<i>" + HtmlUtil.stringToHtml(
        		sFParams.getFieldFormTitle(bmoWhMovItem.getProgramCode().toString(), bmoWhMovItem.getProductId())) +": </i>" + 
    	request.getParameter("whmi_productidLabel") + ", ";
    }
 
    // Obtener disclosure de datos
    String disclosureFilters = new PmWhMovement(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    // Ordenar registros por:
    // Fecha WhMov
    orderBy += " ORDER BY " + bmoWhMovement.getDatemov().getName().toString() + " " + BmOrder.DESC + ",";
    filtersOrderBy += HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getDatemov())) + " " + BmOrder.DESC + ", ";
    // Id WhMov
    orderBy += bmoWhMovement.getIdField().getName().toString() + " " + BmOrder.ASC + "";
    filtersOrderBy += HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getIdField())) + " " + BmOrder.ASC;
        
    // Conexion BD
    PmConn pmConnWhMovement = new PmConn(sFParams);
    pmConnWhMovement.open();
    
    PmConn pmConnWhMovItem = new PmConn(sFParams);
    pmConnWhMovItem.open();
    
	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
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

<table bprodr="0" cellspacing="0" cellpading="0" width="100%">
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
			<b>Ordenado por:</b> <%= filtersOrderBy%>
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report">
	<tr class="">
		<td class="reportHeaderCellCenter">#</td>
		<%	dynamicColspan++; // primera celda numero(#) %>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getCode())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getCode()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getDescription())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getDescription()))%></td>
		<%	}	%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getDatemov())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getDatemov()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getType())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getType()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getBmoToWhSection().getWarehouseId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getBmoToWhSection().getWarehouseId()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getToWhSectionId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getToWhSectionId()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getRequisitionReceiptId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getRequisitionReceiptId()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getBmoRequisitionReceipt().getSupplierId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getBmoRequisitionReceipt().getSupplierId()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getOrderDeliveryId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getOrderDeliveryId()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getBmoOrderDelivery().getOrderId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getBmoOrderDelivery().getOrderId()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getBmoOrderDelivery().getCustomerId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getBmoOrderDelivery().getCustomerId()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getCompanyId())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getCompanyId()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getStatus())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getStatus()))%></td>
		<%	}%>
		
		<%
		if (sFParams.isFieldEnabled(bmoWhMovItem.getProductId())) {
			if (productId > 0) {
				dynamicColspan++;	dynamicColspan++;
				dynamicColspanMinus++;	dynamicColspanMinus++;
				%>
				<td class="reportHeaderCellCenter">
					Cantidad Prod.
				</td>
				<td class="reportHeaderCellRight">
					Subtotal Prod.
				</td>
				<%
			}
		}	
		%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getAmount())) {
			dynamicColspan++;
			dynamicColspanMinus++;
		%>
				<td class="reportHeaderCellRight"><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getAmount())%></td>
		<%	}%>
	</tr>
	<%
	

	sql = "SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " whmovements ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON (whse_whsectionid = whmv_towhsectionid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " warehouses")+" ON (ware_warehouseid = whse_warehouseid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (comp_companyid = whmv_companyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderdeliveries")+" ON (odly_orderdeliveryid = whmv_orderdeliveryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = odly_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (cust_customerid = odly_customerid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitionreceipts")+" ON (rerc_requisitionreceiptid = whmv_requisitionreceiptid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (supl_supplierid = rerc_supplierid) " +
			" WHERE whmv_whmovementid > 0 " + 
			where;
	if (productId > 0) {
		sql += " AND whmv_whmovementid IN ( "+
				" SELECT whmi_whmovementid FROM " + SQLUtil.formatKind(sFParams, " whmovitems ") +
				" WHERE whmi_productid > 0 " +
				whereProduct +
				" ) ";
	}
	int count =0;
	//ejecuto el sql DEL CONTADOR
	pmConnCount.doFetch(sql);
	if(pmConnCount.next())
		count=pmConnCount.getInt("contador");
	
	System.out.println("contador DE REGISTROS --> "+count);
	//if que muestra el mensajede error
	if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
		%>
		
				<%=messageTooLargeList %>
		<% 
	}else{
	sql = " SELECT whmv_whmovementid, whmv_code, whmv_type, whmv_description, whmv_datemov, whmv_amount, whmv_status, ware_name, whse_name, " +
			" rerc_code, supl_code, supl_name, odly_code, orde_code, cust_code, cust_displayname, comp_name " + 
			" FROM " + SQLUtil.formatKind(sFParams, " whmovements ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON (whse_whsectionid = whmv_towhsectionid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " warehouses")+" ON (ware_warehouseid = whse_warehouseid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (comp_companyid = whmv_companyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderdeliveries")+" ON (odly_orderdeliveryid = whmv_orderdeliveryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = odly_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (cust_customerid = odly_customerid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitionreceipts")+" ON (rerc_requisitionreceiptid = whmv_requisitionreceiptid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (supl_supplierid = rerc_supplierid) " +
			" WHERE whmv_whmovementid > 0 " + 
			where;
	if (productId > 0) {
		sql += " AND whmv_whmovementid IN ( "+
				" SELECT whmi_whmovementid FROM " + SQLUtil.formatKind(sFParams, " whmovitems ") +
				" WHERE whmi_productid > 0 " +
				whereProduct +
				" ) ";
	}
	sql += orderBy;

	//System.out.println("sql: " + sql);
	
	pmConnWhMovement.doFetch(sql);
	int i = 1;
	double sumAmountTotal = 0, sumWhMovItemsAmountTotal = 0, sumWhMovItemsQuantityTotal = 0;
	
	while(pmConnWhMovement.next()) {
		// Suma de cantidad y monto de los items(en caso de que exista el producto)
		double sumWhMovItemsAmount = 0, sumWhMovItemsQuantity = 0;
		// Suma total de monto del mov
		sumAmountTotal += pmConnWhMovement.getDouble("whmv_amount");
		
		// Asignar Tipo Mov, Estatus
		bmoWhMovement.getType().setValue(pmConnWhMovement.getString("whmovements", "whmv_type"));
		bmoWhMovement.getStatus().setValue(pmConnWhMovement.getString("whmovements", "whmv_status"));
		%>      
		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getCode())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConnWhMovement.getString("whmovements", "whmv_code"), BmFieldType.CODE) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getDescription())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnWhMovement.getString("whmovements", "whmv_description")), BmFieldType.STRING) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getDatemov())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConnWhMovement.getString("whmovements", "whmv_datemov"), BmFieldType.DATE) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getType())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoWhMovement.getType().getSelectedOption().getLabel()), BmFieldType.STRING) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getBmoToWhSection().getWarehouseId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnWhMovement.getString("warehouses", "ware_name")), BmFieldType.STRING) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getToWhSectionId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnWhMovement.getString("whsections", "whse_name")), BmFieldType.STRING) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getRequisitionReceiptId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConnWhMovement.getString("requisitionreceipts", "rerc_code"), BmFieldType.CODE) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getBmoRequisitionReceipt().getSupplierId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnWhMovement.getString("suppliers", "supl_code") + " " + pmConnWhMovement.getString("suppliers", "supl_name")), BmFieldType.STRING) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getOrderDeliveryId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConnWhMovement.getString("orderdeliveries", "odly_code"), BmFieldType.CODE) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getBmoOrderDelivery().getOrderId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConnWhMovement.getString("orders", "orde_code"), BmFieldType.CODE) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getBmoOrderDelivery().getCustomerId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnWhMovement.getString("customers", "cust_code") + " " + pmConnWhMovement.getString("customers", "cust_displayname")), BmFieldType.STRING) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getCompanyId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConnWhMovement.getString("companies", "comp_name")), BmFieldType.STRING) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getStatus())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoWhMovement.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getProductId())) { 
					if (productId > 0) {
						
						String sqlWhMovItems = " SELECT SUM(whmi_amount) AS sumWhMovItemsAmount, SUM(whmi_quantity) AS sumWhMovItemsQuantity " + 
								"FROM " + SQLUtil.formatKind(sFParams, " whmovitems ") +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON (whse_whsectionid = whmi_towhsectionid) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = whmi_productid) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whmovements")+" ON (whmv_whmovementid = whmi_whmovementid) " +
								" WHERE whmi_whmovitemid > 0 " + 
								" AND whmi_whmovementid = " + pmConnWhMovement.getInt("whmovements", "whmv_whmovementid") +
								whereProduct;
		
						//System.out.println("sqlWhMovItems: " + sqlWhMovItems);
						pmConnWhMovItem.doFetch(sqlWhMovItems);
						if (pmConnWhMovItem.next()) {
							sumWhMovItemsQuantity = pmConnWhMovItem.getDouble("sumWhMovItemsQuantity");
							sumWhMovItemsAmount = pmConnWhMovItem.getDouble("sumWhMovItemsAmount");
							sumWhMovItemsQuantityTotal += pmConnWhMovItem.getDouble("sumWhMovItemsQuantity");
							sumWhMovItemsAmountTotal += pmConnWhMovItem.getDouble("sumWhMovItemsAmount");
						}
						%>
						<%= HtmlUtil.formatReportCell(sFParams, "" + sumWhMovItemsQuantity, BmFieldType.NUMBER) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + sumWhMovItemsAmount, BmFieldType.CURRENCY) %>
						<%
					}
				}	
			%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovement.getAmount())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnWhMovement.getDouble("whmv_amount"), BmFieldType.CURRENCY) %>
			<%	} %>
		</tr>
		<%
		i++;

	} // pmConnWhMovement
	%>
	<tr><td colspan="<%= dynamicColspan %>">&nbsp;</td></tr>
	<tr class="reportCellCode reportCellEven">
		<td colspan="<%= (dynamicColspan - dynamicColspanMinus) %>">&nbsp;</td>
		<%	
		if (sFParams.isFieldEnabled(bmoWhMovItem.getProductId())) { 
			if (productId > 0) {
				%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumWhMovItemsQuantityTotal, BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumWhMovItemsAmountTotal, BmFieldType.CURRENCY) %>
				<%
			}
		}
		%>
		<%	if (sFParams.isFieldEnabled(bmoWhMovement.getAmount())) { %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumAmountTotal, BmFieldType.CURRENCY) %>
		<%	} %>
	</tr>
</table>
<%
	}// Fin de if(no carga datos)
	pmConnCount.close();
    pmConnWhMovItem.close();
	pmConnWhMovement.close();
%>  
<%	if (print.equals("1")) { %>
		<script>
			//window.print();
		</script>
<% 	} 
System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
+ " Reporte: "+title
+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
}// FIN DEL CONTADOR%>
	</body>
</html>
