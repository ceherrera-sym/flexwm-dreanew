<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
 	String title = "Reporte General de Recursos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	String sql = "", where = "", whereSupl = "";
   	String filters = "";
   	int equiType = 0, equiUser = 0;

   	int programId = 0;
    
    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));	
   	if (request.getParameter("equi_userid") != null) equiUser = Integer.parseInt(request.getParameter("equi_userid"));
    if (request.getParameter("equi_equipmenttypeid") != null) equiType = Integer.parseInt(request.getParameter("equi_equipmenttypeid"));
      	
    
    // Filtros listados
    if (equiUser > 0) {
    	where += " AND equi_userid = " + equiUser;
    	filters += "<i>Responsable: </i>" + request.getParameter("equi_useridLabel") + ", ";
    }
    
    if (equiType > 0) {
        where += " AND equi_equipmenttypeid = " + equiType;
        filters += "<i>Tipo: </i>" + request.getParameter("equi_equipmenttypeidLabel") + ", ";
    }
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
    sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " equipments ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
			" WHERE equi_equipmentid > 0 " +
			where +
			" ORDER BY eqty_equipmenttypeid ASC, equi_brand ASC, equi_code ASC";
			int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			pmConnCount.close();
			System.out.println("contador DE REGISTROS --> "+count);

    
	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " equipments ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
			" WHERE equi_equipmentid > 0 " +
			where +
			" ORDER BY eqty_equipmenttypeid ASC, equi_brand ASC, equi_code ASC";
   
   	
   	//System.out.println("sql-----: "+sql);
   	
   	PmConn pmEquipment = new PmConn(sFParams);
   	pmEquipment.open();
   	
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
			<b>Filtros:</b> <%= filters %> 
			<br>
			<b>Ordenado por:</b> Tipo, A&ntilde;o.
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
<table class="report" border="0">
	<tr>
	    <td class="reportHeaderCellCenter">#</td>
	    <td class="reportHeaderCell">Clave</td>
	    <td class="reportHeaderCell">Nombre</td>
	    <td class="reportHeaderCell">Tipo</td>
	    <td class="reportHeaderCell">Responsable</td>
	    <td class="reportHeaderCell">Marca</td>
	    <td class="reportHeaderCell">Modelo</td>
	    <td class="reportHeaderCell">A&ntilde;o</td>
	    <td class="reportHeaderCell">Color</td>
	    <td class="reportHeaderCellRight">Precio Renta</td>
	    <td class="reportHeaderCellRight">Costo</td>
    </tr>
    <%
    int i = 1;
    pmEquipment.doFetch(sql);
    while(pmEquipment.next()) {
 %>      
		 <tr class="reportCellEven">	 
 			 <%= HtmlUtil.formatReportCell(sFParams, "" + i,BmFieldType.NUMBER) %>
			 <%= HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_code"), BmFieldType.CODE) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_name"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipmenttypes", "eqty_name"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("users", "user_code"), BmFieldType.CODE)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_brand"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_model"), BmFieldType.STRING)) %>                 
             <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_year"), BmFieldType.STRING)) %>
             <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_color"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.formatReportCell(sFParams, ""+pmEquipment.getString("equipments", "equi_price"), BmFieldType.CURRENCY) %>                 
			 <%= HtmlUtil.formatReportCell(sFParams, ""+pmEquipment.getString("equipments", "equi_cost"), BmFieldType.CURRENCY) %>
		 </tr>
 <%
    i++;
    } //pmProduct


	}// Fin de if(no carga datos)
    pmEquipment.close();
	pmConnCount.close();
 %>

            
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% }
			}// FIN DEL CONTADOR
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	%>
  </body>
</html>