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
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.flexwm.shared.co.BmoPropertyModel"%>
<%@page import="com.flexwm.shared.co.BmoPropertyModelPrice"%>
<%@page import="com.flexwm.server.co.PmPropertyModel"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 
	// Inicializar variables
 	String title = "Reportes de Modelos de Inmuebles";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	String sql = "", sqlPtym = "", where = "", filters = "", propertyTypeId = "";

   	int programId = 0, developmentId = 0;
   
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("ptym_developmentid") != null) developmentId = Integer.parseInt(request.getParameter("ptym_developmentid"));
   	if (request.getParameter("ptym_propertytypeid") != null) propertyTypeId = request.getParameter("ptym_propertytypeid");

	// Construir filtros 

    if (developmentId > 0) {
		where += " AND ptym_developmentid = " + developmentId;
   		filters += "<i>Desarrollo: </i>" + request.getParameter("ptym_developmentidLabel") + ", ";
   	}
    
    if (!propertyTypeId.equals("")) {
        where += SFServerUtil.parseFiltersToSql("ptym_propertytypeid", propertyTypeId);        
   		filters += "<i>Tipo Inmueble: </i>" + request.getParameter("ptym_propertytypeidLabel") + ", ";
   	}
    PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
    if(pmPropertyModel.getDisclosureFilters().length() > 0){
    	where += " AND " + pmPropertyModel.getDisclosureFilters();
    }
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
	  sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " propertymodels ") +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertytypes")+" ON(ptyp_propertytypeid = ptym_propertytypeid) " +
	    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = ptym_developmentid) " +
	    		" WHERE ptym_propertymodelid > 0 " +
	    		where + 
	    		" ORDER by deve_code ASC, ptyp_code, ptym_code ASC ";
				int count =0;
				//ejecuto el sql DEL CONTADOR
				pmConnCount.doFetch(sql);
				if(pmConnCount.next())
					count=pmConnCount.getInt("contador");
				System.out.println("contador DE REGISTROS --> "+count);
	    		
    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " propertymodels ") +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertytypes")+" ON(ptyp_propertytypeid = ptym_propertytypeid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = ptym_developmentid) " +
    		" WHERE ptym_propertymodelid > 0 " +
    		where + 
    		" ORDER by deve_code ASC, ptyp_code, ptym_code ASC ";
    
    System.out.println("sql: "+sql);
    PmConn pmPropertyModels = new PmConn(sFParams);
    pmPropertyModels.open();
    
    PmConn pmPropertyModelPrice = new PmConn(sFParams);
    pmPropertyModelPrice.open();

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
			<!--<b>Agrupado Por:</b> Vendedor, <b>Ordenado por:</b> Clave -->
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
				<td class="reportHeaderCell">#</td>
				<td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Nombre</td>
				<td class="reportHeaderCell">Desarrollo</td>
				<td class="reportHeaderCell">Tipo Inm.</td>
				<td class="reportHeaderCell">Descripci&oacute;n</td>
				<td class="reportHeaderCell">Meta Mensual</td>
				<td class="reportHeaderCell">Espacios Hab.</td>
				<td class="reportHeaderCell">Dormitorios</td>
				<td class="reportHeaderCell">M2 Terr</td>
				<td class="reportHeaderCell">M2 Const</td>
				<td class="reportHeaderCell">M2 T. Comun</td>
				<!--
				<td class="reportHeaderCell">Resumen</td>
				<td class="reportHeaderCell">Caracter&iacute;sticas</td>
				<td class="reportHeaderCell">Acabados</td>
				<td class="reportHeaderCell">Varios</td>
				-->
				<td class="reportHeaderCell">Vigencia</td>
				<td class="reportHeaderCellRight">$m2 T. Privativo</td>
				<td class="reportHeaderCellRight">$m2. T. Comunal</td>
				<td class="reportHeaderCellRight">$m2 Constr.</td>
				<td class="reportHeaderCellRight">Precio</td>
			</tr>
			<%
				int i = 1;
				double price = 0, meterPrice = 0, publicMeterPrice = 0, constructionMeterPrice =0;
				String startDate ="";
				pmPropertyModels.doFetch(sql);
				while (pmPropertyModels.next()) {
			%>
			        <tr class="reportCellEven">
	                	<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>                                    
		                <%= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_code"), BmFieldType.CODE) %>                                    
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_name"), BmFieldType.STRING)) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("developments","deve_code"), BmFieldType.CODE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertytypes","ptyp_code"), BmFieldType.CODE) %>
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_description"), BmFieldType.STRING)) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_monthlygoal"), BmFieldType.STRING) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_rooms"), BmFieldType.STRING) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_dorms"), BmFieldType.STRING) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_landsize"), BmFieldType.STRING) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_constructionsize"), BmFieldType.STRING) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_publiclandsize"), BmFieldType.STRING) %>
		                <%//= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_highlights"), BmFieldType.STRING) %>
		                <%//= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_details"), BmFieldType.STRING) %>
		                <%//= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_finishing"), BmFieldType.STRING) %>
		                <%//= HtmlUtil.formatReportCell(sFParams, pmPropertyModels.getString("propertymodels","ptym_other"), BmFieldType.STRING) %>
		                <%
			                sqlPtym = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " propertymodelprices ")
			        				+ " WHERE prmp_propertymodelid = " + pmPropertyModels.getInt("propertymodels","ptym_propertymodelid") + " "
			        				+ " AND prmp_startdate <= '" + SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()) + "' "
			        				+ " ORDER BY prmp_startdate DESC";
		                	pmPropertyModelPrice.doFetch(sqlPtym);
			        		if (pmPropertyModelPrice.next()){ 
			        			startDate = pmPropertyModelPrice.getString("prmp_startdate");
			        			meterPrice = pmPropertyModelPrice.getDouble("prmp_meterprice");
			        			publicMeterPrice =pmPropertyModelPrice.getDouble("prmp_publicmeterprice");
			        			constructionMeterPrice = pmPropertyModelPrice.getDouble("prmp_constructionmeterprice");
			        			price = pmPropertyModelPrice.getDouble("prmp_price");
			        		}
		                %>
		            	<%= HtmlUtil.formatReportCell(sFParams, "" + startDate , BmFieldType.DATE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + meterPrice, BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + publicMeterPrice, BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + constructionMeterPrice, BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + price, BmFieldType.CURRENCY) %>
			        </tr>
	    <%
	       i++;
	    }
				
	}// FIN DEL CONTADOR
		
	%>

</table>    
     

	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	}// Fin de if(no carga datos)
	pmConnCount.close();
	pmPropertyModelPrice.close();
	pmPropertyModels.close();
	%>
  </body>
</html>