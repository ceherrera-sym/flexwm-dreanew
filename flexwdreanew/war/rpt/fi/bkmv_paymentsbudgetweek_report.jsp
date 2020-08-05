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
 
<%@page import="com.flexwm.shared.fi.BmoBudgetItemType"%>
<%@page import="com.flexwm.shared.fi.BmoBudgetItem"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.server.op.PmRequisition"%>
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
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
    String title = "Reporte Pagos por Presupuesto";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	String sql = "", where = "", whereBudget = "", whereBudgetItem = "", whereBudgetItemBkmc = "";
	String filters = "", dueDate = "", dueEndDate = "", bkmvStatus = "", bankMovTypeId = "";
	int programId = 0, supplierId = 0, customerId = 0, cols= 0, bankAccountId = 0, budgetId = 0, budgetItemId = 0, activeBudgets = 0;
	boolean enableBudgets = false;
	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
   		activeBudgets = 2;
   	}
	// Obtener parametros  
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));  	
	if (request.getParameter("bkmv_bankaccountid") != null) bankAccountId = Integer.parseInt(request.getParameter("bkmv_bankaccountid"));
	if (request.getParameter("bkmv_bankmovtypeid") != null) bankMovTypeId = request.getParameter("bkmv_bankmovtypeid");
	if (request.getParameter("bkmv_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("bkmv_supplierid"));
	if (request.getParameter("bkmv_customerid") != null) customerId = Integer.parseInt(request.getParameter("bkmv_customerid"));
	if (request.getParameter("bkmv_duedate") != null) dueDate = request.getParameter("bkmv_duedate");
	if (request.getParameter("dueenddate") != null) dueEndDate = request.getParameter("dueenddate");
	if (request.getParameter("budgetId") != null) budgetId = Integer.parseInt(request.getParameter("budgetId"));
	if (request.getParameter("budgetItemId") != null) budgetItemId = Integer.parseInt(request.getParameter("budgetItemId"));
	if (request.getParameter("bkmv_status") != null) bkmvStatus = request.getParameter("bkmv_status");
	
	//Filtros
	if (bankAccountId > 0) {
	    where += " AND bkmv_bankaccountid = " + bankAccountId;
	    filters += "<i>Cta.Banco: </i>" + request.getParameter("bkmv_bankaccountidLabel") + ", ";
	}
	
	if (!bankMovTypeId.equals("")) {
		where += SFServerUtil.parseFiltersToSql("bkmv_bankmovtypeid", bankMovTypeId);
     	filters += "<i>Tipo de Mov.: </i>" + request.getParameter("bkmv_bankmovtypeidLabel") + ", ";
	}
	
	if (supplierId > 0) {
	    where += " AND bkmv_supplierid = " + supplierId;
	    filters += "<i>Proveedor: </i>" + request.getParameter("bkmv_supplieridLabel") + ", ";
	}
	 
	if (customerId > 0) {
	    where += " AND bkmv_customerid = " + customerId;
	    filters += "<i>Cliente: </i>" + request.getParameter("bkmv_customeridLabel") + ", ";
	}
	
	if (!dueDate.equals("")) {
	    where += " AND bkmv_duedate >= '" + dueDate + "'";
	    filters += "<i>Pago Inicio: </i>" + dueDate + ", ";
	} else {
		// Si esta esta vacio tomar por defecto el primero de este mes
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(cal.MONTH) + 1;
		int lastDay = cal.getActualMaximum(cal.DAY_OF_MONTH);
		dueDate = "" + year + "-" + month + "-01";
	 	filters += "<i>Pago Inicio Default: </i>" + dueDate + ", ";
	} 
	 
	if (!dueEndDate.equals("")) {
	    where += " AND bkmv_duedate <= '" + dueEndDate + "'";
	    filters += "<i>Pago Final: </i>" + dueEndDate + ", ";
	} else {
		// Si esta esta vacio tomar por defecto el ultimo dia de este mes
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(cal.MONTH) + 1;
		int lastDay = cal.getActualMaximum(cal.DAY_OF_MONTH);
		dueEndDate = "" + year + "-" + month + "-" + lastDay;
	    filters += "<i>Pago Final Default: </i>" + dueEndDate + ", ";

	} 
	
	// Si las 2 fechas estan vacias tomar mes en curso por defecto
// 	if (dueDate.equals("") && dueEndDate.equals("")) {
// 		Calendar cal = Calendar.getInstance();
// 		int year = cal.get(Calendar.YEAR);
// 		int month = cal.get(cal.MONTH) + 1;
// 		int lastDay = cal.getActualMaximum(cal.DAY_OF_MONTH);
// 		dueDate = "" + year + "-" + month + "-01";
// 		dueEndDate = "" + year + "-" + month + "-" + lastDay;
		
// 		where += " AND bkmv_duedate >= '" + dueDate + "' ";
// 		where += " AND bkmv_duedate <= '" + dueEndDate + "' ";
		
// 	 	filters += "<i>Pago Inicio Default: </i>" + dueDate + ", ";
// 	    filters += "<i>Pago Final Default: </i>" + dueEndDate + ", ";
// 	}
	
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
	    where += SFServerUtil.parseFiltersToSql("bkmv_status", bkmvStatus);
	    filters += "<i>Estatus: </i>" + request.getParameter("bkmv_statusLabel") + ", ";
	}
	 
	if (sFParams.getSelectedCompanyId() > 0)
		filters += "<i>Empresa: </i>" + sFParams.getBmoSelectedCompany().getName().toString() + 
		   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	PmConn pmConnBudgetItem = new PmConn(sFParams); 
	pmConnBudgetItem.open();
	
	PmConn pmConnPayments = new PmConn(sFParams); 
	pmConnPayments.open(); 
	
	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
	
	
	sql = " SELECT COUNT(bgit_budgetitemid) AS budgetItemId FROM " + SQLUtil.formatKind(sFParams, "budgetitems") +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets") +" ON (bgit_budgetid = budg_budgetid) " +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +			  		  
			  " where bgit_budgetitemid > 0 " + 
			  //" AND bgty_type = '" + BmoBudgetItemType.TYPE_WITHDRAW + "'" +
			  whereBudget +
			  whereBudgetItem +
			  " ORDER BY budg_budgetid ASC, bgty_name ASC" ;
				int count =0;
				//ejecuto el sql DEL CONTADOR
				pmConnCount.doFetch(sql);
				if(pmConnCount.next())
					count=pmConnCount.getInt("budgetItemId");
				pmConnCount.close();
				System.out.println("contador DE REGISTROS --> "+count);
			//if que muestra el mensajede error
				if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
					%>
					
							<%=messageTooLargeList %>
					<% 
				}else{

	pmConnBudgetItem.doFetch(sql);
	int countBudgetItemFile = 0;
	if (pmConnBudgetItem.next()) countBudgetItemFile = pmConnBudgetItem.getInt("budgetItemId");

    // Partidas
    sql = " SELECT budg_name, bgty_name, bgit_amount, bgit_budgetitemid FROM " + SQLUtil.formatKind(sFParams, "budgetitems") +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets") +" ON (bgit_budgetid = budg_budgetid) " +
			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +			  		  
			  " WHERE bgit_budgetitemid > 0 " + 
			  //" AND bgty_type = '" + BmoBudgetItemType.TYPE_WITHDRAW + "'" +
			  whereBudget +
			  whereBudgetItem +
			  " ORDER BY budg_budgetid ASC, bgty_name ASC" ;
	
	//System.out.println("sql::: "+sql);
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

<table breqir="0" cellspacing="0" cellpading="0" width="100%">
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
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report">
	<%
	Date requestStartDateCal = SFServerUtil.stringToDate(sFParams.getDateFormat(), dueDate);
	Date requestEndDateCal = SFServerUtil.stringToDate(sFParams.getDateFormat(), dueEndDate);
	// Controlar fecha fin
	Date requestEndDateCalWhile = requestEndDateCal;
	%>
			<tr class="">
				<td class="reportHeaderCellCenter" colspan="3">Presupuestos</td>
				<%
					// Contador para Rowspan
					int countRowspan = 0;
					for (int a = 0; requestStartDateCal.getTime() <= requestEndDateCal.getTime(); a++) {
						
						Calendar startCal = Calendar.getInstance();
						startCal.setTime(requestStartDateCal);
						
						// Sumar 6 dias a la fecha semana  para que de fecha fin de una semana	
						Calendar endCal = Calendar.getInstance();
						endCal.setTime(requestStartDateCal);
						endCal.add(Calendar.DATE, 6);
						requestEndDateCalWhile = endCal.getTime();
						
						// Si le fecha incrementada se pasa, limita hasta la fecha fin seleccionada
						if (requestEndDateCalWhile.getTime() > requestEndDateCal.getTime())
							requestEndDateCalWhile = requestEndDateCal;
						
						// Sumar 7 dias a la fecha de solicitud de inicio 
						startCal.add(Calendar.DATE, 7);
						requestStartDateCal = startCal.getTime();
						
						countRowspan++;
					}
				 %>
				 <td></td>
				 <td class="reportHeaderCellCenter" colspan="<%= countRowspan%>">Semanas</td>
				  <td></td>
				 <td class="reportHeaderCellRight" rowspan="2">
		        	Total x Partida
		    	</td> 
			</tr>
		 	<tr class="">
           		<td class="reportHeaderCell">#</td>
           		<td class="reportHeaderCell">Presupuesto</td>
           		<td class="reportHeaderCell">Partidas</td>
           		<td></td>
           		<%
           		// Regresar fecha origen
           		requestStartDateCal = SFServerUtil.stringToDate(sFParams.getDateFormat(), dueDate);
           		requestEndDateCal = SFServerUtil.stringToDate(sFParams.getDateFormat(), dueEndDate);
           		requestEndDateCalWhile = requestEndDateCal;
           		
           		// Mostrar en cabecera las fechas
				for (int a = 0; requestStartDateCal.getTime() <= requestEndDateCal.getTime(); a++) {
					
					Calendar startCal = Calendar.getInstance();
					startCal.setTime(requestStartDateCal);
					
					// Sumar 6 dias a la fecha semana  para que de fecha fin de una semana	
					Calendar endCal = Calendar.getInstance();
					endCal.setTime(requestStartDateCal);
					endCal.add(Calendar.DATE, 6);
					requestEndDateCalWhile = endCal.getTime();
					
					// Si le fecha incrementada se pasa, limita hasta la fecha fin seleccionada
					if (requestEndDateCalWhile.getTime() > requestEndDateCal.getTime())
						requestEndDateCalWhile = requestEndDateCal;

					%>
					<td class="reportHeaderCellRight">
						<%= SFServerUtil.dateToString(requestStartDateCal, sFParams.getDateFormat()) %> 
						<br>
						<%= SFServerUtil.dateToString(requestEndDateCalWhile, sFParams.getDateFormat()) %>
						
					</td><%
					
					// Sumar 7 dias a la fecha de solicitud de inicio 
					startCal.add(Calendar.DATE, 7);
					requestStartDateCal = startCal.getTime();
				}
			 %>
			 
			<td></td>
			
        	</tr>
        	<% 
        	// Matriz para guardar los montos
		    double[][] sumTotal = new double[(countBudgetItemFile)][(countRowspan)];
        	double sumByBudget = 0;
        	double sumTotalBudget = 0;
        	int i = 1, countBudgetItem = 0;
        	pmConnBudgetItem.doFetch(sql);
    		while (pmConnBudgetItem.next()) {
    			//System.out.println("PARTIDA: "+pmConnBudgetItem.getString("bgty_name"));
    			%>
    			<tr class="">
    				<%= HtmlUtil.formatReportCell(sFParams, "" + i++ , BmFieldType.NUMBER) %>
    				<%= HtmlUtil.formatReportCell(sFParams, "" + pmConnBudgetItem.getString("budg_name"), BmFieldType.STRING) %>
    			    <%= HtmlUtil.formatReportCell(sFParams, "" + pmConnBudgetItem.getString("bgty_name"), BmFieldType.STRING) %>
    			    <td></td>
    			    <%
	           		// Regresar fecha origen
	           		requestStartDateCal = SFServerUtil.stringToDate(sFParams.getDateFormat(), dueDate);
	           		requestEndDateCal = SFServerUtil.stringToDate(sFParams.getDateFormat(), dueEndDate);
	           		requestEndDateCalWhile = requestEndDateCal;
	           		
	           		int countByWeek = 0;
	           		// Sacar montos
					for (int a = 0; requestStartDateCal.getTime() <= requestEndDateCal.getTime(); a++) {
						
						Calendar startCal = Calendar.getInstance();
						startCal.setTime(requestStartDateCal);
						
						// Sumar 6 dias a la fecha semana  para que de fecha fin de una semana	
						Calendar endCal = Calendar.getInstance();
						endCal.setTime(requestStartDateCal);
						endCal.add(Calendar.DATE, 6);
						requestEndDateCalWhile = endCal.getTime();
						
						// Si le fecha incrementada se pasa, limita hasta la fecha fin seleccionada en filtro
						if (requestEndDateCalWhile.getTime() > requestEndDateCal.getTime())
							requestEndDateCalWhile = requestEndDateCal;

						// Pagos directos
						sql = " SELECT bkmv_code, bkmc_amount, bkmc_amountconverted FROM " + SQLUtil.formatKind(sFParams, "bankmovconcepts")
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovements") + " ON (bkmv_bankmovementid = bkmc_bankmovementid) "
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes") + " ON (bkmt_bankmovtypeid = bkmv_bankmovtypeid) " 
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (bkmc_budgetitemid = bgit_budgetitemid) " 
						 	    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
						 	    + " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets") + " ON (bgit_budgetid = budg_budgetid) "
								+ " WHERE bkmc_bankmovconceptid > 0 "
								+ where
								+ " AND bkmt_type <> '" + BmoBankMovType.TYPE_DEPOSIT + "' "
								+ "	AND bkmt_category <> '" + BmoBankMovType.CATEGORY_CXC + "' "
								+ " AND bgit_budgetitemid = " + pmConnBudgetItem.getInt("bgit_budgetitemid")
								+ " AND bkmv_duedate >= '" + SFServerUtil.dateToString(requestStartDateCal, sFParams.getDateFormat()) + "' "
								+ " AND bkmv_duedate <= '" + SFServerUtil.dateToString(requestEndDateCalWhile, sFParams.getDateFormat()) + "' "
								+ " ORDER BY bkmv_bankmovementid ";

						//System.out.println("sql_pagos_directos: "+sql);
						
						pmConnPayments.doFetch(sql);
						double sumByWeek = 0;
						while (pmConnPayments.next()) {
							//System.out.println("MB_CODE: " + pmConnPayments.getString("bkmv_code"));
							if (pmConnPayments.getDouble("bkmc_amountconverted") > 0)
								sumByWeek += pmConnPayments.getDouble("bkmc_amountconverted");
							else 
								sumByWeek += pmConnPayments.getDouble("bkmc_amount");
						}
						
						// Anticipos a Proveedor
						sql = " SELECT bkmv_code,bkmv_withdraw, bkmv_amountconverted FROM " + SQLUtil.formatKind(sFParams, "bankmovements") 
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts") + " ON (bkac_bankaccountid = bkmv_bankaccountid) "
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes") + " ON (bkmt_bankmovtypeid = bkmv_bankmovtypeid) "
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "requisitions") + " ON (reqi_requisitionid = bkmv_requisitionid) "
								+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitems") + " ON (reqi_budgetitemid = bgit_budgetitemid) " 
				 	     	 	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgetitemtypes") + " ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " 
				 	     	 	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "budgets") + " ON (bgit_budgetid = budg_budgetid) "
								+ " WHERE bkmv_bankmovementid NOT IN (SELECT bkmc_bankmovementid FROM bankmovconcepts WHERE bkmc_bankmovconceptid > 0) "
								+ where
								//+ " AND bkmt_type <> '" + BmoBankMovType.TYPE_WITHDRAW+ "'"
								+ " AND bgit_budgetitemid = " + pmConnBudgetItem.getInt("bgit_budgetitemid")
								+ " AND bkmv_duedate >= '" + SFServerUtil.dateToString(requestStartDateCal, sFParams.getDateFormat()) + "' "
								+ " AND bkmv_duedate <= '" + SFServerUtil.dateToString(requestEndDateCalWhile, sFParams.getDateFormat()) + "' "
								+ " ORDER BY bkmv_bankmovementid ";;

						//System.out.println("sql_pago anticipo: "+sql);
						
						pmConnPayments.doFetch(sql);
						while (pmConnPayments.next()) {
							//System.out.println("2MB_CODE: " + pmConnPayments.getString("bkmv_code"));

							if (pmConnPayments.getDouble("bkmv_amountconverted") > 0)
								sumByWeek += pmConnPayments.getDouble("bkmv_amountconverted");
							else 
								sumByWeek += pmConnPayments.getDouble("bkmv_withdraw");							
						}
						
						sumTotal[countBudgetItem][countByWeek] = sumByWeek;
						
						%>
							<%=HtmlUtil.formatReportCell(sFParams, "" + sumByWeek, BmFieldType.CURRENCY) %>
						<%
						// Sumar 7 dias a la fecha de solicitud de inicio 
						startCal.add(Calendar.DATE, 7);
						requestStartDateCal = startCal.getTime();
						sumByBudget = sumByBudget + sumByWeek;
						countByWeek++;
					}
				 %>
				 <td></td>
				 <%=HtmlUtil.formatReportCell(sFParams, "" + sumByBudget, BmFieldType.CURRENCY) %>
				 <%
				 sumTotalBudget = sumTotalBudget + sumByBudget;
				 sumByBudget = 0; %>
				 
    			</tr>
    			<%
    			countBudgetItem++;
    		}
         	%>
         	<tr style="font-weight: bold" >
         		<td  class="reportCellEven reportCellCode" colspan = "3"></td>
         		<td></td>
         		<% 
		    	double sumByMonth = 0;
		    	int rows = sumTotal.length;
		    	// Si no hay cxc, no mostrar resultados
		    	if (rows > 0) {
		   		 	int columns = sumTotal[0].length;
	
		   		 	// Recorrer columnas para sumatoria de estas
		   		 	for (int j = 0; j < columns; j++) {
		   				sumByMonth = 0;
		       	        //Recorrer las filas
		        	 	for (int k = 0; k < rows; k++) {
		       		 		sumByMonth += sumTotal[k][j];
		       		 	}
		       	        %>
		       	        <%= HtmlUtil.formatReportCell(sFParams, "" + sumByMonth, BmFieldType.CURRENCY) %>
		       	        <%
		        	 }
		    	}
				%>
				<td></td>
				 <%=HtmlUtil.formatReportCell(sFParams, "" + sumTotalBudget, BmFieldType.CURRENCY) %>
         	</tr>
</table>
<%
	}// Fin de if(no carga datos)

	
%>  
<% 	if (print.equals("1")) { %>
    	<script>
        	//window.print();
    	</script>
<% 	}
System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
+ " Reporte: "+title
+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
				}
			
				pmConnBudgetItem.close();
				pmConnPayments.close();// FIN DEL CONTADOR
%>
	</body>
</html>
