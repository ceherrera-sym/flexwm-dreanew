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
 
<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.op.BmoProductGroup"%>
<%@page import="com.flexwm.shared.op.BmoOrderItem"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
 	String title = "Reporte de Utilizacion por Producto";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	String sql = "", where = "", whereSupl = "";
   	String prodType = "", prodTrack = "", filters = "", dateStart = "",  dateEnd = "";
   	int supplierId = 0, productFamilyId = 0, productGroupId = 0, unitId = 0;

   	int programId = 0;
    
    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));	
   	if (request.getParameter("prod_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("prod_supplierid"));
    if (request.getParameter("prod_productfamilyid") != null) productFamilyId = Integer.parseInt(request.getParameter("prod_productfamilyid"));
    if (request.getParameter("prod_productgroupid") != null) productGroupId = Integer.parseInt(request.getParameter("prod_productgroupid"));
    if (request.getParameter("prod_unitid") != null) unitId = Integer.parseInt(request.getParameter("prod_unitid"));
    if (request.getParameter("prod_type") != null) prodType = request.getParameter("prod_type");
    if (request.getParameter("prod_track") != null) prodTrack = request.getParameter("prod_track");  
    if (request.getParameter("datestart") != null) dateStart = request.getParameter("datestart");
   	if (request.getParameter("dateend") != null) dateEnd = request.getParameter("dateend");
    
   	
    BmoProduct bmoProduct = new BmoProduct();
    BmoOrder bmoOrder = new BmoOrder();
    
 // Filtros listados
    if (supplierId > 0) {
    	where += " AND prod_supplierid = " + supplierId;
    	filters += "<i>Proveedor: </i>" + request.getParameter("prod_supplieridLabel") + ", ";
    }
    
    if (!prodType.equals("")) {
        where += " AND prod_type LIKE '" + prodType + "'";
        filters += "<i>Tipo: </i>" + request.getParameter("prod_typeLabel") + ", ";
    }
    
    if (productFamilyId > 0) {
        where += " AND prod_productfamilyid = " + productFamilyId;
        filters += "<i>Familia: </i>" + request.getParameter("prod_productfamilyidLabel") + ", ";
    }
    
    if (productGroupId > 0) {
        where += " AND prod_productgroupid = " + productGroupId;
        filters += "<i>Grupo: </i>" + request.getParameter("prod_productgroupidLabel") + ", ";
    }
    
    if (unitId > 0) {
        where += " AND prod_unitid = " + unitId;
        filters += "<i>Unidad: </i>" + request.getParameter("prod_unitidLabel") + ", ";
    }
    
    if (!prodTrack.equals("")) {
        where += " AND prod_track LIKE '" + prodTrack + "'";
        filters += "<i>Rastreo: </i>" + request.getParameter("prod_trackLabel");
    }
   	
    if(!(dateStart.equals(""))){
    	where += " AND orde_lockstart >= '" + dateStart + " 00:00'";
    	filters += "<i>Fecha Apart. Incio: </i>" + request.getParameter("datestart") + ", ";
    }
	
	if(!(dateEnd.equals(""))){
    	where += " AND orde_lockend <= '" + dateEnd + " 23:59'";
    	filters += "<i>Fecha Apart. Fin: </i>" + request.getParameter("dateend") + ", ";
    }
	
	// Obtener disclosure de datos
    String disclosureFilters = new PmProduct(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
   	String sql1 = " SELECT count(contador) AS contador FROM(Select Count(*) AS contador " +

			" FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid=ordi_productid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid=prod_supplierid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" ON(prfm_productfamilyid=prod_productfamilyid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON(prgp_productgroupid=prod_productgroupid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid=ordi_ordergroupid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordg_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " projects")+" ON (proj_orderid = orde_orderid) " +
			" WHERE  ordi_productid IS NOT NULL " +
			" AND orde_status = '" + bmoOrder.STATUS_AUTHORIZED + "'" +
			//" AND ortp_type = '" + BmoOrderType.TYPE_RENTAL + "' " +
			where +
			" GROUP BY prod_productid )AS ALIAS" ;
   	
	int count =0;
	//ejecuto el sql DEL CONTADOR
	pmConnCount.doFetch(sql1);
	if(pmConnCount.next())
		count=pmConnCount.getInt("contador");
	pmConnCount.close();
	System.out.println("contador DE REGISTROS --> "+count);
   	sql = " SELECT " +
			" COUNT(ordi_productid) AS cont, SUM(ordi_quantity) as sumQuantity," +
			" prod_code, prod_name, prod_type, prod_brand, prod_model, prod_track, prod_cost, prod_rentalprice, " +
			" supl_code, supl_name, prfm_name,prgp_name, orde_lockstart,orde_lockend " +
			" FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON(prod_productid=ordi_productid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid=prod_supplierid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" ON(prfm_productfamilyid=prod_productfamilyid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON(prgp_productgroupid=prod_productgroupid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON(ordg_ordergroupid=ordi_ordergroupid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordg_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " projects")+" ON (proj_orderid = orde_orderid) " +
			" WHERE  ordi_productid >0 " +
			" AND orde_status = '" + bmoOrder.STATUS_AUTHORIZED + "'" +
			//" AND ortp_type = '" + BmoOrderType.TYPE_RENTAL + "' " +
			where +
			" GROUP BY prod_productid " + 
			" ORDER BY sumQuantity DESC, prod_productid ASC";
			System.out.println("sql"+sql);
   	
   	//System.out.println("sql-----: "+sql);
   	
   	PmConn pmProduct = new PmConn(sFParams);
   	pmProduct.open();
   	
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
			<b>Filtros:</b> <i>Pedido: </i> Autorizado <%= filters %>
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
<!--<table width="100%" border="1">
	<tr>
		<td width="50%"> <div id="chart_div" style="width: 100%; height: 400px;"></div> </td>
		<td width="50%"> <div id="chart_div2" style="width: 100%; height: 400px;"></div> </td>
	</tr>
</table>-->
<br>
<table class="report" border="0">
	
	<tr class="">
	    <td class="reportHeaderCellCenter">#</td>
	    <td class="reportHeaderCell">En pedidos</td>
	    <td class="reportHeaderCell">Cantidad<br>en pedidos</td>
	    <td class="reportHeaderCell">Clave</td>
	    <td class="reportHeaderCell">Nombre</td>
	    <td class="reportHeaderCell">Proveedor</td>
	    <td class="reportHeaderCell">Tipo</td>
	    <td class="reportHeaderCell">Marca</td>
	    <td class="reportHeaderCell">Modelo</td>
	    <td class="reportHeaderCell">Rastreo</td>
	    <td class="reportHeaderCell">Familia</td>
	    <td class="reportHeaderCell">Grupo</td>
	    <td class="reportHeaderCellRight">Costo</td>
	    <td class="reportHeaderCellRight">Precio</td>
    </tr>
    <%
    int i = 1;
    pmProduct.doFetch(sql);
    while(pmProduct.next()) {
    	bmoProduct.getType().setValue(pmProduct.getString("products", "prod_type"));
    	bmoProduct.getTrack().setValue(pmProduct.getString("products", "prod_track"));
 %>      
		 <tr class="reportCellEven">
			 <%= HtmlUtil.formatReportCell(sFParams, "" + (i),BmFieldType.NUMBER) %>
			 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("cont"), BmFieldType.STRING) %>
			 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("sumQuantity"), BmFieldType.STRING) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_code"), BmFieldType.CODE)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_name"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("suppliers", "supl_code") + " " + pmProduct.getString("suppliers", "supl_name"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoProduct.getType().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_brand"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_model"), BmFieldType.STRING)) %>                 
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoProduct.getTrack().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("productfamilies", "prfm_name"), BmFieldType.CODE)) %>
             <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmProduct.getString("productgroups", "prgp_name"), BmFieldType.CODE)) %>
			 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_cost"), BmFieldType.CURRENCY) %>                 
			 <%= HtmlUtil.formatReportCell(sFParams, pmProduct.getString("products", "prod_rentalprice"), BmFieldType.CURRENCY) %>
		 </tr>
 <%
    i++;
    } //pmProduct
}// FIN DEL CONTADOR
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
	%>
  </body>
</html>