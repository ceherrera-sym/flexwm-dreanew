
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
 
<%@page import="com.flexwm.shared.fi.BmoBudgetItem"%>
<%@page import="com.flexwm.server.fi.PmBudget"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="javax.script.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%@include file="/inc/login.jsp" %>
<%
	// Inicializar variables
 	String title = "Reporte Presupuestos";
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);

   	String sql = "", where = "", whereSupl = "";
   	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "";
   	String status = "", paymentStatus = "", paymentStatus2 = "";
   	String filters = "";
   	int budgetId = 0;
   	String showTotal  = "N";
   	
   	int programId = 0;
    
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("bgit_budgetid") != null) budgetId = Integer.parseInt(request.getParameter("bgit_budgetid"));
   	

   	BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
   	
   	//Conexiones
   	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();

	//Conexiones
   	PmConn pmConn2 = new PmConn(sFParams);
	pmConn2.open();
	
	//Conexiones
   	PmConn pmConn3 = new PmConn(sFParams);
	pmConn3.open();
	
	// Filtros listados
	if (budgetId > 0) {
		  where += " AND budg_budgetid = " + budgetId;
		  filters += "<i>Presupuesto: </i>" + request.getParameter("bgit_budgetidLabel") + ", ";
	}
	
	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
		
	// Obtener disclosure de datos
    String disclosureFilters = new PmBudget(sFParams).getDisclosureFilters();   
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
	
		//Obtener las Partidas
		int items = 0, reqiTypes = 0, countReqiTypes = 0;
		sql = "SELECT count(*) as items FROM " + SQLUtil.formatKind(sFParams, " budgetitems ");
		pmConn.doFetch(sql);
		if (pmConn.next()) items = pmConn.getInt("items");
		
		sql = "SELECT count(*) as reqiTypes FROM " + SQLUtil.formatKind(sFParams, " requisitiontypes ");
		pmConn.doFetch(sql);
		if (pmConn.next()) reqiTypes = pmConn.getInt("reqiTypes");
		
		//Suma 1 para la columna Total(OC)
		countReqiTypes = reqiTypes + 1;
		reqiTypes += 7;
		
		double[][] budgetItems = new double[items][reqiTypes]; 
		
		sql = " SELECT bgty_name, bgit_amount, bgit_budgetitemid FROM " + SQLUtil.formatKind(sFParams, " budgetitems ") +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (bgit_budgetid = budg_budgetid) " +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +			  		  
			  " where bgit_budgetitemid > 0 " + where;
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
		</td>
		<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<table class="report" border="0">
	<tr>
		<td colspan="16">&nbsp;</td>
	</tr>
