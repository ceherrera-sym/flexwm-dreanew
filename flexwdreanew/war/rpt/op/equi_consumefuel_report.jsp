<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
 	String title = "Reporte Consumo de Combustible";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
 	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
		
   	String sql = "", where = "", whereEqsv = "", whereType = "";
   	String filters = "", dateStart = "", dateEnd = "";
   	int equiType = 0, equiUser = 0;
   	
   	int programId = 0;
    
    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId")); 	
   	if (request.getParameter("equi_userid") != null) equiUser = Integer.parseInt(request.getParameter("equi_userid"));
    if (request.getParameter("equi_equipmenttypeid") != null) equiType = Integer.parseInt(request.getParameter("equi_equipmenttypeid"));
    
   	if (request.getParameter("datestart") != null) dateStart = request.getParameter("datestart");
   	if (request.getParameter("dateend") != null) dateEnd = request.getParameter("dateend");

    
    // Filtros listados
    if (equiUser > 0) {
    	where += " AND equi_userid = " + equiUser;
    	filters += "<i>Responsable: </i>" + request.getParameter("equi_useridLabel") + ", ";
    }
    
    if (equiType > 0) {
        where += " AND eqty_equipmenttypeid = " + equiType;
        filters += "<i>Tipo: </i> " + request.getParameter("equi_equipmenttypeidLabel") + ", ";
    }

	if(!(dateStart.equals(""))){
		whereEqsv += " AND eqis_datetime >= '" + dateStart + " 00:00'";
    	filters += "<i>Fecha E.: </i>" + request.getParameter("dateusestart") + ", ";
    }
	
	if(!(dateEnd.equals(""))){
		whereEqsv += " AND eqis_datetimein <= '" + dateEnd + " 23:59'";
    	filters += "<i>Fecha S.: </i>" + request.getParameter("dateuseend") + ", ";
    }
	
   	PmConn pmEquipment = new PmConn(sFParams);
   	pmEquipment.open();
   	
   	PmConn pmEquipmentUses = new PmConn(sFParams);
   	pmEquipmentUses.open();
   	
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	String sql1 = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " equipmentuses  ") +
	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON(equi_equipmentid = eqis_equipmentid) " +
	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = eqis_orderid) " +
	" WHERE equi_equipmentid  > 0" +
	where + whereEqsv +"";
	int count =0;
	//ejecuto el sql DEL CONTADOR
	pmConnCount.doFetch(sql1);
	if(pmConnCount.next())
		count=pmConnCount.getInt("contador");
	pmConnCount.close();
	System.out.println("contador DE REGISTROS --> "+count);
   	
	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " equipments ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
			" WHERE equi_equipmentid > 0 " +
			where +
			" ORDER BY equi_equipmentid ASC";

   	//System.out.println("sql--: "+sql);  	

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
			<b> Agrupado por:</b>  Tipo Equipo,&nbsp;
			<b>Ordenado por:</b> Tipo, A&ntilde;o.
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<br>
<table class="report" border="0">

    <%
    
  

