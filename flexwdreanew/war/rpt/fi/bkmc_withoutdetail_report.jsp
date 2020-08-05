<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito. *
 * @version 2013-10
 */ -->
 
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.server.fi.PmBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoBankAccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
 	String title = "Reporte Movimientos de Banco Sin Detalle Aplicado";	
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	String sql = "", where = "", whereBkac = "";
   	String inputDate = "", dueDate = "", inputEndDate = "", dueEndDate = "";
   	String bkmvStatus = "", bankMovTypeId = "", whereBudget = "", whereBudgetItem = "";
   	String filters = "", showTotal  = "N";
   	int programId = 0, supplierId = 0, customerId = 0, cols= 0, bankAccountId = 0, budgetId = 0, budgetItemId = 0, activeBudgets = 0;
   	boolean enableBudgets = false;
	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
   		activeBudgets = 2;
   	}

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
   	if (request.getParameter("budgetId") != null) budgetId = Integer.parseInt(request.getParameter("budgetId"));
	if (request.getParameter("budgetItemId") != null) budgetItemId = Integer.parseInt(request.getParameter("budgetItemId"));
   	
   	//Filtros
   	if (!bankMovTypeId.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("bkmv_bankmovtypeid", bankMovTypeId);
        filters += "<i>Tipo de Mov.: </i>" + request.getParameter("bkmv_bankmovtypeidLabel") + ", ";
    }
   	
    if (!inputDate.equals("")) {
        where += " AND bkmv_inputdate >= '" + inputDate + "'";
        filters += "<i>Fecha: </i>" + inputDate + ", ";
    }
    
    if (!inputEndDate.equals("")) {
        where += " AND bkmv_inputdate <= '" + inputEndDate + "'";
        filters += "<i>Fecha Final: </i>" + inputEndDate + ", ";
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
        whereBkac += " WHERE bkac_bankaccountid = " + bankAccountId;
        filters += "<i>Cta.Banco: </i>" + request.getParameter("bkmv_bankaccountidLabel") + ", ";
    }
    
    if (enableBudgets) {
	    if (budgetId > 0) {
	    	whereBudget += " AND bgit_budgetid = " + budgetId;
	        filters += "<i>Presupuesto: </i>" + request.getParameter("budgetIdLabel") + ", ";
	    }  

	    if (budgetItemId > 0) {
	    	whereBudgetItem += " AND bgit_budgetitemid = " + budgetItemId;
	        filters += "<i>Partida Presup.: </i>" + request.getParameter("budgetItemIdLabel") + ", ";
	    }
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
	
	PmConn pmConnBkmvTras = new PmConn(sFParams); 
	pmConnBkmvTras.open(); 
	
	PmConn pmConn = new PmConn(sFParams);      
   	pmConn.open();
   	BmoBankMovement bmoBankMovement = new BmoBankMovement();
	//abro conexion para inciar el conteo consulta general
	PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
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
   
   // Obtener disclosure de datos
   String disclosureFilters = new PmBankMovement(sFParams).getDisclosureFilters();   
   if (disclosureFilters.length() > 0)
	   where += " AND " + disclosureFilters;
   
   %>
     <tr>
		<td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Clave</td>
		<td class="reportHeaderCell">Nombre Cta.</td>
		<td class="reportHeaderCell">Descripci&oacute;n</td>
		<td class="reportHeaderCell">Ref</td>
		<td class="reportHeaderCell">Cta.Banco Dest.</td>
		<td class="reportHeaderCell">Pago</td>
		<td class="reportHeaderCell">Cliente/Proveedor</td>				
		<td class="reportHeaderCell">O.C.</td>
		<td class="reportHeaderCell">CxP/CxC</td>
		<td class="reportHeaderCell">Pedido</td>
		<td class="reportHeaderCell">Tipo</td> 
<%		if (enableBudgets) { %>			
			<td class="reportHeaderCellRight">Presupuesto</td>
			<td class="reportHeaderCellRight">Partida</td>
<%		} %>		
		<td class="reportHeaderCell">Estatus</td>
		<td class="reportHeaderCellRight">Cargos</td>
		<td class="reportHeaderCellRight">Abonos</td>
		<td class="reportHeaderCellRight">Saldo</td>		
	</tr>
<%   
	int count = 0;
		sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, "bankmovements") 
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts") + " ON (bkac_bankaccountid = bkmv_bankaccountid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes") + " ON (bkmt_bankmovtypeid = bkmv_bankmovtypeid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies") + " ON (bkac_companyid = comp_companyid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitions") + " ON (reqi_requisitionid = bkmv_requisitionid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers") + " ON (supl_supplierid = bkmv_supplierid)" 
        + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers") + " ON (cust_customerid = bkmv_customerid)" 
        + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders") + " ordeReqi ON (ordeReqi.orde_orderid = reqi_orderid) ";
		if (enableBudgets) {
			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (reqi_budgetitemid = bgit_budgetitemid) " +
 	     	 	" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
 	     	 	" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets") + " ON (bgit_budgetid = budg_budgetid) ";
		}
		sql += " WHERE bkmv_bankmovementid NOT IN (SELECT bkmc_bankmovementid FROM bankmovconcepts WHERE bkmc_bankmovconceptid > 0) "

// 		sql += " WHERE bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "'"
// 		+ " AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'"
		+ where;
		if (enableBudgets) {
			sql += whereBudget 
  	     	 	+ whereBudgetItem;
		}

	   	sql += " ORDER BY bkmv_bankmovementid ";
		// ejecuto el sql DEL CONTADOR 2 sql
		pmConnCount.doFetch(sql);
		if(pmConnCount.next())
			count += pmConnCount.getInt("contador");
		
		System.out.println("contador DE REGISTROS --> "+count);

		// if que muestra el mensaje de error
		if (count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()) { %>
				<%=messageTooLargeList %>
			<% 
		} else {
	 
			int colspan = 0;
			if (enableBudgets) 
				colspan = 18;
			else colspan= 16;

		
	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "bankmovements") 
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts") + " ON (bkac_bankaccountid = bkmv_bankaccountid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes") + " ON (bkmt_bankmovtypeid = bkmv_bankmovtypeid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies") + " ON (bkac_companyid = comp_companyid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitions") + " ON (reqi_requisitionid = bkmv_requisitionid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "suppliers") + " ON (supl_supplierid = bkmv_supplierid)" 
        + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "customers") + " ON (cust_customerid = bkmv_customerid)" 
        + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "orders") + " ordeReqi ON (ordeReqi.orde_orderid = reqi_orderid) ";
		if (enableBudgets) {
			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (reqi_budgetitemid = bgit_budgetitemid) " +
 	     	 	" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
 	     	 	" LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets") + " ON (bgit_budgetid = budg_budgetid) ";
		}
		sql += " WHERE bkmv_bankmovementid NOT IN (SELECT bkmc_bankmovementid FROM bankmovconcepts WHERE bkmc_bankmovconceptid > 0) "
				//" WHERE bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "'"
		//+ " AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'"
		+ where;
		if (enableBudgets) {
			sql += whereBudget 
  	     	 	+ whereBudgetItem;
		}

	   	sql += " ORDER BY bkmv_bankmovementid ";
	   	//System.out.println("sql:: "+sql);
       	pmConn.doFetch(sql);       
	   	while (pmConn.next()) { %>   
		   
			<TR class="reportCellEven">
				<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.STRING)%>
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("bankmovements", "bkmv_code"), BmFieldType.CODE)%>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("bankaccounts", "bkac_name"), BmFieldType.STRING))%>				
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("bankmovements", "bkmv_description"), BmFieldType.STRING))%>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("bankmovements", "bkmv_bankreference"), BmFieldType.STRING))%>
				<%
					String nameBkac = "";
					String sqlTras = "SELECT bkac_name FROM " + SQLUtil.formatKind(sFParams, " bankaccounts")+" WHERE bkac_bankaccountid = " + pmConn.getInt("bankmovements", "bkmv_bankaccotransid");
					pmConnBkmvTras.doFetch(sqlTras);
					if(pmConnBkmvTras.next()) nameBkac = pmConnBkmvTras.getString("bkac_name");
				
				%>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, nameBkac, BmFieldType.CODE))%>			

				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("bankmovements", "bkmv_duedate"), BmFieldType.DATE)%>
				<% if (pmConn.getInt("bkmv_supplierid") > 0 ) { 
					String supplierName = "";
					if (pmConn.getString("suppliers", "supl_legalname").equals("") )
						supplierName = pmConn.getString("suppliers", "supl_code")  + " " + pmConn.getString("suppliers", "supl_name");
					else 
						supplierName = pmConn.getString("suppliers", "supl_code")  + " " + pmConn.getString("suppliers", "supl_legalname");
					%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + supplierName, BmFieldType.STRING))%>
				<% } else if (pmConn.getInt("bkmv_customerid") > 0 ) { 
					// Cliente
					String customerName = "";
		        	if (pmConn.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
		        		customerName = pmConn.getString("cust_code") + " " + pmConn.getString("cust_legalname");	
	      			else
	      				customerName = pmConn.getString("cust_code") + " " + pmConn.getString("cust_displayname");
				%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, customerName, BmFieldType.STRING))%>
					
				<% } else { %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("bkmv_description"), BmFieldType.STRING))%>
				<% } %>				
				<% if (pmConn.getInt("bkmv_requisitionid") > 0) { %>
					<%= HtmlUtil.formatReportCell(sFParams, "OC-" + pmConn.getString("bankmovements", "bkmv_requisitionid"),BmFieldType.STRING)%>
				<% } else { %>
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("bankmovements", "bkmv_requisitionid"),BmFieldType.STRING)%>
				<% } %>

				<%= HtmlUtil.formatReportCell(sFParams, "-",BmFieldType.STRING)%>

				<% 
				String ordeCode = "";
				ordeCode = pmConn.getString("ordeReqi.orde_code") + " " + pmConn.getString("ordeReqi.orde_name");
				%>				
				<%= HtmlUtil.formatReportCell(sFParams, "" + ordeCode, BmFieldType.STRING)%>
				
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("bankmovtypes", "bkmt_name"),BmFieldType.STRING)%>
				
				<% if (enableBudgets) { %> 
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("budg_name"), BmFieldType.STRING)%>				
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("bgty_name"), BmFieldType.STRING)%>
				<% } %>	
				
				<%
				bmoBankMovement.getStatus().setValue(pmConn.getString("bankmovements", "bkmv_status"));
                %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoBankMovement.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>

				<%
				if (pmConn.getString("bankmovtypes","bkmt_type").equals("W")) {
					amountD = 0;
					amountW = pmConn.getDouble("bkmv_withdraw");
				} else {
					amountW = 0;	
					amountD = pmConn.getDouble("bkmv_deposit");
				}	
				%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + amountW, BmFieldType.CURRENCY)%>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + amountD,BmFieldType.CURRENCY)%>			    
			    <%				
				    totalW += amountW;
					totalD += amountD;
					
					saldoTotal = totalD - totalW;					
					amountW = 0;
					amountD = 0;
				%>				
				<%= HtmlUtil.formatReportCell(sFParams, "" + saldoTotal,	BmFieldType.CURRENCY)%>				
			</TR>			 
<%	  
		i++;
	   }	

		if (totalW > 0 || totalD > 0 || saldoTotal > 0) {
			%> 
				<tr><td colspan="<%= colspan%>">&nbsp;</td></tr>
				<tr class="reportCellCode reportCellEven">   
						<td colspan="<%= colspan -3 %>"></td>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalW, BmFieldType.CURRENCY)%>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalD, BmFieldType.CURRENCY)%>
					<%= HtmlUtil.formatReportCell(sFParams, "" + saldoTotal, BmFieldType.CURRENCY)%>
			
				</tr>
		<% }%>
 </table>	
  <%
		} // FIN DEL CONTADOR
	} // Fin de if(no carga datos)
	pmConn.close(); 
	pmConnBkmvTras.close();
	pmConnCount.close();
  %>  
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% 
	
	}System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress());  %>
  </body>
</html>