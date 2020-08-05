<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Javier Alberto Hernandez
 * @version 2013-10
 */ -->
 
<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 
	// Imprimir
	String print = "0";
	if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
	if (exportExcel.equals("1")) {
		response.setContentType("application/vnd.ms-excel");
    	response.setHeader("Content-Disposition", "inline; filename=symgae_report.xls");
    }
%>

<%
	// Inicializar variables
 	String title = "Reporte de Flujo";
	
    String sql = "", where = "", whereOther = "";
	String inputDate = "", dueDate = "", inputEndDate = "", dueEndDate = "";
	String bkmvStatus = "";
	String filters = "";
	int supplierId = 0, customerId = 0, cols= 0, bankAccountId = 0 ;
	String showTotal  = "N";
	
	Calendar calStart = Calendar.getInstance();
	Calendar calEnd = Calendar.getInstance();
	
	// Obtener parametros   	
	if (request.getParameter("bkmv_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("bkmv_supplierid"));
	if (request.getParameter("bkmv_customerid") != null) customerId = Integer.parseInt(request.getParameter("bkmv_customerid"));
	if (request.getParameter("bkmv_bankaccountid") != null) bankAccountId = Integer.parseInt(request.getParameter("bkmv_bankaccountid"));
	if (request.getParameter("bkmv_inputDate") != null) inputDate = request.getParameter("bkmv_inputDate");
	if (request.getParameter("inputenddate") != null) inputEndDate = request.getParameter("inputenddate");
	if (request.getParameter("bkmv_dueDate") != null) dueDate = request.getParameter("bkmv_dueDate");
	if (request.getParameter("dueenddate") != null) dueEndDate = request.getParameter("dueenddate");	
	if (request.getParameter("bkmv_status") != null) bkmvStatus = request.getParameter("bkmv_status");
	
	System.out.println(" dueDate" + dueDate);
	if (!inputDate.equals("")) {
       where += " and bkmv_inputdate >= '" + inputDate + "'";
       filters += "<i>Revisión Inicio: </i>" + inputDate + ", ";
   }
   
   if (!inputEndDate.equals("")) {
       where += " and bkmv_inputdate <= '" + inputEndDate + "'";
       filters += "<i>Revisión Final: </i>" + inputEndDate + ", ";
   }
       
   if (!dueDate.equals("")) {
       where += " and bkmv_duedate >= '" + dueDate + "'";
       filters += "<i>Pago Inicio: </i>" + dueDate + ", ";
       calStart = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), dueDate);
   }
   
   if (!dueEndDate.equals("")) {
       where += " and bkmv_duedate <= '" + dueEndDate + "'";
       filters += "<i>Pago Final: </i>" + dueEndDate + ", ";
       calEnd = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), dueEndDate);
   }
   
   if (customerId > 0) {
       where += " and bkmv_customerid = " + customerId;
       filters += "<i>Cliente: </i>" + request.getParameter("bkmv_customeridLabel") + ", ";
   }
   
   if (supplierId > 0) {
       where += " and bkmv_supplierid = " + customerId;
       filters += "<i>Proveedor: </i>" + request.getParameter("bkmv_supplieridLabel") + ", ";
   }
   
   if (!bkmvStatus.equals("")) {
       where += " and bkmv_status like '" + bkmvStatus + "'";
       filters += "<i>Estatus: </i>" + request.getParameter("bkmv_statusLabel") + ", ";
   }
   	
	
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
	
	
   	
   	
   	PmConn pmConn = new PmConn(sFParams);
   	pmConn.open();
   	
   	PmConn pmConn2 = new PmConn(sFParams);
   	pmConn2.open();
   	
   	
   	 
   		  	
%>

<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
</head>

<body class="default">

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



