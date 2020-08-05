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
 
<%@page import="com.flexwm.server.co.PmDevelopmentBlock"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentBlock"%>
<%@page import="com.flexwm.server.co.PmPropertyModel"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 
	// Inicializar variables
 	String title = "Reporte por Etapa";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	
   	String sql = "", sql2 = "", where = "", filters = "", startDate = "", endDate = "", readyDate = "", readyDateEnd ="", lot = "";

   	int programId = 0, developmentPhaseId = 0, statusProcess = 2, isOpen = 2, isTemporally = 2, developmentId = 0, developmentBlockId = 0, processPercentage = 3;;
   
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("dvbl_developmentphaseid") != null) developmentPhaseId = Integer.parseInt(request.getParameter("dvbl_developmentphaseid"));
   	if (request.getParameter("dvbl_statusprocess") != null) statusProcess = Integer.parseInt(request.getParameter("dvbl_statusprocess"));
   	if (request.getParameter("dvbl_isopen") != null) isOpen = Integer.parseInt(request.getParameter("dvbl_isopen"));
   	if (request.getParameter("dvbl_istemporally") != null) isTemporally = Integer.parseInt(request.getParameter("dvbl_istemporally"));
   	if (request.getParameter("dvbl_startdate") != null) startDate = request.getParameter("dvbl_startdate");
   	if (request.getParameter("dvbl_enddate") != null) endDate = request.getParameter("dvbl_enddate");
   	if (request.getParameter("dvbl_readydate") != null) readyDate = request.getParameter("dvbl_readydate");
   	if (request.getParameter("readyDateEnd") != null) readyDateEnd = request.getParameter("readyDateEnd");
   	if (request.getParameter("dvph_developmentid") != null) developmentId = Integer.parseInt(request.getParameter("dvph_developmentid"));
   	if (request.getParameter("dvbl_developmentblockid") != null) developmentBlockId = Integer.parseInt(request.getParameter("dvbl_developmentblockid"));
   	if (request.getParameter("dvbl_processpercentage") != null) processPercentage = Integer.parseInt(request.getParameter("dvbl_processpercentage"));

	// Construir filtros 	
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
   		filters += "<i>Manzana: </i>" + request.getParameter("dvbl_developmentblockidLabel") + ", ";
   	}
        
    if (statusProcess != 2) {
		where += " AND dvbl_statusprocess = " + statusProcess;
   		filters += "<i>En proceso?: </i>" + request.getParameter("dvbl_statusprocessLabel") + ", ";
   	}else {
   		filters += "<i>En proceso?: Todo</i>,  ";
   	}
    
    if (isOpen != 2) {
		where += " AND dvbl_isopen = " + isOpen;
   		filters += "<i>Mza Abierta: </i>" + request.getParameter("dvbl_isopenLabel") + ", ";
   	}else {
   		filters += "<i>Mza Abierta: Todas</i>,  ";
   	}
    
    if (isTemporally != 2) {
		where += " AND dvbl_istemporally = " + isTemporally;
   		filters += "<i>Mza Temporal: </i>" + request.getParameter("dvbl_istemporallyLabel") + ", ";
   	}else {
   		filters += "<i>Es Temporal: Todas</i>,  ";
   	}
    
    if (processPercentage != 3) {
    	if(processPercentage == 0)
    		where += " AND dvbl_processpercentage = 0 ";
    	else if(processPercentage == 1)
    		where += " AND dvbl_processpercentage < 100 ";
    	else if(processPercentage == 2)
    		where += " AND dvbl_processpercentage = 100 ";
    		
   		filters += "<i>% Avance: </i>" + request.getParameter("dvbl_processpercentageLabel") + ", ";
   	}else 
   		filters += "<i>% Avance: Todos</i>,  ";
    
    if (!startDate.equals("")) {
		where += " AND dvbl_startdate >= '" + startDate + "' ";
   		filters += "<i>Fecha Inicio: </i>" + request.getParameter("dvbl_startdate") + ", ";
   	}
   	
    if (!endDate.equals("")) {
		where += " AND dvbl_enddate <= '" + endDate + "' ";
   		filters += "<i>Fecha Fin: </i>" + request.getParameter("dvbl_enddate") + ", ";
   	}
    
    if (!readyDate.equals("")) {
		where += " AND dvbl_readydate >= '" + readyDate + "' ";
   		filters += "<i>Fecha Entrega: </i>" + request.getParameter("dvbl_readydate") + ", ";
   	}
   	
    if (!readyDateEnd.equals("")) {
		where += " AND dvbl_readydate <= '" + readyDateEnd + "' ";
   		filters += "<i>Fecha Fin: </i>" + request.getParameter("readyDateEnd") + ", ";
   	}
    
    PmDevelopmentBlock pmDevelopmentBlock = new PmDevelopmentBlock(sFParams);
    if (pmDevelopmentBlock.getDisclosureFilters().length() > 0){
    	where += " AND " + pmDevelopmentBlock.getDisclosureFilters();
    }
    
    PmConn pmDevelopmentBlocks = new PmConn(sFParams);
    pmDevelopmentBlocks.open();
    
    PmConn pmOther = new PmConn(sFParams);
    pmOther.open();
    
   	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	  	pmConnCount.open();
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

