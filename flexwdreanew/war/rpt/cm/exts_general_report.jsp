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
 
<%@include file="/inc/login.jsp" %>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
 	String title = "Reportes General Ventas Externas ";
	
	
   	String sql = "", sql2 = "",where = "", filters = "";
   
   	int programId = 0, customerId = 0, producId = 0,count = 0;
   	
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("exts_customerid") != null) customerId = Integer.parseInt(request.getParameter("exts_customerid"));
    if (request.getParameter("exts_productid") != null) producId = Integer.parseInt(request.getParameter("exts_productid"));
  
    bmoProgram = (BmoProgram)pmProgram.get(programId);
	// Construir filtros 
    
    if (customerId > 0) {
        where += " AND exts_customerid = " + customerId;
        filters += "<i>Cliente: </i>" + request.getParameter("exts_customeridLabel") + ", ";
    }
    
    if (producId > 0) {
   	
    	where += " AND exts_productid = " +producId;
		filters += "<i>Producto: </i>" + request.getParameter("exts_productidLabel") ;
    }  
   					  	  		
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
		
		<table border="0" cellspacing="0" cellpading="0" width="100%">
			<tr>
				<td align="left" rowspan="2" valign="top">	
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
					
				</td>
			<td class="reportDate" align="right">
					Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
				</td>
			</tr>
		</table>
		<br>
		<table class="report" width="100%">
			
		<%sql = "SELECT * FROM externalsales " +
				" LEFT JOIN customers ON (cust_customerid = exts_customerid) " +
				" LEFT JOIN products ON (exts_productid = prod_productid)" +
				" WHERE exts_externalsalesid > 0 " + where; 
		  sql2 = "SELECT COUNT(exts_externalsalesid) AS counter FROM externalsales " +
					" LEFT JOIN customers ON (cust_customerid = exts_customerid) " +
					" LEFT JOIN products ON (exts_productid = prod_productid)" +
					" WHERE exts_externalsalesid > 0 " + where; 
		pmConn2.open();
		pmConn2.doFetch(sql2);
		
		if(pmConn2.next())
			count = pmConn2.getInt("counter");
		
		if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){%>
			<%=messageTooLargeList %>
		<%}else{

		pmConn.open();
		pmConn.doFetch(sql);%>
			<tr class="reportCellEven">
				<td class="reportHeaderCellCenter">#</td>
		    	<td class="reportHeaderCell" >Clave</td>
		    	<td class="reportHeaderCell" >Clave/Cliente</td>
		    	<td class="reportHeaderCell" >Cliente</td>
		    	<td class="reportHeaderCell" >Referencia</td>
		    	<td class="reportHeaderCell" >Fecha</td>
		    	<td class="reportHeaderCell" >Clave Prod.</td>
		    	<td class="reportHeaderCell" >Nombre Prod.</td>
		    	<td class="reportHeaderCell" >Cantidad</td>
		    	<td class="reportHeaderCell" >Precio</td>
		    	<td class="reportHeaderCell" >Total</td>
			</tr>
		<%int i = 1;
		while(pmConn.next()){%>
		<tr>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, ""+i, BmFieldType.NUMBER)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("exts_code"), BmFieldType.CODE)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("cust_code"), BmFieldType.CODE)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("cust_displayname"), BmFieldType.STRING)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("exts_extsreference"), BmFieldType.STRING)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("exts_date"), BmFieldType.DATE)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("prod_code"), BmFieldType.CODE)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("prod_description"), BmFieldType.STRING)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("exts_quantity") , BmFieldType.NUMBER)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("exts_price") , BmFieldType.CURRENCY)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("exts_total") , BmFieldType.CURRENCY)) %>
		</tr>
		<%
		i++;
		}%>
		</table>
		
	<%}%>
	<%pmConn.close(); 
	}//Fin carga datos%>
  </body>
</html>