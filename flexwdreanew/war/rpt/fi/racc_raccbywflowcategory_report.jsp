


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
 
<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowCategory"%>
<%@page import="com.flexwm.server.wf.PmWFlowCategory"%>
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
//Inicializar variables
	String title = "Cuentas por Cobrar por Categoria de Flujo";
	
	String sql = "", where = "", where2 = "";
	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "";
	String status = "", paymentStatus = "", paymentStatus2 = "";
	String filters = "";
	int customerid = 0, orderId = 0, cols= 0, paccountTypeId = 0, companyId = 0 , wflowCategoryId = 0;
	String showTotal  = "N";
	
	// Obtener parametros   	
   	if (request.getParameter("racc_customerid") != null) customerid = Integer.parseInt(request.getParameter("racc_customerid"));
   	if (request.getParameter("racc_companyid") != null) companyId = Integer.parseInt(request.getParameter("racc_companyid"));
   	if (request.getParameter("racc_orderid") != null) orderId = Integer.parseInt(request.getParameter("racc_orderid"));
   	if (request.getParameter("racc_raccounttypeid") != null) paccountTypeId = Integer.parseInt(request.getParameter("racc_raccounttypeid"));
   	if (request.getParameter("wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wflowcategoryid"));
   	if (request.getParameter("racc_status") != null) status = request.getParameter("racc_status");
   	//if (request.getParameter("racc_paymentstatus") != null) paymentStatus = request.getParameter("racc_paymentstatus");
   	if (request.getParameter("paymentstatus2") != null) paymentStatus2 = request.getParameter("paymentstatus2");   	
   	if (request.getParameter("racc_receivedate") != null) receiveDate = request.getParameter("racc_receivedate");
   	if (request.getParameter("racc_duedate") != null) dueDate = request.getParameter("racc_duedate");
   	if (request.getParameter("receiveenddate") != null) receiveEndDate = request.getParameter("receiveenddate");    
    if (request.getParameter("dueenddate") != null) dueEndDate = request.getParameter("dueenddate");
   	

   	BmoRaccount bmoRaccount = new BmoRaccount();
	
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
	
	
	// Filtros listados
	if (customerid > 0) {
		  where += " and racc_customerid = " + customerid;
		  filters += "<i>Cliente: </i>" + request.getParameter("racc_customeridLabel") + ", ";
	}
	
	if (orderId > 0) {
        where += " and racc_orderid = " + customerid;
        filters += "<i>Orden Compra: </i>" + request.getParameter("racc_orderidLabel") + ", ";
    }
	
	if (companyId > 0) {
        where += " and racc_companyid = " + companyId;
        filters += "<i>Empresa: </i>" + request.getParameter("racc_companyidLabel") + ", ";
    }
	
	if (paccountTypeId > 0) {
        where += " and racc_raccounttypeid = " + paccountTypeId;
        filters += "<i>Tipo: </i>" + request.getParameter("racc_raccounttypeidLabel") + ", ";
    }
	
	if (wflowCategoryId > 0) {
		  where2 += " and wfca_wflowcategoryid = " + wflowCategoryId;
		  filters += "<i>Categoria: </i>" + request.getParameter("wfca_wflowcategoryidlabel") + ", ";
	}
	
   	if (!receiveDate.equals("")) {
   		where += " and racc_receivedate >= '" + receiveDate + "'";
   		filters += "<i>Recepci&oacute;n Inicio: </i>" + receiveDate + ", ";
   	}
   	
    if (!receiveEndDate.equals("")) {
        where += " and racc_receivedate <= '" + receiveEndDate + "'";
        filters += "<i>Recepci&oacute;n Final: </i>" + receiveEndDate + ", ";
    }
   	   	
   	if (!dueDate.equals("")) {
        where += " and racc_duedate >= '" + dueDate + "'";
        filters += "<i>Programaci&oacute;n Inicio: </i>" + dueDate + ", ";
    }
    
   	if (!dueEndDate.equals("")) {
        where += " and racc_duedate <= '" + dueEndDate + "'";
        filters += "<i>Programaci&oacute;n Final: </i>" + dueEndDate + ", ";
    }
   	
   	if (!status.equals("")) {   		
   		where += " and racc_status like '" + status + "'";
   		filters += "<i>Estatus: </i>" + request.getParameter("racc_statusLabel") + ", ";	
   	}
   	
   	if (!paymentStatus2.equals("")) {
   		if (!paymentStatus2.equals("B")) {
   			where += " AND racc_paymentstatus like '" + paymentStatus2 + "'";
   	        filters += "<i>Pago: </i>" + request.getParameter("racc_paymentstatusLabel") + ", ";
   		} else {   		 
    		where += " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_PENDING + "' ";
    		filters += "<i>Pago: </i> Pendiente/Parcial" +  ", ";
    	}	
    }
	
%>

<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
	 
</head>

<body class="default" style="padding-right: 10px">

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

<!--<table width="100%" border="1">
	<tr>
		<td width="50%"> <div id="chart_div" style="width: 100%; height: 400px;"></div> </td>
		<td width="50%"> <div id="chart_div2" style="width: 100%; height: 400px;"></div> </td>
	</tr>
</table>-->
<table class="report" border="0">			        
		

