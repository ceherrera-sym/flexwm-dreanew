<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="com.flexwm.server.op.PmWarehouse"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.sql.Types"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>
<% 
	// Inicializar variables
 	String title = "Existencias en Almacen";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
 	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	
   	String sql = "", where = "", filters = "", groupFilter = "",
   			wareType = "";
   	int programId = 0, wareHouseId = 0, whSectionId = 0, productId = 0;

   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("whse_warehouseid") != null) wareHouseId = Integer.parseInt(request.getParameter("whse_warehouseid"));
    if (request.getParameter("whst_whsectionid") != null) whSectionId = Integer.parseInt(request.getParameter("whst_whsectionid"));
    if (request.getParameter("whst_productid") != null) productId = Integer.parseInt(request.getParameter("whst_productid"));
    if (request.getParameter("ware_type") != null) wareType = request.getParameter("ware_type");

    // Filtros listados
    if (wareHouseId > 0) {
    	groupFilter += " AND ware_warehouseid = " + wareHouseId;
    	filters += "<i>Almac&eacute;n: </i>" + request.getParameter("whse_warehouseidLabel") + ", ";
    }
    
    if (whSectionId > 0) {
    	where += " AND whse_whsectionid = " + whSectionId;
    	filters += "<i>Secci&oacute;n Almac&eacute;n: </i>" + request.getParameter("whst_whsectionidLabel") + ", ";
    }
    
    if (!(wareType.equals(""))) {
        where += " AND ware_type = '" + wareType + "'";
        filters += "<i>Tipo Almac&eacute;n: </i>" + request.getParameter("ware_typeLabel")+ ", ";
    }
    
    if (productId > 0) {
    	where += " AND whst_productid = " + productId;
    	filters += "<i>Producto: </i>" + request.getParameter("whst_productidLabel") + ", ";
    }
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmWarehouse(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    //Conexion a Base de Datos
	PmConn pmConn = new PmConn(sFParams);
    pmConn.open();
	
	PmConn pmConnGroup = new PmConn(sFParams);
	pmConnGroup.open();
   	
    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
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

	<table border="0" cellspacing="0" cellpading="0" width="100%">
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
				<b>Agrupdo por:</b> Almac&eacute;n, <b>Ordenado por:</b> Secci&oacute;n Alm.
			</td>
		<td class="reportDate" align="right">
				Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
			</td>
		</tr>
	</table>
	<br>
	<table class="report">
		<% 

   		sql = " SELECT COUNT(*) AS contador FROM "+ SQLUtil.formatKind(sFParams," whstocks ") +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," whsections")+" ON(whse_whsectionid = whst_whsectionid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," warehouses")+" ON(ware_warehouseid = whse_warehouseid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," cities")+" ON(city_cityid = ware_cityid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," states")+" ON(stat_stateid = city_stateid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" ON(user_userid = ware_userid) " +
				" WHERE whst_whsectionid > 0 " +
				groupFilter + where +
				" ORDER BY ware_name ASC";
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
		
		sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams," whstocks ") +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," whsections")+" ON(whse_whsectionid = whst_whsectionid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," warehouses")+" ON(ware_warehouseid = whse_warehouseid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," cities")+" ON(city_cityid = ware_cityid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," states")+" ON(stat_stateid = city_stateid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" ON(user_userid = ware_userid) " +
				" WHERE whst_whsectionid > 0 " +
				groupFilter + where +
				" GROUP BY ware_warehouseid " +
				" ORDER BY ware_name ASC";
				
		System.out.println("sql1: "+sql);
		pmConnGroup.doFetch(sql);
		int iTotal = 0; 
		double iWareTotal = 0;
	    while(pmConnGroup.next()) {
			
			sql = " SELECT * FROM"+ SQLUtil.formatKind(sFParams," whstocks ") +
					" LEFT JOIN"+ SQLUtil.formatKind(sFParams," whsections")+" ON(whse_whsectionid = whst_whsectionid) " +
					" LEFT JOIN"+ SQLUtil.formatKind(sFParams," warehouses")+" ON(ware_warehouseid = whse_warehouseid) " +
					" LEFT JOIN"+ SQLUtil.formatKind(sFParams," products")+" ON(prod_productid = whst_productid) " +	
					" LEFT JOIN"+ SQLUtil.formatKind(sFParams," whtracks")+" ON(whtr_whtrackid = whst_whtrackid) " +					
					" WHERE whst_whstockid > 0 " +
					" AND ware_warehouseid = " + pmConnGroup.getInt("ware_warehouseid") +
					where +
					" ORDER BY whse_whsectionid ASC, whse_description ASC, prod_productid ASC, prod_model ASC";
					
			System.out.println("sql2: "+sql);
			
			pmConn.doFetch(sql);
			int i = 1, wareId = 0;
			double iWare = 0;
		    while (pmConn.next()) {
		    	iWare += pmConn.getDouble("whst_quantity");
		    	iWareTotal += pmConn.getDouble("whst_quantity");
		    	
		    	if((pmConnGroup.getInt("warehouses", "ware_warehouseid") != wareId)) {
		    		wareId = pmConnGroup.getInt("warehouses", "ware_warehouseid");
			    	%>
	
			    	 <tr>
						<td colspan="15" class="reportGroupCell">
							<b>Almac&eacute;n:</b>
							<%= HtmlUtil.stringToHtml(pmConnGroup.getString("ware_name")) %> |
						    Domicilio: <%= HtmlUtil.stringToHtml(pmConnGroup.getString("ware_street")) %> 
						    #  <%= pmConnGroup.getString("ware_number") %>
						    <%= HtmlUtil.stringToHtml(pmConnGroup.getString("ware_neighborhood")) %>,
						    <%= HtmlUtil.stringToHtml(pmConnGroup.getString("city_name")) %>,
						    <%= HtmlUtil.stringToHtml(pmConnGroup.getString("stat_name")) %> |
						    Responsable: <%= pmConnGroup.getString("user_code") %> |
						    Tel. <%= pmConnGroup.getString("ware_officephone") %>
						</td>
					</tr>
				    <tr>
				        <td class="reportHeaderCellCenter">#</td>
				        <td class="reportHeaderCell">Clave Secc.</td>
				        <td class="reportHeaderCell">Nombre Secc.</td>
				        <td class="reportHeaderCell">Descripci&oacute;n</td>
				        <td class="reportHeaderCell">Producto</td>
				        <td class="reportHeaderCell">Modelo</td>
				        <td class="reportHeaderCell">#Serie/Lote</td>
				        <td class="reportHeaderCellCenter">Cantidad</td>
				    </tr>
			    <% } %>
		    	<tr class="reportCellEven">
	                <%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
	                <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("whsections", "whse_name"), BmFieldType.CODE) %>
	                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("whsections", "whse_name"), BmFieldType.STRING)) %>
	                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("whsections", "whse_description"), BmFieldType.STRING)) %>
	                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("products", "prod_code") + " " + pmConn.getString("products", "prod_name"), BmFieldType.STRING)) %>
	                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("products", "prod_model"), BmFieldType.STRING)) %>
	                <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("whtracks", "whtr_serial"), BmFieldType.STRING) %>
	                <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("whst_quantity"), BmFieldType.NUMBER) %>
		        </tr>
			   <%
			   i++;
			   iTotal++;
		    } //pmConn
		    %>
		    <!--
		    <tr class="reportCellEven reportCellCode">
		    	<td class="reportCellText" colspan="7">&nbsp;</td>
		    	<%//= HtmlUtil.formatReportCell(sFParams, "" + iWare, BmFieldType.NUMBER) %>
		    </tr>
		    
		    -->
		    <%
	    } //pmConnGroup
	    
	    //if (iTotal > 0 || iWareTotal > 0) {
    %>
	    <tr><td colspan="8">&nbsp;</td></tr>
	    <!--
	    <tr class="reportCellEven reportCellCode">
	    	<%//= HtmlUtil.formatReportCell(sFParams, "" + iTotal, BmFieldType.NUMBER) %>
	    	<td class="reportCellText" colspan="6">&nbsp;</td>
	    	<%//= HtmlUtil.formatReportCell(sFParams, "" + iWareTotal, BmFieldType.NUMBER) %>
	    </tr>
	    -->
    <%	//} %>
    </table>
    
<%
	
	}// Fin de if(no carga datos)
	pmConnCount.close();
	pmConn.close();
	pmConnGroup.close();
%>
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	}// FIN DEL CONTADOR%>
  </body>
</html>