//if que muestra el mensajede error
	if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
		%>
		
				<%=messageTooLargeList %>
		<% 
	}else{
    
    
    
    int i = 1, equiId = 0, cont = 0;
    double fuelPrice = 0, fuelExpend = 0, fuelOut = 0, fuelIn = 0, fuelTotal = 0;
	double importeSumTotal = 0;
    pmEquipment.doFetch(sql);
    while(pmEquipment.next()) {
    	
    	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " equipmentuses  ") +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON(equi_equipmentid = eqis_equipmentid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = eqis_orderid) " +
    			" WHERE equi_equipmentid = " + pmEquipment.getInt("equi_equipmentid") +
    			where + whereEqsv +
    			" ORDER BY eqis_datetime ASC";
    	
    	//System.out.println("sqlEQIS: "+sql);
    	pmEquipmentUses.doFetch(sql);
    	
    	double importe = 0, importeTotal = 0;
    	boolean primer = true;
    	while(pmEquipmentUses.next()){
    		
    		fuelOut = pmEquipmentUses.getInt("equipmentuses", "eqis_fuel");
    		fuelIn = pmEquipmentUses.getInt("equipmentuses", "eqis_fuelin");
    		fuelPrice = pmEquipmentUses.getDouble("eqis_fuelprice");
    		fuelExpend = fuelOut - fuelIn;
    		if(fuelExpend < 0 || primer){
    			fuelExpend = (fuelExpend * -1);
    			primer = false;
    		}

    		importe = fuelExpend * fuelPrice;
    		
    		if(equiId != pmEquipmentUses.getInt("equipments", "equi_equipmentid")){
    			equiId = pmEquipmentUses.getInt("equipments", "equi_equipmentid");
    			i = 1;	
		%>
	 			<tr >
	 				<td colspan="12" class="reportGroupCell">
	 					<b>
	 						<%= HtmlUtil.stringToHtml(pmEquipmentUses.getString("equipments", "equi_code") + " " + pmEquipmentUses.getString("equipments", "equi_name"))  %>
	 						| Resp: <%= HtmlUtil.stringToHtml(pmEquipmentUses.getString("users", "user_code")) %>
	 						| Marca: <%= HtmlUtil.stringToHtml(pmEquipmentUses.getString("equipments", "equi_brand")) %>
	 						| Modelo: <%= HtmlUtil.stringToHtml(pmEquipmentUses.getString("equipments", "equi_model")) %>
 						</b>
					</td>
	 			</tr>
	 			<tr>
	 				<td class="reportHeaderCellCenter">#</td>
		 		    <td class="reportHeaderCell">Fecha Salida</td>
		 		    <td class="reportHeaderCell">Fecha Entrada</td>
		 		    <td class="reportHeaderCell">Pedido</td>
		 		    <td class="reportHeaderCell">Comentarios</td>
		 			<td class="reportHeaderCell">Kilometraje S.</td>
		 			<td class="reportHeaderCell">Kilometraje E.</td>
		 		    <td class="reportHeaderCell">Nivel Comb. S.</td>
		 		    <td class="reportHeaderCell">Nivel Comb. E.</td>
		 		    <td class="reportHeaderCell">Gasto en Litros</td>
		 		    <td class="reportHeaderCellRight">Precio Litro</td>
		 		    <td class="reportHeaderCellRight">Importe</td>
				</tr>
		 			
		<% 			
	 		} %>
			 <tr class="reportCellEven">
				 <%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>	 
				 <%= HtmlUtil.formatReportCell(sFParams, "&nbsp;&nbsp;" + pmEquipmentUses.getString("equipmentuses", "eqis_datetime"), BmFieldType.DATE) %>
				 <%= HtmlUtil.formatReportCell(sFParams, "&nbsp;&nbsp;" + pmEquipmentUses.getString("equipmentuses", "eqis_datetimein"), BmFieldType.DATE) %>
	             <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipmentUses.getString("orders", "orde_code") + " " +pmEquipmentUses.getString("orders", "orde_name"), BmFieldType.STRING)) %>
	             <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipmentUses.getString("equipmentuses", "eqis_comments"), BmFieldType.STRING)) %>
	             <%= HtmlUtil.formatReportCell(sFParams, "&nbsp;" + pmEquipmentUses.getString("equipmentuses", "eqis_meter"), BmFieldType.NUMBER) %>
	             <%= HtmlUtil.formatReportCell(sFParams, "&nbsp;" + pmEquipmentUses.getString("equipmentuses", "eqis_meterin"), BmFieldType.NUMBER) %>
	             <%= HtmlUtil.formatReportCell(sFParams, pmEquipmentUses.getString("equipmentuses", "eqis_fuel"), BmFieldType.NUMBER) %>
	             <%= HtmlUtil.formatReportCell(sFParams, pmEquipmentUses.getString("equipmentuses", "eqis_fuelin"), BmFieldType.NUMBER) %>
	             <%= HtmlUtil.formatReportCell(sFParams, "" + fuelExpend, BmFieldType.NUMBER) %>
	             <%= HtmlUtil.formatReportCell(sFParams, pmEquipmentUses.getString("equipmentuses", "eqis_fuelprice"), BmFieldType.CURRENCY) %>

	             <%= HtmlUtil.formatReportCell(sFParams, "" + importe, BmFieldType.CURRENCY) %>
			 </tr>
		 <%
 			importeTotal += importe;
		 	i++;
    	} //pmEquipmentUses
    
     	importeSumTotal += importeTotal;

    	if(importeTotal > 0){
 %>	
	    	<tr class="reportCellCurrency">
		    	<td class="formSectionLabel" align="right" colspan="11">
		    		&nbsp;
		    	</td>	
		    	<td > 
					&nbsp;<b><%= formatCurrency.format(importeTotal) %></b>
				</td>
	    	</tr>
<%
    	} // Fin if 
    } //pmEquipment
    
    if (importeSumTotal > 0) {
 %>
	 	<tr><td colspan="11">&nbsp;</td></tr>
	 	<tr><td colspan="11">&nbsp;</td></tr>
	
		<tr class="reportCellCurrency">
			<td colspan="12"> 
				&nbsp;<b><%= formatCurrency.format(importeSumTotal) %></b>
			 </td>
		</tr>
	<%
    }
	}// FIN DEL CONTADOR
	}// Fin de if(no carga datos)
	pmEquipment.close();
	pmEquipmentUses.close();
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