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
	// Inicializar variables
 	String title = "Reporte General de Cuentas por Pagar Agrupado por Proveedor";
	BmoPaccount bmoPaccount = new BmoPaccount();    
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
		
   	String sql = "", sqlCurrency = "", where = "", whereSupl = "";
   	String receiveDate = "", dueDate = "", receiveEndDate = "", dueEndDate = "",  wherePaymentDateStartMb = "", wherePaymentDateEndMb = "", paymentDateStartMb = "", paymentDateEndMb = "";
   	String status = "", paymentStatus = "";
   	String filters = "", suplCategoryId = "";
   	int programId = 0, supplierId = 0, requisitionId = 0, paccountTypeId = 0, companyId = 0, currencyId = 0, budgetId = 0, budgetItemId = 0,areaId = -1, 
   			activeBudgets = 0;
   	boolean enableBudgets = false;
   	
   	// se agrega 2 columnas para presupuestos, para manejo de colspans
   	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
   		activeBudgets = 2;
   	}else {
   		activeBudgets = -1;
   	}
   	
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("pacc_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("pacc_supplierid"));
   	if (request.getParameter("supl_suppliercategoryid") != null) suplCategoryId = request.getParameter("supl_suppliercategoryid");
   	if (request.getParameter("pacc_companyid") != null) companyId = Integer.parseInt(request.getParameter("pacc_companyid"));
   	if (request.getParameter("pacc_requisitionid") != null) requisitionId = Integer.parseInt(request.getParameter("pacc_requisitionid"));
   	if (request.getParameter("pacc_paccounttypeid") != null) paccountTypeId = Integer.parseInt(request.getParameter("pacc_paccounttypeid"));
   	if (request.getParameter("pacc_status") != null) status = request.getParameter("pacc_status");
   	if (request.getParameter("pacc_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("pacc_currencyid"));
   	if (request.getParameter("pacc_paymentstatus") != null) paymentStatus = request.getParameter("pacc_paymentstatus");
   	if (request.getParameter("pacc_receivedate") != null) receiveDate = request.getParameter("pacc_receivedate");
   	if (request.getParameter("receiveenddate") != null) receiveEndDate = request.getParameter("receiveenddate");
   	if (request.getParameter("pacc_duedate") != null) dueDate = request.getParameter("pacc_duedate");
   	if (request.getParameter("dueenddate") != null) dueEndDate = request.getParameter("dueenddate");
   	if (enableBudgets) {
	    if (request.getParameter("bgit_budgetid") != null) budgetId = Integer.parseInt(request.getParameter("bgit_budgetid"));
	    if (request.getParameter("pacc_budgetitemid") != null) budgetItemId = Integer.parseInt(request.getParameter("pacc_budgetitemid"));
	    if (request.getParameter("pacc_areaid") != null) areaId = Integer.parseInt(request.getParameter("pacc_areaid"));
	}
   	if (request.getParameter("paymentdatestartmb") != null) paymentDateStartMb = request.getParameter("paymentdatestartmb");
   	if (request.getParameter("paymentdateendmb") != null) paymentDateEndMb = request.getParameter("paymentdateendmb");
   	
	// Filtros listados
	if (supplierId > 0) {
	  where += " AND pacc_supplierid = " + supplierId;
	  filters += "<i>Proveedor: </i>" + request.getParameter("pacc_supplieridLabel") + ", ";
	}
	
	if (!suplCategoryId.equals("")) {
        //where += " AND supl_suppliercategoryid = " + suplCategoryId;
        where += SFServerUtil.parseFiltersToSql("supl_suppliercategoryid", suplCategoryId);        
        filters += "<i>Categoria: </i>" + request.getParameter("supl_suppliercategoryidLabel") + ", ";
  }
	
	if (requisitionId > 0) {
        where += " AND pacc_requisitionid = " + supplierId;
        filters += "<i>Orden Compra: </i>" + request.getParameter("pacc_requisitionidLabel") + ", ";
    }
	
	if (companyId > 0) {
        where += " AND pacc_companyid = " + companyId;
        filters += "<i>Empresa: </i>" + request.getParameter("pacc_companyidLabel") + ", ";
    }
	
	if (paccountTypeId > 0) {
        where += " AND pacc_paccounttypeid = " + paccountTypeId;
        filters += "<i>Tipo: </i>" + request.getParameter("pacc_paccounttypeidLabel") + ", ";
    }
	
   	if (!receiveDate.equals("")) {
   		where += " AND pacc_receivedate >= '" + receiveDate + "'";
   		filters += "<i>Recepci&oacute;n Inicio: </i>" + receiveDate + ", ";
   	}
   	
   	if (!receiveEndDate.equals("")) {
        where += " AND pacc_receivedate <= '" + receiveEndDate + "'";
        filters += "<i>Recepci&oacute;n Final: </i>" + receiveEndDate + ", ";
    }

   	
   	if (!dueDate.equals("")) {
        where += " AND pacc_duedate >= '" + dueDate + "'";
        filters += "<i>Programaci&oacute;n Inicio: </i>" + dueDate + ", ";
    }
   	
   	if (!dueEndDate.equals("")) {
        where += " AND pacc_duedate <= '" + dueEndDate + "'";
        filters += "<i>Programaci&oacute;n Final: </i>" + dueEndDate + ", ";
    }
     
   	if (!status.equals("")) {
   		//where += " and pacc_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("pacc_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("pacc_statusLabel") + ", ";
   	}
   	
   	if (!paymentStatus.equals("")) {
        ///where += " and pacc_paymentstatus like '" + paymentStatus + "'";
        where += SFServerUtil.parseFiltersToSql("pacc_paymentstatus", paymentStatus);
        filters += "<i>Pago: </i>" + request.getParameter("pacc_paymentstatusLabel") + ", ";
    }
   	
   	if (enableBudgets) {
	    if (budgetId > 0) {
	        where += " AND bgit_budgetid = " + budgetId;
	        filters += "<i>Presup.: </i>" + request.getParameter("bgit_budgetidLabel") + ", ";
	    }  
	
	    if (budgetItemId > 0) {
	        where += " AND pacc_budgetitemid = " + budgetItemId;
	        filters += "<i>Item Presup.: </i>" + request.getParameter("pacc_budgetitemidLabel") + ", ";
	    }
	    
	    if (areaId > 0) {
	        where += " AND pacc_areaid = " + areaId;
	        filters += "<i>Departamento.: </i>" + request.getParameter("pacc_areaidLabel") + ", ";
	    }
    }
   	
   	if (!paymentDateStartMb.equals("")) {
        //where += " AND bkmv_duedate >= '" + paymentDateStartMb + "'";
        wherePaymentDateStartMb = " AND bkmv_duedate >= '" + paymentDateStartMb + "' ";
        filters += "<i>Fecha Pago: </i>" + paymentDateStartMb + ", ";
    }
   	
   	if (!paymentDateEndMb.equals("")) {
       // where += " AND bkmv_duedate <= '" + paymentDateEndMb + "'";
        wherePaymentDateEndMb = " AND bkmv_duedate <= '" + paymentDateEndMb + "' ";
        filters += "<i>Fecha Pago Fin: </i>" + paymentDateEndMb + ", ";
    }
   	
   	//Obtener la paridad de la moneda
   	double nowParity = 0;
   	double defaultParity = 0;
   	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
   	if (currencyId > 0) {
   		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);   		
   		defaultParity = bmoCurrency.getParity().toDouble();
   		
   		filters += "<i>Moneda: </i>" + request.getParameter("pacc_currencyidLabel")  + " | <i>Paridad Actual : </i>" + defaultParity;
   	} else {
   		//Obtener la paridad de la moneda del sistema
   		//bmoCurrency = (BmoCurrency)pmCurrency.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getCurrencyId().toInteger());
   		//defaultParity = bmoCurrency.getParity().toDouble();
   		//filters += "<i>Moneda: </i>" + bmoCurrency.getCode().toString() + " | " + bmoCurrency.getName().toString() + ", ";
   		filters += "<i>Moneda: </i> Todas ";
		sqlCurrency = "SELECT cure_currencyid, cure_name FROM " + SQLUtil.formatKind(sFParams, " currencies ");
   		
   	}
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmPaccount(sFParams).getDisclosureFilters();   
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;  	
    
   	PmConn pmSuppliers = new PmConn(sFParams);
   	pmSuppliers.open();
   	
   	PmConn pmPaccounts = new PmConn(sFParams);
    pmPaccounts.open();
    
    PmConn pmPaccountsD =new PmConn(sFParams);
    pmPaccountsD.open();
    
    PmConn pmCurrencyWhile =new PmConn(sFParams);
    pmCurrencyWhile.open();
    
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
			<b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Proveedor" : "Proveedor")%>
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<br>
<table class="report" border="0">
	<%
	
	
	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	sql = "SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
		         		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" on (pacc_supplierid = supl_supplierid)" +
		         		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
		         		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" on (pacc_requisitionid = reqi_requisitionid)" +
		         		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on (pacc_companyid = comp_companyid)" +                                      
		         		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pacc_paccounttypeid = pact_paccounttypeid) " +
		         		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (pacc_currencyid = cure_currencyid) ";
	        if (enableBudgets) {
         		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" on (bgit_budgetitemid = pacc_budgetitemid)" +
	         			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	         			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" on (budg_budgetid = bgit_budgetid)" +
	         			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = pacc_areaid) ";
	        }
         	sql += " WHERE pacc_paccountid = 0 " +                               
	         		where + 
	         		" AND pacc_paccountid IN " +
	         				" (SELECT bkmc_paccountid FROM bankmovconcepts " +
	                		" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovements") + " ON (bkmv_bankmovementid = bkmc_bankmovementid) " +
               				" WHERE bkmc_paccountid > 0 " +
	         				wherePaymentDateStartMb +
	         				wherePaymentDateEndMb + " )" +
	         		" GROUP BY pacc_paccountid " +
	         		" ORDER BY pacc_receivedate ASC";
			
			int count =0;
			//ejecuto el sql DEL CONTADOR
						System.out.println("1 SQL --> "+sql);

			pmConnCount.doFetch(sql);
			System.out.println("2 contador DE REGISTROS --> "+count);

			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			pmConnCount.close();
			System.out.println("contador DE REGISTROS --> "+count);
			//if que muestra el mensajede error
			if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
				%>
				
						<%=messageTooLargeList %>
				<% 
			}else{
	
	if (!(currencyId > 0)) {
		
        int currencyIdWhile = 0, i = 1, y = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
		
			 // lista proveedores
			sql = " SELECT supl_supplierid, supl_code, supl_name, pacc_receivedate " + 
					" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
	        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers") + " ON (pacc_supplierid = supl_supplierid)" +
	        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories") + " ON (spca_suppliercategoryid = supl_suppliercategoryid)" +
	        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions") + " ON (pacc_requisitionid = reqi_requisitionid)" +
	        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies") + " ON (pacc_companyid = comp_companyid)" +                                      
	        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes") + " ON (pacc_paccounttypeid = pact_paccounttypeid) " +
	        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies") + " ON (pacc_currencyid = cure_currencyid) ";
	        if (enableBudgets) {
	        	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems") + " ON (bgit_budgetitemid = pacc_budgetitemid)" +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets") + " ON (budg_budgetid = bgit_budgetid)" +
	        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") + " ON(area_areaid = pacc_areaid) ";
	        }
	        sql += " WHERE pacc_paccountid > 0 " +          
	   	  			" AND pacc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
	        		where + 
	        		" AND pacc_paccountid IN (SELECT bkmc_paccountid FROM bankmovconcepts " +
	        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovements") + " ON (bkmv_bankmovementid = bkmc_bankmovementid) " +
	        				" WHERE bkmc_paccountid > 0 " +
	        				wherePaymentDateStartMb +
	        				wherePaymentDateEndMb + " )" +
	        		" GROUP BY pacc_supplierid " +
	        		" ORDER BY supl_code ASC, pacc_paccountid ASC";   

	   	  	
	   	  	System.out.println("sql: "+sql);  
     	      
           	double amountTotal = 0, taxPaccTotal = 0, withDrawTotal = 0, depositTotal = 0;
            pmSuppliers.doFetch(sql);
            while (pmSuppliers.next()) {
            	
            	if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
            		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
            		y = 0;
            		%>
            		<tr>
	            		<td class="reportHeaderCellCenter" colspan="<%= (20 + activeBudgets)%>">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
            		</tr>
            		<%
            	}
            	
                double amount = 0;
                
                double saldoTotal = 0; 
                double amountSupl = 0, taxPaccSupl = 0, totalPaccSupl = 0, amountW = 0, amountD = 0;
                int supplierid = 0;
             	// Obtener CP
                sql = "SELECT pacc_paccountid, pacc_code, pacc_amount, pacc_payments, pacc_tax, pacc_total, reqi_code, pacc_invoiceno, comp_name, pacc_description, pact_name, " +
                		" pacc_receivedate, pacc_duedate,pacc_status, pacc_paymentstatus, pacc_balance, pacc_requisitionid, pacc_currencyid, " +
                		" pacc_currencyparity, reqi_currencyparity, cure_code,pacc_file ";
                if (enableBudgets) {
                	sql += ", budg_name, bgty_name, area_name ";
                }
                sql += " FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
                		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (pacc_supplierid = supl_supplierid)" +
                		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" on (spca_suppliercategoryid = supl_suppliercategoryid)" +
                		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (pacc_requisitionid = reqi_requisitionid)" +
                		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (pacc_companyid = comp_companyid)" +                                      
                		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pacc_paccounttypeid = pact_paccounttypeid) " +
                		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (pacc_currencyid = cure_currencyid) ";
                if (enableBudgets) {
                	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (bgit_budgetitemid = pacc_budgetitemid)" +
                			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
                			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (budg_budgetid = bgit_budgetid)" +
                			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = pacc_areaid) ";
                 }
                sql += " WHERE pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +   
    	   	  			" AND pacc_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
                		where + 
                		" AND pacc_paccountid IN " +
                				" (SELECT bkmc_paccountid FROM bankmovconcepts " +
            					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovements") + " ON (bkmv_bankmovementid = bkmc_bankmovementid) "  +
                				" WHERE bkmc_paccountid > 0 " +
                				wherePaymentDateStartMb +
                				wherePaymentDateEndMb + " )" +
                		" GROUP BY pacc_paccountid " +
                		" ORDER BY pacc_receivedate ASC";  
               //System.out.println("sqlW; "+ sql);
               pmPaccounts.doFetch(sql);
               
               while (pmPaccounts.next()) {

            	   	if ((pmSuppliers.getInt("suppliers", "supl_supplierid") != supplierid)) {
            		   supplierid = pmSuppliers.getInt("suppliers", "supl_supplierid");
            		   i = 1;
             	  %>       
         	  			<tr>
               				<td align="left" colspan="<%= (20 + activeBudgets)%>" class="reportGroupCell">
               					<b>Proveedor: <%= pmSuppliers.getString("supl_code") + " " + HtmlUtil.stringToHtml(pmSuppliers.getString("supl_name").toUpperCase())%></b>
               				</td>
           				</tr>
           				<TR>
	           				<td class="reportHeaderCellCenter">#</td>
	           				<td class="reportHeaderCell">Clave</td>
	           				<td class="reportHeaderCell">O.C</td>
	           				<td class="reportHeaderCell">Fac</td>
	           				<td class="reportHeaderCell">Empresa</td>                                                                           
	           				<td class="reportHeaderCell">Descripci&oacute;n</td>
	           				<td class="reportHeaderCell">Tipo CxP</td>
           				<% 	if (enableBudgets) { %>
		           				<td class="reportHeaderCell">Presup.</td>
		           				<td class="reportHeaderCell">Item Presup.</td>	
		           				<td class="reportHeaderCell">Departamento</td>
           				<% 	} %>
	           				<td class="reportHeaderCell">Ingreso</td>
	           				<td class="reportHeaderCell">Venc.</td>
	           				<td class="reportHeaderCell">Estatus</td>
	           				<td class="reportHeaderCell">Pago</td>
	           				<td class="reportHeaderCell">Archivo</td>
	           				<td class="reportHeaderCell">Moneda</td>
	           				<td class="reportHeaderCellCenter">Tipo de Cambio</td>
	           				<td class="reportHeaderCellRight">Monto</td>
	           				<td class="reportHeaderCellRight">IVA</td>
	           				<td class="reportHeaderCellRight">Total</td>
	           				<td class="reportHeaderCellRight">Pagos</td>
	           				<td class="reportHeaderCellRight">Saldo</td>
           				</TR>
             <% 	} %>
                  <TR class="reportCellEven">
             	   	   <%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>

                  	   <%= HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_code"), BmFieldType.CODE) %>
                       <%= HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("requisitions","reqi_code"), BmFieldType.CODE) %>
                       
                       <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_invoiceno"), BmFieldType.STRING)) %>                                    
                       <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("companies","comp_name"), BmFieldType.STRING)) %>                                                
                       <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_description"), BmFieldType.STRING)) %>
                       <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounttypes","pact_name"), BmFieldType.STRING)) %>                                                

                       <% if (enableBudgets) { %>
	       					   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("budg_name"), BmFieldType.STRING)) %>
	                           <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
                       		   <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("areas","area_name"), BmFieldType.STRING)) %>
		        	   <% } %>
                       <%= HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_receivedate"), BmFieldType.DATE) %>                                                                                      
                       <%= HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_duedate"), BmFieldType.DATE) %>
                       <%                                   
                          bmoPaccount.getStatus().setValue(pmPaccounts.getString("paccounts","pacc_status"));
                          bmoPaccount.getPaymentStatus().setValue(pmPaccounts.getString("paccounts","pacc_paymentstatus"));
                       %>
                       <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoPaccount.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
                       <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoPaccount.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
	               	 
	               	   <%  String paccFile = ((pmPaccounts.getBoolean("pacc_file") ? "Si" : "No")); %>	
	               	   
					   <%= HtmlUtil.formatReportCell(sFParams, paccFile, BmFieldType.STRING) %>
                       <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("cure_code"), BmFieldType.STRING)) %>
                       <%= HtmlUtil.formatReportCell(sFParams, ""  + pmPaccounts.getDouble("pacc_currencyparity"), BmFieldType.NUMBER) %>
                       
                       <%
                       double amountPacc = pmPaccounts.getDouble("pacc_amount");
                       double taxPacc = pmPaccounts.getDouble("pacc_tax");
                       double totalPacc = pmPaccounts.getDouble("pacc_total");
                       double paymentsPacc = pmPaccounts.getDouble("pacc_payments");					           		
                       double balancePacc = pmPaccounts.getDouble("pacc_balance");
                       
                       amountSupl += amountPacc;
                       taxPaccSupl += taxPacc;
                       amountW += totalPacc;
                       amountD += paymentsPacc;
                       //totalPaccSupl += taxPacc
                       %>
                       
                       <%= HtmlUtil.formatReportCell(sFParams, "" + amountPacc, BmFieldType.CURRENCY) %>
                       <%= HtmlUtil.formatReportCell(sFParams, "" + taxPacc, BmFieldType.CURRENCY) %>
                       <%= HtmlUtil.formatReportCell(sFParams, "" + totalPacc , BmFieldType.CURRENCY) %>
                       <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsPacc, BmFieldType.CURRENCY) %>
                       <%= HtmlUtil.formatReportCell(sFParams, "" + (totalPacc - paymentsPacc), BmFieldType.CURRENCY) %>
                       
                   </TR>                         
               <% 	y++;
               		i++;
               } //End while pmPaccounts (Cargos)
               		
               if (amountW > 0 || amountD > 0) {
        	   %>
            	   <tr class="reportCellEven reportCellCode">
	            	   <td colspan="<%= (15 + activeBudgets)%>">&nbsp;</td>
	                   <%= HtmlUtil.formatReportCell(sFParams, "" + amountSupl, BmFieldType.CURRENCY) %>
	                   <%= HtmlUtil.formatReportCell(sFParams, "" + taxPaccSupl, BmFieldType.CURRENCY) %>
	            	   <%= HtmlUtil.formatReportCell(sFParams, "" + amountW, BmFieldType.CURRENCY) %>
	            	   <%= HtmlUtil.formatReportCell(sFParams, "" + amountD, BmFieldType.CURRENCY) %>
	            	   <%= HtmlUtil.formatReportCell(sFParams, "" + (amountW - amountD), BmFieldType.CURRENCY) %>
            	   </tr>
            	   <%
               }
                  withDrawTotal += amountW;
                  depositTotal += amountD;
                  amountTotal += amountSupl;
                  taxPaccTotal += taxPaccSupl;
                  
            } //End while pmSuppliers 
            %>
            
            <%
            if (withDrawTotal > 0 || depositTotal > 0) {
        	%>    
        		<tr><td colspan="<%= (20 + activeBudgets)%>">&nbsp;</td></tr>
            	<tr class="reportCellEven reportCellCode">
	            	<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
	            	<td colspan="<%= (14 + activeBudgets)%>">&nbsp;</td>
	            	<%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
	            	<%= HtmlUtil.formatReportCell(sFParams, "" + taxPaccTotal, BmFieldType.CURRENCY) %>
	            	<%= HtmlUtil.formatReportCell(sFParams, "" + withDrawTotal, BmFieldType.CURRENCY) %>
	            	<%= HtmlUtil.formatReportCell(sFParams, "" + depositTotal, BmFieldType.CURRENCY) %>
	            	<%= HtmlUtil.formatReportCell(sFParams, "" + (withDrawTotal - depositTotal), BmFieldType.CURRENCY) %>
            	</tr>                     
    	<% } %>
    	<tr><td colspan="<%= (20 + activeBudgets)%>">&nbsp;</td></tr>
        <%
		} // fin de moneda
	} 
	// Existe moneda destino
	else {
		
       	double amountTotal = 0, taxPaccTotal = 0, withDrawTotal = 0, depositTotal = 0;
        int y = 0;
        // lista proveedores
		sql = " SELECT supl_supplierid, supl_code, supl_name, pacc_receivedate " + 
				" FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers") + " ON (pacc_supplierid = supl_supplierid)" +
        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories") + " ON (spca_suppliercategoryid = supl_suppliercategoryid)" +
        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions") + " ON (pacc_requisitionid = reqi_requisitionid)" +
        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies") + " ON (pacc_companyid = comp_companyid)" +                                      
        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes") + " ON (pacc_paccounttypeid = pact_paccounttypeid) " +
        		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies") + " ON (pacc_currencyid = cure_currencyid) ";
        if (enableBudgets) {
        	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems") + " ON (bgit_budgetitemid = pacc_budgetitemid)" +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets") + " ON (budg_budgetid = bgit_budgetid)" +
        			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") + " ON(area_areaid = pacc_areaid) ";
        }
        sql += " WHERE pacc_paccountid > 0 " +                             
        		where + 
        		" AND pacc_paccountid IN (SELECT bkmc_paccountid FROM bankmovconcepts " +
        				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovements") + " ON (bkmv_bankmovementid = bkmc_bankmovementid) " +
        				" WHERE bkmc_paccountid > 0 " +
        				wherePaymentDateStartMb +
        				wherePaymentDateEndMb + " )" +
        		" GROUP BY pacc_supplierid " +
        		" ORDER BY supl_code ASC, pacc_paccountid ASC";    

         System.out.println("sql: "+sql);
      	   	      
                
        pmSuppliers.doFetch(sql);
        while (pmSuppliers.next()) {
        	
            double amountSupl = 0, taxPaccSupl = 0, totalPaccSupl = 0, amountW = 0, amountD = 0;
            int supplierid = 0;
            // Obtener CP
            sql = "SELECT pacc_paccountid, pacc_code, pacc_amount, pacc_payments, pacc_tax, pacc_total, reqi_code, pacc_invoiceno, comp_name, pacc_description, pact_name, " +
            		" pacc_receivedate, pacc_duedate,pacc_status, pacc_paymentstatus, pacc_balance, pacc_requisitionid, pacc_currencyid, " +
            		" pacc_currencyparity, reqi_currencyparity, cure_code,pacc_file ";
            if (enableBudgets) {
            	sql += ", budg_name, bgty_name, area_name ";
            }
            sql += " FROM " + SQLUtil.formatKind(sFParams, " paccounts ") + 
            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (pacc_supplierid = supl_supplierid)" +
            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliercategories")+" ON (spca_suppliercategoryid = supl_suppliercategoryid)" +
            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (pacc_requisitionid = reqi_requisitionid)" +
            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (pacc_companyid = comp_companyid)" +                                      
            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " paccounttypes")+" ON (pacc_paccounttypeid = pact_paccounttypeid) " +
            		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (pacc_currencyid = cure_currencyid) ";
            if (enableBudgets) {
            	sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (bgit_budgetitemid = pacc_budgetitemid)" +
            			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
            			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (budg_budgetid = bgit_budgetid)" +
            			" LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") +" ON(area_areaid = pacc_areaid) ";
             }
            sql += " WHERE pacc_supplierid = " + pmSuppliers.getInt("supl_supplierid") +                                 
            		where + 
            		//" AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "' " +
            		" AND pacc_paccountid IN " +
            				" (SELECT bkmc_paccountid FROM bankmovconcepts " +
        					" LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovements") + " ON (bkmv_bankmovementid = bkmc_bankmovementid) "  +
            				" WHERE bkmc_paccountid > 0 " +
            				wherePaymentDateStartMb +
            				wherePaymentDateEndMb + " )" +
            		" GROUP BY pacc_paccountid " +
            		" ORDER BY pacc_receivedate ASC";
                //System.out.println("sqlW; "+ sql);
           pmPaccounts.doFetch(sql);
           int i = 1;
           while (pmPaccounts.next()) {
        	   
        	   if ((pmSuppliers.getInt("suppliers", "supl_supplierid") != supplierid)) {
             	  supplierid = pmSuppliers.getInt("suppliers", "supl_supplierid");
             	  %>       
             	  	<tr class="reportCell">
                   		<td align="left" colspan="<%= (20 + activeBudgets)%>" class="reportGroupCell">
                   			<b>Proveedor: <%= pmSuppliers.getString("supl_code") + " " + HtmlUtil.stringToHtml(pmSuppliers.getString("supl_name").toUpperCase())%></b>
               			</td>
                    </tr>
	                 <TR>
	                 	 <td class="reportHeaderCellCenter">#</td>
	                 	 <td class="reportHeaderCell">Clave</td>
	                     <td class="reportHeaderCell">O.C</td>
	                     <td class="reportHeaderCell">Fac</td>
	                     <td class="reportHeaderCell">Empresa</td>                                                                           
	                     <td class="reportHeaderCell">Descripci&oacute;n</td>
	                     <td class="reportHeaderCell">Tipo CxP</td>
	                     <% if (enableBudgets) { %>
			     			<td class="reportHeaderCell">Presup.</td>
			                <td class="reportHeaderCell">Item Presup.</td> 
			                <td class="reportHeaderCell">Departamento</td>
			             <% } %>
	                     <td class="reportHeaderCell">Ingreso</td>
	                     <td class="reportHeaderCell">Venc.</td>
	                     <td class="reportHeaderCell">Estatus</td>				                     
	                     <td class="reportHeaderCell">Pago</td>	
	                     <td class="reportHeaderCell">Archivo</td>
	                     <td class="reportHeaderCell">Moneda</td>
		           		 <td class="reportHeaderCellCenter">Tipo de Cambio</td>	
	                     <td class="reportHeaderCellRight">Monto</td>
	                     <td class="reportHeaderCellRight">IVA</td>
	                     <td class="reportHeaderCellRight">Total</td>
	                     <td class="reportHeaderCellRight">Pagos</td>
	                     <td class="reportHeaderCellRight">Saldo</td>
	                 </TR>
             <% } %>
              	<TR class="reportCellEven">
	              	<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
	              	<%= HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_code"), BmFieldType.CODE) %>
	              	<%= HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("requisitions","reqi_code"), BmFieldType.CODE) %>
	              	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_invoiceno"), BmFieldType.STRING)) %>                                    
	              	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("companies","comp_name"), BmFieldType.STRING)) %>                                                
	              	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_description"), BmFieldType.STRING)) %>
	              	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounttypes","pact_name"), BmFieldType.STRING)) %>
	              	<% 	if (enableBudgets) { %>
			              	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("budg_name"),BmFieldType.STRING)) %>
			              	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("budgetitemtypes", "bgty_name"),BmFieldType.STRING)) %>
	              			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("areas","area_name"), BmFieldType.STRING)) %>
		        	 <%} %>
	              	<%= HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_receivedate"), BmFieldType.DATE) %>                                                                                      
	              	<%= HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("paccounts","pacc_duedate"), BmFieldType.DATE) %>
	              	<%                                   
		              	bmoPaccount.getStatus().setValue(pmPaccounts.getString("paccounts","pacc_status"));
		              	bmoPaccount.getPaymentStatus().setValue(pmPaccounts.getString("paccounts","pacc_paymentstatus"));
	              	%>
	              
	              	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoPaccount.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
	              	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoPaccount.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
	              	 
	              	 <%	String paccFile = ((pmPaccounts.getBoolean("pacc_file") ? "Si" : "No")); %>
	              	 
					<%= HtmlUtil.formatReportCell(sFParams, paccFile, BmFieldType.STRING) %>
	              	<%= HtmlUtil.formatReportCell(sFParams, pmPaccounts.getString("cure_code"), BmFieldType.STRING) %>
	              	<%= HtmlUtil.formatReportCell(sFParams, ""  + pmPaccounts.getDouble("pacc_currencyparity"), BmFieldType.NUMBER) %>
	
	              	<%
	              	double amountPacc = pmPaccounts.getDouble("pacc_amount");
	              	double taxPacc = pmPaccounts.getDouble("pacc_tax");
	              	double totalPacc = pmPaccounts.getDouble("pacc_total");
	              	double paymentsPacc = pmPaccounts.getDouble("pacc_payments");					           		
	              	double balancePacc = pmPaccounts.getDouble("pacc_balance");
	              	
	              	//Conversion a la moneda destino(seleccion del filtro)
	              	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	              	double parityOrigin = 0, parityDestiny = 0;
	              	currencyIdOrigin = pmPaccounts.getInt("pacc_currencyid");
	              	parityOrigin = pmPaccounts.getDouble("pacc_currencyparity");
	              	currencyIdDestiny = currencyId;
	              	parityDestiny = defaultParity;
	
	              	//amountPacc = (pmPaccounts.getDouble("pacc_amount") * parityOrigin) / parityDestiny ;
	              	amountPacc = pmCurrencyExchange.currencyExchange(amountPacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	              	//taxPacc = (pmPaccounts.getDouble("pacc_tax") * nowParity) / defaultParity ;
	              	taxPacc = pmCurrencyExchange.currencyExchange(taxPacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	              	totalPacc = pmCurrencyExchange.currencyExchange(totalPacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);

	              	paymentsPacc = pmCurrencyExchange.currencyExchange(paymentsPacc, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	              	//totalPacc = (pmPaccounts.getDouble("pacc_total") * nowParity) / defaultParity ;
	              	//balancePacc = pmCurrencyExchange.currencyExchange((totalPacc - paymentsPacc), parityOrigin, parityDestiny); 	 
	              	
	              	amountSupl += amountPacc;
                    taxPaccSupl += taxPacc;
                    amountW += totalPacc;
                    amountD += paymentsPacc;
	              	%>
	
	              	<%= HtmlUtil.formatReportCell(sFParams, "" + amountPacc, BmFieldType.CURRENCY) %>
	              	<%= HtmlUtil.formatReportCell(sFParams, "" + taxPacc, BmFieldType.CURRENCY) %>
	              	<%= HtmlUtil.formatReportCell(sFParams, "" + totalPacc , BmFieldType.CURRENCY) %>
	              	<%= HtmlUtil.formatReportCell(sFParams, "" + paymentsPacc, BmFieldType.CURRENCY) %>
	              	<%= HtmlUtil.formatReportCell(sFParams, "" + (totalPacc - paymentsPacc), BmFieldType.CURRENCY) %>
              	</TR>                         
           <% 	y++;
           		i++;
       		} //End while pmPaccounts (Cargos)
           		
       		if (amountW > 0 || amountD > 0) {
       %>
               	<tr class="reportCellEven reportCellCode">
	            		<td colspan="<%= (15 + activeBudgets)%>">&nbsp;</td>
			            <%= HtmlUtil.formatReportCell(sFParams, "" + amountSupl, BmFieldType.CURRENCY) %>
		              	<%= HtmlUtil.formatReportCell(sFParams, "" + taxPaccSupl, BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + amountW, BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + amountD, BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + (amountW - amountD), BmFieldType.CURRENCY) %>
	            </tr>
       <%
       		}
       		amountTotal += amountSupl;
       		taxPaccTotal += taxPaccSupl;
       		withDrawTotal += amountW;
       		depositTotal += amountD;
              
        } //End while pmSuppliers 
        %>
                
        <tr><td colspan="<%= (20 + activeBudgets)%>">&nbsp;</td></tr>
        <tr class="reportCellEven reportCellCode">
    		<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
        	<td colspan="<%= (14 + activeBudgets)%>">&nbsp;</td>
	        <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + taxPaccTotal, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + withDrawTotal, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + depositTotal, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + (withDrawTotal - depositTotal), BmFieldType.CURRENCY) %>
       </tr>
    <%
	}// FIN DEL CONTADOR
	} // fin de SI existe moneda
    %>
                                       
</TABLE>
<%	
	}// Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmPaccountsD.close();
 	pmPaccounts.close();
   	pmSuppliers.close(); 
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