</table>
<table class="report" border="0">                   
		<tr class="">
			<td class="reportHeaderCellCenter" colspan="3">Presupuesto</td>
			<td class="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td class="reportHeaderCellCenter" colspan="<%= countReqiTypes%>">Ordenes de Compra</td>
			<td class="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td class="reportHeaderCellCenter" colspan="4">Cuentas por Pagar</td>
		</tr>
        <tr class="">
           <td class="reportHeaderCell">#</td>
           <td class="reportHeaderCell">Partidas</td>           
           <td class="reportHeaderCellRight">Monto</td>
           <td class="">&nbsp;</td>

           <%
           		//Tipos de Ordenes de Compra
           		sql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " requisitiontypes ");
                pmConn2.doFetch(sql);
                while(pmConn2.next()) {
           %>
           		<td class="reportHeaderCellRight"><%= HtmlUtil.stringToHtml(pmConn2.getString("requisitiontypes", "rqtp_name"))%></td>
           <% } %>
           <td class="reportHeaderCellRight">Total</td>
           <td class="">&nbsp;</td>
           <td class="reportHeaderCell">Provisionado</td>
           <td class="reportHeaderCellRight">Pagado</td>
           <td class="reportHeaderCellRight">Por Pagar</td>
           <td class="reportHeaderCellRight">Diferencia</td>
       </tr>
      <%
      	if (budgetId > 0) {
      
	      	int countItems = 0, i = 0, s = 1;
	      	while(pmConn.next()) {
	      %>        
		      <tr>
		           <%= HtmlUtil.formatReportCell(sFParams, "" + s++ , BmFieldType.NUMBER) %>
		      	   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
		      	   <%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getDouble("bgit_amount"), BmFieldType.CURRENCY) %>	      	   
		      	   <%
		      	   		//Montos Items
// 		      	   		budgetItems[countItems][i] += pmConn.getDouble("bgit_amount");
// 		      	   		i++;
		      	   		//Sumar los contratos
// 		      	   		double sumWoco = 0;
		      	   		
// 		      	   		sql = " SELECT SUM(woco_total) AS sumWoco FROM " + SQLUtil.formatKind(sFParams, " workcontracts ") +
// 		      	   			  " WHERE woco_budgetitemid = " + pmConn.getInt("bgit_budgetitemid");	      	   			  
// 		      	   		pmConn2.doFetch(sql);
// 		      	   		if (pmConn2.next()) sumWoco = pmConn2.getDouble("sumWoco");
// 		      	   	    budgetItems[countItems][i] += sumWoco;
// 		      	   	    i++;
		      	   %>
<%-- 		      	   <%= HtmlUtil.formatReportCell(sFParams, "" + sumWoco, BmFieldType.CURRENCY) %> --%>
	   	   			<td>&nbsp;</td>
		      	   <%
			   	   		//Sumar las Ordenes de Compra
			   	   		double sumOC = 0;
		      	   		sql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " requisitiontypes ");
		      	   		pmConn2.doFetch(sql);
		      	   		while(pmConn2.next()) {
		      	   			double sumReqi = 0;
				   	   		sql = " SELECT SUM(rqit_amount) AS sumReqi FROM " + SQLUtil.formatKind(sFParams, " requisitionitems ") +
				   	   			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (reqi_requisitionid = rqit_requisitionid) " +		   	   			  	
			   	   			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" ON (reqi_requisitiontypeid = rqtp_requisitiontypeid) " +		   	   			  	
				   	   			  " WHERE rqit_budgetitemid = " + pmConn.getInt("bgit_budgetitemid") +
				   	   			  " AND reqi_requisitiontypeid = " + pmConn2.getInt("rqtp_requisitiontypeid") +
				   	   			  " GROUP BY rqtp_requisitiontypeid";
				   	   		pmConn3.doFetch(sql);
				   	   		if (pmConn3.next()) { %>
				   	   			<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn3.getDouble("sumReqi"), BmFieldType.CURRENCY) %>
		   	   			<%		sumOC += pmConn3.getDouble("sumReqi");
		   	   			
				   	   		    budgetItems[countItems][i] += pmConn3.getDouble("sumReqi");
				   	   	
				   	   			i++;
				   	       } else {
				   	    	   budgetItems[countItems][i] += 0;
				   	    	   i++;
				   	   %>
				   	   			<%= HtmlUtil.formatReportCell(sFParams, "0", BmFieldType.CURRENCY) %>
				   	   		 
				   	   <%
				   	       }
				   	   		
		      	   		}	
			      	   	budgetItems[countItems][i] += sumOC;
			   	   		i++;
				   	   %>
		   	   			<%= HtmlUtil.formatReportCell(sFParams, "" + sumOC, BmFieldType.CURRENCY) %>
		   	   			<td>&nbsp;</td>
					   	 <%
				   	   		//Sumar las CxP  ligadas a la OC
				   	   		double sumPacc = 0;
				   	   		
				   	   		sql = " SELECT SUM(pait_amount) AS sumPacc FROM " + SQLUtil.formatKind(sFParams, " paccountitems ") +		   	   			  	
				   	   			  " LEFT JOIN" + SQLUtil.formatKind(sFParams, " paccounts")+" ON (pacc_paccountid = pait_paccountid) " +
				   	   		      " LEFT JOIN" + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pacc_paccounttypeid = pact_paccounttypeid) " +
				   	   		      " LEFT JOIN" + SQLUtil.formatKind(sFParams, " requisitionreceipts")+" ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " +	
				   	   		      " LEFT JOIN" + SQLUtil.formatKind(sFParams, " requisitions")+" ON (rerc_requisitionid = reqi_requisitionid) " +	
				   	   			  " WHERE reqi_budgetitemid = " + pmConn.getInt("bgit_budgetitemid") +
				   	   			  " AND pacc_status = '" + BmoPaccount.STATUS_AUTHORIZED + "'" +
				   	   		      " AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'";		   	   		
				   	   		pmConn2.doFetch(sql);
				   	   		if (pmConn2.next()) { 
				   	   			sumPacc = pmConn2.getDouble("sumPacc"); 
				   	   			budgetItems[countItems][i] += sumPacc; 
				   	   			i++;
				   	   	%>
				   	   			
				   	   			<%= HtmlUtil.formatReportCell(sFParams, "" + sumPacc, BmFieldType.CURRENCY) %>		   	   			
				   	   <%
				   	       }
				   	   %>
					   <%
				   	   		//Sumar las CxP Pagadas y Parcialmente Pagadas ligadas a la OC
				   	   		double sumPaccPayments = 0;			   	   		
				   	   		sql = " SELECT SUM(pait_amount) AS sumPaccPayments FROM " + SQLUtil.formatKind(sFParams, " paccountitems ") +
				 	   			  " LEFT JOIN" + SQLUtil.formatKind(sFParams, " paccounts")+" ON (pacc_paccountid = pait_paccountid) " +
				   	   			  " LEFT JOIN" + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pacc_paccounttypeid = pact_paccounttypeid) " +
				   	   		      " LEFT JOIN" + SQLUtil.formatKind(sFParams, " requisitionreceipts")+" ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " +	
				   	   		      " LEFT JOIN" + SQLUtil.formatKind(sFParams, " requisitions")+" ON (rerc_requisitionid = reqi_requisitionid) " +	
				   	   			  " LEFT JOIN" + SQLUtil.formatKind(sFParams, " requisitionitems")+" ON (reqi_requisitionid = rqit_requisitionid) " +	
		   	   			  	      " WHERE rqit_budgetitemid = " + pmConn.getInt("bgit_budgetitemid") +
				   	   			  " AND pacc_status = '" + BmoPaccount.STATUS_AUTHORIZED + "'" +
				   	   			  " AND pacc_paymentstatus = '" + BmoPaccount.PAYMENTSTATUS_PENDING + "' " +
				   	   			  " AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'";
				   	   		pmConn2.doFetch(sql);
				   	   		if (pmConn2.next()) { 
				   	   		   sumPaccPayments = pmConn2.getDouble("sumPaccPayments");
				   	   		   budgetItems[countItems][i] += sumPaccPayments; 
				   	   		   i++;
				   	    %>
				   	   			<%= HtmlUtil.formatReportCell(sFParams, "" + sumPaccPayments, BmFieldType.CURRENCY) %>		   	   			
				   	   <%
				   	       }
				   	   %>
				   	   <%
				   	   		//Sumar las CxP No Pagadas
				   	   		double sumPaccNoPayments = 0;
				   	   		
				   	   		sql = " SELECT SUM(pait_amount) AS sumPaccNoPayments FROM " + SQLUtil.formatKind(sFParams, " paccountitems ") +
				 	   			  " LEFT JOIN" + SQLUtil.formatKind(sFParams, " paccounts")+" ON (pacc_paccountid = pait_paccountid) " +
				   	   		      " LEFT JOIN" + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pacc_paccounttypeid = pact_paccounttypeid) " +
				   	   		      " LEFT JOIN" + SQLUtil.formatKind(sFParams, " requisitionreceipts")+" ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " +	
				   	   		      " LEFT JOIN" + SQLUtil.formatKind(sFParams, " requisitions")+" ON (rerc_requisitionid = reqi_requisitionid) " +	
				   	   			  " LEFT JOIN" + SQLUtil.formatKind(sFParams, " requisitionitems")+" ON (reqi_requisitionid = rqit_requisitionid) " +	
			   	   			  	  " WHERE rqit_budgetitemid = " + pmConn.getInt("bgit_budgetitemid") +
				   	   			  " AND pacc_status = '" + BmoPaccount.STATUS_AUTHORIZED + "'" +
				   	   		      " AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'";		   	   		
				   	   		pmConn2.doFetch(sql);
				   	   		if (pmConn2.next()) { 
				   	   			sumPaccNoPayments = pmConn2.getDouble("sumPaccNoPayments");
				   	   			budgetItems[countItems][i] += sumPaccNoPayments;
				   	   			i++;
				   	   %>
				   	   			<%= HtmlUtil.formatReportCell(sFParams, "" + sumPaccNoPayments, BmFieldType.CURRENCY) %>		   	   			
				   	   <%
				   	       }
				   	   		
				   	   	   budgetItems[countItems][i] += sumPaccNoPayments - sumPaccPayments;
				   	   	   
				   	   	%>
					   
				   	   	<%= HtmlUtil.formatReportCell(sFParams, "" + (sumPaccNoPayments - sumPaccPayments), BmFieldType.CURRENCY) %>
			   	  
		      </tr>
	      <% i = 0; 
	      	
	         } %>
	         <TR>
	         	<td colspan=<%= reqiTypes + 4 %>>&nbsp;</td>
	         </TR>
	         
	       		<TR>
	       			<td class="reportCellCode" colspan="1">Totales:</td>
	       			<%
		       		//Arreglo
		       		for (int x=0;x < 1; x++) {
		       			for (int z=0; z < reqiTypes; z++) {
			       			if(z == 2){ %>
			   	   				<td>&nbsp;</td>
		       				<% } %>
		       				<td class="reportCellCurrency">
		       					<b><%= formatCurrency.format(budgetItems[x][z])%></b>
	       					</td>
	       					<% if(z == (countReqiTypes+1)){%>
	    	   	   				<td>&nbsp;</td>
							<% }
		       			}
		       		} %>
	       		</TR>
        <% } else { %>
        	<TR class="reportCellEven">
        		<script language="javascript">
        			alert("Seleccione un Presupuesto");
        		</script>
			</TR>	
        <% } %>
</TABLE>  
<%

		}// Fin de if(no carga datos)
   pmConn3.close();
   pmConn2.close();	
   pmConn.close();    
%>  
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } %>
  </body>
</html>