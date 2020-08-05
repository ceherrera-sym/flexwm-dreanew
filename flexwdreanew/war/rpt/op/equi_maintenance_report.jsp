<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.flexwm.shared.op.BmoEquipmentService"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
	String title = "Reporte Mantenimiento de Recursos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	BmoEquipmentService bmoEquipmentService = new BmoEquipmentService();
	
   	String sql = "", where = "", whereEqsv = "", filters = "", dateStart = "", dateEnd = "";
   	 	 
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
        where += " AND equi_equipmenttypeid = " + equiType;
        filters += "<i>Tipo: </i>" + request.getParameter("equi_equipmenttypeidLabel") + ", ";
    }

    if(!(dateStart.equals(""))){
    	whereEqsv += " AND eqsv_date >= '" + dateStart + "'";
    	filters += "<i>Fecha Uso Incio: </i>" + request.getParameter("datestart") + ", ";
    }
	
	if(!(dateEnd.equals(""))){
		whereEqsv += " AND eqsv_date <= '" + dateEnd + "'";
    	filters += "<i>Fecha Uso Fin: </i>" + request.getParameter("dateend") + ", ";
    }
	
	
   	PmConn pmEquipment = new PmConn(sFParams);
   	pmEquipment.open();
   	
   	PmConn pmEquipmentServices = new PmConn(sFParams);
   	pmEquipmentServices.open();
   	
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
  	 sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " equipmentservices ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON(equi_equipmentid = eqsv_equipmentid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
			" WHERE equi_equipmentid > 0" +
			where + whereEqsv +
			" ORDER BY equi_equipmentid ASC, eqsv_date ASC";
			int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			
			System.out.println("contador DE REGISTROS --> "+count);

	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " equipments ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
			" WHERE equi_equipmentid > 0 " +
			where +
			" ORDER BY equi_equipmentid ASC";

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
				
				<b>Ordenado por:</b> Fecha Mto.
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
	    <%
	    double sumCostoTotal = 0;
	    pmEquipment.doFetch(sql);
	    while(pmEquipment.next()) {
	    	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " equipmentservices ") +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON(equi_equipmentid = eqsv_equipmentid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
	    			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
	    			" WHERE equi_equipmentid > 0" +
	    			" AND equi_equipmentid = " + pmEquipment.getInt("equi_equipmentid") +
	    			where + whereEqsv +
	    			" ORDER BY equi_equipmentid ASC, eqsv_date ASC";
	    	
	    	System.out.println("sqlEQIS-: "+sql);
	
	    	pmEquipmentServices.doFetch(sql);
	    	boolean primer = true;
	    	int i = 1, equiId = 0;
	    	double costo = 0, costoTotal = 0;
	    	
	    	while(pmEquipmentServices.next()){
	    		costo = pmEquipmentServices.getDouble("eqsv_cost");
	    		costoTotal += costo;
	    		
	    		bmoEquipmentService.getStatus().setValue(pmEquipmentServices.getString("equipmentservices", "eqsv_status"));
	    		
	    		if(equiId != pmEquipmentServices.getInt("equipments", "equi_equipmentid")){
	    			equiId = pmEquipmentServices.getInt("equipments", "equi_equipmentid");
	    			i = 1;	
		%>      
		 			<tr>
		 				<td colspan="7" class="reportGroupCell">
		 					<b>
			 					<%= HtmlUtil.stringToHtml(pmEquipmentServices.getString("equipments", "equi_code") + " " + pmEquipmentServices.getString("equipments", "equi_name"))  %>
			 					| Resp: <%= pmEquipmentServices.getString("users", "user_code") %>
								| Marca: <%= HtmlUtil.stringToHtml(pmEquipmentServices.getString("equipments", "equi_brand")) %>
								| Modelo: <%= HtmlUtil.stringToHtml(pmEquipmentServices.getString("equipments", "equi_model")) %>
		 					</b>
						</td>
		 			</tr>
		 			<tr>
						<td class="reportHeaderCellCenter">#</td>
<!-- 						<td class="reportHeaderCell">Sig. Medidor</td> -->
						<td class="reportHeaderCell">Fecha de Mto.</td>
						<td class="reportHeaderCell">Fecha Sig. Mto.</td>
						<td class="reportHeaderCell">Estatus</td>
						<td class="reportHeaderCell">Comentarios</td>
						<td class="reportHeaderCell">Costo</td>
					</tr>
			<% 			
		 		} %>
	 			
				<tr class="reportCellEven">
					<%= HtmlUtil.formatReportCell(sFParams, "" + i,BmFieldType.NUMBER) %>
<%-- 					<%= HtmlUtil.formatReportCell(sFParams, pmEquipmentServices.getString("equipmentservices", "eqsv_nextmeter"), BmFieldType.STRING) %> --%>
					<%= HtmlUtil.formatReportCell(sFParams, pmEquipmentServices.getString("equipmentservices", "eqsv_date"), BmFieldType.STRING) %>
					<%= HtmlUtil.formatReportCell(sFParams, pmEquipmentServices.getString("equipmentservices", "eqsv_nextdate"), BmFieldType.STRING) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoEquipmentService.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %> 
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipmentServices.getString("equipmentservices", "eqsv_comments"), BmFieldType.STRING)) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + costo, BmFieldType.CURRENCY) %>
			 </tr>
	 <%
	    		i++;
	    	} //pmEquipmentServices
	    	
	    	sumCostoTotal += costoTotal;
	    	if(costoTotal > 0){
    		%>	
    			<tr class="reportCellEven reportCellCurrency">
			    	<td class="reportCellText" align="right" colspan="5">
			    		&nbsp;
			    	</td>	
			    	<td class="reportCellCurrency"> 
						&nbsp;<b><%= formatCurrency.format(costoTotal) %></b>
					</td>
		    	</tr>
    		
    		<%
	    	} // Fin if 
	    	
	    }//pmEquipment
	    
	    if (sumCostoTotal > 0) {
	    %>
		 	<tr><td colspan="7">&nbsp;</td></tr>
			<tr class="reportCellEven">
				<td colspan="7" class="reportCellCurrency" align="right"> 
					&nbsp;<b><%= formatCurrency.format(sumCostoTotal) %></b>
				 </td>
			</tr>
	    
	<%
		}// FIN DEL CONTADOR
		pmEquipment.close();
		pmEquipmentServices.close();
		pmConnCount.close();
		}// Fin de if(no carga datos)
		
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