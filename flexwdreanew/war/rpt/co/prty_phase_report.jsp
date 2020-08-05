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
 
<%@page import="com.flexwm.server.co.PmProperty"%>
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.flexwm.shared.co.BmoPropertyModel"%>
<%@page import="com.flexwm.shared.co.BmoPropertyModelPrice"%>
<%@page import="com.flexwm.server.co.PmPropertyModel"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 
	// Inicializar variables
 	String title = "Reporte de Inmuebles por Etapa Des.";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
   	String sql = "", where = "", filters = "";
   	
   	String iconTrue = "", iconFalse = "";
	iconTrue = GwtUtil.getProperUrl(sFParams, "/icons/boolean_true.png"); 
	iconFalse = GwtUtil.getProperUrl(sFParams, "/icons/boolean_false.png");

   	int developmentId = 0;
   	int developmentPhaseId = 0;
   	int developmentBlockId = 0;
   	int deveIsActivate = 0;
   	int dvblIsOpen = 0;
   	int prtyAvailable = 0;
   	int prtyCanSell = 0;
   	int prtyHabitability = 0;
   	String prtyFinishDate = "";
   	String finishDateEnd = "",tags = "";
   	int programId = 0;
   
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("dvph_developmentid") != null) developmentId = Integer.parseInt(request.getParameter("dvph_developmentid"));
   	if (request.getParameter("dvbl_developmentphaseid") != null) developmentPhaseId = Integer.parseInt(request.getParameter("dvbl_developmentphaseid"));
   	if (request.getParameter("prty_developmentblockid") != null) developmentBlockId = Integer.parseInt(request.getParameter("prty_developmentblockid"));
   	if (request.getParameter("deve_isactivate") != null) deveIsActivate = Integer.parseInt(request.getParameter("deve_isactivate"));
   	if (request.getParameter("prty_finishdate") != null) prtyFinishDate = request.getParameter("prty_finishdate");
   	if (request.getParameter("finishdateend") != null) finishDateEnd = request.getParameter("finishdateend");
   	if (request.getParameter("prty_cansell") != null) prtyCanSell = Integer.parseInt(request.getParameter("prty_cansell"));
   	if (request.getParameter("dvbl_isopen") != null) dvblIsOpen = Integer.parseInt(request.getParameter("dvbl_isopen"));
    if (request.getParameter("prty_available") != null) prtyAvailable = Integer.parseInt(request.getParameter("prty_available"));
    if (request.getParameter("prty_habitability") != null) prtyHabitability = Integer.parseInt(request.getParameter("prty_habitability"));
    if (request.getParameter("prty_tags") != null) tags = request.getParameter("prty_tags");
    
	// Construir filtros 
	// Filtros listados    
