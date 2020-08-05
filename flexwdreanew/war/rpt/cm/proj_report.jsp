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
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.client.ui.UiParams"%>

<% 
	// Exportar a Excel
	String exportExcel = "0";
	if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
	if (exportExcel.equals("1")) {
		response.setContentType("application/vnd.ms-excel");
    	response.setHeader("Content-Disposition", "inline; filename=symgae_report.xls");
    }
%>
<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="/css/<%= defaultCss %>"> 
</head>

<body class="default" style="padding-right: 10px">
<%
	String title = "Reporte de Proyectos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	String subTitle = "Filtros: , Ordenado por: ";

%>

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
			<a href="<%= request.getRequestURL().toString() + "?" + request.getQueryString() + "&exportexcel=1" %>"><input type="image" id="exportExcel" src="<%= GwtUtil.getProperUrl(sFParams, "/icons/xls.png")%>" name="exportExcel" title="Exportar a Excel"></a>
			<input type="image" id="printImage" src="<%= GwtUtil.getProperUrl(sFParams, "/icons/32/wi0146-32.png")%>" name="image" onclick="doPrint()" title="Imprimir">
		<% } %>
		</td>
	</tr>
	<tr>
		<td class="reportSearchTitle">
			<%= subTitle %>
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>

<%
   String sql = "",where = "", wherePhase = "";
   
   int wflowTypeId = 0;
   if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
   
   int venueId = 0;
   if (request.getParameter("proj_venueid") != null) venueId = Integer.parseInt(request.getParameter("proj_venueid"));   
   
   int wflowPhaseId = 0;
   if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid")); 

   if (wflowTypeId > 0) where += " and proj_wflowtypeid = " + wflowTypeId;
   if (venueId > 0) where += " and proj_venueid = " + venueId;
   if (wflowPhaseId > 0) wherePhase += " and wfsp_wflowphaseid = " + wflowPhaseId;
   
   //Fase
  
   //Conexion a Base de Datos
   PmConn pmConn = new PmConn(sFParams);
   pmConn.open();
   
  
   //abro conexion para inciar el conteo consulta general
	    PmConn pmConnCount= new PmConn(sFParams);
   	pmConnCount.open();
   sql = " SELECT COUNT(*) AS contador FROM projects " +
		 " left join customers on (cust_customerid = proj_customerid) " +
	     " left join venues on (venu_venueid = proj_venueid) " +
	     " left join cities on (city_cityid = venu_cityid) " +
	     " left join states on (stat_stateid = city_stateid) " +
	     " left join countries on (cont_countryid = stat_stateid ) " + 
	     " left join wflowtypes on (wfty_wflowtypeid = proj_wflowtypeid) " +
	     " left join wflows on (wflw_wflowid = proj_wflowid) " +         
         " left join wflowphases on (wfph_wflowphaseid = wflw_wflowphaseid) " +
	     " left join users on (user_userid = proj_userid) " +
		 " where proj_projectid > 0 " + where; 
			int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			System.out.println("contador DE REGISTROS --> "+count);
			//if que muestra el mensajede error
			if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
				%>
				
						<%=messageTooLargeList %>
				<% 
			}else{
				 //Listado de Proyectos
   sql = " select * from projects " +
		 " left join customers on (cust_customerid = proj_customerid) " +
	     " left join venues on (venu_venueid = proj_venueid) " +
	     " left join cities on (city_cityid = venu_cityid) " +
	     " left join states on (stat_stateid = city_stateid) " +
	     " left join countries on (cont_countryid = stat_stateid ) " + 
	     " left join wflowtypes on (wfty_wflowtypeid = proj_wflowtypeid) " +
	     " left join wflows on (wflw_wflowid = proj_wflowid) " +         
         " left join wflowphases on (wfph_wflowphaseid = wflw_wflowphaseid) " +
	     " left join users on (user_userid = proj_userid) " +
		 " where proj_projectid > 0 " + where; 
         
         
   pmConn.doFetch(sql);
   
%>
<br>
<table class="report">
    <tr class="">
        <td class="reportHeaderCell">Proyecto</td>
        <td class="reportHeaderCell">Cliente</td>
        <td class="reportHeaderCell">Lugar</td>
        <td class="reportHeaderCell">Ciudad</td>
        <td class="reportHeaderCell">Tipo Evento</td>
        <td class="reportHeaderCell">Fase</td>
        <td class="reportHeaderCell">Productor</td>
        <td class="reportHeaderCell">Fecha</td>

    </tr>
    <%
          int  i = 0;
          while(pmConn.next()) {        	  
    %>
          <tr class="reportCellEven">
                <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects", "proj_code"), BmFieldType.STRING) %>
                
                <% if (pmConn.getString("customers","cust_legalname").equals("NA")) {  
                	String customer = pmConn.getString("customers","cust_fatherlastname") + " " +
                						pmConn.getString("customers","cust_motherlastname") + " " +
                						pmConn.getString("customers","cust_firstname");
                %>
                	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)) %>
                <% } else { %>
                	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("customers","cust_legalname"), BmFieldType.STRING)) %>
                <% } %>
                    
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("venues","venu_name"), BmFieldType.STRING)) %>
					
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("cities", "city_name"), BmFieldType.STRING)) %>
					
				    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
				    
				    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowphases", "wfph_name"), BmFieldType.STRING)) %>	

					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_email"), BmFieldType.EMAIL)) %>
					
				    <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects", "proj_startdate"), BmFieldType.DATE) %>	

          </tr>
    <%
       i++;
       }
         
			}// FIN DEL CONTADOR
			 System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
				+ " Reporte: "+title
				+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
    %>
</table>    
    
<% pmConn.close(); 
pmConnCount.close();
%> 

	<script>
		function doPrint() {
		    var img = document.getElementById('printImage');
    		img.style.visibility = 'hidden';
    		
    		var img2 = document.getElementById('exportExcel');
    		img2.style.visibility = 'hidden';
    		
			window.print();
			
			img.style.visibility = 'visible';
			img2.style.visibility = 'visible';			
		}
	</script>
  

  </body>
</html>