<p>
<table class="report">
			<tr class="">		
				<td class="reportHeaderCell">Etapa</td>
				<td class="reportHeaderCell">Clave M/T</td>
				<td class="reportHeaderCell">Lotes</td>
				<td class="reportHeaderCellCenter">Viviendas</td>
				<td class="reportHeaderCell">Prog. Inicio</td>
				<td class="reportHeaderCell">Prog. Fin</td>
				<td class="reportHeaderCell">Fecha Term.</td>
			</tr>
			<%
		
			sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " developmentblocks ") +
    		" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
    		" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON(deve_developmentid = dvph_developmentid) " +
    		" WHERE dvbl_developmentblockid	> 0 " +
    		where + 
    		" ORDER by deve_code ASC, dvph_code ASC, dvbl_code ASC ";
			int countr =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				countr=pmConnCount.getInt("contador");
			System.out.println("contador DE REGISTROS --> "+countr);
		//if que muestra el mensajede error
			if(countr>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
				%>
				
						<%=messageTooLargeList %>
				<% 
			}else{
    		
    		
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " developmentblocks ") +
			    		" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
			    		" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON(deve_developmentid = dvph_developmentid) " +
			    		" WHERE dvbl_developmentblockid	> 0 " +
			    		where + 
			    		" ORDER by deve_code ASC, dvph_code ASC, dvbl_code ASC ";
		    	//System.out.println("sql2: "+sql);

			    pmDevelopmentBlocks.doFetch(sql);
				int developmentPhaseIdTable = 0, i = 0, count = 0, viv = 0, vivEtapaTotal = 0, vivTotal = 0;
				while (pmDevelopmentBlocks.next()) {
					lot = "";
			%>
			        <tr class="reportCellEven">
			        	<% if(pmDevelopmentBlocks.getInt("developmentphases","dvph_developmentphaseid") != developmentPhaseIdTable){
			        		developmentPhaseIdTable = pmDevelopmentBlocks.getInt("developmentphases","dvph_developmentphaseid");
			        		i=0; vivEtapaTotal = 0;
			        		//Contador por etapa des. para rowspan
			        		sql2 = " SELECT COUNT(dvbl_developmentblockid) as countDvbl FROM " + SQLUtil.formatKind(sFParams, " developmentblocks ") +
			        	    		" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
			        	    		" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON(deve_developmentid = dvph_developmentid) " +
			        	    		" WHERE dvbl_developmentblockid	> 0 " +
			        	    		" AND dvph_developmentphaseid = " + developmentPhaseIdTable +
			        	    		where +
			        	    		" GROUP BY dvph_developmentphaseid ";
			        	    //System.out.println("mzas por etapa: "+sql2);
			        	    pmOther.doFetch(sql2);
			        	    // le sumo 1 para el renglon final de total
			        	    if(pmOther.next()) count = pmOther.getInt("countDvbl");
			        	    //count = count;
		        		%>
				        	<td class="reportCellEven" rowspan="<%= count + 1%>">
				        		<b><%= pmDevelopmentBlocks.getString("developmentphases","dvph_code")%>&nbsp;</b>
				        	</td>
				        	
			        	<% }
			        		i++; 
			        	%>
		                <%= HtmlUtil.formatReportCell(sFParams, pmDevelopmentBlocks.getString("developmentblocks","dvbl_code"), BmFieldType.CODE) %>
		                <%	
			              	//Lotes por manzana
		                	sql2 = " SELECT DISTINCT(prty_lot) AS lotes FROM " + SQLUtil.formatKind(sFParams, " properties ") +
			        	    		" WHERE prty_developmentblockid = " + pmDevelopmentBlocks.getString("developmentblocks","dvbl_developmentblockid");		                
			        	    System.out.println("lotes por manzana: "+sql2);
			        	    pmOther.doFetch(sql2);
			        	    while(pmOther.next()) lot += pmOther.getString("lotes") + ",";
			        	    
			        	    //Viviendas por manzana
		                	sql2 = " SELECT COUNT(prty_developmentblockid) AS viv FROM " + SQLUtil.formatKind(sFParams, " properties ") +
			        	    		" WHERE prty_developmentblockid = " + pmDevelopmentBlocks.getString("developmentblocks","dvbl_developmentblockid");		                
			        	    System.out.println("viv por manzana: "+sql2);
			        	    pmOther.doFetch(sql2);
			        	    while(pmOther.next()) viv = pmOther.getInt("viv");
			        	    vivEtapaTotal += viv;
			        	    vivTotal += viv;
		                %>
		                <td class="reportCellText" style="font-size: 7pt">
		                	<%= lot%>
		                </td>
		                <%//= HtmlUtil.formatReportCell(sFParams, "" + lot , BmFieldType.STRING,) %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + viv, BmFieldType.NUMBER) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmDevelopmentBlocks.getString("developmentblocks", "dvbl_startdate"), BmFieldType.DATE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmDevelopmentBlocks.getString("developmentblocks", "dvbl_enddate"), BmFieldType.DATE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmDevelopmentBlocks.getString("developmentblocks", "dvbl_readydate"), BmFieldType.DATE) %>
	                </tr>
	                <% if(i == count){%>
	                	<tr class="reportCellEven">
		                	<td class="reportCellText" colspan="2">&nbsp;</td>
		                	<td class="reportCellNumber"><b><%= vivEtapaTotal%></b></td>
		                	<td class="reportCellText" colspan="3">&nbsp;</td>
		                </tr>
	                <% }
				}
			%>	
			<tr>
				<td colspan="7">&nbsp;</td>
			</tr>
			<tr class="reportCellEven">
				<td class="reportCellText" colspan="3">&nbsp;</td>
	        	<td class="reportCellNumber"><b><%= vivTotal%></b></td>
	        	<td class="reportCellText" colspan="3">&nbsp;</td>
			</tr>
				
	<%	
	}
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
	pmDevelopmentBlocks.close();
	pmOther.close();
	pmConnCount.close();
	%>
  </body>
</html>