<%
		double totalAmount = 0;
		double totalPayments = 0;		
		double sumAmount = 0;
		double sumPayments = 0;		

		PmConn pmRaccounts = new PmConn(sFParams);
		pmRaccounts.open();

		int bmObjectProgramId = sFParams.getProgramId("PROJ");
		PmWFlowCategory pmWflowCategory = new PmWFlowCategory(sFParams);
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		BmFilter filterWFlowCategory= new BmFilter();
		filterWFlowCategory.setValueFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), bmObjectProgramId);
		Iterator<BmObject> wflowCategories = pmWflowCategory.list(filterWFlowCategory).iterator();
		while (wflowCategories.hasNext()) { 
			bmoWFlowCategory = (BmoWFlowCategory)wflowCategories.next();
			sumAmount = 0;
			sumPayments = 0;			
		%>
			<TR class="reportCellEven">
				<td align="left" width="" colspan="2">
	        		Categoria Flujo:
	        	</td> 
				<%= HtmlUtil.formatReportCell(sFParams, bmoWFlowCategory.getName().toString(), BmFieldType.STRING) %>
			</TR>
			<TR class="">              
				<td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Fac</td>
				<td class="reportHeaderCell">Referencia</td>
				<td class="reportHeaderCell">Empresa</td>				
				<td class="reportHeaderCell">Descripci&oacute;n</td>                                    
				<td class="reportHeaderCell">Ingreso</td>
				<td class="reportHeaderCell">Vencimiento</td>
				<td class="reportHeaderCell">Estatus</td>
				<td class="reportHeaderCell">Pago</td>
				<td class="reportHeaderCellRight">Monto</td>
				<td class="reportHeaderCellRight">Pagos</td>
				<td class="reportHeaderCellRight">Saldo</td>
			</TR>
		<%
	
	        //Obtener los cargos ligados al proveedor
	        sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccounts ") + 
	              " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (racc_customerid = cust_customerid)" +                              
	              " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (racc_orderid = orde_orderid)" +              
	      	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (orde_wflowtypeid = wfty_wflowtypeid) " +
	      	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfty_wflowcategoryid = wfca_wflowcategoryid) " +
	              " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (racc_companyid = comp_companyid)" +                                      
	              " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (racc_raccounttypeid = ract_raccounttypeid)" +
	              " WHERE racc_raccountid > 0 " + 
	              " AND wfca_wflowcategoryid = " + bmoWFlowCategory.getId() +
	              " AND racc_total > 0 " + where + " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'";             
	             
	       pmRaccounts.doFetch(sql);
	       while (pmRaccounts.next()) {
   %> 	   
		   		<TR class="reportCellEven">                      
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("orders","orde_code"), BmFieldType.CODE) %>                                    
				   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_invoiceno"), BmFieldType.STRING)) %>
				   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_reference"), BmFieldType.STRING)) %>
				   <%
					   /*if(pmRaccounts.getInt("racc_companyid") > 0){
						   bmoCompany = (BmoCompany)pmCompany.get(pmRaccounts.getInt("racc_companyid"));
					   }*/   
				   %>
				   <%//= HtmlUtil.formatReportCell(sFParams, bmoCompany.getName().toString(), BmFieldType.STRING) %>
				   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("companies","comp_name"), BmFieldType.STRING)) %>				   
				   <% if (pmRaccounts.getString("raccounts","racc_description").length() > 0) { %>
				   		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_description"), BmFieldType.STRING)) %>                                                
				   <% } else { %>
				   		<td class="reportCell">&nbsp;</td>
				   <% } %>  
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_receivedate"), BmFieldType.DATE) %>                                                                                      
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("raccounts","racc_duedate"), BmFieldType.DATE) %>
				   <%                                   
					   bmoRaccount.getStatus().setValue(pmRaccounts.getString("raccounts","racc_status"));
					   bmoRaccount.getPaymentStatus().setValue(pmRaccounts.getString("raccounts","racc_paymentstatus"));
				   %>
				   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRaccount.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
				   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRaccount.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
				   <% 
				   		sumAmount += pmRaccounts.getDouble("racc_total");
				   		sumPayments += pmRaccounts.getDouble("racc_payments");	
				   		totalAmount += pmRaccounts.getDouble("racc_total");
				   		totalPayments += pmRaccounts.getDouble("racc_payments");
				   %> 
				   
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("racc_total"), BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("racc_payments"), BmFieldType.CURRENCY) %>
				   <%= HtmlUtil.formatReportCell(sFParams, pmRaccounts.getString("racc_balance"), BmFieldType.CURRENCY) %>
			  </TR>
		    	 
		   <% } %>
		   <TR class="reportCellEven">
	           <td colspan="9">&nbsp;</td>
	           <%= HtmlUtil.formatReportCell(sFParams, "" + sumAmount, BmFieldType.CURRENCY) %>
	           <%= HtmlUtil.formatReportCell(sFParams, "" + sumPayments, BmFieldType.CURRENCY) %>
	           <%= HtmlUtil.formatReportCell(sFParams, "" + (sumAmount -sumPayments), BmFieldType.CURRENCY) %>
	      </TR>
		   <TR><TD colspan="4">&nbsp;</TD></TR>
    <% } %>		   
    <tr class="reportCellEven">
	    <td colspan="9">Totales:</td>
	    <%= HtmlUtil.formatReportCell(sFParams, "" + totalAmount, BmFieldType.CURRENCY) %>
	    <%= HtmlUtil.formatReportCell(sFParams, "" + totalPayments, BmFieldType.CURRENCY) %>
	    <%= HtmlUtil.formatReportCell(sFParams, "" + (totalAmount - totalPayments), BmFieldType.CURRENCY) %>
	</tr>   
</TABLE>  
<%
pmRaccounts.close(); 
%>  
<% if (print.equals("1")) { %>
<script>
window.print();
</script>
<% } %>
</body>
</html>