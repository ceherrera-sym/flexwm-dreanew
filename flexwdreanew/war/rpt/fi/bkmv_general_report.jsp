<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito. *
 * @version 2013-10
 */ -->
 
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.server.fi.PmBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoBankAccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%
	// Inicializar variables
 	String title = "Reporte General de Mov. Bancarios";	
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	String sql = "", where = "", whereBkac = "";
   	String inputDate = "", dueDate = "", inputEndDate = "", dueEndDate = "";
   	String bkmvStatus = "";
   	String bkmvType = "";
   	String bankMovTypeId = "";
   	String filters = "";
   	int supplierId = 0, customerId = 0, cols= 0, bankAccountId = 0  ;
   	String showTotal  = "N";
   	int programId = 0; 
    BmoBankMovement bmoBankMovement = new BmoBankMovement();
   	
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));  	
   	if (request.getParameter("bkmv_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("bkmv_supplierid"));
   	if (request.getParameter("bkmv_customerid") != null) customerId = Integer.parseInt(request.getParameter("bkmv_customerid"));
   	if (request.getParameter("bkmv_bankaccountid") != null) bankAccountId = Integer.parseInt(request.getParameter("bkmv_bankaccountid"));
   	if (request.getParameter("bkmv_bankmovtypeid") != null) bankMovTypeId = request.getParameter("bkmv_bankmovtypeid");
   	if (request.getParameter("bkmv_inputDate") != null) inputDate = request.getParameter("bkmv_inputDate");
   	if (request.getParameter("inputenddate") != null) inputEndDate = request.getParameter("inputenddate");
   	if (request.getParameter("bkmv_duedate") != null) dueDate = request.getParameter("bkmv_duedate");
   	if (request.getParameter("dueenddate") != null) dueEndDate = request.getParameter("dueenddate");	
   	if (request.getParameter("bkmv_status") != null) bkmvStatus = request.getParameter("bkmv_status");
   	
   	
   	//Filtros
   	
   	if (!bankMovTypeId.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("bkmv_bankmovtypeid", bankMovTypeId);
        filters += "<i>Tipo de Mov: </i>" + request.getParameter("bkmv_bankmovtypeidLabel") + ", ";
    }
   	
    if (!inputDate.equals("")) {
        where += " AND bkmv_inputdate >= '" + inputDate + "'";
        filters += "<i>Fecha Reg: </i>" + inputDate + ", ";
    }
    
    if (!inputEndDate.equals("")) {
        where += " AND bkmv_inputdate <= '" + inputEndDate + "'";
        filters += "<i>Fecha Reg Final: </i>" + inputEndDate + ", ";
    }
        
    if (!dueDate.equals("")) {
        where += " AND bkmv_duedate >= '" + dueDate + "'";
        filters += "<i>Pago Inicio: </i>" + dueDate + ", ";
    }
    
    if (!dueEndDate.equals("")) {
        where += " AND bkmv_duedate <= '" + dueEndDate + "'";
        filters += "<i>Pago Final: </i>" + dueEndDate + ", ";
    }
    
    if (customerId > 0) {
        where += " AND bkmv_customerid = " + customerId;
        filters += "<i>Cliente: </i>" + request.getParameter("bkmv_customeridLabel") + ", ";
    }
    
    if (supplierId > 0) {
        where += " AND bkmv_supplierid = " + supplierId;
        filters += "<i>Proveedor: </i>" + request.getParameter("bkmv_supplieridLabel") + ", ";
    }

    if (bankAccountId > 0) {
        where += " AND bkmv_bankaccountid = " + bankAccountId;
        whereBkac += " AND bkac_bankaccountid = " + bankAccountId;
        filters += "<i>Cta.Banco: </i>" + request.getParameter("bkmv_bankaccountidLabel") + ", ";
    }
    
    if (!bkmvStatus.equals("")) {
        //where += " and bkmv_status like '" + bkmvStatus + "'";
        where += SFServerUtil.parseFiltersToSql("bkmv_status", bkmvStatus);
        filters += "<i>Estatus: </i>" + request.getParameter("bkmv_statusLabel") + ", ";
    }
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";

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
<br>
<table class="report" border="0">
<%
	double initialBalance = 0;   
	int p = 0, i = 1;
	double totalW = 0;
	double totalD = 0;        
	double amount = 0;
	double withDrawTotal = 0;
	double depositTotal = 0;
	double saldoTotal = initialBalance;
	double amountW = 0;
	double amountD = 0;   
   double withDraws = 0;
   double deposits = 0;   
   String dateBalance = "";
   boolean notNow = true;
    
   PmConn pmConn = new PmConn(sFParams); 
   PmConn pmBankMovements = new PmConn(sFParams);
   PmConn pmBankMovAccounts = new PmConn(sFParams);
   pmConn.open();
   pmBankMovements.open();
   pmBankMovAccounts.open();
   
   PmConn pmConnBkmvTras = new PmConn(sFParams); 
   pmConnBkmvTras.open();   
   
    // Obtener disclosure de datos
    String disclosureFilters = new PmBankMovement(sFParams).getDisclosureFilters();   
    if (disclosureFilters.length() > 0){
   		where += " AND " + disclosureFilters;
   		disclosureFilters = " AND " + disclosureFilters;
    }
   
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
    
	   sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
		         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankmovtypes")+" on (bkmt_bankmovtypeid = bkmv_bankmovtypeid)" +
		         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankaccounts")+" bkac on (bkac.bkac_bankaccountid = bkmv_bankaccountid)" +    
		         //" LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankaccounts bkacTras on (bkacTras.bkac_bankaccountid = bkmv_bankaccotransid)" +  
		         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = bkmv_supplierid)" +
		         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" on (cust_customerid = bkmv_customerid)" +
		         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (user_userid = bkmv_salesmanId)" +    
		         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (bkac_companyid = comp_companyid) " +
		         " WHERE bkmv_bankmovementid > 0 " + where +
		         "  	 ";
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

	sql = " SELECT bkmv_bankaccountid, bkac_bankaccountid, bkac_name, bkac_accountno " +
   			" FROM " + SQLUtil.formatKind(sFParams, "bankmovements") +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts")+" on (bkac_bankaccountid = bkmv_bankaccountid)" +    
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes")+" on (bkmt_bankmovtypeid = bkmv_bankmovtypeid)" +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers")+" on (supl_supplierid = bkmv_supplierid)" +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers")+" on (cust_customerid = bkmv_customerid)" +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "users")+" on (user_userid = bkmv_salesmanId)" +    
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies")+" on (bkac_companyid = comp_companyid) " +
    		" WHERE bkmv_bankaccountid > 0 " +
    		whereBkac +
    		where +
    		" GROUP BY bkmv_bankaccountid " +
   			" ORDER BY bkac_bankaccountid ";
 
   pmBankMovAccounts.doFetch(sql);  
   while (pmBankMovAccounts.next()) {
	   %>
	   
		   <tr class="reportCellEven">
		   
		   		<td class="reportGroupCell" colspan="13">
		   			<b>Nombre Cta. Banco:</b> <%= HtmlUtil.stringToHtml(pmBankMovAccounts.getString("bankaccounts", "bkac_name")) %> |
		   			<b>No. Cta. Banco: </b> <%= pmBankMovAccounts.getString("bankaccounts", "bkac_accountno") %> |
		   		
		   			<%
		   			double sumWithDraw = 0;
		   			double sumDeposit = 0;
		   			double lastBalance = 0;
		   			//Obtener la fecha del primer MB
		   			String firstDueDate = "";
		   			sql = " SELECT bkmv_duedate FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
		   					" WHERE bkmv_bankaccountid = " + pmBankMovAccounts.getInt("bkac_bankaccountid") +
		   					" ORDER BY bkmv_duedate, bkmv_nocheck ";
		   			pmConn.doFetch(sql);
		   			if (pmConn.next()) firstDueDate = pmConn.getString("bkmv_duedate");      


		   			//Obtener el saldo anterior a la fecha
		   			sql = " SELECT SUM(bkmv_withdraw) as withdraw, SUM(bkmv_deposit) AS deposit FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
		   					" WHERE bkmv_bankaccountid = " + pmBankMovAccounts.getInt("bkac_bankaccountid");
		   			if (!inputDate.equals(""))
		   				sql += " AND bkmv_inputdate < '" + inputDate + "'";
		   			else if (!dueDate.equals(""))	   			      	   			
		   				sql += " AND bkmv_duedate < '" + dueDate + "'";
		   			else 
		   				sql += " AND bkmv_duedate < '" + firstDueDate + "'";
		   			pmConn.doFetch(sql);
		   			if (pmConn.next()) {
		   				sumWithDraw = pmConn.getDouble("withdraw");
		   				sumDeposit = pmConn.getDouble("deposit");
		   			}

		   			lastBalance = sumDeposit - sumWithDraw;
		   			%>
		   			Saldo anterior: <%= SFServerUtil.formatCurrency(lastBalance) %></B>
	   			</td>
		    </tr>
		    <tr>
	    		<td class="reportHeaderCellCenter">#</td>
		    	<td class="reportHeaderCell">Pago</td>
		    	<td class="reportHeaderCell">Cliente/Proveedor</td>
		    	<td class="reportHeaderCell">Clave</td>
		    	<td class="reportHeaderCell">Descripci&oacute;n</td>
		    	<td class="reportHeaderCell">Ref</td>
		    	<td class="reportHeaderCell">Cta.Banco Dest.</td>
<!-- 				<td class="reportHeaderCell">O.C.</td> -->
				<td class="reportHeaderCell">Tipo</td>
				<td class="reportHeaderCell">Estatus</td>
				<td class="reportHeaderCellRight">Cargos</td>
				<td class="reportHeaderCellRight">Abonos</td>
				<td class="reportHeaderCellRight">Saldo</td>
			</tr>
	   <%
	   sql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " bankmovements ") +
	         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankmovtypes")+" on (bkmt_bankmovtypeid = bkmv_bankmovtypeid)" +
	         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankaccounts")+" bkac on (bkac.bkac_bankaccountid = bkmv_bankaccountid)" +    
	         //" LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankaccounts bkacTras on (bkacTras.bkac_bankaccountid = bkmv_bankaccotransid)" +  
	         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (supl_supplierid = bkmv_supplierid)" +
	         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" on (cust_customerid = bkmv_customerid)" +
	         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (user_userid = bkmv_salesmanId)" +    
	         " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (bkac_companyid = comp_companyid) " +
	         " WHERE bkmv_bankmovementid > 0 " + where +
	         " AND bkmv_bankaccountid = " + pmBankMovAccounts.getInt("bkac_bankaccountid") +
	         " ORDER BY bkmv_duedate, bkmv_bankmovementid ";
	   
       pmBankMovements.doFetch(sql);
       int bankaccountid = 0;
       int x = 1;
	   while (pmBankMovements.next()) { %>	   
		   
			<TR class="reportCellEven">
				<%= HtmlUtil.formatReportCell(sFParams, "" + x, BmFieldType.NUMBER)%>

				<%= HtmlUtil.formatReportCell(sFParams, pmBankMovements.getString("bankmovements", "bkmv_duedate"), BmFieldType.DATE)%>
				<% if (pmBankMovements.getInt("bkmv_supplierid") > 0 ) { %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmBankMovements.getString("suppliers", "supl_code") + " " + pmBankMovements.getString("suppliers", "supl_name"), BmFieldType.STRING))%>
				<% } else if (pmBankMovements.getInt("bkmv_customerid") > 0 ) { 
					String customerName = pmBankMovements.getString("customers", "cust_code") + " " + 
				                          pmBankMovements.getString("customers", "cust_displayname"); %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, customerName, BmFieldType.STRING))%>
					
				<% } else { %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmBankMovements.getString("bkmv_description"), BmFieldType.STRING))%>
				<% } %>
				<%= HtmlUtil.formatReportCell(sFParams, pmBankMovements.getString("bankmovements", "bkmv_code"),BmFieldType.CODE)%>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmBankMovements.getString("bankmovements", "bkmv_description"), BmFieldType.STRING))%>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmBankMovements.getString("bankmovements", "bkmv_bankreference"), BmFieldType.STRING))%>
				<%
					String nameBkac = "";
					String sqlTras = "SELECT bkac_name FROM " + SQLUtil.formatKind(sFParams, " bankaccounts")+" WHERE bkac_bankaccountid = " + pmBankMovements.getInt("bankmovements", "bkmv_bankaccotransid");
					pmConnBkmvTras.doFetch(sqlTras);
					if(pmConnBkmvTras.next()) nameBkac = pmConnBkmvTras.getString("bkac_name");
					
				%>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, nameBkac, BmFieldType.CODE))%>			

				<%// if (pmBankMovements.getInt("bkmv_requisitionid") > 0) { %>
					<%//= HtmlUtil.formatReportCell(sFParams, "OC-" + pmBankMovements.getString("bankmovements", "bkmv_requisitionid"),BmFieldType.STRING)%>
				<%// } else { %>
					<%//=HtmlUtil.formatReportCell(sFParams, pmBankMovements.getString("bankmovements", "bkmv_requisitionid"),BmFieldType.STRING)%>
				<%// } %>
				<%= HtmlUtil.formatReportCell(sFParams, pmBankMovements.getString("bankmovtypes", "bkmt_name"),BmFieldType.CODE)%>
				
				<%
				bmoBankMovement.getStatus().setValue(pmBankMovements.getString("bankmovements", "bkmv_status"));

				%>
            	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoBankMovement.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
            	<%
				amountW = pmBankMovements.getDouble("bkmv_withdraw");
				amountD = pmBankMovements.getDouble("bkmv_deposit");				
				%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + amountW, BmFieldType.CURRENCY)%>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + amountD,BmFieldType.CURRENCY)%>
			    <%				
				    totalW += amountW;
					totalD += amountD;
											
					if (pmBankMovements.getString("bankmovtypes", "bkmt_type").equals("" + BmoBankMovType.TYPE_WITHDRAW)) {
						saldoTotal = lastBalance - amountW;
					} else {	
						saldoTotal = lastBalance + amountD;
					}
					
					lastBalance = saldoTotal;
					
					amountW = 0;
					amountD = 0;
					
						
					x++; 
				%>				
				<%=HtmlUtil.formatReportCell(sFParams, "" + saldoTotal,	BmFieldType.CURRENCY)%>
			</TR>			 
<%	  
		i++;
	   }	
%>
		<tr class="reportCellCode reportCellEven">   
			<td colspan="9"></td>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalW, BmFieldType.CURRENCY)%>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalD, BmFieldType.CURRENCY)%>
			<%= HtmlUtil.formatReportCell(sFParams, "" + lastBalance, BmFieldType.CURRENCY)%>		
		</tr>
<%

	withDrawTotal += totalW;
	depositTotal += totalD;

	totalW = 0;
	totalD = 0;
	saldoTotal = 0;
   }
 %>
		 
 </table>
  <%
  	
	}// FIN DEL CONTADOR
	pmConn.close();
  	pmBankMovAccounts.close();
    pmBankMovements.close(); 
    pmConnBkmvTras.close(); 
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
