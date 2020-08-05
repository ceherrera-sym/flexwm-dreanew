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
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
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
   
   PmConn pmConn2 = new PmConn(sFParams);
   pmConn2.open();
   
   PmConn pmConn3 = new PmConn(sFParams);
   pmConn3.open();
   
   //Obtener los Proveedores de las OC del los proyectos
   sql = " SELECT DISTINCT(supl_supplierid), orde_orderid, supl_name, orde_code FROM requisitions " +
		 " LEFT JOIN suppliers ON (supl_supplierid = reqi_supplierid) " +  
         " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +   
         " WHERE reqi_orderid > 0 " + 
		 " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" + 
         " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
		 " GROUP BY supl_supplierid ";
         
   pmConn.doFetch(sql);
   
%>
<br>
<table class="report">
	<%	
	
	while(pmConn.next()) {        	  
	%>
		
		<tr class="reportCellEven">		
			<td class="reportHeaderCell">Proveedor:</td>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("suppliers", "supl_name"), BmFieldType.STRING)) %></td>
		</tr>
		<tr class="">
	        <td class="reportHeaderCell">Pedido/OC</td>
	        <td class="reportHeaderCell">Fecha</td>
	        <td class="reportHeaderCellRight">Total Pedido</td>
	        <td class="reportHeaderCellRight">Total OC</td>
	        <td class="reportHeaderCellRight">Total CXP</td>	        
	    </tr>    
	<%
		//Obtener los proyectos tomando el pedido
	   sql = " SELECT * FROM requisitions " +
		     " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +
		     " WHERE reqi_supplierid = " + pmConn.getInt("supl_supplierid") +
		     " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
		     " AND reqi_orderid IN (select proj_orderid from projects " +
			 " left join customers on (cust_customerid = proj_customerid) " +
		     " left join venues on (venu_venueid = proj_venueid) " +
		     " left join cities on (city_cityid = venu_cityid) " +
		     " left join states on (stat_stateid = city_stateid) " +
		     " left join countries on (cont_countryid = stat_stateid ) " + 
		     " left join wflowtypes on (wfty_wflowtypeid = proj_wflowtypeid) " +
		     " left join wflows on (wflw_wflowid = proj_wflowid) " +         
	         " left join wflowphases on (wfph_wflowphaseid = wflw_wflowphaseid) " +
		     " left join users on (user_userid = proj_userid) " +	         		     
			 " where proj_projectid > 0 " + where + ")";		     
			 		
		pmConn2.doFetch(sql);
		
		double sumOrderBySupl = 0, sumReqiBySupl = 0, sumPaccBySupl = 0;
	
	    while(pmConn2.next()) { %>
			<tr class="reportCellEven">    
	                <%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("orders", "orde_code") + "/" + 
	                pmConn2.getString("requisitions", "reqi_code"), BmFieldType.STRING) %>
	                <%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("orders", "orde_lockstart"), BmFieldType.STRING) %>
	                <%= HtmlUtil.formatReportCell(sFParams, "" + pmConn2.getDouble("orde_total"), BmFieldType.CURRENCY) %>	                
	                <%
	                	double sumReqi = 0;
	                	//Obtener el total de las ordenes de compra
	                	sql = " SELECT (reqi_total) AS sumReqi FROM requisitions " +
	                		  " WHERE reqi_orderid = " + pmConn2.getInt("orde_orderid") +
	                		  " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'";	                		   
	                	pmConn3.doFetch(sql);
	                	if (pmConn3.next()) sumReqi = pmConn3.getDouble("sumReqi"); 
	                %>
	                <%= HtmlUtil.formatReportCell(sFParams, "" + sumReqi, BmFieldType.CURRENCY) %>
	                <%
                	double sumPacc = 0;
                	//Obtener el total del pagos a la OC del proveedor
                	sql = " SELECT SUM(pacc_payments) AS sumPacc FROM paccounts " +
                		  " left join paccounttypes on (pacc_paccounttypeid = pact_paccounttypeid) " +	
                	      " LEFT JOIN requisitionreceipts ON (rerc_requisitionreceiptid = pacc_requisitionreceiptid) " +
                		  " LEFT JOIN requisitions ON (reqi_requisitionid = rerc_requisitionid) " +	
                		  " WHERE reqi_orderid = " + pmConn2.getInt("orde_orderid") +
                	      " AND pacc_status = '" + BmoPaccount.STATUS_AUTHORIZED + "'" +
                	      " AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
                		  " AND pacc_paymentstatus <> '" + BmoPaccount.PAYMENTSTATUS_PENDING + "'";
                	pmConn3.doFetch(sql);
                	if (pmConn3.next()) sumPacc = pmConn3.getDouble("sumPacc"); 
                %>
                <%= HtmlUtil.formatReportCell(sFParams, "" + sumPacc, BmFieldType.CURRENCY) %>
			</tr>
			<tr>
				<td colspan="5">&nbsp;</td>
			</tr>
    <%
    	sumOrderBySupl += pmConn2.getDouble("orde_total");
    	sumReqiBySupl += sumReqi;
    	sumPaccBySupl += sumPacc;
		}
	 %>
	 	<!-- Totales por Proveedor -->
		 <tr class="reportCellEven">
		     <td colspan="2">&nbsp;</td>
		     <%= HtmlUtil.formatReportCell(sFParams, "" + sumOrderBySupl, BmFieldType.CURRENCY) %>
		     <%= HtmlUtil.formatReportCell(sFParams, "" + sumReqiBySupl, BmFieldType.CURRENCY) %>
		     <%= HtmlUtil.formatReportCell(sFParams, "" + sumPaccBySupl, BmFieldType.CURRENCY) %>
		 </tr>
	 <%
	    
       }
    %>
</table>    

<% pmConn3.close(); %>    
<% pmConn2.close(); %> 
<% pmConn.close(); %>

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