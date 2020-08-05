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
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 
    // Inicializar variables
    String title = "Reportes Detallado Tx. de Inventarios";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoWhMovement bmoWhMovement = new BmoWhMovement();
	BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	String sql = "", where = "", filters = "", whereProduct = "",  filtersOrderBy = "", orderBy = "", filtersGroupBy = "", groupBy = "",
			type = "", whmvStatus = "", dateMovStart = "", dateMovEnd = "";
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
    			sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getBmoToWhSection().getWarehouseId())) + " : </i>" + 
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

    // Agrupar registros por:
    // Id WhMov
    // groupBy += bmoWhMovement.getIdField().getName().toString();
    filtersGroupBy += HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getIdField())) + ".";
    
    // Ordenar registros por:
    // Fecha WhMov
    orderBy += " ORDER BY " + bmoWhMovement.getDatemov().getName().toString() + " " + BmOrder.DESC + ",";
    filtersOrderBy += HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getDatemov())) + " " + BmOrder.DESC + ", ";
    // Id WhMov
    orderBy += bmoWhMovement.getIdField().getName().toString() + " " + BmOrder.ASC + "";
    filtersOrderBy += HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getIdField())) + " " + BmOrder.ASC + ".";
    
//    System.out.println("orderBy: " + orderBy);
//    System.out.println("groupBy: " + groupBy);
    
    // Como es una agrupacion el reporte, obtener conteo de las columnas, para colocar el dynamicColspan
    dynamicColspan++;	// primera celda numero(#) 
    if (sFParams.isFieldEnabled(bmoWhMovItem.getProductId())) 
		dynamicColspan++;
    if (sFParams.isFieldEnabled(bmoWhMovItem.getSerial())) 
		dynamicColspan++;
    if (sFParams.isFieldEnabled(bmoWhMovItem.getQuantity())) {
		dynamicColspan++;
		dynamicColspanMinus++;
    }
    if (sFParams.isFieldEnabled(bmoWhMovItem.getAmount())) {
		dynamicColspan++;
		dynamicColspanMinus++;
    }
    
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
	if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))){ %>
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

<table cellspacing="0" cellpading="0" width="100%">
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
			<b>Agrupado por:</b> <%= filtersGroupBy %>
            <b>Ordenado por:</b> <%= filtersOrderBy %>
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report">
	<%
	// Variables para suma de Cantidad y Monto de los items(si existe el producto)
	double sumAmountItemsTotal = 0;
	double sumQuantityItemsTotal = 0;	
	int iWhMov= 1, iWhMovItemTotal = 0;
	
	sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " whmovements ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON (whse_whsectionid = whmv_towhsectionid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " warehouses")+" ON (ware_warehouseid = whse_warehouseid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (comp_companyid = whmv_companyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderdeliveries")+" ON (odly_orderdeliveryid = whmv_orderdeliveryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = odly_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitionreceipts")+" ON (rerc_requisitionreceiptid = whmv_requisitionreceiptid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (supl_supplierid = rerc_supplierid) " +
			" WHERE whmv_whmovementid > 0 " + 
			where;
	if (productId > 0) {
		sql += " AND whmv_whmovementid IN ( "+
				" SELECT whmi_whmovementid FROM " + SQLUtil.formatKind(sFParams, " whmovitems ") +
				" WHERE whmi_whmovitemid > 0 " +
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
			" rerc_code, supl_code, supl_name, odly_code, orde_code, comp_name, odly_orderdeliveryid, rerc_requisitionreceiptid, orde_orderid " + 
			" FROM " + SQLUtil.formatKind(sFParams, " whmovements ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON (whse_whsectionid = whmv_towhsectionid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " warehouses")+" ON (ware_warehouseid = whse_warehouseid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (comp_companyid = whmv_companyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderdeliveries")+" ON (odly_orderdeliveryid = whmv_orderdeliveryid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = odly_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitionreceipts")+" ON (rerc_requisitionreceiptid = whmv_requisitionreceiptid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (supl_supplierid = rerc_supplierid) " +
			" WHERE whmv_whmovementid > 0 " + 
			where;
	if (productId > 0) {
		sql += " AND whmv_whmovementid IN ( "+
				" SELECT whmi_whmovementid FROM " + SQLUtil.formatKind(sFParams, " whmovitems ") +
				" WHERE whmi_whmovitemid > 0 " +
				whereProduct +
				" ) ";
	}
	sql += orderBy;
	
	//System.out.println("SQL_WhMov: " + sql);

	pmConnWhMovement.doFetch(sql);
	int y = 1;
	while(pmConnWhMovement.next()) {
		// Sumas de cantidad y monto de los items por cada movimiento
		double sumAmountItemsByWhMov = 0,  sumQuantityItemsByWhMov = 0;
		// Asignar Tipo Mov, Estatus
		bmoWhMovement.getType().setValue(pmConnWhMovement.getString("whmovements", "whmv_type"));
		bmoWhMovement.getStatus().setValue(pmConnWhMovement.getString("whmovements", "whmv_status"));
		
		%>
		<tr>
			<td class="reportGroupCell" colspan="<%= dynamicColspan%>">
				#<%= iWhMov%> |
				<%	if (sFParams.isFieldEnabled(bmoWhMovement.getCode())) { %>
						<%= pmConnWhMovement.getString("whmovements", "whmv_code") %> 
				<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoWhMovement.getDescription())) { %>
						<%= HtmlUtil.stringToHtml(pmConnWhMovement.getString("whmovements", "whmv_description"))%> 
				<%	} %>|
				<%	if (sFParams.isFieldEnabled(bmoWhMovement.getDatemov())) { %>
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getDatemov()))%>:
						<%= pmConnWhMovement.getString("whmovements", "whmv_datemov")%> |
				<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoWhMovement.getType())) { %>
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getType()))%>:
						<%= HtmlUtil.stringToHtml(bmoWhMovement.getType().getSelectedOption().getLabel())%> |
				<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoWhMovement.getRequisitionReceiptId())) { %>
						<% 	if (pmConnWhMovement.getInt("requisitionreceipts", "rerc_requisitionreceiptid") > 0) { %>
								<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getRequisitionReceiptId()))%>:
								<%= pmConnWhMovement.getString("requisitionreceipts", "rerc_code")%> -
								<%= pmConnWhMovement.getString("suppliers", "supl_code")%>
								<%= HtmlUtil.stringToHtml(pmConnWhMovement.getString("suppliers", "supl_name"))%> |
						<%	} %>
				<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoWhMovement.getAmount())) { %>
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getAmount()))%>:
						<%= SFServerUtil.formatCurrency(pmConnWhMovement.getDouble("whmv_amount")) %> |
				<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoWhMovement.getOrderDeliveryId())) { %>
						<% 	if (pmConnWhMovement.getInt("orderdeliveries", "odly_orderdeliveryid") > 0) { %>
								<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getOrderDeliveryId()))%>:
								<%= pmConnWhMovement.getString("orderdeliveries", "odly_code")%> |
						<%	} %>
				<%	} %>
				<%	if (sFParams.isFieldEnabled(bmoWhMovement.getBmoOrderDelivery().getOrderId())) { %>
						<% 	if (pmConnWhMovement.getInt("orders", "orde_orderid") > 0) { %>
								<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getBmoOrderDelivery().getOrderId()))%>:
								<%= pmConnWhMovement.getString("orders", "orde_code")%> |
						<%	} %>
				<%	} %>
			</td>
		</tr>
		<tr class="">
			<td class="reportHeaderCellCenter">#</td>
			<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getProductId())) { %>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovItem.getProductId()))%></td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getSerial())) { %>
					<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovItem.getSerial()))%></td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getQuantity())) { %>
					<td class="reportHeaderCellCenter"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovItem.getQuantity()))%></td>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getAmount())) { %>
					<td class="reportHeaderCellRight"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovItem.getAmount()))%></td>
			<%	}%>
		</tr>
		<%
		sql = " SELECT whmi_quantity, whmi_amount, whmi_serial, prod_code, prod_name " +
				" FROM " + SQLUtil.formatKind(sFParams, " whmovitems ") +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = whmi_productid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whmovements")+" ON (whmv_whmovementid = whmi_whmovementid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON (whse_whsectionid = whmv_towhsectionid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " warehouses")+" ON (ware_warehouseid = whse_warehouseid) " +
				" WHERE whmi_whmovitemid > 0 " + 
				" AND whmi_whmovementid = " + pmConnWhMovement.getInt("whmovements", "whmv_whmovementid") +
				whereProduct+ 
				where;
		//System.out.println("sqll: " + sql);
	
		pmConnWhMovItem.doFetch(sql);
		int iWhMovItem = 1;
		//System.out.println( "------------" +pmConnWhMovement.getString("whmovements", "whmv_code")+ "------------");

		while(pmConnWhMovItem.next()) {
			// Suma de cantidad por Mov y Total general
			sumQuantityItemsByWhMov += pmConnWhMovItem.getDouble("whmi_quantity");
			sumQuantityItemsTotal += pmConnWhMovItem.getDouble("whmi_quantity");
			
			// Suma de monto por Mov y Total general
			sumAmountItemsTotal += pmConnWhMovItem.getDouble("whmi_amount");
			sumAmountItemsByWhMov += pmConnWhMovItem.getDouble("whmi_amount");
			%>      
			<tr class="reportCellEven">
				<%= HtmlUtil.formatReportCell(sFParams, "" + iWhMovItem, BmFieldType.NUMBER) %>
				<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getProductId())) { %>
						<%= HtmlUtil.formatReportCell(sFParams, "" +  HtmlUtil.stringToHtml(pmConnWhMovItem.getString("products", "prod_code") + " " + pmConnWhMovItem.getString("products", "prod_name")), BmFieldType.STRING) %>
				<%	}%>
				<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getSerial())) { %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnWhMovItem.getString("whmovitems", "whmi_serial"), BmFieldType.STRING) %>
				<%	}%>
				<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getQuantity())) { %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnWhMovItem.getDouble("whmi_quantity"), BmFieldType.NUMBER) %>
				<%	}%>
				<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getAmount())) { %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnWhMovItem.getDouble("whmi_amount"), BmFieldType.CURRENCY) %>
				<%	}%>
			</tr>
			<%
			iWhMovItem++;
			iWhMovItemTotal++;
		} // pmConnWhMovItems
		%>
		<tr class="reportCellEven reportCellCode">
			<td class="reportCellText" colspan="<%= dynamicColspan - dynamicColspanMinus%>"> 
				&nbsp;
			</td>
			<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getQuantity())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumQuantityItemsByWhMov, BmFieldType.NUMBER) %>
			<%	}%>
			<%	if (sFParams.isFieldEnabled(bmoWhMovItem.getAmount())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumAmountItemsByWhMov, BmFieldType.CURRENCY) %>
			<%	}%>
		</tr>
		<%
		iWhMov++;
	} // pmConnWhMovement
	if (iWhMovItemTotal > 0) {
		%>
		<tr><td colspan="<%= dynamicColspan%>">&nbsp;</td></tr>
		<tr class="reportCellCode reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + iWhMovItemTotal, BmFieldType.NUMBER) %>
			<%	// Le quito una columna porque ya se coloco en la linea anterior %>
			<td colspan="<%= ((dynamicColspan-1) - dynamicColspanMinus) %>">&nbsp;</td>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumQuantityItemsTotal, BmFieldType.NUMBER) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumAmountItemsTotal, BmFieldType.CURRENCY) %>
		</tr>
<%	} %>
</table>
<%
		
	}// Fin de if(no carga datos)
	pmConnCount.close();
	pmConnWhMovement.close();
	pmConnWhMovItem.close();
%>  
<%	if (print.equals("1")) { %>
		<script>
		//window.print();
		</script>
<% 	}
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	}// FIN DEL CONTADOR
%>
	</body>
</html>