// 	 if(!tags.equals("0")){
// 		where += " AND prty_tags LIKE '%:"+tags+":%' ";
// 		filters += "<i>Tags: </i>" + request.getParameter("prty_tagsLabel") + ", ";				
// 	}
	if(!tags.equals("")){
		where += FlexUtil.parseTagsFiltersToSql("prty_tags", tags);
		filters += "<i>Tags: </i>" + request.getParameter("prty_tagsLabel") + ", ";
	}
    if (developmentId > 0) {
		where += " AND dvph_developmentid = " + developmentId;
   		filters += "<i>Desarrollo: </i>" + request.getParameter("dvph_developmentidLabel") + ", ";
   	}
    
    if (developmentPhaseId > 0) {
		where += " AND dvbl_developmentphaseid = " + developmentPhaseId;
   		filters += "<i>Etapa Desarrollo: </i>" + request.getParameter("dvbl_developmentphaseidLabel") + ", ";
   	}
    
    if (developmentBlockId > 0) {
		where += " AND dvbl_developmentblockid = " + developmentBlockId;
   		filters += "<i>Manzana: </i>" + request.getParameter("prty_developmentblockidLabel") + ", ";
   	}
    
    if (prtyAvailable != 2){
		where += " AND prty_available = " + prtyAvailable;
		filters += "<i>Inm. Disponible: </i>" + request.getParameter("prty_availableLabel") + ", ";
     }else {
		filters += "<i>Inm. Disponible: </i> Todos, ";
    }
    
    if (deveIsActivate  != 2){
		where += " AND deve_isactivate = " + deveIsActivate;
		filters += "<i>Desarrollo Activo: </i>" + request.getParameter("deve_isactivateLabel") + ", ";
    } else {
		filters += "<i>Desarrollo Activo: </i> Todos, ";
    }
   
    if (prtyCanSell  != 2){
    	where += " AND prty_cansell = " + prtyCanSell;
		filters += "<i>Inm. Abierto: </i>" + request.getParameter("prty_cansellLabel") + ", ";
    } else {
    	filters += "<i>Inm. Abierto: </i> Todos, ";
    }
    
    if (dvblIsOpen  != 2){
    	where += " AND dvbl_isopen = " + dvblIsOpen;
		filters += "<i>Mza. Abierta: </i>" + request.getParameter("dvbl_isopenLabel") + ", ";
    } else {
    	filters += "<i>Mza. Abierta: </i> Todos, ";
    }
    
    if (prtyHabitability  != 2){
    	where += " AND prty_habitability = " + prtyHabitability;
		filters += "<i>Habitabilidad: </i>" + request.getParameter("prty_habitabilityLabel") + ", ";
    }else {
    	filters += "<i>Habitabilidad: </i> Todos, ";
    }
    
    if (!(prtyFinishDate.equals(""))) {
		where += " AND prty_finishdate >= '" + prtyFinishDate + "'";
   		filters += "<i>Fecha Terminaci&oacute;n Inicio: </i>" + prtyFinishDate + ", ";
   	}
    
    if (!(finishDateEnd.equals(""))) {
		where += " AND prty_finishdate <= '" + finishDateEnd + "'";
   		filters += "<i>Fecha Terminaci&oacute;n Fin: </i>" + request.getParameter("finishdateend") + ", ";
   	}    
    
    PmProperty pmProperty = new PmProperty(sFParams);
    if (pmProperty.getDisclosureFilters().length() > 0){
    	where += " AND " + pmProperty.getDisclosureFilters();
    	
    }
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
    sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " properties ") +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertymodels")+" ON(ptym_propertymodelid = prty_propertymodelid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentblocks")+" ON(dvbl_developmentblockid = prty_developmentblockid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = dvph_developmentid) " +
    		" WHERE prty_propertyid > 0 " +
    		where + 
    		" ORDER by dvph_code ASC, dvbl_code ASC, prty_lot ASC, prty_number ASC";
			int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			System.out.println("contador DE REGISTROS --> "+count);
    		
    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " properties ") +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertymodels")+" ON(ptym_propertymodelid = prty_propertymodelid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentblocks")+" ON(dvbl_developmentblockid = prty_developmentblockid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = dvph_developmentid) " +
    		" WHERE prty_propertyid > 0 " +
    		where + 
    		" ORDER by dvph_code ASC, dvbl_code ASC, prty_lot ASC, prty_number ASC";
    
    //System.out.println("sql::: "+sql);
    PmConn pmProperties = new PmConn(sFParams);
   	pmProperties.open();
   	
	PmConn pmExtraProperties = new PmConn(sFParams);
   	pmExtraProperties.open();
   	
   	PmConn pmConnPrmp = new PmConn(sFParams);
	pmConnPrmp.open();
	pmConnPrmp.disableAutoCommit();
	BmUpdateResult bmUpdateResultPrmp = new BmUpdateResult();

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
			if (print.equals("1") || exportExcel.equals("1")) { %>
				<script>
					alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
				</script>
			<% }
		}
			
	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) { 
%>
   	
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
					<b>Ordenado por: </b>Clave Etapa, Clave Manzana, Lote, No. Official.
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
			<tr>
				<!--
				<th class="reportHeaderCellCenter">
					Etapa de Desarrollo
				</th>
				-->
				<th class="reportHeaderCellCenter">
					Manzana
				</th>
				<th class="reportHeaderCellCenter">
					Inmueble
				</th>
				<th class="reportHeaderCell">
					Detalle del Inmueble
				</th>
				<th class="reportHeaderCellCenter">
					Fachada
				</th>
				<th class="reportHeaderCellCenter">
					Plano
				</th>
				<th class="reportHeaderCellCenter">
					GPS
				</th>
			</tr>
		<%
			int i = 1, dvph = 0, dvbl = 0;
			pmProperties.doFetch(sql);
			while (pmProperties.next()) {
		%>
		        <tr class="reportCellEven">
		        <!--
			        <td class="reportCellText" style="text-align: center" title="Clic para abrir imagen en pesta&ntilde;a nueva">
				        <b>
					        <%//= pmProperties.getString("developmentphases", "dvph_code")%> -
					        <%//= pmProperties.getString("developmentphases", "dvph_name")%>
				        </b>
						<br>
						<%
//						if (pmProperties.getString("developmentphases", "dvph_blueprint").length() > 0) {
//							String blobKeyParseDvph = pmProperties.getString("developmentphases", "dvph_blueprint");
//							if (pmProperties.getString("developmentphases", "dvph_blueprint").indexOf(".") > 0)
//								blobKeyParseDvph = pmProperties.getString("developmentphases", "dvph_blueprint").substring(0, pmProperties.getString("developmentphases", "dvph_blueprint").indexOf("."));
							%> 
							<a target='_blank' href="/fileserve?blob-key=<%//= blobKeyParseDvph %>">
		        				<img src="/fileserve?blob-key=<%//= blobKeyParseDvph %>" width="300px" height="200px">
		        			</a>
		    			<%// } %>
					</td>
					-->
					<td class="reportCellText" style="text-align: center" title="Clic para abrir imagen en pesta&ntilde;a nueva">
						<b>
							<%= pmProperties.getString("developmentblocks", "dvbl_code")%> - 
							<%= HtmlUtil.stringToHtml(pmProperties.getString("developmentblocks", "dvbl_name"))%>
							<br> Mza Abierta: 
							<%= ((pmProperties.getBoolean("dvbl_isopen")) ? 
									"<img src=\"" + iconTrue + "\"> Si" : "<img src=\"" + iconFalse + "\"> No") %>
						</b>
						<!--
						<br>
						<%
//						if (pmProperties.getString("developmentblocks", "dvbl_blueprint").length() > 0) {
//							String blobKeyParseDvbl = pmProperties.getString("developmentblocks", "dvbl_blueprint");
//							if (pmProperties.getString("developmentblocks", "dvbl_blueprint").indexOf(".") > 0)
//								blobKeyParseDvbl = pmProperties.getString("developmentblocks", "dvbl_blueprint").substring(0, pmProperties.getString("developmentblocks", "dvbl_blueprint").indexOf("."));
							%>
							<a target='_blank' href="/fileserve?blob-key=<%//= blobKeyParseDvbl %>">
	            				<img src="/fileserve?blob-key=<%//= blobKeyParseDvbl %>" width="300px" height="200px">
	        				</a>
	        			<% //} %>
	        			-->
					</td>
			        <th class="reportCellText" style="text-align: center">
						<%= pmProperties.getString("properties", "prty_code")%>
						<br>
						<%= HtmlUtil.stringToHtml(pmProperties.getString("properties", "prty_street"))%> 
						# <%= pmProperties.getString("properties", "prty_number")%>
					</th>
					<td class="reportCellText">
						<table border="0" cellspacing="0" cellpading="0" width="100%" style="font-size: 12px">
							<tr class="reportCellEven">
								<th class="reportCellText" align="left">Inm. Abierto:</th>
								<td class="reportCellText" >
									<%= ((pmProperties.getBoolean("prty_cansell")) ? 
											"<img src=\"" + iconTrue + "\"> Si" : "<img src=\"" + iconFalse + "\"> No") %>
								</td>
							</tr>
							<tr class="reportCellEven">
								<th class="reportCellText" align="left">Disponible:</th>
								<td class="reportCellText" >
									<%= ((pmProperties.getBoolean("prty_available")) ? 
											"<img src=\"" + iconTrue + "\"> Si" : "<img src=\"" + iconFalse + "\"> No") %>
								</td>
							</tr>
							<tr class="reportCellEven">
								<th class="reportCellText"  style="text-align: left">Avance:</th>
				                <%= HtmlUtil.formatReportCell(sFParams, pmProperties.getString("properties", "prty_progress") + "%", BmFieldType.STRING) %>
							</tr>
							<tr class="reportCellEven">
								<th class="reportCellText" align="left">Habitabilidad:</th>
								<td class="reportCellText" >
									<%= ((pmProperties.getBoolean("prty_habitability")) ? 
											"<img src=\"" + iconTrue + "\"> Si" : "<img src=\"" + iconFalse + "\"> No")%>
								</td>
							</tr>
							<tr class="reportCellEven">
								<th class="reportCellText" align="left">Estim. Termino:</th>
				                <%= HtmlUtil.formatReportCell(sFParams, pmProperties.getString("properties", "prty_finishdate"), BmFieldType.DATE) %>
							</tr>
							<tr class="reportCellEven">
								<th class="reportCellText" align="left">Cuartos:</th>
				                <%= HtmlUtil.formatReportCell(sFParams, pmProperties.getString("propertymodels", "ptym_rooms"), BmFieldType.STRING) %>
							</tr>
							<tr class="reportCellEven">
								<th class="reportCellText" align="left">T. Lote:</th>
				                <%= HtmlUtil.formatReportCell(sFParams, pmProperties.getString("properties", "prty_landsize") + " m2", BmFieldType.STRING) %>
							</tr>
							<tr class="reportCellEven">
								<th class="reportCellText" align="left">C. Vivienda:</th>
				                <%= HtmlUtil.formatReportCell(sFParams, pmProperties.getString("properties", "prty_constructionsize") + " m2", BmFieldType.STRING) %>
							</tr>
							
							<%	
			            	
			        		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
			        		PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
			        		bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(pmProperties.getInt("propertymodels","ptym_propertymodelid"));
			        		
			                BmoPropertyModelPrice bmoPropertyModelPrice = pmPropertyModel.getCurrentPrice(pmConnPrmp, bmoPropertyModel, bmUpdateResultPrmp);
		
			               //Precio + Extra
			            	double extraPrice = 0, extra = 0, terrenoExcedente = 0, consExcedente = 0, valoresExtra = 0;
			            	
			            	String sqlExtraProperties = "SELECT * " +
			            									" FROM " + SQLUtil.formatKind(sFParams, " properties ") +
			            									" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertymodels")+"  ON(ptym_propertymodelid = prty_propertymodelid) " +
			            								  	" WHERE prty_propertyid > 0 " +
			            									" AND prty_propertyid = " + pmProperties.getInt("properties", "prty_propertyid");
			            	
			            	pmExtraProperties.doFetch(sqlExtraProperties);
			            	while(pmExtraProperties.next()){
			            		// Asigna valor de terreno excedente
			            		terrenoExcedente = (pmExtraProperties.getDouble("prty_landsize") - pmExtraProperties.getDouble("ptym_landsize")) *  bmoPropertyModelPrice.getMeterPrice().toDouble();
			            		// Asigna valor de construccion excedente
			            		consExcedente = (pmExtraProperties.getDouble("prty_constructionsize") - pmExtraProperties.getDouble("ptym_constructionsize"))	* bmoPropertyModelPrice.getConstructionMeterPrice().toDouble();	
			        	        // Asigna valores extras
			            		valoresExtra = pmExtraProperties.getDouble("prty_extraprice") + ((pmExtraProperties.getDouble("prty_publiclandsize") - pmExtraProperties.getDouble("ptym_publiclandsize")) * bmoPropertyModelPrice.getPublicMeterPrice().toDouble());
			            	}
			            	
			            	extra = terrenoExcedente + consExcedente + valoresExtra;
			            	extraPrice = bmoPropertyModelPrice.getPrice().toDouble() + extra;
			            	%>
			            	<tr class="reportCellEven">
								<th class="reportCellText" >Precio:</th>
								<td class="reportCellText">
				               		<%= formatCurrency.format(extraPrice)%> 
								</td>
							</tr>
						</table>
	                </td>
	                <td class="reportCellText" style="text-align: center" title="Clic para abrir imagen en pesta&ntilde;a nueva">						
						<%
						if (pmProperties.getString("properties", "prty_facade").length() > 0) {

							String blobKeyParseFacade = pmProperties.getString("properties", "prty_facade");
// 							if (pmProperties.getString("properties", "prty_facade").indexOf(".") > 0)
// 								blobKeyParseFacade = pmProperties.getString("properties", "prty_facade").substring(0, pmProperties.getString("properties", "prty_facade").indexOf("."));
							
							%> 
							<a target='_blank' href="<%= HtmlUtil.getFileViewURL(sFParams, blobKeyParseFacade) %>">
		        				<img src="<%= HtmlUtil.getFileViewURL(sFParams, blobKeyParseFacade) %>" width="300px" height="200px">
	        				</a>
        				<% } %>
					</td>
					<td class="reportCellText" style="text-align: center" title="Clic para abrir imagen en pesta&ntilde;a nueva">						
						<%
						if (pmProperties.getString("properties", "prty_blueprint").length() > 0) {
							String blobKeyParseMap = pmProperties.getString("properties", "prty_blueprint");
// 							if (pmProperties.getString("properties", "prty_blueprint").indexOf(".") > 0)
// 								blobKeyParseMap = pmProperties.getString("properties", "prty_blueprint").substring(0, pmProperties.getString("properties", "prty_blueprint").indexOf("."));
							%> 
							<a target='_blank' href="<%= HtmlUtil.getFileViewURL(sFParams, blobKeyParseMap) %>">
		        				<img src="<%= HtmlUtil.getFileViewURL(sFParams, blobKeyParseMap) %>" width="300px" height="200px">
		        			</a>
		        			<% } %>
					</td>
					<td class="reportCellText" style="text-align: center" title="Abrir mapa de la ubicaci&oacute;n">						
						<%
						String coordindates = "", hiper = "HIPERVINCULO"; 
						coordindates = pmProperties.getString("properties", "prty_coordinates");
						
						if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) { %>
							<% 	if (coordindates.length() > 0) { %>
								https://www.google.com/maps/place/<%= coordindates %>
							<% 	} %>
					<%	} else {
							if (coordindates.length() > 0) {
								%> 
								<a target='_blank' href="https://www.google.com/maps/place/<%= coordindates%>">
			        				<img src="<%= GwtUtil.getProperUrl(sFParams, "/icons/gps.png")%>" width="" height="">
			        			</a>
		        			<% } %>
	        			<% } %>
					</td>
		        </tr>
	    <%
			       i++;
		    } // fin while properties
				
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
	pmConnPrmp.close();	
	pmExtraProperties.close();
    pmProperties.close();
	%>
  </body>
</html>