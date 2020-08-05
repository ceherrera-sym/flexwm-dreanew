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

<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.shared.op.BmoWhTrack"%>
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
    String title = "Reporte General Rastreo de Productos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoWhTrack bmoWhTrack = new BmoWhTrack();
	BmoProduct bmoProduct = new BmoProduct();

	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	FlexUtil flexUtil = new FlexUtil();
    String sql = "", where = "", filters = "", whereProduct = "", filtersOrderBy = "", orderBy = "",  dateMovStart = "", dateMovEnd = "";
    int programId = 0, productId = 0,
    		// dynamicColspan incrementar por cada columna del reporte
    		// dynamicColspanMinus incrementar por cada columna que vaya a mostrar totales(es decir, se va a restar al dynamicColspan si HAY FILA TOTALES)
    		dynamicColspan = 0, dynamicColspanMinus = 0;
    
    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));   	
    if (request.getParameter("datemovstart") != null) dateMovStart = request.getParameter("datemovstart");
    if (request.getParameter("datemovend") != null) dateMovEnd = request.getParameter("datemovend");
    if (request.getParameter("whtr_productid") != null) productId = Integer.parseInt(request.getParameter("whtr_productid"));
    
    // Modulo principal de Reportes
	bmoProgram = (BmoProgram)pmProgram.get(programId);

    // Filtros listados
    if (!dateMovStart.equals("")) {
    	where += " AND whtr_datemov >= '" + dateMovStart + " 00:00' ";
    	filters += "<i>" + HtmlUtil.stringToHtml("Fecha Mov Inicio") + " : </i>" + 
    			dateMovStart + ", ";
    }

    if (!dateMovEnd.equals("")) {
    	where += " AND whtr_datemov <= '" + dateMovEnd + " 23:59' ";
    	filters += "<i> "+ HtmlUtil.stringToHtml("Fecha Mov Fin") + " : </i>" 
    			+ dateMovEnd + ", ";
    }
 
    if (productId > 0) {
    	where += " AND whtr_productid = " + productId;
        filters += "<i>" + HtmlUtil.stringToHtml(
        		sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhTrack.getProductId())) +": </i>" + 
    	request.getParameter("whtr_productidLabel") + ", ";
    }
 
    // Obtener disclosure de datos
    String disclosureFilters = new PmWhMovement(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    // Ordenar registros por:
    // Fecha WhMov
    orderBy += " ORDER BY " + bmoWhTrack.getDatemov().getName() + " " + BmOrder.DESC + ", ";
    filtersOrderBy += HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhTrack.getDatemov())) + " " + BmOrder.DESC + ", ";
   
 	// Id Producto
    orderBy += bmoWhTrack.getProductId().getName() + " " + BmOrder.ASC + "";
    filtersOrderBy += HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhTrack.getProductId())) + " " + BmOrder.ASC;
  
    
    // Conexion BD
    PmConn pmConWhTracks = new PmConn(sFParams);
    pmConWhTracks.open();
    
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
		<%	if (sFParams.isFieldEnabled(bmoProduct.getCode())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProduct.getProgramCode(), bmoProduct.getCode()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoProduct.getName())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProduct.getProgramCode(), bmoProduct.getName()))%></td>
		<%	}	%>
		<%	if (sFParams.isFieldEnabled(bmoProduct.getType())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProduct.getProgramCode(), bmoProduct.getType()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhTrack.getSerial())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhTrack.getSerial()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhTrack.getDatemov())) {
				dynamicColspan++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhTrack.getDatemov()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhTrack.getInQuantity())) {
				dynamicColspan++;
				dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhTrack.getInQuantity()))%></td>
		<%	}%>
		<%	if (sFParams.isFieldEnabled(bmoWhTrack.getOutQuantity())) {
				dynamicColspan++;
				dynamicColspanMinus++;
		%>
				<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoWhTrack.getOutQuantity()))%></td>
		<%	}%>
		
	</tr>
	<%

	sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, "whtracks") 
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = whtr_productid) " 
			+ " WHERE whtr_whtrackid > 0 " 
			+ where 
			+ orderBy;
			
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
	
	sql = " SELECT prod_code, prod_name, prod_type, whtr_serial, whtr_datemov, whtr_inquantity, whtr_outquantity " 
			+ " FROM " + SQLUtil.formatKind(sFParams, "whtracks") 
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = whtr_productid) " 
			+ " WHERE whtr_whtrackid > 0 " 
			+ where 
			+ orderBy;

	System.out.println("sqlWHTR: " + sql);
	
	pmConWhTracks.doFetch(sql);
	int i = 1;
	double sumInQuantity = 0, sumOutQuantity = 0;
	double sumAmountTotal = 0, sumWhMovItemsAmountTotal = 0, sumWhMovItemsQuantityTotal = 0;
	
	while(pmConWhTracks.next()) {
		// Asignar Tipo de Producto
		bmoProduct.getType().setValue(pmConWhTracks.getString("products", "prod_type"));
		
		// Sumar el almacenes
		sumInQuantity += pmConWhTracks.getDouble("whtracks", "whtr_inquantity");
		sumOutQuantity += pmConWhTracks.getDouble("whtracks", "whtr_outquantity");

		%>      
		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
			<%	if (sFParams.isFieldEnabled(bmoProduct.getCode())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConWhTracks.getString("products", "prod_code"), BmFieldType.CODE) %>
			<%	}%>
			
			<%	if (sFParams.isFieldEnabled(bmoProduct.getName())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmConWhTracks.getString("products", "prod_name")), BmFieldType.STRING) %>
			<%	}%>
			
			<%	if (sFParams.isFieldEnabled(bmoProduct.getType())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoProduct.getType().getSelectedOption().getLabel()), BmFieldType.STRING) %>
			<%	}%>
			
			<%	if (sFParams.isFieldEnabled(bmoWhTrack.getSerial())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConWhTracks.getString("whtracks", "whtr_serial"), BmFieldType.STRING) %>
			<%	}%>
			
			<%	if (sFParams.isFieldEnabled(bmoWhTrack.getDatemov())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConWhTracks.getString("whtracks", "whtr_datemov"), BmFieldType.DATE) %>
			<%	}%>
			
			<%	if (sFParams.isFieldEnabled(bmoWhTrack.getInQuantity())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmConWhTracks.getDouble("whtr_inquantity"), BmFieldType.NUMBER) %>
			<%	}%>
			
			<%	if (sFParams.isFieldEnabled(bmoWhTrack.getOutQuantity())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmConWhTracks.getDouble("whtr_outquantity"), BmFieldType.NUMBER) %>
			<%	}%>
		</tr>
		<%
		i++;

	} // pmConnWhMovement
	%>
	<tr><td colspan="<%= dynamicColspan %>">&nbsp;</td></tr>
	<%	if (sumInQuantity > 0 || sumOutQuantity > 0) { %>
			<tr class="reportCellCode reportCellEven">
				<td colspan="<%= dynamicColspan - dynamicColspanMinus %>">&nbsp;</td>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumInQuantity, BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumOutQuantity, BmFieldType.NUMBER) %>
			</tr>
	<%	} %>
</table>
<%
	}// Fin de if(no carga datos)
	pmConnCount.close();
	pmConWhTracks.close();

	if (print.equals("1")) { %>
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
