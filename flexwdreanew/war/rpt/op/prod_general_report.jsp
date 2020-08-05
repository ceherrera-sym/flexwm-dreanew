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

<%@page import="com.flexwm.shared.op.BmoProductCompany"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes de Productos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
    
    String sql = "", where = "";
    BmoProductCompany bmoProductCompany =  new BmoProductCompany();
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
    /*
    if (request.getParameter("datemaintenancestart") != null) dateMaintenanceStart = request.getParameter("datemaintenancestart");
   	if (request.getParameter("datemaintenanceend") != null) dateMaintenanceEnd = request.getParameter("datemaintenanceend");
   	if (request.getParameter("datenextmaintenancestart") != null) dateNextMaintenanceStart = request.getParameter("datenextmaintenancestart");
   	if (request.getParameter("datenextmaintenanceend") != null) dateNextMaintenanceEnd = request.getParameter("datenextmaintenanceend");
    */
    // Filtros listados
    if (supplierId > 0) {
          where += " AND prod_supplierid = " + supplierId;
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
        where += " AND prod_unitid = " + unitId;
        filters += "<i>Unidad: </i>" + request.getParameter("prod_unitidLabel") + ", ";
    }
    
    if (!prodType.equals("")) {
        where += " AND prod_type like '" + prodType + "'";
        filters += "<i>Tipo: </i>" + request.getParameter("prod_typeLabel") + ", ";
    }
    
    if (!prodTrack.equals("")) {
        where += " AND prod_track like '" + prodTrack + "'";
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
	
	
	 sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, "products") +
             " LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" ON (supl_supplierid = prod_supplierid) " +
             " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productfamilies")+" ON (prod_productfamilyid = prfm_productfamilyid) " +
             " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productgroups")+" ON (prod_productgroupid = prgp_productgroupid) " +
             " LEFT JOIN " + SQLUtil.formatKind(sFParams, "units")+" ON (prod_unitid = unit_unitid) " +
             " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (ortp_ordertypeid = prod_ordertypeid) " +
             " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wfty_wflowtypeid = prod_wflowtypeid) " +
             " WHERE prod_productid > 0 " + 
             where +
             " ORDER by prod_productid ASC";
            
		int count =0;
		//ejecuto el sql DEL CONTADOR
		pmConnCount.doFetch(sql);
		if(pmConnCount.next())
			count=pmConnCount.getInt("contador");
		pmConnCount.close();
		System.out.println("contador DE REGISTROS --> "+count);
		//if que muestra el mensajede error
			if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
				%>
				
						<%=messageTooLargeList %>
				<% 
			}else{
    
    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "products") +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" ON (supl_supplierid = prod_supplierid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productfamilies")+" ON (prod_productfamilyid = prfm_productfamilyid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, "productgroups")+" ON (prod_productgroupid = prgp_productgroupid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, "units")+" ON (prod_unitid = unit_unitid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes")+" ON (ortp_ordertypeid = prod_ordertypeid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes")+" ON (wfty_wflowtypeid = prod_wflowtypeid) " +
          " WHERE prod_productid > 0 " + 
          where +
          " ORDER by prod_productid ASC";
    
    //System.out.println("sql: " + sql);
    PmConn pmProduct = new PmConn(sFParams);
    pmProduct.open();
    
    PmConn pmCountProduct = new PmConn(sFParams);
    pmCountProduct.open();
    
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
            <td class="reportHeaderCell">Clave</td>
            <td class="reportHeaderCell">Nombre</td>
            <td class="reportHeaderCell">Nombre<br>Comercial</td>
            <td class="reportHeaderCell">Tipo</td>
            
            <td class="reportHeaderCell">Descripci&oacute;n</td>
            <td class="reportHeaderCell">Marca</td>
            <td class="reportHeaderCell">Modelo</td>
            
            <td class="reportHeaderCell">Familia</td>
            <td class="reportHeaderCell">Grupo</td>
            <td class="reportHeaderCell">Proveedor</td>
            <td class="reportHeaderCell">Activo</td>
            <td class="reportHeaderCell">Comisi&oacute;n</td>
            <td class="reportHeaderCell">Consumible</td>
            <!-- 
            <td class="reportHeaderCellRight">Precio<br>Venta</td>
            <td class="reportHeaderCellRight">Precio<br>Renta</td>
            -->
            <td class="reportHeaderCellRight">Costo<br>Compra</td>
            <td class="reportHeaderCellRight">Costo<br>Renta</td>
            <td class="reportHeaderCell">Rastreo</td>
            <td class="reportHeaderCell">Unidad</td>
            <td class="reportHeaderCell">Afecta<br>Inventario</td>
            <td class="reportHeaderCell">Punto<br>Reorden</td>
            <td class="reportHeaderCell">Stok<br>M&aacute;x.</td>            
            <td class="reportHeaderCell">Stock<br>Min</td>
            <!-- <td class="reportHeaderCell">Moneda</td>-->
            <td class="reportHeaderCell">Renovar<br>Pedido</td>
            <td class="reportHeaderCell">%<br>Incremento</td>
            <td class="reportHeaderCell">Tipo<br>Pedido</td>
            <td class="reportHeaderCell">Tipo <br>WFlow</td>
			
            <td class="reportHeaderCellRight">Existencias</td>
            <% 	if (sFParams.hasRead(bmoProductCompany.getProgramCode())) { %>
            		<td class="reportHeaderCellRight">Empresas</td>
            <%	} %>

        </tr>
        <%
           int i = 0;
           while(pmProduct.next()) {
        	   
               //Estatus
        	   bmoProduct.getType().setValue(pmProduct.getString("products", "prod_type"));
        	   bmoProduct.getTrack().setValue(pmProduct.getString("products", "prod_track"));
              
        	   double countProduct = 0;
        	   sql = " SELECT SUM(whst_whstockid) AS countProd FROM " + SQLUtil.formatKind(sFParams, "whstocks") +
  					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = whst_productid) " +	
  					" WHERE whst_whstockid > 0 " +
  					" AND whst_productid = " + pmProduct.getInt("products", "prod_productid");
  					
  				//System.out.println("sql222: "+sql);
  				pmCountProduct.doFetch(sql);
  				if(pmCountProduct.next()) {
  					countProduct = pmCountProduct.getDouble("countProd");
  				}
              
		        %>      
		        <tr class="reportCellEven">
		                 <%= HtmlUtil.formatReportCell(sFParams, "" + (i + 1),BmFieldType.NUMBER) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_code"), BmFieldType.CODE) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_name"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_displayname"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoProduct.getType().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_description"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_brand"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_model"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("productfamilies", "prfm_name"), BmFieldType.CODE)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("productgroups", "prgp_name"), BmFieldType.CODE)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("suppliers", "supl_code") + " " + pmProduct.getString("suppliers", "supl_name") , BmFieldType.STRING)) %>
						 <%= HtmlUtil.formatReportCell(sFParams, (pmProduct.getBoolean("prod_enabled") ? "Si" : "No"), BmFieldType.STRING) %>
		                 <%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("currencies", "cure_code"), BmFieldType.CODE)) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, (pmProduct.getBoolean("prod_commission") ? "Si" : "No"), BmFieldType.STRING) %>
   		                 <%= HtmlUtil.formatReportCell(sFParams, (pmProduct.getBoolean("prod_consumable") ? "Si" : "No"), BmFieldType.STRING) %>
   		                 <%//= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_saleprice"), BmFieldType.CURRENCY) %>                 
		                 <%//= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_rentalprice"), BmFieldType.CURRENCY) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_cost"), BmFieldType.CURRENCY) %>                 
		                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_rentalcost"), BmFieldType.CURRENCY) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoProduct.getTrack().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("units", "unit_code"), BmFieldType.CODE)) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, (pmProduct.getBoolean("prod_inventory") ? "Si" : "No"), BmFieldType.STRING) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_reorder"), BmFieldType.NUMBER) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_stockmax"), BmFieldType.NUMBER) %>                 
		                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_stockmin"), BmFieldType.NUMBER) %>
   		                 <%= HtmlUtil.formatReportCell(sFParams, (pmProduct.getBoolean("prod_reneworder") ? "Si" : "No"), BmFieldType.STRING) %>
   		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_renewrate"), BmFieldType.NUMBER)) %>
   		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("ordertypes", "ortp_name"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
		                
		                 
		                 <%= HtmlUtil.formatReportCell(sFParams, "" + countProduct, BmFieldType.NUMBER) %>
		                 <% 
		                 	if (sFParams.hasRead(bmoProductCompany.getProgramCode())) {
			                 	String productCompanies = "";

		                		String sqlCompanies = "SELECT GROUP_CONCAT(comp_name SEPARATOR ', ') AS prodCompanies FROM " + SQLUtil.formatKind(sFParams, " productcompanies ")
		                				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (comp_companyid = prcp_companyid) "
		                				+ " WHERE prcp_productid = " + pmProduct.getInt("products", "prod_productid");
		                		
		                		pmCountProduct.doFetch(sqlCompanies);
		                		if (pmCountProduct.next()) 
		                			productCompanies = pmCountProduct.getString("prodCompanies");
		                		
		                 %>
		                 		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, productCompanies, BmFieldType.STRING)) %>
		                 <% } %>
		         </tr>
		        <%
		           i++;
		        
        		
           } //pmProduct
        %>    
</table>
<%

	}// Fin de if(no carga datos)
   pmProduct.close();
   pmCountProduct.close();
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
