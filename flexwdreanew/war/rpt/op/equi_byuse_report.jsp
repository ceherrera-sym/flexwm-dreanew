<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
 	String title = "Utilizacion por Recursos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	String sql = "", where = "", whereSupl = "";
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
        where += " AND equi_equipmenttypeid = " + equiType;
        filters += "<i>Tipo: </i>" + request.getParameter("equi_equipmenttypeidLabel") + ", ";
    }
    
    if(!(dateStart.equals(""))){
    	where += " AND orde_lockstart >= '" + dateStart + " 00:00'";
    	filters += "<i>Fecha Apart. Incio: </i>" + request.getParameter("datestart") + ", ";
    }
	
	if(!(dateEnd.equals(""))){
    	where += " AND orde_lockend <= '" + dateEnd + " 23:59'";
    	filters += "<i>Fecha Apart. Fin: </i>" + request.getParameter("dateend") + ", ";
    }
	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
	sql = "  SELECT count(contador) AS contador FROM(Select Count(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " orderequipments ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON(equi_equipmentid = ordq_equipmentid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordq_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
			" WHERE NOT ordq_equipmentid IS NULL " +
			" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
			" AND ortp_type = '" + BmoOrderType.TYPE_RENTAL + "' " +
			where + 
			" GROUP BY equi_equipmentid  "+
	" ORDER BY  equi_equipmentid ASC)AS alias";
	int count =0;
	//ejecuto el sql DEL CONTADOR
	pmConnCount.doFetch(sql);
	if(pmConnCount.next())
		count=pmConnCount.getInt("contador");
	System.out.println("contador DE REGISTROS --> "+count);


	sql = " SELECT " +
			" COUNT(ordq_equipmentid) AS contEqui, SUM(ordq_quantity) as sumQuantity," +
			" equi_equipmentid, equi_code, equi_name, equi_brand, equi_model, equi_year, equi_color, equi_cost, equi_price, " +
			" eqty_name, eqty_name, user_code, user_email, orde_lockstart, orde_lockend " +
			" FROM " + SQLUtil.formatKind(sFParams, " orderequipments ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON(equi_equipmentid = ordq_equipmentid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipmenttypes")+" ON(eqty_equipmenttypeid = equi_equipmenttypeid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = equi_userid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = ordq_orderid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) " +
			" WHERE NOT ordq_equipmentid IS NULL " +
			" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
			" AND ortp_type = '" + BmoOrderType.TYPE_RENTAL + "' " +
			where +
			" GROUP BY equi_equipmentid " +
			" ORDER BY sumQuantity DESC, equi_equipmentid ASC";
   
   	
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
	    <td class="reportHeaderCell">En pedidos</td>
	    <td class="reportHeaderCell">Cantidad<br>en pedidos</td>
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
			 <%= HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("contEqui"), BmFieldType.STRING) %>
			 <%= HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("sumQuantity"), BmFieldType.STRING) %>

			 <%= HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_code"), BmFieldType.CODE) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_name"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipmenttypes", "eqty_name"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("users", "user_code"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_brand"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_model"), BmFieldType.STRING)) %>                 
             <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_year"), BmFieldType.STRING)) %>
             <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_color"), BmFieldType.STRING)) %>
			 <%= HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_price"), BmFieldType.CURRENCY) %>                 
			 <%= HtmlUtil.formatReportCell(sFParams, pmEquipment.getString("equipments", "equi_cost"), BmFieldType.CURRENCY) %>
		 </tr>
 <%
    i++;
    } //pmProduct
    
	}// Fin de if(no carga datos)
    
	
 %>

            
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% }
	pmEquipment.close();
	pmConnCount.close();
	}// FIN DEL CONTADOR
	
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
					+ " Reporte: "+title
					+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); %>
  </body>
</html>