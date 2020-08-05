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

<%@page import="com.flexwm.shared.op.BmoSupplier"%>
<%@page import="com.flexwm.server.op.PmSupplier"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 
    // Inicializar variables
    String title = "Reportes de Proveedores";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
    String sql = "", where = "";
    BmoSupplier bmoSupplier = new BmoSupplier();
    String filters = "", suplType = "", fiscalType = "";
    int programId = 0, supplierCategoryId = 0, cityId = 0;
    
    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));  
    if (request.getParameter("supl_type") != null) suplType = request.getParameter("supl_type");
    if (request.getParameter("supl_fiscaltype") != null) fiscalType = request.getParameter("supl_fiscaltype");
    if (request.getParameter("supl_suppliercategoryid") != null) supplierCategoryId = Integer.parseInt(request.getParameter("supl_suppliercategoryid"));
    //if (request.getParameter("supl_cityid") != null) cityId = Integer.parseInt(request.getParameter("supl_cityid"));   

    // Filtros listados
    if (!suplType.equals("")) {
        where += " AND supl_type = '" + suplType + "'";
        filters += "<i>Tipo: </i>" + request.getParameter("supl_typeLabel") + ", ";
    }
    
    if (!fiscalType.equals("")) {
        where += " AND supl_fiscaltype = '" + fiscalType + "'";
        filters += "<i>R&eacute;gimen: </i>" + request.getParameter("supl_fiscaltypeLabel") + ", ";
    }
    
    if (supplierCategoryId > 0) {
          where += " AND supl_suppliercategoryid = " + supplierCategoryId;
          filters += "<i>Categor&iacute;a: </i>" + request.getParameter("supl_suppliercategoryidLabel") + ", ";
    }
    /*
    if (cityId > 0) {
        where += " AND supl_cityid = " + cityId;
        filters += "<i>Ciudad: </i>" + request.getParameter("supl_cityidLabel") + ", ";
    }*/
 
 
    // Obtener disclosure de datos
    String disclosureFilters = new PmSupplier(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
    sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " suppliers ") +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (supl_suppliercategoryid = spca_suppliercategoryid) " +
            //" LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities citySupl on (city_cityid = supl_cityid) " +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities")+" cityDeed on (cityDeed.city_cityid = supl_citydeedid) " +
            " WHERE supl_supplierid > 0 " + 
            where +
            " ORDER by supl_supplierid ASC";
			int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			
			System.out.println("contador DE REGISTROS --> "+count);

	
    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " suppliers ") +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (supl_suppliercategoryid = spca_suppliercategoryid) " +
          //" LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities citySupl on (city_cityid = supl_cityid) " +
          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities")+" cityDeed on (cityDeed.city_cityid = supl_citydeedid) " +
          " WHERE supl_supplierid > 0 " + 
          where +
          " ORDER by supl_supplierid ASC";
    
    System.out.println("sql: " + sql);
    PmConn pmSupplier = new PmConn(sFParams);
    pmSupplier.open();

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
<%
//if que muestra el mensajede error
if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
	%>
	
			<%=messageTooLargeList %>
	<% 
}else{
%>
<p>
<table class="report">
        <tr class="">
            <td class="reportHeaderCellCenter">#</td>
            <td class="reportHeaderCell">Clave</td>
            <td class="reportHeaderCell">Nombre</td>
            <td class="reportHeaderCell">Raz&oacute;n Social</td>
            <td class="reportHeaderCell">RFC</td>
            <td class="reportHeaderCell">Tipo</td>
            <td class="reportHeaderCell">Categor&iacute;a</td>
            <td class="reportHeaderCell">Representante Legal</td>
            <td class="reportHeaderCell">IMSS</td>
            <td class="reportHeaderCell">R&eacute;gimen</td>
            <td class="reportHeaderCell">Clabe</td>
            <td class="reportHeaderCell">Banco</td>
            
            <td class="reportHeaderCell">Tel.Oficina</td>
            <td class="reportHeaderCell">Email</td>
            
            <!--
            <td class="reportHeaderCell">Direcci&oacute;n</td>
            <td class="reportHeaderCell">Colonia</td>
            <td class="reportHeaderCell">C&oacute;digo Postal:</td>
            <td class="reportHeaderCell">Tel.Fax</td>
            <td class="reportHeaderCell">Ciudad</td>
            -->
            <td class="reportHeaderCell">Notaria No</td>
            <td class="reportHeaderCell">Nom. Notario Acta</td>
            <td class="reportHeaderCell">No.Escritura</td>
            <td class="reportHeaderCell">Fecha Escritura</td>
            <td class="reportHeaderCell">Ciudad Escritura</td>
        </tr>
        <%
        
        pmSupplier.doFetch(sql);
        int i = 0;
        while(pmSupplier.next()) {

        	//Estatus
        	bmoSupplier.getType().setValue(pmSupplier.getString("suppliers", "supl_type"));
        	bmoSupplier.getFiscalType().setValue(pmSupplier.getString("suppliers", "supl_fiscaltype"));

        	%>      
        	<tr class="reportCellEven">
	        	<%= HtmlUtil.formatReportCell(sFParams, "" + (i+1), BmFieldType.NUMBER) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_code"), BmFieldType.CODE) %>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_name"), BmFieldType.STRING)) %>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_legalname"), BmFieldType.STRING)) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_rfc"), BmFieldType.RFC) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoSupplier.getType().getSelectedOption().getLabel(), BmFieldType.STRING)) %> 
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliercategories", "spca_name"), BmFieldType.STRING)) %>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_legalRep"), BmFieldType.STRING)) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_imss"), BmFieldType.STRING) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoSupplier.getFiscalType().getSelectedOption().getLabel(), BmFieldType.STRING)) %> 
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_clabe"), BmFieldType.NUMBER)) %>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_bank"), BmFieldType.STRING)) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_officephone1"), BmFieldType.PHONE) %>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_email"), BmFieldType.EMAIL)) %>
	        	<%//= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmSupplier.getString("suppliers", "supl_address1")), BmFieldType.STRING) %>
	        	<%//= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmSupplier.getString("suppliers", "supl_address2")), BmFieldType.STRING) %>
	        	<%//= HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_zip"), BmFieldType.NUMBER) %>
	        	<%//= HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_officephone2"), BmFieldType.STRING) %>
	        	<%//= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmSupplier.getString("citySupl", "city_name")), BmFieldType.STRING) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_lawyernumber"), BmFieldType.NUMBER) %>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_lawyername"), BmFieldType.STRING)) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_lawyerdeed"), BmFieldType.NUMBER) %>
	        	<%= HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("suppliers", "supl_lawyerdeeddate"), BmFieldType.DATE) %>
	        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmSupplier.getString("cityDeed", "city_name"), BmFieldType.STRING)) %>
        	</tr>
        	<%
        	i++;

        } //pmProduct
        %>    
</table>
<%

	}// Fin de if(no carga datos)
	pmConnCount.close();
	pmSupplier.close();
%>  
<% if (print.equals("1")) { %>
    <script>
        //window.print();
    </script>
    <% } 
System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
+ " Reporte: "+title
+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
    }%>
  </body>
</html>
