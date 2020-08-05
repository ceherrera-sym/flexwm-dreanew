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
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes de Maximos y Minimos por Productos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
    String sql = "", where = "";
    
    String prodStatus = "", prodType = "", prodTrack = "", productFamilyId = "", productGroupId = "";
    String filters = "", fullName = "", dateMaintenanceStart = "", dateMaintenanceEnd = "", dateNextMaintenanceStart = "", dateNextMaintenanceEnd = "";
    int supplierId = 0, unitId = 0, cols= 0;
    
	int programId = 0;
    
    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));       
    if (request.getParameter("prod_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("prod_supplierid"));    
    if (request.getParameter("prod_productfamilyid") != null) productFamilyId = request.getParameter("prod_productfamilyid");
    if (request.getParameter("prod_productgroupid") != null) productGroupId = request.getParameter("prod_productgroupid");
    if (request.getParameter("prod_unitid") != null) unitId = Integer.parseInt(request.getParameter("prod_unitid"));
    if (request.getParameter("prod_type") != null) prodType = request.getParameter("prod_type");
    if (request.getParameter("prod_track") != null) prodTrack = request.getParameter("prod_track");    
    
    // Filtros listados
    if (supplierId > 0) {
          where += " and prod_supplierid = " + supplierId;
          filters += "<i>Proveedor: </i>" + request.getParameter("prod_supplieridLabel") + ", ";
    }
    
    if (!productFamilyId.equals("")) {
    	where += SFServerUtil.parseFiltersToSql("prod_productfamilyid", productFamilyId);
        filters += "<i>Familia: </i>" + request.getParameter("prod_productfamilyidLabel") + ", ";
    }
    
    if (!productGroupId.equals("")) {
    	where += SFServerUtil.parseFiltersToSql("prod_productgroupid", productGroupId);
        filters += "<i>Grupo: </i>" + request.getParameter("prod_productgroupidLabel") + ", ";
    }
    
    if (unitId > 0) {
        where += " and prod_unitid = " + unitId;
        filters += "<i>Unidad: </i>" + request.getParameter("prod_unitidLabel") + ", ";
    }
    
    if (!prodType.equals("")) {
        where += " and prod_type like '" + prodType + "'";
        filters += "<i>Tipo: </i>" + request.getParameter("prod_typeLabel") + ", ";
    }
    
    if (!prodTrack.equals("")) {
        where += " and prod_track like '" + prodTrack + "'";
        filters += "<i>Rastreo: </i>" + request.getParameter("prod_trackLabel") + ", ";
    }
    /*
    if(!(dateMaintenanceStart.equals(""))){
    	where += " AND eqsv_date >= '" + dateMaintenanceStart+ "'";
    	filters += "<i>Fecha Mto. Incio: </i>" + request.getParameter("datemaintenancestart") + ", ";
    }
	
	if(!(dateMaintenanceEnd.equals(""))){
    	where += " AND eqsv_date <= '" + dateMaintenanceEnd+ "'";
    	filters += "<i>Fecha Mto. Fin: </i>" + request.getParameter("datemaintenanceend") + ", ";
    }
	
	if(!(dateNextMaintenanceStart.equals(""))){
    	where += " AND eqsv_nextdate >= '" + dateNextMaintenanceStart+ "'";
    	filters += "<i>Fecha Sig. Mto. Incio: </i>" + request.getParameter("datenextmaintenancestart") + ", ";
    }
	
	if(!(dateNextMaintenanceEnd.equals(""))){
    	where += " AND eqsv_nextdate <= '" + dateNextMaintenanceEnd + "'";
    	filters += "<i>Fecha Sig. Mto. Fin: </i>" + request.getParameter("datenextmaintenanceend") + " ";
    }*/
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmProduct(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
    sql = " SELECT count(contador) AS contador FROM(Select Count(*) AS contador FROM" + SQLUtil.formatKind(sFParams, " products ") +
      	  " INNER JOIN"+ SQLUtil.formatKind(sFParams," whstocks")+" ON (prod_productid = whst_productid) " +
      	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON (whst_whsectionid = whse_whsectionid) " +
      	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " warehouses")+" ON (whse_warehouseid = ware_warehouseid) " +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = prod_supplierid) " +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" on (prod_productfamilyid = prfm_productfamilyid) " +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" on (prod_productgroupid = prgp_productgroupid) " +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " units")+" on (prod_unitid = unit_unitid) " +
            " WHERE prod_productid > 0 " +  where +      
      	  "  GROUP BY prod_productid, ware_warehouseid)AS alias";
			int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			pmConnCount.close();
			System.out.println("contador DE REGISTROS --> "+count);
		
    
    sql = " SELECT prod_code, prod_name, supl_code, supl_name, prod_stockmax,prod_stockmin, prod_brand, prod_model, prod_reorder, unit_code, " +
    	  " ware_name, SUM(whst_quantity) as sumprod FROM " + SQLUtil.formatKind(sFParams, " products ") +
    	  " INNER JOIN"+ SQLUtil.formatKind(sFParams," whstocks")+" ON (prod_productid = whst_productid) " +
    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON (whst_whsectionid = whse_whsectionid) " +
    	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " warehouses")+" ON (whse_warehouseid = ware_warehouseid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = prod_supplierid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" on (prod_productfamilyid = prfm_productfamilyid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" on (prod_productgroupid = prgp_productgroupid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " units")+" on (prod_unitid = unit_unitid) " +
          " WHERE prod_productid > 0 " +  where +      
    	  " GROUP BY prod_productid, ware_warehouseid ";
          System.out.println("slql"+sql);
          
    PmConn pmProduct = new PmConn(sFParams);
    pmProduct.open();
    
    pmProduct.doFetch(sql);
    BmoProduct bmoProduct = new BmoProduct();

    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
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
    <title>:::<%= appTitle %>:::</title>
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
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<%
//if que muestra el mensajede error
if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
	%>
	
			<%=messageTooLargeList %>
	<% 
}else{
%>
<br>
<table class="report">
        <tr class="">
            <td class="reportHeaderCellCenter">#</td>
            <td class="reportHeaderCell">Almacen</td>
            <td class="reportHeaderCell">Clave</td>
            <td class="reportHeaderCell">Nombre</td>
            <td class="reportHeaderCell">Proveedor</td>
            <td class="reportHeaderCell">Tipo</td>
            <td class="reportHeaderCell">Marca</td>
            <td class="reportHeaderCell">Modelo</td>
            <td class="reportHeaderCell">Unidad</td>
            <td class="reportHeaderCellRight">Maximo</td>
            <td class="reportHeaderCellRight">Minimo</td>
            <td class="reportHeaderCellRight">Reorden</td>
            <td class="reportHeaderCellRight">Existencias</td>            
        </tr>
        <%
           int i = 0;
           while(pmProduct.next()) {
        	   
        %>      
        <tr class="reportCellEven">
                 <%= HtmlUtil.formatReportCell(sFParams, "" + (i + 1),BmFieldType.NUMBER) %>
                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("warehouses", "ware_name"), BmFieldType.CODE) %>
                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_code"), BmFieldType.CODE) %>
                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_name"), BmFieldType.STRING)) %>
                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("suppliers", "supl_code") + " " + pmProduct.getString("suppliers", "supl_name"), BmFieldType.STRING)) %>
                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoProduct.getType().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_brand"), BmFieldType.STRING)) %>
                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_model"), BmFieldType.STRING)) %>
                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("units", "unit_code"), BmFieldType.CODE) %>
                 <%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getInt("prod_reorder"), BmFieldType.NUMBER) %>
                 <%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getInt("prod_stockmax"), BmFieldType.NUMBER) %>                 
                 <%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getInt("prod_stockmin"), BmFieldType.NUMBER) %>
                 <%= HtmlUtil.formatReportCell(sFParams, "" + pmProduct.getDouble("sumprod"), BmFieldType.NUMBER) %>
             </tr>
        <%
           i++;
           } //pmProduct
        %>    
</table>
<%

	}// Fin de if(no carga datos)
		pmConnCount.close();
   pmProduct.close();
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
