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
<%@page import="java.util.Date"%>
<%@include file="/inc/login.jsp" %>

<% 
    // Inicializar variables
    String title = "Reportes de Tx. de Inventarios por Mes";
	BmoWhMovement bmoWhMovement = new BmoWhMovement();
	BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	StringTokenizer stType =  new StringTokenizer("");

	String sql = "", where = "", sqlItems = "", filters = "", whereProduct = "", filtersOrderBy = "", orderBy = "", orderBySecondSql = "", groupBy = "", filtersGroupBy = "",
			type = "", whmvStatus = "", dateMovStart = "", dateMovEnd = "";
	int programId = 0, companyId = 0, warehouseId = 0, toWhSectionId = 0, productId = 0, stTypeCount = 0,
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
		where += SFServerUtil.parseFiltersToSql("whmv_type", type.trim());
		filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getType())) + ": </i>" +
				request.getParameter("whmv_typeLabel") + ", ";
			
		stType = new StringTokenizer(type.trim(), ":");
		stTypeCount = stType.countTokens();
		
	} else {
		// Seleccionar todos los tipos(ya que el reporte muestra info por tipo) en caso de que no seleccione ninguno
    	type = BmoWhMovement.TYPE_IN + ":" + BmoWhMovement.TYPE_IN_DEV + ":" + BmoWhMovement.TYPE_IN_ADJUST + ":" +
    			BmoWhMovement.TYPE_OUT + ":" + BmoWhMovement.TYPE_OUT_ADJUST + ":" + BmoWhMovement.TYPE_OUT_DEV + ":" +
    			BmoWhMovement.TYPE_TRANSFER + ":" + BmoWhMovement.TYPE_RENTAL_OUT + ":" + BmoWhMovement.TYPE_RENTAL_IN + ":";
    
    	where += SFServerUtil.parseFiltersToSql("whmv_type", type.trim());
		filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getType())) + ": </i>Todos, ";
		stTypeCount = 9;
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
        		sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getBmoToWhSection().getWarehouseId())) + " :</i>" + 
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
    groupBy += bmoWhMovItem.getProductId().getName().toString() + "";
    filtersGroupBy += HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovItem.getProductId())) + ".";
    
    // Ordenar registros por:
    // Fecha WhMov
    orderBy += " ORDER BY MONTH(whmv_datemov) " + BmOrder.ASC + " ";
    filtersOrderBy +=   "Mes " + BmOrder.ASC + ", ";
    // Producto (Segundo/Tercer consulta SQL)
    orderBySecondSql += " ORDER BY " + bmoWhMovItem.getProductId().getName().toString() + " " + BmOrder.ASC;
    filtersOrderBy += HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovItem.getProductId())) + " " + BmOrder.ASC;
    
    // Conexion BD
    PmConn pmConnWhMovement = new PmConn(sFParams);
    pmConnWhMovement.open();
    
    PmConn pmConnWhMovItem = new PmConn(sFParams);
    pmConnWhMovItem.open();
    
    PmConn pmConnProduct = new PmConn(sFParams);
    pmConnProduct.open();
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
			<b>Agrupado por:</b> Mes, <%= filtersGroupBy%>
			<b>Ordenado por:</b> <%= filtersOrderBy%>
			</td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report">
	<%
	// Arreglo (totales)para cada columna de cada tipo, sin considerar la celda vacia
	double[][] sumByTotal = new double[1][(stTypeCount * 2)];

	// Fecha Mes
	sql = " SELECT whmv_datemov FROM whmovements " +
			" LEFT JOIN whsections ON (whse_whsectionid = whmv_towhsectionid) " +
			" LEFT JOIN warehouses ON (ware_warehouseid = whse_warehouseid) " +
			" LEFT JOIN companies ON (comp_companyid = whmv_companyid) " +
			" LEFT JOIN orderdeliveries ON (odly_orderdeliveryid = whmv_orderdeliveryid) " +
			" LEFT JOIN requisitionreceipts ON (rerc_requisitionreceiptid = whmv_requisitionreceiptid) " +
			" WHERE whmv_whmovementid > 0 " + 
			where;
	if (productId > 0) {
		sql += " AND whmv_whmovementid IN ( "+
				" SELECT whmi_whmovementid FROM whmovitems " +
				" WHERE whmi_productid > 0 " +
				whereProduct +
				" ) ";
	}
	sql += " GROUP BY MONTH(whmv_datemov) "	+ orderBy;
	//System.out.println("sql1: "+sql);
	pmConnWhMovement.doFetch(sql);
	while(pmConnWhMovement.next()) {
		// Cada mes volver a llenar los tipos de mov
		stType = new StringTokenizer(type.trim(), ":");
		// Arreglo (total por mes) para cada columna de cada tipo, sin considerar la celda vacia
		double[][] sumByMonth = new double[1][(stTypeCount * 2)];
		%>
		<tr class="reportGroupCell">
		<%
		// colspan;
		// 1 (por la columna Producto) + 
		// (se multiplica por 3, porque cada Tipo tendra 3 columnas incluidas(celdaVacia, cantidad, monto)) 
		%>
			<th colspan="<%= 1 + (stTypeCount * 3) %>" align="center">
				<%= FlexUtil.getMonthName(sFParams, pmConnWhMovement.getString("whmv_datemov"))%> 
				<%= FlexUtil.getYear(sFParams, pmConnWhMovement.getString("whmv_datemov"))%> 
			</th>
		</tr>
		<tr class="reportCellEven ">
			<th class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhMovement.getType()))%>
			</th>
			<% 
			while (stType.hasMoreTokens()) {
				bmoWhMovement.getType().setValue(stType.nextToken().trim());
				%>
				<td style="border-style: hidden">&nbsp;</td>
				<th class="reportHeaderCellCenter" colspan="2">
					<%= HtmlUtil.stringToHtml(bmoWhMovement.getType().getSelectedOption().getLabel())%>
				</th>
				<%
			}
			%>
		</tr>
		<tr class="reportCellEven">
			<th class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoWhMovItem.getProgramCode().toString(), bmoWhMovItem.getProductId()))%>
			</th>
		<%
			for (int n = 1; n <= stTypeCount; n++) {
		%>
			<td style="border-style: hidden">&nbsp;</td>
			<th class="reportHeaderCellCenter">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoWhMovItem.getProgramCode().toString(), bmoWhMovItem.getQuantity()))%>
			</th>
			<th class="reportHeaderCellRight">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoWhMovItem.getProgramCode().toString(), bmoWhMovItem.getAmount()))%>
			</th>
		<%	}%>
		</tr>
		<%
		// Sacar productos con filtros
		String sqlProd = "SELECT whmi_productid, prod_code, prod_name FROM whmovitems " +
				" LEFT JOIN products ON (prod_productid = whmi_productid) " +
				" LEFT JOIN whmovements ON (whmv_whmovementid = whmi_whmovementid) " +
				" LEFT JOIN whsections ON (whse_whsectionid = whmv_towhsectionid) " +
				" WHERE whmv_whmovementid > 0 " +
				whereProduct +
				where +
				" AND MONTH(whmv_datemov) = " + FlexUtil.getMonth(sFParams, pmConnWhMovement.getString("whmv_datemov")) +
				" AND YEAR(whmv_datemov) = " + FlexUtil.getYear(sFParams, pmConnWhMovement.getString("whmv_datemov")) +
				" GROUP BY whmi_productid " +
				orderBySecondSql;
				
		//System.out.println("sqlProd: "+sqlProd);
		pmConnProduct.doFetch(sqlProd);
		int countProd = 0;
		while (pmConnProduct.next()) {
			// En cada producto volver a llenar los tipos de mov
			stType = new StringTokenizer(type.trim(), ":");
			%>
			<tr class="reportCellEven">
				<%= HtmlUtil.formatReportCell(sFParams, "" +  HtmlUtil.stringToHtml(pmConnProduct.getString("products", "prod_code") + " " + 
					pmConnProduct.getString("products", "prod_name")), BmFieldType.STRING) %>
			<%
			int countWhMovItem = 0;
			while (stType.hasMoreTokens()) {
				sqlProd = "SELECT SUM(whmi_quantity) AS quantitySum, SUM(whmi_amount) AS amountSum, whmv_type AS whType FROM whmovitems " +
						" LEFT JOIN products ON (prod_productid = whmi_productid) " +
						" LEFT JOIN whmovements ON (whmv_whmovementid = whmi_whmovementid) " +
						" LEFT JOIN whsections ON (whse_whsectionid = whmv_towhsectionid) " +
						" WHERE whmv_whmovementid > 0 " +
						" AND whmi_productid = " + pmConnProduct.getInt("whmi_productid") +
						where +
						" AND whmv_type = '" + stType.nextToken().trim() + "' "+
						" AND MONTH(whmv_datemov) = " + FlexUtil.getMonth(sFParams, pmConnWhMovement.getString("whmv_datemov")) +
						" AND YEAR(whmv_datemov) = " + FlexUtil.getYear(sFParams, pmConnWhMovement.getString("whmv_datemov")) +
						" GROUP BY whmi_productid " +
						orderBySecondSql;
				
				//System.out.println("sqlProdItems: "+sqlProd);
				pmConnWhMovItem.doFetch(sqlProd);
				
				if (pmConnWhMovItem.next()) {
					// Almacenar cantidad y monto de cada tipo, por mes, y total
					sumByMonth[countProd][countWhMovItem] += pmConnWhMovItem.getDouble("quantitySum");
					sumByTotal[countProd][countWhMovItem] += pmConnWhMovItem.getDouble("quantitySum");
					countWhMovItem++;
					sumByMonth[countProd][countWhMovItem] += pmConnWhMovItem.getDouble("amountSum");
					sumByTotal[countProd][countWhMovItem] += pmConnWhMovItem.getDouble("amountSum");
					countWhMovItem++;
					%>
					<td style="border-style: hidden">&nbsp;</td>
					<%= HtmlUtil.formatReportCell(sFParams, "" +  pmConnWhMovItem.getDouble("quantitySum"), BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" +  pmConnWhMovItem.getDouble("amountSum"), BmFieldType.CURRENCY) %>
					<%
				} else {
					sumByMonth[countProd][countWhMovItem] += 0;
					sumByTotal[countProd][countWhMovItem] += 0;
					countWhMovItem++;
					sumByMonth[countProd][countWhMovItem] += 0;
					sumByTotal[countProd][countWhMovItem] += 0;
					countWhMovItem++;
					%>
					<td style="border-style: hidden">&nbsp;</td>
					<%= HtmlUtil.formatReportCell(sFParams, "" +  0.0, BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" +  0.0, BmFieldType.CURRENCY) %>
					<%
				}
			} // Fin stType por cada producto
			%>
			</tr>
			<%
		} // pmConnProduct
		%>
		<tr class="reportCellCode reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "Total Mes", BmFieldType.CODE) %>
			<%
			// Total por Mes
			for (int n=0; n < 1; n++) {
				// firstColumn; Cambiar tipo de dato para las dos columnas
				boolean firstColumn = true;
				for(int j=0; j < sumByMonth[0].length; j++) {
					// Poner celda vacia
					if ((j % 2) == 0) {
						%>
						<td style="border-style: hidden">&nbsp;</td>
			<%		}
					if (firstColumn) {
						firstColumn = false;
					%>
						<%= HtmlUtil.formatReportCell(sFParams, "" + sumByMonth[n][j], BmFieldType.NUMBER)%>
					<%
					} else {
						firstColumn = true;
					%>
						<%= HtmlUtil.formatReportCell(sFParams, "" + sumByMonth[n][j], BmFieldType.CURRENCY)%>
			<%		}
				}
			}
			%>
		</tr>
	<%	
	} // pmConnWhMovement
	%>
	<tr class=""><td colspan="<%= 1 + (stTypeCount * 3) %>">&nbsp;</td></tr>
	<tr class="reportCellCode reportCellEven">
		<%= HtmlUtil.formatReportCell(sFParams, "Total", BmFieldType.CODE) %>
		<%
		// Total General
		for (int m=0; m < 1; m++) {
			// firstColumn; Cambiar tipo de dato para las dos columnas
			boolean firstColumn = true;
			for(int k=0; k < sumByTotal[0].length; k++) {
				// Poner celda vacia
				if ((k % 2) == 0) {
					%>
					<td style="border-style: hidden">&nbsp;</td>
		<%		}
				if (firstColumn) {
					firstColumn = false;
				%>
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumByTotal[m][k], BmFieldType.NUMBER)%>
				<%
				} else {
					firstColumn = true;
				%>
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumByTotal[m][k], BmFieldType.CURRENCY)%>
		<%		}
			}
		}
		%>
	</tr>
</table>
<%
	}// Fin de if(no carga datos)
    pmConnProduct.close();
    pmConnWhMovItem.close();
	pmConnWhMovement.close();
%>  
<%	if (print.equals("1")) { %>
		<script>
		//window.print();
		</script>
<% 	} %>
	</body>
</html>
