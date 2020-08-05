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
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.server.fi.PmPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables // firp_paymentschedule.jsp
 	String title = "Reporte de Antiguedad de Saldos de CxP";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	
   	String sql = "", sqlSupl = "", sqlCurrency = "", where = "", whereSupl = "", whereDueDate = "", whereDueDateMinor = "";
   	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "";
   	String status = "", paymentStatus = "", paymentStatus2 = "";
   	String filters = "", filtersDefault = "", suplCategoryId = "";
   	int programId = 0, supplierId = 0, requisitionId = 0, cols= 0, paccountTypeId = 0, companyId = 0, currencyId = 0, budgetId = 0, budgetItemId = 0, activeBudgets = 0;
   	boolean t = false, enableBudgets = false;
   	// se agrega 2 columnas para presupuestos, para manejo de colspans
   	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
   		activeBudgets = 2;
   	}
   	
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("pacc_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("pacc_supplierid"));
   	if (request.getParameter("supl_suppliercategoryid") != null) suplCategoryId = request.getParameter("supl_suppliercategoryid");
   	if (request.getParameter("pacc_companyid") != null) companyId = Integer.parseInt(request.getParameter("pacc_companyid"));
   	if (request.getParameter("pacc_paccounttypeid") != null) paccountTypeId = Integer.parseInt(request.getParameter("pacc_paccounttypeid"));
   	if (request.getParameter("pacc_status") != null) status = request.getParameter("pacc_status");
   	if (request.getParameter("pacc_paymentstatus") != null) paymentStatus = request.getParameter("pacc_paymentstatus");
   	//if (request.getParameter("pacc_receivedate") != null) receiveDate = request.getParameter("pacc_receivedate");
   	//if (request.getParameter("receiveenddate") != null) receiveEndDate = request.getParameter("receiveenddate");
   	if (request.getParameter("pacc_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("pacc_currencyid"));
   	//if (request.getParameter("dueenddate") != null) dueEndDate = request.getParameter("dueenddate");
//    	if (request.getParameter("pacc_duedate") != null) {
//    		if(request.getParameter("pacc_duedate").equals("")) {
//    			dueDate = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
//    			t = true;
//    		}else 
//    			dueDate = request.getParameter("pacc_duedate");
//    	} else {
   		dueDate = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
//    		t = true;
//    	}
   	if (enableBudgets){
	    if (request.getParameter("bgit_budgetid") != null) budgetId = Integer.parseInt(request.getParameter("bgit_budgetid"));
	    if (request.getParameter("pacc_budgetitemid") != null) budgetItemId = Integer.parseInt(request.getParameter("pacc_budgetitemid"));
    }
   	
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
    BmoPaccount bmoPaccount = new BmoPaccount();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
    
	// Filtros listados
	if (supplierId > 0) {
		whereSupl += " AND pacc_supplierid = " + supplierId;
		filters += "<i>Proveedor: </i>" + request.getParameter("pacc_supplieridLabel") + ", ";
	}
	
	if (!suplCategoryId.equals("")) {
        //where += " AND supl_suppliercategoryid = " + suplCategoryId;
        where += SFServerUtil.parseFiltersToSql("supl_suppliercategoryid", suplCategoryId);        
        filters += "<i>Categoria: </i>" + request.getParameter("supl_suppliercategoryidLabel") + ", ";
	}
	
	if (companyId > 0) {
        where += " AND pacc_companyid = " + companyId;
        filters += "<i>Empresa: </i>" + request.getParameter("pacc_companyidLabel") + ", ";
    }
	
	if (paccountTypeId > 0) {
        where += " AND pacc_paccounttypeid = " + paccountTypeId;
        filters += "<i>Tipo: </i>" + request.getParameter("pacc_paccounttypeidLabel") + ", ";
    }
	
	// Tipo de cxp de tipo cargo
    where += " AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "' ";
    filtersDefault += "<i>Tipo CxP: </i> Cargo, ";

    
    if (!paymentStatus.equals("")) {
	    where += SFServerUtil.parseFiltersToSql("pacc_paymentstatus", paymentStatus);
	    filters += "<i>Estatus Pago: </i>" + request.getParameter("pacc_paymentstatusLabel") + ", ";
    }
    
	if (!status.equals("")) {
	    where += SFServerUtil.parseFiltersToSql("pacc_status", status);
	    filters += "<i>Estatus: </i>" + request.getParameter("pacc_statusLabel") + ", ";
	}
	
	
	/*
   	if (!receiveDate.equals("")) {
   		where += " AND pacc_receivedate >= '" + receiveDate + "'";
   		filters += "<i>Recepci&oacute;n Inicio: </i>" + receiveDate + ", ";
   	}
   	
   	if (!receiveEndDate.equals("")) {
        where += " AND pacc_receivedate <= '" + receiveEndDate + "'";
        filters += "<i>Recepci&oacute;n Final: </i>" + receiveEndDate + ", ";
    }
   	*/
   	
//     if (!dueDate.equals("")) {
// //    	whereDueDate += " AND pacc_duedate >= '" + dueDate + "'";
// //    	whereDueDateMinor += " AND pacc_duedate < '" + dueDate + "'";
//     	if(t) filtersDefault += "<i>F. Programaci&oacute;n Inicio: </i>" + dueDate + ", ";
//     	else filters += "<i>F. Programaci&oacute;n Inicio: </i>" + dueDate + ", ";
//     }
    
    if (enableBudgets) {
	    if (budgetId > 0) {
	        where += " AND bgit_budgetid = " + budgetId;
	        filters += "<i>Presup.: </i>" + request.getParameter("bgit_budgetidLabel") + ", ";
	    }  
	
	    if (budgetItemId > 0) {
	        where += " AND pacc_budgetitemid = " + budgetItemId;
	        filters += "<i>Item Presup.: </i>" + request.getParameter("pacc_budgetitemidLabel") + ", ";
	    }
    }
    
    //Obtener la paridad de la moneda
    double defaultParity = 0;
   	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
   	if (currencyId > 0) {
   		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);   		
   		defaultParity = bmoCurrency.getParity().toDouble();   		
   		filters += "<i>Moneda: </i>" + request.getParameter("pacc_currencyidLabel") + " | <i>Paridad Actual : </i>" + defaultParity;
   	} else {
   		//Obtener la paridad de la moneda del sistema
   		//bmoCurrency = (BmoCurrency)pmCurrency.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getCurrencyId().toInteger());
   		//defaultParity = bmoCurrency.getParity().toDouble();
   		filters += "<i>Moneda: </i> Todas";
   		sqlCurrency = "SELECT cure_currencyid, cure_name FROM " + SQLUtil.formatKind(sFParams, " currencies ");
   	}
   	
   	/*
   	if (!dueEndDate.equals("")) {
        where += " AND pacc_duedate <= '" + dueEndDate + "'";
        filters += "<i>Programaci&oacute;n Final: </i>" + dueEndDate + ", ";
    }
    */
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmPaccount(sFParams).getDisclosureFilters();   
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
   	   	 
   	
   	PmConn pmConn = new PmConn(sFParams);
   	pmConn.open();
   	
   	PmConn pmSuppliers = new PmConn(sFParams);
   	pmSuppliers.open();
   	
   	PmConn pmPaccounts = new PmConn(sFParams);
   	pmPaccounts.open();
   	
   	PmConn pmCurrencyWhile =new PmConn(sFParams);
    pmCurrencyWhile.open();

   	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
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
<p>
<table class="report" border="0">	
    <%

    sql= " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " suppliers ") +
   		   " WHERE supl_supplierid in ( " +
   		   " SELECT pacc_supplierid FROM " + SQLUtil.formatKind(sFParams, " paccounts  ") +
   		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
   		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
   		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
   		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
 if (enableBudgets) {
sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
}
sql += " WHERE pacc_paccountid > 0 AND pacc_balance > 0 " +										     
	whereSupl + 
	where +
	")  ";
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
    if (!(currencyId > 0)) {
		
        int currencyIdWhile = 0, y = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			
			double 	DV030 = 0, DV3060 = 0, DV60 = 0, total = 0, 
	        		DPV030 = 0, DPV3060 = 0, DPV6090 = 0, DPV90 = 0,
	        		sumDV030 = 0, sumDV3060 = 0, sumDV60 = 0, sumTotal = 0, 
	        		sumDPV030 = 0, sumDPV3060 = 0, sumDPV6090 = 0, sumDPV90 = 0;
	        int i = 0;
	        sqlSupl = " SELECT supl_supplierid, supl_code, supl_name FROM " + SQLUtil.formatKind(sFParams, " suppliers ") +
	          		   " WHERE supl_supplierid in ( " +
	          		   " SELECT pacc_supplierid FROM " + SQLUtil.formatKind(sFParams, " paccounts  ") +
	          		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	          		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
	          		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
	          		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
	        if (enableBudgets) {
	        	sqlSupl += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
	        }
	        sqlSupl += " WHERE pacc_paccountid > 0 AND pacc_balance > 0 " +
	        		" AND pacc_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
	        		whereSupl + 
	        		where +
	        		") ORDER BY supl_supplierid ASC ";

	        
	        pmSuppliers.doFetch(sqlSupl);
	        while (pmSuppliers.next()) {
	        	
	        	if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
	        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
	        		y = 0;
	        		%>
	        		<tr>
	            		<td class="reportHeaderCellCenter" colspan="13">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
	        		</tr>
	        		<tr>                    
		        		<td class="reportHeaderCellCenter" rowspan="2">#</td>
		        		<td class="reportHeaderCell" rowspan="2">Proveedor</td>
		        		<td rowspan="2">&nbsp;</td>
		        		<td class="reportHeaderCellCenter" colspan="3">C x P vencidas</td>
		        		<td rowspan="2">&nbsp;</td>
		        		<td class="reportHeaderCellCenter" colspan="4">Fecha Programada de Pago</td>
		        		<td rowspan="2">&nbsp;</td>
		        		<td class="reportHeaderCellRight" rowspan="2" >Total&nbsp;</td>                                                                           
		        	</tr>
		        	<tr>                    
		        		<td class="reportHeaderCellRight">+60 D&iacute;as<br>Vencido</td>
		        		<td class="reportHeaderCellRight">30-60 D&iacute;as<br>Vencido</td>                                                                           
		        		<td class="reportHeaderCellRight">0-30 D&iacute;as<br>Vencido</td>                                                                           
		        		<td class="reportHeaderCellRight">0-30 D&iacute;as Para<br>Vencimiento</td>                                                                           
		        		<td class="reportHeaderCellRight">30-60 D&iacute;as Para<br>Vencimiento</td>                                                                           
		        		<td class="reportHeaderCellRight">60-90 D&iacute;as Para<br>Vencimiento</td>                                                                           
		        		<td class="reportHeaderCellRight">+90 D&iacute;as Para<br>Vencimiento</td>                                                                           
	
		        	</tr>
	        		<%
	        	}
	        	
	        	total = 0;
	        	//Acumulados
	            double  DV030T = 0, DV3060T = 0, DV60T = 0;
	            double DPV030T = 0, DPV3060T = 0, DPV6090T = 0, DPV90T = 0;
	        	double parityOrigin = 0, parityDestiny = 0;
	        	parityDestiny = defaultParity;
	        	
	        	// +60 Dias Vencido
	        	sql = " SELECT SUM(pacc_balance) AS DV60 " +
	        			" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
	        	if (enableBudgets) {
	        		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
	        	}
	        	sql += " WHERE pacc_balance > 0 " +
	        			" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
	            		" AND pacc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	        			where + 
	        			" AND pacc_duedate < DATE_ADD('" + dueDate + "', INTERVAL -60 DAY) " +
	        			" ORDER BY pacc_duedate ASC";  
	        	
	        	pmPaccounts.doFetch(sql);
	    		if (pmPaccounts.next()) {
	    			DV60 = pmPaccounts.getDouble("DV60");
	    		}
	    		
	    		sumDV60 += DV60;
	    		DV60T += DV60;
	    		total += DV60;
	    		
	    		// 30-60 Dias Vencido
	    		sql = " SELECT SUM(pacc_balance) AS DV3060 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
	    		if (enableBudgets) {
	    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
	    		}
	    		sql += " WHERE pacc_balance > 0 " + 
	    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
	            		" AND pacc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	    				where + 
	    				" AND pacc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -60 DAY) " +
	    				" AND pacc_duedate < DATE_ADD('" + dueDate + "', INTERVAL -30 DAY) " +
	    				" ORDER BY pacc_duedate ASC";        		
	    		pmPaccounts.doFetch(sql);
	    		if (pmPaccounts.next()) { 
	    			DV3060 = pmPaccounts.getDouble("DV3060");
	    		}
	    		
	    		sumDV3060 += DV3060;
	    		DV3060T += DV3060;
	    		total += DV3060;
	    		
	    		// 0-30 Dias Vencido
	    		sql = " SELECT SUM(pacc_balance) AS DV030 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
	    		if (enableBudgets) {
	    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;        				
	    		}
	    		sql += " WHERE pacc_balance > 0 " + 
	    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
	            		" AND pacc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	    				where + 
	    				" AND pacc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -30 DAY) " + 
	    				" AND pacc_duedate < DATE_ADD('" + dueDate + "', INTERVAL 0 DAY) " +
	    				//whereDueDateMinor +
	    				" ORDER BY pacc_duedate ASC";
	    		pmPaccounts.doFetch(sql);
	    		if (pmPaccounts.next()) {
	    			DV030 = pmPaccounts.getDouble("DV030");
	    		}
	    		
	    		sumDV030 += DV030;
	    		DV030T += DV030;
	    		total += DV030;
	    		
	    		// 0-30 Dias Para Vencimiento
	    		sql = " SELECT SUM(pacc_balance) AS DPV030 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
	    		if (enableBudgets) {
	    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
	    		}
	    		sql += " WHERE pacc_balance > 0 " + 
	    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
	    				" AND pacc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	    				where + 
	    				//whereDueDate +
	    				" AND pacc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 0 DAY) " +
	    				" AND pacc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 30 DAY) " + 
	    				" ORDER BY pacc_duedate ASC";
	    		pmPaccounts.doFetch(sql);
	    		if (pmPaccounts.next()) { 
	    			DPV030 = pmPaccounts.getDouble("DPV030");
	    		}
	    		
	    		sumDPV030 += DPV030;
	    		DPV030T += DPV030;
	    		total += DPV030;

	    		// 30-60 Dias Para Vencimiento
	    		sql = " SELECT SUM(pacc_balance) AS DPV3060 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
	    		if (enableBudgets) {
	    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
	    		}
	    		sql += " WHERE pacc_balance > 0 " + 
	    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
	    				" AND pacc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	    				where + 
	    				" AND pacc_duedate > DATE_ADD('" + dueDate + "', INTERVAL 30 DAY) " +
	    				" AND pacc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 60 DAY) " +
	    				" ORDER BY pacc_duedate ASC";
	    		pmPaccounts.doFetch(sql);
	    		if (pmPaccounts.next()) {
	    			DPV3060 = pmPaccounts.getDouble("DPV3060");
	    		}
	    		
	    		sumDPV3060 += DPV3060;
	    		DPV3060T += DPV3060;
	    		total += DPV3060;
	    		
	    		// 60-90 Dias Para Vencimiento
	    		sql = " SELECT SUM(pacc_balance) AS DPV6090 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
	    		if (enableBudgets) {
	    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
	    		}
	    		sql += " WHERE pacc_balance > 0 " + 
	    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
	    				" AND pacc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	    				where + 
	    				" AND pacc_duedate > DATE_ADD('" + dueDate + "', INTERVAL 60 DAY) " +
	    				" AND pacc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 90 DAY) " +
	    				" ORDER BY pacc_duedate ASC";
	    		pmPaccounts.doFetch(sql);
	    		if (pmPaccounts.next()) {
	    			DPV6090 = pmPaccounts.getDouble("DPV6090");
	    		}
	    		
	    		sumDPV6090 += DPV6090;
	    		DPV6090T += DPV6090;
	    		total += DPV6090;
	    		
	    		// +90 Dias Para Vencimiento
	    		sql = " SELECT SUM(pacc_balance) AS DPV90 " +
	    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
	    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
	    		if (enableBudgets) {
	    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
	    		}
	    		sql += " WHERE pacc_balance > 0 " + 
	    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
	    				" AND pacc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	    				where + 
	    				" AND pacc_duedate > DATE_ADD('" + dueDate + "', INTERVAL 90 DAY) " +
	    				" ORDER BY pacc_duedate ASC";
	    		pmPaccounts.doFetch(sql);
	    		if (pmPaccounts.next()) {
	    			DPV90 = pmPaccounts.getDouble("DPV90");
	    		}
	    		
	    		sumDPV90 += DPV90;
	    		DPV90T = DPV90;
	    		total += DPV90;
	              
	        	sumTotal += total;
	           	i++;
	        %>
	        	
	            <tr class="reportCellEven">	
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
	           		<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmSuppliers.getString("suppliers", "supl_code") + " - " + pmSuppliers.getString("suppliers", "supl_name")), BmFieldType.STRING) %>	
	           		<td style="border-style: hidden">&nbsp;</td>
	           		<%= HtmlUtil.formatReportCell(sFParams, "" + DV60T, BmFieldType.CURRENCY) %>
	    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DV3060T, BmFieldType.CURRENCY) %>
	    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DV030T , BmFieldType.CURRENCY) %>
	    	    	<td style="border-style: hidden">&nbsp;</td>
	    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DPV030T, BmFieldType.CURRENCY) %>
	    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DPV3060T, BmFieldType.CURRENCY) %>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + DPV6090T, BmFieldType.CURRENCY) %>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + DPV90T, BmFieldType.CURRENCY) %>	
	    			<td style="border-style: hidden">&nbsp;</td>
	    			<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
	    		</tr>
	    		
	        <%    
	    	}   //End while pmSuppliers
	        %>
	        <tr><td colspan="13">&nbsp;</td></tr>
	    	<tr class="reportCellEven reportCellCode">
	    		<td class="reportCellText" colspan="2">&nbsp;</td>
	    		<td style="border-style: hidden">&nbsp;</td>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV60, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV3060, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV030, BmFieldType.CURRENCY) %>
	    		<td style="border-style: hidden">&nbsp;</td>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV030, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV3060, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV6090, BmFieldType.CURRENCY) %>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV90, BmFieldType.CURRENCY) %>
	    		<td style="border-style: hidden">&nbsp;</td>
	    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotal, BmFieldType.CURRENCY) %>
	    	</tr>
	    	<tr><td colspan="13">&nbsp;</td></tr>
        <%
		} // fin pmCurrencyWhile
		
    }// Existe moneda destino
    else {
	%>
	<tr>                    
		<td class="reportHeaderCellCenter" rowspan="2">#</td>
		<td class="reportHeaderCell" rowspan="2">Proveedor</td>
		<td rowspan="2">&nbsp;</td>
		<td class="reportHeaderCellCenter" colspan="3">C X P vencidas</td>
		<td rowspan="2">&nbsp;</td>
		<td class="reportHeaderCellCenter" colspan="4">Fecha Programada de Pago</td>
		<td rowspan="2">&nbsp;</td>
		<td class="reportHeaderCellRight" rowspan="2" >Total&nbsp;</td>                                                                           
	</tr>
	<tr>                    
		<td class="reportHeaderCellRight">+60 D&iacute;as<br>Vencido</td>
		<td class="reportHeaderCellRight">30-60 D&iacute;as<br>Vencido</td>                                                                           
		<td class="reportHeaderCellRight">0-30 D&iacute;as<br>Vencido</td>                                                                           
		<td class="reportHeaderCellRight">0-30 D&iacute;as Para<br>Vencimiento</td>                                                                           
		<td class="reportHeaderCellRight">30-60 D&iacute;as Para<br>Vencimiento</td>                                                                           
		<td class="reportHeaderCellRight">60-90 D&iacute;as Para<br>Vencimiento</td>                                                                           
		<td class="reportHeaderCellRight">+90 D&iacute;as Para<br>Vencimiento</td>                                                                           
	
	</tr>
	<%
		// suma general de totales por proveedor
    	double 	sumDV030 = 0, sumDV3060 = 0, sumDV60 = 0, total = 0, sumTotal = 0,
        		sumDPV030 = 0, sumDPV3060 = 0, sumDPV6090 = 0, sumDPV90 = 0;
        int i = 0;
        sqlSupl = " SELECT supl_supplierid, supl_code, supl_name FROM " + SQLUtil.formatKind(sFParams, " suppliers ") +
          		   " WHERE supl_supplierid in ( " +
          		   " SELECT pacc_supplierid FROM " + SQLUtil.formatKind(sFParams, " paccounts  ") +
          		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
          		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
          		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
          		   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
        if (enableBudgets) {
        	sqlSupl += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
        }
        sqlSupl += " WHERE pacc_paccountid > 0 AND pacc_balance > 0 " +										     
        		whereSupl + 
        		where +
        		") ORDER BY supl_supplierid ASC ";

        pmSuppliers.doFetch(sqlSupl);
        while (pmSuppliers.next()) {        	
        	total = 0;
        	// totales por proveedor
        	double 	DV030 = 0, DV3060 = 0, DV60 = 0, 
            		DPV030 = 0, DPV3060 = 0, DPV6090 = 0, DPV90 = 0,
        	// suma totales por proveedor por linea
            		DV030T = 0, DV3060T = 0, DV60T = 0,
            		DPV030T = 0, DPV3060T = 0, DPV6090T = 0, DPV90T = 0;
            
        	// variables de conversion moneda-paridad
        	int currencyIdOrigin = 0, currencyIdDestiny = 0;
        	double parityOrigin = 0, parityDestiny = 0;
        	currencyIdDestiny = currencyId;
        	parityDestiny = defaultParity;
        	
        	// +60 Dias Vencido
        	sql = " SELECT pacc_balance AS DV60, pacc_currencyid, pacc_currencyparity " +
        			//" SELECT SUM(pacc_balance) AS DV60 " +
        			" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
        	if (enableBudgets) {
        		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
        	}
        	sql += " WHERE pacc_balance > 0 " +
        			" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
        			where + 
        			" AND pacc_duedate < DATE_ADD('" + dueDate + "', INTERVAL -60 DAY) " +
        			" ORDER BY pacc_duedate ASC";  
        	
        	pmPaccounts.doFetch(sql);
    		while (pmPaccounts.next()) {             	
    			//DV60 = pmPaccounts.getDouble("DV60");
              	currencyIdOrigin = pmPaccounts.getInt("pacc_currencyid");
        		parityOrigin = pmPaccounts.getDouble("pacc_currencyparity");
    			DV60 += pmCurrencyExchange.currencyExchange(pmPaccounts.getDouble("DV60"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDV60 += DV60;
    		DV60T += DV60;
    		total += DV60;
    		
    		// 30-60 Dias Vencido
    		sql = " SELECT pacc_balance AS DV3060, pacc_currencyid, pacc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
    		if (enableBudgets) {
    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
    		}
    		sql += " WHERE pacc_balance > 0 " + 
    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
    				where + 
    				" AND pacc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -60 DAY) " +
    				" AND pacc_duedate < DATE_ADD('" + dueDate + "', INTERVAL -30 DAY) " +
    				" ORDER BY pacc_duedate ASC";        		
    		pmPaccounts.doFetch(sql);
    		while (pmPaccounts.next()) { 
    			//DV3060 = pmPaccounts.getDouble("DV3060");
    			currencyIdOrigin = pmPaccounts.getInt("pacc_currencyid");
        		parityOrigin = pmPaccounts.getDouble("pacc_currencyparity");
        		DV3060 += pmCurrencyExchange.currencyExchange(pmPaccounts.getDouble("DV3060"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDV3060 += DV3060;
    		DV3060T += DV3060;
    		total += DV3060;
    		
    		// 0-30 Dias Vencido
    		sql = " SELECT pacc_balance AS DV030, pacc_currencyid, pacc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
    		if (enableBudgets) {
    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;        				
    		}
    		sql += " WHERE pacc_balance > 0 " + 
    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
    				where + 
    				" AND pacc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL -30 DAY) " + 
    				" AND pacc_duedate < DATE_ADD('" + dueDate + "', INTERVAL 0 DAY) " +
    				//whereDueDateMinor +
    				" ORDER BY pacc_duedate ASC";
    		pmPaccounts.doFetch(sql);
    		while (pmPaccounts.next()) {
    			//DV030 = pmPaccounts.getDouble("DV030");
    			currencyIdOrigin = pmPaccounts.getInt("pacc_currencyid");
        		parityOrigin = pmPaccounts.getDouble("pacc_currencyparity");
        		DV030 += pmCurrencyExchange.currencyExchange(pmPaccounts.getDouble("DV030"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDV030 += DV030;
    		DV030T += DV030;
    		total += DV030;
    		
    		// 0-30 Dias Para Vencimiento
    		sql = " SELECT pacc_balance AS DPV030, pacc_currencyid, pacc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
    		if (enableBudgets) {
    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
    		}
    		sql += " WHERE pacc_balance > 0 " + 
    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
    				where + 
    				//whereDueDate +
    				" AND pacc_duedate >= DATE_ADD('" + dueDate + "', INTERVAL 0 DAY) " +
    				" AND pacc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 30 DAY) " + 
    				" ORDER BY pacc_duedate ASC";
    		pmPaccounts.doFetch(sql);
    		while (pmPaccounts.next()) { 
    			//DPV030 = pmPaccounts.getDouble("DPV030");
    			currencyIdOrigin = pmPaccounts.getInt("pacc_currencyid");
        		parityOrigin = pmPaccounts.getDouble("pacc_currencyparity");
        		DPV030 += pmCurrencyExchange.currencyExchange(pmPaccounts.getDouble("DPV030"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDPV030 += DPV030;
    		DPV030T += DPV030;
    		total += DPV030;

    		// 30-60 Dias Para Vencimiento
    		sql = " SELECT pacc_balance AS DPV3060, pacc_currencyid, pacc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
    		if (enableBudgets) {
    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
    		}
    		sql += " WHERE pacc_balance > 0 " + 
    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
    				where + 
    				" AND pacc_duedate > DATE_ADD('" + dueDate + "', INTERVAL 30 DAY) " +
    				" AND pacc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 60 DAY) " +
    				" ORDER BY pacc_duedate ASC";
    		pmPaccounts.doFetch(sql);
    		while (pmPaccounts.next()) {
    			//DPV3060 = pmPaccounts.getDouble("DPV3060");
    			currencyIdOrigin = pmPaccounts.getInt("pacc_currencyid");
        		parityOrigin = pmPaccounts.getDouble("pacc_currencyparity");
        		DPV3060 += pmCurrencyExchange.currencyExchange(pmPaccounts.getDouble("DPV3060"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDPV3060 += DPV3060;
    		DPV3060T += DPV3060;
    		total += DPV3060;
    		
    		// 60-90 Dias Para Vencimiento
    		sql = " SELECT pacc_balance AS DPV6090, pacc_currencyid, pacc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
    		if (enableBudgets) {
    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
    		}
    		sql += " WHERE pacc_balance > 0 " + 
    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
    				where + 
    				" AND pacc_duedate > DATE_ADD('" + dueDate + "', INTERVAL 60 DAY) " +
    				" AND pacc_duedate <= DATE_ADD('" + dueDate + "', INTERVAL 90 DAY) " +
    				" ORDER BY pacc_duedate ASC";
    		pmPaccounts.doFetch(sql);
    		while (pmPaccounts.next()) {
    			//DPV6090 = pmPaccounts.getDouble("DPV6090");
    			currencyIdOrigin = pmPaccounts.getInt("pacc_currencyid");
        		parityOrigin = pmPaccounts.getDouble("pacc_currencyparity");
        		DPV6090 += pmCurrencyExchange.currencyExchange(pmPaccounts.getDouble("DPV6090"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}
    		
    		sumDPV6090 += DPV6090;
    		DPV6090T += DPV6090;
    		total += DPV6090;
    		
    		// +90 Dias Para Vencimiento
    		sql = " SELECT pacc_balance AS DPV90, pacc_currencyid, pacc_currencyparity " +
    				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pact_paccounttypeid = pacc_paccounttypeid) " +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
    				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid) ";
    		if (enableBudgets) {
    			sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
    					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid) " ;
    		}
    		sql += " WHERE pacc_balance > 0 " + 
    				" AND pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +
    				where + 
    				" AND pacc_duedate > DATE_ADD('" + dueDate + "', INTERVAL 90 DAY) " +
    				" ORDER BY pacc_duedate ASC";
    		pmPaccounts.doFetch(sql);
    		while (pmPaccounts.next()) {
    			//DPV90 = pmPaccounts.getDouble("DPV90");
    			currencyIdOrigin = pmPaccounts.getInt("pacc_currencyid");
        		parityOrigin = pmPaccounts.getDouble("pacc_currencyparity");
        		DPV90 += pmCurrencyExchange.currencyExchange(pmPaccounts.getDouble("DPV90"), currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
    		}

    		sumDPV90 += DPV90;
    		DPV90T = DPV90;
    		total += DPV90;
              
        	sumTotal += total;
           	i++;
        %>
        	
            <tr class="reportCellEven">	
    			<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
           		<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmSuppliers.getString("suppliers", "supl_code") + " - " + pmSuppliers.getString("suppliers", "supl_name")), BmFieldType.STRING) %>	
           		<td style="border-style: hidden">&nbsp;</td>
           		<%= HtmlUtil.formatReportCell(sFParams, "" + DV60T, BmFieldType.CURRENCY) %>
    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DV3060T, BmFieldType.CURRENCY) %>
    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DV030T , BmFieldType.CURRENCY) %>
    	    	<td style="border-style: hidden">&nbsp;</td>
    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DPV030T, BmFieldType.CURRENCY) %>
    	    	<%= HtmlUtil.formatReportCell(sFParams, "" + DPV3060T, BmFieldType.CURRENCY) %>
    			<%= HtmlUtil.formatReportCell(sFParams, "" + DPV6090T, BmFieldType.CURRENCY) %>
    			<%= HtmlUtil.formatReportCell(sFParams, "" + DPV90T, BmFieldType.CURRENCY) %>	
    			<td style="border-style: hidden">&nbsp;</td>
    			<%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
    		</tr>
    		
        <%    
    	}   //End while pmSuppliers
            %>
        <tr><td colspan="13">&nbsp;</td></tr>      
    	<tr class="reportCellEven reportCellCode">
    		<td class="reportCellText" colspan="2">&nbsp;</td>
    		<td style="border-style: hidden">&nbsp;</td>
    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV60, BmFieldType.CURRENCY) %>
    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV3060, BmFieldType.CURRENCY) %>
    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDV030, BmFieldType.CURRENCY) %>
    		<td style="border-style: hidden">&nbsp;</td>
    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV030, BmFieldType.CURRENCY) %>
    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV3060, BmFieldType.CURRENCY) %>
    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV6090, BmFieldType.CURRENCY) %>
    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumDPV90, BmFieldType.CURRENCY) %>
    		<td style="border-style: hidden">&nbsp;</td>
    		<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotal, BmFieldType.CURRENCY) %>
    		
    	</tr>
    <%	
    } // fin de SI existe moneda
    %>
    
    
</TABLE>

<%
        } 
	pmCurrencyWhile.close();
	pmConn.close();	
	pmPaccounts.close();
   	pmSuppliers.close(); 
   	pmConnCount.close();
%>  
	
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<%
	}
}
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
%>
  </body>
</html>