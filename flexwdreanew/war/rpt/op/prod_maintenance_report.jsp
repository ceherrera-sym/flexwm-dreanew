<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.flexwm.shared.op.BmoEquipmentService"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes de Mantenimiento de Productos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoProduct bmoProduct = new BmoProduct();
	BmoEquipmentService bmoEquipmentService = new BmoEquipmentService(); 

    String sql = "", where = "";
    String prodStatus = "", prodType = "", prodTrack = "";
    String filters = "", fullName = "", dateStart = "",  dateEnd = "", productFamilyId = "", productGroupId = "";

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
    if (request.getParameter("datestart") != null) dateStart = request.getParameter("datestart");
   	if (request.getParameter("dateend") != null) dateEnd = request.getParameter("dateend");
    
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
    
    if(!(dateStart.equals(""))){
    	where += " AND eqsv_date >= '" + dateStart + "'";
    	filters += "<i>Fecha Apart. Incio: </i>" + request.getParameter("datestart") + ", ";
    }
	
	if(!(dateEnd.equals(""))){
    	where += " AND eqsv_date <= '" + dateEnd + "'";
    	filters += "<i>Fecha Apart. Fin: </i>" + request.getParameter("dateend") + ", ";
    }
	
	// Obtener disclosure de datos
    String disclosureFilters = new PmProduct(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
    
    sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " equipmentservices ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whtracks")+" ON(whtr_whtrackid = eqsv_whtrackid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = whtr_productid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (supl_supplierid = prod_supplierid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" ON (prod_productfamilyid = prfm_productfamilyid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON (prod_productgroupid = prgp_productgroupid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " units")+" ON (prod_unitid = unit_unitid) " +
			" WHERE eqsv_whtrackid > 0 " +
	        where +
			" ORDER BY prod_productid ASC, whtr_serial ASC, eqsv_date DESC ";
			int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			pmConnCount.close();
			System.out.println("contador DE REGISTROS --> "+count);
	
	
    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " equipmentservices ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whtracks")+" ON(whtr_whtrackid = eqsv_whtrackid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid = whtr_productid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (supl_supplierid = prod_supplierid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" ON (prod_productfamilyid = prfm_productfamilyid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON (prod_productgroupid = prgp_productgroupid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " units")+" ON (prod_unitid = unit_unitid) " +
			" WHERE eqsv_whtrackid > 0 " +
	        where +
			" ORDER BY prod_productid ASC, whtr_serial ASC, eqsv_date DESC ";
			
    System.out.println("sql: " + sql);
    PmConn pmProduct = new PmConn(sFParams);
    pmProduct.open();
    
    pmProduct.doFetch(sql);

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
	<br>
	<%
	//if que muestra el mensajede error
	if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
		%>
		
				<%=messageTooLargeList %>
		<% 
	}else{
	%>
	<table class="report">
        <%
           int i = 0, prodId = 0;
           while(pmProduct.next()) {
        	   
               //Estatus
              bmoProduct.getType().setValue(pmProduct.getString("products", "prod_type"));
              bmoProduct.getTrack().setValue(pmProduct.getString("products", "prod_track"));
              bmoEquipmentService.getStatus().setValue(pmProduct.getString("equipmentservices", "eqsv_status"));
              
              if(prodId != pmProduct.getInt("products", "prod_productid")){
            	  prodId = pmProduct.getInt("products", "prod_productid");	
            	  i = 0;
		%>      
		 			<tr>
		 				<td colspan="7" class="reportGroupCell">
		 					<b>
		 					<%= HtmlUtil.stringToHtml(pmProduct.getString("products", "prod_code") + " " + pmProduct.getString("products", "prod_name"))  %>
		 					&nbsp;&nbsp;|&nbsp;&nbsp;Marca: <%= HtmlUtil.stringToHtml(pmProduct.getString("products", "prod_brand")) %>
		 					&nbsp;&nbsp;|&nbsp;&nbsp;Modelo: <%= HtmlUtil.stringToHtml(pmProduct.getString("products", "prod_model")) %>
		 					</b>
						</td>
		 			</tr>
		 			<tr>
						<td class="reportHeaderCellCenter">#</td>
						<td class="reportHeaderCell">#Serie/Lote</td>
						<td class="reportHeaderCell">Fecha de Mto.</td>
						<td class="reportHeaderCell">Fecha Sig. Mto.</td>
						<td class="reportHeaderCell">Sig. Medidor</td>
						<td class="reportHeaderCell">Estatus</td>
						<td class="reportHeaderCell">Comentarios</td>
					</tr>
			<% 			
		 		} %>    
        <tr class="reportCellEven">
                 <%= HtmlUtil.formatReportCell(sFParams, "" + (i + 1), BmFieldType.NUMBER) %>
                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("whtracks", "whtr_serial"), BmFieldType.CODE) %>
                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("equipmentservices", "eqsv_date"), BmFieldType.CODE) %>
                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("equipmentservices", "eqsv_nextdate"), BmFieldType.STRING) %>
                 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("equipmentservices", "eqsv_nextmeter"), BmFieldType.STRING) %>
                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoEquipmentService.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("equipmentservices", "eqsv_comments"), BmFieldType.STRING)) %>
        </tr>
        <%
	           i++;
	           } //pmProduct
        %>    
	</table>
<% 

	}// Fin de if(no carga datos)
	pmProduct.close();
	pmConnCount.close();
%>  
<% if (print.equals("1")) { %>
    <script>
        //window.print();
    </script>
    <% }
System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
+ " Reporte: "+title
+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
}
	
	%>
  </body>
</html>
