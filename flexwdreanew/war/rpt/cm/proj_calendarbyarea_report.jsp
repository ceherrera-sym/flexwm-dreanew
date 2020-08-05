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

<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import= "com.flexwm.shared.cm.BmoProject"%>
<%@page import= "com.flexwm.server.cm.PmProject"%>
<%@page import= "com.flexwm.shared.op.BmoOrder"%>
<%@page import= "com.flexwm.server.op.PmOrder"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.client.ui.UiParams"%>

<%
	BmoProject bmoProject = new BmoProject();
	BmoOrder bmoOrder = new BmoOrder();
	String title = "Calendario de Proyectos por Area";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	String subTitle = "Filtros: , Ordenado por: ";
	String sql = "", where = "", wherePhase = "", whereArea = "", where2 = "";
	String startDate = "", endDate = "", startDateCreate = "", endDateCreate = "", groupFilter = "", filters = "";
	String status = "", paymentStatus = "";
	int wflowTypeId = 0, projectId = 0;
	int wflowCategoryId = 0;
	int venueId = 0;
	int userId = 0;
	int customerId = 0;
	int referralId = 0;
	int areaId = 0;
	int wflowPhaseId = 0;   	
	int programId = 0;
	int isProjectSocial = 0;
	
	//Obtener parametros
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("isProjectSocial") != null) isProjectSocial = Integer.parseInt(request.getParameter("isProjectSocial"));
	if (request.getParameter("wfty_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfty_wflowcategoryid"));
	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
	if (request.getParameter("proj_venueid") != null) venueId = Integer.parseInt(request.getParameter("proj_venueid"));   
	if (request.getParameter("proj_startdate") != null) startDate = request.getParameter("proj_startdate");	
	if (request.getParameter("proj_enddate") != null) endDate = request.getParameter("proj_enddate");
	if (request.getParameter("proj_userid") != null) userId = Integer.parseInt(request.getParameter("proj_userid")); 
	if (request.getParameter("proj_customerid") != null) customerId = Integer.parseInt(request.getParameter("proj_customerid"));
	if (request.getParameter("proj_status") != null) status = request.getParameter("proj_status");
	if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
	if (request.getParameter("user_areaid") != null) areaId = Integer.parseInt(request.getParameter("user_areaid"));
	if (request.getParameter("startdatecreateproject") != null) startDateCreate = request.getParameter("startdatecreateproject");
	if (request.getParameter("enddatecreateproject") != null) endDateCreate = request.getParameter("enddatecreateproject");
    if (request.getParameter("paymentStatus") != null) paymentStatus = request.getParameter("paymentStatus");
    if (request.getParameter("proj_projectid") != null) projectId = Integer.parseInt(request.getParameter("proj_projectid"));
    
	// Filtros listados
	
	// Revisa si solo debe mostrar Proyectos Sociales
	if (isProjectSocial > 0){
		where += " AND wfca_name = 'Proyecto Social' " ;
		where2 += " AND wfca_name = 'Proyecto Social' " ;
		filters += "<i>Categor&iacute;a de Flujo:</i> Proyecto Social, ";
	}else if (wflowCategoryId > 0) {
		where += " AND wfty_wflowcategoryid = " + wflowCategoryId;
		where2 += " AND wfty_wflowcategoryid = " + wflowCategoryId;
		filters += "<i>Categor&iacute;a de Flujo: </i>" + request.getParameter("wfty_wflowcategoryidLabel") + ", ";
	}
	
	if (customerId > 0) {
		where += " AND proj_customerid = " + customerId;
		where2 += " AND proj_customerid = " + customerId;
		filters += "<i>Cliente: </i>" + request.getParameter("proj_customeridLabel") + ", ";
	}
	
	if (userId > 0) {
		String whereUser = "";
		where += " AND proj_userid = " + userId;				
		where2 += " AND proj_userid = " + userId;
		/*
		if (sFParams.restrictData(bmoProject.getProgramCode())) {
			whereUser += " AND proj_userid = " + userId;
		} else {
			whereUser += " AND ( " +
					" proj_userid = " + userId +
					" OR proj_wflowid IN ( " +
						 " SELECT wflw_wflowid FROM wflowusers  " +
						 " LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
						 " WHERE wflu_userid = " + userId + 
						 " AND (wflw_callercode = '" + bmoProject.getProgramCode().toString() + "' OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "') " + 
					 " ) " + 
				 " )";
		}
		where += whereUser;				
		where2 += whereUser;
		*/
		
		filters += "<i>Vendedor: </i>" + request.getParameter("proj_useridLabel") + ", ";
	}
	
	if (wflowTypeId > 0) {
		where += " AND wfty_wflowtypeid = " + wflowTypeId;
		where2 += " AND wfty_wflowtypeid = " + wflowTypeId;
		filters += "<i>Tipo de Flujo: </i>" + request.getParameter("wflw_wflowtypeidLabel") + ", ";
	}
	
	if (wflowPhaseId > 0) {
		where += " AND wflw_wflowphaseid = " + wflowPhaseId;
		where2 += " AND wflw_wflowphaseid = " + wflowPhaseId;
		filters += "<i>Fase de Flujo: </i>" + request.getParameter("wflw_wflowphaseidLabel") + ", ";
	}
	
	if (projectId > 0) {
   		where += " AND proj_projectid = " + projectId;
   		where2 += " AND proj_projectid = " + projectId;
   		filters += "<i>Proyecto: </i>" + request.getParameter("proj_projectidLabel") + ", ";
   	}
	
	if (areaId > 0) {
		whereArea += " AND user_areaid = " + areaId;
		filters += "<i>Departamento: </i>" + request.getParameter("user_areaidLabel") + ", ";
	}
	
	if (referralId > 0) {
		where += " AND cust_referralid = " + referralId;
		where2 += " AND cust_referralid = " + referralId;
		filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
	}
	
	if (venueId > 0) {
		where += " AND proj_venueid = " + venueId;
		where2 += " AND proj_venueid = " + venueId;
		filters += "<i>Lugar: </i>" + request.getParameter("proj_venueidLabel") + ", ";
	}
	
	if (!startDate.equals("")) {
		where += " AND proj_startdate >= '" + startDate + " 00:00' ";
		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
	}
	
	if (!endDate.equals("")) {
		where += " AND proj_startdate <= '" + endDate + " 23:59' ";
		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
	}
	
	if (!status.equals("")) {
   		//where += " AND proj_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("proj_status", status);
        where2 += SFServerUtil.parseFiltersToSql("proj_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("proj_statusLabel") + ", ";
   	}
	
	if (!paymentStatus.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
   		where2 += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
   		filters += "<i>Estatus Pago: </i>" + request.getParameter("paymentStatusLabel") + ", ";
   	}
	
	if (!startDateCreate.equals("")) {
		where += " AND proj_datecreateproject >= '" + startDateCreate + " 00:00' ";
		where2 += " AND proj_datecreateproject >= '" + startDateCreate + " 00:00' ";
		filters += "<i>Fecha Inicio Sis.: </i>" + startDateCreate + ", ";
	}
	
	if (!endDateCreate.equals("")) {
		where += " AND proj_datecreateproject <= '" + endDateCreate + " 23:59' ";
		where2 += " AND proj_datecreateproject <= '" + endDateCreate + " 23:59' ";
		filters += "<i>Fecha Fin Sis.: </i>" + endDateCreate + ", ";
	}
	
	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
   	
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmProject(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0) {
    	where += " AND " + disclosureFilters;
    	where2 += " AND " + disclosureFilters;
    }
	Calendar calStart = Calendar.getInstance();
	Calendar calEnd = Calendar.getInstance();
	
	//Conexion a Base de Datos
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	PmConn pmConn2 = new PmConn(sFParams);
	pmConn2.open();
	
	PmConn pmConn3 = new PmConn(sFParams);
	pmConn3.open();
	
	PmConn pmConn4 = new PmConn(sFParams);
	pmConn4.open();
	
	//Listado de Proyectos
	//abro conexion para inciar el conteo consulta general
	   int co = 0;
	  
			
					
	sql = " SELECT month(proj_startdate) AS monthNumber, proj_startdate, proj_comments FROM projects " +
		  " LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
		  " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
		  " LEFT JOIN wflows on (wflw_wflowid = proj_wflowid) " +         
		  " LEFT JOIN wflowphases on (wfph_wflowphaseid = wflw_wflowphaseid) " +
		  " LEFT JOIN users on (user_userid = proj_userid) " +
		  " LEFT JOIN customers on (cust_customerid = proj_customerid) " +
	      " LEFT JOIN orders ON (proj_orderid = orde_orderid) " +
		  " WHERE proj_projectid > 0 " + where +
		  " GROUP BY month(proj_startdate) ASC";
	pmConn.doFetch(sql);	
	
	//System.out.println("sql: "+sql);

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
		<td align="left" width="60" rowspan="2" valign="top">	
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td class="reportTitle" align="left">
			<%= title %>
		</td>
		<td align="right">
		<% if (!exportExcel.equals("1")) { %>
			<!-- <a href="<%//= request.getRequestURL().toString() + "?" + request.getQueryString() + "&exportexcel=1" %>"><input type="image" id="exportExcel" src="<%//= GwtUtil.getProperUrl(sFParams, "/icons/xls.png")%>" name="exportExcel" title="Exportar a Excel"></a>
			<input type="image" id="printImage" src="<%//= GwtUtil.getProperUrl(sFParams, "/icons/32/wi0146-32.png")%>" name="image" onclick="doPrint()" title="Imprimir">
			-->
		<% } %>
		</td>
	</tr>
	<tr>
		<td class="reportSearchTitle">
			<%= filters %><br>
			<i>Filas rojas son proyectos dentro de las fechas bloqueadas</i>
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>

<%

%>
<br>
<table class="report">    
    <%
    	  String monthName = "";	
          int  i = 0, y = 1;
          while(pmConn.next()) {
        	  
        	  switch(pmConn.getInt("monthNumber") - 1 ){
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
        	  
        	if (i == 0) calStart = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), pmConn.getString("projects", "proj_startdate").substring(0,10));
     %>
		 	 <tr>
			    <td align="center" colspan="12" class="reportGroupCell">
			    	<%= HtmlUtil.stringToHtml(monthName.toUpperCase()) %> <%= calStart.get(calStart.YEAR)%>
			    </td>
		     </tr>
     <%   	  

        	  //Dividir lo proyectos por mes
        	  //int month = calStart.get(calStart.MONTH) + 1;
     			int month = pmConn.getInt("monthNumber");
      	    	
	  	      //Obtener el día del mes
	  	      String firstDay = ""  + calStart.getActualMinimum(calStart.DAY_OF_MONTH);	  	      
	  	      firstDay = "" + calStart.get(calStart.YEAR) + "-" + month + "-" + firstDay ;
	  	    	
	  	      String lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);	  	       
	  	      lastDay = "" + calStart.get(calStart.YEAR) + "-" + month + "-" + lastDay;
	  	  
	  	    
	  	  
	  	 %>     
	  	      <tr>
	  	      	<td class="reportHeaderCellCenter">#</td>
		    	<td class="reportHeaderCell">Fecha Inicio</td>
		    	<td class="reportHeaderCell">Hora Inicio</td>
		    	<td class="reportHeaderCell">Fecha Fin</td>
		    	<td class="reportHeaderCell">Hora Fin</td>
		        <td class="reportHeaderCell" colspan="2">Proyecto</td>		        
		        <td class="reportHeaderCell">Cliente</td>
		        <td class="reportHeaderCell">Lugar</td>
		        <td class="reportHeaderCell">Vendedor</td>
		        <td class="reportHeaderCell">Colaboradores</td>
		        <td class="reportHeaderCell"><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoProject.getComments())%></td>
		      </tr>
	  	 <%     
        	  sql = " SELECT month(proj_startdate) as mes, proj_startdate, proj_enddate, proj_code, proj_name, " +
        	  		" cust_code, cust_displayname, user_code, proj_wflowid, venu_name, proj_comments  FROM projects " +
	        		" LEFT JOIN customers on (cust_customerid = proj_customerid) " +
	        		" LEFT JOIN venues on (venu_venueid = proj_venueid) " +
	        		" LEFT JOIN cities on (city_cityid = venu_cityid) " +
	        		" LEFT JOIN states on (stat_stateid = city_stateid) " +
	        		" LEFT JOIN countries on (cont_countryid = stat_stateid ) " + 
	        		" LEFT JOIN wflowtypes on (wfty_wflowtypeid = proj_wflowtypeid) " +
	        		" LEFT JOIN wflows on (wflw_wflowid = proj_wflowid) " +         
	        		" LEFT JOIN wflowphases on (wfph_wflowphaseid = wflw_wflowphaseid) " +
	        		" LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
	        		" LEFT JOIN users on (user_userid = proj_userid) " +
	   	         	" LEFT JOIN orders ON (proj_orderid = orde_orderid) " +
	        		" WHERE proj_projectid > 0 " + where2 +
	        		" AND proj_startdate >= '" + firstDay + " 00:00' " +
	  	            " AND proj_startdate <= '" + lastDay + " 23:59' " +
	        		" ORDER BY proj_startdate ASC, proj_code ASC ";
        	  pmConn2.doFetch(sql);       
        	  
        	  boolean color = false;
        	  
        	  int mes = 0;
        	  while(pmConn2.next()) {
        		  
        		  if(pmConn2.getInt("mes") != mes){
        			  mes = pmConn2.getInt("mes");
        			  y=1;
        		  }
        		  
            		// Revisar asignaciones en las fechas establecidas 
        			String blockedSql = "SELECT * FROM orderblockdates "
        					+ " WHERE ("
        					+ "		('" + pmConn2.getString("proj_startdate") + "' BETWEEN orbl_startdate AND orbl_enddate)"
        					+ "		OR"
        					+ "		('" + pmConn2.getString("proj_enddate") + "' BETWEEN orbl_startdate AND orbl_enddate) "
        					+ "		) "
        					+ "		OR ("
        					+ "		'" + pmConn2.getString("proj_startdate") + "' < orbl_startdate AND  '" + pmConn2.getString("proj_enddate") + "' > orbl_enddate "
        					+ "		)";
        					
        					pmConn4.doFetch(blockedSql);
        			
        			        			
        		  if(pmConn4.next()){
        			  %>
        			  <tr class="reportCellEven" style="background: red">	
        			  <%
        		  }
        		  else {
        			  %>
        			  <tr class="reportCellEven" >	
        			  <%
        		  }
    %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + y + "&nbsp;", BmFieldType.NUMBER) %>
		          		<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("projects", "proj_startdate").substring(0,10), BmFieldType.DATE) %>
		          		<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("projects", "proj_startdate").substring(10,16), BmFieldType.DATE) %>
		          		<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("projects", "proj_enddate").substring(0,10), BmFieldType.DATE) %>
		          		<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("projects", "proj_enddate").substring(10,16), BmFieldType.DATE) %>

		          		<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("projects", "proj_code"), BmFieldType.STRING) %>                
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn2.getString("projects", "proj_name"), BmFieldType.STRING)) %>
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn2.getString("customers", "cust_code") + " " + pmConn2.getString("customers", "cust_displayname"), BmFieldType.STRING)) %>
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn2.getString("venues", "venu_name"), BmFieldType.STRING)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn2.getString("users", "user_code"), BmFieldType.STRING)) %>
						<%
							//Obtener el Dj buscando por el Area
							String userDj = "";
							sql = " SELECT * FROM wflowusers " +
							      " LEFT JOIN users ON (wflu_userid = user_userid) " +						  
							      " WHERE wflu_wflowid = " + pmConn2.getInt("proj_wflowid") + whereArea;
							pmConn3.doFetch(sql);
							while (pmConn3.next()) userDj += pmConn3.getString("users", "user_code") + " | ";
						%>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, userDj, BmFieldType.STRING)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn2.getString("projects", "proj_comments"), BmFieldType.STRING)) %>	

		          </tr>
		          
    <% 	    y++;
    	  	} //fin pmConn2        	  
        	//Sumar un mes
        	calStart.add(calStart.MONTH, 1);
        	
       i++;
    %>
    	<tr class="">
    		<tr><td colspan="12">&nbsp;</td><tr>
		</tr>
	<%	
       }
    %>
</table>    
    
<% 	
	}// Fin de if(no carga datos)
	
	pmConn4.close();
	pmConn3.close();	
	pmConn2.close();
	pmConn.close(); 

%> 

	<script>
		function doPrint() {
		    var img = document.getElementById('printImage');
    		img.style.visibility = 'hidden';
    		
    		var img2 = document.getElementById('exportExcel');
    		img2.style.visibility = 'hidden';
    		
			//window.print();
			
			img.style.visibility = 'visible';
			img2.style.visibility = 'visible';		
			
			
		}
		System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
				+ " Reporte: "+title
				+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	</script>
  

  </body>
</html>