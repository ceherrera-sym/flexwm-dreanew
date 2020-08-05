
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
 
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="javax.script.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%@include file="/inc/login.jsp" %>
<%
	// Inicializar variables
 	String title = "Reporte Reloj Checador";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	DecimalFormat df = new DecimalFormat("##.######");

   	String sql = "", where = "";
   	String startDate = "", endDate = "";   	
   	String filters = "", statusUser = "";
   	int userId = 0, areaId = 0;
   	
   	int programId = 0;
    
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("ustc_userid") != null) userId = Integer.parseInt(request.getParameter("ustc_userid"));
   	if (request.getParameter("user_areaid") != null) areaId = Integer.parseInt(request.getParameter("user_areaid"));
   	if (request.getParameter("ustc_datetime") != null) startDate = request.getParameter("ustc_datetime");
   	if (request.getParameter("enddate") != null) endDate = request.getParameter("enddate");
   	if (request.getParameter("user_status") != null) statusUser = request.getParameter("user_status");
   	

   	BmoUserTimeClock bmoUserTimeClock = new BmoUserTimeClock();
   	
   	//Conexiones
   	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
		
	// Filtros listados
	if (userId > 0) {
		  where += " AND user_userid = " + userId;
		  filters += "<i>Usuario: </i>" + request.getParameter("ustc_useridLabel") + ", ";
	}
	
	if (areaId > 0) {
		  where += " AND area_areaid = " + areaId;
		  filters += "<i>Departamento: </i>" + request.getParameter("user_areaidLabel") + ", ";
	}
	
	if (!startDate.equals("")) {
   		where += " AND ustc_datetime >= '" + startDate + " 00:00'";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
   	
	if (!endDate.equals("")) {
   		where += " AND ustc_datetime <= '" + endDate + " 23:59'";
   		filters += "<i>Fecha Final: </i>" + endDate + ", ";
   	}
	
	if (!(statusUser.equals(""))) {
        where += SFServerUtil.parseFiltersToSql("user_status", statusUser);
   		filters += "<i>Estatus Usuario: </i>" + request.getParameter("user_statusLabel") + " ";
   	}
	
	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmUser(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
		
    //abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
    pmConnCount.open();
    
	sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " usertimeclock ") +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (ustc_userid = user_userid) " +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON (user_areaid = area_areaid) " +	
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" ON (user_locationid = loct_locationid) " +	
			  " WHERE ustc_usertimeclockid > 0 " + where +
			  " ORDER BY area_code asc, user_fatherlastname asc, user_motherlastname asc, user_firstname asc ";
			  
			  int count =0;
				//ejecuto el sql
				pmConnCount.doFetch(sql);
				if(pmConnCount.next())
					count=pmConnCount.getInt("contador");
				System.out.println("contador de reportes - "+count);
    

	pmConn.doFetch(sql);

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
		<td class="reportTitle" align="left">
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
<TABLE class="report" border="0">                          
	
<%
//if que muestra el mensajede error
if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
	%>
	
<%=messageTooLargeList %>
	<% 
}else{
	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " usertimeclock ") +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (ustc_userid = user_userid) " +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON (user_areaid = area_areaid) " +	
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" ON (user_locationid = loct_locationid) " +	
			  " WHERE ustc_usertimeclockid > 0 " + where +
			  " ORDER BY area_code asc, user_fatherlastname asc, user_motherlastname asc, user_firstname asc ";
			  //" GROUP BY area_areaid ";	
		pmConn.doFetch(sql);

	PmUser pmUser = new PmUser(sFParams);
	//BmoUser bmoUser2 = null;
	
	int s = 1, y = 0 , idUser = 0;
	while(pmConn.next()) {
		if(idUser != pmConn.getInt("user_userid")){
			idUser = pmConn.getInt("user_userid");
			s=1;
%>			
			<tr >
				<td class="reportCellEven" colspan="10">
				<b>
					<%= HtmlUtil.stringToHtml(pmConn.getString("areas", "area_name"))%> -
					<%= HtmlUtil.stringToHtml(pmConn.getString("users", "user_fatherlastname"))%>
					<%= HtmlUtil.stringToHtml(pmConn.getString("users", "user_motherlastname"))%>
					<%= HtmlUtil.stringToHtml(pmConn.getString("users", "user_firstname"))%>
					</b>
				</td>
			</tr>
			<tr>
				<td class="reportHeaderCellCenter">#</td>
				<!-- <td class="reportHeaderCell">Departamento</td>
					<td class="reportHeaderCell">Usuario</td> 
				-->
				<td class="reportHeaderCell">Tipo</td>
				<td class="reportHeaderCell">Comentarios</td>
				<td class="reportHeaderCell">Ip</td>
				<td class="reportHeaderCell">Fecha</td>
				<td class="reportHeaderCell">Latitud</td>
				<td class="reportHeaderCell">Longitud</td>
				<td class ="reportHeaderCell">GPS</td>				
				<td class="reportHeaderCell">Ubicaci&oacute;n</td>
				<td class="reportHeaderCell">Checado</td>       
			</tr>
		<% } %>
	    <tr class="reportCellEven">
	       <%= HtmlUtil.formatReportCell(sFParams, "" + s, BmFieldType.NUMBER) %>	
		   <%//= HtmlUtil.formatReportCell(sFParams, pmConn.getString("areas", "area_code"), BmFieldType.CODE) %>      	   
		   <%//= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_firstname") + " " + pmConn.getString("users", "user_fatherlastname"), BmFieldType.STRING) %>
		   <%
		   		bmoUserTimeClock.getType().setValue(pmConn.getString("usertimeclock", "ustc_type"));
		   %>	   
		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoUserTimeClock.getType().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("usertimeclock", "ustc_comments"), BmFieldType.STRING)) %>
		   <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("usertimeclock", "ustc_remoteip"), BmFieldType.STRING) %>
		   <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("usertimeclock", "ustc_datetime"), BmFieldType.DATETIME) %>
		   <%= HtmlUtil.formatReportCell(sFParams, "" + df.format(pmConn.getDouble("ustc_gpslatitude")), BmFieldType.STRING) %>
		   <%= HtmlUtil.formatReportCell(sFParams, "" + df.format(pmConn.getDouble("ustc_gpslongitude")), BmFieldType.STRING) %>
		   <%
		   		//Validar la ubicación
		   		String isValid = "Valido";
		   
		   		//Obtener la Latitud y Longitud de la ubicación
		   		//bmoUser2 = (BmoUser)pmUser.get(pmConn.getInt("user_userid"));
		   
		   		//double minLatitud = bmoUser2.getBmoLocation().getGpsLatitude().toInteger() - 100;
		   		//double maxLatitud = bmoUser2.getBmoLocation().getGpsLatitude().toInteger() + 100;
		   		double minLatitud = pmConn.getDouble("loct_gpslatitude") ;
