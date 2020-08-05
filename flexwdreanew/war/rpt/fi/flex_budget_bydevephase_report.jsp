
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
 
<%@page import="com.flexwm.shared.co.BmoDevelopmentPhase"%>
<%@page import="com.flexwm.server.co.PmDevelopmentPhase"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.shared.co.BmoProperty"%>
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
<%@page import="com.flexwm.shared.co.BmoDevelopment"%>
<%@page import="com.flexwm.server.co.PmDevelopment"%>
<%@page import="com.flexwm.shared.fi.BmoBudget"%>
<%@page import="com.flexwm.server.fi.PmBudget"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.shared.fi.BmoBudgetItemType"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowStep"%>
<%@page import="com.flexwm.server.wf.PmWFlowStep"%>
<%@page import="com.flexwm.shared.fi.BmoLoan"%>
<%@page import="com.flexwm.server.fi.PmLoan"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%@include file="/inc/login.jsp" %>
<%
	// Inicializar variables
 	String title = "Reporte Presupuestos Etapas Desarrollo";
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	
	DecimalFormat formateador = new DecimalFormat("#.##%");

   	String sql = "", where = "", whereSupl = "";
   	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "";
   	String status = "", paymentStatus = "", paymentStatus2 = "";
   	String filters = "";
   	int budgetId = 0, developmentPhaseId = 0;
   	String showTotal  = "N";
   	
   	int programId = 0;
    
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));   	
   	if (request.getParameter("dvph_developmentphaseid") != null) developmentPhaseId = Integer.parseInt(request.getParameter("dvph_developmentphaseid"));
   	
   	

   	PmDevelopmentPhase pmDevelopmentPhase = new PmDevelopmentPhase(sFParams);
  	if(developmentPhaseId<=0){%>
   	<TR class="">
   		<script language="javascript">
   			alert("Seleccione la Etapa");
   		</script>
   	</TR>	
   	<% }else{ %>
   	<%
   	BmoDevelopmentPhase bmoDevelopmentPhase = (BmoDevelopmentPhase)pmDevelopmentPhase.get(developmentPhaseId);
 
   	
   	PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
   	BmoDevelopment bmoDevelopment = (BmoDevelopment)pmDevelopment.get(bmoDevelopmentPhase.getDevelopmentId().toInteger());
   	
	BmoBudget bmoBudget = new BmoBudget();
	PmBudget pmBudget = new PmBudget(sFParams);
	bmoBudget = (BmoBudget)pmBudget.get(bmoDevelopmentPhase.getBudgetId().toInteger());
   	
   	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	pmConn.open();
	pmConn2.open();
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
if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
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
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) { %>
<head>
	<title>:::<%= title %>:::</title>	
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

<table border="0" cellspacing="0" width="100%" cellpadding="0" class="report">
	<tr>
		<td align="left" width="" rowspan="6" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="4" class="reportHeaderCell">
			&nbsp;<%= bmoDevelopmentPhase.getName().toHtml()%>
		</td>
	</tr>
	<tr>
	    <th class="reportCellEven">
	        Desarrollo:
	    </th>
	    <td class="reportCellEven">&nbsp;
			<%= bmoDevelopment.getName().toString() %>
	    </td>	     
	    <th class="reportCellEven">
	        Descripci&oacute;n:
	    </th>
	    <td class="reportCellEven">&nbsp;
			<%= bmoDevelopmentPhase.getDescription().toString() %>
	    </td>    
	</tr>
	<tr>	     
	    <th class="reportCellEven">
	        Inicio:
	    </th>
	    <td class="reportCellEven">&nbsp;
			<%= bmoDevelopmentPhase.getStartDate().toString() %>
	    </td>
	    <th class="reportCellEven">
	        T&eacute;rmino:
	    </th>
	    <td class="reportCellEven">&nbsp;
	    	<%= bmoDevelopmentPhase.getEndDate().toString() %>
	    </td>
	</tr>	
	<tr>	     
	    <th class="reportCellEven">
	        Presupuesto:
	    </th>
	    <td class="reportCellEven">&nbsp;
			<%= bmoBudget.getName().toString() %>
	    </td>
	    <th class="reportCellEven">
	        Monto del Presupuesto:
	    </th>
	    <td class="reportCellEven">&nbsp;
	    	<%= formatCurrency.format(bmoBudget.getTotal().toDouble()) %>
	    </td>
	</tr>
	<tr>	     
	    <th class="reportCellEven">
	        No. Viviendas :
	    </th>
	    <td class="reportCellEven">&nbsp;
			<%= bmoDevelopmentPhase.getNumberProperties().toInteger() %>
	    </td>
	    <th class="reportCellEven">
        	Reponsable:
	    </th>
	    <td class="reportCellEven">&nbsp;
	    	<%
	    		PmUser pmUser = new PmUser(sFParams);
	    		BmoUser bmoUser2 = (BmoUser)pmUser.get(bmoDevelopmentPhase.getUserId().toInteger());
	    	%>
				<%= bmoUser2.getFirstname().toString() %>
				<%= bmoUser2.getFatherlastname().toString() %>
	    </td>	
	</tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td>&nbsp</td>
	</tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr valign="top">				
		<td colspan="4">
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
					<TR>
						<td colspan="8" class="reportHeaderCell">
							Presupuestos
						</td>						
					</TR>		
					<TR>
						<td colspan="" class="reportHeaderCell">
							Clave
						</td>
						<td colspan="" class="reportHeaderCell">
							Partida
						</td>
						<td colspan="" class="reportHeaderCellRight">
							Monto
						</td>
						<td colspan="" class="reportHeaderCellRight">
							&nbsp;
						</td>	
						<td colspan="" class="reportHeaderCellRight">
							Por Vivivienda
						</td>							
						<td colspan="" class="reportHeaderCellRight">
							Provisionado
						</td>
						<td colspan="" class="reportHeaderCellRight">
							Pagado
						</td>
						<td colspan="" class="reportHeaderCellRight">
							Saldo
						</td>
					</TR>
					<TR class="reportCellEven">
						<td colspan="8" class="documentText">
							&nbsp;<b>Ingresos</b>
						</td>						
					</TR>
					<%
						double sumDepositBalance = 0; 
						double sumDepositAmount = 0; 
						double sumDepositProvisioned = 0;
						double sumDepositPayments = 0;
						//Total de CxC
						double sumRacc = 0;
						
						//Disposiciones
						sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " budgetitems ") +	
							  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +	      
						      " WHERE bgit_budgetid = " + bmoBudget.getId() +
						      " AND bgty_type = '" + BmoBudgetItemType.TYPE_DEPOSIT + "'" +
						      " ORDER BY bgty_code";
						pmConn.doFetch(sql);		
						while (pmConn.next()) {
							if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getDepositBudgetItemTypeId().toInteger() == 
									pmConn.getInt("bgty_budgetitemtypeid")) {
								sumRacc = pmConn.getDouble("bgit_amount");
							}	
							%>
							<TR class="reportCellEven">
								<td colspan="" align="left" class="documentText">&nbsp;
									<%= pmConn.getString("bgty_code")%>
								</td>
								<td colspan="" align="left" class="documentText">&nbsp;
									<%= HtmlUtil.stringToHtml(pmConn.getString("bgty_name"))%>
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_amount")) %>									
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									&nbsp;									
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_amount") / bmoDevelopmentPhase.getNumberProperties().toInteger())%>
								</td>								
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_provisioned"))%>
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_payments"))%>
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_balance"))%>
								</td>
							</TR>	
					<%		
						sumDepositAmount += pmConn.getDouble("bgit_amount");
						sumDepositProvisioned += pmConn.getDouble("bgit_provisioned");
						sumDepositPayments += pmConn.getDouble("bgit_payments");						
						sumDepositBalance += pmConn.getDouble("bgit_balance");
						}
					%>		
					<TR class="reportCellEven reportCellCurrency">
						<td colspan="3" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(sumDepositAmount) %></b>
						</td>
						<td colspan="2" align="right" class="documentText">&nbsp;
							&nbsp;
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(sumDepositProvisioned) %></b>
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(sumDepositPayments) %></b>
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(sumDepositBalance) %></b>
						</td>
					</TR>
					<TR class="reportCellEven">
						<td colspan="8" class="documentText">
							&nbsp;<b>Egresos</b>
						</td>						
					</TR>
					<%
						double sumWithdrawAmount = 0; 
						double sumWithdrawProvisioned = 0;
						double sumWithdrawPayments = 0;
						double sumWithdrawBalance = 0;
						
						
						sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " budgetitems ") +	
							  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +	      
						      " WHERE bgit_budgetid = " + bmoBudget.getId() +
						      " AND bgty_type = '" + BmoBudgetItemType.TYPE_WITHDRAW + "'" +
						      " ORDER BY bgty_code";
						System.out.println(" sql budjet " + sql);
						pmConn.doFetch(sql);		
						while (pmConn.next()) { %>
							<TR class="reportCellEven">
								<td colspan="" align="left" class="documentText">&nbsp;
									<%= pmConn.getString("bgty_code")%>
								</td>
								<td colspan="" align="left" class="documentText">&nbsp;
									<%= HtmlUtil.stringToHtml(pmConn.getString("bgty_name"))%>
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_amount"))%>
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;	
									<%= formateador.format(pmConn.getDouble("bgit_amount") / sumRacc) %>
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_amount") / bmoDevelopmentPhase.getNumberProperties().toInteger())%>									
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_provisioned"))%>
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_payments"))%>
								</td>
								<td colspan="" align="right" class="documentText">&nbsp;
									<%= formatCurrency.format(pmConn.getDouble("bgit_balance"))%>
								</td>
							</TR>	
					<%		
						sumWithdrawAmount += pmConn.getDouble("bgit_amount");
						sumWithdrawProvisioned += pmConn.getDouble("bgit_provisioned");
						sumWithdrawPayments += pmConn.getDouble("bgit_payments");
						sumWithdrawBalance += pmConn.getDouble("bgit_balance");
						}
					%>
					<%
						double paccNDC = 0;
						//Obtener las OC que se pagaron con Notas de Credito						
						sql = " SELECT SUM(pass_amount) AS totalpayments FROM " + SQLUtil.formatKind(sFParams, " paccountassignments ") +
							  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounts")+" ON (pass_paccountid = pacc_paccountid) " +
							  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +				  			
							  " WHERE pacc_paymentstatus = '" + BmoPaccount.PAYMENTSTATUS_TOTAL + "' " +				  
							  " AND pact_category  = '" + BmoPaccountType.CATEGORY_CREDITNOTE + "'" +  
							  " AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" +
							  " AND pass_foreignpaccountid IN ( " +
							  	" SELECT pacc_paccountid FROM " + SQLUtil.formatKind(sFParams, " paccounts ") +
							    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (pacc_requisitionid = reqi_requisitionid ) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid) " +
							    " WHERE bgit_budgetid = " + bmoBudget.getId() +
							    //" WHERE pacc_requisitionid = " + pmConn.getInt("reqi_requisitionid") +							    
								" AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +										
								" ) ";							
							pmConn2.doFetch(sql);
							if (pmConn2.next()) {
								paccNDC += pmConn2.getDouble("totalpayments");			
							}
							
							sumWithdrawPayments += paccNDC;
					%>
					<TR class="reportCellEven reportCellCurrency">
						<td colspan="" align="left" class="documentText">&nbsp;
							NOTAS CR&Eacute;DITO
						</td>
						<td colspan="" align="left" class="documentText">&nbsp;
							OC pagas con notas de cr&eacute;dito
						</td>
						<td colspan="5" align="right" class="documentText">&nbsp;
							<%= formatCurrency.format(paccNDC) %>
						</td>
						<td colspan="2" align="right" class="documentText">&nbsp;
							&nbsp;
						</td>
					</TR>
					<TR class="reportCellEven reportCellCurrency">
						<td colspan="3" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(sumWithdrawAmount) %></b>
						</td>
						<td colspan="2" align="right" class="documentText">&nbsp;
							&nbsp;
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(sumWithdrawProvisioned) %></b>
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(sumWithdrawPayments) %></b>
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(sumWithdrawBalance) %></b>
						</td>
					</TR>
					
					<TR class="reportCellEven reportCellCurrency">						
						<td colspan="8" align="right" class="documentText">&nbsp;<br>
							<b><%= formatCurrency.format(sumDepositBalance - sumWithdrawBalance) %></b>
						</td>
					</TR>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
	<TR valign="top">
		<td>
				<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px" class="">
					<%		
						BmoLoan bmoLoan = new BmoLoan();
					%>
					<TR valing="top">
						<td colspan="7" class="reportHeaderCell" valign="top">
							Cr&eacute;ditos
						</td>		
					</TR>
					<%
						sql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, "loans")+" WHERE loan_developmentphaseid = " + bmoDevelopmentPhase.getId();
						pmConn.doFetch(sql);
						if (pmConn.next()) {
						 
						PmLoan pmLoan = new PmLoan(sFParams);
						bmoLoan = (BmoLoan)pmLoan.getBy(bmoDevelopmentPhase.getId(), bmoLoan.getDevelopmentPhaseId().getName());
					%>
					
					<TR class="reportCellEven">
						<td class="reportHeaderCell">
							Total
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
					    	<%= formatCurrency.format(bmoLoan.getAmount().toDouble()) %>
					    </td>
					    <td class="reportHeaderCell" colspan="3">
							&nbsp;Disposiciones
						</td>						
					    <td colspan="" align="right" class="documentText">&nbsp;
					    	<%= formatCurrency.format(bmoLoan.getBalance().toDouble()) %>
					    </td>
					</TR>				
					<TR class="reportCellEven">
						<td class="reportHeaderCell">
							Fecha Inicio
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
					    	<%= bmoLoan.getStartDate().toString() %>
					    </td>					
					    <td class="reportHeaderCell" colspan="3">
							&nbsp;Saldo
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
					    	<%= formatCurrency.format(bmoLoan.getDisbursed().toDouble()) %>
					    </td>					
					</TR>
					<TR>
						<TD colspan="7">&nbsp;</TD>
					</TR>
					<TR>
						<td colspan="7" class="reportHeaderCell">
							&nbsp;Disposiciones
						</td>						
					</TR>
					<TR class="reportCellEven">
						<td colspan="" class="reportHeaderCell">
							&nbsp;Fecha
						</td>
						<td colspan="" class="reportHeaderCellCenter">
							&nbsp;Avance
						</td>
						<td colspan="" class="reportHeaderCellRight">
							&nbsp;Monto
						</td>
						<td colspan="" class="reportHeaderCell">
							&nbsp;Fecha
						</td>
						<td colspan="" class="reportHeaderCellCenter">
							&nbsp;Avance
						</td>
						<td colspan="" class="reportHeaderCellRight">
							&nbsp;Monto
						</td>
					</TR>
					<%
						double sumLoanDis = 0;
						//Disposiciones
						sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " loandisbursements ") +
						      " WHERE lodi_loanid = " + bmoLoan.getId() + 
						      " ORDER BY lodi_date"; 		
						pmConn.doFetch(sql);
						int i = 1;
						while (pmConn.next()) {
							sumLoanDis += pmConn.getDouble("lodi_amount");
							%>
							<TR class="reportCellEven">				
							    <td class="documentText">&nbsp;
							    	<%= pmConn.getString("loandisbursements", "lodi_date") %>
							    </td>
							    <td class="documentText" align="center" width="3">
							    	<%= pmConn.getDouble("lodi_progress") %>%
							    </td>
							    <td align="right" class="documentText" width="10">
							    	<%= formatCurrency.format(pmConn.getDouble("lodi_amount")) %>&nbsp;
							    </td>
							    <% if (pmConn.next()) { %>
								    <td class="documentText">
								    	<%= pmConn.getString("loandisbursements", "lodi_date") %>
								    </td>
								    <td class="documentText" align="center" width="3">
								    	<%= pmConn.getDouble("lodi_progress") %>%
								    </td>
								    <td colspan="" align="right" class="documentText" width="10">
								    	<%= formatCurrency.format(pmConn.getDouble("lodi_amount")) %>
								    </td>
								<% } else {	%>
									<td class="documentText" colspan="3">
										&nbsp;
								    </td>
								<% } %>
							</TR>
						    
					<%
						i++;
						}
					%>					
				<% } %>	
					<td>
						&nbsp;&nbsp;
					</td>
			</table> 
		</td>
		<td>
			&nbsp;
		</td>
		<td colspan="" class="" valign="top">
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px" class="">
				<tr valign="top">
					<td colspan="5" class="reportHeaderCell">
						Contratos Obra
					</td>
				</tr>	
				<TR>
					<td colspan="" class="reportHeaderCell">
						Clave
					</td>
					<td colspan="" class="reportHeaderCell">
						Partida
					</td>
					<td colspan="" class="reportHeaderCellRight">
						Monto
					</td>
					<td colspan="" class="reportHeaderCellRight">
						Pagado
					</td>
					<td colspan="" class="reportHeaderCellRight">
						Saldo
					</td>
				</TR>
				<%
					double wocoTotal = 0, reqiPayment = 0, balance = 0;
					double wocoTotalSum = 0, reqiPaymentSum = 0, balanceSum = 0;
					//Contratos
	
					sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " workcontracts ") +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (woco_budgetitemid = bgit_budgetitemid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " works")+" ON (woco_workid = work_workid) " +
						  " WHERE work_developmentphaseid = " + developmentPhaseId +
						  " GROUP BY bgit_budgetitemid " +
						  " ORDER BY bgty_code";
					System.out.println("sql woco" + sql);
					pmConn.doFetch(sql);
					while (pmConn.next()) {
					%>	
						<TR class="reportCellEven">			
							<td colspan="" align="left" class="documentText">&nbsp;
								<%= pmConn.getString("bgty_code")%>
							</td>
							<td colspan="" align="left" class="documentText">&nbsp;							
								<%= HtmlUtil.stringToHtml(pmConn.getString("bgty_name"))%>
							</td>
							<td colspan="" align="right" class="documentText">&nbsp;
								<%
									//Obtener el total de las OC ligadas a un contrato
									wocoTotal = 0;
									sql = " SELECT SUM(woco_totalreal) AS wocoTotal FROM " + SQLUtil.formatKind(sFParams, " workcontracts ") +					     
									      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " works")+" ON (woco_workid = work_workid) " +			  
										  " WHERE work_developmentphaseid = " + developmentPhaseId + 
										  " AND woco_budgetitemid = " + pmConn.getInt("woco_budgetitemid");
									pmConn2.doFetch(sql);
									if (pmConn2.next()) wocoTotal = pmConn2.getDouble("wocoTotal");
									
									wocoTotalSum += wocoTotal;
								%>
								<%= formatCurrency.format(wocoTotal)%>
							</td>
							<td colspan="" align="right" class="documentText">&nbsp;
								<%
									//Obtener los pagos de las OC ligadas a un contrato
									reqiPayment = 0;
									sql = " SELECT SUM(reqi_payments) AS reqiPayment FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
									      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " contractestimations")+" ON (reqi_contractestimationid = coes_contractestimationid) " +
										  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " workcontracts")+" ON (coes_workcontractid = woco_workcontractid) " +	
									      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " works")+" ON (woco_workid = work_workid) " +
										  " WHERE work_developmentphaseid = " + developmentPhaseId +
										  " AND woco_budgetitemid = " + pmConn.getInt("woco_budgetitemid");
									pmConn2.doFetch(sql);									
									if (pmConn2.next()) reqiPayment = pmConn2.getDouble("reqiPayment");	
									
									reqiPaymentSum += reqiPayment;
								%>
								<%= formatCurrency.format(reqiPayment)%>
							</td>
							<td colspan="" align="right" class="documentText">&nbsp;
								<%= formatCurrency.format(wocoTotal - reqiPayment)%>
							</td>
						</TR>
					<%
						}
					%>
					<TR class="reportCellEven reportCellCurrency">
						<td colspan="3" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(wocoTotalSum) %></b>
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(reqiPaymentSum) %></b>
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
							<b><%= formatCurrency.format(wocoTotalSum - reqiPaymentSum) %></b>
						</td>
					</TR>
			</table>	
		</td>
	</tr>	
	<tr valign="top">		
		<td colspan="" class="reportHeaderCell">
			Ordenes de Compra
		</td>
		<td>
			&nbsp;
		</td>		
		<td colspan="" class="reportHeaderCell">
			Mov. Bancarios
		</td>		
	</tr>
	<tr valign="top">
		<TD>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
					<TR>
						<td colspan="" class="reportHeaderCell">
							Clave
						</td>
						<td colspan="" class="reportHeaderCell">
							Partida
						</td>
						<td colspan="" class="reportHeaderCellRight">
							Monto
						</td>
						<td colspan="" class="reportHeaderCellRight">
							Pagado
						</td>
						<td colspan="" class="reportHeaderCellRight">
							Saldo
						</td>
					</TR>
				
				<%		
					//Presupuestos ligados a OC
					double reqiTotal = 0;
					double reqiTotalSum = 0;
					
					reqiPayment = 0; 
					balance = 0;
					reqiPaymentSum = 0; 
					balanceSum = 0;
					sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +	      
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid) " +
					  	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +	
						  " WHERE bgit_budgetid = " + bmoBudget.getId() +
						  " GROUP BY bgit_budgetitemid " + 
						  " ORDER BY bgty_code";
					pmConn.doFetch(sql);
					while (pmConn.next()) { %>
						<TR class="reportCellEven">
							<td colspan="" align="left" class="documentText">&nbsp;
								<%= pmConn.getString("bgty_code")%>
							</td>
							<td colspan="" align="left" class="documentText">&nbsp;
								<%= HtmlUtil.stringToHtml(pmConn.getString("bgty_name"))%>
							</td>
							<td colspan="" align="right" class="documentText">&nbsp;
								<%
								//Obtener los pagos de las OC ligadas a un contrato
								reqiTotal = 0;
								sql = " SELECT SUM(reqi_total) AS reqiTotal FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
									  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid) " +
								      " WHERE bgit_budgetid = " + bmoBudget.getId() +					
								      " AND reqi_status <> '" + BmoRequisition.STATUS_CANCELLED + "'" +
									  " AND bgit_budgetitemid = " + pmConn.getInt("reqi_budgetitemid");
								System.out.println("sql " + sql);
								pmConn2.doFetch(sql);								
								if (pmConn2.next()) reqiTotal = pmConn2.getDouble("reqiTotal");	
								
								reqiTotalSum += reqiTotal;
								%>
								<%= formatCurrency.format(reqiTotal)%>
							</td>
							<td colspan="" align="right" class="documentText">&nbsp;
								<%
								//Obtener los pagos de las OC ligadas a un contrato
								reqiPayment = 0;
								sql = " SELECT SUM(reqi_payments) AS reqiPayment FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
									  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid) " +
								      " WHERE bgit_budgetid = " + bmoBudget.getId() +					
								      " AND reqi_status <> '" + BmoRequisition.STATUS_CANCELLED + "'" +
									  " AND bgit_budgetitemid = " + pmConn.getInt("reqi_budgetitemid");
								pmConn2.doFetch(sql);								
								if (pmConn2.next()) reqiPayment = pmConn2.getDouble("reqiPayment");
								
								reqiPaymentSum += reqiPayment;
								
								
								%>
								<%= formatCurrency.format(reqiPayment)%>
							</td>
							<td colspan="" align="right" class="documentText">&nbsp;
								<%= formatCurrency.format(reqiTotal - reqiPayment) %>
							</td>
						</TR>
				<%
					}
				%>
				<TR class="reportCellEven reportCellCurrency">
					<td colspan="3" align="right" class="documentText">&nbsp;
						<b><%= formatCurrency.format(reqiTotalSum) %></b>
					</td>
					<td colspan="" align="right" class="documentText">&nbsp;
						<b><%= formatCurrency.format(reqiPaymentSum) %></b>
					</td>
					<td colspan="" align="right" class="documentText">&nbsp;
						<b><%= formatCurrency.format(reqiTotalSum - reqiPaymentSum) %></b>
					</td>
				</TR>
				<tr>
					<td>&nbsp</td>
				</tr>
			</table>
		</td>
		<td>
			&nbsp;
		</td>
		<td valign="top">
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">				
				<TR class="">
					<td colspan="" class="reportHeaderCell">
						Clave
					</td>
					<td colspan="" class="reportHeaderCell">
						Partida
					</td>
					<td colspan="2" class="reportHeaderCellRight">
						Pagos
					</td>	
				</TR>
				<%
				double payments = 0, payments2 = 0, sumPayments = 0;
				
				//Presupuestos
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +	      
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +	
					  " WHERE bgit_budgetid = " + bmoBudget.getId() +
					  " AND reqi_paymentstatus <> '" + BmoRequisition.PAYMENTSTATUS_PENDING + "'" +
					  " GROUP BY bgit_budgetitemid " +
					  " ORDER BY bgty_code";	
				pmConn.doFetch(sql);
				while (pmConn.next()) { %>		
					<TR class="reportCellEven">
						<td colspan="" align="left" class="documentText">&nbsp;
							<%= pmConn.getString("bgty_code")%>
						</td>
						<td colspan="" align="left" class="documentText">&nbsp;
							<%= HtmlUtil.stringToHtml(pmConn.getString("bgty_name"))%>
						</td>
						<td colspan="" align="right" class="documentText">&nbsp;
							<%
							//Obtener los pagos de OC mediante Anticipo
							payments = 0;
							sql = " SELECT SUM(bkmv_withdraw) AS payments FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
								  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankmovtypes")+" ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
							      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (bkmv_requisitionid = reqi_requisitionid) " +
								  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid) " +				      
							      " WHERE bgit_budgetid = " + bmoBudget.getId() +						  
								  " AND bgit_budgetitemid = " + pmConn.getInt("reqi_budgetitemid") +
								  " AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'";
							pmConn2.doFetch(sql);							
							if (pmConn2.next()) payments = pmConn2.getDouble("payments");					
							
							sumPayments += payments;
							
							//Obtener los pagos de OC mediante Pago a proveedor
							payments2 = 0;
							sql = " SELECT SUM(bkmc_amount) AS payments FROM " + SQLUtil.formatKind(sFParams, " bankmovconcepts ") +
								  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankmovements")+" ON (bkmc_bankmovementid = bkmv_bankmovementid) " +	
								  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankmovtypes")+" ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +					  
								  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounts")+" ON (bkmc_paccountid = pacc_paccountid) " +
						 		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitionreceipts")+" ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " +
						 		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (rerc_requisitionid = reqi_requisitionid) " +
						 		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (reqi_budgetitemid = bgit_budgetitemid) " +
							      " WHERE bgit_budgetid = " + bmoBudget.getId() +						  
								  " AND bgit_budgetitemid = " + pmConn.getInt("reqi_budgetitemid") +					  
								  " AND bkmt_category <> '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'";
							pmConn2.doFetch(sql);							
							if (pmConn2.next()) payments2 = pmConn2.getDouble("payments");	
							
							sumPayments += payments2;
				
							%>
							<%= formatCurrency.format(payments + payments2)%>
							
						</td>
						<% } %>
					</TR>					
				<TR class="reportCellEven">
					<td colspan="3" align="right" class="documentText">&nbsp;
						<b><%= formatCurrency.format(sumPayments) %></b>
					</td>
				</TR>
				<TR class="reportCellEven">
					<td colspan="2" align="left" class="documentText">&nbsp;
						NOTAS CR&Eacute;DITO OC
					</td>
					<%	
					//double paccNDC = 0;
					
					//Presupuestos
					/*sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " requisitions " +	      
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems ON (reqi_budgetitemid = bgit_budgetitemid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +	
						  " WHERE bgit_budgetid = " + bmoBudget.getId() +
						  " AND reqi_paymentstatus <> '" + BmoRequisition.PAYMENTSTATUS_REVISION + "'" ;						  	
					pmConn.doFetch(sql);
					while (pmConn.next()) {*/ 
					
						//Obtener las OC que se pagaron con Notas de Credito						
						/*sql = " SELECT SUM(pass_amount) AS totalpayments FROM " + SQLUtil.formatKind(sFParams, " paccountassignments " +
							  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounts ON (pass_paccountid = pacc_paccountid) " +
							  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) " +				  			
							  " WHERE pacc_paymentstatus = '" + BmoPaccount.PAYMENTSTATUS_TOTAL + "' " +				  
							  " AND pact_category  = '" + BmoPaccountType.CATEGORY_CREDITNOTE + "'" +  
							  " AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" +
							  " AND pass_foreignpaccountid IN ( " +
							  	" SELECT pacc_paccountid FROM " + SQLUtil.formatKind(sFParams, " paccounts " +
							    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions ON (pacc_requisitionid = reqi_requisitionid ) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) " +
								" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems ON (reqi_budgetitemid = bgit_budgetitemid) " +
							    " WHERE bgit_budgetid = " + bmoBudget.getId() +
							    //" WHERE pacc_requisitionid = " + pmConn.getInt("reqi_requisitionid") +							    
								" AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +										
								" ) ";
							System.out.println("NDC " + sql);
							pmConn2.doFetch(sql);
							if (pmConn2.next()){			
								System.out.println("NDC " + sql);
								paccNDC += pmConn2.getDouble("totalpayments");			
							}	
				
					//}*/
				%>		
					<td colspan="" align="right" class="documentText">&nbsp;
						<b><%= formatCurrency.format(paccNDC) %></b>
					</td>
				</TR>
				<TR class="reportCellEven">
					<td colspan="4" align="right" class="documentText">&nbsp;
						<b><%= formatCurrency.format(sumPayments + paccNDC) %></b>
					</td>
				</TR>
			</table>
		</td>
	</tr>	
	<TR>
		<TD colspan="3">
			&nbsp;
		</TD>
	</TR>
	<TR>
		<TD colspan="3">
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<TR>
					<td colspan="6" class="reportHeaderCell">
						<b>Tareas de la Etapa<b>
					</td>
				</TR>
				<tr class="">		           
				   <td class="reportHeaderCell">Tarea</td>	
		           <td class="reportHeaderCell">Usuario</td>
		           <td class="reportHeaderCell">Grupo</td>
		           <td class="reportHeaderCell">Inicio</td>
		           <td class="reportHeaderCell">Fin</td>
		           <td class="reportHeaderCell">Avance</td>		           
		       </tr>
				<%
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " wflowsteps ") +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (wfsp_wflowid = wflw_wflowid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON (wflw_wflowtypeid = wfty_wflowtypeid) " +
					  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowphases")+" ON (wfsp_wflowphaseid = wfph_wflowphaseid) " +		  	
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON (wfph_wflowcategoryid = wfca_wflowcategoryid) " +
					  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " profiles")+" ON (wfsp_profileid = prof_profileid) " +		 
					  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (user_userid = wflw_userid) " +		
					  "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" userStep ON (userStep.user_userid = wfsp_userid) " +		  		  
					  " WHERE wfsp_wflowid = " + bmoDevelopmentPhase.getWFlowId().toInteger() + 
					  " ORDER BY wflw_wflowid, wfph_sequence, wfsp_sequence, wfsp_progress";
				pmConn2.doFetch(sql);
	            while (pmConn2.next()) {
				%>
					<TR class="reportCellEven">
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn2.getString("wflowphases", "wfph_sequence") + "." + pmConn2.getString("wflowsteps", "wfsp_sequence") + " " + pmConn2.getString("wflowsteps", "wfsp_name"), BmFieldType.STRING)) %>							
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn2.getString("userStep", "user_code"),BmFieldType.STRING)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn2.getString("profiles", "prof_name"),BmFieldType.STRING)) %>
						<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("wflowsteps", "wfsp_startdate"),BmFieldType.DATETIME) %>
						<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("wflowsteps", "wfsp_enddate"),BmFieldType.DATETIME) %>
						<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("wflowsteps", "wfsp_progress"),BmFieldType.PERCENTAGE) %>
					</TR>
				<% } %>	
			
			</table>
		</TD>		
	</TR>
</table>


<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td>&nbsp</td>
	</tr>
</table>


<%
	pmConn2.close();
	pmConn.close();
%>
<% } %>
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% }
	}%>
  </body>
</html>