<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito. *
 * @version 2013-10
 */ -->

<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.flexwm.server.fi.PmBankAccount"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.server.fi.PmBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoBankAccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@include file="/inc/login.jsp"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil"%>
<%
	// Inicializar variables
 	String title = "Reporte General de Cuentas de Banco";	
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	String sql = "", where = "", filters = "", bankAccountId = "";

   	int programId = 0; 
    BmoBankAccount bmoBankAccount = new BmoBankAccount();
    PmBankAccount pmBankAccount = new PmBankAccount(sFParams);
    
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));  	
   	if (request.getParameter("bkac_bankaccountid") != null) bankAccountId = request.getParameter("bkac_bankaccountid");
   	
   	//Filtros
    if (!bankAccountId.equals("")) {
        where += SFServerUtil.parseFiltersToSql("bkac_bankaccountid", bankAccountId);
        filters += "<i>Cta. Banco: </i>" + request.getParameter("bkac_bankaccountidLabel") + ", ";
    }
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";

    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	// Conexiones BD
	PmConn pmBankAccounts = new PmConn(sFParams);
	pmBankAccounts.open();
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
body {
	user-select: none;
	-moz-user-select: none;
	-o-user-select: none;
	-webkit-user-select: none;
	-ie-user-select: none;
	-khtml-user-select: none;
	-ms-user-select: none;
	-webkit-touch-callout: none
}
</style>
<style type="text/css" media="print">
* {
	display: none;
}
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
	<title>:::<%= title %>:::
	</title>
	<link rel="stylesheet" type="text/css"	href="<%= GwtUtil.getProperUrl(sFParams, "/css/" + defaultCss)%>">
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

	<table border="0" cellspacing="0" cellpading="0" width="100%">
		<tr>
			<td align="left" width="80" rowspan="2" valign="top"><img
				border="0" width="<%= SFParams.LOGO_WIDTH %>"
				height="<%= SFParams.LOGO_HEIGHT %>"
				src="<%= sFParams.getMainImageUrl() %>"></td>
			<td class="reportTitle" align="left" colspan="2"><%= title %></td>
		</tr>
		<tr>
			<td class="reportSubTitle"><b>Filtros:</b> <%= filters %> <br>
			</td>
			<td class="reportDate" align="right">Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %>
				por: <%= sFParams.getLoginInfo().getEmailAddress() %>
			</td>
		</tr>
	</table>
	<br>
	<table class="report" border="0">
		<tr class="reportCellEven">
			<td class="reportHeaderCellCenter">#</td>
			<td class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoBankAccount.getName() ))%>
			</td>
			<td class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoBankAccount.getCompanyId() ))%>
			</td>
			<td class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoBankAccount.getBankName() ))%>
			</td>
			<td class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoBankAccount.getAccountNo() ))%>
			</td>
			<td class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoBankAccount.getBmoBankAccountType().getName() ))%>
			</td>
			<td class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoBankAccount.getCutDate() ))%>
			</td>
			<td class="reportHeaderCell">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoBankAccount.getCurrencyId()))%>
			</td>
			<td class="reportHeaderCellRight">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoBankAccount.getBalance() ))%>
			</td>
		</tr>
		<%	
			// Obtener disclosure de datos
			String disclosureFilters = new PmBankAccount(sFParams).getDisclosureFilters();
			if (disclosureFilters.length() > 0) {
				where += " AND " + disclosureFilters;
				disclosureFilters = " AND " + disclosureFilters;
			}

			// abro conexion para inciar el conteo consulta general
			PmConn pmConnCount = new PmConn(sFParams);
			pmConnCount.open();

			sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, "bankaccounts")
					+ " LEFT JOIN companies ON (comp_companyid = bkac_companyid) "
				   	+ " LEFT JOIN currencies ON (cure_currencyid = bkac_currencyid) "
					+ " WHERE bkac_bankaccountid > 0 " 
				   	+ where 
				   	+ " ORDER BY bkac_bankaccountid ";
			int count = 0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if (pmConnCount.next())
				count = pmConnCount.getInt("contador");
			System.out.println("CONTADOR DE REGISTROS --> " + count);
			// if que muestra el mensaje de error
			if (count > sFParams.getBmoSFConfig().getMaxRecords().toInteger()) {
		%>
		<%=messageTooLargeList %>
		<% 
	} else {

    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "bankaccounts") 
   			 + " LEFT JOIN bankaccounttype ON (bact_bankaccounttypeid = bkac_bankaccounttypeid) "
    		+ " LEFT JOIN companies ON (comp_companyid = bkac_companyid) "
    	    + " LEFT JOIN currencies ON (cure_currencyid = bkac_currencyid) "
    		+ " WHERE bkac_bankaccountid > 0 " 
    		+ where 
    		+ " ORDER BY bkac_bankaccountid "; 
 
    		//System.out.println("sql:: "+ sql);
    		pmBankAccounts.doFetch(sql); 
   	int x = 1;
  	while (pmBankAccounts.next()) {
   		%>
		<tr class="reportCellEven">
			<%= HtmlUtil.formatReportCell(sFParams, "" + x, BmFieldType.NUMBER) %>
			<%	if (sFParams.isFieldEnabled(bmoBankAccount.getName())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmBankAccounts.getString("bankaccounts", "bkac_name"), BmFieldType.STRING) %>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoBankAccount.getCompanyId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmBankAccounts.getString("companies", "comp_name"), BmFieldType.STRING) %>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoBankAccount.getBankName())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmBankAccounts.getString("bankaccounts", "bkac_bankname"), BmFieldType.STRING) %>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoBankAccount.getAccountNo())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmBankAccounts.getString("bankaccounts", "bkac_accountno"), BmFieldType.STRING) %>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoBankAccount.getBankAccountTypeId() )) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmBankAccounts.getString("bankaccounttype", "bact_name"), BmFieldType.STRING) %>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoBankAccount.getCutDate())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmBankAccounts.getString("bankaccounts", "bkac_cutdate"), BmFieldType.STRING) %>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoBankAccount.getCurrencyId())) { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmBankAccounts.getString("currencies", "cure_code"), BmFieldType.STRING) %>
			<%	} %>
			<%	if (sFParams.isFieldEnabled(bmoBankAccount.getBalance())) { 
			%>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmBankAccount.getBalanceBankAccount(pmBankAccounts.getInt("bkac_bankaccountid")), BmFieldType.CURRENCY) %>
			<%	} %>
<% 		x++;
	}
 %>
		
	</table>
	<%
  	
	}// FIN DEL CONTADOR
	pmBankAccounts.close();
    pmConnCount.close();
	}// Fin de if(no carga datos)	
  %>

	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
					+ " Reporte: "+title
					+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); %>
</body>
</html>