// 		   		double maxLatitud = pmConn.getDouble("loct_gpslatitude") ;
	
		   		//double minLongitude = bmoUser2.getBmoLocation().getGpsLongitude().toInteger() - 100;
		   		//double maxLongitude = bmoUser2.getBmoLocation().getGpsLongitude().toInteger() + 100;
// 		   		double minLongitude = pmConn.getDouble("loct_gpslongitude") ;
		   		double maxLongitude = pmConn.getDouble("loct_gpslongitude") ;
		   			   		
		   		//Latitud		   
		   		if (!pmConn.getString("locations", "loct_gpslongitude").equals("") && !pmConn.getString("locations", "loct_gpslatitude").equals("")) {
		   		

						double result1 = pmConn.getDouble("ustc_gpslongitude")-minLatitud;
						if(result1 < 0)result1 = result1 * -1;
						
						double result2 = pmConn.getDouble("ustc_gpslatitude") - maxLongitude;
						if(result2 < 0)result2 = result2 * -1;
						
					if (( result1 > 0.001000) && (result2 > 0.001000))
						isValid = "No Valido";	
			   			
		   		} else {
		   		
		   			isValid = "Lat y Long Incompleta";
		   		}
		   				   		
		   %>
		      <td class="reportCellEven" title="Abrir mapa de la ubicaci&oacute;n">
	   <%if(pmConn.getString("usertimeclock","ustc_gpslongitude").length() > 0 && pmConn.getString("usertimeclock","ustc_gpslongitude").length()>0){
		   String link = ""+pmConn.getString("ustc_gpslatitude")+","+pmConn.getString("ustc_gpslongitude")+"";
		   %>
		   <a target='_blank' href="https://www.google.com/maps/place/<%= link%>">
			<img src=<%=GwtUtil.getProperUrl(sFParams, "portal/img/gps.png") %> width="" height="">
		</a>
	  <% } %>
	   
	   </td>
		   <%//= HtmlUtil.formatReportCell(sFParams, bmoUser2.getBmoLocation().getCode().toString(), BmFieldType.CODE) %>
		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("locations", "loct_name"), BmFieldType.STRING)) %>
		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, isValid, BmFieldType.STRING)) %>
		<tr>   
<% 		s++ ;
		y++;
	} %>
	
	<tr class="reportCellEven">
		<td colspan="10">
			&nbsp;
		</td>
	</tr>
	<tr class="reportCellCode" >
		<td align="center">
			&nbsp;&nbsp;<%= y%>
		</td>
		<td colspan="9">
			&nbsp;
		</td>
	</tr>
</table>  
<%
	}
	}// Fin de if(no carga datos)
 
   pmConn.close();    
   pmConnCount.close();
%>  
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } %>
  </body>
</html>