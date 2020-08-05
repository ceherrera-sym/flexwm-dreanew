
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

<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
<%@page import= "com.flexwm.server.co.PmPropertySale"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%//@page import="com.flexwm.shared.sf.BmoUser"%>
<%//@page import="com.flexwm.server.sf.PmUser"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%

		//Inicializar variables
 		String title = "Reporte de Gerencial Comercial";
	
		String sql = "",where = "", whereCategory= "", whereWfty = "", whereRacc = "", wherePacc = "", wherePhase = "";
		String whereUser = "", firstDay = "", lastDay = "", whereYear = "", nowYear = "";
		String startDate = "", endDate = "", startDateCreate = "", endDateCreate = "";
		
		
		int wflowTypeId = 0;
		int wflowCategoryId = 0;
		int venueId = 0;
		int userId = 0;
		int customerId = 0;
		int referralId = 0;
		int wflowPhaseId = 0;  
		int wFlowTypeId = 0;
		String status = "";
		String groupFilter = "";
		String filters = "";
		int programId = 0;
		int properties = 0;
		
		//Obtener parametros
		if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
		if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
		if (request.getParameter("prsa_wflowtypeid") != null) wFlowTypeId = Integer.parseInt(request.getParameter("prsa_wflowtypeid"));
		if (request.getParameter("prsa_salesuserid") != null) userId = Integer.parseInt(request.getParameter("prsa_salesuserid"));
		if (request.getParameter("prsa_status") != null) status = request.getParameter("prsa_status");
		if (request.getParameter("yearfield") != null) nowYear = "" + request.getParameter("yearfield");
		
		userId = ((BmoFlexConfig)sFParams.getBmoAppConfig()).getDefaultSalesMan().toInteger();
		PmUser pmUser = new PmUser(sFParams);
		bmoUser = (BmoUser)pmUser.get(userId);
		
		Calendar calStart = Calendar.getInstance();
		if (Integer.parseInt(nowYear) > 0) {			
			calStart.set(calStart.YEAR, Integer.parseInt(nowYear));
		}
		
		nowYear = "" + calStart.get(calStart.YEAR);
	
		
		
		whereYear =  " AND prsa_startDate >= '" + nowYear + "-01-01 00:00'";
		whereYear += " AND prsa_startDate <= '" + nowYear + "-12-31 23:59'";
		
		
		
		String monthName = "";
			
		if (wflowPhaseId > 0) {
			//where += " and wflw_wflowphaseid = " + wflowPhaseId;
			wherePhase += " and wfph_wflowphaseid <= " + wflowPhaseId;			
		}
		
		if (userId > 0) {
			whereUser += " and user_userid = " + userId;
			filters += "<i>Estructura Comercial: </i>" + bmoUser.getFirstname().toString() +  " " + bmoUser.getFatherlastname().toString() + ", ";
		}
	
		if (!status.equals("")) {
	   		where += " and prsa_status like '" + status + "'";
	   		filters += "<i>Estatus: </i>" + request.getParameter("prsa_statusLabel") + ", ";
	   	}
		
		if (wFlowTypeId > 0) {
			whereWfty += " AND wfty_wflowtypeid = " + wFlowTypeId;
		 	filters += "<i>Medio Titulaci&oacute;n: </i>" + request.getParameter("wfty_wflowtypeidLabel") + ", ";
		}
		
		// Obtener disclosure de datos
	    String disclosureFilters = new PmPropertySale(sFParams).getDisclosureFilters();
	    if (disclosureFilters.length() > 0)
	    	where += " AND " + disclosureFilters;
		 
		 filters += "<i>Periodo </i>" + nowYear;
		
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		PmConn pmDevelopment = new PmConn(sFParams);
		pmDevelopment.open();
		
		PmConn pmPhase = new PmConn(sFParams);
		pmPhase.open();
		
		PmConn pmWflowType = new PmConn(sFParams);
		pmWflowType.open();
		
		int countWflowType = 0;
		sql = " SELECT COUNT(*) AS wflowtypes FROM "+ SQLUtil.formatKind(sFParams,"wflowtypes ") +
			  " WHERE wfty_wflowtypeid IN (" +
		      " SELECT wflw_wflowtypeid FROM "+ SQLUtil.formatKind(sFParams,"wflows ") +
			  " INNER JOIN "+ SQLUtil.formatKind(sFParams,"propertysales")+" ON (wflw_wflowid = prsa_wflowid) " +
		      " WHERE prsa_propertysaleid > 0) ";
		pmWflowType.doFetch(sql);
		if (pmWflowType.next()) countWflowType = pmWflowType.getInt("wflowtypes");
				
		sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"wflowtypes ") +
			  " WHERE wfty_wflowtypeid IN (" +
			  " SELECT wflw_wflowtypeid FROM "+ SQLUtil.formatKind(sFParams,"wflows ") +
			  " INNER JOIN "+ SQLUtil.formatKind(sFParams,"propertysales")+" ON (wflw_wflowid = prsa_wflowid) " +
			  " WHERE prsa_propertysaleid > 0) " + whereWfty +
			  " ORDER BY wfty_name";
			  
		pmWflowType.doFetch(sql);
		
		
		int countDeve = 0;
		sql = " SELECT COUNT(*) as deves FROM "+ SQLUtil.formatKind(sFParams,"developmentphases ") +
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +	
			  " WHERE dvph_isactive = 1 ";
		pmDevelopment.doFetch(sql);
		if (pmDevelopment.next()) countDeve = pmDevelopment.getInt("deves"); 
		
		sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"developmentphases ") +
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +	
			  " WHERE dvph_isactive = 1 " + 
			  " ORDER BY dvph_code ";
		pmDevelopment.doFetch(sql);
		
		//Obtener la categoria		
		String phaseCode = "";
		sql = " SELECT wfph_wflowcategoryid, wfph_name FROM "+ SQLUtil.formatKind(sFParams,"wflowphases ") +
		      " WHERE wfph_wflowphaseid = " + wflowPhaseId;
		pmConn.doFetch(sql);
		if (pmConn.next()) { 
			wflowCategoryId = pmConn.getInt("wfph_wflowcategoryid");
			phaseCode = HtmlUtil.stringToHtml(pmConn.getString("wflowphases","wfph_name"));
			wherePhase += " AND wfph_wflowcategoryid = " + wflowCategoryId;
		}
		
		int phases = 0;
		sql = " SELECT COUNT(*) AS phases FROM "+ SQLUtil.formatKind(sFParams,"wflowphases ") +
		      " WHERE wfph_wflowphaseid > 1 " + wherePhase;
		pmPhase.doFetch(sql);
		if (pmPhase.next()) phases = pmPhase.getInt("phases");
		
		sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"wflowphases ") +
		      " WHERE wfph_wflowphaseid > 1 " + wherePhase +
		      " ORDER BY wfph_wflowphaseid ";
		pmPhase.doFetch(sql);
		
		//Arreglo para los expedientes
		int[][] phasesExp = new int[phases][countDeve + countWflowType];
		
		int sumTotal = 0;
		
		if (wflowPhaseId > 0) {
			//where += " and wflw_wflowphaseid = " + wflowPhaseId;
			wherePhase += " and wfph_wflowphaseid <= " + wflowPhaseId;
			filters += " <i>Fase: </i>" + phaseCode + ", ";
		}
		
		//Mostrar las ventas diferentes a canceladas
		where += " AND prsa_status <>  '" + BmoPropertySale.STATUS_CANCELLED + "'" ;

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

<TABLE border="0" cellspacing="0" cellpading="0" width="100%">
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
			Filtros: <%= filters %>
			<br>			
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
	