<table class="report" border="0">
	<TR class="">      					
		<td class="reportHeaderCell">Mes</td>
		<td class="reportHeaderCellRight">CXC Pagadas</td>
		<td class="reportHeaderCellRight">CXC Autorizadas</td>
		<td class="reportHeaderCellRight">CXP Pagadas</td>
		<td class="reportHeaderCellRight">CXP Autorizadas</td>
		<td class="reportHeaderCellRight">Diferencia</td>		
	</TR>
	<%
		
	    
	    
    	
	    while(calEnd.compareTo(calStart) > 0 ) {
	    	
		    
	    	int month = calStart.get(calStart.MONTH) + 1;
	    
	    		
	    		
	    	//Obtener el día del mes
	    	String firstDay = ""  + calStart.get(calStart.DAY_OF_MONTH);
	    	//if (!(Integer.parseInt(firstDay) >= 9)) firstDay = "0" + firstDay;
	    	firstDay = "" + calStart.get(calStart.YEAR) + "-" + month + "-" + firstDay ;
	    	
	    	String lastDay = "" + calStart.getActualMaximum(calStart.DAY_OF_MONTH);
	    	//if (!(Integer.parseInt(lastDay) >= 9)) lastDay = "0" + lastDay;
	    	lastDay = "" + calStart.get(calStart.YEAR) + "-" + month + "-" + lastDay;
	    	
	    	
	    	String monthName = "";
	    	
	    	switch(calStart.get(calStart.MONTH)){
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
	
	<TR class="reportCellEven">
      		<%
  			    double sumPacc = 0, sumRacc = 0, sumPaccA = 0, sumRaccA = 0;
      			sql = " SELECT SUM(pacc_deposit) AS totalpaccounts FROM " + SQLUtil.formatKind(sFParams, " paccounts ") +
  			          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
					  " WHERE pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" +
      				  " AND pacc_paymentstatus = '" + BmoPaccount.PAYMENTSTATUS_TOTAL + "' " + 
      				  " AND pacc_duedate >= '" + firstDay + "' AND pacc_duedate <= '" + lastDay + "'";
      				  
      			pmConn.doFetch(sql);
      			if (pmConn.next()) sumPacc = pmConn.getDouble("totalpaccounts");
      			
      			sql = " SELECT SUM(pacc_withdraw) AS totalpaccounts FROM " + SQLUtil.formatKind(sFParams, " paccounts ") +
      				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
      				  " WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
      				  " AND pacc_paymentstatus = '" + BmoPaccount.PAYMENTSTATUS_PENDING + "'" + 
                      " AND pacc_status = '" + BmoPaccount.STATUS_AUTHORIZED + "'" + 
      				  " AND pacc_duedate >= '" + firstDay + "' AND pacc_duedate <= '" + lastDay + "'";
      			pmConn.doFetch(sql);
      			if (pmConn.next()) sumPaccA = pmConn.getDouble("totalpaccounts");
      			
      			sql = " SELECT SUM(racc_deposit) AS totalraccounts FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
      				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
  					  " WHERE ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'" +
      				  " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' " + 
                      " AND racc_duedate >= '" + firstDay + "'  AND racc_duedate <= '" + lastDay + "'";
      			pmConn.doFetch(sql);
      			if (pmConn.next()) sumRacc = pmConn.getDouble("totalraccounts");
      			
      			sql = " SELECT SUM(racc_withdraw) AS totalraccounts FROM " + SQLUtil.formatKind(sFParams, " raccounts ") +
      				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) " +
        			  " WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
    				  " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_PENDING+ "'" + 
                      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" + 
    				  " AND racc_duedate >= '" + firstDay + "'  AND racc_duedate <= '" + lastDay + "'";
    			pmConn.doFetch(sql);
    			if (pmConn.next()) sumRaccA = pmConn.getDouble("totalraccounts");
      			
        		
      			
	      	%>
	      	<%= HtmlUtil.formatReportCell(sFParams, monthName , BmFieldType.STRING) %>
	      	<%= HtmlUtil.formatReportCell(sFParams, "" + sumRacc, BmFieldType.CURRENCY) %>
	      	<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccA, BmFieldType.CURRENCY) %>
	      	<%= HtmlUtil.formatReportCell(sFParams, "" + sumPacc, BmFieldType.CURRENCY) %>
	      	<%= HtmlUtil.formatReportCell(sFParams, "" + sumRaccA, BmFieldType.CURRENCY) %>
	      	<%= HtmlUtil.formatReportCell(sFParams, "" + (sumRacc - sumPacc), BmFieldType.CURRENCY) %>
      </TR>
      <% 
         
         calStart.add(calStart.MONTH, 1);
         } %>
</TABLE>

            
  
<%
	pmConn2.close();
    pmConn.close();
 
%>  

	<% if (print.equals("1")) { %>
	<script>
		window.print();
	</script>
	<% } %>
  </body>
</html>