</TABLE>
<br>
<%
	if (wflowPhaseId > 0) {
%>
		<TABLE class="report" border="0" width="100%">		       				
   				<TR>
					<td class="reportHeaderCell" colspan=<%= countDeve + 1 %>>Ventas por Etapa Con Habitabilidad</td>
   					<td class="" width="20">&nbsp;</td>			       				
					<td class="reportHeaderCell" colspan=<%= countWflowType + 1 %>>Ventas por Tipo de Cr&eacute;dito con Habitabilidad</td>			
				</TR>
				<TR class="">           
			        <td class="reportHeaderCell">Fase</td>
			           <%
			           		while(pmDevelopment.next()) {
			           %>
			           		<td class="reportHeaderCellCenter"><%= pmDevelopment.getString("developmentphases", "dvph_code") %></td>
			           <% } %>
			           <td class="" width="20">&nbsp;</td>	
			           <%
			           		while(pmWflowType.next()) {
			           %>
			           			<td class="reportHeaderCellCenter"><%= HtmlUtil.stringToHtml(pmWflowType.getString("wflowtypes", "wfty_name")) %></td>
			           <%			
			           		}
			           %>
			           <td class="reportHeaderCellCenter">TOTAL</td>
			    </TR>
				
				       <%
				       		//Mostrar las fases
				       		int i = 0, p = 0, d = 0, total = 0;
				       		while(pmPhase.next()) { %>
				       			<tr class="reportCellEvenCenter">	       			
					       			<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmPhase.getString("wflowphases", "wfph_name")), BmFieldType.CODE)%>
					       			<%
					       				//Por desarrollo
					       				pmDevelopment.beforeFirst();
					       				while(pmDevelopment.next()) {
						       				//Contar las viviendas en cada fase
					       					properties = 0;		
						       				sql = " SELECT COUNT(*) AS exp FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
						       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
						       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
						       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
						       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
						       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
						       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
						       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
						       					  " WHERE prty_propertyid > 0 " + where +
						       					  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid") +		       					  		       					  
						       					  " AND wflw_progress < 100 " +
						       					  " AND prty_habitability = 1 " +
						       					  " AND wflw_wflowphaseid = " + pmPhase.getInt("wfph_wflowphaseid") +
						       					 // whereYear +
						       					  " ORDER BY dvph_developmentphaseid";						       				
						       				pmConn.doFetch(sql);						       				
						       				if(pmConn.next()) { 
						       					properties = pmConn.getInt("exp") ;
						       					phasesExp [p][d] += properties;	
						       				}
						       				d++;
						       		%>
						       					<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
						       		<%
						       		   } //Fin while
					       			%>
					       			<td class="" width="20">&nbsp;</td>
					       			<%
				       				//Por Tipo de Flujo
					       			int xt = d;	       			
				       				pmWflowType.beforeFirst();
				       				while(pmWflowType.next()) {       				
				       					properties = 0;				
					       				sql = " SELECT COUNT(*) AS exp FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
					       					  " WHERE prty_propertyid > 0 " + where + //whereYear +			       					  		       					  		       					  
					       					  " AND wflw_progress < 100 " +	
					       					  " AND prty_habitability = 1 " +
					       					  " AND wfty_wflowtypeid = " + pmWflowType.getInt("wfty_wflowtypeid") +
					       					  " AND wflw_wflowphaseid = " + pmPhase.getInt("wfph_wflowphaseid");		       			 		       				
					       				pmConn.doFetch(sql);
					       				if(pmConn.next()) { 
					       					properties = pmConn.getInt("exp") ;
					       					phasesExp [p][d] += properties;		       					
					       		%>
					       					<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
					       		<%		}
				       				
						       			d++;	
					       				}	       				
					       				
					       				//Obtener los totales	       				
					       				for (int x = xt; x < d; x++) {
					       					total += phasesExp [p][x];	
					       				}
					       				
					       				d = 0;
					       			%>
						  			<td class="reportCellNumber">
						  				<b><%= total %></b>
						  			</td>
					       		</tr>
				       <%	
				       		total = 0;
				       		p++;
				       		}
				       %>
				           
					    <tr class="reportCellEvenCenter">
					    	<td class="reportHeaderCell">
					    		<b>Totales:</b>
					    	</td>
				  			<%
				  			sumTotal = 0;
				  			total = 0;
				      		//Arreglo
				  			for (int z=0; z < countDeve; z++) {  				
				  				for (int x=0;x < phases; x++) {      			
				      				total += phasesExp[x][z];
				  				}
				  			%>
				  			<td class="reportHeaderCellCenter">
				  				<%= total %>
				  			</td>
				  			<%	total = 0;
				  			  }
				  			%>
				      		<td class="" width="20">&nbsp;</td>
				      		<%
				  			for (int y=countDeve; y < countDeve + countWflowType; y++) {  				
				  				for (int x=0;x < phases; x++) {
				  					total += phasesExp[x][y];		  					
				  				}
				  			%>	
				  			<td class="reportHeaderCellCenter">
				  				<%= total %>
				  			</td>						  				
				      		<% 
				      			sumTotal += total;
				      			total = 0;						  				      			
				      		  } 
				      		%>
				  			<td class="reportHeaderCellCenter">
				  				<%= sumTotal %>
				  			</td>							      		
				  		</TR>
				 </TD> 		
			</TR>	  		
		</TABLE>		  		
 
		<TABLE class="report" border="0">
		  	<TR class="">
		    	<td colspan=<%= countDeve + 1 %>>
		    		&nbsp;
		    	</td>
		    </TR>
		</TABLE>    
		<!-- Inmubles por Desarrollo -->
					<TABLE class="report" border="0" width="100%">
							<tr class="">
								<td class="reportHeaderCell" colspan=<%= countDeve + 2 %>>Inmuebles por Etapa</td>
								<td class="" width="10">&nbsp;</td>
								<td class="reportHeaderCell" colspan="5">Totales Mensuales <%= nowYear %></td>
							</tr>
					        <tr class="">
					           <td class="reportHeaderCell">Etapa</td>           
					           <%
					           		pmDevelopment.beforeFirst();
					           		while(pmDevelopment.next()) {
					           %>
					           		<td class="reportHeaderCellCenter"><%= pmDevelopment.getString("developmentphases", "dvph_code") %></td>
					           <% } %>
					           <td class="reportHeaderCellCenter">TOTAL</td>
					           <td class="" width="20">&nbsp;</td>
					           <td class="reportHeaderCellCenter">Mes</td>
							   <td class="reportHeaderCellCenter">Ventas</td>
							    <td class="reportHeaderCellCenter">Cancel.</td>
								<td class="reportHeaderCellCenter">Ventas Netas</td>
								<td class="reportHeaderCellCenter"><%= phaseCode %></td>								
					       </tr>
					       <%       	
					           int[][] phasesExp2 = new int[12][4];
							   int d1=0, month = 0;
					       	   String sqlSale = "", sqlCancel = "", sqlPhase = "", sqlDate = "";
						       sqlSale = " SELECT COUNT(*) AS properties FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
										 " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
										 " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
						       			 " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
						       			 " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
										 " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
										 " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
										 " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
										 " WHERE prsa_propertysaleid > 0 ";	
								
						       sqlCancel = " SELECT COUNT(*) AS properties FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
						       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
						       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
										  " WHERE prsa_propertysaleid > 0 " +
										  " AND prsa_status = '" + BmoPropertySale.STATUS_CANCELLED + "'";	
										  
						       
						       sqlPhase = " SELECT COUNT(*) AS properties FROM (SELECT wflw_name, wfsp_name, wflw_wflowid, wfsp_wflowphaseid, wfph_name, max(wfsp_enddate) AS enddate FROM "+ SQLUtil.formatKind(sFParams,"wflowsteps ") +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON (wfsp_wflowid = wflw_wflowid) " +
										  " INNER JOIN "+ SQLUtil.formatKind(sFParams,"propertysales")+" ON (wflw_wflowid = prsa_wflowid) " +	
									      " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfsp_wflowphaseid = wfph_wflowphaseid) " +
									      " WHERE wfsp_progress = 100 " + where +	
									      " AND wfsp_wflowphaseid = " + wflowPhaseId +
									      " AND prsa_status <> '" + BmoPropertySale.STATUS_CANCELLED + "'" +
									      " GROUP BY wflw_wflowid, wfsp_wflowphaseid " +
									      " ORDER BY wflw_wflowid " +
									      "   ) AS s ";
									      
									 
					       
					       		int prtyForSale = 0;
					       		int cansell = 0;
					       %>
					       <TR class="">
				   			<%= HtmlUtil.formatReportCell(sFParams, "Inmuebles", BmFieldType.STRING) %>	       			
				   			<%
				   				phasesExp = new int[10][countDeve];
				   				p = 0;
				   				d = 0;
				   				int numproperties = 0;
				   				pmDevelopment.beforeFirst();
				   				while(pmDevelopment.next()) {				   					
				       				sql = " SELECT dvph_numberproperties FROM "+ SQLUtil.formatKind(sFParams,"developmentphases ") +				       					  
				       					  " WHERE dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid");
				       				pmConn.doFetch(sql);		       				
				       				if(pmConn.next()) { 
				       					numproperties = pmConn.getInt("dvph_numberproperties") ;
				       					phasesExp[p][d] += numproperties;
				       				}			
				       			%>
				       				<%= HtmlUtil.formatReportCell(sFParams, "" + numproperties, BmFieldType.NUMBER)%>
				       			<% 
				       				
				       				d++;
				       				
				   				}        		
				       		
				       			//Calcular el total
				       			for(int x = 0;x < countDeve;x++) {
				       				total += phasesExp[p][x];
				       			}
				       		%>					       		
							<td class="reportCellNumber">
		  						<b><%= total %></b>
		  					</td>
		  					<td class="" width="10">&nbsp;</td>
		  					<%= HtmlUtil.formatReportCell(sFParams, "Enero", BmFieldType.STRING) %>
							<%
								month = 0;
								calStart.set(calStart.get(calStart.YEAR), month, 01);
								//Obtener el día del mes
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
						    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
						    	
						    	int propertiesSale = 0;	    	
								
						    	pmConn.doFetch(sqlSale + sqlDate);		       				
								if(pmConn.next()) {
									propertiesSale = pmConn.getInt("properties");
									phasesExp2[month][d1] += propertiesSale;
								} 
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
							<%
								d1++;
								//Obtener el día del mes
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
						    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
						    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
						    	
						    	int propertiesCancelled = 0;	    	
								
								pmConn.doFetch(sqlCancel + sqlDate);		       				
								if(pmConn.next()) {
									propertiesCancelled = pmConn.getInt("properties"); 
									phasesExp2[month][d1] += propertiesCancelled;
								} 
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
							<%
								d1++;
								phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
							<%
								d++;
								//Calular los pasos de la fase finalizados en el mes
								d1++;
								//Obtener el día del mes
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
						    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
						    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
								properties = 0;
								pmConn.doFetch(sqlPhase + sqlDate);
								if(pmConn.next()) { 
									properties = pmConn.getInt("properties");
									phasesExp2[month][d1] += properties;
								}
											
								d1 = 0;
						    %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>				       		
				       		</TR>
							<TR class="">
					   			<%= HtmlUtil.formatReportCell(sFParams, "Bloqueadas", BmFieldType.STRING) %>	       			
					   			<%
					   				p++;
					   				d = 0;
					   				total = 0;
					   				pmDevelopment.beforeFirst();
					   				while(pmDevelopment.next()) {					   					
					       				sql = " SELECT COUNT(prty_propertyid) AS cansell FROM "+ SQLUtil.formatKind(sFParams,"properties ") +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
					       					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
					       					  " WHERE prty_propertyid > 0 " +
					       					  " AND prty_cansell = 0 " +
					       					  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid");
					       				pmConn.doFetch(sql);		       				
					       				if(pmConn.next()) { 
					       					cansell = pmConn.getInt("cansell") ;
					       					phasesExp[p][d] += cansell;
					       				}			
					       			%>
					       				<%= HtmlUtil.formatReportCell(sFParams, "" + cansell, BmFieldType.NUMBER)%>
					       			<%
					       				d++;
					   				}
					       			//Calcular el total
					       			for(int x = 0;x < countDeve;x++) {					       				
					       				total += phasesExp[p][x];
					       			}
					       		%>					       		
								<td class="reportCellNumber">
			  						<b><%= total %></b>
			  					</td>	
					       		
					       		<td class="" width="10">&nbsp;</td>
					       		<%= HtmlUtil.formatReportCell(sFParams, "Febrero", BmFieldType.STRING) %>
								<%
									month = 1;
									//Obtener el día del mes
									calStart.set(calStart.get(calStart.YEAR), month, 01);
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesSale = 0;	    	
									
									pmConn.doFetch(sqlSale + sqlDate);		       				
									if(pmConn.next()) {
										propertiesSale = pmConn.getInt("properties");
										phasesExp2[month][d1] += propertiesSale;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
								<%
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesCancelled = 0;	    	
									
									pmConn.doFetch(sqlCancel + sqlDate);		       				
									if(pmConn.next()) {
										propertiesCancelled = pmConn.getInt("properties"); 
										phasesExp2[month][d1] += propertiesCancelled;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
								<%
									d1++;
									phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
								<%
									d++;
									//Calular los pasos de la fase finalizados en el mes
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
							    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
									properties = 0;
									pmConn.doFetch(sqlPhase + sqlDate);
									if(pmConn.next()) { 
										properties = pmConn.getInt("properties");
										phasesExp2[month][d1] += properties;
									}
												
									d1 = 0;
							    %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>
							<TR>
							<%= HtmlUtil.formatReportCell(sFParams, "Con Habitabilidad", BmFieldType.STRING) %>
							<%
								p++;
								d = 0;
								total = 0;
				       			prtyForSale = 0;
								int prtySale = 0;       	
								pmDevelopment.beforeFirst();
								while(pmDevelopment.next()) {
									sql = " SELECT COUNT(prty_propertyid) AS forSale FROM "+ SQLUtil.formatKind(sFParams,"properties ") +						  
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
										  " WHERE prty_propertyid > 0 " +											  
										  " AND prty_habitability = 1 " +
										  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid");
									pmConn.doFetch(sql);	
									if(pmConn.next()) { 
										prtySale = pmConn.getInt("forSale");
										phasesExp[p][d] += prtySale;
									}	
							%>
									<%= HtmlUtil.formatReportCell(sFParams, "" + prtySale, BmFieldType.NUMBER)%>
							<% 
				   				
				   				d++;
				   				
								}        		
				   		
					   			//Calcular el total
					   			for(int x = 0;x < countDeve;x++) {
					   				total += phasesExp[p][x];
					   			}
					   		%>
							<td class="reportCellNumber">
		  						<b><%= total %></b>
		  					</td>	
					   		<td class="" width="10">&nbsp;</td>
					   		<%= HtmlUtil.formatReportCell(sFParams, "Marzo", BmFieldType.STRING) %>
							<%
								month = 2;
								//Obtener el día del mes
								calStart.set(calStart.get(calStart.YEAR), month, 01);
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
						    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
						    	
						    	propertiesSale = 0;	    	
								
								pmConn.doFetch(sqlSale + sqlDate);		       				
								if(pmConn.next()) {
									propertiesSale = pmConn.getInt("properties");
									phasesExp2[month][d1] += propertiesSale;
								} 
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
							<%
								d1++;
								//Obtener el día del mes
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
						    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
						    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
						    	
						    	propertiesCancelled = 0;	    	
								
								pmConn.doFetch(sqlCancel + sqlDate);		       				
								if(pmConn.next()) {
									propertiesCancelled = pmConn.getInt("properties"); 
									phasesExp2[month][d1] += propertiesCancelled;
								} 
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
							<%
								d1++;
								phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
							<%
								d++;
								//Calular los pasos de la fase finalizados en el mes
								d1++;
								//Obtener el día del mes
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
						    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
						    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
								properties = 0;
								pmConn.doFetch(sqlPhase + sqlDate);
								if(pmConn.next()) { 
									properties = pmConn.getInt("properties");
									phasesExp2[month][d1] += properties;
								}
											
								d1 = 0;
						    %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>
							<TR>
							<%= HtmlUtil.formatReportCell(sFParams, "Ventas Totales", BmFieldType.STRING) %>	       			
							<%			
								p++;
								d = 0;
								total = 0;
								prtySale = 0;
								pmDevelopment.beforeFirst();
								while(pmDevelopment.next()) {						   				
									sql = " SELECT COUNT(*) AS sale FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
											  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
											  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
							       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
							       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
											  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
											  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
											  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
											  " WHERE prty_propertyid > 0 " + where +
											  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid");													  
					   				pmConn.doFetch(sql);		       				
					   				if(pmConn.next()) { 
					   					prtySale = pmConn.getInt("sale");
					   					phasesExp[p][d] += prtySale;
					   				}			
					   		%>
					   				<%= HtmlUtil.formatReportCell(sFParams, "" + prtySale, BmFieldType.NUMBER)%>
					   				<% 
					   				
					   				d++;
				   				
								}        		
				   		
				   			//Calcular el total
				   			for(int x = 0;x < countDeve;x++) {
				   				total += phasesExp[p][x];
				   			}
				   		%>
						<td class="reportCellNumber">
	  						<b><%= total %></b>
	  					</td>	
					   		<td class="" width="10">&nbsp;</td>
					   		<%= HtmlUtil.formatReportCell(sFParams, "Abril", BmFieldType.STRING) %>
							<%
								month = 3;
								//Obtener el día del mes
								calStart.set(calStart.get(calStart.YEAR), month, 01);
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
						    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
						    	
						    	propertiesSale = 0;	    	
								
								pmConn.doFetch(sqlSale + sqlDate);		       				
								if(pmConn.next()) {
									propertiesSale = pmConn.getInt("properties");
									phasesExp2[month][d1] += propertiesSale;
								} 
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
							<%
								d1++;
								//Obtener el día del mes
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
						    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
						    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
						    	
						    	propertiesCancelled = 0;	    	
								
								pmConn.doFetch(sqlCancel + sqlDate);		       				
								if(pmConn.next()) {
									propertiesCancelled = pmConn.getInt("properties"); 
									phasesExp2[month][d1] += propertiesCancelled;
								} 
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
							<%
								d1++;
								phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
							<%
								d++;
								//Calular los pasos de la fase finalizados en el mes
								d1++;
								//Obtener el día del mes
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
						    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
						    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
								properties = 0;
								pmConn.doFetch(sqlPhase + sqlDate);
								if(pmConn.next()) { 
									properties = pmConn.getInt("properties");
									phasesExp2[month][d1] += properties;
								}
											
								d1 = 0;
						    %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>							
								<td class="reportCellChar" colspan=<%= countDeve + 2 %>><b>Ventas<b></td>	
						   		<td class="" width="10">&nbsp;</td>
						   		<%= HtmlUtil.formatReportCell(sFParams, "Mayo", BmFieldType.STRING) %>
								<%
									month = 4;
									//Obtener el día del mes
									calStart.set(calStart.get(calStart.YEAR), month, 01);
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesSale = 0;	    	
									
									pmConn.doFetch(sqlSale + sqlDate);
									if(pmConn.next()) {
										propertiesSale = pmConn.getInt("properties");
										phasesExp2[month][d1] += propertiesSale;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
								<%
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesCancelled = 0;	    	
									
									pmConn.doFetch(sqlCancel + sqlDate);		       				
									if(pmConn.next()) {
										propertiesCancelled = pmConn.getInt("properties"); 
										phasesExp2[month][d1] += propertiesCancelled;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
								<%
									d1++;
									phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
								<%
									d++;
									//Calular los pasos de la fase finalizados en el mes
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
							    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
									properties = 0;
									
									pmConn.doFetch(sqlPhase + sqlDate);
									if(pmConn.next()) { 
										properties = pmConn.getInt("properties");
										phasesExp2[month][d1] += properties;
									}
												
									d1 = 0;
							    %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>								
							<TR class="">
							<%= HtmlUtil.formatReportCell(sFParams, "Ventas con Hab.", BmFieldType.STRING) %>
							<%
								//Contar las viviendas antes de la fase
								p++;
								d = 0;
								properties = 0;
								total = 0;
								pmDevelopment.beforeFirst();
								while(pmDevelopment.next()) {					
									sql = " SELECT COUNT(*) AS properties FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
					 					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
						       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
						       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
										  " WHERE prty_propertyid > 0 " + where +
										  " AND prty_habitability = 1 " +						  					  
										  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid");						  
									pmConn.doFetch(sql);		       				
									if(pmConn.next())  {
										properties = pmConn.getInt("properties") ;
										phasesExp[p][d] += properties;
									}	
							%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							<% 
				   				
				   				d++;
				   				
								}        		
				   		
					   			//Calcular el total
					   			for(int x = 0;x < countDeve;x++) {
					   				total += phasesExp[p][x];
					   			}
					   		%>
						<td class="reportCellNumber">
	  						<b><%= total %></b>
	  					</td>
											
						   		<td class="" width="10">&nbsp;</td>
						   		<%= HtmlUtil.formatReportCell(sFParams, "Junio", BmFieldType.STRING) %>
								<%
									month = 5;
									//Obtener el día del mes
									calStart.set(calStart.get(calStart.YEAR), month, 01);
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesSale = 0;	    	
									
									pmConn.doFetch(sqlSale + sqlDate);		       				
									if(pmConn.next()) {
										propertiesSale = pmConn.getInt("properties");
										phasesExp2[month][d1] += propertiesSale;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
								<%
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesCancelled = 0;	    	
									
									pmConn.doFetch(sqlCancel + sqlDate);		       				
									if(pmConn.next()) {
										propertiesCancelled = pmConn.getInt("properties"); 
										phasesExp2[month][d1] += propertiesCancelled;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
								<%
									d1++;
									phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
								<%
									d++;
									//Calular los pasos de la fase finalizados en el mes
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	// sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
							    	sqlDate = " WHERE enddate >= '" + firstDay + " 00:00'" + 
							    	          " AND enddate <= '" + lastDay + " 23:59'";
									properties = 0;
									pmConn.doFetch(sqlPhase + sqlDate);
									
									if(pmConn.next()) { 
										properties = pmConn.getInt("properties");
										phasesExp2[month][d1] += properties;
									}
												
									d1 = 0;
							    %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>
							<TR class="">
							<%= HtmlUtil.formatReportCell(sFParams, "Ventas Sin Hab.", BmFieldType.STRING) %>
							<%
							//Contar las viviendas Ventas Sin Habi
							p++;
							d = 0;
							properties = 0;
							total = 0;
							pmDevelopment.beforeFirst();
							while(pmDevelopment.next()) {	
							sql = " SELECT COUNT(*) AS properties FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
					       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
					       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
									  " WHERE prty_propertyid > 0 " + where + 			  
									  " AND (prty_habitability = 0 OR prty_habitability is null) " +						  					  
									  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid");						  
								pmConn.doFetch(sql);		       				
								if(pmConn.next()) { 
									properties += pmConn.getInt("properties");
									phasesExp[p][d] += properties;					
								}
							%>	<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							<% 
								properties = 0;
								d++;
								
							}        		
							
					   			//Calcular el total
					   			for(int x = 0;x < countDeve;x++) {
					   				total += phasesExp[p][x];
					   			}
					   		%>
						<td class="reportCellNumber">
	  						<b><%= total %></b>
	  					</td>
									
						   		<td class="" width="10">&nbsp;</td>
						   		<%= HtmlUtil.formatReportCell(sFParams, "Julio", BmFieldType.STRING) %>
								<%
									month = 6;
									//Obtener el día del mes
									calStart.set(calStart.get(calStart.YEAR), month, 01);
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesSale = 0;	    	
									
									pmConn.doFetch(sqlSale + sqlDate);		       				
									if(pmConn.next()) {
										propertiesSale = pmConn.getInt("properties");
										phasesExp2[month][d1] += propertiesSale;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
								<%
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesCancelled = 0;	    	
									
									pmConn.doFetch(sqlCancel + sqlDate);		       				
									if(pmConn.next()) {
										propertiesCancelled = pmConn.getInt("properties"); 
										phasesExp2[month][d1] += propertiesCancelled;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
								<%
									d1++;
									phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
								<%
									d++;
									//Calular los pasos de la fase finalizados en el mes
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
							    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
									properties = 0;
									pmConn.doFetch(sqlPhase + sqlDate);
									if(pmConn.next()) { 
										properties = pmConn.getInt("properties");
										phasesExp2[month][d1] += properties;
									}
												
									d1 = 0;
							    %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>
							<TR class="">
							<%= HtmlUtil.formatReportCell(sFParams, "Hasta " + phaseCode, BmFieldType.STRING) %>
							<%
								//Contar las viviendas antes de la fase
								p++;
								d = 0;
								total = 0;
								int afterPhase = 0;
								pmDevelopment.beforeFirst();
								while(pmDevelopment.next()) {					
									sql = " SELECT COUNT(*) AS afterPhase FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
					 					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
						       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
						       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
										  " WHERE prsa_propertysaleid > 0 " + where +										  					  
										  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid") +										   
										  " AND wfph_wflowphaseid <= " + wflowPhaseId ;
									pmConn.doFetch(sql);		       				
									if(pmConn.next())  {
										afterPhase = pmConn.getInt("afterPhase") ;
										phasesExp[p][d] += afterPhase;
									}			
							%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + afterPhase, BmFieldType.NUMBER)%>
							<% 
				   				
				   				d++;
				   				
								}        		
				   		
					   			//Calcular el total
					   			for(int x = 0;x < countDeve;x++) {
					   				total += phasesExp[p][x];
					   			}
					   		%>
							<td class="reportCellNumber">
		  						<b><%= total %></b>
		  					</td>
							
						   		<td class="" width="10">&nbsp;</td>
						   		<%= HtmlUtil.formatReportCell(sFParams, "Agosto", BmFieldType.STRING) %>
								<%
									month = 7;
									//Obtener el día del mes
									calStart.set(calStart.get(calStart.YEAR), month, 01);
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay +  " 23:59'";
							    	
							    	propertiesSale = 0;
									pmConn.doFetch(sqlSale + sqlDate);		
									
									if(pmConn.next()) {
										propertiesSale = pmConn.getInt("properties");
										phasesExp2[month][d1] += propertiesSale;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
								<%
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay +  " 23:59'";
							    	
							    	propertiesCancelled = 0;	    	
									
									pmConn.doFetch(sqlCancel + sqlDate);		       				
									if(pmConn.next()) {
										propertiesCancelled = pmConn.getInt("properties"); 
										phasesExp2[month][d1] += propertiesCancelled;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
								<%
									d1++;
									phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
								<%
									d++;
									//Calular los pasos de la fase finalizados en el mes
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
							    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
									properties = 0;
									pmConn.doFetch(sqlPhase + sqlDate);
									if(pmConn.next()) { 
										properties = pmConn.getInt("properties");
										phasesExp2[month][d1] += properties;
									}
												
									d1 = 0;
							    %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>
							<TR class="">			
							<%= HtmlUtil.formatReportCell(sFParams, "Despues " + phaseCode, BmFieldType.STRING) %>
							<%
								p++;
								d = 0;
								total = 0;
								//Contar las viviendas en fase
								int inPhase = 0;
								pmDevelopment.beforeFirst();
								while(pmDevelopment.next()) {					
									sql = " SELECT COUNT(*) AS afterPhase FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
					 					  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
						       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
						       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
										  " WHERE prsa_propertysaleid > 0 " + where +										  
										  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid") +
										  " AND wfph_wflowphaseid > " + wflowPhaseId;
									
									pmConn.doFetch(sql);
									if(pmConn.next()) {
										inPhase = pmConn.getInt("afterPhase");
										phasesExp[p][d] += inPhase;
									}				
							%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + inPhase, BmFieldType.NUMBER)%>
							<% 
				   				
				   				d++;
				   				
								}        		
				   		
					   			//Calcular el total
					   			for(int x = 0;x < countDeve;x++) {
					   				total += phasesExp[p][x];
					   			}
					   		%>
							<td class="reportCellNumber">
		  						<b><%= total %></b>
		  					</td>
						   		<td class="" width="10">&nbsp;</td>
						   		<%= HtmlUtil.formatReportCell(sFParams, "Septiembre", BmFieldType.STRING) %>
								<%
									month = 8;
									//Obtener el día del mes
									calStart.set(calStart.get(calStart.YEAR), month, 01);
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesSale = 0;	    	
									
									pmConn.doFetch(sqlSale + sqlDate);	
									
									if(pmConn.next()) {
										propertiesSale = pmConn.getInt("properties");
										phasesExp2[month][d1] += propertiesSale;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
								<%
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesCancelled = 0;	    	
									
									pmConn.doFetch(sqlCancel + sqlDate);		       				
									if(pmConn.next()) {
										propertiesCancelled = pmConn.getInt("properties"); 
										phasesExp2[month][d1] += propertiesCancelled;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
								<%
									d1++;
									phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
								<%
									d++;
									//Calular los pasos de la fase finalizados en el mes
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
							    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
									properties = 0;
									pmConn.doFetch(sqlPhase + sqlDate);
									if(pmConn.next()) { 
										properties = pmConn.getInt("properties");
										phasesExp2[month][d1] += properties;
									}
												
									d1 = 0;
							    %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>
							<TR class="">			
								<td class="reportCellChar" colspan=<%= countDeve + 2 %>><b>Disponibilidad<b></td>	
						   		<td class="" width="10">&nbsp;</td>
						   		<%= HtmlUtil.formatReportCell(sFParams, "Octubre", BmFieldType.STRING) %>
								<%
									month = 9;
									calStart.set(calStart.get(calStart.YEAR), month, 01);
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay +  " 23:59'";
							    	
							    	propertiesSale = 0;	    	
									
									pmConn.doFetch(sqlSale + sqlDate);									
									if(pmConn.next()) {
										propertiesSale = pmConn.getInt("properties");
										phasesExp2[month][d1] += propertiesSale;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
								<%
									d1++;
									//Obtener el día del mes									
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesCancelled = 0;	    	
									
									pmConn.doFetch(sqlCancel + sqlDate);		       				
									if(pmConn.next()) {
										propertiesCancelled = pmConn.getInt("properties"); 
										phasesExp2[month][d1] += propertiesCancelled;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
								<%
									d1++;
									phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
								<%								
									//Calular los pasos de la fase finalizados en el mes
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
							    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
									properties = 0;
									pmConn.doFetch(sqlPhase + sqlDate);
									if(pmConn.next()) { 
										properties = pmConn.getInt("properties");
										phasesExp2[month][d1] += properties;
									}
												
									d1 = 0;
							    %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>
							<TR class="">
							<%= HtmlUtil.formatReportCell(sFParams, "Disponibles", BmFieldType.STRING) %>
							<%
								p++;
								d = 0;
								total = 0;
				       			prtyForSale = 0;
								prtySale = 0;       	
								pmDevelopment.beforeFirst();
								while(pmDevelopment.next()) {
									sql = " SELECT COUNT(prty_propertyid) AS forSale FROM "+ SQLUtil.formatKind(sFParams,"properties ") +						  
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
										  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
										  " WHERE prty_propertyid > 0 " +											  
										  " AND prty_cansell = 1 " +
										  " AND prty_available = 1 " +										  
										  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid");
									pmConn.doFetch(sql);	
									if(pmConn.next()) { 
										prtySale = pmConn.getInt("forSale");
										phasesExp[p][d] += prtySale;
									}	
							%>
									<%= HtmlUtil.formatReportCell(sFParams, "" + prtySale, BmFieldType.NUMBER)%>
							<% 
				   				
				   				d++;
				   				
								}        		
				   		
					   			//Calcular el total
					   			for(int x = 0;x < countDeve;x++) {
					   				total += phasesExp[p][x];
					   			}
					   		%>
							<td class="reportCellNumber">
		  						<b><%= total %></b>
		  					</td>
					   		<td class="" width="10">&nbsp;</td>
					   		<%= HtmlUtil.formatReportCell(sFParams, "Noviembre", BmFieldType.STRING) %>
							<%
								month = 10;
								//Obtener el día del mes
								calStart.set(calStart.get(calStart.YEAR), month, 01);
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
						    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
						    	
						    	propertiesSale = 0;
								pmConn.doFetch(sqlSale + sqlDate);								
								if(pmConn.next()) {
									propertiesSale = pmConn.getInt("properties");
									phasesExp2[month][d1] += propertiesSale;
								} 
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
							<%
								d1++;
								//Obtener el día del mes
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
						    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
						    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
						    	
						    	propertiesCancelled = 0;	    	
								
								pmConn.doFetch(sqlCancel + sqlDate);		       				
								if(pmConn.next()) {
									propertiesCancelled = pmConn.getInt("properties"); 
									phasesExp2[month][d1] += propertiesCancelled;
								} 
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
							<%
								d1++;
								phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
							<%								
								//Calular los pasos de la fase finalizados en el mes
								d1++;
								//Obtener el día del mes
								firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
								firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay;
						    	
						    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
						    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
						    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
						    	
						    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
						    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
								properties = 0;
								pmConn.doFetch(sqlPhase + sqlDate);								
								if(pmConn.next()) { 
									properties = pmConn.getInt("properties");
									phasesExp2[month][d1] += properties;
								}
											
								d1 = 0;
						    %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>						
							</TR>
							<TR class="">
							
							<%= HtmlUtil.formatReportCell(sFParams, "Disponible con Hab.", BmFieldType.STRING) %>
							<%
								//Contar las viviendas Con Habi.Disp
								p++;
								d = 0;
								properties = 0;
								total = 0;
								pmDevelopment.beforeFirst();
								while(pmDevelopment.next()) {					
									sql = " SELECT COUNT(prty_propertyid) AS properties FROM "+ SQLUtil.formatKind(sFParams,"properties ") +
											  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
											  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
											  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
											  " WHERE prty_propertyid > 0 " + 
											  " AND prty_habitability = 1 " +
											  " AND prty_available = 1 " +												  
											  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid");
										pmConn.doFetch(sql);		       				
										if(pmConn.next()) {
											properties = pmConn.getInt("properties") ;
											phasesExp[p][d] += properties;
										}
							%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							<% 
				   				
				   				d++;
				   				
								}        		
				   		
					   			//Calcular el total
					   			for(int x = 0;x < countDeve;x++) {
					   				total += phasesExp[p][x];
					   			}
					   		%>
							<td class="reportCellNumber">
		  						<b><%= total %></b>
		  					</td>
								<td class="" width="10">&nbsp;</td>
						   		<%= HtmlUtil.formatReportCell(sFParams, "Diciembre", BmFieldType.STRING) %>
								<%
									month = 11;
									//Obtener el día del mes
									calStart.set(calStart.get(calStart.YEAR), month, 01);
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);							    	
						    		firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);										    	
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesSale = 0;	    	
									
									pmConn.doFetch(sqlSale + sqlDate);		       				
									if(pmConn.next()) {
										propertiesSale = pmConn.getInt("properties");
										phasesExp2[month][d1] += propertiesSale;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesSale, BmFieldType.NUMBER) %>							       		
								<%
									d1++;
									//Obtener el día del mes
									firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);										
									firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	sqlDate = " AND prsa_startDate >= '" + firstDay + " 00:00'" +
							    			  " AND prsa_startDate <= '" + lastDay + " 23:59'";
							    	
							    	propertiesCancelled = 0;	    	
									
									pmConn.doFetch(sqlCancel + sqlDate);		       				
									if(pmConn.next()) {
										propertiesCancelled = pmConn.getInt("properties"); 
										phasesExp2[month][d1] += propertiesCancelled;
									} 
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + propertiesCancelled, BmFieldType.NUMBER)%>
								<%
									d1++;
									phasesExp2[month][d1] += propertiesSale - propertiesCancelled;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + (propertiesSale - propertiesCancelled), BmFieldType.NUMBER)%>
								<%								
									//Calular los pasos de la fase finalizados en el mes
									d1++;
									//Obtener el día del mes
							    	firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(firstDay) >= 9)) firstDay = "0" + firstDay;
							    	firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
							    	
							    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
							    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
							    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
							    	
							    	//sqlDate = " WHERE enddate BETWEEN '" + firstDay + "' AND '" + lastDay + "'";
							    	sqlDate = " WHERE enddate >= '" + firstDay  + " 00:00' AND enddate <= '" + lastDay + " 23:59'";
									properties = 0;
									
									pmConn.doFetch(sqlPhase + sqlDate);									
									if(pmConn.next()) { 
										properties = pmConn.getInt("properties");
										phasesExp2[month][d1] += properties;
									}
												
									d1 = 0;
							    %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
							</TR>
							<TR class="">
								<td colspan="<%= countDeve + 2 %>">&nbsp;</td>
								
								<td class="" width="10">&nbsp;</td>
					      		<td colspan="1" class="reportHeaderCell">
					      			Totales:
					      		</td>
								<%
									total = 0;
									sumTotal = 0;
									
									for (int z=0; z < 4; z++) {  				
										for (int x=0;x < 12; x++) {      			
							  				total += phasesExp2[x][z];	      				
										}
									%>  				
							  			<td class="reportHeaderCellCenter">
							  				<%= total %>
							  			</td>	
									<% 	
										total = 0;      			
									} 
								%>
					      		
							</TR>	
					</TABLE>
				</TD>
				<TD>
					
				</TD>
		  </TR>					
		<TABLE class="report" border="0">
				<TR class="">
		    		<td colspan=<%= countDeve + 1 %>>&nbsp;</td>
		    	</TR>
		</TABLE>
		    	<TABLE class="report" border="0" width="100%">
		    	<TR class="">
					<td class="reportHeaderCell" colspan=<%= countDeve + 2 %>>Ventas Mensuales por Etapa <%= nowYear %></td>
					<td class="" width="10">&nbsp;</td>
					<td class="reportHeaderCell" colspan=<%= countDeve + 2 %>>Ventas Mensuales por Gerencia <%= nowYear %></td>
				</TR>
				<TR class="">	
				<td class="reportHeaderCellCenter">Mes</td>
					<%
					int countUsers = 0;
					d = 0;
					//userId = 93;
					sql = " SELECT COUNT(*) AS countusers FROM "+ SQLUtil.formatKind(sFParams,"users ") +
					      " WHERE user_parentid = " + userId;					      
					pmConn.doFetch(sql);
					if (pmConn.next())
						countUsers = pmConn.getInt("countusers");
					
					phasesExp = new int[12][countDeve + countUsers];
					//Recorrer los desarrollos
					pmDevelopment.beforeFirst();
					while(pmDevelopment.next()) {
						%>
						<td class="reportHeaderCellCenter"><%= pmDevelopment.getString("developmentphases", "dvph_code") %></td>
					<% } %>
					<td class="reportHeaderCellCenter">TOTAL</td>
					<td class="" width="10">&nbsp;</td>
					<%	
						
						//Recorrer los desarrollos
						sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"users ") +		      
							  " WHERE user_parentid =  " + userId +
							  " ORDER BY user_code";
						pmConn.doFetch(sql);
						while(pmConn.next()) { 
					%>			
						<td class="reportHeaderCell">
							<%= pmConn.getString("users", "user_firstname").substring(0,1) + "" +
							    pmConn.getString("users", "user_fatherlastname").substring(0,1) + "" +
								pmConn.getString("users", "user_motherlastname").substring(0,1) %>
						</td>			
					<% } %>
					<td class="reportHeaderCellCenter">TOTAL</td>
				</TR>		
				<%
								 
				//Recorrer los meses			
					for (month=0; month < 12; month++) {
						
						switch(month){
				    	  case 0:
				    	    {
				    	      monthName="Enero";
				    	      break;
				    	    }
				    	  case 1:
				    	    {
				    	      monthName="Febrero";
				    	      break;
				    	    }
				    	  case 2:
				    	    {
				    	      monthName="Marzo";
				    	      break;
				    	    }
				    	  case 3:
				    	    {
				    	      monthName="Abril";
				    	      break;
				    	    }
				    	  case 4:
				    	    {
				    	      monthName="Mayo";
				    	      break;
				    	    }
				    	  case 5:
				    	    {
				    	      monthName="Junio";
				    	      break;
				    	    }
				    	  case 6:
				    	    {
				    	      monthName="Julio";
				    	      break;
				    	    }
				    	  case 7:
				    	    {
				    	      monthName="Agosto";
				    	      break;
				    	    }
				    	  case 8:
				    	    {
				    	      monthName="Septiembre";
				    	      break;
				    	    }
				    	  case 9:
				    	    {
				    	      monthName="Octubre";
				    	      break;
				    	    }
				    	  case 10:
				    	    {
				    	      monthName="Noviembre";
				    	      break;
				    	    }
				    	  case 11:
				    	    {
				    	      monthName="Diciembre";
				    	      break;
				    	    }
				    	  default:
				    	    {
				    	      monthName="Error";
				    	      break;
				    	    }
				    	}
				%>
					<TR class="">			
						<%= HtmlUtil.formatReportCell(sFParams, "" + monthName, BmFieldType.STRING) %>
						<%
							
							//Ventas por desarrollo
							//Obtener el día del mes
					    	firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
					    	//if (!(Integer.parseInt(firstDay) >= 9)) firstDay = "0" + firstDay;
					    	firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
					    	
					    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
					    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
					    	lastDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + lastDay;
					    	
					    	properties = 0;
					    	pmDevelopment.beforeFirst();
							while(pmDevelopment.next()) {
								sql = " SELECT COUNT(*) AS properties FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
					       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
					       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
									  " WHERE prsa_propertysaleid > 0 " +
									  " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid") +
									  " AND prsa_status <> '" + BmoPropertySale.STATUS_CANCELLED + "'" +
									  " AND prsa_startDate >= '" + firstDay + " 00:00'" +
									  " AND prsa_startDate <= '" + lastDay + " 23:59'";								
								pmConn.doFetch(sql);		       				
								if(pmConn.next()) {
									properties = pmConn.getInt("properties"); 
									phasesExp[month][d] += properties;
								}						
								d++;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>				
						<% } 
							//Mostrar el Total de Desarrollos
							total = 0;
							for (int x = 0;x < countDeve;x++) {
								total += phasesExp[month][x];
							}
						%>
			  			<td class="reportCellNumber">
			  				<b><%= total %></b>
			  			</td>	
						<td class="" width="10">&nbsp;</td>
						<%	
							//Recorrer los desarrollos							
							sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"users ") +		      
								  " WHERE user_parentid =  " + userId +
						          " ORDER BY user_code";							
							pmConn.doFetch(sql);
							while(pmConn.next()) {
								int loggedUserId = pmConn.getInt("user_userid");								
								sql = " SELECT COUNT(*) AS properties FROM "+ SQLUtil.formatKind(sFParams,"propertysales ") +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON(wflw_wflowid = prsa_wflowid) " +
					       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
					       			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
									  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
									  " WHERE prsa_propertysaleid > 0 " +	
									  " AND prsa_status <> '" + BmoPropertySale.STATUS_CANCELLED + "'" +
									  " AND prsa_startDate >= '" + firstDay + " 00:00'" +
									  " AND prsa_startDate <= '" + lastDay + " 23:59'" +
									  " AND prsa_salesuserid in (" +
												" select user_userid from "+ SQLUtil.formatKind(sFParams,"users ") +
												" where " + 
												" user_userid = " + loggedUserId +
												" or user_userid in ( " +
												" select u2.user_userid from "+ SQLUtil.formatKind(sFParams,"users")+" u1 " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 on (u2.user_parentid = u1.user_userid) " +
												" where u1.user_userid = " + loggedUserId +
												" ) " +
												" or user_userid in ( " +
												" select u3.user_userid from "+ SQLUtil.formatKind(sFParams,"users")+" u1 " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 on (u2.user_parentid = u1.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 on (u3.user_parentid = u2.user_userid) " +
												" where u1.user_userid = " + loggedUserId +
												" ) " +
												" or user_userid in ( " +
												" select u4.user_userid from "+ SQLUtil.formatKind(sFParams,"users")+" u1 " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 on (u2.user_parentid = u1.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 on (u3.user_parentid = u2.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u4 on (u4.user_parentid = u3.user_userid) " +
												" where u1.user_userid = " + loggedUserId +
												" ) " +
												" or user_userid in ( " +
												" select u5.user_userid from"+ SQLUtil.formatKind(sFParams," users")+" u1 " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 on (u2.user_parentid = u1.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 on (u3.user_parentid = u2.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u4 on (u4.user_parentid = u3.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u5 on (u5.user_parentid = u4.user_userid) " +
												" where u1.user_userid = " + loggedUserId +
												" ) " + 
												" ) ";								
								pmConn2.doFetch(sql);								
								if(pmConn2.next()) {									
									properties = pmConn2.getInt("properties");									
									phasesExp[month][d] += properties;
								} 
								d++;
							%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
					<% } 
						//Mostrar el Total de Desarrollos				
						total = 0;
						for (int x = countDeve;x < countUsers + countDeve;x++) {					
							total += phasesExp[month][x];
							
						}
					%>
			  			<td class="reportCellNumber">
			  				<b><%= total %></b>
			  			</td>				
					<%				
						d = 0;
					%>
					</TR>
				<% } %>				
				<TR class="">	
					<td colspan="1" class="reportHeaderCell">
						<b>Totales:</b>
					</td>
					<%
						total = 0;
						sumTotal = 0;
						
						for (int z=0; z < countDeve; z++) {  				
			  				for (int x=0;x < 12; x++) {      			
			      				total += phasesExp[x][z];	      				
			  				}
			  				sumTotal += total;	  				
							%>  				
				  			<td class="reportHeaderCellCenter">
				  				<%= total %>
				  			</td>
			  		<%	
			  			total = 0;      			
						} 
					%>
			  			<td class="reportHeaderCellCenter">
			  				<%= sumTotal %>
			  			</td>
					<td class="" width="10">&nbsp;</td>
					<%
						total = 0;
						sumTotal = 0;
						
						for (int z = countDeve ; z < countUsers + countDeve; z++) {  				
			  				for (int x=0;x < 12; x++) {      			
			      				total += phasesExp[x][z];	      				
			  				}
			  				sumTotal += total;	  				
							%>  				
			  			<td class="reportHeaderCellCenter">
			  				<%= total %>
			  			</td>
			  		<%	
			  			total = 0;      			
						} 
					%>
			  			<td class="reportHeaderCellCenter">
			  				<%= sumTotal %>
			  			</td>
				
				</TR>	
		</TABLE>  
		<TABLE class="report" border="0">
			<TR class="">
	    		<td colspan=<%= countDeve + 1 %>>&nbsp;</td>
	    	</TR>
		</TABLE>    	
		   <!-- Firmas por etapa -->
		<TABLE class="report" border="0" width="100%">
		    	<TR class="">
					<td class="reportHeaderCell" colspan=<%= countDeve + 2 %>><%= phaseCode %> Mensuales por Etapa <%= nowYear %></td>
					<td class="" width="10">&nbsp;</td>
					<td class="reportHeaderCell" colspan=<%= countDeve + 2 %>><%= phaseCode %> por Gerencia <%= nowYear %></td>
				</TR>
				<TR class="">	
				<td class="reportHeaderCellCenter">Mes</td>
					<%
					countUsers = 0;
					d = 0;
					//userId = 93;
					
					sql = " SELECT count(*) as countusers FROM "+ SQLUtil.formatKind(sFParams,"users ") +
						  " WHERE user_parentid = " + userId;						      
					pmConn.doFetch(sql);
					if (pmConn.next())
						countUsers = pmConn.getInt("countusers");
					
					//Recorrer los desarrollos
					pmDevelopment.beforeFirst();
					while(pmDevelopment.next()) {
						%>
						<td class="reportHeaderCellCenter"><%= pmDevelopment.getString("developmentphases", "dvph_code") %></td>
					<% } %>
					<td class="reportHeaderCellCenter">TOTAL</td>
					<td class="" width="10">&nbsp;</td>
					<%	
						//Recorrer los desarrollos
						sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"users ") +		      
							  " WHERE user_parentid =  " + userId +
							  " ORDER BY user_code";
						pmConn.doFetch(sql);
						while(pmConn.next()) { 
					%>			
						<td class="reportHeaderCell">							
							<%= pmConn.getString("users", "user_firstname").substring(0,1) + "" +
							    pmConn.getString("users", "user_fatherlastname").substring(0,1) + "" +
								pmConn.getString("users", "user_motherlastname").substring(0,1) %>
						</td>			
					<% } %>
					<td class="reportHeaderCellCenter">TOTAL</td>
				</TR>		
				<%
				int[][] signByDevePhase = new int[12][countDeve + countUsers];			 
				//Recorrer los meses			
					for (month=0; month < 12; month++) {
						
						switch(month){
				    	  case 0:
				    	    {
				    	      monthName="Enero";
				    	      break;
				    	    }
				    	  case 1:
				    	    {
				    	      monthName="Febrero";
				    	      break;
				    	    }
				    	  case 2:
				    	    {
				    	      monthName="Marzo";
				    	      break;
				    	    }
				    	  case 3:
				    	    {
				    	      monthName="Abril";
				    	      break;
				    	    }
				    	  case 4:
				    	    {
				    	      monthName="Mayo";
				    	      break;
				    	    }
				    	  case 5:
				    	    {
				    	      monthName="Junio";
				    	      break;
				    	    }
				    	  case 6:
				    	    {
				    	      monthName="Julio";
				    	      break;
				    	    }
				    	  case 7:
				    	    {
				    	      monthName="Agosto";
				    	      break;
				    	    }
				    	  case 8:
				    	    {
				    	      monthName="Septiembre";
				    	      break;
				    	    }
				    	  case 9:
				    	    {
				    	      monthName="Octubre";
				    	      break;
				    	    }
				    	  case 10:
				    	    {
				    	      monthName="Noviembre";
				    	      break;
				    	    }
				    	  case 11:
				    	    {
				    	      monthName="Diciembre";
				    	      break;
				    	    }
				    	  default:
				    	    {
				    	      monthName="Error";
				    	      break;
				    	    }
				    	}
				%>
					<TR class="">			
						<%= HtmlUtil.formatReportCell(sFParams, "" + monthName, BmFieldType.STRING) %>
						<%
						calStart.set(calStart.get(calStart.YEAR), month, 01);
						//Calular los pasos de la fase finalizados en el mes
						d1++;
						//Obtener el día del mes
				    	firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);
				    	//if (!(Integer.parseInt(firstDay) >= 9)) firstDay = "0" + firstDay;
				    	firstDay = "" + calStart.get(calStart.YEAR) + "-" + (month + 1) + "-" + firstDay ;
				    	
				    	lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
				    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
				    	lastDay = "" + calStart.get(calStart.YEAR) + "-" +  (month + 1) + "-" + lastDay;
				    	
				    	sqlDate = " WHERE enddate >= '" + firstDay + " 00:00'" + 
				    	          " AND enddate <= '" + lastDay + " 23:59'";				    	
						properties = 0;
						
						pmDevelopment.beforeFirst();
						int devePhase = 0;
						
						while(pmDevelopment.next()) {
							sqlPhase = " SELECT COUNT(*) AS properties FROM (SELECT wflw_name, wfsp_name, wflw_wflowid, wfsp_wflowphaseid, wfph_name, max(wfsp_enddate) AS enddate FROM"+ SQLUtil.formatKind(sFParams," wflowsteps ") +
									   " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON (wfsp_wflowid = wflw_wflowid) " +
									   " INNER JOIN "+ SQLUtil.formatKind(sFParams,"propertysales")+" ON (wflw_wflowid = prsa_wflowid) " +
									   " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON (prsa_propertyid = prty_propertyid) " +
								       " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfsp_wflowphaseid = wfph_wflowphaseid) " +
								       " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON (prty_developmentblockid = dvbl_developmentblockid) " +
									   " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
									   " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (dvph_developmentid = deve_developmentid) " +
								       " WHERE wfsp_progress = 100 " + where +
								       " AND dvph_developmentphaseid = " + pmDevelopment.getInt("dvph_developmentphaseid") +
								       " AND wfsp_wflowphaseid = " + wflowPhaseId +
								       " AND prsa_status <> '" + BmoPropertySale.STATUS_CANCELLED + "'" +
								       " GROUP BY wflw_wflowid, wfsp_wflowphaseid " +
								       " ORDER BY wflw_wflowid " +
								       "   ) AS s ";							
							pmConn.doFetch(sqlPhase + sqlDate);							
							if(pmConn.next()) { 
								properties = pmConn.getInt("properties");
								signByDevePhase[month][devePhase] += properties;
							}
							devePhase ++;
						%>
						<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
					<% }	
						//Mostrar el Total de Desarrollos
						total = 0;
						for (int x = 0;x < countDeve;x++) {
							total += signByDevePhase[month][x];
						}
					%>
						<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.NUMBER)%>
						<td class="" width="10">&nbsp;</td>
					<%	
						sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"users ") +
						      " WHERE user_parentid = " + userId +
							  " ORDER BY user_code";
						pmConn.doFetch(sql);
						while(pmConn.next()) {
							int loggedUserId = pmConn.getInt("user_userid");
							sqlPhase = " SELECT COUNT(*) AS properties FROM (SELECT wflw_name, wfsp_name, wflw_wflowid, wfsp_wflowphaseid, wfph_name, max(wfsp_enddate) AS enddate FROM "+ SQLUtil.formatKind(sFParams,"wflowsteps ") +
									   " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON (wfsp_wflowid = wflw_wflowid) " +
									   " INNER JOIN "+ SQLUtil.formatKind(sFParams,"propertysales")+" ON (wflw_wflowid = prsa_wflowid) " +									   
								       " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wfsp_wflowphaseid = wfph_wflowphaseid) " +								       									   
								       " WHERE wfsp_progress = 100 " +							       
								       " AND wfsp_wflowphaseid = " + wflowPhaseId +
								       " AND prsa_salesuserid in (" +
												" select user_userid from "+ SQLUtil.formatKind(sFParams,"users ") +
												" where " + 
												" user_userid = " + loggedUserId +
												" or user_userid in ( " +
												" select u2.user_userid from "+ SQLUtil.formatKind(sFParams,"users")+" u1 " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 on (u2.user_parentid = u1.user_userid) " +
												" where u1.user_userid = " + loggedUserId +
												" ) " +
												" or user_userid in ( " +
												" select u3.user_userid from "+ SQLUtil.formatKind(sFParams,"users")+" u1 " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 on (u2.user_parentid = u1.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 on (u3.user_parentid = u2.user_userid) " +
												" where u1.user_userid = " + loggedUserId +
												" ) " +
												" or user_userid in ( " +
												" select u4.user_userid from "+ SQLUtil.formatKind(sFParams,"users")+" u1 " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 on (u2.user_parentid = u1.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 on (u3.user_parentid = u2.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u4 on (u4.user_parentid = u3.user_userid) " +
												" where u1.user_userid = " + loggedUserId +
												" ) " +
												" or user_userid in ( " +
												" select u5.user_userid from "+ SQLUtil.formatKind(sFParams,"users")+" u1 " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 on (u2.user_parentid = u1.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 on (u3.user_parentid = u2.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u4 on (u4.user_parentid = u3.user_userid) " +
												" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u5 on (u5.user_parentid = u4.user_userid) " +
												" where u1.user_userid = " + loggedUserId +
												" ) " + 
												" ) " +	
								       " AND prsa_status <> '" + BmoPropertySale.STATUS_CANCELLED + "'" +
								       " GROUP BY wflw_wflowid, wfsp_wflowphaseid " +
								       " ORDER BY wflw_wflowid " +
								       "   ) AS s ";
							
							pmConn2.doFetch(sqlPhase + sqlDate);
							if(pmConn2.next()) { 
								properties = pmConn2.getInt("properties");
								signByDevePhase[month][devePhase] += properties;
							}
							devePhase ++;
						%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + properties, BmFieldType.NUMBER)%>
						<%
							
						}	
						
						//Mostrar el Total de Desarrollos
						total = 0;
						for (int x = countDeve;x < countDeve + countUsers;x++) {
							total += signByDevePhase[month][x];
						}
						%>
						<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.NUMBER)%>
					<TR>
				<% } %>	
				<TR class="">	
					<td colspan="1" class="reportHeaderCell">
						<b>Totales:</b>
					</td>
					<%
						total = 0;
						sumTotal = 0;
						
						for (int z=0; z < countDeve; z++) {  				
			  				for (int x=0;x < 12; x++) {      			
			      				total += signByDevePhase[x][z];	      				
			  				}
			  				sumTotal += total;	  				
							%>  				
				  			<td class="reportHeaderCellCenter">
				  				<%= total %>
				  			</td>
			  		<%	
			  			total = 0;      			
						} 
					%>
			  			<td class="reportHeaderCellCenter">
			  				<%= sumTotal %>
			  			</td>
					<td class="" width="10">&nbsp;</td>
					<%
						total = 0;
						sumTotal = 0;
						
						for (int z = countDeve ; z < countDeve + countUsers; z++) {  				
			  				for (int x=0;x < 12; x++) {      			
			      				total += signByDevePhase[x][z];	      				
			  				}
			  				sumTotal += total;	  				
							%>  				
			  			<td class="reportHeaderCellCenter">
			  				<%= total %>
			  			</td>
			  		<%	
			  			total = 0;      			
						} 
					%>
			  			<td class="reportHeaderCellCenter">
			  				<%= sumTotal %>
			  			</td>
				
			</TR>    	
		 
		<TABLE class="report" border="0">
			<TR class="">
				<td colspan=<%= countDeve + 1 %>>&nbsp;</td>
			</TR>
		</TABLE>
		
<% } else { %>
<TR class="">
	<script language="javascript">
		alert("Seleccione la Fase");
	</script>
</TR>	
<% } %>
<%
	}// Fin de if(no carga datos)
   pmConn2.close();
   pmWflowType.close();
   pmPhase.close();
   pmDevelopment.close();	
   pmConn.close();    
%>  
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } %>
  </body>
</html>